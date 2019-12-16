package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料页大房间bean
 * 作者：
 * 创建时间：2018/7/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoRoomBean extends BaseBean {

    public int t_is_debut;//0.未开播 1.直播中
    public long t_room_id;// 房间号
    public long t_chat_room_id;//聊天室编号

    public InfoRoomBean(int t_is_debut, long t_room_id, long t_chat_room_id) {
        this.t_is_debut = t_is_debut;
        this.t_room_id = t_room_id;
        this.t_chat_room_id = t_chat_room_id;
    }
    public InfoRoomBean(){

    }

    public int getT_is_debut() {
        return t_is_debut;
    }

    public void setT_is_debut(int t_is_debut) {
        this.t_is_debut = t_is_debut;
    }

    public long getT_room_id() {
        return t_room_id;
    }

    public void setT_room_id(long t_room_id) {
        this.t_room_id = t_room_id;
    }

    public long getT_chat_room_id() {
        return t_chat_room_id;
    }

    public void setT_chat_room_id(long t_chat_room_id) {
        this.t_chat_room_id = t_chat_room_id;
    }
}
