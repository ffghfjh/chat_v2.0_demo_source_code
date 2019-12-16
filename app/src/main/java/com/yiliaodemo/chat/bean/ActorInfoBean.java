package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料bean
 * 作者：
 * 创建时间：2018/7/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorInfoBean<T, K, L, M> extends BaseBean {

    public int t_height;//身高
    public String t_autograph;//个性签名
    public String t_phone;//手机号
    public String t_weixin;//微信号
    public String t_constellation;//星座
    public int t_onLine = 3;//是否在线 0.在线1.离线
    public double t_weight;//	体重
    public int totalCount;//	粉丝数
    public String t_nickName;// 昵称
    public String t_city;//	城市
    public String t_vocation;//		职业
    public int isFollow;//		是否关注 0.未关注 1.已关注
    public String t_reception;//		接听率
    public List<T> lunbotu;//			轮播图数组
    public String t_addres_url;//	轮播图地址
    public String t_handImg;//		头像
    public List<K> lable;//标签
    public String t_login_time;//登录时间
    public List<L> anchorSetup;//	主播设置
    public int isWeixin;//是否查看过微信 0.未查看 1.已查看
    public int isPhone;//是否查看过手机号0.未查看1.已查看
    public int t_idcard;//用户号
    public int t_sex;//性别：0.女，1.男
    public int t_role;
    public int t_age;
    public M bigRoomData;//大房间直播

}
