package com.yiliaodemo.chat.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.GirlRecyclerGridAdapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.BannerBean;
import com.yiliaodemo.chat.bean.GirlListBean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
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
 * 功能描述：女神页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GirlFragment extends BaseFragment {

    public GirlFragment() {

    }

    private GirlRecyclerGridAdapter mGridAdapter;//用于grid
    private List<GirlListBean> mGirlListBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private SmartRefreshLayout mRefreshLayout;
    public boolean mHaveFirstVisible = false;

    @Override
    protected int initLayout() {
        return R.layout.fragment_girl_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mContentRv.setNestedScrollingEnabled(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getRecommendList();
                getGirlList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getGirlList(refreshlayout, false, mCurrentPage + 1);
            }
        });
        //grid方式
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mContentRv.setLayoutManager(gridLayoutManager);
        mGridAdapter = new GirlRecyclerGridAdapter(mContext);
        mContentRv.setAdapter(mGridAdapter);
        //先加一条空 用于上方推荐 banner  官方热门
        mGirlListBeans.add(0, null);
    }

    @Override
    protected void onFirstVisible() {
        mHaveFirstVisible = true;
        getRecommendList();
        getBannerList();
        getGirlList(mRefreshLayout, true, 1);
    }

    /**
     * 获取推荐主播
     */
    private void getRecommendList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", "1");
        OkHttpUtils.post().url(ChatApi.GET_HOME_NOMINATE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<GirlListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<GirlListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<GirlListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<GirlListBean> girlListBeans = pageBean.data;
                        if (girlListBeans != null && girlListBeans.size() > 0) {
                            girlListBeans.add(0, null);
                            mGridAdapter.loadRecommendData(girlListBeans);
                        } else {
                            List<GirlListBean> girlListBeanList = new ArrayList<>();
                            girlListBeanList.add(0, null);
                            mGridAdapter.loadRecommendData(girlListBeanList);
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取女神列表数据
     */
    private void getGirlList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(0));
        OkHttpUtils.post().url(ChatApi.GET_HOME_PAGE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<GirlListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<GirlListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<GirlListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<GirlListBean> girlListBeans = pageBean.data;
                        if (girlListBeans != null) {
                            int size = girlListBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mGirlListBeans.clear();
                                mGirlListBeans.add(0, null);
                                mGirlListBeans.addAll(girlListBeans);
                                mGridAdapter.loadData(mGirlListBeans);
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mGirlListBeans.addAll(girlListBeans);
                                mGridAdapter.loadData(mGirlListBeans);
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
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }

        });
    }

    /**
     * 获取banner
     */
    private void getBannerList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_BANNER_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<BannerBean>>() {
            @Override
            public void onResponse(BaseListResponse<BannerBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<BannerBean> bannerBeans = response.m_object;
                    if (bannerBeans != null && bannerBeans.size() > 0) {
                        mGridAdapter.loadBannerData(bannerBeans);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible && mGridAdapter != null) {
            mGridAdapter.resumeChange();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHaveFirstVisible && mGridAdapter != null) {
            mGridAdapter.pauseChange();
        }
    }

}
