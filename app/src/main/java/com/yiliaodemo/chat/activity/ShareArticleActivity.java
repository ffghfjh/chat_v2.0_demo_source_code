package com.yiliaodemo.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.ShareArticleRecyclerAdapter;
import com.yiliaodemo.chat.adapter.ShareRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.ShareArticleBean;
import com.yiliaodemo.chat.bean.ShareLayoutBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：分享列表页面
 * 作者：
 * 创建时间：2018/8/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ShareArticleActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    private Tencent mTencent;
    private IWXAPI mWxApi;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_share_article_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.share_earn);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        initRecycler();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        List<ShareArticleBean> shareArticleBeans = new ArrayList<>();
        //1.小夫妻在家玩这个，半年后竟存款惊人！
        ShareArticleBean oneBean = new ShareArticleBean();
        oneBean.title = getResources().getString(R.string.little_couple);
        oneBean.des = getResources().getString(R.string.real_say);
        oneBean.resourceId = R.drawable.share_little_cuple;
        oneBean.targetUrl = ChatApi.JUMP_SPOUSE + getUserId();
        //2.在校女大学生兼职赚钱，三个月后竟给父母买车买房！   cps:  方便吗，你的好友【】邀请您视频私聊！  des: 点击接听
        ShareArticleBean twoBean = new ShareArticleBean();
        twoBean.title = getResources().getString(R.string.real_one);
        twoBean.des = getResources().getString(R.string.cps_des);
        twoBean.resourceId = R.drawable.icon_cps_friend;
        twoBean.targetUrl = ChatApi.CHAT_OFFICAL_URL;
        //3.APP招主播啦，无需才艺，轻松赚钱！收入高，提现快！
        ShareArticleBean threeBean = new ShareArticleBean();
        threeBean.title = getResources().getString(R.string.get_actor_title);
        threeBean.des = getResources().getString(R.string.get_actor_des);
        threeBean.resourceId = R.drawable.share_get_actor;
        threeBean.targetUrl = ChatApi.JUMP_ANCHORS + getUserId();
        //4.App【在家赚钱】盛大开业，任性发红包了！先到先得，领完为止！
        ShareArticleBean fourBean = new ShareArticleBean();
        fourBean.title = getResources().getString(R.string.share_qun_title_one);
        fourBean.des = getResources().getString(R.string.share_qun_des_one);
        fourBean.resourceId = R.drawable.share_game;
        fourBean.targetUrl = ChatApi.JUMP_GAME + getUserId();

        shareArticleBeans.add(oneBean);
        shareArticleBeans.add(twoBean);
        shareArticleBeans.add(threeBean);
        shareArticleBeans.add(fourBean);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        ShareArticleRecyclerAdapter mAdapter = new ShareArticleRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
        mAdapter.loadData(shareArticleBeans);
        mAdapter.setOnItemShareClickListener(new ShareArticleRecyclerAdapter.OnItemShareClickListener() {
            @Override
            public void onItemShareClick(ShareArticleBean shareArticleBean) {
                showShareDialog(shareArticleBean);
            }
        });
    }

    /**
     * 显示分享dialog
     */
    private void showShareDialog(ShareArticleBean shareArticleBean) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_share_layout, null);
        setDialogView(view, mDialog, shareArticleBean);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog, final ShareArticleBean shareArticleBean) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        GridLayoutManager manager = new GridLayoutManager(getBaseContext(), 4);
        content_rv.setLayoutManager(manager);
        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(ShareArticleActivity.this);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new ShareRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {//微信
                    shareToWeChat(false, shareArticleBean);
                } else if (position == 1) {//朋友圈
                    shareToWeChat(true, shareArticleBean);
                } else if (position == 2) {//QQ
                    shareToQQ(shareArticleBean);
                } else if (position == 3) {//QQ空间
                    shareToQZone(shareArticleBean);
                }
                mDialog.dismiss();
            }
        });
        List<ShareLayoutBean> beans = new ArrayList<>();
        beans.add(new ShareLayoutBean("微信", R.drawable.share_wechat));
        beans.add(new ShareLayoutBean("朋友圈", R.drawable.share_wechatfriend));
        beans.add(new ShareLayoutBean("QQ", R.drawable.share_qq));
        beans.add(new ShareLayoutBean("QQ空间", R.drawable.share_qzone));
        adapter.loadData(beans);
    }

    //-------------------QQ QZone分享--------------

    /**
     * 分享到QQ
     */
    private void shareToQQ(ShareArticleBean shareArticleBean) {
        String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareArticleBean.title);// 标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareArticleBean.des);// 摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareArticleBean.targetUrl);// 内容地址
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mineUrl);// 图片地址
        mTencent.shareToQQ(ShareArticleActivity.this, params, new MyUIListener());
    }

    /**
     * 分享到QZone
     */
    private void shareToQZone(ShareArticleBean shareArticleBean) {
        String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareArticleBean.title);// 标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareArticleBean.des);// 摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareArticleBean.targetUrl);// 内容地址
        ArrayList<String> images = new ArrayList<>();
        images.add(mineUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);// 图片地址
        mTencent.shareToQzone(ShareArticleActivity.this, params, new MyUIListener());
    }

    class MyUIListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastUtil.showToast(getApplicationContext(), R.string.share_success);
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.showToast(getApplicationContext(), R.string.share_fail);
        }

        @Override
        public void onCancel() {
            ToastUtil.showToast(getApplicationContext(), R.string.share_cancel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new MyUIListener());
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE
                    || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, new MyUIListener());
            }
        }
    }

    //----------------微信分享------------------

    /**
     * 分享到微信
     */
    private void shareToWeChat(boolean isFriendCricle, ShareArticleBean shareArticleBean) {
        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareArticleBean.targetUrl;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareArticleBean.title;
        msg.description = shareArticleBean.des;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), shareArticleBean.resourceId);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //WXSceneTimeline朋友圈    WXSceneSession聊天界面
        req.scene = isFriendCricle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;//聊天界面
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());
        mWxApi.sendReq(req);
        boolean res = mWxApi.sendReq(req);
        if (res) {
            AppManager.getInstance().setIsMainPageShareQun(false);
        }
    }

}
