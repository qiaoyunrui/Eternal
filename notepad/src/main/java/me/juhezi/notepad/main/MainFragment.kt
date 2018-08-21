package me.juhezi.notepad.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.eternal.base.BaseFragment
import me.juhezi.eternal.enum.DialogType
import me.juhezi.eternal.extension.di
import me.juhezi.eternal.global.ADD_ARTICLE_REQUEST_CODE
import me.juhezi.eternal.global.ADD_ARTICLE_RESULT_CODE
import me.juhezi.eternal.global.DELETE_ARTICLE_RESULT_CODE
import me.juhezi.eternal.global.VIEW_ARTICLE_REQUEST_CODE
import me.juhezi.eternal.model.Article
import me.juhezi.notepad.R
import me.juhezi.notepad.addarticle.AddArticleActivity
import me.juhezi.notepad.addarticle.AddArticleFragment
import me.juhezi.notepad.viewarticle.ViewArticleActivity

class MainFragment : BaseFragment(), MainContract.View {

    // 应该添加标题的

    private var mPresenter: MainContract.Presenter? = null
    private lateinit var rootView: View
    private lateinit var mFab: FloatingActionButton
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mEmptyView: View
    private var mAdapter: ArticleAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.notepad_fragment_main, container, false)
        initView(rootView)
        initEvent()
        mPresenter?.requestData()
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
        mFab = view.findViewById(R.id.fab_main_add_article)
        mRecyclerView = view.findViewById(R.id.rv_main_article_list)
        mEmptyView = view.findViewById(R.id.vg_no_article)
        mAdapter = ArticleAdapter()
        mAdapter!!.emptyView = mEmptyView
        val layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = layoutManager
        dialogConfig = {
            setCanceledOnTouchOutside(false)
            canBack = false     // 不可返回
        }
        mAdapter?.onClickItemClickListener = { article, _ ->
            startActivityForResult(ViewArticleActivity.newIntent(context!!, article), VIEW_ARTICLE_REQUEST_CODE)
        }
    }

    private fun initEvent() {
        mFab.setOnClickListener {
            startActivityForResult(AddArticleActivity.newIntent(context!!), ADD_ARTICLE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ARTICLE_REQUEST_CODE) {
            if (resultCode == ADD_ARTICLE_RESULT_CODE) {
                val article = data?.getSerializableExtra(AddArticleFragment.ARTICLE_KEY) as Article?
                if (article != null) {
                    di("$article")
                    addArticle(article, 0)
                    mRecyclerView.scrollToPosition(0)
                }
            }
        } else if (requestCode == VIEW_ARTICLE_REQUEST_CODE) {
            if (resultCode == DELETE_ARTICLE_RESULT_CODE) {
                // 后续进行改进
                mPresenter?.requestData()   // 重新加载数据
            }
        }

    }

    override fun showLoading() {
        showDialog(DialogType.PROGRESS)
    }

    override fun hideLoading() {
        hideDialog()
    }

    override fun refreshList(data: List<Article>) {
        mAdapter?.setArticles(data)
    }

    override fun addArticle(article: Article, position: Int) {
        mAdapter?.addArticle(article, position)
    }

}