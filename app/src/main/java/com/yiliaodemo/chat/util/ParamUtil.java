package com.yiliaodemo.chat.util;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：通用工具
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ParamUtil {

    /**
     * 获取加密后的参数
     */
    public static String getParam(Map<String, String> paramMap) {
        try {
            String json = JSON.toJSONString(paramMap);
            return RUtil.publicEncrypt(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取加密后的参数
     */
    public static String getParam(Map<String, Object> paramMap, boolean res) {
        try {
            String json = JSON.toJSONString(paramMap);
            return RUtil.publicEncrypt(json);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("res==: " + res);
        }
        return "";
    }

}
