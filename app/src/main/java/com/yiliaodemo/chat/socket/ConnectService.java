package com.yiliaodemo.chat.socket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.yiliaodemo.chat.BuildConfig;
import com.yiliaodemo.chat.util.LogUtil;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：开启长连接的服务
 * 作者：lyf
 * 创建时间：2017/5/20.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ConnectService extends Service {

    private ConnectThread connectThread;
    private volatile ServiceHandler mServiceHandler;
    private final int CHECK = 0;
    private final int CHANGE = 1;// 46秒后改变状态为离线
    private final long INTERVAL = 10 * 1000L;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "com.yiliaodemo.chat.channelid";
            String CHANNEL_NAME = "com.yiliaodemo.chat.channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID).build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //使用子线程开启连接
        if (connectThread == null) {
            connectThread = new ConnectThread("mina", getApplicationContext());
            connectThread.start();
            mServiceHandler = new ServiceHandler(connectThread.getLooper());
            mServiceHandler.sendEmptyMessageDelayed(CHECK, INTERVAL);
        }
        LogUtil.i(" -------------服务 onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connectThread != null) {
            connectThread.disConnection();
            connectThread = null;
        }
        if (mServiceHandler != null) {
            mServiceHandler.removeCallbacksAndMessages(null);
            mServiceHandler = null;
        }
    }

    /**
     * 负责调用connect manager类来完成与服务器的连接
     */
    class ConnectThread extends HandlerThread {

        boolean isConnection;
        ConnectManager mManager;

        ConnectThread(String name, Context context) {
            super(name);
            ConnectConfig config = new ConnectConfig.Builder(context)
                    .setIp(BuildConfig.socketIp)
                    .setPort(12580)//10026
                    .setReadBufferSize(2048)//10240
                    .setConnectionTimeout(30 * 1000L)
                    .build();
            //创建连接的管理类
            mManager = new ConnectManager(config, ConnectService.this);
        }

        ConnectManager getManager() {
            return mManager;
        }

        @Override
        protected void onLooperPrepared() {
            //利用循环请求连接
            while (true) {
                isConnection = mManager.connect();
                if (isConnection) {
                    //当请求成功的时候,跳出循环
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 断开连接
         */
        void disConnection() {
            mManager.disConnect();
        }

    }

    private final class ServiceHandler extends Handler {

        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK: {
                    try {
                        ConnectManager manager = connectThread.getManager();
                        if (manager != null) {
                            boolean isConnect = manager.isSocketConnect();
                            if (!isConnect) {
                                manager.connect();
                            }
                            //LogUtil.i("==--isConnect:  " + isConnect);
                        }
                        if (mServiceHandler != null) {
                            mServiceHandler.sendEmptyMessageDelayed(CHECK, INTERVAL);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CHANGE: {
                    try {
                        ConnectManager manager = connectThread.getManager();
                        LogUtil.i("人为改变为连接中断,然后重连");
                        if (manager != null) {
                            LogUtil.i("人为改变为连接中断,然后重连 manager不为空");
                            manager.setMineConnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    void sendChangeMessage() {
        if (mServiceHandler != null) {
            mServiceHandler.removeMessages(CHANGE);
            mServiceHandler.sendEmptyMessageDelayed(CHANGE, 46 * 1000L);
        }
    }

}
