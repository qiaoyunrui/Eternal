package me.juhezi.notepad.addarticle

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.notepad.R

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticleFragment : Fragment(), AddArticleContract.View {

    private var mPresenter: AddArticleContract.Presenter? = null
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add_article,
                container, false)
        return rootView
    }

    override fun setPresenter(t: AddArticleContract.Presenter) {
        mPresenter = t
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }

    private fun initView(view: View) {

    }

    private fun initEvent() {

    }

}
