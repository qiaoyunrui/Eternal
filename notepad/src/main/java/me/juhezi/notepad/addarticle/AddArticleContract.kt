package me.juhezi.notepad.addarticle

import me.juhezi.eternal.base.BasePresenter
import me.juhezi.eternal.base.BaseView

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
interface AddArticleContract {

    interface Presenter : BasePresenter

    interface View : BaseView<Presenter>

}
