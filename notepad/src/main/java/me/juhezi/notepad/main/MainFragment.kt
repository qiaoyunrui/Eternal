package me.juhezi.notepad.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.eternal.base.BaseFragment
import me.juhezi.eternal.extension.i
import me.juhezi.eternal.global.ADD_ARTICLE_REQUEST_CODE
import me.juhezi.notepad.R
import me.juhezi.notepad.addarticle.AddArticleActivity
import me.juhezi.notepad.addarticle.AddArticleFragment

class MainFragment : BaseFragment(), MainContract.View {

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
            startActivityForResult(AddArticleActivity.newIntent(context!!), ADD_ARTICLE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val article = data?.getSerializableExtra(AddArticleFragment.ARTICLE_KEY)
        // todo 把这个 Article 添加到列表里
        i("$article")
    }

}