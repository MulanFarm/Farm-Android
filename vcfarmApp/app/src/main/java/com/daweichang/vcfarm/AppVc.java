package com.daweichang.vcfarm;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDex;

import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.mode.WalletMode;
import com.daweichang.vcfarm.utils.FileOperateUtil;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.ImgSelectConfig;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.wxapi.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xcc.mylibrary.OtherUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import retrofit2.Response;

/**
 * Created by xcc on 2017/2/21.
 */
public class AppVc extends Application {
    public static final String terminalType = "android";
    public static final String Refresh = "com.daweichang.vcfarm.Refresh";
    public static final String Login = "com.daweichang.vcfarm.Login";
    public static final String WXLogin = "com.daweichang.vcfarm.WXLogin";
    public static final String WXLoginSuccess = "com.daweichang.vcfarm.WXLoginSuccess"; //微信登录授权成功
    private static AppVc appVc;
    private boolean isLogin = false;
    private String ip;
    public static IWXAPI mWxApi;

    public static AppVc getAppVc() {
        return appVc;
    }

    public void onCreate() {
        super.onCreate();

        appVc = this;

        init();

        registToWX();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void init() {
        isLogin = UserConfig.isLogin();
        //isLogin = true;//TODO 测试使用
        CrashHandler.getInstance().init(this);
        ImgSelectConfig.init(this);
    }

    private void registToWX() {
        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
        mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        mWxApi.registerApp(Constants.APP_ID);
    }

    public static boolean isLoginOut(Response response) {
        if (403 == response.code()) {
            AppVc appVc = getAppVc();
            appVc.setLogin(false);
        } else return false;
        return true;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
        UserConfig.setLogin(isLogin);

        Intent intent = new Intent(AppVc.Login);
        intent.putExtra("isLogin", isLogin);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLoginMode(LoginMode loginMode) {
        LoginMode.setMode(this, loginMode);
        UserConfig.setToken(loginMode.token);
    }

    /**
     * 清除缓存和清除数据
     *
     * @param i 1缓存,2数据
     */
    public void clearConfigAndData(int i) {
        if ((i & 1) == 1) {//删缓存
            GlideUtils.clearMemory(this);
            FileOperateUtil.deleteDirFile();
        }
        if ((i & 2) == 2) {//删数据
            UserConfig.clear();
            LoginMode.clear(this);
            WalletMode.clear(this);
        }
    }

    public String getIp() {
        String ip = "";
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            ip = getLocalIpAddress();
        } else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
        }
        if (ip != null) {
            this.ip = ip;
        } else {
            ip = this.ip;
        }
        return ip;
    }

    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<?> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<?> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr
                            .nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //获取缓存大小
    public String getDataSize() {
        long dataSize = FileOperateUtil.getDataSize();
        double d = 0;
        if (dataSize > 1024) {
            d = dataSize / 1024.0;
        }
        if (d > 1024) {
            d = d / 1024.0;
        } else {
            return OtherUtils.doubleDot2(d) + "KB";
        }
        if (d > 1024) {
            d = d / 1024.0;
        } else {
            return OtherUtils.doubleDot2(d) + "MB";
        }
        return OtherUtils.doubleDot2(d) + "GB";
    }
}
