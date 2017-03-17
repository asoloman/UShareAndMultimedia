package com.dbyc.ushareandmultimedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dbyc.ushareandmultimedia.youmeng.activity.StatisticsActivity;
import com.dbyc.ushareandmultimedia.mediarelease.activity.MediaActivity;
import com.dbyc.ushareandmultimedia.mediarelease.activity.Play_videoActivity;
import com.dbyc.ushareandmultimedia.youmeng.activity.ShareHelper;
import com.dbyc.ushareandmultimedia.youmeng.activity.ThirdLoginHelper;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_share;
    private Button btn_wblogin;
    private Button btn_wxlogin;
    private Button btn_qqlogin;
    private ThirdLoginHelper thirdLoginHelper;
    private ShareHelper shareHelper;
    private Button btn_recorderVideo;
    private ImageView iv_video_img;
    private ImageView iv_paly;
    private RelativeLayout rl_videoAndAudio;
    private MediaPlayer mMediaPlayer;//媒体播放器
    String videoUrl;
    private Button btn_meida;

    private int isClickType = 0;
    public List<String> mSelectedImage = new LinkedList<>();
    long suijishu;
    private Button btn_statistic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
        btn_wblogin = (Button) findViewById(R.id.btn_wblogin);
        btn_wblogin.setOnClickListener(this);
        btn_wxlogin = (Button) findViewById(R.id.btn_wxlogin);
        btn_wxlogin.setOnClickListener(this);
        btn_qqlogin = (Button) findViewById(R.id.btn_qqlogin);
        btn_qqlogin.setOnClickListener(this);

        iv_video_img = (ImageView) findViewById(R.id.iv_video_img);
        iv_video_img.setOnClickListener(this);
        iv_paly = (ImageView) findViewById(R.id.iv_paly);
        iv_paly.setOnClickListener(this);
        rl_videoAndAudio = (RelativeLayout) findViewById(R.id.rl_videoAndAudio);
        rl_videoAndAudio.setOnClickListener(this);
        btn_meida = (Button) findViewById(R.id.btn_meida);
        btn_meida.setOnClickListener(this);
        btn_statistic = (Button) findViewById(R.id.btn_statistic);
        btn_statistic.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wblogin:
                thirdLoginHelper = new ThirdLoginHelper(this, SHARE_MEDIA.SINA);
                break;
            case R.id.btn_wxlogin:
                thirdLoginHelper = new ThirdLoginHelper(this, SHARE_MEDIA.WEIXIN);
                break;
            case R.id.btn_qqlogin:
                thirdLoginHelper = new ThirdLoginHelper(this, SHARE_MEDIA.QQ);
                break;
            case R.id.btn_share:
                shareHelper = new ShareHelper(this);
                break;
            case R.id.iv_paly:
//                if (isSendType == 2) {
//                    try {
//                        if (mMediaPlayer.isPlaying()) {
//                            mMediaPlayer.pause();
//                        } else {
//                            play(fileUrl);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else if (isSendType == 1) {
                Intent in = new Intent(this, Play_videoActivity.class);
                in.putExtra("url", videoUrl);
                startActivity(in);
//                }
                Log.e("sss", "开始播放");
                break;
            case R.id.btn_meida:
                startActivity(new Intent(MainActivity.this, MediaActivity.class));
                break;
            case R.id.btn_statistic:
                Intent intent1 = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            videoUrl = data.getStringExtra("videoUrl");
            playVideo(videoUrl);
            rl_videoAndAudio.setVisibility(View.VISIBLE);
        } else {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }
    }

    /*准备播放资源*/
    private void playVideo(String videoUrl) {
        mMediaPlayer = new MediaPlayer();
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoUrl);
        Bitmap video_img_bitmap = media.getFrameAtTime();
        iv_video_img.setImageBitmap(video_img_bitmap);
    }


}
