package me.juhezi.notepad.addarticle

import me.juhezi.eternal.base.BasePresenter
import me.juhezi.eternal.base.BaseView
import me.juhezi.eternal.model.Article

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
interface AddArticleContract {

    interface Presenter : BasePresenter {
        fun addArticle()
    }

    interface View : BaseView<Presenter> {
        fun generateArticle(): Article

        fun showLoading()

        fun hideLoading()

        fun setResult(article: Article)

    }

}
