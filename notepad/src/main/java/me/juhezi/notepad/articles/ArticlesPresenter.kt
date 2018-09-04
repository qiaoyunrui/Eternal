package me.juhezi.notepad.articles

import android.content.Context
import me.juhezi.eternal.extension.di
import me.juhezi.eternal.extension.showToast
import me.juhezi.eternal.global.runInUIThread
import me.juhezi.eternal.repository.impl.ArticleRepo
import me.juhezi.eternal.repository.interfaces.IArticleRepo

class ArticlesPresenter(private val fragment: ArticlesContract.View,
                        private val context: Context) : ArticlesContract.Presenter {

    var repo: IArticleRepo

    init {
        fragment.setPresenter(this)
        repo = ArticleRepo()
    }

    override fun start() {
    }

    /**
     * 请求数据 -> 数据解析 -> 刷新数据
     * 整条链路
     */
    override fun requestData() {
        fragment.showLoading()
        repo.queryAll({
            runInUIThread(Runnable {
                fragment.hideLoading()
                fragment.refreshList(it)
            })
        }, { message: String, throwable: Throwable ->
            runInUIThread(Runnable {
                context.showToast("获取数据失败")
                fragment.hideLoading()
                di("Query All Error: $message")
                throwable.printStackTrace()
            })
        })
    }

    override fun refresh() {
        repo.queryAll({
            runInUIThread(Runnable {
                fragment.finishRefresh()
                fragment.refreshList(it)
            })
        }, { message: String, throwable: Throwable ->
            runInUIThread(Runnable {
                fragment.finishRefresh()
                context.showToast("获取数据失败")
                di("Query All Error: $message")
                throwable.printStackTrace()
            })
        })
    }

}

