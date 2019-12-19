package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.ShareRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.ShareLayoutBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.util.FileUtil;
import com.yiliaodemo.chat.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：二维码页面
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ErWeiCodeActivity extends BaseActivity {

    @BindView(R.id.content_iv)
    ImageView mContentIv;
    @BindView(R.id.content_fl)
    FrameLayout mContentFl;

    private Tencent mTencent;
    private IWXAPI mWxApi;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_er_wei_code_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.my_code);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        loadImage();
    }

    /**
     * 加载图片
     */
    private void loadImage() {
        final String url = ChatApi.ON_LOAD_GALANCE_OVER + getUserId();
        //加载
        showLoadingDialog();
        Glide.with(ErWeiCodeActivity.this).load(url).asBitmap()
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mContentIv.setImageBitmap(resource);
                        dismissLoadingDialog();
                    }
                });
    }

    @OnClick({R.id.invite_tv, R.id.copy_tv, R.id.share_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite_tv: {//邀请主播
                String url = ChatApi.SHARE_URL + getUserId();
                //获取剪贴板管理器
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", url);
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtil.showToast(mContext, R.string.copy_success);
                }
                break;
            }
            case R.id.copy_tv: {//分享链接
                showShareDialog(false);
                break;
            }
            case R.id.share_tv: {//分享二维码
                showShareDialog(true);
                break;
            }
        }
    }

    private String viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        String res = saveImageToGallery(ErWeiCodeActivity.this, cachebmp);
        view.destroyDrawingCache();
        return res;
    }

    //保存文件到指定路径
    private String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return null;
            }
        }
        File dFile = new File(Constant.ER_CODE);
        if (!dFile.exists()) {
            boolean res = dFile.mkdir();
            if (!res) {
                return null;
            }
        } else {
            FileUtil.deleteFiles(dFile.getPath());
        }
        File file = new File(dFile, "erCode.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        // 如果不设置canvas画布为白色，则生成透明
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    //------------------------------------分享------------------

    /**
     * 显示分享dialog
     *
     * @param shareImage true是分享二维码
     */
    private void showShareDialog(boolean shareImage) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_share_layout, null);
        setDialogView(view, mDialog, shareImage);
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
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog, final boolean shareImage) {
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
        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(ErWeiCodeActivity.this);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new ShareRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {//微信
                    if (shareImage) {
                        String path = viewSaveToImage(mContentFl);
                        if (!TextUtils.isEmpty(path)) {
                            shareImageToWeChat(false, path);
                        }
                    } else {
                        shareUrlToWeChat(false);
                    }
                } else if (position == 1) {//朋友圈
                    if (shareImage) {
                        String path = viewSaveToImage(mContentFl);
                        if (!TextUtils.isEmpty(path)) {
                            shareImageToWeChat(true, path);
                        }
                    } else {
                        shareUrlToWeChat(true);
                    }
                } else if (position == 2) {//QQ
                    if (shareImage) {
                        String path = viewSaveToImage(mContentFl);
                        if (!TextUtils.isEmpty(path)) {
                            shareImageToQQ(path);
                        }
                    } else {
                        shareUrlToQQ();
                    }
                } else if (position == 3) {//QQ空间
                    if (shareImage) {
                        String path = viewSaveToImage(mContentFl);
                        if (!TextUtils.isEmpty(path)) {
                            shareImageToQZone(path);
                        }
                    } else {
                        shareUrlToQZone();
                    }
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

    //--------------------------QQ分享----------------------

    /**
     * 分享图片到QQ
     */
    private void shareImageToQQ(String filePath) {
        if (mTencent != null) {
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);// 设置分享类型为纯图片分享
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);// 需要分享的本地图片URL
            mTencent.shareToQQ(ErWeiCodeActivity.this, params, new MyUIListener());
        }
    }

    /**
     * 分享链接到QQ
     */
    private void shareUrlToQQ() {
        if (mTencent != null) {
            String url = ChatApi.SHARE_URL + getUserId();
            String title = getResources().getString(R.string.chat_title);
            String des = getResources().getString(R.string.chat_des);
            String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);// 标题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mineUrl);// 图片地址
            mTencent.shareToQQ(ErWeiCodeActivity.this, params, new MyUIListener());
        }
    }

    /**
     * 分享图片到QQ空间
     */
    private void shareImageToQZone(String filePath) {
        if (mTencent != null) {
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);  //注意，要向qq空间分享纯图片，只能传这三个参数，不能传其他的
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);  //localImgUrl必须是本地手机图片地址
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
            mTencent.shareToQQ(ErWeiCodeActivity.this, params, new MyUIListener());
        }
    }

    /**
     * 分享链接到QQ空间
     */
    private void shareUrlToQZone() {
        if (mTencent != null) {
            String url = ChatApi.SHARE_URL + getUserId();
            String title = getResources().getString(R.string.chat_title);
            String des = getResources().getString(R.string.chat_des);
            String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
            Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 标题
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
            ArrayList<String> images = new ArrayList<>();
            images.add(mineUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);// 图片地址
            mTencent.shareToQzone(ErWeiCodeActivity.this, params, new MyUIListener());
        }
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


    //------------------------------微信分享-----------------

    /**
     * 分享图片到微信
     */
    private void shareImageToWeChat(boolean isFriendCricle, String filePath) {
        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
            return;
        }

        WXImageObject wxImageObject = new WXImageObject();
        wxImageObject.setImagePath(filePath);

        WXMediaMessage msg = new WXMediaMessage(wxImageObject);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Bitmap thumb = Bitmap.createScaledBitmap(bitmap, 150, 240, true);
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

    /**
     * 分享url到微信
     */
    private void shareUrlToWeChat(boolean isFriendCricle) {
        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
            return;
        }
        String url = ChatApi.SHARE_URL + getUserId();
        String title = getResources().getString(R.string.chat_title);
        String des = getResources().getString(R.string.chat_des);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = des;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
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
