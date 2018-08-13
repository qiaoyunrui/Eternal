package me.juhezi.notepad.viewarticle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.eternal.base.BaseFragment
import me.juhezi.notepad.R

class ViewArticleFragment : BaseFragment(), ViewArticleContract.View {

    private var mPresenter: ViewArticleContract.Presenter? = null
    private lateinit var mRootView: View

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_view_article,
                container, false)
        return mRootView
    }

    override fun setPresenter(t: ViewArticleContract.Presenter) {
        mPresenter = t
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }
}