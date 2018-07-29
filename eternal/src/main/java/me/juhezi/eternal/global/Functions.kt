package me.juhezi.eternal.global

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import me.juhezi.eternal.BuildConfig
import me.juhezi.eternal.base.BaseApplication
import me.juhezi.eternal.builder.buildUIHandler
import java.util.*

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */

fun run(handler: Handler, runnable: Runnable) = handler.post(runnable)

fun runInUIThread(runnable: Runnable) = buildUIHandler().post(runnable)

fun isDebug() = BuildConfig.DEBUG

fun logi(message: String) {
    if (isDebug()) {
        Log.i(TAG, message)
    }
}

fun logd(message: String) {
    if (isDebug()) {
        Log.d(TAG, message)
    }
}

@SuppressLint("MissingPermission", "HardwareIds")
fun getIMEI(): String {
    val telManager = BaseApplication.getApplicationContext()
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telManager.deviceId
}

fun log(tag: String, enable: Boolean = true): (String) -> Unit = { message: String ->
    if (enable) {
        Log.i(tag, message)
    }
}

/**
 * 设置点击特效
 * 必须要实现点击事件
 */
fun applyClickEffect(view: View, effect: (View, Boolean) -> Unit) {
    view.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                effect(view, true)
            MotionEvent.ACTION_UP ->
                effect(view, false)
            MotionEvent.ACTION_CANCEL ->
                effect(view, false)
        }
        false
    }
}

fun applyClickEffect(views: Array<View>, effect: (View, Boolean) -> Unit) {
    views.forEach {
        applyClickEffect(it, effect)
    }
}

fun createAlphaEffect(alpha: Float) = { view: View, enable: Boolean ->
    if (enable) {
        view.alpha = alpha
    } else {
        view.alpha = 1f
    }
}

fun createScaleEffect(scale: Float) = { view: View, enable: Boolean ->
    if (enable) {
        view.scaleX = scale
        view.scaleY = scale
    } else {
        view.scaleX = 1f
        view.scaleY = 1f
    }
}

fun generateRandomID() = UUID.randomUUID().toString()