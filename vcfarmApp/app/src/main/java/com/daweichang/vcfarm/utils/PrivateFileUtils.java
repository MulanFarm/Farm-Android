package com.daweichang.vcfarm.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by yeqiu on 2017/2/23.
 */

public class PrivateFileUtils {
    public PrivateFileUtils(Context context, String jsonName) {
        this.context = context;
        this.jsonName = jsonName;
    }

    private String jsonName;//如"login.json";
    private Context context;

    public String getString() {
        try {
            FileInputStream inputStream = context.openFileInput(jsonName);
            byte bs[] = new byte[1024];
            int idx;
            String txt = "";
            while ((idx = inputStream.read(bs)) > 0) {
                txt += new String(bs, 0, idx);
            }
            return txt;
        } catch (Exception e) {
        }
        return null;
    }

    public void setString(String string) {
        setByte(string.getBytes());
    }

    public byte[] getByte() {
        try {
            FileInputStream inputStream = context.openFileInput(jsonName);
            byte bs[] = new byte[1024];
            int idx;
            String txt = "";
            while ((idx = inputStream.read(bs)) > 0) {
                txt += new String(bs, 0, idx);
            }
            return txt.getBytes();
        } catch (Exception e) {
        }
        return null;
    }

    public void setByte(byte[] bytes) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(jsonName, 0);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        File f = new File(context.getFilesDir(), jsonName);
        if (f.exists()) {
            f.delete();
        }
    }
}
