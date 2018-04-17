package com.daweichang.vcfarm.utils;

import android.content.SharedPreferences;

import com.daweichang.vcfarm.AppVc;


/**
 * Created by yeqiu on 2017/2/21.
 */
public class UserConfig {
    private static final String UserMsg = "AppVc.vcfarm";
    private static final String Setting = "AppVc.vcfarm2";

    //>>>-----------------------------用户设置
    public static boolean isLogin() {//是否登录
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        return sp.getBoolean("isLogin", false);
    }

    public static void setLogin(boolean isLogin) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        sp.edit().putBoolean("isLogin", isLogin).commit();
    }

    //    用户token
    public static String getToken() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        String string = sp.getString("token", "token");
        return string;
    }

    public static void setToken(String token) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        sp.edit().putString("token", token).commit();
    }

    public static void setEmailCaptcha(String emailCaptcha) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        sp.edit().putString("emailCaptcha", emailCaptcha).commit();
    }

    public static String getEmailCaptcha() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        return sp.getString("emailCaptcha", null);
    }

    public static void setCodeTime(long CodeTime) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        sp.edit().putLong("CodeTime", CodeTime).commit();
    }

    public static long getCodeTime() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        return sp.getLong("CodeTime", 0);
    }

    public static void setSignInTime(int signInTime) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        sp.edit().putInt("SignInTime", signInTime).commit();
    }

    public static int getSignInTime() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        return sp.getInt("SignInTime", 0);
    }
    //<<<-----------------------------用户设置

    //>>>-----------------------------功能配置
    public static void setMaxMsgId(long maxMsgId) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        sp.edit().putLong("MaxMsgId", maxMsgId).commit();
    }

    public static long getMaxMsgId() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        return sp.getLong("MaxMsgId", 0);
    }
    public static boolean hasWifi() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        return sp.getBoolean("hasWifi", false);
    }

    public static void setHasWifi(boolean hasWifi) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        sp.edit().putBoolean("hasWifi", hasWifi).commit();
    }
    public static boolean isWelcome() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        return sp.getBoolean("welcome", false);
    }

    public static void setWelcome(boolean welcome) {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        sp.edit().putBoolean("welcome", welcome).commit();
    }
    //<<<-----------------------------功能配置

    public static void clear() {
        SharedPreferences sp = AppVc.getAppVc().getSharedPreferences(Setting, 0);
        sp.edit().clear().commit();
        sp = AppVc.getAppVc().getSharedPreferences(UserMsg, 0);
        sp.edit().clear().commit();
    }
}
