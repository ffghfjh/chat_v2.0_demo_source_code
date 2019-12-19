package com.yiliaodemo.chat.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.CameraActivity;
import com.yiliaodemo.chat.activity.CommentMessageActivity;
import com.yiliaodemo.chat.activity.MainActivity;
import com.yiliaodemo.chat.activity.PostActiveActivity;
import com.yiliaodemo.chat.adapter.ActiveRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActiveBean;
import com.yiliaodemo.chat.bean.ActiveFileBean;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.NewMessageCountBean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.LogUtil;
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
 * 功能描述：动态Fragment
 * 作者：
 * 创建时间：2018/12/18
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveFragment extends BaseFragment implements View.OnClickListener {

    public ActiveFragment() {

    }

    //全部
    private TextView mAllBigTv;
    private TextView mAllTv;
    private View mAllV;
    //关注
    private TextView mFocusBigTv;
    private TextView mFocusTv;
    private View mFocusV;
    //新消息
    private TextView mNewMessageTv;

    private SmartRefreshLayout mRefreshLayout;
    private ActiveRecyclerAdapter mAdapter;
    private List<ActiveBean<ActiveFileBean>> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private int mReqType = 0;//0.公开动态，1.关注动态
    private final int ALL = 0;//全部
    private final int FOCUS = 1;//关注

    private final int CAMERA_REQUEST_CODE = 0x116;
    public boolean mHaveFirstVisible = false;

    @Override
    protected int initLayout() {
        return R.layout.fragment_active_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        View all_ll = view.findViewById(R.id.all_ll);
        mAllBigTv = view.findViewById(R.id.all_big_tv);
        mAllTv = view.findViewById(R.id.all_tv);
        mAllV = view.findViewById(R.id.all_v);
        View focus_ll = view.findViewById(R.id.focus_ll);
        mFocusBigTv = view.findViewById(R.id.focus_big_tv);
        mFocusTv = view.findViewById(R.id.focus_tv);
        mFocusV = view.findViewById(R.id.focus_v);
        mNewMessageTv = view.findViewById(R.id.new_message_tv);
        all_ll.setOnClickListener(this);
        focus_ll.setOnClickListener(this);
        ImageView camera_iv = view.findViewById(R.id.camera_iv);
        camera_iv.setOnClickListener(this);
        mNewMessageTv.setOnClickListener(this);

        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getActiveList(refreshlayout, true, 1);
                getNewCommentCount();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getActiveList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new ActiveRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);

        //只有主播能发布
        if (getUserRole() == 1) {
            camera_iv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onFirstVisible() {
        mHaveFirstVisible = true;
        switchTab(ALL);
        getNewCommentCount();
    }

    /**
     * 获取通知记录数或者新动态
     */
    public void getNewCommentCount() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_DYNAMIC_NOTICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<NewMessageCountBean>>() {
            @Override
            public void onResponse(BaseListResponse<NewMessageCountBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<NewMessageCountBean> bean = response.m_object;
                    if (mNewMessageTv != null) {
                        if (bean != null && bean.size() > 0) {
                            NewMessageCountBean countBean = bean.get(0);
                            if (countBean != null && countBean.t_mesg_count > 0 && countBean.t_role == 1) {
                                String content = countBean.t_mesg_count + getResources().getString(R.string.new_message);
                                mNewMessageTv.setText(content);
                                mNewMessageTv.setVisibility(View.VISIBLE);
                            } else {
                                mNewMessageTv.setVisibility(View.GONE);
                            }
                        } else {
                            mNewMessageTv.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取动态列表
     */
    private void getActiveList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("reqType", String.valueOf(mReqType));//0.公开动态，1.关注动态
        OkHttpUtils.post().url(ChatApi.GET_USER_DYNAMIC_LIST)
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
            case R.id.all_ll: {
                switchTab(ALL);
                break;
            }
            case R.id.focus_ll: {
                switchTab(FOCUS);
                break;
            }
            case R.id.camera_iv: {//发布
                showPostDialog();
                break;
            }
            case R.id.new_message_tv: {//新消息
                if (mNewMessageTv != null) {
                    mNewMessageTv.setVisibility(View.GONE);
                }
                //清除红点
                ((MainActivity) mContext).clearActiveRedPot();
                Intent intent = new Intent(getContext(), CommentMessageActivity.class);
                startActivity(intent);
                break;
            }
        }
    }


    /**
     * 显示发布dialog
     */
    private void showPostDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_post_active_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //其他
        TextView other_tv = view.findViewById(R.id.other_tv);
        other_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActiveActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
        //拍摄
        TextView shoot_tv = view.findViewById(R.id.shoot_tv);
        shoot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToCamera();
                mDialog.dismiss();
            }
        });
        //相册
        TextView album_tv = view.findViewById(R.id.album_tv);
        album_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageHelper.openPictureVideoChoosePage(getActivity(), Constant.REQUEST_ALBUM_IMAGE_AND_VIDEO);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 切换
     */
    private void switchTab(int position) {
        if (position == ALL) {//全部
            if (mAllV.getVisibility() == View.VISIBLE) {
                return;
            }
            mAllBigTv.setVisibility(View.VISIBLE);
            mAllV.setVisibility(View.VISIBLE);
            mAllTv.setVisibility(View.GONE);
            mFocusTv.setVisibility(View.VISIBLE);
            mFocusBigTv.setVisibility(View.GONE);
            mFocusV.setVisibility(View.INVISIBLE);
            mReqType = 0;
        } else if (position == FOCUS) {//关注
            if (mFocusV.getVisibility() == View.VISIBLE) {
                return;
            }
            mFocusV.setVisibility(View.VISIBLE);
            mFocusTv.setVisibility(View.GONE);
            mFocusBigTv.setVisibility(View.VISIBLE);
            mAllBigTv.setVisibility(View.GONE);
            mAllTv.setVisibility(View.VISIBLE);
            mAllV.setVisibility(View.INVISIBLE);
            mReqType = 1;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    /**
     * 获取角色
     */
    private int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_role;
            }
        }
        return 0;
    }

    /**
     * 跳转到拍照
     */
    private void jumpToCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(mContext, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, 100);
            }
        } else {
            Intent intent = new Intent(getContext(), CameraActivity.class);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && data != null) {//相机返回
            if (resultCode == 101) {//图片
                String imagePath = data.getStringExtra("imagePath");
                LogUtil.i("相机拍照图片:  " + imagePath);
                if (!TextUtils.isEmpty(imagePath)) {
                    //跳转到发布页面
                    int shoot = 1;//拍摄
                    Intent intent = new Intent(getContext(), PostActiveActivity.class);
                    intent.putExtra(Constant.POST_FROM, shoot);
                    intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_IMAGE);
                    intent.putExtra(Constant.POST_IMAGE_PATH, imagePath);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.file_invalidate);
                }
            } else if (resultCode == 102) {//视频
                String videoUrl = data.getStringExtra("videoUrl");
                LogUtil.i("相机录视频Url:  " + videoUrl);
                if (!TextUtils.isEmpty(videoUrl)) {
                    //跳转到发布页面
                    int shoot = 1;//拍摄
                    Intent intent = new Intent(getContext(), PostActiveActivity.class);
                    intent.putExtra(Constant.POST_FROM, shoot);
                    intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_VIDEO);
                    intent.putExtra(Constant.POST_VIDEO_URL, videoUrl);
                    startActivity(intent);
                }
            }
        }
    }

}
