package com.yiliaodemo.chat.fragment.near;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.HomeFragmentAdapter;
import com.yiliaodemo.chat.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：附近Fragment
 * 作者：
 * 创建时间：2018/11/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class NearFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout mContentLl;
    private FrameLayout mCoverFl;
    private ViewPager mContentVp;
//    //距离
//    private ImageView mDistanceIv;
    private TextView mDistanceTv;
//    //地图
//    private ImageView mMapIv;
//    private TextView mMapTv;

    private final int DISTANCE = 0;
    private final int MAP = 1;
    //初始化过ViewPager了
    private boolean mHaveInitViewPager = false;

    @Override
    protected int initLayout() {
        return R.layout.fragment_near_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mContentLl = view.findViewById(R.id.content_ll);
        mCoverFl = view.findViewById(R.id.cover_fl);
        mContentVp = view.findViewById(R.id.content_vp);
        //距离
//        View distance_fl = view.findViewById(R.id.distance_fl);
//        mDistanceIv = view.findViewById(R.id.distance_iv);
        mDistanceTv = view.findViewById(R.id.distance_tv);
//        //地图
//        View map_fl = view.findViewById(R.id.map_fl);
//        mMapIv = view.findViewById(R.id.map_iv);
//        mMapTv = view.findViewById(R.id.map_tv);
        //开启
        TextView open_tv = view.findViewById(R.id.open_tv);
        open_tv.setOnClickListener(this);
//        distance_fl.setOnClickListener(this);
//        map_fl.setOnClickListener(this);
        switchSelect(DISTANCE, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //显示授权dialog
            mCoverFl.setVisibility(View.VISIBLE);
            mContentLl.setVisibility(View.GONE);
        } else {
            if (!mHaveInitViewPager) {
                mHaveInitViewPager = true;
                initViewPager();
            }
        }
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        mCoverFl.setVisibility(View.GONE);
        mContentLl.setVisibility(View.VISIBLE);

        List<Fragment> mFragmentList = new ArrayList<>();
        DistanceFragment distanceFragment = new DistanceFragment();
//        MapFragment mapFragment = new MapFragment();
//        mFragmentList.add(0, distanceFragment);
//        mFragmentList.add(1, mapFragment);

        HomeFragmentAdapter mFragmentPagerAdapter = new HomeFragmentAdapter(getChildFragmentManager());
        mContentVp.setAdapter(mFragmentPagerAdapter);
        mFragmentPagerAdapter.loadData(mFragmentList);
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchSelect(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mContentVp.setOffscreenPageLimit(2);
    }

    @Override
    protected void onFirstVisible() {
        switchSelect(DISTANCE, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.distance_fl: {//距离
//                switchSelect(DISTANCE, false);
//                break;
//            }
//            case R.id.map_fl: {//地图
//                switchSelect(MAP, false);
//                break;
//            }
            case R.id.open_tv: {//立即开启
                requestPermission();
                break;
            }
        }
    }

    protected void requestPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                List<String> permissions = new ArrayList<>();
                //定位
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permissions.size() != 0) {
                    ActivityCompat.requestPermissions(mContext, permissions.toArray(new String[0]), 100);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换选中
     */
    private void switchSelect(int position, boolean fromViewPager) {
        try {
            if (position == DISTANCE) {
//                if (mDistanceIv.isSelected() || mDistanceTv.isSelected()) {
//                    return;
//                }
                if (!fromViewPager) {
                    mContentVp.setCurrentItem(DISTANCE);
                }

//                mDistanceIv.setSelected(true);
//                mDistanceTv.setSelected(true);

//                mMapIv.setSelected(false);
//                mMapTv.setSelected(false);
            } else if (position == MAP) {
//                if (mMapIv.isSelected() || mMapTv.isSelected()) {
//                    return;
//                }
                if (!fromViewPager) {
                    mContentVp.setCurrentItem(MAP);
                }

//                mMapIv.setSelected(true);
//                mMapTv.setSelected(true);

//                mDistanceIv.setSelected(false);
//                mDistanceTv.setSelected(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
