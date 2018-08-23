package me.juhezi.time.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_time.*
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.eternal.builder.buildTypeface
import me.juhezi.eternal.global.荷包鼓鼓
import me.juhezi.time.R
import java.util.*

class TimeActivity : BaseActivity() {

    companion object {

        /**
         * todo 下沉到基础模块
         * 计算剩余时间的函数
         */
        fun getRemaining(destination: Calendar): String {
            val current = Calendar.getInstance()
            val diff = destination.timeInMillis - current.timeInMillis
            val day = diff / (1000 * 60 * 60 * 24)
            val remindTimeForHour = diff % (1000 * 60 * 60 * 24)
            val hour = remindTimeForHour / (1000 * 60 * 60)
            val remindTimeForMinute = remindTimeForHour % (1000 * 60 * 60)
            val minute = remindTimeForMinute / (1000 * 60)
            val remindTimeForSecond = remindTimeForMinute % (1000 * 60)
            val second = remindTimeForSecond / 1000
            return "$day 天 $hour:$minute:$second"
        }
    }

    private var timer: Timer? = null
    private val destination: Calendar = Calendar.getInstance()

    // 2018 11 22
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)
        toolBarVisibility = false
        destination.time = Date(2018 - 1900, 11, 22)
        tv_time_remaining.typeface = buildTypeface {
            path = 荷包鼓鼓
            assetManager = assets
        }
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


    private fun startTimer() {
        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tv_time_remaining.text = getRemaining(destination)
                }
            }
        }
        if (timer == null) {
            timer = Timer()
        }
        timer!!.schedule(timerTask, 0L, 500L)
    }
}
