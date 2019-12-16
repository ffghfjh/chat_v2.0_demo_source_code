package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料页下方主播视频/照片bean
 * 作者：
 * 创建时间：2018/7/6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoBean extends BaseBean {

    public VideoBean(String url){
        this.t_addres_url = url;
    }

    public int t_is_private;//是否私密：0.否1.是
    public String t_handImg;//头像地址
    public int t_money;//	金币(可能为空)
    public String t_addres_url;//	图片或者视频地址
    public String t_video_img;//视频地址封面
    public String t_nickName;//		昵称
    public String t_title;//	标题
    public int t_file_type;//	文件类型 0.图片 1.视频
    public int t_id;//	数据编号
    public int is_see;//是否查看过：0.未查看1.已查看
    public int t_user_id;//	视频发布人编号
    public int t_auditing_type = 3;//	文件状态 0.审核中1.已通过2.审核失败 (私密图片存在)

    public VideoBean(){

    }
    public VideoBean(int t_is_private, String t_handImg, int t_money, String t_addres_url, String t_video_img, String t_nickName, String t_title, int t_file_type, int t_id, int is_see, int t_user_id, int t_auditing_type) {
        this.t_is_private = t_is_private;
        this.t_handImg = t_handImg;
        this.t_money = t_money;
        this.t_addres_url = t_addres_url;
        this.t_video_img = t_video_img;
        this.t_nickName = t_nickName;
        this.t_title = t_title;
        this.t_file_type = t_file_type;
        this.t_id = t_id;
        this.is_see = is_see;
        this.t_user_id = t_user_id;
        this.t_auditing_type = t_auditing_type;
    }
    public VideoBean(int t_is_private, String t_handImg, int t_money, String t_addres_url, String t_video_img, String t_nickName, String t_title, int t_file_type, int t_id, int is_see, int t_user_id) {
        this.t_is_private = t_is_private;
        this.t_handImg = t_handImg;
        this.t_money = t_money;
        this.t_addres_url = t_addres_url;
        this.t_video_img = t_video_img;
        this.t_nickName = t_nickName;
        this.t_title = t_title;
        this.t_file_type = t_file_type;
        this.t_id = t_id;
        this.is_see = is_see;
        this.t_user_id = t_user_id;
    }
}
