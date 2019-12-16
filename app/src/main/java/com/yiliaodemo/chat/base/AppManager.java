package com.yiliaodemo.chat.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.tillusory.sdk.TiSDK;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：自定义Application,用于初始化,储存一些全局变量,如UserInfo
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AppManager extends Application {

    private ChatUserInfo mUserInfo;
    private static AppManager mInstance;
    //判断微信支付是充值VIP 还是充值金币
    private boolean mIsWeChatForVip = false;
    //判断微信登录是绑定提现账号,还是登录页面的微信登录
    private boolean mIsWeChatBindAccount = false;
    //判断是首页微信分享还是
    private boolean mIsMainPageShareQun = false;
    //用于视频接通后提示用户
    public String mVideoStartHint = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JMessageClient.init(this, false);
        //离线鉴权初始化方法
        TiSDK.init(Constant.TI_KEY, this);
    }

    public ChatUserInfo getUserInfo() {
        if (mUserInfo != null) {
            return mUserInfo;
        } else {
            return SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        }
    }

    public void setUserInfo(ChatUserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    public static AppManager getInstance() {
        return mInstance;
    }

    public void setIsWeChatForVip(boolean isWeChatForVip) {
        mIsWeChatForVip = isWeChatForVip;
    }

    public boolean getIsWeChatForVip() {
        return mIsWeChatForVip;
    }

    public void setIsWeChatBindAccount(boolean isWeChatBindAccount) {
        mIsWeChatBindAccount = isWeChatBindAccount;
    }

    public boolean getIsWeChatBindAccount() {
        return mIsWeChatBindAccount;
    }

    public boolean getIsMainPageShareQun() {
        return mIsMainPageShareQun;
    }

    public void setIsMainPageShareQun(boolean mIsMainPageShareQun) {
        this.mIsMainPageShareQun = mIsMainPageShareQun;
    }
}
