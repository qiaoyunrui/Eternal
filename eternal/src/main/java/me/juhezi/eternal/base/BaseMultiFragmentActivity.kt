package me.juhezi.eternal.base

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import me.juhezi.eternal.R
import java.util.*

/**
 * 盛放多个 Fragment 的 Activity 基类
 * 提供一个默认的布局
 */
abstract class BaseMultiFragmentActivity : BaseActivity() {

    private val mFragmentStack = Stack<BaseFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayoutRes())
    }

    @LayoutRes
    open fun getActivityLayoutRes(): Int = R.layout.activity_single_fragment

    @IdRes
    open fun getFragmentContainerId() = R.id.vg_activity_single_fragment

    private fun getShowingFragment(): BaseFragment? =
            if (mFragmentStack.empty()) {
                null
            } else {
                mFragmentStack.peek()
            }

    /**
     * 移除当前显示的 Fragment
     */
    private fun removeFragment() {

    }

    /**
     * Activity 的返回事件
     * @return true: 对返回事件进行拦截 false：对返回事件不进行拦截
     * 默认返回 false，不进行拦截
     */
    open fun handleBackPressed(): Boolean {
        // 默认逻辑
        // 如果当前 fragment 栈里只有一个 fragment 的时候，不进行拦截，执行 Activity 的返回操作
        if (mFragmentStack.size > 1) {
            removeFragment()
            return true
        } else {
            return false
        }
    }

    override fun onBackPressed() {
        val fragment = getShowingFragment()
        if (fragment != null && fragment.handleBackPressed()) {     // 前台有 Fragment，并且前台 Fragment 拦截了返回事件
            return
        }
        if (handleBackPressed()) {
            return
        }
        super.onBackPressed()
    }
    
}