package com.daweichang.vcfarm.mode;

import android.content.Context;
import android.text.TextUtils;

import com.daweichang.vcfarm.utils.PrivateFileUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/9.
 */

public class LoginMode {
    /**
     * user_name : test
     * user_pwd : null
     * name : null
     * email : cliveyuan@foxmail.com
     * phone : null
     * qq : null
     * wechat : null
     * status : normal
     * avatar : null
     * nick_name : null
     * levle : 0
     * gender : null
     * area : null
     * address : null
     * signature : null
     * open_id : null
     * access_token : gVwV2rPkR4sfGmjt4Cj6vL71E67p8oOXitsQ3rgkGWcmuGUX9Vg9cV9D4wzoDtXFzcJkp4Ypk04pYLnVVWYLgA==
     * id : 5230c7d3b5e48e3b21e544d6130c3c24
     * create_date : 2017-03-09 13:40:37
     * start_date : null
     * end_date : null
     */
    public String user_name;
    //public Object user_pwd;
    public String name;
    public String email;
    public Object phone;
    public Object qq;
    public Object wechat;
    public String status;
    public String avatar;//头像
    @SerializedName("nick_name")
    private String nickName;//昵称
    public int levle;
    @SerializedName("gender")
    public int sex;
    public String area;//地区
    public String address;//详细地址
    public String signature;//签名
    public Object open_id;
    @SerializedName("access_token")
    public String token;
    public String id;
    public String create_date;
    public Object start_date;
    public Object end_date;

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        if (TextUtils.isEmpty(nickName)) {
            nickName = email;
            if (TextUtils.isEmpty(nickName)) nickName = "暂无昵称";
        }
        return nickName;
    }

    private static LoginMode loginMode;
    private static final String jsonName = "login.json";

    public static LoginMode getMode(Context context) {
        if (loginMode == null) {
            try {
                String txt = new PrivateFileUtils(context, jsonName).getString();
                loginMode = new Gson().fromJson(txt, LoginMode.class);
            } catch (Exception e) {
                loginMode = new LoginMode();
            }
            if (loginMode == null) loginMode = new LoginMode();
        }
        return loginMode;
    }

    public static void setMode(Context context, LoginMode loginMode) {
        LoginMode.loginMode = loginMode;
        try {
            String s = new Gson().toJson(loginMode);
            new PrivateFileUtils(context, jsonName).setString(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(Context context) {
        loginMode = null;
        new PrivateFileUtils(context, jsonName).delete();
    }

    public LoginMode copy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), LoginMode.class);
    }
}
