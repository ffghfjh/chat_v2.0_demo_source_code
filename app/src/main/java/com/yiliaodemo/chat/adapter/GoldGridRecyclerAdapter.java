package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.bean.GoldBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：金币8个item的recyclerView的adapter
 * 作者：
 * 创建时间：2018/10/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GoldGridRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<GoldBean> mBeans = new ArrayList<>();
    private GoldBean mSelectedBean;

    public GoldGridRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<GoldBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public GoldBean getSelectedBean() {
        return mSelectedBean;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gold_grid_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GoldBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //图片
            int resourceId = bean.resourceId;
            if (resourceId > 0) {
                mHolder.mGoldIv.setImageResource(resourceId);
            }
            //是否选中
            if (bean.isSelected) {
                mHolder.mRectV.setVisibility(View.VISIBLE);
                mSelectedBean = bean;
            } else {
                mHolder.mRectV.setVisibility(View.GONE);
            }
            //点击事件
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bean.isSelected) {
                        for (int i = 0; i < mBeans.size(); i++) {
                            mBeans.get(i).isSelected = i == mPosition;
                        }
                        notifyDataSetChanged();
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

        View mContentRl;
        ImageView mGoldIv;
        View mRectV;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mGoldIv = itemView.findViewById(R.id.gold_iv);
            mRectV = itemView.findViewById(R.id.rect_v);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
