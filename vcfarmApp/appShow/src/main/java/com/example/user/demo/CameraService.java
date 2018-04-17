package com.example.user.demo;


import com.xcc.mylibrary.NetWorkUtil;
import com.xcc.mylibrary.Sysout;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class CameraService {
    private static final String URL = "http://api1.cloudlinks.cn/";
//    private static final String URL = hostIp + "Users/LoginCheck.ashx";

    private static CameraService baseService;

    public static CameraService getInstance() {
        if (baseService == null) {
            synchronized (CameraService.class) {
                if (baseService == null) baseService = new CameraService();
            }
        }
        return baseService;
    }

    private CameraService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache);

        if (Sysout.isShow) {
            logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                public void log(String message) {
                    Sysout.i("OkHttpClient", "OkHttpMessage:" + message);
                }
            });
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logInterceptor);
        }
        client = builder.build();
    }

    private HttpLoggingInterceptor logInterceptor;
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(MyApp.app)) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
    private File httpCacheDirectory = new File(MyApp.app.getCacheDir(), "Vcfarm");
    private int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private Cache cache = new Cache(httpCacheDirectory, cacheSize);
    private OkHttpClient client;
    private CameraServiceUrl serviceUrl = null;

    public synchronized CameraServiceUrl getServiceUrl() {
        if (serviceUrl == null) {
            serviceUrl = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(CameraServiceUrl.class);
        }
        return serviceUrl;
    }
}
