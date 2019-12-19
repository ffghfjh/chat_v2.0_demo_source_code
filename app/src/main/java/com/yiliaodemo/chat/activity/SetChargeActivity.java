package com.yiliaodemo.chat.activity;

import android.app.Dialog;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.SetChargeRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.CharSetBean;
import com.yiliaodemo.chat.bean.ChargeBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.layoutmanager.PickerLayoutManager;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：设置收费项目页面
 * 作者：
 * 创建时间：2018/6/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SetChargeActivity extends BaseActivity {

    @BindView(R.id.video_chat_tv)
    TextView mVideoChatTv;
    @BindView(R.id.text_chat_tv)
    TextView mTextChatTv;
    @BindView(R.id.phone_tv)
    TextView mPhoneTv;
    @BindView(R.id.we_chat_tv)
    TextView mWeChatTv;

    private final int VIDEO = 0;//视频
    private final int TEXT = 1;//文字
    private final int PHONE = 2;//电话
    private final int WE_CHAT = 3;//微信
    private String mSelectContent = "";

    private List<CharSetBean> mCharSetBeans = new ArrayList<>();
    private String[] mVideoStrs = new String[]{};
    private String[] mTextStrs = new String[]{};
    private String[] mPhoneStrs = new String[]{};
    private String[] mWeChatStrs = new String[]{};

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_set_charge_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.set_charge);
        getAnchorVideoCost();
        getActorSetCharge();
    }

    /**
     * 获取收费设置
     */
    private void getAnchorVideoCost() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_CHARGE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<CharSetBean>>() {
            @Override
            public void onResponse(BaseListResponse<CharSetBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<CharSetBean> charSetBeans = response.m_object;
                    if (charSetBeans != null && charSetBeans.size() > 0) {
                        mCharSetBeans = charSetBeans;
                        //处理bean
                        for (CharSetBean charSetBean : mCharSetBeans) {
                            if (charSetBean.t_project_type == 5) {//5.视频聊天
                                mVideoStrs = charSetBean.t_extract_ratio.split(",");
                            } else if (charSetBean.t_project_type == 6) {//6.文字聊天
                                mTextStrs = charSetBean.t_extract_ratio.split(",");
                            } else if (charSetBean.t_project_type == 7) {//7.查看手机号
                                mPhoneStrs = charSetBean.t_extract_ratio.split(",");
                            } else if (charSetBean.t_project_type == 8) {//8.查看微信号
                                mWeChatStrs = charSetBean.t_extract_ratio.split(",");
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取主播收费设置
     */
    private void getActorSetCharge() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_CHARGE_SETUP)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChargeBean>>() {
            @Override
            public void onResponse(BaseResponse<ChargeBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ChargeBean bean = response.m_object;
                    if (bean != null) {
                        String video = bean.t_video_gold + getResources().getString(R.string.gold);
                        mVideoChatTv.setText(video);
                        String text = bean.t_text_gold + getResources().getString(R.string.gold);
                        mTextChatTv.setText(text);
                        int phoneGold = bean.t_phone_gold;
                        if (phoneGold > 0) {
                            String phone = bean.t_phone_gold + getResources().getString(R.string.gold);
                            mPhoneTv.setText(phone);
                        } else {
                            mPhoneTv.setText(getResources().getString(R.string.phone_private));
                        }
                        String weChat = bean.t_weixin_gold + getResources().getString(R.string.gold);
                        mWeChatTv.setText(weChat);
                    } else {
                        String video = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                        mVideoChatTv.setText(video);
                        String text = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                        mTextChatTv.setText(text);
                        String phone = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                        mPhoneTv.setText(phone);
                        String weChat = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                        mWeChatTv.setText(weChat);
                    }
                }
            }
        });
    }

    /**
     * 修改主播收费设置
     */
    private void modifyChargeSet() {
        //视频
        String video = "";
        String videoContent = mVideoChatTv.getText().toString().trim();
        String[] videoEnum = videoContent.split(getResources().getString(R.string.gold));
        if (videoEnum.length > 0) {
            video = videoEnum[0];
        }
        //文字
        String text = "";
        String textContent = mTextChatTv.getText().toString().trim();
        String[] textEnum = textContent.split(getResources().getString(R.string.gold));
        if (textEnum.length > 0) {
            text = textEnum[0];
        }
        //电话
        String phone = "";
        String phoneContent = mPhoneTv.getText().toString().trim();
        if (phoneContent.contains(getResources().getString(R.string.phone_private))) {
            phone = "0";
        } else {
            String[] phoneEnum = phoneContent.split(getResources().getString(R.string.gold));
            if (phoneEnum.length > 0) {
                phone = phoneEnum[0];
            }
        }

        //微信
        String weChat = "";
        String weChatContent = mWeChatTv.getText().toString().trim();
        String[] weChatEnum = weChatContent.split(getResources().getString(R.string.gold));
        if (weChatEnum.length > 0) {
            weChat = weChatEnum[0];
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_video_gold", video);
        paramMap.put("t_text_gold", text);
        paramMap.put("t_phone_gold", phone);
        paramMap.put("t_weixin_gold", weChat);
        OkHttpUtils.post().url(ChatApi.UPDATE_CHARGE_SET)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                            finish();
                        }
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.charge_can_not);
                    }
                }
            }
        });
    }

    @OnClick({R.id.video_chat_rl, R.id.submit_tv, R.id.text_rl, R.id.phone_rl, R.id.we_chat_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_chat_rl: {//视频聊天
                if (mVideoStrs != null && mVideoStrs.length > 0) {
                    showChargeOptionDialog(VIDEO);
                }
                break;
            }
            case R.id.text_rl: {
                if (mTextStrs != null && mTextStrs.length > 0) {
                    showChargeOptionDialog(TEXT);
                }
                break;
            }
            case R.id.phone_rl: {
                if (mPhoneStrs != null && mPhoneStrs.length > 0) {
                    showChargeOptionDialog(PHONE);
                }
                break;
            }
            case R.id.we_chat_rl: {
                if (mWeChatStrs != null && mWeChatStrs.length > 0) {
                    showChargeOptionDialog(WE_CHAT);
                }
                break;
            }
            case R.id.submit_tv: {
                modifyChargeSet();
                break;
            }
        }
    }

    /**
     * 显示收费标准dialog
     */
    private void showChargeOptionDialog(int position) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog, position);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置 dialog view
     */
    private void setDialogView(View view, final Dialog mDialog, final int passPosition) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        TextView title_tv = view.findViewById(R.id.title_tv);

        final List<String> beans = new ArrayList<>();
        switch (passPosition) {
            case VIDEO: {
                String content = getResources().getString(R.string.video_chat) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mVideoStrs));
                break;
            }
            case TEXT: {
                String content = getResources().getString(R.string.text_private_chat) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mTextStrs));
                break;
            }
            case PHONE: {
                String content = getResources().getString(R.string.see_phone_gold) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.add(0, getResources().getString(R.string.phone_private));
                beans.addAll(Arrays.asList(mPhoneStrs));
                break;
            }
            case WE_CHAT: {
                String content = getResources().getString(R.string.see_we_chat_gold) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mWeChatStrs));
                break;
            }
        }

        SetChargeRecyclerAdapter adapter = new SetChargeRecyclerAdapter(this);
        final RecyclerView content_rv = view.findViewById(R.id.content_rv);
        final PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(getApplicationContext(),
                content_rv, PickerLayoutManager.VERTICAL, false, 5, 0.4f, true);
        content_rv.setLayoutManager(pickerLayoutManager);
        content_rv.setAdapter(adapter);
        adapter.loadData(beans);
        pickerLayoutManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                LogUtil.i("位置: " + position);
                if (position < beans.size()) {
                    mSelectContent = beans.get(position);
                }
            }
        });

        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (passPosition) {
                    case VIDEO: {
                        if (TextUtils.isEmpty(mSelectContent)) {
                            mSelectContent = mVideoStrs[0];
                        }
                        String content = mSelectContent + getResources().getString(R.string.gold);
                        mVideoChatTv.setText(content);
                        mSelectContent = "";
                        break;
                    }
                    case TEXT: {
                        if (TextUtils.isEmpty(mSelectContent)) {
                            mSelectContent = mTextStrs[0];
                        }
                        String content = mSelectContent + getResources().getString(R.string.gold);
                        mTextChatTv.setText(content);
                        mSelectContent = "";
                        break;
                    }
                    case PHONE: {
                        if (TextUtils.isEmpty(mSelectContent)
                                || mSelectContent.equals(getResources().getString(R.string.phone_private))) {
                            mSelectContent = getResources().getString(R.string.phone_private);
                            mPhoneTv.setText(mSelectContent);
                        } else {
                            String content = mSelectContent + getResources().getString(R.string.gold);
                            mPhoneTv.setText(content);
                        }
                        mSelectContent = "";
                        break;
                    }
                    case WE_CHAT: {
                        if (TextUtils.isEmpty(mSelectContent)) {
                            mSelectContent = mWeChatStrs[0];
                        }
                        String content = mSelectContent + getResources().getString(R.string.gold);
                        mWeChatTv.setText(content);
                        mSelectContent = "";
                        break;
                    }
                }
                mDialog.dismiss();
            }
        });

    }


}
