package com.dbyc.ushareandmultimedia.mediarelease.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dbyc.ushareandmultimedia.BaseActivity;
import com.dbyc.ushareandmultimedia.R;
import com.dbyc.ushareandmultimedia.mediarelease.utils.ShowUtil;
import com.dbyc.ushareandmultimedia.mediarelease.weight.ActionSheetDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MediaActivity extends BaseActivity implements View.OnClickListener {
    PopupWindow promptBoxPopupWindow;
    private Button btn_Choice;
    private int isClickType;
    public static List<String> mSelectedImage = new LinkedList<>();
    long suijishu;
    private ImageView iv_dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        initView();
    }

    private void initView() {
        btn_Choice = (Button) findViewById(R.id.btn_Choice);
        btn_Choice.setOnClickListener(this);
        iv_dog = (ImageView) findViewById(R.id.iv_dog);
        iv_dog.setOnClickListener(this);
    }

    public void initpopu() {
        View prompt_box;
        // 空白区域
        prompt_box = getLayoutInflater().inflate(R.layout.popu_send_note, null);
        LinearLayout ll_send_video = (LinearLayout) prompt_box.findViewById(R.id.ll_send_video);
        LinearLayout ll_send_audio = (LinearLayout) prompt_box.findViewById(R.id.ll_send_audio);
        LinearLayout ll_send_text = (LinearLayout) prompt_box.findViewById(R.id.ll_send_text);
        LinearLayout ll_send_img = (LinearLayout) prompt_box.findViewById(R.id.ll_send_img);
        ImageView iv_send_quxiao = (ImageView) prompt_box.findViewById(R.id.iv_send_quxiao);
        TextView tv_nong_li_nian = (TextView) prompt_box.findViewById(R.id.tv_nong_li_nian);
        TextView tv_day_popu = (TextView) prompt_box.findViewById(R.id.tv_day_popu);
        TextView tv_week_popu = (TextView) prompt_box.findViewById(R.id.tv_week_popu);
        TextView tv_nianyue = (TextView) prompt_box.findViewById(R.id.tv_nianyue);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat sdf_2 = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_3 = new SimpleDateFormat("EEEE");
        String dateNowStr = sdf.format(d);
        String dateNowStr_2 = sdf_2.format(d);
        String dateNowStr_3 = sdf_3.format(d);
        tv_nong_li_nian.setText(ShowUtil.getYear());
        tv_nianyue.setText(dateNowStr);
        tv_day_popu.setText(dateNowStr_2);
        tv_week_popu.setText(dateNowStr_3);
        prompt_box.setOnClickListener(new View.OnClickListener() {
            // 空白区域
            @Override
            public void onClick(View v) {
                promptBoxPopupWindow.dismiss();
            }
        });
        ll_send_video.setOnClickListener(new View.OnClickListener() {
            // 发布视频
            @Override
            public void onClick(View v) {
                addImg("拍摄视频", "本地选择", 3);
                promptBoxPopupWindow.dismiss();
            }
        });
        ll_send_audio.setOnClickListener(new View.OnClickListener() {
            // 发布音乐
            @Override
            public void onClick(View v) {
                addImg("录音", "本地选择", 2);
                promptBoxPopupWindow.dismiss();
            }
        });
        ll_send_img.setOnClickListener(new View.OnClickListener() {
            // 发布图片
            @Override
            public void onClick(View v) {
                addImg("手机拍照", "相册选择", 1);
                promptBoxPopupWindow.dismiss();
            }
        });
        ll_send_text.setOnClickListener(new View.OnClickListener() {
            // 发布文本
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaActivity.this, ReleaseNoteActivity.class);
                intent.putExtra("isSendType", 4);
                startActivity(intent);
                promptBoxPopupWindow.dismiss();
            }
        });
        iv_send_quxiao.setOnClickListener(new View.OnClickListener() {
            // 取消  图片按钮
            @Override
            public void onClick(View v) {
                promptBoxPopupWindow.dismiss();
            }
        });

        promptBoxPopupWindow = new PopupWindow(prompt_box, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        promptBoxPopupWindow.setFocusable(true);
        // 设置弹出动画
        promptBoxPopupWindow.setAnimationStyle(R.style.ActionSheetDialogStyle);
        // 设置popupWindow背景图片(只能通过popupWindow提供的返回键返回)
        ColorDrawable dw = new ColorDrawable(0x90ffffff);
        promptBoxPopupWindow.setBackgroundDrawable(dw);
        promptBoxPopupWindow.setOutsideTouchable(true);
        promptBoxPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }


    public void addImg(String str, String str2, final int isType) {
        isClickType = isType;
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(str, ActionSheetDialog.SheetItemColor.Grey,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent;
                                int code = ContextCompat.checkSelfPermission(MediaActivity.this,
                                        Manifest.permission.CAMERA);
                                switch (isType) {
                                    case 1://拍照
                                        if (code != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MediaActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                                        } else {
                                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            suijishu = System.currentTimeMillis();
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera", suijishu + "demoImg")));
                                            startActivityForResult(intent, 1);
                                        }

                                        break;
                                    case 2://录音ll_add_sendnote
                                        //音频录制
//                                        intent = news Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                                        intent = new Intent(MediaActivity.this, AudioActivity.class);
                                        startActivityForResult(intent, 3);
                                        break;
                                    case 3://录制视频 5
                                        if (code != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MediaActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                                        } else {
                                            intent = new Intent(MediaActivity.this, NewRecordVideoActivity.class);
                                            startActivityForResult(intent, 5);
                                        }
                                        break;
                                }
                            }
                        }).addSheetItem(str2, ActionSheetDialog.SheetItemColor.Grey, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent;
                switch (isClickType) {
                    case 1://本地选择照片
                        if (mSelectedImage == null) {
                            mSelectedImage = new LinkedList<>();
                        }
                        intent = new Intent(MediaActivity.this, PicChoiceActivity.class);
                        intent.putExtra("sendAct", 1);
                        intent.putExtra("choiceLimit", 9);
                        startActivity(intent);
                        break;
                    case 2://本地选择音频 4
                        intent = new Intent(MediaActivity.this, GetvAudioAndVideoActivity.class);
                        intent.putExtra("type", "audio");
                        startActivity(intent);
                        break;
                    case 3://本地选择 视频 6
                        intent = new Intent(MediaActivity.this, GetvAudioAndVideoActivity.class);
                        intent.putExtra("type", "video");
                        startActivity(intent);
                        break;
                }
            }
        }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent;
            switch (requestCode) {
                case 1:
                    File tempFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", suijishu + "demoImg");
                    Bitmap b;
                    if (mSelectedImage == null) {
                        mSelectedImage = new LinkedList<>();
                    }
//                    if (WeValueApplication.phoneName.equals("samsung")) {
////                        b = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
////                        b = ZipBitmapUtil.reduce(b, width, height, true);
////                        b = ZipBitmapUtil.rotate(b, 90);
////                        File file = saveMyBitmap(Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + suijishu + "__1" + "demoImg", b);
////                        mSelectedImage.add(file.getAbsolutePath());
//                    } else {
                    mSelectedImage.add(tempFile.getAbsolutePath());
//                    }
//                    LogUtils.e("123321", tempFile.getAbsolutePath());
                    intent = new Intent(MediaActivity.this, ReleaseNoteActivity.class);
                    intent.putExtra("isSendType", 3);
                    startActivity(intent);
                    break;
                case 3:
                    Uri uri = data.getData();
//                    LogUtils.e("uri = " + uri.getEncodedPath());
                    break;
                case 5:
                    String videoUrl = data.getStringExtra("videoUrl");
                    intent = new Intent(MediaActivity.this, ReleaseNoteActivity.class);
                    intent.putExtra("isSendType", 1);
                    intent.putExtra("fileUrl", videoUrl);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaActivity.mSelectedImage.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Choice:
                initpopu();
                break;
        }
    }
}
