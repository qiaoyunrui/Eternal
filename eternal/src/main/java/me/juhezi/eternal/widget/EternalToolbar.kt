package me.juhezi.eternal.widget

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import me.juhezi.eternal.R

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

    @LayoutRes
    private var layoutRes = R.layout.view_eternal_toolbar

    private lateinit var mRightTextView: TextView

    public var onRightTextClickListener: (() -> Unit)? = null

    init {
        View.inflate(context, layoutRes, this)
        mRightTextView = findViewById(R.id.tv_eternal_right)

        //--- event

        mRightTextView.setOnClickListener {
            onRightTextClickListener?.invoke()
        }
    }

}
