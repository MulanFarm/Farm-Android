package com.daweichang.vcfarm.utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.daweichang.vcfarm.AppVc;
import com.xcc.mylibrary.Sysout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/23.
 */

public class LocationUtils implements AMapLocationListener {
    private static LocationUtils mInstance;
    private List<OnLocationListener> onLocationListenerList;

    private LocationUtils() {
        onLocationListenerList = new ArrayList<>();
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(AppVc.getAppVc());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        }
    }

    public synchronized static LocationUtils getInstance() {
        if (mInstance == null) {
            mInstance = new LocationUtils();
        }
        return mInstance;
    }

    public synchronized void start() {
        if (!isStart)
            mlocationClient.startLocation();//启动定位
        isStart = true;
    }

    public synchronized void stop() {
        if (isStart)
            mlocationClient.stopLocation();
        isStart = false;
    }

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;
    private AMapLocation mLocation;
    private boolean isStart = false;

    public synchronized void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLocation = aMapLocation;
            Sysout.out("定位成功状态:" + isSucc());
            List<OnLocationListener> list = new ArrayList<>();
            list.addAll(onLocationListenerList);
            for (OnLocationListener locationListener : list) {
                locationListener.onLocation(aMapLocation);
            }
        }
    }

    public AMapLocation getLocation() {
        return mLocation;
    }

    public boolean isSucc() {
        if (mLocation != null && mLocation.getErrorCode() == 0) {
            return true;
        }
        return false;
    }

    public synchronized void setOnLocationListener(OnLocationListener onLocationListener) {
        if (!onLocationListenerList.contains(onLocationListener))
            onLocationListenerList.add(onLocationListener);
        start();
    }

    public synchronized void clearOnLocationListener(OnLocationListener onLocationListener) {
        onLocationListenerList.remove(onLocationListener);
        if (onLocationListenerList.size() == 0) stop();
    }

    public interface OnLocationListener {
        void onLocation(AMapLocation location);
    }
}
