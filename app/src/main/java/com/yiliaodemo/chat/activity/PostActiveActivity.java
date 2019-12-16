package com.yiliaodemo.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.yiliaodemo.chat.adapter.PostActiveRecyclerAdapter;
import com.yiliaodemo.chat.adapter.SetChargeRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActiveLocalBean;
import com.yiliaodemo.chat.bean.PostFileBean;
import com.yiliaodemo.chat.bean.VideoRetrieverBean;
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
 * 功能描述：发布动态页面
 * 作者：
 * 创建时间：2018/12/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PostActiveActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.video_fl)
    FrameLayout mVideoFl;
    @BindView(R.id.video_iv)
    ImageView mVideoIv;
    @BindView(R.id.see_iv)
    ImageView mSeeIv;
    @BindView(R.id.see_tv)
    TextView mSeeTv;
    @BindView(R.id.focus_iv)
    ImageView mFocusIv;
    @BindView(R.id.focus_tv)
    TextView mFocusTv;
    @BindView(R.id.video_charge_tv)
    TextView mVideoChargeTv;
    @BindView(R.id.content_et)
    EditText mContentEt;
    @BindView(R.id.upload_fl)
    FrameLayout mUploadFl;
    @BindView(R.id.process_pv)
    MyProcessView mProcessPv;
    @BindView(R.id.video_done_tv)
    TextView mVideoDoneTv;
    @BindView(R.id.where_tv)
    TextView mWhereTv;
    @BindView(R.id.where_iv)
    ImageView mWhereIv;

    private PostActiveRecyclerAdapter mAdapter;
    //只用于图片
    private List<ActiveLocalBean> mLocalBeans = new ArrayList<>();

    private final int REQUEST_ALBUM_IMAGE_VIDEO = 0x100;//相册请求图片和视频
    private final int REQUEST_ALBUM_IMAGE = 0x111;//相册请求图片
    //视频相关
    //只用于视频的封面预览图
    private String mSelectedLocalVideoThumbPath = "";
    private String mVideoFileId = "";
    private String mVideoFileUrl = "";
    private String mVideoTime = "";
    //收费
    private final int VIDEO = 0;
    private final int PICTURE = 1;
    private String[] mVideoStrs = new String[]{};
    private String[] mPictureStrs = new String[]{};
    private String mSelectContent = "";
    private int mPostType = PICTURE;//动态发布的是图片还是视频,默认图片
    private final int CAMERA_REQUEST_CODE = 0x116;
    private final int REQUEST_SELECT_POSITION = 0x180;

    //腾讯点播 视频上传
    private TXUGCPublish mVideoPublish = null;
    private QServiceCfg mQServiceCfg;//图片

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_post_active_layout);
    }

    @Override
    protected void onContentAdded() {
        initTitle();
        initRecycler();
        getAnchorVideoCost();
        getPrivatePhotoMoney();
        dealPassUri();
    }

    /**
     * 如果是带参数进来的,处理
     */
    private void dealPassUri() {
        //默认从其他点进来,什么操作都不做
        int postFrom = getIntent().getIntExtra(Constant.POST_FROM, 0);
        int type = getIntent().getIntExtra(Constant.PASS_TYPE, Constant.TYPE_IMAGE);
        if (postFrom == 1) {//从拍摄点进来
            //图片
            if (type == Constant.TYPE_IMAGE) {
                String passImageUrl = getIntent().getStringExtra(Constant.POST_IMAGE_PATH);
                LogUtil.i("传递过来的图片Path: " + passImageUrl);
                if (!TextUtils.isEmpty(passImageUrl)) {
                    mPostType = PICTURE;
                    compressImageWithLuBan(passImageUrl);
                }
            } else if (type == Constant.TYPE_VIDEO) {//视频
                String videoUrl = getIntent().getStringExtra(Constant.POST_VIDEO_URL);
                if (!TextUtils.isEmpty(videoUrl)) {
                    mPostType = VIDEO;
                    dealShootVideoFile(videoUrl);
                }
            }
        } else if (postFrom == 2) {//从相册点进来
            //图片
            String postFileUri = getIntent().getStringExtra(Constant.POST_FILE_URI);
            Uri uri = Uri.parse(postFileUri);
            if (type == Constant.TYPE_IMAGE) {
                LogUtil.i("传递过来的Uri: " + postFileUri);
                if (uri != null) {
                    mPostType = PICTURE;
                    dealImageFile(uri);
                }
            } else if (type == Constant.TYPE_VIDEO) {//视频
                if (uri != null) {
                    mPostType = VIDEO;
                    dealVideoFile(uri);
                }
            }
        }
    }

    /**
     * 设置标题栏
     */
    private void initTitle() {
        setTitle(R.string.post_active);
        setRightText(R.string.post);
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setBackgroundResource(R.drawable.shape_post_text_back);
        mRightTv.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightTv.getLayoutParams();
        params.rightMargin = DevicesUtil.dp2px(getApplicationContext(), 15);

        mSeeIv.setSelected(true);
        mSeeTv.setSelected(true);

        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
    }

    /**
     * 初始化recycler
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mContentRv.setNestedScrollingEnabled(false);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new PostActiveRecyclerAdapter(PostActiveActivity.this);
        mContentRv.setAdapter(mAdapter);
        mAdapter.setOnPostItemClickListener(new PostActiveRecyclerAdapter.OnPostItemClickListener() {
            @Override
            public void onAddClick(int position) {
                showChooseDialog();
            }

            @Override
            public void onDeleteClick(ActiveLocalBean bean, int position) {
                try {
                    if (bean != null && !TextUtils.isEmpty(bean.localPath)) {
                        File file = new File(bean.localPath);
                        boolean result = file.delete();
                        if (result) {//本地删除后
                            //如果是9条数据,且最后一条不为空
                            int size = mLocalBeans.size();
                            if (size >= 9 && !TextUtils.isEmpty(mLocalBeans.get(size - 1).localPath)) {
                                mLocalBeans.remove(position);
                                mLocalBeans.add(new ActiveLocalBean());
                            } else {
                                mLocalBeans.remove(position);
                            }
                            mAdapter.loadData(mLocalBeans);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.delete_fail);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(getApplicationContext(), R.string.delete_fail);
                }
            }

            @Override
            public void onFreeClick(ActiveLocalBean bean, int position) {
                showChargeOptionDialog(PICTURE, position);
            }
        });

        //先添加一条空的  显示加的图片
        mLocalBeans.add(new ActiveLocalBean());
        mAdapter.loadData(mLocalBeans);
    }

    @OnClick({R.id.video_delete_iv, R.id.video_charge_tv, R.id.see_ll, R.id.focus_ll, R.id.right_text,
            R.id.where_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_delete_iv: {//视频删除
                mContentRv.setVisibility(View.VISIBLE);
                mVideoFl.setVisibility(View.GONE);
                mSelectedLocalVideoThumbPath = "";
                mVideoChargeTv.setText(getResources().getString(R.string.free_one));
                mVideoFileId = "";
                mVideoFileUrl = "";
                mVideoTime = "";
                break;
            }
            case R.id.video_charge_tv: {//视频收费
                showChargeOptionDialog(VIDEO, 0);
                break;
            }
            case R.id.see_ll: {//全部人可见
                if (mSeeIv.isSelected() || mSeeTv.isSelected()) {
                    return;
                }
                mSeeIv.setSelected(true);
                mSeeTv.setSelected(true);
                mFocusIv.setSelected(false);
                mFocusTv.setSelected(false);
                break;
            }
            case R.id.focus_ll: {//关注人可见
                if (mFocusIv.isSelected() || mFocusTv.isSelected()) {
                    return;
                }
                mFocusIv.setSelected(true);
                mFocusTv.setSelected(true);
                mSeeIv.setSelected(false);
                mSeeTv.setSelected(false);
                break;
            }
            case R.id.right_text: {//发布
                postActive();
                break;
            }
            case R.id.where_ll: {//选择位置
                Intent intent = new Intent(getApplicationContext(), SelectPositionActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_POSITION);
                break;
            }
        }
    }

    /**
     * 发布视频
     */
    private void postActive() {
        //文字
        String textContent = mContentEt.getText().toString().trim();
        //校验
        //如果图片和视频都为空
        if (mLocalBeans.size() == 1 && TextUtils.isEmpty(mSelectedLocalVideoThumbPath)
                && TextUtils.isEmpty(textContent)) {
            ToastUtil.showToast(getApplicationContext(), R.string.set_content);
            return;
        }

        showLoadingDialog();
        //上传视频
        if (mPostType == VIDEO) {
            if (!TextUtils.isEmpty(mSelectedLocalVideoThumbPath)) {
                uploadVideoCoverFileWithQQ(mSelectedLocalVideoThumbPath);
            }
        } else {//图片,默认就是图片,所以空文字应该也是走这里
            //如果有图片
            if (mLocalBeans != null && mLocalBeans.size() > 1) {
                uploadImageFileWithQQ(0, new OnFileUploadListener() {
                    @Override
                    public void onFileUploadSuccess() {
                        List<PostFileBean> postFileBeans = new ArrayList<>();
                        for (ActiveLocalBean bean : mLocalBeans) {
                            if (!TextUtils.isEmpty(bean.imageUrl)) {
                                int gold = bean.gold;
                                PostFileBean fileBean = new PostFileBean();
                                fileBean.fileType = 0;
                                fileBean.fileUrl = bean.imageUrl;
                                fileBean.gold = gold;
                                if (gold > 0) {
                                    fileBean.t_is_private = 1;
                                }
                                postFileBeans.add(fileBean);
                            }
                        }
                        addToOurActive(postFileBeans);
                    }
                });
            } else {
                addToOurActive(null);
            }
        }
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadImageFileWithQQ(final int index, final OnFileUploadListener listener) {
        String filePath = mLocalBeans.get(index).localPath;
        if (TextUtils.isEmpty(filePath)) {//如果为空,表示是最后一张了
            listener.onFileUploadSuccess();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            uploadImageFileWithQQ(index + 1, listener);
            return;
        }
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

        String cosPath = "/active/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, filePath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云动态success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }
                if (mLocalBeans != null) {
                    //给ActiveLocalBean设置地址
                    if (mLocalBeans.size() >= index + 1) {
                        ActiveLocalBean localBean = mLocalBeans.get(index);
                        localBean.imageUrl = resultUrl;
                    }
                    //如果还有下一张,就继续上传,且最后一张不为空
                    if (mLocalBeans.size() > index + 1
                            && !TextUtils.isEmpty(mLocalBeans.get(index + 1).localPath)) {
                        uploadImageFileWithQQ(index + 1, listener);
                    } else {
                        listener.onFileUploadSuccess();
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
            }
        });
    }

    /**
     * 使用腾讯云上传图片文件
     */
    private void uploadVideoCoverFileWithQQ(String filePath) {
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

        String cosPath = "/active/" + fileName;
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

                int gold = 0;
                if (!mVideoChargeTv.getText().toString().trim().equals(getResources().getString(R.string.free_one))) {
                    gold = Integer.parseInt(mVideoChargeTv.getText().toString());
                }
                List<PostFileBean> fileBeans = new ArrayList<>();
                PostFileBean bean = new PostFileBean();
                bean.fileId = mVideoFileId;
                bean.fileType = 1;
                bean.fileUrl = mVideoFileUrl;
                bean.gold = gold;
                bean.t_video_time = mVideoTime;
                if (gold > 0) {
                    bean.t_is_private = 1;
                }
                bean.t_cover_img_url = resultUrl;
                fileBeans.add(bean);
                addToOurActive(fileBeans);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        ToastUtil.showToast(getApplicationContext(), R.string.post_fail);
                    }
                });
            }
        });
    }

    /**
     * 添加到我们服务器
     */
    private void addToOurActive(List<PostFileBean> fileBeans) {
        //文字
        String textContent = mContentEt.getText().toString().trim();
        //是否可见
        String visible = "0";
        if (mFocusTv.isSelected() || mFocusIv.isSelected()) {
            visible = "1";
        }
        //文件
        Object fileJson = "";
        if (fileBeans != null && fileBeans.size() > 0) {
            fileJson = JSON.toJSON(fileBeans);
        }
        //位置
        String position = mWhereTv.getText().toString().trim();
        if (!TextUtils.isEmpty(position) && position.equals(getResources().getString(R.string.where))) {
            position = "";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("content", TextUtils.isEmpty(textContent) ? "" : textContent);
        paramMap.put("isVisible", visible);//是否可见
        paramMap.put("address", position);//位置
        paramMap.put("files", fileJson);//文件
        OkHttpUtils.post().url(ChatApi.RELEASE_DYNAMIC)
                .addParams("param", ParamUtil.getParam(paramMap,false))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getApplicationContext(), R.string.post_success);
                    finish();
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.post_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.post_fail);
            }
        });
    }

    /**
     * 显示选择dialog
     */
    private void showChooseDialog() {
        final Dialog mDialog = new Dialog(PostActiveActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_choose_image_from_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //拍摄
        TextView shoot_tv = view.findViewById(R.id.shoot_tv);
        shoot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToCamera();
                mDialog.dismiss();
            }
        });
        //相册
        TextView album_tv = view.findViewById(R.id.album_tv);
        album_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果什么都没选
                //选择图片和视频
                if (mLocalBeans != null && mLocalBeans.size() == 1) {
                    ImageHelper.openPictureVideoChoosePage(PostActiveActivity.this, REQUEST_ALBUM_IMAGE_VIDEO);
                } else {
                    //图片选择
                    ImageHelper.openPictureChoosePage(PostActiveActivity.this, REQUEST_ALBUM_IMAGE);
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * 跳转到拍照
     */
    private void jumpToCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(PostActiveActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, 100);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //相册选择    返回可能是图片也可能是视频, 也可能只返回图片
            if (requestCode == REQUEST_ALBUM_IMAGE_VIDEO || requestCode == REQUEST_ALBUM_IMAGE) {
                List<Uri> mSelectedUris = Matisse.obtainResult(data);
                if (mSelectedUris != null && mSelectedUris.size() > 0) {
                    LogUtil.i("选择的: " + mSelectedUris.toString());
                    if (checkUri(mSelectedUris)) {//判断通过
                        Uri fileUri = mSelectedUris.get(0);
                        if (fileUri != null) {
                            //如果是图片
                            if (requestCode == REQUEST_ALBUM_IMAGE || !fileUri.toString().contains("video")) {
                                mPostType = PICTURE;
                                dealImageFile(fileUri);
                            } else {
                                //如果是视频
                                mPostType = VIDEO;
                                dealVideoFile(fileUri);
                            }
                        }
                    }
                }
            } else if (requestCode == REQUEST_SELECT_POSITION) {//选择位置
                if (data != null) {
                    String selectedPosition = data.getStringExtra(Constant.CHOOSED_POSITION);
                    if (!TextUtils.isEmpty(selectedPosition) &&
                            !selectedPosition.equals(getResources().getString(R.string.not_show))) {
                        mWhereTv.setText(selectedPosition);
                        mWhereTv.setTextColor(getResources().getColor(R.color.red_f72b41));
                        mWhereIv.setSelected(true);
                    }
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
            if (resultCode == 101) {//图片
                String imagePath = data.getStringExtra("imagePath");
                LogUtil.i("相机拍照图片:  " + imagePath);
                if (!TextUtils.isEmpty(imagePath)) {
                    mPostType = PICTURE;
                    compressImageWithLuBan(imagePath);
                } else {
                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.file_invalidate);
                }
            } else if (resultCode == 102) {//视频
                String videoUrl = data.getStringExtra("videoUrl");
                LogUtil.i("相机录视频Url:  " + videoUrl);
                if (!TextUtils.isEmpty(videoUrl)) {
                    mPostType = VIDEO;
                    dealShootVideoFile(videoUrl);
                } else {
                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.file_invalidate);
                }
            }
        }
    }

    /**
     * 检查选择的
     */
    private boolean checkUri(List<Uri> mSelectedUris) {
        //判断文件是否无效
        Uri uri = mSelectedUris.get(0);
        if (!checkUriFileExist(uri)) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_invalidate);
            return false;
        }
        return true;
    }

    /**
     * 判断文件
     */
    private boolean checkUriFileExist(Uri uri) {
        if (uri != null) {
            String filePath = FileUtil.getPathAbove19(this, uri);
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    return true;
                } else {
                    LogUtil.i("文件不存在: " + uri.toString());
                    return false;
                }
            }
        }
        return false;
    }


    //-----------------------------处理视频----------------------------------------

    /**
     * 处理拍摄的视频,包括传递的过来的
     */
    private void dealShootVideoFile(String videoUrl) {
        File file = new File(videoUrl);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }
        LogUtil.i("视频大小: " + file.length() / 1024 / 1024);
        double fileSize = (double) file.length() / 1024 / 1024;
        if (fileSize > 50) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_too_big);
            return;
        }
        //获取视频缩略图并显示
        VideoThumbTask task = new VideoThumbTask(PostActiveActivity.this, videoUrl);
        task.execute();
        //获取签名
        getSign(videoUrl);
    }

    /**
     * ---------------------------处理视频文件-------------------------------------
     */
    private void dealVideoFile(Uri videoUri) {
        try {
            String filePath = VideoFileUtils.getRealPathFromUri(this, videoUri);
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                LogUtil.i("视频大小: " + file.length() / 1024 / 1024);
                double fileSize = (double) file.length() / 1024 / 1024;
                if (fileSize > 50) {
                    ToastUtil.showToast(getApplicationContext(), R.string.file_too_big);
                    return;
                }
                showLoadingDialog();

                //获取视频缩略图并显示
                VideoThumbTask task = new VideoThumbTask(PostActiveActivity.this, filePath);
                task.execute();
                //获取签名
                getSign(filePath);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                dismissLoadingDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissLoadingDialog();
        }
    }

    static class VideoThumbTask extends AsyncTask<Integer, Void, Bitmap> {

        private WeakReference<PostActiveActivity> mWeakAty;
        private String mPath;

        VideoThumbTask(PostActiveActivity activity, String path) {
            mWeakAty = new WeakReference<>(activity);
            mPath = path;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            final PostActiveActivity activity = mWeakAty.get();
            if (activity != null) {
                try {
                    final int overWidth = (int) (DevicesUtil.getScreenW(activity) * 0.75);
                    final int overHeight = (int) (DevicesUtil.getScreenH(activity) * 0.75);
                    VideoRetrieverBean retrieverBean = VideoFileUtils.getVideoInfo(mPath);
                    Bitmap bitmap = retrieverBean.bitmap;
                    Bitmap showBmp = VideoFileUtils.getVideoThumbnail(mPath, DevicesUtil.dp2px(activity, 100),
                            DevicesUtil.dp2px(activity, 100), MediaStore.Video.Thumbnails.MICRO_KIND);
                    //保存到本地
                    File pFile = new File(FileUtil.YCHAT_DIR);
                    if (!pFile.exists()) {
                        boolean res = pFile.mkdir();
                        if (!res) {
                            return null;
                        }
                    }
                    File dir = new File(Constant.ACTIVE_VIDEO_DIR);
                    if (!dir.exists()) {
                        boolean res = dir.mkdir();
                        if (!res) {
                            return null;
                        }
                    } else {
                        FileUtil.deleteFiles(Constant.ACTIVE_VIDEO_DIR);
                    }

                    activity.mVideoTime = retrieverBean.videoDuration;
                    activity.mSelectedLocalVideoThumbPath = Constant.ACTIVE_VIDEO_DIR + System.currentTimeMillis() + ".png";
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    final byte[] bytes = baos.toByteArray();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(activity).load(bytes).asBitmap().override(overWidth, overHeight)
                                    .centerCrop().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    BitmapUtil.saveBitmapAsJpg(resource, activity.mSelectedLocalVideoThumbPath);
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
            PostActiveActivity activity = mWeakAty.get();
            if (activity != null && bitmap != null) {//刷新adapter
                activity.mVideoFl.setVisibility(View.VISIBLE);
                activity.mContentRv.setVisibility(View.GONE);
                activity.mVideoIv.setImageBitmap(bitmap);
                activity.dismissLoadingDialog();
            }
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
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = JSON.parseObject(response);
                    int m_istatus = jsonObject.getInteger("m_istatus");
                    if (m_istatus == NetCode.SUCCESS) {
                        String m_object = jsonObject.getString("m_object");
                        if (!TextUtils.isEmpty(m_object)) {
                            //上传文件
                            beginUpload(m_object, filePath);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                    }
                } else {
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
                    if (result.retCode == 0) {//上传成功
                        mVideoDoneTv.setVisibility(View.VISIBLE);
                        mProcessPv.setVisibility(View.INVISIBLE);
                        LogUtil.i("视频文件id: " + result.videoId);
                        LogUtil.i("视频文件url: " + result.videoURL);
                        mVideoFileId = result.videoId;
                        mVideoFileUrl = result.videoURL;
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                            }
                        });
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


    //----------------------------处理图片-------------------------------------------

    /**
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealImageFile(Uri fileUri) {
        try {
            String filePath = FileUtil.getPathAbove19(this, fileUri);
            if (!TextUtils.isEmpty(filePath)) {
                //压缩图片
                compressImageWithLuBan(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用LuBan压缩图片
     */
    private void compressImageWithLuBan(String filePath) {
        ImageHelper.compressImageWithLuBanNotDelete(getApplicationContext(), filePath, Constant.ACTIVE_IMAGE_DIR,
                new OnLuCompressListener() {
                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (!TextUtils.isEmpty(file.getAbsolutePath())) {
                            ActiveLocalBean localBean = new ActiveLocalBean();
                            localBean.type = 0;//图片
                            localBean.localPath = file.getAbsolutePath();

                            if (mLocalBeans != null && mLocalBeans.size() > 0) {
                                int size = mLocalBeans.size();
                                if (size >= 9) {
                                    mLocalBeans.remove(size - 1);//删除最后一条空
                                }
                                mLocalBeans.add(size - 1, localBean);
                                mAdapter.loadData(mLocalBeans);
                            }
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

    /**
     * 显示收费标准dialog
     */
    private void showChargeOptionDialog(int passPosition, int beanPosition) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog, passPosition, beanPosition);
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
    private void setDialogView(View view, final Dialog mDialog, final int passPosition, final int beanPosition) {
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
                        mVideoChargeTv.setText(mSelectContent);
                        mSelectContent = "";
                        break;
                    }
                    case PICTURE: {//图片
                        if (TextUtils.isEmpty(mSelectContent)) {
                            mSelectContent = mPictureStrs[0];
                        }

                        if (mLocalBeans != null && mLocalBeans.size() > beanPosition + 1) {
                            ActiveLocalBean localBean = mLocalBeans.get(beanPosition);
                            localBean.gold = Integer.parseInt(mSelectContent);
                            mAdapter.loadData(mLocalBeans);
                        }

                        mSelectContent = "";
                        break;
                    }
                }
                mDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除post目录中的图片
        try {
            FileUtil.deleteFiles(Constant.ACTIVE_VIDEO_DIR);
            FileUtil.deleteFiles(Constant.ACTIVE_IMAGE_DIR);
            String videoPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera";
            FileUtil.deleteFiles(videoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
