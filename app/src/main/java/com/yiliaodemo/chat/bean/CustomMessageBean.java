package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：IM自定义消息
 * 作者：
 * 创建时间：2018/8/4
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CustomMessageBean extends BaseBean {

    public String type;//礼物类型  0-金币 1-礼物
    public int gift_id;//礼物id
    public String gift_name;//礼物名称
    public String gift_gif_url;//礼物静态图
    public int gold_number;//金币数量
    public String nickName;//昵称
    public String headUrl;//头像

}
