package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.VipMoneyRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.bean.VipBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.pay.PayResult;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
 * 功能描述   VIP会员页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VipCenterActivity extends BaseActivity {

    @BindView(R.id.money_rv)
    RecyclerView mMoneyRv;
    @BindView(R.id.alipay_check_iv)
    ImageView mAlipayCheckIv;
    @BindView(R.id.wechat_check_iv)
    ImageView mWechatCheckIv;

    //支付
    private IWXAPI mWxApi;
    //支付宝
    private static final int SDK_PAY_FLAG = 1;

    private VipMoneyRecyclerAdapter mVipMoneyRecyclerAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_vip_center_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.vip_title);
        mWxApi = WXAPIFactory.createWXAPI(mContext, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        initView();
        getVipList();
    }

    private void initView() {
        //默认选中微信
        mWechatCheckIv.setSelected(true);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext);
        mMoneyRv.setLayoutManager(gridLayoutManager);
        mVipMoneyRecyclerAdapter = new VipMoneyRecyclerAdapter(this);
        mMoneyRv.setAdapter(mVipMoneyRecyclerAdapter);
    }

    /**
     * 获取VIP列表
     */
    private void getVipList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VIP_SET_MEAL_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<VipBean>>() {
            @Override
            public void onResponse(BaseListResponse<VipBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<VipBean> vipBeans = response.m_object;
                    if (vipBeans != null && vipBeans.size() > 0) {
                        if (vipBeans.size() >= 1) {
                            vipBeans.get(0).isSelected = true;
                        }
                        mVipMoneyRecyclerAdapter.loadData(vipBeans);
                    }
                }
            }
        });
    }

    @OnClick({R.id.alipay_rl, R.id.wechat_rl, R.id.go_pay_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alipay_rl: {//支付宝
                if (mAlipayCheckIv.isSelected()) {
                    return;
                }
                mAlipayCheckIv.setSelected(true);
                mWechatCheckIv.setSelected(false);
                break;
            }
            case R.id.wechat_rl: {//微信
                if (mWechatCheckIv.isSelected()) {
                    return;
                }
                mWechatCheckIv.setSelected(true);
                mAlipayCheckIv.setSelected(false);
                break;
            }
            case R.id.go_pay_tv: {//去支付
                VipBean mSelectVipBean = mVipMoneyRecyclerAdapter.getSelectBean();
                if (mSelectVipBean == null) {
                    ToastUtil.showToast(mContext, R.string.please_choose_vip);
                    return;
                }
                if (!mAlipayCheckIv.isSelected() && !mWechatCheckIv.isSelected()) {
                    ToastUtil.showToast(mContext, R.string.please_choose_pay_way);
                    return;
                }

                int payTypeAliPay;
                if (mAlipayCheckIv.isSelected()) {//支付宝选中
                    payTypeAliPay = 0;//0.支付宝
                } else {
                    payTypeAliPay = 1;//1.微信
                }
                payForVip(mSelectVipBean.t_id, payTypeAliPay);
                break;
            }
        }
    }

    //-------------------------支付-------------------------

    /**
     * VIP支付
     * 0.支付宝 1.微信
     */
    private void payForVip(int vipId, final int payType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("setMealId", String.valueOf(vipId));
        paramMap.put("payType", String.valueOf(payType));
        OkHttpUtils.post().url(ChatApi.VIP_STORE_VALUE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.i("Vip支付: " + response);
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = JSON.parseObject(response);
                    if (object.containsKey("m_istatus") && object.getIntValue("m_istatus") == 1) {
                        if (payType == 1) {//微信
                            JSONObject payObject = object.getJSONObject("m_object");
                            payWithWeChat(payObject);
                        } else if (payType == 0) {//支付宝
                            String orderInfo = object.getString("m_object");
                            if (!TextUtils.isEmpty(orderInfo)) {
                                payWithAlipay(orderInfo);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 微信支付
     */
    private void payWithWeChat(JSONObject payObject) {
        if (mWxApi != null && mWxApi.isWXAppInstalled()) {
            try {
                PayReq request = new PayReq();
                request.appId = payObject.getString("appid");
                request.partnerId = payObject.getString("partnerid");
                request.prepayId = payObject.getString("prepayid");
                request.packageValue = "Sign=WXPay";
                request.nonceStr = payObject.getString("noncestr");
                request.timeStamp = payObject.getString("timestamp");
                request.sign = payObject.getString("sign");
                boolean res = mWxApi.sendReq(request);
                if (res) {//设置是充值Vip
                    AppManager.getInstance().setIsWeChatForVip(true);
                    finish();
                }
                LogUtil.i("res : " + res);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(mContext, R.string.pay_vip_fail);
            }
        } else {
            ToastUtil.showToast(mContext, R.string.not_install_we_chat);
        }
    }

    /**
     * 支付宝支付
     */
    private void payWithAlipay(final String orderInfo) {//订单信息
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(VipCenterActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_success);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

}
