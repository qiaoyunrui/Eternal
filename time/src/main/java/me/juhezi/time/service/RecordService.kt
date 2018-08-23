package me.juhezi.time.service

import android.os.Build
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import io.realm.Realm
import me.juhezi.eternal.builder.buildTypeface
import me.juhezi.eternal.global.logi
import me.juhezi.eternal.global.荷包鼓鼓
import me.juhezi.eternal.model.Record
import me.juhezi.eternal.repository.impl.RecordRepo
import me.juhezi.eternal.widget.dialog.EternalMessageDialog
import me.juhezi.time.activity.TimeActivity
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
/**
 * Created by Juhezi[juhezix@163.com] on 2018/8/23.
 */
class RecordService : TileService() {

    private val destination: Calendar by lazy {
        val result = Calendar.getInstance()
        result.time = Date(2018 - 1900, 11, 22)
        result
    }
    private val mRecordRepo: RecordRepo by lazy {
        RecordRepo()
    }
    private var mMessageDialog: EternalMessageDialog? = null

    override fun onStartListening() {
        super.onStartListening()
        Realm.init(baseContext)
    }

    override fun onClick() {
        super.onClick()
        if (mMessageDialog == null) {
            mMessageDialog = EternalMessageDialog(baseContext)
            mMessageDialog!!.configMessageTextView {
                typeface = buildTypeface {
                    path = 荷包鼓鼓
                    assetManager = assets
                }
            }
        }
        mMessageDialog!!.setDisplayContent(message = "还有: ${TimeActivity.getRemaining(destination)}")
        showDialog(mMessageDialog)
        mRecordRepo.add(Record.generateRecord(), {
            logi("记录成功：$it")
        }, null)
    }
}
