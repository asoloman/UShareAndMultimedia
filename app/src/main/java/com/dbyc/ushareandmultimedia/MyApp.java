package com.dbyc.ushareandmultimedia;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by K on 2017/2/13.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobclickAgent.setDebugMode( true );
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx302bd154a171eba4", "6356e67b017f2ac9b9c1b8ffd15eba7a");
        PlatformConfig.setSinaWeibo("2057322213", "7057698139fbd8f0d43514d1cbc67365");
        PlatformConfig.setQQZone("1105753931", "vCwtUeMaNwMrsi66");
    }
}
