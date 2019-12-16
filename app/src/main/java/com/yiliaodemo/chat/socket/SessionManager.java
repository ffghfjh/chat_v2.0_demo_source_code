package com.yiliaodemo.chat.socket;

import org.apache.mina.core.session.IoSession;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：ession管理类,通过ioSession与服务器通信
 * 作者：lyf
 * 创建时间：2017/5/20.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SessionManager {

    private static SessionManager mInstance = null;
    private IoSession ioSession;//最终与服务器 通信的对象

    static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager() {

    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    /**
     * 将对象写到服务器
     */
    public void writeToServer(Object msg) {
        if (ioSession != null) {
            ioSession.write(msg);
        }
    }

    /**
     * 关闭连接
     */
    public void closeSession() {
        if (ioSession != null) {
            ioSession.closeOnFlush();
        }
    }

    public void removeSession() {
        ioSession = null;
    }


}
