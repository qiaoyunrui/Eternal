package me.juhezi.notepad.addarticle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.juhezi.eternal.base.BaseSingleFragmentActivity

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticleActivity : BaseSingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, AddArticleActivity::class.java)
    }

    override fun getFragment(): Fragment {
        mFragment = AddArticleFragment()
        return mFragment
    }

    private lateinit var mFragment: AddArticleFragment
    private lateinit var mPresenter: AddArticlePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = AddArticlePresenter(mFragment, this)
    }

}
