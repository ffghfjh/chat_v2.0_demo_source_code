package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.LabelRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.LabelBean;
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
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   评论页面
 * 作者：
 * 创建时间：2018/8/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CommentActivity extends BaseActivity {

    @BindView(R.id.star_rb)
    RatingBar mStarRb;

    private int mActorId;
    //选中的标签列表
    private LabelRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_comment_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.send_comment);

        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        initRecycler();
        getLabelList(mActorId);
    }

    /**
     * 初始化recycler
     */
    private void initRecycler() {
        RecyclerView content_rv = findViewById(R.id.content_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 4);
        content_rv.setLayoutManager(gridLayoutManager);
        mAdapter = new LabelRecyclerAdapter(getBaseContext());
        content_rv.setAdapter(mAdapter);
    }

    /**
     * 获取标签列表
     */
    private void getLabelList(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(actorId));
        OkHttpUtils.post().url(ChatApi.GET_LABEL_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<LabelBean>>() {
            @Override
            public void onResponse(BaseListResponse<LabelBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<LabelBean> mLabelBeans = response.m_object;
                    if (mLabelBeans != null && mLabelBeans.size() > 0) {
                        //随机选两个
                        Random random = new Random();
                        for (LabelBean bean : mLabelBeans) {
                            bean.selected = false;
                        }
                        int r = random.nextInt(mLabelBeans.size());
                        int r1 = random.nextInt(mLabelBeans.size());
                        mLabelBeans.get(r).selected = true;
                        if (r != r1) {
                            mLabelBeans.get(r1).selected = true;
                        } else {
                            if (r1 + 1 == mLabelBeans.size()) {
                                mLabelBeans.get(r1 - 1).selected = true;
                            } else {
                                mLabelBeans.get(r1 + 1).selected = true;
                            }
                        }
                        mAdapter.loadData(mLabelBeans);
                    }
                }
            }
        });
    }

    @OnClick({R.id.right_text, R.id.submit_tv, R.id.complain_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_tv: {
                commentActor();
                break;
            }
            case R.id.complain_tv: {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 评价主播
     */
    private void commentActor() {
        List<LabelBean> beans = mAdapter.getSelectedLabels();
        //评分
        int number = (int) mStarRb.getRating();
        //标签
        //形象标签
        StringBuilder labels = new StringBuilder();
        if (beans != null && beans.size() > 0) {
            for (LabelBean bean : beans) {
                labels.append(bean.t_id + ",");
            }
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverCommUserId", String.valueOf(mActorId));
        paramMap.put("commScore", String.valueOf(number));
        paramMap.put("lables", TextUtils.isEmpty(labels) ? "" : labels.toString());
        OkHttpUtils.post().url(ChatApi.SAVE_COMMENT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getApplicationContext(), R.string.comment_success);
                    finish();
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.comment_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.comment_fail);
                dismissLoadingDialog();
            }
        });
    }

}
