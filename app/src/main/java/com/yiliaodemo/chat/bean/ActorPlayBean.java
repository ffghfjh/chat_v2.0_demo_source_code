package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播视频播放页面信息bean
 * 作者：
 * 创建时间：2018/7/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorPlayBean<T,M> extends BaseBean {

    public String t_weixin;//微信号
    public String t_handImg;//用户头像
    public int t_score;//用户评分
    public String t_addres_url;//视频地址
    public int t_age;//		年龄
    public int t_weixin_gold;//	查看微信金币数
    public String t_nickName;//	用户昵称
    public String t_city;//	所在城市
    public String t_video_img;//视频封面地址
    public int isSee;//是否查看过微信 0.未查看 1.已查看
    public List<T> labels;//标签集
    public int isLaud;//当前用户是否给查看人点赞 0:未点赞 1.已点赞
    public int laudtotal;//	总的点赞数
    public String t_title;//视频标题
    public int videoGold;//视频金币
    public int t_see_count;//视频查看次数
    public int t_onLine = 3;//在线状态 0.空闲1.忙碌2.离线
    public int isFollow;//是否关注 0:未关注 1：已关注
    public M bigRoomData;//大房间直播


    public String getT_weixin() {
        return t_weixin;
    }

    public void setT_weixin(String t_weixin) {
        this.t_weixin = t_weixin;
    }

    public String getT_handImg() {
        return t_handImg;
    }

    public void setT_handImg(String t_handImg) {
        this.t_handImg = t_handImg;
    }

    public int getT_score() {
        return t_score;
    }

    public void setT_score(int t_score) {
        this.t_score = t_score;
    }

    public String getT_addres_url() {
        return t_addres_url;
    }

    public void setT_addres_url(String t_addres_url) {
        this.t_addres_url = t_addres_url;
    }

    public int getT_age() {
        return t_age;
    }

    public void setT_age(int t_age) {
        this.t_age = t_age;
    }

    public int getT_weixin_gold() {
        return t_weixin_gold;
    }

    public void setT_weixin_gold(int t_weixin_gold) {
        this.t_weixin_gold = t_weixin_gold;
    }

    public String getT_nickName() {
        return t_nickName;
    }

    public void setT_nickName(String t_nickName) {
        this.t_nickName = t_nickName;
    }

    public String getT_city() {
        return t_city;
    }

    public void setT_city(String t_city) {
        this.t_city = t_city;
    }

    public String getT_video_img() {
        return t_video_img;
    }

    public void setT_video_img(String t_video_img) {
        this.t_video_img = t_video_img;
    }

    public int getIsSee() {
        return isSee;
    }

    public void setIsSee(int isSee) {
        this.isSee = isSee;
    }

    public List<T> getLabels() {
        return labels;
    }

    public void setLabels(List<T> labels) {
        this.labels = labels;
    }

    public int getIsLaud() {
        return isLaud;
    }

    public void setIsLaud(int isLaud) {
        this.isLaud = isLaud;
    }

    public int getLaudtotal() {
        return laudtotal;
    }

    public void setLaudtotal(int laudtotal) {
        this.laudtotal = laudtotal;
    }

    public String getT_title() {
        return t_title;
    }

    public void setT_title(String t_title) {
        this.t_title = t_title;
    }

    public int getVideoGold() {
        return videoGold;
    }

    public void setVideoGold(int videoGold) {
        this.videoGold = videoGold;
    }

    public int getT_see_count() {
        return t_see_count;
    }

    public void setT_see_count(int t_see_count) {
        this.t_see_count = t_see_count;
    }

    public int getT_onLine() {
        return t_onLine;
    }

    public void setT_onLine(int t_onLine) {
        this.t_onLine = t_onLine;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public M getBigRoomData() {
        return bigRoomData;
    }

    public void setBigRoomData(M bigRoomData) {
        this.bigRoomData = bigRoomData;
    }

    public ActorPlayBean(){

    }

    public ActorPlayBean(String t_weixin, String t_handImg, int t_score, String t_addres_url, int t_age, int t_weixin_gold, String t_nickName, String t_city, String t_video_img, int isSee, List<T> labels, int isLaud, int laudtotal, String t_title, int videoGold, int t_see_count, int t_onLine, int isFollow, M bigRoomData) {
        this.t_weixin = t_weixin;
        this.t_handImg = t_handImg;
        this.t_score = t_score;
        this.t_addres_url = t_addres_url;
        this.t_age = t_age;
        this.t_weixin_gold = t_weixin_gold;
        this.t_nickName = t_nickName;
        this.t_city = t_city;
        this.t_video_img = t_video_img;
        this.isSee = isSee;
        this.labels = labels;
        this.isLaud = isLaud;
        this.laudtotal = laudtotal;
        this.t_title = t_title;
        this.videoGold = videoGold;
        this.t_see_count = t_see_count;
        this.t_onLine = t_onLine;
        this.isFollow = isFollow;
        this.bigRoomData = bigRoomData;
    }

    public ActorPlayBean(String t_weixin, String t_handImg, int t_score, String t_addres_url, int t_age, int t_weixin_gold, String t_nickName, String t_city, String t_video_img, int isSee, int isLaud, int laudtotal, String t_title, int videoGold, int t_see_count, int t_onLine, int isFollow) {
        this.t_weixin = t_weixin;
        this.t_handImg = t_handImg;
        this.t_score = t_score;
        this.t_addres_url = t_addres_url;
        this.t_age = t_age;
        this.t_weixin_gold = t_weixin_gold;
        this.t_nickName = t_nickName;
        this.t_city = t_city;
        this.t_video_img = t_video_img;
        this.isSee = isSee;
        this.isLaud = isLaud;
        this.laudtotal = laudtotal;
        this.t_title = t_title;
        this.videoGold = videoGold;
        this.t_see_count = t_see_count;
        this.t_onLine = t_onLine;
        this.isFollow = isFollow;
    }
}
