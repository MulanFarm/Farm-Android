package com.daweichang.vcfarm.utils;

/**
 * Created by Administrator on 2017/3/9.
 */

public class Key {
    private static final String KEY_ = "F$0.%a~+r^#=`M|?";

//    static {
//        System.loadLibrary("hyclibkey");
//    }

    /**
     * @param context 此处必须是Context
     * @return
     */
    //private native String getKey(Object context);// 方法名不能变
    public static String getKey(Object context) {
        return KEY_;
    }
}