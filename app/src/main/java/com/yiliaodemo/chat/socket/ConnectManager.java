package com.yiliaodemo.chat.socket;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiliaodemo.chat.activity.WaitActorActivity;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.socket.domain.Mid;
import com.yiliaodemo.chat.socket.domain.SocketResponse;
import com.yiliaodemo.chat.socket.domain.UserLoginReq;
import com.yiliaodemo.chat.util.LogUtil;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 连接的管理类
 * Created by lyf on 2017/5/20.
 */

public class ConnectManager {

    public static final String BROADCAST_ACTION = "com.yiliaodemo.chat.socket";
    public static final String MESSAGE = "message";

    private ConnectConfig mConfig;//配置文件
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    //心跳包内容
    private static final String HEARTBEATREQUEST = "0x11";
    private static final String HEARTBEATRESPONSE = "01010";

    private ConnectService mConnectService;
    private boolean mMineConnect = false;

    ConnectManager(ConnectConfig mConfig, ConnectService connectService) {
        mConnectService = connectService;
        this.mConfig = mConfig;
        this.mContext = new WeakReference<>(mConfig.getContext());
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        //创建连接对象
        mConnection = new NioSocketConnector();
        //设置连接地址
        mConnection.setDefaultRemoteAddress(mAddress);
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        //设置过滤
        mConnection.getFilterChain().addLast("logger", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory(Charset.forName("UTF-8"))));//自定义解编码器
        //设置连接监听
        mConnection.setHandler(new DefaultHandler(mContext.get()));
    }

    private class DefaultHandler extends IoHandlerAdapter {

        private Context context;

        DefaultHandler(Context context) {
            this.context = context;
        }

        /**
         * 连接成功时回调的方法
         */
        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            //向服务器注册当前用户
            ChatUserInfo chatUserInfo;
            if (AppManager.getInstance().getUserInfo() != null) {
                chatUserInfo = AppManager.getInstance().getUserInfo();
            } else {
                chatUserInfo = SharedPreferenceHelper.getAccountInfo(context);
            }
            if (chatUserInfo != null && chatUserInfo.t_id > 0) {
                UserLoginReq ui = new UserLoginReq();
                ui.setUserId(chatUserInfo.t_id);
                ui.setT_is_vip(chatUserInfo.t_is_vip);
                ui.setT_role(chatUserInfo.t_role);
                if (ui.getT_sex() != 2) {
                    ui.setT_sex(chatUserInfo.t_sex);
                }
                ui.setMid(30001);
                session.write(JSONObject.toJSONString(ui));
            }
            //当与服务器连接成功时,将我们的session保存到我们的session manager类中,从而可以发送消息到服务器
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            mSession = null;
            LogUtil.i("sessionClosed 关闭了");
            super.sessionClosed(session);
        }

        /**
         * 接收到消息时回调的方法
         */
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            if (context != null) {
                mMineConnect = true;
                String content = message.toString().trim();
                //LogUtil.i("接收到的消息: " + content);
                if (content.equals(HEARTBEATREQUEST)) {//心跳消息
                    try {
                        session.write(HEARTBEATRESPONSE);
                        if (mConnectService != null) {
                            mConnectService.sendChangeMessage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                //将接收到的消息利用广播发送出去
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, content);
                context.sendBroadcast(intent);
                openPage(context, content);
            }
        }
    }

    /**
     * 跳转页面
     */
    private void openPage(Context context, String message) {
        try {
            SocketResponse response = JSON.parseObject(message, SocketResponse.class);
            if (response != null) {
                if (response.mid == Mid.CHAT_LINK || response.mid == Mid.USER_GET_INVITE) { //用户来视频了 用户收到邀请
                    int roomId = response.roomId;
                    int userId = response.connectUserId;
                    int satisy = response.satisfy;
                    Intent videoIntent = new Intent(context, WaitActorActivity.class);
                    videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    videoIntent.putExtra(Constant.ROOM_ID, roomId);
                    videoIntent.putExtra(Constant.PASS_USER_ID, userId);
                    if (response.mid == Mid.USER_GET_INVITE) {
                        videoIntent.putExtra(Constant.USER_HAVE_MONEY, satisy);
                    }
                    try {
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                videoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                } else if (response.mid == Mid.VIDEO_CHAT_START_HINT) {//接通视频的时候,下发提示消息,存在内存中,
                    //用于用户速配进入的时候,提示
                    String hintMessage = response.msgContent;
                    if (AppManager.getInstance() != null && !TextUtils.isEmpty(hintMessage)) {
                        AppManager.getInstance().mVideoStartHint = hintMessage;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与服务器连接的方法
     */
    boolean connect() {
        try {
            ConnectFuture mConnectFuture = mConnection.connect();
            mConnectFuture.awaitUninterruptibly();
            mSession = mConnectFuture.getSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return mSession != null;
    }

    /**
     * 断开连接的方法
     */
    void disConnect() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
        mMineConnect = false;
    }

    boolean isSocketConnect() {
        return mSession != null && mSession.isConnected() && mMineConnect;
    }

    void setMineConnect() {
        mMineConnect = false;
    }

}
