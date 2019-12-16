package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.activity.QuickVideoChatActivity;
import com.yiliaodemo.chat.activity.UserViewQuickActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.GirlListBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：推荐页面上方推荐RecyclerView的Grid形式的Adapter
 * 作者：
 * 创建时间：2018/10/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GirlTopRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<GirlListBean> mRecommendBeans = new ArrayList<>();
    private final int CHANGE = 0;
    private final int CHANGE_NEXT = 0x13;
    private ChangeHolder mChangeHolder;
    private MyHandler mChangeHandler = new MyHandler(GirlTopRecyclerAdapter.this);

    GirlTopRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<GirlListBean> beans) {
        mRecommendBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return CHANGE;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == CHANGE) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_quick_flash_layout,
                    parent, false);
            mChangeHolder = new ChangeHolder(itemView);
            return mChangeHolder;
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_girl_recycler_grid_layout,
                    parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GirlListBean bean = mRecommendBeans.get(position);
        if (holder instanceof ChangeHolder) {
            ChangeHolder mHolder = (ChangeHolder) holder;
            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext != null) {
                        //1 主播 0 用户
                        if (mContext.getUserRole() == 1) {//主播
                            //如果是主播,获取房间号,加入房间预览视频
                            getRoomId();
                        } else {//用户
                            Intent intent = new Intent(mContext, UserViewQuickActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
            changeImage();
        } else {
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
                //职业
                if (!TextUtils.isEmpty(bean.t_vocation)) {
                    mHolder.mJobTv.setVisibility(View.VISIBLE);
                    mHolder.mJobTv.setText(bean.t_vocation);
                } else {
                    mHolder.mJobTv.setVisibility(View.GONE);
                }
                //签名
                String sign = bean.t_autograph;
                if (!TextUtils.isEmpty(sign)) {
                    mHolder.mSignTv.setText(sign);
                } else {
                    mHolder.mSignTv.setText(mContext.getResources().getString(R.string.lazy));
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
        }
    }

    @Override
    public int getItemCount() {
        return mRecommendBeans != null ? mRecommendBeans.size() : 0;
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
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mJobTv = itemView.findViewById(R.id.job_tv);
            mInfoLl = itemView.findViewById(R.id.info_ll);
            mSignTv = itemView.findViewById(R.id.sign_tv);
        }
    }

    class ChangeHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;
        FrameLayout mContentFl;

        ChangeHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mContentFl = itemView.findViewById(R.id.content_fl);
        }
    }

    /**
     * 更换图片
     */
    //头一张
    private int mLastImageIndex;

    private void changeImage() {
        if (mChangeHolder != null && mChangeHandler != null) {
            int[] res = {R.drawable.change_one, R.drawable.change_two, R.drawable.change_three,
                    R.drawable.change_four, R.drawable.change_five, R.drawable.change_six, R.drawable.change_seven,
                    R.drawable.chang_eight, R.drawable.change_nine};
            Random random = new Random();
            int next = random.nextInt(res.length);
            //淡入淡出动画需要先设置一个Drawable数组，用于变换图片
            Drawable[] drawableArray = {mContext.getResources().getDrawable(res[mLastImageIndex]),
                    mContext.getResources().getDrawable(res[next])
            };
            TransitionDrawable transitionDrawable = new TransitionDrawable(drawableArray);
            mChangeHolder.mContentIv.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(230);
            mLastImageIndex = next;
            mChangeHandler.removeCallbacksAndMessages(null);
            mChangeHandler.sendEmptyMessageDelayed(CHANGE_NEXT, 3000);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<GirlTopRecyclerAdapter> mSettingActivityWeakReference;

        MyHandler(GirlTopRecyclerAdapter settingActivity) {
            mSettingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GirlTopRecyclerAdapter settingActivity = mSettingActivityWeakReference.get();
            if (settingActivity != null && msg.what == settingActivity.CHANGE_NEXT) {
                settingActivity.changeImage();
            }
        }
    }

    void resumeChange() {
        if (mChangeHandler != null) {
            mChangeHandler.removeCallbacksAndMessages(null);
            mChangeHandler.sendEmptyMessageDelayed(CHANGE_NEXT, 3000);
        }
    }

    void pauseChange() {
        if (mChangeHandler != null) {
            mChangeHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 速配
     */
    private void getRoomId() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SPEED_DATING_ROOM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer roomId = response.m_object;
                    if (roomId > 0) {
                        Intent intent = new Intent(mContext, QuickVideoChatActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR);
                        mContext.startActivity(intent);
                    }
                }
            }


        });
    }

}
