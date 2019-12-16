package com.yiliaodemo.chat.fragment;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.BigHouseActivity;
import com.yiliaodemo.chat.activity.UploadActivity;
import com.yiliaodemo.chat.adapter.BigHouseListRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.BigRoomListBean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.MenuHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.samlss.timomenu.TimoItemViewParameter;
import me.samlss.timomenu.TimoMenu;
import me.samlss.timomenu.animation.ItemAnimation;
import me.samlss.timomenu.animation.ScaleItemAnimation;
import me.samlss.timomenu.interfaces.OnTimoItemClickListener;
import me.samlss.timomenu.interfaces.TimoMenuListener;
import me.samlss.timomenu.view.TimoItemView;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间直播页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LiveFragment extends BaseFragment {

    public LiveFragment() {

    }

    private SmartRefreshLayout mRefreshLayout;
    private BigHouseListRecyclerAdapter mAdapter;//用于grid
    private List<BigRoomListBean> mGirlListBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private Button button;

    @Override
    protected int initLayout() {
        return R.layout.fragment_live_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        final ImageView live_iv = view.findViewById(R.id.live_iv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getLiveList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getLiveList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new BigHouseListRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
        mGirlListBeans.add(0, null);
        mAdapter.loadData(mGirlListBeans);

        //1 主播 0 用户,主播才显示开播
        if (mContext.getUserRole() == 1) {
            live_iv.setVisibility(View.VISIBLE);
            live_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity().getApplication());
                    View view = inflater.inflate(R.layout.layout_out_video, null);
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setView(view);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                    RelativeLayout liveLay,videoLay;
                    liveLay = view.findViewById(R.id.lay_live);
                    videoLay = view.findViewById(R.id.lay_video);
                    videoLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext().getApplicationContext(), UploadActivity.class);
                            startActivity(intent);
                        }
                    });
                    liveLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtil.showToast(getContext(),"敬请期待~~");
                        }
                    });

//                    Intent intent = new Intent(mContext, BigHouseActivity.class);
////                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR);
////                    intent.putExtra(Constant.ACTOR_ID, Integer.parseInt(mContext.getUserId()));
////                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onFirstVisible() {
        getLiveList(mRefreshLayout, true, 1);
    }

    /**
     * 获取女神列表数据
     */
    private void getLiveList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_BIG_ROOM_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<BigRoomListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<BigRoomListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<BigRoomListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<BigRoomListBean> girlListBeans = pageBean.data;
                        if (girlListBeans != null) {
                            int size = girlListBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mGirlListBeans.clear();
                                mGirlListBeans.add(0, null);
                                mGirlListBeans.addAll(girlListBeans);
                                mAdapter.loadData(mGirlListBeans);
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mGirlListBeans.addAll(girlListBeans);
                                mAdapter.loadData(mGirlListBeans);
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

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.resumeChange();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.pauseChange();
        }
    }

}
