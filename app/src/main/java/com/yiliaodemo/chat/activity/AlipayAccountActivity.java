package com.yiliaodemo.chat.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.AccountBean;
import com.yiliaodemo.chat.bean.WithDrawBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：管理微信账号页面
 * 作者：
 * 创建时间：2018/8/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AlipayAccountActivity extends BaseActivity {

    @BindView(R.id.alipay_account_et)
    EditText mAlipayAccountEt;
    @BindView(R.id.real_name_et)
    EditText mRealNameEt;
    @BindView(R.id.submit_tv)
    TextView mSubmitTv;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_alipay_account_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.alipay_account);
        getMyAccountInfo();
    }

    /**
     * 获取用户可提现金币
     */
    private void getMyAccountInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USEABLE_GOLD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<WithDrawBean<AccountBean>>>() {
            @Override
            public void onResponse(BaseResponse<WithDrawBean<AccountBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    WithDrawBean<AccountBean> balanceBean = response.m_object;
                    if (balanceBean != null) {
                        //设置账号
                        List<AccountBean> accountBeans = balanceBean.data;
                        if (accountBeans != null && accountBeans.size() > 0) {
                            for (AccountBean bean : accountBeans) {
                                if (bean.t_type == 0) {//0.支付宝 1.微信
                                    String mAlipayName = bean.t_real_name;
                                    String mAlipayAccountNumber = bean.t_account_number;

                                    if (!TextUtils.isEmpty(mAlipayName)) {
                                        mRealNameEt.setText(mAlipayName);
                                    }
                                    if (!TextUtils.isEmpty(mAlipayAccountNumber)) {
                                        mAlipayAccountEt.setText(mAlipayAccountNumber);
                                    }
                                    mRealNameEt.setEnabled(false);
                                    mAlipayAccountEt.setEnabled(false);
                                }
                            }
                        } else {
                            mRealNameEt.setEnabled(true);
                            mAlipayAccountEt.setEnabled(true);
                            mSubmitTv.setText(getResources().getString(R.string.finish));
                        }
                    } else {
                        mRealNameEt.setEnabled(true);
                        mAlipayAccountEt.setEnabled(true);
                        mSubmitTv.setText(getResources().getString(R.string.finish));
                    }
                }
            }
        });
    }

    @OnClick({R.id.submit_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_tv: {//保存
                String content = mSubmitTv.getText().toString().trim();
                if (content.equals(getResources().getString(R.string.save_safely))) {
                    mRealNameEt.setEnabled(true);
                    mAlipayAccountEt.setEnabled(true);
                    mSubmitTv.setText(getResources().getString(R.string.finish));
                } else {
                    saveInfo();
                }
                break;
            }
        }
    }

    /**
     * 保存信息
     */
    private void saveInfo() {
        String realName = mRealNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(realName)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_alipay_name);
            return;
        }
        String alipayAccountNumber = mAlipayAccountEt.getText().toString().trim();
        if (TextUtils.isEmpty(alipayAccountNumber)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_alipay_account);
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_real_name", realName);
        paramMap.put("t_nick_name", "");
        paramMap.put("t_account_number", alipayAccountNumber);
        paramMap.put("t_type", "0");//0.支付宝1.微信
        OkHttpUtils.post().url(ChatApi.MODIFY_PUT_FORWARD_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getApplicationContext(), R.string.save_success);
                    finish();
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.save_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.save_fail);
            }
        });
    }

}
