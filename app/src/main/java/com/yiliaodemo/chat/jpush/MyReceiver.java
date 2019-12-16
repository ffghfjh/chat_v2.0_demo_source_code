package com.yiliaodemo.chat.jpush;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.yiliaodemo.chat.activity.ScrollLoginActivity;
import com.yiliaodemo.chat.activity.SystemMessageActivity;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.socket.ConnectService;
import com.yiliaodemo.chat.socket.WakeupService;
import com.yiliaodemo.chat.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                LogUtil.i("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
                if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                    dealMessage(bundle, context);
                } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                    LogUtil.i("用户点击打开了通知");
                    //打开自定义的Activity
                    Intent i = new Intent(context, SystemMessageActivity.class);
                    i.putExtras(bundle);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                    LogUtil.i("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                    //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
                } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                    LogUtil.i("[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
                } else {
                    LogUtil.i("[MyReceiver] Unhandled intent - " + intent.getAction());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理自定义消息
     */
    private void dealMessage(Bundle bundle, Context context) {
        String customMessage = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        LogUtil.i("接收到推送下来的自定义消息: " + customMessage);
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extra)) {
                    try {
                        JSONObject json = new JSONObject(extra);
                        Iterator<String> it = json.keys();
                        while (it.hasNext()) {
                            String myKey = it.next();
                            if (myKey.equals("noticeType")) {
                                int result = json.getInt("noticeType");
                                if (result == 1) {
                                    exit(context, customMessage);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtil.i("This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtil.i("Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    /**
     * 退出
     */
    private void exit(Context context, String beenCloseDes) {
        try {
            AppManager.getInstance().setUserInfo(null);
            ChatUserInfo chatUserInfo = new ChatUserInfo();
            chatUserInfo.t_sex = 2;
            chatUserInfo.t_id = 0;
            SharedPreferenceHelper.saveAccountInfo(context, chatUserInfo);

            //关闭service
            finishJob(context);

            //IM登出
            JMessageClient.logout();

            //极光
            JPushInterface.stopPush(context);

            //关闭所有页面
            Intent intent = new Intent(context, ScrollLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constant.BEEN_CLOSE, true);
            intent.putExtra(Constant.BEEN_CLOSE_DES, beenCloseDes);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context, ScrollLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constant.BEEN_CLOSE, true);
            intent.putExtra(Constant.BEEN_CLOSE_DES, beenCloseDes);
            context.startActivity(intent);
        }
    }

    private void finishJob(Context context) {
        try {
            Intent connect = new Intent(context, ConnectService.class);
            context.stopService(connect);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent job = new Intent(context, WakeupService.class);
                context.stopService(job);

                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
                    if (allPendingJobs.size() > 0) {
                        jobScheduler.cancel(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
