package me.juhezi.time

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_time.*
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.eternal.extension.i
import me.juhezi.eternal.global.formatTime
import java.util.*

class TimeActivity : BaseActivity() {

    private var timer: Timer? = null
    private val destination: Date = Date(2018 - 1900, 11, 22)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)
        toolBarVisibility = false
        i(destination.toGMTString())
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    /**
     * 计算剩余时间的函数
     */
    private fun getRemaining(): String {
        val diff = destination.time - System.currentTimeMillis()
        return formatTime(diff, "D 天 HH:mm:ss")
    }

    private fun startTimer() {
        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tv_time_remaining.text = getRemaining()
                }
            }
        }
        if (timer == null) {
            timer = Timer()
        }
        timer!!.schedule(timerTask, 0L, 500L)
    }
}
