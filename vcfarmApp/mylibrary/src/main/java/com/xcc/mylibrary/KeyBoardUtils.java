package com.xcc.mylibrary;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtils {
    /**
     * 关闭软键盘
     */
    public static void closeKey(Activity context) {
        InputMethodManager im = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (context.getCurrentFocus() != null)
            im.hideSoftInputFromWindow(context.getCurrentFocus()
                            .getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 打开软键盘
     */
    public static void openKey(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 设置EditText焦点
    public static void setFocusab(Activity activity, View et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        et.findFocus();
        openKey(activity);
    }
}
