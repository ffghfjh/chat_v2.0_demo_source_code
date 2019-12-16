package com.yiliaodemo.chat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class ModifyTwoActivity extends BaseActivity {

    @BindView(R.id.input_et)
    EditText mInputEt;

    private final int NICK = 0;
    private final int WE_CHAT = 1;

    private int mFromType;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_modify_two_layout);
    }

    @Override
    protected void onContentAdded() {
        mFromType = getIntent().getIntExtra(Constant.MODIFY_TWO, NICK);
        initText();
    }

    /**
     * 设置文字
     */
    private void initText() {
        if (mFromType == NICK) {
            setTitle(R.string.nick);
            mInputEt.setHint(R.string.please_input_nick_des);
        } else if (mFromType == WE_CHAT) {
            setTitle(R.string.we_chat_number);
            mInputEt.setHint(R.string.please_input_we_chat);
        } else {
            setTitle(R.string.sign);
            mInputEt.setHint(R.string.please_input_sign_one);
        }

        String content = getIntent().getStringExtra(Constant.MODIFY_CONTENT);
        if (!TextUtils.isEmpty(content)) {
            mInputEt.setText(content);
        }
    }

    @OnClick({R.id.finish_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish_tv: {
                finishBack();
                break;
            }
        }
    }

    /**
     * 返回
     */
    private void finishBack() {
        String input = mInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            if (mFromType == NICK) {
                ToastUtil.showToast(getApplicationContext(), R.string.please_input_nick_des);
            } else if (mFromType == WE_CHAT) {
                ToastUtil.showToast(getApplicationContext(), R.string.please_input_we_chat);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.please_input_sign_one);
            }
            return;
        }
        if (mFromType == NICK) {//如果是昵称,检查重复
            if (input.length() > 10) {
                ToastUtil.showToast(getApplicationContext(), R.string.nick_length);
                return;
            }
            checkNickName();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(Constant.MODIFY_CONTENT, input);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 验证昵称
     */
    private void checkNickName() {
        final String nick = mInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(nick)) {
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("nickName", nick);
        OkHttpUtils.post().url(ChatApi.GET_NICK_REPEAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Boolean>>() {
            @Override
            public void onResponse(BaseResponse<Boolean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Boolean result = response.m_object;
                    if (!result) {
                        ToastUtil.showToast(getApplicationContext(), R.string.nick_repeat);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(Constant.MODIFY_CONTENT, nick);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.MODIFY_CONTENT, nick);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                Intent intent = new Intent();
                intent.putExtra(Constant.MODIFY_CONTENT, nick);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
