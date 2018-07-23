package me.juhezi.notepad.main

import me.juhezi.eternal.base.BasePresenter
import me.juhezi.eternal.base.BaseView

interface MainContract {

    interface Presenter : BasePresenter

    interface View : BaseView<BasePresenter>

}