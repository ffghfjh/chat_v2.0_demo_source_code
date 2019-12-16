package com.yiliaodemo.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.MainActivity;
import com.yiliaodemo.chat.adapter.MessageRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.bean.CustomMessageBean;
import com.yiliaodemo.chat.bean.MessageBean;
import com.yiliaodemo.chat.bean.UnReadBean;
import com.yiliaodemo.chat.bean.UnReadMessageBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.util.TimeUtil;
import com.yiliaodemo.chat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：消息页面fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MessageFragment extends BaseFragment implements View.OnClickListener {

    public MessageFragment() {

    }

    private List<MessageBean> mFocusBeans = new ArrayList<>();
    private MessageRecyclerAdapter mAdapter;
    public boolean mHaveFirstVisible = false;

    @Override
    protected int initLayout() {
        return R.layout.fragment_message_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        TextView clear_tv = view.findViewById(R.id.clear_tv);
        clear_tv.setOnClickListener(this);
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        final SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getConversation();
                refreshLayout.finishRefresh(700);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(linearLayoutManager);
        mAdapter = new MessageRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {
        mHaveFirstVisible = true;
        getConversation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible && mIsVisibleToUser) {
            getConversation();
        }
    }

    public void getConversation() {
        mFocusBeans.clear();
        //先加一条,用作显示系统消息
        mFocusBeans.add(0, null);
        mFocusBeans.add(1, null);
        mFocusBeans.add(2, null);
        //待获取用户资料的好友列表
        List<Conversation> list = JMessageClient.getConversationList();
        if (list != null && list.size() > 0) {
            for (final Conversation conversation : list) {
                if (conversation.getType() != ConversationType.single) {
                    continue;
                }
                UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                if (userInfo == null || TextUtils.isEmpty(userInfo.getUserName())) {
                    continue;
                }
                //设置MessageBean
                MessageBean messageBean = new MessageBean();
                messageBean.t_id = userInfo.getUserName();
                messageBean.nickName = userInfo.getNickname();
                messageBean.userInfo = userInfo;
                messageBean.unReadCount = conversation.getUnReadMsgCnt();
                //获取最后一条Text消息
                Message latestMessage = conversation.getLatestMessage();
                if (latestMessage != null) {
                    switch (latestMessage.getContentType()) {
                        case text: {//文本
                            TextContent textContent = (TextContent) latestMessage.getContent();
                            messageBean.t_message_content = textContent.getText();
                            messageBean.isText = true;
                            break;
                        }
                        case custom: {//自定义
                            CustomContent customContent = (CustomContent) latestMessage.getContent();
                            messageBean.t_message_content = "[ " + parseCustomMessage(customContent) + " ]";
                            messageBean.isText = false;
                            break;
                        }
                    }
                    messageBean.t_create_time = TimeUtil.getTimeStr(latestMessage.getCreateTime() / 1000);
                }
                mFocusBeans.add(messageBean);
            }
            mAdapter.loadData(mFocusBeans);
        } else {
            mAdapter.loadData(mFocusBeans);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_tv: {//清空
                clearAllMessage();
                break;
            }
        }
    }

    /**
     * 清空
     */
    private void clearAllMessage() {
        try {
            mContext.showLoadingDialog();
            List<Conversation> list = JMessageClient.getConversationList();
            if (list != null && list.size() > 0) {
                for (Conversation conversation : list) {
                    if (conversation.getType() != ConversationType.single) {
                        continue;
                    }
                    //删除
                    conversation.resetUnreadCount();
                    UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                    JMessageClient.deleteSingleConversation(userInfo.getUserName(), Constant.JPUSH_APP_KEY);
                }
            }
            //清除完成
            mContext.dismissLoadingDialog();
            //清除红点
            ((MainActivity) mContext).resetRedPot();
            getConversation();
        } catch (Exception e) {
            e.printStackTrace();
            mContext.dismissLoadingDialog();
            ToastUtil.showToast(getContext(), R.string.system_error);
        }
    }

    /**
     * 解析自定义消息
     */
    private String parseCustomMessage(CustomContent customElem) {
        try {
            String json = customElem.getStringValue("custom");
            CustomMessageBean bean = JSON.parseObject(json, CustomMessageBean.class);
            if (bean != null) {
                if (bean.type.equals("1")) {//礼物
                    return bean.gift_name;
                } else if (bean.type.equals("0")) {//金币
                    return mContext.getResources().getString(R.string.gold);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加载系统消息数据
     */
    public void loadSystemMessage(UnReadBean<UnReadMessageBean> bean) {
        if (mAdapter != null) {
            mAdapter.loadSystemMessage(bean);
        }
    }

}
