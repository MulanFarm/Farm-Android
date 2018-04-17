package com.daweichang.vcfarm.mode;

import android.content.Context;
import android.text.TextUtils;

import com.daweichang.vcfarm.utils.PrivateFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static android.R.attr.mode;

/**
 * Created by Administrator on 2017/3/13.
 */

public class CameraMode {
    /**
     * id : 1234567890
     * camera_no : abc120
     * archive_id : 6543210
     * name : 摄像头0
     * thumbnail : http://120.27.212.121/farm/upload/img/img_2017031200001.png
     */
    public String id;
    public String archive_id;
    public String name;
    public int is_selected;
    public String thumbnail;
    /**
     * create_date : 2017-04-02 21:01:30
     * user_id : 7f38270bdd7829cec9bb014d4afbbb12
     * is_bound : 1
     * camera_user_name : clive
     * camera_user_pswd : 123456
     * camera_device_pwd : 654321
     */
    public String create_date;
    public String user_id;
    public String is_bound;
    public String camera_no;
    public String camera_user_name;
    public String camera_user_pswd;
    public String camera_device_pwd;

    public boolean hasCamera() {
        if (TextUtils.isEmpty(camera_user_name) || TextUtils.isEmpty(camera_user_pswd) || TextUtils.isEmpty(camera_device_pwd))
            return false;
        return true;
    }

    private static ArrayList<CameraMode> modeList;
    private static final String jsonName = "CameraMode.json";

    public static ArrayList<CameraMode> getMode(Context context) {
        if (modeList == null) {
            try {
                String txt = new PrivateFileUtils(context, jsonName).getString();
                modeList = new Gson().fromJson(txt, new TypeToken<ArrayList<CameraMode>>() {
                }.getType());
            } catch (Exception e) {
            }
            if (modeList == null) modeList = new ArrayList<>();
        }
        return modeList;
    }

    public static void setMode(Context context, ArrayList<CameraMode> modeList) {
        CameraMode.modeList = modeList;
        try {
            String s = new Gson().toJson(mode);
            new PrivateFileUtils(context, jsonName).setString(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(Context context) {
        modeList = null;
        new PrivateFileUtils(context, jsonName).delete();
    }
}