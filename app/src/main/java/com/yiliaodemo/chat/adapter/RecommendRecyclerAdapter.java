package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.content.Intent;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.bean.GirlListBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：女神页面RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RecommendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<GirlListBean> mBeans = new ArrayList<>();
    private final int BANNER = 0;
    private int mType;

    public RecommendRecyclerAdapter(Activity context, int type) {
        mContext = context;
        mType = type;
    }

    public void loadData(List<GirlListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return BANNER;
        } else {
            return 1;//主播
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == BANNER) {//广告
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recommend_adver_layout, parent, false);
            return new BannerHolder(itemView);
        } else {//主播
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recommend_recycler_layout, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GirlListBean bean = mBeans.get(position);
        //主播
        if (holder instanceof MyViewHolder) {
            MyViewHolder mHolder = (MyViewHolder) holder;
            //绑定数据
            if (bean != null) {
                mHolder.mNameTv.setText(bean.t_nickName);
                mHolder.mStarRb.setRating(bean.t_score);
                int age = bean.t_age;
                if (age > 0) {
                    String ageStr = String.valueOf(bean.t_age) + mContext.getResources().getString(R.string.age);
                    mHolder.mAgeTv.setText(ageStr);
                }
                if (!TextUtils.isEmpty(bean.t_city)) {
                    mHolder.mCityTv.setText(bean.t_city);
                    mHolder.mCityTv.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mCityTv.setVisibility(View.GONE);
                }

                //处理状态 主播状态(0.空闲1.忙碌2.离线)
                int state = bean.t_state;
                if (state == 0) {
                    mHolder.mStatusFreeTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusBusyTv.setVisibility(View.GONE);
                    mHolder.mStatusOfflineTv.setVisibility(View.GONE);
                } else if (state == 1) {
                    mHolder.mStatusBusyTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusFreeTv.setVisibility(View.GONE);
                    mHolder.mStatusOfflineTv.setVisibility(View.GONE);
                } else if (state == 2) {
                    mHolder.mStatusOfflineTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusBusyTv.setVisibility(View.GONE);
                    mHolder.mStatusFreeTv.setVisibility(View.GONE);
                } else {
                    mHolder.mStatusFreeTv.setVisibility(View.GONE);
                    mHolder.mStatusBusyTv.setVisibility(View.GONE);
                    mHolder.mStatusOfflineTv.setVisibility(View.GONE);
                }

                //显示封面图
                String coverImg = bean.t_cover_img;
                if (!TextUtils.isEmpty(coverImg)) {
                    //计算 图片resize的大小
                    int overWidth = DevicesUtil.getScreenW(mContext);
                    int overHeight = DevicesUtil.dp2px(mContext, 360);
                    if (overWidth > 800) {
                        overWidth = (int) (overWidth * 0.7);
                        overHeight = (int) (overHeight * 0.7);
                    }
                    ImageLoadHelper.glideShowImageWithUrl(mContext, coverImg, mHolder.mHeadIv,
                            overWidth, overHeight);
                }
                //处理头像
                String handImg = bean.t_handImg;
                if (!TextUtils.isEmpty(handImg)) {
                    //计算头像resize
                    int smallOverWidth = DevicesUtil.dp2px(mContext, 50);
                    int smallOverHeight = DevicesUtil.dp2px(mContext, 50);
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHolder.mSmallHeadIv,
                            smallOverWidth, smallOverHeight);
                }
                //金币
                int gold = bean.t_video_gold;
                if (gold > 0) {
                    String content = gold + mContext.getResources().getString(R.string.price);
                    mHolder.mPriceTv.setText(content);
                    mHolder.mPriceTv.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mPriceTv.setVisibility(View.GONE);
                }

                //点击跳转
                mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //该主播是否存在免费视频0.不存在1.存在
                        int freeVideo = bean.t_is_public;
                        if (freeVideo == 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                            intent.putExtra(Constant.FROM_WHERE, Constant.FROM_GIRL);
                            intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                            mContext.startActivity(intent);
                        }
                    }
                });
                //跳转到主播信息
                mHolder.mSmallHeadIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                });
                mHolder.mInfoLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if (holder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) holder;
            if (mType == 0) {//0:推荐  1:试看  2:新人
                ImageLoadHelper.glideShowImageWithResource(mContext, R.drawable.recommend_recommend, bannerHolder.mContentIv);
            } else if (mType == 1) {
                ImageLoadHelper.glideShowImageWithResource(mContext, R.drawable.recommend_free, bannerHolder.mContentIv);
            } else {
                ImageLoadHelper.glideShowImageWithResource(mContext, R.drawable.recommend_new, bannerHolder.mContentIv);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNameTv;
        RatingBar mStarRb;
        TextView mAgeTv;
        TextView mCityTv;
        TextView mPriceTv;
        ImageView mSmallHeadIv;
        TextView mStatusFreeTv;
        TextView mStatusBusyTv;
        TextView mStatusOfflineTv;
        View mContentFl;
        View mInfoLl;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mStarRb = itemView.findViewById(R.id.star_rb);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mCityTv = itemView.findViewById(R.id.city_tv);
            mPriceTv = itemView.findViewById(R.id.price_tv);
            mSmallHeadIv = itemView.findViewById(R.id.small_head_iv);
            mStatusFreeTv = itemView.findViewById(R.id.status_free_tv);
            mStatusBusyTv = itemView.findViewById(R.id.status_busy_tv);
            mStatusOfflineTv = itemView.findViewById(R.id.status_offline_tv);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mInfoLl = itemView.findViewById(R.id.info_ll);
        }
    }

    class BannerHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;

        BannerHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
        }
    }


}
