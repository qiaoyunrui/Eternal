package me.juhezi.notepad.viewarticle

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import me.juhezi.eternal.base.BaseDialog
import me.juhezi.eternal.base.BaseFragment
import me.juhezi.eternal.enum.DialogType
import me.juhezi.eternal.enum.ToolbarStyle
import me.juhezi.eternal.global.formatTime
import me.juhezi.eternal.model.Article
import me.juhezi.eternal.widget.dialog.EternalOperationDialog
import me.juhezi.eternal.widget.view.EternalToolbar
import me.juhezi.notepad.R

class ViewArticleFragment : BaseFragment(), ViewArticleContract.View {

    private var mPresenter: ViewArticleContract.Presenter? = null
    private lateinit var mRootView: View
    private lateinit var mToolbar: EternalToolbar
    private lateinit var mTvTitle: TextView
    private lateinit var mTvContent: TextView

    companion object {
        private const val DELETE = "delete"
        private const val EDIT = "edit"
        private const val SHARE = "share"
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_view_article,
                container, false)
        mToolbar = mRootView.findViewById(R.id.tb_view_article)
        mTvContent = mRootView.findViewById(R.id.tv_view_article_content)
        mTvTitle = mRootView.findViewById(R.id.tv_view_article_title)
        with(mToolbar) {
            leftStyle = ToolbarStyle.ICON_AND_TEXT
            onLeftGroupIconClickListener = {
                activity?.onBackPressed()
            }
            rightStyle = ToolbarStyle.ICON
            onRightIconClickListener = {
                showDialog(DialogType.OPERATION)
            }
        }
        configDialog()
        initData()
        return mRootView
    }

    private fun configDialog() {
        var dialog: BaseDialog = getDialog(DialogType.OPERATION) as? EternalOperationDialog
                ?: return
        dialog = dialog as EternalOperationDialog
        with(dialog) {
            config {
                setCanceledOnTouchOutside(true)
            }
            addGeneralOperation(DELETE,
                    content = getString(R.string.delete),
                    drawableRes = R.drawable.ic_delete_black)
            addGeneralOperation(EDIT,
                    content = getString(R.string.edit),
                    drawableRes = R.drawable.ic_edit_black)
            addGeneralOperation(SHARE,
                    content = getString(R.string.share),
                    drawableRes = R.drawable.ic_share_black)
            addCancelOperation()
            apply()
            onClickListener = { id, _ ->
                when (id) {
                    EternalOperationDialog.CANCEL_ID -> onBackPressed()
                    else ->
                        Toast.makeText(context, "开发者正在努力开发中", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun showArticle(article: Article) {
        if (TextUtils.isEmpty(article.title)) {
            mTvTitle.visibility = View.GONE
        } else {
            mTvTitle.text = article.title
        }
        mTvContent.text = article.content
        mToolbar.configLeftGroup(formatTime(article.createTime.toLong()))
    }

    override fun setPresenter(t: ViewArticleContract.Presenter) {
        mPresenter = t
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.start()
    }

    private fun initData() {
        val article: Article? = arguments?.get(ViewArticleActivity.KEY_ARTICLE) as Article?
                ?: return
        showArticle(article!!)
    }
}