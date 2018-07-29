package me.juhezi.notepad.addarticle

import android.content.Context
import me.juhezi.eternal.extension.di
import me.juhezi.eternal.extension.i
import me.juhezi.eternal.repository.impl.ArticleRepo
import me.juhezi.eternal.repository.interfaces.IArticleRepo

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticlePresenter(private val fragment: AddArticleContract.View,
                          private val context: Context) : AddArticleContract.Presenter {

    lateinit var repo: IArticleRepo

    init {
        fragment.setPresenter(this)
        repo = ArticleRepo()
    }

    override fun addArticle() {
        val article = fragment.generateArticle()
        repo.add(article, {
            i("上传成功\t$article")
        }, { message: String, throwable: Throwable ->
            di("Add Error: $message")
            throwable.printStackTrace()
        })
    }

    override fun start() {

    }

}
