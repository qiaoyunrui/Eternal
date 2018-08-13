package me.juhezi.notepad.viewarticle

import android.content.Context

class ViewArticlePresenter(private val fragment: ViewArticleContract.View,
                           private val context: Context) : ViewArticleContract.Presenter {

    init {
        fragment.setPresenter(this)
    }

    override fun start() {

    }

}