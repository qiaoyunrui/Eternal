package me.juhezi.eternal.widget

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import me.juhezi.eternal.R

/**
 * 项目中通用的控件
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

    init {
        View.inflate(context, layoutRes, this)
    }

}
