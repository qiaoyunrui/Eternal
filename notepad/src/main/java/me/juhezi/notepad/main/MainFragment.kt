package me.juhezi.notepad.main

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.eternal.extension.i
import me.juhezi.notepad.R
import me.juhezi.notepad.addarticle.AddArticleActivity

class MainFragment : Fragment(), MainContract.View {

    // 应该添加标题的

    private var mPresenter: MainContract.Presenter? = null
    private lateinit var rootView: View
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        initView(rootView)
        initEvent()
        return rootView
    }

    override fun setPresenter(t: MainContract.Presenter) {
        mPresenter = t
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }

    private fun initView(view: View) {
        fab = view.findViewById(R.id.fab_main_add_article)
    }

    private fun initEvent() {
        fab.setOnClickListener {
            i()
            startActivity(AddArticleActivity.newIntent(context!!))
        }
    }

}