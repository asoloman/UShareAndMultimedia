package com.dbyc.ushareandmultimedia.mediarelease.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogUtils {

    // 当前日志级别
    private static int Log_level = 5;

    private static int verbose = 4;
    private static int dubug = 3;
    private static int info = 2;
    private static int warn = 1;
    private static int error = 0;

    private static String tag = "wevalue.log";

    public static void v(String msg) {
        if (Log_level > verbose) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Log_level > verbose) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, Object msg) {
        if (Log_level > verbose) {
            Log.i(tag, msg.toString());
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (Log_level > verbose) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String msg) {
        if (Log_level > dubug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Log_level > dubug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (Log_level > dubug) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String msg) {
        if (Log_level > info) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Log_level > info) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, Object msg) {
        if (Log_level > info) {
            Log.i(tag, msg.toString());
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (Log_level > info) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String msg) {
        if (Log_level > warn) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Log_level > warn) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (Log_level > warn) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (Log_level > warn) {
            Log.w(tag, tr);
        }
    }

    public static void e(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Log_level > error && !TextUtils.isEmpty(msg)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Log_level > error) {
            if (!TextUtils.isEmpty(msg)) {
                Log.e(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Log_level > error) {
            Log.e(tag, msg, tr);
        }
    }

    public static void getStackTraceString(Throwable tr) {

        if (Log_level > error) {
            Log.getStackTraceString(tr);
        }
    }
}
