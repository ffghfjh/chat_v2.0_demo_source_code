package com.yiliaodemo.chat.activity;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.CommentMessageRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.bean.CommentMessageBean;
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

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：评论消息页面
 * 作者：
 * 创建时间：2018/1/17
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CommentMessageActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int mCurrentPage = 1;
    private List<CommentMessageBean> mFocusBeans = new ArrayList<>();
    private CommentMessageRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_comment_message_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.comment_message);
        initRecycler();
        getCommentMessageList(mRefreshLayout, true, 1);
    }

    /**
     * 获取个人浏览记录
     */
    private void getCommentMessageList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_NEW_COMMENT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<CommentMessageBean>>() {
            @Override
            public void onResponse(BaseListResponse<CommentMessageBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<CommentMessageBean> focusBeans = response.m_object;
                    if (focusBeans != null) {
                        int size = focusBeans.size();
                        if (isRefresh) {
                            mCurrentPage = 1;
                            mFocusBeans.clear();
                            mFocusBeans.addAll(focusBeans);
                            mAdapter.loadData(mFocusBeans);
                            if (mFocusBeans.size() > 0) {
                                mRefreshLayout.setEnableRefresh(true);
                            } else {
                                mRefreshLayout.setEnableRefresh(false);
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
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getCommentMessageList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getCommentMessageList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new CommentMessageRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }
}
