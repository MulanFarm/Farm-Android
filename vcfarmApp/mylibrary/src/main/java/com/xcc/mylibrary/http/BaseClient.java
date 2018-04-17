package com.xcc.mylibrary.http;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xcc.mylibrary.Sysout;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseClient<E> {
    private static Dispatcher dispatcher;
    protected OkHttpClient okHttpClient;
    protected Handler handler;
    protected Context context;

    public static Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher(new ScheduledThreadPoolExecutor(5));
        }
        return dispatcher;
    }

    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(getDispatcher());
        builder.connectTimeout(600, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient;
    }

    public BaseClient(Context context) {
        handler = new ClientHandler(Looper.getMainLooper());
        okHttpClient = getOkHttpClient();

//        if (context == null) context = AppQfm.getAppQfm();
        this.context = context;
    }

    //    protected String rid;
//    public static String imei;
//    public static String osVersion = Build.VERSION.SDK;
//    public static String appVersion;
//    public static int appType = 2;
//    public static String sign_type = "MD5";
//    public static boolean addSign = true;
    public static final int HandlerOnResponse = 1;//请求成功
    public static final int HandlerOnFailure = 2;//请求失败
    public static final int HandlerOnCanceled = 3;//请求取消


    public void postRequest(RequestBack requestBack) {
        postAsyn(getUrl(), requestBack);
    }

    public void getRequest(RequestBack requestBack) {
        getAsyn(getUrl(), requestBack);
    }

    /**
     * 异步的post请求
     */
    public void postAsyn(String url, RequestBack requestBack) {
        isStop = false;
        Sysout.v("-----url-----", url);
        this.requestBack = requestBack;

        RequestBody formBody = new FormBody.Builder()
                //.add("size", "10")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(callback);

//        FormEncodingBuilder builder = new FormEncodingBuilder();
//        RequestBody requestBody = builder.build();
//        Request.Builder builder1 = new Request.Builder();
//        Request build = builder1
//                .url(url)
//                .post(requestBody)
//                .build();
////        Headers headers = build.headers();
////        Sysout.v("----headers----", headers.toString());
//        call = okHttpClient.newCall(build);
//        call.enqueue(callback);
    }

    /**
     * 异步的get请求
     */
    public void getAsyn(String url, RequestBack requestBack) {
        isStop = false;
        Sysout.v("-----url-----", url);
        this.requestBack = requestBack;
        Request.Builder builder1 = new Request.Builder();
        Request build = builder1
                .url(url)
                .get()
                .build();
        call = okHttpClient.newCall(build);
        call.enqueue(callback);
    }

    Callback callback = new Callback() {
        public void onFailure(Call call, IOException e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            msg.obj = e;
            if (e != null && ("Socket closed".equals(e.getMessage()) || "Canceled".equals(e.getMessage()))) {
                Sysout.v("---网络请求---", "------请求取消-----");
//                bundle.putString("text", context.getResources().getString(R.string.qingqiuquxiao));
                bundle.putString("text", "请求取消");
                msg.what = HandlerOnCanceled;
            } else {
                bundle.putString("text", "网络错误");
                msg.what = HandlerOnFailure;
            }
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                Message msg = new Message();
                String jsonString = response.body().string();
                Sysout.out("---json---" + jsonString);
                try {
                    E e = jsonToMode(jsonString);
                    msg.obj = e;
                    msg.what = HandlerOnResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.obj = e;
                    msg.what = HandlerOnFailure;
                }
                handler.sendMessage(msg);
            } else onFailure(null, null);
        }

//        public void onFailure(Request request, IOException e) {
//            Message msg = new Message();
//            Bundle bundle = new Bundle();
//            msg.obj = e;
//            if (e != null && ("Socket closed".equals(e.getMessage()) || "Canceled".equals(e.getMessage()))) {
//                Sysout.v("---网络请求---", "------请求取消-----");
////                bundle.putString("text", context.getResources().getString(R.string.qingqiuquxiao));
//                bundle.putString("text", "请求取消");
//                msg.what = HandlerOnCanceled;
//            } else {
//                bundle.putString("text", "网络错误");
//                msg.what = HandlerOnFailure;
//            }
//            msg.setData(bundle);
//            handler.sendMessage(msg);
//        }
//        public void onResponse(Response response) throws IOException {
//            if (response.isSuccessful()) {
//                Message msg = new Message();
//                String jsonString = response.body().string();
//                Sysout.out("---json---" + jsonString);
//                try {
//                    E e = jsonToMode(jsonString);
//                    msg.obj = e;
//                    msg.what = HandlerOnResponse;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    msg.obj = e;
//                    msg.what = HandlerOnFailure;
//                }
//                handler.sendMessage(msg);
//            } else onFailure(null, null);
//        }
    };
    /**
     * 获取参数
     */
//    public abstract JSONObject getJsonObject();

    /**
     * 获取链接
     */
    public abstract String getUrl();

    /**
     * 转换到object
     */
    public abstract E jsonToMode(String jsonString);

    protected Call call;
    public boolean isStop;
    protected RequestBack requestBack;

    public RequestBack getRequestBack() {
        return requestBack;
    }

    public void stop() {
        if (call != null) call.cancel();
        isStop = true;
    }

    public class ClientHandler extends Handler {
        public ClientHandler(Looper mainLooper) {
            super(mainLooper);
        }

        public ClientHandler() {
        }

        public void handleMessage(Message msg) {
            if (requestBack == null) return;
            Bundle data = msg.getData();
            switch (msg.what) {
                case HandlerOnResponse: {
                    requestBack.onResponse(msg.obj);
                }
                break;
                case HandlerOnFailure: {
                    IOException e = (IOException) msg.obj;
                    String text = data.getString("text");
                    requestBack.onFailure(text, e);
                }
                break;
                case HandlerOnCanceled: {
                    String text = data.getString("text");
                    requestBack.onCanceled(text);
                }
                break;
            }
        }
    }

    public interface RequestBack<E> {
        void onFailure(String errMsg, IOException e);

        void onCanceled(String msg);

        void onResponse(E baseRet);
    }
}
