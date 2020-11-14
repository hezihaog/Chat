package com.zh.android.circle.mall.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.blankj.utilcode.util.RegexUtils
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.widget.SwitchButton
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.mall.enums.UserAddressEditType
import com.zh.android.circle.mall.R
import com.zh.android.circle.mall.enums.DefaultAddressFlag
import com.zh.android.circle.mall.http.MallPresenter
import com.zh.android.circle.mall.model.UserAddressModel
import com.zh.android.circle.mall.model.picker.RegionModel
import com.zh.android.circle.mall.util.RegionDataSource
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotterknife.bindView

/**
 * @author wally
 * @date 2020/10/21
 * 用户收货地址编辑
 */
class UserAddressEditFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vNameInput: EditText by bindView(R.id.name_input)
    private val vPhoneInput: EditText by bindView(R.id.phone_input)
    private val vRegionLayout: View by bindView(R.id.region_layout)
    private val vRegionContent: TextView by bindView(R.id.region_content)
    private val vDetailAddressInput: EditText by bindView(R.id.detail_address_input)
    private val vSetDefaultAddressSwitch: SwitchButton by bindView(R.id.set_default_address_switch)
    private val vSave: View by bindView(R.id.save)
    private val vDelete: View by bindView(R.id.delete)

    /**
     * 编辑类型，默认为新增
     */
    private val mEditType by bindArgument(
        AppConstant.Key.MALL_USER_ADDRESS_EDIT_TYPE,
        UserAddressEditType.ADD
    )

    /**
     * 是否是更新类型
     */
    private val isUpdateType by lazy {
        mEditType == UserAddressEditType.UPDATE
    }

    /**
     * 收货地址Id，编辑时使用
     */
    private val mAddressId by bindArgument(AppConstant.Key.MALL_USER_ADDRESS_ID, "")

    /**
     * 用户收货地址信息
     */
    private var mUserAddressInfo: UserAddressModel? = null

    private val mWaitLoadingController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }
    private val mMallPresenter by lazy {
        MallPresenter()
    }

    /**
     * 当前选中的区域信息
     */
    private var mSelectRegionInfo: Triple<String, String, String>? = null

    companion object {
        fun newInstance(args: Bundle? = Bundle()): UserAddressEditFragment {
            val fragment = UserAddressEditFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mall_user_address_edit_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(
                if (isUpdateType) {
                    R.string.mall_add_user_address
                } else {
                    R.string.mall_update_user_address
                }
            )
        }
        //地区
        vRegionLayout.click {
            showRegionChooseDialog()
        }
        vSave.click {
            if (mAddressId.isBlank()) {
                //保存
                saveOrUpdateUserAddress(true, "")
            } else {
                //更新
                saveOrUpdateUserAddress(false, mAddressId)
            }
        }
        vDelete.click {
            //删除
            deleteAddress()
        }
        vDelete.apply {
            //编辑状态时，才显示删除按钮
            if (isUpdateType) {
                setVisible()
            } else {
                setGone()
            }
        }
    }

    override fun setData() {
        super.setData()
        if (isUpdateType && mAddressId.isNotBlank()) {
            //编辑状态，拉取地址信息
            getUserAddress(mAddressId)
        }
    }

    /**
     * 显示区域选择弹窗
     */
    private fun showRegionChooseDialog() {
        //获取区域数据
        Observable.create(ObservableOnSubscribe
        <Triple<List<RegionModel>, List<List<String>>, List<List<List<String>>>>> {
            it.onNext(
                RegionDataSource.getRegionData(fragmentActivity)
            )
        })
            //注意，一定要用子线程
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                val options1Items: List<RegionModel> = it.first
                val options2Items: List<List<String>> = it.second
                val options3Items: List<List<List<String>>> = it.third
                //弹出选择地址弹窗
                hideSoftInput()
                //构建城市选择
                val pickerOptions = OptionsPickerBuilder(
                    activity,
                    OnOptionsSelectListener { options1, options2, options3, _ ->
                        //省
                        val opt1tx = if (options1Items.isNotEmpty()
                            && options1Items.isNotEmpty()
                        ) {
                            options1Items[options1].pickerViewText
                        } else {
                            ""
                        }
                        //市
                        val opt2tx = if (options2Items.isNotEmpty()
                            && options3Items[options1].isNotEmpty()
                        ) {
                            options2Items[options1][options2]
                        } else {
                            ""
                        }
                        //区
                        val opt3tx = if (
                            options2Items.isNotEmpty()
                            && options3Items[options1].isNotEmpty()
                            && options3Items[options1][options2].isNotEmpty()
                        ) {
                            options3Items[options1][options2][options3]
                        } else {
                            ""
                        }
                        //保存用户的选择
                        mSelectRegionInfo = Triple(opt1tx, opt2tx, opt3tx)
                        vRegionContent.text =
                            getString(R.string.mall_region_text, opt1tx, opt2tx, opt3tx)
                    })
                    //处理有些机子的NavigationBar虚拟键在DecorView里面，导致和弹窗重合的问题
                    .setDecorView(fragmentActivity.findViewById(android.R.id.content))
                    //隐藏标题文字
                    .setTitleText("")
                    //隐藏分割线
                    .setDividerColor(resources.getColor(R.color.base_transparent))
                    //设置选中项文字颜色
                    .setTextColorCenter(resources.getColor(R.color.base_black))
                    //设置确认文字的字体颜色
                    .setSubmitColor(resources.getColor(R.color.base_blue))
                    //设置取消文字的字体颜色
                    .setCancelColor(resources.getColor(R.color.base_blue))
                    //设置文字字体大小
                    .setContentTextSize(16)
                    //设置取消、确认的字体大小
                    .setSubCalSize(14)
                    .build<Any>()
                pickerOptions.setPicker(options1Items, options2Items, options3Items)
                pickerOptions.show()
            }, {
                it.printStackTrace()
                toast("获取数据失败，请重试")
            })
    }

    /**
     * 渲染用户收货地址信息
     */
    private fun renderUserAddressInfo() {
        mUserAddressInfo?.run {
            vNameInput.setTextWithSelection(userName)
            vPhoneInput.setTextWithSelection(userPhone)
            //区域选择
            vRegionContent.text = getString(
                R.string.mall_region_text,
                provinceName,
                cityName,
                regionName
            )
            mSelectRegionInfo = Triple(
                provinceName,
                cityName,
                regionName
            )
            vDetailAddressInput.setTextWithSelection(detailAddress)
            //切换按钮
            vSetDefaultAddressSwitch.isChecked = DefaultAddressFlag.IS_DEFAULT.code == defaultFlag
        }
    }

    /**
     * 根据Id，获取收货地址信息
     */
    private fun getUserAddress(
        addressId: String
    ) {
        mMallPresenter.getUserAddress(addressId)
            .doOnSubscribeUi {
                mWaitLoadingController.showWait()
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({
                mWaitLoadingController.hideWait()
                if (handlerErrorCode(it)) {
                    mUserAddressInfo = it.data
                    //渲染收货信息页面
                    renderUserAddressInfo()
                }
            }, {
                it.printStackTrace()
                mWaitLoadingController.hideWait()
                showRequestError()
            })
    }

    /**
     * 保存或更新，用户收货地址
     * @param isAdd 是否是新增
     * @param addressId 地址Id，更新时必传，新增时不要传
     */
    private fun saveOrUpdateUserAddress(isAdd: Boolean, addressId: String) {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        //更新时，才检查是否传了地址Id
        if (!isAdd) {
            if (addressId.isBlank()) {
                return
            }
        }
        //收货人姓名
        val name = vNameInput.text.toString().trim()
        if (name.isBlank()) {
            toast(R.string.mall_name_tip)
            return
        }
        //收货人手机号
        val phone = vPhoneInput.text.toString().trim()
        if (phone.isBlank()) {
            toast(R.string.mall_phone_tip)
            return
        }
        //检查手机号格式
        if (!RegexUtils.isMobileSimple(phone)) {
            toast(R.string.mall_phone_format_no_match)
            return
        }
        //地区信息
        if (mSelectRegionInfo == null) {
            toast(R.string.mall_region_tip)
            return
        }
        //详细地址
        val detailAddress = vDetailAddressInput.text.toString().trim()
        if (detailAddress.isBlank()) {
            toast(R.string.mall_detail_address_tip)
            return
        }
        //是否设备为默认地址
        val defaultFlag = if (vSetDefaultAddressSwitch.isChecked) {
            DefaultAddressFlag.IS_DEFAULT
        } else {
            DefaultAddressFlag.NO_DEFAULT
        }
        mSelectRegionInfo?.let {
            if (isAdd) {
                mMallPresenter.saveUserAddress(
                    userId,
                    name,
                    phone,
                    defaultFlag,
                    it.first,
                    it.second,
                    it.third,
                    detailAddress
                )
            } else {
                mMallPresenter.updateUserAddress(
                    addressId,
                    userId,
                    name,
                    phone,
                    defaultFlag,
                    it.first,
                    it.second,
                    it.third,
                    detailAddress
                )
            }.doOnSubscribeUi {
                mWaitLoadingController.showWait()
            }.ioToMain().lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    mWaitLoadingController.hideWait()
                    if (handlerErrorCode(httpModel)) {
                        if (isAdd) {
                            toast(R.string.base_save_success)
                        } else {
                            toast(R.string.base_update_success)
                        }
                        AppBroadcastManager.sendBroadcast(AppConstant.Action.MALL_USER_ADDRESS_REFRESH)
                        fragmentActivity.finish()
                    }
                }, { error ->
                    error.printStackTrace()
                    showRequestError()
                    mWaitLoadingController.hideWait()
                })
        }
    }

    /**
     * 删除收货地址
     */
    private fun deleteAddress() {
        if (mAddressId.isBlank()) {
            return
        }
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        AlertDialog.Builder(fragmentActivity)
            .setMessage(R.string.mall_delete_user_address_confirm)
            .setPositiveButton(R.string.base_confirm) { _, _ ->
                //请求删除
                mMallPresenter.deleteAddress(userId, mAddressId)
                    .doOnSubscribeUi {
                        mWaitLoadingController.showWait()
                    }
                    .ioToMain()
                    .lifecycle(lifecycleOwner)
                    .subscribe({
                        mWaitLoadingController.hideWait()
                        if (handlerErrorCode(it)) {
                            toast(R.string.base_delete_success)
                            AppBroadcastManager.sendBroadcast(AppConstant.Action.MALL_USER_ADDRESS_REFRESH)
                            fragmentActivity.finish()
                        }
                    }, {
                        it.printStackTrace()
                        showRequestError()
                        mWaitLoadingController.hideWait()
                    })
            }
            .setNegativeButton(R.string.base_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}