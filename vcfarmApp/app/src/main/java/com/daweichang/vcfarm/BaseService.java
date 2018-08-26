package com.daweichang.vcfarm;


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
public class BaseService {
    /**
     * 联调地址ip更改为:  120.27.212.121
     api文档不再往群里发, 实时地址为:
     http://farmapp.daweichang.com/bin/doc/farm_api.pdf
     */
    // 线上地址
    // public static final String URL = "http://192.168.1.205:911/index.php/Unified/Login/login";
    // 测试地址 旧103.239.247.30:8090
    public static final String URL = "http://farmapp.daweichang.com/controller/api/";
    public static final String ImgURL = "http://farmapp.daweichang.com/controller/api/";

    private static BaseService baseService;

    public static BaseService getInstance() {
        if (baseService == null) {
            synchronized (BaseService.class) {
                if (baseService == null) baseService = new BaseService();
            }
        }
        return baseService;
    }

    private BaseService() {
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
//            = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//        public void log(String message) {
//            Sysout.i("OkHttpClient", "OkHttpMessage:" + message);
//        }
//    });
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(AppVc.getAppVc())) {
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
    private File httpCacheDirectory = new File(AppVc.getAppVc().getCacheDir(), "Vcfarm");
    private int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private Cache cache = new Cache(httpCacheDirectory, cacheSize);
    private OkHttpClient client;
//    = new OkHttpClient.Builder()
//            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//            .addInterceptor(logInterceptor)
//            .cache(cache)
//            .build();

    private ServiceUrl serviceUrlCache = null;
    private ServiceUrl serviceUrl = null;

    /**
     * @param isCache 是否缓存
     * @return
     */
    public synchronized ServiceUrl getServiceUrl(boolean isCache) {
        if (isCache) return getServiceUrl();
        if (serviceUrl == null) {
            serviceUrl = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ServiceUrl.class);
        }
        return serviceUrl;
    }

    public synchronized ServiceUrl getServiceUrl() {
        if (serviceUrlCache == null) {
            serviceUrlCache = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ServiceUrl.class);
        }
        return serviceUrlCache;
    }
}
