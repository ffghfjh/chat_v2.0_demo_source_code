package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：提现页面bean
 * 作者：
 * 创建时间：2018/8/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AccountBean extends BaseBean {

    public int t_id;//	数据编号
    public int t_type;//账号类型:0.支付宝 1.微信
    public String t_real_name;//真实姓名
    public String t_nick_name;//支付宝账号名或者微信昵称
    public String t_account_number;//支付宝账号或者微信openId
    public String t_head_img;//微信头像

}
