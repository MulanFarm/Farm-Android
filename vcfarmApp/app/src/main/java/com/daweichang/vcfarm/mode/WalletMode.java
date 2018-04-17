package com.daweichang.vcfarm.mode;

import android.content.Context;

import com.daweichang.vcfarm.utils.PrivateFileUtils;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/3/29.
 */

public class WalletMode {
    /**
     * balance : 0
     * status : 0
     */
    public double balance;
    public int status;//status: 0正常,1 冻结

    private static WalletMode mode;
    private static final String jsonName = "WalletMode.json";

    public static WalletMode getMode(Context context) {
        if (mode == null) {
            try {
                String txt = new PrivateFileUtils(context, jsonName).getString();
                mode = new Gson().fromJson(txt, WalletMode.class);
            } catch (Exception e) {
                mode = new WalletMode();
            }
            if (mode == null) mode = new WalletMode();
        }
        return mode;
    }

    public static void setMode(Context context, WalletMode mode) {
        WalletMode.mode = mode;
        try {
            String s = new Gson().toJson(mode);
            new PrivateFileUtils(context, jsonName).setString(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(Context context) {
        mode = null;
        new PrivateFileUtils(context, jsonName).delete();
    }
}
