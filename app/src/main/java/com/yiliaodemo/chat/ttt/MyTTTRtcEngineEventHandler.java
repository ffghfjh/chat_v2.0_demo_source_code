package com.yiliaodemo.chat.ttt;

import android.content.Context;
import android.content.Intent;

import com.wushuangtech.bean.LocalAudioStats;
import com.wushuangtech.bean.LocalVideoStats;
import com.wushuangtech.bean.RemoteAudioStats;
import com.wushuangtech.bean.RemoteVideoStats;
import com.wushuangtech.bean.RtcStats;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;
import com.yiliaodemo.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_AUDIO_ROUTE;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_AUDIO_VOLUME_INDICATION;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_CONNECTLOST;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_ENTER_ROOM;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_ERROR;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_LOCAL_AUDIO_STATE;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_LOCAL_VIDEO_STATE;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_MUTE_AUDIO;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_REMOTE_AUDIO_STATE;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_REMOTE_VIDEO_STATE;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_REMOVE_FIRST_FRAME_COME;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_SEI;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_SPEAK_MUTE_AUDIO;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_USER_JOIN;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_USER_MUTE_VIDEO;
import static com.yiliaodemo.chat.ttt.LocalConstans.CALL_BACK_ON_USER_OFFLINE;


/**
 * Created by wangzhiguo on 17/10/24.
 */

public class MyTTTRtcEngineEventHandler extends TTTRtcEngineEventHandler {

    public static final String TAG = "MyTTTRtcEngineEventHandlerMM";
    public static final String MSG_TAG = "MyTTTRtcEngineEventHandlerMSGMM";
    private boolean mIsSaveCallBack;
    private List<JniObjs> mSaveCallBack;
    private Context mContext;

    public MyTTTRtcEngineEventHandler(Context mContext) {
        this.mContext = mContext;
        mSaveCallBack = new ArrayList<>();
    }

    @Override
    public void onJoinChannelSuccess(String channel, long uid) {
        LogUtil.i("onJoinChannelSuccess.... channel ： " + channel + " | uid : " + uid);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_ENTER_ROOM;
        mJniObjs.mChannelName = channel;
        mJniObjs.mUid = uid;
        sendMessage(mJniObjs);
        mIsSaveCallBack = true;
    }

    @Override
    public void onError(final int errorType) {
        LogUtil.i("onError.... errorType ： " + errorType + "mIsSaveCallBack : " + mIsSaveCallBack);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_ERROR;
        mJniObjs.mErrorType = errorType;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onUserJoined(long nUserId, int identity) {
        LogUtil.i("onUserJoined.... nUserId ： " + nUserId + " | identity : " + identity
                + " | mIsSaveCallBack : " + mIsSaveCallBack);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_USER_JOIN;
        mJniObjs.mUid = nUserId;
        mJniObjs.mIdentity = identity;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onUserOffline(long nUserId, int reason) {
        LogUtil.i("onUserOffline.... nUserId ： " + nUserId + " | reason : " + reason);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_USER_OFFLINE;
        mJniObjs.mUid = nUserId;
        mJniObjs.mReason = reason;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onConnectionLost() {
        LogUtil.i("onConnectionLost.... ");
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_CONNECTLOST;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onUserEnableVideo(long uid, boolean muted) {
        LogUtil.i("onUserEnableVideo.... uid : " + uid + " | mute : " + muted);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_USER_MUTE_VIDEO;
        mJniObjs.mUid = uid;
        mJniObjs.mIsEnableVideo = muted;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onAudioVolumeIndication(long nUserID, int audioLevel, int audioLevelFullRange) {
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_AUDIO_VOLUME_INDICATION;
        mJniObjs.mUid = nUserID;
        mJniObjs.mAudioLevel = audioLevel;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onFirstRemoteVideoFrame(long uid, int width, int height) {
        LogUtil.i("onFirstRemoteVideoFrame.... uid ： " + uid + " | width : " + width + " | height : " + height);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_REMOVE_FIRST_FRAME_COME;
        mJniObjs.mUid = uid;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onRemoteVideoStats(RemoteVideoStats stats) {
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_REMOTE_VIDEO_STATE;
        mJniObjs.mRemoteVideoStats = stats;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onRemoteAudioStats(RemoteAudioStats stats) {
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_REMOTE_AUDIO_STATE;
        mJniObjs.mRemoteAudioStats = stats;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onLocalVideoStats(LocalVideoStats stats) {
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_LOCAL_VIDEO_STATE;
        mJniObjs.mLocalVideoStats = stats;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onLocalAudioStats(LocalAudioStats stats) {
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_LOCAL_AUDIO_STATE;
        mJniObjs.mLocalAudioStats = stats;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onSetSEI(String sei) {
        LogUtil.i("onSei.... sei : " + sei);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_SEI;
        mJniObjs.mSEI = sei;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onUserMuteAudio(long uid, boolean muted) {
        LogUtil.i("OnRemoteAudioMuted.... uid : " + uid + " | muted : " + muted + " | mIsSaveCallBack : " + mIsSaveCallBack);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_MUTE_AUDIO;
        mJniObjs.mUid = uid;
        mJniObjs.mIsDisableAudio = muted;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onSpeakingMuted(long uid, boolean muted) {
        LogUtil.i("onSpeakingMuted.... uid : " + uid + " | muted : " + muted + " | mIsSaveCallBack : " + mIsSaveCallBack);
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_SPEAK_MUTE_AUDIO;
        mJniObjs.mUid = uid;
        mJniObjs.mIsDisableAudio = muted;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onAudioRouteChanged(int routing) {
        LogUtil.i("onAudioRouteChanged.... routing : " + routing);
        //MainActivity.mCurrentAudioRoute = routing;
        JniObjs mJniObjs = new JniObjs();
        mJniObjs.mJniType = CALL_BACK_ON_AUDIO_ROUTE;
        mJniObjs.mAudioRoute = routing;
        if (mIsSaveCallBack) {
            saveCallBack(mJniObjs);
        } else {
            sendMessage(mJniObjs);
        }
    }

    @Override
    public void onLeaveChannel(RtcStats stats) {
        LogUtil.i("onLeaveChannel....");
    }

    @Override
    public void onFirstRemoteVideoDecoded(long uid, int width, int height) {
    }

    @Override
    public void onFirstLocalVideoFrame(int width, int height) {
        LogUtil.i("onFirstLocalVideoFrame.... width : " + width + " | height : " + height);
    }

    @Override
    public void onPlayChatAudioCompletion(String filePath) {
        LogUtil.i("onPlayChatAudioCompletion: ");
    }

    @Override
    public void onRtcStats(RtcStats stats) {
    }

    private void sendMessage(JniObjs mJniObjs) {
        Intent i = new Intent();
        i.setAction(TAG);
        i.putExtra(MSG_TAG, mJniObjs);
        i.setExtrasClassLoader(JniObjs.class.getClassLoader());
        mContext.sendBroadcast(i);
    }

    public void setIsSaveCallBack(boolean mIsSaveCallBack) {
        this.mIsSaveCallBack = mIsSaveCallBack;
        if (!mIsSaveCallBack) {
            for (int i = 0; i < mSaveCallBack.size(); i++) {
                sendMessage(mSaveCallBack.get(i));
            }
            mSaveCallBack.clear();
        }
    }

    private void saveCallBack(JniObjs mJniObjs) {
        if (mIsSaveCallBack) {
            mSaveCallBack.add(mJniObjs);
        }
    }

}
