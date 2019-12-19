package com.yiliaodemo.chat.activity;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.fragment.BeautyRankFragment;
import com.yiliaodemo.chat.fragment.CostRankFragment;
import com.yiliaodemo.chat.fragment.GiftRankFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：榜单页面
 * 作者：
 * 创建时间：2018/9/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RankActivity extends BaseActivity {

    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    @BindView(R.id.beauty_tv)
    TextView mBeautyTv;
    @BindView(R.id.beauty_v)
    View mBeautyV;
    @BindView(R.id.cost_tv)
    TextView mCostTv;
    @BindView(R.id.cost_v)
    View mCostV;
    @BindView(R.id.gift_tv)
    TextView mGiftTv;
    @BindView(R.id.gift_v)
    View mGiftV;
    //大
    @BindView(R.id.beauty_big_tv)
    TextView mBeautyBigTv;
    @BindView(R.id.cost_big_tv)
    TextView mCostBigTv;
    @BindView(R.id.gift_big_tv)
    TextView mGiftBigTv;

    private final int BEAUTY = 0;//魅力
    private final int COST = 1;//消费
    private final int GIFT = 2;//豪礼

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_rank_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initViewPager();
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        final List<Fragment> list = new ArrayList<>();
        BeautyRankFragment beautyRankFragment = new BeautyRankFragment();
        CostRankFragment costRankFragment = new CostRankFragment();
        GiftRankFragment giftRankFragment = new GiftRankFragment();
        list.add(0, beautyRankFragment);
        list.add(1, costRankFragment);
        list.add(2, giftRankFragment);
        mContentVp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTab(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mContentVp.setOffscreenPageLimit(3);
        switchTab(BEAUTY, false);
    }

    @OnClick({R.id.beauty_ll, R.id.cost_ll, R.id.gift_ll, R.id.back_fl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.beauty_ll: {//魅力榜
                switchTab(BEAUTY, false);
                break;
            }
            case R.id.cost_ll: {//消费
                switchTab(COST, false);
                break;
            }
            case R.id.gift_ll: {//豪礼榜
                switchTab(GIFT, false);
                break;
            }
            case R.id.back_fl: {//返回
                finish();
                break;
            }
        }
    }


    /**
     * 切换顶部
     */
    private void switchTab(int position, boolean fromViewPager) {
        if (position == BEAUTY) {//魅力
            if (mBeautyV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(BEAUTY);
            }
            mBeautyTv.setVisibility(View.GONE);
            mBeautyBigTv.setVisibility(View.VISIBLE);
            mBeautyV.setVisibility(View.VISIBLE);

            mCostTv.setVisibility(View.VISIBLE);
            mCostBigTv.setVisibility(View.GONE);
            mCostV.setVisibility(View.INVISIBLE);

            mGiftTv.setVisibility(View.VISIBLE);
            mGiftBigTv.setVisibility(View.GONE);
            mGiftV.setVisibility(View.INVISIBLE);
        } else if (position == COST) {//消费
            if (mCostV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(COST);
            }
            mCostTv.setVisibility(View.GONE);
            mCostBigTv.setVisibility(View.VISIBLE);
            mCostV.setVisibility(View.VISIBLE);

            mBeautyTv.setVisibility(View.VISIBLE);
            mBeautyBigTv.setVisibility(View.GONE);
            mBeautyV.setVisibility(View.INVISIBLE);

            mGiftTv.setVisibility(View.VISIBLE);
            mGiftBigTv.setVisibility(View.GONE);
            mGiftV.setVisibility(View.INVISIBLE);
        } else if (position == GIFT) {//豪礼
            if (mGiftV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(GIFT);
            }
            mGiftTv.setVisibility(View.GONE);
            mGiftBigTv.setVisibility(View.VISIBLE);
            mGiftV.setVisibility(View.VISIBLE);

            mBeautyTv.setVisibility(View.VISIBLE);
            mBeautyBigTv.setVisibility(View.GONE);
            mBeautyV.setVisibility(View.INVISIBLE);

            mCostTv.setVisibility(View.VISIBLE);
            mCostBigTv.setVisibility(View.GONE);
            mCostV.setVisibility(View.INVISIBLE);
        }
    }

}
