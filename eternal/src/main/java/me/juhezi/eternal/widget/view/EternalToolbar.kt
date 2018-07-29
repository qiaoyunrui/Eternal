package me.juhezi.eternal.widget.view

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import me.juhezi.eternal.R
import me.juhezi.eternal.enum.ToolbarStyle

/**
 * 项目中通用的控件
 *
 * mode:
 *
 * Created by Juhezi[juhezix@163.com] on 2018/7/26.
 */
class EternalToolbar @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0,
                                               defStyleRes: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {

    var leftStyle = ToolbarStyle.TEXT    // 左边默认为 Text
        set(value) {
            field = value
            updateLeftView()
        }
    var rightStyle = ToolbarStyle.NONE   // 右边默认为 空
        set(value) {
            field = value
            updateRightView()
        }
    var centerStyle = ToolbarStyle.NONE    // 中间也默认为空
        set(value) {
            field = value
            updateCenterView()
        }

    private var mLeftViewMap = ArrayMap<Int, View?>()
    private var mRightViewMap = ArrayMap<Int, View?>()
    private var mCenterViewMap = ArrayMap<Int, View?>()

    @LayoutRes
    private var layoutRes = R.layout.view_eternal_toolbar

    private var mRightTextView: TextView?

    var onRightTextClickListener: (() -> Unit)? = null

    init {
        //--- init
        View.inflate(context, layoutRes, this)
        mRightTextView = findViewById(R.id.tv_eternal_right)

        //--- init View Map

        mRightViewMap[ToolbarStyle.TEXT.key] = mRightTextView

        //--- init Style Show

        updateLeftView()
        updateCenterView()
        updateRightView()

        //--- event

        mRightTextView?.setOnClickListener {
            onRightTextClickListener?.invoke()
        }
    }

    private fun updateLeftView() {
        updateView(leftStyle, mLeftViewMap)
    }

    private fun updateCenterView() {
        updateView(centerStyle, mCenterViewMap)
    }

    private fun updateRightView() {
        updateView(rightStyle, mRightViewMap)
    }

    private fun updateView(style: ToolbarStyle, viewMap: Map<Int, View?>) {

        when (style) {
            ToolbarStyle.TEXT -> {
                viewMap[ToolbarStyle.TEXT.key]?.visibility = View.VISIBLE
                viewMap[ToolbarStyle.ICON.key]?.visibility = View.GONE
                viewMap[ToolbarStyle.ICON_AND_TEXT.key]?.visibility = View.GONE
            }
            ToolbarStyle.NONE -> {
                viewMap[ToolbarStyle.TEXT.key]?.visibility = View.GONE
                viewMap[ToolbarStyle.ICON.key]?.visibility = View.GONE
                viewMap[ToolbarStyle.ICON_AND_TEXT.key]?.visibility = View.GONE
            }
        }

    }


}
