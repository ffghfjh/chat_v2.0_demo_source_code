package com.yiliaodemo.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.videoplayer.player.VideoView;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.activity.ActorInfoOneActivity;
import com.yiliaodemo.chat.activity.BigHouseActivity;
import com.yiliaodemo.chat.activity.ChargeActivity;
import com.yiliaodemo.chat.activity.ReportActivity;
import com.yiliaodemo.chat.activity.VideoChatOneActivity;
import com.yiliaodemo.chat.activity.VipCenterActivity;
import com.yiliaodemo.chat.base.AppManager;
import com.yiliaodemo.chat.base.BaseListResponse;
import com.yiliaodemo.chat.base.BaseResponse;
import com.yiliaodemo.chat.bean.ActorPlayBean;
import com.yiliaodemo.chat.bean.BalanceBean;
import com.yiliaodemo.chat.bean.ChatUserInfo;
import com.yiliaodemo.chat.bean.GiftBean;
import com.yiliaodemo.chat.bean.GoldBean;
import com.yiliaodemo.chat.bean.InfoRoomBean;
import com.yiliaodemo.chat.bean.LabelBean;
import com.yiliaodemo.chat.bean.VideoBean;
import com.yiliaodemo.chat.bean.VideoSignBean;
import com.yiliaodemo.chat.constant.ChatApi;
import com.yiliaodemo.chat.constant.Constant;
import com.yiliaodemo.chat.helper.ChargeHelper;
import com.yiliaodemo.chat.helper.ImageLoadHelper;
import com.yiliaodemo.chat.helper.SharedPreferenceHelper;
import com.yiliaodemo.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliaodemo.chat.listener.OnViewPagerListener;
import com.yiliaodemo.chat.listener.SingleDoubleClickListener;
import com.yiliaodemo.chat.net.AjaxCallback;
import com.yiliaodemo.chat.net.NetCode;
import com.yiliaodemo.chat.util.DevicesUtil;
import com.yiliaodemo.chat.util.DialogUtil;
import com.yiliaodemo.chat.util.ParamUtil;
import com.yiliaodemo.chat.util.ToastUtil;
import com.yiliaodemo.chat.widget.controller.TikTokController;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Request;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private Activity activity;
    private ArrayList<VideoBean> mDatas;
    private ActorPlayBean mActorPlayBean;
    private boolean mToReport = false;
    public int mActorId;//主播id
    //加载dialog
    private Dialog mDialogLoading;
    //礼物相关
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private int mMyGoldNumber;
    //大房间直播
    private InfoRoomBean mInfoRoomBean;

    public VideoAdapter(Context context, FragmentActivity activity, ArrayList<VideoBean> datas) {
        mContext = context;
        mDatas = datas;
        this.activity = activity;
        getGiftList();//获取礼物相关
        mDialogLoading = DialogUtil.showLoadingDialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_actor_video_play_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         VideoBean videoBean = mDatas.get(position);

//         holder.videoView.setUrl(mDatas.get(position).t_addres_url);
//         TikTokController controller = new TikTokController(mContext);
//         holder.videoView.setVideoController(controller); //设置控制器
//         holder.videoView.setLooping(true);
//        holder.videoView.setScreenScale(IjkVideoView.SCREEN_SCALE_CENTER_CROP);
//        Glide.with(mContext).load(mDatas.get(position).t_handImg)
//                .bitmapTransform(new RoundedCornersTransformation(mContext,100,0,RoundedCornersTransformation.CornerType.ALL))
//                .into(holder.mSmallHeadIv);
//        holder.mNameTv.setText(videoBean.t_nickName);
//        holder.mTitleTv.setText(videoBean.t_title);
        getActorInfo(videoBean.t_id,0,videoBean.t_user_id,holder,position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ImageView mComplainIv;
        ImageView mCoverIv;
        ImageView mSmallHeadIv;
        TextView mNameTv;
        TextView mWeChatTv;
        View mWeChatFl;
        TextView mVideoChatTv;
        View mRightLl;
        View mLeftRl;
        TextView mTitleTv;
        TextView mLoveTv;
        TextView mSeeTv;
        TextView mGoldPriceTv;
        TextView mStatusFreeTv;
        TextView mStatusOfflineTv;
        TextView mStatusBusyTv;
        View mFocusFl;
        ImageView giftIv;
        ImageView stopBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
            videoView.setAlpha(1);
            //mComplainIv.setOnClickListener(VideoAdapter.this);
            mSmallHeadIv = itemView.findViewById(R.id.small_head_iv);
            mSmallHeadIv.setOnClickListener(VideoAdapter.this);
            mWeChatTv = itemView.findViewById(R.id.we_chat_tv);
            mWeChatFl = itemView.findViewById(R.id.we_chat_fl);
            mVideoChatTv = itemView.findViewById(R.id.video_chat_tv);
            mRightLl = itemView.findViewById(R.id.right_ll);
            mLeftRl = itemView.findViewById(R.id.left_rl);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mLoveTv = itemView.findViewById(R.id.love_tv);
            mSeeTv = itemView.findViewById(R.id.see_tv);
            mGoldPriceTv = itemView.findViewById(R.id.gold_price_tv);
            mStatusFreeTv = itemView.findViewById(R.id.status_free_tv);
            mStatusOfflineTv = itemView.findViewById(R.id.status_offline_tv);
            mStatusBusyTv = itemView.findViewById(R.id.status_busy_tv);
            mFocusFl = itemView.findViewById(R.id.focus_fl);
            mNameTv = itemView.findViewById(R.id.name_tv);
            giftIv = itemView.findViewById(R.id.gift_iv);
            giftIv.setOnClickListener(VideoAdapter.this);
            stopBtn = itemView.findViewById(R.id.paly_stop);
        }
    }
    /**
     * 获取礼物列表
     */
    private void getGiftList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_GIFT_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<GiftBean> giftBeans = response.m_object;
                    if (giftBeans != null && giftBeans.size() > 0) {
                        mGiftBeans = giftBeans;
                    }
                }
            }
        });
    }

    /**
     * 获取主播数据
     */
    private void getActorInfo(int fileId, int queryType, final int authorId, final ViewHolder holder, int position) {
        //holder.stopBtn.setVisibility(View.GONE);
        holder.videoView.setUrl(mDatas.get(position).t_addres_url);
        holder.videoView.setLooping(true);
        TikTokController controller = new TikTokController(mContext);
        holder.videoView.setVideoController(controller); //设置控制器
        //holder.videoView.start(); //开始播放，不调用则不自动播放
        Log.d("palyVideo","播放视频2");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(authorId));
        paramMap.put("albumId", fileId > 0 ? String.valueOf(fileId) : "");
        paramMap.put("queryType", String.valueOf(queryType));
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_PLAY_PAGE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorPlayBean<LabelBean, InfoRoomBean>>>() {
            @Override
            public void onResponse(BaseResponse<ActorPlayBean<LabelBean, InfoRoomBean>> response, int id) {
                Log.d("getVideos","获取主播信息响应成功");
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ActorPlayBean<LabelBean, InfoRoomBean> playBean = response.m_object;
                    if (playBean != null) {
                        mActorPlayBean = playBean;
                        //处理头像
                        String handImg = playBean.t_handImg;
                        Log.d("getVideos","头像:"+handImg);
                        if (!TextUtils.isEmpty(handImg)) {
                            //计算头像resize
                            int smallOverWidth = DevicesUtil.dp2px(mContext, 42);
                            int smallOverHeight = DevicesUtil.dp2px(mContext, 42);
                            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, holder.mSmallHeadIv,
                                    smallOverWidth, smallOverHeight);
                        }
                        //昵称
                        String nick = playBean.t_nickName;
                        Log.d("getVideos","昵称:"+nick);
                        if (!TextUtils.isEmpty(nick)) {
                            holder.mNameTv.setText(nick);
                        }
                        //标题
                        String title = playBean.t_title;
                        if (!TextUtils.isEmpty(title)) {
                            holder.mTitleTv.setText(title);
                        }
                        //点赞总数
                        int laudtotal = playBean.laudtotal;
                        holder.mLoveTv.setText(String.valueOf(laudtotal));
                        //当前用户是否给查看人点赞 0:未点赞 1.已点赞
                        int isLaud = playBean.isLaud;
                        if (isLaud == 0) {
                            holder.mLoveTv.setSelected(false);
                        } else {
                            holder.mLoveTv.setSelected(true);
                        }
                        //查看次数
                        holder.mSeeTv.setText(String.valueOf(playBean.t_see_count));
                        //视频聊天金币
                        int videoGold = playBean.videoGold;
                        if (videoGold > 0) {
                            String content = videoGold + mContext.getResources().getString(R.string.price);
                            holder.mGoldPriceTv.setText(content);
                        }
                        //是否关注  0:未关注 1：已关注
                        int isFollow = playBean.isFollow;
                        if (isFollow == 0) {
                            holder.mFocusFl.setVisibility(View.VISIBLE);
                        } else {
                            holder.mFocusFl.setVisibility(View.INVISIBLE);
                        }
                        //状态 在线状态 0.空闲1.忙碌2.离线
                        int t_onLine = playBean.t_onLine;
                        if (t_onLine == 0) {
                            holder.mStatusFreeTv.setVisibility(View.VISIBLE);
                            holder.mStatusBusyTv.setVisibility(View.GONE);
                            holder.mStatusOfflineTv.setVisibility(View.GONE);
                        } else if (t_onLine == 1) {
                            holder.mStatusBusyTv.setVisibility(View.VISIBLE);
                            holder.mStatusFreeTv.setVisibility(View.GONE);
                            holder.mStatusOfflineTv.setVisibility(View.GONE);
                        } else if (t_onLine == 2) {
                            holder.mStatusOfflineTv.setVisibility(View.VISIBLE);
                            holder.mStatusBusyTv.setVisibility(View.GONE);
                            holder.mStatusFreeTv.setVisibility(View.GONE);
                        } else {
                            holder.mStatusFreeTv.setVisibility(View.GONE);
                            holder.mStatusBusyTv.setVisibility(View.GONE);
                            holder.mStatusOfflineTv.setVisibility(View.GONE);
                        }

                        //微信
                        int isSee = playBean.isSee;
                        String weChat = playBean.t_weixin;
                        if (!TextUtils.isEmpty(weChat)) {
                            if (isSee == 1) {//看过
                                String content = mContext.getResources().getString(R.string.we_chat_number_des) + weChat;
                                holder.mWeChatTv.setText(content);
                                holder.mWeChatTv.setVisibility(View.VISIBLE);
                                holder.mWeChatFl.setVisibility(View.GONE);
                            } else {
                                holder.mWeChatTv.setVisibility(View.GONE);
                                holder.mWeChatFl.setVisibility(View.VISIBLE);
                            }
                        } else {
                            holder.mWeChatTv.setVisibility(View.GONE);
                            holder.mWeChatFl.setVisibility(View.GONE);
                        }
//                        //播放视频,从首页进来
//                        if (mFromWhere == Constant.FROM_GIRL) {
//                            mCoverUrl = playBean.t_video_img;
//                            String mVideoUrl = playBean.t_addres_url;
//                            if (!TextUtils.isEmpty(mCoverUrl)) {
//                                loadCoverImage(mCoverUrl);
//                            }
//                            if (!TextUtils.isEmpty(mVideoUrl)) {
//                                playVideoWithUrl(mVideoUrl);
//                            }
//                        }
                        //处理大房间
                        mInfoRoomBean = playBean.bigRoomData;
                        if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                                && mInfoRoomBean.t_chat_room_id > 0) {
                            holder.mVideoChatTv.setText(mContext.getString(R.string.enter_house));
                        }
                        mActorId = authorId;
                        holder.mVideoChatTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                                        && mInfoRoomBean.t_chat_room_id > 0) {
                                    Intent intent = new Intent(mContext.getApplicationContext(), BigHouseActivity.class);
                                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                                    intent.putExtra(Constant.ACTOR_ID, authorId);
                                    intent.putExtra(Constant.ROOM_ID, mInfoRoomBean.t_room_id);
                                    intent.putExtra(Constant.CHAT_ROOM_ID, mInfoRoomBean.t_chat_room_id);
                                    mContext.startActivity(intent);
                                } else {
                                    if (getUserSex() == 0) {
                                        ToastUtil.showToast(mContext.getApplicationContext(), R.string.sex_can_not_communicate);
                                        return;
                                    }
                                    getSign();
                                }
                            }
                        });
                        holder.mFocusFl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getUserSex() == 0) {
                                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.sex_can_not_communicate);
                                    return;
                                }
                                if (mActorId > 0) {
                                    saveFollow(authorId,holder);
                                }
                            }
                        });
                        holder.mLoveTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mActorId > 0) {
                                    if (!holder.mLoveTv.isSelected()) {//没有点赞过
                                        addLike(holder);
                                    } else {
                                        cancelLike(holder);
                                    }
                                }
                            }
                        });
                        holder.mWeChatFl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getUserSex() == 0) {
                                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.sex_can_not_communicate);
                                    return;
                                }
                                if (mActorPlayBean != null && mActorPlayBean.isSee == 0) {
                                    showSeeWeChatRemindDialog(holder);
                                }
                            }
                        });
                        holder.videoView.setOnTouchListener(new SingleDoubleClickListener(new SingleDoubleClickListener.MyClickCallBack() {
                            @Override
                            public void oneClick() {
                                Log.d("getVideo","点击");
                                if(holder.videoView.isPlaying()){
                                    holder.videoView.pause();
                                    holder.stopBtn.setVisibility(View.VISIBLE);
                                }else {
                                    holder.stopBtn.setVisibility(View.GONE);
                                    holder.videoView.start();
                                }
                            }

                            @Override
                            public void doubleClick() {
                                if (mActorId > 0) {
                                    if (!holder.mLoveTv.isSelected()) {//没有点赞过
                                        addLike(holder);
                                    } else {
                                        cancelLike(holder);
                                    }
                                }
                            }
                        }));
                        holder.stopBtn.setVisibility(View.GONE);
                    }
                }else {
                    Log.d("getVideos","获取主播信息响应 null");
                }
            }
        });

    }

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anthorId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                //mContext.getApplicationContext().dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        int onlineState = signBean.onlineState;
                        if (onlineState == 1) {//1.余额刚刚住够
                            //showGoldJustEnoughDialog(mRoomId);
                        } else {
                            requestChat(mRoomId);
                        }
                    } else {
                        ToastUtil.showToast(mContext.getApplicationContext(), R.string.system_error);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(mContext.getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 显示请求网络数据进度条
     */
    public void showLoadingDialog() {
        try {
            if (!activity.isFinishing() && mDialogLoading != null && !mDialogLoading.isShowing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭请求网络数据进度条
     */
    public void dismissLoadingDialog() {
        try {
            if (!activity.isFinishing() && mDialogLoading != null && mDialogLoading.isShowing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取用户性别
     */
    private int getUserSex() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //0.女，1.男
                int sex = userInfo.t_sex;
                return sex != 2 ? sex : 0;
            } else {
                int sex = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_sex;
                return sex != 2 ? sex : 0;
            }
        }
        return 0;
    }
    /**
     * 获取主播数据
     */
    private void getActorInfo1(int fileId, int queryType, int authorId, final ViewHolder holder,int position) {
        holder.videoView.setUrl(mDatas.get(position).t_addres_url);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(authorId));
        paramMap.put("albumId", fileId > 0 ? String.valueOf(fileId) : "");
        paramMap.put("queryType", String.valueOf(queryType));
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_PLAY_PAGE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("getVideos","获取主播信息响应成功:"+response);
            }
        });

    }

    /**
     * 新增查看次数
     */
    private void addSeeTime(int fileId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("fileId", String.valueOf(fileId));
        OkHttpUtils.post().url(ChatApi.ADD_QUERY_DYNAMIC_COUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {

            }
        });
    }
    /**
     * 获取UserId
     */
    public String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.small_head_iv: {//个人信息
                //Intent intent = new Intent(mContext, ActorInfoActivity.class);
                Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                mContext.startActivity(intent);
                break;
            }
            case R.id.back_iv: {
                activity.finish();
                break;
            }
            case R.id.gift_iv: {//赠送礼物
                if (getUserSex() == 0) {
                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.sex_can_not_communicate);
                    return;
                }
                if (mActorId > 0) {
                    showRewardDialog();
                }
                break;
            }
        }
    }
    /**
     * 显示打赏礼物Dialog
     */
    private void showRewardDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_gift_layout, null);
        setGiftDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!activity.isFinishing()) {
            mDialog.show();
        }
    }
    /**
     * 礼物dialog view 初始化
     */
    private void setGiftDialogView(View view, final Dialog mDialog) {
        //-----------------初始化----------------
        final RecyclerView gift_rv = view.findViewById(R.id.gift_rv);
        final RecyclerView red_rv = view.findViewById(R.id.red_rv);
        final LinearLayout indicator_ll = view.findViewById(R.id.indicator_ll);
        final TextView gift_tv = view.findViewById(R.id.gift_tv);
        final TextView red_tv = view.findViewById(R.id.red_tv);
        final TextView gold_tv = view.findViewById(R.id.gold_tv);
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        TextView reward_tv = view.findViewById(R.id.reward_tv);

        //初始化显示礼物
        gift_tv.setSelected(true);
        red_tv.setSelected(false);
        gift_rv.setVisibility(View.VISIBLE);
        red_rv.setVisibility(View.GONE);
        indicator_ll.setVisibility(View.VISIBLE);

        //可用金币
        getMyGold(gold_tv);

        //处理list
        List<List<GiftBean>> giftListBeanList = new ArrayList<>();
        if (mGiftBeans != null && mGiftBeans.size() > 0) {
            int count = mGiftBeans.size() / 8;
            int left = mGiftBeans.size() % 8;
            if (count > 0) {//如果大于等于8个
                for (int i = 1; i <= count; i++) {
                    int start = (i - 1) * 8;
                    int end = i * 8;
                    List<GiftBean> subList = mGiftBeans.subList(start, end);
                    giftListBeanList.add(i - 1, subList);
                }
                if (left != 0) {//如果还剩余的话,那剩余的加进入
                    List<GiftBean> leftBeans = mGiftBeans.subList(count * 8, mGiftBeans.size());
                    giftListBeanList.add(count, leftBeans);
                }
            } else {
                giftListBeanList.add(0, mGiftBeans);
            }
        }

        //-----------------礼物---------------
        final List<ImageView> imageViews = new ArrayList<>();
        ViewPagerLayoutManager mLayoutManager = new ViewPagerLayoutManager(mContext, OrientationHelper.HORIZONTAL);
        gift_rv.setLayoutManager(mLayoutManager);
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(activity);
        gift_rv.setAdapter(giftAdapter);
        if (giftListBeanList.size() > 0) {
            giftAdapter.loadData(giftListBeanList);
            //设置指示器
            for (int i = 0; i < giftListBeanList.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                int width = DevicesUtil.dp2px(getApplicationContext(), 6);
                int height = DevicesUtil.dp2px(getApplicationContext(), 6);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = 10;
                imageView.setLayoutParams(params);
                if (i == 0) {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_white_back);
                } else {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_gray_back);
                }
                imageViews.add(imageView);
                indicator_ll.addView(imageView);
            }
        }

        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (imageViews.size() > 0) {
                    for (int i = 0; i < imageViews.size(); i++) {
                        if (i == position) {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_white_back);
                        } else {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_gray_back);
                        }
                    }
                }
            }
        });

        //红包
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        red_rv.setLayoutManager(gridLayoutManager);
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(activity);
        red_rv.setAdapter(goldGridRecyclerAdapter);
        goldGridRecyclerAdapter.loadData(getLocalRedList());

        //--------------处理切换----------------
        //礼物
        gift_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gift_tv.isSelected()) {
                    return;
                }
                gift_tv.setSelected(true);
                red_tv.setSelected(false);
                gift_rv.setVisibility(View.VISIBLE);
                red_rv.setVisibility(View.GONE);
                indicator_ll.setVisibility(View.VISIBLE);
            }
        });
        //红包
        red_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (red_tv.isSelected()) {
                    return;
                }
                red_tv.setSelected(true);
                gift_tv.setSelected(false);
                red_rv.setVisibility(View.VISIBLE);
                gift_rv.setVisibility(View.GONE);
                indicator_ll.setVisibility(View.INVISIBLE);
            }
        });
        //充值
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                activity.startActivity(intent);
                mDialog.dismiss();
            }
        });
        //dismiss的时候清空
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mGiftBeans != null && mGiftBeans.size() > 0) {
                    for (GiftBean bean : mGiftBeans) {
                        bean.isSelected = false;
                    }
                }
            }
        });
        //打赏
        reward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是礼物选中
                if (gift_tv.isSelected()) {
                    GiftBean giftBean = giftAdapter.getSelectBean();
                    if (giftBean == null) {
                        ToastUtil.showToast(getApplicationContext(), R.string.please_select_gift);
                        return;
                    }
                    //判断是否够
                    if (giftBean.t_gift_gold > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
                    reWardGift(giftBean);
                } else {//如果是红包选中
                    GoldBean goldBean = goldGridRecyclerAdapter.getSelectedBean();
                    if (goldBean == null) {
                        ToastUtil.showToast(getApplicationContext(), R.string.please_select_gold);
                        return;
                    }
                    if (goldBean.goldNumber > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
                    reWardGold(goldBean.goldNumber);
                }
                mDialog.dismiss();
            }
        });
    }
    /**
     * 打赏金币(红包)
     */
    private void reWardGold(int gold) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("gold", String.valueOf(gold));
        OkHttpUtils.post().url(ChatApi.SEND_RED_ENVELOPE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(), R.string.reward_success);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }
    /**
     * 打赏礼物
     */
    private void reWardGift(final GiftBean giftBean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("giftId", String.valueOf(giftBean.t_gift_id));
        paramMap.put("giftNum", "1");
        OkHttpUtils.post().url(ChatApi.USER_GIVE_GIFT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(getApplicationContext(), R.string.reward_success);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                }
            }
        });
    }
    /**
     * 获取我的金币余额
     */
    private void getMyGold(final TextView can_use_iv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_BALANCE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mMyGoldNumber = balanceBean.amount;
                        String content = activity.getResources().getString(R.string.can_use_gold) + mMyGoldNumber;
                        can_use_iv.setText(content);
                        can_use_iv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    /**
     * 获取本地红包集合
     */
    private List<GoldBean> getLocalRedList() {
        List<GoldBean> goldBeans = new ArrayList<>();
        // 99
        GoldBean one = new GoldBean();
        one.resourceId = R.drawable.reward_gold_one;
        one.goldNumber = 99;
        // 188
        GoldBean two = new GoldBean();
        two.resourceId = R.drawable.reward_gold_two;
        two.goldNumber = 188;
        // 520
        GoldBean three = new GoldBean();
        three.resourceId = R.drawable.reward_gold_three;
        three.goldNumber = 520;
        // 999
        GoldBean four = new GoldBean();
        four.resourceId = R.drawable.reward_gold_four;
        four.goldNumber = 999;
        // 1314
        GoldBean five = new GoldBean();
        five.resourceId = R.drawable.reward_gold_five;
        five.goldNumber = 1314;
        // 8888
        GoldBean six = new GoldBean();
        six.resourceId = R.drawable.reward_gold_six;
        six.goldNumber = 8888;
        goldBeans.add(0, one);
        goldBeans.add(1, two);
        goldBeans.add(2, three);
        goldBeans.add(3, four);
        goldBeans.add(4, five);
        goldBeans.add(5, six);
        return goldBeans;
    }

    /**
     * 点赞
     */
    private void addLike(final ViewHolder holder) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverLaudUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.ADD_LAUD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    holder.mLoveTv.setSelected(true);
                    String content = holder.mLoveTv.getText().toString().trim();
                    int number = Integer.parseInt(content) + 1;
                    holder.mLoveTv.setText(String.valueOf(number));
                }
            }
        });
    }
    /**
     * 取消点赞
     */
    private void cancelLike(final ViewHolder holder) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.CANCEL_LAUD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    holder.mLoveTv.setSelected(false);
                    String content = holder.mLoveTv.getText().toString().trim();
                    int number = Integer.parseInt(content) - 1;
                    holder.mLoveTv.setText(String.valueOf(number));
                }
            }
        });
    }
    /**
     * 添加关注
     */
    private void saveFollow(int actorId, final ViewHolder holder) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//关注人
        paramMap.put("coverFollowUserId", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(mContext.getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(mContext.getApplicationContext(), message);
                        holder.mFocusFl.setVisibility(View.INVISIBLE);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
     * 显示投诉举报Popup
     */
    private void showComplainPopup() {
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(mContext.getApplicationContext()).inflate(R.layout.popup_complain_layout, null, false);
        final PopupWindow window = new PopupWindow(contentView, DevicesUtil.dp2px(mContext.getApplicationContext(), 81),
                DevicesUtil.dp2px(mContext.getApplicationContext(), 81), true);
        TextView complainTv = contentView.findViewById(R.id.complain_tv);
        complainTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToReport = true;
                Intent intent = new Intent(mContext.getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                mContext.startActivity(intent);
                window.dismiss();
            }
        });
        TextView reportTv = contentView.findViewById(R.id.report_tv);
        reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToReport = true;
                Intent intent = new Intent(mContext.getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                mContext.startActivity(intent);
                window.dismiss();
            }
        });
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
    }

    /**
     * 显示查看微信号提醒
     */
    private void showSeeWeChatRemindDialog(ViewHolder holder) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_video_layout, null);
        setDialogView(view, mDialog,holder);
        mDialog.setContentView(view);
        Point outSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!activity.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog, final ViewHolder holder) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        des_tv.setText(mContext.getResources().getString(R.string.see_we_chat_number_des_one));
        //金币
        TextView gold_tv = view.findViewById(R.id.gold_tv);
        int gold = mActorPlayBean.t_weixin_gold;
        if (gold > 0) {
            String content = gold + mContext.getResources().getString(R.string.gold);
            gold_tv.setText(content);
        }
        //升级
        ImageView update_iv = view.findViewById(R.id.update_iv);
        update_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VipCenterActivity.class);
                mContext.startActivity(intent);
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
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeWeChat(holder);
                mDialog.dismiss();
            }
        });
    }
    /**
     * 查看微信号码
     */
    private void seeWeChat(final ViewHolder holder) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.SEE_WEI_XIN_CONSUME)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                            holder.mWeChatFl.setVisibility(View.GONE);
                            showSeeWeChatCopyDialog(holder);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        }
                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialog(activity);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }
    /**
     * 显示查看微信成功复制Dialog
     */
    private void showSeeWeChatCopyDialog(ViewHolder holder) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_we_chat_number_layout, null);
        setCopyDialogView(view, mDialog,holder);
        mDialog.setContentView(view);
        Point outSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!activity.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setCopyDialogView(View view, final Dialog mDialog, final ViewHolder holder) {
        //描述
        TextView gold_tv = view.findViewById(R.id.des_tv);
        final String number = mActorPlayBean.t_weixin;
        String content = activity.getResources().getString(R.string.we_chat_number_des_one) + number;
        gold_tv.setText(content);
        //复制
        TextView copy_tv = view.findViewById(R.id.copy_tv);
        copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", number);
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtil.showToast(getApplicationContext(), R.string.copy_success);
                }
                String content = activity.getResources().getString(R.string.we_chat_number_des) + number;
                holder.mWeChatTv.setText(content);
                holder.mWeChatTv.setVisibility(View.VISIBLE);
                mDialog.dismiss();
            }
        });
    }
    /**
     * 用户对主播发起聊天
     */
    private void requestChat(final int roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverLinkUserId", String.valueOf(mActorId));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(mContext.getApplicationContext(), VideoChatOneActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);//主播ID
                        mContext.startActivity(intent);
                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext.getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(mContext.getApplicationContext(), R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext.getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(mContext.getApplicationContext(), R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(mContext.getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(mContext.getApplicationContext(), R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(activity);
                    } else {
                        ToastUtil.showToast(mContext.getApplicationContext(), R.string.system_error);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext.getApplicationContext(), R.string.system_error);
            }

        });
    }
}
