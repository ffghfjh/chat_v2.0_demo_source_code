package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

import cn.jpush.im.android.api.model.UserInfo;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：消息列表bean
 * 作者：
 * 创建时间：2018/7/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MessageBean extends BaseBean {

    public String t_id;//用户编号
    public String t_message_content;//		消息内容
    public int unReadCount;//未读消息数
    public String t_create_time;//	通知时间
    public String nickName;//	通知时间
    public UserInfo userInfo;
    public boolean isText;

}
