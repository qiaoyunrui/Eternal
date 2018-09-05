package me.juhezi.notepad

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import io.realm.Realm
import me.juhezi.eternal.base.BaseDialog
import me.juhezi.eternal.extension.di
import me.juhezi.eternal.extension.showToast
import me.juhezi.eternal.global.logi
import me.juhezi.eternal.global.runInUIThread
import me.juhezi.eternal.repository.impl.ArticleRepo
import me.juhezi.eternal.service.FileService
import me.juhezi.eternal.widget.dialog.EternalMessageDialog

@RequiresApi(Build.VERSION_CODES.N)
class ExportService : TileService() {

    private val mArticleRepo: ArticleRepo by lazy {
        ArticleRepo()
    }

    private var mSuccessDialog: EternalMessageDialog? = null
    private var mErrorDialog: EternalMessageDialog? = null

    override fun onStartListening() {
        super.onStartListening()
        Realm.init(baseContext)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick() {
        super.onClick()
        mArticleRepo.export({
            logi("json: $it")
            val path = "${Environment.getExternalStorageDirectory().path}/${getExportFileName()}"
            if (FileService.getInstance().write(path, it)) {
                runInUIThread(Runnable {
                    val dialog = getSuccessDialog()
                    dialog.configMessageTextView { text = "导出路径为：\n$path" }
                    showDialog(dialog)
                })
            } else {
                runInUIThread(Runnable {
                    showDialog(getErrorDialog())
                })
            }
        }, { message: String, throwable: Throwable ->
            runInUIThread(Runnable {
                showDialog(getErrorDialog())
            })
            di("Export Error: $message")
            throwable.printStackTrace()
        })
    }

    private fun getSuccessDialog(): EternalMessageDialog {
        if (mSuccessDialog == null) {
            mSuccessDialog = EternalMessageDialog(baseContext)
            mSuccessDialog!!.setDisplayContent("导出成功")
            mSuccessDialog!!.configConfirmButton {
                text = "查看导出文件"
            }
            mSuccessDialog!!.onConfirmClickListener = {
                showToast("正在开发中...")
            }
        }
        return mSuccessDialog!!
    }

    private fun getErrorDialog(): BaseDialog {
        if (mErrorDialog == null) {
            mErrorDialog = EternalMessageDialog(baseContext)
            mErrorDialog!!.setDisplayContent("导出失败")
        }
        return mErrorDialog!!
    }

    private fun getExportFileName() = "export_${System.currentTimeMillis()}.json"

}