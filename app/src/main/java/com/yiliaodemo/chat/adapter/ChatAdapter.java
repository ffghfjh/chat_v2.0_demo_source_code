package com.yiliaodemo.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.bean.CustomMessageBean;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.util.DevicesUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：聊天页面Adapter
 * 作者：
 * 创建时间：2018/7/31
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatAdapter extends BaseAdapter {

    private Activity mContext;
    private List<Message> mMessageList = new ArrayList<>();
    private String mUserUrl;
    private String mMineUrl;

    public ChatAdapter(Activity context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    public void loadUrl(String userUrl, String mineUrl) {
        mUserUrl = userUrl;
        mMineUrl = mineUrl;
    }

    @Override
    public int getCount() {
        return mMessageList != null ? mMessageList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mMessageList != null ? mMessageList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        Message bean = (Message) getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
            holder.leftMessage = convertView.findViewById(R.id.leftMessage);
            holder.rightMessage = convertView.findViewById(R.id.rightMessage);
            holder.leftAvatar = convertView.findViewById(R.id.leftAvatar);
            holder.rightAvatar = convertView.findViewById(R.id.rightAvatar);
            holder.leftPanel = convertView.findViewById(R.id.leftPanel);
            holder.rightPanel = convertView.findViewById(R.id.rightPanel);
            holder.leftGiftLl = convertView.findViewById(R.id.left_gift_ll);
            holder.leftGoldTv = convertView.findViewById(R.id.left_gold_tv);
            holder.leftGiftNameTv = convertView.findViewById(R.id.left_gift_name_tv);
            holder.leftGiftIv = convertView.findViewById(R.id.left_gift_iv);
            holder.rightGiftLl = convertView.findViewById(R.id.right_gift_ll);
            holder.rightGiftIv = convertView.findViewById(R.id.right_gift_iv);
            holder.rightGiftNameTv = convertView.findViewById(R.id.right_gift_name_tv);
            holder.rightGoldTv = convertView.findViewById(R.id.right_gold_tv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (bean != null) {
            final int overWidth = DevicesUtil.dp2px(mContext, 40);
            final int overHeight = DevicesUtil.dp2px(mContext, 40);
            //如果是自己 右边
            if (bean.getDirect() == MessageDirect.send) {
                //布局
                holder.leftPanel.setVisibility(View.GONE);
                holder.rightPanel.setVisibility(View.VISIBLE);
                //头像
                if (!TextUtils.isEmpty(mMineUrl)) {
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, mMineUrl, holder.rightAvatar, overWidth, overHeight);
                } else {
                    holder.rightAvatar.setImageResource(R.drawable.default_head_img);
                }
                //如果是文字
                if (bean.getContentType() == ContentType.text) {
                    holder.rightMessage.setVisibility(View.VISIBLE);
                    holder.rightGiftLl.setVisibility(View.GONE);
                    SpannableStringBuilder stringBuilder;
                    TextContent textContent = (TextContent) bean.getContent();
                    stringBuilder = getString(textContent.getText());
                    if (stringBuilder == null) {
                        stringBuilder = new SpannableStringBuilder(textContent.getText());
                    }
                    holder.rightMessage.setText(stringBuilder);
                } else {//如果是礼物
                    holder.rightGiftLl.setVisibility(View.VISIBLE);
                    holder.rightMessage.setVisibility(View.GONE);
                    CustomContent customContent = (CustomContent) bean.getContent();
                    CustomMessageBean customBean = parseCustomMessage(customContent);
                    if (customBean != null) {
                        if (customBean.type.equals("1")) {//礼物
                            //礼物图片
                            ImageLoadHelper.glideShowImageWithUrl(mContext, customBean.gift_gif_url, holder.rightGiftIv);
                            //礼物名称
                            String content = customBean.gift_name + mContext.getResources().getString(R.string.multi_one_one);
                            holder.rightGiftNameTv.setText(content);
                        } else if (customBean.type.equals("0")) {//金币
                            //金币图片
                            holder.rightGiftIv.setImageResource(R.drawable.ic_gold);
                            //金币名称
                            holder.rightGiftNameTv.setText(mContext.getResources().getString(R.string.gold));
                        }
                        //金币数量
                        holder.rightGoldTv.setText(String.valueOf(customBean.gold_number));
                    }
                }
            } else {//左边
                //消息接收方发送已读回执
                if (!bean.haveRead()) {
                    bean.setHaveRead(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                        }
                    });
                }
                //布局
                holder.leftPanel.setVisibility(View.VISIBLE);
                holder.rightPanel.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(mUserUrl)) {
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, mUserUrl, holder.leftAvatar, overWidth, overHeight);
                } else {
                    UserInfo userInfo = bean.getFromUser();
                    if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                            @Override
                            public void gotResult(int status, String desc, Bitmap bitmap) {
                                if (status == 0) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                    final byte[] bytes = baos.toByteArray();
                                    ImageLoadHelper.glideShowCircleImageWithByte(mContext, bytes, holder.leftAvatar, overWidth, overHeight);
                                } else {
                                    holder.leftAvatar.setImageResource(R.drawable.default_head_img);
                                }
                            }
                        });
                    } else {
                        holder.leftAvatar.setImageResource(R.drawable.default_head_img);
                    }
                }
                //如果是文字
                if (bean.getContentType() == ContentType.text) {
                    holder.leftMessage.setVisibility(View.VISIBLE);
                    holder.leftGiftLl.setVisibility(View.GONE);
                    SpannableStringBuilder stringBuilder;
                    TextContent textContent = (TextContent) bean.getContent();
                    stringBuilder = getString(textContent.getText());
                    if (stringBuilder == null) {
                        stringBuilder = new SpannableStringBuilder(textContent.getText());
                    }
                    holder.leftMessage.setText(stringBuilder);
                } else {//如果是礼物
                    holder.leftGiftLl.setVisibility(View.VISIBLE);
                    holder.leftMessage.setVisibility(View.GONE);
                    CustomContent customContent = (CustomContent) bean.getContent();
                    CustomMessageBean customBean = parseCustomMessage(customContent);
                    if (customBean != null) {
                        if (customBean.type.equals("1")) {//礼物
                            //礼物图片
                            ImageLoadHelper.glideShowImageWithUrl(mContext, customBean.gift_gif_url, holder.leftGiftIv);
                            //礼物名称
                            String content = customBean.gift_name + mContext.getResources().getString(R.string.multi_one_one);
                            holder.leftGiftNameTv.setText(content);
                        } else if (customBean.type.equals("0")) {//金币
                            //金币图片
                            holder.leftGiftIv.setImageResource(R.drawable.ic_gold);
                            //金币名称
                            holder.leftGiftNameTv.setText(mContext.getResources().getString(R.string.gold));
                        }
                        //金币数量
                        holder.leftGoldTv.setText(String.valueOf(customBean.gold_number));
                    }
                }

                final UserInfo userInfo = bean.getFromUser();
                if (userInfo != null) {
                    holder.leftAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userName = userInfo.getUserName();
                            int actorId = Integer.parseInt(userName) - 10000;
                            if (actorId > 0) {
                                Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                                intent.putExtra(Constant.ACTOR_ID, actorId);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }

            }
        }
        return convertView;
    }

    public class Holder {
        View leftPanel;
        ImageView leftAvatar;
        TextView leftMessage;
        LinearLayout leftGiftLl;
        TextView leftGoldTv;
        TextView leftGiftNameTv;
        ImageView leftGiftIv;

        View rightPanel;
        ImageView rightAvatar;
        TextView rightMessage;
        LinearLayout rightGiftLl;
        ImageView rightGiftIv;
        TextView rightGiftNameTv;
        TextView rightGoldTv;
    }

    /**
     * 解析自定义消息
     */
    private CustomMessageBean parseCustomMessage(CustomContent customElem) {
        try {
            String json = customElem.getStringValue("custom");
            return JSON.parseObject(json, CustomMessageBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                                    boolean res = addImageToSpan(returnStringBuilder, Character.getNumericValue(chIndex), mContext, i, i + 2);
                                    if (res) {
                                        i = i + 2;
                                    }
                                }
                            } else if (chTwo == ']') {//如果是双位数
                                String index = content.substring(i + 1, i + 3);
                                if (Integer.parseInt(index) > 0) {
                                    boolean res = addImageToSpan(returnStringBuilder, Integer.parseInt(index), mContext, i, i + 3);
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
                                    boolean res = addImageToSpan(returnStringBuilder, Character.getNumericValue(chIndex), mContext, i, i + 2);
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
                                    boolean res = addImageToSpan(returnStringBuilder, Integer.parseInt(index), mContext, i, i + 3);
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
                                   int startIndex, int endIndex) {
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
