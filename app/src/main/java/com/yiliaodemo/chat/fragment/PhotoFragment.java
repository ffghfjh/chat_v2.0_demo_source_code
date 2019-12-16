package com.yiliaodemo.chat.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.VipCenterActivity;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActiveFileBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.view.LoadingView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：查看动态图片Fragment页面
 * 作者：
 * 创建时间：2018/1/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhotoFragment extends BaseFragment {

    public PhotoFragment() {

    }

    private PhotoView mContentPv;
    private FrameLayout mLockFl;
    private ImageView mCoverIv;
    private View mCoverV;
    private LoadingView mLoadingLv;

    @Override
    protected int initLayout() {
        return R.layout.fragment_photo_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mContentPv = view.findViewById(R.id.content_pv);
        mLockFl = view.findViewById(R.id.lock_fl);
        mCoverIv = view.findViewById(R.id.cover_iv);
        mCoverV = view.findViewById(R.id.cover_v);
        mLoadingLv = view.findViewById(R.id.loading_lv);
    }

    @Override
    protected void onFirstVisible() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            final ActiveFileBean fileBean = (ActiveFileBean) bundle.getSerializable(Constant.ACTIVE_FILE_BEAN);
            final int actorId = bundle.getInt(Constant.ACTOR_ID);
            if (fileBean != null) {
                //锁
                if (judgePrivate(fileBean, actorId)) {
                    mLockFl.setVisibility(View.VISIBLE);
                    mCoverIv.setVisibility(View.VISIBLE);
                    mCoverV.setVisibility(View.VISIBLE);
                    ImageLoadHelper.glideShowImageWithFade(mContext, fileBean.t_file_url, mCoverIv);
                    mLockFl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSeeWeChatRemindDialog(fileBean);
                        }
                    });
                } else {
                    mCoverV.setVisibility(View.GONE);
                    mCoverIv.setVisibility(View.GONE);
                    mLockFl.setVisibility(View.GONE);
                }

                final PhotoViewAttacher attacher = new PhotoViewAttacher(mContentPv);
                Glide.with(this).load(fileBean.t_file_url).crossFade()
                        .into(new GlideDrawableImageViewTarget(mContentPv) {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                attacher.update();
                                mLoadingLv.setVisibility(View.GONE);
                            }
                        });
                attacher.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(ImageView view, float x, float y) {
                        if (getActivity() != null) {
                            getActivity().finish();
                        } else {
                            if (mContext != null) {
                                mContext.finish();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 判断是否应该显示消费弹窗
     */
    private boolean judgePrivate(ActiveFileBean fileBean, int actorId) {
        int mineId = Integer.parseInt(mContext.getUserId());
        return fileBean.t_gold > 0 && fileBean.isConsume == 0
                && mContext.getUserVip() == 1 && mineId != actorId;
    }

    /**
     * 显示查看微信号提醒
     */
    private void showSeeWeChatRemindDialog(ActiveFileBean fileBean) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_video_layout, null);
        setDialogView(view, mDialog, fileBean);
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
    private void setDialogView(View view, final Dialog mDialog, final ActiveFileBean fileBean) {
        //金币
        TextView gold_tv = view.findViewById(R.id.gold_tv);
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        see_des_tv.setText(R.string.see_picture_need);
        String content = fileBean.t_gold + mContext.getResources().getString(R.string.gold);
        gold_tv.setText(content);
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
                seePrivate(fileBean);
                mDialog.dismiss();
            }
        });
    }

    /**
     * Vip查看照片 视频
     */
    private void seePrivate(final ActiveFileBean fileBean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("fileId", String.valueOf(fileBean.t_id));
        OkHttpUtils.post().url(ChatApi.DYNAMIC_PAY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        ToastUtil.showToast(mContext, R.string.vip_free);
                        mCoverIv.setVisibility(View.GONE);
                        mLockFl.setVisibility(View.GONE);
                        mCoverV.setVisibility(View.GONE);
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

}
