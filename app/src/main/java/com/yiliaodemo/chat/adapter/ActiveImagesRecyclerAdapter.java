package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.bean.ActiveFileBean;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：粉丝RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveImagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<ActiveFileBean> mBeans = new ArrayList<>();

    ActiveImagesRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<ActiveFileBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_active_image_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActiveFileBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        final int mPosition = position;
        if (bean != null) {
            int overWidth = DevicesUtil.dp2px(mContext, 83);
            int overHeight = DevicesUtil.dp2px(mContext, 83);
            //私密
            if (bean.t_gold > 0 && bean.isConsume == 0) {
                mHolder.mLockIv.setVisibility(View.VISIBLE);
                ImageLoadHelper.glideShowImageWithFade(mContext, bean.t_file_url, mHolder.mContentIv,
                        overWidth, overHeight);
            } else {
                mHolder.mLockIv.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(bean.t_file_url)) {
                    ImageLoadHelper.glideShowImageWithUrl(mContext, bean.t_file_url, mHolder.mContentIv,
                            overWidth, overHeight);
                }
            }
            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnImageItemClickListener != null) {
                        mOnImageItemClickListener.onImageItemClick(mPosition, bean);
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

        ImageView mContentIv;
        ImageView mLockIv;
        FrameLayout mContentFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mLockIv = itemView.findViewById(R.id.lock_iv);
            mContentFl = itemView.findViewById(R.id.content_fl);
        }
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(int position, ActiveFileBean bean);
    }

    private OnImageItemClickListener mOnImageItemClickListener;

    void setOnImageItemClickListener(OnImageItemClickListener onImageItemClickListener) {
        mOnImageItemClickListener = onImageItemClickListener;
    }

}
