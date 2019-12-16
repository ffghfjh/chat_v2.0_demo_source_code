package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Vip bean
 * 作者：
 * 创建时间：2018/8/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VipBean extends BaseBean {

    public int t_id;
    public int t_money;
    public int t_gold;
    public String t_setmeal_name;//	套餐名称
    public boolean isSelected = false;
    public int t_cost_price;
    public int t_duration;

}
