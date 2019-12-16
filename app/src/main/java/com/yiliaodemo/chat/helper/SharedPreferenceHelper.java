package com.yiliaodemo.chat.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.yiliaodemo.chat.bean.ChatUserInfo;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：SharedPreference帮助类
 * 作者：
 * 创建时间：2018/6/72
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SharedPreferenceHelper {

    //登录相关
    private static final String KEY_CONFIG_LOGIN = "login";
    private static final String PHONE = "phone";
    private static final String GENDER = "t_sex";
    private static final String USER_ID = "t_id";
    private static final String IS_VIP = "t_is_vip";
    private static final String T_ROLE = "t_role";
    private static final String NICK_NAME = "nickName";
    private static final String HEAD_URL = "headUrl";//头像
    //极光相关
    private static final String KEY_JPUSH = "jpush";
    private static final String JPUSH_ALIAS = "alias";//别名
    private static final String JPUSH_FACE = "face";//头像

    //坐标
    private static final String KEY_CODE = "code";
    private static final String CODE_LAT = "code_lat";
    private static final String CODE_LNG = "code_lng";

    //保存用户自己是否mute
    private static final String KEY_MUTE = "mute";
    private static final String MUTE = "mute_mute";

    //保存消息提示音 震动
    private static final String KEY_TIP = "key_tip";
    private static final String SOUND = "tip_sound";
    private static final String VIBRATE = "tip_vibrate";

    //客服QQ号
    private static final String KEY_QQ = "key_qq";
    private static final String QQ = "qq";

    //速配引导
    private static final String KEY_QUIDE = "key_guide";
    private static final String GUIDE = "guide";

    /**
     * 保存用户登录账户信息
     */
    public static void saveAccountInfo(Context context, ChatUserInfo chatUserInfo) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PHONE, chatUserInfo.phone);
        editor.putInt(USER_ID, chatUserInfo.t_id);
        editor.putInt(IS_VIP, chatUserInfo.t_is_vip);
        editor.putInt(T_ROLE, chatUserInfo.t_role);
        editor.putInt(GENDER, chatUserInfo.t_sex);
        editor.putString(NICK_NAME, chatUserInfo.nickName);
        editor.putString(HEAD_URL, chatUserInfo.headUrl);
        editor.apply();
    }

    /**
     * 保存用户账户性别信息
     */
    public static void saveGenderInfo(Context context, int gender) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(GENDER, gender);
        editor.apply();
    }

    /**
     * 保存用户账户角色信息
     */
    public static void saveRoleInfo(Context context, int role) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(T_ROLE, role);
        editor.apply();
    }

    /**
     * 获取登录的账户信息
     */
    public static ChatUserInfo getAccountInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        ChatUserInfo chatUserInfo = new ChatUserInfo();
        chatUserInfo.t_id = sp.getInt(USER_ID, 0);
        chatUserInfo.phone = sp.getString(PHONE, "");
        chatUserInfo.t_is_vip = sp.getInt(IS_VIP, 1);
        chatUserInfo.t_sex = sp.getInt(GENDER, 2);
        chatUserInfo.t_role = sp.getInt(T_ROLE, 2);
        chatUserInfo.nickName = sp.getString(NICK_NAME, "");
        chatUserInfo.headUrl = sp.getString(HEAD_URL, "");
        return chatUserInfo;
    }

    /**
     * 保存用户VIP
     */
    public static void saveUserVip(Context context, int vip) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(IS_VIP, vip);
        editor.apply();
    }

    /**
     * 保存用户头像
     */
    public static void saveHeadImgUrl(Context context, String imgUrl) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(HEAD_URL, imgUrl);
        editor.apply();
    }

    /**
     * 保存用户昵称
     */
    public static void saveUserNickName(Context context, String nickName) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(NICK_NAME, nickName);
        editor.apply();
    }

    /**
     * 保存极光别名
     */
    public static void saveJPushAlias(Context context, String alisa) {
        SharedPreferences sp = context.getSharedPreferences(KEY_JPUSH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(JPUSH_ALIAS, alisa);
        editor.apply();
    }

    /**
     * 获取极光别名
     */
    public static String getJPushAlias(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_JPUSH, Context.MODE_PRIVATE);
        return sp.getString(JPUSH_ALIAS, "");
    }

    /**
     * 保存极光im头像地址
     */
    public static void saveJIMFaceUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(KEY_JPUSH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(JPUSH_FACE, url);
        editor.apply();
    }

    /**
     * 获取极光im头像地址
     */
    public static String getJIMFaceUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_JPUSH, Context.MODE_PRIVATE);
        return sp.getString(JPUSH_FACE, "");
    }

    /**
     * 保存坐标
     */
    public static void saveCode(Context context, String lat, String lng) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CODE_LAT, lat);
        editor.putString(CODE_LNG, lng);
        editor.apply();
    }

    /**
     * 获取坐标LAT
     */
    public static String getCodeLat(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        return sp.getString(CODE_LAT, "");
    }

    /**
     * 获取坐标LNG
     */
    public static String getCodeLng(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        return sp.getString(CODE_LNG, "");
    }

    /**
     * 保存mute
     */
    public static void saveMute(Context context, boolean mute) {
        SharedPreferences sp = context.getSharedPreferences(KEY_MUTE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(MUTE, mute);
        editor.apply();
    }

    /**
     * 获取MUTE,默认false  表示没有mute,摄像头是开启的
     */
    public static boolean getMute(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_MUTE, Context.MODE_PRIVATE);
        return sp.getBoolean(MUTE, false);
    }

    /**
     * 保存消息提示音
     */
    public static void saveTipSound(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SOUND, open);
        editor.apply();
    }

    /**
     * 保存消息提示震动
     */
    public static void saveTipVibrate(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(VIBRATE, open);
        editor.apply();
    }

    /**
     * 默认开启声音
     */
    public static boolean getTipSound(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        return sp.getBoolean(SOUND, true);
    }

    /**
     * 默认开启震动
     */
    public static boolean getTipVibrate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        return sp.getBoolean(VIBRATE, true);
    }

    /**
     * 保存客服QQ
     */
    public static void saveQQ(Context context, String qq) {
        SharedPreferences sp = context.getSharedPreferences(KEY_QQ, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(QQ, qq);
        editor.apply();
    }

    /**
     * 获取客服QQ号
     */
    public static String getQQ(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_QQ, Context.MODE_PRIVATE);
        return sp.getString(QQ, "");
    }

    /**
     * 保存速配引导
     */
    public static void saveQuickGuide(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_QUIDE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(GUIDE, false);
        editor.apply();
    }

    /**
     * 获取速配引导,默认安装后需要引导
     */
    public static boolean getQuickGuide(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_QUIDE, Context.MODE_PRIVATE);
        return sp.getBoolean(GUIDE, true);
    }

}
