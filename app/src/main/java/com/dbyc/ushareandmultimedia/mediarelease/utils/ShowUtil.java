package com.dbyc.ushareandmultimedia.mediarelease.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowUtil {
    private static Toast mToast;

    public static void showToast(Context context, String text) {
        cancelLastToast();
        mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /*取消上一次toast*/
    public static void cancelLastToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }


    public static void showToast(Context context, int id) {

        mToast = Toast.makeText(context, id, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static String getYear() {

        String[] array = {"甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉",
                "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛己", "壬午", "癸未",
                "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳",
                "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸丑",
                "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
                "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"};
        String[] shengxiaoArray = {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};
        long tiem = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateFormat_month = new SimpleDateFormat("MM");
        SimpleDateFormat simpleDateFormat_day = new SimpleDateFormat("dd");

        String dateStr = simpleDateFormat.format(new Date(tiem));
        String dateStr_month = simpleDateFormat_month.format(new Date(tiem));
        String dateStr_day = simpleDateFormat_day.format(new Date(tiem));
        int year = Integer.valueOf(dateStr);
        int month = Integer.valueOf(dateStr_month);
        int day = Integer.valueOf(dateStr_day);


//        LogUtils.e("--农历年--" + LunarCalendar.solarToLunar(year, month, day)[0] + "-" + LunarCalendar.solarToLunar(year, month, day)[1] + "-" + LunarCalendar.solarToLunar(year, month, day)[2]);
        int yushu = year % 60;
        int shengxiao = year % 12;
        int index;
        if (yushu > 3) {
            index = yushu - 4;
        } else {
            index = yushu + 60 - 4;
        }
        return array[index] + shengxiaoArray[shengxiao] + "年";
    }


    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context mContext, String text, int duration) {

        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(r, duration);

        mToast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

}
