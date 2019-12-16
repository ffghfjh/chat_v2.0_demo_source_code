package com.yiliaodemo.chat.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.MyCpsUserRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.CpsInfoBean;
import com.yiliaodemo.chat.bean.CpsUserBean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.FileUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.util.ZXingUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的CPS页面
 * 作者：
 * 创建时间：2018/9/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MyCpsActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.gender_iv)
    ImageView mGenderIv;
    @BindView(R.id.vip_iv)
    ImageView mVipIv;
    @BindView(R.id.contact_tv)
    TextView mContactTv;
    @BindView(R.id.user_total_tv)
    TextView mUserTotalTv;
    @BindView(R.id.earn_total_tv)
    TextView mEarnTotalTv;
    @BindView(R.id.have_count_tv)
    TextView mHaveCountTv;
    @BindView(R.id.not_count_tv)
    TextView mNotCountTv;

    private int mCurrentPage = 1;
    private List<CpsUserBean> mFocusBeans = new ArrayList<>();
    private MyCpsUserRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_my_cps_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.my_cps);
        initRecycler();
        getMyCpsInfo();
        getMyCpsUserList(mRefreshLayout, true, 1);
    }

    @OnClick({R.id.url_tv, R.id.code_tv, R.id.share_to_friend_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.url_tv: {//推广链接
                showShareUrlDialog();
                break;
            }
            case R.id.code_tv: {//二维码
                showErCodeDialog();
                break;
            }
            case R.id.share_to_friend_tv: {//分享到朋友
                Intent intent = new Intent(getApplicationContext(), ShareArticleActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 显示推广链接
     */
    private void showErCodeDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_er_code_layout, null);
        setErCodeDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setErCodeDialogView(View view, final Dialog mDialog) {
        try {
            //二维码
            ImageView code_iv = view.findViewById(R.id.code_iv);
            int width = DevicesUtil.dp2px(getApplicationContext(), 222);
            int height = DevicesUtil.dp2px(getApplicationContext(), 222);
            final Bitmap codeBitmap = ZXingUtils.createQRImage("http://www.baidu.com", width, height);
            if (codeBitmap != null) {
                code_iv.setImageBitmap(codeBitmap);
            }
            //保存
            TextView save_tv = view.findViewById(R.id.save_tv);
            save_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (codeBitmap != null) {
                        boolean result = saveImageToGallery(getApplicationContext(), codeBitmap);
                        if (result) {
                            ToastUtil.showToast(getApplicationContext(), R.string.have_save_to_gallery);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.save_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.save_fail);
                    }
                    mDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //保存文件到指定路径
    private boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return false;
            }
        }
        File dFile = new File(Constant.ER_CODE);
        if (!dFile.exists()) {
            boolean res = dFile.mkdir();
            if (!res) {
                return false;
            }
        } else {
            FileUtil.deleteFiles(dFile.getPath());
        }
        File file = new File(dFile, "special.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示推广链接
     */
    private void showShareUrlDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_my_special_url_layout, null);
        setShareUrlDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setShareUrlDialogView(View view, final Dialog mDialog) {
        //链接
        TextView url_tv = view.findViewById(R.id.url_tv);
        url_tv.setText("http://www.baidu.com");
        //复制
        TextView copy_tv = view.findViewById(R.id.copy_tv);
        copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", "这里是要复制的文字");
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtil.showToast(getApplicationContext(), R.string.copy_success);
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * 获取CPS联盟统计
     */
    private void getMyCpsInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_TOTAL_DATEIL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<CpsInfoBean>>() {
            @Override
            public void onResponse(BaseResponse<CpsInfoBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    CpsInfoBean cpsInfoBean = response.m_object;
                    if (cpsInfoBean != null) {
                        //头像
                        String handImg = cpsInfoBean.t_handImg;
                        if (!TextUtils.isEmpty(handImg)) {
                            int width = DevicesUtil.dp2px(mContext, 60);
                            int high = DevicesUtil.dp2px(mContext, 60);
                            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHeadIv, width, high);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //真实姓名
                        String realName = cpsInfoBean.t_real_name;
                        if (!TextUtils.isEmpty(realName)) {
                            mNickTv.setText(realName);
                        }
                        //性别
                        if (cpsInfoBean.t_sex == 0) {//性别 0.女 1.男
                            mGenderIv.setImageResource(R.drawable.female_white);
                        } else {
                            mGenderIv.setImageResource(R.drawable.male_white);
                        }
                        //vip
                        if (cpsInfoBean.t_is_vip == 0) {//是否VIP 0.是 1.否
                            mVipIv.setImageResource(R.drawable.qq_vip);
                        } else {
                            mVipIv.setImageResource(R.drawable.qq_vip_not);
                        }
                        //手机号码
                        String phone = cpsInfoBean.realPhone;
                        if (!TextUtils.isEmpty(phone)) {
                            String content = getResources().getString(R.string.phone_number_one) + phone;
                            mContactTv.setText(content);
                        }
                        //用户总数
                        mUserTotalTv.setText(String.valueOf(cpsInfoBean.totalUser));
                        //总收益
                        mEarnTotalTv.setText(String.valueOf(cpsInfoBean.totalMoney));
                        //已结算
                        mHaveCountTv.setText(String.valueOf(cpsInfoBean.setMoney));
                        //未结算
                        BigDecimal not = cpsInfoBean.totalMoney.subtract(cpsInfoBean.setMoney);
                        mNotCountTv.setText(String.valueOf(not));
                    }
                }
            }
        });
    }

    /**
     * 获取个人浏览记录
     */
    private void getMyCpsUserList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_MY_CONTRIBUTION_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<CpsUserBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<CpsUserBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<CpsUserBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<CpsUserBean> focusBeans = pageBean.data;
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
                getMyCpsUserList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getMyCpsUserList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new MyCpsUserRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

}
