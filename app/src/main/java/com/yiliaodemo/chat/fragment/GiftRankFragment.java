package com.yiliaodemo.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ReceiveGiftListActivity;
import com.yiliaodemo.chat.adapter.BeautyRankRecyclerAdapter;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.bean.RankBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
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
 * 功能描述：榜单页面豪礼榜
 * 作者：
 * 创建时间：2018/9/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GiftRankFragment extends BaseFragment implements View.OnClickListener {

    public GiftRankFragment() {

    }

    private ImageView mDayIv;
    private TextView mDayTv;
    private ImageView mWeekIv;
    private TextView mWeekTv;
    private ImageView mMonthIv;
    private TextView mMonthTv;
    private ImageView mTotalIv;
    private TextView mTotalTv;
    //第一名
    private TextView mOneNickTv;
    private TextView mOneGoldTv;
    private ImageView mOneHeadIv;
    private TextView mOneChatNumberTv;
    //第二名
    private TextView mTwoNickTv;
    private TextView mTwoGoldTv;
    private ImageView mTwoHeadIv;
    private TextView mTwoChatNumberTv;
    //第三名
    private TextView mThreeNickTv;
    private TextView mThreeGoldTv;
    private ImageView mThreeHeadIv;
    private TextView mThreeChatNumberTv;
    private TextView mNoMoreTv;

    private BeautyRankRecyclerAdapter mAdapter;

    private final int DAY = 1;
    private final int WEEK = 2;
    private final int MONTH = 3;
    private final int TOTAL = 4;

    @Override
    protected int initLayout() {
        return R.layout.fragment_gift_rank_layout;
    }

    @Override
    protected void initView(View view,Bundle savedInstanceState) {
        mDayIv = view.findViewById(R.id.day_iv);
        mDayTv = view.findViewById(R.id.day_tv);
        mWeekIv = view.findViewById(R.id.week_iv);
        mWeekTv = view.findViewById(R.id.week_tv);
        mMonthIv = view.findViewById(R.id.month_iv);
        mMonthTv = view.findViewById(R.id.month_tv);
        mTotalIv = view.findViewById(R.id.total_iv);
        mTotalTv = view.findViewById(R.id.total_tv);
        //第一名
        mOneNickTv = view.findViewById(R.id.one_nick_tv);
        mOneGoldTv = view.findViewById(R.id.one_gold_tv);
        mOneHeadIv = view.findViewById(R.id.one_head_iv);
        mOneChatNumberTv = view.findViewById(R.id.one_chat_number_tv);
        //第二名
        mTwoNickTv = view.findViewById(R.id.two_nick_tv);
        mTwoGoldTv = view.findViewById(R.id.two_gold_tv);
        mTwoHeadIv = view.findViewById(R.id.two_head_iv);
        mTwoChatNumberTv = view.findViewById(R.id.two_chat_number_tv);
        //第三名
        mThreeNickTv = view.findViewById(R.id.three_nick_tv);
        mThreeGoldTv = view.findViewById(R.id.three_gold_tv);
        mThreeHeadIv = view.findViewById(R.id.three_head_iv);
        mThreeChatNumberTv = view.findViewById(R.id.three_chat_number_tv);
        mNoMoreTv = view.findViewById(R.id.no_more_tv);

        View day_fl = view.findViewById(R.id.day_fl);
        View week_fl = view.findViewById(R.id.week_fl);
        View month_fl = view.findViewById(R.id.month_fl);
        View total_fl = view.findViewById(R.id.total_fl);
        day_fl.setOnClickListener(this);
        week_fl.setOnClickListener(this);
        month_fl.setOnClickListener(this);
        total_fl.setOnClickListener(this);

        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mContentRv.setNestedScrollingEnabled(false);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new BeautyRankRecyclerAdapter(mContext, false);
        mContentRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BeautyRankRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RankBean rankBean) {
                int actorId = rankBean.t_id;
                if (actorId > 0) {
                    Intent intent = new Intent(mContext, ReceiveGiftListActivity.class);
                    intent.putExtra(Constant.ACTOR_ID, actorId);
                    intent.putExtra(Constant.NICK_NAME, rankBean.t_nickName);
                    startActivity(intent);
                }
            }
        });
        mAdapter.setOnInfoClickListener(new BeautyRankRecyclerAdapter.OnInfoClickListener() {
            @Override
            public void onInfoClick(RankBean rankBean) {
                int actorId = rankBean.t_id;
                if (actorId > 0) {
                    Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                    intent.putExtra(Constant.ACTOR_ID, actorId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onFirstVisible() {
        switchRank(TOTAL);
    }

    /**
     * 获取豪礼榜 1.日榜  2.周榜  3.月榜  4.总榜
     */
    private void getCourtesyList(int queryType) {
        mContext.showLoadingDialog();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("queryType", String.valueOf(queryType));
        OkHttpUtils.post().url(ChatApi.GET_COURTESY_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<RankBean>>() {
            @Override
            public void onResponse(BaseListResponse<RankBean> response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<RankBean> rankBeans = response.m_object;
                    if (rankBeans != null) {
                        dealBean(rankBeans);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mContext.dismissLoadingDialog();
            }
        });
    }

    /**
     * 处理bean
     */
    private void dealBean(List<RankBean> rankBeans) {
        //第一名
        if (rankBeans.size() > 0) {
            final RankBean firstBean = rankBeans.get(0);
            if (firstBean != null) {
                //昵称
                if (!TextUtils.isEmpty(firstBean.t_nickName)) {
                    mOneNickTv.setText(firstBean.t_nickName);
                    mOneNickTv.setVisibility(View.VISIBLE);
                }
                //号
                if (firstBean.t_idcard > 0) {
                    String content = getResources().getString(R.string.chat_number_one) + firstBean.t_idcard;
                    mOneChatNumberTv.setText(content);
                    mOneChatNumberTv.setVisibility(View.VISIBLE);
                }
                //金币
                if (firstBean.gold > 0) {
                    mOneGoldTv.setText(String.valueOf(firstBean.gold));
                    mOneGoldTv.setVisibility(View.VISIBLE);
                } else {
                    mOneGoldTv.setVisibility(View.INVISIBLE);
                }
                //头像
                if (!TextUtils.isEmpty(firstBean.t_handImg)) {
                    int width = DevicesUtil.dp2px(mContext, 50);
                    int high = DevicesUtil.dp2px(mContext, 50);
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, firstBean.t_handImg,
                            mOneHeadIv, width, high);
                } else {
                    mOneHeadIv.setImageResource(R.drawable.default_head_img);
                }
                mOneHeadIv.setVisibility(View.VISIBLE);
                //点击事件
                mOneHeadIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = firstBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mOneNickTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = firstBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mOneChatNumberTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = firstBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mOneGoldTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = firstBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ReceiveGiftListActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            intent.putExtra(Constant.NICK_NAME, firstBean.t_nickName);
                            startActivity(intent);
                        }
                    }
                });
            }
        } else {
            mOneNickTv.setVisibility(View.INVISIBLE);
            mOneChatNumberTv.setVisibility(View.INVISIBLE);
            mOneGoldTv.setVisibility(View.INVISIBLE);
            mOneHeadIv.setVisibility(View.INVISIBLE);
            mOneHeadIv.setOnClickListener(null);
            mOneNickTv.setOnClickListener(null);
            mOneChatNumberTv.setOnClickListener(null);
            mOneGoldTv.setOnClickListener(null);
        }

        //第二名
        if (rankBeans.size() > 1) {
            final RankBean twoBean = rankBeans.get(1);
            if (twoBean != null) {
                //昵称
                if (!TextUtils.isEmpty(twoBean.t_nickName)) {
                    mTwoNickTv.setText(twoBean.t_nickName);
                    mTwoNickTv.setVisibility(View.VISIBLE);
                }
                //号
                if (twoBean.t_idcard > 0) {
                    String content = getResources().getString(R.string.chat_number_one) + twoBean.t_idcard;
                    mTwoChatNumberTv.setText(content);
                    mTwoChatNumberTv.setVisibility(View.VISIBLE);
                }
                //金币
                if (twoBean.gold > 0) {
                    mTwoGoldTv.setText(String.valueOf(twoBean.gold));
                    mTwoGoldTv.setVisibility(View.VISIBLE);
                } else {
                    mTwoGoldTv.setVisibility(View.INVISIBLE);
                }
                //头像
                if (!TextUtils.isEmpty(twoBean.t_handImg)) {
                    int width = DevicesUtil.dp2px(mContext, 37);
                    int high = DevicesUtil.dp2px(mContext, 37);
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, twoBean.t_handImg,
                            mTwoHeadIv, width, high);
                } else {
                    mTwoHeadIv.setImageResource(R.drawable.default_head_img);
                }
                mTwoHeadIv.setVisibility(View.VISIBLE);
                //点击事件
                mTwoHeadIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = twoBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mTwoNickTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = twoBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mTwoChatNumberTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = twoBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mTwoGoldTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = twoBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ReceiveGiftListActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            intent.putExtra(Constant.NICK_NAME, twoBean.t_nickName);
                            startActivity(intent);
                        }
                    }
                });
            }
        } else {
            mTwoNickTv.setVisibility(View.INVISIBLE);
            mTwoChatNumberTv.setVisibility(View.INVISIBLE);
            mTwoGoldTv.setVisibility(View.INVISIBLE);
            mTwoHeadIv.setVisibility(View.INVISIBLE);
            mTwoHeadIv.setOnClickListener(null);
            mTwoNickTv.setOnClickListener(null);
            mTwoChatNumberTv.setOnClickListener(null);
            mTwoGoldTv.setOnClickListener(null);
        }
        //第三名
        if (rankBeans.size() > 2) {
            final RankBean threeBean = rankBeans.get(2);
            if (threeBean != null) {
                //昵称
                if (!TextUtils.isEmpty(threeBean.t_nickName)) {
                    mThreeNickTv.setText(threeBean.t_nickName);
                    mThreeNickTv.setVisibility(View.VISIBLE);
                }
                //号
                if (threeBean.t_idcard > 0) {
                    String content = getResources().getString(R.string.chat_number_one) + threeBean.t_idcard;
                    mThreeChatNumberTv.setText(content);
                    mThreeChatNumberTv.setVisibility(View.VISIBLE);
                }
                //金币
                if (threeBean.gold > 0) {
                    mThreeGoldTv.setText(String.valueOf(threeBean.gold));
                    mThreeGoldTv.setVisibility(View.VISIBLE);
                } else {
                    mThreeGoldTv.setVisibility(View.INVISIBLE);
                }
                //头像
                if (!TextUtils.isEmpty(threeBean.t_handImg)) {
                    int width = DevicesUtil.dp2px(mContext, 37);
                    int high = DevicesUtil.dp2px(mContext, 37);
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, threeBean.t_handImg,
                            mThreeHeadIv, width, high);
                } else {
                    mThreeHeadIv.setImageResource(R.drawable.default_head_img);
                }
                mThreeHeadIv.setVisibility(View.VISIBLE);
                //点击事件
                mThreeHeadIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = threeBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mThreeNickTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = threeBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mThreeChatNumberTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = threeBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            startActivity(intent);
                        }
                    }
                });
                mThreeGoldTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int actorId = threeBean.t_id;
                        if (actorId > 0) {
                            Intent intent = new Intent(mContext, ReceiveGiftListActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, actorId);
                            intent.putExtra(Constant.NICK_NAME, threeBean.t_nickName);
                            startActivity(intent);
                        }
                    }
                });
            }
        } else {
            mThreeNickTv.setVisibility(View.INVISIBLE);
            mThreeChatNumberTv.setVisibility(View.INVISIBLE);
            mThreeGoldTv.setVisibility(View.INVISIBLE);
            mThreeHeadIv.setVisibility(View.INVISIBLE);
            mThreeHeadIv.setOnClickListener(null);
            mThreeNickTv.setOnClickListener(null);
            mThreeChatNumberTv.setOnClickListener(null);
            mThreeGoldTv.setOnClickListener(null);
        }
        //处理下方数据
        if (rankBeans.size() > 3) {
            List<RankBean> lastRankBeans = rankBeans.subList(3, rankBeans.size());
            mAdapter.loadData(lastRankBeans);
            mNoMoreTv.setVisibility(View.VISIBLE);
        } else {
            //空list
            List<RankBean> lastRankBeans = new ArrayList<>();
            mAdapter.loadData(lastRankBeans);
            mNoMoreTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.day_fl: {//日榜
                switchRank(DAY);
                break;
            }
            case R.id.week_fl: {//周榜
                switchRank(WEEK);
                break;
            }
            case R.id.month_fl: {//月榜
                switchRank(MONTH);
                break;
            }
            case R.id.total_fl: {//总榜
                switchRank(TOTAL);
                break;
            }
        }
    }

    /**
     * 切换榜单
     */
    private void switchRank(int position) {
        if (position == DAY) {
            if (mDayTv.isSelected() || mDayIv.isSelected()) {
                return;
            }
            mDayIv.setSelected(true);
            mDayTv.setSelected(true);

            mWeekIv.setSelected(false);
            mWeekTv.setSelected(false);

            mMonthIv.setSelected(false);
            mMonthTv.setSelected(false);

            mTotalIv.setSelected(false);
            mTotalTv.setSelected(false);

            getCourtesyList(DAY);
        } else if (position == WEEK) {
            if (mWeekTv.isSelected() || mWeekIv.isSelected()) {
                return;
            }
            mWeekIv.setSelected(true);
            mWeekTv.setSelected(true);

            mDayIv.setSelected(false);
            mDayTv.setSelected(false);

            mMonthIv.setSelected(false);
            mMonthTv.setSelected(false);

            mTotalIv.setSelected(false);
            mTotalTv.setSelected(false);

            getCourtesyList(WEEK);
        } else if (position == MONTH) {
            if (mMonthTv.isSelected() || mMonthIv.isSelected()) {
                return;
            }
            mMonthIv.setSelected(true);
            mMonthTv.setSelected(true);

            mDayIv.setSelected(false);
            mDayTv.setSelected(false);

            mWeekIv.setSelected(false);
            mWeekTv.setSelected(false);

            mTotalIv.setSelected(false);
            mTotalTv.setSelected(false);

            getCourtesyList(MONTH);
        } else if (position == TOTAL) {
            if (mTotalTv.isSelected() || mTotalIv.isSelected()) {
                return;
            }
            mTotalTv.setSelected(true);
            mTotalIv.setSelected(true);

            mDayIv.setSelected(false);
            mDayTv.setSelected(false);

            mWeekIv.setSelected(false);
            mWeekTv.setSelected(false);

            mMonthIv.setSelected(false);
            mMonthTv.setSelected(false);

            getCourtesyList(TOTAL);
        }
    }

}
