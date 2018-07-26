package me.juhezi.notepad.addarticle

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import me.juhezi.eternal.base.BaseSingleFragmentActivity

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticleActivity : BaseSingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, AddArticleActivity::class.java)
    }

    override fun getFragment(): Fragment = AddArticleFragment()

}
