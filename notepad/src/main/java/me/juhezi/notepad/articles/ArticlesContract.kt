package me.juhezi.notepad.articles

import me.juhezi.eternal.base.BasePresenter
import me.juhezi.eternal.base.BaseView
import me.juhezi.eternal.model.Article

interface ArticlesContract {

    interface Presenter : BasePresenter {

        fun requestData()   // 请求数据

        fun refresh()

    }

    interface View : BaseView<Presenter> {

        fun showLoading()

        fun hideLoading()

        fun refreshList(data: List<Article>)   // 刷新列表

        fun addArticle(article: Article, position: Int)     // 添加一个 Article

        fun finishRefresh()

    }

}