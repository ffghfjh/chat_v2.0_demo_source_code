package com.yiliaodemo.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.SearchActivity;
import com.yiliaodemo.chat.adapter.HomeFragmentAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.fragment.douyin.Dy2VideoFragment;
import com.yiliaodemo.chat.fragment.douyin.DyVideoFragment;
import com.yiliaodemo.chat.fragment.near.NearFragment;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：首页的Fragment改版One
 * 作者：
 * 创建时间：2019/3/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HomeOneFragment extends BaseFragment implements View.OnClickListener {

    public HomeOneFragment() {

    }

    //ViewPager
    private ViewPager mContentVp;
    //粉丝
    private RelativeLayout mFanRl;
    private TextView mFanBigTv;
    private TextView mFanTv;
    private View mFanV;
    //关注
    private TextView mFocusBigTv;
    private TextView mFocusTv;
    private View mFocusV;

    RelativeLayout video_rl;

    GirlFragment mGirlFragment = new GirlFragment();
    FocusFragment focusFragment = new FocusFragment();
    NewManFragment newManFragment = new NewManFragment();
    NearFragment mNearFragment = new NearFragment();
    //VideoFragment mVideoFragment = new VideoFragment();
    Dy2VideoFragment mVideoFragment = new Dy2VideoFragment();
    FansFragment mFansFragment;
//    //推荐
//    private TextView mRecommendBigTv;
//    private TextView mRecommendTv;
//    private View mRecommendV;
//    //新人
//    private TextView mNewManBigTv;
//    private TextView mNewManTv;
//    private View mNewManV;
    //附近
    private TextView mNearBigTv;
    private TextView mNearTv;
    private View mNearV;
    //视频
    private TextView mVideoBigTv;
    private TextView mVideoTv;
    private View mVideoV;

    //角色是用户还是主播
    private int mRole = 0;

    //角色是用户时,切换
    private final int U_FOCUS = 0;//关注
    private final int U_RECOMMEND = 1;//推荐
    private final int U_NEW_MAN = 2;//新人
    private final int U_NEAR = 3;//附近
    private final int U_VIDEO = 4;//视频
    //角色是主播时,切换
    private final int A_FANS = 0;//男粉
    private final int A_FOCUS = 1;//关注
    private final int A_RECOMMEND = 2;//推荐
    private final int A_NEW_MAN = 3;//新人
    private final int A_NEAR = 4;//附近
    private final int A_VIDEO = 5;//视频

    @Override
    protected int initLayout() {
        return R.layout.fragment_home_one_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //viewPager
        mContentVp = view.findViewById(R.id.content_vp);
        //粉丝
        mFanRl = view.findViewById(R.id.fan_rl);
        mFanBigTv = view.findViewById(R.id.fan_big_tv);
        mFanTv = view.findViewById(R.id.fan_tv);
        mFanV = view.findViewById(R.id.fan_v);
        //关注
        RelativeLayout mFocusRl = view.findViewById(R.id.focus_rl);
        mFocusBigTv = view.findViewById(R.id.focus_big_tv);
        mFocusTv = view.findViewById(R.id.focus_tv);
        mFocusV = view.findViewById(R.id.focus_v);
        //推荐
        //RelativeLayout mRecommendRl = view.findViewById(R.id.recommend_rl);
//        mRecommendBigTv = view.findViewById(R.id.recommend_big_tv);
//        mRecommendTv = view.findViewById(R.id.recommend_tv);
//        mRecommendV = view.findViewById(R.id.recommend_v);
        //新人
        //RelativeLayout mNewManRl = view.findViewById(R.id.new_man_rl);
//        mNewManBigTv = view.findViewById(R.id.new_man_big_tv);
//        mNewManTv = view.findViewById(R.id.new_man_tv);
//        mNewManV = view.findViewById(R.id.new_man_v);
        //附近
        RelativeLayout near_rl = view.findViewById(R.id.near_rl);
        mNearBigTv = view.findViewById(R.id.near_big_tv);
        mNearTv = view.findViewById(R.id.near_tv);
        mNearV = view.findViewById(R.id.near_v);
        //视频
        video_rl = view.findViewById(R.id.video_rl);
        mVideoBigTv = view.findViewById(R.id.video_big_tv);
        mVideoTv = view.findViewById(R.id.video_tv);
        mVideoV = view.findViewById(R.id.video_v);
        //排行榜
        ImageView mCategoryIv = view.findViewById(R.id.category_iv);

        //点击事件
        mFanRl.setOnClickListener(this);
        mFocusRl.setOnClickListener(this);
//        mRecommendRl.setOnClickListener(this);
//        mNewManRl.setOnClickListener(this);
        near_rl.setOnClickListener(this);
        video_rl.setOnClickListener(this);
        mCategoryIv.setOnClickListener(this);

        //初始化
        initViewPager();
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        //设置主播 或用户能看到的项目
        mRole = getUserRole();
        if (mRole == 1 && getUserSex() != 1) {//主播才能看见男粉栏目
            mFanRl.setVisibility(View.VISIBLE);
        } else {
            mFanRl.setVisibility(View.GONE);
        }

//        GirlFragment mGirlFragment = new GirlFragment();
//        FocusFragment focusFragment = new FocusFragment();
//        NewManFragment newManFragment = new NewManFragment();
//        NearFragment mNearFragment = new NearFragment();
        //VideoFragment mVideoFragment = new VideoFragment();


        List<Fragment> mFragmentList = new ArrayList<>();
        if (mRole == 1 && getUserSex() != 1) {//女主播
            mFansFragment = new FansFragment();
            mFragmentList.add(0, mFansFragment);
            mFragmentList.add(1, focusFragment);
            mFragmentList.add(2, mGirlFragment);
            mFragmentList.add(3, newManFragment);
            mFragmentList.add(4, mNearFragment);
            mFragmentList.add(5, mVideoFragment);
        } else {//用户
            mFragmentList.add(0, focusFragment);
            mFragmentList.add(1, mGirlFragment);
            mFragmentList.add(2, newManFragment);
            mFragmentList.add(3, mNearFragment);
            mFragmentList.add(4, mVideoFragment);
        }

        HomeFragmentAdapter mFragmentPagerAdapter = new HomeFragmentAdapter(getChildFragmentManager());
        mContentVp.setAdapter(mFragmentPagerAdapter);
        mFragmentPagerAdapter.loadData(mFragmentList);
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (mRole == 1 && getUserSex() != 1) {
                    switchActorTab(position, true);
                } else {
                    switchUserTab(position, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (mRole == 1 && getUserSex() != 1) {//是主播
            mContentVp.setOffscreenPageLimit(6);
            switchActorTab(A_VIDEO, false);
        } else {
            mContentVp.setOffscreenPageLimit(5);
            switchUserTab(A_VIDEO, false);
            video_rl.callOnClick();
        }
    }

    @Override
    protected void onFirstVisible() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fan_rl: {//男粉
                switchActorTab(A_FANS, false);
                break;
            }
            case R.id.focus_rl: {//关注
                if (mRole == 1 && getUserSex() != 1) {
                    switchActorTab(A_FOCUS, false);
                } else {
                    switchUserTab(U_FOCUS, false);
                }
                break;
            }
//            case R.id.recommend_rl: {//推荐
//                if (mRole == 1 && getUserSex() != 1) {
//                    switchActorTab(A_RECOMMEND, false);
//                } else {
//                    switchUserTab(U_RECOMMEND, false);
//                }
//                break;
//            }
//            case R.id.new_man_rl: {//新人
//                if (mRole == 1 && getUserSex() != 1) {
//                    switchActorTab(A_NEW_MAN, false);
//                } else {
//                    switchUserTab(U_NEW_MAN, false);
//                }
//                break;
//            }
            case R.id.near_rl: {//附近

                if (mRole == 1 && getUserSex() != 1) {
                    switchActorTab(A_NEAR, false);
                } else {
                    switchUserTab(U_NEAR, false);
                }
                break;
            }
            case R.id.video_rl: {//视频
                if (mRole == 1 && getUserSex() != 1) {
                    switchActorTab(A_VIDEO, false);
                } else {
                    switchUserTab(U_VIDEO, false);
                }
                break;
            }
            case R.id.category_iv: {//搜索
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    //------------------切换部分 start -----------------------

    /**
     * 当角色是用户的时候切换
     */
    private void switchUserTab(int position, boolean fromViewPager) {
        if (position == U_FOCUS) {//关注
            mVideoFragment.switchVideo();
            if (mFocusV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(U_FOCUS);
            }

            //关注
            mFocusV.setVisibility(View.VISIBLE);
            mFocusBigTv.setVisibility(View.VISIBLE);
            mFocusTv.setVisibility(View.GONE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        }
        else if (position == U_RECOMMEND) {//推荐
//            if (mRecommendV.getVisibility() == View.VISIBLE) {
//                return;
//            }
            mVideoFragment.switchVideo();
            if (!fromViewPager) {
                mContentVp.setCurrentItem(U_RECOMMEND);
            }

//            //推荐
//            mRecommendV.setVisibility(View.VISIBLE);
//            mRecommendBigTv.setVisibility(View.VISIBLE);
//            mRecommendTv.setVisibility(View.GONE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == U_NEW_MAN) {//新人
//            if (mNewManV.getVisibility() == View.VISIBLE) {
//                return;
//            }
            mVideoFragment.switchVideo();
            if (!fromViewPager) {
                mContentVp.setCurrentItem(U_NEW_MAN);
            }

//            //新人
//            mNewManV.setVisibility(View.VISIBLE);
//            mNewManBigTv.setVisibility(View.VISIBLE);
//            mNewManTv.setVisibility(View.GONE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == U_NEAR) {//附近
            mVideoFragment.switchVideo();
            if (mNearV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(U_NEAR);
            }

            //附近
            mNearV.setVisibility(View.VISIBLE);
            mNearBigTv.setVisibility(View.VISIBLE);
            mNearTv.setVisibility(View.GONE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == U_VIDEO) {//视频
            //mVideoFragment.playVideo();
            if (mVideoV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(U_VIDEO);
            }

            //视频
            mVideoV.setVisibility(View.VISIBLE);
            mVideoBigTv.setVisibility(View.VISIBLE);
            mVideoTv.setVisibility(View.GONE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 当角色是主播的时候切换
     */
    private void switchActorTab(int position, boolean fromViewPager) {
        if (position == A_FANS) {//男粉
            mVideoFragment.switchVideo();
            if (mFanV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(A_FANS);
            }

            //男粉
            mFanV.setVisibility(View.VISIBLE);
            mFanBigTv.setVisibility(View.VISIBLE);
            mFanTv.setVisibility(View.GONE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == A_FOCUS) {//关注
            mVideoFragment.switchVideo();
            if (mFocusV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(A_FOCUS);
            }

            //关注
            mFocusV.setVisibility(View.VISIBLE);
            mFocusBigTv.setVisibility(View.VISIBLE);
            mFocusTv.setVisibility(View.GONE);
            //男粉
            mFanV.setVisibility(View.GONE);
            mFanBigTv.setVisibility(View.GONE);
            mFanTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == A_RECOMMEND) {//推荐
            mVideoFragment.switchVideo();
//            if (mRecommendV.getVisibility() == View.VISIBLE) {
//                return;
//            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(A_RECOMMEND);
            }

//            //推荐
//            mRecommendV.setVisibility(View.VISIBLE);
//            mRecommendBigTv.setVisibility(View.VISIBLE);
//            mRecommendTv.setVisibility(View.GONE);
            //男粉
            mFanV.setVisibility(View.GONE);
            mFanBigTv.setVisibility(View.GONE);
            mFanTv.setVisibility(View.VISIBLE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == A_NEW_MAN) {//新人
            mVideoFragment.switchVideo();
//            if (mNewManV.getVisibility() == View.VISIBLE) {
//                return;
//            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(A_NEW_MAN);
            }

//            //新人
//            mNewManV.setVisibility(View.VISIBLE);
//            mNewManBigTv.setVisibility(View.VISIBLE);
//            mNewManTv.setVisibility(View.GONE);
            //男粉
            mFanV.setVisibility(View.GONE);
            mFanBigTv.setVisibility(View.GONE);
            mFanTv.setVisibility(View.VISIBLE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == A_NEAR) {//附近
            mVideoFragment.switchVideo();
            if (mNearV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(A_NEAR);
            }

            //附近
            mNearV.setVisibility(View.VISIBLE);
            mNearBigTv.setVisibility(View.VISIBLE);
            mNearTv.setVisibility(View.GONE);
            //男粉
            mFanV.setVisibility(View.GONE);
            mFanBigTv.setVisibility(View.GONE);
            mFanTv.setVisibility(View.VISIBLE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //视频
            mVideoV.setVisibility(View.GONE);
            mVideoBigTv.setVisibility(View.GONE);
            mVideoTv.setVisibility(View.VISIBLE);

        } else if (position == A_VIDEO) {//视频
            if (mVideoV.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!fromViewPager) {
                mContentVp.setCurrentItem(A_VIDEO);
            }

            //视频
            mVideoV.setVisibility(View.VISIBLE);
            mVideoBigTv.setVisibility(View.VISIBLE);
            mVideoTv.setVisibility(View.GONE);
            //男粉
            mFanV.setVisibility(View.GONE);
            mFanBigTv.setVisibility(View.GONE);
            mFanTv.setVisibility(View.VISIBLE);
            //关注
            mFocusV.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
//            //推荐
//            mRecommendV.setVisibility(View.GONE);
//            mRecommendBigTv.setVisibility(View.GONE);
//            mRecommendTv.setVisibility(View.VISIBLE);
//            //新人
//            mNewManV.setVisibility(View.GONE);
//            mNewManBigTv.setVisibility(View.GONE);
//            mNewManTv.setVisibility(View.VISIBLE);
            //附近
            mNearV.setVisibility(View.GONE);
            mNearBigTv.setVisibility(View.GONE);
            mNearTv.setVisibility(View.VISIBLE);

        }
    }

    //------------------切换部分 end   -----------------------


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
                int sex = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_sex;
                return sex != 2 ? sex : 0;
            }
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("getVideo","onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("getVideo","onDestroy");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("getVideo","onPause");
    }

    public void stopVideo(){
        mVideoFragment.switchVideo();
    }
}
