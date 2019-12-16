package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
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
 * 功能描述：手机号码验证页面
 * 作者：
 * 创建时间：2018/7/23
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneVerifyActivity extends BaseActivity {

    @BindView(R.id.get_tv)
    TextView mSendVerifyTv;//发送验证码
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;

    private CountDownTimer mCountDownTimer;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_verify_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.phone_verify);
    }

    @OnClick({R.id.get_tv, R.id.finish_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_tv: {
                sendSmsVerifyCode();
                break;
            }
            case R.id.finish_tv: {
                finishVerify();
                break;
            }
        }
    }

    /**
     * 完成验证
     */
    private void finishVerify() {
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

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("phone", phone);
        paramMap.put("smsCode", verifyCode);
        OkHttpUtils.post().url(ChatApi.UPDATE_PHONE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                LogUtil.i("修改手机号==--", JSON.toJSONString(response));
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(), R.string.verify_good);
                        Intent intent = new Intent();
                        intent.putExtra(Constant.PHONE_MODIFY, phone);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
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
     * 发送短信验证码
     */
    private void sendSmsVerifyCode() {
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
        paramMap.put("resType", "2");
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
        mSendVerifyTv.setTextColor(getResources().getColor(R.color.white));
        mSendVerifyTv.setBackgroundResource(R.drawable.shape_send_verify_text_gray_background);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                String text = getResources().getString(R.string.re_send) + l / 1000 + getResources().getString(R.string.second);
                mSendVerifyTv.setText(text);
            }

            @Override
            public void onFinish() {
                mSendVerifyTv.setClickable(true);
                mSendVerifyTv.setTextColor(getResources().getColor(R.color.pink_main));
                mSendVerifyTv.setBackgroundColor(getResources().getColor(R.color.transparent));
                mSendVerifyTv.setText(R.string.get_code);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

}
