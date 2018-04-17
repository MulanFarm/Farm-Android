package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.utils.AMapUtil;
import com.daweichang.vcfarm.utils.LocationUtils;
import com.daweichang.vcfarm.widget.ShowToast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import overlay.DrivingRouteOverlay;

/**
 * Created by Administrator on 2017/3/23.
 * 校车轨迹
 */
public class MapActivity extends BaseActivity implements LocationSource, LocationUtils.OnLocationListener, RouteSearch.OnRouteSearchListener {
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.textName)
    TextView textName;
    @BindView(R.id.textTel)
    TextView textTel;
    private AMap aMap;
    private static final double dIdx[] = new double[]{41.890090, 117.276375};
    private RouteSearch routeSearch;

    protected int finishBtn() {
        return R.id.imgBack;
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        // aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        LatLng latLng = new LatLng(dIdx[0], dIdx[1]);
        //BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.xiaocheguiji));
        Marker marker = aMap.addMarker(new MarkerOptions().position(latLng));

        getData();

        //划线
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    @OnClick({R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin://导航
                Intent intent;
                if (isAvilible(this, "com.autonavi.minimap")) {
                    try {
                        intent = Intent.getIntent("androidamap://navi?sourceApplication=木兰农场&poiname=我的目的地&lat=" + dIdx[0] + "&lon=" + dIdx[1] + "&dev=0");
                        startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    ShowToast.alertShortOfWhite(this, "您尚未安装高德地图");
                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        LocationUtils.getInstance().clearOnLocationListener(this);
    }

    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private OnLocationChangedListener onLocationChangedListener;

    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
        LocationUtils.getInstance().setOnLocationListener(this);
    }

    public void deactivate() {
    }

    public void onLocation(AMapLocation location) {
        if (location != null && location.getErrorCode() == 0) {
            sendLocation = location;
            onLocationChangedListener.onLocationChanged(location);
            LatLonPoint startPoint = AMapUtil.convertToLatLonPoint(new LatLng(location.getLatitude(), location.getLongitude()));
            LatLonPoint endPoint = AMapUtil.convertToLatLonPoint(new LatLng(dIdx[0], dIdx[1]));
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault,
                    null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    private AMapLocation sendLocation;

    private void getData() {// 获取数据
//        String token = UserConfig.getToken();
//        Call<SchoolBusRet> baseRetCall = BaseService.getInstance().getServiceUrl().getSchoolBus(token);
//        baseRetCall.enqueue(new Callback<SchoolBusRet>() {
//            public void onResponse(Call<SchoolBusRet> call, Response<SchoolBusRet> response) {
//                SchoolBusRet body = response.body();
//                if (body != null) {
//                    if (body.isOk()) {
//                        List<SchoolBusMode> schoolBus = body.schoolBus;
//                        aMap.clear();
//                        if (schoolBus != null) {
//                            if (sendLocation != null)
//                                onLocationChangedListener.onLocationChanged(sendLocation);
//                            for (int i = 0; i < schoolBus.size(); i++) {
//                                SchoolBusMode schoolBusMode = schoolBus.get(i);
////                                LatLng latLng = new LatLng(39.936404, 116.385871);
////                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.xiangce1));
////                               aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker").icon(bitmapDescriptor));
//                                LatLng latLng = new LatLng(schoolBusMode.lat, schoolBusMode.lng);
//                                aMap.addMarker(new MarkerOptions().position(latLng).title("老师").snippet(schoolBusMode.users_name));
//                            }
//                        }
//                    } else ShowToast.alertShortOfWhite(MapActivity.this, body.msg);
//                } else onFailure(null, null);
//            }
//
//            public void onFailure(Call<SchoolBusRet> call, Throwable t) {
//                ShowToast.alertShortOfWhite(MapActivity.this, R.string.wangluochucuo);
//            }Drive Route Searched
//        });
    }

    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }

    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
//        if (i == 1000) {
        List<DrivePath> paths = driveRouteResult.getPaths();
        DrivePath drivePath = paths.get(0);
        aMap.clear();
        DrivingRouteOverlay routeOverlay = new DrivingRouteOverlay(this, aMap,
                drivePath, driveRouteResult.getStartPos(),
                driveRouteResult.getTargetPos(), null);
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
        routeOverlay.zoomToSpan();

//            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
//                    getActivity(), aMap, drivePath, driveRouteResult.getStartPos(),
//                    driveRouteResult.getTargetPos());
//            drivingRouteOverlay.removeFromMap();
//            drivingRouteOverlay.addToMap();
//            drivingRouteOverlay.zoomToSpan();
//        }
    }

    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
    }

    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

}
