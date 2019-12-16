package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：新评论消息bean
 * 作者：
 * 创建时间：2019/1/17
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CommentMessageBean extends BaseBean {

    public int t_id;//	用户编号
    public String t_nickName;//	用户昵称
    public String t_handImg;//	用户头像
    public String t_comment;//	评论内容
    public long t_create_time;//	评论时间
    public int t_dynamic_id;//	动态编号
    public int dynamic_type;//	动态类型: -1.文本动态 0.图片 1.视频动态
    public String dynamic_com;//动态内容
    public String t_cover_img_url;//视频动态封面图片

}
