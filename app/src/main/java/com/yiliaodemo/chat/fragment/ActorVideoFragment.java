package com.yiliaodemo.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.adapter.InfoVideoRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.base.LazyFragment;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.bean.VideoBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料页下方主播视频Fragment
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorVideoFragment extends LazyFragment {

    private ActorInfoOneActivity mContext;

    public ActorVideoFragment() {

    }

    private int mActorId;
    private TextView mNoVideoTv;
    private InfoVideoRecyclerAdapter mAdapter;
    private boolean mHaveFirstVisible;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = (ActorInfoOneActivity) getActivity();
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_actor_video_layout,
                container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mNoVideoTv = view.findViewById(R.id.no_video_tv);
        RecyclerView mVideoRv = view.findViewById(R.id.video_rv);
        mVideoRv.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mVideoRv.setLayoutManager(gridLayoutManager);
        mAdapter = new InfoVideoRecyclerAdapter(mContext);
        mVideoRv.setAdapter(mAdapter);
        mIsViewPrepared = true;
    }

    @Override
    protected void onFirstVisibleToUser() {
        mHaveFirstVisible = true;
        Bundle bundle = getArguments();
        if (bundle != null) {
            mActorId = bundle.getInt(Constant.ACTOR_ID);
            getActorVideo();
        }
        mIsDataLoadCompleted = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible && mActorId > 0) {
            getActorVideo();
        }
    }

    /**
     * 获取主播视频照片
     */
    private void getActorVideo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("coverUserId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(1));
        OkHttpUtils.post().url(ChatApi.GET_DYNAMIC_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<VideoBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<VideoBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<VideoBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<VideoBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (size > 0) {
                                mAdapter.loadData(focusBeans, mActorId);
                                mNoVideoTv.setVisibility(View.GONE);
                            } else {
                                mNoVideoTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    ToastUtil.showToast(getContext(), R.string.system_error);
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
