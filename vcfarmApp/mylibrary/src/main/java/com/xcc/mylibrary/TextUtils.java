package com.xcc.mylibrary;

/**
 * Created by xcc on 2016/12/8.
 */
public class TextUtils {
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || "null".equals(str) || "NULL".equals(str))
            return true;
        else
            return false;
    }
}
