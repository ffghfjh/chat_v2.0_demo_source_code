package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Message;
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
import com.yiliaodemo.chat.activity.BigHouseActivity;
import com.yiliaodemo.chat.activity.QuickVideoChatActivity;
import com.yiliaodemo.chat.activity.UserViewQuickActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.BigRoomListBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间列表RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BigHouseListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<BigRoomListBean> mRecommendBeans = new ArrayList<>();
    private final int CHANGE = 0;
    private final int CHANGE_NEXT = 0x13;
    private ChangeHolder mChangeHolder;
    private MyHandler mChangeHandler = new MyHandler(BigHouseListRecyclerAdapter.this);

    public BigHouseListRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<BigRoomListBean> beans) {
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
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_big_house_quick_flash_layout,
                    parent, false);
            mChangeHolder = new ChangeHolder(itemView);
            return mChangeHolder;
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_big_house_recycler_layout,
                    parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BigRoomListBean bean = mRecommendBeans.get(position);
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
                mHolder.mNickTv.setText(bean.t_nickName);
                //显示封面图
                String coverImg = bean.t_cover_img;
                if (!TextUtils.isEmpty(coverImg)) {
                    ImageLoadHelper.glideShowCornerImageWithUrl(mContext, coverImg, mHolder.mContentIv);
                }
                //人数
                int number = bean.viewerCount;
                if (number > 0) {
                    String content;
                    if (number < 10000) {
                        content = number + mContext.getString(R.string.number_man);
                    } else {
                        BigDecimal old = new BigDecimal(number);
                        BigDecimal ten = new BigDecimal(10000);
                        BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
                        content = res + mContext.getString(R.string.number_ten_thousand);
                    }
                    mHolder.mNumberTv.setText(content);
                }
                //是否开播 0.未开播
                if (bean.t_is_debut == 0) {
                    mHolder.mStatusTv.setVisibility(View.GONE);
                } else {
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                }
                //点击
                mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.t_user_id > 0 && bean.t_room_id > 0) {
                            int selfId = Integer.parseInt(mContext.getUserId());
                            if (selfId == bean.t_user_id) {//如果是自己看自己,就直接开播
                                Intent intent = new Intent(mContext, BigHouseActivity.class);
                                intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR);
                                intent.putExtra(Constant.ACTOR_ID, Integer.parseInt(mContext.getUserId()));
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, BigHouseActivity.class);
                                intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                                intent.putExtra(Constant.ACTOR_ID, bean.t_user_id);
                                intent.putExtra(Constant.ROOM_ID, bean.t_room_id);
                                intent.putExtra(Constant.CHAT_ROOM_ID, bean.t_chat_room_id);
                                mContext.startActivity(intent);
                            }
                        }
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

        ImageView mContentIv;
        TextView mNickTv;
        TextView mNumberTv;
        FrameLayout mContentFl;
        TextView mStatusTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mNumberTv = itemView.findViewById(R.id.number_tv);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mStatusTv = itemView.findViewById(R.id.status_tv);
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
            int[] res = {R.drawable.change_one_big, R.drawable.change_two_big, R.drawable.change_three_big,
                    R.drawable.change_four_big, R.drawable.change_five_big, R.drawable.change_six_big, R.drawable.change_seven_big,
                    R.drawable.change_eight_big, R.drawable.change_nine_big};
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
        private WeakReference<BigHouseListRecyclerAdapter> mSettingActivityWeakReference;

        MyHandler(BigHouseListRecyclerAdapter settingActivity) {
            mSettingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BigHouseListRecyclerAdapter settingActivity = mSettingActivityWeakReference.get();
            if (settingActivity != null && msg.what == settingActivity.CHANGE_NEXT) {
                settingActivity.changeImage();
            }
        }
    }

    public void resumeChange() {
        if (mChangeHandler != null) {
            mChangeHandler.removeCallbacksAndMessages(null);
            mChangeHandler.sendEmptyMessageDelayed(CHANGE_NEXT, 3000);
        }
    }

    public void pauseChange() {
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
