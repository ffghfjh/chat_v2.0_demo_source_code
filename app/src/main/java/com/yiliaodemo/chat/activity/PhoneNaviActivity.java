package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.view.View;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;

import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：手机指南页面
 * 作者：
 * 创建时间：2018/11/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneNaviActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_navi_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.phone_navi);
    }

    @OnClick({R.id.phone_vivo, R.id.phone_meizu, R.id.phone_huawei, R.id.phone_xiaomi, R.id.phone_oppo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_vivo: {//vivo
                Intent intent = new Intent(getApplicationContext(), PhoneVivoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.phone_meizu: {//魅族
                Intent intent = new Intent(getApplicationContext(), PhoneMeizuActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.phone_huawei: {//华为
                Intent intent = new Intent(getApplicationContext(), PhoneHuaweiActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.phone_xiaomi: {//小米
                Intent intent = new Intent(getApplicationContext(), PhoneXiaomiActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.phone_oppo: {//oppo
                Intent intent = new Intent(getApplicationContext(), PhoneOppoActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

}
