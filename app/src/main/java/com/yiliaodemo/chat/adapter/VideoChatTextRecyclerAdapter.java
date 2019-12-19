package com.yiliaodemo.chat.adapter;


import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：视频聊天页面文字消息RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoChatTextRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<Message> mBeans = new ArrayList<>();
    private String mHintMessage;

    public VideoChatTextRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void addData(Message message) {
        mBeans.add(message);
        notifyItemChanged(mBeans.size() - 1);
    }

    public void addHintData(String hintMessage) {
        mBeans.add(null);
        mHintMessage = hintMessage;
        notifyItemChanged(mBeans.size() - 1);
    }

    public int getPosition() {
        return mBeans.size() - 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video_chat_text_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            TextContent textContent = (TextContent) bean.getContent();
            String text = textContent.getText();
            //如果是自己 红色 加个 我:
            if (bean.getDirect() == MessageDirect.send) {
                mHolder.mContentTv.setTextColor(mContext.getResources().getColor(R.color.white));
                String content = mContext.getString(R.string.me_one) + text;
                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.yellow_f9fb44)),
                        0, 3, SPAN_EXCLUSIVE_EXCLUSIVE);
                mHolder.mContentTv.setText(builder);
            } else {
                mHolder.mContentTv.setTextColor(mContext.getResources().getColor(R.color.green_00ffd8));
                mHolder.mContentTv.setText(text);
            }
        }else {
            mHolder.mContentTv.setTextColor(mContext.getResources().getColor(R.color.yellow_f9fb44));
            mHolder.mContentTv.setText(mHintMessage);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mContentTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }
    }

}
