package com.dbyc.ushareandmultimedia.mediarelease.activity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.dbyc.ushareandmultimedia.BaseActivity;
import com.dbyc.ushareandmultimedia.R;
import com.dbyc.ushareandmultimedia.mediarelease.weight.FullScreenVideoView;

/**
 * Created by Administrator on 2016-07-06.
 */
public class Play_videoActivity extends BaseActivity implements View.OnClickListener {

    private FullScreenVideoView vv_play_audio;
    private VideoView vv_play;
    private ImageView iv_back;
    private ImageView iv_music_bg;
    private String fileurl;
    Handler mHandler = new Handler();
    ProgressBar pb_ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileurl = getIntent().getStringExtra("url");

//        if (fileurl.endsWith(".mp3")) {
//            setContentView(R.layout.activity_play_audio_);
//            initView_audio();
//
//        } else {
            //允许频幕旋转
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            setContentView(R.layout.activity_play_video);
            initView();
//        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initView() {
//        vv_play = (FullScreenVideoView) findViewById(R.id.vv_play);
        vv_play = (VideoView) findViewById(R.id.vv_play);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_music_bg = (ImageView) findViewById(R.id.iv_music_bg);
        pb_ProgressBar = (ProgressBar) findViewById(R.id.pb_ProgressBar);
        iv_back.setOnClickListener(this);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vv_play.setMediaController(new MediaController(Play_videoActivity.this));
                vv_play.setVideoURI(Uri.parse(fileurl));
                vv_play.requestFocus();
                vv_play.start();
            }
        }, 100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vv_play.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        pb_ProgressBar.setVisibility(View.VISIBLE);
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                        if (mp.isPlaying()) {
                            pb_ProgressBar.setVisibility(View.GONE);
                        }
                    }

                    return true;
                }
            });
        }
        vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pb_ProgressBar.setVisibility(View.GONE);//缓冲完成就隐藏

            }
        });

    }


    private void initView_audio() {
        vv_play_audio = (FullScreenVideoView) findViewById(R.id.vv_play_audio);
//        vv_play = (VideoView) findViewById(R.id.vv_play);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_music_bg = (ImageView) findViewById(R.id.iv_music_bg);
        pb_ProgressBar = (ProgressBar) findViewById(R.id.pb_ProgressBar);
        iv_back.setOnClickListener(this);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vv_play_audio.setMediaController(new MediaController(Play_videoActivity.this));
                vv_play_audio.setVideoURI(Uri.parse(fileurl));
                vv_play_audio.requestFocus();
                vv_play_audio.start();
            }
        }, 100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vv_play_audio.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        pb_ProgressBar.setVisibility(View.VISIBLE);
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                        if (mp.isPlaying()) {
                            pb_ProgressBar.setVisibility(View.GONE);
                        }
                    }
                    return true;
                }
            });
        }
        vv_play_audio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pb_ProgressBar.setVisibility(View.GONE);//缓冲完成就隐藏

            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
