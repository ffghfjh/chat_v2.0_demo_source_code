package com.yiliaodemo.chat.socket.domain;

import java.io.Serializable;

public class Mid implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer mid;

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    /**
     * 发送模拟消息
     */
    public static final int SEND_VIRTUAL_MESSAGE = 30003;
    /**
     * 通知连线
     */
    public static final int CHAT_LINK = 30004;

    /**
     * 对方已挂断
     */
    public static final int HAVE_HANG_UP = 30005;

    /**
     * 被封号
     */
    public static final int BEAN_SUSPEND = 30006;

    /**
     * 用户收到主播邀请
     */
    public static final int USER_GET_INVITE = 30008;

    /**
     * 用户余额不足一分钟
     */
    public static final int MONEY_NOT_ENOUGH = 30010;

    /**
     * 动态新评论
     */
    public static final int ACTIVE_NEW_COMMENT = 30009;

    /**
     * 主播开启速配的时候,下发提示消息,只提示主播
     */
    public static final int QUICK_START_HINT_ANCHOR = 30013;

    /**
     * 接通视频的时候,下发提示消息,主播用户都要提示
     */
    public static final int VIDEO_CHAT_START_HINT = 30012;

    /**
     * 大房间人数变化
     */
    public static final int BIG_ROOM_COUNT_CHANGE = 30014;

}
