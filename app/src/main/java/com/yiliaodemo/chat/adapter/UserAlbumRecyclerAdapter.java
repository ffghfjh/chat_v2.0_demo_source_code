package com.yiliaodemo.chat.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.activity.PhotoActivity;
import com.yiliaodemo.chat.activity.UserAlbumListActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.AlbumBean;
import com.yiliaodemo.chat.bean.AlbumNeedBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：账户余额RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserAlbumRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<AlbumNeedBean> mBeans = new ArrayList<>();
    private String mYear;

    public UserAlbumRecyclerAdapter(BaseActivity context, String year) {
        mContext = context;
        mYear = year;
    }

    public void loadData(List<AlbumNeedBean> beans, String year) {
        mBeans = beans;
        mYear = year;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_album_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlbumNeedBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //月份
            mHolder.mMonthTv.setText(bean.month);
            //查看更多
            int total = bean.total;
            if (total > 3) {
                String content = mContext.getResources().getString(R.string.more_one) + mContext.getResources().getString(R.string.left) + total + mContext.getResources().getString(R.string.right);
                mHolder.mMoreTv.setText(content);
                mHolder.mMoreTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mMoreTv.setVisibility(View.GONE);
            }
            mHolder.mMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(mContext, UserAlbumListActivity.class);
                        intent.putExtra(Constant.YEAR, mYear);
                        intent.putExtra(Constant.MONTH, bean.month);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            List<AlbumBean> albumBeans = bean.albumBeans;
            if (albumBeans != null) {
                //---------------------第一张-----------------
                if (albumBeans.size() > 0) {
                    mHolder.mFirstFl.setVisibility(View.VISIBLE);
                    final AlbumBean oneBean = albumBeans.get(0);
                    //审核状态
                    if (oneBean.t_auditing_type == 0) {//审核中
                        mHolder.mFirstStatusTv.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mFirstStatusTv.setVisibility(View.GONE);
                    }
                    //金币
                    int gold = oneBean.t_money;
                    if (gold > 0) {
                        mHolder.mFirstLockIv.setVisibility(View.VISIBLE);
                        mHolder.mFirstCoverV.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mFirstLockIv.setVisibility(View.GONE);
                        mHolder.mFirstCoverV.setVisibility(View.GONE);
                    }
                    //文件类型
                    final int fileType = oneBean.t_file_type;
                    final String t_addres_url = oneBean.t_addres_url;
                    final String t_video_img = oneBean.t_video_img;
                    if (fileType == 0) {//0.图片 1.视频
                        mHolder.mFirstPlayIv.setVisibility(View.GONE);
                        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                        int height = DevicesUtil.dp2px(mContext, 165);
                        if (!TextUtils.isEmpty(t_addres_url)) {
                            ImageLoadHelper.glideShowImageWithUrl(mContext, t_addres_url, mHolder.mFirstIv,
                                    width, height);
                        }
                    } else {//视频
                        mHolder.mFirstPlayIv.setVisibility(View.VISIBLE);
                        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                        int height = DevicesUtil.dp2px(mContext, 165);
                        if (!TextUtils.isEmpty(t_video_img)) {
                            ImageLoadHelper.glideShowImageWithUrl(mContext, t_video_img, mHolder.mFirstIv,
                                    width, height);
                        }
                    }
                    //点击事件
                    mHolder.mFirstFl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fileType == 1) {
                                Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                                intent.putExtra(Constant.VIDEO_URL, t_addres_url);
                                intent.putExtra(Constant.FILE_ID, oneBean.t_id);
                                intent.putExtra(Constant.ACTOR_ID, mContext.getUserId());
                                intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ALBUM);
                                intent.putExtra(Constant.COVER_URL, t_video_img);
                                mContext.startActivity(intent);
                            } else {//图片
                                if (!TextUtils.isEmpty(t_addres_url)) {
                                    Intent intent = new Intent(mContext, PhotoActivity.class);
                                    intent.putExtra(Constant.IMAGE_URL, t_addres_url);
                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    });
                    //长按
                    mHolder.mFirstFl.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mOnItemLongClickListener != null) {
                                mOnItemLongClickListener.onItemLongClick(oneBean, v);
                            }
                            return false;
                        }
                    });
                } else {
                    mHolder.mFirstFl.setVisibility(View.INVISIBLE);
                }

                //---------------------第二张-----------------------
                if (albumBeans.size() > 1) {
                    mHolder.mSecondFl.setVisibility(View.VISIBLE);
                    final AlbumBean twoBean = albumBeans.get(1);
                    //审核状态
                    if (twoBean.t_auditing_type == 0) {//审核中
                        mHolder.mSecondStatusTv.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mSecondStatusTv.setVisibility(View.GONE);
                    }
                    //金币
                    int gold = twoBean.t_money;
                    if (gold > 0) {
                        mHolder.mSecondLockIv.setVisibility(View.VISIBLE);
                        mHolder.mSecondCoverV.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mSecondLockIv.setVisibility(View.GONE);
                        mHolder.mSecondCoverV.setVisibility(View.GONE);
                    }
                    //文件类型
                    final int fileType = twoBean.t_file_type;
                    final String t_addres_url = twoBean.t_addres_url;
                    final String t_video_img = twoBean.t_video_img;
                    if (fileType == 0) {//0.图片 1.视频
                        mHolder.mSecondPlayIv.setVisibility(View.GONE);
                        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                        int height = DevicesUtil.dp2px(mContext, 165);
                        if (!TextUtils.isEmpty(t_addres_url)) {
                            ImageLoadHelper.glideShowImageWithUrl(mContext, t_addres_url, mHolder.mSecondIv,
                                    width, height);
                        }
                    } else {//视频
                        mHolder.mSecondPlayIv.setVisibility(View.VISIBLE);
                        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                        int height = DevicesUtil.dp2px(mContext, 165);
                        if (!TextUtils.isEmpty(t_video_img)) {
                            ImageLoadHelper.glideShowImageWithUrl(mContext, t_video_img, mHolder.mSecondIv,
                                    width, height);
                        }
                    }
                    //点击事件
                    mHolder.mSecondFl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fileType == 1) {
                                Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                                intent.putExtra(Constant.VIDEO_URL, t_addres_url);
                                intent.putExtra(Constant.FILE_ID, twoBean.t_id);
                                intent.putExtra(Constant.ACTOR_ID, mContext.getUserId());
                                intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ALBUM);
                                intent.putExtra(Constant.COVER_URL, t_video_img);
                                mContext.startActivity(intent);
                            } else {//图片
                                if (!TextUtils.isEmpty(t_addres_url)) {
                                    Intent intent = new Intent(mContext, PhotoActivity.class);
                                    intent.putExtra(Constant.IMAGE_URL, t_addres_url);
                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    });
                    //长按
                    mHolder.mSecondFl.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mOnItemLongClickListener != null) {
                                mOnItemLongClickListener.onItemLongClick(twoBean, v);
                            }
                            return false;
                        }
                    });
                } else {
                    mHolder.mSecondFl.setVisibility(View.INVISIBLE);
                }

                //---------------------第三张-----------------------
                if (albumBeans.size() > 2) {
                    mHolder.mThirdFl.setVisibility(View.VISIBLE);
                    final AlbumBean thirdBean = albumBeans.get(2);
                    //审核状态
                    if (thirdBean.t_auditing_type == 0) {//审核中
                        mHolder.mThirdStatusTv.setVisibility(View.VISIBLE);
                    }
                    //金币
                    int gold = thirdBean.t_money;
                    if (gold > 0) {
                        mHolder.mThirdLockIv.setVisibility(View.VISIBLE);
                        mHolder.mThirdCoverV.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mThirdLockIv.setVisibility(View.GONE);
                        mHolder.mThirdCoverV.setVisibility(View.GONE);
                    }
                    //文件类型
                    final int fileType = thirdBean.t_file_type;
                    final String t_addres_url = thirdBean.t_addres_url;
                    final String t_video_img = thirdBean.t_video_img;
                    if (fileType == 0) {//0.图片 1.视频
                        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                        int height = DevicesUtil.dp2px(mContext, 165);
                        if (!TextUtils.isEmpty(t_addres_url)) {
                            ImageLoadHelper.glideShowImageWithUrl(mContext, t_addres_url, mHolder.mThirdIv,
                                    width, height);
                        }
                    } else {//视频
                        mHolder.mThirdPlayIv.setVisibility(View.VISIBLE);
                        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                        int height = DevicesUtil.dp2px(mContext, 165);
                        if (!TextUtils.isEmpty(t_video_img)) {
                            ImageLoadHelper.glideShowImageWithUrl(mContext, t_video_img, mHolder.mThirdIv,
                                    width, height);
                        }
                    }
                    //点击事件
                    mHolder.mThirdFl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fileType == 1) {
                                Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                                intent.putExtra(Constant.VIDEO_URL, t_addres_url);
                                intent.putExtra(Constant.FILE_ID, thirdBean.t_id);
                                intent.putExtra(Constant.ACTOR_ID, mContext.getUserId());
                                intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ALBUM);
                                intent.putExtra(Constant.COVER_URL, t_video_img);
                                mContext.startActivity(intent);
                            } else {//图片
                                if (!TextUtils.isEmpty(t_addres_url)) {
                                    Intent intent = new Intent(mContext, PhotoActivity.class);
                                    intent.putExtra(Constant.IMAGE_URL, t_addres_url);
                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    });
                    //长按
                    mHolder.mThirdFl.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mOnItemLongClickListener != null) {
                                mOnItemLongClickListener.onItemLongClick(thirdBean, v);
                            }
                            return false;
                        }
                    });
                } else {
                    mHolder.mThirdFl.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mMonthTv;
        TextView mMoreTv;
        //第一张
        View mFirstFl;
        ImageView mFirstIv;
        ImageView mFirstPlayIv;
        TextView mFirstStatusTv;
        ImageView mFirstLockIv;
        View mFirstCoverV;
        //第二张
        View mSecondFl;
        ImageView mSecondIv;
        ImageView mSecondPlayIv;
        TextView mSecondStatusTv;
        ImageView mSecondLockIv;
        View mSecondCoverV;
        //第三张
        View mThirdFl;
        ImageView mThirdIv;
        ImageView mThirdPlayIv;
        TextView mThirdStatusTv;
        ImageView mThirdLockIv;
        View mThirdCoverV;

        MyViewHolder(View itemView) {
            super(itemView);
            mMonthTv = itemView.findViewById(R.id.month_tv);
            mMoreTv = itemView.findViewById(R.id.more_tv);

            mFirstFl = itemView.findViewById(R.id.first_fl);
            mFirstIv = itemView.findViewById(R.id.first_iv);
            mFirstPlayIv = itemView.findViewById(R.id.first_play_iv);
            mFirstStatusTv = itemView.findViewById(R.id.first_status_tv);
            mFirstLockIv = itemView.findViewById(R.id.first_lock_iv);
            mFirstCoverV = itemView.findViewById(R.id.first_cover_v);

            mSecondFl = itemView.findViewById(R.id.second_fl);
            mSecondIv = itemView.findViewById(R.id.second_iv);
            mSecondPlayIv = itemView.findViewById(R.id.second_play_iv);
            mSecondStatusTv = itemView.findViewById(R.id.second_status_tv);
            mSecondLockIv = itemView.findViewById(R.id.second_lock_iv);
            mSecondCoverV = itemView.findViewById(R.id.second_cover_v);

            mThirdFl = itemView.findViewById(R.id.third_fl);
            mThirdIv = itemView.findViewById(R.id.third_iv);
            mThirdPlayIv = itemView.findViewById(R.id.third_play_iv);
            mThirdStatusTv = itemView.findViewById(R.id.third_status_tv);
            mThirdLockIv = itemView.findViewById(R.id.third_lock_iv);
            mThirdCoverV = itemView.findViewById(R.id.third_cover_v);
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(AlbumBean videoBean, View view);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

}
