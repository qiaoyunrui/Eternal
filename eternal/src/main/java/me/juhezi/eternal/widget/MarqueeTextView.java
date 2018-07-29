package me.juhezi.eternal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import me.juhezi.eternal.extension.ContextExtensionKt;

/**
 * 跑马灯 TextView
 */
public class MarqueeTextView extends View {

    private static final String TAG = "MarqueeTextView";

    // 间隔字符
    private String mMarginString = "     ";
    private int mTextSize;
    private int mTextColor;
    private int mSpeed = 1;     //滚动速度
    private TextPaint mTextPaint;
    private String mText;
    private String mShowText;   //最终显示的字符串
    private int mTextWidth = 0, mTextHeight = 0;
    private int mCurrentX = 0;  //当前位置

    private int mTextHeigh;
    private int mBottomFont;

    private boolean isScrolling = true;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTextSize = ContextExtensionKt.dip2px(getContext(), 12);    // 默认为 12dp
        mTextPaint = new TextPaint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);     // 抗锯齿
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(0.5f);
        mTextPaint.setFakeBoldText(true);   // 设置为粗体
        mTextPaint.setShadowLayer(4.0f, 0f, 0f, Color.parseColor("#66000000")); //添加阴影
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        // 可显示内容的宽高
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        if (TextUtils.isEmpty(mText)) { // 字符串为空，不进行控制
            return;
        }
    }

    // 测量当前 Text
    private void measureText() {

    }

    // 根据文本的宽度和控件的宽度扩展文本
    private void expandText() {
        int widgetWidth = getWidth();   // 控件的宽度
        int textWidth = (int) mTextPaint.measureText(mText);  // 文本的宽度
        String tempString = "";
        while (textWidth <= widgetWidth) {  // 如果文本宽度小于控件宽度，那么就进行扩展
            tempString = tempString + (mText + mMarginString + mText);     // 将两个文本拼接成一个新的字符串
            textWidth = (int) mTextPaint.measureText(tempString); // 对拼接后的字符串进行测量宽度
        }
        mShowText = tempString;
    }

    public void setText(String text) {
        this.mText = text;
        // 要进行重新绘制
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        configTextPaint();
    }

    private void configTextPaint() {
        mTextPaint.setTextSize(mTextSize);
    }

    public void reset() {
        mCurrentX = 0;
    }

    public void startScroll() {

    }

    public void endScroll() {

    }
}
