package me.juhezi.notepad.viewarticle

import me.juhezi.eternal.base.BasePresenter
import me.juhezi.eternal.base.BaseView
import me.juhezi.eternal.model.Article

interface ViewArticleContract {

    interface Presenter : BasePresenter {

        fun remove(id: String)

        fun refresh(id: String)

    }

    interface View : BaseView<Presenter> {
        // 显示文章
        fun showArticle(article: Article)

        fun showLoading()

        fun hideLoading()

        fun setResult()

        fun finishRefresh()
    }

}