package com.yiliaodemo.chat.fragment.douyin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.VideoAdapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.Bean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.bean.VideoBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DataUtils;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.widget.OnViewPagerListener;
import com.yiliaodemo.chat.widget.PagerLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class DyVideoFragment extends BaseFragment implements View.OnClickListener{

    //请求类型 -1：全部 0.免费  1.私密
    private final int ALL = -1;
    private final int FREE = 0;
    private final int CHARGE = 1;
    private int mQueryType = -1;
    private int page = 1;

    private RecyclerView recyclerView;
    private ArrayList<VideoBean> mDatas = new ArrayList<>();
    private VideoAdapter mAdapter;
    private IjkVideoView mVideoView;
    private ImageView stopView;

    public DyVideoFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected int initLayout() {
        return R.layout.dylist_fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        stopView = view.findViewById(R.id.paly_stop);
        recyclerView = view.findViewById(R.id.dy_recyler);
        PagerLayoutManager mLayoutManager = new PagerLayoutManager(getActivity(), OrientationHelper.VERTICAL);
        //mDatas = DataUtils.getDatas();
        mAdapter = new VideoAdapter(getActivity(),getActivity(), mDatas);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete(View view) {
                view.findViewById(R.id.paly_stop).setVisibility(View.GONE);
                //playVideo(0, view);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom, View view) {
                view.findViewById(R.id.paly_stop).setVisibility(View.GONE);
                playVideo(position, view);
                if(position==mAdapter.getItemCount()-1){
                    page = page +1;
                    getVideoList((page));
                }
            }

            @Override
            public void onPageRelease(boolean isNext, int position, View view) {
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(view);
            }
        });
        getVideoList(page);
    }

    @Override
    protected void onFirstVisible() {

    }


    /**
     * 播放视频
     */
    private void playVideo(int position, View view) {
        Log.d("palyVideo","播放视频1");
        stopView=view.findViewById(R.id.paly_stop);
        stopView.setVisibility(View.GONE);
        if (view != null) {
            mVideoView = view.findViewById(R.id.video_view);
            mVideoView.start();
        }
    }

    /**
     * 停止播放
     */
    private void releaseVideo(View view) {
        stopView=view.findViewById(R.id.paly_stop);
        stopView.setVisibility(View.GONE);
        if (view != null) {
            IjkVideoView videoView = view.findViewById(R.id.video_view);
            videoView.stopPlayback();
            view.findViewById(R.id.paly_stop).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        Log.d("getVideo","onResume");
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onPause() {
        Log.d("getVideo","onPause");
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("getVideo","onDestroy");
        super.onDestroy();
        if(mVideoView!=null){
            mVideoView.stopPlayback();
        }
    }

    public void switchVideo(){
       releaseVideo(getView());
    }

    /**
     * 获取主播视频照片
     */
    private void getVideoList1(int page) {
        mDatas.clear();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(mQueryType));
        Log.d("getVideo",ParamUtil.getParam(paramMap));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
               Log.d("getVideos","视频请求响应："+response);
            }
        });
    }
    /**
     * 获取主播视频照片
     */
    private void getVideoList(int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(mQueryType));
        Log.d("getVideo",ParamUtil.getParam(paramMap));
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
                                int size = focusBeans.size();
                                for(VideoBean videoBean : focusBeans){
                                    mDatas.add(videoBean);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
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
}
