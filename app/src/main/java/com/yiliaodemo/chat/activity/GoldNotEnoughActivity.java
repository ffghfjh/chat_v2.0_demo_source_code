package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
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
import com.yiliaodemo.chat.adapter.GoldItemRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.bean.ChargeListBean;
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

import java.util.ArrayList;
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
 * 功能描述   金币不足页面
 * 作者：
 * 创建时间：2018/8/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GoldNotEnoughActivity extends BaseActivity {

    @BindView(R.id.we_chat_iv)
    ImageView mWeChatIv;
    @BindView(R.id.alipay_iv)
    ImageView mAlipayIv;

    //支付
    private IWXAPI mWxApi;
    //支付宝
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private GoldItemRecyclerAdapter mVipMoneyRecyclerAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_gold_not_anouth_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        mWxApi = WXAPIFactory.createWXAPI(mContext, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        needHeader(false);
        initStart();
        getChargeList();
    }

    /**
     * 初始化
     */
    private void initStart() {
        mWeChatIv.setSelected(true);
        //设置RecyclerView
        RecyclerView content_rv = findViewById(R.id.content_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        content_rv.setLayoutManager(gridLayoutManager);
        mVipMoneyRecyclerAdapter = new GoldItemRecyclerAdapter(mContext);
        content_rv.setAdapter(mVipMoneyRecyclerAdapter);
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
                    if (beans != null && beans.size() > 0) {
                        dealBean(beans);
                    }
                }
            }
        });
    }

    /**
     * 处理获取到的bean
     */
    private void dealBean(List<ChargeListBean> beans) {
        //取第二 和 第四
        List<ChargeListBean> newBeans = new ArrayList<>();
        if (beans.size() > 4) {
            ChargeListBean bean = beans.get(1);
            bean.isSelected = true;
            newBeans.add(bean);
            newBeans.add(beans.get(3));
        }
        mVipMoneyRecyclerAdapter.loadData(newBeans);
    }

    @OnClick({R.id.we_chat_rl, R.id.alipay_rl, R.id.charge_tv, R.id.top_v, R.id.upgrade_tv,
            R.id.more_tv, R.id.get_gold_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.we_chat_rl: {//微信
                if (mWeChatIv.isSelected()) {
                    return;
                }
                mWeChatIv.setSelected(true);
                mAlipayIv.setSelected(false);
                break;
            }
            case R.id.alipay_rl: {//支付宝
                if (mAlipayIv.isSelected()) {
                    return;
                }
                mAlipayIv.setSelected(true);
                mWeChatIv.setSelected(false);
                break;
            }
            case R.id.charge_tv: {
                if (!mWeChatIv.isSelected() && !mAlipayIv.isSelected()) {
                    ToastUtil.showToast(mContext, R.string.please_choose_pay_way);
                    return;
                }
                ChargeListBean mSelectVipBean = mVipMoneyRecyclerAdapter.getSelectBean();
                if (mSelectVipBean == null) {
                    ToastUtil.showToast(mContext, R.string.please_choose_money);
                    return;
                }
                int payType;
                if (mWeChatIv.isSelected()) {
                    payType = 1;
                } else {
                    payType = 0;
                }
                payForGold(mSelectVipBean.t_id, payType);
                break;
            }
            case R.id.top_v: {
                finish();
                break;
            }
            case R.id.upgrade_tv: {//立即升级vip
                Intent intent = new Intent(getApplicationContext(), VipCenterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.more_tv: {//更多
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.get_gold_tv: {//邀请赚钱
                Intent intent = new Intent(getApplicationContext(), InviteEarnActivity.class);
                startActivity(intent);
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
                LogUtil.i("金币支付: " + response);
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
                    AppManager.getInstance().setIsWeChatForVip(false);
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
                PayTask alipay = new PayTask(GoldNotEnoughActivity.this);
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

}
