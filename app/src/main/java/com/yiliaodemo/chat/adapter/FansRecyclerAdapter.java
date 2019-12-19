package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ChatActivity;
import com.yiliaodemo.chat.activity.VideoChatOneActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.FansBean;
import com.yiliaodemo.chat.bean.VideoSignBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

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
public class FansRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<FansBean> mBeans = new ArrayList<>();

    public FansRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<FansBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_fans_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FansBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //主播昵称
            final String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mTitleTv.setText(nick);
            }
            //头像
            final String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 60);
                int high = DevicesUtil.dp2px(mContext, 60);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv,
                        width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //是否VIP 0.是 1.否
            if (bean.t_is_vip == 0) {//是VIP
                mHolder.mVipIv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mVipIv.setVisibility(View.GONE);
            }
            //金币档
            int goldLevel = bean.goldfiles;
            if (goldLevel == 1) {
                mHolder.mGoldTv.setText(mContext.getString(R.string.level_one));
            } else if (goldLevel == 2) {
                mHolder.mGoldTv.setText(mContext.getString(R.string.level_two));
            } else if (goldLevel == 3) {
                mHolder.mGoldTv.setText(mContext.getString(R.string.level_three));
            } else if (goldLevel == 4) {
                mHolder.mGoldTv.setText(mContext.getString(R.string.level_four));
            } else if (goldLevel == 5) {
                mHolder.mGoldTv.setText(mContext.getString(R.string.level_five));
            }
            //充值档 1.第一档 星星 2.第二档 月亮 3.第三档 太阳
            int grade = bean.grade;
            if (grade == 1) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.grade_star);
            } else if (grade == 2) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.grade_moon);
            } else if (grade == 3) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.grade_sun);
            }
            //视频聊天
            mHolder.mChatIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int userId = bean.t_id;
                    if (userId > 0) {
                        getSign(bean);
                    }
                }
            });
            //文字聊天
            mHolder.mTextIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int userId = bean.t_id;
                    if (userId > 0) {
                        String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).headUrl;
                        Intent intent = new Intent(mContext.getApplicationContext(), ChatActivity.class);
                        intent.putExtra(Constant.TITLE, nick);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.USER_HEAD_URL, headImg);
                        intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                        intent.putExtra(Constant.MINE_ID, mContext.getUserId());
                        mContext.startActivity(intent);
                    }
                }
            });
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
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mTitleTv;
        ImageView mChatIv;
        ImageView mVipIv;
        ImageView mLevelIv;
        ImageView mTextIv;
        TextView mGoldTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.header_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mChatIv = itemView.findViewById(R.id.chat_iv);
            mVipIv = itemView.findViewById(R.id.vip_iv);
            mLevelIv = itemView.findViewById(R.id.level_iv);
            mTextIv = itemView.findViewById(R.id.text_iv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
        }
    }

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final FansBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(bean.t_id));
        paramMap.put("anthorId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        requestChat(mRoomId, bean);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mContext.showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mContext.dismissLoadingDialog();
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }

    /**
     * 主播对用户发起聊天
     */
    private void requestChat(final int roomId, final FansBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anchorUserId", mContext.getUserId());
        paramMap.put("userId", String.valueOf(bean.t_id));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.ACTOR_LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(mContext, VideoChatOneActivity.class);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR_INVITE);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.NICK_NAME, bean.t_nickName);
                        intent.putExtra(Constant.USER_HEAD_URL, bean.t_handImg);
                        mContext.startActivity(intent);
                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {//对方设置了勿扰
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(mContext);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mContext.dismissLoadingDialog();
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

}
