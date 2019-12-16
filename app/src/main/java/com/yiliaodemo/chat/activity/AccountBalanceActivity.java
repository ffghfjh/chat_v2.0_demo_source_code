package com.yiliaodemo.chat.activity;

import android.app.Dialog;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.yiliaodemo.chat.adapter.AccountBalanceRecyclerAdapter;
import com.yiliaodemo.chat.adapter.YearPickerRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.AccountBalanceBean;
import com.yiliaodemo.chat.bean.InOutComeBean;
import com.yiliaodemo.chat.bean.PageBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.layoutmanager.PickerLayoutManager;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：账户余额
 * 作者：
 * 创建时间：2018/10/24
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AccountBalanceActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.year_tv)
    TextView mYearTv;
    @BindView(R.id.month_tv)
    TextView mMonthTv;
    @BindView(R.id.left_number_tv)
    TextView mLeftNumberTv;
    @BindView(R.id.income_tv)
    TextView mIncomeTv;
    @BindView(R.id.out_come_tv)
    TextView mOutComeTv;

    private String mSelectYear = "";
    private boolean mYearHaveSelected = false;
    private String mSelectMonth = "";
    private boolean mMonthHaveSelected = false;

    private int mCurrentPage = 1;
    private List<AccountBalanceBean> mFocusBeans = new ArrayList<>();
    private AccountBalanceRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_account_balance_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.account_left);
        initStart();
    }

    /**
     * 初始化
     */
    private void initStart() {
        //设置年月
        Calendar calendar = Calendar.getInstance();
        mSelectYear = String.valueOf(calendar.get(Calendar.YEAR));
        String year = mSelectYear + getResources().getString(R.string.year);
        mYearTv.setText(year);
        mSelectMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        mMonthTv.setText(mSelectMonth);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getWalletDetail(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getWalletDetail(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(linearLayoutManager);
        mAdapter = new AccountBalanceRecyclerAdapter(AccountBalanceActivity.this);
        mContentRv.setAdapter(mAdapter);

        getProfitAndPayTotal();
        getWalletDetail(mRefreshLayout, true, 1);
    }

    /**
     * 获取钱包消费或者提现明细
     */
    private void getWalletDetail(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("queryType", "-1");//查询类型 -1：全部 0.收入 1.支出
        paramMap.put("year", mSelectYear);
        paramMap.put("month", mSelectMonth);
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_USER_GOLD_DETAILS)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<AccountBalanceBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<AccountBalanceBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<AccountBalanceBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<AccountBalanceBean> focusBeans = pageBean.data;
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
                    try {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        if (isRefresh) {
                            mFocusBeans.clear();
                            mAdapter.loadData(mFocusBeans);
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (isRefresh) {
                    showLoadingDialog();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (isRefresh) {
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                try {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        mFocusBeans.clear();
                        mAdapter.loadData(mFocusBeans);
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * 1.4版 钱包头部统计
     */
    private void getProfitAndPayTotal() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("year", mSelectYear);
        paramMap.put("month", mSelectMonth);
        OkHttpUtils.post().url(ChatApi.GET_PROFIT_AND_PAY_TOTAL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<InOutComeBean>>() {
            @Override
            public void onResponse(BaseResponse<InOutComeBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    InOutComeBean bean = response.m_object;
                    if (bean != null) {
                        //收入
                        int income = bean.profit;
                        mIncomeTv.setText(String.valueOf(income));
                        //支出
                        int outCome = bean.pay;
                        mOutComeTv.setText(String.valueOf(outCome));
                        //剩余金币
                        int left = income - outCome;
                        mLeftNumberTv.setText(String.valueOf(left));
                        if (left <= 0) {
                            mLeftNumberTv.setTextColor(getResources().getColor(R.color.black_3f3b48));
                        } else {
                            mLeftNumberTv.setTextColor(getResources().getColor(R.color.red_fe2947));
                        }
                    }
                }
            }
        });
    }

    @OnClick({R.id.year_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.year_ll: {
                showCityPickerDialog();
                break;
            }
        }
    }

    /**
     * 显示年月Dialog
     */
    private void showCityPickerDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_year_month_picker_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置city picker dialog view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        final List<String> years = new ArrayList<>();
        for (int i = 2018; i < 2040; i++) {
            years.add(i + getResources().getString(R.string.year));
        }
        final List<String> month = new ArrayList<>();
        month.add("1月");
        month.add("2月");
        month.add("3月");
        month.add("4月");
        month.add("5月");
        month.add("6月");
        month.add("7月");
        month.add("8月");
        month.add("9月");
        month.add("10月");
        month.add("11月");
        month.add("12月");
        //左边
        YearPickerRecyclerAdapter leftAdapter = new YearPickerRecyclerAdapter(this);
        RecyclerView left_rv = view.findViewById(R.id.left_rv);
        PickerLayoutManager leftManager = new PickerLayoutManager(getApplicationContext(),
                left_rv, PickerLayoutManager.VERTICAL, false, 3, 0.3f, true);
        left_rv.setLayoutManager(leftManager);
        left_rv.setAdapter(leftAdapter);
        leftAdapter.loadData(years);

        //右边
        final YearPickerRecyclerAdapter rightAdapter = new YearPickerRecyclerAdapter(this);
        RecyclerView right_rv = view.findViewById(R.id.right_rv);
        PickerLayoutManager rightManager = new PickerLayoutManager(getApplicationContext(),
                right_rv, PickerLayoutManager.VERTICAL, false, 3, 0.3f, true);
        right_rv.setLayoutManager(rightManager);
        right_rv.setAdapter(rightAdapter);
        rightAdapter.loadData(month);

        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        leftManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                String select = years.get(position);
                String[] result = select.split(getResources().getString(R.string.year));
                if (result.length > 0) {
                    mSelectYear = result[0];
                    mYearHaveSelected = true;
                }
            }
        });

        rightManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                String select = month.get(position);
                String[] result = select.split(getResources().getString(R.string.month));
                if (result.length > 0) {
                    mSelectMonth = result[0];
                    mMonthHaveSelected = true;
                }
            }
        });

        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mYearHaveSelected) {
                    mSelectYear = "2018";
                }
                if (!mMonthHaveSelected) {
                    mSelectMonth = "1";
                }

                String content = mSelectYear + getResources().getString(R.string.year);
                mYearTv.setText(content);
                mMonthTv.setText(mSelectMonth);
                mYearHaveSelected = false;
                mMonthHaveSelected = false;
                mDialog.dismiss();
                //刷新数据
                getProfitAndPayTotal();
                getWalletDetail(mRefreshLayout, true, 1);
            }
        });
    }

}
