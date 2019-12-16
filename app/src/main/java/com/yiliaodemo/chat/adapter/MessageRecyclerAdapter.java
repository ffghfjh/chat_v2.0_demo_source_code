package com.yiliaodemo.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.CallListActivity;
import com.yiliaodemo.chat.activity.ChatActivity;
import com.yiliaodemo.chat.activity.SystemMessageActivity;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.bean.MessageBean;
import com.yiliaodemo.chat.bean.UnReadBean;
import com.yiliaodemo.chat.bean.UnReadMessageBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.util.CodeUtil;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：消息RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<MessageBean> mBeans = new ArrayList<>();
    private final int SYSTEM = 0;
    private UnReadBean<UnReadMessageBean> mSystemBean;

    public MessageRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<MessageBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public void loadSystemMessage(UnReadBean<UnReadMessageBean> bean) {
        mSystemBean = bean;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 1 || position == 2) {
            return SYSTEM;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SYSTEM) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_system_messgae_layout, parent, false);
            return new SystemViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_message_recycler_layout, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final MessageBean bean = mBeans.get(position);
        //3个系统
        if (holder instanceof SystemViewHolder) {
            SystemViewHolder mHolder = (SystemViewHolder) holder;
            if (position == 0) {//系统消息
                mHolder.mHeaderIv.setImageResource(R.drawable.message_system);
                mHolder.mTitleTv.setText(mContext.getResources().getString(R.string.system_message));
                mHolder.mContentTv.setVisibility(View.VISIBLE);
                mHolder.mDivineV.setVisibility(View.GONE);
                mHolder.mOfficialTv.setVisibility(View.GONE);
                if (mSystemBean != null) {
                    if (mSystemBean.totalCount > 0) {
                        int count = mSystemBean.totalCount;
                        if (count <= 99) {
                            mHolder.mRedCountTv.setText(String.valueOf(count));
                            mHolder.mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_big_text_back);
                        } else {
                            mHolder.mRedCountTv.setText(mContext.getResources().getString(R.string.nine_nine));
                            mHolder.mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_nine_nine_text_back);
                        }
                        mHolder.mRedCountTv.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mRedCountTv.setVisibility(View.GONE);
                    }
                    UnReadMessageBean messageBean = mSystemBean.data;
                    if (messageBean != null && !TextUtils.isEmpty(messageBean.t_message_content)) {
                        mHolder.mContentTv.setText(messageBean.t_message_content);
                    } else {
                        mHolder.mContentTv.setText(mContext.getResources().getString(R.string.click_to_see));
                    }
                } else {
                    mHolder.mRedCountTv.setVisibility(View.GONE);
                    mHolder.mContentTv.setText(mContext.getResources().getString(R.string.click_to_see));
                }
                mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SystemMessageActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            } else if (position == 1) {//我的通话
                mHolder.mHeaderIv.setImageResource(R.drawable.message_phone);
                mHolder.mTitleTv.setText(mContext.getResources().getString(R.string.system_phone));
                mHolder.mContentTv.setVisibility(View.GONE);
                mHolder.mDivineV.setVisibility(View.GONE);
                mHolder.mOfficialTv.setVisibility(View.GONE);
                mHolder.mRedCountTv.setVisibility(View.GONE);
                mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CallListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            } else {//在线客服
                mHolder.mHeaderIv.setImageResource(R.drawable.message_online);
                mHolder.mTitleTv.setText(mContext.getResources().getString(R.string.online_service));
                mHolder.mContentTv.setText(mContext.getResources().getString(R.string.click_to_know));

                mHolder.mContentTv.setVisibility(View.VISIBLE);
                mHolder.mOfficialTv.setVisibility(View.VISIBLE);
                mHolder.mDivineV.setVisibility(View.VISIBLE);
                mHolder.mRedCountTv.setVisibility(View.GONE);
                mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CodeUtil.jumpToQQ(mContext);
                    }
                });
            }
        } else {//内容
            final MyViewHolder mHolder = (MyViewHolder) holder;
            if (bean != null) {
                //内容
                String content = bean.t_message_content;
                if (!TextUtils.isEmpty(content)) {
                    SpannableStringBuilder stringBuilder;
                    if (bean.isText) {
                        stringBuilder = getString(content);
                        if (stringBuilder == null) {
                            stringBuilder = new SpannableStringBuilder(content);
                        }
                    } else {
                        stringBuilder = new SpannableStringBuilder(content);
                    }
                    mHolder.mContentTv.setText(stringBuilder);
                }
                //头像
                String headUrl = bean.userInfo.getAvatar();
                if (!TextUtils.isEmpty(headUrl)) {
                    bean.userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int status, String desc, Bitmap bitmap) {
                            if (status == 0) {
                                int overWidth = DevicesUtil.dp2px(mContext, 40);
                                int overHeight = DevicesUtil.dp2px(mContext, 40);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                final byte[] bytes = baos.toByteArray();
                                ImageLoadHelper.glideShowCircleImageWithByte(mContext, bytes, mHolder.mHeaderIv, overWidth, overHeight);
                            } else {
                                mHolder.mHeaderIv.setImageResource(R.drawable.default_head_img);
                            }
                        }
                    });
                } else {
                    mHolder.mHeaderIv.setImageResource(R.drawable.default_head_img);
                }
                //昵称
                String nickName = bean.nickName;
                if (!TextUtils.isEmpty(nickName)) {
                    mHolder.mTitleTv.setText(nickName);
                } else {
                    String nickContent = mContext.getResources().getString(R.string.chat_user) + bean.t_id;
                    mHolder.mTitleTv.setText(nickContent);
                }
                //时间
                String time = bean.t_create_time;
                if (!TextUtils.isEmpty(time)) {
                    mHolder.mTimeTv.setText(time);
                    mHolder.mTimeTv.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mTimeTv.setVisibility(View.GONE);
                }
                //未读消息
                if (bean.unReadCount > 0) {
                    if (bean.unReadCount <= 99) {
                        mHolder.mRedCountTv.setText(String.valueOf(bean.unReadCount));
                        mHolder.mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_big_text_back);
                    } else {
                        mHolder.mRedCountTv.setText(mContext.getResources().getString(R.string.nine_nine));
                        mHolder.mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_nine_nine_text_back);
                    }
                    mHolder.mRedCountTv.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mRedCountTv.setVisibility(View.GONE);
                }
                mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra(Constant.TITLE, bean.nickName);
                        intent.putExtra(Constant.ACTOR_ID, Integer.parseInt(bean.t_id) - 10000);
                        intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                        intent.putExtra(Constant.MINE_ID, mContext.getUserId());
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class SystemViewHolder extends RecyclerView.ViewHolder {

        View mContentLl;
        ImageView mHeaderIv;
        TextView mTitleTv;
        TextView mOfficialTv;
        TextView mContentTv;
        TextView mRedCountTv;
        View mDivineV;

        SystemViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mHeaderIv = itemView.findViewById(R.id.header_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mOfficialTv = itemView.findViewById(R.id.official_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mRedCountTv = itemView.findViewById(R.id.red_count_tv);
            mDivineV = itemView.findViewById(R.id.divine_v);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentLl;
        ImageView mHeaderIv;
        TextView mTitleTv;
        TextView mContentTv;
        TextView mTimeTv;
        TextView mRedCountTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mHeaderIv = itemView.findViewById(R.id.header_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mRedCountTv = itemView.findViewById(R.id.red_count_tv);
        }
    }

    private SpannableStringBuilder getString(String content) {
        try {
            SpannableStringBuilder returnStringBuilder = new SpannableStringBuilder();
            if (!TextUtils.isEmpty(content)) {
                int length = content.length();
                for (int i = 0; i < content.length(); i++) {
                    char ch = content.charAt(i);
                    //如果包含'[' ']'
                    if (ch == '[') {//如果后2 或者3 为]
                        if (length > i + 3) {//处理中间
                            char chOne = content.charAt(i + 2);
                            char chTwo = content.charAt(i + 3);
                            if (chOne == ']') {//如果index是个位数
                                char chIndex = content.charAt(i + 1);
                                if (Character.isDigit(chIndex)) {//如果是数字
                                    boolean res = addImageToSpan(returnStringBuilder, Character.getNumericValue(chIndex), mContext, i);
                                    if (res) {
                                        i = i + 2;
                                    }
                                }
                            } else if (chTwo == ']') {//如果是双位数
                                String index = content.substring(i + 1, i + 3);
                                if (Integer.parseInt(index) > 0) {
                                    boolean res = addImageToSpan(returnStringBuilder, Integer.parseInt(index), mContext, i);
                                    if (res) {
                                        i = i + 3;
                                    }
                                }
                            }
                        } else if (length - 1 == i + 2) {//最后一位index是各位数
                            char chOne = content.charAt(i + 2);
                            if (chOne == ']') {//如果index是个位数
                                char chIndex = content.charAt(i + 1);
                                if (Character.isDigit(chIndex)) {//如果是数字
                                    boolean res = addImageToSpan(returnStringBuilder, Character.getNumericValue(chIndex), mContext, i);
                                    if (res) {
                                        i = i + 2;
                                    }
                                }
                            }
                        } else if (length - 1 == i + 3) {//最后一位index是十位数
                            char chTwo = content.charAt(i + 3);
                            if (chTwo == ']') {//如果是双位数
                                String index = content.substring(i, i + 3);
                                if (Integer.parseInt(index) > 0) {
                                    boolean res = addImageToSpan(returnStringBuilder, Integer.parseInt(index), mContext, i);
                                    if (res) {
                                        i = i + 3;
                                    }
                                }
                            }
                        } else {
                            returnStringBuilder.append(ch);
                        }
                    } else {//如果不包含表情
                        returnStringBuilder.append(ch);
                    }
                }
            }
            return returnStringBuilder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加图片到span
     */
    private boolean addImageToSpan(SpannableStringBuilder stringBuilder, int imageIndex, Context context,
                                   int startIndex) {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(String.format(Locale.CHINA, "emoticon/[%d].gif", imageIndex));
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Matrix matrix = new Matrix();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                matrix.postScale(2, 2);
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        width, height, matrix, true);
                ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
                stringBuilder.append(String.valueOf("[" + imageIndex + "]"));
                stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(imageIndex),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getNumLength(int imageIndex) {
        return String.valueOf("[" + imageIndex + "]").length();
    }

}
