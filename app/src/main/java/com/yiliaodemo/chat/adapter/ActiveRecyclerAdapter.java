package com.yiliaodemo.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActiveCommentActivity;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ActorVideoPlayActivity;
import com.yiliaodemo.chat.activity.ChatActivity;
import com.yiliaodemo.chat.activity.PhotoActivity;
import com.yiliaodemo.chat.activity.PhotoViewActivity;
import com.yiliaodemo.chat.activity.ReportActivity;
import com.yiliaodemo.chat.activity.VipCenterActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActiveBean;
import com.yiliaodemo.chat.bean.ActiveFileBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.TimeUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.view.ExpandTextView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  动态RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ActiveBean<ActiveFileBean>> mBeans = new ArrayList<>();

    public ActiveRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ActiveBean<ActiveFileBean>> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_active_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActiveBean<ActiveFileBean> bean = mBeans.get(position);
        final MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //头像
            final String handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(handImg)) {
                //计算头像resize
                int smallOverWidth = DevicesUtil.dp2px(mContext, 40);
                int smallOverHeight = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHolder.mHeadIv,
                        smallOverWidth, smallOverHeight);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //昵称
            final String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
            }
            //性别:0.女 1.男
            if (bean.t_sex == 0) {
                mHolder.mGenderIv.setImageResource(R.drawable.female_white_new);
            } else {
                mHolder.mGenderIv.setImageResource(R.drawable.male_white_new);
            }
            //年龄
            int age = bean.t_age;
            if (age > 0) {
                mHolder.mAgeTv.setText(String.valueOf(age));
                mHolder.mAgeTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mAgeTv.setVisibility(View.GONE);
            }
            //时间
            long time = bean.t_create_time;
            if (time > 0) {
                mHolder.mTimeTv.setText(TimeUtil.getTimeStr(time));
                mHolder.mTimeTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mTimeTv.setVisibility(View.GONE);
            }
            //位置
            if (!TextUtils.isEmpty(bean.t_address)) {
                mHolder.mPositionTv.setText(bean.t_address);
                mHolder.mPositionTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mPositionTv.setVisibility(View.GONE);
            }
            //点赞
            mHolder.mHeartTv.setText(String.valueOf(bean.praiseCount));
            //是否点赞 0 未点赞 1 已点赞
            if (bean.isPraise == 1) {//已点赞
                mHolder.mHeartIv.setSelected(true);
            } else {
                mHolder.mHeartIv.setSelected(false);
            }
            //评论
            mHolder.mCommentTv.setText(String.valueOf(bean.commentCount));
            //是否关注 0.未关注 1.已关注
            if (bean.isFollow == 0) {
                mHolder.mFocusTv.setText(mContext.getResources().getString(R.string.focus));
                mHolder.mFocusTv.setSelected(false);
            } else {
                mHolder.mFocusTv.setText(mContext.getResources().getString(R.string.have_focus));
                mHolder.mFocusTv.setSelected(true);
            }
            //-------处理图片、文字内容------------
            dealContent(mHolder, bean);
            //------------------点击事件---------------
            //评论
            mHolder.mCommentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.dynamicId > 0) {
                        Intent intent = new Intent(mContext, ActiveCommentActivity.class);
                        intent.putExtra(Constant.ACTIVE_ID, bean.dynamicId);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.COMMENT_NUMBER, bean.commentCount);
                        mContext.startActivity(intent);
                    }
                }
            });
            //点赞
            mHolder.mHeartLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.dynamicId > 0 && !mHolder.mHeartIv.isSelected()) {
                        addHeart(mHolder.mHeartTv, mHolder.mHeartIv, bean.dynamicId);
                    }
                }
            });
            //撩她文字聊天
            mHolder.mChatHerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int userId = bean.t_id;
                    if (userId > 0) {
                        String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).headUrl;
                        Intent intent = new Intent(mContext.getApplicationContext(), ChatActivity.class);
                        intent.putExtra(Constant.TITLE, nick);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.USER_HEAD_URL, handImg);
                        intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                        intent.putExtra(Constant.MINE_ID, mContext.getUserId());
                        mContext.startActivity(intent);
                    }
                }
            });
            //关注
            mHolder.mFocusTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        String text = mHolder.mFocusTv.getText().toString().trim();
                        if (text.equals(mContext.getResources().getString(R.string.focus))) {//未关注
                            saveFollow(bean.t_id, mHolder.mFocusTv);
                        } else {
                            cancelFollow(bean.t_id, mHolder.mFocusTv);
                        }
                    }
                }
            });
            //跳转到信息
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        //Intent intent = new Intent(mContext, ActorInfoActivity.class);
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                }
            });
            //举报
            mHolder.mMoreIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        showReportDialog(bean.t_id);
                    }
                }
            });
        }
    }

    /**
     * 添加关注
     */
    private void saveFollow(int actorId, final TextView mFocusTv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());//关注人
        paramMap.put("coverFollowUserId", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(mContext, R.string.focus_success);
                    mFocusTv.setText(R.string.have_focus);
                    mFocusTv.setSelected(true);
                }
            }
        });
    }

    /**
     * 取消关注
     */
    private void cancelFollow(int actorId, final TextView mFocusTv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());//关注人
        paramMap.put("coverFollow", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.DEL_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(mContext, R.string.cancel_focus_success);
                    mFocusTv.setText(R.string.focus);
                    mFocusTv.setSelected(false);
                }
            }
        });
    }

    /**
     * 添加点赞
     */
    private void addHeart(final TextView heart_tv, final ImageView heart_iv, int dynamicId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("dynamicId", String.valueOf(dynamicId));
        OkHttpUtils.post().url(ChatApi.GIVE_THE_THUMB_UP)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    heart_iv.setSelected(true);
                    String number = heart_tv.getText().toString().trim();
                    String last = String.valueOf(Integer.parseInt(number) + 1);
                    heart_tv.setText(last);
                    ToastUtil.showToast(mContext, R.string.heart_success);
                }
            }
        });
    }

    /**
     * 处理图片
     */
    private void dealContent(final MyViewHolder mHolder, final ActiveBean<ActiveFileBean> bean) {
        //---------------文字---------------------------
        String contentText = bean.t_content;
        if (!TextUtils.isEmpty(contentText)) {
            mHolder.mTextContentLl.setVisibility(View.VISIBLE);
            mHolder.mExpandTv.setText(contentText, false, new ExpandTextView.Callback() {
                @Override
                public void onExpand() {
                    // 展开状态，比如：显示“收起”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.VISIBLE);
                    mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.collapse));
                }

                @Override
                public void onCollapse() {
                    // 收缩状态，比如：显示“查看”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.VISIBLE);
                    mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.see_all));
                }

                @Override
                public void onLoss() {
                    // 不满足展开的条件，比如：隐藏“全文”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.GONE);
                }
            });
            //点击收起
            mHolder.mSeeMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mHolder.mSeeMoreTv.getText().toString().trim();
                    //查看全部
                    if (text.equals(mContext.getResources().getString(R.string.see_all))) {
                        //切换状态
                        mHolder.mExpandTv.setChanged(true);
                        mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.collapse));
                    } else {
                        mHolder.mExpandTv.setChanged(false);
                        mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.see_all));
                    }
                }
            });
        } else {
            mHolder.mTextContentLl.setVisibility(View.GONE);
        }
        //--------------图片视频-----------------------
        final List<ActiveFileBean> fileBeans = bean.dynamicFiles;
        if (fileBeans != null && fileBeans.size() > 0) {
            mHolder.mImageFl.setVisibility(View.VISIBLE);
            //-------------------如果只有一个文件-------------------
            if (fileBeans.size() == 1) {
                final ActiveFileBean fileBean = fileBeans.get(0);
                mHolder.mOneImageFl.setVisibility(View.VISIBLE);
                mHolder.mTwoImageLl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.GONE);

                //显示图片
                String imgUrl;
                int overWidth = DevicesUtil.dp2px(mContext, 180);
                int overHeight = DevicesUtil.dp2px(mContext, 240);
                if (fileBean.t_file_type == 1) {//视频
                    imgUrl = fileBean.t_cover_img_url;
                } else {//图片
                    imgUrl = fileBean.t_file_url;
                }
                if (!TextUtils.isEmpty(imgUrl)) {//0.未消费 1.已消费
                    if (fileBean.t_gold > 0 && fileBean.isConsume == 0) {//私密且未看过
                        mHolder.mOneLockFl.setVisibility(View.VISIBLE);
                        ImageLoadHelper.glideShowImageWithFade(mContext, imgUrl, mHolder.mOneImageIv,
                                overWidth, overHeight);
                    } else {
                        mHolder.mOneLockFl.setVisibility(View.GONE);
                        ImageLoadHelper.glideShowImageWithUrl(mContext, imgUrl, mHolder.mOneImageIv,
                                overWidth, overHeight);
                    }
                } else {
                    mHolder.mOneImageFl.setVisibility(View.GONE);
                }
                //视频时长
                if (!TextUtils.isEmpty(fileBean.t_video_time) && fileBean.t_file_type == 1) {
                    mHolder.mVideoTimeTv.setVisibility(View.VISIBLE);
                    mHolder.mVideoTimeTv.setText(fileBean.t_video_time);
                } else {
                    mHolder.mVideoTimeTv.setVisibility(View.GONE);
                }
                //点击事件
                mHolder.mOneImageFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fileBean.t_file_type == 1) {//视频
                            //私密 且未看过  且不是vip 且不是自己看
                            if (judgePrivate(fileBean, bean.t_id)) {
                                int video = 1;
                                showSeeWeChatRemindDialog(video, fileBean, bean.t_id);
                            } else {
                                Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                                intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ACTIVE);
                                intent.putExtra(Constant.VIDEO_URL, fileBean.t_file_url);
                                intent.putExtra(Constant.FILE_ID, fileBean.t_id);
                                intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                                intent.putExtra(Constant.COVER_URL, fileBean.t_cover_img_url);
                                mContext.startActivity(intent);
                            }
                        } else {//图片
                            if (judgePrivate(fileBean, bean.t_id)) {
                                int photo = 0;
                                showSeeWeChatRemindDialog(photo, fileBean, bean.t_id);
                            } else {
                                Intent intent = new Intent(mContext, PhotoActivity.class);
                                intent.putExtra(Constant.IMAGE_URL, fileBean.t_file_url);
                                mContext.startActivity(intent);
                            }
                        }
                    }
                });
            } else if (fileBeans.size() == 2) {//-------------------2个文件(图片)------------------
                mHolder.mOneImageFl.setVisibility(View.GONE);
                mHolder.mTwoImageLl.setVisibility(View.VISIBLE);
                mHolder.mThreeRv.setVisibility(View.GONE);
                //第一张-------
                final ActiveFileBean fileBean = fileBeans.get(0);
                int overWidth = DevicesUtil.dp2px(mContext, 126);
                int overHeight = DevicesUtil.dp2px(mContext, 135);
                if (!TextUtils.isEmpty(fileBean.t_file_url)) {
                    mHolder.mTwoImageOneIv.setVisibility(View.VISIBLE);
                    if (fileBean.t_gold > 0 && fileBean.isConsume == 0) {//私密
                        mHolder.mTwoLockOneIv.setVisibility(View.VISIBLE);
                        ImageLoadHelper.glideShowImageWithFade(mContext, fileBean.t_file_url, mHolder.mTwoImageOneIv,
                                overWidth, overHeight);
                    } else {
                        mHolder.mTwoLockOneIv.setVisibility(View.GONE);
                        ImageLoadHelper.glideShowImageWithUrl(mContext, fileBean.t_file_url, mHolder.mTwoImageOneIv,
                                overWidth, overHeight);
                    }
                } else {
                    mHolder.mTwoLockOneIv.setVisibility(View.GONE);
                    mHolder.mTwoImageOneIv.setVisibility(View.GONE);
                }
                //第二张---------
                final ActiveFileBean fileBeanTwo = fileBeans.get(1);
                if (!TextUtils.isEmpty(fileBeanTwo.t_file_url)) {
                    mHolder.mTwoImageTwoIv.setVisibility(View.VISIBLE);
                    if (fileBeanTwo.t_gold > 0 && fileBeanTwo.isConsume == 0) {//私密
                        mHolder.mTwoLockTwoIv.setVisibility(View.VISIBLE);
                        ImageLoadHelper.glideShowImageWithFade(mContext, fileBeanTwo.t_file_url, mHolder.mTwoImageTwoIv,
                                overWidth, overHeight);
                    } else {
                        mHolder.mTwoLockTwoIv.setVisibility(View.GONE);
                        ImageLoadHelper.glideShowImageWithUrl(mContext, fileBeanTwo.t_file_url, mHolder.mTwoImageTwoIv,
                                overWidth, overHeight);
                    }
                } else {
                    mHolder.mTwoLockTwoIv.setVisibility(View.GONE);
                    mHolder.mTwoImageTwoIv.setVisibility(View.GONE);
                }
                //点击事件
                //第一张
                mHolder.mTwoImageOneFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (judgePrivate(fileBean, bean.t_id)) {
                            int photo = 0;
                            showSeeWeChatRemindDialog(photo, fileBean, bean.t_id);
                        } else {
                            Intent intent = new Intent(mContext, PhotoViewActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, (Serializable) fileBeans);
                            intent.putExtra(Constant.CLICK_POSITION, 0);
                            intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                            mContext.startActivity(intent);
                        }
                    }
                });
                //第二张
                mHolder.mTwoImageTwoFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (judgePrivate(fileBeanTwo, bean.t_id)) {
                            int photo = 0;
                            showSeeWeChatRemindDialog(photo, fileBeanTwo, bean.t_id);
                        } else {
                            Intent intent = new Intent(mContext, PhotoViewActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, (Serializable) fileBeans);
                            intent.putExtra(Constant.CLICK_POSITION, 1);
                            mContext.startActivity(intent);
                        }
                    }
                });
            } else {//-----------------大于等于3个文件(图片)-----------------
                mHolder.mOneImageFl.setVisibility(View.GONE);
                mHolder.mTwoImageLl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.VISIBLE);
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                ActiveImagesRecyclerAdapter adapter = new ActiveImagesRecyclerAdapter(mContext);
                mHolder.mThreeRv.setLayoutManager(layoutManager);
                mHolder.mThreeRv.setAdapter(adapter);
                final int actorId = bean.t_id;
                adapter.setOnImageItemClickListener(new ActiveImagesRecyclerAdapter.OnImageItemClickListener() {
                    @Override
                    public void onImageItemClick(int position, ActiveFileBean bean) {
                        if (judgePrivate(bean, actorId)) {
                            int photo = 0;
                            showSeeWeChatRemindDialog(photo, bean, actorId);
                        } else {
                            Intent intent = new Intent(mContext, PhotoViewActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, (Serializable) fileBeans);
                            intent.putExtra(Constant.CLICK_POSITION, position);
                            mContext.startActivity(intent);
                        }
                    }
                });
                adapter.loadData(fileBeans);
            }
        } else {
            mHolder.mImageFl.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否应该显示消费弹窗
     */
    private boolean judgePrivate(ActiveFileBean fileBean, int actorId) {
        int mineId = Integer.parseInt(mContext.getUserId());
        return fileBean.t_gold > 0 && fileBean.isConsume == 0
                && mContext.getUserVip() == 1 && mineId != actorId;
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNickTv;
        ImageView mGenderIv;
        TextView mAgeTv;
        TextView mTimeTv;
        TextView mChatHerTv;
        //内容相关
        TextView mContentTv;
        FrameLayout mImageFl;
        //一张图
        FrameLayout mOneLockFl;
        FrameLayout mOneImageFl;
        ImageView mOneImageIv;
        //2张图
        LinearLayout mTwoImageLl;
        FrameLayout mTwoImageOneFl;
        ImageView mTwoImageOneIv;
        ImageView mTwoLockOneIv;
        FrameLayout mTwoImageTwoFl;
        ImageView mTwoImageTwoIv;
        ImageView mTwoLockTwoIv;
        //大于等于三张
        RecyclerView mThreeRv;
        //点赞
        View mHeartLl;
        ImageView mHeartIv;
        TextView mHeartTv;
        //评论
        View mCommentLl;
        ImageView mCommentIv;
        TextView mCommentTv;
        ImageView mMoreIv;
        //关注
        TextView mFocusTv;
        //位置
        TextView mPositionTv;
        //查看更多
        LinearLayout mTextContentLl;
        TextView mSeeMoreTv;
        ExpandTextView mExpandTv;
        //视频时长
        TextView mVideoTimeTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGenderIv = itemView.findViewById(R.id.gender_iv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mChatHerTv = itemView.findViewById(R.id.chat_her_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mImageFl = itemView.findViewById(R.id.image_fl);
            mOneLockFl = itemView.findViewById(R.id.one_lock_fl);
            mOneImageFl = itemView.findViewById(R.id.one_image_fl);
            mOneImageIv = itemView.findViewById(R.id.one_image_iv);
            mTwoImageLl = itemView.findViewById(R.id.two_image_ll);
            mTwoImageOneIv = itemView.findViewById(R.id.two_image_one_iv);
            mTwoLockOneIv = itemView.findViewById(R.id.two_lock_one_iv);
            mTwoImageTwoIv = itemView.findViewById(R.id.two_image_two_iv);
            mTwoLockTwoIv = itemView.findViewById(R.id.two_lock_two_iv);
            mThreeRv = itemView.findViewById(R.id.three_rv);
            mHeartLl = itemView.findViewById(R.id.heart_ll);
            mHeartIv = itemView.findViewById(R.id.heart_iv);
            mHeartTv = itemView.findViewById(R.id.heart_tv);
            mCommentLl = itemView.findViewById(R.id.comment_ll);
            mCommentIv = itemView.findViewById(R.id.comment_iv);
            mCommentTv = itemView.findViewById(R.id.comment_tv);
            mMoreIv = itemView.findViewById(R.id.more_iv);
            mFocusTv = itemView.findViewById(R.id.focus_tv);
            mPositionTv = itemView.findViewById(R.id.position_tv);
            mTextContentLl = itemView.findViewById(R.id.text_content_ll);
            mSeeMoreTv = itemView.findViewById(R.id.see_more_tv);
            mExpandTv = itemView.findViewById(R.id.expand_tv);
            mVideoTimeTv = itemView.findViewById(R.id.video_time_tv);
            mTwoImageOneFl = itemView.findViewById(R.id.two_image_one_fl);
            mTwoImageTwoFl = itemView.findViewById(R.id.two_image_two_fl);
        }
    }

    /**
     * 显示查看微信号提醒
     */
    private void showSeeWeChatRemindDialog(int position, ActiveFileBean fileBean, int actorId) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_video_layout, null);
        setDialogView(view, mDialog, position, fileBean, actorId);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog, final int position,
                               final ActiveFileBean fileBean, final int actorId) {
        //金币
        TextView gold_tv = view.findViewById(R.id.gold_tv);
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        int gold = fileBean.t_gold;
        if (position == 0) {//照片
            see_des_tv.setText(R.string.see_picture_need);
        } else {//视频
            see_des_tv.setText(R.string.see_video_need);
        }
        String content = gold + mContext.getResources().getString(R.string.gold);
        gold_tv.setText(content);
        //升级
        ImageView update_iv = view.findViewById(R.id.update_iv);
        update_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VipCenterActivity.class);
                mContext.startActivity(intent);
                mDialog.dismiss();
            }
        });
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否Vip
                seePrivate(position, fileBean, actorId);
                mDialog.dismiss();
            }
        });
    }

    /**
     * Vip查看照片 视频
     */
    private void seePrivate(final int position, final ActiveFileBean fileBean, final int mActorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("fileId", String.valueOf(fileBean.t_id));
        OkHttpUtils.post().url(ChatApi.DYNAMIC_PAY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        ToastUtil.showToast(mContext, R.string.pay_success);
                        //变更是否看过
                        fileBean.isConsume = 1;
                        notifyDataSetChanged();
                        if (position == 0) {//图片
                            Intent intent = new Intent(mContext, PhotoActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, fileBean.t_file_url);
                            mContext.startActivity(intent);
                        } else {//视频
                            Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                            intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ACTIVE);
                            intent.putExtra(Constant.VIDEO_URL, fileBean.t_file_url);
                            intent.putExtra(Constant.ACTOR_ID, mActorId);
                            intent.putExtra(Constant.FILE_ID, fileBean.t_id);
                            intent.putExtra(Constant.COVER_URL, fileBean.t_cover_img_url);
                            mContext.startActivity(intent);
                        }
                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialog(mContext);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

    /**
     * 显示头像选择dialog
     */
    private void showReportDialog(int mActorId) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_active_more_layout, null);
        setDialogView(view, mDialog, mActorId);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog, final int mActorId) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //举报
        TextView report_tv = view.findViewById(R.id.report_tv);
        report_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                mContext.startActivity(intent);
                mDialog.dismiss();
            }
        });

    }

}
