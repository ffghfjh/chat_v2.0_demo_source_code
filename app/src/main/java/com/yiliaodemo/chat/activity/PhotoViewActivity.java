package com.yiliaodemo.chat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.ActiveFileBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.fragment.PhotoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：查看动态图片页面
 * 作者：
 * 创建时间：2018/1/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhotoViewActivity extends BaseActivity {

    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    @BindView(R.id.indicator_ll)
    LinearLayout mIndicatorLl;

    private ArrayList<ImageView> mIndicatorImages = new ArrayList<>();

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_photo_view_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initStart();
    }

    /**
     * 初始化
     */
    @SuppressWarnings("unchecked")
    private void initStart() {
        int actorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        int clickPosition = getIntent().getIntExtra(Constant.CLICK_POSITION, 0);
        List<ActiveFileBean> fileBeans = (List<ActiveFileBean>) getIntent().getSerializableExtra(Constant.IMAGE_URL);
        final List<Fragment> mFragmentList = new ArrayList<>();
        if (fileBeans != null && fileBeans.size() > 0) {
            for (ActiveFileBean bean : fileBeans) {
                PhotoFragment photoFragment = new PhotoFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.ACTIVE_FILE_BEAN, bean);
                bundle.putInt(Constant.ACTOR_ID, actorId);
                photoFragment.setArguments(bundle);
                mFragmentList.add(photoFragment);
            }
        }
        if (mFragmentList.size() > 0) {
            mContentVp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return mFragmentList.get(position);
                }

                @Override
                public int getCount() {
                    return mFragmentList.size();
                }
            });
            mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (mIndicatorImages != null && mIndicatorImages.size() > position) {
                        for (int i = 0; i < mIndicatorImages.size(); i++) {
                            ImageView imageView = mIndicatorImages.get(i);
                            if (i == position) {
                                imageView.setImageResource(R.drawable.indicator_selected);
                            } else {
                                imageView.setImageResource(R.drawable.indicator_normal);
                            }
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            mContentVp.setCurrentItem(clickPosition);
            mContentVp.setOffscreenPageLimit(mFragmentList.size());

            initIndicator(mFragmentList.size(), clickPosition);
        }
    }

    /**
     * 初始化indicator
     */
    private void initIndicator(int size, int position) {
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            if (i == position) {
                imageView.setImageResource(R.drawable.indicator_selected);
            } else {
                imageView.setImageResource(R.drawable.indicator_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            imageView.setLayoutParams(layoutParams);
            mIndicatorLl.addView(imageView);
            mIndicatorImages.add(imageView);
        }
    }

}
