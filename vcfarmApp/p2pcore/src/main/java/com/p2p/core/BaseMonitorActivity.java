package com.p2p.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.p2p.core.global.Constants;
import com.p2p.core.utils.MyUtils;

import java.io.IOException;

public abstract class BaseMonitorActivity extends BaseCoreActivity {
    private final int MSG_SHOW_CAPTURERESULT = 0x00002;
    public static int mVideoFrameRate = 15;
    private final int MINX = 50;
    private final int MINY = 25;
    private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
    private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
    private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
    private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
    public P2PView pView;
    boolean isBaseRegFilter = false;
    public boolean isFullScreen = false;
    public boolean isLand = true;// 是否全屏
    public boolean isHalfScreen = true;
    private int PrePoint = -1;
    public static String contactId;
    public static String password;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_CAPTURERESULT:
                    if (msg.arg1 == 1) {
                        onCaptureScreenResult(true, PrePoint);
                    } else {
                        onCaptureScreenResult(false, PrePoint);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        baseRegFilter();
        MediaPlayer.getInstance().setCaptureListener(new CaptureListener(mHandler));
    }

    public void initP2PView(int type) {
        pView.setCallBack();
        pView.setGestureDetector(new GestureDetector(this, new GestureListener(), null, true));
        pView.setDeviceType(type);
    }

//    public void initScaleView(Activity activity, int windowWidth, int windowHeight) {
//        pView.setmActivity(activity);
//        pView.setScreen_W(windowHeight);
//        pView.setScreen_H(windowWidth);
//        pView.initScaleView();
//    }

    private void baseRegFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
        this.registerReceiver(baseReceiver, filter);
        isBaseRegFilter = true;
    }

    public void setMute(boolean bool) {
        try {
            MediaPlayer.getInstance()._SetMute(bool);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setIsLand(boolean isLan) {
        this.isLand = isLan;
    }

    public void setHalfScreen(boolean isHalfScreen) {
        this.isHalfScreen = isHalfScreen;
    }

    private BroadcastReceiver baseReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(
                    Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START)) {
                final MediaPlayer mPlayer = MediaPlayer.getInstance();
                new Thread(new Runnable() {
                    public void run() {
                        MediaPlayer.nativeInit(mPlayer);
                        try {
                            mPlayer.setDisplay(pView);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start(mVideoFrameRate);
                        setMute(true);
                    }
                }).start();
            }
        }
    };

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onP2PViewSingleTap();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (isLand && !isHalfScreen) {
                if (!isFullScreen) {
                    isFullScreen = true;
                    pView.fullScreen();
                } else {
                    isFullScreen = false;
                    pView.halfScreen();
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
                if (Math.abs(distance) > MyUtils.dip2px(
                        BaseMonitorActivity.this, MINX)) {
                    if (distance > 0) {
                        id = USR_CMD_OPTION_PTZ_TURN_RIGHT;
                    } else {
                        id = USR_CMD_OPTION_PTZ_TURN_LEFT;
                    }
                }
            } else {
                distance = e2.getY() - e1.getY();
                if (Math.abs(distance) > MyUtils.dip2px(
                        BaseMonitorActivity.this, MINY)) {
                    if (distance > 0) {
                        id = USR_CMD_OPTION_PTZ_TURN_UP;
                    } else {
                        id = USR_CMD_OPTION_PTZ_TURN_DOWN;
                    }
                }
            }

            if (id != -1) {
                MediaPlayer.getInstance().native_p2p_control(id);
//				if(id==USR_CMD_OPTION_PTZ_TURN_UP){
//					P2PHandler.getInstance().controlCamera(contactId, password,MyUtils.btop);
//				}else if(id==USR_CMD_OPTION_PTZ_TURN_DOWN){
//					P2PHandler.getInstance().controlCamera(contactId, password,MyUtils.bbottom);
//				}else if(id==USR_CMD_OPTION_PTZ_TURN_LEFT){
//					P2PHandler.getInstance().controlCamera(contactId, password,MyUtils.bleft);
//				}else if(id==USR_CMD_OPTION_PTZ_TURN_RIGHT){
//					P2PHandler.getInstance().controlCamera(contactId, password,MyUtils.bright);
//				}
            } else {
            }

            return true;
        }
    }

    private class CaptureListener implements MediaPlayer.ICapture {
        Handler mSubHandler;

        public CaptureListener(Handler handler) {
            mSubHandler = handler;
        }

        @Override
        public void vCaptureResult(int result) {
            Log.e("wzytest", "result in vCaptureResult:" + result);
            Message msg = new Message();
            msg.what = MSG_SHOW_CAPTURERESULT;
            msg.arg1 = result;
            mSubHandler.sendMessage(msg);
        }
    }

    /**
     * -1是普通截图，0~4是预置位截图
     *
     * @param prePoint
     */
    public void captureScreen(int prePoint) {
        this.PrePoint = prePoint;
        try {
            MediaPlayer.getInstance()._CaptureScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBaseRegFilter) {
            this.unregisterReceiver(baseReceiver);
            isBaseRegFilter = false;
        }
    }

    protected abstract void onP2PViewSingleTap();

    protected abstract void onCaptureScreenResult(boolean isSuccess, int prePoint);
}
