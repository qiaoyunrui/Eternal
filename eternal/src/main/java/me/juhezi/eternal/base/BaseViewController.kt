package me.juhezi.eternal.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator

/**
 * 控制基本 View 的显示与消失
 * Created by shixi_yunrui on 2018/1/16.
 */
class BaseViewController() {

    companion object {
        const val STATUS_CONTENT = "content"     //内容
        const val STATUS_LOADING = "loading"     //加载 View
        const val STATUS_EMPTY = "empty"       //空界面
        const val STATUS_ERROR = "error"       //错误界面

        const val STATUS_NONE = "none"

        const val STATUS_INIT = STATUS_CONTENT    //初始界面
        const val ANIM_DURATION = 500L
    }

    var currentStatus = STATUS_INIT

    private var mViewMap = HashMap<String, View>()  //管理所有的 View

    private var showAnim = ValueAnimator.ofFloat(0f, 1f)
    private var hideAnim = ValueAnimator.ofFloat(1f, 0f)

    init {
        configAnim()
    }

    fun load(status: String, view: View?): Boolean {
        if (STATUS_NONE == status) return false
        if (mViewMap.containsKey(status)) return false
        if (view == null) return false
        mViewMap[status] = view
        return true
    }

    fun unload(status: String): Boolean {
        if (!mViewMap.containsKey(status)) return false
        mViewMap.remove(status)
        return true
    }

    /**
     * 显示 status 对应的 View
     */
    fun show(status: String, anim: Boolean) {
        val preStatus = currentStatus
        currentStatus = status
        val preView = mViewMap[preStatus]
        val currentView = mViewMap[status]
        hideView(preView, anim)
        showView(currentView, anim)
    }

    /**
     * 隐藏 status 对应的 View
     */
    fun hide(status: String, anim: Boolean) {
        hideView(mViewMap[status], anim)
    }

    fun hideAll() {
        mViewMap.forEach {
            it.value.visibility = View.GONE
        }
        currentStatus = STATUS_NONE
    }

    private fun showView(view: View?, anim: Boolean) {
        if (view == null) return
        if (view.visibility == View.VISIBLE) return
        if (anim) {
            applyShowAnim(view, showAnim)
            showAnim.start()
        } else {
            view.visibility = View.VISIBLE
        }
    }

    private fun hideView(view: View?, anim: Boolean) {
        if (view == null) return
        if (view.visibility != View.VISIBLE) return
        if (anim) {
            applyHideAnim(view, hideAnim)
            hideAnim.start()
        } else {
            view.visibility = View.GONE
        }
    }

    fun size() = mViewMap.size

    private fun configAnim() {
        //设置动画间隔
        showAnim.duration = ANIM_DURATION
        hideAnim.duration = ANIM_DURATION
        //设置动画差值器
        showAnim.interpolator = AccelerateInterpolator()
        hideAnim.interpolator = AccelerateInterpolator()
    }

    private fun applyShowAnim(view: View, anim: ValueAnimator) {
        showAnim.removeAllUpdateListeners()
        showAnim.removeAllListeners()
        showAnim.addUpdateListener {
            view.alpha = it.animatedValue as Float
        }
        showAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.visibility = View.VISIBLE
            }
        })
    }

    private fun applyHideAnim(view: View, anim: ValueAnimator) {
        hideAnim.removeAllUpdateListeners()
        hideAnim.removeAllListeners()
        hideAnim.addUpdateListener {
            view.alpha = it.animatedValue as Float
        }
        hideAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                view.visibility = View.GONE
            }
        })
    }

    fun init() {
        mViewMap.forEach {
            it.value.visibility = if (it.key == currentStatus) View.VISIBLE else View.GONE
        }
    }

}