package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：收入、支出、充值明细详情二级bean
 * 作者：
 * 创建时间：2018/7/11
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DetailSecondBean extends BaseBean {

    public String tTime;//时间
    public int totalMoney;//	钱
    //来源0.充值 1.聊天 2.视频 3.私密照片 4.私密视频 5.查看手机 6.查看微信 7.红包 8.VIP 9.礼物 10.提现 11.推荐分成
    public int t_change_category;

}
