package me.juhezi.eternal.base

import android.support.v4.app.Fragment
import android.util.ArrayMap
import me.juhezi.eternal.enum.DialogType
import me.juhezi.eternal.widget.dialog.EternalOperationDialog
import me.juhezi.eternal.widget.dialog.EternalProgressDialog

open class BaseFragment : Fragment() {

    private val mDialogMap = ArrayMap<String, BaseDialog>()

    private var mCurrentDialog: BaseDialog? = null

    var dialogConfig: (BaseDialog.() -> Unit)? = null

    protected fun showDialog(dialogType: DialogType) {
        mCurrentDialog = getDialog(dialogType)
        mCurrentDialog?.config(dialogConfig)
        mCurrentDialog?.show()
    }

    protected fun getDialog(dialogType: DialogType): BaseDialog {
        var dialog = mDialogMap[dialogType.key]
        if (dialog == null) {
            dialog = createDialog(dialogType)
            mDialogMap[dialogType.key] = dialog
        }
        return dialog
    }

    // 非主线程也可以调用
    protected fun hideDialog() {
        mCurrentDialog?.dismiss()
    }

    private fun createDialog(dialogType: DialogType) = when (dialogType) {
        DialogType.PROGRESS ->
            EternalProgressDialog(context)
        DialogType.OPERATION ->
            EternalOperationDialog(context)
    }

}