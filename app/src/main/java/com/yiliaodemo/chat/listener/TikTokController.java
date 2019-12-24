package com.yiliaodemo.chat.listener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.yiliaodemo.chat.R;

/**
 * 抖音
 * Created by Ryan 2019/6/5
 */

public class TikTokController extends GestureVideoController implements View.OnClickListener{

    private ImageView stopBtn;
    private RelativeLayout bgBtn;
    private VideoView videoView;
    public TikTokController(@NonNull Context context) {
        super(context);
    }

    public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dy_controller_layout;
    }

    public void setPlayViewStatus(boolean isPause){
        if (isPause){
            stopBtn.setVisibility(VISIBLE);
        }else {
            stopBtn.setVisibility(GONE);
        }
    }

    @Override
    public void show() {
        if (!mShowing) {
            if (videoView.isFullScreen()) {
                if (!mIsLocked) {
                    showAllViews();
                }
            } else {
                bgBtn.setVisibility(View.VISIBLE);
                doPauseResume();
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
    }

    private void showAllViews() {
        stopBtn.setVisibility(View.VISIBLE);
        doPauseResume();
    }


    @Override
    protected void initView() {
        super.initView();
        stopBtn = findViewById(R.id.paly_stop);
        bgBtn = findViewById(R.id.video_bg);
        videoView = findViewById(R.id.video_view);
        stopBtn.setOnClickListener(this);
        bgBtn.setOnClickListener(this);
    }

    protected void doPauseResume() {

        if (videoView.isPlaying()) {
            stopBtn.setVisibility(VISIBLE);
            videoView.pause();
        } else {
            stopBtn.setVisibility(GONE);
            videoView.start();
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case VideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                bgBtn.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                bgBtn.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.video_bg){
            doPauseResume();
        }else if (v.getId() == R.id.comment_ll){
            Log.e("ryan","点击了");
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            if (videoView.isFullScreen()) {
                if (!mIsLocked) {
                    hideAllViews();
                }
            } else {
                bgBtn.setVisibility(View.GONE);
                doPauseResume();
            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        stopBtn.setVisibility(View.GONE);
        doPauseResume();
    }
}