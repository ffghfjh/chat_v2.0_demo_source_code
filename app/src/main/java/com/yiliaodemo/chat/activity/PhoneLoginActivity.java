package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.socket.ConnectService;
import com.yiliaodemo.chat.socket.WakeupService;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.SystemUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.util.VerifyUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：登录页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneLoginActivity extends BaseActivity {

    @BindView(R.id.send_verify_tv)
    TextView mSendVerifyTv;//发送验证码
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;
    //账号密码登录
    @BindView(R.id.account_v)
    View mAccountV;
    @BindView(R.id.account_small_tv)
    TextView mAccountSmallTv;
    @BindView(R.id.account_big_tv)
    TextView mAccountBigTv;
    //验证码登录
    @BindView(R.id.verify_small_tv)
    TextView mVerifySmallTv;
    @BindView(R.id.verify_big_tv)
    TextView mVerifyBigTv;
    @BindView(R.id.verify_v)
    View mVerifyV;
    @BindView(R.id.down_text_tv)
    TextView mDownTextTv;
    //忘记密码
    @BindView(R.id.forget_tv)
    TextView mForgetTv;

    private CountDownTimer mCountDownTimer;
    private final int ACCOUNT = 0;//账号密码登录

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_login_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mCodeEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mAccountV.getVisibility() == View.VISIBLE) {//账号密码登录
                        requestAccountLogin();
                    } else {//短信验证码登录
                        requestSmsLogin();
                    }
                    return true;
                }
                return false;
            }
        });
        switchPosition(ACCOUNT);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @OnClick({R.id.login_tv, R.id.send_verify_tv, R.id.agree_tv, R.id.account_ll, R.id.verify_code_ll,
            R.id.register_tv, R.id.forget_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv: {//登录
                if (mAccountV.getVisibility() == View.VISIBLE) {//账号密码登录
                    requestAccountLogin();
                } else {//短信验证码登录
                    requestSmsLogin();
                }
                break;
            }
            case R.id.send_verify_tv: {//发送短信验证码
                showVerifyDialog();
                break;
            }
            case R.id.agree_tv: {//用户协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                startActivity(intent);
                break;
            }
            case R.id.account_ll: {//账号密码登录
                switchPosition(ACCOUNT);
                break;
            }
            case R.id.verify_code_ll: {//验证码登录
                int verify = 1;
                switchPosition(verify);
                break;
            }
            case R.id.register_tv: {//注册
                int joinTypeRegister = 0;//注册
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeRegister);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.forget_tv: {//忘记密码
                int joinTypeForget = 1;//忘记密码
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeForget);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    /**
     * 切换
     */
    private void switchPosition(int position) {
        if (position == ACCOUNT) {//账号密码
            if (mAccountV.getVisibility() == View.VISIBLE) {
                return;
            }

            mAccountBigTv.setVisibility(View.VISIBLE);
            mAccountSmallTv.setVisibility(View.GONE);
            mVerifyBigTv.setVisibility(View.GONE);
            mVerifySmallTv.setVisibility(View.VISIBLE);
            mAccountV.setVisibility(View.VISIBLE);
            mVerifyV.setVisibility(View.GONE);

            mDownTextTv.setText(getString(R.string.password));
            mCodeEt.setHint(getString(R.string.please_input_password));
            mSendVerifyTv.setVisibility(View.GONE);
            mForgetTv.setVisibility(View.VISIBLE);
            mCodeEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mCodeEt.setText("");
            mMobileEt.setText("");
        } else {
            if (mVerifyV.getVisibility() == View.VISIBLE) {
                return;
            }

            mVerifyBigTv.setVisibility(View.VISIBLE);
            mVerifySmallTv.setVisibility(View.GONE);
            mAccountBigTv.setVisibility(View.GONE);
            mAccountSmallTv.setVisibility(View.VISIBLE);
            mVerifyV.setVisibility(View.VISIBLE);
            mAccountV.setVisibility(View.GONE);

            mDownTextTv.setText(getString(R.string.verify_code));
            mCodeEt.setHint(getString(R.string.please_verify_code));
            mSendVerifyTv.setVisibility(View.VISIBLE);
            mForgetTv.setVisibility(View.INVISIBLE);
            mCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
            mCodeEt.setText("");
            mMobileEt.setText("");
        }
    }

    /**
     * 账号密码登录
     */
    private void requestAccountLogin() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        //密码
        String password = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(getApplicationContext(), R.string.length_wrong);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("password", password);
        OkHttpUtils.post().url(ChatApi.USER_LOGIN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.phone = phone;
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            loginSocket(userInfo);
                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
                            if (userInfo.t_sex == 2) {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                            }
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
                    } else {
                        if (!TextUtils.isEmpty(response.m_strMessage)) {
                            ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
            }
        });

    }

    /**
     * 请求短信验证码登录
     */
    private void requestSmsLogin() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
        String verifyCode = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_code_number_null);
            return;
        }
        //获取真实IP
        requestSmsLogin(phone, verifyCode);
    }

    /**
     * 发送短信验证码
     */
    private void sendSmsVerifyCode(String imageCode) {
        String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("resType", "1");
        paramMap.put("verifyCode", imageCode);
        OkHttpUtils.post().url(ChatApi.SEND_SMS_CODE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                LogUtil.i("获取短信验证码==--", JSON.toJSONString(response));
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.send_success))) {
                        ToastUtil.showToast(getApplicationContext(), R.string.send_success_des);
                        startCountDown();
                    }
                } else if (response != null && response.m_istatus == NetCode.FAIL) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message)) {
                        ToastUtil.showToast(getApplicationContext(), message);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        mSendVerifyTv.setClickable(false);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                String text = getResources().getString(R.string.re_send_one) + l / 1000 + getResources().getString(R.string.second);
                mSendVerifyTv.setText(text);
            }

            @Override
            public void onFinish() {
                mSendVerifyTv.setClickable(true);
                mSendVerifyTv.setText(R.string.get_code_one);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }

    /**
     * 开启服务并登陆
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
            loginJIM(chatUserInfo);

            //极光
            if (JPushInterface.isPushStopped(getApplicationContext())) {
                JPushInterface.resumePush(getApplicationContext());
            }
        }
    }

    /**
     * 登录Im
     */
    private void loginJIM(final ChatUserInfo chatUserInfo) {
        //检测账号是否登陆
        cn.jpush.im.android.api.model.UserInfo myInfo = JMessageClient.getMyInfo();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }


    /**
     * 验证码登录方式登录
     */
    private void requestSmsLogin(final String phone, String verifyCode) {
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("smsCode", verifyCode);
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        OkHttpUtils.post().url(ChatApi.LOGIN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                LogUtil.i("短信验证码登录==--", JSON.toJSONString(response));
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.phone = phone;
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            loginSocket(userInfo);
                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
                            if (userInfo.t_sex == 2) {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                            }
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            showBeenCloseDialog(message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
                    } else {
                        if (!TextUtils.isEmpty(response.m_strMessage)) {
                            ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 被封号
     */
    private void showVerifyDialog() {
        String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }

        final Dialog mDialog = new Dialog(PhoneLoginActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(PhoneLoginActivity.this).inflate(R.layout.dialog_sms_verify_layout, null);
        setVerifyDialogView(view, mDialog, phone);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setVerifyDialogView(View view, final Dialog mDialog, final String phone) {
        //关闭
        ImageView cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //图片
        final ImageView code_iv = view.findViewById(R.id.code_iv);
        final String url = ChatApi.GET_VERIFY + phone;
        //加载
        Glide.with(PhoneLoginActivity.this).load(url)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(code_iv);
        //点击换一张
        TextView change_tv = view.findViewById(R.id.change_tv);
        change_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(PhoneLoginActivity.this).load(url)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(code_iv);
            }
        });
        //验证码
        final EditText code_et = view.findViewById(R.id.code_et);
        //确认
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code_et.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_input_image_code);
                    return;
                }
                checkVerifyCode(code, phone);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 验证图形验证码是否正确
     */
    private void checkVerifyCode(final String code, String phone) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("verifyCode", code);
        OkHttpUtils.post().url(ChatApi.GET_VERIFY_CODE_IS_CORRECT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    sendSmsVerifyCode(code);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.wrong_image_code);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_image_code);
            }
        });
    }

    /**
     * 被封号
     */
    private void showBeenCloseDialog(String des) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_been_close_layout, null);
        setDialogView(view, mDialog, des);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog, String des) {
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        see_des_tv.setText(des);
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //查看规则
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

}
