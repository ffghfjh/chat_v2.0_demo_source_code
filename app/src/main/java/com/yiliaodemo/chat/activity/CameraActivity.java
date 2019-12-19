package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.util.BitmapUtil;
import com.yiliaodemo.chat.util.ToastUtil;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    private JCameraView jCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                ToastUtil.showToast(getApplicationContext(), R.string.get_permission_fail);
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                ToastUtil.showToast(getApplicationContext(), R.string.get_permission_fail);
            }

        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                File pFile = new File(com.yiliaodemo.chat.util.FileUtil.YCHAT_DIR);
                if (!pFile.exists()) {
                    boolean res = pFile.mkdir();
                    if (!res) {
                        return;
                    }
                }
                File file = new File(Constant.ACTIVE_IMAGE_DIR);
                if (!file.exists()) {
                    boolean res = file.mkdir();
                    if (!res) {
                        return;
                    }
                }
                String jpegName = Constant.ACTIVE_IMAGE_DIR + File.separator + System.currentTimeMillis() + ".jpg";
                File localFile = BitmapUtil.saveBitmapAsJpg(bitmap, jpegName);
                if (localFile != null) {
                    Intent intent = new Intent();
                    intent.putExtra("imagePath", localFile.getAbsolutePath());
                    setResult(101, intent);
                    finish();
                }
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                File pFile = new File(com.yiliaodemo.chat.util.FileUtil.YCHAT_DIR);
                if (!pFile.exists()) {
                    boolean res = pFile.mkdir();
                    if (!res) {
                        return;
                    }
                }
                File file = new File(Constant.ACTIVE_IMAGE_DIR);
                if (!file.exists()) {
                    boolean res = file.mkdir();
                    if (!res) {
                        return;
                    }
                }
                String jpegName = Constant.ACTIVE_IMAGE_DIR + File.separator + System.currentTimeMillis() + ".jpg";
                File localFile = BitmapUtil.saveBitmapAsJpg(firstFrame, jpegName);
                if (localFile != null) {
                    Intent intent = new Intent();
                    intent.putExtra("imagePath", localFile.getAbsolutePath());
                    intent.putExtra("videoUrl", url);
                    setResult(102, intent);
                    finish();
                }

            }
        });
        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

}
