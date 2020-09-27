package com.zh.android.chat.mine.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.linghit.base.util.argument.bindArgument
import com.luck.picture.lib.photoview.PhotoView
import com.zh.android.base.constant.ApiUrl
import com.zh.android.base.core.BaseFragment
import com.zh.android.base.ext.*
import com.zh.android.base.util.AppBroadcastManager
import com.zh.android.base.util.takephoto.RxTakePhoto
import com.zh.android.base.widget.TopBar
import com.zh.android.chat.mine.R
import com.zh.android.chat.mine.http.MinePresenter
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.module.base.CompressPresenter
import io.reactivex.Observable
import kotterknife.bindView
import java.io.File

/**
 * @author wally
 * @date 2020/08/31
 * 修改头像
 */
class ModifyAvatarFragment : BaseFragment() {
    private val vTopBar: TopBar by bindView(R.id.top_bar)
    private val vPhotoView: PhotoView by bindView(R.id.photo_view)

    private val mUserId by bindArgument(AppConstant.Key.USER_ID, "")
    private val mAvatarUrl by bindArgument(AppConstant.Key.AVATAR_URL, "")

    private val mMinePresenter by lazy {
        MinePresenter()
    }

    private val mUploadPresenter by lazy {
        CompressPresenter()
    }

    companion object {
        fun newInstance(args: Bundle? = Bundle()): ModifyAvatarFragment {
            val fragment = ModifyAvatarFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onInflaterViewId(): Int {
        return R.layout.mine_modify_avatar_fragment
    }

    override fun onBindView(view: View?) {
        vTopBar.apply {
            addLeftBackImageButton().click {
                fragmentActivity.finish()
            }
            setTitle(R.string.mine_modify_avatar)
            addRightTextButton(R.string.mine_modify, R.id.mine_topview_modify).click {
                showOption()
            }
        }
    }

    override fun setData() {
        super.setData()
        vPhotoView.loadUrlImage(ApiUrl.getFullFileUrl(mAvatarUrl))
    }

    /**
     * 显示选项
     */
    private fun showOption() {
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
                        rxTakePhoto.startByCamera(fragmentActivity, true)
                    }
                    1 -> {
                        //选择图库
                        rxTakePhoto.startByGallery(fragmentActivity, true)
                    }
                    else -> Observable.empty()
                }
                observable.lifecycle(lifecycleOwner)
                    .subscribe({
                        if (!it.isTakeCancel) {
                            val imagePath = it.imgPaths[0]
                            //上传图片
                            modifyAvatar(imagePath)
                        }
                    }, {
                        it.printStackTrace()
                    })
            }
            .create()
            .show()
    }

    /**
     * 修改头像
     */
    private fun modifyAvatar(filePath: String) {
        mUploadPresenter.compressImage(fragmentActivity, filePath)
            .flatMap { path ->
                mMinePresenter.uploadAvatar(mUserId, File(path))
            }
            .ioToMain()
            .lifecycle(lifecycleOwner)
            .subscribe({ httpModel ->
                if (handlerErrorCode(httpModel)) {
                    val newAvatarUrl = ApiUrl.getFullFileUrl(httpModel.data?.picNormal)
                    vPhotoView.loadUrlImage(newAvatarUrl)
                    //通知更新头像
                    AppBroadcastManager.sendBroadcast(AppConstant.Action.UPDATE_AVATAR,
                        Intent().apply {
                            putExtra(AppConstant.Key.AVATAR_URL, newAvatarUrl)
                        }
                    )
                }
            }, {
                it.printStackTrace()
                showRequestError()
            })
    }
}