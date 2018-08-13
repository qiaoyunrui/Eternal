package me.juhezi.notepad.viewarticle

import me.juhezi.eternal.base.BasePresenter
import me.juhezi.eternal.base.BaseView

interface ViewArticleContract {

    interface Presenter : BasePresenter

    interface View : BaseView<Presenter>

}