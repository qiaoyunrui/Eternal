package me.juhezi.notepad.viewarticle

import android.content.Context
import me.juhezi.eternal.extension.di
import me.juhezi.eternal.extension.showToast
import me.juhezi.eternal.global.runInUIThread
import me.juhezi.eternal.repository.impl.ArticleRepo
import me.juhezi.eternal.repository.interfaces.IArticleRepo

class ViewArticlePresenter(private val fragment: ViewArticleContract.View,
                           private val context: Context) : ViewArticleContract.Presenter {

    var repo: IArticleRepo

    init {
        fragment.setPresenter(this)
        repo = ArticleRepo()
    }

    override fun start() {

    }

    override fun remove(id: String) {
        repo.remove(id, {
            runInUIThread(Runnable {
                fragment.hideLoading()
                context.showToast("删除成功")
                fragment.setResult()
            })
        }, { message: String, throwable: Throwable ->
            runInUIThread(Runnable {
                context.showToast("删除失败")
                fragment.hideLoading()
                di("Remove Error: $message")
                throwable.printStackTrace()
            })
        })
    }

    override fun refresh(id: String) {
        repo.query(id, {
            runInUIThread(Runnable {
                fragment.finishRefresh()
                fragment.showArticle(it)
            })
        }, { message: String, throwable: Throwable ->
            runInUIThread(Runnable {
                fragment.finishRefresh()
                context.showToast("获取日记失败")
                di("Refresh Error: $message")
                throwable.printStackTrace()
            })
        })
    }

}