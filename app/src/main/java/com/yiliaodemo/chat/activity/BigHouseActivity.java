package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wushuangtech.bean.TTTVideoFrame;
import com.wushuangtech.bean.VideoCompositingLayout;
import com.wushuangtech.library.Constants;
import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.model.PublisherConfiguration;
import com.wushuangtech.wstechapi.model.VideoCanvas;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.BigRoomChatTextRecyclerAdapter;
import com.yiliaodemo.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliaodemo.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliaodemo.chat.adapter.ShareRecyclerAdapter;
import com.yiliaodemo.chat.adapter.TopUserRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.BalanceBean;
import com.yiliaodemo.chat.bean.BigRoomInfoBean;
import com.yiliaodemo.chat.bean.BigRoomTextBean;
import com.yiliaodemo.chat.bean.BigRoomUserBean;
import com.yiliaodemo.chat.bean.CoverBean;
import com.yiliaodemo.chat.bean.CustomMessageBean;
import com.yiliaodemo.chat.bean.GiftBean;
import com.yiliaodemo.chat.bean.GoldBean;
import com.yiliaodemo.chat.bean.ShareLayoutBean;
import com.yiliaodemo.chat.bean.StartBean;
import com.yiliaodemo.chat.bean.UserCenterBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.dialog.InputDialogFragment;
import com.yiliaodemo.chat.dialog.UserDialogFragment;
import com.yiliaodemo.chat.dialog.UserInfoDialogFragment;
import com.yiliaodemo.chat.gift.AnimMessage;
import com.yiliaodemo.chat.gift.LPAnimationManager;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliaodemo.chat.listener.OnViewPagerListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.CodeUtil;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
//import cn.tillusory.sdk.TiSDKManager;
//import cn.tillusory.sdk.bean.TiRotation;
//import cn.tillusory.tiui.view.TiBeautyView;
import okhttp3.Call;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_ANCHOR;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_AUDIENCE;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_BAD_VERSION;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_TIMEOUT;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_UNKNOW;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_VERIFY_FAILED;
import static com.wushuangtech.library.Constants.LOG_FILTER_OFF;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间直播页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BigHouseActivity extends BaseActivity {

    //视频View
    @BindView(R.id.content_fl)
    ConstraintLayout mContentFl;
    //模糊背景
    @BindView(R.id.furry_cover_iv)
    ImageView mFurryCoverIv;
    //直播已结束
    @BindView(R.id.live_end_tv)
    TextView mLiveEndTv;
    //主播开始直播FrameLayout
    @BindView(R.id.start_live_fl)
    FrameLayout mStartLiveFl;
    //正在直播界面(主播 用户界面应该是差不多的)
    @BindView(R.id.living_layout_fl)
    FrameLayout mLivingLayoutFl;
    //-----开始直播相关---
    //封面图
    @BindView(R.id.cover_iv)
    ImageView mCoverIv;
//    //美颜UI
//    @BindView(R.id.tiBeautyTrimView)
//    TiBeautyView mTiBeautyView;//底部
    @BindView(R.id.start_bottom_ll)
    LinearLayout mStartBottomLl;
    //----------正在直播页面-------------
    //头像
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    //昵称
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    //关注人数
    @BindView(R.id.focus_number_tv)
    TextView mFocusNumberTv;
    //关注按钮
    @BindView(R.id.focus_tv)
    TextView mFocusTv;
    //观看人数
    @BindView(R.id.total_number_tv)
    TextView mTotalNumberTv;
    @BindView(R.id.top_user_rv)
    RecyclerView mTopUserRv;//顶部
    @BindView(R.id.camera_iv)
    ImageView mCameraIv;
    //文字消息
    @BindView(R.id.message_rv)
    RecyclerView mMessageRv;
    //礼物
    @BindView(R.id.gift_container_ll)
    LinearLayout mGiftContainerLl;
    //上方信息
    @BindView(R.id.top_info_ll)
    LinearLayout mTopInfoLl;

//    //美颜
//    private TiSDKManager mTiSDKManager;
    //视频聊天相关
    private TTTRtcEngine mTttRtcEngine;
    //头部用户列表
    private TopUserRecyclerAdapter mTopUserRecyclerAdapter;

    //分享
    private Tencent mTencent;
    private IWXAPI mWxApi;
    private CoverBean mCoverBean;//主播还未开播分享用的封面 昵称
    private String mActorHeadImg;//主播开播后的头像地址
    //其他
    private long mRoomId;
    private int mFromType;//是主播开播,还是用户进来看
    private int mActorId;//主播ID
    private long mChatRoomId;//聊天室ID
    //文字聊天
    private BigRoomChatTextRecyclerAdapter mTextRecyclerAdapter;
    //礼物相关
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private int mMyGoldNumber;
    private String mSelfNickName;//个人昵称
    private String mSelfHeadUrl;//个人头像
    //进来时间
    private long mEnterTime;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_big_house_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mFromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_USER);
        mRoomId = getIntent().getLongExtra(Constant.ROOM_ID, 0);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);

        initConfig();
        initTTT();
        initView();
        initTextChat();
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        mEnterTime = System.currentTimeMillis();
        //注册sdk的event用于接收各种event事件
        JMessageClient.registerEventReceiver(BigHouseActivity.this);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        //礼物
        LPAnimationManager.init(this);
        LPAnimationManager.addGiftContainer(mGiftContainerLl);
    }

    /**
     * 根据角色不同, 进来的界面也不同
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        if (mFromType == Constant.FROM_ACTOR) {
            //主播进来开播,显示开始直播页面
            mStartLiveFl.setVisibility(VISIBLE);
            //显示切换摄像头
            mCameraIv.setVisibility(VISIBLE);
//            //初始化美颜
//            mTiBeautyView.init(mTiSDKManager);
            //设置触摸关闭美颜
            mStartLiveFl.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();
                    mStartBottomLl.setVisibility(View.VISIBLE);
//                    mTiBeautyView.setVisibility(GONE);
                    return false;
                }
            });
            //获取直播自己封面
            getUserCoverImg();
        } else {
            //用户进来,显示正在直播
            mLivingLayoutFl.setVisibility(VISIBLE);
            mFurryCoverIv.setVisibility(VISIBLE);
            //不显示切换摄像头
            mCameraIv.setVisibility(GONE);
            //聊天室ID,用户直接加入聊天室
            mChatRoomId = getIntent().getLongExtra(Constant.CHAT_ROOM_ID, 0);
            joinChatRoom(mChatRoomId);
            //获取直播间信息
            getActorInfo(getUserId(), String.valueOf(mActorId));
        }

        //初始化头部用户
        mTopUserRecyclerAdapter = new TopUserRecyclerAdapter(this);
        mTopUserRv.setAdapter(mTopUserRecyclerAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mTopUserRv.setLayoutManager(manager);
        mTopUserRecyclerAdapter.setOnItemClickListener(new TopUserRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BigRoomUserBean bean) {
                if (bean.t_id > 0) {
                    showUserInfoDialog(bean.t_id);
                }
            }
        });
        //获取礼物列表
        getGiftList();
        //获取自身信息
        getSelfInfo();
    }

    /**
     * 初始化TTT
     */
    private void initTTT() {
        //生成默认参数渲染器
//        mTiSDKManager = new TiSDKManager();
        mTttRtcEngine = TTTRtcEngine.create(getApplicationContext(), Constant.TTT_APP_ID, false,
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
                    public void onReconnectServerFailed() {
                        super.onReconnectServerFailed();
                        LogUtil.i("onReconnectServerFailed");
                    }

                    @Override
                    public void onJoinChannelSuccess(String channel, final long uid) {
                        super.onJoinChannelSuccess(channel, uid);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mFromType == Constant.FROM_ACTOR) {//是主播自己
                                    LogUtil.i("主播加入房间成功: " + uid);
                                    ToastUtil.showToast(getApplicationContext(), R.string.open_success);
                                    dismissLoadingDialog();
                                    //改变界面
                                    mStartLiveFl.setVisibility(GONE);
                                    mLivingLayoutFl.setVisibility(VISIBLE);
                                } else {
                                    LogUtil.i("用户加入房间成功: " + uid);
                                }
                            }
                        });
                    }

                    @Override
                    public void onUserJoined(final long nUserId, final int identity) {
                        super.onUserJoined(nUserId, identity);
                        LogUtil.i("用户加入: nUserId = " + nUserId + "  identity: " + identity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (identity == CLIENT_ROLE_ANCHOR && mFromType == Constant.FROM_USER) {
                                    //获取远端主播的视频
                                    SurfaceView mSurfaceView = mTttRtcEngine.CreateRendererView(BigHouseActivity.this);
                                    mTttRtcEngine.setupRemoteVideo(new VideoCanvas(nUserId,
                                            Constants.RENDER_MODE_HIDDEN, mSurfaceView));
                                    mContentFl.addView(mSurfaceView);
                                }
                            }
                        });
                    }

                    @Override
                    public void onUserOffline(long nUserId, int reason) {
                        super.onUserOffline(nUserId, reason);
                        LogUtil.i("用户离线" + nUserId);
                    }

                    @Override
                    public void onLocalVideoFrameCaptured(TTTVideoFrame frame) {
                        super.onLocalVideoFrameCaptured(frame);
//                        if (mTiSDKManager != null) {
//                            frame.textureID = mTiSDKManager.renderTexture2D(frame.textureID, frame.stride,
//                                    frame.height, TiRotation.CLOCKWISE_ROTATION_0, true);
//                        }
                    }

                    @Override
                    public void onUserKicked(long uid, int reason) {
                        super.onUserKicked(uid, reason);
                        LogUtil.i("用户端收到: onUserKicked:  uid:  " + uid);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLiveEndTv.setText(getString(R.string.live_end));
                                mFurryCoverIv.setVisibility(VISIBLE);
                                mLiveEndTv.setVisibility(VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFirstRemoteVideoFrame(long uid, int width, int height) {
                        super.onFirstRemoteVideoFrame(uid, width, height);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mFromType == Constant.FROM_USER) {
                                    mFurryCoverIv.setVisibility(GONE);
                                }
                            }
                        });
                    }
                });
        if (mTttRtcEngine != null) {
            mTttRtcEngine.setLogFilter(LOG_FILTER_OFF);
            mTttRtcEngine.enableVideo();
            mTttRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            if (mFromType == Constant.FROM_ACTOR) {//主播
                mTttRtcEngine.muteLocalAudioStream(false);
                mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
                mTttRtcEngine.startPreview();
                delayShow();
            } else {//观众
                LogUtil.i("用户加入:  房间号: " + mRoomId);
                mTttRtcEngine.muteLocalAudioStream(true);
                mTttRtcEngine.setClientRole(CLIENT_ROLE_AUDIENCE);
                mTttRtcEngine.joinChannel("", String.valueOf(mRoomId), Integer.parseInt(getUserId()));
            }
        }
    }

    /**
     * 初始化文字聊天
     */
    private void initTextChat() {
        mTextRecyclerAdapter = new BigRoomChatTextRecyclerAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mMessageRv.setLayoutManager(manager);
        mMessageRv.setAdapter(mTextRecyclerAdapter);
    }

    /**
     * 加入聊天室
     */
    private void joinChatRoom(long mChatRoomId) {
        if (mChatRoomId > 0) {
            ChatRoomManager.enterChatRoom(mChatRoomId, new RequestCallback<Conversation>() {
                @Override
                public void gotResult(int responseCode, String responseMessage, Conversation conversation) {
                    if (responseCode == 0 || responseCode == 851003) {
                        LogUtil.i("用户加入聊天室成功");
                    }
                }
            });
        }
    }

    /**
     * 上传自己的流
     */
    private void delayShow() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                SurfaceView mLocalSurfaceView = mTttRtcEngine.CreateRendererView(BigHouseActivity.this);
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
//                        if (mTiSDKManager != null) {
//                            mTiSDKManager.destroy();
//                        }
                    }
                });
            }
        }, 10);
    }

    /**
     * 获取自己封面
     */
    private void getUserCoverImg() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_COVER_IMG)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<CoverBean>>() {
            @Override
            public void onResponse(BaseResponse<CoverBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mCoverBean = response.m_object;
                    if (mCoverBean != null && !TextUtils.isEmpty(mCoverBean.t_cover_img)) {
                        ImageLoadHelper.glideShowCornerImageWithUrl(BigHouseActivity.this,
                                mCoverBean.t_cover_img, mCoverIv);
                    }
                }
            }
        });
    }

    @OnClick({R.id.close_iv, R.id.beauty_tv, R.id.camera_tv, R.id.start_live_tv, R.id.focus_tv,
            R.id.live_close_iv, R.id.total_number_tv, R.id.head_iv, R.id.qq_iv, R.id.we_chat_iv,
            R.id.we_circle_iv, R.id.qzone_iv, R.id.share_iv, R.id.camera_iv, R.id.input_tv,
            R.id.gift_iv, R.id.deal_one_tv, R.id.connect_tv, R.id.living_layout_fl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv: {//开始直播页面,关闭页面
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;
                }
                finish();
                break;
            }
            case R.id.deal_one_tv: {//帮助中心
                Intent intent = new Intent(getApplicationContext(), HelpCenterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.connect_tv: {//联系客服
                CodeUtil.jumpToQQ(BigHouseActivity.this);
                break;
            }
            case R.id.beauty_tv: {//开始直播页面,设置美颜,开启美颜面板
                mStartBottomLl.setVisibility(GONE);
//                mTiBeautyView.setVisibility(VISIBLE);
                break;
            }
            case R.id.camera_iv:
            case R.id.camera_tv: {//开始直播页面,切换摄像头
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.switchCamera();
                }
                break;
            }
            case R.id.start_live_tv: {//开始直播页面,开始直播
                showLoadingDialog();
                startLive();
                break;
            }
            case R.id.focus_tv: {//关注
                if (mActorId > 0) {
                    saveFollow(mActorId);
                }
                break;
            }
            case R.id.live_close_iv: {//关闭直播
                if (mFromType == Constant.FROM_ACTOR) {
                    actorCloseLive();
                } else {
                    userExitRoom();
                }
                break;
            }
            case R.id.total_number_tv: {//贡献值 在线人数
                showUserDialog();
                break;
            }
            case R.id.head_iv: {//主播信息
                if (mActorId > 0) {
                    showUserInfoDialog(mActorId);
                }
                break;
            }
            case R.id.qq_iv: {//QQ分享
                shareUrlToQQ(false);
                break;
            }
            case R.id.we_chat_iv: {//微信分享
                shareUrlToWeChat(false, false);
                break;
            }
            case R.id.we_circle_iv: {//朋友圈
                shareUrlToWeChat(true, false);
                break;
            }
            case R.id.qzone_iv: {//QQ空间
                shareUrlToQZone(false);
                break;
            }
            case R.id.share_iv: {//开播后的分享
                showShareDialog();
                break;
            }
            case R.id.input_tv: {//输入文字
                showInputDialog();
                break;
            }
            case R.id.gift_iv: {//礼物
                showRewardDialog();
                break;
            }
            case R.id.living_layout_fl: {//清除
                if (mMessageRv.getVisibility() == VISIBLE) {
                    mMessageRv.setVisibility(View.INVISIBLE);
                } else {
                    mMessageRv.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    /**
     * 显示输入文字对话框
     */
    private void showInputDialog() {
        final InputDialogFragment inputDialogFragment = new InputDialogFragment();
        inputDialogFragment.setOnTextSendListener(new InputDialogFragment.OnTextSendListener() {
            @Override
            public void onTextSend(String text) {
                if (!TextUtils.isEmpty(text)) {
                    //发送文字
                    LogUtil.i("发送文字: " + text);
                    sendTextMessage(text);
                    inputDialogFragment.dismiss();
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_input_text_message);
                }
            }
        });
        inputDialogFragment.show(getSupportFragmentManager(), "tag");
    }

    /**
     * 用户退出房间
     */
    private void userExitRoom() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.USER_QUIT_BIG_ROOM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (mTttRtcEngine != null) {
                        mTttRtcEngine.leaveChannel();
                        mTttRtcEngine = null;
                    }
                    // 离开聊天室
                    ChatRoomManager.leaveChatRoom(mChatRoomId, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                LogUtil.i("用户退出聊天室成功");
                            }
                        }
                    });
                    //删除聊天室会话
                    JMessageClient.deleteChatRoomConversation(mChatRoomId);
                    finish();
                }
            }
        });
    }

    /**
     * 主播关闭或者暂停直播
     */
    private void actorCloseLive() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.CLOSE_LIVE_TELECAST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getApplicationContext(), R.string.have_finish_live);
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
     * 主播开启直播
     */
    private void startLive() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.OPEN_LIVE_TELECAST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<StartBean>>() {
            @Override
            public void onResponse(BaseResponse<StartBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        StartBean startBean = response.m_object;
                        if (startBean.roomId > 0 && startBean.chatRoomId > 0) {
                            //聊天室ID
                            mChatRoomId = startBean.chatRoomId;
                            //加入聊天室
                            joinChatRoom(mChatRoomId);
                            //推流地址
                            String pushUrl = Constant.TTT_PUSH_ADDRESS + getUserId() + "/" + String.valueOf(startBean.roomId);
                            LogUtil.i("推流地址: " + pushUrl);
                            PublisherConfiguration configuration = new PublisherConfiguration();
                            configuration.setPushUrl(pushUrl);
                            mTttRtcEngine.configPublisher(configuration);
                            //主播加入房间
                            mTttRtcEngine.joinChannel("", String.valueOf(startBean.roomId), Integer.parseInt(getUserId()));

                            // 发送SEI
                            VideoCompositingLayout layout = new VideoCompositingLayout();
                            layout.regions = buildRemoteLayoutLocation();
                            TTTRtcEngine.getInstance().setVideoCompositingLayout(layout);

                            //开启直播成功后获取直播间信息
                            getActorInfo(getUserId(), getUserId());
                        } else {
                            dismissLoadingDialog();
                        }
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.open_fail);
                        }
                        dismissLoadingDialog();
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.open_fail);
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.open_fail);
                dismissLoadingDialog();
            }

        });
    }

    /**
     * 获取主播 房间内相关信息
     */
    private void getActorInfo(String userId, String anchorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("anchorId", anchorId);
        OkHttpUtils.post().url(ChatApi.USER_MIX_BIG_ROOM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BigRoomInfoBean<BigRoomUserBean>>>() {
            @Override
            public void onResponse(BaseResponse<BigRoomInfoBean<BigRoomUserBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BigRoomInfoBean<BigRoomUserBean> roomInfoBean = response.m_object;
                    if (roomInfoBean != null) {
                        mTopInfoLl.setVisibility(VISIBLE);
                        //头像
                        mActorHeadImg = roomInfoBean.t_handImg;
                        if (!TextUtils.isEmpty(mActorHeadImg)) {
                            ImageLoadHelper.glideShowCircleImageWithUrl(BigHouseActivity.this,
                                    mActorHeadImg, mHeadIv);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //昵称
                        mNickTv.setText(roomInfoBean.t_nickName);
                        //关注人数
                        int number = roomInfoBean.followNumber;
                        if (number > 0) {
                            String content;
                            if (number < 10000) {
                                content = String.valueOf(number);
                            } else {
                                BigDecimal old = new BigDecimal(number);
                                BigDecimal ten = new BigDecimal(10000);
                                BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
                                content = res + mContext.getString(R.string.number_ten_thousand);
                            }
                            mFocusNumberTv.setText(content);
                        }
                        //是否关注   0.未关注 1.已关注
                        if (roomInfoBean.isFollow == 0) {
                            mFocusTv.setVisibility(VISIBLE);
                        }
                        //观看人数
                        int viewNumber = roomInfoBean.viewer;
                        String content;
                        if (viewNumber < 10000) {
                            content = String.valueOf(viewNumber);
                        } else {
                            BigDecimal old = new BigDecimal(viewNumber);
                            BigDecimal ten = new BigDecimal(10000);
                            BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
                            content = res + mContext.getString(R.string.number_ten_thousand);
                        }
                        mTotalNumberTv.setText(content);
                        //用户
                        List<BigRoomUserBean> userBeanList = roomInfoBean.devoteList;
                        if (userBeanList != null && userBeanList.size() > 0) {
                            mTopUserRecyclerAdapter.loadData(userBeanList);
                        }
                        //提示
                        String warning = roomInfoBean.warning;
                        if (!TextUtils.isEmpty(warning) && mTextRecyclerAdapter != null) {
                            BigRoomTextBean textBean = new BigRoomTextBean();
                            textBean.content = warning;
                            textBean.type = 3;
                            mTextRecyclerAdapter.addData(textBean);
                        }
                        //是否开播  0.未开播 1.已开播
                        if (roomInfoBean.isDebut == 0) {
                            mLiveEndTv.setText(getString(R.string.live_not_start));
                            mLiveEndTv.setVisibility(VISIBLE);
                        }
                    }
                }
            }
        });
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
                        mFocusTv.setVisibility(GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
//            if (mTiSDKManager != null) {
//                mTiSDKManager.destroy();
//            }
            //注销消息接收
            JMessageClient.unRegisterEventReceiver(BigHouseActivity.this);
            //释放礼物
            LPAnimationManager.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //------------------贡献值 在线观众------------------------

    /**
     * 显示打赏礼物Dialog
     */
    private void showUserDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ACTOR_ID, mActorId);
        UserDialogFragment userDialog = new UserDialogFragment();
        userDialog.setArguments(bundle);
        userDialog.show(getSupportFragmentManager(), "tag");
    }

    /**
     * 显示用户信息Dialog
     */
    private void showUserInfoDialog(int mActorId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ACTOR_ID, mActorId);
        UserInfoDialogFragment userDialog = new UserInfoDialogFragment();
        userDialog.setArguments(bundle);
        userDialog.show(getSupportFragmentManager(), "tag");
    }

    //-----------人数变化------------

    @Override
    protected void onBigRoomCountChange(int count, String sendUserName) {
        LogUtil.i("大房间人数变化: " + count);
        mTotalNumberTv.setText(String.valueOf(count));
        if (!TextUtils.isEmpty(sendUserName)) {
            BigRoomTextBean textBean = new BigRoomTextBean();
            textBean.nickName = sendUserName;
            textBean.type = 2;
            if (mTextRecyclerAdapter != null) {
                mTextRecyclerAdapter.addData(textBean);
                if (mMessageRv != null) {
                    mMessageRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                }
            }
        }
    }

    //----------------------分享----------------------

    /**
     * 分享链接到QQ
     */
    private void shareUrlToQQ(boolean haveStart) {
        if (mTencent != null) {
            String url = ChatApi.SHARE_URL + getUserId();
            String title;
            String des;
            String mineUrl;
            if (haveStart) {//已经开播
                String actorNick = mNickTv.getText().toString().trim();
                title = getString(R.string.share_five) + actorNick + getString(R.string.share_six);
                des = getString(R.string.share_four);
                mineUrl = mActorHeadImg;
            } else {//未开播
                if (mCoverBean != null) {
                    title = getString(R.string.hao_one) + mCoverBean.t_nickName + getString(R.string.share_one);
                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
                    mineUrl = mCoverBean.t_cover_img;
                } else {
                    title = getString(R.string.hao_one) + getString(R.string.share_one);
                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
                    mineUrl = getString(R.string.empty_text);
                }
            }

            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);// 标题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mineUrl);// 图片地址
            mTencent.shareToQQ(BigHouseActivity.this, params, new MyUIListener());
        }
    }

    /**
     * 分享链接到QQ空间
     */
    private void shareUrlToQZone(boolean haveStart) {
        if (mTencent != null) {
            String url = ChatApi.SHARE_URL + getUserId();
            String title;
            String des;
            String mineUrl;
            if (haveStart) {//已经开播
                String actorNick = mNickTv.getText().toString().trim();
                title = getString(R.string.share_five) + actorNick + getString(R.string.share_six);
                des = getString(R.string.share_four);
                mineUrl = mActorHeadImg;
            } else {//未开播
                if (mCoverBean != null) {
                    title = getString(R.string.hao_one) + mCoverBean.t_nickName + getString(R.string.share_one);
                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
                    mineUrl = mCoverBean.t_cover_img;
                } else {
                    title = getString(R.string.hao_one) + getString(R.string.share_one);
                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
                    mineUrl = getString(R.string.empty_text);
                }
            }

            Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 标题
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
            ArrayList<String> images = new ArrayList<>();
            images.add(mineUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);// 图片地址
            mTencent.shareToQzone(BigHouseActivity.this, params, new MyUIListener());
        }
    }

    class MyUIListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            ToastUtil.showToast(getApplicationContext(), R.string.share_success);
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.showToast(getApplicationContext(), R.string.share_fail);
        }

        @Override
        public void onCancel() {
            ToastUtil.showToast(getApplicationContext(), R.string.share_cancel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new MyUIListener());
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
            if (resultCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE
                    || resultCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE
                    || resultCode == com.tencent.connect.common.Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, new MyUIListener());
            }
        }
    }

    /**
     * 分享url到微信
     */
    private void shareUrlToWeChat(boolean isFriendCricle, boolean haveStart) {
        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
            return;
        }

        String url = ChatApi.SHARE_URL + getUserId();
        String title;
        String des;
        if (haveStart) {//已经开播
            String actorNick = mNickTv.getText().toString().trim();
            title = getString(R.string.share_five) + actorNick + getString(R.string.share_six);
            des = getString(R.string.share_four);
        } else {//未开播
            if (mCoverBean != null) {
                title = getString(R.string.hao_one) + mCoverBean.t_nickName + getString(R.string.share_one);
                des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
            } else {
                title = getString(R.string.hao_one) + getString(R.string.share_one);
                des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
            }
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = des;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //WXSceneTimeline朋友圈    WXSceneSession聊天界面
        req.scene = isFriendCricle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;//聊天界面
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());
        mWxApi.sendReq(req);
        boolean res = mWxApi.sendReq(req);
        if (res) {
            AppManager.getInstance().setIsMainPageShareQun(false);
        }
    }

    /**
     * 显示分享dialog
     */
    private void showShareDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_share_layout, null);
        setDialogView(view, mDialog);
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
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        GridLayoutManager manager = new GridLayoutManager(getBaseContext(), 4);
        content_rv.setLayoutManager(manager);
        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(BigHouseActivity.this);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new ShareRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {//微信
                    shareUrlToWeChat(false, true);
                } else if (position == 1) {//朋友圈
                    shareUrlToWeChat(true, true);
                } else if (position == 2) {//QQ
                    shareUrlToQQ(true);
                } else if (position == 3) {//QQ空间
                    shareUrlToQZone(true);
                }
                mDialog.dismiss();
            }
        });
        List<ShareLayoutBean> beans = new ArrayList<>();
        beans.add(new ShareLayoutBean("微信", R.drawable.share_wechat));
        beans.add(new ShareLayoutBean("朋友圈", R.drawable.share_wechatfriend));
        beans.add(new ShareLayoutBean("QQ", R.drawable.share_qq));
        beans.add(new ShareLayoutBean("QQ空间", R.drawable.share_qzone));
        adapter.loadData(beans);
    }

    //---------------------------------分享结束-------------------------

    //--------------------------------消息  礼物-----------------------------
    public void onEventMainThread(ChatRoomMessageEvent event) {
        List<Message> messages = event.getMessages();
        if (messages != null && messages.size() > 0 && messages.size() <= 10) {
            LogUtil.i("新的聊天室信息");
            for (Message message : messages) {
                switch (message.getContentType()) {
                    case text: {//文本
                        long messageTime = message.getCreateTime();
                        if (messageTime > mEnterTime) {
                            LogUtil.i("礼物时间大于进入时间");
                            addTextAdapterBean(message);
                        } else {
                            LogUtil.i("礼物时间小于进入时间");
                        }
                        break;
                    }
                    case custom: {//自定义
                        LogUtil.i("新的聊天室自定义消息: ");
                        CustomContent customContent = (CustomContent) message.getContent();
                        long messageTime = message.getCreateTime();
                        if (messageTime > mEnterTime) {
                            LogUtil.i("礼物时间大于进入时间");
                            parseCustomMessage(customContent);
                        } else {
                            LogUtil.i("礼物时间小于进入时间");
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 发送消息
     */
    private void sendTextMessage(String text) {
        // 发送聊天室消息
        Conversation conv = JMessageClient.getChatRoomConversation(mChatRoomId);
        if (null == conv) {
            conv = Conversation.createChatRoomConversation(mChatRoomId);
        }
        if (conv != null) {
            final Message message = conv.createSendTextMessage(text);
            if (message != null) {
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (0 == responseCode) {
                            LogUtil.i("聊天室消息发送成功");
                            addTextAdapterBean(message);
                        } else {
                            LogUtil.i("聊天室消息发送失败responseCode: " + responseCode + "  " + responseMessage);
                        }
                    }
                });
                JMessageClient.sendMessage(message);
            }
        }
    }

    /**
     * 添加文字bean
     */
    private void addTextAdapterBean(Message message) {
        TextContent textContent = (TextContent) message.getContent();
        String text = textContent.getText();
        LogUtil.i("新的聊天室文本消息: " + text);
        UserInfo userInfo = message.getFromUser();
        if (userInfo != null && !TextUtils.isEmpty(text) && !TextUtils.isEmpty(userInfo.getNickname())) {
            BigRoomTextBean textBean = new BigRoomTextBean();
            textBean.nickName = userInfo.getNickname();
            textBean.content = text;
            textBean.type = 1;
            mTextRecyclerAdapter.addData(textBean);
            mMessageRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
        }
    }

    /**
     * 发送Im自定义消息
     */
    private void sendIMCustomMessage(CustomMessageBean customMessageBean) {
        // 发送聊天室消息
        Conversation conv = JMessageClient.getChatRoomConversation(mChatRoomId);
        if (null == conv) {
            conv = Conversation.createChatRoomConversation(mChatRoomId);
        }
        if (conv != null) {
            String json = JSON.toJSONString(customMessageBean);
            CustomContent customContent = new CustomContent();
            customContent.setStringValue("custom", json);
            Message message = conv.createSendMessage(customContent);
            if (message != null) {
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
                if (bean.type.equals("0")) {//金币
                    bean.gift_name = getResources().getString(R.string.gold);
                }
                showGiftAnim(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(BigHouseActivity.this);
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
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(BigHouseActivity.this);
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
                        bean.nickName = mSelfNickName;
                        bean.headUrl = mSelfHeadUrl;

                        sendIMCustomMessage(bean);
                        showGiftAnim(bean);
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
                        bean.nickName = mSelfNickName;
                        bean.headUrl = mSelfHeadUrl;

                        sendIMCustomMessage(bean);
                        showGiftAnim(bean);
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
     * 显示礼物动画
     */
    private void showGiftAnim(CustomMessageBean customMessageBean) {
        AnimMessage animMessage = new AnimMessage();
        animMessage.userName = customMessageBean.nickName;
        animMessage.headUrl = customMessageBean.headUrl;
        animMessage.giftImgUrl = customMessageBean.gift_gif_url;
        if (customMessageBean.type.equals("1")) {// 0-金币 1-礼物
            animMessage.giftNum = 1;
        } else {
            animMessage.giftNum = customMessageBean.gold_number;
        }
        animMessage.giftName = customMessageBean.gift_name;
        animMessage.giftType = customMessageBean.type;
        LPAnimationManager.addAnimalMessage(animMessage);
    }

    /**
     * 获取个人自身头像 昵称信息
     */
    private void getSelfInfo() {
        String imgUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
        String saveNick = SharedPreferenceHelper.getAccountInfo(mContext).nickName;
        if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(saveNick)) {
            mSelfHeadUrl = imgUrl;
            mSelfNickName = saveNick;
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.INDEX)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
            @Override
            public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UserCenterBean bean = response.m_object;
                    if (bean != null) {
                        //头像
                        mSelfHeadUrl = bean.handImg;
                        //昵称
                        mSelfNickName = bean.nickName;
                    }
                }
            }
        });
    }

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

    @Override
    protected void beenShutDown() {
        super.beenShutDown();
        if (mTttRtcEngine != null) {
            mTttRtcEngine.leaveChannel();
            mTttRtcEngine = null;
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

}
