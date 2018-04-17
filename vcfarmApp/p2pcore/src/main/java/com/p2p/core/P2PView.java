package com.p2p.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;

import com.p2p.core.GestureDetector.OnZoomInListener;
import com.p2p.core.global.Constants;

public class P2PView extends BaseP2PView {
    static final String TAG = "p2p";
    Context mContext;
    MediaPlayer mPlayer;
    private int mWidth, mHeight;
    boolean isInitScreen = false;
    protected GestureDetector mGestureDetector;
    int deviceType;
    int mWindowWidth, mWindowHeight;
    public int fgFullScreen = 0;
    boolean isInitScale;
    public static boolean SUPPORT_ZOOM = false;
    public static boolean SUPPORT_ZOOM_FOCUS = false;
    public static int type = 0;
    public static int scale = 0;
    public static String contactId;
    public static String password;

    public P2PView(Context context) {
        super(context);
        this.mContext = context;
        try {
            mPlayer = MediaPlayer.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("leleTest", "P2PView--");
    }

    public P2PView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public P2PView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mPlayer = MediaPlayer.getInstance();
        Log.e("leleTest", "P2PView++");
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        mWindowWidth = dm.widthPixels;
        mWindowHeight = dm.heightPixels;
    }

    public void initScaleView() {
        isInitScale = true;
    }

    private void vSetWindow() {
//        DisplayMetrics dm = new DisplayMetrics();
//        dm = getResources().getDisplayMetrics();
//        mWindowWidth = dm.widthPixels;
//        mWindowHeight = dm.heightPixels;
//
//        Log.e("my", "xWidth:" + mWindowWidth + " xHeight:" + mWindowHeight);
//        mWidth = mWindowWidth;
//        mHeight = mWindowHeight;
//        //如果type==1，type等于其它值时，通过npc和ipc来判断
//        //scale：0 代表4：3     1 代表16：9
//        if (fgFullScreen == 0) {
//            if (type == 1) {
//                if (scale == 0) {
//                    int Rate, Rate2;
//                    Rate = mWidth * 1024 / mHeight;
//                    Rate2 = 4 * 1024 / 3;
//                    if (Rate > Rate2) {
//                        mWidth = mHeight * 4 / 3;
//                    } else {
//                        mHeight = mWidth * 3 / 4;
//                    }
//
//                    // fullScreen();//为何要全屏
//                } else {
//                    int Rate, Rate2;
//                    Rate = mWidth * 1024 / mHeight;
//                    Rate2 = 16 * 1024 / 9;
//                    if (Rate > Rate2) {
//                        mWidth = mHeight * 16 / 9;
//                    } else {
//                        mHeight = mWidth * 9 / 16;
//                    }
//                }
//            } else {
//                if (deviceType == P2PValue.DeviceType.NPC) {
//                    int Rate, Rate2;
//                    Rate = mWidth * 1024 / mHeight;
//                    Rate2 = 4 * 1024 / 3;
//                    if (Rate > Rate2) {
//                        mWidth = mHeight * 4 / 3;
//                    } else {
//                        mHeight = mWidth * 3 / 4;
//                    }
//
//                    // fullScreen();//为何要全屏
//                } else {
//                    int Rate, Rate2;
//                    Rate = mWidth * 1024 / mHeight;
//                    Rate2 = 16 * 1024 / 9;
//                    if (Rate > Rate2) {
//                        mWidth = mHeight * 16 / 9;
//                    } else {
//                        mHeight = mWidth * 9 / 16;
//                    }
//                }
//            }
//        }

//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
//        layoutParams.width = mWidth;
//        layoutParams.height = mHeight;
        // layoutParams.leftMargin = (mWindowWidth - mWidth) / 2;
        // layoutParams.topMargin = (mWindowHeight - mHeight) / 2;
//        setLayoutParams(layoutParams);
        mPlayer.ChangeScreenSize(mWidth, mHeight, fgFullScreen);
        Log.e("dxslayout", "mWidth---" + mWidth + "mHeight---" + mHeight);
    }

