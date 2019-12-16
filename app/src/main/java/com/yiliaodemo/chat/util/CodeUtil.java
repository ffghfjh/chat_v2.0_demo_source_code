package com.yiliaodemo.chat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：转码工具
 * 作者：
 * 创建时间：2018/7/12
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CodeUtil {

    /**
     * 跳转到QQ
     */
    public static void jumpToQQ(Activity mContext) {
        // 跳转之前，可以先判断手机是否安装QQ
        if (isQQClientAvailable(mContext)) {
            // 跳转到客服的QQ
            String mQQNumber = SharedPreferenceHelper.getQQ(mContext.getApplicationContext());
            if (TextUtils.isEmpty(mQQNumber)) {
                ToastUtil.showToast(mContext.getApplicationContext(), R.string.qq_not_exist);
                return;
            }
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + mQQNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // 跳转前先判断Uri是否存在，如果打开一个不存在的Uri，App可能会崩溃
            if (isValidIntent(mContext, intent)) {
                mContext.startActivity(intent);
            } else {
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        } else {
            ToastUtil.showToast(mContext, R.string.not_install_qq);
        }
    }


    /**
     * 判断 用户是否安装QQ客户端
     */
    private static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite")
                        || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 Uri是否有效
     */
    private static boolean isValidIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

}
