package cn.tillusory.tiui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.fragment.TiBeautyFragment;
import cn.tillusory.tiui.fragment.TiFaceTrimFragment;
import cn.tillusory.tiui.fragment.TiStickerFragment;
import cn.tillusory.tiui.model.RxBusAction;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiBeautyView extends LinearLayout {

    private TiSDKManager tiSDKManager;
    private ScrollIndicatorView tiIndicatorView;
    private ViewPager tiViewPager;

    private TextView tiNumberTV;
    private SeekBar tiSeekBar;
    private ImageView tiRenderEnableIV;

    private LinearLayout tiEnableLL;
    private TextView tiEnableTV;
    private ImageView tiEnableIV;

    private List<String> tiTabs = new ArrayList<>();

    private boolean isBeautyEnable = false;
    private boolean isFaceTrimEnable = false;

    private TiOnSeekBarChangeListener onSeekBarChangeListener = new TiOnSeekBarChangeListener();

    private String selectedBeautyAction = RxBusAction.ACTION_SKIN_WHITENING;
    private String selectedFaceTrimAction = RxBusAction.ACTION_EYE_MAGNIFYING;

    public TiBeautyView(Context context) {
        super(context);
    }

    public TiBeautyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiBeautyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TiBeautyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TiBeautyView init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;

        RxBus.get().register(this);

        initView();

        initData();

        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        RxBus.get().unregister(this);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ti_beauty, this);

        tiNumberTV = findViewById(R.id.tiNumberTV);
        tiSeekBar = findViewById(R.id.tiSeekBar);
        tiRenderEnableIV = findViewById(R.id.tiRenderEnableIV);

        tiIndicatorView = findViewById(R.id.tiIndicatorView);
        tiViewPager = findViewById(R.id.tiViewPager);

        tiEnableLL = findViewById(R.id.tiEnableLL);
        tiEnableTV = findViewById(R.id.tiEnableTV);
        tiEnableIV = findViewById(R.id.tiEnableIV);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {

        //屏蔽点击事件
        setOnClickListener(null);

        isBeautyEnable = tiSDKManager.isBeautyEnable();
        isFaceTrimEnable = tiSDKManager.isFaceTrimEnable();

        tiEnableIV.setSelected(isBeautyEnable);
        tiEnableTV.setSelected(isBeautyEnable);
        tiEnableTV.setText(isBeautyEnable ? R.string.ti_beauty_on : R.string.ti_beauty_off);

        tiEnableLL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (tiViewPager.getCurrentItem()) {
                    case 0://美颜
                        isBeautyEnable = !isBeautyEnable;
                        tiSDKManager.setBeautyEnable(isBeautyEnable);
                        tiEnableIV.setSelected(isBeautyEnable);
                        tiEnableTV.setSelected(isBeautyEnable);
                        tiEnableTV.setText(isBeautyEnable ? R.string.ti_beauty_on : R.string.ti_beauty_off);
                        tiSeekBar.setEnabled(isBeautyEnable);
                        break;
                    case 1://美型
                        isFaceTrimEnable = !isFaceTrimEnable;
                        tiSDKManager.setFaceTrimEnable(isFaceTrimEnable);
                        tiEnableIV.setSelected(isFaceTrimEnable);
                        tiEnableTV.setSelected(isFaceTrimEnable);
                        tiEnableTV.setText(isFaceTrimEnable ? R.string.ti_face_trim_on : R.string.ti_face_trim_off);
                        tiSeekBar.setEnabled(isFaceTrimEnable);
                        break;
                }
            }
        });

        tiTabs.clear();
        tiTabs.add(getResources().getString(R.string.beauty));
        tiTabs.add(getResources().getString(R.string.face_trim));
        tiTabs.add(getResources().getString(R.string.sticker));

        tiViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                tiIndicatorView.getIndicatorAdapter().notifyDataSetChanged();

                switch (position) {
                    case 0:
                        tiEnableLL.setVisibility(VISIBLE);
                        tiSeekBar.setVisibility(VISIBLE);
                        tiNumberTV.setVisibility(VISIBLE);

                        tiEnableIV.setSelected(isBeautyEnable);
                        tiEnableTV.setSelected(isBeautyEnable);
                        tiEnableTV.setText(isBeautyEnable ? R.string.ti_beauty_on : R.string.ti_beauty_off);

                        RxBus.get().post(selectedBeautyAction);

                        tiSeekBar.setEnabled(isBeautyEnable);
                        break;
                    case 1:
                        tiEnableLL.setVisibility(VISIBLE);
                        tiSeekBar.setVisibility(VISIBLE);
                        tiNumberTV.setVisibility(VISIBLE);

                        tiEnableIV.setSelected(isFaceTrimEnable);
                        tiEnableTV.setSelected(isFaceTrimEnable);
                        tiEnableTV.setText(isFaceTrimEnable ? R.string.ti_face_trim_on : R.string.ti_face_trim_off);

                        RxBus.get().post(selectedFaceTrimAction);

                        tiSeekBar.setEnabled(isFaceTrimEnable);
                        break;
                    default:
                        tiEnableLL.setVisibility(GONE);
                        tiSeekBar.setVisibility(INVISIBLE);
                        tiNumberTV.setVisibility(INVISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tiIndicatorView.setOnTransitionListener(new OnTransitionTextListener()
                .setColor(getResources().getColor(R.color.ti_blue), getResources().getColor(R.color.ti_white)));
        tiIndicatorView.setSplitAuto(false);
        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(tiIndicatorView, tiViewPager);
        indicatorViewPager.setPageOffscreenLimit(3);
        IndicatorViewPager.IndicatorFragmentPagerAdapter fragmentPagerAdapter =
                new IndicatorViewPager.IndicatorFragmentPagerAdapter(((FragmentActivity) getContext()).getSupportFragmentManager()) {
                    @Override
                    public int getCount() {
                        return tiTabs.size();
                    }

                    @Override
                    public View getViewForTab(int position, View convertView, ViewGroup container) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ti_tab, container, false);
                        }

                        ((TextView) convertView).setText(tiTabs.get(position));
                        return convertView;
                    }

                    @Override
                    public Fragment getFragmentForPage(int position) {
                        switch (position) {
                            case 0://美颜
                                return new TiBeautyFragment();
                            case 1://美型
                                return new TiFaceTrimFragment();
                            case 2://贴纸
                                return new TiStickerFragment().setTiSDKManager(tiSDKManager);
                        }
                        return null;
                    }
                };

        indicatorViewPager.setAdapter(fragmentPagerAdapter);

        tiSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        tiSeekBar.setProgress(0);

        tiRenderEnableIV.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tiSDKManager.renderEnable(false);
                        return true;
                    case MotionEvent.ACTION_UP:
                        tiSDKManager.renderEnable(true);
                        return true;
                }
                return false;
            }
        });

        //初始化默认选中美颜功能中的美白
        RxBus.get().post(RxBusAction.ACTION_SKIN_WHITENING);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    public void setSelectAction(String selectAction) {

        onSeekBarChangeListener.setSelectAction(selectAction);

        switch (selectAction) {
            case RxBusAction.ACTION_SKIN_WHITENING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getSkinWhitening()));
                tiSeekBar.setProgress(tiSDKManager.getSkinWhitening());

                selectedBeautyAction = selectAction;
                break;
            case RxBusAction.ACTION_SKIN_BLEMISH_REMOVAL:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getSkinBlemishRemoval()));
                tiSeekBar.setProgress(tiSDKManager.getSkinBlemishRemoval());

                selectedBeautyAction = selectAction;
                break;
            case RxBusAction.ACTION_SKIN_TENDERNESS:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getSkinTenderness()));
                tiSeekBar.setProgress(tiSDKManager.getSkinTenderness());

                selectedBeautyAction = selectAction;
                break;
            case RxBusAction.ACTION_SKIN_SATURATION:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getSkinSaturation()));
                tiSeekBar.setProgress(tiSDKManager.getSkinSaturation());

                selectedBeautyAction = selectAction;
                break;
            case RxBusAction.ACTION_EYE_MAGNIFYING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getEyeMagnifying()));
                tiSeekBar.setProgress(tiSDKManager.getEyeMagnifying());

                selectedFaceTrimAction = selectAction;
                break;
            case RxBusAction.ACTION_CHIN_SLIMMING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getChinSlimming()));
                tiSeekBar.setProgress(tiSDKManager.getChinSlimming());

                selectedFaceTrimAction = selectAction;
                break;
            case RxBusAction.ACTION_JAW_TRANSFORMING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getJawTransforming()));
                tiSeekBar.setProgress(tiSDKManager.getJawTransforming());

                selectedFaceTrimAction = selectAction;
                break;
            case RxBusAction.ACTION_FOREHEAD_TRANSFORMING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getForeheadTransforming()));
                tiSeekBar.setProgress(tiSDKManager.getForeheadTransforming());

                selectedFaceTrimAction = selectAction;
                break;
            case RxBusAction.ACTION_MOUTH_TRANSFORMING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getMouthTransforming()));
                tiSeekBar.setProgress(tiSDKManager.getMouthTransforming());

                selectedFaceTrimAction = selectAction;
                break;
            case RxBusAction.ACTION_NOSE_MINIFYING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getNoseMinifying()));
                tiSeekBar.setProgress(tiSDKManager.getNoseMinifying());

                selectedFaceTrimAction = selectAction;
                break;
            case RxBusAction.ACTION_TEETH_WHITENING:
                tiNumberTV.setText(new StringBuilder().append(tiSDKManager.getTeethWhitening()));
                tiSeekBar.setProgress(tiSDKManager.getTeethWhitening());

                selectedFaceTrimAction = selectAction;
                break;
        }
    }

    private class TiOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        private String selectAction;

        void setSelectAction(String selectAction) {
            this.selectAction = selectAction;
        }

        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {

            switch (selectAction) {
                case RxBusAction.ACTION_SKIN_WHITENING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setSkinWhitening(progress);
                    break;
                case RxBusAction.ACTION_SKIN_BLEMISH_REMOVAL:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setSkinBlemishRemoval(progress);
                    break;
                case RxBusAction.ACTION_SKIN_TENDERNESS:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setSkinTenderness(progress);
                    break;
                case RxBusAction.ACTION_SKIN_SATURATION:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setSkinSaturation(progress);
                    break;
                case RxBusAction.ACTION_EYE_MAGNIFYING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setEyeMagnifying(progress);
                    break;
                case RxBusAction.ACTION_CHIN_SLIMMING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setChinSlimming(progress);
                    break;
                case RxBusAction.ACTION_JAW_TRANSFORMING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setJawTransforming(progress);
                    break;
                case RxBusAction.ACTION_FOREHEAD_TRANSFORMING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setForeheadTransforming(progress);
                    break;
                case RxBusAction.ACTION_MOUTH_TRANSFORMING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setMouthTransforming(progress);
                    break;
                case RxBusAction.ACTION_NOSE_MINIFYING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setNoseMinifying(progress);
                    break;
                case RxBusAction.ACTION_TEETH_WHITENING:
                    tiNumberTV.setText(new StringBuilder().append(progress));
                    tiSDKManager.setTeethWhitening(progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
