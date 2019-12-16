package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：视频聊天bean
 * 作者：
 * 创建时间：2018/7/17
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoSignBean extends BaseBean {

    public int onlineState;//用户是否有钱 -1:余额不足不能聊天   0.余额刚刚住够   1.余额大于主播设置的金币数
    public int roomId;//	房间号

}
