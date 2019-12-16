package com.yiliaodemo.chat.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载
 */
public abstract class BaseFragment extends LazyFragment {

    public BaseActivity mContext;

    public BaseFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (BaseActivity) getActivity();
        View view = LayoutInflater.from(getContext()).inflate(initLayout(), container, false);
        initView(view, savedInstanceState);
        mIsViewPrepared = true;
        return view;
    }

    /**
     * 初始化layout
     */
    protected abstract int initLayout();

    /**
     * 初始化view
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 第一次可见的操作
     */
    protected abstract void onFirstVisible();

    @Override
    protected void onFirstVisibleToUser() {
        onFirstVisible();
        mIsDataLoadCompleted = true;
    }

}
