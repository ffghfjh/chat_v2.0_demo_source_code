package com.yiliaodemo.chat.activity;

import android.view.View;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：oppo手机指南页面
 * 作者：
 * 创建时间：2018/11/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneOppoActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_oppo_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.oppo_des_one);
    }
}
