package com.example.user.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PView;

/**
 * Created by wzy on 2016/6/14.
 */
public class MonitorActivity extends BaseMonitorActivity implements View.OnClickListener {
    public static String P2P_ACCEPT = "com.yoosee.P2P_ACCEPT";
    public static String P2P_READY = "com.yoosee.P2P_READY";
    public static String P2P_REJECT = "com.yoosee.P2P_REJECT";
    private Button btn_screenShot,btn_monitor;
    private String pwd,callId,callPwd;
    private TextView tv_content;
    private EditText et_callId;
    private EditText et_callPwd ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monito);
        initComponent();
        regFilter();
    }

    public void initComponent() {
        tv_content = (TextView) findViewById(R.id.tv_content);
        btn_screenShot = (Button) findViewById(R.id.btn_screenShot);
        btn_monitor = (Button) findViewById(R.id.btn_monitor);
        btn_screenShot.setOnClickListener(this);
        btn_monitor.setOnClickListener(this);
        et_callId = (EditText) findViewById(R.id.et_callid);
        et_callPwd = (EditText) findViewById(R.id.et_callpwd);
    }
    protected void onResume() {
        super.onResume();
        pView = (P2PView) findViewById(R.id.pview);
        initP2PView(7);
        //setMute(true);  //设置手机静音
        P2PHandler.getInstance().openAudioAndStartPlaying(1);//打开音频并准备播放，calllType与call时type一致
    }
    protected void onP2PViewSingleTap() {
    }
    protected void onCaptureScreenResult(boolean isSuccess, int prePoint) {
        if(isSuccess){
            tv_content.append("\n 截图成功，默认保存路径为SD下面的screenshot");
        }else{
            tv_content.append("\n 截图失败");
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_screenShot:
                tv_content.append("开始截屏。。。。");
                captureScreen(-1);
                break;
            case R.id.btn_monitor:
                tv_content.setText("发送监控命令。。。。。");
                callId = et_callId.getText().toString();//设备号
                callPwd = et_callPwd.getText().toString();
                pwd = P2PHandler.getInstance().EntryPassword(callPwd);//经过转换后的设备密码
                String contactId = "0810090";//登陆的用户账号
                P2PHandler.getInstance().call(contactId, pwd, true, 1,callId, "", "", 2,callId);
                break;
            default:
                break;
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2P_REJECT);
        filter.addAction(P2P_ACCEPT);
        filter.addAction(P2P_READY);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(P2P_ACCEPT)){
                tv_content.append("\n 监控数据接收");
            }else if(intent.getAction().equals(P2P_READY)){
                tv_content.append("\n 监控准备,开始监控");
            }else if(intent.getAction().equals(P2P_REJECT)){
                tv_content.append("\n 监控挂断");
            }
        }
    };
}
