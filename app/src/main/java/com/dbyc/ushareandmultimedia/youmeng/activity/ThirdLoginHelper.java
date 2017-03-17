package com.dbyc.ushareandmultimedia.youmeng.activity;

import android.app.Activity;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by K on 2017/2/13.
 */

public class ThirdLoginHelper {
    Activity activity;
    SHARE_MEDIA shareMedia;

    public ThirdLoginHelper(Activity activity, SHARE_MEDIA shareMedia) {
        this.activity = activity;
        this.shareMedia = shareMedia;
        UMShareAPI.get(activity).getPlatformInfo(activity, shareMedia, authListener);

    }


    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String temp = "";
            for (String key : data.keySet()) {
                temp = temp + key + " : " + data.get(key) + "\n";
            }
            Toast.makeText(activity, temp, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(activity, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(activity, "取消登录了", Toast.LENGTH_SHORT).show();
        }
    };

}
