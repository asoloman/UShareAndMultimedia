package com.dbyc.ushareandmultimedia.mediarelease.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbyc.ushareandmultimedia.R;
import com.dbyc.ushareandmultimedia.mediarelease.adapter.NoScrollGridViewAdapter;
import com.dbyc.ushareandmultimedia.mediarelease.okhttp.OkHttpUtils;
import com.dbyc.ushareandmultimedia.mediarelease.okhttp.callback.StringCallback;
import com.dbyc.ushareandmultimedia.mediarelease.utils.LogUtils;
import com.dbyc.ushareandmultimedia.mediarelease.weight.ActionSheetDialog;
import com.dbyc.ushareandmultimedia.mediarelease.weight.CircleProgress;
import com.dbyc.ushareandmultimedia.mediarelease.weight.NoScrollGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;

public class ReleaseNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_video_img;
    private ImageView iv_paly;
    private RelativeLayout rl_videoAndAudio;
    private NoScrollGridView nsgv_send_note_gridview;
    private NoScrollGridViewAdapter mAdapter;
    private List<Bitmap> mImgList;
    private int isClickType = 0; //1 = 图片 , 2 = 音频; 3 = 视频;
    private static final String PHOTO_FILE_NAME = "wevalue_img.jpg";
    private String fileUrl;
    private int isSendType;
    private MediaPlayer mMediaPlayer;//媒体播放器
    private Bitmap bitmap;
    long suijishu;
    String api = "http://192.168.0.107:8003/api/dbsocial.ashx?method=sendvideo";
    private TextView tv_queding;
    private CircleProgress c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_show);
        initView();
    }

    private void initView() {
        c = (CircleProgress) findViewById(R.id.pgb);
        tv_queding = (TextView) findViewById(R.id.tv_queding);
        iv_paly = (ImageView) findViewById(R.id.iv_paly);
        nsgv_send_note_gridview = (NoScrollGridView) findViewById(R.id.nsgv_send_note_gridview);
        rl_videoAndAudio = (RelativeLayout) findViewById(R.id.rl_videoAndAudio);
        iv_video_img = (ImageView) findViewById(R.id.iv_video_img);
        nsgv_send_note_gridview = (NoScrollGridView) findViewById(R.id.nsgv_send_note_gridview);
        iv_paly.setOnClickListener(this);
        tv_queding.setOnClickListener(this);
        isSendType = getIntent().getIntExtra("isSendType", -1);
        nsgv_send_note_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == MediaActivity.mSelectedImage.size()) {
                    addImg("手机拍照", "相册选择");
                }
            }
        });
        switch (isSendType) {
            case 1://视频
                fileUrl = getIntent().getStringExtra("fileUrl");
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(fileUrl);
                Bitmap video_img_bitmap = media.getFrameAtTime();
                iv_video_img.setImageBitmap(video_img_bitmap);
                LogUtils.e("fileUrl = " + fileUrl);
                rl_videoAndAudio.setVisibility(View.VISIBLE);
                break;
            case 2://音频
                fileUrl = getIntent().getStringExtra("fileUrl");
                rl_videoAndAudio.setVisibility(View.VISIBLE);
                mMediaPlayer = new MediaPlayer();
                iv_video_img.setImageResource(R.mipmap.default_head);
                break;
            case 3://图片
                initGridViewData();
                break;
            case 4://文字
                initGridViewData();
                break;
        }
    }

    /*图片预览*/
    private void initGridViewData() {
        if (MediaActivity.mSelectedImage == null) {
            MediaActivity.mSelectedImage = new LinkedList<>();
        }
        if (mImgList != null && mAdapter != null) {
            mAdapter.setmList(MediaActivity.mSelectedImage);
            mAdapter.notifyDataSetChanged();
        } else {
            mImgList = new ArrayList<>();
            mAdapter = new NoScrollGridViewAdapter(ReleaseNoteActivity.this);
            mAdapter.setmList(MediaActivity.mSelectedImage);
            mAdapter.notifyDataSetChanged();
            nsgv_send_note_gridview.setAdapter(mAdapter);
            nsgv_send_note_gridview.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 删除选中的图片
     */
    public void itemDeleteClick(int index) {
        if (MediaActivity.mSelectedImage.size() > index) {
            MediaActivity.mSelectedImage.remove(index);
        }
        if (mImgList.size() > index) {
            mImgList.remove(index);
            mAdapter.setmList(MediaActivity.mSelectedImage);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_paly://播放
                if (isSendType == 2) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                        } else {
                            play(fileUrl);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (isSendType == 1) {
                    Intent in = new Intent(this, Play_videoActivity.class);
                    in.putExtra("url", fileUrl);
                    startActivity(in);
                }
                break;
            case R.id.tv_queding:
                upLoadVideo();
                break;
        }
    }

    /**
     * 选择拍照或相册
     */
    private void addImg(String str, String str2) {
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(str, ActionSheetDialog.SheetItemColor.Grey,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                int code = ContextCompat.checkSelfPermission(ReleaseNoteActivity.this,
                                        Manifest.permission.CAMERA);
                                if (code != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ReleaseNoteActivity.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            100);
                                } else {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    suijishu = System.currentTimeMillis();
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera", suijishu + PHOTO_FILE_NAME)));
                                    startActivityForResult(intent, 1);
                                }


                            }
                        }).addSheetItem(str2, ActionSheetDialog.SheetItemColor.Grey, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent = new Intent(ReleaseNoteActivity.this, PicChoiceActivity.class);
                intent.putExtra("sendAct", 2);
                intent.putExtra("choiceLimit", 9);
                startActivity(intent);
            }
        }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  1 = 拍照, 2 = 选择照片, 3 = 录音 ,4= 本地音频, 5 =视频录制, 6 = 本地视频
        LogUtils.e("------1111---------" + requestCode);
        if (resultCode == RESULT_OK) {
            LogUtils.e("---------------" + requestCode);
            switch (requestCode) {
                case 1:
                    File tempFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", suijishu + PHOTO_FILE_NAME);
                    LogUtils.e("123321", tempFile.getAbsolutePath());
                    MediaActivity.mSelectedImage.add(tempFile.getAbsolutePath());
                    try {
                        mAdapter.setmList(MediaActivity.mSelectedImage);
                        mAdapter.notifyDataSetChanged();
                        LogUtils.e("123321", bitmap.getByteCount() + "size");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 初始化音乐播放器
     */
    private void play(String fileurl) throws IOException {
        LogUtils.e("fileurl = " + fileurl);
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(fileurl);
        mMediaPlayer.prepare();
        mMediaPlayer.start();//播放
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSendType == 3 || isSendType == 4) {
            initGridViewData();
        }
    }

    private void upLoadVideo() {
        File file = new File(fileUrl);
        if (file == null) {
            Toast.makeText(this, "文件为空", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtils.e("filepath", file.getAbsolutePath());
        OkHttpUtils
                .post()
                .url(api)
                .addParams("code", "weizhi")
                .addFile("file", "001.mp4", file)
                .build()
                .connTimeOut(2000000000)
                .readTimeOut(2000000000)
                .writeTimeOut(2000000000)
                .execute(new MyStringCallback());
    }

    /*网络请求回调*/
    public class MyStringCallback extends StringCallback {
        @Override
        public void inProgress(float progress, long total, int id) {
            super.inProgress(progress, total, id);
//            progressBar.setProgress((int) (progress / total));
            c.setValue((int) (progress * 100));
            LogUtils.e("progress1", progress + "");
            LogUtils.e("progress2", total + "");
            LogUtils.e("progress3", (progress / total) + "");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            LogUtils.e("response", e.getMessage());
            Toast.makeText(ReleaseNoteActivity.this, "错误", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(String response, int id) {
            Toast.makeText(ReleaseNoteActivity.this, response, Toast.LENGTH_SHORT).show();
            LogUtils.e("response", response);
        }
    }
}
