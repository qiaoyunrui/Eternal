package me.juhezi.notepad.main

import android.os.Bundle
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.notepad.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
