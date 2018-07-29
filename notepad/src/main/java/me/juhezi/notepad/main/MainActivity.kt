package me.juhezi.notepad.main

import android.os.Bundle
import android.support.v4.app.Fragment
import me.juhezi.eternal.base.BaseSingleFragmentActivity

class MainActivity : BaseSingleFragmentActivity() {

    private lateinit var mFragment: MainFragment
    private lateinit var mPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = MainPresenter(mFragment, this)
    }

    override fun getFragment(): Fragment {
        mFragment = MainFragment()
        return mFragment
    }



}
