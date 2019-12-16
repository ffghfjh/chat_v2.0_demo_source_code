package com.yiliaodemo.chat.util;

import android.content.Context;
import android.widget.Toast;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Toast工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ToastUtil {

    public static void showToast(Context context, String text) {
        try {
            Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, int resourceId) {
        try {
            Toast.makeText(context.getApplicationContext(), resourceId, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToastLong(Context context, int resourceId) {
        try {
            Toast.makeText(context.getApplicationContext(), resourceId, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
