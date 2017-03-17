package com.dbyc.ushareandmultimedia.mediarelease.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbyc.ushareandmultimedia.BaseActivity;
import com.dbyc.ushareandmultimedia.R;
import com.dbyc.ushareandmultimedia.mediarelease.utils.DateTiemUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by K on 2017/2/14.
 */
public class AudioActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView tv_queding, tv_head_title;
    private TextView tv_time_count;
    private TextView btnStart, btnStop, btnPlay, btnFinish;

    private File fpath;
    private File audioFile;

    private ProgressBar progressBar;
    private ProgressBar pb_time;
    private MediaRecorder mediaRecorder;

    private MediaPlayer mMediaPlayer;//媒体播放器
    private Timer timer;
    private Timer timer_2;// 时间进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        mMediaPlayer = new MediaPlayer();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pb_time = (ProgressBar) findViewById(R.id.pb_time);
        progressBar.setMax(10);
        progressBar.setProgress(3);

        pb_time.setMax(3 * 60);
        pb_time.setProgress(0);

        tv_queding = (TextView) findViewById(R.id.tv_queding);
        tv_time_count = (TextView) this.findViewById(R.id.tv_time_count);
        tv_queding.setOnClickListener(this);
        tv_queding.setVisibility(View.VISIBLE);
        tv_queding.setClickable(false);

        tv_head_title = (TextView) this.findViewById(R.id.tv_head_title);
        tv_head_title.setText("录音");

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        btnStart = (TextView) this.findViewById(R.id.btn_start);
//        btnStart.setText("开始");
        btnStart.setOnClickListener(this);
        btnStop = (TextView) this.findViewById(R.id.btn_stop);
//        btnStop.setText("结束");
        btnStop.setOnClickListener(this);
        btnPlay = (TextView) this.findViewById(R.id.btn_play);
//        btnPlay.setText("播放");
        btnPlay.setOnClickListener(this);
        btnFinish = (TextView) findViewById(R.id.btn_finish);
//        btnFinish.setText("停止");
        btnFinish.setOnClickListener(this);


        btnStop.setClickable(false);
        btnPlay.setClickable(false);
        btnFinish.setClickable(false);
    }

    /**
     * 初始化播放器
     */
    private void play(final String fileurl) throws IOException {
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(fileurl);
        mMediaPlayer.prepare();
        mMediaPlayer.start();//播放

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setClickable(true);
                btnFinish.setClickable(true);
                btnStart.setClickable(true);
                btnStop.setClickable(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                // 开始录制
                //第6步：调用start方法开始录音
                setAudio();
                try {
                    mediaRecorder.start();
                } catch (Exception e) {
                    e.printStackTrace();
//                    ShowUtil.showToast(this, "缺少录音权限，请打开录音权限，稍后重试");
                    displayFrameworkBugMessageAndExit();
                }
                launchTimerTask();
                launchTimerTask_time();
                btnStart.setClickable(false);
                btnPlay.setClickable(false);
                btnFinish.setClickable(false);
                btnStop.setClickable(true);
                tv_queding.setClickable(false);
                btnStart.setTextColor(getResources().getColor(R.color.blue));
                btnStop.setTextColor(getResources().getColor(R.color.white));
                btnPlay.setTextColor(getResources().getColor(R.color.white));
                btnFinish.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.btn_stop:
                // 停止录制
                timer.cancel();
                timer_2.cancel();
                count = 0;
                mediaRecorder.stop();
                mediaRecorder.release();
                btnPlay.setClickable(true);
                btnFinish.setClickable(true);
                btnStart.setClickable(false);
                btnStop.setClickable(false);
                tv_queding.setClickable(true);
                btnStop.setTextColor(getResources().getColor(R.color.blue));
                btnStart.setTextColor(getResources().getColor(R.color.white));
                btnPlay.setTextColor(getResources().getColor(R.color.white));
                btnFinish.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.btn_play:
                btnStart.setClickable(false);
                btnStop.setClickable(false);
                btnPlay.setClickable(true);
                btnFinish.setClickable(true);
                try {
                    play(audioFile.getAbsolutePath());
                    btnStop.setTextColor(getResources().getColor(R.color.white));
                    btnStart.setTextColor(getResources().getColor(R.color.white));
                    btnPlay.setTextColor(getResources().getColor(R.color.blue));
                    btnFinish.setTextColor(getResources().getColor(R.color.white));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_finish:
                // 完成播放
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                btnPlay.setClickable(true);
                btnFinish.setClickable(false);
                btnStart.setClickable(true);
                btnStop.setClickable(false);
                btnStop.setTextColor(getResources().getColor(R.color.white));
                btnStart.setTextColor(getResources().getColor(R.color.white));
                btnPlay.setTextColor(getResources().getColor(R.color.white));
                btnFinish.setTextColor(getResources().getColor(R.color.blue));
                break;
            case R.id.tv_queding:
//                if (audioFile.length() > 0) {
//                    Intent intent = new Intent(AudioActivity.this, ReleaseNoteActivity.class);
//                    intent.putExtra("isSendType", 2);
//                    intent.putExtra("fileUrl", audioFile.getAbsolutePath());
//                    startActivity(intent);
//                    finish();
//                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("硬件未准备好，或者缺少录音权限。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    /**
     * 初始化录音功能
     */
    public void setAudio() {
        try {
            mediaRecorder = new MediaRecorder();
            // 第1步：设置音频来源（MIC表示麦克风）
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //第2步：设置音频输出格式（默认的输出格式）
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            //第3步：设置音频编码方式（默认的编码方式）
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED))// 手机有SD卡的情况
            {
                // 在这里我们创建一个文件，用于保存录制内容
                fpath = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/data/files/");
                fpath.mkdirs();// 创建文件夹
            } else// 手机无SD卡的情况
            {
                fpath = this.getCacheDir();
            }
            //创建一个临时的音频输出文件
            audioFile = File.createTempFile("recording", ".amr", fpath);
            //第4步：指定音频输出文件
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.getMaxAmplitude();
            //第5步：调用prepare方法
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            displayFrameworkBugMessageAndExit();
        }
    }

    int count = 0;
    private Handler handler = new Handler();

    public void launchTimerTask_time() {
        timer_2 = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > 3 * 60) {
                    // 停止录制
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            timer.cancel();
                            timer_2.cancel();
                            count = 0;
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            btnPlay.setClickable(true);
                            btnFinish.setClickable(true);
                            btnStart.setClickable(false);
                            btnStop.setClickable(false);
                            tv_queding.setClickable(true);
                            btnStop.setTextColor(getResources().getColor(R.color.blue));
                            btnStart.setTextColor(getResources().getColor(R.color.white));
                            btnPlay.setTextColor(getResources().getColor(R.color.white));
                            btnFinish.setTextColor(getResources().getColor(R.color.white));
                        }
                    });

                }
                pb_time.setProgress(count);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_time_count.setText(DateTiemUtils.longToDate(count * 1000));
                    }
                });

            }
        };
        timer_2.schedule(timerTask, 1000, 1000);
    }


    public void launchTimerTask() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int ratio = mediaRecorder.getMaxAmplitude();

                int db = 0;// 分贝
                if (ratio > 1)
                    db = (int) (20 * Math.log10(ratio));
                progressBar.setProgress(db / 10);
            }
        };

        timer.schedule(timerTask, 400, 400);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.seekTo(0);
    }

    @Override
    protected void onDestroy() {
        mMediaPlayer.release();
        super.onDestroy();
    }
}
