package com.example.user.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.GestureDetector;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PView;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.MD5;
import com.p2p.core.utils.MyUtils;
import com.xcc.mylibrary.Sysout;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/5/9.
 * 摄像头全屏测试
 */
public class PlayerActivity extends BaseMonitorActivity {
    @BindView(R.id.pview)
    P2PView pview;
//    P2PView pView;
    @BindView(R.id.textMsga)
    TextView textMsg;
    @BindView(R.id.palyLayouta)
    RelativeLayout palyLayouta;
    private static int mVideoFrameRate = 15;
    public static final String action = "com.daweichang.vcfarm.fragment.FarmFragment.CameraMode";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            if (!TextUtils.isEmpty(json)) {
                CameraMode mode = new Gson().fromJson(json, CameraMode.class);
                setMode(getActivity(), mode);
                cameraLoginMode = null;

                farmHandler.sendEmptyMessage(0);
            }
        }
    };
    private BroadcastReceiver baseReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            Sysout.log("---BroadcastReceiver---", "--baseReceiver--");
            if (intent.getAction().equals(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START)) {
                final MediaPlayer mPlayer = MediaPlayer.getInstance();
                new Thread(new Runnable() {
                    public void run() {
                        MediaPlayer.nativeInit(mPlayer);
                        try {
                            mPlayer.setDisplay(pview);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start(mVideoFrameRate);
                        //setMute(true);
                    }
                }).start();
            }
        }
    };
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(P2P_ACCEPT)) {
                ShowToast.alertShortOfWhite(getActivity(), "监控数据接收");
            } else if (intent.getAction().equals(P2P_READY)) {
                textShow = "点击重连";
                textMsg.setText(textShow);
                ShowToast.alertShortOfWhite(getActivity(), "监控准备,开始监控");
            } else if (intent.getAction().equals(P2P_REJECT)) {
                ShowToast.alertShortOfWhite(getActivity(), "监控挂断 请重试");
                P2PHandler.getInstance().p2pDisconnect();//断开
            }
        }
    };
    private String textShow;
    private static final int farmHandlerTime = 3000;
    private Handler farmHandler = new Handler() {
        public void handleMessage(Message msg) {
            int callType = 2;
            switch (msg.what) {
                case 0: {
                    CameraMode mode = getMode(getActivity());
//                    if (mode == null || !mode.hasCamera()) {
//                        ShowToast.alertShortOfWhite(getActivity(), "whdsxtxx");
//                        return;
//                    }
//                    if (UserConfig.hasWifi() && !NetWorkUtil.isWifiConnected(getActivity())) {
//                        ShowToast.alertShortOfWhite(getActivity(), "R.string.xy_cnbf");
//                        return;
//                    }
                    //TODO 调试
                    mode.camera_user_name = "0810090";
                    mode.camera_user_pswd = "111222";
                    mode.camera_no = "7019032";
                    mode.camera_device_pwd = "abc123";
                    textShow = "开始登录摄像头...";
                    startCamera();
                }
                break;
                case 1: {
                    textShow += "\n登录成功,\n开始连接摄像头...";
                    String rCode1 = cameraLoginMode.P2PVerifyCode1;
                    String rCode2 = cameraLoginMode.P2PVerifyCode2;
                    P2PHandler.getInstance().p2pInit(getActivity(), new P2PListener(), new SettingListener());
                    P2PHandler.getInstance().p2pConnect(camera_user_name, Integer.parseInt(rCode1), Integer.parseInt(rCode2));
                    //pview.fullScreen();
                    farmHandler.sendEmptyMessageDelayed(2, farmHandlerTime);
                }
                break;
                case 2: {
                    textShow += "\n开始初始化摄像头...";
                    initP2PView(7);
                    //setMute(true);  //设置手机静音
                    P2PHandler.getInstance().openAudioAndStartPlaying(callType);//打开音频并准备播放，calllType与call时type一致
                    farmHandler.sendEmptyMessageDelayed(3, farmHandlerTime);
                }
                break;
                case 3: {
                    if (cameraLoginMode != null) {//开启视屏
                        textShow += "\n连接结束,\n开始获取数据流...";
                        pview.setBackgroundColor(0);
                        CameraMode mode = getMode(getActivity());
                        String pwd = P2PHandler.getInstance().EntryPassword(mode.camera_device_pwd);//经过转换后的设备密码
                        boolean call = P2PHandler.getInstance().call(mode.camera_user_name, pwd, true, callType, mode.camera_no, "", "", 2, mode.camera_no);
                        Sysout.v("call-->", call + "");
                        //farmHandler.sendEmptyMessageDelayed(4, farmHandlerTime);
                        return;
                    }
                }
                break;
                case 4: {
//                    MediaPlayer mPlayer = MediaPlayer.getInstance();
//                    mPlayer.start(mVideoFrameRate);
//                    pview.halfScreen();
//                    Intent start = new Intent();
//                    start.setAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
//                    getActivity().sendBroadcast(start);
                }
            }
            textMsg.setText(textShow);
            Sysout.v("--textShow-->", textShow);
        }
    };

    public PlayerActivity getActivity() {
        return this;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_player);
        ButterKnife.bind(this);
        pView = pview;

        IntentFilter filter = new IntentFilter(action);
        getActivity().registerReceiver(receiver, filter);

        filter = new IntentFilter();
        filter.addAction(P2P_REJECT);
        filter.addAction(P2P_ACCEPT);
        filter.addAction(P2P_READY);
        getActivity().registerReceiver(mReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
        getActivity().registerReceiver(baseReceiver, filter);

        farmHandler.sendEmptyMessageDelayed(0, farmHandlerTime);
    }

    public void onDestroy() {
        super.onDestroy();
        P2PHandler.getInstance().p2pDisconnect();//断开
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(baseReceiver);
    }

    @Override
    public int getActivityInfo() {
        return 0;
    }

    @Override
    protected void onGoBack() {

    }

    @Override
    protected void onGoFront() {

    }

    @Override
    protected void onExit() {

    }

    @Override
    protected void onP2PViewSingleTap() {

    }

    @Override
    protected void onCaptureScreenResult(boolean isSuccess, int prePoint) {

    }

    private CameraLoginMode cameraLoginMode;
    private String camera_user_name;

    private void startCamera() {
        CameraMode mode = getMode(getActivity());
        //登录摄像头
        String username = mode.camera_user_name;
        camera_user_name = username;
        if (MyUtils.isNumeric(username)) {
            username = String.valueOf((Integer.parseInt(username) | 0x80000000));
        }
        MD5 md = new MD5();
        Call<CameraLoginMode> baseRetCall = CameraService.getInstance().getServiceUrl().loginCheck(username, md.getMD5ofStr(mode.camera_user_pswd), "1", "3", "3014666");
        baseRetCall.enqueue(new Callback<CameraLoginMode>() {
            public void onResponse(Call<CameraLoginMode> call, Response<CameraLoginMode> response) {
//                if (AppVc.isLoginOut(response)) return;
                CameraLoginMode body = response.body();
                if (body != null) {
                    Sysout.v("-----摄像头登录结果------", body.toString());
                    if (body.isOk()) {
                        cameraLoginMode = body;
                        farmHandler.sendEmptyMessageDelayed(1, farmHandlerTime);
                    } else {
                        ShowToast.alertShortOfWhite(getActivity(), "R.string.sxtdlcg");
                    }
                } else {
                    ShowToast.alertShortOfWhite(getActivity(), "R.string.wangluoyichang");
                }
            }

            public void onFailure(Call<CameraLoginMode> call, Throwable t) {
                ShowToast.alertShortOfWhite(getActivity(), "R.string.wangluoyichang");
            }
        });
    }

    public void initP2PView(int type) {
        pview.setCallBack();
        pview.setGestureDetector(new GestureDetector(getActivity(), new GestureListener(), null, true));
        pview.setDeviceType(type);
    }

    private static CameraMode cameraMode;
    private static final String jsonName = "DCameraMode.json";

    public static CameraMode getMode(Context context) {
//        if (cameraMode == null) {
//            try {
//                String txt = new PrivateFileUtils(context, jsonName).getString();
//                cameraMode = new Gson().fromJson(txt, CameraMode.class);
//            } catch (Exception e) {
//            }
//            if (cameraMode == null) {
        cameraMode = new CameraMode();
        cameraMode.camera_user_name = "0810090";
        cameraMode.camera_user_pswd = "111222";
        cameraMode.camera_no = "7019032";
        cameraMode.camera_device_pwd = "abc123";
//        }
//        }
        return cameraMode;
    }

    public static void setMode(Context context, CameraMode cameraMode) {
        PlayerActivity.cameraMode = cameraMode;
        try {
            String s = new Gson().toJson(cameraMode);
            new PrivateFileUtils(context, jsonName).setString(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
    private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
    private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
    private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
    public static String P2P_ACCEPT = "com.yoosee.P2P_ACCEPT";
    public static String P2P_READY = "com.yoosee.P2P_READY";
    public static String P2P_REJECT = "com.yoosee.P2P_REJECT";
    public boolean isFullScreen = false;
    public boolean isLand = true;// 是否全屏
    public boolean isHalfScreen = true;
    private final int MINX = 50;
    private final int MINY = 25;

    @OnClick(R.id.textMsga)
    public void onViewClicked() {
        P2PHandler.getInstance().p2pDisconnect();//断开


        farmHandler.sendEmptyMessage(0);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // onP2PViewSingleTap();
            return super.onSingleTapConfirmed(e);
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (isLand && !isHalfScreen) {
                if (!isFullScreen) {
                    isFullScreen = true;
                    pview.fullScreen();
                } else {
                    isFullScreen = false;
                    pview.halfScreen();
                }
            }
            return super.onDoubleTap(e);
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            int id = -1;
            float distance = 0;
            boolean ishorizontal = false;
            if ((Math.abs(e2.getX() - e1.getX())) > (Math.abs(e2.getY()
                    - e1.getY()))) {
                ishorizontal = true;
            }

            if (ishorizontal) {
                distance = e2.getX() - e1.getX();
                if (Math.abs(distance) > MyUtils.dip2px(getActivity(), MINX)) {
                    if (distance > 0) {
                        id = USR_CMD_OPTION_PTZ_TURN_RIGHT;
                    } else {
                        id = USR_CMD_OPTION_PTZ_TURN_LEFT;
                    }
                }
            } else {
                distance = e2.getY() - e1.getY();
                if (Math.abs(distance) > MyUtils.dip2px(getActivity(), MINY)) {
                    if (distance > 0) {
                        id = USR_CMD_OPTION_PTZ_TURN_UP;
                    } else {
                        id = USR_CMD_OPTION_PTZ_TURN_DOWN;
                    }
                }
            }
            if (id != -1) {
                MediaPlayer.getInstance().native_p2p_control(id);
            } else {
            }
            return true;
        }
    }
}
