package com.example.user.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PView;

/**
 * Created by Administrator on 2017/5/9.
 */

public class PlayerActivity2 extends BaseMonitorActivity {
    public PlayerActivity2 getActivity() {
        return this;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_player);
        regFilter();

        new Handler() {
        }.postDelayed(new Runnable() {
            public void run() {
                call();
            }
        }, 3000);
    }

    protected void onResume() {
        super.onResume();
        pView = (P2PView) findViewById(R.id.pview);
        initP2PView(7);
        //setMute(true);  //设置手机静音
        P2PHandler.getInstance().openAudioAndStartPlaying(1);//打开音频并准备播放，calllType与call时type一致
    }

    public static String P2P_ACCEPT = "com.yoosee.P2P_ACCEPT";
    public static String P2P_READY = "com.yoosee.P2P_READY";
    public static String P2P_REJECT = "com.yoosee.P2P_REJECT";
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(P2P_ACCEPT)) {
                ShowToast.alertShortOfWhite(getActivity(), "监控数据接收");
            } else if (intent.getAction().equals(P2P_READY)) {
                // textShow = "点击重连";
                //  textMsg.setText(textShow);
                ShowToast.alertShortOfWhite(getActivity(), "监控准备,开始监控");
            } else if (intent.getAction().equals(P2P_REJECT)) {
                ShowToast.alertShortOfWhite(getActivity(), "监控挂断 请重试");
                // P2PHandler.getInstance().p2pDisconnect();//断开
            }
        }
    };

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P_REJECT);
        filter.addAction(P2P_ACCEPT);
        filter.addAction(P2P_READY);
        registerReceiver(mReceiver, filter);
    }

    private void call() {
//        mode.camera_user_name = "0810090";
//        mode.camera_device_pwd = "111222";
//        mode.camera_no = "7019032";
//        mode.camera_device_pwd = "abc123";
        String pwd = "abc123";
        String callId = "7019032";
        String contactId = "0810090";//登陆的用户账号
        pwd = P2PHandler.getInstance().EntryPassword(pwd);//经过转换后的设备密码
        P2PHandler.getInstance().call(contactId, pwd, true, 1, callId, "", "", 2, callId);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    protected void onP2PViewSingleTap() {
    }

    protected void onCaptureScreenResult(boolean isSuccess, int prePoint) {
    }

    public int getActivityInfo() {
        return 0;
    }

    protected void onGoBack() {
    }

    protected void onGoFront() {
    }

    protected void onExit() {
    }
}
