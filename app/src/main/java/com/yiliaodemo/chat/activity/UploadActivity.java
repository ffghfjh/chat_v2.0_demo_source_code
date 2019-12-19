package com.yiliaodemo.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.SetChargeRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageHelper;
import com.yiliaodemo.chat.layoutmanager.PickerLayoutManager;
import com.yiliaodemo.chat.listener.OnFileUploadListener;
import com.yiliaodemo.chat.listener.OnLuCompressListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.oss.QServiceCfg;
import com.yiliaodemo.chat.util.BitmapUtil;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.FileUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.util.VideoFileUtils;
import com.yiliaodemo.chat.videoupload.TXUGCPublish;
import com.yiliaodemo.chat.videoupload.TXUGCPublishTypeDef;
import com.yiliaodemo.chat.view.MyProcessView;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：上传相册页面
 * 作者：
 * 创建时间：2018/7/11
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UploadActivity extends BaseActivity {

    @BindView(R.id.title_et)
    EditText mTitleEt;
    @BindView(R.id.money_tv)
    TextView mMoneyTv;
    @BindView(R.id.upload_iv)
    ImageView mUploadIv;
    @BindView(R.id.upload_fl)
    FrameLayout mUploadFl;
    @BindView(R.id.process_pv)
    MyProcessView mProcessPv;
    @BindView(R.id.charge_rl)
    View mChargeRl;
    @BindView(R.id.v_one)
    View mVOne;
    @BindView(R.id.video_done_tv)
    TextView mVideoDoneTv;
    @BindView(R.id.rule_tv)
    TextView mRuleTv;

    //腾讯点播 视频上传
    private TXUGCPublish mVideoPublish = null;
    private QServiceCfg mQServiceCfg;//图片

    private String mType = "";//	0.图片1.视频
    private String mVideoImageUrl = "";//视频封面地址
    private String mPassedUrl = "";//通过审核的url,包括图片和视频
    private String mSelectLocalPath = "";
    private String mFileId = "";//视频文件id

    private String[] mVideoStrs = new String[]{};
    private String[] mPictureStrs = new String[]{};
    private final int VIDEO = 0;
    private final int PICTURE = 1;
    private String mSelectContent = "";

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_upload_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.upload_album);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
        if (getUserRole() == 1) {//主播
            mChargeRl.setVisibility(View.VISIBLE);
            mVOne.setVisibility(View.VISIBLE);
            mRuleTv.setVisibility(View.VISIBLE);
            getAnchorVideoCost();
            getPrivatePhotoMoney();
        } else {
            mChargeRl.setVisibility(View.GONE);
            mVOne.setVisibility(View.GONE);
            mRuleTv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.upload_iv, R.id.submit_tv, R.id.left_fl, R.id.charge_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.charge_rl: {//收费设置
                if (mType.equals("1")) {//视频
                    if (mVideoStrs != null && mVideoStrs.length > 0) {
                        showChargeOptionDialog(VIDEO);
                    }
                } else {
                    if (mVideoStrs != null && mVideoStrs.length > 0) {
                        showChargeOptionDialog(PICTURE);
                    }
                }
                break;
            }
            case R.id.upload_iv: {//选择图片/视频
                showSelectDialog();
                break;
            }
            case R.id.submit_tv: {
                if (TextUtils.isEmpty(mType)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_choose_file);
                    return;
                }
                if (TextUtils.isEmpty(mSelectLocalPath)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_choose_file);
                    return;
                }
                File file = new File(mSelectLocalPath);
                if (!file.exists()) {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_choose_file);
                    return;
                }
                if (mType.equals("1") && TextUtils.isEmpty(mFileId)) {//视频
                    ToastUtil.showToast(getApplicationContext(), R.string.video_uploading);
                    return;
                }
                showLoadingDialog();
                uploadFileWithQQ(mSelectLocalPath, new OnFileUploadListener() {
                    @Override
                    public void onFileUploadSuccess() {
                        addMyPhotoAlbum();
                    }
                });
                break;
            }
            case R.id.left_fl: {//
                if (mType.equals("1") && TextUtils.isEmpty(mFileId)) {//视频
                    ToastUtil.showToast(getApplicationContext(), R.string.video_uploading);
                    return;
                }
                finish();
                break;
            }
        }
    }

    /**
     * 获取收费设置
     */
    private void getAnchorVideoCost() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_PRIVATE_VIDEO_MONEY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String m_object = response.m_object;
                    if (!TextUtils.isEmpty(m_object)) {
                        mVideoStrs = m_object.split(",");
                    }
                }
            }
        });
    }

    /**
     * 获取收费设置
     */
    private void getPrivatePhotoMoney() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_PRIVATE_PHOTO_MONEY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String m_object = response.m_object;
                    if (!TextUtils.isEmpty(m_object)) {
                        mPictureStrs = m_object.split(",");
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_CHOOSE_VIDEO && resultCode == RESULT_OK) {//选择视频
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "mSelected: " + mSelectedUris);
            mType = "1";
            dealVideoFile(mSelectedUris);
        } else if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {//图片
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "mSelected: " + mSelectedUris);
            mType = "0";
            dealImageFile(mSelectedUris);
        }
    }

    /**
     * 显示头像选择dialog
     */
    private void showSelectDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_video_image_layout, null);
        setDialogView(view, mDialog);
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
    private void setDialogView(View view, final Dialog mDialog) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //图片
        TextView album_tv = view.findViewById(R.id.album_tv);
        album_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //图片选择
                ImageHelper.openPictureChoosePage(UploadActivity.this, Constant.REQUEST_CODE_CHOOSE);
                mDialog.dismiss();
            }
        });
        //视频
        TextView take_tv = view.findViewById(R.id.take_tv);
        take_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageHelper.openVideoChoosePage(UploadActivity.this, Constant.REQUEST_CODE_CHOOSE_VIDEO);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 处理视频文件
     */
    private void dealVideoFile(List<Uri> mSelectedUris) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                Uri uri = mSelectedUris.get(0);
                String filePath = VideoFileUtils.getRealPathFromUri(this, uri);
                if (!TextUtils.isEmpty(filePath)) {
                    Log.d("uploadVideo","filePath不为空");
                    File file = new File(filePath);
                    if (!file.exists()) {
                        ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
                        return;
                    } else {//限制文件大小
                        LogUtil.i("视频大小: " + file.length() / 1024 / 1024);
                        double fileSize = (double) file.length() / 1024 / 1024;
                        if (fileSize > 50) {
                            ToastUtil.showToast(getApplicationContext(), R.string.file_too_big);
                            return;
                        }
                    }
                    //获取视频缩略图并显示
                    showVideoThumbnail(filePath);
                    //获取签名
                    getSign(filePath);
                } else {
                    Log.d("uploadVideo","上传失败,filePath为空");
                    ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取视频缩略图并显示
     */
    private void showVideoThumbnail(String filePath) {
        try {
            BitmapTask bitmapTask = new BitmapTask(UploadActivity.this, filePath);
            bitmapTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频上传签名
     * fromCheck  是鉴黄用的签名 还是上传用的
     */
    private void getSign(final String filePath) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    Log.d("uploadVideo","响应");
                    JSONObject jsonObject = JSON.parseObject(response);
                    int m_istatus = jsonObject.getInteger("m_istatus");
                    if (m_istatus == NetCode.SUCCESS) {
                        Log.d("uploadVideo","SUCCESS");
                        Log.d("uploadVideo",jsonObject.toJSONString());
                        String m_object = jsonObject.getString("m_object");
                        if (!TextUtils.isEmpty(m_object)) {
                            //上传文件
                            beginUpload(m_object, filePath);
                        }
                    } else {
                        Log.d("uploadVideo","FAIL");
                        ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                    }
                } else {
                    Log.d("uploadVideo","无响应");
                    ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                }
            }
        });
    }

    /**
     * 开始上传
     */
    private void beginUpload(final String sign, String filePath) {
        mUploadFl.setVisibility(View.VISIBLE);
        mProcessPv.setVisibility(View.VISIBLE);
        if (mVideoPublish == null) {
            mVideoPublish = new TXUGCPublish(this.getApplicationContext(), "carol_android");
            mVideoPublish.setListener(new TXUGCPublishTypeDef.ITXVideoPublishListener() {
                @Override
                public void onPublishProgress(long uploadBytes, long totalBytes) {
                    mProcessPv.setProcess((int) (100 * uploadBytes / totalBytes));
                }

                @Override
                public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {
                    Log.d("uploadVideo",result.descMsg+","+result.retCode+",");
                    if (result.retCode == 0) {//上传成功
                        mVideoDoneTv.setVisibility(View.VISIBLE);
                        mProcessPv.setVisibility(View.INVISIBLE);
                        LogUtil.i("文件id: " + result.videoId);
                        LogUtil.i("文件url: " + result.videoURL);
                        mFileId = result.videoId;
                        if (mType.equals("1")) {//视频
                            mPassedUrl = result.videoURL;
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                    }
                }
            });
        }

        TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
        // signature计算规则可参考 https://www.qcloud.com/document/product/266/9221
        param.signature = sign;
        param.videoPath = filePath;
        int publishCode = mVideoPublish.publishVideo(param);
        if (publishCode != 0) {
            LogUtil.i("发布失败，错误码：" + publishCode);
        }
    }

    /**
     * 新增相册数据
     */
    private void addMyPhotoAlbum() {
        String title = mTitleEt.getText().toString().trim();
        String money = mMoneyTv.getText().toString().trim();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_title", TextUtils.isEmpty(title) ? "" : title);
        paramMap.put("video_img", mVideoImageUrl);
        paramMap.put("url", mPassedUrl);
        paramMap.put("fileId", mFileId);
        paramMap.put("type", mType);
        paramMap.put("gold", money);
        OkHttpUtils.post().url(ChatApi.ADD_MY_PHOTO_ALBUM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                Log.d("uploadVideo","上传响应："+response.m_strMessage);
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 100);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d("uploadVideo","请求错误");
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
            }
        });
    }

    /**
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealImageFile(List<Uri> mSelectedUris) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                Uri uri = mSelectedUris.get(0);
                String filePath = FileUtil.getPathAbove19(this, uri);
                if (!TextUtils.isEmpty(filePath)) {
                    //压缩图片
                    compressImageWithLuBan(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用LuBan压缩图片
     */
    private void compressImageWithLuBan(String filePath) {
        ImageHelper.compressImageWithLuBan(getApplicationContext(), filePath, Constant.AFTER_COMPRESS_DIR, new OnLuCompressListener() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(File file) {
                mSelectLocalPath = file.getAbsolutePath();
                Bitmap bitmap = BitmapUtil.getImageThumbnail(file.getAbsolutePath(),
                        DevicesUtil.dp2px(getApplicationContext(), 83), DevicesUtil.dp2px(getApplicationContext(), 83));
                if (bitmap != null) {
                    mUploadIv.setImageBitmap(bitmap);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.not_good_image);
                }
                dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(getApplicationContext(), R.string.choose_picture_failed);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 使用腾讯云上传图片文件
     */
    private void uploadFileWithQQ(String filePath, final OnFileUploadListener listener) {
        String fileName;
        if (filePath.length() < 50) {
            fileName = filePath.substring(filePath.length() - 17, filePath.length());
        } else {
            String last = filePath.substring(filePath.length() - 4, filePath.length());
            if (last.contains("png")) {
                fileName = System.currentTimeMillis() + ".png";
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
        }

        String cosPath = "/album/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, filePath);
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }
                if (mType.equals("1")) {//视频,那么上传的就是视频的封面
                    mVideoImageUrl = resultUrl;
                } else if (mType.equals("0")) {//图片
                    mPassedUrl = resultUrl;
                }
                if (listener != null) {
                    listener.onFileUploadSuccess();
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.AFTER_COMPRESS_DIR);
    }

    static class BitmapTask extends AsyncTask<Integer, Void, Bitmap> {

        private WeakReference<UploadActivity> mWeakAty;
        private String mPath;

        BitmapTask(UploadActivity activity, String path) {
            mWeakAty = new WeakReference<>(activity);
            mPath = path;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            final UploadActivity activity = mWeakAty.get();
            if (activity != null) {
                try {
                    //final int overWidth = (int) (((DevicesUtil.getScreenW(activity) - DevicesUtil.dp2px(activity, 30)) / 2) * 1.5);
                    //final int overHeight = (int) (DevicesUtil.dp2px(activity, 212) * 1.5);
                    final int overWidth = (int) (DevicesUtil.getScreenW(activity) * 0.75);
                    final int overHeight = (int) (DevicesUtil.getScreenH(activity) * 0.75);
                    Bitmap bitmap = VideoFileUtils.getVideoFirstFrame(mPath);
                    Bitmap showBmp = VideoFileUtils.getVideoThumbnail(mPath, DevicesUtil.dp2px(activity, 83),
                            DevicesUtil.dp2px(activity, 83), MediaStore.Video.Thumbnails.MICRO_KIND);
                    //保存到本地
                    File pFile = new File(FileUtil.YCHAT_DIR);
                    if (!pFile.exists()) {
                        pFile.mkdir();
                    }
                    File dir = new File(Constant.AFTER_COMPRESS_DIR);
                    if (!dir.exists()) {
                        dir.mkdir();
                    } else {
                        FileUtil.deleteFiles(Constant.AFTER_COMPRESS_DIR);
                    }
                    activity.mSelectLocalPath = Constant.AFTER_COMPRESS_DIR + System.currentTimeMillis() + ".png";
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    final byte[] bytes = baos.toByteArray();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(activity).load(bytes).asBitmap().override(overWidth, overHeight).centerCrop().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    BitmapUtil.saveBitmapAsJpg(resource, activity.mSelectLocalPath);
                                }
                            });
                        }
                    });
                    return showBmp;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mWeakAty.get() != null && bitmap != null) {
                UploadActivity activity = mWeakAty.get();
                activity.mUploadIv.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mType.equals("1") && TextUtils.isEmpty(mFileId)) {//视频
                ToastUtil.showToast(getApplicationContext(), R.string.video_uploading);
                return true;
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 显示收费标准dialog
     */
    private void showChargeOptionDialog(int position) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog, position);
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
     * 设置 dialog view
     */
    private void setDialogView(View view, final Dialog mDialog, final int passPosition) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        TextView title_tv = view.findViewById(R.id.title_tv);

        final List<String> beans = new ArrayList<>();
        switch (passPosition) {
            case VIDEO: {
                String content = getResources().getString(R.string.private_video) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mVideoStrs));
                break;
            }
            case PICTURE: {
                String content = getResources().getString(R.string.private_image) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mPictureStrs));
                break;
            }
        }

        SetChargeRecyclerAdapter adapter = new SetChargeRecyclerAdapter(this);
        final RecyclerView content_rv = view.findViewById(R.id.content_rv);
        final PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(getApplicationContext(),
                content_rv, PickerLayoutManager.VERTICAL, false, 5, 0.4f, true);
        content_rv.setLayoutManager(pickerLayoutManager);
        content_rv.setAdapter(adapter);
        adapter.loadData(beans);
        pickerLayoutManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                LogUtil.i("位置: " + position);
                if (position < beans.size()) {
                    mSelectContent = beans.get(position);
                }
            }
        });

        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (passPosition) {
                    case VIDEO: {
                        if (TextUtils.isEmpty(mSelectContent)) {
                            mSelectContent = mVideoStrs[0];
                        }
                        mMoneyTv.setText(mSelectContent);
                        mSelectContent = "";
                        break;
                    }
                    case PICTURE: {
                        if (TextUtils.isEmpty(mSelectContent)) {
                            mSelectContent = mPictureStrs[0];
                        }
                        mMoneyTv.setText(mSelectContent);
                        mSelectContent = "";
                        break;
                    }
                }
                mDialog.dismiss();
            }
        });
    }
}
