package com.yiliaodemo.chat.socket;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Socket帮助类
 * 作者：
 * 创建时间：2018/7/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SocketHelper {

    /**
     * 兼容启动service
     */
    public static void compatStartService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            //8.0 startService throw IllegalStateException
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}
