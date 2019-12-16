package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
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
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliaodemo.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliaodemo.chat.adapter.ShareRecyclerAdapter;
import com.yiliaodemo.chat.banner.MZBannerView;
import com.yiliaodemo.chat.banner.MZHolderCreator;
import com.yiliaodemo.chat.banner.MZViewHolder;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActorInfoBean;
import com.yiliaodemo.chat.bean.BalanceBean;
import com.yiliaodemo.chat.bean.ChargeBean;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.CoverUrlBean;
import com.yiliaodemo.chat.bean.GiftBean;
import com.yiliaodemo.chat.bean.GoldBean;
import com.yiliaodemo.chat.bean.InfoRoomBean;
import com.yiliaodemo.chat.bean.LabelBean;
import com.yiliaodemo.chat.bean.ShareLayoutBean;
import com.yiliaodemo.chat.bean.VideoSignBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.fragment.ActorVideoFragment;
import com.yiliaodemo.chat.fragment.InfoActiveFragment;
import com.yiliaodemo.chat.fragment.PersonInfoOneFragment;
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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料新页面
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorInfoOneActivity extends AppCompatActivity {

    @BindView(R.id.my_banner)
    MZBannerView<CoverUrlBean> mMZBannerView;
    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    //昵称
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    //职业
    @BindView(R.id.job_tv)
    TextView mJobTv;
    //粉丝数
    @BindView(R.id.fans_number_tv)
    TextView mFansNumberTv;
    //金币数
    @BindView(R.id.price_tv)
    TextView mPriceTv;
    //状态
    @BindView(R.id.status_tv)
    TextView mStatusTv;
    //年龄
    @BindView(R.id.age_tv)
    TextView mAgeTv;
    //签名
    @BindView(R.id.sign_tv)
    TextView mSignTv;
    //title的昵称
    @BindView(R.id.title_nick_tv)
    TextView mTitleNickTv;
    //关注
    @BindView(R.id.focus_iv)
    ImageView mFocusIv;
    //tabs
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    //ToolBar
    @BindView(R.id.title_tb)
    Toolbar mTitleTb;
    //ToolBar右边点
    @BindView(R.id.dian_white_iv)
    ImageView mDianWhiteIv;
    @BindView(R.id.dian_black_iv)
    ImageView mDianBlackIv;
    //ToolBar返回
    @BindView(R.id.back_white_iv)
    ImageView mBackWhiteIv;
    @BindView(R.id.back_black_iv)
    ImageView mBackBlackIv;
    //CollapsingToolbarLayout
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    //AppBarLayout
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    //直播中
    @BindView(R.id.living_tv)
    TextView mLivingTv;
    @BindView(R.id.video_chat_tv)
    TextView mVideoChatTv;

    private int mActorId;//对方id
    private ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> mActorInfoBean;
    //粉丝数
    private int mFansNumber;
    private PersonInfoOneFragment mPersonInfoFragment;
    private ActorVideoFragment mActorVideoFragment;
    private InfoActiveFragment mInfoActiveFragment;
    //注解
    private Unbinder mUnbinder;
    //分享
    private Tencent mTencent;
    private IWXAPI mWxApi;
    //礼物相关
    private int mMyGoldNumber;
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    //大房间直播
    private InfoRoomBean mInfoRoomBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info_one_layout);
        mUnbinder = ButterKnife.bind(this);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);

        initBar();
        initFragment();
        getActorInfo(mActorId);
        getGiftList();
    }

    /**
     * 初始化bar
     */
    private void initBar() {
        mCollapsingToolbarLayout.setTitle(getString(R.string.no_text));
        setSupportActionBar(mTitleTb);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float fraction = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
                mTitleTb.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white),
                        fraction));
                mTitleNickTv.setTextColor(changeAlpha(getResources().getColor(R.color.black_333333),
                        fraction));
                float imageAlpha = fraction * 255;
                mDianWhiteIv.setImageAlpha((int) (255 - imageAlpha));
                mDianBlackIv.setImageAlpha((int) imageAlpha);
                mBackWhiteIv.setImageAlpha((int) (255 - imageAlpha));
                mBackBlackIv.setImageAlpha((int) imageAlpha);
            }
        });
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ACTOR_ID, mActorId);
        mPersonInfoFragment = new PersonInfoOneFragment();
        mPersonInfoFragment.setArguments(bundle);
        mActorVideoFragment = new ActorVideoFragment();
        mActorVideoFragment.setArguments(bundle);
        mInfoActiveFragment = new InfoActiveFragment();
        mInfoActiveFragment.setArguments(bundle);
    }

    /**
     * 获取主播资料
     */
    private void getActorInfo(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//查看人
        paramMap.put("coverUserId", String.valueOf(actorId));
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_INFO)//被查看人
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>>>() {
            @Override
            public void onResponse(BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean = response.m_object;
                    if (bean != null) {
                        mActorInfoBean = bean;
                        //昵称
                        String nick = bean.t_nickName;
                        if (!TextUtils.isEmpty(nick)) {
                            mNickTv.setText(nick);
                            mTitleNickTv.setText(nick);
                        }
                        //职业
                        String job = bean.t_vocation;
                        if (!TextUtils.isEmpty(job)) {
                            mJobTv.setText(job);
                        }
                        //粉丝数
                        mFansNumber = bean.totalCount;
                        if (mFansNumber > 0) {
                            mFansNumberTv.setText(String.valueOf(mFansNumber));
                        }
                        //岁数
                        if (bean.t_age > 0) {
                            mAgeTv.setText(String.valueOf(bean.t_age));
                            mAgeTv.setVisibility(View.VISIBLE);
                        }
                        //个性签名
                        if (!TextUtils.isEmpty(bean.t_autograph)) {
                            mSignTv.setText(bean.t_autograph);
                        } else {
                            mSignTv.setText(getString(R.string.lazy));
                        }
                        //关注  是否关注 0.未关注 1.已关注
                        int mIsFollowed = bean.isFollow;
                        if (mIsFollowed == 0) {
                            mFocusIv.setSelected(false);
                        } else {
                            mFocusIv.setSelected(true);
                        }
                        //视频聊天价格
                        if (bean.anchorSetup != null && bean.anchorSetup.size() > 0) {
                            ChargeBean chargeBean = bean.anchorSetup.get(0);
                            if (chargeBean != null && chargeBean.t_video_gold > 0) {
                                mPriceTv.setText(String.valueOf(chargeBean.t_video_gold));
                            }
                        }
                        //轮播图
                        List<CoverUrlBean> coverUrlBeanList = bean.lunbotu;
                        if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                            setBanner(coverUrlBeanList);
                        }
                        //状态
                        //处理状态 主播状态(0.空闲1.忙碌2.离线)
                        int state = bean.t_onLine;
                        if (state == 0) {//空闲
                            mStatusTv.setText(getString(R.string.free));
                        } else if (state == 1) {//忙碌
                            mStatusTv.setText(getString(R.string.busy));
                        } else if (state == 2) {//离线
                            mStatusTv.setText(getString(R.string.offline));
                        }
                        //处理大房间
                        mInfoRoomBean = bean.bigRoomData;
                        if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                                && mInfoRoomBean.t_chat_room_id > 0) {
                            mLivingTv.setVisibility(View.VISIBLE);
                            mVideoChatTv.setText(getString(R.string.enter_house));
                        }
                        //角色
                        bindViewPager(bean.t_role, bean);
                    }
                }
            }
        });
    }

    /**
     * 初始化下方资料ViewPager
     */
    private void bindViewPager(int role, ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean) {
        //标题
        final List<String> mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.info));
        mTitle.add(getString(R.string.video));
        if (role == 1) {//主播
            mTitle.add(getString(R.string.active));
        }
        final List<Fragment> list = new ArrayList<>();
        list.add(0, mPersonInfoFragment);
        list.add(1, mActorVideoFragment);
        if (role == 1) {
            list.add(2, mInfoActiveFragment);
        }
        mContentVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        if (role == 1) {
            mContentVp.setOffscreenPageLimit(3);
        } else {
            mContentVp.setOffscreenPageLimit(2);
        }
        mTabLayout.setupWithViewPager(mContentVp);
        //资料页加载数据
        if (mPersonInfoFragment != null && mPersonInfoFragment.mIsViewPrepared) {
            mPersonInfoFragment.loadData(bean);
        }
    }

    @OnClick({R.id.back_fl, R.id.dian_fl, R.id.message_iv, R.id.gift_iv, R.id.video_chat_fl,
            R.id.focus_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_fl: {//返回
                finish();
                break;
            }
            case R.id.dian_fl: {//分享
                showShareDialog();
                break;
            }
            case R.id.message_iv: {//私信
                if (mActorInfoBean == null) {
                    return;
                }
                if (getUserSex() == mActorInfoBean.t_sex) {
                    ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                String mineUrl = SharedPreferenceHelper.getAccountInfo(getBaseContext()).headUrl;
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constant.TITLE, mActorInfoBean.t_nickName);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                intent.putExtra(Constant.USER_HEAD_URL, mActorInfoBean.t_handImg);
                intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                intent.putExtra(Constant.MINE_ID, getUserId());
                startActivity(intent);
                break;
            }
            case R.id.gift_iv: {//礼物
                if (mActorInfoBean == null) {
                    return;
                }
                if (getUserSex() == mActorInfoBean.t_sex) {
                    ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                showRewardDialog();
                break;
            }
            case R.id.video_chat_fl: {//与TA视频
                if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                        && mInfoRoomBean.t_chat_room_id > 0) {
                    Intent intent = new Intent(getApplicationContext(), BigHouseActivity.class);
                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                    intent.putExtra(Constant.ACTOR_ID, mActorId);
                    intent.putExtra(Constant.ROOM_ID, mInfoRoomBean.t_room_id);
                    intent.putExtra(Constant.CHAT_ROOM_ID, mInfoRoomBean.t_chat_room_id);
                    startActivity(intent);
                } else {
                    if (mActorInfoBean == null) {
                        return;
                    }
                    if (getUserSex() == mActorInfoBean.t_sex) {
                        ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                        return;
                    }
                    //如果对方是主播,直接用户对主播发起,如果不是就主播对用户发起
                    getSign(mActorInfoBean.t_role == 1);
                }
                break;
            }
            case R.id.focus_iv: {//关注
                if (mActorInfoBean == null) {
                    return;
                }
                if (getUserSex() == mActorInfoBean.t_sex) {
                    ToastUtil.showToast(getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                if (mActorId > 0) {
                    if (!mFocusIv.isSelected()) {//未关注
                        saveFollow(mActorId);
                    } else {//已关注
                        cancelFollow(mActorId);
                    }
                }
                break;
            }
        }
    }

    //----------------------关注  start-------------------------

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
                        mFocusIv.setSelected(true);
                        mFansNumber = mFansNumber + 1;
                        if (mFansNumber > 0) {
                            mFansNumberTv.setText(String.valueOf(mFansNumber));
                        }
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
     * 取消关注
     */
    private void cancelFollow(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//关注人
        paramMap.put("coverFollow", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.DEL_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        mFocusIv.setSelected(false);
                        mFansNumber = mFansNumber - 1;
                        if (mFansNumber >= 0) {
                            mFansNumberTv.setText(String.valueOf(mFansNumber));
                        }
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
    //----------------------关注  end---------------------------

    //----------------------发起视频 start----------------------

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final boolean isUserCallActor) {
        String userId;
        String actorId;
        if (isUserCallActor) {
            userId = getUserId();
            actorId = String.valueOf(mActorId);
        } else {
            userId = String.valueOf(mActorId);
            actorId = getUserId();
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("anthorId", actorId);
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        int onlineState = signBean.onlineState;
                        if (onlineState == 1 && getUserRole() == 0) {//1.余额刚刚住够
                            showGoldJustEnoughDialog(mRoomId, isUserCallActor);
                        } else {
                            if (isUserCallActor) {//是用户call主播
                                userRequestChat(mRoomId);
                            } else {//主播call用户
                                requestChat(mRoomId);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 显示金币刚好够dialog
     */
    private void showGoldJustEnoughDialog(int mRoomId, boolean isUserCallActor) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_one_minute_layout, null);
        setGoldDialogView(view, mDialog, mRoomId, isUserCallActor);
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
    private void setGoldDialogView(View view, final Dialog mDialog, final int mRoomId,
                                   final boolean isUserCallActor) {
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
                if (isUserCallActor) {//是用户call主播
                    userRequestChat(mRoomId);
                } else {//主播call用户
                    requestChat(mRoomId);
                }
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
     * 主播对用户发起聊天
     */
    private void requestChat(final int roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anchorUserId", getUserId());
        paramMap.put("userId", String.valueOf(mActorId));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.ACTOR_LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(getApplicationContext(), VideoChatOneActivity.class);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR_INVITE);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);//用户ID
                        intent.putExtra(Constant.NICK_NAME, mActorInfoBean.t_nickName);
                        intent.putExtra(Constant.USER_HEAD_URL, mActorInfoBean.t_handImg);
                        startActivity(intent);
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
                    } else if (response.m_istatus == -3) {//对方设置了勿扰
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(ActorInfoOneActivity.this);
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
     * 用户对主播发起聊天
     */
    private void userRequestChat(final int roomId) {
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
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
                        startActivity(intent);
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
                        ChargeHelper.showSetCoverDialog(ActorInfoOneActivity.this);
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

    //----------------------发起视频 end----------------------


    //-------------------------Banner------------------
    private void setBanner(List<CoverUrlBean> coverUrlBeans) {
        mMZBannerView.setPages(coverUrlBeans, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        if (coverUrlBeans.size() > 1) {
            mMZBannerView.setCanLoop(true);
            mMZBannerView.start();
        } else {
            mMZBannerView.setCanLoop(false);
        }
    }

    class BannerViewHolder implements MZViewHolder<CoverUrlBean> {

        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(context).inflate(R.layout.item_info_image_vp_layout, null);
            mImageView = view.findViewById(R.id.content_iv);
            return view;
        }

        @Override
        public void onBind(Context context, int i, CoverUrlBean bannerBean) {
            if (bannerBean != null) {
                String coverImg = bannerBean.t_img_url;
                if (!TextUtils.isEmpty(coverImg)) {
                    //计算 图片resize的大小
                    int overWidth = DevicesUtil.getScreenW(getApplicationContext());
                    int overHeight = DevicesUtil.dp2px(getApplicationContext(), 360);
                    if (overWidth > 800) {
                        overWidth = (int) (overWidth * 0.85);
                        overHeight = (int) (overHeight * 0.85);
                    }
                    ImageLoadHelper.glideShowImageWithUrl(getApplicationContext(), coverImg, mImageView, overWidth, overHeight);
                }
            }
        }
    }

    //-----------------------Banner end--------------------

    //----------------------分享----------------

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
        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(ActorInfoOneActivity.this);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new ShareRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {//微信
                    if (mActorInfoBean != null) {
                        //图片地址
                        String imgUrl = "";
                        List<CoverUrlBean> coverUrlBeanList = mActorInfoBean.lunbotu;
                        if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                            imgUrl = coverUrlBeanList.get(0).t_img_url;
                        }
                        BitmapTask bitmapTask = new BitmapTask(ActorInfoOneActivity.this, imgUrl, false);
                        bitmapTask.execute();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else if (position == 1) {//朋友圈
                    if (mActorInfoBean != null) {
                        //图片地址
                        String imgUrl = "";
                        List<CoverUrlBean> coverUrlBeanList = mActorInfoBean.lunbotu;
                        if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                            imgUrl = coverUrlBeanList.get(0).t_img_url;
                        }
                        BitmapTask bitmapTask = new BitmapTask(ActorInfoOneActivity.this, imgUrl, true);
                        bitmapTask.execute();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else if (position == 2) {//QQ
                    shareToQQ();
                } else if (position == 3) {//QQ空间
                    shareToQZone();
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

    static class BitmapTask extends AsyncTask<Integer, Void, Bitmap> {

        private WeakReference<ActorInfoOneActivity> mWeakAty;
        private String mImgUrl;
        private boolean mIsFriendCricle;

        BitmapTask(ActorInfoOneActivity activity, String path, boolean isFriendCricle) {
            mWeakAty = new WeakReference<>(activity);
            mImgUrl = path;
            mIsFriendCricle = isFriendCricle;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            final ActorInfoOneActivity activity = mWeakAty.get();
            if (activity != null) {
                try {
                    Bitmap thumb = BitmapFactory.decodeStream(new URL(mImgUrl).openStream());
                    int width = thumb.getWidth() / 5;
                    int height = thumb.getHeight() / 5;
                    //注意下面的这句压缩，120，150是长宽。
                    //一定要压缩，不然会分享失败
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, width, height, true);
                    //Bitmap回收
                    thumb.recycle();
                    return thumbBmp;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mWeakAty.get() != null) {
                ActorInfoOneActivity activity = mWeakAty.get();
                activity.shareToWeChat(mIsFriendCricle, bitmap);
            }
        }
    }

    /**
     * 分享到QQ
     */
    private void shareToQQ() {
        if (mActorInfoBean != null && mTencent != null) {
            //图片地址
            String imgUrl = "";
            List<CoverUrlBean> coverUrlBeanList = mActorInfoBean.lunbotu;
            if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                imgUrl = coverUrlBeanList.get(0).t_img_url;
            }
            //标题
            String title = getResources().getString(R.string.your_friend) + mActorInfoBean.t_nickName + getResources().getString(R.string.your_friend_one);
            //target地址
            String url = ChatApi.SHARE_URL + getUserId();
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);// 标题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getResources().getString(R.string.please_check));// 摘要
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);// 网络图片地址　　params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称");// 应用名称
            mTencent.shareToQQ(ActorInfoOneActivity.this, params, new MyUIListener());
        } else {
            ToastUtil.showToast(getApplicationContext(), R.string.system_error);
        }
    }

    /**
     * 分享到QZone
     */
    private void shareToQZone() {
        if (mActorInfoBean != null && mTencent != null) {
            //图片地址
            String imgUrl = "";
            List<CoverUrlBean> coverUrlBeanList = mActorInfoBean.lunbotu;
            if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                imgUrl = coverUrlBeanList.get(0).t_img_url;
            }
            //标题【】
            String title = getResources().getString(R.string.your_friend) + mActorInfoBean.t_nickName + getResources().getString(R.string.your_friend_one);
            //target地址
            String url = ChatApi.SHARE_URL + getUserId();
            Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 标题
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, getResources().getString(R.string.please_check));// 摘要
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);// 内容地址
            ArrayList<String> imgUrlList = new ArrayList<>();
            imgUrlList.add(imgUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);// 图片地址
            mTencent.shareToQzone(ActorInfoOneActivity.this, params, new MyUIListener());
        } else {
            ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE
                    || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, new MyUIListener());
            }
        }
    }

    /**
     * 分享到微信
     */
    private void shareToWeChat(boolean isFriendCricle, Bitmap bitmap) {
        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
            return;
        }

        //标题【】
        String title = getResources().getString(R.string.your_friend) + mActorInfoBean.t_nickName + getResources().getString(R.string.your_friend_one);

        String url = ChatApi.SHARE_URL + getUserId();
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = getResources().getString(R.string.please_check);
        if (bitmap != null) {
            msg.setThumbImage(bitmap);
        } else {
            msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
        }

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

    //------------------------------分享end--------------------


    //----------------------------礼物 start-------------------

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
        /*if (getUserRole() == 1) {
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
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(ActorInfoOneActivity.this);
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
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(ActorInfoOneActivity.this);
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

    //----------------------------礼物 end---------------------


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
                int id = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

    /**
     * 获取用户性别
     */
    public int getUserSex() {
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

    /**
     * 获取角色
     */
    public int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_role;
            }
        }
        return 0;
    }

    /**
     * 获取Vip
     */
    public int getUserVip() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //是否VIP 0.是1.否
                return userInfo.t_is_vip;
            } else {
                return SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_is_vip;
            }
        }
        return 2;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

}
