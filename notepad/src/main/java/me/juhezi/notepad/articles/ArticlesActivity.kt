package me.juhezi.notepad.articles

import android.os.Bundle
import android.support.v4.app.Fragment
import me.juhezi.eternal.base.BaseSingleFragmentActivity

class ArticlesActivity : BaseSingleFragmentActivity() {

    private lateinit var mFragment: ArticlesFragment
    private lateinit var mPresenter: ArticlesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = ArticlesPresenter(mFragment, this)
    }

    override fun getFragment(): Fragment {
        mFragment = ArticlesFragment()
        return mFragment
    }

}
