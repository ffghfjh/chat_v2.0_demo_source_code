package com.yiliaodemo.chat.bean;

import com.yiliaodemo.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：标签bean
 * 作者：
 * 创建时间：2018/7/2
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LabelBean extends BaseBean {

    public int t_id;
    public int t_user_lable_id;//用户标签ID
    public String t_label_name;
    public boolean selected = false;

    public LabelBean(){

    }
    public LabelBean(int t_id, int t_user_lable_id, String t_label_name, boolean selected) {
        this.t_id = t_id;
        this.t_user_lable_id = t_user_lable_id;
        this.t_label_name = t_label_name;
        this.selected = selected;
    }

    public int getT_id() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id = t_id;
    }

    public int getT_user_lable_id() {
        return t_user_lable_id;
    }

    public void setT_user_lable_id(int t_user_lable_id) {
        this.t_user_lable_id = t_user_lable_id;
    }

    public String getT_label_name() {
        return t_label_name;
    }

    public void setT_label_name(String t_label_name) {
        this.t_label_name = t_label_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
