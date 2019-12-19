package com.yiliaodemo.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.activity.PhotoActivity;
import com.yiliaodemo.chat.activity.VipCenterActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.VideoBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料下方视频/图片RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoVideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ActorInfoOneActivity mContext;
    private int mActorId;
    private List<VideoBean> mBeans = new ArrayList<>();

    public InfoVideoRecyclerAdapter(ActorInfoOneActivity context) {
        mContext = context;
    }

    public void loadData(List<VideoBean> beans, int actorId) {
        mBeans = beans;
        mActorId = actorId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_info_video_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final VideoBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //标题
            if (!TextUtils.isEmpty(bean.t_title)) {
                mHolder.mTitleTv.setText(bean.t_title);
                mHolder.mTitleTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mTitleTv.setVisibility(View.GONE);
            }
            //昵称
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNickTv.setText(bean.t_nickName);
                mHolder.mNickTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mNickTv.setVisibility(View.GONE);
            }
            //加载封面
            final int fileType = bean.t_file_type;
            //是否私密
            final int mPrivate = bean.t_is_private;
            final int isSee = bean.is_see;//0.未查看1.已查看
            if (mPrivate == 1 && isSee == 0) {//是否私密：0.否1.是
                mHolder.mLockFl.setVisibility(View.VISIBLE);
                mHolder.mPlayIv.setVisibility(View.GONE);
                if (fileType == 0) {//0.图片
                    ImageLoadHelper.glideShowCornerImageWithFade(mContext, bean.t_addres_url, mHolder.mContentIv);
                } else {
                    ImageLoadHelper.glideShowCornerImageWithFade(mContext, bean.t_video_img, mHolder.mContentIv);
                }
                //钱
                int gold = bean.t_money;
                if (gold > 0) {
                    String content = String.valueOf(gold) + mContext.getResources().getString(R.string.gold_des_one);
                    mHolder.mGoldTv.setText(content);
                    mHolder.mGoldTv.setVisibility(View.VISIBLE);
                }
            } else {
                mHolder.mLockFl.setVisibility(View.GONE);
                mHolder.mGoldTv.setVisibility(View.GONE);
                String imageUrl;
                if (fileType == 0) {
                    imageUrl = bean.t_addres_url;
                    mHolder.mPlayIv.setVisibility(View.GONE);
                } else {
                    imageUrl = bean.t_video_img;
                    mHolder.mPlayIv.setVisibility(View.VISIBLE);
                }
                ImageLoadHelper.glideShowCornerImageWithUrl(mContext, imageUrl, mHolder.mContentIv);
            }
            //点击事件
            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int vip = mContext.getUserVip();
                    if (mPrivate == 1 && isSee == 0 && vip == 1) {//私密且未查看过且不是VIP
                        if (fileType == 0) {//图片
                            int photo = 0;
                            showSeeWeChatRemindDialog(photo, bean);
                        } else {
                            int video = 1;
                            showSeeWeChatRemindDialog(video, bean);
                        }
                    } else {
                        if (fileType == 0) {//0.图片
                            String imageUrl = bean.t_addres_url;
                            if (!TextUtils.isEmpty(imageUrl)) {
                                Intent intent = new Intent(mContext, PhotoActivity.class);
                                intent.putExtra(Constant.IMAGE_URL, imageUrl);
                                mContext.startActivity(intent);
                            } else {
                                ToastUtil.showToast(mContext, R.string.system_error);
                            }
                        } else if (fileType == 1) {//视频
                            Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                            intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ACTOR_VIDEO);
                            intent.putExtra(Constant.VIDEO_URL, bean.t_addres_url);
                            intent.putExtra(Constant.ACTOR_ID, mActorId);
                            intent.putExtra(Constant.FILE_ID, bean.t_id);
                            intent.putExtra(Constant.COVER_URL, bean.t_video_img);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    /**
     * 显示查看微信号提醒
     */
    private void showSeeWeChatRemindDialog(int position, VideoBean bean) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_video_layout, null);
        setDialogView(view, mDialog, position, bean);
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
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog, final int position, final VideoBean bean) {
        //金币
        TextView gold_tv = view.findViewById(R.id.gold_tv);
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        int gold = bean.t_money;
        if (gold > 0) {
            if (position == 0) {//照片
                see_des_tv.setText(R.string.see_picture_need);
            } else {//视频
                see_des_tv.setText(R.string.see_video_need);
            }
            String content = gold + mContext.getResources().getString(R.string.gold);
            gold_tv.setText(content);
        }
        //升级
        ImageView update_iv = view.findViewById(R.id.update_iv);
        update_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VipCenterActivity.class);
                mContext.startActivity(intent);
                mDialog.dismiss();
            }
        });

        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否Vip
                int vip = mContext.getUserVip();
                if (vip == 0) {//是VIP
                    vipSeePrivate(position, bean);
                } else if (vip == 1) {//非vip
                    notVipSeePrivate(position, bean);
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * Vip查看照片 视频
     */
    private void vipSeePrivate(final int position, final VideoBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("sourceId", String.valueOf(bean.t_id));
        OkHttpUtils.post().url(ChatApi.VIP_SEE_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        ToastUtil.showToast(mContext, R.string.vip_free);
                        if (position == 0) {//图片
                            String imageUrl = bean.t_addres_url;
                            Intent intent = new Intent(mContext, PhotoActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, imageUrl);
                            mContext.startActivity(intent);
                        } else {//视频
                            Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                            intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ACTOR_VIDEO);
                            intent.putExtra(Constant.VIDEO_URL, bean.t_addres_url);
                            intent.putExtra(Constant.ACTOR_ID, mActorId);
                            intent.putExtra(Constant.FILE_ID, bean.t_id);
                            intent.putExtra(Constant.COVER_URL, bean.t_video_img);
                            mContext.startActivity(intent);
                        }
                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialog(mContext);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.system_error);
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
     * 非VIP查看私密照片 视频
     */
    private void notVipSeePrivate(final int position, final VideoBean bean) {
        String url;
        String key;
        if (position == 0) {//图片
            url = ChatApi.SEE_IMAGE_CONSUME;
            key = "photoId";
        } else {
            url = ChatApi.SEE_VIDEO_CONSUME;
            key = "videoId";
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put(key, String.valueOf(bean.t_id));
        OkHttpUtils.post().url(url)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            if (response.m_istatus == 2) {
                                ToastUtil.showToast(mContext, R.string.vip_free);
                            } else {
                                ToastUtil.showToast(mContext, R.string.pay_success);
                            }
                        }
                        if (position == 0) {//图片
                            String imageUrl = bean.t_addres_url;
                            Intent intent = new Intent(mContext, PhotoActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, imageUrl);
                            mContext.startActivity(intent);
                        } else {//视频
                            Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                            intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ACTOR_VIDEO);
                            intent.putExtra(Constant.VIDEO_URL, bean.t_addres_url);
                            intent.putExtra(Constant.ACTOR_ID, mActorId);
                            intent.putExtra(Constant.FILE_ID, bean.t_id);
                            intent.putExtra(Constant.COVER_URL, bean.t_video_img);
                            mContext.startActivity(intent);
                        }
                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialog(mContext);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });


    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentFl;
        ImageView mContentIv;
        TextView mTitleTv;
        TextView mNickTv;
        TextView mGoldTv;
        ImageView mPlayIv;
        FrameLayout mLockFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mPlayIv = itemView.findViewById(R.id.play_iv);
            mLockFl =itemView.findViewById(R.id.lock_fl);
        }
    }

}
