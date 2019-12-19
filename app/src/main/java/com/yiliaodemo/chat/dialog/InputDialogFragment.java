package com.yiliaodemo.chat.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yiliaodemo.chat.R;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间输入文字Dialog
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InputDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_input_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText input_et = view.findViewById(R.id.input_et);
        TextView btn_send = view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTextSendListener != null) {
                    String inputText = input_et.getText().toString().trim();
                    mOnTextSendListener.onTextSend(inputText);
                }
            }
        });
        input_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mOnTextSendListener != null) {
                        String inputText = input_et.getText().toString().trim();
                        mOnTextSendListener.onTextSend(inputText);
                    }
                    return true;
                }
                return false;
            }
        });
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (input_et.requestFocus()) {
                    if (getContext() != null) {
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(input_et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
            }
        }, 200);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            DisplayMetrics dm = new DisplayMetrics();
            if (getActivity() != null) {
                WindowManager windowManager = getActivity().getWindowManager();
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.BOTTOM;
                    // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    window.setAttributes(params);
                }
            }
        }
    }

    /**
     * 发送回调
     */
    public interface OnTextSendListener {
        void onTextSend(String text);
    }

    private OnTextSendListener mOnTextSendListener;

    public void setOnTextSendListener(OnTextSendListener onTextSendListener) {
        mOnTextSendListener = onTextSendListener;
    }

}
