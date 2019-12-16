package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   我的Fragment
 * 作者：
 * 创建时间：2018/7/2
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserCenterBean extends BaseBean {

    public int amount;//用户金币
    public String nickName;//用户昵称
    public String handImg;//头像地址
    public int t_role;//用户角色:0.普通用户1.主播
    public int t_is_vip = 1;//是否VIP 0.是1.否
    public int t_is_not_disturb;//是否勿扰 0.否 1.是
    public int t_idcard;//	ID号
    public int t_sex = 2;//性别:0.女 1.男
    public int isGuild;//是否申请公会 0.未申请 1.审核中 2.已通过
    public int isApplyGuild = 1;//	是否加入公会 0.未加入 1.已加入
    public String guildName;//	公会名称(用户申请了公会或者加入了公会才会存在)
    public int isCps = -1;//	 cps推广 -1:未申请 1:审核中 2:已通过 3:已下架
    public int extractGold;//可提现金币
    public String t_autograph;//	个性签名
    public int albumCount;//		相册数量
    public int dynamCount;//		动态数量
    public int followCount;//		关注数量
    public int t_age;//		用户年龄
    public int spprentice;//	徒弟数
    public String endTime;//vip到期时间

}
