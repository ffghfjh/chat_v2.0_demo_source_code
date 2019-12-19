package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliaodemo.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActorPlayBean;
import com.yiliaodemo.chat.bean.BalanceBean;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.GiftBean;
import com.yiliaodemo.chat.bean.GoldBean;
import com.yiliaodemo.chat.bean.InfoRoomBean;
import com.yiliaodemo.chat.bean.LabelBean;
import com.yiliaodemo.chat.bean.VideoSignBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliaodemo.chat.listener.OnViewPagerListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播视频播放页面
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorVideoPlayActivity extends BaseActivity {


    @BindView(R.id.video_view)
    PLVideoView mVideoView;
    @BindView(R.id.cover_iv)
    ImageView mCoverIv;
    @BindView(R.id.small_head_iv)
    ImageView mSmallHeadIv;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.we_chat_tv)
    TextView mWeChatTv;
    @BindView(R.id.we_chat_fl)
    View mWeChatFl;
    @BindView(R.id.video_chat_tv)
    TextView mVideoChatTv;
    @BindView(R.id.right_ll)
    View mRightLl;
    @BindView(R.id.left_rl)
    View mLeftRl;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.love_tv)
    TextView mLoveTv;
    @BindView(R.id.see_tv)
    TextView mSeeTv;
    @BindView(R.id.gold_price_tv)
    TextView mGoldPriceTv;
    @BindView(R.id.status_free_tv)
    TextView mStatusFreeTv;
    @BindView(R.id.status_offline_tv)
    TextView mStatusOfflineTv;
    @BindView(R.id.status_busy_tv)
    TextView mStatusBusyTv;
    @BindView(R.id.focus_fl)
    View mFocusFl;
    @BindView(R.id.paly_stop)
    ImageView stopView;

    private int mActorId;//主播id
    private int mFromWhere = 2;//从哪里进入,2我的相册
    private ActorPlayBean mActorPlayBean;
    private boolean mToReport = false;
    private String mCoverUrl = "";//图片地址
    //礼物相关
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private int mMyGoldNumber;
    //大房间直播
    private InfoRoomBean mInfoRoomBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_actor_video_play_layout_1);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mFromWhere = getIntent().getIntExtra(Constant.FROM_WHERE, Constant.FROM_GIRL);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        int fileId = getIntent().getIntExtra(Constant.FILE_ID, 0);

        initVideoView();

        if (mFromWhere == Constant.FROM_ALBUM
                || mFromWhere == Constant.FROM_ACTOR_VIDEO || mFromWhere == Constant.FROM_ACTIVE) {
            String mVideoUrl = getIntent().getStringExtra(Constant.VIDEO_URL);
            mCoverUrl = getIntent().getStringExtra(Constant.COVER_URL);
            loadCoverImage(mCoverUrl);
            playVideoWithUrl(mVideoUrl);

            if (mFromWhere == Constant.FROM_ALBUM || String.valueOf(mActorId).equals(getUserId())) {//是主播自己看自己
                //mComplainIv.setVisibility(View.GONE);
                mRightLl.setVisibility(View.GONE);
                mLeftRl.setVisibility(View.GONE);
            } else {
                //mComplainIv.setVisibility(View.VISIBLE);
                mRightLl.setVisibility(View.VISIBLE);
                mLeftRl.setVisibility(View.VISIBLE);
            }

        } else {//从首页
            //mComplainIv.setVisibility(View.VISIBLE);
            mRightLl.setVisibility(View.VISIBLE);
            mLeftRl.setVisibility(View.VISIBLE);
        }

        //查询类型  0.相册视频 1.动态视频
        int queryType = 0;
        if (mFromWhere == Constant.FROM_ACTIVE) {
            queryType = 1;
        }

        getActorInfo(fileId, queryType);
        if (fileId > 0) {
            addSeeTime(fileId);
        }
        getGiftList();
    }

    /**
     * 获取主播数据
     */
    private void getActorInfo(int fileId, int queryType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("albumId", fileId > 0 ? String.valueOf(fileId) : "");
        paramMap.put("queryType", String.valueOf(queryType));
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_PLAY_PAGE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorPlayBean<LabelBean, InfoRoomBean>>>() {
            @Override
            public void onResponse(BaseResponse<ActorPlayBean<LabelBean, InfoRoomBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ActorPlayBean<LabelBean, InfoRoomBean> playBean = response.m_object;
                    if (playBean != null) {
                        mActorPlayBean = playBean;
                        //处理头像
                        String handImg = playBean.t_handImg;
                        if (!TextUtils.isEmpty(handImg)) {
                            //计算头像resize
                            int smallOverWidth = DevicesUtil.dp2px(mContext, 42);
                            int smallOverHeight = DevicesUtil.dp2px(mContext, 42);
                            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mSmallHeadIv,
                                    smallOverWidth, smallOverHeight);
                        }
                        //昵称
                        String nick = playBean.t_nickName;
                        if (!TextUtils.isEmpty(nick)) {
                            mNameTv.setText(nick);
                        }
                        //标题
                        String title = playBean.t_title;
                        if (!TextUtils.isEmpty(title)) {
                            mTitleTv.setText(title);
                        }
                        //点赞总数
                        int laudtotal = playBean.laudtotal;
                        mLoveTv.setText(String.valueOf(laudtotal));
                        //当前用户是否给查看人点赞 0:未点赞 1.已点赞
                        int isLaud = playBean.isLaud;
                        if (isLaud == 0) {
                            mLoveTv.setSelected(false);
                        } else {
                            mLoveTv.setSelected(true);
                        }
                        //查看次数
                        mSeeTv.setText(String.valueOf(playBean.t_see_count));
                        //视频聊天金币
                        int videoGold = playBean.videoGold;
                        if (videoGold > 0) {
                            String content = videoGold + getResources().getString(R.string.price);
                            mGoldPriceTv.setText(content);
                        }
                        //是否关注  0:未关注 1：已关注
                        int isFollow = playBean.isFollow;
                        if (isFollow == 0) {
                            mFocusFl.setVisibility(View.VISIBLE);
                        } else {
                            mFocusFl.setVisibility(View.INVISIBLE);
                        }
                        //状态 在线状态 0.空闲1.忙碌2.离线
                        int t_onLine = playBean.t_onLine;
                        if (t_onLine == 0) {
                            mStatusFreeTv.setVisibility(View.VISIBLE);
                            mStatusBusyTv.setVisibility(View.GONE);
                            mStatusOfflineTv.setVisibility(View.GONE);
                        } else if (t_onLine == 1) {
                            mStatusBusyTv.setVisibility(View.VISIBLE);
                            mStatusFreeTv.setVisibility(View.GONE);
                            mStatusOfflineTv.setVisibility(View.GONE);
                        } else if (t_onLine == 2) {
                            mStatusOfflineTv.setVisibility(View.VISIBLE);
                            mStatusBusyTv.setVisibility(View.GONE);
                            mStatusFreeTv.setVisibility(View.GONE);
                        } else {
                            mStatusFreeTv.setVisibility(View.GONE);
                            mStatusBusyTv.setVisibility(View.GONE);
                            mStatusOfflineTv.setVisibility(View.GONE);
                        }

                        //微信
                        int isSee = playBean.isSee;
                        String weChat = playBean.t_weixin;
                        if (!TextUtils.isEmpty(weChat)) {
                            if (isSee == 1) {//看过
                                String content = getResources().getString(R.string.we_chat_number_des) + weChat;
                                mWeChatTv.setText(content);
                                mWeChatTv.setVisibility(View.VISIBLE);
                                mWeChatFl.setVisibility(View.GONE);
                            } else {
                                mWeChatTv.setVisibility(View.GONE);
                                mWeChatFl.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mWeChatTv.setVisibility(View.GONE);
                            mWeChatFl.setVisibility(View.GONE);
                        }
                        //播放视频,从首页进来
                        if (mFromWhere == Constant.FROM_GIRL) {
                            mCoverUrl = playBean.t_video_img;
                            String mVideoUrl = playBean.t_addres_url;
                            if (!TextUtils.isEmpty(mCoverUrl)) {
                                loadCoverImage(mCoverUrl);
                            }
                            if (!TextUtils.isEmpty(mVideoUrl)) {
                                playVideoWithUrl(mVideoUrl);
                            }
                        }
                        //处理大房间
                        mInfoRoomBean = playBean.bigRoomData;
                        if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                                && mInfoRoomBean.t_chat_room_id > 0) {
                            mVideoChatTv.setText(getString(R.string.enter_house));
                        }
                    }
                }
            }
        });

    }

    /**
     * 新增查看次数
     */
    private void addSeeTime(int fileId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("fileId", String.valueOf(fileId));
        OkHttpUtils.post().url(ChatApi.ADD_QUERY_DYNAMIC_COUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {

            }
        });
    }

    /**
     * 加载封面图
     */
    private void loadCoverImage(String coverUrl) {
        if (!TextUtils.isEmpty(coverUrl)) {
            mCoverIv.setVisibility(View.VISIBLE);
            ImageLoadHelper.glideShowImageWithUrl(ActorVideoPlayActivity.this, coverUrl, mCoverIv);
        }
    }

    /**
     * 初始化VideoView
     */
    private void initVideoView() {
        //加载封面图
        mVideoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                if (!mToReport) {
                    mCoverIv.setVisibility(View.GONE);
                }
            }
        });
        mWeChatTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActorPlayBean != null) {
                    //获取剪贴板管理器
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", mActorPlayBean.t_weixin);
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        ToastUtil.showToast(getApplicationContext(), R.string.copy_success);
                    }
                }
                return false;
            }
        });
    }

    private void playVideoWithUrl(String url) {
        if(stopView!=null){
            stopView.setVisibility(View.GONE);
        }
        if (mVideoView != null && !TextUtils.isEmpty(url)) {
            mVideoView.setVideoPath(url);
            mVideoView.requestFocus();
            mVideoView.setLooping(true);
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(stopView!=null){
            stopView.setVisibility(View.VISIBLE);
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mToReport) {
            loadCoverImage(mCoverUrl);
        }
    }

    @OnClick({ R.id.video_chat_tv, R.id.info_ll, R.id.we_chat_fl, R.id.back_iv,
            R.id.small_head_iv, R.id.focus_fl, R.id.love_tv, R.id.gift_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_chat_tv: {//视频聊天
                if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                        && mInfoRoomBean.t_chat_room_id > 0) {
                    Intent intent = new Intent(getApplicationContext(), BigHouseActivity.class);
                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                    intent.putExtra(Constant.ACTOR_ID, mActorId);
                    intent.putExtra(Constant.ROOM_ID, mInfoRoomBean.t_room_id);
                    intent.putExtra(Constant.CHAT_ROOM_ID, mInfoRoomBean.t_chat_room_id);
                    startActivity(intent);
                } else {
                    if (getUserSex() == 0) {
                        ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                        return;
                    }
                    getSign();
                }
                break;
            }
            case R.id.info_ll:
            case R.id.small_head_iv: {//个人信息
                //Intent intent = new Intent(mContext, ActorInfoActivity.class);
                Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.we_chat_fl: {//查看微信号
                if (getUserSex() == 0) {
                    ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                if (mActorPlayBean != null && mActorPlayBean.isSee == 0) {
                    showSeeWeChatRemindDialog();
                }
                break;
            }
            case R.id.back_iv: {
                finish();
                break;
            }
            case R.id.focus_fl: {//关注
                if (getUserSex() == 0) {
                    ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                if (mActorId > 0) {
                    saveFollow(mActorId);
                }
                break;
            }
            case R.id.love_tv: {//点赞
                if (mActorId > 0) {
                    if (!mLoveTv.isSelected()) {//没有点赞过
                        addLike();
                    } else {
                        cancelLike();
                    }
                }
                break;
            }
            case R.id.gift_iv: {//赠送礼物
                if (getUserSex() == 0) {
                    ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                if (mActorId > 0) {
                    showRewardDialog();
                }
                break;
            }
        }
    }

    /**
     * 点赞
     */
    private void addLike() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverLaudUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.ADD_LAUD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mLoveTv.setSelected(true);
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
    private void cancelLike() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.CANCEL_LAUD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mLoveTv.setSelected(false);
                    String content = mLoveTv.getText().toString().trim();
                    int number = Integer.parseInt(content) - 1;
                    mLoveTv.setText(String.valueOf(number));
                }
            }
        });
    }

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anthorId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        int onlineState = signBean.onlineState;
                        if (onlineState == 1) {//1.余额刚刚住够
                            showGoldJustEnoughDialog(mRoomId);
                        } else {
                            requestChat(mRoomId);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 显示金币刚好够dialog
     */
    private void showGoldJustEnoughDialog(int mRoomId) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_one_minute_layout, null);
        setGoldDialogView(view, mDialog, mRoomId);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setGoldDialogView(View view, final Dialog mDialog, final int mRoomId) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanRoom();
                mDialog.dismiss();
            }
        });
        //是 发起聊天
        TextView yes_tv = view.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestChat(mRoomId);
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空房间
                cleanRoom();
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 清空房间
     */
    private void cleanRoom() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.USER_HANG_UP_LINK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("清空房间成功");
                }
            }
        });
    }

    /**
     * 用户对主播发起聊天
     */
    private void requestChat(final int roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverLinkUserId", String.valueOf(mActorId));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(getApplicationContext(), VideoChatOneActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);//主播ID
                        startActivity(intent);
                        finish();
                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(ActorVideoPlayActivity.this);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
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
     * 显示查看微信号提醒
     */
    private void showSeeWeChatRemindDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pay_video_layout, null);
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
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        des_tv.setText(getResources().getString(R.string.see_we_chat_number_des_one));
        //金币
        TextView gold_tv = view.findViewById(R.id.gold_tv);
        int gold = mActorPlayBean.t_weixin_gold;
        if (gold > 0) {
            String content = gold + getResources().getString(R.string.gold);
            gold_tv.setText(content);
        }
        //升级
        ImageView update_iv = view.findViewById(R.id.update_iv);
        update_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VipCenterActivity.class);
                mContext.startActivity(intent);
                mDialog.dismiss();
            }
        });
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeWeChat();
                mDialog.dismiss();
            }
        });
    }

    /**
     * 查看微信号码
     */
    private void seeWeChat() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.SEE_WEI_XIN_CONSUME)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                            mWeChatFl.setVisibility(View.GONE);
                            showSeeWeChatCopyDialog();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        }
                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialog(ActorVideoPlayActivity.this);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
     * 显示查看微信成功复制Dialog
     */
    private void showSeeWeChatCopyDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_we_chat_number_layout, null);
        setCopyDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setCopyDialogView(View view, final Dialog mDialog) {
        //描述
        TextView gold_tv = view.findViewById(R.id.des_tv);
        final String number = mActorPlayBean.t_weixin;
        String content = getResources().getString(R.string.we_chat_number_des_one) + number;
        gold_tv.setText(content);
        //复制
        TextView copy_tv = view.findViewById(R.id.copy_tv);
        copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", number);
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtil.showToast(getApplicationContext(), R.string.copy_success);
                }
                String content = getResources().getString(R.string.we_chat_number_des) + number;
                mWeChatTv.setText(content);
                mWeChatTv.setVisibility(View.VISIBLE);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 显示投诉举报Popup
     */
    private void showComplainPopup() {
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_complain_layout, null, false);
        final PopupWindow window = new PopupWindow(contentView, DevicesUtil.dp2px(getApplicationContext(), 81),
                DevicesUtil.dp2px(getApplicationContext(), 81), true);
        TextView complainTv = contentView.findViewById(R.id.complain_tv);
        complainTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToReport = true;
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                startActivity(intent);
                window.dismiss();
            }
        });
        TextView reportTv = contentView.findViewById(R.id.report_tv);
        reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToReport = true;
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                startActivity(intent);
                window.dismiss();
            }
        });
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        //window.showAsDropDown(mComplainIv, -123, 3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
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
                        mFocusFl.setVisibility(View.INVISIBLE);
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

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }
        });
    }


    //------------------礼物模块-----------------

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
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(ActorVideoPlayActivity.this);
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
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(ActorVideoPlayActivity.this);
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
                        ToastUtil.showToast(getApplicationContext(), R.string.reward_success);
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
     * 打赏金币(红包)
     */
    private void reWardGold(int gold) {
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
                        ToastUtil.showToast(getApplicationContext(), R.string.reward_success);
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
     * 获取用户性别
     */
    private int getUserSex() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //0.女，1.男
                int sex = userInfo.t_sex;
                return sex != 2 ? sex : 0;
            } else {
                int sex = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_sex;
                return sex != 2 ? sex : 0;
            }
        }
        return 0;
    }

}
