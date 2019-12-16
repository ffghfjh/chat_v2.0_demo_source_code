package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.activity.PhotoActivity;
import com.yiliaodemo.chat.activity.dy.VideoPlayerActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.AlbumBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：用户资料相册列表页面adapter
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserAlbumListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<AlbumBean> mBeans = new ArrayList<>();

    public UserAlbumListRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<AlbumBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_user_album_list_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlbumBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //审核状态
            if (bean.t_auditing_type == 0) {//审核中
                mHolder.mStatusTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mStatusTv.setVisibility(View.GONE);
            }
            //金币
            int gold = bean.t_money;
            if (gold > 0) {
                mHolder.mLockIv.setVisibility(View.VISIBLE);
                mHolder.mCoverV.setVisibility(View.VISIBLE);
            } else {
                mHolder.mLockIv.setVisibility(View.GONE);
                mHolder.mCoverV.setVisibility(View.GONE);
            }
            //文件类型
            final int fileType = bean.t_file_type;
            final String t_addres_url = bean.t_addres_url;
            final String t_video_img = bean.t_video_img;
            if (fileType == 0) {//0.图片 1.视频
                mHolder.mPlayIv.setVisibility(View.GONE);
                int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                int height = DevicesUtil.dp2px(mContext, 165);
                if (!TextUtils.isEmpty(t_addres_url)) {
                    ImageLoadHelper.glideShowImageWithUrl(mContext, t_addres_url, mHolder.mImageIv,
                            width, height);
                }
            } else {//视频
                mHolder.mPlayIv.setVisibility(View.VISIBLE);
                int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                int height = DevicesUtil.dp2px(mContext, 165);
                if (!TextUtils.isEmpty(t_video_img)) {
                    ImageLoadHelper.glideShowImageWithUrl(mContext, t_video_img, mHolder.mImageIv,
                            width, height);
                }
            }
            //点击事件
            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fileType == 1) {
//                        Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
//                        intent.putExtra(Constant.VIDEO_URL, t_addres_url);
//                        intent.putExtra(Constant.FILE_ID, bean.t_id);
//                        intent.putExtra(Constant.ACTOR_ID, mContext.getUserId());
//                        intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ALBUM);
//                        intent.putExtra(Constant.COVER_URL, t_video_img);
                        Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                        intent.putExtra("url",t_addres_url);
                        mContext.startActivity(intent);
                    } else {//图片
                        if (!TextUtils.isEmpty(t_addres_url)) {
                            Intent intent = new Intent(mContext, PhotoActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, t_addres_url);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
            mHolder.mContentFl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(bean, v);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentFl;
        ImageView mImageIv;
        ImageView mLockIv;
        ImageView mPlayIv;
        TextView mStatusTv;
        View mCoverV;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mImageIv = itemView.findViewById(R.id.image_iv);
            mLockIv = itemView.findViewById(R.id.lock_iv);
            mPlayIv = itemView.findViewById(R.id.play_iv);
            mStatusTv = itemView.findViewById(R.id.status_tv);
            mCoverV = itemView.findViewById(R.id.cover_v);
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(AlbumBean videoBean, View view);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

}
