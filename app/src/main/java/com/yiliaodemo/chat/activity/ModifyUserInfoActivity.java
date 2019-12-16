package com.yiliaodemo.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.adapter.CityPickerRecyclerAdapter;
import com.yiliaodemo.chat.adapter.CoverRecyclerAdapter;
import com.yiliaodemo.chat.adapter.LabelRecyclerAdapter;
import com.yiliaodemo.chat.adapter.SetChargeRecyclerAdapter;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.CoverUrlBean;
import com.yiliaodemo.chat.bean.LabelBean;
import com.yiliaodemo.chat.bean.PersonBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ImageHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.layoutmanager.PickerLayoutManager;
import com.yiliaodemo.chat.listener.OnDownLoadSuccessListener;
import com.yiliaodemo.chat.listener.OnFileUploadListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.oss.QServiceCfg;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.FileUtil;
import com.yiliaodemo.chat.util.JsonUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：编辑修改个人资料
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ModifyUserInfoActivity extends BaseActivity {

    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.job_tv)
    TextView mJobTv;
    @BindView(R.id.mobile_tv)
    TextView mMobileTv;
    @BindView(R.id.we_chat_tv)
    TextView mWeChatTv;
    @BindView(R.id.high_tv)
    TextView mHighTv;
    @BindView(R.id.age_tv)
    TextView mAgeTv;
    @BindView(R.id.body_tv)
    TextView mBodyTv;
    @BindView(R.id.star_tv)
    TextView mStarTv;
    @BindView(R.id.city_tv)
    TextView mCityTv;
    @BindView(R.id.tag_tv)
    TextView mTagTv;
    @BindView(R.id.sign_tv)
    TextView mSignTv;
    @BindView(R.id.submit_tv)
    TextView mSubmitTv;//确认修改
    @BindView(R.id.evidence_rv)
    RecyclerView mEvidenceRv;//封面Ll
    @BindView(R.id.upload_iv)
    ImageView mUploadIv;
    @BindView(R.id.tags_ll)
    LinearLayout mTagLl;
    @BindView(R.id.head_img_iv)
    ImageView mHeadImgIv;
    @BindView(R.id.scrollView)
    LinearLayout mScrollView;

    //标签列表
    private List<LabelBean> mLabelBeans = new ArrayList<>();

    //腾讯云
    private QServiceCfg mQServiceCfg;

    //封面文件path的list,可以用于上传腾讯云
    private List<String> mCoverLocalPaths = new ArrayList<>();
    private String mCoverImageHttpUrl = "";//拼接封面url
    private String mFirstCoverImageHttpUrl = "";//设为封面url
    private String mHeadImageHttpUrl = "";//头像url
    //头像本地路径
    private String mHeadImageLocalPath = "";
    //标签列表
    private List<LabelBean> mSelectedLabels = new ArrayList<>();
    private CoverRecyclerAdapter mCoverAdapter;
    private GridLayoutManager mGridLayoutManager;
    //option选中的
    private String mOptionSelectStr = "";
    private String mOptionSelectCity = "";
    private String mOptionSelectSecondCity = "";
    private List<String> mOptionSelectCityList = new ArrayList<>();

    //验证昵称
    private Handler mHandler = new Handler();
    private boolean mNickReapt = false;
    //修改手机号
    private final int SET_PHONE_NUMBER = 0x08;
    private String mRequestPhone = "";//接口返回的phone
    //修改其他
    private final int SET_NICK = 0x09;//昵称
    private final int SET_WE_CHAT = 0x10;//微信
    private final int SET_SIGN = 0x11;//个性签名

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_modify_user_info_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.edit_verify_info);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
        setListener();
        controlKeyboardLayout();
        getLabelList();
        getUserInfo();
    }

    private void setListener() {
        mSignTv.addTextChangedListener(new MyTextWatcher());
        mNickTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    mHandler.removeCallbacks(delayRun);
                }
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                if (mNickTv.hasFocus()) {
                    mHandler.postDelayed(delayRun, 1000);
                }
            }
        });

        mCoverAdapter = new CoverRecyclerAdapter(this);
        mGridLayoutManager = new GridLayoutManager(this, 1);
        mEvidenceRv.setLayoutManager(mGridLayoutManager);
        mEvidenceRv.setAdapter(mCoverAdapter);
        mCoverAdapter.setOnItemClickListener(new CoverRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showSetCoverDialog(position);
            }
        });
        mCoverAdapter.setOnDataChangeListener(new CoverRecyclerAdapter.OnDataChangeListener() {
            @Override
            public void onDataChange() {
                if (checkInput()) {
                    mSubmitTv.setBackgroundResource(R.drawable.shape_submit_pink);
                } else {
                    mSubmitTv.setBackgroundResource(R.drawable.shape_submit_gray);
                }
            }
        });

        int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 40)) / 4;
        int height = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 40)) / 4;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(width, height);
        params1.leftMargin = DevicesUtil.dp2px(mContext, 5);
        params1.rightMargin = DevicesUtil.dp2px(mContext, 5);
        mUploadIv.setLayoutParams(params1);
    }

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            checkNickName();
        }
    };

    /**
     * 验证昵称
     */
    private void checkNickName() {
        String nick = mNickTv.getText().toString().trim();
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
                        mNickReapt = true;
                        ToastUtil.showToast(getApplicationContext(), R.string.nick_repeat);
                    } else {
                        mNickReapt = false;
                    }
                }
            }
        });
    }

    @OnClick({R.id.submit_tv, R.id.tag_rl, R.id.upload_iv, R.id.job_ll, R.id.age_rl, R.id.high_rl,
            R.id.body_rl, R.id.star_rl, R.id.city_rl, R.id.head_ll, R.id.phone_rl, R.id.nick_rl,
            R.id.we_chat_rl, R.id.sign_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_tv: {//确认修改
                if (!submitCheckInput()) {
                    return;
                }
                //循环上传图片
                showLoadingDialog();
                if (mCoverLocalPaths != null && mCoverLocalPaths.size() > 0) {
                    try {
                        uploadCoverFileWithQQ(0, new OnFileUploadListener() {
                            @Override
                            public void onFileUploadSuccess() {
                                uploadInfo();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!TextUtils.isEmpty(mHeadImageLocalPath)) {
                    uploadHeadFileWithQQ(new OnFileUploadListener() {
                        @Override
                        public void onFileUploadSuccess() {
                            uploadInfo();
                        }
                    });
                } else {
                    uploadInfo();
                }
                break;
            }
            case R.id.job_ll: {//职业
                showOptionDialog(JOB);
                break;
            }
            case R.id.age_rl: {//年龄
                showOptionDialog(AGE);
                break;
            }
            case R.id.high_rl: {//身高
                showOptionDialog(HIGH);
                break;
            }
            case R.id.body_rl: {//体重
                showOptionDialog(BODY);
                break;
            }
            case R.id.star_rl: {//星座
                showOptionDialog(STAR);
                break;
            }
            case R.id.city_rl: {//城市
                showCityPickerDialog();
                break;
            }
            case R.id.tag_rl: {//标签
                showLabelListDialog();
                break;
            }
            case R.id.upload_iv: {//上传封面
                //判断是否多于4张
                if (mEvidenceRv.getChildCount() >= 4 || mCoverLocalPaths.size() >= 4) {
                    ToastUtil.showToast(getApplicationContext(), R.string.four_most);
                    return;
                }
                //图片选择
                ImageHelper.openPictureChoosePage(ModifyUserInfoActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            }
            case R.id.head_ll: {//头像
                //图片选择
                ImageHelper.openPictureChoosePage(ModifyUserInfoActivity.this, Constant.REQUEST_CODE_CHOOSE_HEAD_IMG);
                break;
            }
            case R.id.phone_rl: {
                Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);
                startActivityForResult(intent, SET_PHONE_NUMBER);
                break;
            }
            case R.id.nick_rl: {//昵称
                String content = mNickTv.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(), ModifyTwoActivity.class);
                intent.putExtra(Constant.MODIFY_TWO, 0);//昵称
                intent.putExtra(Constant.MODIFY_CONTENT, content);//昵称
                startActivityForResult(intent, SET_NICK);
                break;
            }
            case R.id.we_chat_rl: {//微信
                String content = mWeChatTv.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(), ModifyTwoActivity.class);
                intent.putExtra(Constant.MODIFY_TWO, 1);//微信
                intent.putExtra(Constant.MODIFY_CONTENT, content);//微信
                startActivityForResult(intent, SET_WE_CHAT);
                break;
            }
            case R.id.sign_rl: {//个性签名
                String content = mSignTv.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(), ModifyTwoActivity.class);
                intent.putExtra(Constant.MODIFY_TWO, 2);//个性签名
                intent.putExtra(Constant.MODIFY_CONTENT, content);//个性签名
                startActivityForResult(intent, SET_SIGN);
                break;
            }
        }
    }

    /**
     * 显示城市选择dialog
     */
    private void showCityPickerDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_city_picker_layout, null);
        setCityPickerDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置city picker dialog view
     */
    private void setCityPickerDialogView(View view, final Dialog mDialog) {
        //获取city数据
        final List<String> cityNameBeans = new ArrayList<>();
        final List<List<String>> secondNameBeans = new ArrayList<>();
        String cityStr = JsonUtil.getJson(getBaseContext(), "city.json");
        JSONObject jsonObject = JSON.parseObject(cityStr);
        JSONArray provinces = jsonObject.getJSONArray("provinces");
        for (int i = 0; i < provinces.size(); i++) {
            JSONObject object = provinces.getJSONObject(i);
            String cityName = object.getString("name");
            cityNameBeans.add(cityName);

            JSONArray jsonArray = object.getJSONArray("citys");
            List<String> tempList = new ArrayList<>();
            for (int j = 0; j < jsonArray.size(); j++) {
                String secondName = (String) jsonArray.get(j);
                tempList.add(secondName);
            }
            secondNameBeans.add(tempList);
        }

        //左边
        CityPickerRecyclerAdapter leftAdapter = new CityPickerRecyclerAdapter(this);
        RecyclerView left_rv = view.findViewById(R.id.left_rv);
        PickerLayoutManager leftManager = new PickerLayoutManager(getApplicationContext(),
                left_rv, PickerLayoutManager.VERTICAL, false, 5, 0.3f, true);
        left_rv.setLayoutManager(leftManager);
        left_rv.setAdapter(leftAdapter);
        leftAdapter.loadData(cityNameBeans);

        //右边
        final CityPickerRecyclerAdapter rightAdapter = new CityPickerRecyclerAdapter(this);
        RecyclerView right_rv = view.findViewById(R.id.right_rv);
        PickerLayoutManager rightManager = new PickerLayoutManager(getApplicationContext(),
                right_rv, PickerLayoutManager.VERTICAL, false, 5, 0.3f, true);
        right_rv.setLayoutManager(rightManager);
        right_rv.setAdapter(rightAdapter);
        rightAdapter.loadData(secondNameBeans.get(0));
        mOptionSelectCityList = secondNameBeans.get(0);
        leftManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                mOptionSelectCity = cityNameBeans.get(position);
                mOptionSelectCityList = secondNameBeans.get(position);
                rightAdapter.loadData(mOptionSelectCityList);
            }
        });
        rightManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                mOptionSelectSecondCity = mOptionSelectCityList.get(position);
            }
        });

        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mOptionSelectCity)) {
                    mOptionSelectCity = getResources().getString(R.string.bei_jing_des);
                }
                if (TextUtils.isEmpty(mOptionSelectSecondCity)) {
                    if (mOptionSelectCityList != null && mOptionSelectCityList.size() > 0) {
                        mOptionSelectSecondCity = mOptionSelectCityList.get(0);
                    } else {
                        mOptionSelectSecondCity = getResources().getString(R.string.dong_cheng);
                    }
                }

                String str = mOptionSelectCity + mOptionSelectSecondCity;
                mCityTv.setText(str);
                mOptionSelectCity = "";
                mOptionSelectSecondCity = "";
                mDialog.dismiss();
                if (checkInput()) {
                    mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_pink);
                } else {
                    mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_gray);
                }
            }
        });

    }

    /**
     * 显示收费标准dialog
     */
    private final int JOB = 0;
    private final int AGE = 1;
    private final int STAR = 2;
    private final int HIGH = 3;
    private final int BODY = 4;

    private void showOptionDialog(int index) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog, index);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置 dialog view
     */
    private void setDialogView(View view, final Dialog mDialog, final int index) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView title_tv = view.findViewById(R.id.title_tv);

        final List<String> beans = new ArrayList<>();
        switch (index) {
            case JOB: {
                title_tv.setText(R.string.job);
                beans.add("网红");
                beans.add("模特");
                beans.add("白领");
                beans.add("护士");
                beans.add("空姐");
                beans.add("学生");
                beans.add("健身教练");
                beans.add("医生");
                beans.add("客服");
                beans.add("其他");
                break;
            }
            case AGE: {
                title_tv.setText(R.string.age_title);
                for (int i = 18; i < 100; i++) {
                    beans.add(String.valueOf(i));
                }
                break;
            }
            case STAR: {
                title_tv.setText(R.string.star);
                beans.add("白羊座");
                beans.add("金牛座");
                beans.add("双子座");
                beans.add("巨蟹座");
                beans.add("狮子座");
                beans.add("处女座");
                beans.add("天秤座");
                beans.add("天蝎座");
                beans.add("射手座");
                beans.add("魔羯座");
                beans.add("水瓶座");
                beans.add("双鱼座");
                break;
            }
            case HIGH: {
                title_tv.setText(R.string.high_title_des);
                for (int i = 160; i < 200; i++) {
                    beans.add(String.valueOf(i));
                }
                break;
            }
            case BODY: {
                title_tv.setText(R.string.body_title_des);
                for (int i = 30; i < 81; i++) {
                    beans.add(String.valueOf(i));
                }
                break;
            }
        }

        SetChargeRecyclerAdapter adapter = new SetChargeRecyclerAdapter(this);
        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(getApplicationContext(),
                content_rv, PickerLayoutManager.VERTICAL, false, 5, 0.3f, true);
        content_rv.setLayoutManager(pickerLayoutManager);
        content_rv.setAdapter(adapter);
        adapter.loadData(beans);
        pickerLayoutManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                LogUtil.i("位置: " + position);
                mOptionSelectStr = beans.get(position);
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (index) {
                    case JOB: {
                        if (TextUtils.isEmpty(mOptionSelectStr)) {
                            mJobTv.setText(R.string.net_hot);
                        } else {
                            mJobTv.setText(mOptionSelectStr);
                        }
                        mOptionSelectStr = "";
                        break;
                    }
                    case AGE: {
                        if (TextUtils.isEmpty(mOptionSelectStr)) {
                            mAgeTv.setText(R.string.eighteen);
                        } else {
                            mAgeTv.setText(mOptionSelectStr);
                        }
                        mOptionSelectStr = "";
                        break;
                    }
                    case STAR: {
                        if (TextUtils.isEmpty(mOptionSelectStr)) {
                            mStarTv.setText(R.string.sheep);
                        } else {
                            mStarTv.setText(mOptionSelectStr);
                        }
                        mOptionSelectStr = "";
                        break;
                    }
                    case HIGH: {
                        if (TextUtils.isEmpty(mOptionSelectStr)) {
                            mHighTv.setText(R.string.one_hundred_and_sixty);
                        } else {
                            mHighTv.setText(mOptionSelectStr);
                        }
                        mOptionSelectStr = "";
                        break;
                    }
                    case BODY: {
                        if (TextUtils.isEmpty(mOptionSelectStr)) {
                            mBodyTv.setText(R.string.four);
                        } else {
                            mBodyTv.setText(mOptionSelectStr);
                        }
                        mOptionSelectStr = "";
                        break;
                    }
                }
                mDialog.dismiss();
                if (checkInput()) {
                    mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_pink);
                } else {
                    mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_gray);
                }
            }
        });
    }

    /**
     * 获取个人信息
     */
    private void getUserInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_PERSONAL_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PersonBean<LabelBean, CoverUrlBean>>>() {
            @Override
            public void onResponse(BaseResponse<PersonBean<LabelBean, CoverUrlBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PersonBean<LabelBean, CoverUrlBean> personBean = response.m_object;
                    if (personBean != null) {
                        //昵称
                        String nick = personBean.t_nickName;
                        if (!TextUtils.isEmpty(nick)) {
                            mNickTv.setText(nick);
                        }
                        //职业
                        String job = personBean.t_vocation;
                        if (!TextUtils.isEmpty(job)) {
                            mJobTv.setText(job);
                        }
                        //手机号
                        mRequestPhone = personBean.t_phone;
                        if (!TextUtils.isEmpty(mRequestPhone)) {
                            mMobileTv.setText(mRequestPhone);
                        }
                        //微信号
                        String weChat = personBean.t_weixin;
                        if (!TextUtils.isEmpty(weChat)) {
                            mWeChatTv.setText(weChat);
                        }
                        //身高
                        int high = personBean.t_height;
                        if (high > 0) {
                            mHighTv.setText(String.valueOf(high));
                        }
                        //年龄
                        int age = personBean.t_age;
                        if (age > 0) {
                            mAgeTv.setText(String.valueOf(age));
                        }
                        //体重
                        double weight = personBean.t_weight;
                        if (weight > 0) {
                            mBodyTv.setText(String.valueOf(weight));
                        }
                        //星座
                        String star = personBean.t_constellation;
                        if (!TextUtils.isEmpty(star)) {
                            mStarTv.setText(star);
                        }
                        //城市
                        String city = personBean.t_city;
                        if (!TextUtils.isEmpty(city)) {
                            mCityTv.setText(city);
                        }
                        //个性签名
                        String sign = personBean.t_autograph;
                        if (!TextUtils.isEmpty(sign)) {
                            mSignTv.setText(sign);
                        }
                        //形象标签
                        mSelectedLabels = personBean.lable;
                        setLabelView(mSelectedLabels);
                        //头像
                        mHeadImageHttpUrl = personBean.t_handImg;
                        if (!TextUtils.isEmpty(mHeadImageHttpUrl)) {
                            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, mHeadImageHttpUrl, mHeadImgIv);
                        }
                        //封面图
                        List<CoverUrlBean> coverUrlBeanList = personBean.coverList;
                        if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                            try {
                                downLoadCoverImage(coverUrlBeanList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //数据加载完成
                        if (checkInput()) {
                            mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_pink);
                        } else {
                            mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_gray);
                        }
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }

        });
    }

    /**
     * 下载封面图
     */
    private void downLoadCoverImage(List<CoverUrlBean> coverUrlBeanList) {
        //排序
        List<String> urlPaths = new ArrayList<>();
        for (CoverUrlBean bean : coverUrlBeanList) {
            if (bean.t_first == 0) {//是主封面,那么放在第一位
                urlPaths.add(0, bean.t_img_url);
            } else {
                urlPaths.add(bean.t_img_url);
            }
        }
        //下载图片到本地
        if (urlPaths.size() > 0) {
            downloadWithQQ(urlPaths, 0, new OnDownLoadSuccessListener() {
                @Override
                public void onFileDownLoadSuccess() {
                    getWindow().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCoverLocalPaths != null && mCoverLocalPaths.size() > 0) {
                                mGridLayoutManager.setSpanCount(mCoverLocalPaths.size());
                                if (mCoverLocalPaths.size() >= 4) {
                                    mUploadIv.setVisibility(View.GONE);
                                }
                            }
                            mCoverAdapter.loadData(mCoverLocalPaths);
                            mEvidenceRv.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }
    }

    /**
     * 使用腾讯云下载
     */
    private void downloadWithQQ(final List<String> urlPaths, final int index, final OnDownLoadSuccessListener listener) {
        //目录路径
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        final String dirPath = Constant.COVER_AFTER_SHEAR_DIR;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
        String url = urlPaths.get(index);
        String fileName = url.substring(url.length() - 17);
        //如果本地存在
        final String fileLocalPath = dirPath + fileName;
        File localFile = new File(fileLocalPath);
        if (localFile.exists()) {
            localFile.delete();
        }

        String cosPath = "/cover/" + fileName;
        GetObjectRequest getObjectRequest = new GetObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, dirPath);
        getObjectRequest.setSign(600, null, null);
        //**使用异步回调请求**
        mQServiceCfg.getCosCxmService().getObjectAsync(getObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                mCoverLocalPaths.add(fileLocalPath);
                if (index + 1 < urlPaths.size()) {//如果还有下一张
                    downloadWithQQ(urlPaths, index + 1, listener);
                } else {
                    if (listener != null) {
                        listener.onFileDownLoadSuccess();
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
            }
        });
    }

    /**
     * 上传资料
     */
    private void uploadInfo() {
        //昵称
        String nick = mNickTv.getText().toString().trim();
        //职业
        String job = mJobTv.getText().toString().trim();
        //手机号
        String phone = mMobileTv.getText().toString().trim();
        if (!TextUtils.isEmpty(mRequestPhone) && phone.equals(mRequestPhone)) {//如果号码没变,传空
            phone = "";
        }
        //微信号
        String weChat = mWeChatTv.getText().toString().trim();
        //身高
        String high = mHighTv.getText().toString().trim();
        //年龄
        String age = mAgeTv.getText().toString().trim();
        //体重
        String body = mBodyTv.getText().toString().trim();
        //星座
        String star = mStarTv.getText().toString().trim();
        //城市
        String city = mCityTv.getText().toString().trim();
        //形象标签
        StringBuilder labels = new StringBuilder();
        String finalLabels = "";
        if (mSelectedLabels != null && mSelectedLabels.size() > 0) {
            for (LabelBean bean : mSelectedLabels) {
                labels.append(bean.t_id + ",");
            }
            finalLabels = labels.toString();
            if (!TextUtils.isEmpty(finalLabels) &&
                    (finalLabels.equals("0,") || finalLabels.equals("0,0,"))) {
                finalLabels = "";
            }
        }
        //个性签名
        String sign = mSignTv.getText().toString().trim();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_nickName", nick);
        paramMap.put("t_phone", phone);
        paramMap.put("t_height", high);
        paramMap.put("t_weight", body);
        paramMap.put("t_constellation", star);
        paramMap.put("t_city", city);
        paramMap.put("t_autograph", sign);
        paramMap.put("t_vocation", job);
        paramMap.put("coverImg", TextUtils.isEmpty(mCoverImageHttpUrl) ? "" : mCoverImageHttpUrl);//封面图
        paramMap.put("t_age", age);
        paramMap.put("t_weixin", weChat);
        paramMap.put("t_handImg", TextUtils.isEmpty(mHeadImageHttpUrl) ? "" : mHeadImageHttpUrl);//头像图
        paramMap.put("lables", finalLabels);//头像图
        paramMap.put("t_first", TextUtils.isEmpty(mFirstCoverImageHttpUrl) ? "" : mFirstCoverImageHttpUrl);//设为首页封面http url
        OkHttpUtils.post().url(ChatApi.UPDATE_PERSON_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                mCoverImageHttpUrl = "";
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                            ToastUtil.showToast(getApplicationContext(), message);
                            finish();
                        }
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.edit_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.edit_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mCoverImageHttpUrl = "";
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.edit_fail);
            }

        });
    }

    /**
     * 获取标签列表
     */
    private void getLabelList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_LABEL_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<LabelBean>>() {
            @Override
            public void onResponse(BaseListResponse<LabelBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<LabelBean> beans = response.m_object;
                    if (beans != null && beans.size() > 0) {
                        mLabelBeans = beans;
                    }
                }
            }
        });
    }

    /**
     * 设置标签View
     */
    private void setLabelView(List<LabelBean> labelBeans) {
        //形象标签
        mTagLl.removeAllViews();
        int[] backs = {R.drawable.tag_1, R.drawable.tag_2, R.drawable.tag_3, R.drawable.tag_4, R.drawable.tag_5,
                R.drawable.tag_6, R.drawable.tag_7, R.drawable.tag_8, R.drawable.tag_9, R.drawable.tag_10, R.drawable.tag_11,
                R.drawable.tag_12};
        if (labelBeans != null && labelBeans.size() > 0) {
            for (int i = 0; i < labelBeans.size(); i++) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tag_grid_layout, null);
                TextView textView = view.findViewById(R.id.content_tv);
                textView.setText(labelBeans.get(i).t_label_name);
                Random random = new Random();
                int index = random.nextInt(backs.length);
                textView.setBackgroundResource(backs[index]);
                if (i != 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 20;
                    textView.setLayoutParams(params);
                }
                mTagLl.addView(textView);
            }
            if (mTagLl.getChildCount() > 0) {
                mTagLl.setVisibility(View.VISIBLE);
                mTagTv.setVisibility(View.GONE);
            } else {
                mTagTv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 显示标签列表dialog
     */
    private void showLabelListDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_label_list_layout, null);
        setLabelListView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置label list view
     */
    private void setLabelListView(View view, final Dialog mDialog) {
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 4);
        content_rv.setLayoutManager(gridLayoutManager);
        final LabelRecyclerAdapter adapter = new LabelRecyclerAdapter(getBaseContext());
        content_rv.setAdapter(adapter);
        if (mLabelBeans != null && mLabelBeans.size() > 0) {
            //随机选两个
            Random random = new Random();
            for (LabelBean bean : mLabelBeans) {
                bean.selected = false;
            }
            int r = random.nextInt(mLabelBeans.size());
            int r1 = random.nextInt(mLabelBeans.size());
            mLabelBeans.get(r).selected = true;
            if (r != r1) {
                mLabelBeans.get(r1).selected = true;
            } else {
                if (r1 + 1 == mLabelBeans.size()) {
                    mLabelBeans.get(r1 - 1).selected = true;
                } else {
                    mLabelBeans.get(r1 + 1).selected = true;
                }
            }
            adapter.loadData(mLabelBeans);
        }
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<LabelBean> beans = adapter.getSelectedLabels();
                if (beans != null) {
                    if (beans.size() == 0) {
                        ToastUtil.showToast(getApplicationContext(), R.string.tags_not_select);
                    } else if (beans.size() <= 2) {
                        mSelectedLabels = beans;
                        setLabelView(beans);
                        mDialog.dismiss();
                        if (checkInput()) {
                            mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_pink);
                        } else {
                            mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_gray);
                        }
                    }
                }

            }
        });
    }

    /**
     * 点击提交的时候的提示
     */
    private boolean submitCheckInput() {
        //昵称
        String nick = mNickTv.getText().toString().trim();
        if (TextUtils.isEmpty(nick) || mNickReapt) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_nick_des);
            return false;
        }
        //形象标签
        if (mSelectedLabels == null || mSelectedLabels.size() <= 0) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_tag_des);
            return false;
        }
        //如果是主播,那么必须上传封面
        ChatUserInfo chatUserInfo = AppManager.getInstance().getUserInfo();
        if (chatUserInfo == null) {
            chatUserInfo = SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        }
        if (chatUserInfo.t_role == 1) {//主播
            if (mCoverLocalPaths == null || mCoverLocalPaths.size() <= 0) {
                ToastUtil.showToast(getApplicationContext(), R.string.actor_at_least_upload_one);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否都填了
     */
    private boolean checkInput() {
        //昵称
        String nick = mNickTv.getText().toString().trim();
        if (TextUtils.isEmpty(nick) || mNickReapt) {
            return false;
        }
        //职业
        String job = mJobTv.getText().toString().trim();
        if (TextUtils.isEmpty(job)) {
            return false;
        }
        //身高
        String high = mHighTv.getText().toString().trim();
        if (TextUtils.isEmpty(high)) {
            return false;
        }
        //年龄
        String age = mAgeTv.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            return false;
        }
        //体重
        String body = mBodyTv.getText().toString().trim();
        if (TextUtils.isEmpty(body)) {
            return false;
        }
        //星座
        String star = mStarTv.getText().toString().trim();
        if (TextUtils.isEmpty(star)) {
            return false;
        }
        //城市
        String city = mCityTv.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            return false;
        }
        //形象标签
        if (mSelectedLabels == null || mSelectedLabels.size() <= 0) {
            return false;
        }
        //如果是主播,那么必须上传封面
        ChatUserInfo chatUserInfo = AppManager.getInstance().getUserInfo();
        if (chatUserInfo == null) {
            chatUserInfo = SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        }
        if (chatUserInfo.t_role == 1) {//主播
            return mCoverLocalPaths != null && mCoverLocalPaths.size() > 0;
        }
        return true;
    }

    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s) && s.length() > 0 && checkInput()) {
                mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_pink);
            } else {
                mSubmitTv.setBackgroundResource(R.drawable.shape_submit_button_gray);
            }
        }
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadCoverFileWithQQ(final int index, final OnFileUploadListener listener) {
        String filePath = mCoverLocalPaths.get(index);
        File file = new File(filePath);
        if (!file.exists()) {
            mCoverLocalPaths.remove(filePath);
            uploadCoverFileWithQQ(index, listener);
            return;
        }
        //文件名
        String fileName;
        if (filePath.length() < 50) {
            fileName = filePath.substring(filePath.length() - 17);
        } else {
            String last = filePath.substring(filePath.length() - 4);
            if (last.contains("png")) {
                fileName = System.currentTimeMillis() + ".png";
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
        }

        String cosPath = "/cover/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, filePath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }
                if (index == 0) {
                    mFirstCoverImageHttpUrl = resultUrl;
                }
                mCoverImageHttpUrl += resultUrl + ",";
                if (mCoverLocalPaths != null) {
                    if (mCoverLocalPaths.size() > index + 1) {//如果还有下一张,就继续上传
                        uploadCoverFileWithQQ(index + 1, listener);
                    } else {//没有下一张了就上传头像
                        uploadHeadFileWithQQ(listener);
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
            }
        });
    }

    /**
     * 上传头像
     */
    private void uploadHeadFileWithQQ(final OnFileUploadListener listener) {
        if (TextUtils.isEmpty(mHeadImageLocalPath)) {//如果头像为空
            listener.onFileUploadSuccess();
            return;
        }
        String fileName = mHeadImageLocalPath.substring(mHeadImageLocalPath.length() - 17);
        String cosPath = "/head/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, mHeadImageLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success 头像=  " + result.accessUrl);
                mHeadImageHttpUrl = result.accessUrl;
                if (!mHeadImageHttpUrl.contains("http") || !mHeadImageHttpUrl.contains("https")) {
                    mHeadImageHttpUrl = "https://" + mHeadImageHttpUrl;
                }
                if (listener != null) {
                    listener.onFileUploadSuccess();
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == Constant.REQUEST_CODE_CHOOSE || requestCode == Constant.REQUEST_CODE_CHOOSE_HEAD_IMG)
                && resultCode == RESULT_OK) {//图库选择
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            if (mSelectedUris != null && mSelectedUris.size() > 0) {
                try {
                    Uri uri = mSelectedUris.get(0);
                    String filePath = FileUtil.getPathAbove19(this, uri);
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (!file.exists()) {
                            LogUtil.i("文件不存在 ");
                        } else {
                            LogUtil.i("文件大小: " + file.length() / 1024);
                        }
                        //直接裁剪
                        if (requestCode == Constant.REQUEST_CODE_CHOOSE) {
                            cutWithUCrop(filePath, true);
                        } else {
                            cutWithUCrop(filePath, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == RESULT_OK
                && (requestCode == Constant.UCROP_REQUEST_CODE_COVER || requestCode == Constant.UCROP_REQUEST_CODE_HEAD)) {
            Uri resultUri = UCrop.getOutput(data);
            String filePath = FileUtil.getPathAbove19(this, resultUri);
            if (requestCode == Constant.UCROP_REQUEST_CODE_COVER) {
                setThumbImage(filePath);
            } else {
                mHeadImageLocalPath = filePath;
                int width = DevicesUtil.dp2px(getApplicationContext(), 54);
                int height = DevicesUtil.dp2px(getApplicationContext(), 54);
                ImageLoadHelper.glideShowCircleImageWithUri(this, resultUri, mHeadImgIv,
                        width, height);
            }
        } else if (requestCode == SET_PHONE_NUMBER && resultCode == RESULT_OK) {//修改手机号码
            if (data != null) {
                String phone = data.getStringExtra(Constant.PHONE_MODIFY);
                if (!TextUtils.isEmpty(phone)) {
                    mMobileTv.setText(phone);
                }
            }
        } else if (requestCode == SET_NICK && resultCode == RESULT_OK) {//修改昵称
            if (data != null) {
                String phone = data.getStringExtra(Constant.MODIFY_CONTENT);
                if (!TextUtils.isEmpty(phone)) {
                    mNickTv.setText(phone);
                }
            }
        } else if (requestCode == SET_WE_CHAT && resultCode == RESULT_OK) {//修改微信号
            if (data != null) {
                String phone = data.getStringExtra(Constant.MODIFY_CONTENT);
                if (!TextUtils.isEmpty(phone)) {
                    mWeChatTv.setText(phone);
                }
            }
        } else if (requestCode == SET_SIGN && resultCode == RESULT_OK) {//修改个性签名
            if (data != null) {
                String phone = data.getStringExtra(Constant.MODIFY_CONTENT);
                if (!TextUtils.isEmpty(phone)) {
                    mSignTv.setText(phone);
                }
            }
        }

    }

    /**
     * 显示缩略图到封面
     */
    private void setThumbImage(String filePath) {
        mCoverLocalPaths.add(filePath);
        if (mCoverLocalPaths != null && mCoverLocalPaths.size() > 0) {
            mGridLayoutManager.setSpanCount(mCoverLocalPaths.size());
            if (mCoverLocalPaths.size() >= 4) {
                mUploadIv.setVisibility(View.GONE);
            }
        }
        mCoverAdapter.loadData(mCoverLocalPaths);
        mEvidenceRv.setVisibility(View.VISIBLE);
    }

    /**
     * 显示设为封面dialog
     */
    private void showSetCoverDialog(int position) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_cover_layout, null);
        setCoverDialogView(view, mDialog, position);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置view
     */
    private void setCoverDialogView(View view, final Dialog mDialog, final int position) {
        //删除
        TextView delete_tv = view.findViewById(R.id.delete_tv);
        delete_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCoverLocalPaths != null && mCoverLocalPaths.size() > position) {
                    String filePath = mCoverLocalPaths.get(position);
                    File file = new File(filePath);
                    boolean result = file.delete();
                    if (result) {//本地删除后
                        mCoverLocalPaths.remove(position);
                        if (mCoverLocalPaths.size() > 0) {
                            mGridLayoutManager.setSpanCount(mCoverLocalPaths.size());
                        }
                        mCoverAdapter.loadData(mCoverLocalPaths);
                        if (mCoverLocalPaths.size() < 4) {
                            mUploadIv.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.delete_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.delete_fail);
                }
                mDialog.dismiss();
            }
        });
        //设为封面
        TextView set_tv = view.findViewById(R.id.set_tv);
        set_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCoverLocalPaths != null && mCoverLocalPaths.size() > position && position != 0) {
                    String filePath = mCoverLocalPaths.get(position);
                    mCoverLocalPaths.remove(position);
                    mCoverLocalPaths.add(0, filePath);
                    mCoverAdapter.loadData(mCoverLocalPaths);
                }
                mDialog.dismiss();
            }
        });
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 使用u crop裁剪
     */
    private void cutWithUCrop(String sourceFilePath, boolean fromCover) {
        //计算 图片resize的大小
        int overWidth;
        int overHeight;
        if (fromCover) {
            overWidth = DevicesUtil.getScreenW(mContext);
            overHeight = DevicesUtil.dp2px(mContext, 435);
        } else {
            overWidth = DevicesUtil.getScreenW(getApplicationContext());
            overHeight = DevicesUtil.getScreenW(getApplicationContext());
        }
        //目录路径
        String dirPath;
        if (fromCover) {
            dirPath = Constant.COVER_AFTER_SHEAR_DIR;
        } else {
            dirPath = Constant.HEAD_AFTER_SHEAR_DIR;
        }
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
        if (!fromCover) {
            FileUtil.deleteFiles(dirPath);
        }
        //文件名
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".png";
        //请求码
        int requestCode;
        if (fromCover) {
            requestCode = Constant.UCROP_REQUEST_CODE_COVER;
        } else {
            requestCode = Constant.UCROP_REQUEST_CODE_HEAD;
        }
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }
        UCrop.of(Uri.fromFile(new File(sourceFilePath)), Uri.fromFile(new File(filePath)))
                .withAspectRatio(overWidth, overHeight)
                .withMaxResultSize(overWidth, overHeight)
                .start(this, requestCode);
    }

    /**
     * 键盘
     */
    private void controlKeyboardLayout() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mScrollView.getWindowVisibleDisplayFrame(r);
                //r.top 是状态栏高度
                int screenHeight = mScrollView.getRootView().getHeight();
                int softHeight = screenHeight - r.bottom;
                if (softHeight > 200) {//当输入法高度大于100判定为输入法打开了
                    mScrollView.scrollTo(0, 150);
                } else {//否则判断为输入法隐藏了
                    mScrollView.scrollTo(0, 0);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除cover目录中的图片
        FileUtil.deleteFiles(Constant.COVER_AFTER_SHEAR_DIR);
        FileUtil.deleteFiles(Constant.HEAD_AFTER_SHEAR_DIR);
    }

}