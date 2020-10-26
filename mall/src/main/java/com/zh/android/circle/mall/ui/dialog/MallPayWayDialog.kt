package com.zh.android.circle.mall.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.zh.android.base.ext.click
import com.zh.android.base.ui.dialog.bottomsheet.BaseBottomSheetDialog
import com.zh.android.circle.mall.R

/**
 * @author wally
 * @date 2020/10/25
 * 支付方式弹窗
 */
class MallPayWayDialog(context: Context, owner: LifecycleOwner) :
    BaseBottomSheetDialog(context, owner) {
    private lateinit var vRoot: View
    private lateinit var vClose: View
    private lateinit var vAlipay: View
    private lateinit var vWxpay: View

    private var mCallback: Callback? = null

    override fun onStart() {
        super.onStart()
        //点击阴影不关闭
        setCanceledOnTouchOutside(false)
    }

    override fun onCreateContentView(inflater: LayoutInflater?, parent: ViewGroup?): View {
        return inflater!!.inflate(R.layout.mall_pay_way_dialog, parent, false)
    }

    override fun onBindView(view: View?) {
        super.onBindView(view)
        view?.let {
            findView(it)
            bindView()
        }
    }

    private fun findView(view: View) {
        vRoot = view.findViewById(R.id.root)
        vClose = view.findViewById(R.id.close)
        vAlipay = view.findViewById(R.id.alipay)
        vWxpay = view.findViewById(R.id.wxpay)
    }

    private fun bindView() {
        vRoot.click {
            //不做关闭处理
            //dismiss()
        }
        vClose.click {
            dismiss()
        }
        vAlipay.click {
            mCallback?.onClickAlipay()
            dismiss()
        }
        vWxpay.click {
            mCallback?.onClickWxpay()
            dismiss()
        }
    }

    interface Callback {
        /**
         * 点击了支付宝支付
         */
        fun onClickAlipay()

        /**
         * 点击了微信支付
         */
        fun onClickWxpay()
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }
}