package com.zh.android.chat.moment.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.takephoto.RxTakePhoto
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.moment.R
import com.zh.android.chat.moment.http.MomentPresenter
import com.zh.android.chat.moment.item.MomentAddPublishImageViewBinder
import com.zh.android.chat.moment.item.MomentPublishImageViewBinder
import com.zh.android.chat.moment.model.AddPublishImageModel
import com.zh.android.chat.moment.model.MomentPublishImageModel
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.getLoginService
import com.zh.android.chat.service.module.base.UploadPresenter
import io.reactivex.Observable
import kotterknife.bindView
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @author wally
 * @date 2020/09/20
 * 动态发布
 */
class MomentPublishFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vInput: EditText by bindView(R.id.input)
    private val vImageList: RecyclerView by bindView(R.id.image_list)

    /**
     * 添加图标
     */
    private val mAddPublishImageModel = AddPublishImageModel(MAX_IMAGE_COUNT)

    private val mListItems by lazy {
        Items()
    }
    private val mListAdapter by lazy {
        MultiTypeAdapter(mListItems).apply {
            //增加图片
            register(AddPublishImageModel::class.java, MomentAddPublishImageViewBinder { item ->
                chooseImage(item.needCount)
            })
            //图片条目
            register(
                MomentPublishImageModel::class.java,
                MomentPublishImageViewBinder({ _, item ->
                    //删除图片
                    deleteImage(item)
                }, { position, item ->
                    toast("跳转到图片预览")
                })
            )
        }
    }

    private val mUploadPresenter by lazy {
        UploadPresenter()
    }
    private val mMomentPresenter by lazy {
        MomentPresenter()
    }

    companion object {
        /**
         * 最多图片数量
         */
        private const val MAX_IMAGE_COUNT = 6

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
     * 选择了图片
     */
    private fun chooseImage(needCount: Int) {
        //当前图片数量
        val currentCount = getAllImageItemModel().size
        //计算剩余数量
        val residueSelectPicCount = needCount - currentCount
        AlertDialog.Builder(fragmentActivity)
            .setItems(
                arrayOf(
                    getString(R.string.base_take_photo),
                    getString(R.string.base_take_gallery)
                )
            ) { _, which ->
                val rxTakePhoto = RxTakePhoto()
                val observable = when (which) {
                    0 -> {
                        //拍照
                        rxTakePhoto.startByCamera(fragmentActivity, false)
                    }
                    1 -> {
                        //选择图库
                        rxTakePhoto.startByGallery(fragmentActivity, residueSelectPicCount, false)
                    }
                    else -> Observable.empty()
                }
                observable
                    .filter {
                        !it.isTakeCancel
                    }
                    .flatMap {
                        //上传图片
                        mUploadPresenter.uploadMultipleImage(fragmentActivity, it.imgPaths)
                    }
                    .lifecycle(lifecycleOwner)
                    .subscribe({ urlList ->
                        //添加图片到添加图标的后面
                        var index = mListItems.size - 1
                        //第一次添加时，会小于0
                        if (index < 0) {
                            index = 0
                        }
                        mListItems.addAll(
                            index,
                            urlList.map {
                                MomentPublishImageModel(it)
                            }
                        )
                        //计算图片数量是否满了，没有满，则显示默认添加图片，满了则不显示
                        if (getAllImageItemModel().size == MAX_IMAGE_COUNT) {
                            mListItems.remove(mAddPublishImageModel)
                        }
                        mListAdapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
            }
            .create()
            .show()
    }

    /**
     * 删除图片
     */
    private fun deleteImage(item: MomentPublishImageModel) {
        mListItems.remove(item)
        //删除后，如果没有默认图片，那么加上
        if (mListItems.filterIsInstance<AddPublishImageModel>().isEmpty()) {
            mListItems.add(mAddPublishImageModel)
        }
        mListAdapter.notifyDataSetChanged()
    }

    /**
     * 获取当前所有图片条目的模型
     */
    private fun getAllImageItemModel(): List<MomentPublishImageModel> {
        return mListItems.filterIsInstance<MomentPublishImageModel>()
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
        val imageUrls = getAllImageItemModel().map {
            it.url
        }
        //只能单发文字，或者单发图片，不能2个都没有
        if (inputText.isBlank() && imageUrls.isEmpty()) {
            toast(R.string.moment_publish_input_empty_tip)
            return
        }
        val observable = if (inputText.isNotBlank() && imageUrls.isEmpty()) {
            //单发文字
            mMomentPresenter.publishMoment(userId, inputText)
        } else if (inputText.isBlank() && imageUrls.isNotEmpty()) {
            //单发图片
            mMomentPresenter.publishMoment(userId, pictures = imageUrls)
        } else if (inputText.isNotBlank() && imageUrls.isNotEmpty()) {
            //既发文字，也发图片
            mMomentPresenter.publishMoment(userId, inputText, imageUrls)
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