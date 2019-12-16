package com.yiliaodemo.chat.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.InfoActiveRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActiveBean;
import com.yiliaodemo.chat.bean.ActiveFileBean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播动态页面
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoActiveActivity extends BaseActivity {

    @BindView(R.id.all_tv)
    TextView mAllTv;
    @BindView(R.id.all_v)
    View mAllV;
    @BindView(R.id.focus_tv)
    TextView mFocusTv;
    @BindView(R.id.focus_v)
    View mFocusV;
    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private InfoActiveRecyclerAdapter mAdapter;
    private List<ActiveBean<ActiveFileBean>> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private int mReqType = 0;//0.公开动态，1.关注动态
    private final int ALL = 0;//全部
    private final int FOCUS = 1;//关注
    private int mActorId;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_info_active_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        initRecycler();
        switchTab(ALL);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getActiveList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getActiveList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new InfoActiveRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

    /**
     * 获取动态列表
     */
    private void getActiveList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverUserId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(page));
        paramMap.put("reqType", String.valueOf(mReqType));//0.公开动态，1.关注动态
        OkHttpUtils.post().url(ChatApi.GET_PRIVATE_DYNAMIC_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActiveBean<ActiveFileBean>>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActiveBean<ActiveFileBean>>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActiveBean<ActiveFileBean>> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActiveBean<ActiveFileBean>> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
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
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    @OnClick({R.id.all_ll, R.id.focus_ll, R.id.back_fl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_ll: {
                switchTab(ALL);
                break;
            }
            case R.id.focus_ll: {
                switchTab(FOCUS);
                break;
            }
            case R.id.back_fl: {
                finish();
                break;
            }
        }
    }

    /**
     * 切换
     */
    private void switchTab(int position) {
        if (position == ALL) {//全部
            if (mAllTv.isSelected() || mAllV.getVisibility() == View.VISIBLE) {
                return;
            }
            mAllTv.setSelected(true);
            mAllV.setVisibility(View.VISIBLE);
            mFocusTv.setSelected(false);
            mFocusV.setVisibility(View.INVISIBLE);
            mReqType = 0;
        } else if (position == FOCUS) {//关注
            if (mFocusTv.isSelected() || mFocusV.getVisibility() == View.VISIBLE) {
                return;
            }
            mFocusTv.setSelected(true);
            mFocusV.setVisibility(View.VISIBLE);
            mAllTv.setSelected(false);
            mAllV.setVisibility(View.INVISIBLE);
            mReqType = 1;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

}
