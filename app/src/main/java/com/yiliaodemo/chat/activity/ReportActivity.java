package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageHelper;
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
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
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
 * 功能描述：投诉举报页面
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ReportActivity extends BaseActivity {

    @BindView(R.id.can_not_iv)
    ImageView mCanNotIv;
    @BindView(R.id.bad_attitude_iv)
    ImageView mBadAttitudeIv;
    @BindView(R.id.bad_adver_iv)
    ImageView mBadAdverIv;
    @BindView(R.id.sex_iv)
    ImageView mSexIv;
    @BindView(R.id.against_low_iv)
    ImageView mAgainstLowIv;
    @BindView(R.id.evidence_ll)
    LinearLayout mEvidenceLl;//证据Ll

    private int mActorId;//主播id
    //腾讯云
    private QServiceCfg mQServiceCfg;
    private List<String> mSelectFilePath = new ArrayList<>();
    private String mCoverImageHttpUrl = "";//拼接图片url
    private List<String> mSelectReason = new ArrayList<>();//理由

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_report_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.report);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
    }

    @OnClick({R.id.can_not_rl, R.id.bad_attitude_rl, R.id.bad_adver_rl, R.id.sex_rl,
            R.id.against_low_rl, R.id.upload_iv, R.id.report_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.can_not_rl: {//黑屏看不到人
                String reason = getResources().getString(R.string.can_not_see);
                setReasonSelected(mCanNotIv, reason);
                break;
            }
            case R.id.bad_attitude_rl: {//态度恶劣
                String reason = getResources().getString(R.string.bad_attitude);
                setReasonSelected(mBadAttitudeIv, reason);
                break;
            }
            case R.id.bad_adver_rl: {//垃圾广告
                String reason = getResources().getString(R.string.bad_adver);
                setReasonSelected(mBadAdverIv, reason);
                break;
            }
            case R.id.sex_rl: {//淫秽色情
                String reason = getResources().getString(R.string.sex);
                setReasonSelected(mSexIv, reason);
                break;
            }
            case R.id.against_low_rl: {//违法行为
                String reason = getResources().getString(R.string.against_low);
                setReasonSelected(mAgainstLowIv, reason);
                break;
            }
            case R.id.upload_iv: {//上传证据
                //判断是否多于4张
                if (mEvidenceLl.getChildCount() >= 4) {
                    ToastUtil.showToast(getApplicationContext(), R.string.four_most);
                    return;
                }
                //图片选择
                ImageHelper.openPictureChoosePage(ReportActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            }
            case R.id.report_tv: {
                if (mSelectReason == null || mSelectReason.size() <= 0) {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_select_reason);
                    return;
                }

                showLoadingDialog();
                if (mSelectFilePath == null || mSelectFilePath.size() <= 0) {
                    complainUser();
                } else {
                    uploadReportFileWithQQ(0, new OnFileUploadListener() {
                        @Override
                        public void onFileUploadSuccess() {
                            complainUser();
                        }
                    });
                }
                break;
            }
        }
    }

    /**
     * 投诉举报
     */
    private void complainUser() {
        //理由
        StringBuilder comment = new StringBuilder();
        for (String content : mSelectReason) {
            comment.append(content + ",");
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverUserId", String.valueOf(mActorId));
        paramMap.put("comment", comment.toString());
        paramMap.put("img", mCoverImageHttpUrl);
        OkHttpUtils.post().url(ChatApi.SAVE_COMPLAINT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 300);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.complain_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.complain_fail);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 设置选中为选中
     */
    private void setReasonSelected(ImageView imageView, String reason) {
        if (imageView != null) {
            if (imageView.isSelected()) {
                imageView.setSelected(false);
                mSelectReason.remove(reason);
            } else {
                imageView.setSelected(true);
                mSelectReason.add(reason);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "mSelected: " + mSelectedUris);
            dealFile(mSelectedUris);
        }
    }

    /**
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealFile(List<Uri> mSelectedUris) {
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
        ImageHelper.compressImageWithLuBanNotDelete(getApplicationContext(), filePath, Constant.REPORT_DIR, new OnLuCompressListener() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(File file) {
                mSelectFilePath.add(file.getAbsolutePath());
                Bitmap bitmap = BitmapUtil.getImageThumbnail(file.getAbsolutePath(),
                        DevicesUtil.dp2px(getApplicationContext(), 50), DevicesUtil.dp2px(getApplicationContext(), 50));
                addImageToLinearLayout(bitmap);
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
     * 添加imageView到Ll布局
     */
    private void addImageToLinearLayout(Bitmap bitmap) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DevicesUtil.dp2px(getApplicationContext(), 50),
                DevicesUtil.dp2px(getApplicationContext(), 50));
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(bitmap);
        mEvidenceLl.addView(imageView);
        mEvidenceLl.setVisibility(View.VISIBLE);
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadReportFileWithQQ(final int index, final OnFileUploadListener listener) {
        String filePath = mSelectFilePath.get(index);
        //文件名
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

        String cosPath = "/report/" + fileName;
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
                mCoverImageHttpUrl += resultUrl + ",";
                if (mSelectFilePath != null) {
                    if (mSelectFilePath.size() > index + 1) {//如果还有下一张,就继续上传
                        uploadReportFileWithQQ(index + 1, listener);
                    } else {//没有下一张了就上传头像
                        listener.onFileUploadSuccess();
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                ToastUtil.showToast(getApplicationContext(), R.string.choose_picture_failed);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.REPORT_DIR);
    }

}
