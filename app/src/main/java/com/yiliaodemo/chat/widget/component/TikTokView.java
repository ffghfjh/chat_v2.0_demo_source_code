package com.yiliaodemo.chat.widget.component;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.Tiktok2Adapter;
import com.yiliaodemo.chat.adapter.VideoAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.listener.SingleDoubleClickListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

public class TikTokView extends FrameLayout implements IControlComponent {

    private ImageView thumb;
    private ImageView mPlayBtn;

    public ControlWrapper mMediaPlayer;
    private int mScaledTouchSlop;
    private int mStartX, mStartY;


    public TikTokView(@NonNull Context context) {
        super(context);
    }

    public TikTokView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TikTokView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_actor_video_play_layout, this, true);
        thumb = findViewById(R.id.iv_thumb);
        mPlayBtn = findViewById(R.id.play_btn);
        setOnTouchListener(new SingleDoubleClickListener(new SingleDoubleClickListener.MyClickCallBack() {
            @Override
            public void oneClick() {
                mMediaPlayer.togglePlay();
            }

            @Override
            public void doubleClick() {
                TextView mLoveTv = getView().findViewById(R.id.love_tv);
                if (Tiktok2Adapter.viewHolder.mLoveTv.isSelected()) {//没有点赞过
                    addLike(getUserId(),Tiktok2Adapter.mActorId,Tiktok2Adapter.viewHolder.mLoveTv);
                } else {
                    cancelLike(getUserId(),Tiktok2Adapter.mActorId,Tiktok2Adapter.viewHolder.mLoveTv);
                }
            }
        }));
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    /**
     * 获取UserId
     */
    public String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(getContext().getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }


    /**
     * 点赞
     */
    private void addLike(String userId, int actourId, TextView mLoveTv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("coverLaudUserId", String.valueOf(actourId));
        OkHttpUtils.post().url(ChatApi.ADD_LAUD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Tiktok2Adapter.viewHolder.mLoveTv.setSelected(true);
                    String content = mLoveTv.getText().toString().trim();
                    int number = Integer.parseInt(content) + 1;
                    mLoveTv.setText(String.valueOf(number));
                }
            }
        });
    }
    /**
     * 取消点赞
     */
    private void cancelLike(String userId, int actourId, TextView mLoveTv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("coverUserId", String.valueOf(actourId));
        OkHttpUtils.post().url(ChatApi.CANCEL_LAUD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Tiktok2Adapter.viewHolder.mLoveTv.setSelected(false);
                    String content = mLoveTv.getText().toString().trim();
                    int number = Integer.parseInt(content) - 1;
                    mLoveTv.setText(String.valueOf(number));
                }
            }
        });
    }
    

    /**
     * 解决点击和VerticalViewPager滑动冲突问题
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                if (Math.abs(endX - mStartX) < mScaledTouchSlop
                        && Math.abs(endY - mStartY) < mScaledTouchSlop) {
                    performClick();
                }
                break;
        }
        return false;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mMediaPlayer = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }


    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
                L.e("STATE_IDLE " + hashCode());
                thumb.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING " + hashCode());
                thumb.setVisibility(GONE);
                mPlayBtn.setVisibility(GONE);
                break;
            case VideoView.STATE_PAUSED:
                L.e("STATE_PAUSED " + hashCode());
                thumb.setVisibility(GONE);
                mPlayBtn.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED " + hashCode());
                break;
            case VideoView.STATE_ERROR:
                L.e("STATE_ERROR " + hashCode());
                Toast.makeText(getContext(), R.string.dkplayer_error_message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }



    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
