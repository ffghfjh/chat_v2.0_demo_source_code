package com.yiliaodemo.chat.wxapi;

import android.app.Activity;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ApplyVerifyOneActivity;
import com.yiliaodemo.chat.activity.ChooseGenderActivity;
import com.yiliaodemo.chat.activity.MainActivity;
import com.yiliaodemo.chat.base.AppManager;
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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：微信页面
 * 作者：
 * 创建时间：2018/6/28
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constant.WE_CHAT_APPID, true);
        //将应用的app id注册到微信
        api.registerApp(Constant.WE_CHAT_APPID);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        api.handleIntent(getIntent(), this);
        LogUtil.i("wxonCreate: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
        LogUtil.i("wxonActivityResult: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.i("baseReq:" + JSON.toJSONString(baseReq));
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtil.i("baseResp:" + JSON.toJSONString(baseResp));
        LogUtil.i("baseResp:" + baseResp.errStr + "," + baseResp.openId + "," + baseResp.transaction + "," + baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信登录
                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    if (!TextUtils.isEmpty(resp.code)) {
                        getAccessToken(resp.code);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//微信分享
                    LogUtil.i("微信分享成功");
                    if (AppManager.getInstance().getIsMainPageShareQun()) {
                        //继续完成分享
                        addShareTime();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.share_success);
                        finish();
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信登录
                    ToastUtil.showToast(getApplicationContext(), R.string.login_cancel);
                    finish();
                } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//微信分享
                    ToastUtil.showToast(getApplicationContext(), R.string.share_cancel);
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 获取Access_token
     */
    private void getAccessToken(String code) {
        OkHttpUtils.get().url(ChatApi.WX_GET_ACCESS_TOKEN)
                .addParams("appid", Constant.WE_CHAT_APPID)
                .addParams("secret", Constant.WE_CHAT_SECRET)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = JSON.parseObject(response);
                    String token = jsonObject.getString("access_token");
                    String openId = jsonObject.getString("openid");
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(openId)) {
                        getUserInfo(token, openId);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }
        });
    }

    /**
     * 获取UserInfo
     */
    private void getUserInfo(String token, String openId) {
        OkHttpUtils.get().url(ChatApi.WX_GET_USER_INFO)
                .addParams("access_token", token)
                .addParams("openid", openId)//openid:授权用户唯一标识
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = JSON.parseObject(response);
                    if (object != null) {
                        if (!AppManager.getInstance().getIsWeChatBindAccount()) {//微信登录
                            loginWx(object);
                        } else {//绑定微信号 提现
                            LogUtil.i("绑定微信号 提现");
                            //发送广播传递信息回去
                            String nickName = object.getString("nickname");
                            String handImg = object.getString("headimgurl");
                            String openId = object.getString("openid");
                            Intent intent = new Intent(Constant.WECHAT_WITHDRAW_ACCOUNT);
                            intent.putExtra(Constant.WECHAT_NICK_INFO, nickName);
                            intent.putExtra(Constant.WECHAT_HEAD_URL, handImg);
                            intent.putExtra(Constant.WECHAT_OPEN_ID, openId);
                            sendBroadcast(intent);
                            finish();
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }
        });
    }

    /**
     * 调用自己的api 进行微信登录
     */
    private void loginWx(JSONObject jsonObject) {
        String openId = jsonObject.getString("openid");
        if (TextUtils.isEmpty(openId)) {
            ToastUtil.showToast(getApplicationContext(), R.string.we_chat_fail);
            finish();
            return;
        }
        final String nickName = jsonObject.getString("nickname");
        final String handImg = jsonObject.getString("headimgurl");
        String city = jsonObject.getString("city");
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("openId", TextUtils.isEmpty(openId) ? "" : openId);
        paramMap.put("nickName", TextUtils.isEmpty(nickName) ? "" : nickName);
        paramMap.put("handImg", TextUtils.isEmpty(handImg) ? "" : handImg);
        paramMap.put("city", TextUtils.isEmpty(city) ? "" : city);
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        OkHttpUtils.post().url(ChatApi.WE_CHAT_LOGIN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.nickName = nickName;
                            userInfo.headUrl = handImg;
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            loginSocket(userInfo);
                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
                            if (userInfo.t_sex == 2) {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                intent.putExtra(Constant.NICK_NAME, nickName);
                                intent.putExtra(Constant.MINE_HEAD_URL, handImg);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            //发送广播关闭Login页面
                            Intent intent = new Intent(Constant.FINISH_LOGIN_PAGE);
                            sendBroadcast(intent);
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                            }
                            finish();
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        Intent intent = new Intent(Constant.BEEN_CLOSE_LOGIN_PAGE);
                        intent.putExtra(Constant.BEEN_CLOSE, message);
                        sendBroadcast(intent);
                        finish();
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
                        finish();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }
        });
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

    /**
     * 分享微信群完成后 添加分享次数
     */
    private void addShareTime() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.ADD_SHARE_COUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer m_object = response.m_object;
                    showThreeQunDialog(m_object);
                } else {
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                finish();
            }
        });
    }

    /**
     * 显示分享到3个群dialog
     */
    private void showThreeQunDialog(int count) {
        final Dialog mDialog = new Dialog(WXEntryActivity.this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(WXEntryActivity.this).inflate(R.layout.dialog_share_qun_success_layout, null);
        setThreeQunDialogView(view, mDialog, count);
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
        mDialog.setCancelable(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    private void setThreeQunDialogView(View view, final Dialog mDialog, int count) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        //按钮
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        final int leftTime = 3 - count;
        if (leftTime > 0) {//还剩几次,继续分享
            String content = getResources().getString(R.string.need_one) + leftTime + getResources().getString(R.string.need_two);
            des_tv.setText(content);
            confirm_tv.setText(getResources().getString(R.string.continue_share));
        } else {//分享完成  开始认证
            des_tv.setText(getResources().getString(R.string.back_app));
            confirm_tv.setText(getResources().getString(R.string.back_and_verify));
        }
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (leftTime > 0) {
                    finish();
                } else {
                    Intent broadcastIntent = new Intent(Constant.QUN_SHARE_QUN_CLOSE);
                    sendBroadcast(broadcastIntent);
                    Intent intent = new Intent(getApplicationContext(), ApplyVerifyOneActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 获取UserId
     */
    private String getUserId() {
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

}
