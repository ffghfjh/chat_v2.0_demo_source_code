package com.yiliaodemo.chat.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.AccountBalanceActivity;
import com.yiliaodemo.chat.activity.ApplyCompanyActivity;
import com.yiliaodemo.chat.activity.ApplyVerifyOneActivity;
import com.yiliaodemo.chat.activity.CPSIntroduceActivity;
import com.yiliaodemo.chat.activity.ChargeActivity;
import com.yiliaodemo.chat.activity.HelpCenterActivity;
import com.yiliaodemo.chat.activity.InviteEarnActivity;
import com.yiliaodemo.chat.activity.MainActivity;
import com.yiliaodemo.chat.activity.ModifyUserInfoActivity;
import com.yiliaodemo.chat.activity.MyActorActivity;
import com.yiliaodemo.chat.activity.MyCpsActivity;
import com.yiliaodemo.chat.activity.MyFocusActivity;
import com.yiliaodemo.chat.activity.PhoneNaviActivity;
import com.yiliaodemo.chat.activity.RankActivity;
import com.yiliaodemo.chat.activity.SetBeautyActivity;
import com.yiliaodemo.chat.activity.SetChargeActivity;
import com.yiliaodemo.chat.activity.SettingActivity;
import com.yiliaodemo.chat.activity.UserAlbumListActivity;
import com.yiliaodemo.chat.activity.UserSelfActiveActivity;
import com.yiliaodemo.chat.activity.VipCenterActivity;
import com.yiliaodemo.chat.activity.WithDrawActivity;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.CompanyInviteBean;
import com.yiliaodemo.chat.bean.ReceiveRedBean;
import com.yiliaodemo.chat.bean.UserCenterBean;
import com.yiliaodemo.chat.bean.VerifyBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.BitmapUtil;
import com.yiliaodemo.chat.util.FileUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   我的Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    public MineFragment() {
    }

    private TextView mBecomeTv;
    private TextView mGoldTv;
    private ImageView mHeaderIv;
    private TextView mNickNameTv;
    //是否firstVisible了
    private boolean mHaveFirstVisible = false;
    //VIP
    private ImageView mVipIv;
    //YL ID
    private TextView mChatNumberTv;
    //已认证
    private TextView mHaveVerifyTv;
    //性别
    private LinearLayout mSexAgeLl;
    private ImageView mSexIv;
    //年龄
    private TextView mAgeTv;
    //工会
    private TextView mCompanyTv;
    private ImageView mCompanyIv;
    //CPS
    private TextView mCpsTv;
    //可提现
    private TextView mCanWithdrawTv;
    //个性签名
    private TextView mSignTv;
    //勿扰
    private ImageView mBotherIv;
    //ID号
    private int mIdCardNnmber;
    //相册数字
    private TextView mAlbumTv;
    private TextView mActiveTv;
    //关注数字
    private TextView mFocusTv;
    //红包
    private TextView mRedNumberTv;
    //徒弟
    private TextView mMasterTv;

    private String mGuildName = "";
    private int mIsGuild;
    private int mIsCps;
    private String mVipTime;

    @Override
    protected int initLayout() {
        return R.layout.fragment_mine_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mSignTv = view.findViewById(R.id.sign_tv);
        //可提现
        mCanWithdrawTv = view.findViewById(R.id.can_withdraw_tv);
        //昵称
        mNickNameTv = view.findViewById(R.id.nick_name_tv);
        //账户余额
        mGoldTv = view.findViewById(R.id.gold_tv);
        //头像
        mHeaderIv = view.findViewById(R.id.header_iv);
        //VIP
        mVipIv = view.findViewById(R.id.vip_iv);
        mVipIv.setOnClickListener(this);
        //YL ID
        mChatNumberTv = view.findViewById(R.id.chat_number_tv);
        //是否认证
        mHaveVerifyTv = view.findViewById(R.id.have_verify_tv);
        //性别
        mSexAgeLl = view.findViewById(R.id.sex_age_ll);
        mSexIv = view.findViewById(R.id.sex_iv);
        //年龄
        mAgeTv = view.findViewById(R.id.age_tv);
        //公会FLAG
        mCompanyIv = view.findViewById(R.id.company_iv);
        mCompanyIv.setOnClickListener(this);
        //红包
        View red_fl = view.findViewById(R.id.red_fl);
        red_fl.setOnClickListener(this);
        mRedNumberTv = view.findViewById(R.id.red_number_tv);
        //账户余额
        View account_left_ll = view.findViewById(R.id.account_left_ll);
        account_left_ll.setOnClickListener(this);
        //可提现
        View can_use_ll = view.findViewById(R.id.can_use_ll);
        can_use_ll.setOnClickListener(this);
        //收徒赚钱
        View master_ll = view.findViewById(R.id.master_ll);
        master_ll.setOnClickListener(this);
        View share_rl = view.findViewById(R.id.share_rl);
        share_rl.setOnClickListener(this);
        //申请主播
        mBecomeTv = view.findViewById(R.id.become_tv);
        mBecomeTv.setOnClickListener(this);
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(this);
        //申请工会
        View company_rl = view.findViewById(R.id.company_rl);
        mCompanyTv = view.findViewById(R.id.company_tv);
        company_rl.setOnClickListener(this);
        //CPS推广
        View cps_rl = view.findViewById(R.id.cps_rl);
        cps_rl.setOnClickListener(this);
        mCpsTv = view.findViewById(R.id.cps_tv);
        //设置
        View set_rl = view.findViewById(R.id.set_rl);
        set_rl.setOnClickListener(this);
        //在线客服
        ImageView modify_iv = view.findViewById(R.id.modify_iv);
        modify_iv.setOnClickListener(this);
        //勿扰
        mBotherIv = view.findViewById(R.id.bother_iv);
        mBotherIv.setOnClickListener(this);
        //填写邀请码
        View invite_code_rl = view.findViewById(R.id.invite_code_rl);
        invite_code_rl.setOnClickListener(this);
        //我的相册
        View album_ll = view.findViewById(R.id.album_ll);
        album_ll.setOnClickListener(this);
        mAlbumTv = view.findViewById(R.id.album_tv);
        //动态
        View mActiveLl = view.findViewById(R.id.active_ll);
        mActiveLl.setOnClickListener(this);
        mActiveTv = view.findViewById(R.id.active_tv);
        View mLineOneV = view.findViewById(R.id.line_one_v);
        //关注
        View focus_ll = view.findViewById(R.id.focus_ll);
        focus_ll.setOnClickListener(this);
        mFocusTv = view.findViewById(R.id.focus_tv);
        //关于android
        ImageView about_android_iv = view.findViewById(R.id.about_android_iv);
        about_android_iv.setOnClickListener(this);
        //帮助中心
        ImageView user_help_iv = view.findViewById(R.id.user_help_iv);
        user_help_iv.setOnClickListener(this);
        //徒弟
        mMasterTv = view.findViewById(R.id.master_tv);
        //VIP
        TextView vip_tv = view.findViewById(R.id.vip_tv);
        vip_tv.setOnClickListener(this);
        //排行榜
        ImageView rank_iv = view.findViewById(R.id.rank_iv);
        rank_iv.setOnClickListener(this);
//        //美颜
//        View beauty_rl = view.findViewById(R.id.beauty_rl);
//        beauty_rl.setOnClickListener(this);

        //只有主播有动态
        if (getUserRole() == 1) {
            mActiveLl.setVisibility(View.VISIBLE);
            mLineOneV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onFirstVisible() {
        mHaveFirstVisible = true;
        setCacheInfo();
        getInfo();
        getVerifyStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible) {
            getInfo();
            getVerifyStatus();
        }
    }

    /**
     * 显示缓存头像  昵称
     */
    private void setCacheInfo() {
        //头像
        String imgUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
        if (!TextUtils.isEmpty(imgUrl)) {
            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, imgUrl, mHeaderIv);
        }
        //昵称
        String saveNick = SharedPreferenceHelper.getAccountInfo(mContext).nickName;
        if (!TextUtils.isEmpty(saveNick)) {
            mNickNameTv.setText(saveNick);
        } else {
            String phone = AppManager.getInstance().getUserInfo().phone;
            if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
                String lastFour = phone.substring(7, phone.length());
                String content = mContext.getResources().getString(R.string.chat_user) + lastFour;
                mNickNameTv.setText(content);
            }
        }
    }

    /**
     * 获取实名认证状态
     */
    private void getVerifyStatus() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VERIFY_STATUS)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VerifyBean>>() {
            @Override
            public void onResponse(BaseResponse<VerifyBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {//0.未审核  1.审核成功 2.审核失败
                        VerifyBean bean = response.m_object;
                        if (bean != null) {
                            int status = bean.t_certification_type;
                            if (status == 0) {//审核中
                                if (mContext.getUserRole() != 0) {
                                    AppManager.getInstance().getUserInfo().t_role = 0;
                                    SharedPreferenceHelper.saveRoleInfo(getContext(), 0);
                                }
                                mBecomeTv.setText(R.string.actor_ing);
                                mHaveVerifyTv.setVisibility(View.GONE);
                            } else if (status == 1) {//审核成功
                                if (mContext.getUserRole() != 1) {
                                    AppManager.getInstance().getUserInfo().t_role = 1;
                                    SharedPreferenceHelper.saveRoleInfo(getContext(), 1);
                                }
                                mBecomeTv.setText(R.string.set_money);
                                mHaveVerifyTv.setVisibility(View.VISIBLE);
                            } else {//审核失败
                                if (mContext.getUserRole() != 0) {
                                    AppManager.getInstance().getUserInfo().t_role = 0;
                                    SharedPreferenceHelper.saveRoleInfo(getContext(), 0);
                                }
                                mBecomeTv.setText(R.string.apply_actor);
                                mHaveVerifyTv.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        if (mContext.getUserRole() != 0) {
                            AppManager.getInstance().getUserInfo().t_role = 0;
                            SharedPreferenceHelper.saveRoleInfo(getContext(), 0);
                        }
                        mBecomeTv.setText(R.string.apply_actor);
                        mHaveVerifyTv.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * 获取个人中心信息
     */
    private void getInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.INDEX)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
            @Override
            public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UserCenterBean bean = response.m_object;
                    if (bean != null) {
                        //性别:0.女 1.男
                        mSexAgeLl.setVisibility(View.VISIBLE);
                        if (bean.t_sex == 0) {//女
                            mSexIv.setBackgroundResource(R.drawable.female_white_new);
                            mSexIv.setVisibility(View.VISIBLE);
                        } else {//男
                            mSexIv.setBackgroundResource(R.drawable.male_white_new);
                            mSexIv.setVisibility(View.VISIBLE);
                        }
                        //年龄
                        if (bean.t_age > 0) {
                            mAgeTv.setVisibility(View.VISIBLE);
                            mAgeTv.setText(String.valueOf(bean.t_age));
                        }
                        //可提现
                        mCanWithdrawTv.setText(String.valueOf(bean.extractGold));
                        //个性签名
                        if (!TextUtils.isEmpty(bean.t_autograph)) {
                            mSignTv.setText(bean.t_autograph);
                        } else {
                            mSignTv.setText(mContext.getResources().getString(R.string.lazy));
                        }
                        //号
                        mIdCardNnmber = bean.t_idcard;
                        String chatNumber = mContext.getResources().getString(R.string.chat_number_one) + mIdCardNnmber;
                        mChatNumberTv.setText(chatNumber);
                        //是否VIP 0.是1.否
                        if (bean.t_is_vip == 0) {
                            mVipIv.setVisibility(View.VISIBLE);
                            mVipIv.setSelected(true);
                            //刷新vip,原来不是VIP的话
                            if (mContext != null && mContext.getUserVip() == 1) {
                                if (AppManager.getInstance().getUserInfo() != null) {
                                    AppManager.getInstance().getUserInfo().t_is_vip = 0;
                                }
                                SharedPreferenceHelper.saveUserVip(mContext, 0);
                            }
                        } else {
                            mVipIv.setVisibility(View.VISIBLE);
                            mVipIv.setSelected(false);
                        }
                        int amount = bean.amount;
                        if (amount >= 0) {
                            mGoldTv.setText(String.valueOf(amount));
                        }
                        //头像
                        String imgUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
                        if (TextUtils.isEmpty(imgUrl) || !imgUrl.equals(bean.handImg)) {
                            SharedPreferenceHelper.saveHeadImgUrl(mContext, bean.handImg);
                            if (!TextUtils.isEmpty(bean.handImg)) {
                                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, bean.handImg, mHeaderIv);
                            }
                        }
                        //昵称
                        String nickName = bean.nickName;
                        String saveNick = SharedPreferenceHelper.getAccountInfo(mContext).nickName;
                        if (TextUtils.isEmpty(saveNick) || !saveNick.equals(nickName)) {
                            SharedPreferenceHelper.saveUserNickName(mContext, nickName);
                            if (!TextUtils.isEmpty(nickName)) {
                                mNickNameTv.setText(bean.nickName);
                            } else {
                                String phone = SharedPreferenceHelper.getAccountInfo(mContext).phone;
                                if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
                                    String lastFour = phone.substring(7, phone.length());
                                    String content = mContext.getResources().getString(R.string.chat_user) + lastFour;
                                    mNickNameTv.setText(content);
                                }
                            }
                        }
                        //勿扰状态
                        if (bean.t_is_not_disturb == 0) {//是否勿扰 0.否 1.是
                            mBotherIv.setSelected(false);
                        } else {
                            mBotherIv.setSelected(true);
                        }

                        if (!TextUtils.isEmpty(bean.handImg) && !TextUtils.isEmpty(nickName)) {
                            setIMInfo(bean.handImg, nickName);
                        }
                        //申请工会状态 0.未申请 1.审核中 2.已通过
                        mIsGuild = bean.isGuild;
                        if (mIsGuild == 0) {
                            mCompanyTv.setText(mContext.getResources().getString(R.string.apply_company));
                        } else if (mIsGuild == 1) {
                            mCompanyTv.setText(mContext.getResources().getString(R.string.apply_company_ing));
                        } else {
                            mCompanyTv.setText(mContext.getResources().getString(R.string.my_company));
                        }
                        //拉取是否邀请主播加入公会 	是否加入公会 0.未加入 1.已加入
                        if (bean.isApplyGuild == 0) {
                            mCompanyIv.setVisibility(View.GONE);
                            getCompanyInvite();
                        } else {
                            mCompanyIv.setVisibility(View.VISIBLE);
                            mGuildName = bean.guildName;
                        }
                        // cps推广 -1:未申请 1:审核中 2:已通过 3::已下架
                        mIsCps = bean.isCps;
                        if (mIsCps == -1) {
                            mCpsTv.setText(mContext.getResources().getString(R.string.cps_share));
                        } else if (mIsCps == 1) {
                            mCpsTv.setText(mContext.getResources().getString(R.string.apply_company_ing));
                        } else {
                            mCpsTv.setText(mContext.getResources().getString(R.string.my_cps));
                        }
                        //相册数量
                        if (bean.albumCount >= 0) {
                            mAlbumTv.setText(String.valueOf(bean.albumCount));
                        }
                        //动态数量
                        if (bean.t_role == 1 && bean.dynamCount >= 0) {
                            mActiveTv.setText(String.valueOf(bean.dynamCount));
                        }
                        //关注
                        if (bean.followCount >= 0) {
                            mFocusTv.setText(String.valueOf(bean.followCount));
                        }
                        //徒弟数
                        mMasterTv.setText(String.valueOf(bean.spprentice));
                        //vip到期时间
                        mVipTime = bean.endTime;
                    }
                }
            }
        });
    }

    /**
     * 设置头像  昵称
     */
    private void setIMInfo(String headImg, String nick) {
        UserInfo myUserInfo = JMessageClient.getMyInfo();
        if (myUserInfo != null) {
            //昵称
            String nickName = myUserInfo.getNickname();
            if (TextUtils.isEmpty(nickName) || !nickName.equals(nick)) {
                myUserInfo.setNickname(nick);
                setIMNick(myUserInfo);
            }
            //头像
            String face = myUserInfo.getAvatar();
            String saveFace = SharedPreferenceHelper.getJIMFaceUrl(getContext());
            if (TextUtils.isEmpty(face) || !headImg.equals(saveFace)) {
                setIMFace(headImg);
            }
        }
    }

    /**
     * 设置Im头像
     */
    private void setIMFace(final String faceUrl) {
        //保存到本地
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return;
            }
        }
        File dir = new File(Constant.HEAD_AFTER_SHEAR_DIR);
        if (!dir.exists()) {
            boolean res = dir.mkdir();
            if (!res) {
                return;
            }
        } else {
            FileUtil.deleteFiles(Constant.HEAD_AFTER_SHEAR_DIR);
        }
        final String filePath = Constant.HEAD_AFTER_SHEAR_DIR + System.currentTimeMillis() + ".png";
        Glide.with(getActivity()).load(faceUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                File file = BitmapUtil.saveBitmapAsJpg(resource, filePath);
                if (file != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JMessageClient.updateUserAvatar(new File(filePath), new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {//保存头像地址到本地
                                        LogUtil.i("极光更新头像成功");
                                        SharedPreferenceHelper.saveJIMFaceUrl(getContext(), faceUrl);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 设置Im昵称
     */
    private void setIMNick(UserInfo myUserInfo) {
        //注册时候更新昵称
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
            @Override
            public void gotResult(final int status, String desc) {
                if (status == 0) {
                    LogUtil.i("更新极光im昵称成功");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_left_ll: {//账户余额
                Intent intent = new Intent(getContext(), AccountBalanceActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.can_use_ll: {//可提现
                Intent intent = new Intent(getContext(), WithDrawActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.master_ll:
            case R.id.share_rl: {//收徒赚钱
                Intent intent = new Intent(getContext(), InviteEarnActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.become_tv: {//成为主播,设置收费项目
                Intent intent;
                String text = mBecomeTv.getText().toString().trim();
                if (text.equals(getResources().getString(R.string.apply_actor))) {
                    if (getUserSex() == 1) {
                        ToastUtil.showToast(getContext(), R.string.male_not);
                        return;
                    }
                    intent = new Intent(getContext(), ApplyVerifyOneActivity.class);
                } else if (text.equals(getResources().getString(R.string.actor_ing))) {
                    ToastUtil.showToast(getContext(), R.string.actor_ing_des);
                    return;
                } else {
                    intent = new Intent(getContext(), SetChargeActivity.class);
                }
                startActivity(intent);
                break;
            }
            case R.id.charge_tv: {//充值
                Intent intent = new Intent(getContext(), ChargeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.set_rl: {//设置
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.red_fl: {//红包
                if (mRedNumberTv.getVisibility() == View.VISIBLE) {
                    String redCount = mRedNumberTv.getText().toString().trim();
                    int count = Integer.parseInt(redCount);
                    showRedPackDialog(count);
                } else {
                    ToastUtil.showToast(mContext, R.string.no_pack);
                }
                break;
            }
            case R.id.modify_iv: {//编辑资料
                Intent intent = new Intent(mContext, ModifyUserInfoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.company_rl: {//公会
                String content = mCompanyTv.getText().toString().trim();
                if (content.equals(mContext.getResources().getString(R.string.apply_company))) {
                    showCompanyDialog();
                } else if (content.equals(mContext.getResources().getString(R.string.apply_company_ing))) {
                    ToastUtil.showToast(mContext, R.string.apply_company_ing_des);
                } else {//我的公会
                    if (mIsGuild == 3) {//已下架
                        ToastUtil.showToast(getContext(), R.string.company_down);
                    } else {
                        Intent intent = new Intent(getContext(), MyActorActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            }
            case R.id.company_iv: {//公会图标
                if (!TextUtils.isEmpty(mGuildName)) {
                    String content = mContext.getResources().getString(R.string.belong_company) + mGuildName;
                    ToastUtil.showToast(getContext(), content);
                }
                break;
            }
            case R.id.vip_tv: {//VIP页面
                Intent intent = new Intent(getContext(), VipCenterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cps_rl: {//CPS推广
                String text = mCpsTv.getText().toString().trim();
                if (text.equals(mContext.getResources().getString(R.string.cps_share))) {
                    Intent intent = new Intent(getContext(), CPSIntroduceActivity.class);
                    startActivity(intent);
                } else if (text.equals(mContext.getResources().getString(R.string.apply_company_ing))) {
                    ToastUtil.showToast(mContext, R.string.apply_cps_ing_des);
                } else {//我的公会
                    if (mIsCps == 3) {
                        ToastUtil.showToast(getContext(), R.string.cps_down);
                    } else {
                        Intent intent = new Intent(getContext(), MyCpsActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            }
            case R.id.bother_iv: {//勿扰
                if (mBotherIv.isSelected()) {//原来是开启勿扰
                    int cancelNotBother = 0;
                    setBother(cancelNotBother);
                } else {
                    int setNotBother = 1;
                    setBother(setNotBother);
                }
                break;
            }
            case R.id.album_ll: {//我的相册
                Intent intent = new Intent(getContext(), UserAlbumListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.invite_code_rl: {//填写邀请码
                showInputInviteCodeDialog();
                break;
            }
            case R.id.focus_ll: {//关注
                Intent intent = new Intent(getContext(), MyFocusActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.active_ll: {//动态
                Intent intent = new Intent(getContext(), UserSelfActiveActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.about_android_iv: {//android设置
                Intent intent = new Intent(mContext, PhoneNaviActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.user_help_iv: {//帮助中心
                Intent intent = new Intent(mContext, HelpCenterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.vip_iv: {//vip到期时间
                if (!TextUtils.isEmpty(mVipTime)) {
                    String content = mContext.getString(R.string.vip_end_time) + mVipTime;
                    ToastUtil.showToast(mContext, content);
                }
                break;
            }
            case R.id.rank_iv: {//榜单
                Intent intent = new Intent(mContext, RankActivity.class);
                startActivity(intent);
                break;
            }
//            case R.id.beauty_rl: {//美颜
//                Intent intent = new Intent(mContext, SetBeautyActivity.class);
//                startActivity(intent);
//                break;
//            }
        }
    }

    /**
     * 勿扰
     */
    private void setBother(final int disturb) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("disturb", String.valueOf(disturb));
        OkHttpUtils.post().url(ChatApi.UPDATE_USER_DISTURB)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (disturb == 0) {//先前已经是勿扰
                        mBotherIv.setSelected(false);
                    } else {
                        mBotherIv.setSelected(true);
                        ToastUtil.showToast(mContext, R.string.not_bother_des);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }


    /**
     * 显示红包dialog
     */
    private void showRedPackDialog(int count) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_red_pack_layout, null);
        setRedPackView(view, mDialog, count);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置红包view
     */
    private void setRedPackView(View view, final Dialog mDialog, int count) {
        //背景
        ImageView back_iv = view.findViewById(R.id.back_iv);
        if (count > 1) {
            back_iv.setImageResource(R.drawable.red_pack_background_multi);
        } else {
            back_iv.setImageResource(R.drawable.red_pack_background_one);
        }
        //数量
        TextView count_tv = view.findViewById(R.id.count_tv);
        count_tv.setText(String.valueOf(count));
        final ImageView open_iv = view.findViewById(R.id.open_iv);
        open_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_iv.setEnabled(false);
                receiveRedPacket();
                mDialog.dismiss();
            }
        });
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 用户拆开红包
     */
    private void receiveRedPacket() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.RECEIVE_RED_PACKET)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ReceiveRedBean>>() {
            @Override
            public void onResponse(BaseResponse<ReceiveRedBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    showRedPack(0);
                    ((MainActivity) mContext).clearRed();
                    getInfo();
                } else {
                    ToastUtil.showToast(getContext(), R.string.system_error);
                }
            }
        });
    }

    /**
     * 处理选择后的头像,先裁剪然后再压缩 上传
     */
    public void showHeadImage(Uri uri) {
        ImageLoadHelper.glideShowCircleImageWithUri(getActivity(), uri, mHeaderIv);
    }

    /**
     * 显示红包
     */
    public void showRedPack(int number) {
        if (number > 0) {
            mRedNumberTv.setText(String.valueOf(number));
            mRedNumberTv.setVisibility(View.VISIBLE);
        } else {
            mRedNumberTv.setVisibility(View.GONE);
        }
    }

    /**
     * 显示邀请主播加入公会dialog
     */
    private void showCompanyInviteDialog(String des, int guildId) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_join_company_layout, null);
        setCompanyInviteView(view, mDialog, des, guildId);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 拉取是否有人邀请主播加入公会
     */
    private void getCompanyInvite() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_ANCHOR_ADD_GUILD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<CompanyInviteBean>>() {
            @Override
            public void onResponse(BaseResponse<CompanyInviteBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    CompanyInviteBean inviteBean = response.m_object;
                    if (inviteBean != null && inviteBean.t_id > 0) {
                        String content = inviteBean.t_admin_name + mContext.getResources().getString(R.string.invite_you)
                                + inviteBean.t_guild_name + mContext.getResources().getString(R.string.company);
                        showCompanyInviteDialog(content, inviteBean.t_id);
                    }
                }
            }
        });
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setCompanyInviteView(View view, final Dialog mDialog, String des, final int guildId) {
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        if (!TextUtils.isEmpty(des)) {
            see_des_tv.setText(des);
        }
        //关闭
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //拒绝
        TextView reject_tv = view.findViewById(R.id.reject_tv);
        reject_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int reject = 0;
                joinCompany(guildId, reject);
                mDialog.dismiss();
            }
        });
        //接受
        TextView accept_tv = view.findViewById(R.id.accept_tv);
        accept_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int accept = 1;
                joinCompany(guildId, accept);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 主播确认是否加入公会
     */
    private void joinCompany(int guildId, int isApply) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("guildId", String.valueOf(guildId));
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("isApply", String.valueOf(isApply));//是否加入公会 0.否 1.是
        OkHttpUtils.post().url(ChatApi.IS_APPLY_GUILD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getContext(), R.string.operate_success);
                } else {
                    ToastUtil.showToast(getContext(), R.string.operate_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getContext(), R.string.operate_fail);
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
                return userInfo.t_sex;
            } else {
                return SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_sex;
            }
        }
        return 0;
    }

    /**
     * 显示输入邀请码弹窗
     */
    private void showInputInviteCodeDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_invite_code_layout, null);
        setInviteCodeView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    private void setInviteCodeView(View view, final Dialog mDialog) {
        //邀请码
        TextView code_tv = view.findViewById(R.id.code_tv);
        if (mIdCardNnmber > 0) {
            code_tv.setText(String.valueOf(mIdCardNnmber));
        }
        //复制
        TextView copy_tv = view.findViewById(R.id.copy_tv);
        copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdCardNnmber > 0) {
                    //获取剪贴板管理器
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", String.valueOf(mIdCardNnmber));
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        ToastUtil.showToast(mContext, R.string.copy_success);
                    }
                }
                mDialog.dismiss();
            }
        });
        //输入邀请框
        final EditText code_et = view.findViewById(R.id.code_et);
        //确认
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code_et.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showToast(mContext, R.string.please_input_code);
                    return;
                }
                if (Integer.parseInt(code) <= 0) {
                    ToastUtil.showToast(mContext, R.string.please_input_right_code);
                    return;
                }
                bindCode(code);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 手动绑定推广人
     */
    private void bindCode(String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("idCard", code);
        OkHttpUtils.post().url(ChatApi.UNITE_ID_CARD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {//	-3：当前用户已绑定推广人 -2：当前推广人不存在 -1：推广人不能是自己 0.程序异常 1:绑定成功
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(mContext, R.string.bind_success);
                    } else if (response.m_istatus == -3) {
                        ToastUtil.showToast(mContext, R.string.have_bind);
                    } else if (response.m_istatus == -2) {
                        ToastUtil.showToast(mContext, R.string.bind_man_not_exist);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(mContext, R.string.can_not_bind_yourself);
                    } else {
                        ToastUtil.showToast(mContext, R.string.bind_fail);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.bind_fail);
                }
            }
        });
    }

    /**
     * 获取角色
     */
    private int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_role;
            }
        }
        return 0;
    }

    /**
     * 显示奖励规则
     */
    private void showCompanyDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_connect_qq_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //取消
        ImageView cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(getContext(), ApplyCompanyActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

}
