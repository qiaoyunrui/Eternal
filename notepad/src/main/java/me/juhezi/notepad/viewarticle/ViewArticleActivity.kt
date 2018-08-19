package me.juhezi.notepad.viewarticle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.juhezi.eternal.base.BaseSingleFragmentActivity
import me.juhezi.eternal.model.Article

class ViewArticleActivity : BaseSingleFragmentActivity() {

    companion object {

        const val KEY_ARTICLE = "article"

        fun newIntent(context: Context, article: Article): Intent {
            val intent = Intent(context, ViewArticleActivity::class.java)
            intent.putExtra(KEY_ARTICLE, article)
            return intent
        }
    }

    private lateinit var mFragment: ViewArticleFragment
    private lateinit var mPresenter: ViewArticlePresenter

    override fun getFragment(): Fragment {
        mFragment = ViewArticleFragment()
        return mFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = ViewArticlePresenter(mFragment, this)
        mFragment.arguments = intent.extras
    }

}