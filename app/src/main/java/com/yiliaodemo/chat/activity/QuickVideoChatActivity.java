package com.yiliaodemo.chat.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wushuangtech.bean.TTTVideoFrame;
import com.wushuangtech.bean.VideoCompositingLayout;
import com.wushuangtech.library.Constants;
import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.model.PublisherConfiguration;
import com.wushuangtech.wstechapi.model.VideoCanvas;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliaodemo.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliaodemo.chat.adapter.VideoChatTextRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.BalanceBean;
import com.yiliaodemo.chat.bean.CustomMessageBean;
import com.yiliaodemo.chat.bean.GiftBean;
import com.yiliaodemo.chat.bean.GoldBean;
import com.yiliaodemo.chat.bean.PersonBean;
import com.yiliaodemo.chat.bean.QuickAnchorBean;
import com.yiliaodemo.chat.bean.QuickTimeBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliaodemo.chat.listener.OnViewPagerListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.TimeUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import okhttp3.Call;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_ANCHOR;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_BAD_VERSION;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_TIMEOUT;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_UNKNOW;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_VERIFY_FAILED;
import static com.wushuangtech.library.Constants.LOG_FILTER_DEBUG;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  速配聊天页面,包含主播预览  主播用户聊天
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class QuickVideoChatActivity extends BaseActivity {

    @BindView(R.id.content_fl)
    ConstraintLayout mContentFl;
    @BindView(R.id.remote_fl)
    ConstraintLayout mRemoteFl;
    //开始速配
    @BindView(R.id.start_quick_tv)
    TextView mStartQuickTv;
    //用户关闭摄像头相关cover视图,提示视图
    @BindView(R.id.big_cover_black_v)
    View mBigCoverBlackV;
    @BindView(R.id.small_cover_black_v)
    View mSmallCoverBlackV;
    @BindView(R.id.have_off_camera_tv)
    TextView mHaveOffCameraTv;
    //几个操作按钮
    @BindView(R.id.little_ll)
    LinearLayout mLittleLl;
    @BindView(R.id.close_video_iv)
    ImageView mCloseVideoIv;//关闭摄像头
    @BindView(R.id.close_micro_iv)
    ImageView mCloseMicroIv;//关闭麦克风
    //文字输入
    @BindView(R.id.input_ll)
    LinearLayout mInputLl;
    @BindView(R.id.input_et)
    EditText mInputEt;
    @BindView(R.id.text_list_rv)
    RecyclerView mTextListRv;
    //礼物动画相关
    @BindView(R.id.gift_ll)
    LinearLayout mGiftLl;
    @BindView(R.id.gift_head_iv)
    ImageView mGiftHeadIv;
    @BindView(R.id.gift_des_tv)
    TextView mGiftDesTv;
    @BindView(R.id.gift_iv)
    ImageView mGiftIv;
    @BindView(R.id.gift_number_tv)
    TextView mGiftNumberTv;
    //关闭按钮
    @BindView(R.id.finish_iv)
    ImageView mFinishIv;
    //计时
    @BindView(R.id.time_tv)
    TextView mTimeTv;
    //左上角信息
    @BindView(R.id.info_ll)
    LinearLayout mInfoLl;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.gold_iv)
    ImageView mGoldIv;
    @BindView(R.id.city_tv)
    TextView mCityTv;
    @BindView(R.id.focus_tv)
    TextView mFocusTv;
    @BindView(R.id.verify_iv)
    ImageView mVerifyIv;
    @BindView(R.id.age_tv)
    TextView mAgeTv;
    @BindView(R.id.job_tv)
    TextView mJobTv;
    //麦克风
    @BindView(R.id.micro_iv)
    ImageView mMicroIv;
    //速配时间
    @BindView(R.id.time_ll)
    LinearLayout mTimeLl;
    @BindView(R.id.today_time_tv)
    TextView mTodayTimeTv;
    @BindView(R.id.month_time_tv)
    TextView mMonthTimeTv;
    //切换摄像头
    @BindView(R.id.change_camera_iv)
    ImageView mChangeCameraIv;
    //主播开始速配后的提示
    @BindView(R.id.hint_tv)
    TextView mHintTv;

    //视频聊天相关
    private TTTRtcEngine mTttRtcEngine;
    private SurfaceView mLocalSurfaceView;
    private SurfaceView mRemoteSurfaceView;
    private int mRoomId;
    private int mFromType;//是用户进入,还是主播进入
    private boolean mHaveSwitchBigToSmall = false;//已经从大图切到小图
    //礼物相关
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private int mMyGoldNumber;
    //礼物动画相关
    //记录单次动画时间内用户赠送的礼物
    private int mSingleTimeSendGiftCount = 0;
    //用户相关
    private int mActorId;//主播id
    //计时器
    private Handler mHandler = new Handler();
    //用户关闭摄像头
    private boolean mUserSelfMute = false;//用户端自己mute了
    private boolean mUserHaveMute = false;//主播端  用户mute了
    //挂断
    private boolean mHaveHangUp = false;  //是否挂断了
    //计时  包括1. 主播开启速配后到用户进入房间这段时间  2. 接通视频后时间
    private long mCurrentSecond = 0;//当前毫秒数
    //是否已经结束速配,防止用户直接杀死app,没有调用结束速配
    private boolean mHaveEndQuick = false;
    private MediaPlayer mPlayer;
    //文字聊天
    private VideoChatTextRecyclerAdapter mTextRecyclerAdapter;
    //美颜
    private TiSDKManager mTiSDKManager;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_quick_video_chat_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        // 禁止截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        checkPermission();

        mFromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_USER);
        mRoomId = getIntent().getIntExtra(Constant.ROOM_ID, 0);

        initView();
        initTextChat();
        //注册sdk的event用于接收各种event事件
        JMessageClient.registerEventReceiver(this);
        initHelper();
        //获取礼物列表
        getGiftList();
        dealCrash();
    }

    /**
     * 初始化view
     */
    private void initView() {
        if (mFromType == Constant.FROM_ACTOR) {//主播进入,刚开始是速配预览, 显示开始速配按钮, 速配时间
            mStartQuickTv.setVisibility(View.VISIBLE);
            getQuickTime();
        } else {//用户进来
            //获取传递过来的主播信息,只有用户进入activity才会获取
            QuickAnchorBean anchorBean = (QuickAnchorBean) getIntent().getSerializableExtra(Constant.QUICK_ANCHOR_BEAN);
            if (anchorBean != null) {
                mActorId = anchorBean.t_id;
                //用户端设置主播信息
                setAnchorInfo(anchorBean);
            }
            //用户进来默认显示操作摄像头按钮
            //获取缓存中用户是否关闭摄像头
            mUserSelfMute = SharedPreferenceHelper.getMute(getApplicationContext());
            if (mUserSelfMute) {//缓存中摄像头是关闭的
                mCloseVideoIv.setSelected(false);
            } else {
                mCloseVideoIv.setSelected(true);
            }
            //没有速配,直接进来就显示下面小按钮
            mLittleLl.setVisibility(View.VISIBLE);
            //
        }
    }

    /**
     * 初始化文字聊天
     */
    private void initTextChat() {
        mTextRecyclerAdapter = new VideoChatTextRecyclerAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mTextListRv.setLayoutManager(manager);
        mTextListRv.setAdapter(mTextRecyclerAdapter);
        mInputEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendTextMessage();
                    return true;
                }
                return false;
            }
        });
        mCloseMicroIv.setSelected(false);
        //处理用户进来,接通视频的时候,下发提示消息
        if (mFromType == Constant.FROM_USER && AppManager.getInstance() != null) {
            String hintMessage = AppManager.getInstance().mVideoStartHint;
            if (!TextUtils.isEmpty(hintMessage)) {
                mTextRecyclerAdapter.addHintData(hintMessage);
                if (mTextListRv != null) {
                    if (mTextListRv.getVisibility() != View.VISIBLE) {
                        mTextListRv.setVisibility(View.VISIBLE);
                    }
                    mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                }
                AppManager.getInstance().mVideoStartHint = "";
            }
        }
    }

    /**
     * 初始化TTT
     */
    private void initHelper() {
        //生成默认参数渲染器
        mTiSDKManager = new TiSDKManager();
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
                    public void onJoinChannelSuccess(String channel, final long uid) {
                        super.onJoinChannelSuccess(channel, uid);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i("加入房间成功: " + uid);
                                uploadSelfVideo();
                            }
                        });
                    }

                    @Override
                    public void onUserJoined(final long nUserId, final int identity) {
                        super.onUserJoined(nUserId, identity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i("用户加入: nUserId = " + nUserId + "  identity: " + identity);
                                //隐藏关闭按钮
                                mFinishIv.setVisibility(View.GONE);
                                //开始接通视频计时
                                mCurrentSecond = 0;
                                mHandler.removeCallbacksAndMessages(null);
                                mHandler.postDelayed(mTimeRunnable, 1000);
                                //远端
                                mRemoteSurfaceView = mTttRtcEngine.CreateRendererView(getApplicationContext());
                                mTttRtcEngine.setupRemoteVideo(new VideoCanvas(nUserId, Constants.RENDER_MODE_HIDDEN, mRemoteSurfaceView));
                                //用户加入了,切换大小图
                                switchBigAndSmall(!mHaveSwitchBigToSmall);

                                //如果是主播端,速配按钮隐藏,显示三个按钮,同时结束速配状态
                                //获取用户信息,显示在左上角信息
                                if (mFromType == Constant.FROM_ACTOR) {
                                    //设置用户id
                                    mActorId = (int) nUserId;
                                    mStartQuickTv.setVisibility(View.GONE);
                                    mLittleLl.setVisibility(View.VISIBLE);
                                    //隐藏麦克风按钮
                                    mMicroIv.setVisibility(View.GONE);
                                    mChangeCameraIv.setVisibility(View.GONE);
                                    mCloseVideoIv.setVisibility(View.GONE);
                                    //关闭统计时长
                                    mTimeLl.setVisibility(View.GONE);
                                    mTttRtcEngine.muteLocalAudioStream(false);
                                    //播放用户加入声音
                                    playMusic();
                                    endQuick(false);
                                    //获取用户信息,设置左上角信息,礼物头像信息
                                    getUserInfo(nUserId);
                                } else {//用户端发送SEI
                                    // 发送SEI
                                    VideoCompositingLayout layout = new VideoCompositingLayout();
                                    layout.regions = buildRemoteLayoutLocation();
                                    TTTRtcEngine.getInstance().setVideoCompositingLayout(layout);
                                }
                            }
                        });
                    }

                    @Override
                    public void onUserEnableVideo(final long uid, final boolean muted) {
                        super.onUserEnableVideo(uid, muted);
                        LogUtil.i("video改变: UID " + uid + "  muted:  " + muted);
                        //主播监听对方
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //处理主播端情况
                                if (mFromType == Constant.FROM_ACTOR || mFromType == Constant.FROM_ACTOR_INVITE) {
                                    if (uid != Long.parseLong(getUserId())) {
                                        mUserHaveMute = muted;
                                        if (mHaveSwitchBigToSmall) {//主播端大图显示的用户
                                            if (muted) {
                                                mBigCoverBlackV.setVisibility(View.VISIBLE);
                                                mSmallCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.VISIBLE);
                                            } else {
                                                mRemoteSurfaceView.setVisibility(View.VISIBLE);
                                                mBigCoverBlackV.setVisibility(View.GONE);
                                                mSmallCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.GONE);
                                            }
                                        } else {//主播端小图显示的用户
                                            if (muted) {
                                                mRemoteSurfaceView.setVisibility(View.INVISIBLE);
                                                mSmallCoverBlackV.setVisibility(View.VISIBLE);
                                                mBigCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.GONE);
                                            } else {
                                                mRemoteSurfaceView.setVisibility(View.VISIBLE);
                                                mBigCoverBlackV.setVisibility(View.GONE);
                                                mSmallCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onLocalVideoFrameCaptured(TTTVideoFrame frame) {
                        super.onLocalVideoFrameCaptured(frame);
                        if (mTiSDKManager != null) {
                            frame.textureID = mTiSDKManager.renderTexture2D(frame.textureID, frame.stride,
                                    frame.height, TiRotation.CLOCKWISE_ROTATION_0, true);
                        }
                    }
                });
        if (mTttRtcEngine != null) {
            mTttRtcEngine.setLogFilter(LOG_FILTER_DEBUG);
            mTttRtcEngine.enableVideo();
            mTttRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mTttRtcEngine.enableCrossRoom(true);
            mTttRtcEngine.muteLocalAudioStream(false);
            //推流地址
            String pushUrl = Constant.TTT_PUSH_ADDRESS + getUserId() + "/" + String.valueOf(mRoomId);
            LogUtil.i("推流地址: " + pushUrl);
            PublisherConfiguration configuration = new PublisherConfiguration();
            configuration.setPushUrl(pushUrl);
            mTttRtcEngine.configPublisher(configuration);
            //都以主播身份加入
            mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
            mTttRtcEngine.joinChannel("", String.valueOf(mRoomId), Integer.parseInt(getUserId()));
        }
    }

    /**
     * 切换大小视图
     *
     * @param isBigToSmall true 本地视图是大图
     */
    private void switchBigAndSmall(boolean isBigToSmall) {
        try {
            mContentFl.removeAllViews();
            mRemoteFl.removeAllViews();
            if (isBigToSmall) {
                mHaveSwitchBigToSmall = true;
                mRemoteFl.addView(mLocalSurfaceView);
                mContentFl.addView(mRemoteSurfaceView);

                mLocalSurfaceView.setZOrderMediaOverlay(true);
                mLocalSurfaceView.setZOrderOnTop(true);
                mRemoteSurfaceView.setZOrderMediaOverlay(false);
                mRemoteSurfaceView.setZOrderOnTop(false);

                //用户切换
                //用户自己是在小图, 如果mute暂停了,小图就显示黑
                if (mFromType == Constant.FROM_USER) {
                    if (mUserSelfMute) {
                        mLocalSurfaceView.setVisibility(View.INVISIBLE);
                        mSmallCoverBlackV.setVisibility(View.VISIBLE);
                        mBigCoverBlackV.setVisibility(View.GONE);
                    }
                    //主播切换
                } else {
                    if (mUserHaveMute) {
                        mBigCoverBlackV.setVisibility(View.VISIBLE);
                        mSmallCoverBlackV.setVisibility(View.GONE);
                        mHaveOffCameraTv.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                mHaveSwitchBigToSmall = false;
                mRemoteFl.addView(mRemoteSurfaceView);
                mContentFl.addView(mLocalSurfaceView);

                mLocalSurfaceView.setZOrderMediaOverlay(false);
                mLocalSurfaceView.setZOrderOnTop(false);
                mRemoteSurfaceView.setZOrderMediaOverlay(true);
                mRemoteSurfaceView.setZOrderOnTop(true);

                //用户自己是在大图, 如果mute暂停了,大图就显示黑
                //用户切换
                if (mFromType == Constant.FROM_USER) {
                    if (mUserSelfMute) {
                        mBigCoverBlackV.setVisibility(View.VISIBLE);
                        mSmallCoverBlackV.setVisibility(View.GONE);
                    }
                } else {
                    if (mUserHaveMute) {
                        mRemoteSurfaceView.setVisibility(View.INVISIBLE);
                        mSmallCoverBlackV.setVisibility(View.VISIBLE);
                        mBigCoverBlackV.setVisibility(View.GONE);
                        mHaveOffCameraTv.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRemoteFl.setEnabled(true);
        }
    }

    /**
     * 上传自己的流
     */
    private void uploadSelfVideo() {
        try {
            mLocalSurfaceView = mTttRtcEngine.CreateRendererView(this);
            mTttRtcEngine.setupLocalVideo(new VideoCanvas(Integer.parseInt(getUserId()),
                    Constants.RENDER_MODE_HIDDEN, mLocalSurfaceView), getRequestedOrientation());
            mContentFl.addView(mLocalSurfaceView);
            SurfaceHolder holder = mLocalSurfaceView.getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (mTiSDKManager != null) {
                        mTiSDKManager.destroy();
                    }
                }
            });
            if (mFromType == Constant.FROM_ACTOR) {
                //主播进来默认关闭麦克风
                mMicroIv.setVisibility(View.VISIBLE);
                mChangeCameraIv.setVisibility(View.VISIBLE);
                mMicroIv.setSelected(false);
                mTttRtcEngine.muteLocalAudioStream(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.start_quick_tv, R.id.finish_iv, R.id.hang_up_iv, R.id.change_iv, R.id.reward_iv,
            R.id.close_video_iv, R.id.remote_fl, R.id.content_fl, R.id.micro_iv, R.id.focus_tv,
            R.id.message_iv, R.id.btn_send, R.id.text_cover_v, R.id.close_micro_iv, R.id.change_camera_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_quick_tv: {//开始/结束速配
                String content = mStartQuickTv.getText().toString().trim();
                if (content.equals(getResources().getString(R.string.start_quick))) {
                    showNoticeDialog();
                } else {
                    endQuick(true);
                }
                break;
            }
            case R.id.finish_iv: {//关闭图标
                String content = mStartQuickTv.getText().toString().trim();
                if (content.equals(getResources().getString(R.string.start_quick))) {
                    if (mTttRtcEngine != null) {
                        mTttRtcEngine.leaveChannel();
                        mTttRtcEngine = null;
                    }
                    mHaveEndQuick = true;
                    finish();
                } else {
                    endQuick(true);
                }
                break;
            }
            case R.id.hang_up_iv: {//挂断
                hangUp();
                break;
            }
            case R.id.change_camera_iv:
            case R.id.change_iv: {//切换前后置
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.switchCamera();
                }
                break;
            }
            case R.id.reward_iv: {//打赏礼物
                showRewardDialog();
                break;
            }
            case R.id.close_video_iv: {//操作摄像头
                clickCamera();
                break;
            }
            case R.id.remote_fl: {//切换,点击小屏切换
                //如果开始速配可见
                if (mStartQuickTv.getVisibility() == View.VISIBLE) {
                    return;
                }
                mRemoteFl.setEnabled(false);
                switchBigAndSmall(!mHaveSwitchBigToSmall);
                break;
            }
            case R.id.text_cover_v:
            case R.id.content_fl: {//显示或隐藏操作按钮
                //如果开始速配可见
                if (mStartQuickTv.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (mLittleLl.getVisibility() == View.VISIBLE) {
                    mLittleLl.setVisibility(View.INVISIBLE);
                    mTextListRv.setVisibility(View.GONE);
                    mInputLl.setVisibility(View.GONE);
                } else {
                    mLittleLl.setVisibility(View.VISIBLE);
                    mTextListRv.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.micro_iv: {//麦克风
                changeMicro();
                break;
            }
            case R.id.focus_tv: {//关注
                if (mActorId > 0) {
                    saveFollow(mActorId);
                }
                break;
            }
            case R.id.close_micro_iv: {//接通后的切换麦克风
                changeMicroAfterConnect();
                break;
            }
            case R.id.message_iv: {//文字
                if (mInputLl.getVisibility() != View.VISIBLE) {
                    mInputLl.setVisibility(View.VISIBLE);
                    showSpan();
                } else {
                    mInputLl.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.btn_send: {//发送文字消息
                sendTextMessage();
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
     * 切换麦克,接通后
     */
    private void changeMicroAfterConnect() {
        if (mCloseMicroIv.isSelected()) {//原来是开启的
            mCloseMicroIv.setSelected(false);
            //静音/取消静音。该方法用于允许/禁止往网络发送本地音频流。 	True麦克风静音，False取消静音
            if (mTttRtcEngine != null) {
                mTttRtcEngine.muteLocalAudioStream(false);
            }
        } else {
            mCloseMicroIv.setSelected(true);
            if (mTttRtcEngine != null) {
                mTttRtcEngine.muteLocalAudioStream(true);
            }
        }
    }

    /**
     * 切换麦克
     */
    private void changeMicro() {
        if (mMicroIv.isSelected()) {//原来是开启的
            mMicroIv.setSelected(false);
            //静音/取消静音。该方法用于允许/禁止往网络发送本地音频流。 	True麦克风静音，False取消静音
            if (mTttRtcEngine != null) {
                mTttRtcEngine.muteLocalAudioStream(true);
            }
        } else {
            mMicroIv.setSelected(true);
            if (mTttRtcEngine != null) {
                mTttRtcEngine.muteLocalAudioStream(false);
            }
        }
    }

    /**
     * 点击操作摄像头,只有用户可以操作
     */
    private void clickCamera() {
        if (mCloseVideoIv.isSelected()) {//如果摄像头是开启的
            mCloseVideoIv.setSelected(false);

            SharedPreferenceHelper.saveMute(getApplicationContext(), true);

            //如果大图显示的对方,小图是自己
            if (mHaveSwitchBigToSmall) {
                mLocalSurfaceView.setVisibility(View.INVISIBLE);
                mSmallCoverBlackV.setVisibility(View.VISIBLE);
                mBigCoverBlackV.setVisibility(View.GONE);
            } else {
                //大图是自己
                mBigCoverBlackV.setVisibility(View.VISIBLE);
                mSmallCoverBlackV.setVisibility(View.GONE);
            }

            //暂停本地视频流  true是启用，false是禁用
            //开关本地视频采集
            if (mTttRtcEngine != null) {
                mTttRtcEngine.enableLocalVideo(false);
                mUserSelfMute = true;
            }
        } else {
            mCloseVideoIv.setSelected(true);

            SharedPreferenceHelper.saveMute(getApplicationContext(), false);

            //如果大图显示的是对方,小图是自己
            if (mHaveSwitchBigToSmall) {
                mBigCoverBlackV.setVisibility(View.GONE);
                mSmallCoverBlackV.setVisibility(View.GONE);
                mHaveOffCameraTv.setVisibility(View.GONE);
                mLocalSurfaceView.setVisibility(View.VISIBLE);
            } else {
                mBigCoverBlackV.setVisibility(View.GONE);
                mSmallCoverBlackV.setVisibility(View.GONE);
                mHaveOffCameraTv.setVisibility(View.GONE);
                mLocalSurfaceView.setVisibility(View.VISIBLE);
            }

            //暂停本地视频流  true是启用，false是禁用
            if (mTttRtcEngine != null) {
                mTttRtcEngine.enableLocalVideo(true);
                mUserSelfMute = false;
            }
        }
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
                    ToastUtil.showToast(getApplicationContext(), R.string.have_hang_up_one);
                    mHaveHangUp = true;
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;

                    if (mFromType == Constant.FROM_USER) {
                        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
                        startActivity(intent);
                    }
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                //接口调用失败,也退出房间
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;
                    finish();
                }
            }
        });
    }

    /**
     * 显示提示dialog
     */
    private void showNoticeDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_anchor_quick_notice_layout, null);
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
     * 设置view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //内容
        TextView content_tv = view.findViewById(R.id.content_tv);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(getString(R.string.quick_notice_des));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red_fe2947)),
                26, 40, SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red_fe2947)),
                49, 51, SPAN_EXCLUSIVE_EXCLUSIVE);
        content_tv.setText(spannableStringBuilder);
        //确认
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuick();
                mDialog.dismiss();
            }
        });
    }

    /**
     * 主播结束速配
     */
    private void endQuick(final boolean needFinish) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.END_SPEED_DATING)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        mHaveEndQuick = true;
                        if (needFinish) {
                            //退出房间
                            if (mTttRtcEngine != null) {
                                mTttRtcEngine.leaveChannel();
                                mTttRtcEngine = null;
                            }
                            finish();
                        }
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        }
                        if (needFinish) {
                            //退出房间
                            if (mTttRtcEngine != null) {
                                mTttRtcEngine.leaveChannel();
                                mTttRtcEngine = null;
                            }
                            finish();
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    if (needFinish) {
                        //退出房间
                        if (mTttRtcEngine != null) {
                            mTttRtcEngine.leaveChannel();
                            mTttRtcEngine = null;
                        }
                        finish();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                //接口调用失败,也退出房间
                if (needFinish) {
                    //退出房间
                    if (mTttRtcEngine != null) {
                        mTttRtcEngine.leaveChannel();
                        mTttRtcEngine = null;
                    }
                    finish();
                }
            }
        });
    }

    /**
     * 主播开始速配
     */
    private void startQuick() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", String.valueOf(mRoomId));
        OkHttpUtils.post().url(ChatApi.OPEN_SPEED_DATING)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(), R.string.open_success);
                        mStartQuickTv.setText(getResources().getString(R.string.end_quick));
                        //主播开始速配计时
                        mHandler.postDelayed(mTimeRunnable, 1000);
                        // 发送SEI,开始推流
                        VideoCompositingLayout layout = new VideoCompositingLayout();
                        layout.regions = buildRemoteLayoutLocation();
                        TTTRtcEngine.getInstance().setVideoCompositingLayout(layout);
                    } else {//房间丢失
                        String messge = response.m_strMessage;
                        if (!TextUtils.isEmpty(messge)) {
                            ToastUtil.showToast(getApplicationContext(), messge);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.open_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.open_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                if (isFinishing()) {
                    return;
                }
                ToastUtil.showToast(getApplicationContext(), R.string.open_fail);
            }
        });
    }

    /**
     * 开始本地计时
     */
    private Runnable mTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mCurrentSecond = mCurrentSecond + 1000;
            mTimeTv.setText(TimeUtil.getFormatHMS(mCurrentSecond));
            //递归调用本run able对象，实现每隔一秒一次执行任务
            mHandler.postDelayed(this, 1000);
        }
    };

    /**
     * 建造推流
     */
    private VideoCompositingLayout.Region[] buildRemoteLayoutLocation() {
        List<VideoCompositingLayout.Region> tempList = new ArrayList<>();
        //主播
        if (Long.parseLong(getUserId()) > 0) {
            VideoCompositingLayout.Region mRegion = new VideoCompositingLayout.Region();
            mRegion.mUserID = Long.parseLong(getUserId());
            //上下
            //1:1
            mRegion.x = 0;
            mRegion.y = 0;
            mRegion.width = 1;
            mRegion.height = 1;
            mRegion.zOrder = 0;
            tempList.add(mRegion);
        }
        VideoCompositingLayout.Region[] mRegions = new VideoCompositingLayout.Region[tempList.size()];
        for (int k = 0; k < tempList.size(); k++) {
            VideoCompositingLayout.Region region = tempList.get(k);
            mRegions[k] = region;
        }
        return mRegions;
    }

    //-------------------权限---------------------
    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    //------------------礼物模块-------------------------

    /**
     * 显示打赏礼物Dialog
     */
    private void showRewardDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_gift_layout, null);
        setGiftDialogView(view, mDialog);
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
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 礼物dialog view 初始化
     */
    private void setGiftDialogView(View view, final Dialog mDialog) {
        //-----------------初始化----------------
        final RecyclerView gift_rv = view.findViewById(R.id.gift_rv);
        final RecyclerView red_rv = view.findViewById(R.id.red_rv);
        final LinearLayout indicator_ll = view.findViewById(R.id.indicator_ll);
        final TextView gift_tv = view.findViewById(R.id.gift_tv);
        final TextView red_tv = view.findViewById(R.id.red_tv);
        final TextView gold_tv = view.findViewById(R.id.gold_tv);
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        TextView reward_tv = view.findViewById(R.id.reward_tv);

        //如果是主播进来 就不显示打赏
        /*if (mFromType == Constant.FROM_ACTOR || mFromType == Constant.FROM_ACTOR_INVITE) {
            reward_tv.setVisibility(View.INVISIBLE);
        } else {
            reward_tv.setVisibility(View.VISIBLE);
        }*/

        //初始化显示礼物
        gift_tv.setSelected(true);
        red_tv.setSelected(false);
        gift_rv.setVisibility(View.VISIBLE);
        red_rv.setVisibility(View.GONE);
        indicator_ll.setVisibility(View.VISIBLE);

        //可用金币
        getMyGold(gold_tv);

        //处理list
        List<List<GiftBean>> giftListBeanList = new ArrayList<>();
        if (mGiftBeans != null && mGiftBeans.size() > 0) {
            int count = mGiftBeans.size() / 8;
            int left = mGiftBeans.size() % 8;
            if (count > 0) {//如果大于等于8个
                for (int i = 1; i <= count; i++) {
                    int start = (i - 1) * 8;
                    int end = i * 8;
                    List<GiftBean> subList = mGiftBeans.subList(start, end);
                    giftListBeanList.add(i - 1, subList);
                }
                if (left != 0) {//如果还剩余的话,那剩余的加进入
                    List<GiftBean> leftBeans = mGiftBeans.subList(count * 8, mGiftBeans.size());
                    giftListBeanList.add(count, leftBeans);
                }
            } else {
                giftListBeanList.add(0, mGiftBeans);
            }
        }

        //-----------------礼物---------------
        final List<ImageView> imageViews = new ArrayList<>();
        ViewPagerLayoutManager mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.HORIZONTAL);
        gift_rv.setLayoutManager(mLayoutManager);
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(QuickVideoChatActivity.this);
        gift_rv.setAdapter(giftAdapter);
        if (giftListBeanList.size() > 0) {
            giftAdapter.loadData(giftListBeanList);
            //设置指示器
            for (int i = 0; i < giftListBeanList.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                int width = DevicesUtil.dp2px(getApplicationContext(), 6);
                int height = DevicesUtil.dp2px(getApplicationContext(), 6);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = 10;
                imageView.setLayoutParams(params);
                if (i == 0) {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_white_back);
                } else {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_gray_back);
                }
                imageViews.add(imageView);
                indicator_ll.addView(imageView);
            }
        }

        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (imageViews.size() > 0) {
                    for (int i = 0; i < imageViews.size(); i++) {
                        if (i == position) {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_white_back);
                        } else {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_gray_back);
                        }
                    }
                }
            }
        });

        //红包
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        red_rv.setLayoutManager(gridLayoutManager);
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(QuickVideoChatActivity.this);
        red_rv.setAdapter(goldGridRecyclerAdapter);
        goldGridRecyclerAdapter.loadData(getLocalRedList());

        //--------------处理切换----------------
        //礼物
        gift_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gift_tv.isSelected()) {
                    return;
                }
                gift_tv.setSelected(true);
                red_tv.setSelected(false);
                gift_rv.setVisibility(View.VISIBLE);
                red_rv.setVisibility(View.GONE);
                indicator_ll.setVisibility(View.VISIBLE);
            }
        });
        //红包
        red_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (red_tv.isSelected()) {
                    return;
                }
                red_tv.setSelected(true);
                gift_tv.setSelected(false);
                red_rv.setVisibility(View.VISIBLE);
                gift_rv.setVisibility(View.GONE);
                indicator_ll.setVisibility(View.INVISIBLE);
            }
        });
        //充值
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
        //dismiss的时候清空
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mGiftBeans != null && mGiftBeans.size() > 0) {
                    for (GiftBean bean : mGiftBeans) {
                        bean.isSelected = false;
                    }
                }
            }
        });
        //打赏
        reward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是礼物选中
                if (gift_tv.isSelected()) {
                    GiftBean giftBean = giftAdapter.getSelectBean();
                    if (giftBean == null) {
                        ToastUtil.showToast(getApplicationContext(), R.string.please_select_gift);
                        return;
                    }
                    //判断是否够
                    if (giftBean.t_gift_gold > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
                    reWardGift(giftBean);
                } else {//如果是红包选中
                    GoldBean goldBean = goldGridRecyclerAdapter.getSelectedBean();
                    if (goldBean == null) {
                        ToastUtil.showToast(getApplicationContext(), R.string.please_select_gold);
                        return;
                    }
                    if (goldBean.goldNumber > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
                    reWardGold(goldBean.goldNumber);
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * 获取本地红包集合
     */
    private List<GoldBean> getLocalRedList() {
        List<GoldBean> goldBeans = new ArrayList<>();
        // 99
        GoldBean one = new GoldBean();
        one.resourceId = R.drawable.reward_gold_one;
        one.goldNumber = 99;
        // 188
        GoldBean two = new GoldBean();
        two.resourceId = R.drawable.reward_gold_two;
        two.goldNumber = 188;
        // 520
        GoldBean three = new GoldBean();
        three.resourceId = R.drawable.reward_gold_three;
        three.goldNumber = 520;
        // 999
        GoldBean four = new GoldBean();
        four.resourceId = R.drawable.reward_gold_four;
        four.goldNumber = 999;
        // 1314
        GoldBean five = new GoldBean();
        five.resourceId = R.drawable.reward_gold_five;
        five.goldNumber = 1314;
        // 8888
        GoldBean six = new GoldBean();
        six.resourceId = R.drawable.reward_gold_six;
        six.goldNumber = 8888;
        goldBeans.add(0, one);
        goldBeans.add(1, two);
        goldBeans.add(2, three);
        goldBeans.add(3, four);
        goldBeans.add(4, five);
        goldBeans.add(5, six);
        return goldBeans;
    }

    /**
     * 打赏礼物
     */
    private void reWardGift(final GiftBean giftBean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("giftId", String.valueOf(giftBean.t_gift_id));
        paramMap.put("giftNum", "1");
        OkHttpUtils.post().url(ChatApi.USER_GIVE_GIFT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        //发送自定义消息
                        CustomMessageBean bean = new CustomMessageBean();
                        bean.type = "1";
                        bean.gift_id = giftBean.t_gift_id;
                        bean.gift_name = giftBean.t_gift_name;
                        bean.gift_gif_url = giftBean.t_gift_still_url;
                        bean.gold_number = giftBean.t_gift_gold;

                        String mTargetId = String.valueOf(10000 + mActorId);
                        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
                        if (mConversation == null) {
                            mConversation = Conversation.createSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
                        }
                        if (mConversation != null) {
                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message msg = mConversation.createSendMessage(customContent);
                            sendIMCustomMessage(msg);
                        }

                        startGiftInAnim(bean, true, false);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                }
            }
        });
    }

    /**
     * 发送Im自定义消息
     */
    private void sendIMCustomMessage(Message message) {
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    LogUtil.i("发送礼物自定义消息成功");
                } else {
                    LogUtil.i("发送礼物自定义消息失败");
                }
            }
        });
        MessageSendingOptions options = new MessageSendingOptions();
        options.setShowNotification(false);
        JMessageClient.sendMessage(message, options);
    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold(final TextView can_use_iv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_BALANCE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mMyGoldNumber = balanceBean.amount;
                        String content = getResources().getString(R.string.can_use_gold) + mMyGoldNumber;
                        can_use_iv.setText(content);
                        can_use_iv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 打赏金币(红包)
     */
    private void reWardGold(final int gold) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("gold", String.valueOf(gold));
        OkHttpUtils.post().url(ChatApi.SEND_RED_ENVELOPE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        //发送自定义消息
                        CustomMessageBean bean = new CustomMessageBean();
                        bean.type = "0";//金币
                        bean.gold_number = gold;
                        bean.gift_name = getResources().getString(R.string.gold);

                        String mTargetId = String.valueOf(10000 + mActorId);
                        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
                        if (mConversation == null) {
                            mConversation = Conversation.createSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
                        }
                        if (mConversation != null) {
                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message msg = mConversation.createSendMessage(customContent);
                            sendIMCustomMessage(msg);
                        }

                        startGiftInAnim(bean, true, true);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
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
     * 获取礼物列表
     */
    private void getGiftList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_GIFT_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<GiftBean> giftBeans = response.m_object;
                    if (giftBeans != null && giftBeans.size() > 0) {
                        mGiftBeans = giftBeans;
                    }
                }
            }
        });
    }

    /**
     * 开始礼物进来动画
     *
     * @param fromSend 是收到 还是送出
     */
    private void startGiftInAnim(CustomMessageBean bean, boolean fromSend, boolean isGold) {
        String lastDes = mGiftDesTv.getText().toString().trim();
        //判断需不需要重新开始一个,如果礼物不是重复,或则是金币
        if ((!TextUtils.isEmpty(lastDes) && !lastDes.contains(bean.gift_name)) || isGold) {
            mSingleTimeSendGiftCount = 0;
        }
        mSingleTimeSendGiftCount++;
        if (mSingleTimeSendGiftCount == 1) {//如果是第一次点击礼物
            //描述
            String des;
            if (fromSend) {//是送出
                des = getResources().getString(R.string.send_to) + bean.gift_name;
            } else {//是接收到
                des = getResources().getString(R.string.send_you) + bean.gift_name;
            }
            mGiftDesTv.setText(des);
            //礼物类型
            if (isGold) {//是金币
                mGiftIv.setImageResource(R.drawable.ic_gold);
                String goldNumber = getResources().getString(R.string.multi) + bean.gold_number;
                mGiftNumberTv.setText(goldNumber);
            } else {
                ImageLoadHelper.glideShowImageWithUrl(this, bean.gift_gif_url, mGiftIv);
                String giftNumber = getResources().getString(R.string.multi) + mSingleTimeSendGiftCount;
                mGiftNumberTv.setText(giftNumber);
            }

            mGiftLl.setVisibility(View.VISIBLE);
            mGiftLl.clearAnimation();
            TranslateAnimation mGiftLayoutInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lp_gift_in);
            mGiftLl.setAnimation(mGiftLayoutInAnim);
            mGiftLayoutInAnim.start();
            mHandler.removeCallbacks(mGiftRunnable);
            mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //2秒后消失
                    mHandler.postDelayed(mGiftRunnable, 3000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            if (!isGold) {//礼物
                String giftNumber = getResources().getString(R.string.multi) + mSingleTimeSendGiftCount;
                mGiftNumberTv.setText(giftNumber);

                mHandler.removeCallbacks(mGiftRunnable);
                mHandler.postDelayed(mGiftRunnable, 3000);
                startComboAnim(mGiftNumberTv);
            }
        }
    }

    private void startGiftOutAnim() {
        mGiftLl.clearAnimation();
        Animation mGiftLayoutInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lp_gift_out);
        mGiftLl.setAnimation(mGiftLayoutInAnim);
        mGiftLayoutInAnim.start();
        mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mSingleTimeSendGiftCount != 1) {
                    mGiftLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startComboAnim(final TextView giftNumView) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(giftNumView, "scaleX", 1.8f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(giftNumView, "scaleY", 1.8f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }

    /**
     * 礼物runnable
     */
    private Runnable mGiftRunnable = new Runnable() {
        @Override
        public void run() {
            mSingleTimeSendGiftCount = 0;
            startGiftOutAnim();
        }
    };

    /**
     * 在线消息
     */
    public void onEventMainThread(MessageEvent event) {
        Message message = event.getMessage();
        if (message.getTargetType() == ConversationType.single) {
            UserInfo fromUser = message.getFromUser();
            int mTargetId = 10000 + mActorId;
            if (fromUser != null && Integer.parseInt(fromUser.getUserName()) == mTargetId) {
                switch (message.getContentType()) {
                    case text: {//文本
                        final String content = ((TextContent) message.getContent()).getText();
                        LogUtil.i("新的文本消息: " + content);
                        mTextRecyclerAdapter.addData(message);
                        if (mTextListRv != null) {
                            if (mTextListRv.getVisibility() != View.VISIBLE) {
                                mTextListRv.setVisibility(View.VISIBLE);
                            }
                            mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                        }
                        break;
                    }
                    case custom: {//自定义
                        LogUtil.i("新的自定义消息: ");
                        CustomContent customContent = (CustomContent) message.getContent();
                        parseCustomMessage(customContent);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 解析自定义消息
     */
    private void parseCustomMessage(CustomContent customContent) {
        try {
            String json = customContent.getStringValue("custom");
            CustomMessageBean bean = JSON.parseObject(json, CustomMessageBean.class);
            if (bean != null) {
                if (bean.type.equals("1")) {//礼物
                    LogUtil.i("接收到的礼物: " + bean.gift_name);
                    startGiftInAnim(bean, false, false);
                } else if (bean.type.equals("0")) {//金币
                    bean.gift_name = getResources().getString(R.string.gold);
                    LogUtil.i("接收到的礼物: " + bean.gift_name);
                    startGiftInAnim(bean, false, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage() {
        if (mActorId <= 0) {
            return;
        }
        String text = mInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_text_message);
            return;
        }

        String mTargetId = String.valueOf(10000 + mActorId);
        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
        if (mConversation == null) {
            mConversation = Conversation.createSingleConversation(mTargetId, Constant.JPUSH_APP_KEY);
        }
        if (mConversation != null) {
            TextContent content = new TextContent(text);
            final Message message = mConversation.createSendMessage(content);
            if (message != null) {
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            LogUtil.i("发送文字消息成功");
                            mTextRecyclerAdapter.addData(message);
                            if (mTextListRv != null) {
                                if (mTextListRv.getVisibility() != View.VISIBLE) {
                                    mTextListRv.setVisibility(View.VISIBLE);
                                }
                                mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                            }
                        } else {
                            LogUtil.i("发送文字消息失败");
                        }
                    }
                });
                MessageSendingOptions options = new MessageSendingOptions();
                options.setShowNotification(false);
                JMessageClient.sendMessage(message, options);
            }
        }
        //清空输入框
        mInputEt.setText(null);
        //关闭软件盘
        closeSoft();
    }

    //-------------礼物模块end--------------------------

    /**
     * 挂断模块
     */
    @Override
    protected void onHangUp() {
        try {
            if (!isFinishing()) {
                LogUtil.i("连接已挂断 30005");
                if (mTttRtcEngine != null && !mHaveHangUp) {
                    ToastUtil.showToast(getApplicationContext(), R.string.have_hang_up_one);
                    mHaveHangUp = true;
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;

                    if (mFromType == Constant.FROM_USER) {
                        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     */
    private void playMusic() {
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pick_up);
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主播获取速配时间
     */
    private void getQuickTime() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_SPEED_TIME)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<QuickTimeBean>>() {
            @Override
            public void onResponse(BaseResponse<QuickTimeBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    QuickTimeBean bean = response.m_object;
                    if (bean != null) {
                        mTimeLl.setVisibility(View.VISIBLE);
                        String dayTime = getString(R.string.today_time) + bean.dayTime;
                        String monthTime = getString(R.string.month_time) + bean.monthTime;
                        mTodayTimeTv.setText(dayTime);
                        mMonthTimeTv.setText(monthTime);
                    }
                }
            }
        });
    }

    /**
     * 获取用户的信息
     */
    private void getUserInfo(long userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        OkHttpUtils.post().url(ChatApi.GET_PERSONAL_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PersonBean>>() {
            @Override
            public void onResponse(BaseResponse<PersonBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PersonBean bean = response.m_object;
                    if (bean != null) {
                        //设置用户信息
                        mInfoLl.setVisibility(View.VISIBLE);
                        //头像
                        String t_handImg = bean.t_handImg;
                        if (!TextUtils.isEmpty(t_handImg)) {
                            int width = DevicesUtil.dp2px(QuickVideoChatActivity.this, 38);
                            int height = DevicesUtil.dp2px(QuickVideoChatActivity.this, 38);
                            ImageLoadHelper.glideShowCircleImageWithUrl(QuickVideoChatActivity.this,
                                    t_handImg, mHeadIv, width, height);
                            //显示用户送出礼物头像
                            ImageLoadHelper.glideShowCircleImageWithUrl(QuickVideoChatActivity.this, t_handImg, mGiftHeadIv);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                            mGiftHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //昵称
                        mNickTv.setText(bean.t_nickName);
                        //等级
                        int goldLevel = bean.goldLevel;
                        if (goldLevel == 1) {
                            mGoldIv.setBackgroundResource(R.drawable.gold_one);
                        } else if (goldLevel == 2) {
                            mGoldIv.setBackgroundResource(R.drawable.gold_two);
                        } else if (goldLevel == 3) {
                            mGoldIv.setBackgroundResource(R.drawable.gold_three);
                        } else if (goldLevel == 4) {
                            mGoldIv.setBackgroundResource(R.drawable.gold_four);
                        } else if (goldLevel == 5) {
                            mGoldIv.setBackgroundResource(R.drawable.gold_five);
                        }
                        mGoldIv.setVisibility(View.VISIBLE);
                        //城市
                        mCityTv.setText(bean.t_city);
                    }
                }
            }
        });
    }

    /**
     * 用户端设置主播信息
     */
    private void setAnchorInfo(QuickAnchorBean bean) {
        mInfoLl.setVisibility(View.VISIBLE);
        //昵称
        mNickTv.setText(bean.t_nickName);
        //头像
        String t_handImg = bean.t_handImg;
        if (!TextUtils.isEmpty(t_handImg)) {
            int width = DevicesUtil.dp2px(QuickVideoChatActivity.this, 38);
            int height = DevicesUtil.dp2px(QuickVideoChatActivity.this, 38);
            ImageLoadHelper.glideShowCircleImageWithUrl(QuickVideoChatActivity.this,
                    t_handImg, mHeadIv, width, height);
            //显示用户送出礼物头像
            ImageLoadHelper.glideShowCircleImageWithUrl(QuickVideoChatActivity.this, t_handImg, mGiftHeadIv);
        } else {
            mHeadIv.setImageResource(R.drawable.default_head_img);
            mGiftHeadIv.setImageResource(R.drawable.default_head_img);
        }
        //关注 0.未关注 1.已关注
        if (bean.isFollow == 0) {
            mFocusTv.setText(getString(R.string.focus));
            mFocusTv.setVisibility(View.VISIBLE);
        }
        //认证图标
        mVerifyIv.setVisibility(View.VISIBLE);
        //年龄
        if (bean.t_age > 0) {
            mAgeTv.setText(String.valueOf(bean.t_age));
            mAgeTv.setVisibility(View.VISIBLE);
        }
        //职业
        String job = bean.t_vocation;
        if (!TextUtils.isEmpty(job)) {
            mJobTv.setText(job);
            mJobTv.setVisibility(View.VISIBLE);
        }
        //城市
        mCityTv.setText(bean.t_city);
        //设置礼物相关
    }

    @Override
    protected void onUserLinkAnchor() {
        try {
            LogUtil.i("用户走原来流程发起视频了,结束速配");
            String content = mStartQuickTv.getText().toString().trim();
            if (!content.equals(getResources().getString(R.string.start_quick))) {
                endQuick(false);
            }
            if (mTttRtcEngine != null) {
                mTttRtcEngine.leaveChannel();
                mTttRtcEngine = null;
            }
            mHaveEndQuick = true;
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mTiSDKManager != null) {
                mTiSDKManager.destroy();
            }
            if (mTimeRunnable != null) {
                mTimeRunnable = null;
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            //如果没调用结束速配
            if (!mHaveEndQuick && mFromType == Constant.FROM_ACTOR) {
                LogUtil.i("杀死页面调用结束速配");
                endQuick(false);
            }
            //注销消息接收
            JMessageClient.unRegisterEventReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理奔溃情况
     */
    private void dealCrash() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LogUtil.i("检测到奔溃");
                if (mHandler != null) {
                    LogUtil.i("奔溃情况下,调用挂断");
                    hangUp();
                } else {
                    try {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(mContext,
                                0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        if (mgr != null) {
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent);
                        }
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (Exception ep) {
                        ep.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 关闭软件盘
     */
    private void closeSoft() {
        try {
            if (mInputEt != null && mInputEt.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mInputEt.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用键盘
     */
    private void showSpan() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInputEt.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mInputEt, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }, 200);
    }


    //主播开启速配的时候,下发提示消息,只提示主播
    @Override
    protected void onQuickStartSocketHintAnchor(String hintMessage) {
        if (mFromType == Constant.FROM_ACTOR) {
            mHintTv.setText(hintMessage);
            mHintTv.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setVisibility(View.GONE);
                }
            }, 10000);
        }
    }

    //接通视频的时候,下发提示消息,主播用户都要提示
    @Override
    protected void onVideoStartSocketHint(String hintMessage) {
        if (mTextRecyclerAdapter != null && !TextUtils.isEmpty(hintMessage)
                && mFromType == Constant.FROM_ACTOR) {
            mTextRecyclerAdapter.addHintData(hintMessage);
            if (mTextListRv != null) {
                if (mTextListRv.getVisibility() != View.VISIBLE) {
                    mTextListRv.setVisibility(View.VISIBLE);
                }
                mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
            }
        }
    }

    @Override
    protected void beenShutDown() {
        super.beenShutDown();
        if (mTttRtcEngine != null) {
            mTttRtcEngine.leaveChannel();
            mTttRtcEngine = null;
            finish();
        }
    }

}
