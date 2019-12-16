package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

import java.math.BigDecimal;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   充值列表bean
 * 作者：
 * 创建时间：2018/6/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChargeListBean extends BaseBean {

    public int t_id;
    public int t_gold;
    public String t_describe;
    public BigDecimal t_money;
    public boolean isSelected = false;

    public ChargeListBean(int t_id, int t_gold, String t_describe, BigDecimal t_money, boolean isSelected) {
        this.t_id = t_id;
        this.t_gold = t_gold;
        this.t_describe = t_describe;
        this.t_money = t_money;
        this.isSelected = isSelected;
    }
}
