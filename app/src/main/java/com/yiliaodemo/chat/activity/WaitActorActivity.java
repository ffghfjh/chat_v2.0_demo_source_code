package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pili.pldroid.player.widget.PLVideoView;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.CoverUrlBean;
import com.yiliaodemo.chat.bean.LabelBean;
import com.yiliaodemo.chat.bean.PersonBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

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
 * 功能描述：等待主播接通页面
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WaitActorActivity extends BaseActivity {

    @BindView(R.id.invite_by_tv)
    TextView mInviteByTv;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.content_iv)
    ImageView mContentIv;
    @BindView(R.id.camera_ll)
    LinearLayout mCameraLl;
    @BindView(R.id.camera_iv)
    ImageView mCameraIv;
    @BindView(R.id.camera_tv)
    TextView mCameraTv;
    @BindView(R.id.video_view)
    PLVideoView mVideoView;

    private int mPassUserId;//对方的id
    private int mRoomId;//房间id
    private MediaPlayer mPlayer;
    private String mNickName = "";
    private String mHandImg = "";//头像
    private int mSatisfy;//是否有足够的钱,如果不为0就表示是用户受到邀请进来
    //30秒计时器 自动挂断
    private CountDownTimer mAutoHangUpTimer;
    //需要暂停
    private boolean mNeedPause = true;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_wait_actor_layout);
    }

    @Override
    protected void onContentAdded() {
        mPassUserId = getIntent().getIntExtra(Constant.PASS_USER_ID, 0);
        mRoomId = getIntent().getIntExtra(Constant.ROOM_ID, 0);
        mSatisfy = getIntent().getIntExtra(Constant.USER_HAVE_MONEY, 0);
        LogUtil.i("房间号: " + mRoomId + "  userId: " + mPassUserId + " 满足: " + mSatisfy);
        needHeader(false);
        initAutoCountTimer();
        playMusic();
        getPassUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNeedPause = true;
    }

    /**
     * 自动挂断
     */
    private void initAutoCountTimer() {
        //1 主播 0 用户
        if (getUserRole() == 0) {
            mCameraLl.setVisibility(View.VISIBLE);
            boolean mute = SharedPreferenceHelper.getMute(getApplicationContext());
            if (mute) {//缓存是关闭的
                mCameraIv.setSelected(false);
                mCameraTv.setText(getResources().getString(R.string.off_camera));
            } else {
                mCameraIv.setSelected(true);
                mCameraTv.setText(getResources().getString(R.string.open_camera));
            }
        }
        if (mAutoHangUpTimer == null) {
            mAutoHangUpTimer = new CountDownTimer((long) (35 * 1000), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    ToastUtil.showToast(getApplication(), R.string.no_response);
                    hangUp();
                }
            };
            //mAutoHangUpTimer.start();
        }
    }

    /**
     * 取消timer
     */
    private void cancelAutoTimer() {
        if (mAutoHangUpTimer != null) {
            mAutoHangUpTimer.cancel();
            mAutoHangUpTimer = null;
        }
    }

    /**
     * 获取对方id的信息
     */
    private void getPassUserInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(mPassUserId));
        OkHttpUtils.post().url(ChatApi.GET_PERSONAL_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PersonBean<LabelBean, CoverUrlBean>>>() {
            @Override
            public void onResponse(BaseResponse<PersonBean<LabelBean, CoverUrlBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PersonBean<LabelBean, CoverUrlBean> bean = response.m_object;
                    if (bean != null) {
                        mNickName = bean.t_nickName;
                        if (!TextUtils.isEmpty(mNickName)) {
                            String content = mNickName + getResources().getString(R.string.invite_by);
                            mInviteByTv.setText(content);
                            mNameTv.setText(mNickName);
                        } else {
                            String phone = bean.t_phone;
                            if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
                                String lastFour = phone.substring(7, phone.length());
                                String content = mContext.getResources().getString(R.string.chat_user) + lastFour;
                                mInviteByTv.setText(content);
                                mNameTv.setText(content);
                            }
                        }
                        //头像
                        mHandImg = bean.t_handImg;
                        if (!TextUtils.isEmpty(mHandImg)) {
                            int width = DevicesUtil.dp2px(WaitActorActivity.this, 60);
                            int height = DevicesUtil.dp2px(WaitActorActivity.this, 60);
                            ImageLoadHelper.glideShowCircleImageWithUrl(WaitActorActivity.this,
                                    mHandImg, mHeadIv, width, height);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //如果有公开视频,显示视频
                        String videoUrl = bean.t_addres_url;
                        if (!TextUtils.isEmpty(videoUrl) && mSatisfy != 0) {
                            //加载封面图
                            String coverUrl = bean.t_video_img;
                            if (!TextUtils.isEmpty(coverUrl)) {
                                int overWidth = DevicesUtil.getScreenW(mContext);
                                int overHeight = DevicesUtil.getScreenH(mContext);
                                if (overWidth > 800) {
                                    overWidth = (int) (overWidth * 0.7);
                                    overHeight = (int) (overHeight * 0.7);
                                }
                                ImageLoadHelper.glideShowImageWithUrl(mContext, coverUrl, mContentIv,
                                        overWidth, overHeight);
                            }
                            playVideoWithUrl(videoUrl);
                        } else {
                            //封面图
                            List<CoverUrlBean> coverUrlBeanList = bean.coverList;
                            if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                                String firstUrl = coverUrlBeanList.get(0).t_img_url;
                                if (!TextUtils.isEmpty(firstUrl)) {
                                    int overWidth = DevicesUtil.getScreenW(mContext);
                                    int overHeight = DevicesUtil.getScreenH(mContext);
                                    if (overWidth > 800) {
                                        overWidth = (int) (overWidth * 0.7);
                                        overHeight = (int) (overHeight * 0.7);
                                    }
                                    ImageLoadHelper.glideShowImageWithUrl(mContext, firstUrl, mContentIv,
                                            overWidth, overHeight);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 播放视频
     */
    private void playVideoWithUrl(String url) {
        if (mVideoView != null) {
            mContentIv.setVisibility(View.GONE);
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
            mVideoView.setVideoPath(url);
            mVideoView.setVolume(0, 0);
            mVideoView.setLooping(true);
            mVideoView.start();
        }
    }

    @OnClick({R.id.hang_up_tv, R.id.accept_tv, R.id.camera_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up_tv: {//挂断
                hangUp();
                break;
            }
            case R.id.accept_tv: {//接通
                if (mSatisfy == -1) {//如果钱不够
                    mNeedPause = false;
                    ChargeHelper.showSetCoverDialog(WaitActorActivity.this);
                } else if (mSatisfy == 1) {//钱够
                    jumpToVideoChatOne(true);
                } else {//是主播进来
                    jumpToVideoChatOne(false);
                }
                break;
            }
            case R.id.camera_ll: {//关闭摄像头
                if (mCameraIv.isSelected()) {//如果摄像头是开启的
                    mCameraIv.setSelected(false);
                    mCameraTv.setText(getResources().getString(R.string.off_camera));
                    SharedPreferenceHelper.saveMute(getApplicationContext(), true);
                } else {
                    mCameraIv.setSelected(true);
                    mCameraTv.setText(getResources().getString(R.string.open_camera));
                    SharedPreferenceHelper.saveMute(getApplicationContext(), false);
                }
                break;
            }
        }
    }

    /**
     * 跳转到videoChat
     * 不需要获取签名了
     *
     * @param fromUserJoin 是不是用户受到主播邀请
     */
    private void jumpToVideoChatOne(boolean fromUserJoin) {
        if (fromUserJoin) {//是用户受到主播邀请
            Intent intent = new Intent(getApplicationContext(), VideoChatOneActivity.class);
            intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER_JOIN);
            intent.putExtra(Constant.ACTOR_ID, mPassUserId);
            intent.putExtra(Constant.ROOM_ID, mRoomId);
            intent.putExtra(Constant.USER_HEAD_URL, mHandImg);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), VideoChatOneActivity.class);
            intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR);
            intent.putExtra(Constant.ROOM_ID, mRoomId);
            intent.putExtra(Constant.ACTOR_ID, mPassUserId);
            intent.putExtra(Constant.NICK_NAME, mNickName);
            intent.putExtra(Constant.USER_HEAD_URL, mHandImg);
            startActivity(intent);
        }
        cancelAutoTimer();
        finish();
    }


    /**
     * 用户或者主播挂断链接
     */
    private void hangUp() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", String.valueOf(mRoomId));
        OkHttpUtils.post().url(ChatApi.BREAK_LINK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    cancelAutoTimer();
                    finish();
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 播放音频
     */
    private void playMusic() {
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.call_come);
                mPlayer.setLooping(true);
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHangUp() {
        try {
            if (!isFinishing()) {
                ToastUtil.showToast(getApplicationContext(), R.string.have_hang_up);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean needDestroyBroadcastOnStop() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mNeedPause) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        destroyBroadcast();
        cancelAutoTimer();
    }

}
