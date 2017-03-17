package com.dbyc.ushareandmultimedia.mediarelease.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbyc.ushareandmultimedia.BaseActivity;
import com.dbyc.ushareandmultimedia.R;
import com.dbyc.ushareandmultimedia.mediarelease.adapter.PicChoiceAdapter;
import com.dbyc.ushareandmultimedia.mediarelease.utils.ImageFloder;
import com.dbyc.ushareandmultimedia.mediarelease.utils.LogUtils;
import com.dbyc.ushareandmultimedia.mediarelease.utils.ShowUtil;
import com.dbyc.ushareandmultimedia.mediarelease.weight.ListImageDirPopupWindow;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;


/**
 * 图片选择界面
 */
public class PicChoiceActivity extends BaseActivity implements ListImageDirPopupWindow.OnImageDirSelected {
    private static final int scanFail = 388;//相册无图片
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case scanFail:
                    ShowUtil.showToast(PicChoiceActivity.this, "相册无图片，请选择其他方式上传图片。");
                    finish();
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private int choiceLimit;//选择图片时的限制

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    private List<File> picList;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    private GridView mGirdView;
    private PicChoiceAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;
    int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
//			mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    private TextView tv_queding;

    private ImageView iv_back;

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (picList == null || picList.size() == 0) {
            Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = new ArrayList<String>();
        for (int i = 0; i < picList.size(); i++) {
            mImgs.add(picList.get(i).getName().toString());
        }
//		mImgs.add(0, "拍照");
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
//        Collections.reverse(mImgs);
        LogUtils.e("log", "---mImgDir- list = " + mImgDir.list());
        mAdapter = new PicChoiceAdapter(this, mImgs, R.layout.item_pic_choice_grid, mImgDir.getAbsolutePath(), choiceLimit);
        mGirdView.setAdapter(mAdapter);
        mImageCount.setText(totalCount + "张");
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_choice);
        choiceLimit = getIntent().getIntExtra("choiceLimit", 9);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        initView();
        getImages();
        initEvent();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
//		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String firstImage = null;
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = PicChoiceActivity.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, null
                        /*MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?"*/,
                        /*news String[] { "image/jpeg", "image/png" }*/null,
                        /*MediaStore.Images.Media.DATE_MODIFIED*/null);

//				Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
//					Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);
                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                if (mImgDir == null) {
                    handler.sendEmptyMessage(scanFail);
                    return;
                }

                mCursor.close();
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                // 通知Handler扫描图片完成
                picList = new ArrayList<File>();
                picList = Arrays.asList(mImgDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        String tmp = f.getName().toLowerCase();
                        if (tmp.endsWith(".png") || tmp.endsWith(".jpg")
                                || tmp.endsWith(".jpeg")) {
                            return true;
                        }
                        return false;
                    }
                }));
                Collections.sort(picList, new FileComparator());
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();

    }

    /**
     * 初始化View
     */
    private void initView() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        tv_queding = (TextView) findViewById(R.id.tv_queding);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

        final int sendAct = getIntent().getIntExtra("sendAct", -1);
        tv_queding.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendAct == 1) {
                    if (MediaActivity.mSelectedImage != null && MediaActivity.mSelectedImage.size() > 0) {
                        Intent intent = new Intent(PicChoiceActivity.this, ReleaseNoteActivity.class);
                        intent.putExtra("isSendType", 3);
                        startActivity(intent);
                        finish();
                    } else {
                        ShowUtil.showToast(PicChoiceActivity.this, "请选择图片!");
                    }
                } else if (sendAct == 2) {
                    if (MediaActivity.mSelectedImage != null && MediaActivity.mSelectedImage.size() > 0) {
                        finish();
                    } else {
                        ShowUtil.showToast(PicChoiceActivity.this, "请选择图片!");
                    }
                } else if (sendAct == 3) {
                }
            }
        });

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void selected(ImageFloder floder) {
        mImgDir = new File(floder.getDir());
//		mImgs = Arrays.asList();
        mImgs = new ArrayList<String>();
        picList = new ArrayList<>();
        picList = Arrays.asList(mImgDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String tmp = f.getName().toLowerCase();
                if (tmp.endsWith(".png") || tmp.endsWith(".jpg") || tmp.endsWith(".jpeg")) {
                    return true;
                }
                return false;
            }
        }));
        Collections.sort(picList, new FileComparator());
        for (int i = 0; i < picList.size(); i++) {
            mImgs.add(picList.get(i).getName().toString());
        }
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
//        Collections.reverse(mImgs);
//        Collections.sort();
        mAdapter = new PicChoiceAdapter(this, mImgs, R.layout.item_pic_choice_grid, mImgDir.getAbsolutePath(), choiceLimit);
        mGirdView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    /**
     * 调用相机
     */
    public void showCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    private static final String PHOTO_FILE_NAME = "logo.jpg";

    /**
     * @author fengyaru TODO 简单描述该方法的实现功能
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                return;
            }
            switch (requestCode) {
                case 2:
                    try {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        String s = saveMyBitmap(getSDPath() + "/demo/" + PHOTO_FILE_NAME, bitmap);
//                        MediaActivity.mSelectedImage.add(s);
                        Intent it = new Intent();
                        setResult(RESULT_OK, it);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * Title:  saveMyBitmap<br>
     * Description: TODO  保存图片<br>
     *
     * @param bitName
     * @param mBitmap
     * @since JDK 1.7
     */
    public String saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File(bitName);
        File path = new File(getSDPath() + "/demo/");
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    /**
     * Title: getSDPath<br>
     * Description: TODO 获取SD卡路径<br>
     *
     * @return
     * @since JDK 1.7
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() < rhs.lastModified()) {
                return 1;//最后修改的照片在前
            } else {
                return -1;
            }
        }

    }
}
