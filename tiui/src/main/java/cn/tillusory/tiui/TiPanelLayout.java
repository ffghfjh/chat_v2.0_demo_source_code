package cn.tillusory.tiui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.view.TiBeautyView;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiPanelLayout extends ConstraintLayout {

    private TiSDKManager mTiSDKManager;
    private TextView mPasteTv;
    private TextView mBeautyTv;
    private TiBeautyView mTiBeautyView;
    private LinearLayout mClickLl;

    public TiPanelLayout(Context context) {
        super(context);
    }

    public TiPanelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiPanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TiPanelLayout init(@NonNull TiSDKManager tiSDKManager) {
        mTiSDKManager = tiSDKManager;
        initView();
        initData();
        return this;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ti_panel, this);
        mClickLl = findViewById(R.id.click_ll);
        mPasteTv = findViewById(R.id.paste_tv);
        mBeautyTv = findViewById(R.id.beauty_tv);
        mTiBeautyView = findViewById(R.id.tiBeautyTrimView);
    }

    private void initData() {
        mTiBeautyView.init(mTiSDKManager);
        mPasteTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickLl.setVisibility(GONE);
                mTiBeautyView.setVisibility(VISIBLE);
            }
        });
        mBeautyTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickLl.setVisibility(GONE);
                mTiBeautyView.setVisibility(VISIBLE);
            }
        });

        //空白处隐藏面板
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                mTiBeautyView.setVisibility(GONE);
                mClickLl.setVisibility(VISIBLE);
                return false;
            }
        });
    }

}
