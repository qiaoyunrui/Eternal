package me.juhezi.notepad.addarticle

import android.content.Context
import me.juhezi.eternal.extension.di
import me.juhezi.eternal.extension.isEmpty
import me.juhezi.eternal.extension.showToast
import me.juhezi.eternal.extension.trim
import me.juhezi.eternal.global.runInUIThread
import me.juhezi.eternal.model.Article
import me.juhezi.eternal.repository.impl.ArticleRepo
import me.juhezi.eternal.repository.interfaces.IArticleRepo

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticlePresenter(private val fragment: AddArticleContract.View,
                          private val context: Context) : AddArticleContract.Presenter {

    var repo: IArticleRepo

    init {
        fragment.setPresenter(this)
        repo = ArticleRepo()
    }

    override fun addArticle() {
        val article = fragment.generateArticle()
        if (!checkArticleValid(article)) {
            context.showToast("内容不能为空")
            fragment.hideLoading()
            return
        }
        fragment.showLoading()
        repo.add(article, {
            runInUIThread(Runnable {
                fragment.hideLoading()
                context.showToast("添加成功")
                fragment.setResult(it)
            })
        }, { message: String, throwable: Throwable ->
            runInUIThread(Runnable {
                context.showToast("添加失败")
                fragment.hideLoading()
                di("Add Error: $message")
                throwable.printStackTrace()
            })
        })
    }

    override fun start() {

    }

    private fun checkArticleValid(article: Article): Boolean {
        if (isEmpty(article.id)) return false
        if (isEmpty(trim(article.content))) return false
        return true
    }

}