    public void updateScreenOrientation() {
        vSetWindow();
    }

    public void setCallBack() {
        MediaPlayer.setEglView(this);
        getHolder().addCallback(mSHCallback);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    public void setDeviceType(int type) {
        this.deviceType = type;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d(TAG, "onTouchEvent");
        if (!(super.onTouchEvent(event))) {
            if (mGestureDetector != null) {
                mGestureDetector.setOnZoomInListener(zoomInListener);
                mGestureDetector.onTouchEvent(event);
            }
        }

        return true;
    }

    public OnZoomInListener zoomInListener = new OnZoomInListener() {

        @Override
        public void onZoom(MotionEvent event) {
//			if (SUPPORT_ZOOM) {
            mode = MODE.ZOOM;
            touchSuper(event);
//			}
        }

    };

    public void touchSuper(MotionEvent event) {
        super.onTouchEvent(event);
    }

    public void fullScreen() {
        fgFullScreen = 1;
        vSetWindow();
    }

    public void halfScreen() {
        fgFullScreen = 0;
        vSetWindow();
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // startVideo();
            Log.v(TAG, "surfaceChanged()");
            int sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565 by default
            switch (format) {
                case PixelFormat.A_8:
                    Log.v(TAG, "pixel format A_8");
                    break;
                case PixelFormat.LA_88:
                    Log.v(TAG, "pixel format LA_88");
                    break;
                case PixelFormat.L_8:
                    Log.v(TAG, "pixel format L_8");
                    break;
                case PixelFormat.RGBA_4444:
                    Log.v(TAG, "pixel format RGBA_4444");
                    sdlFormat = 0x85421002; // SDL_PIXELFORMAT_RGBA4444
                    break;
                case PixelFormat.RGBA_5551:
                    Log.v(TAG, "pixel format RGBA_5551");
                    sdlFormat = 0x85441002; // SDL_PIXELFORMAT_RGBA5551
                    break;
                case PixelFormat.RGBA_8888:
                    Log.v(TAG, "pixel format RGBA_8888");
                    sdlFormat = 0x86462004; // SDL_PIXELFORMAT_RGBA8888
                    break;
                case PixelFormat.RGBX_8888:
                    Log.v(TAG, "pixel format RGBX_8888");
                    sdlFormat = 0x86262004; // SDL_PIXELFORMAT_RGBX8888
                    break;
                case PixelFormat.RGB_332:
                    Log.v(TAG, "pixel format RGB_332");
                    sdlFormat = 0x84110801; // SDL_PIXELFORMAT_RGB332
                    break;
                case PixelFormat.RGB_565:
                    Log.v(TAG, "pixel format RGB_565");
                    sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565
                    break;
                case PixelFormat.RGB_888:
                    Log.v(TAG, "pixel format RGB_888");
                    // Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
                    sdlFormat = 0x86161804; // SDL_PIXELFORMAT_RGB888
                    break;
                default:
                    Log.v(TAG, "pixel format unknown " + format);
                    break;
            }
            mPlayer.onNativeResize(w, h, sdlFormat);
            Log.e("surface", w + ":" + h);
            mWidth = w;
            mHeight = h;
//TODO xcc删除
//            if (fgFullScreen == 0) {
//                if (type == 1) {
//                    if (scale == 0) {
//                        int Rate, Rate2;
//                        Rate = mWidth * 1024 / mHeight;
//                        Rate2 = 4 * 1024 / 3;
//                        if (Rate > Rate2) {
//                            mWidth = mHeight * 4 / 3;
//                        } else {
//                            mHeight = mWidth * 3 / 4;
//                        }
//                    } else {
//                        int Rate, Rate2;
//                        Rate = mWidth * 1024 / mHeight;
//                        Rate2 = 16 * 1024 / 9;
//                        if (Rate > Rate2) {
//                            mWidth = mHeight * 16 / 9;
//                        } else {
//                            mHeight = mWidth * 9 / 16;
//                        }
//                    }
//                } else {
//                    if (deviceType == P2PValue.DeviceType.NPC) {
//                        int Rate, Rate2;
//                        Rate = mWidth * 1024 / mHeight;
//                        Rate2 = 4 * 1024 / 3;
//                        if (Rate > Rate2) {
//                            mWidth = mHeight * 4 / 3;
//                        } else {
//                            mHeight = mWidth * 3 / 4;
//                        }
//                    } else {
//                        int Rate, Rate2;
//                        Rate = mWidth * 1024 / mHeight;
//                        Rate2 = 16 * 1024 / 9;
//                        if (Rate > Rate2) {
//                            mWidth = mHeight * 16 / 9;
//                        } else {
//                            mHeight = mWidth * 9 / 16;
//                        }
//                    }
//                }
//            }

//			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
//			layoutParams.width = mWidth;
//			layoutParams.height = mHeight;
//			// layoutParams.leftMargin = (mWindowWidth - mWidth) / 2;
//			// layoutParams.topMargin = (mWindowHeight - mHeight) / 2;
//			setLayoutParams(layoutParams);
            sendStartBrod();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.v(TAG, "surfaceCreated()");
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0xFF000000);
                holder.unlockCanvasAndPost(canvas);
            }
            //new 画蓝色背景色线程(holder).start();
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            holder.setKeepScreenOn(true);
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed");
            type = 1;
            scale = 1;
            release();
        }
    };

    public void sendStartBrod() {
        if (!isInitScreen) {
            isInitScreen = true;
            MediaPlayer.getInstance().init(mWidth, mHeight, mWindowWidth);
            Intent start = new Intent();
            start.setAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
            mContext.sendBroadcast(start);
        } else {
            mPlayer.ChangeScreenSize(mWidth, mHeight, 1);
        }
    }

    public synchronized void release() {

        if (mPlayer != null) {
            // mPlayer.native_p2p_hungup();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        isInitScreen = false;
        MediaPlayer.ReleaseOpenGL();
    }

    @Override
    protected int getCurrentWidth() {
        return mWindowWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    @Override
    protected int getCurrentHeight() {
        return mWindowHeight;
    }

    @Override
    protected void setVideoScale(int x, int y, float scale) {
        // TODO Auto-generated method stub
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        y = mHeight - (layoutParams.topMargin + y);
        Log.e("Gview", "zoom" + x + ":" + y + "       " + scale);
        mPlayer.ZoomView(x, y, scale);
    }

    protected boolean MovePicture(int left, int top) {
        if (mPlayer.MoveView(left, 0 - top) == 0) {
            return false;
        }
        return true;

    }

    protected void StopMoving() {
        mPlayer.MoveView(0, 0);
    }

    public void changeNormalSize() {
        /*
         * LayoutParams layoutParams = (LayoutParams) getLayoutParams();
		 * layoutParams.width = mFixWidth; layoutParams.height = mFixHeight;
		 * layoutParams.leftMargin = (mWindowWidth - mFixWidth) / 2;
		 * layoutParams.topMargin = (mWindowHeight - mFixHeight) / 2;
		 * setLayoutParams(layoutParams); mPlayer.ChangeScreenSize(mFixWidth,
		 * mFixHeight, 0);
		 */
    }

    public void setHandler(Handler handler) {
        myHandler = handler;
    }

    public class 画蓝色背景色线程 extends Thread {
        public 画蓝色背景色线程(SurfaceHolder holder) {
            this.holder = holder;
        }

        private SurfaceHolder holder;

        public void run() {
            while (true) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(0xFF76B701);
                    holder.unlockCanvasAndPost(canvas);
                    System.out.println("--lockCanvas--");
                    break;
                }
                try {
                    sleep(100);
                } catch (Exception e) {
                }
            }
        }
    }
}
