package me.juhezi.eternal.widget.dialog

import android.content.Context
import android.support.annotation.LayoutRes
import me.juhezi.eternal.R
import me.juhezi.eternal.base.BaseDialog

class EternalOperationDialog(context: Context?) :
        BaseDialog(context, R.style.full_screen_dialog) {

    @LayoutRes
    private val mLayoutRes = R.layout.dialog_operation

    init {
        setContentView(R.layout.dialog_operation)
    }
}
