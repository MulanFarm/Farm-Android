package com.daweichang.vcfarm.fragment;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseFragment;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.CameraService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.ArchivesDetailsActivity;
import com.daweichang.vcfarm.activity.CameraListActivity;
import com.daweichang.vcfarm.activity.MainActivity;
import com.daweichang.vcfarm.activity.MessageListActivity;
import com.daweichang.vcfarm.mode.ArchiveMode;
import com.daweichang.vcfarm.mode.CameraLoginMode;
import com.daweichang.vcfarm.mode.CameraMode;
import com.daweichang.vcfarm.netret.CameraListRet;
import com.daweichang.vcfarm.utils.P2PListener;
import com.daweichang.vcfarm.utils.PrivateFileUtils;
import com.daweichang.vcfarm.utils.SettingListener;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.google.gson.Gson;
import com.p2p.core.GestureDetector;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PView;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.MD5;
import com.p2p.core.utils.MyUtils;
import com.xcc.mylibrary.KeyBoardUtils;
import com.xcc.mylibrary.NetWorkUtil;
import com.xcc.mylibrary.Sysout;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 农场界面
 * Created by Administrator on 2017/3/23.
 */
public class FarmFragment extends BaseFragment {
    @BindView(R.id.dot)
    View dot;
    @BindView(R.id.textTitle)
    TextView textTitle;
    @BindView(R.id.imgR)
    TextView imgR;
    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;
    @BindView(R.id.textName)
    TextView textName;
    @BindView(R.id.textLook)
    TextView textLook;
    @BindView(R.id.textAdd)
    TextView textAdd;
    @BindView(R.id.editTitle)
    EditText editTitle;
    @BindView(R.id.editContent)
    EditText editContent;
    @BindView(R.id.btnClear)
    Button btnClear;
    @BindView(R.id.btnSave)
    Button btnSave;
    //@BindView(R.id.pview)
    P2PView pview;
    private static CameraMode cameraMode;
    @BindView(R.id.textMsg)
    TextView textMsg;
    @BindView(R.id.imgSwitch)
    ImageView imgSwitch;
    @BindView(R.id.palyLayout)
    RelativeLayout palyLayout;
    //    private ArchiveMode archiveMode;//档案
    private int playStats = 0;//0空闲，1连接中，2已中断
    private static int mVideoFrameRate = 15;
    private final int MINX = 50;
    private final int MINY = 25;
    private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
    private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
    private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
    private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
    private static final String jsonName = "DCameraMode.json";
    public static final String action = "com.daweichang.vcfarm.fragment.FarmFragment.CameraMode";
    public static final String CameraDis = "com.daweichang.vcfarm.CameraDis";//断开摄像头
    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (CameraDis.equals(intent.getAction())) {
                P2PHandler.getInstance().p2pDisconnect();//断开
                return;
            }
            boolean isLogin = intent.getBooleanExtra("isLogin", false);
            if (isLogin) {
                CameraMode mode = getMode(getActivity());
                if (mode == null) getSelectMode();
                else farmHandler.sendEmptyMessageDelayed(0, farmHandlerTime);
            } else {
                P2PHandler.getInstance().p2pDisconnect();//断开
                clear(AppVc.getAppVc());
                playStats = 0;
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            if (!TextUtils.isEmpty(json)) {
                CameraMode mode = new Gson().fromJson(json, CameraMode.class);
                setMode(getActivity(), mode);
                cameraLoginMode = null;
                textName.setText(mode.name);
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
                textShow = "连接成功";
                textMsg.setText(textShow);
                ShowToast.alertShortOfWhite(getActivity(), "监控准备,开始监控");
                //请求屏幕常亮
                wakeLock.acquire();
                playStats = 1;
            } else if (intent.getAction().equals(P2P_REJECT)) {
                //pview.setBackgroundColor(0xFF76B701);
                ShowToast.alertShortOfWhite(getActivity(), "监控挂断 请重试");
                P2PHandler.getInstance().p2pDisconnect();//断开
                textShow = "点击重连";
                textMsg.setText(textShow);
                //取消屏幕常亮
                wakeLock.release();
                if (playStats == 1)
                    playStats = 2;
            }
        }
    };
    private String textShow;
    private static final int farmHandlerTime = 3000;
    private Handler farmHandler = new Handler() {
        public void handleMessage(Message msg) {
            int callType = 1;
            switch (msg.what) {
                case 0: {
                    CameraMode mode = getMode(getActivity());
                    if (mode == null || !mode.hasCamera()) {
                        ShowToast.alertShortOfWhite(getActivity(), R.string.whdsxtxx);
                        return;
                    }
                    if (UserConfig.hasWifi() && !NetWorkUtil.isWifiConnected(getActivity())) {
                        ShowToast.alertShortOfWhite(getActivity(), R.string.xy_cnbf);
                        return;
                    }
                    //TODO 调试
//                    mode.camera_user_name = "0810090";
//                    mode.camera_user_pswd = "111222";
//                    mode.camera_no = "7019032";
//                    mode.camera_device_pwd = "abc123";

                    //textShow = "开始初始化摄像头...";
                    textShow = "正在连接";
                    farmHandler.sendEmptyMessageDelayed(1, farmHandlerTime);
                }
                break;
                case 1:
                    //textShow += "\n开始登录摄像头...";
                    startCamera();
                    break;
                case 2: {
                    //textShow += "\n登录成功,\n开始连接摄像头...";
                    String rCode1 = cameraLoginMode.P2PVerifyCode1;
                    String rCode2 = cameraLoginMode.P2PVerifyCode2;
                    P2PHandler.getInstance().p2pInit(getActivity(), new P2PListener(), new SettingListener());
                    P2PHandler.getInstance().p2pConnect(camera_user_name, Integer.parseInt(rCode1), Integer.parseInt(rCode2));
                    farmHandler.sendEmptyMessageDelayed(3, farmHandlerTime);
                }
                break;
                case 3: {
                    if (cameraLoginMode != null) {//开启视屏
                        //textShow += "\n连接结束,\n开始获取数据流...";
                        //pview.setBackgroundColor(0);
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
                }
            }
            textMsg.setText(textShow);
            Sysout.v("--textShow-->", textShow);
        }
    };

    public static FarmFragment newInstance() {
        FarmFragment fragment = new FarmFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farm, null);
        ButterKnife.bind(this, view);
        return view;
    }

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        powerManager = (PowerManager) getActivity().getSystemService(Service.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
        //是否需计算锁的数量
        wakeLock.setReferenceCounted(false);
        pview = (P2PView) view.findViewById(R.id.ptpView);
        if (pview == null)
            pview = (P2PView) palyLayout.findViewById(R.id.ptpView);

        Sysout.out("pview is null:" + (pview == null));

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

        filter = new IntentFilter(AppVc.Login);
        filter.addAction(CameraDis);
        getActivity().registerReceiver(loginReceiver, filter);

        AppVc appVc = AppVc.getAppVc();
        if (appVc.isLogin()) {
            CameraMode mode = getMode(getActivity());
            if (mode == null)
                getSelectMode();
            else {
                textName.setText(mode.name);
                farmHandler.sendEmptyMessageDelayed(0, farmHandlerTime);
            }
        }

        editContent.setFocusable(true);
        editContent.setFocusableInTouchMode(true);
        editTitle.setFocusable(true);
        editTitle.setFocusableInTouchMode(true);
    }

    public void onResume() {
        super.onResume();
        if (pview != null)
            initP2PView(7);
        P2PHandler.getInstance().openAudioAndStartPlaying(1);//打开音频并准备播放，calllType与call时type一致

        if (playStats == 2) {
            farmHandler.removeCallbacksAndMessages(null);
            farmHandler.sendEmptyMessage(0);
        }
    }

    public void onPause() {
        super.onPause();
        farmHandler.removeCallbacksAndMessages(null);
        //取消屏幕常亮
        wakeLock.release();
    }

    public void onDestroyView() {
        super.onDestroyView();
        P2PHandler.getInstance().p2pDisconnect();//断开
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(baseReceiver);
        getActivity().unregisterReceiver(loginReceiver);
    }

    @OnClick({R.id.imgBack, R.id.imgR, R.id.textLook, R.id.textAdd, R.id.btnClear, R.id.btnSave, R.id.textMsg, R.id.imgSwitch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                dot.setVisibility(View.GONE);
                MessageListActivity.open(getActivity());
                break;
            case R.id.imgR://签到
                if (handler != null) handler.sendEmptyMessage(MainActivity.Handler_SignIn);
                break;
            case R.id.textLook://查看档案
//                if (archiveMode == null) {
//                    return;
//                }
//                ArchivesDetailsActivity.open(getActivity(), archiveMode);
                archiveDetail();
                break;
            case R.id.textAdd:
//                P2PHandler.getInstance().p2pDisconnect();//断开
                cameraLoginMode = null;
                CameraListActivity.open(getActivity());
                break;
            case R.id.btnClear:
                editContent.setText("");
                editTitle.setText("");
                break;
            case R.id.btnSave://添加笔记
                noteSave();
                break;
            case R.id.textMsg:
                //pview.setBackgroundColor(0xFF76B701);
                farmHandler.removeCallbacksAndMessages(null);
                farmHandler.sendEmptyMessage(0);
                break;
            case R.id.imgSwitch:
                //TODO
                if (MainActivity.isP) openFullScreen();
                else closeFullScreen();
                break;
        }
    }

    private void getSelectMode() {
        Call<CameraListRet> baseRetCall = BaseService.getInstance().getServiceUrl().cameraList(UserConfig.getToken());
        openLoadDialog();
        baseRetCall.enqueue(new Callback<CameraListRet>() {
            public void onResponse(Call<CameraListRet> call, Response<CameraListRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                CameraListRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        if (body.data != null && body.data.size() > 0) {
                            ArrayList<CameraMode> modeArrayList = body.data;
                            for (CameraMode mode : modeArrayList) {
                                if (mode.is_selected == 1) {
                                    FragmentActivity activity = getActivity();
                                    if (activity != null) {
                                        setMode(activity, mode);
                                        farmHandler.removeCallbacksAndMessages(null);
                                        farmHandler.sendEmptyMessage(0);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else
                    Sysout.out("==摄像头列表接口返回成功==");
            }

            public void onFailure(Call<CameraListRet> call, Throwable t) {
                dismissDialog();
            }
        });
    }

    private CameraLoginMode cameraLoginMode;
    private String camera_user_name;

    private void startCamera() {
        CameraMode mode = getMode(getActivity());
        //登录摄像头
        openLoadDialog();
        String username = mode.camera_user_name;
        camera_user_name = username;
        if (MyUtils.isNumeric(username)) {
            username = String.valueOf((Integer.parseInt(username) | 0x80000000));
        }
        textName.setText(mode.name);
        MD5 md = new MD5();
        Call<CameraLoginMode> baseRetCall = CameraService.getInstance().getServiceUrl().loginCheck(username, md.getMD5ofStr(mode.camera_user_pswd), "1", "3", "3014666");
        baseRetCall.enqueue(new Callback<CameraLoginMode>() {
            public void onResponse(Call<CameraLoginMode> call, Response<CameraLoginMode> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                CameraLoginMode body = response.body();
                if (body != null) {
                    Sysout.v("-----摄像头登录结果------", body.toString());
                    if (body.isOk()) {
                        cameraLoginMode = body;
                        farmHandler.sendEmptyMessageDelayed(2, farmHandlerTime);
                    } else {
//                        ShowToast.alertShortOfWhite(getActivity(), R.string.sxtdlcg);
                        textMsg.setText(R.string.sxtdlsb);
                    }
                } else {
                    ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
                }
            }

            public void onFailure(Call<CameraLoginMode> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
            }
        });
    }

    public void initP2PView(int type) {
        pview.setCallBack();
        pview.setGestureDetector(new GestureDetector(getActivity(), new GestureListener(), null, true));
        pview.setDeviceType(type);
    }

    // 保存笔记
    private void noteSave() {
        // 接口需要添加Title
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ShowToast.alertShortOfWhite(getActivity(), R.string.shurubiaoti);
            KeyBoardUtils.setFocusab(getActivity(), editTitle);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            ShowToast.alertShortOfWhite(getActivity(), R.string.qsrlr);
            KeyBoardUtils.setFocusab(getActivity(), editContent);
            return;
        }
        openLoadDialog();
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().noteSave(UserConfig.getToken(), title, content);
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body == null)
                    ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
                else if (body.isOk()) {
                    editTitle.setText("");
                    editContent.setText("");
                    ShowToast.alertShortOfWhite(getActivity(), R.string.baocunchenggong);
                } else ShowToast.alertShortOfWhite(getActivity(), body.msg);
                Sysout.out("==保存笔记接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
            }
        });
    }

    // 返回数据需要修改 档案详细
    private void archiveDetail() {
        Call<BaseRet<ArchiveMode>> baseRetCall = BaseService.getInstance().getServiceUrl()
                .archiveDetail(UserConfig.getToken(), cameraMode.archive_id);
        openLoadDialog(baseRetCall);
        baseRetCall.enqueue(new Callback<BaseRet<ArchiveMode>>() {
            public void onResponse(Call<BaseRet<ArchiveMode>> call, Response<BaseRet<ArchiveMode>> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet<ArchiveMode> body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        ArchiveMode data = body.getData();
                        //initData(data);
                        //archiveMode = data;
                        ArchivesDetailsActivity.open(getActivity(), data);
                    } else {
                        ShowToast.alertShortOfWhite(getActivity(), body.msg);
                    }
                } else {
                    ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
                }
                Sysout.out("==档案详细接口返回成功==");
            }

            public void onFailure(Call<BaseRet<ArchiveMode>> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
            }
        });
    }

    public static CameraMode getMode(Context context) {
        if (cameraMode == null) {
            try {
                String txt = new PrivateFileUtils(context, jsonName).getString();
                cameraMode = new Gson().fromJson(txt, CameraMode.class);
            } catch (Exception e) {
            }
//            if (cameraMode == null) {
//                cameraMode = new CameraMode();
//                cameraMode.camera_no = "";
//            }
        }
        return cameraMode;
    }

    public static void setMode(Context context, CameraMode cameraMode) {
        FarmFragment.cameraMode = cameraMode;
        try {
            String s = new Gson().toJson(cameraMode);
            new PrivateFileUtils(context, jsonName).setString(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(Context context) {
        cameraMode = null;
        new PrivateFileUtils(context, jsonName).delete();
    }

    public void setMute(boolean bool) {
        try {
            MediaPlayer.getInstance()._SetMute(bool);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onP2PViewSingleTap() {
    }

    //开启全屏
    private void openFullScreen() {
//        if (palyLayout2 == null) return;
//        pview.setBackgroundColor(0xFF76B701);
//        P2PHandler.getInstance().p2pDisconnect();//断开
//        palyLayout2.setVisibility(View.VISIBLE);
//        palyLayout.removeView(pview);
//        palyLayout2.addView(pview, 0);
//        //pview.fullScreen();
//        textMsg = textMsg2;
//        farmHandler.sendEmptyMessage(0);

        KeyBoardUtils.closeKey(getActivity());
        fullscreen(true);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) palyLayout.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.topMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.leftMargin = 0;

        MainActivity.isP = false;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private float y377, y167, x25;

    //TODO
    //关闭全屏
    private void closeFullScreen() {
        //        android:layout_height="@dimen/y377"
//        android:layout_marginLeft="@dimen/x25"
//        android:layout_marginRight="@dimen/x25"
//        android:layout_marginTop="@dimen/y167"
        if (y377 == 0) {
            y377 = getResources().getDimension(R.dimen.y377);
            x25 =0;// getResources().getDimension(R.dimen.x25);
            y167 = getResources().getDimension(R.dimen.y167);
        }
        //P2PHandler.getInstance().p2pDisconnect();//断开
        //pview.setBackgroundColor(0xFF76B701);
        //farmHandler.removeCallbacksAndMessages(null);

        fullscreen(false);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) palyLayout.getLayoutParams();
        layoutParams.height = (int) y377;
        layoutParams.topMargin = (int) y167;
        layoutParams.rightMargin = (int) x25;
        layoutParams.leftMargin = (int) x25;
        MainActivity.isP = true;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        palyLayout2.setVisibility(View.INVISIBLE);
//        palyLayout2.removeView(pview);
//        palyLayout.addView(pview, 0);
//        //pview.halfScreen();
//        textMsg = textMsg1;
//        farmHandler.sendEmptyMessage(0);
    }

    private void fullscreen(boolean enable) {
        if (enable) { //显示状态栏
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getActivity().getWindow().setAttributes(lp);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else { //隐藏状态栏
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setAttributes(lp);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static String P2P_ACCEPT = "com.yoosee.P2P_ACCEPT";
    public static String P2P_READY = "com.yoosee.P2P_READY";
    public static String P2P_REJECT = "com.yoosee.P2P_REJECT";
    public boolean isFullScreen = false;
    public boolean isLand = true;// 是否全屏
    public boolean isHalfScreen = true;


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onP2PViewSingleTap();
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