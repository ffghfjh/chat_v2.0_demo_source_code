package com.yiliaodemo.chat.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.HomeVideoRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.bean.VideoBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：短视频页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoFragment extends BaseFragment implements View.OnClickListener {

    public VideoFragment() {

    }

    private SmartRefreshLayout mRefreshLayout;
    private HomeVideoRecyclerAdapter mAdapter;
    private List<VideoBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;

    private ImageView mAllIv;
    private TextView mAllTv;

    private ImageView mFreeIv;
    private TextView mFreeTv;

    private ImageView mChargeIv;
    private TextView mChargeTv;

    //请求类型 -1：全部 0.免费  1.私密
    private final int ALL = -1;
    private final int FREE = 0;
    private final int CHARGE = 1;
    private int mQueryType = -1;

    @Override
    protected int initLayout() {
        return R.layout.fragment_date_layout;
    }

    @Override
    protected void initView(View view,Bundle savedInstanceState) {
        RecyclerView mVideoRv = view.findViewById(R.id.video_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getVideoList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getVideoList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        mAllIv = view.findViewById(R.id.all_iv);
        mAllTv = view.findViewById(R.id.all_tv);
        mFreeIv = view.findViewById(R.id.free_iv);
        mFreeTv = view.findViewById(R.id.free_tv);
        mChargeIv = view.findViewById(R.id.charge_iv);
        mChargeTv = view.findViewById(R.id.charge_tv);

        View all_fl = view.findViewById(R.id.all_fl);
        all_fl.setOnClickListener(this);
        View free_fl = view.findViewById(R.id.free_fl);
        free_fl.setOnClickListener(this);
        View charge_fl = view.findViewById(R.id.charge_fl);
        charge_fl.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mVideoRv.setLayoutManager(gridLayoutManager);
        mAdapter = new HomeVideoRecyclerAdapter(mContext);
        mVideoRv.setAdapter(mAdapter);

    }

    @Override
    protected void onFirstVisible() {
        switchSelect(ALL);
    }

    /**
     * 获取主播视频照片
     */
    private void getVideoList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(mQueryType));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_LIST)
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
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    }
                } else {
                    ToastUtil.showToast(getContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_fl: {//全部
                switchSelect(ALL);
                break;
            }
            case R.id.free_fl: {//免费
                switchSelect(FREE);
                break;
            }
            case R.id.charge_fl: {//收费
                switchSelect(CHARGE);
                break;
            }
        }
    }

    /**
     * 切换选中
     */
    private void switchSelect(int position) {
        try {
            if (position == ALL) {
                if (mAllTv.isSelected() || mAllIv.isSelected()) {
                    return;
                }

                mAllTv.setSelected(true);
                mAllIv.setSelected(true);

                mFreeTv.setSelected(false);
                mFreeIv.setSelected(false);

                mChargeTv.setSelected(false);
                mChargeIv.setSelected(false);

                mQueryType = ALL;
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            } else if (position == FREE) {
                if (mFreeTv.isSelected() || mFreeIv.isSelected()) {
                    return;
                }
                mFreeTv.setSelected(true);
                mFreeIv.setSelected(true);

                mAllTv.setSelected(false);
                mAllIv.setSelected(false);

                mChargeTv.setSelected(false);
                mChargeIv.setSelected(false);

                mQueryType = FREE;
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            } else if (position == CHARGE) {
                if (mChargeTv.isSelected() || mChargeIv.isSelected()) {
                    return;
                }
                mChargeTv.setSelected(true);
                mChargeIv.setSelected(true);

                mFreeTv.setSelected(false);
                mFreeIv.setSelected(false);

                mAllTv.setSelected(false);
                mAllIv.setSelected(false);

                mQueryType = CHARGE;
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
