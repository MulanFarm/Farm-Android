package com.xcc.mylibrary;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by xcc on 2016/11/21.
 */
public class Sysout {
    public static final boolean isShow =true;// BuildConfig.DEBUG;
    // private static final boolean isShow = false;

    public static void i(Object object, String msg) {
        if (isShow) {
            String s = object.toString();
            if (TextUtils.isEmpty(s) || TextUtils.isEmpty(msg))
                return;
            Log.i(s, msg);
        }
    }

    public static void e(Object object, String msg) {
        if (isShow) {
            String s = object.toString();
            if (TextUtils.isEmpty(s) || TextUtils.isEmpty(msg))
                return;
            Log.e(object.toString(), msg);
        }
    }

    public static void d(Object object, String msg) {
        if (isShow) {
            String s = object.toString();
            if (TextUtils.isEmpty(s) || TextUtils.isEmpty(msg))
                return;
            Log.d(object.toString(), msg);
        }
    }

    public static void log(String tag, String msg) {
        v(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isShow) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg))
                return;
            Log.v(tag, msg);
        }
    }

    public static void out(String msg) {
        if (isShow && !TextUtils.isEmpty(msg))
            System.err.println(msg);
    }

    public static void println(String msg) {
        if (isShow && !TextUtils.isEmpty(msg))
            System.out.println(msg);
    }

    public static void w(String tag, String msg) {
        if (isShow) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg))
                return;
            Log.w(tag, msg);
        }
    }

    public static void w(String logTag, InterruptedException e) {
        if (isShow) {
            if (TextUtils.isEmpty(logTag) || e == null)
                return;
            Log.w(logTag, e);
        }
    }
}
