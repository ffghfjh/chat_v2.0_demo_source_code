package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.activity.PhotoActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.CommentMessageBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：评论消息RecyclerView的Adapter
 * 作者：
 * 创建时间：2019/1/17
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CommentMessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<CommentMessageBean> mBeans = new ArrayList<>();

    public CommentMessageRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<CommentMessageBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_comment_message_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CommentMessageBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //昵称
            mHolder.mNickTv.setText(bean.t_nickName);
            //评论内容
            mHolder.mCommentTv.setText(bean.t_comment);
            //评论时间
            //时间
            long time = bean.t_create_time;
            if (time > 0) {
                mHolder.mTimeTv.setText(TimeUtil.getTimeStr(time));
                mHolder.mTimeTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mTimeTv.setVisibility(View.GONE);
            }
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 47);
                int high = DevicesUtil.dp2px(mContext, 47);
                ImageLoadHelper.glideShowCornerImageWithUrl(mContext, headImg, mHolder.mHeadIv,
                        3, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //右边内容 	动态类型: -1.文本动态 0.图片 1.视频动态
            final int type = bean.dynamic_type;
            if (type == -1) {//文本动态
                mHolder.mContentTv.setText(bean.dynamic_com);
                mHolder.mContentTv.setVisibility(View.VISIBLE);
                mHolder.mContentIv.setVisibility(View.GONE);
                mHolder.mVideoIv.setVisibility(View.GONE);
            } else if (type == 0) {//图片
                int width = DevicesUtil.dp2px(mContext, 60);
                int high = DevicesUtil.dp2px(mContext, 60);
                ImageLoadHelper.glideShowImageWithUrl(mContext, bean.dynamic_com,
                        mHolder.mContentIv, width, high);
                mHolder.mContentIv.setVisibility(View.VISIBLE);
                mHolder.mContentTv.setVisibility(View.GONE);
                mHolder.mVideoIv.setVisibility(View.GONE);
            } else if (type == 1) {//视频动态
                int width = DevicesUtil.dp2px(mContext, 60);
                int high = DevicesUtil.dp2px(mContext, 60);
                ImageLoadHelper.glideShowImageWithUrl(mContext, bean.t_cover_img_url,
                        mHolder.mContentIv, width, high);
                mHolder.mContentIv.setVisibility(View.VISIBLE);
                mHolder.mContentTv.setVisibility(View.GONE);
                mHolder.mVideoIv.setVisibility(View.VISIBLE);
            }
            //点击事件
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                }
            });
            //右边信息
            mHolder.mRightFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 0) {//图片
                        Intent intent = new Intent(mContext, PhotoActivity.class);
                        intent.putExtra(Constant.IMAGE_URL, bean.dynamic_com);
                        mContext.startActivity(intent);
                    } else if (type == 1) {//视频
                        Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                        intent.putExtra(Constant.VIDEO_URL, bean.dynamic_com);
                        intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ALBUM);
                        intent.putExtra(Constant.COVER_URL, bean.t_cover_img_url);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNickTv;
        TextView mCommentTv;
        TextView mTimeTv;
        //右边信息
        FrameLayout mRightFl;
        ImageView mContentIv;
        TextView mContentTv;
        ImageView mVideoIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mCommentTv = itemView.findViewById(R.id.comment_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mRightFl = itemView.findViewById(R.id.right_fl);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mVideoIv = itemView.findViewById(R.id.video_iv);
        }
    }

}
