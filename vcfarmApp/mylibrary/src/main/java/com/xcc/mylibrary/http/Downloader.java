package com.xcc.mylibrary.http;

import android.content.Context;
import android.os.Message;

import com.xcc.mylibrary.Sysout;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载器
 */
public class Downloader extends BaseClient {
    private String fileStr;
    public int id;//任务id
    private long curLen = 0;
    private long totalLen = 0;

    public void setCurLen(long curLen) {
        this.curLen = curLen;
    }

    public void setCurLen(long curLen, long totalLen) {
        this.curLen = curLen;
        this.totalLen = totalLen;
    }

    /**
     * @param context
     * @param fileStr 保存的位置
     */
    public Downloader(Context context, String fileStr) {
        super(context);
        this.fileStr = fileStr;
    }

    public void postAsyn(String url, DownloaderRet downloaderRet) {
        isStop = false;
        Sysout.v("-----url-----", url);
        this.requestBack = downloaderRet;
        downloaderRet.id = id;

        Request.Builder builder1 = new Request.Builder();
        if (curLen != 0) builder1.addHeader("Range", "bytes=" + curLen + "-");
        Request build = builder1
                .url(url)
                .build();
        Headers headers = build.headers();
        Sysout.v("----headers----", headers.toString());
        call = okHttpClient.newCall(build);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                if (e != null && ("Socket closed".equals(e.getMessage()) || "Canceled".equals(e.getMessage()))) {
                    Sysout.v("---网络请求---", "------请求取消-----");
                    handler.sendEmptyMessage(HandlerOnCanceled);
                } else {
                    handler.sendEmptyMessage(HandlerOnFailure);
                }
            }
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    DownMsg downMsg;
                    long l = response.body().contentLength();
                    int len, idx = 0;
                    long currlen = 0;
                    if (totalLen > 0 && curLen > 0) {
                        l = totalLen;
                        currlen = curLen;
                    }
                    InputStream inputStream = response.body().byteStream();
                    //FileOutputStream outputStream = new FileOutputStream(fileStr);
                    RandomAccessFile outputStream = new RandomAccessFile(fileStr, "rwd");
                    outputStream.seek(curLen);//移动到读写位置
                    byte[] bs = new byte[1024];

                    //inputStream.skip(l / 2);
                    while ((len = inputStream.read(bs)) > 0) {
                        if (isStop) break;
                        outputStream.write(bs, 0, len);
                        currlen += len;
                        idx++;
                        if (idx % 200 == 0) {
                            downMsg = new DownMsg(id);
                            downMsg.len = l;
                            downMsg.currlen = currlen;
                            Message msg = new Message();
                            msg.obj = downMsg;
                            msg.what = HandlerOnResponse;
                            handler.sendMessage(msg);
                        }
                    }
                    outputStream.close();
                    inputStream.close();
                    if (isStop) handler.sendEmptyMessage(HandlerOnCanceled);
                    else {
                        Message msg = new Message();
                        downMsg = new DownMsg(id);
                        downMsg.isEnd = true;
                        downMsg.fileStr = fileStr;
                        msg.obj = downMsg;
                        msg.what = HandlerOnResponse;
                        handler.sendMessage(msg);
                    }
                } else onFailure(null, null);
            }
        });
    }

    public JSONObject getJsonObject() {
        return null;
    }

    public String getUrl() {
        return null;
    }

    public Object jsonToMode(String jsonString) {
        return jsonString;
    }

    public class DownMsg extends BaseRet {

        public DownMsg(int id) {
            this.id = id;
        }

        public String fileStr;
        public long len;
        public int id;//NotificationDown 返回的id
        public boolean isEnd = false;
        public long currlen;
    }
}
