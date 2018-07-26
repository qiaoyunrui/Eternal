package me.juhezi.notepad.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.notepad.R

class MainFragment : Fragment(), MainContract.View {

    private var mPresenter: MainContract.Presenter? = null
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        return rootView
    }

    override fun setPresenter(t: MainContract.Presenter) {
        mPresenter = t
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }

}