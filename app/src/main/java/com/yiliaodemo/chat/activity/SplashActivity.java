package com.yiliaodemo.chat.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.UserCenterBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.socket.ConnectService;
import com.yiliaodemo.chat.socket.WakeupService;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：启动页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SplashActivity extends BaseActivity {

    private boolean mHasLightSensor = true;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_splash_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());
        LogUtil.i("设备标识: " + deviceNumber);
        checkEme();
        final ChatUserInfo chatUserInfo = SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        initTIM();
        loginSocket(chatUserInfo);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mHasLightSensor) {
                    if (chatUserInfo != null && chatUserInfo.t_id > 0) {
                        if (chatUserInfo.t_sex == 2) {//还没有选择性别
                            startActivity(new Intent(getApplicationContext(), ChooseGenderActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), ScrollLoginActivity.class));
                    }
                }
                finish();
            }
        }, 2000);

    }

    /**
     * 检测模拟器
     */
    private void checkEme() {
        if (DevicesUtil.notHasLightSensorManager(getApplicationContext())) {
            mHasLightSensor = false;
        }
    }

    /**
     * 开启服务并登陆socket
     */
    private void loginSocket(ChatUserInfo chatUserInfo) {
        if (chatUserInfo != null && chatUserInfo.t_id > 0) {
            if (chatUserInfo.t_sex != 2) {//有性别的话 就直接登录socket
                Intent intent = new Intent(getApplicationContext(), ConnectService.class);
                startService(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(),
                            WakeupService.class.getName()))
                            .setPeriodic(5 * 60 * 1000L)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .build();
                    if (jobScheduler != null) {
                        jobScheduler.schedule(jobInfo);
                    }
                }
            }

            //登录Im
            loginJIM(chatUserInfo);

            //极光
            if (JPushInterface.isPushStopped(getApplicationContext())) {
                JPushInterface.resumePush(getApplicationContext());
            }

            //获取个人信息
            getInfo(chatUserInfo.t_id);

            //更新登录时间
            updateLoginTime(chatUserInfo.t_id);
        }
    }

    /**
     * 获取个人中心信息
     */
    private void getInfo(final int userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        OkHttpUtils.post().url(ChatApi.INDEX)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
            @Override
            public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UserCenterBean bean = response.m_object;
                    if (bean != null) {
                        ChatUserInfo info = new ChatUserInfo();
                        info.t_id = userId;
                        info.nickName = bean.nickName;
                        info.headUrl = bean.handImg;
                        info.gold = bean.amount;
                        info.t_is_vip = bean.t_is_vip;
                        info.t_role = bean.t_role;
                        info.t_sex = bean.t_sex;
                        AppManager.getInstance().setUserInfo(info);
                        //保存
                        SharedPreferenceHelper.saveUserVip(getApplicationContext(), bean.t_is_vip);
                        SharedPreferenceHelper.saveRoleInfo(getApplicationContext(), bean.t_role);
                    }
                }
            }
        });
    }

    /**
     * 初始化IM
     */
    private void initTIM() {
        //三体
        TTTRtcEngine engine = TTTRtcEngine.create(getApplicationContext(), Constant.TTT_APP_ID, true, null);
        engine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
    }

    /**
     * 登录Im
     */
    private void loginJIM(final ChatUserInfo chatUserInfo) {
        //检测账号是否登陆
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null && !TextUtils.isEmpty(myInfo.getUserName())) {
            //登录
            JMessageClient.login(myInfo.getUserName(), String.valueOf(10000 + chatUserInfo.t_id),
                    new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                LogUtil.i("极光im登录成功");
                            } else {
                                LogUtil.i("极光im登录失败:  " + i + "描述: " + s);
                            }
                        }
                    });
        } else {
            JMessageClient.register(String.valueOf(10000 + chatUserInfo.t_id), String.valueOf(10000 + chatUserInfo.t_id),
                    new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0 || i == 898001) {
                                LogUtil.i("极光注册成功");
                                //登录
                                JMessageClient.login(String.valueOf(10000 + chatUserInfo.t_id), String.valueOf(10000 + chatUserInfo.t_id),
                                        new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 0) {
                                                    LogUtil.i("极光im登录成功");
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    /**
     * 更新登录时间
     */
    private void updateLoginTime(int userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        OkHttpUtils.post().url(ChatApi.UP_LOGIN_TIME)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("更新登录时间成功");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            TTTRtcEngine.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
