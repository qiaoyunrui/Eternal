package me.juhezi.main

import android.os.Bundle
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.eternal.enum.ToolbarStyle
import me.juhezi.eternal.global.logi
import me.juhezi.eternal.widget.dialog.EternalOperationDialog
import me.juhezi.eternal.widget.view.EternalToolbar

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolBarVisibility = false   //隐藏 toolbar
        val toolbar = findViewById<EternalToolbar>(R.id.tb_main)
        toolbar.rightStyle = ToolbarStyle.ICON
        val operationDialog = EternalOperationDialog(this)
        with(operationDialog) {
            config {
                setCanceledOnTouchOutside(true)
            }
            logi("${addTextOperation("text#1")}")
            addTextOperation("text#2")
            addGeneralOperation("general#1", textClosure = {
                text = "Hello World"
            })
            addGeneralOperation("general#2", textClosure = {
                text = "Nice World"
            })
            addCancelOperation()
            apply()
        }
        operationDialog.onClickListener = { id, type ->
            logi("id: $id , type: $type")
        }
        toolbar.onRightIconClickListener = {
            operationDialog.show()
        }
    }
}
