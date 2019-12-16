package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.UserAlbumListRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.AlbumBean;
import com.yiliaodemo.chat.bean.AlbumResponseBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：个人资料相册列表页面
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserAlbumListActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int mCurrentPage = 1;
    private List<AlbumBean> mFocusBeans = new ArrayList<>();
    private UserAlbumListRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_user_album_list_layout);
    }

    @Override
    protected void onContentAdded() {
        initStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAlbumList(mRefreshLayout, true, 1);
    }

    /**
     * 初始化
     */
    private void initStart() {
        setTitle(getString(R.string.album));

        mRightTv.setBackgroundResource(R.drawable.active_camera);
        mRightTv.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightTv.getLayoutParams();
        params.rightMargin = DevicesUtil.dp2px(getApplicationContext(), 15);
        mRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);
            }
        });

        initRecycler();
    }

    /**
     * 获取个人浏览记录
     */
    private void getAlbumList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_MY_ANNUAL_ALBUM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<AlbumResponseBean<AlbumBean>>>() {
            @Override
            public void onResponse(BaseResponse<AlbumResponseBean<AlbumBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    AlbumResponseBean<AlbumBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        //列表
                        List<AlbumBean> focusBeans = pageBean.data;
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
                getAlbumList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getAlbumList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new UserAlbumListRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
        mAdapter.setOnItemLongClickListener(new UserAlbumListRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(AlbumBean videoBean, View view) {
                showDeletePopup(videoBean, view);
            }
        });
    }

    /**
     * 显示删除Popup
     */
    private void showDeletePopup(final AlbumBean videoBean, View view) {
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_photo_delete_layout, null, false);
        final PopupWindow window = new PopupWindow(contentView, DevicesUtil.dp2px(getApplicationContext(), 81),
                DevicesUtil.dp2px(getApplicationContext(), 81), true);
        TextView deleteTv = contentView.findViewById(R.id.delete_tv);
        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoBean != null) {
                    deletePhoto(videoBean, window);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.delete_fail);
                    window.dismiss();
                }
            }
        });
        TextView cancelTv = contentView.findViewById(R.id.cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view, 0, 0);
    }

    /**
     * 删除照片/视频
     */
    private void deletePhoto(AlbumBean bean, final PopupWindow window) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("photoId", String.valueOf(bean.t_id));
        OkHttpUtils.post().url(ChatApi.DEL_MY_PHOTO)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        getAlbumList(mRefreshLayout, true, 1);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.delete_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                window.dismiss();
                dismissLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }
}
