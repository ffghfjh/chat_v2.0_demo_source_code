package com.yiliaodemo.chat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  可展开的TextView
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ExpandTextView extends android.support.v7.widget.AppCompatTextView {

    /**
     * true：展开，false：收起
     */
    boolean mExpanded;
    /**
     * 状态回调
     */
    Callback mCallback;
    /**
     * 源文字内容
     */
    String mText = "";
    /**
     * 最多展示的行数
     */
    final int maxLineCount = 4;
    /**
     * 省略文字
     */
    final String ellipsizeText = "...";

    public ExpandTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 文字计算辅助工具
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        @SuppressLint("DrawAllocation")
        StaticLayout sl = new StaticLayout(mText, getPaint(),
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                , Layout.Alignment.ALIGN_CENTER, 1, 0, true);
        // 总计行数
        int lineCount = sl.getLineCount();
        if (lineCount > maxLineCount) {
            if (mExpanded) {
                setText(mText);
                if (mCallback != null) {
                    mCallback.onExpand();
                }
            } else {
                lineCount = maxLineCount;

                // 省略文字的宽度
                float dotWidth = getPaint().measureText(ellipsizeText);

                // 找出第 showLineCount 行的文字
                int start = sl.getLineStart(lineCount - 1);
                int end = sl.getLineEnd(lineCount - 1);
                String lineText = mText.substring(start, end);

                // 将第 showLineCount 行最后的文字替换为 ellipsizeText
                int endIndex = 0;
                for (int i = lineText.length() - 1; i >= 0; i--) {
                    String str = lineText.substring(i, lineText.length());
                    // 找出文字宽度大于 ellipsizeText 的字符
                    if (getPaint().measureText(str) >= dotWidth) {
                        endIndex = i;
                        break;
                    }
                }

                // 新的第 showLineCount 的文字
                String newEndLineText = lineText.substring(0, endIndex) + ellipsizeText;
                // 最终显示的文字
                String content = mText.substring(0, start) + newEndLineText;
                setText(content);

                if (mCallback != null) {
                    mCallback.onCollapse();
                }
            }
        } else {
            setText(mText);
            if (mCallback != null) {
                mCallback.onLoss();
            }
        }

        // 重新计算高度
        int lineHeight = 0;
        for (int i = 0; i < lineCount; i++) {
            Rect lineBound = new Rect();
            sl.getLineBounds(i, lineBound);
            lineHeight += lineBound.height();
        }
        lineHeight += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(getMeasuredWidth(), lineHeight);
    }

    /**
     * 设置要显示的文字以及状态
     *
     * @param expanded true：展开，false：收起
     */
    public void setText(String text, boolean expanded, Callback callback) {
        mText = text;
        mExpanded = expanded;
        mCallback = callback;

        // 设置要显示的文字，这一行必须要，否则 onMeasure 宽度测量不正确
        setText(text);
    }

    /**
     * 展开收起状态变化
     */
    public void setChanged(boolean expanded) {
        mExpanded = expanded;
        requestLayout();
    }

    public interface Callback {
        /**
         * 展开状态
         */
        void onExpand();

        /**
         * 收起状态
         */
        void onCollapse();

        /**
         * 行数小于最小行数，不满足展开或者收起条件
         */
        void onLoss();
    }

}

