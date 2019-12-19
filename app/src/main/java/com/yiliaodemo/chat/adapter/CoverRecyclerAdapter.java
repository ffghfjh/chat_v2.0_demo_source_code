package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：编辑个人资料封面RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CoverRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<String> mBeans = new ArrayList<>();

    public CoverRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
        mBeans = beans;
        notifyDataSetChanged();
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onDataChange();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_edit_info_cover_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        String filePath = mBeans.get(position);
        int len = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 40)) / 4;
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                Uri pictureUri = FileUtil.getUriAdjust24(mContext, file);
                ImageLoadHelper.glideShowCornerImageWithUri(mContext, pictureUri, mHolder.mCoverIv,
                        3, len, len);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final int mPosition = position;
        if (position == 0) {
            mHolder.mSetTv.setVisibility(View.VISIBLE);
        } else {
            mHolder.mSetTv.setVisibility(View.GONE);
        }
        mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentFl;
        ImageView mCoverIv;
        TextView mSetTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mCoverIv = itemView.findViewById(R.id.cover_iv);
            mSetTv = itemView.findViewById(R.id.set_tv);
            int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 40)) / 4;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
            params.leftMargin = DevicesUtil.dp2px(mContext, 5);
            params.rightMargin = DevicesUtil.dp2px(mContext, 5);
            mCoverIv.setLayoutParams(params);
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.leftMargin = DevicesUtil.dp2px(mContext, 5);
            params1.rightMargin = DevicesUtil.dp2px(mContext, 5);
            params1.gravity = Gravity.BOTTOM;
            mSetTv.setLayoutParams(params1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnDataChangeListener {
        void onDataChange();
    }

    private OnDataChangeListener mOnDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }


}
