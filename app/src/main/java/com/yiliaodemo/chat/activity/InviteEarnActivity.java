package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.InviteBean;
import com.yiliaodemo.chat.bean.InviteRewardBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.fragment.invite.ManFragment;
import com.yiliaodemo.chat.fragment.invite.RewardFragment;
import com.yiliaodemo.chat.fragment.invite.TudiFragment;
import com.yiliaodemo.chat.fragment.invite.TusunFragment;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  邀请赚钱页面
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InviteEarnActivity extends AppCompatActivity {

    @BindView(R.id.earn_gold_tv)
    TextView mEarnGoldTv;
    @BindView(R.id.invite_man_tv)
    TextView mInviteManTv;
    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    //tabs
    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    //注解
    private Unbinder mUnbinder;
    //沉浸式状态栏
    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_earn_layout);
        mUnbinder = ButterKnife.bind(this);
        setStatusBarStyle();
        initStart();
        getShareInfo();
    }

    private void initStart() {
        final List<String> mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.reward_twenty));
        mTitle.add(getString(R.string.number_twenty));
        mTitle.add(getString(R.string.my_one));
        mTitle.add(getString(R.string.my_two));
        final List<Fragment> list = new ArrayList<>();
        RewardFragment rewardFragment = new RewardFragment();
        ManFragment manFragment = new ManFragment();
        TudiFragment tudiFragment = new TudiFragment();
        TusunFragment tusunFragment = new TusunFragment();
        list.add(0, rewardFragment);
        list.add(1, manFragment);
        list.add(2, tudiFragment);
        list.add(3, tusunFragment);
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
        mTabLayout.setupWithViewPager(mContentVp);
        mContentVp.setOffscreenPageLimit(4);
    }

    /**
     * 获取推广赚钱信息
     */
    private void getShareInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SHARE_TOTAL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<InviteBean>>() {
            @Override
            public void onResponse(BaseResponse<InviteBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    InviteBean shareBean = response.m_object;
                    if (shareBean != null) {
                        //总收益
                        mEarnGoldTv.setText(String.valueOf(shareBean.profitTotal));
                        //总用户
                        int total = shareBean.oneSpreadCount + shareBean.twoSpreadCount;
                        mInviteManTv.setText(String.valueOf(total));
                    }
                }
            }
        });
    }

    @OnClick({R.id.rule_tv, R.id.earn_tv, R.id.back_black_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rule_tv: {//奖励规则
                showRewardRuleDialog();
                break;
            }
            case R.id.earn_tv: {//我要赚钱
                Intent intent = new Intent(getApplicationContext(), ErWeiCodeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.back_black_iv: {
                finish();
                break;
            }
        }
    }

    /**
     * 显示奖励规则
     */
    private void showRewardRuleDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_reward_rule_layout, null);
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
        TextView content_tv = view.findViewById(R.id.content_tv);
        //获取规则
        getSpreadAward(content_tv);
        //取消
        ImageView cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 获取推广奖励规则
     */
    private void getSpreadAward(final TextView content_tv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SPREAD_AWARD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<InviteRewardBean>>() {
            @Override
            public void onResponse(BaseResponse<InviteRewardBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    InviteRewardBean rewardBean = response.m_object;
                    if (rewardBean != null) {
                        String content = rewardBean.t_award_rules;
                        if (!TextUtils.isEmpty(content)) {
                            content_tv.setText(content);
                        }
                    }
                }
            }
        });
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
                int id = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    protected void setStatusBarStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果不是沉浸式,就设置为黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return;
        }
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true).navigationBarColor(R.color.black).init();
    }

}
