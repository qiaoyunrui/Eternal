package me.juhezi.notepad

import android.os.Build
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import io.realm.Realm
import me.juhezi.eternal.extension.showToast
import me.juhezi.eternal.repository.impl.RecordRepo

@RequiresApi(Build.VERSION_CODES.N)
class ExportService : TileService() {

    private val mRecordRepo: RecordRepo by lazy {
        RecordRepo()
    }

    override fun onStartListening() {
        super.onStartListening()
        Realm.init(baseContext)
    }

    override fun onClick() {
        super.onClick()
        showToast("Hello World")
    }
}