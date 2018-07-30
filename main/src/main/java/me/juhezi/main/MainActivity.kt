package me.juhezi.main

import android.os.Bundle
import me.juhezi.eternal.base.BaseActivity
import me.juhezi.eternal.widget.view.MarqueeTextView

class MainActivity : BaseActivity() {

    private lateinit var textView: MarqueeTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolBarVisibility = false   //隐藏 toolbar
        textView = findViewById(R.id.tv_marquee)
        textView.setText("Hello World")
    }
}
