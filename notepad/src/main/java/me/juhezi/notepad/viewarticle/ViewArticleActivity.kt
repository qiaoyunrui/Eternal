package me.juhezi.notepad.viewarticle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.juhezi.eternal.base.BaseSingleFragmentActivity

class ViewArticleActivity : BaseSingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, ViewArticleActivity::class.java)
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
    }

}