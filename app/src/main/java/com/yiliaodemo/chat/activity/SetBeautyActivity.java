package com.yiliaodemo.chat.activity;

import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wushuangtech.bean.TTTVideoFrame;
import com.wushuangtech.library.Constants;
import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.model.VideoCanvas;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;


import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_ANCHOR;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_BAD_VERSION;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_TIMEOUT;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_UNKNOW;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_VERIFY_FAILED;
import static com.wushuangtech.library.Constants.LOG_FILTER_OFF;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：美颜
 * 作者：
 * 创建时间：2018/7/24
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SetBeautyActivity extends BaseActivity {

    @BindView(R.id.content_fl)
    ConstraintLayout mContentFl;

//    //美颜
//    private TiSDKManager mTiSDKManager;
    //TTT视频聊天相关
    private TTTRtcEngine mTttRtcEngine;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_set_beauty_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initStart();
    }

    /**
     * 初始化
     */
    private void initStart() {
//        mTiSDKManager = new TiSDKManager();
//        addContentView(new TiPanelLayout(this).init(mTiSDKManager),
//                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));

        mTttRtcEngine = TTTRtcEngine.create(getApplicationContext(), Constant.TTT_APP_ID, true,
                new TTTRtcEngineEventHandler() {
                    @Override
                    public void onError(int errorType) {
                        super.onError(errorType);
                        if (errorType == ERROR_ENTER_ROOM_TIMEOUT) {
                            LogUtil.i("超时，10秒未收到服务器返回结果");
                        } else if (errorType == ERROR_ENTER_ROOM_UNKNOW) {
                            LogUtil.i("无法连接服务器");
                        } else if (errorType == ERROR_ENTER_ROOM_VERIFY_FAILED) {
                            LogUtil.i("验证码错误");
                        } else if (errorType == ERROR_ENTER_ROOM_BAD_VERSION) {
                            LogUtil.i("版本错误");
                        } else if (errorType == 6) {
                            LogUtil.i("该直播间不存在");
                        }
                    }

                    @Override
                    public void onLocalVideoFrameCaptured(TTTVideoFrame frame) {
                        super.onLocalVideoFrameCaptured(frame);
//                        if (mTiSDKManager != null) {
//                            frame.textureID = mTiSDKManager.renderTexture2D(frame.textureID, frame.stride,
//                                    frame.height, TiRotation.CLOCKWISE_ROTATION_0, true);
//                        }
                    }

                });
        if (mTttRtcEngine != null) {
            mTttRtcEngine.setLogFilter(LOG_FILTER_OFF);
            mTttRtcEngine.enableVideo();
            mTttRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
            mTttRtcEngine.startPreview();

            delayShow();
        }
    }

    /**
     * 延时显示
     */
    private void delayShow() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    SurfaceView mLocalSurfaceView = mTttRtcEngine.CreateRendererView(SetBeautyActivity.this);
                    mTttRtcEngine.setupLocalVideo(new VideoCanvas(Integer.parseInt(getUserId()),
                            Constants.RENDER_MODE_HIDDEN, mLocalSurfaceView), getRequestedOrientation());
                    mContentFl.addView(mLocalSurfaceView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10);
    }


    @OnClick({R.id.finish_iv, R.id.switch_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_iv: {//关闭
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;
                }
                finish();
                break;
            }
            case R.id.switch_iv: {//切换
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.switchCamera();
                }
                break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mTiSDKManager != null) {
//            mTiSDKManager.destroy();
//        }
    }

}
