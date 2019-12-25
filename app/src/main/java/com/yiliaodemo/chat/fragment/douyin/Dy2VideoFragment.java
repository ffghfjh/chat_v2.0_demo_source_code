package com.yiliaodemo.chat.fragment.douyin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.Tiktok2Adapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.bean.VideoBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.util.Utils;
import com.yiliaodemo.chat.util.cache.PreloadManager;
import com.yiliaodemo.chat.util.cache.ProxyVideoCacheManager;
import com.yiliaodemo.chat.widget.VerticalViewPager;
import com.yiliaodemo.chat.widget.controller.TikTokController;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class Dy2VideoFragment extends BaseFragment{

    private int mQueryType = -1;
    private int page = 1;
    public static VideoView mVideoView;
    /**
     * 当前播放位置
     */
    private int mCurPos;
    private List<VideoBean> mVideoList = new ArrayList<>();
    private Tiktok2Adapter mTiktok2Adapter;
    private VerticalViewPager mViewPager;

    private PreloadManager mPreloadManager;

    /**
     * VerticalViewPager是否反向滑动
     */
    private boolean mIsReverseScroll;

    private TikTokController mController;

    private static final String KEY_INDEX = "index";


    @Override
    protected int initLayout() {
        return R.layout.activity_tiktok2;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        setStatusBarTransparent();
        initViewPager(view);
        initVideoView();
        mPreloadManager = PreloadManager.getInstance(getContext());
        initData();
        Intent extras = getActivity().getIntent();
        int index = extras.getIntExtra(KEY_INDEX, 0);
        mViewPager.setCurrentItem(index);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                Log.d("playVideo","播放");
                startPlay(index);
            }
        });

    }

    @Override
    protected void onFirstVisible() {

    }

    /**
     * 初次加载主播视频
     */
    private void initData(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page","1");
        paramMap.put("queryType", String.valueOf(mQueryType));
        Log.d("getVideo", ParamUtil.getParam(paramMap));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<VideoBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<VideoBean>> response, int id) {
                if(response==null){
                    Log.d("getVideos","response为空");
                }else{
                    Log.d("getVideos",response.m_istatus+"");
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if(response.m_object!=null){
                        PageBean<VideoBean> pageBean = response.m_object;
                        if (pageBean != null) {
                            List<VideoBean> focusBeans = pageBean.data;
                            Log.d("getVideos","获取视频成功："+focusBeans.size());
                            if (focusBeans != null) {
                                Log.d("getVideo","获取视频：page:"+page);
                                for(VideoBean videoBean : focusBeans){
                                    Log.d("getVideo",videoBean.t_id+"");
                                    mVideoList.add(videoBean);
                                }
                            }
                            Tiktok2Adapter.mActorId = mVideoList.get(0).t_user_id;
                        }
                        mTiktok2Adapter.notifyDataSetChanged();

                    }else{
                        Log.d("getVideos","m_object为空");
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getContext(), R.string.system_error);
            }
        });
    }


    /**
     * 获取主播视频照片
     */
    private void addData(int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(mQueryType));
        Log.d("getVideo", ParamUtil.getParam(paramMap));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<VideoBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<VideoBean>> response, int id) {
                if(response==null){
                    Log.d("getVideos","response为空");
                }else{
                    Log.d("getVideos",response.m_istatus+"");
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if(response.m_object!=null){
                        PageBean<VideoBean> pageBean = response.m_object;
                        if (pageBean != null) {
                            List<VideoBean> focusBeans = pageBean.data;
                            Log.d("getVideos","获取视频成功："+focusBeans.size());
                            if (focusBeans != null) {
                                Log.d("getVideo","获取视频：page:"+page);
                                for(VideoBean videoBean : focusBeans){
                                    Log.d("getVideo",videoBean.t_id+"");
                                    mVideoList.add(videoBean);
                                }
                            }
                        }
                        mTiktok2Adapter.notifyDataSetChanged();
                    }else{
                        Log.d("getVideos","m_object为空");
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getContext(), R.string.system_error);
            }
        });
    }

    private void initVideoView() {
        mVideoView = new VideoView(getContext());
        mVideoView.setMinimumHeight(VideoView.SCREEN_SCALE_MATCH_PARENT);
        mVideoView.setLooping(true);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
        mController = new TikTokController(getContext());
        mVideoView.setVideoController(mController);
    }

    private void initViewPager(View view) {
        mViewPager = view.findViewById(R.id.vvp);
        mViewPager.setOffscreenPageLimit(4);
        mTiktok2Adapter = new Tiktok2Adapter(mVideoList,getActivity(),getActivity());
        mViewPager.setAdapter(mTiktok2Adapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mIsReverseScroll = position < mCurPos;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == mCurPos) return;
                Tiktok2Adapter.mActorId =  mVideoList.get(position).t_user_id;
                startPlay(position);
                Log.d("playPage",mViewPager.getCurrentItem()+","+mVideoList.size());
                if(mViewPager.getCurrentItem()==mVideoList.size()-1){
                    page++;
                    addData(page);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mPreloadManager.resumePreload(mCurPos, mIsReverseScroll);
                } else {
                    mPreloadManager.pausePreload(mCurPos, mIsReverseScroll);
                }
            }
        });
    }

    public void switchVideo(){
        releaseVideo(getView());
    }

    /**
     * 停止播放
     */
    private void releaseVideo(View view) {
        if(mVideoView!=null){
            mVideoView.pause();
        }
    }
    private void startPlay(int position) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i ++) {
            View itemView = mViewPager.getChildAt(i);
            Tiktok2Adapter.ViewHolder viewHolder = (Tiktok2Adapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                mVideoView.release();
                Utils.removeViewFormParent(mVideoView);

                VideoBean tiktokBean = mVideoList.get(position);
                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.t_addres_url);
                L.i("startPlay: " + "position: " + position + "  url: " + playUrl);
                mVideoView.setUrl(playUrl);
                mController.addControlComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoView, 0);
                mVideoView.start();
                mCurPos = position;
                break;
            }
        }
    }

    /**
     * 把状态栏设成透明
     */
    protected void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
            ViewCompat.requestApplyInsets(decorView);
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        }
    }


    @Override
    public void onResume() {
        Log.d("statesss","onResume");
        super.onResume();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }


    @Override
    public void onPause() {
        Log.d("statesss","onPause");
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("statesss","onDestroy");
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
        mPreloadManager.removeAllPreloadTask();
        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(getContext());
    }
}
