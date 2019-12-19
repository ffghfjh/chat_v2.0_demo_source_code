package com.yiliaodemo.chat.fragment.near;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.ChargeActivity;
import com.yiliaodemo.chat.activity.ChatActivity;
import com.yiliaodemo.chat.activity.VideoChatOneActivity;
import com.yiliaodemo.chat.base.BaseFragment;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.InfoBean;
import com.yiliaodemo.chat.bean.MapBean;
import com.yiliaodemo.chat.bean.VideoSignBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.glide.GlideCircleTransform;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.listener.OnMapSuccessListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.LogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：地图Fragment
 * 作者：
 * 创建时间：2018/11/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MapFragment extends BaseFragment implements AMapLocationListener, AMap.OnCameraChangeListener {

    private MapView mMapView = null;
    private AMap mAMap;
    private AMapLocationClient mLocationClient;

    @Override
    protected int initLayout() {
        return R.layout.fragment_map_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        initMap();
    }

    /**
     * 初始化map
     */
    private void initMap() {
        if (mAMap == null) {
            // 显示地图
            mAMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        myLocationStyle.radiusFillColor(mContext.getResources().getColor(R.color.transparent));
        myLocationStyle.strokeColor(mContext.getResources().getColor(R.color.transparent));
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.getUiSettings().setMyLocationButtonEnabled(true); //显示默认的定位按钮
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.setOnCameraChangeListener(this);

        mLocationClient = new AMapLocationClient(mContext.getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    protected void onFirstVisible() {
    }

    /**
     * 获取附近的用户列表
     */
    private void getNearbyList(String lat, String lng) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("lat", lat);
        paramMap.put("lng", lng);
        OkHttpUtils.post().url(ChatApi.GET_NEAR_BY_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<MapBean>>() {
            @Override
            public void onResponse(BaseListResponse<MapBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<MapBean> mapBeans = response.m_object;
                    if (mapBeans != null) {
                        LogUtil.i("返回的number: " + mapBeans.size());
                        List<MapBean> loadBeans;
                        if (mapBeans.size() > 100) {
                            loadBeans = mapBeans.subList(0, 100);
                        } else {
                            loadBeans = mapBeans;
                        }
                        dealMap(loadBeans);
                    }
                }
            }
        });
    }

    /**
     * 处理地图
     */
    private void dealMap(final List<MapBean> mapBeans) {
        if (mAMap != null) {
            //清除
            mAMap.clear();

            MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
            myLocationStyle.radiusFillColor(mContext.getResources().getColor(R.color.transparent));
            myLocationStyle.strokeColor(mContext.getResources().getColor(R.color.transparent));
            mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

            //添加
            if (mapBeans.size() > 0) {
                for (final MapBean mapBean : mapBeans) {
                    if (mapBean.t_lat > 0 && mapBean.t_lng > 0) {
                        final MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(new LatLng(mapBean.t_lat, mapBean.t_lng));
                        markerOption.draggable(false);//设置Marker可拖动
                        markerOption.title(String.valueOf(mapBean.t_user_id));//标识ID
                        dealMarker(mapBean, false, new OnMapSuccessListener() {
                            @Override
                            public void onResourceReady(View view) {
                                markerOption.icon(BitmapDescriptorFactory.fromView(view));
                                mAMap.addMarker(markerOption);
                            }

                            @Override
                            public void onLocalReady(View view) {
                                markerOption.icon(BitmapDescriptorFactory.fromView(view));
                                mAMap.addMarker(markerOption);
                            }
                        });
                    }
                }
            }
            mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    if (marker != null) {
                        String title = marker.getTitle();
                        if (!TextUtils.isEmpty(title)) {
                            LogUtil.i("点击marker: " + title);
                            marker.setClickable(false);
                            MapBean bean = null;
                            for (MapBean mapBean : mapBeans) {
                                if (title.equals(String.valueOf(mapBean.t_user_id))) {
                                    bean = mapBean;
                                }
                            }
                            if (bean != null) {
                                dealMarker(bean, true, new OnMapSuccessListener() {
                                    @Override
                                    public void onResourceReady(View view) {
                                        marker.setIcon(BitmapDescriptorFactory.fromView(view));
                                    }

                                    @Override
                                    public void onLocalReady(View view) {
                                        marker.setIcon(BitmapDescriptorFactory.fromView(view));
                                    }
                                });
                            }
                            showInfoDialog(title, marker, bean);
                        }
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 处理marker
     */
    private void dealMarker(MapBean mapBean, boolean select, final OnMapSuccessListener onMapSuccessListener) {
        @SuppressLint("InflateParams") final View view = LayoutInflater.from(mContext).inflate(R.layout.item_map_marker_layout, null);
        //背景
        ImageView content_iv = view.findViewById(R.id.content_iv);
        //头像
        final ImageView head_iv = view.findViewById(R.id.head_iv);
        //背景
        if (mapBean.t_onLine == 0) {//0.在线 1.离线
            if (select) {
                content_iv.setBackgroundResource(R.drawable.map_head_online_select);
            } else {
                content_iv.setBackgroundResource(R.drawable.map_head_online_unselect);
            }
        } else if (mapBean.t_onLine == 1) {
            if (select) {
                content_iv.setBackgroundResource(R.drawable.map_head_offline_select);
            } else {
                content_iv.setBackgroundResource(R.drawable.map_head_offline_unselect);
            }
        }
        //头像
        if (!TextUtils.isEmpty(mapBean.t_handImg)) {
            int width = DevicesUtil.dp2px(mContext, 36);
            int height = DevicesUtil.dp2px(mContext, 36);
            Glide.with(mContext).load(mapBean.t_handImg).asBitmap()
                    .transform(new GlideCircleTransform(mContext)).override(width, height)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (resource != null) {
                                if (onMapSuccessListener != null) {
                                    head_iv.setImageBitmap(resource);
                                    onMapSuccessListener.onResourceReady(view);
                                }
                            }
                        }
                    });
        } else {
            if (onMapSuccessListener != null) {
                head_iv.setImageResource(R.drawable.default_head_img);
                onMapSuccessListener.onLocalReady(view);
            }
        }
    }

    /**
     * 显示信息dialog
     */
    private void showInfoDialog(String userId, final Marker marker, final MapBean mapBean) {
        if (mContext != null) {
            final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle);
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_map_info_layout, null);
            setInfoDialogView(view, userId, mDialog, marker);
            mDialog.setContentView(view);
            Point outSize = new Point();
            mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = outSize.x;
                window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
                window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
            }
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (marker != null) {
                        marker.setClickable(true);
                        dealMarker(mapBean, false, new OnMapSuccessListener() {
                            @Override
                            public void onResourceReady(View view) {
                                marker.setIcon(BitmapDescriptorFactory.fromView(view));
                            }

                            @Override
                            public void onLocalReady(View view) {
                                marker.setIcon(BitmapDescriptorFactory.fromView(view));
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 礼物dialog view 初始化
     */
    private void setInfoDialogView(View view, String userId, Dialog dialog, Marker marker) {
        String lat = SharedPreferenceHelper.getCodeLat(getContext());
        String lng = SharedPreferenceHelper.getCodeLng(getContext());
        getUserData(userId, String.valueOf(lat), String.valueOf(lng), view, dialog, marker);
    }

    /**
     * 获取用户信息
     */
    private void getUserData(String userId, String lat, String lng, final View view, final Dialog dialog,
                             final Marker marker) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("coverSeeUserId", userId);
        paramMap.put("lat", lat);
        paramMap.put("lng", lng);
        OkHttpUtils.post().url(ChatApi.GET_USER_DETA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<InfoBean>>() {
            @Override
            public void onResponse(BaseResponse<InfoBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    InfoBean infoBean = response.m_object;
                    if (infoBean != null) {
                        setInfo(view, infoBean, dialog);
                    } else {
                        marker.setClickable(true);
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                } else {
                    marker.setClickable(true);
                    ToastUtil.showToast(mContext, R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                marker.setClickable(true);
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }

    /**
     * 设置信息
     */
    private void setInfo(View view, final InfoBean infoBean, final Dialog dialog) {
        //头像
        ImageView head_iv = view.findViewById(R.id.head_iv);
        //状态
        FrameLayout state_fl = view.findViewById(R.id.state_fl);
        TextView state_tv = view.findViewById(R.id.state_tv);
        //距离
        TextView distance_tv = view.findViewById(R.id.distance_tv);
        //昵称
        TextView nick_tv = view.findViewById(R.id.nick_tv);
        //认证
        ImageView verify_iv = view.findViewById(R.id.verify_iv);
        //性别
        ImageView sex_iv = view.findViewById(R.id.sex_iv);
        //年龄
        TextView age_tv = view.findViewById(R.id.age_tv);
        //职业
        TextView job_tv = view.findViewById(R.id.job_tv);
        //平台id
        TextView id_tv = view.findViewById(R.id.id_tv);
        //签名
        TextView sign_tv = view.findViewById(R.id.sign_tv);
        //关注
        final TextView focus_tv = view.findViewById(R.id.focus_tv);
        final View focus_v = view.findViewById(R.id.focus_v);
        //主页
        TextView home_tv = view.findViewById(R.id.home_tv);
        //私信
        TextView message_tv = view.findViewById(R.id.message_tv);
        //视频
        TextView video_tv = view.findViewById(R.id.video_tv);
        //覆盖
        View cover_v = view.findViewById(R.id.cover_v);
        View bottom_v = view.findViewById(R.id.bottom_v);

        //绑定数据
        //头像
        final String headImg = infoBean.t_handImg;
        if (!TextUtils.isEmpty(headImg)) {
            int width = DevicesUtil.dp2px(mContext, 45);
            int height = DevicesUtil.dp2px(mContext, 45);
            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, head_iv, width, height);
        } else {
            head_iv.setImageResource(R.drawable.default_head_img);
        }
        //昵称
        final String nick = infoBean.t_nickName;
        nick_tv.setText(nick);
        //认证 0.普通用户 1.主播
        final int role = infoBean.t_role;
        if (role == 1) {
            verify_iv.setVisibility(View.VISIBLE);
        }
        if (infoBean.t_sex == 0) {//女
            sex_iv.setImageResource(R.drawable.female_white);
        } else {
            sex_iv.setImageResource(R.drawable.male_white);
        }
        //年龄
        age_tv.setText(String.valueOf(infoBean.t_age));
        //职业
        job_tv.setText(infoBean.t_vocation);
        //ID号
        if (infoBean.t_idcard > 0) {
            String content = mContext.getResources().getString(R.string.id_card) + infoBean.t_idcard;
            id_tv.setText(content);
        }
        //签名
        if (!TextUtils.isEmpty(infoBean.t_autograph)) {
            sign_tv.setText(infoBean.t_autograph);
        } else {
            sign_tv.setText(mContext.getResources().getString(R.string.lazy));
        }
        //关注  0.未关注 1.已关注
        if (infoBean.isFollow == 1) {
            focus_tv.setVisibility(View.GONE);
            focus_v.setVisibility(View.GONE);
        } else {
            focus_tv.setVisibility(View.VISIBLE);
            focus_v.setVisibility(View.VISIBLE);
        }
        //状态 0.在线1.离线
        int state = infoBean.t_onLine;
        if (state == 0) {
            state_fl.setBackgroundResource(R.drawable.info_state_online);
            state_tv.setText(mContext.getResources().getString(R.string.free));
            state_fl.setVisibility(View.VISIBLE);
        } else if (state == 1) {
            state_fl.setBackgroundResource(R.drawable.info_state_offline);
            state_tv.setText(mContext.getResources().getString(R.string.offline));
            state_fl.setVisibility(View.VISIBLE);
        } else {
            state_fl.setVisibility(View.GONE);
        }
        //距离
        String distanceStr;
        BigDecimal b = new BigDecimal(infoBean.distance);
        double d = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (d <= 0.01) {
            distanceStr = mContext.getResources().getString(R.string.distance) + 0.01 + mContext.getResources().getString(R.string.distance_one);
        } else {
            distanceStr = mContext.getResources().getString(R.string.distance) + d + mContext.getResources().getString(R.string.distance_one);
        }
        distance_tv.setText(distanceStr);
        //点击事件
        //关注
        focus_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFollow(infoBean.t_id, focus_tv, focus_v);
            }
        });
        //主页
        home_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int actorId = infoBean.t_id;
                if (actorId > 0) {
                    Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                    intent.putExtra(Constant.ACTOR_ID, actorId);
                    mContext.startActivity(intent);
                }
            }
        });
        //私信
        message_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = infoBean.t_id;
                if (userId > 0) {
                    String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).headUrl;
                    Intent intent = new Intent(mContext.getApplicationContext(), ChatActivity.class);
                    intent.putExtra(Constant.TITLE, nick);
                    intent.putExtra(Constant.ACTOR_ID, infoBean.t_id);
                    intent.putExtra(Constant.USER_HEAD_URL, headImg);
                    intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                    intent.putExtra(Constant.MINE_ID, mContext.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });
        //视频
        video_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userId = infoBean.t_id;
                if (userId > 0) {
                    //0.普通用户 1.主播
                    getSign(infoBean, role == 1);
                }
            }
        });
        cover_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bottom_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!mContext.isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final InfoBean bean, final boolean isUserCallActor) {
        String userId;
        String actorId;
        if (isUserCallActor) {
            userId = mContext.getUserId();
            actorId = String.valueOf(bean.t_id);
        } else {
            userId = String.valueOf(bean.t_id);
            actorId = mContext.getUserId();
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("anthorId", actorId);
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        int onlineState = signBean.onlineState;
                        if (onlineState == 1 && mContext.getUserRole() == 0) {//1.余额刚刚住够
                            showGoldJustEnoughDialog(mRoomId, isUserCallActor, bean);
                        } else {
                            if (isUserCallActor) {//是用户call主播
                                userRequestChat(mRoomId, bean);
                            } else {//主播call用户
                                requestChat(mRoomId, bean);
                            }
                        }
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mContext.showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mContext.dismissLoadingDialog();
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }

    /**
     * 显示金币刚好够dialog
     */
    private void showGoldJustEnoughDialog(int mRoomId, boolean isUserCallActor, InfoBean bean) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_one_minute_layout, null);
        setGoldDialogView(view, mDialog, mRoomId, isUserCallActor, bean);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setGoldDialogView(View view, final Dialog mDialog, final int mRoomId,
                                   final boolean isUserCallActor, final InfoBean bean) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanRoom();
                mDialog.dismiss();
            }
        });
        //是 发起聊天
        TextView yes_tv = view.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserCallActor) {//是用户call主播
                    userRequestChat(mRoomId, bean);
                } else {//主播call用户
                    requestChat(mRoomId, bean);
                }
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空房间
                cleanRoom();
                Intent intent = new Intent(getContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 清空房间
     */
    private void cleanRoom() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.USER_HANG_UP_LINK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("清空房间成功");
                }
            }
        });
    }

    /**
     * 主播对用户发起聊天
     */
    private void requestChat(final int roomId, final InfoBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anchorUserId", mContext.getUserId());
        paramMap.put("userId", String.valueOf(bean.t_id));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.ACTOR_LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(mContext, VideoChatOneActivity.class);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR_INVITE);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.NICK_NAME, bean.t_nickName);
                        intent.putExtra(Constant.USER_HEAD_URL, bean.t_handImg);
                        mContext.startActivity(intent);
                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {//对方设置了勿扰
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(mContext);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mContext.dismissLoadingDialog();
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

    /**
     * 用户对主播发起聊天
     */
    private void userRequestChat(final int roomId, final InfoBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("coverLinkUserId", String.valueOf(bean.t_id));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(mContext, VideoChatOneActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext, message);
                        } else {
                            ToastUtil.showToast(mContext, R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(mContext);
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mContext.dismissLoadingDialog();
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

    /**
     * 添加关注
     */
    private void saveFollow(int userId, final TextView focus_tv, final View focus_v) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());//关注人
        paramMap.put("coverFollowUserId", String.valueOf(userId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(mContext.getApplicationContext(), message);
                        focus_tv.setVisibility(View.GONE);
                        focus_v.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext.getApplicationContext(), R.string.system_error);
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && mAMap != null) {
            if (aMapLocation.getErrorCode() == 0) {//成功
                // 设置缩放级别
                double mSelfLat = aMapLocation.getLatitude();
                double mSelfLng = aMapLocation.getLongitude();
                if (mSelfLat > 0 && mSelfLng > 0) {
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(mAMap.getMaxZoomLevel() - 4));
                    // 将地图移动到定位点
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(
                            new LatLng(mSelfLat, mSelfLng)));
                }
            } else {
                LogUtil.i("Map: 定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (cameraPosition != null) {
            LatLng latLng = cameraPosition.target;
            if (latLng != null) {
                double lat = latLng.latitude;
                double lng = latLng.longitude;
                if (lat > 0 && lng > 0) {
                    LogUtil.i("滑动完成: " + cameraPosition.target);
                    getNearbyList(String.valueOf(lat), String.valueOf(lng));
                }
            }
        }
    }


}
