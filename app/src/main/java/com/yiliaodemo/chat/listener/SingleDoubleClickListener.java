package com.yiliaodemo.chat.listener;


import android.os.Handler;

import android.view.MotionEvent;

import android.view.View;

/**
 * 创建者    yf
 * <p>
 * 创建时间  2018/8/28 17:12
 * <p>
 * 描述       ${TODO}
 */

public class SingleDoubleClickListener implements View.OnTouchListener {

    //双击间四百毫秒延时

    private static int timeout = 400;

    //记录连续点击次数

    private int clickCount = 0;

    private Handler handler;

    private MyClickCallBack myClickCallBack;

    public interface MyClickCallBack {

        void oneClick();//点击一次的回调

        void doubleClick();//连续点击两次的回调

    }

    public SingleDoubleClickListener(MyClickCallBack myClickCallBack) {

        this.myClickCallBack = myClickCallBack;

        handler = new Handler();

    }

    @Override

    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            clickCount++;

            handler.postDelayed(new Runnable() {

                @Override

                public void run() {

                    if (clickCount == 1) {

                        myClickCallBack.oneClick();

                    } else if (clickCount == 2) {

                        myClickCallBack.doubleClick();

                    }

                    handler.removeCallbacksAndMessages(null);

                    //清空handler延时，并防内存泄漏

//计数清零

                    clickCount = 0;

                }

                //延时timeout后执行run方法中的代码

            }, timeout);

        }

        //让点击事件继续传播，方便再给View添加其他事件监听

        return true;

    }

}
