package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：个人资料页下方主播视频/照片bean
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AlbumBean extends BaseBean {

    public int t_id;//文件编号
    public int t_auditing_type;//审核状态 0.未审核1.已审核2.审核失败
    public int t_money;//收费数
    public String t_addres_url;//文件地址
    public String t_video_img;//视频封面
    public int t_file_type;//	文件类型 0.图片  1.视频

}
