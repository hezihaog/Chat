package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.draggable.library.extension.ImageViewerHelper
import com.linghit.base.util.argument.bindArgument
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.RecyclerViewItemTouchCallback
import com.zh.android.base.util.loading.WaitLoadingController
import com.zh.android.base.util.takephoto.RxTakePhoto
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.enums.PublicFlag
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.MomentAddPublishMediaViewBinder
import com.zh.android.chat.moment.item.MomentPublishMediaViewBinder
import com.zh.android.chat.moment.model.AddPublishMediaModel
import com.zh.android.chat.moment.model.MomentPublishMediaModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.base.UploadPresenter
import com.zh.android.chat.service.module.moment.enums.MomentPublishType
import io.reactivex.Observable
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*

/**
 * @author wally
 * @date 2020/09/20
 * 动态发布
 */
class MomentPublishFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vInput: EditText by bindView(R.id.input)
    private val vImageList: RecyclerView by bindView(R.id.image_list)
    private val vPrivacyLayout: View by bindView(R.id.privacy_layout)
    private val vSelectPrivacySymbol: ImageView by bindView(R.id.select_privacy_symbol)

    /**
     * 是否选中了私密，默认不选中
     */
    private var mIsSelectPrivacy: Boolean = false

    /**
     * 动态发布类型
     */
    private val mPublishType by bindArgument(
        AppConstant.Key.MOMENT_PUBLISH_TYPE,
        MomentPublishType.TEXT_IMAGE
    )

    /**
     * 添加图标
     */
    private val mAddPublishImageModel = AddPublishMediaModel(
        AppConstant.Config.MAX_IMAGE_COUNT
    )

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //增加资源图标
            register(AddPublishMediaModel::class.java, MomentAddPublishMediaViewBinder { item ->
                chooseMedia(item.needCount)
            })
            //资源条目
            register(
                MomentPublishMediaModel::class.java,
                MomentPublishMediaViewBinder({ _, item ->
                    //删除资源
                    deleteImage(item)
                }, { position, _ ->
                    //预览资源
                    val mediaUrls = getAllMediaItemModel().map {
                        it.url
                    }
                    ImageViewerHelper.showImages(
                        fragmentActivity,
                        mediaUrls, index = position
                    )
                })
            )
        }
    }

    private val mWaitController by lazy {
        WaitLoadingController(fragmentActivity, lifecycleOwner)
    }

    private val mUploadPresenter by lazy {
        UploadPresenter()
    }
    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    private val mRxTakePhoto by lazy {
        RxTakePhoto()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): MomentPublishFragment {
            val fragment = MomentPublishFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.moment_publish_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.moment_module_name)
            addRightTextButton(R.string.moment_publish, R.id.moment_publish).click {
                publishMoment()
            }
        }
        vImageList.apply {
            layoutManager = GridLayoutManager(fragmentActivity, 3)
            adapter = mListAdapter
            //拽托帮助类
            val itemTouchHelper = ItemTouchHelper(
                RecyclerViewItemTouchCallback(object :
                    RecyclerViewItemTouchCallback.OnItemMoveCallback {
                    override fun isCanMove(
                        current: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        //2种ViewType类型相同的条目才可以互换
                        return current.itemViewType == target.itemViewType
                    }

                    override fun onItemMove(fromPosition: Int, toPosition: Int) {
                        //交换数据
                        mRxTakePhoto.movePhotoPosition(fragmentActivity, fromPosition, toPosition)
                            .lifecycle(lifecycleOwner)
                            .subscribe({
                                //移动Rv数据，和选择图片的数据联动
                                Collections.swap(mListItems, fromPosition, toPosition)
                                mListAdapter.notifyItemMoved(fromPosition, toPosition)
                            }, {
                                it.printStackTrace()
                                showRequestDataWrong()
                            })
                    }
                })
            )
            itemTouchHelper.attachToRecyclerView(this)
        }
        //单文字，隐藏图片、视频选择按钮
        if (mPublishType == MomentPublishType.SINGLE_TEXT) {
            vImageList.setGone()
        } else {
            vImageList.setVisible()
        }
        //是否私密
        vPrivacyLayout.click {
            toggleSelectPrivacy()
        }
        renderSelectPrivacy(mIsSelectPrivacy)
    }

    /**
     * 切换选中私密
     */
    private fun toggleSelectPrivacy() {
        val newState = !mIsSelectPrivacy
        mIsSelectPrivacy = newState
        renderSelectPrivacy(mIsSelectPrivacy)
    }

    /**
     * 渲染私密选中状态
     */
    private fun renderSelectPrivacy(isPrivacy: Boolean) {
        vSelectPrivacySymbol.apply {
            setImageResource(
                if (isPrivacy) {
                    R.drawable.base_select
                } else {
                    R.drawable.base_un_select
                }
            )
        }
    }

    override fun setData() {
        super.setData()
        renderDefault()
    }

    /**
     * 渲染默认视图
     */
    private fun renderDefault() {
        mListItems.add(mAddPublishImageModel)
        mListAdapter.notifyDataSetChanged()
    }

    /**
     * 选择图片或视频
     */
    private fun chooseMedia(needCount: Int) {
        //当前图片数量
        val currentCount = getAllMediaItemModel().size
        //计算剩余数量
        val residueSelectPicCount = needCount - currentCount
        AlertDialog.Builder(fragmentActivity)
            .setItems(
                arrayOf(
                    getString(R.string.base_take_photo),
                    getString(R.string.base_take_gallery)
                )
            ) { _, which ->
                val observable = when (which) {
                    0 -> {
                        //拍照
                        when (mPublishType) {
                            MomentPublishType.TEXT_IMAGE -> {
                                mRxTakePhoto.takeImageByCamera(fragmentActivity, false)
                            }
                            MomentPublishType.TEXT_VIDEO -> {
                                mRxTakePhoto.takeVideoByCamera(fragmentActivity)
                            }
                            else -> Observable.empty()
                        }
                    }
                    1 -> {
                        //选择图库
                        when (mPublishType) {
                            MomentPublishType.TEXT_IMAGE -> {
                                mRxTakePhoto.takeImageByGallery(
                                    fragmentActivity,
                                    //图片可以多张
                                    residueSelectPicCount,
                                    false
                                )
                            }
                            MomentPublishType.TEXT_VIDEO -> {
                                mRxTakePhoto.takeVideoByGallery(
                                    fragmentActivity,
                                    //视频只能每次发表一个
                                    1
                                )
                            }
                            else -> Observable.empty()
                        }
                    }
                    else -> Observable.empty()
                }
                observable
                    .flatMap {
                        if (it.isTakeCancel) {
                            mWaitController.hideWait()
                            Observable.empty()
                        } else {
                            Observable.just(it)
                        }
                    }
                    .doOnSubscribeUi {
                        mWaitController.showWait()
                    }
                    .flatMap {
                        when (mPublishType) {
                            //上传图片
                            MomentPublishType.TEXT_IMAGE -> {
                                mUploadPresenter.uploadMultipleImage(it.imgPaths)
                            }
                            //上传视频
                            MomentPublishType.TEXT_VIDEO -> {
                                mUploadPresenter.uploadFiles(it.imgPaths)
                            }
                            else -> {
                                Observable.empty()
                            }
                        }
                    }
                    .lifecycle(lifecycleOwner)
                    .subscribe({ urlList ->
                        mWaitController.hideWait()
                        //添加图片到添加图标的后面
                        var index = mListItems.size - 1
                        //第一次添加时，会小于0
                        if (index < 0) {
                            index = 0
                        }
                        mListItems.addAll(
                            index,
                            urlList.map {
                                MomentPublishMediaModel(it)
                            }
                        )
                        //计算图片数量是否满了，没有满，则显示默认添加图片，满了则不显示
                        if (getAllMediaItemModel().size == AppConstant.Config.MAX_IMAGE_COUNT) {
                            mListItems.remove(mAddPublishImageModel)
                        }
                        mListAdapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                        mWaitController.hideWait()
                        showRequestError()
                    }, {
                        mWaitController.hideWait()
                    })
            }
            .create()
            .show()
    }

    /**
     * 删除图片
     */
    private fun deleteImage(item: MomentPublishMediaModel) {
        mListItems.remove(item)
        //删除后，如果没有默认图片，那么加上
        if (mListItems.filterIsInstance<AddPublishMediaModel>().isEmpty()) {
            mListItems.add(mAddPublishImageModel)
        }
        mListAdapter.notifyDataSetChanged()
    }

    /**
     * 获取当前所有媒体资源条目的模型
     */
    private fun getAllMediaItemModel(): List<MomentPublishMediaModel> {
        return mListItems.filterIsInstance<MomentPublishMediaModel>()
            .toList()
    }

    /**
     * 发布动态
     */
    private fun publishMoment() {
        val userId = getLoginService()?.getUserId()
        if (userId.isNullOrBlank()) {
            return
        }
        val inputText = vInput.text.toString().trim()
        val mediaUrls = getAllMediaItemModel().map {
            it.url
        }
        //只能单发文字，或者单发图片，不能2个都没有
        if (inputText.isBlank() && mediaUrls.isEmpty()) {
            toast(R.string.moment_publish_input_empty_tip)
            return
        }
        //是否私密
        val publicFlag = if (mIsSelectPrivacy) {
            PublicFlag.PRIVACY
        } else {
            PublicFlag.PUBLIC
        }
        val observable = if (inputText.isNotBlank() && mediaUrls.isEmpty()) {
            //单发文字
            mMomentPresenter.publishMoment(userId, inputText, publicFlag = publicFlag)
        } else if (inputText.isBlank() && mediaUrls.isNotEmpty()) {
            //单发资源
            when (mPublishType) {
                MomentPublishType.TEXT_IMAGE -> {
                    mMomentPresenter.publishMoment(
                        userId,
                        pictures = mediaUrls,
                        publicFlag = publicFlag
                    )
                }
                MomentPublishType.TEXT_VIDEO -> {
                    mMomentPresenter.publishMoment(
                        userId,
                        videos = mediaUrls,
                        publicFlag = publicFlag
                    )
                }
                else -> {
                    null
                }
            }
        } else if (inputText.isNotBlank() && mediaUrls.isNotEmpty()) {
            //既发文字，也发资源
            when (mPublishType) {
                MomentPublishType.TEXT_IMAGE -> {
                    mMomentPresenter.publishMoment(
                        userId,
                        inputText,
                        pictures = mediaUrls,
                        publicFlag = publicFlag
                    )
                }
                MomentPublishType.TEXT_VIDEO -> {
                    mMomentPresenter.publishMoment(
                        userId,
                        inputText,
                        videos = mediaUrls,
                        publicFlag = publicFlag
                    )
                }
                else -> {
                    null
                }
            }
        } else {
            null
        }
        observable?.let {
            it.ioToMain()
                .lifecycle(lifecycleOwner)
                .subscribe({ httpModel ->
                    if (handlerErrorCode(httpModel)) {
                        //发送广播，刷新动态列表
                        AppBroadcastManager.sendBroadcast(AppConstant.Action.MOMENT_PUBLISH_SUCCESS)
                        toast(R.string.moment_publish_success)
                        fragmentActivity.finish()
                    }
                }, { error ->
                    error.printStackTrace()
                    showRequestError()
                })
        }
    }
}