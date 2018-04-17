package com.xcc.mylibrary.http;


import java.io.IOException;

/**
 * Created by Administrator on 2016/12/13.
 */

public abstract class DownloaderRet implements BaseClient.RequestBack {
    public int id;

    public void onFailure(String errMsg, IOException e) {
        onFailure(id, errMsg, e);
    }

    public void onCanceled(String msg) {
        onCanceled(id, msg);
    }

    public abstract void onCanceled(int id, String msg);

    public abstract void onFailure(int id, String errMsg, IOException e);
}
