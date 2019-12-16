package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
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
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：注册 忘记密码页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.pass_code_et)
    EditText mPassCodeEt;
    @BindView(R.id.register_tv)
    TextView mRegisterTv;
    @BindView(R.id.pass_v)
    View mPassV;
    @BindView(R.id.des_tv)
    TextView mDesTv;

    private CountDownTimer mCountDownTimer;
    private int mJoinType;//进来的类型, 注册:  0  找回密码: 1
    private final int REGISTER = 0;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_register_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mJoinType = getIntent().getIntExtra(Constant.JOIN_TYPE, REGISTER);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        int FIND_BACK = 1;
        if (mJoinType == FIND_BACK) {//找回密码
            mRegisterTv.setVisibility(View.VISIBLE);
            mPassV.setVisibility(View.VISIBLE);
            mDesTv.setText(getString(R.string.new_password));
            mPassCodeEt.setHint(getString(R.string.please_set_new_password));
        }
    }

    @OnClick({ R.id.confirm_tv, R.id.login_tv, R.id.register_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_tv: {//确认
                if (mJoinType == REGISTER) {
                    register();//注册
                } else {
                    updatePassword();//忘记密码
                }
                break;
            }
            case R.id.login_tv: {//登录
                Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.register_tv: {//注册
                int joinTypeRegister = 0;
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeRegister);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    /**
     * 更新密码
     */
    private void updatePassword() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
//        String verifyCode = mCodeEt.getText().toString().trim();
//        if (TextUtils.isEmpty(verifyCode)) {
//            ToastUtil.showToast(getApplicationContext(), R.string.verify_code_number_null);
//            return;
//        }
        //密码
        String password = mPassCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_new_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(getApplicationContext(), R.string.new_length_wrong);
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("password", password);
//        paramMap.put("smsCode", verifyCode);
        OkHttpUtils.post().url(ChatApi.UP_PASSWORD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null) {//-2:验证码错误 -1:账号不存在 0:程序异常 1:修改成功
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToastLong(getApplicationContext(), R.string.set_new_password_success);
                        Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                }

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                dismissLoadingDialog();
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

        });
    }

    /**
     * 注册
     */

    private void register() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
//        String verifyCode = mCodeEt.getText().toString().trim();
//        if (TextUtils.isEmpty(verifyCode)) {
//            ToastUtil.showToast(getApplicationContext(), R.string.verify_code_number_null);
//            return;
//        }
        //密码
        String password = mPassCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(getApplicationContext(), R.string.length_wrong);
            return;
        }

        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("password", password);
        //paramMap.put("smsCode", verifyCode);
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        OkHttpUtils.post().url(ChatApi.REGISTER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                LogUtil.i("注册==--", JSON.toJSONString(response));
                dismissLoadingDialog();
                //状态码： -2：验证码错误-1:账号已存在,0:程序异常,1.注册成功
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToastLong(getApplicationContext(), R.string.register_success);
                        Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.register_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.register_fail);
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
     * 图形验证码
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

        final Dialog mDialog = new Dialog(RegisterActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.dialog_sms_verify_layout, null);
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
        Glide.with(RegisterActivity.this).load(url)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(code_iv);
        //点击换一张
        TextView change_tv = view.findViewById(R.id.change_tv);
        change_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(RegisterActivity.this).load(url)
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
                        //startCountDown();
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

//    /**
//     * 开始倒计时
//     */
//    private void startCountDown() {
//        mSendVerifyTv.setClickable(false);
//        mCountDownTimer = new CountDownTimer(60000, 1000) {
//            @Override
//            public void onTick(long l) {
//                String text = getResources().getString(R.string.re_send_one) + l / 1000 + getResources().getString(R.string.second);
//                mSendVerifyTv.setText(text);
//            }
//
//            @Override
//            public void onFinish() {
//                mSendVerifyTv.setClickable(true);
//                mSendVerifyTv.setText(R.string.get_code_one);
//                if (mCountDownTimer != null) {
//                    mCountDownTimer.cancel();
//                    mCountDownTimer = null;
//                }
//            }
//        }.start();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

}
