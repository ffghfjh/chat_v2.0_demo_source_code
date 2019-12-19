package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.bean.RankBean;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：榜单RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BeautyRankRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<RankBean> mBeans = new ArrayList<>();
    private boolean mFromCost = false;

    public BeautyRankRecyclerAdapter(Activity context, boolean fromCost) {
        mContext = context;
        mFromCost = fromCost;
    }

    public void loadData(List<RankBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_beauty_rank_recycler_content_layout, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RankBean bean = mBeans.get(position);
        ContentViewHolder mHolder = (ContentViewHolder) holder;
        if (bean != null) {
            //排名
            mHolder.mNumberTv.setText(String.valueOf(position + 4));
            //头像
            if (!TextUtils.isEmpty(bean.t_handImg)) {
                int width = DevicesUtil.dp2px(mContext, 40);
                int high = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, bean.t_handImg,
                        mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //昵称
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNickTv.setText(bean.t_nickName);
            }
            //收益
            if (bean.gold > 0) {
                String content;
                if (mFromCost) {
                    content = mContext.getResources().getString(R.string.cost_des) + bean.gold;
                } else {
                    content = mContext.getResources().getString(R.string.earn_rank) + bean.gold;
                }
                mHolder.mEarnTv.setText(content);
            }
            //点击事件
            mHolder.mPointIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean);
                    }
                }
            });
            //跳转到主播信息页面
            mHolder.mInfoLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnInfoClickListener != null) {
                        mOnInfoClickListener.onInfoClick(bean);
                    }
                }
            });
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnInfoClickListener != null) {
                        mOnInfoClickListener.onInfoClick(bean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        View mInfoLl;
        TextView mNumberTv;
        ImageView mHeadIv;
        TextView mNickTv;
        TextView mEarnTv;
        ImageView mPointIv;

        ContentViewHolder(View itemView) {
            super(itemView);
            mInfoLl = itemView.findViewById(R.id.info_ll);
            mNumberTv = itemView.findViewById(R.id.number_tv);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mEarnTv = itemView.findViewById(R.id.earn_tv);
            mPointIv = itemView.findViewById(R.id.point_iv);
        }
    }

    //收益详情
    public interface OnItemClickListener {
        void onItemClick(RankBean rankBean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    //主播详情
    public interface OnInfoClickListener {
        void onInfoClick(RankBean rankBean);
    }

    private OnInfoClickListener mOnInfoClickListener;

    public void setOnInfoClickListener(OnInfoClickListener onInfoClickListener) {
        mOnInfoClickListener = onInfoClickListener;
    }

}
