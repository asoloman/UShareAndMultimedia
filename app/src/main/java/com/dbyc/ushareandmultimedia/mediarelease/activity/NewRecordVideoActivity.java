package com.dbyc.ushareandmultimedia.mediarelease.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.dbyc.ushareandmultimedia.BaseActivity;
import com.dbyc.ushareandmultimedia.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import sz.itguy.utils.FileUtil;
import sz.itguy.wxlikevideo.camera.CameraHelper;
import sz.itguy.wxlikevideo.recorder.WXLikeVideoRecorder;
import sz.itguy.wxlikevideo.views.CameraPreviewView;


/**
 * 新视频录制页面
 *
 * @author Martin
 */
public class NewRecordVideoActivity extends BaseActivity implements View.OnTouchListener {
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(NewRecordVideoActivity.this, "没有声音权限", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    // 输出宽度
    private static final int OUTPUT_WIDTH = 640;
    // 输出高度
    private static final int OUTPUT_HEIGHT = 480;
    // 宽高比
    private static final float RATIO = 1f * OUTPUT_WIDTH / OUTPUT_HEIGHT;

    private Camera mCamera;

    private WXLikeVideoRecorder mRecorder;

    private static final int CANCEL_RECORD_OFFSET = -100;
    private float mDownX, mDownY;
    private boolean isCancelRecord = false;
    private ProgressBar rpb_recordProgressBar;

    private int mTimeCount;

    private Timer mTimer;
    private boolean isLvZhi = true;
    private TextView button_start;
    private boolean isHaveException = false;//是否产生了异常

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recorder);
        int cameraId = CameraHelper.getDefaultCameraID();
        // Create an instance of Camera
        mCamera = CameraHelper.getCameraInstance(cameraId);
        if (null == mCamera) {
            Toast.makeText(this, "打开相机失败！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 初始化录像机
        mRecorder = new WXLikeVideoRecorder(this, FileUtil.MEDIA_FILE_DIR, handler);
        mRecorder.setOutputSize(OUTPUT_WIDTH, OUTPUT_HEIGHT);


        CameraPreviewView preview = (CameraPreviewView) findViewById(R.id.camera_preview);
        try {
            preview.setCamera(mCamera, cameraId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            isHaveException = true;
            displayFrameworkBugMessageAndExit();
        }

        mRecorder.setCameraPreviewView(preview);

        rpb_recordProgressBar = (ProgressBar) findViewById(R.id.rpb_recordProgressBar);
        rpb_recordProgressBar.setMax(30);
        button_start = (TextView) findViewById(R.id.button_start);

        button_start.setOnTouchListener(this);

//        ((TextView) findViewById(R.id.filePathTextView)).setText("请在" + FileUtil.MEDIA_FILE_DIR + "查看录制的视频文件");
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("硬件未准备好，或者缺少相机启动权限。");
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecorder != null) {
            boolean recording = mRecorder.isRecording();
            // 页面不可见就要停止录制
            mRecorder.stopRecording();
            // 录制时退出，直接舍弃视频
            if (recording) {
                FileUtil.deleteFile(mRecorder.getFilePath());
            }
        }
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (mCamera != null && !isHaveException) {
            mCamera.setPreviewCallback(null);
            // 释放前先停止预！
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        button_start.setBackgroundResource(R.drawable.wz_text_yuanxian_bai);
        button_start.setTextColor(Color.WHITE);
        if (mRecorder.isRecording()) {
            Log.e("log", "正在录制中…");
//        	ShowUtil.showToast(this, );
//        	rpb_recordProgressBar.start();
            return;
        }

        // initialize video camera
        if (prepareVideoRecorder()) {
            // 录制视频
            if (!mRecorder.startRecording())
                Toast.makeText(this, "录制失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 准备视频录制器
     *
     * @return
     */
    private boolean prepareVideoRecorder() {
        if (!FileUtil.isSDCardMounted()) {
            Toast.makeText(this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        mRecorder.stopRecording();
        String videoPath = mRecorder.getFilePath();
        // 没有录制视频
        if (null == videoPath) {
            return;
        }
        // 若取消录制，则删除文件，否则通知宿主页面发送视频
        if (isCancelRecord) {
            FileUtil.deleteFile(videoPath);
        } else {
            // 告诉宿主页面录制视频的路径

            Log.e("log", "videoPath  = " + videoPath);
            Intent intent = new Intent();
            intent.putExtra("videoUrl", videoPath);

            setResult(RESULT_OK, intent);
            finish();
//            startActivity(news Intent(this, PlayVideoActivity.class).putExtra("videoUrl", videoPath));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCancelRecord = false;
                mDownX = event.getX();
                mDownY = event.getY();
                startRecord();
                mTimeCount = 0;// 时间计数器重新赋值
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mTimeCount++;
                        rpb_recordProgressBar.setProgress(mTimeCount);// 设置进度条
                        if (mTimeCount == 31) {// 达到指定时间，停止拍摄
                            // TODO: 2016/12/12   修改录制时间
                            isLvZhi = false;
                            stopRecord();

                        }
                    }
                }, 0, 1000);

                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRecorder.isRecording())
                    return false;

                float y = event.getY();
                if (y - mDownY < CANCEL_RECORD_OFFSET) {
                    if (!isCancelRecord) {
                        // cancel record
                        isCancelRecord = true;
                        button_start.setBackgroundResource(R.drawable.wz_text_yuanxian_lan);
                        button_start.setTextColor(getResources().getColor(R.color.but_text_color));
                        mTimeCount = 0;// 时间计数器重新赋值
                        rpb_recordProgressBar.setProgress(0);
                        mTimer.cancel();
                        Toast.makeText(this, "取消 ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isCancelRecord = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isLvZhi) {
                    stopRecord();
                }

                break;
        }

        return true;
    }

    /**
     * 开始录制失败回调任务
     *
     * @author Martin
     */
    public static class StartRecordFailCallbackRunnable implements Runnable {

        private WeakReference<NewRecordVideoActivity> mNewRecordVideoActivityWeakReference;

        public StartRecordFailCallbackRunnable(NewRecordVideoActivity activity) {
            mNewRecordVideoActivityWeakReference = new WeakReference<NewRecordVideoActivity>(activity);
        }

        @Override
        public void run() {
            NewRecordVideoActivity activity;
            if (null == (activity = mNewRecordVideoActivityWeakReference.get()))
                return;

            String filePath = activity.mRecorder.getFilePath();
            if (!TextUtils.isEmpty(filePath)) {
                FileUtil.deleteFile(filePath);
                Toast.makeText(activity, "Start record failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 停止录制回调任务
     *
     * @author Martin
     */
    public static class StopRecordCallbackRunnable implements Runnable {

        private WeakReference<NewRecordVideoActivity> mNewRecordVideoActivityWeakReference;

        public StopRecordCallbackRunnable(NewRecordVideoActivity activity) {
            mNewRecordVideoActivityWeakReference = new WeakReference<NewRecordVideoActivity>(activity);
        }

        @Override
        public void run() {
            NewRecordVideoActivity activity;
            if (null == (activity = mNewRecordVideoActivityWeakReference.get()))
                return;

            String filePath = activity.mRecorder.getFilePath();
            if (!TextUtils.isEmpty(filePath)) {
                if (activity.isCancelRecord) {
                    FileUtil.deleteFile(filePath);
                } else {
                    Toast.makeText(activity, "Video file path: " + filePath, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
