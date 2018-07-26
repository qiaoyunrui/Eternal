package me.juhezi.notepad.addarticle

import android.content.Context

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticlePresenter(private val fragment: AddArticleContract.View,
                          private val context: Context) : AddArticleContract.Presenter {

    init {
        fragment.setPresenter(this)
    }

    override fun start() {

    }

}
