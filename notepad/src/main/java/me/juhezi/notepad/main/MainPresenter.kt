package me.juhezi.notepad.main

import android.content.Context

class MainPresenter(private val fragment: MainContract.View,
                    private val context: Context) : MainContract.Presenter {

    init {
        fragment.setPresenter(this)
    }

    override fun start() {

    }
}