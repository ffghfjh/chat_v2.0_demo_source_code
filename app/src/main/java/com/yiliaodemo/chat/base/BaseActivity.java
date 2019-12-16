package com.yiliaodemo.chat.base;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gyf.barlibrary.ImmersionBar;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ScrollLoginActivity;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.socket.ConnectManager;
import com.yiliaodemo.chat.socket.ConnectService;
import com.yiliaodemo.chat.socket.WakeupService;
import com.yiliaodemo.chat.socket.domain.Mid;
import com.yiliaodemo.chat.socket.domain.SocketResponse;
import com.yiliaodemo.chat.util.ActivityManager;
import com.yiliaodemo.chat.util.DialogUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述： Activity基类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class BaseActivity extends FragmentActivity {

    //根布局
    protected LinearLayout mBaseLayout;
    protected FrameLayout mBaseContent;

    //标题栏
    protected View mHeadLayout;
    protected View mLeftFl;
    protected ImageView mLeftIv;
    protected TextView mTvTitle;
    protected TextView mRightTv;

    //沉浸式状态栏
    protected ImmersionBar mImmersionBar;
    //注解
    private Unbinder mUnbinder;
    protected Context mContext;
    //加载dialog
    private Dialog mDialogLoading;

    //接收socket 广播
    private MyBroadcastReceiver mMyBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        mContext = this;

        boolean supportFullScr = supportFullScreen();
        if (supportFullScr) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.base_activity_base);

        //1.设置状态栏样式
        if (!supportFullScr) {
            setStatusBarStyle();
        }

        //2.设置是否屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //3.初始化http请求request集合，保证在activity结束的时候终止http请求
        //4.初始化view
        initView();
        //5.添加view到content容器中，子类实现
        addIntoContent(getContentView());
        //6.初始化view，设置onclick监听器
        //解决继承自BaseActivity且属于当前库(framework)的子类butterknife不能使用Bindview的注解，onclick的注解
        initSubView();
        //7.register eventbus
        //8.view已添加到container
        onContentAdded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //广播接收 自己socket消息
            IntentFilter filter = new IntentFilter(ConnectManager.BROADCAST_ACTION);
            if (mMyBroadcastReceiver == null) {
                mMyBroadcastReceiver = new MyBroadcastReceiver();
            }
            registerReceiver(mMyBroadcastReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needDestroyBroadcastOnStop()) {
            destroyBroadcast();
        }
    }

    /**
     * 销毁广播
     */
    protected void destroyBroadcast() {
        try {
            if (mMyBroadcastReceiver != null) {
                unregisterReceiver(mMyBroadcastReceiver);
                mMyBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否需要在onStop的时候销毁广播
     */
    protected boolean needDestroyBroadcastOnStop() {
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        OkHttpUtils.getInstance().cancelTag(BaseActivity.this);
        ActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 初始化view
     */
    private void initView() {
        mDialogLoading = DialogUtil.showLoadingDialog(this);
        mBaseLayout = findViewById(R.id.base_layout);
        mBaseContent = findViewById(R.id.base_content);

        mHeadLayout = findViewById(R.id.head);
        //left
        mLeftFl = findViewById(R.id.left_fl);
        mLeftIv = findViewById(R.id.left_image);
        //middle
        mTvTitle = findViewById(R.id.middle_title);
        //right
        mRightTv = findViewById(R.id.right_text);

        //默认处理title左上view的点击事件（back）
        mLeftFl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 添加view到容器中
     */
    private void addIntoContent(View view) {
        if (view != null) {
            if (!attachMergeLayout()) {
                mBaseContent.removeAllViews();
                mBaseContent.addView(view);
            }
            mUnbinder = ButterKnife.bind(this);
        } else {
            try {
                throw new Exception("content view can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return view
     */
    protected abstract View getContentView();

    /**
     * 加载布局
     */
    protected View inflate(@LayoutRes int resource) {
        return LayoutInflater.from(this).inflate(resource, null);
    }

    /**
     * 加载布局
     */
    protected View inflate(@LayoutRes int resource, @Nullable ViewGroup root) {
        return LayoutInflater.from(this).inflate(resource, root);
    }

    /**
     * 初始化 subView，一般只用于在framework中BaseActivity子类，为了解决
     * 继承自BaseActivity且属于当前库(framework)的子类butterknife不能使用Bindview的注解，onclick的注解
     */
    protected void initSubView() {

    }

    /**
     * @return return true 添加的layout以merge标签作为根布局, false layout不以merge标签作为根布局
     */
    protected boolean attachMergeLayout() {
        return false;
    }

    /**
     * 添加view完成回调，用于初始化数据
     */
    protected abstract void onContentAdded();

    /**
     * 是否需要显示顶部栏
     */
    protected final void needHeader(boolean isNeed) {
        if (isNeed) {
            mHeadLayout.setVisibility(View.VISIBLE);
        } else {
            mHeadLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置页面title
     */
    @Override
    public final void setTitle(int res) {
        if (res > 0) {
            setTitle(getResources().getText(res));
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置页面title
     */
    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
            mTvTitle.setVisibility(View.VISIBLE);
        } else {
            mTvTitle.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置返回不可见
     */
    public void setBackVisibility(int visibility) {
        mLeftFl.setVisibility(visibility);
    }

    /**
     * 是否支持全屏
     */
    protected boolean supportFullScreen() {
        return false;
    }

    /**
     * 设置状态栏背景
     */
    protected void setStatusBarStyle() {
        if (!isImmersionBarEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果不是沉浸式,就设置为黑色字体
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            return;
        }
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true).navigationBarColor(R.color.black).init();
    }

    /**
     * 是否可以使用沉浸式
     */
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    /**
     * 设置状态栏背景色资源id
     */
    protected int getStatusBarColorResId() {
        return R.color.white;
    }

    /**
     * 设置状态栏背景色
     */
    protected int getStatusBarColor() {
        if (Build.VERSION.SDK_INT > 22) {
            return getColor(getStatusBarColorResId());
        } else {
            return getResources().getColor(getStatusBarColorResId());
        }
    }

    /**
     * 显示请求网络数据进度条
     */
    public void showLoadingDialog() {
        try {
            if (!isFinishing() && mDialogLoading != null && !mDialogLoading.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭请求网络数据进度条
     */
    public void dismissLoadingDialog() {
        try {
            if (!isFinishing() && mDialogLoading != null && mDialogLoading.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置title右边文字
     */
    protected void setRightText(int resourceId) {
        if (resourceId > 0) {
            mRightTv.setVisibility(View.VISIBLE);
            mRightTv.setText(resourceId);
        }
    }

    /**
     * 获取UserId
     */
    public String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

    /**
     * 获取Vip
     */
    public int getUserVip() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //是否VIP 0.是1.否
                return userInfo.t_is_vip;
            } else {
                return SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_is_vip;
            }
        }
        return 2;
    }

    /**
     * 获取角色
     */
    public int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_role;
            }
        }
        return 0;
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ConnectManager.MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                try {
                    SocketResponse response = JSON.parseObject(message, SocketResponse.class);
                    if (response != null) {
                        if (response.mid == Mid.HAVE_HANG_UP) {//对方已挂断
                            onHangUp();
                        } else if (response.mid == Mid.BEAN_SUSPEND) {//被封号
                            beenShutDown();
                            exit();
                        } else if (response.mid == Mid.SEND_VIRTUAL_MESSAGE) {//发送模拟消息
                            String content = response.msgContent;
                            int activeUserId = response.activeUserId;
                            if (!TextUtils.isEmpty(content) && activeUserId > 0) {
                                sendMessage(content, activeUserId);
                            }
                        } else if (response.mid == Mid.MONEY_NOT_ENOUGH) {//用户余额不住一分钟
                            moneyNotEnough();
                        } else if (response.mid == Mid.ACTIVE_NEW_COMMENT) {//动态新评论
                            onActiveNewComment();
                        } else if (response.mid == Mid.CHAT_LINK) {//通知连线
                            onUserLinkAnchor();
                        } else if (response.mid == Mid.QUICK_START_HINT_ANCHOR) {//
                            String content = response.msgContent;
                            if (!TextUtils.isEmpty(content)) {
                                onQuickStartSocketHintAnchor(content);
                            }
                        } else if (response.mid == Mid.VIDEO_CHAT_START_HINT) {//
                            String content = response.msgContent;
                            if (!TextUtils.isEmpty(content)) {
                                onVideoStartSocketHint(content);
                            }
                        } else if (response.mid == Mid.BIG_ROOM_COUNT_CHANGE) {//大房间总人数变化
                            onBigRoomCountChange(response.userCount, response.sendUserName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 对方已挂断
     */
    protected void onHangUp() {

    }

    /**
     * 被封号处理
     */
    protected void beenShutDown() {

    }

    /**
     * 用户余额不足一分钟
     */
    protected void moneyNotEnough() {

    }

    /**
     * 动态新评论
     */
    protected void onActiveNewComment() {

    }

    /**
     * 主播在速配中, 用户通过原来的方式连接主播,此时关闭速配页面
     */
    protected void onUserLinkAnchor() {

    }

    /**
     * 主播开启速配的时候,下发提示消息,只提示主播
     */
    protected void onQuickStartSocketHintAnchor(String hintMessage) {

    }

    /**
     * 接通视频的时候,下发提示消息,主播用户都要提示
     */
    protected void onVideoStartSocketHint(String hintMessage) {

    }

    /**
     * 大房间总人数变化
     */
    protected void onBigRoomCountChange(int count, String sendUserName) {

    }

    /**
     * 退出
     */
    private void exit() {
        try {
            AppManager.getInstance().setUserInfo(null);
            ChatUserInfo chatUserInfo = new ChatUserInfo();
            chatUserInfo.t_sex = 2;
            chatUserInfo.t_id = 0;
            SharedPreferenceHelper.saveAccountInfo(mContext, chatUserInfo);

            //关闭service
            finishJob();

            //IM登出
            JMessageClient.logout();

            //极光
            JPushInterface.stopPush(mContext);

            Window window = getWindow();
            if (window != null) {
                window.getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Constant.BEEN_CLOSE, true);
                        startActivity(intent);
                        finish();
                    }
                }, 100);
            } else {
                Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Constant.BEEN_CLOSE, true);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constant.BEEN_CLOSE, true);
            startActivity(intent);
            finish();
        }
    }

    private void finishJob() {
        try {
            Intent connect = new Intent(getApplicationContext(), ConnectService.class);
            stopService(connect);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent job = new Intent(getApplicationContext(), WakeupService.class);
                stopService(job);

                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
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

    /**
     * 发送消息
     */
    private void sendMessage(String text, int userId) {
        String mTargetId = String.valueOf(10000 + userId);
        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
        if (mConversation == null) {
            mConversation = Conversation.createSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
        }

        TextContent textContent = new TextContent(text);
        Message message = mConversation.createSendMessage(textContent);
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    LogUtil.i("发送模拟消息成功");
                } else {
                    LogUtil.i("发送模拟消息失败:code  " + i + " des: " + s);
                }
            }
        });
        MessageSendingOptions options = new MessageSendingOptions();
        options.setShowNotification(false);
        JMessageClient.sendMessage(message, options);
    }

}
