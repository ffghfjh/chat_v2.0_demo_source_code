package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：礼物bean
 * 作者：
 * 创建时间：2018/6/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GiftBean extends BaseBean {

    public int t_gift_id;
    public String t_gift_name;//礼物名称
    public String t_gift_gif_url;//动态图地址
    public String t_gift_still_url;//静态图地址
    public int t_gift_gold;//消耗金币
    public boolean isSelected = false;//是否选中

}
