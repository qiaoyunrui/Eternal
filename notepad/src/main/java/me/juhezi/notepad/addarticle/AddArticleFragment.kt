package me.juhezi.notepad.addarticle

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import me.juhezi.eternal.global.logi
import me.juhezi.eternal.model.Article
import me.juhezi.eternal.widget.EternalToolbar
import me.juhezi.notepad.R

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticleFragment : Fragment(), AddArticleContract.View {

    private var mPresenter: AddArticleContract.Presenter? = null
    private lateinit var mRootView: View
    private lateinit var mEtTitle: EditText
    private lateinit var mEtContent: EditText
    private lateinit var mEternalToolbar: EternalToolbar


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_add_article,
                container, false)
        initView(mRootView)
        initEvent()
        return mRootView
    }

    override fun setPresenter(t: AddArticleContract.Presenter) {
        mPresenter = t
        logi("$t")
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }

    private fun initView(view: View) {
        mEtTitle = view.findViewById(R.id.et_add_article_title)
        mEtContent = view.findViewById(R.id.et_add_article_content)
        mEternalToolbar = view.findViewById(R.id.tb_add_article)
    }

    private fun initEvent() {
        mEternalToolbar.onRightTextClickListener = {
            mPresenter?.addArticle()
        }
    }

    override fun generateArticle(): Article {
        val article = Article.generateArticle()
                .setCreateTime(System.currentTimeMillis().toString())
        return article
    }

}
