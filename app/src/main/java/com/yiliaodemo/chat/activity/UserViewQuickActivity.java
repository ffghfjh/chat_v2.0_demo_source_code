package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.QuickAnchorBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  用户预览速配主播页面
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserViewQuickActivity extends BaseActivity {

    @BindView(R.id.video_sv)
    SurfaceView mVideoSv;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.id_tv)
    TextView mIdTv;
    @BindView(R.id.focus_tv)
    TextView mFocusTv;
    @BindView(R.id.city_tv)
    TextView mCityTv;
    @BindView(R.id.age_tv)
    TextView mAgeTv;
    @BindView(R.id.sign_tv)
    TextView mSignTv;
    @BindView(R.id.anim_fl)
    FrameLayout mAnimFl;
    @BindView(R.id.time_tv)
    TextView mTimeTv;
    @BindView(R.id.switch_iv)
    ImageView mSwitchIv;
    @BindView(R.id.cover_iv)
    ImageView mCoverIv;
    @BindView(R.id.chat_iv)
    ImageView mChatIv;
    @BindView(R.id.notice_tv)
    TextView mNoticeTv;
    @BindView(R.id.info_rl)
    RelativeLayout mInfoRl;
    @BindView(R.id.no_anchor_tv)
    TextView mNoAnchorTv;
    @BindView(R.id.line_v)
    View mLineV;
    @BindView(R.id.down_info_ll)
    LinearLayout mDownInfoLl;

    //切换主播
    private TimeHandler mTimeHandler = new TimeHandler(UserViewQuickActivity.this);
    private final int NEXT_COUNT = 0x11;//倒计时5秒 减少
    private final int REFRESH_ANCHOR = 0x12;//重新获取下一个主播信息
    private QuickAnchorBean mAnchorBean;
    //判断是否需要由于视频流不能播放,是否需要刷新下一个主播
    private boolean mRmtpCanPlay = true;//true 表示能播放,则当2秒时间到的时候,不刷新下一个主播
    private final int RMTP_REFRESH_ANCHOR = 0x13;//由于视频流不能播放,重新获取下一个主播信息
    private PLMediaPlayer mMediaPlayer;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_user_view_quick_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimeHandler.sendEmptyMessage(REFRESH_ANCHOR);
    }

    /**
     * 用户拉取速配主播
     */
    private void getQuickAnchor() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SPEED_DATING_ANCHOR)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<QuickAnchorBean>>() {
            @Override
            public void onResponse(BaseResponse<QuickAnchorBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                mSwitchIv.setEnabled(true);
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    QuickAnchorBean bean = response.m_object;
                    //获取到主播信息,开始播放,如果3秒内还不能播放出来,切换下一个主播
                    if (bean != null) {
                        //设置rmtp不能播放
                        mRmtpCanPlay = false;
                        //播放地址
                        String rmtp = bean.rtmp;
                        LogUtil.i("========流: " + rmtp);
                        mTimeHandler.removeCallbacksAndMessages(null);
                        mTimeHandler.sendEmptyMessageDelayed(RMTP_REFRESH_ANCHOR, 3000);
                        startPlay(rmtp, bean);
                        //隐藏 提示被抢走了
                        mNoticeTv.setVisibility(View.GONE);
                    }
                } else {
                    mAnchorBean = null;
                    mInfoRl.setVisibility(View.GONE);
                    mAnimFl.setVisibility(View.GONE);
                    mCityTv.setVisibility(View.GONE);
                    mAgeTv.setVisibility(View.GONE);
                    mSignTv.setVisibility(View.GONE);
                    mNoAnchorTv.setVisibility(View.VISIBLE);
                    mCoverIv.setVisibility(View.VISIBLE);
                    //关闭横条动画
                    if (mLineV != null) {
                        mLineV.clearAnimation();
                        mLineV.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mSwitchIv.setEnabled(true);
            }

        });
    }

    /**
     * 初始化VideoView
     */
    private void startPlay(String rmtp, final QuickAnchorBean bean) {
        mMediaPlayer = new PLMediaPlayer(this);
        mMediaPlayer.setDisplay(mVideoSv.getHolder());
        try {
            mMediaPlayer.setDataSource(rmtp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.setOnInfoListener(new PLOnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                if (what == MEDIA_INFO_VIDEO_RENDERING_START) {
                    LogUtil.i("video 开始播放: ");
                    mRmtpCanPlay = true;
                    setAnchorInfo(bean);
                }
            }
        });
        mMediaPlayer.prepareAsync();
    }

    /**
     * 设置信息,等视频能播放了之后才设置并显示信息
     */
    private void setAnchorInfo(QuickAnchorBean bean) {
        //设置主播信息,只有视频能播放了,才设置
        mAnchorBean = bean;
        //信息部分
        mInfoRl.setVisibility(View.VISIBLE);
        mDownInfoLl.setVisibility(View.VISIBLE);
        mCoverIv.setVisibility(View.GONE);
        mNoAnchorTv.setVisibility(View.GONE);
        //头像
        String headImg = bean.t_handImg;
        if (!TextUtils.isEmpty(headImg)) {
            int width = DevicesUtil.dp2px(mContext, 38);
            int height = DevicesUtil.dp2px(mContext, 38);
            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHeadIv, width, height);
        } else {
            mHeadIv.setImageResource(R.drawable.default_head_img);
        }
        //昵称
        String nick = bean.t_nickName;
        if (!TextUtils.isEmpty(nick)) {
            mNickTv.setText(nick);
            mNickTv.setVisibility(View.VISIBLE);
        } else {
            mNickTv.setVisibility(View.INVISIBLE);
        }
        //ID
        int idCard = bean.t_idcard;
        if (idCard > 0) {
            String content = getResources().getString(R.string.chat_number_one) + idCard;
            mIdTv.setText(content);
            mIdTv.setVisibility(View.VISIBLE);
        } else {
            mIdTv.setVisibility(View.INVISIBLE);
        }
        //关注 0.未关注 1.已关注
        if (bean.isFollow == 0) {
            mFocusTv.setVisibility(View.VISIBLE);
        }
        //城市
        String city = bean.t_city;
        if (!TextUtils.isEmpty(city)) {
            mCityTv.setText(city);
            mCityTv.setVisibility(View.VISIBLE);
        } else {
            mCityTv.setVisibility(View.INVISIBLE);
        }
        //年龄
        int age = bean.t_age;
        if (age > 0) {
            String content = age + getString(R.string.age);
            mAgeTv.setText(content);
            mAgeTv.setVisibility(View.VISIBLE);
        } else {
            mAgeTv.setVisibility(View.INVISIBLE);
        }
        //签名
        String sign = bean.t_autograph;
        if (!TextUtils.isEmpty(sign)) {
            mSignTv.setText(sign);
            mSignTv.setVisibility(View.VISIBLE);
        } else {
            mSignTv.setText(getString(R.string.lazy));
        }
        //关闭横条动画
        if (mLineV != null) {
            mLineV.clearAnimation();
            mLineV.setVisibility(View.INVISIBLE);
        }
        //旋转动画
        if (mAnimFl.getVisibility() != View.VISIBLE) {
            mAnimFl.setVisibility(View.VISIBLE);
        }
        //发送5秒倒计时消息
        if (mTimeHandler != null) {
            mTimeTv.setText(getString(R.string.five_one));
            mTimeHandler.removeCallbacksAndMessages(null);
            mTimeHandler.sendEmptyMessageDelayed(NEXT_COUNT, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacksAndMessages(null);
            mTimeHandler = null;
        }
    }

    @OnClick({R.id.close_iv, R.id.switch_iv, R.id.chat_iv, R.id.focus_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv: {//关闭
                finish();
                break;
            }
            case R.id.switch_iv: {//切换
                mSwitchIv.setEnabled(false);
                mAnchorBean = null;
                mCoverIv.setVisibility(View.VISIBLE);
                mDownInfoLl.setVisibility(View.INVISIBLE);
                mSignTv.setVisibility(View.INVISIBLE);
                mInfoRl.setVisibility(View.INVISIBLE);
                //开始动画
                Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.quick_scale);
                mLineV.startAnimation(scaleAnimation);
                mLineV.setVisibility(View.VISIBLE);
                //发送刷新消息
                mTimeHandler.removeCallbacksAndMessages(null);
                mTimeHandler.sendEmptyMessageDelayed(REFRESH_ANCHOR, 1000);
                break;
            }
            case R.id.chat_iv: {//发起视频
                if (mAnchorBean != null) {
                    mTimeHandler.removeCallbacksAndMessages(null);
                    mChatIv.setEnabled(false);
                    startCountTime();
                }
                break;
            }
            case R.id.focus_tv: {//关注
                if (mAnchorBean != null) {
                    saveFollow(mAnchorBean.t_id);
                }
                break;
            }
        }
    }

    /**
     * 添加关注
     */
    private void saveFollow(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//关注人
        paramMap.put("coverFollowUserId", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        mFocusTv.setVisibility(View.GONE);
                    }
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
     * 用户端先调开始计时,判断主播是否正忙
     * 如果不忙就直接加入房间
     */
    private void startCountTime() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anthorId", String.valueOf(mAnchorBean.t_id));
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", String.valueOf(mAnchorBean.roomId));
        OkHttpUtils.post().url(ChatApi.VIDEO_CHAT_BIGIN_TIMING)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                //-3：对方已挂断视频请求,-2：主播资料未完善 -1:用户余额不足,0:程序异常 1:开始计时  -4 : 被抢了
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        //开始计时成功,以用户身份加入房间
                        LogUtil.i("开始计时成功");
                        Intent intent = new Intent(getApplicationContext(), QuickVideoChatActivity.class);
                        intent.putExtra(Constant.ROOM_ID, mAnchorBean.roomId);
                        intent.putExtra(Constant.QUICK_ANCHOR_BEAN, mAnchorBean);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                        startActivity(intent);
                    } else if (response.m_istatus == -4) {//被抢了
                        mNoticeTv.setVisibility(View.VISIBLE);
                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialog(UserViewQuickActivity.this);
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), getString(R.string.system_error));
                        }
                    }
                } else {//如果失败了
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.system_error));
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                mChatIv.setEnabled(true);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mChatIv.setEnabled(true);
            }
        });
    }

    /**
     * 切换主播倒计时Handler
     */
    private static class TimeHandler extends Handler {

        private WeakReference<UserViewQuickActivity> mSettingActivityWeakReference;

        TimeHandler(UserViewQuickActivity settingActivity) {
            mSettingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserViewQuickActivity settingActivity = mSettingActivityWeakReference.get();
            if (settingActivity != null && !settingActivity.isFinishing()) {
                if (msg.what == settingActivity.NEXT_COUNT) {
                    int number = Integer.parseInt(settingActivity.mTimeTv.getText().toString().trim());
                    if (number > 1) {//改变中间数字
                        number--;
                        settingActivity.mTimeTv.setText(String.valueOf(number));
                        settingActivity.mTimeHandler.removeCallbacksAndMessages(null);
                        settingActivity.mTimeHandler.sendEmptyMessageDelayed(settingActivity.NEXT_COUNT, 1000);
                    } else {//切换到下一个
                        settingActivity.mAnchorBean = null;
                        settingActivity.mCoverIv.setVisibility(View.VISIBLE);
                        settingActivity.mDownInfoLl.setVisibility(View.INVISIBLE);
                        settingActivity.mSignTv.setVisibility(View.INVISIBLE);
                        settingActivity.mInfoRl.setVisibility(View.INVISIBLE);
                        if (settingActivity.mMediaPlayer != null) {
                            settingActivity.mMediaPlayer.release();
                            settingActivity.mMediaPlayer = null;
                        }
                        //开始动画
                        Animation scaleAnimation = AnimationUtils.loadAnimation(settingActivity, R.anim.quick_scale);
                        settingActivity.mLineV.startAnimation(scaleAnimation);
                        settingActivity.mLineV.setVisibility(View.VISIBLE);
                        settingActivity.mTimeHandler.removeCallbacksAndMessages(null);
                        settingActivity.mTimeHandler.sendEmptyMessageDelayed(settingActivity.REFRESH_ANCHOR, 1000);
                    }
                } else if (msg.what == settingActivity.REFRESH_ANCHOR) {//5秒常规刷新
                    settingActivity.mAnchorBean = null;
                    settingActivity.mCoverIv.setVisibility(View.VISIBLE);
                    settingActivity.mDownInfoLl.setVisibility(View.INVISIBLE);
                    settingActivity.mSignTv.setVisibility(View.INVISIBLE);
                    settingActivity.mInfoRl.setVisibility(View.INVISIBLE);
                    if (settingActivity.mMediaPlayer != null) {
                        settingActivity.mMediaPlayer.release();
                        settingActivity.mMediaPlayer = null;
                    }
                    //开始动画
                    Animation scaleAnimation = AnimationUtils.loadAnimation(settingActivity, R.anim.quick_scale);
                    settingActivity.mLineV.startAnimation(scaleAnimation);
                    settingActivity.mLineV.setVisibility(View.VISIBLE);
                    settingActivity.getQuickAnchor();
                } else if (msg.what == settingActivity.RMTP_REFRESH_ANCHOR && !settingActivity.mRmtpCanPlay) {//rmtp不能播放刷新
                    settingActivity.mAnchorBean = null;
                    settingActivity.mCoverIv.setVisibility(View.VISIBLE);
                    settingActivity.mDownInfoLl.setVisibility(View.INVISIBLE);
                    settingActivity.mSignTv.setVisibility(View.INVISIBLE);
                    settingActivity.mInfoRl.setVisibility(View.INVISIBLE);
                    if (settingActivity.mMediaPlayer != null) {
                        settingActivity.mMediaPlayer.release();
                        settingActivity.mMediaPlayer = null;
                    }
                    //开始动画
                    Animation scaleAnimation = AnimationUtils.loadAnimation(settingActivity, R.anim.quick_scale);
                    settingActivity.mLineV.startAnimation(scaleAnimation);
                    settingActivity.mLineV.setVisibility(View.VISIBLE);
                    settingActivity.getQuickAnchor();
                }
            }
        }
    }

}
