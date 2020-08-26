package com.zh.android.base.ext

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.zh.android.base.util.SoftKeyBoardUtil
import com.zh.android.base.util.listener.DelayOnClickListener
import com.zh.android.base.util.rx.RxUtil
import io.reactivex.Observable


/**
 * <b>Package:</b> com.linghit.base.ext <br>
 * <b>Create Date:</b> 2019-06-29  09:28 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> View拓展 <br>
 */

/**
 * 测量View
 */
fun measureView(target: View): Pair<Int, Int> {
    val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    target.measure(w, h)
    val width = target.measuredWidth
    val height = target.measuredHeight
    return Pair(width, height)
}

/**
 * 给View设置带有防暴击的监听
 */
fun View.click(listener: (view: View) -> Unit) {
    this.setOnClickListener(DelayOnClickListener(listener))
}

/**
 * 长按
 */
fun View.longClick(listener: (view: View) -> Boolean) {
    this.setOnLongClickListener {
        listener(it)
    }
}

fun <T : View> androidx.fragment.app.Fragment.findView(id: Int): T {
    this.view ?: throw NullPointerException("Fragment view must be not null")
    return this.view!!.findViewById(id)
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun View.setInVisible() {
    this.visibility = View.INVISIBLE
}

fun TextView.setTextWithDefault(text: CharSequence?, default: CharSequence = "") {
    if (text.isNull()) {
        this.text = default
    } else {
        this.text = text
    }
}

/**
 * 给TextView设置text时去掉null字样
 */
var TextView.notNullText: String?
    get() = text.toString()
    set(value) {
        text = value?.replace("null", "") ?: ""
    }

/**
 * 判断ViewGroup是否有子View
 */
fun ViewGroup.hasChildView(): Boolean {
    return childCount > 0
}

/**
 * 获取ViewGroup所有的子View集合
 */
fun ViewGroup.getAllChildView(): MutableList<View> {
    return mutableListOf<View>().apply {
        val count = childCount
        for (viewIndex in 0 until count) {
            add(getChildAt(viewIndex))
        }
    }
}

/**
 * 获取ViewGroup的第一个子View
 */
val ViewGroup.getFirstChildView: View?
    get() {
        return if (childCount > 0) {
            getChildAt(0)
        } else {
            null
        }
    }

/**
 * 获取最后一个子View
 */
val ViewGroup.getLastChildView: View?
    get() {
        return if (childCount > 0) {
            getChildAt(this.childCount - 1)
        } else {
            null
        }
    }

/**
 * 更新MarginLeft
 */
fun ViewGroup.MarginLayoutParams.setMarginLeft(newLeft: Int) {
    setMargins(newLeft, topMargin, rightMargin, bottomMargin)
}

/**
 * 更新MarginRight
 */
fun ViewGroup.MarginLayoutParams.setMarginRight(newRight: Int) {
    setMargins(leftMargin, topMargin, newRight, bottomMargin)
}

/**
 * 更新MarginTop
 */
fun ViewGroup.MarginLayoutParams.setMarginTop(newTop: Int) {
    setMargins(leftMargin, newTop, rightMargin, bottomMargin)
}

/**
 * 更新MarginBottom
 */
fun ViewGroup.MarginLayoutParams.setMarginBottom(newBottom: Int) {
    setMargins(leftMargin, topMargin, rightMargin, newBottom)
}

/**
 * 更新PaddingTop
 */
fun View.setPaddingTop(newTop: Int) {
    setPadding(paddingLeft, newTop, paddingRight, paddingBottom)
}

/**
 * 更新PaddingTop
 */
fun View.setPaddingLeft(newLeft: Int) {
    setPadding(newLeft, paddingTop, paddingRight, paddingBottom)
}

/**
 * 更新PaddingRight
 */
fun View.setPaddingRight(newRight: Int) {
    setPadding(paddingLeft, paddingTop, newRight, paddingBottom)
}

/**
 * 更新PaddingBottom
 */
fun View.setPaddingBottom(newBottom: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, newBottom)
}

/**
 * 获取ImageView上的图像
 */
@SuppressWarnings
fun ImageView.getImageBitmap(): Bitmap? {
    this.isDrawingCacheEnabled = true
    val bitmap = this.drawingCache
    this.isDrawingCacheEnabled = false
    return bitmap
}

fun androidx.fragment.app.Fragment.setStatusBarBlack() {
    activity?.run {
        setStatusBarBlack()
    }
}

/**
 * 移除所有CompoundDrawables
 */
fun TextView.removeAllCompoundDrawables() {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}

fun TextView.setDrawableLeft(drawableResId: Int) {
    setDrawableLeft(context.resources.getDrawable(drawableResId)!!)
}

/**
 * 动态设置TextView的DrawableLeft
 */
fun TextView.setDrawableLeft(drawable: Drawable) {
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    val originCompoundDrawables = compoundDrawables
    val drawableTop = originCompoundDrawables[1].apply {
        setBounds()
    }
    val drawableRight = originCompoundDrawables[2].apply {
        setBounds()
    }
    val drawableBottom = originCompoundDrawables[3].apply {
        setBounds()
    }
    setCompoundDrawablesWithIntrinsicBounds(drawable, drawableTop, drawableRight, drawableBottom)
}

fun TextView.setDrawableRight(drawableResId: Int) {
    setDrawableRight(context.resources.getDrawable(drawableResId)!!)
}

/**
 * 动态设置TextView的DrawableRight
 */
fun TextView.setDrawableRight(drawable: Drawable) {
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    val originCompoundDrawables = compoundDrawables
    val drawableLeft = originCompoundDrawables[0].apply {
        setBounds()
    }
    val drawableTop = originCompoundDrawables[1].apply {
        setBounds()
    }
    val drawableBottom = originCompoundDrawables[3].apply {
        setBounds()
    }
    setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawable, drawableBottom)
}

fun Drawable?.setBounds() {
    this?.run {
        setBounds(0, 0, minimumWidth, minimumHeight)
    }
}

/**
 * Rx方式监听文字改变
 */
fun EditText.textChangesWithDebounce(timeout: Long = 200): Observable<CharSequence> {
    return RxUtil.textChangesWithDebounce(this, timeout)
        .filter {
            it.isNotEmpty()
        }
}

/**
 * 显示软件盘
 */
fun EditText.showKeyboard() {
    this.requestFocus()
    SoftKeyBoardUtil.showKeyboard(this)
}

/**
 * 隐藏软键盘
 */
fun EditText.hideKeyboard() {
    SoftKeyBoardUtil.hideKeyboard(this)
}