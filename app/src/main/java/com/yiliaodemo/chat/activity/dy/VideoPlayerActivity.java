package com.yiliaodemo.chat.activity.dy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import com.dueeeke.videoplayer.player.VideoView;
import com.yiliaodemo.chat.R;


public class VideoPlayerActivity extends AppCompatActivity {

    VideoView mVideoView;
    ImageView stopView;
    //PlayerConfig playerConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mVideoView = findViewById(R.id.video_player);
        stopView = findViewById(R.id.paly_stop_1);
        String videoUrl = getIntent().getStringExtra("url");
//        playerConfig = new PlayerConfig.Builder()
//                .enableCache()
//                .usingSurfaceView()
//                .savingProgress()
//                .disableAudioFocus()
//                .setLooping()
//                .addToPlayerManager()
//                .build();
        mVideoView.setUrl(videoUrl);
//        mVideoView.setPlayerConfig(playerConfig);
//        mVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_CENTER_CROP);
        playVideo();
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoView.isPlaying()){
                    stopView.setVisibility(View.VISIBLE);
                    mVideoView.pause();
                }else {
                    stopView.setVisibility(View.GONE);
                    mVideoView.start();
                }
            }
        });
    }


    @Override
    public void onResume() {
        Log.d("getVideo","onResume");
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onPause() {
        Log.d("getVideo","onPause");
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("getVideo","onDestroy");
        super.onDestroy();
        mVideoView.release();
    }


    /**
     * 播放视频
     */
    private void playVideo() {
        if (mVideoView != null) {
            stopView.setVisibility(View.GONE);
            mVideoView.start();
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if(!mVideoView.onBackPressed()){
//            super.onBackPressed();
//        }
//    }

    /**
     * 停止播放
     */
    private void releaseVideo() {
        if (mVideoView != null) {
            stopView.setVisibility(View.VISIBLE);
            mVideoView.release();
        }
    }
}
