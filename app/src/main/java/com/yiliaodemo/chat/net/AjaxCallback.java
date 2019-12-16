package com.yiliaodemo.chat.net;


import com.alibaba.fastjson.JSON;
import com.yiliaodemo.chat.util.LogUtil;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：返回数据解析封装
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class AjaxCallback<T> extends Callback<T> {

    private Type[] types;

    public AjaxCallback() {
        types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        if (response.isSuccessful() && types != null && types.length > 0) {
            String str = response.body().string();
            try {
                return JSON.parseObject(str, types[0]);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.i("==--", "数据解析失败- " + response.request().url()
                        + "  message:  " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        LogUtil.i("请求数据失败==--", call.request().url().toString() + "   Exception:  "
                + e.getMessage());
    }

}
