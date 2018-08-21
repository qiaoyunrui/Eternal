package me.juhezi.notepad.addarticle

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import me.juhezi.eternal.base.BaseFragment
import me.juhezi.eternal.enum.DialogType
import me.juhezi.eternal.enum.ToolbarStyle
import me.juhezi.eternal.global.ADD_ARTICLE_RESULT_CODE
import me.juhezi.eternal.model.Article
import me.juhezi.eternal.widget.view.EternalToolbar
import me.juhezi.notepad.R

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class AddArticleFragment : BaseFragment(), AddArticleContract.View {

    companion object {
        val ARTICLE_KEY = "article_key"
    }

    private var mPresenter: AddArticleContract.Presenter? = null
    private lateinit var mRootView: View
    private lateinit var mEtTitle: EditText
    private lateinit var mEtContent: EditText
    private lateinit var mEternalToolbar: EternalToolbar

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.notepad_fragment_add_article,
                container, false)
        initView(mRootView)
        initEvent()
        return mRootView
    }

    override fun setPresenter(t: AddArticleContract.Presenter) {
        mPresenter = t
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }

    private fun initView(view: View) {
        mEtTitle = view.findViewById(R.id.et_add_article_title)
        mEtContent = view.findViewById(R.id.et_add_article_content)
        mEternalToolbar = view.findViewById(R.id.tb_add_article)
        with(mEternalToolbar) {
            leftStyle = ToolbarStyle.ICON_AND_TEXT
            configLeftGroup(getString(R.string.add_article))
            rightStyle = ToolbarStyle.TEXT
            onLeftGroupIconClickListener = {
                activity?.onBackPressed()
            }
        }
        dialogConfig = {
            setCanceledOnTouchOutside(false)
            canBack = false     // 不可返回
        }
    }

    private fun initEvent() {
        mEternalToolbar.onRightTextClickListener = {
            mPresenter?.addArticle()
        }
    }

    override fun generateArticle(): Article {
        return Article.generateArticle()
                .setCreateTime(System.currentTimeMillis().toString())
                .setTitle(mEtTitle.text.toString())
                .setContent(mEtContent.text.toString())
    }

    override fun showLoading() {
        showDialog(DialogType.PROGRESS)
    }

    override fun hideLoading() {
        hideDialog()
    }

    /**
     * 返回到上一页
     */
    override fun setResult(article: Article) {
        val intent = Intent()
        intent.putExtra(ARTICLE_KEY, article)
        activity?.setResult(ADD_ARTICLE_RESULT_CODE, intent)
        activity?.finish()
    }

}
