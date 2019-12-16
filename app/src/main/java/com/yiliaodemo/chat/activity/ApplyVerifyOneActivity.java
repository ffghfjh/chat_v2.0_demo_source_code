package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.oss.QServiceCfg;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.FileUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
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
 * 功能描述   申请认证页面
 * 作者：
 * 创建时间：2018/6/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ApplyVerifyOneActivity extends BaseActivity {

    @BindView(R.id.head_img_iv)
    ImageView mHeadImgIv;
    @BindView(R.id.self_iv)
    ImageView mSelfIv;

    private String mSelectIdCardLocalPath;
    private String mSelectSelfLocalPath;

    private final int REQUEST_CODE_ID_CARD = 0x100;
    private final int REQUEST_CODE_SELF = 0x200;
    private final int UCROP_ID_CARD = 0x300;
    private final int UCROP_SELF = 0x400;

    //腾讯云
    private QServiceCfg mQServiceCfg;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_verify_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.apply_verify);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
    }

    @OnClick({R.id.head_img_iv, R.id.submit_now_tv, R.id.agree_tv, R.id.self_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_img_iv: {//上传身份证头像
                //图片选择8
                ImageHelper.openPictureChoosePage(ApplyVerifyOneActivity.this, REQUEST_CODE_ID_CARD);
                break;
            }
            case R.id.self_iv: {//本人正面
                //图片选择
                ImageHelper.openPictureChoosePage(ApplyVerifyOneActivity.this, REQUEST_CODE_SELF);
                break;
            }
            case R.id.submit_now_tv: {//提交认证
                submitVerify();
                break;
            }
            case R.id.agree_tv: {//绿色协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.green_agree));
                intent.putExtra(Constant.URL, "file:///android_asset/green.html");
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 提交认证
     */
    private void submitVerify() {
        Log.d("idcard","主播申请点击");
        //本人身份证正面照片
        if (TextUtils.isEmpty(mSelectIdCardLocalPath)) {
            ToastUtil.showToast(getApplicationContext(), R.string.id_card_picture_not_choose);
            return;
        }
        File file = new File(mSelectIdCardLocalPath);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }
        //本人正面照片
        if (TextUtils.isEmpty(mSelectSelfLocalPath)) {
            ToastUtil.showToast(getApplicationContext(), R.string.self_picture_not_choose);
            return;
        }
        File selfFile = new File(mSelectSelfLocalPath);
        if (!selfFile.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }

        uploadIdCardFileWithQQ();
    }

    /**
     * 上传认证图到腾讯云
     * 先上传身份证反面
     */
    private void uploadIdCardFileWithQQ() {
        Log.d("idcard","主播申请：反面：WithQQ");
        File file = new File(mSelectIdCardLocalPath);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
            return;
        }
        String fileName = mSelectIdCardLocalPath.substring(mSelectIdCardLocalPath.length() - 17, mSelectIdCardLocalPath.length());
        String cosPath = "/verify/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, mSelectIdCardLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d("idcard","主播申请：WithQQ:成功");
                LogUtil.i("身份证正面 认证=  " + result.accessUrl);
                String mVerifyImageHttpUrl = result.accessUrl;
                if (!mVerifyImageHttpUrl.contains("http") || !mVerifyImageHttpUrl.contains("https")) {
                    mVerifyImageHttpUrl = "https://" + mVerifyImageHttpUrl;
                }
                uploadSelfFileWithQQ(mVerifyImageHttpUrl);
            }


            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                Log.d("idcard","主播申请：WithQQ:失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                    }
                });
            }
        });
    }

    /**
     * 再上传本人正面
     */
    private void uploadSelfFileWithQQ(final String idCardUrl) {
        Log.d("idcard","主播申请：正面 ：WithQQ");
        File file = new File(mSelectSelfLocalPath);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
            return;
        }
        String fileName = mSelectSelfLocalPath.substring(mSelectSelfLocalPath.length() - 17, mSelectSelfLocalPath.length());
        String cosPath = "/verify/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, mSelectSelfLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("本人正面=  " + result.accessUrl);
                String mVerifyImageHttpUrl = result.accessUrl;
                if (!mVerifyImageHttpUrl.contains("http") || !mVerifyImageHttpUrl.contains("https")) {
                    mVerifyImageHttpUrl = "https://" + mVerifyImageHttpUrl;
                }
                uploadVerifyInfo(idCardUrl, mVerifyImageHttpUrl);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                    }
                });
            }
        });
    }

    /**
     * 上传认证信息到公司服务器
     */
    private void uploadVerifyInfo(String idCardUrl, String selfUrl) {
        Log.d("idcard","主播申请");
        if (TextUtils.isEmpty(idCardUrl) || TextUtils.isEmpty(selfUrl)) {
            ToastUtil.showToast(getApplicationContext(), R.string.choose_again);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_user_photo", selfUrl);//本人
        paramMap.put("t_user_video", idCardUrl);//身份证正面
        OkHttpUtils.post().url(ChatApi.SUBMIT_VERIFY_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                Log.d("idcard","主播申请成功回调");
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(), R.string.verify_success);
                        Intent intent = new Intent(getApplicationContext(), ActorVerifyingActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d("idcard","主播申请失败");
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ID_CARD && resultCode == RESULT_OK) {//身份证
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "身份证mSelected: " + mSelectedUris);
            dealFile(mSelectedUris, true);
        } else if (requestCode == REQUEST_CODE_SELF && resultCode == RESULT_OK) {//本人
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "本人正面mSelected: " + mSelectedUris);
            dealFile(mSelectedUris, false);
        } else if (resultCode == RESULT_OK && (requestCode == UCROP_ID_CARD || requestCode == UCROP_SELF)) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                int resizeWidth = DevicesUtil.dp2px(getApplicationContext(), 179);
                int resizeHeight = DevicesUtil.dp2px(getApplicationContext(), 115);
                if (requestCode == UCROP_ID_CARD) {//身份证
                    ImageLoadHelper.glideShowImageWithUri(ApplyVerifyOneActivity.this, resultUri,
                            mHeadImgIv, resizeWidth, resizeHeight);
                    mSelectIdCardLocalPath = resultUri.getPath();
                } else {
                    ImageLoadHelper.glideShowImageWithUri(ApplyVerifyOneActivity.this, resultUri,
                            mSelfIv, resizeWidth, resizeHeight);
                    mSelectSelfLocalPath = resultUri.getPath();
                }
            }
            //用完之后应该删除
        } else if (resultCode == UCrop.RESULT_ERROR) {
            ToastUtil.showToast(getApplicationContext(), R.string.choose_picture_failed);
        }
    }

    /**
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     *
     * @param fromIdCard true 是身份证正面
     */
    private void dealFile(List<Uri> mSelectedUris, boolean fromIdCard) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                Uri uri = mSelectedUris.get(0);
                String filePath = FileUtil.getPathAbove19(this, uri);
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    LogUtil.i("==--", "file大小: " + file.length() / 1024);
                    //压缩图片
                    cutWithUCrop(uri, fromIdCard);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用u crop裁剪
     *
     * @param fromIdCard true 是身份证正面
     */
    private void cutWithUCrop(Uri sourceUri, boolean fromIdCard) {
        //计算 图片裁剪的大小
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                return;
            }
        }
        File file = new File(Constant.VERIFY_AFTER_RESIZE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                return;
            }
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        //请求码
        int requestCode;
        if (fromIdCard) {
            requestCode = UCROP_ID_CARD;
        } else {
            requestCode = UCROP_SELF;
        }
        UCrop.of(sourceUri, Uri.fromFile(new File(filePath)))
                .withAspectRatio(1280, 960)
                .withMaxResultSize(1280, 960)
                .start(this, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }

}
