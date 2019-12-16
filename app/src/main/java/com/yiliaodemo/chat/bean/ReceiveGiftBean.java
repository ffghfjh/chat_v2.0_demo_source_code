package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：收到礼物bean
 * 作者：
 * 创建时间：2018/7/6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ReceiveGiftBean extends BaseBean {

    public int t_consume_type;//礼物类型 7.红包 9.图片礼物
    public String t_gift_still_url;//	图片地址
    public int t_amount;//数量

}
