package com.yiliaodemo.chat.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ChargeActivity;
import com.yiliaodemo.chat.activity.ChatActivity;
import com.yiliaodemo.chat.activity.VideoChatOneActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.NearBean;
import com.yiliaodemo.chat.bean.VideoSignBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
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
 * 功能描述：距离recycler的adapter
 * 作者：
 * 创建时间：2018/11/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DistanceRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<NearBean> mBeans = new ArrayList<>();

    public DistanceRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<NearBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_distance_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NearBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //nick
            final String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
                mHolder.mNickTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mNickTv.setVisibility(View.GONE);
            }
            //头像
            final String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 62);
                int high = DevicesUtil.dp2px(mContext, 62);
                ImageLoadHelper.glideShowCornerImageWithUrl(mContext, headImg, mHolder.mHeadIv,
                        5, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //距离
            String distance = bean.distance + mContext.getResources().getString(R.string.distance_one);
            mHolder.mDistanceTv.setText(distance);
            //角色 0.普通用户 1.主播
            final int role = bean.t_role;
            if (role == 0) {
                mHolder.mVerifyIv.setVisibility(View.GONE);
            } else {
                mHolder.mVerifyIv.setVisibility(View.VISIBLE);
            }
            //状态  0.空闲 1.忙碌 2.离线
            int state = bean.t_onLine;
            if (role == 0) {//用户
                if (state == 0) {
                    mHolder.mOnlineTv.setVisibility(View.VISIBLE);
                    mHolder.mOfflineTv.setVisibility(View.GONE);
                    mHolder.mBusyTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_yellow);
                    mHolder.mVideoChatIv.setEnabled(true);
                } else if (state == 1) {
                    mHolder.mOfflineTv.setVisibility(View.VISIBLE);
                    mHolder.mBusyTv.setVisibility(View.GONE);
                    mHolder.mOnlineTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_gray);
                    mHolder.mVideoChatIv.setEnabled(false);
                } else {
                    mHolder.mOfflineTv.setVisibility(View.GONE);
                    mHolder.mBusyTv.setVisibility(View.GONE);
                    mHolder.mOnlineTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_gray);
                    mHolder.mVideoChatIv.setEnabled(false);
                }
            } else {//主播
                if (state == 0) {
                    mHolder.mOnlineTv.setVisibility(View.VISIBLE);
                    mHolder.mOfflineTv.setVisibility(View.GONE);
                    mHolder.mBusyTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_yellow);
                    mHolder.mVideoChatIv.setEnabled(true);
                } else if (state == 1) {
                    mHolder.mBusyTv.setVisibility(View.VISIBLE);
                    mHolder.mOnlineTv.setVisibility(View.GONE);
                    mHolder.mOfflineTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_yellow);
                    mHolder.mVideoChatIv.setEnabled(true);
                } else if (state == 2) {
                    mHolder.mOfflineTv.setVisibility(View.VISIBLE);
                    mHolder.mBusyTv.setVisibility(View.GONE);
                    mHolder.mOnlineTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_gray);
                    mHolder.mVideoChatIv.setEnabled(false);
                } else {
                    mHolder.mOfflineTv.setVisibility(View.GONE);
                    mHolder.mBusyTv.setVisibility(View.GONE);
                    mHolder.mOnlineTv.setVisibility(View.GONE);
                    mHolder.mVideoChatIv.setImageResource(R.drawable.video_chat_gray);
                    mHolder.mVideoChatIv.setEnabled(false);
                }
            }

            //性别 性别 0.女 1.男
            if (bean.t_sex == 0) {
                mHolder.mSexIv.setImageResource(R.drawable.female_white);
            } else {
                mHolder.mSexIv.setImageResource(R.drawable.male_white);
            }
            //年龄
            mHolder.mAgeTv.setText(String.valueOf(bean.t_age));
            //职业
            String job = bean.t_vocation;
            if (!TextUtils.isEmpty(job)) {
                mHolder.mJobTv.setText(job);
                mHolder.mJobTv.setVisibility(View.VISIBLE);
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
            //视频聊天
            mHolder.mVideoChatIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int userId = bean.t_id;
                    if (userId > 0) {
                        //0.普通用户 1.主播
                        getSign(bean, role == 1);
                    }
                }
            });
            //文字聊天
            mHolder.mPrivateMessageIv.setOnClickListener(new View.OnClickListener() {
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
                    int actorId = bean.t_id;
                    if (actorId > 0) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, actorId);
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

        private ImageView mHeadIv;
        private TextView mDistanceTv;
        private ImageView mPrivateMessageIv;
        private ImageView mVideoChatIv;
        private TextView mNickTv;
        private TextView mOfflineTv;
        private TextView mOnlineTv;
        private TextView mBusyTv;
        private ImageView mVerifyIv;
        private ImageView mSexIv;
        private TextView mAgeTv;
        private TextView mJobTv;
        private TextView mSignTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mDistanceTv = itemView.findViewById(R.id.distance_tv);
            mPrivateMessageIv = itemView.findViewById(R.id.private_message_iv);
            mVideoChatIv = itemView.findViewById(R.id.video_chat_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mOfflineTv = itemView.findViewById(R.id.offline_tv);
            mOnlineTv = itemView.findViewById(R.id.online_tv);
            mBusyTv = itemView.findViewById(R.id.busy_tv);
            mVerifyIv = itemView.findViewById(R.id.verify_iv);
            mSexIv = itemView.findViewById(R.id.sex_iv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mJobTv = itemView.findViewById(R.id.job_tv);
            mSignTv = itemView.findViewById(R.id.sign_tv);
        }
    }

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final NearBean bean, final boolean isUserCallActor) {
        String userId;
        String actorId;
        if (isUserCallActor) {
            userId = mContext.getUserId();
            actorId = String.valueOf(bean.t_id);
        } else {
            userId = String.valueOf(bean.t_id);
            actorId = mContext.getUserId();
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("anthorId", actorId);
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        int onlineState = signBean.onlineState;
                        if (onlineState == 1 && mContext.getUserRole() == 0) {//1.余额刚刚住够
                            showGoldJustEnoughDialog(mRoomId, isUserCallActor, bean);
                        } else {
                            if (isUserCallActor) {//是用户call主播
                                userRequestChat(mRoomId, bean);
                            } else {//主播call用户
                                requestChat(mRoomId, bean);
                            }
                        }
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
     * 显示金币刚好够dialog
     */
    private void showGoldJustEnoughDialog(int mRoomId, boolean isUserCallActor, NearBean bean) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_one_minute_layout, null);
        setGoldDialogView(view, mDialog, mRoomId, isUserCallActor, bean);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setGoldDialogView(View view, final Dialog mDialog, final int mRoomId,
                                   final boolean isUserCallActor, final NearBean bean) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanRoom();
                mDialog.dismiss();
            }
        });
        //是 发起聊天
        TextView yes_tv = view.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserCallActor) {//是用户call主播
                    userRequestChat(mRoomId, bean);
                } else {//主播call用户
                    requestChat(mRoomId, bean);
                }
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空房间
                cleanRoom();
                Intent intent = new Intent(mContext, ChargeActivity.class);
                mContext.startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 清空房间
     */
    private void cleanRoom() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.USER_HANG_UP_LINK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("清空房间成功");
                }
            }
        });
    }

    /**
     * 主播对用户发起聊天
     */
    private void requestChat(final int roomId, final NearBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anchorUserId", mContext.getUserId());
        paramMap.put("userId", String.valueOf(bean.t_id));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.ACTOR_LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
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
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

    /**
     * 用户对主播发起聊天
     */
    private void userRequestChat(final int roomId, final NearBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("coverLinkUserId", String.valueOf(bean.t_id));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(mContext, VideoChatOneActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
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
                    } else if (response.m_istatus == -3) {
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
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

}
