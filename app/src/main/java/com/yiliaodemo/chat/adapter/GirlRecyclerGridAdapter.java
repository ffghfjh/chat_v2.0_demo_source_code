package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.BannerBean;
import com.yiliaodemo.chat.bean.GirlListBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：女神页面RecyclerView的Grid形式的Adapter
 * 作者：
 * 创建时间：2018/10/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GirlRecyclerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<GirlListBean> mBeans = new ArrayList<>();
    private List<GirlListBean> mRecommendBeans = new ArrayList<>();
    private List<BannerBean> mBannerBeans = new ArrayList<>();
    private final int TOP = 0;
    private TopHolder mTopHolder;

    public GirlRecyclerGridAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<GirlListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public void loadBannerData(List<BannerBean> bannerBeans) {
        mBannerBeans = bannerBeans;
        notifyDataSetChanged();
    }

    public void loadRecommendData(List<GirlListBean> beans) {
        mRecommendBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TOP;
        } else {
            return 2;//主播
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TOP) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_girl_recycler_top_layout,
                    parent, false);
            mTopHolder = new TopHolder(itemView);
            return mTopHolder;
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_girl_recycler_grid_layout,
                    parent, false);
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
                //昵称
                mHolder.mNameTv.setText(bean.t_nickName);
                //处理状态 主播状态(0.空闲1.忙碌2.离线)
                int state = bean.t_state;
                if (state == 0) {//在线
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_free_indicator);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusTv.setCompoundDrawables(drawable, null, null, null);
                    mHolder.mStatusTv.setText(mContext.getString(R.string.free));
                    mHolder.mStatusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                } else if (state == 1) {//忙碌
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_busy_indicator);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusTv.setCompoundDrawables(drawable, null, null, null);
                    mHolder.mStatusTv.setText(mContext.getString(R.string.busy));
                    mHolder.mStatusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                } else if (state == 2) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_offline_indicator);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusTv.setCompoundDrawables(drawable, null, null, null);
                    mHolder.mStatusTv.setText(mContext.getString(R.string.offline));
                    mHolder.mStatusTv.setTextColor(mContext.getResources().getColor(R.color.gray_bcbcbc));
                }
                //年龄
                if (bean.t_age > 0) {
                    mHolder.mAgeTv.setVisibility(View.VISIBLE);
                    mHolder.mAgeTv.setText(String.valueOf(bean.t_age));
                } else {
                    mHolder.mAgeTv.setVisibility(View.GONE);
                }
                //签名
                String sign = bean.t_autograph;
                if (!TextUtils.isEmpty(sign)) {
                    mHolder.mSignTv.setText(sign);
                } else {
                    mHolder.mSignTv.setText(mContext.getResources().getString(R.string.lazy));
                }
                //职业
                if (!TextUtils.isEmpty(bean.t_vocation)) {
                    mHolder.mJobTv.setVisibility(View.VISIBLE);
                    mHolder.mJobTv.setText(bean.t_vocation);
                } else {
                    mHolder.mJobTv.setVisibility(View.GONE);
                }
                //显示封面图
                String coverImg = bean.t_cover_img;
                if (!TextUtils.isEmpty(coverImg)) {
                    ImageLoadHelper.glideShowCornerImageWithUrl(mContext, coverImg, mHolder.mHeadIv);
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
                mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
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
                mHolder.mInfoLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if (holder instanceof TopHolder) {
            TopHolder bannerHolder = (TopHolder) holder;
            if (mRecommendBeans != null) {
                bannerHolder.loadRecommendData(mRecommendBeans);
            }
            if (mBannerBeans != null && mBannerBeans.size() > 0) {
                bannerHolder.loadBannerData(mBannerBeans);
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
        TextView mPriceTv;
        TextView mStatusTv;
        TextView mSignTv;
        TextView mAgeTv;
        TextView mJobTv;
        View mContentLl;
        View mInfoLl;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mPriceTv = itemView.findViewById(R.id.price_tv);
            mStatusTv = itemView.findViewById(R.id.status_tv);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mInfoLl = itemView.findViewById(R.id.info_ll);
            mSignTv = itemView.findViewById(R.id.sign_tv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mJobTv = itemView.findViewById(R.id.job_tv);
        }
    }

    class TopHolder extends RecyclerView.ViewHolder {

        GirlTopRecyclerAdapter mGirlTopRecyclerAdapter;
        GirlTopBannerRecyclerAdapter mBannerRecyclerAdapter;
        RecyclerView mRecommendRv;
        RecyclerView mBannerRv;

        TopHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        /**
         * 初始化
         */
        private void init(View itemView) {
            mRecommendRv = itemView.findViewById(R.id.recommend_rv);
            mBannerRv = itemView.findViewById(R.id.banner_rv);
            mRecommendRv.setNestedScrollingEnabled(false);
            GridLayoutManager manager = new GridLayoutManager(mContext, 2);
            mRecommendRv.setLayoutManager(manager);
            mGirlTopRecyclerAdapter = new GirlTopRecyclerAdapter(mContext);
            mRecommendRv.setAdapter(mGirlTopRecyclerAdapter);

            List<GirlListBean> beans = new ArrayList<>();
            beans.add(0, null);
            mGirlTopRecyclerAdapter.loadData(beans);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            mBannerRv.setLayoutManager(linearLayoutManager);
            mBannerRecyclerAdapter = new GirlTopBannerRecyclerAdapter(mContext);
            mBannerRv.setAdapter(mBannerRecyclerAdapter);
        }

        void loadRecommendData(List<GirlListBean> beans) {
            mGirlTopRecyclerAdapter.loadData(beans);
        }

        void loadBannerData(List<BannerBean> beans) {
            mBannerRecyclerAdapter.loadBannerData(beans);
        }

        void resumeChange() {
            if (mGirlTopRecyclerAdapter != null) {
                mGirlTopRecyclerAdapter.resumeChange();
            }
        }

        void pauseChange() {
            if (mGirlTopRecyclerAdapter != null) {
                mGirlTopRecyclerAdapter.pauseChange();
            }
        }
    }

   public void resumeChange() {
        if (mTopHolder != null) {
            mTopHolder.resumeChange();
        }
    }

    public void pauseChange() {
        if (mTopHolder != null) {
            mTopHolder.pauseChange();
        }
    }


}
