package me.juhezi.eternal.other

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import me.juhezi.eternal.global.formatTime
import me.juhezi.eternal.global.logi
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.concurrent.Executors

class CrashHandler(val context: Context) : Thread.UncaughtExceptionHandler {

    companion object {
        var PATH = "${Environment.getExternalStorageDirectory().path}/log/"
        const val FILE_NAME = "crash"
        const val FILE_NAME_SUFFIX = ".log"
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        handleException(e)
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun handleException(exception: Throwable?) {
        try {
            Executors.newSingleThreadExecutor()
                    .submit {
                        dumpExceptionToSDCard(exception)
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dumpExceptionToSDCard(ex: Throwable?) {
        // 判断是否支持 SD 卡
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            logi("sdcard not find ,skip dump exception")
        }

        val dir = File(PATH)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val currentTime = System.currentTimeMillis()
        val timeStr = formatTime(currentTime)
        val file = File("$PATH$FILE_NAME$timeStr$FILE_NAME_SUFFIX")
        try {
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            pw.println(timeStr)
            dumpPhoneInfo(pw)
            pw.println()
            ex?.printStackTrace(pw)
            pw.close()
        } catch (e: Exception) {
            logi("dump Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun dumpPhoneInfo(pw: PrintWriter) {
        val pm = context.packageManager
        val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)

        pw.print("App Version:${pi.versionName}_${pi.versionCode}\n" +
                "OS Version:${Build.VERSION.RELEASE}_${Build.VERSION.SDK_INT}\n" +
                "Vendor:${Build.MANUFACTURER}\n" +
                "Model:${Build.MODEL}\n" +
                "Cpu ABI:${Build.CPU_ABI}\n")
    }


}
