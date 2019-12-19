package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.bean.TudiBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的徒弟 徒孙RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TudiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<TudiBean> mBeans = new ArrayList<>();

    public TudiRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<TudiBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_tudi_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TudiBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //主播昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNameTv.setText(nick);
            }
            //时间
            String time = bean.t_create_time;
            if (!TextUtils.isEmpty(time)) {
                mHolder.mTimeTv.setText(time);
            } else {
                mHolder.mTimeTv.setText(null);
            }
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 40);
                int high = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //钱
            mHolder.mMoneyTv.setText(String.valueOf(bean.spreadMoney));
            //是否认证 0.普通用户1.主播
            int t_role = bean.t_role;
            if (t_role == 1) {//主播
                mHolder.mHaveVerifyIv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mHaveVerifyIv.setVisibility(View.GONE);
            }
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int actorId = bean.t_id;
                    if (actorId > 0) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, actorId);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNameTv;
        TextView mTimeTv;
        TextView mMoneyTv;
        ImageView mHaveVerifyIv;
        View mContentLl;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.header_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mMoneyTv = itemView.findViewById(R.id.money_tv);
            mHaveVerifyIv = itemView.findViewById(R.id.have_verify_iv);
            mContentLl = itemView.findViewById(R.id.content_ll);
        }

    }

}
