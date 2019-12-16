package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.ChargeRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.BalanceBean;
import com.yiliaodemo.chat.bean.ChargeListBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.pay.PayResult;
import com.yiliaodemo.chat.util.CodeUtil;
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
 * 功能描述   充值页面
 * 作者：
 * 创建时间：2018/6/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChargeActivity extends BaseActivity {

//    @BindView(R.id.content_rv)
//    RecyclerView mContentRv;
    @BindView(R.id.gold_number_tv)
    TextView mGoldNumberTv;
//    @BindView(R.id.wechat_check_iv)
//    ImageView mWechatCheckIv;
//    @BindView(R.id.alipay_check_iv)
//    ImageView mAlipayCheckIv;

    private ChargeRecyclerAdapter mAdapter;

    //支付宝
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    //微信
    private IWXAPI mWxApi;
    //接收关闭activity广播
    private MyFinishBroadcastReceiver mMyBroadcastReceiver;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_charge_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.charge_gold);
        setRightText(R.string.service);

        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        mMyBroadcastReceiver = new MyFinishBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constant.FINISH_CHARGE_PAGE);
        registerReceiver(mMyBroadcastReceiver, filter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
//        mContentRv.setLayoutManager(gridLayoutManager);
//        mAdapter = new ChargeRecyclerAdapter(this);
//        mContentRv.setAdapter(mAdapter);
//
//        mWechatCheckIv.setSelected(true);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                getChargeList();
            }
        }, 20);
        getMyGold();
    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_BALANCE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mGoldNumberTv.setText(String.valueOf(balanceBean.amount));
                    }
                }
            }
        });
    }

    /**
     * 获取充值列表
     */
    private void getChargeList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_end_type", "0");//端类型:0.Android 1.iPhone
        OkHttpUtils.post().url(ChatApi.GET_RECHARGE_DISCOUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<ChargeListBean>>() {
            @Override
            public void onResponse(BaseListResponse<ChargeListBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<ChargeListBean> beans = response.m_object;
                    if (beans != null && beans.size() > 1) {
                        if (beans.size() >= 2) {
                            beans.get(1).isSelected = true;
                        }
                        mAdapter.loadData(beans);
                    }
                }
            }
        });
    }

    @OnClick({R.id.account_detail_tv, R.id.right_text})
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.account_detail_tv: {//账单详情
                Intent intent = new Intent(getApplicationContext(), AccountBalanceActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.right_text: {
                CodeUtil.jumpToQQ(ChargeActivity.this);
                break;
            }


        }
    }

    //-------------------------支付-------------------------

    /**
     * 金币充值
     * 0.支付宝 1.微信
     */
    private void payForGold(int goldId, final int payType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("setMealId", String.valueOf(goldId));
        paramMap.put("payType", String.valueOf(payType));
        OkHttpUtils.post().url(ChatApi.GOLD_STORE_VALUE)
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
     * 支付宝支付
     */
    private void payWithAlipay(final String orderInfo) {//订单信息
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ChargeActivity.this);
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
                case SDK_AUTH_FLAG: {
                    break;
                }
                default:
                    break;
            }
        }
    };

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
                    AppManager.getInstance().setIsWeChatForVip(false);
                }
                LogUtil.i("res : " + res);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
            }
        } else {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyBroadcastReceiver != null) {
            unregisterReceiver(mMyBroadcastReceiver);
        }
    }

    class MyFinishBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

}
