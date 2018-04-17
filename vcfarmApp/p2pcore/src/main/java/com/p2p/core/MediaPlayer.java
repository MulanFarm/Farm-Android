package com.p2p.core;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.p2p.core.P2PInterface.IP2P;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.DES;
import com.p2p.core.utils.MyUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class MediaPlayer {
    private static MediaPlayer manager = null;
    private static IP2P p2pInterface = null;
    private static ISetting settingInterface = null;
    private static Context mContext;

    public MediaPlayer(Context context) {
        native_setup(new WeakReference<MediaPlayer>(this));
        this.mContext = context;
        manager = this;
    }

    ;

    public static MediaPlayer getInstance() {
        if (manager == null) {
            manager = new MediaPlayer(mContext);
        }
        return manager;
    }

    public static boolean isMute = false;
    public static boolean isSendAudio = false;

    private final static String TAG = "2cu";
    private boolean mScreenOnWhilePlaying;
    private static ICapture mCapture = null;

    private static AudioRecord mAudioRecord = null;
    private static int mCpuVersion = 0;
    private int mNativeContext; // accessed by native methods
    private Surface mSurface;

    static {
        System.loadLibrary("SDL");
        // System.loadLibrary("mp4v2");//被注释且应用包中没有MP4v2SO库时，setReceveAVData(boolean)不可用
        mCpuVersion = MyUtils.getCPUVesion();
        System.loadLibrary("mediaplayer");
        native_init(mCpuVersion);
    }

    public void setCaptureListener(ICapture captureLister) {
        mCapture = captureLister;
    }

    public void setP2PInterface(IP2P p2pInterface) {
        this.p2pInterface = p2pInterface;
    }

    public void setSettingInterface(ISetting settingInterface) {
        this.settingInterface = settingInterface;
    }

    public void setIsSendAudio(boolean bool) {
        this.isSendAudio = bool;
    }

    public static int getConvertAckResult(int result) {
        if (result == 0) {
            return Constants.ACK_RET_TYPE.ACK_SUCCESS;
        } else if (result == 1) {
            return Constants.ACK_RET_TYPE.ACK_PWD_ERROR;
        } else if (result == 2) {
            return Constants.ACK_RET_TYPE.ACK_NET_ERROR;
        } else if (result == 4) {
            return Constants.ACK_RET_TYPE.ACK_INSUFFICIENT_PERMISSIONS;
        } else {
            return result;
        }
    }

    private static void postEventFromNative(Object mediaplayer_ref, int what,
                                            int iDesID, int arg1, int arg2, String msgStr) {
        Log.e("wzytest", "postEventFromNative");
        if (p2pInterface == null || settingInterface == null) {
            return;
        }
        int reason_code = 0;
        // 截获字串
        if (msgStr.equals("pw_incrrect")) {
            reason_code = 0;
        } else if (msgStr.equals("busy")) {
            reason_code = 1;
        } else if (msgStr.equals("none")) {
            reason_code = 2;
        } else if (msgStr.equals("id_disabled")) {
            reason_code = 3;
        } else if (msgStr.equals("id_overdate")) {
            reason_code = 4;
        } else if (msgStr.equals("id_inactived")) {
            reason_code = 5;
        } else if (msgStr.equals("offline")) {
            reason_code = 6;
        } else if (msgStr.equals("powerdown")) {
            reason_code = 7;
        } else if (msgStr.equals("nohelper")) {
            reason_code = 8;
        } else if (msgStr.equals("hungup")) {
            reason_code = 9;
        } else if (msgStr.equals("timeout")) {
            reason_code = 10;
        } else if (msgStr.equals("nobody")) {
            reason_code = 11;
        } else if (msgStr.equals("internal_error")) {
            reason_code = 12;
        } else if (msgStr.equals("conn_fail")) {
            reason_code = 13;
        } else if (msgStr.equals("not_support")) {
            reason_code = 14;
        } else if (msgStr.equals("noframe")) {
            reason_code = 15;
        }

        switch (what) {
            case 1:
                String threeNumber = "";
                if (arg2 > 0) {
                    threeNumber = String.valueOf(arg2);
                } else {
                    threeNumber = "0" + String.valueOf((0 - arg2));
                }
                if (arg1 == 1) {
                    // 鍛煎叆
                    p2pInterface.vCalling(false, threeNumber,
                            Integer.parseInt(msgStr));
                } else {
                    // 鍛煎嚭
                    p2pInterface.vCalling(true, threeNumber,
                            Integer.parseInt(msgStr));
                }
                break;
            case 2:
                Log.e("leleTestvReject", "iDesID=" + iDesID + "reason_code=" + reason_code);
                p2pInterface.vReject(reason_code);
                break;
            case 3:
                p2pInterface.vAccept(arg1, arg2);
                break;
            case 4:
                p2pInterface.vConnectReady();
                break;
            case 5:
                if (mCapture != null) {
                    mCapture.vCaptureResult(arg1);
                }
                break;
            case 6:
                // remote 绾跨▼ack鍥炶皟
                if (settingInterface != null) {
                    settingInterface.ACK_vRetSetOrGetEx(arg1, arg2);
                }
                break;
            case 7:

                break;
            case 8:
                p2pInterface.vChangeVideoMask(arg1);
                break;
            case 9:
                p2pInterface.vRetRTSPNotify(arg2, msgStr);
                break;
            default:
                p2pInterface.vRetPostFromeNative(what, iDesID, arg1, arg2, msgStr);
                break;
        }

    }

    static int iAudioDataInputNs = 0;
    static long AudioTrackPTSBegin = 0;
    static boolean fgdoPlayInit = true;
    static boolean fgdoRecordInit = true;

    public static void openAudioTrack() {
        Log.i(TAG, "openAudioTrack");
        try {
            int maxjitter = AudioTrack.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (Build.MODEL.equals("HTC One X")) {
                mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                        8000, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, maxjitter * 2,
                        AudioTrack.MODE_STREAM);
            } else {
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, maxjitter * 2,
                        AudioTrack.MODE_STREAM);
            }

            Log.i(TAG, "Audio Track min buffer size:" + maxjitter); // 870
            // frames--
            // 108.75mS
            iAudioDataInputNs = 0;
            AudioTrackPTSBegin = System.currentTimeMillis();
            mAudioTrack.play();
            fgdoPlayInit = true;
        } catch (Exception e) {
            Log.e("test", "error");
        }
    }

    public static void openAudioRecord() {
        Log.i(TAG, "openAudioRecord");
        int samp_rate = 8000;
        int min = AudioRecord.getMinBufferSize(samp_rate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER,
                samp_rate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, min);
        try {
            mAudioRecord.startRecording();
        } catch (Exception e) {
            Log.e("dxserror",
                    "AudioRecord staart error,Maybe user inhibit AudioRecord");
            mAudioRecord = null;
            fgdoRecordInit = false;
        }

        fgdoRecordInit = true;
    }

    private static int setAudioBuffer(byte[] buffer, int buffer_size, int[] iPTS) {
        int readNum = 0;
        if (mAudioRecord == null || buffer == null) {
            readNum = 0;
        } else {
            // Set priority, only do once
            if (fgdoRecordInit == true) {
                try {
                    android.os.Process
                            .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                } catch (Exception e) {
                    // DoLog("Set play thread priority failed: " +
                }
                fgdoRecordInit = false;
            }
            readNum = mAudioRecord.read(buffer, 0, buffer_size);
            // Log.e("my",readNum+"");
            iPTS[0] = (int) (System.currentTimeMillis() - AudioTrackPTSBegin - (readNum / 16));
            // iAudioRecordPTS += readNum/16 ; ///time mS
        }
        return readNum;
    }

    private static void getAudioBuffer(byte[] buffer, int buffer_size,
                                       int[] iPTS) {
        int i;
        int iTime1;

        i = mAudioTrack.getPlaybackHeadPosition();
        iTime1 = (int) (System.currentTimeMillis() - AudioTrackPTSBegin);

        iPTS[0] = iTime1 + (iAudioDataInputNs - i) / 8; // /PTS mS

        if (mAudioTrack != null) {
            if (fgdoPlayInit == true) {
                try {
                    android.os.Process
                            .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                } catch (Exception e) {

                }
                fgdoPlayInit = false;
            }
            int result = mAudioTrack.write(buffer, 0, 320);

            iAudioDataInputNs += (buffer_size / 2);
        }

    }

    public static void RecvAVData(byte[] AudioBuffer, int AudioLen,
                                  int AudioFrames, long AudioPTS, byte[] VideoBuffer, int VideoLen,
                                  long VideoPTS) {
        p2pInterface.vRecvAudioVideoData(AudioBuffer, AudioLen, AudioFrames,
                AudioPTS, VideoBuffer, VideoLen, VideoPTS);
    }

    public native void setAVFilePath(String filename);

    public native int stopRecoder();

    public native int startRecoder();

    public void setDisplay(SurfaceView sh) throws IOException {
        _setVideoSurface(sh);
    }

    public void init(int width, int height, int fullScreenSize)
            throws IllegalStateException {
        _InitSession(width, height, fullScreenSize);
    }

    public void start(int iFrameRate) throws IllegalStateException {
        openAudioRecord();
        _StartSending(iFrameRate);
    }

    public void stop() throws IllegalStateException {
        _StopSession();

        if (mAudioTrack != null) {
            mAudioTrack.flush();
            mAudioTrack.stop();

            mAudioTrack.release();
            mAudioTrack = null;
        }
        if (mAudioRecord != null) {
            mAudioRecord.stop();

            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public void pause() throws IllegalStateException {
        _PauseSession();
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            mScreenOnWhilePlaying = screenOn;
        }
    }

    private native void _setVideoSurface(SurfaceView surface)
            throws IOException;

    public native void _SetMute(boolean isMute) throws IOException;

    public native void _CaptureScreen() throws IOException;

    public native void _SetRecvAVDataEnable(boolean fgRecv);

    private native void _InitSession(int width, int height, int fullScreenSize)
            throws IllegalStateException;

    public native void _StartPlaying(int width, int height, int callType)
            throws IOException, IllegalStateException;

    private native void _StartSending(int iFrameRate)
            throws IllegalStateException;

    private native void _PauseSession() throws IllegalStateException;

    private native void _StopSession() throws IllegalStateException;

    /**
     * Checks whether the MediaPlayer is playing.
     *
     * @return true if currently playing, false otherwise
     */
    public native boolean _isPlaying();

    /**
     * H264 ENCODER
     */
    public native int _FillVideoRawFrame(byte[] in, int insize, int width,
                                         int height, int isYUV);

    /**
     * AMR ENCODER
     */

    /**
     * P2P connect
     */

    private static native final void native_init(int cpuVersion)
            throws RuntimeException;

    private native final void native_setup(Object mediaplayer_this);

    public native int native_p2p_connect(int uID, int password, int code1,
                                         int code2, byte[] szMesg, int[] iCustomerID);

    public native int native_p2p_call(long id, int bMonitor, int password,
                                      int iFileIndex, int VideoTrans, byte[] filename, byte[] szMesg,
                                      String ipdress, long headerID);

    public native void native_p2p_accpet();

    public native void native_p2p_hungup();

    public native void native_p2p_control(int control);

    public native void native_p2p_disconnect();

    // SDL function
    public static native void nativeInit(Object classObj);

    public static native void nativeQuit();

    public static native void nativePause();

    public static native void nativeResume();

    public static native void onNativeResize(int x, int y, int format);

    public static native void onNativeKeyDown(int keycode);

    public static native void onNativeKeyUp(int keycode);

    public static native void onNativeTouch(int touchDevId,
                                            int pointerFingerId, int action, float x, float y, float p);

    public static native void onNativeAccel(float x, float y, float z);

    public static native void nativeRunAudioThread();

    /**
     * Releases resources associated with this MediaPlayer object. It is
     * considered good practice to call this method when you're done using the
     * MediaPlayer.
     */

    public void release() {
        // stayAwake(false);
        // updateSurfaceScreenOn();
        // _release();
    }

    /**
     * Resets the MediaPlayer to its uninitialized state. After calling this
     * method, you will have to initialize it again by setting the data source
     * and calling prepare().
     */
    public void reset() {
        // stayAwake(false);
        // _reset();
    }

    // @Override

    public interface IFFMpegPlayer {
        public void onPlay();

        public void onStop();

        public void onRelease();

        public void onError(String msg, Exception e);
    }

    public interface ICapture {
        public void vCaptureResult(int result);
    }

    // yi add
    public static native void nativeInitPlayBack();

    /**
     * +++++++++++++++++++++++SDL++++++++++++++++++++++*************************
     * ************88
     */
    private static EGLContext mEGLContext;
    private static EGLSurface mEGLSurface;
    private static EGLDisplay mEGLDisplay;
    private static EGLConfig mEGLConfig;
    private static int mGLMajor, mGLMinor;
    private static EGL10 mEgl;

    // Audio
    private static Thread mAudioThread;
    private static AudioTrack mAudioTrack;
    private static Object buf;

    /**
     * ++++++++++++++++For native callback function+++++++++++++++++++++++++
     */
    public static void testFunction(int sb1, int sb2) {

    }

    public static boolean createGLContext(int majorVersion, int minorVersion) {
        Log.e(TAG, "createGLContext");
        return initEGL(majorVersion, minorVersion);
    }

    public static void flipBuffers() {
        // Log.e(TAG, "flipBuffers");
        flipEGL();
    }

    public static Object audioInit(int sampleRate, boolean is16Bit,
                                   boolean isStereo, int desiredFrames) {
        return null;
    }

    public static void audioWriteShortBuffer(short[] buffer) {

    }

    public static void audioWriteByteBuffer(byte[] buffer) {
    }

    public static void audioQuit() {
        Log.i(TAG, "++ audioQuit");
        if (mAudioThread != null) {
            try {
                mAudioThread.join();
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping audio thread: " + e);
            }
            mAudioThread = null;

            // Log.v("SDL", "Finished waiting for audio thread");
        }

        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }

        Log.i(TAG, "-- audioQuit");
    }

    /**
     * ----------------For native callback function-------------------------
     */

    public static void audioStartThread() {

    }

    // EGL functions
    public static boolean initEGL(int majorVersion, int minorVersion) {
        Log.i(TAG, "++ initEGL");
        Log.i("surface", "initEGL");
        if (mEGLDisplay == null) {
            // Log.v("SDL", "Starting up OpenGL ES " + majorVersion + "." +
            // minorVersion);

            try {
                if (mEgl == null) {
                    mEgl = (EGL10) EGLContext.getEGL();
                }

                EGLDisplay dpy = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

                int[] version = new int[2];
                mEgl.eglInitialize(dpy, version);

                int EGL_OPENGL_ES_BIT = 1;
                int EGL_OPENGL_ES2_BIT = 4;
                int renderableType = 0;

                if (majorVersion == 2) {
                    renderableType = EGL_OPENGL_ES2_BIT;
                } else if (majorVersion == 1) {
                    renderableType = EGL_OPENGL_ES_BIT;
                }
                int[] configSpec = {
                        // EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_RENDERABLE_TYPE, renderableType,
                        EGL10.EGL_NONE};
                EGLConfig[] configs = new EGLConfig[1];
                int[] num_config = new int[1];
                if (!mEgl.eglChooseConfig(dpy, configSpec, configs, 1,
                        num_config) || num_config[0] == 0) {
                    Log.e(TAG, "No EGL config available");
                    return false;
                }
                EGLConfig config = configs[0];

				/*
                 * int EGL_CONTEXT_CLIENT_VERSION=0x3098; int contextAttrs[] =
				 * new int[] { EGL_CONTEXT_CLIENT_VERSION, majorVersion,
				 * EGL10.EGL_NONE }; EGLContext ctx = egl.eglCreateContext(dpy,
				 * config, EGL10.EGL_NO_CONTEXT, contextAttrs);
				 * 
				 * if (ctx == EGL10.EGL_NO_CONTEXT) { Log.e("SDL",
				 * "Couldn't create context"); return false; }
				 * SDLActivity.mEGLContext = ctx;
				 */
                mEGLDisplay = dpy;
                mEGLConfig = config;
                mGLMajor = majorVersion;
                mGLMinor = minorVersion;

                Log.i("SDL", "majorVersion " + majorVersion);
                Log.i("SDL", "minorVersion " + minorVersion);

                createEGLSurface();
            } catch (Exception e) {
                Log.v(TAG, e + "");
                for (StackTraceElement s : e.getStackTrace()) {
                    Log.v(TAG, s.toString());
                }
            }
        } else
            createEGLSurface();

        Log.i(TAG, "-- initEGL");
        return true;
    }

    static long timeStart = 0;
    static int frame = 0;
    private static Object showView = null;

    public static void setEglView(Object view) {
        Log.e(TAG, "surfaceView.hashcode-->" + view.hashCode());
        showView = view;
    }

    // EGL buffer flip
    public static void flipEGL() {
        if (frame == 0) {
            // timeStart = Calendar.getInstance().getTimeInMillis(); //shengming
        }

        try {
            mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface,
                    mEGLContext);
            mEgl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, null);
            // drawing here
            mEgl.eglWaitGL();
            mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
        } catch (Exception e) {
            Log.v(TAG, "flipEGL(): " + e);
            for (StackTraceElement s : e.getStackTrace()) {
                Log.v(TAG, s.toString());
            }
        }

		/*
         * frame++;
		 * 
		 * if (frame == 25) { frame = 0; Log.e(TAG, "duration time is: " +
		 * (Calendar.getInstance().getTimeInMillis() - timeStart)); }
		 */
    }

    public static boolean createEGLContext() {
        Log.i(TAG, "createEGLContext");
        int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        int contextAttrs[] = new int[]{EGL_CONTEXT_CLIENT_VERSION, mGLMajor,
                EGL10.EGL_NONE};
        mEGLContext = mEgl.eglCreateContext(mEGLDisplay, mEGLConfig,
                EGL10.EGL_NO_CONTEXT, contextAttrs);
        if (mEGLContext == EGL10.EGL_NO_CONTEXT) {
            Log.e(TAG, "Couldn't create context");
            return false;
        }
        return true;
    }

    public static boolean createEGLSurface() {
        Log.i(TAG, "createEGLSurface");
        if (mEGLDisplay != null && mEGLConfig != null) {
            if (mEGLContext == null)
                createEGLContext();

            Log.v(TAG, "Creating new EGL Surface");
            EGLSurface surface = mEgl.eglCreateWindowSurface(mEGLDisplay,
                    mEGLConfig, showView, null);
            if (surface == EGL10.EGL_NO_SURFACE) {
                Log.e(TAG, "Couldn't create surface");
                return false;
            }

            if (!mEgl
                    .eglMakeCurrent(mEGLDisplay, surface, surface, mEGLContext)) {
                Log.e(TAG, "Old EGL Context doesnt work, trying with a new one");
                createEGLContext();
                if (!mEgl.eglMakeCurrent(mEGLDisplay, surface, surface,
                        mEGLContext)) {
                    Log.e(TAG, "Failed making EGL Context current");
                    return false;
                }
            }
            mEGLSurface = surface;
            return true;
        }

        return false;
    }

    public static void ReleaseOpenGL() {
        if (mEgl == null) return;
        // 棣栧厛瑙ｉ櫎display,surface绛夐〉闈㈠拰鎺ュ彛鐨勯偊瀹�
        mEgl.eglMakeCurrent(EGL10.EGL_NO_DISPLAY, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

        // 鎺ョ潃鍒犻櫎鍚勪釜椤甸潰
        if (mEGLContext != null) {
            mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEGLContext = null;
        }

        if (mEGLSurface != null) {
            mEgl.eglDestroySurface(mEGLDisplay, mEGLSurface);
            mEGLSurface = null;
        }

        if (mEGLDisplay != null) {
            mEgl.eglTerminate(mEGLDisplay);
            mEGLDisplay = null;
        }
        Log.i(TAG, "ReleaseOpenGL");
    }

    /**
     * +++++++++++++++++++++++SDL++++++++++++++++++++++
     ************************************************/

    public static native int iSetNPCSettings(int iNPCID, int iPassword,
                                             int iMsgID, int iSettingID, int iSettingValue);

    public static native int iGetNPCSettings(int iNPCID, int iPassword,
                                             int iMsgID);

    public static void vRetNPCSettings(int iSrcID, int iCount,
                                       int[] iSettingID, int[] iValue, int iResult) {
        if (settingInterface != null) {
            settingInterface.vRetSettingEx(iSrcID, iCount, iSettingID, iValue, iResult);
        }
//		if (iResult == 1) {
//			Log.e("my", "鑾峰彇");
//			for (int i = 0; i < iCount; i++) {
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_DEFENCE) {
//					settingInterface.vRetGetRemoteDefenceResult("" + iSrcID,
//							iValue[i]);
//					if (settingInterface == null) {
//						Log.e("settingInterface", "settingInterface is null"
//								+ "iSrcID=" + iSrcID);
//					}
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_BUZZER) {
//					settingInterface.vRetGetBuzzerResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_RECORD) {
//					settingInterface.vRetGetRemoteRecordResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_MOTION_DECT) {
//					settingInterface.vRetGetMotionResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VIDEO_FORMAT) {
//					settingInterface.vRetGetVideoFormatResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TYPE) {
//					settingInterface.vRetGetRecordTypeResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TIME) {
//					settingInterface.vRetGetRecordTimeResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_NET_TYPE) {
//					/*
//					 * iValue[i]>>16 鏈夌嚎锛� wifi:2 閮芥湁锛�
//					 */
//					settingInterface.vRetGetNetTypeResult(iValue[i] & 0xffff);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VOLUME) {
//					settingInterface.vRetGetVideoVolumeResult(iValue[i]);
//					continue;
//				}
//
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_PLAN_TIME) {
//					int time = iValue[i];
//					settingInterface.vRetGetRecordPlanTimeResult(MyUtils
//							.convertPlanTime(time));
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_IMAGE_REVERSE) {
//					settingInterface.vRetGetImageReverseResult(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_IR_ALARM_EN) {
//					settingInterface.vRetGetInfraredSwitch(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_IN_EN) {
//					settingInterface.vRetGetWiredAlarmInput(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_OUT_EN) {
//					settingInterface.vRetGetWiredAlarmOut(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SECUPGDEV) {
//					settingInterface.vRetGetAutomaticUpgrade(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_TIMEZONE) {
//					settingInterface.vRetGetTimeZone(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.STTING_ID_GET_AUDIO_DEVICE_TYPE) {
//					settingInterface.vRetGetAudioDeviceType(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_PRERECORD) {
//					settingInterface.vRetGetPreRecord(iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_LAMP) {
//					settingInterface.vRecvGetLAMPStatus(String.valueOf(iSrcID),iValue[i]);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_GUEST_PASSWD) {
//					// settingInterface.vRetNPCVistorPwd(iValue[i]);不再使用21返回访客密码
//					int k = -1;
//					for (int j = 0; j < iCount; j++) {
//						if (iSettingID[j] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_VISITORPWD) {
//							k = iValue[j];
//							break;
//						}
//					}
//					settingInterface.vRetNPCVistorPwd(k);
//					continue;
//				}
//				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_FOCUS_ZOOM) {
//					settingInterface.vRetFocusZoom(String.valueOf(iSrcID),iValue[i]);
//					continue;
//				}
//
//				if(iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_CHECK_AP_MODE_SURPPORT){
//					settingInterface.vRetAPModeSurpport(String.valueOf(iSrcID), iValue[i]);
//					continue;
//				}
//				if(iSettingID[i]==Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_DEVICE_TYPE){
//					byte[] type=MyUtils.intToByte4(iValue[i]);
//					int mainType=MyUtils.bytes2ToInt(type, 0);
//					int subType=MyUtils.bytes2ToInt(type, 2);
//					Log.e("leleTestType", "iSrcID="+String.valueOf(iSrcID)+"--"+"MainType="+mainType+"--"+"subType="+subType);
//					settingInterface.vRetDeviceType(String.valueOf(iSrcID), mainType, subType);
//				}
//				if(iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEFENCE_SWITCH){
//					settingInterface.vRetGetDefenceSwitch(iValue[i]);
//					continue;
//				}
//
//			}
//		} else {
//			if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VIDEO_FORMAT) {
//				settingInterface.vRetSetVideoFormatResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VOLUME) {
//				settingInterface.vRetSetVolumeResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_BUZZER) {
//				settingInterface.vRetSetBuzzerResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TYPE) {
//				settingInterface.vRetSetRecordTypeResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_MOTION_DECT) {
//				settingInterface.vRetSetMotionResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TIME) {
//				settingInterface.vRetSetRecordTimeResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_PLAN_TIME) {
//				settingInterface.vRetSetRecordPlanTimeResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_DEFENCE) {
//				if (settingInterface != null) {
//					settingInterface.vRetSetRemoteDefenceResult(String.valueOf(iSrcID),iResult);
//				}
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEVICE_PWD) {
//				settingInterface.vRetSetDevicePasswordResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_NET_TYPE) {
//				settingInterface.vRetSetNetTypeResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_RECORD) {
//				settingInterface.vRetSetRemoteRecordResult(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_IMAGE_REVERSE) {
//				settingInterface.vRetSetImageReverse(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_IR_ALARM_EN) {
//				settingInterface.vRetSetInfraredSwitch(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_IN_EN) {
//				settingInterface.vRetSetWiredAlarmInput(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_OUT_EN) {
//				settingInterface.vRetSetWiredAlarmOut(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SECUPGDEV) {
//				settingInterface.vRetSetAutomaticUpgrade(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_GUEST_PASSWD) {
//				settingInterface.vRetSetVisitorDevicePassword(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_TIMEZONE) {
//				settingInterface.vRetSetTimeZone(iResult);
//			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_PRERECORD) {
//				settingInterface.vRetSetPreRecord(iResult);
//			} else if (iSettingID[0] == 34) {
//				settingInterface.vRecvSetLAMPStatus(String.valueOf(iSrcID),iResult);
//			}else if(iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SET_WIFI_WORK_MODE){
//				settingInterface.vRetSetWifiMode(String.valueOf(iSrcID), iResult);
//			}else if(iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEFENCE_SWITCH){
//				settingInterface.vRetSetDefenceSwitch(iResult);
//			}
//		}

    }

    public static void vRetFriendsStatus(final int iFriendsCount,
                                         final int[] iIDArray, final byte[] bStatus, final byte[] bType) {
        String[] threeNumbers = new String[iFriendsCount];
        int[] status = new int[iFriendsCount];
        int[] types = new int[iFriendsCount];

        for (int i = 0; i < iFriendsCount; i++) {
            int id = iIDArray[i] & 0x7FFFFFFF;
            int state = bStatus[i] & 0x0f;
            int type = bType[i] & 0x0f;

            status[i] = state;
            types[i] = type;
            if ((iIDArray[i] & 0x80000000) != 0) {
                threeNumbers[i] = "0" + id;
            } else {
                threeNumbers[i] = "" + id;
            }

        }

        settingInterface.vRetGetFriendStatus(iFriendsCount, threeNumbers,
                status, types);
    }

    public static void vRetMessage(int srcID, int iLen, byte[] cString) {
        /* ***********************************
		 * ***********************************
		 * ***********************************
		 * ***********************************
		 * ***********************************
		 * **********************************
		 */
        int id = srcID & 0x7FFFFFFF;

        if (id == 10000) {
            settingInterface.vRetSysMessage(new String(cString));
            return;
        }
        settingInterface.vRetMessage("0" + String.valueOf(id), new String(
                cString));
    }

    public static native int iGetFriendsStatus(int[] data, int iFriendNS);

    public static native int iSendMesgToFriend(int iDesID, int iMesgID,
                                               byte[] data, int datasize);

    public static native int iGetRecFiles(int iNPCID, int iPassword,
                                          int iMesgID, int iStartDateTime, int iEndDateTime);

    public static void vRetRecordFilesList(int id, int count, byte[] bytes) {
        String name = new String(bytes);
        String[] names = name.split("\\|");
        String[] names_moveEndNull = new String[count];
        System.arraycopy(names, 0, names_moveEndNull, 0, count);
        settingInterface.vRetGetRecordFiles(names_moveEndNull);
    }

    public static native int iRecFilePlayingControl(int iCommand, int iParm,
                                                    byte[] filename);

    public static native int iLocalVideoControl(int iCommand);

    public static void vRetPlayingStatus(int iStatus) {
        p2pInterface.vRetPlayBackStatus(iStatus);
    }

    public static void vRetPlayingPos(int iLength, int iCurrentSec) {
        p2pInterface.vRetPlayBackPos(iLength, iCurrentSec);
    }

    public static void vRetPlayingSize(int iWidth, int iHeight) {
        p2pInterface.vRetPlaySize(iWidth, iHeight);
    }

    public static void vRetPlayingNumber(int iNumber) {
        p2pInterface.vRetPlayNumber(iNumber);
    }

    public static native int iSetNPCDateTime(int iNpcID, int iPassword,
                                             int iMesgID, int iTime);

    public static native int iGetNPCDateTime(int iNpcID, int iPassword,
                                             int iMesgID);

    public static native int iGetNPCEmail(int iNpcID, int iPassword, int iMesgID);

    public static native int iSetNPCEmail(int iNpcID, int iPassword,
                                          int iMesgID, byte[] data, int iLen);

    public static void vRetEmail(int srcID, int iLen, byte[] cString, int result) {
        byte option = (byte) ((result >> 0) & (0x1));
        if (option == 1) {
            // 鑾峰彇鎴愬姛
            String email = new String(cString);
            // settingInterface.vRetAlarmEmailResult(result, email);
        } else {
            // settingInterface.vRetAlarmEmailResult(result, "");
        }
    }

    public static native int iGetNPCWifiList(int iNpcID, int iPassword,
                                             int iMesgID);

    /*
     * @param data1 Wifi Name
     *
     * @param data2 Wifi password
     */
    public static native int iSetNPCWifi(int iNpcID, int iPassword,
                                         int iMesgID, int iType, byte[] data1, int iLen1, byte[] data2,
                                         int iLen2);

    public static native int iGetNPCIpConfig(int iNpcID, int iPassword,
                                             int iMesgID, int iType, byte[] data1, int iLen1, byte[] data2,
                                             int iLen2);

    public static void vRetNPCWifiList(int srcID, int iCurrentId, int iCount,
                                       int[] iType, int[] iStrength, byte[] cString, int iResult) {

        String strbuffer = "--";
        for (int j = 0; j < cString.length; j++) {
            if (cString[j] == 0) {
                Log.e("wifidata", strbuffer);
                strbuffer = "--";
            }
            strbuffer = strbuffer + "  " + cString[j];
        }
        // for(int i=0;i<cString.length;i++){
        // if(cString[i]==0){
        // Log.e("data", "/");
        // Log.e("data", strbuffer);
        // strbuffer = "--";
        // continue;
        // }
        // if(cString[i]/16 == 10 )
        // strbuffer = strbuffer + "A";
        // else if(cString[i]/16 == 11 )
        // strbuffer = strbuffer + "B";
        // else if(cString[i]/16 == 12 )
        // strbuffer = strbuffer + "C";
        // else if(cString[i]/16 == 13 )
        // strbuffer = strbuffer + "D";
        // else if(cString[i]/16 == 14 )
        // strbuffer = strbuffer + "E";
        // else if(cString[i]/16 == 15 )
        // strbuffer = strbuffer + "F";
        // strbuffer = strbuffer + (cString[i]/16);
        //
        // if(cString[i]*16/16 == 10 )
        // strbuffer = strbuffer + "A";
        // else if(cString[i]*16/16 == 11 )
        // strbuffer = strbuffer + "B";
        // else if(cString[i]*16/16 == 12 )
        // strbuffer = strbuffer + "C";
        // else if(cString[i]*16/16 == 13 )
        // strbuffer = strbuffer + "D";
        // else if(cString[i]*16/16 == 14 )
        // strbuffer = strbuffer + "E";
        // else if(cString[i]*16/16 == 15 )
        // strbuffer = strbuffer + "F";
        // strbuffer = strbuffer + (cString[i]*16/16);
        //
        // strbuffer = strbuffer + " ";
        //
        // }
        if (iResult == 1) {
            String[] names;
            try {
                names = new String(cString, "UTF-8").split("\0");
                settingInterface.vRetWifiResult(iResult, iCurrentId, iCount,
                        iType, iStrength, names);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            settingInterface.vRetWifiResult(iResult, 0, 0, null, null, null);
        }

    }

    public static native int iGetAlarmCodeStatus(int iNpcID, int iPassword,
                                                 int iMesgID);

    public static native int iSetAlarmCodeStatus(int iNpcID, int iPassword,
                                                 int iMesgID, int iCount, int iType, int[] iGroup, int[] iItem);

    public static void vRetAlarmCodeStatus(int srcID, int iCount, int key,
                                           byte[] bData, int iResult) {
        if (iResult == 1) {
            ArrayList<int[]> data = new ArrayList<int[]>();
            int[] status_key = new int[8];
            status_key[0] = (key >> 0) & 0x1;
            status_key[1] = (key >> 1) & 0x1;
            status_key[2] = (key >> 2) & 0x1;
            status_key[3] = (key >> 3) & 0x1;
            status_key[4] = (key >> 4) & 0x1;
            status_key[5] = (key >> 5) & 0x1;
            status_key[6] = (key >> 6) & 0x1;
            status_key[7] = (key >> 7) & 0x1;
            Log.e("area", status_key[0] + " " + status_key[1] + " "
                    + status_key[2] + " " + status_key[3] + " " + status_key[4]
                    + " " + status_key[5] + " " + status_key[6] + " "
                    + status_key[7] + " ");
            data.add(0, status_key);
            for (int i = 0; i < iCount; i++) {
                byte b = bData[i];
                int[] status = new int[8];
                status[0] = (b >> 0) & 0x1;
                status[1] = (b >> 1) & 0x1;
                status[2] = (b >> 2) & 0x1;
                status[3] = (b >> 3) & 0x1;
                status[4] = (b >> 4) & 0x1;
                status[5] = (b >> 5) & 0x1;
                status[6] = (b >> 6) & 0x1;
                status[7] = (b >> 7) & 0x1;
                Log.e("area", status[0] + " " + status[1] + " " + status[2]
                        + " " + status[3] + " " + status[4] + " " + status[5]
                        + " " + status[6] + " " + status[7] + " ");
                data.add(i + 1, status);
            }

            settingInterface.vRetDefenceAreaResult(iResult, data, 0, 0);
        } else {
            int group = bData[0];
            int item = bData[4];
            settingInterface.vRetDefenceAreaResult(iResult, null, group, item);
        }

    }

    public static native int iGetBindAlarmId(int iNpcID, int iPassword,
                                             int iMesgID);

    public static native int iSetBindAlarmId(int iNpcID, int iPassword,
                                             int iMesgID, int iCount, int[] iData);

    public static void vRetBindAlarmId(int srcID, int iMaxCount, int iCount,
                                       int[] iData, int iResult) {
        if (iResult == 1) {
            if (iCount == 1 && iData[0] == 0) {
                settingInterface.vRetBindAlarmIdResult(srcID, iResult,
                        iMaxCount, new String[0]);
            } else {
                String[] new_data = new String[iData.length];
                for (int i = 0; i < iData.length; i++) {
                    new_data[i] = "0" + iData[i];
                }
                settingInterface.vRetBindAlarmIdResult(srcID, iResult,
                        iMaxCount, new_data);
            }
        } else {
            settingInterface.vRetBindAlarmIdResult(srcID, iResult, 0, null);
        }

    }

    public static void vRetDeviceNotSupport(int iNpcId) {
        Log.e("my", "device not support:" + iNpcId);
        settingInterface.vRetDeviceNotSupport();
    }

    public static native void ChangeScreenSize(int windowWidth,
                                               int windowHeight, int isFullScreen);

    public static native int ZoomView(int x, int y, float fScale);

    public static native int MoveView(int DetaX, int DetaY);

    public static native int iSetInitPassword(int iNpcID, int iPassword,
                                              int iMesgID, int iNewPassword, byte[] rtspPwd, int AppID, int pwdLen, byte[] EntryPwd);

    public static void vRetInitPassword(int iNpcId, int iResult) {
        settingInterface.vRetSetInitPasswordResult(iResult);
    }

    public static void vRetAlarm(int iSrcId, int iType,
                                 int isSupportExternAlarm, int iGroup, int iItem) {
        boolean bool = false;
        boolean isSupportDelete = false;
        if ((isSupportExternAlarm & 0x1) == 1) {
            bool = true;
        } else {
            bool = false;
        }
        Log.e("dxsAlarmActivity", "iSrcId-->" + iSrcId + "iType-->" + iType
                + "--isSupportExternAlarm-->" + isSupportExternAlarm);
        if (((isSupportExternAlarm >> 2) & (0x1)) == 1) {
            isSupportDelete = true;
        } else {
            isSupportDelete = false;
        }
        if (iGroup > 8) {
            isSupportExternAlarm = 0;
        }

        p2pInterface.vAllarming(String.valueOf(iSrcId), iType, bool, iGroup,
                iItem, isSupportDelete);
    }

	/* ***********************************************************************
	 * SDK
	 * ***********************************************************************
	 */

    // 璁剧疆璁惧鏃堕棿鍥炶皟
    public static void vRetNPCTime(int iTime, int result) {
        if (result == 1) {
            // 鑾峰彇鎴愬姛
            settingInterface.vRetGetDeviceTimeResult(MyUtils
                    .convertDeviceTime(iTime));
        } else {
            settingInterface.vRetSetDeviceTimeResult(result);
        }
    }

    public static native int iSendCmdToFriend(int iDesID, int iPassword,
                                              int iMesgID, byte[] data, int datasize);

    // 鑷畾涔夊懡浠�
    public static void vRetCustomCmd(int srcID, int iLen, byte[] cString) {
        int id = srcID & 0x7FFFFFFF;
        settingInterface.vRetCustomCmd(id, iLen, cString);
    }

    public static native int iSetVideoMode(int type);

    public static native void checkDeviceUpdate(int iNpcID, int iPassword,
                                                int iMesgID);

    public static void vRetCheckDeviceUpdate(int iSrcID, int result,
                                             int iCurVersion, int iUpgVersion) {
        int a = iCurVersion & 0xff;
        int b = (iCurVersion >> 8) & 0xff;
        int c = (iCurVersion >> 16) & 0xff;
        int d = (iCurVersion >> 24) & 0xff;

        String cur_version = d + "." + c + "." + b + "." + a;

        int e = iUpgVersion & 0xff;
        int f = (iUpgVersion >> 8) & 0xff;
        int g = (iUpgVersion >> 16) & 0xff;
        int h = (iUpgVersion >> 24) & 0xff;

        String upg_version = h + "." + g + "." + f + "." + e;
        String contactId = String.valueOf(iSrcID);
        settingInterface.vRetCheckDeviceUpdate(contactId, result, cur_version,
                upg_version);
    }

    public static native void doDeviceUpdate(int iNpcID, int iPassword,
                                             int iMesgID);

    public static void vRetDoDeviceUpdate(int iSrcID, int result, int value) {

        settingInterface.vRetDoDeviceUpdate(String.valueOf(iSrcID), result,
                value);
    }

    public static native void cancelDeviceUpdate(int iNpcID, int iPassword,
                                                 int iMesgID);

    public static void vRetCancelDeviceUpdate(int iSrcID, int result) {

        settingInterface.vRetCancelDeviceUpdate(result);
    }

    public static native int iSendCtlCmd(int iCmd, int iOption);

    public static native void getDeviceVersion(int iNpcID, int iPassword,
                                               int iMesgID);

    public static void vRetGetDeviceVersion(int iSrcID, int result,
                                            int iCurVersion, int iUbootVersion, int iKernelVersion,
                                            int iRootfsVersion) {
        int a = iCurVersion & 0xff;
        int b = (iCurVersion >> 8) & 0xff;
        int c = (iCurVersion >> 16) & 0xff;
        int d = (iCurVersion >> 24) & 0xff;

        String cur_version = d + "." + c + "." + b + "." + a;
        settingInterface.vRetGetDeviceVersion(result, cur_version,
                iUbootVersion, iKernelVersion, iRootfsVersion);
    }

    public static native void setBindFlag(int flag);

    public static void vGXNotifyFlag(int flag) {
        p2pInterface.vGXNotifyFlag(flag);
    }

    public static native int iClearAlarmCodeGroup(int iNpcID, int iPassword,
                                                  int iMesgID, int iGroup);

    public static void vRetClearAlarmCodeGroup(int iSrcID, int result) {
        settingInterface.vRetClearDefenceAreaState(result);
    }

    public static native int iExtendedCmd(int iTargetID, int iPassword,
                                          int iMesgID, byte[] data, int datasize);

    public static final int MESG_TYPE_GET_DISK_INFO = 80;
    public static final int MESG_TYPE_FORMAT_DISK = 81;
    public static final int MESG_SDCARD_NO_EXIST = 82;
    public static final int MESG_SET_GPIO_INFO = 96;
    public static final int MESG_TYPE_RET_DEFENCE_SWITCH_STATE = 84;

    public static final int MESG_TYPE_GET_ALARM_TYPE_MOTOR_PRESET_POS = 89;// 预置位
    public static final int MESG_TYPE_SET_MOTOR_PRESET_POS = 87;// 预置位
    public static final int MESG_TYPE_LOOK_MOTOR_PRESET_POS = 88;// 返回预置位
    public static final int MESG_TYPE_RET_ALARM_TYPE_MOTOR_PRESET_POS = 91;// 返回设置于获取预置位
    public static final int IP_CONFIG = 105;// 返回IP信息

    public static final int MESG_GET_ALARM_CENTER_PARAMETER = 102;
    public static final int MESG_DELETE_ALARMDEVICEID = 128;// 删除设备绑定报警推送ID

    public static final int MESG_TYPE_RET_LAN_IPC_LIST = 130;// 返回NVR的ipc列表

    public static final int MESG_TYPE_RET_NVR_DEV_INFO = 132;//获取NVR信息
    public static final int MESG_TYPE_FISHEYE_SETTING = 149;// 鱼眼设置
    public static final int MESG_TYPE_FISHEYE_MEMBERLIST = 221;// 鱼眼获取列表
    public static final int MESG_TYPE_DEVIECE_NOT_SUPPORT_RET = 255;
    public static final int MESG_TYPE_RET_DEVICE_LANGUAGE = 213;
    public static final int MESG_TYPE_RET_FOCUS_ZOOM = 224;
    public static final int MESG_GET_DEFENCE_WORK_GROUP = 214;//获取定时布防列表
    public static final int MESG_SET_DEFENCE_WORK_GROUP = 215;//设置定时布防列表
    public static final int MESG_FTP_CONFIG_INFO = 227;//FTP获取设置信息返回

    public static final int MESG_DEFENCE_AREA_NAME = 223;//防区通道名设置和返回

    public static final int MESG_TYPE_USER_GPIO_CONTROL_RET = 181;
    public static final int MESG_TYPE_USER_GET_GPIO_STAT_RET = 186;

    public static void vRetExtenedCmd(int iSrcID, byte[] data, int datasize) {
        Log.e("vRetExtenedCmd", "vRetExtenedCmd-->" + "iSrcID=" + iSrcID + "--" + Arrays.toString(data));
        if (settingInterface != null) {
            settingInterface.vRetExtenedCmd(iSrcID, data, datasize);
        }
//		int option = 0;
//		if (data[0] < 0) {
//			option = data[0] + 256;
//		} else {
//			option = data[0];
//		}
//		if (option == MESG_TYPE_GET_DISK_INFO) {
//			if (data[1] == MESG_SDCARD_NO_EXIST) {
//				settingInterface.vRetGetSdCard(0, 0, 0, 0);
//			} else {
//				int DiskCount;
//				int DiskID;
//				long TotalSpace, FreeSpace;
//				DiskCount = data[2] + data[3] * 256;
//				Log.e("2cu", "---" + DiskCount);
//				DiskID = data[4];
//				Log.e("diskid", "DiskID" + DiskID);
//
//				long[] longData = new long[8];
//				longData[0] = 0xFF & data[5];
//				longData[0] <<= 0;
//				longData[1] = 0xFF & data[6];
//				longData[1] <<= 8;
//				longData[2] = 0xFF & data[7];
//				longData[2] <<= 16;
//				longData[3] = 0xFF & data[8];
//				longData[3] <<= 24;
//				longData[4] = 0xFF & data[9];
//				longData[4] <<= 32;
//				longData[5] = 0xFF & data[10];
//				longData[5] <<= 40;
//				longData[6] = 0xFF & data[11];
//				longData[6] <<= 48;
//				longData[7] = 0xFF & data[12];
//				longData[7] <<= 56;
//
//				TotalSpace = longData[0] + longData[1] + longData[2]
//						+ longData[3] + longData[4] + longData[5] + longData[6]
//						+ longData[7];
//				TotalSpace = TotalSpace / 1024 / 1024; // MB
//
//				longData[0] = 0xFF & data[13];
//				longData[0] <<= 0;
//				longData[1] = 0xFF & data[14];
//				longData[1] <<= 8;
//				longData[2] = 0xFF & data[15];
//				longData[2] <<= 16;
//				longData[3] = 0xFF & data[16];
//				longData[3] <<= 24;
//				longData[4] = 0xFF & data[17];
//				longData[4] <<= 32;
//				longData[5] = 0xFF & data[18];
//				longData[5] <<= 40;
//				longData[6] = 0xFF & data[19];
//				longData[6] <<= 48;
//				longData[7] = 0xFF & data[20];
//				longData[7] <<= 56;
//
//				FreeSpace = longData[0] + longData[1] + longData[2]
//						+ longData[3] + longData[4] + longData[5] + longData[6]
//						+ longData[7];
//				FreeSpace = FreeSpace / 1024 / 1024; // MB
//
//				Log.e("2cu", "TotalSpace=" + TotalSpace);
//				Log.e("2cu", "FreeSpace=" + FreeSpace);
//
//				settingInterface.vRetGetSdCard((int) TotalSpace,
//						(int) FreeSpace, DiskID, 1);
//
//				if (DiskCount > 1) {
//					DiskID = data[21];
//					Log.e("diskid", "DiskID" + DiskID);
//					longData[0] = 0xFF & data[22];
//					longData[0] <<= 0;
//					longData[1] = 0xFF & data[23];
//					longData[1] <<= 8;
//					longData[2] = 0xFF & data[24];
//					longData[2] <<= 16;
//					longData[3] = 0xFF & data[25];
//					longData[3] <<= 24;
//					longData[4] = 0xFF & data[26];
//					longData[4] <<= 32;
//					longData[5] = 0xFF & data[27];
//					longData[5] <<= 40;
//					longData[6] = 0xFF & data[28];
//					longData[6] <<= 48;
//					longData[7] = 0xFF & data[29];
//					longData[7] <<= 56;
//
//					TotalSpace = longData[0] + longData[1] + longData[2]
//							+ longData[3] + longData[4] + longData[5]
//							+ longData[6] + longData[7];
//					TotalSpace = TotalSpace / 1024 / 1024; // MB
//
//					longData[0] = 0xFF & data[30];
//					longData[0] <<= 0;
//					longData[1] = 0xFF & data[31];
//					longData[1] <<= 8;
//					longData[2] = 0xFF & data[32];
//					longData[2] <<= 16;
//					longData[3] = 0xFF & data[33];
//					longData[3] <<= 24;
//					longData[4] = 0xFF & data[34];
//					longData[4] <<= 32;
//					longData[5] = 0xFF & data[35];
//					longData[5] <<= 40;
//					longData[6] = 0xFF & data[36];
//					longData[6] <<= 48;
//					longData[7] = 0xFF & data[37];
//					longData[7] <<= 56;
//
//					FreeSpace = longData[0] + longData[1] + longData[2]
//							+ longData[3] + longData[4] + longData[5]
//							+ longData[6] + longData[7];
//					FreeSpace = FreeSpace / 1024 / 1024; // MB
//
//					Log.e("2cu", "TotalSpace=" + TotalSpace);
//					Log.e("2cu", "FreeSpace=" + FreeSpace);
//					settingInterface.VRetGetUsb((int) TotalSpace,
//							(int) FreeSpace, DiskID, 1);
//				}
//
//			}
//
//		} else if (option == MESG_TYPE_FORMAT_DISK) {
//			settingInterface.vRetSdFormat(data[1]);
//		} else if (option == MESG_SET_GPIO_INFO) {
//			int result = -1;
//			if (data[1] < 0) {
//				result = (data[1] + 256);
//			} else {
//				result = data[1];
//			}
//			if (result==0) {
//				settingInterface.vRetSetGPIO(String.valueOf(iSrcID),result);
//			}else{
//				settingInterface.vRetGetGPIO(String.valueOf(iSrcID), result, data[4]);
//			}
//		} else if (option == MESG_TYPE_RET_DEFENCE_SWITCH_STATE) {
//			if (data[1] == 1) {
//				ArrayList<int[]> sensors = new ArrayList<int[]>();
//				for (int i = 4; i < data.length; i++) {
//					if (data[i] < 0) {
//						String sensor = Integer.toBinaryString(data[i] + 256);
//						int[] sensor_switchs = new int[8];
//						ArrayList<Integer> list = new ArrayList<Integer>();
//						if (sensor.length() < 8) {
//							for (int k = 0; k < 8 - sensor.length(); k++) {
//								list.add(0);
//							}
//						}
//						for (int j = 0; j < sensor.length(); j++) {
//							list.add(Integer.parseInt(sensor
//									.substring(j, j + 1)));
//						}
//						Log.e("length", "list_size" + list.size());
//						String s = "";
//						for (int k = 0; k < list.size(); k++) {
//							sensor_switchs[k] = list.get(k);
//							s = s + sensor_switchs[k];
//						}
//						Log.e("length", s);
//						// Log.e("length",
//						// "sensor="+sensor+"length="+sensor.length());
//						// int [] sensor_switchs=new int[8];
//						// if(sensor.length()<8){
//						// for(int k=0;k<8-sensor.length();k++){
//						// sensor_switchs[k]=0;
//						// }
//						// }
//						// for(int j=8-sensor.length();j<8;j++){
//						// sensor_switchs[j]=Integer.parseInt(sensor.substring(j,
//						// j+1));
//						// }
//						sensors.add(sensor_switchs);
//					} else {
//						String sensor = Integer.toBinaryString(data[i]);
//						int[] sensor_switchs = new int[8];
//						ArrayList<Integer> list = new ArrayList<Integer>();
//						if (sensor.length() < 8) {
//							for (int k = 0; k < 8 - sensor.length(); k++) {
//								list.add(0);
//							}
//						}
//						for (int j = 0; j < sensor.length(); j++) {
//							list.add(Integer.parseInt(sensor
//									.substring(j, j + 1)));
//						}
//						Log.e("length", "list_size" + list.size());
//						String s = "";
//						for (int k = 0; k < list.size(); k++) {
//							sensor_switchs[k] = list.get(k);
//							s = s + sensor_switchs[k];
//						}
//						Log.e("length", s);
//						// Log.e("length",
//						// "sensor="+sensor+"length="+sensor.length());
//						// int [] sensor_switchs=new int[8];
//						// if(sensor.length()<8){
//						// for(int k=0;k<8-sensor.length();k++){
//						// sensor_switchs[k]=0;
//						// }
//						// }
//						// for(int j=8-sensor.length();j<8;j++){
//						// sensor_switchs[j]=Integer.parseInt(sensor.substring(j,
//						// j+1));
//						// }
//						sensors.add(sensor_switchs);
//					}
//				}
//				settingInterface.vRetGetSensorSwitchs(1, sensors);
//			} else if (data[1] == 0) {
//				settingInterface.vRetSetSensorSwitchs(0);
//			} else if (data[1] == 41) {
//				settingInterface.vRetGetSensorSwitchs(41,
//						new ArrayList<int[]>());
//			} else if (data[1] == 88) {
//				settingInterface.vRetSetSensorSwitchs(88);
//			}
//		} else if (option == MESG_TYPE_RET_ALARM_TYPE_MOTOR_PRESET_POS) {
//			settingInterface.vRetAlarmPresetMotorPos(data);
//		} else if (option == MESG_TYPE_LOOK_MOTOR_PRESET_POS) {
//			settingInterface.vRetPresetMotorPos(data);
//		} else if (option == IP_CONFIG) {
//			settingInterface.vRetIpConfig(data);
//		} else if (option == MESG_GET_ALARM_CENTER_PARAMETER) {
//			Log.e("sddata", "---");
//			if (data[1] == 1) {
//				int[] newdata = new int[data.length];
//				for (int i = 2; i < data.length; i++) {
//					if (data[i] < 0) {
//						newdata[i] = data[i] + 256;
//					} else {
//						newdata[i] = data[i];
//					}
//					Log.e("sddate", "newdata[i]=" + newdata[i]);
//				}
//				for (int j = 0; j < data.length; j++) {
//					Log.e("sddate", "data[j]" + data[j] + "---" + data.length);
//				}
//				int state = data[4];
//				int ipdress1 = (data[8] + 256) % 256;
//				int ipdress2 = (data[9] + 256) % 256;
//				int ipdress3 = (data[10] + 256) % 256;
//				int ipdress4 = (data[11] + 256) % 256;
//				String ipdress = ipdress4 + "." + ipdress3 + "." + ipdress2
//						+ "." + ipdress1;
//				Log.e("ipdress", ipdress);
//				int port = newdata[12] | newdata[13] << 8 | newdata[14] << 16
//						| newdata[15] << 24;
//				String userId;
//				// if(newdata.length>=24){
//				// long
//				// user_Id=newdata[16]|newdata[17]<<8|newdata[18]<<16|newdata[19]<<24|newdata[20]<<32|newdata[21]<<40|newdata[22]<<48|newdata[23]<<56;
//				// userId="0x"+Long.toHexString(user_Id).toString().toUpperCase();
//				// }else{
//				int user_Id = newdata[16] | newdata[17] << 8
//						| newdata[18] << 16 | newdata[19] << 24;
//				userId = "0x"
//						+ Integer.toHexString(user_Id).toString().toUpperCase();
//				// }
//				Log.e("alarm_center", "userId=" + userId);
//				Log.e("sddata", "state=" + state + " " + "ipdress=" + ipdress
//						+ " " + "port" + port + " " + "userId" + userId);
//				settingInterface.vRetGetAlarmCenter(1, state, ipdress, port,
//						userId);
//			} else if (data[1] == 0) {
//				settingInterface.vRetSetAlarmCenter(data[1]);
//			} else {
//				settingInterface.vRetSetAlarmCenter(data[1]);
//			}
//		} else if (option == MESG_DELETE_ALARMDEVICEID) {
//			settingInterface.vRetDeleteDeviceAlarmID(data[1],data[8]);
//			Log.e("leledelete", "id="+iSrcID+"--"+"result="+data[1]+"data[8]="+data[8]);
//		} else if (option == MESG_TYPE_RET_DEVICE_LANGUAGE) {
//			int result = data[1];
//			int languegecount = data[4];
//			int curlanguege = data[5];
//			int[] langueges = new int[languegecount];
//			for (int i = 0; i < langueges.length; i++) {
//				if (data.length > i + 6) {
//					langueges[i] = data[i + 6];
//				}
//			}
//			Log.e("languege", "result=" + result + "--" + "languegecount="
//					+ languegecount + "--" + "curlanguege=" + curlanguege
//					+ "--" + "--" + "langueges_length=" + langueges.length);
//			settingInterface.vRetDeviceLanguege(result, languegecount,
//					curlanguege, langueges);
//		} else if (option == MESG_TYPE_FISHEYE_SETTING) {
//			settingInterface.vRetFishEyeData(iSrcID, data, datasize);
//		} else if (option == MESG_TYPE_FISHEYE_MEMBERLIST) {
//			data[1] = 45;// 由于鱼眼获取用户列表结构改变，复用之前的广播
//			settingInterface.vRetFishEyeData(iSrcID, data, datasize);
//		} else if (option == MESG_TYPE_RET_LAN_IPC_LIST) {
//			int result = -1;
//			if (data[1] < 0) {
//				result = (data[1] + 256);
//			} else {
//				result = data[1];
//			}
//			byte[] list_data = new byte[data.length - 6];
//			int number = data[2] | data[3] << 8
//					| data[4] << 16 | data[5] << 24;
//			System.arraycopy(data, 6, list_data, 0, list_data.length);
//			String s_list = "";
//			try {
//				s_list = new String(list_data, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			String[] ipc_list = s_list.split(",");
//			String[] list=new String[number];
//			for(int i=0;i<number;i++){
//				list[i]=ipc_list[i];
//			}
//			Log.e("leleTest", "ipc list="+s_list);
//			settingInterface.vRetGetNvrIpcList(String.valueOf(iSrcID),list,number);
//
//		}else if(option == MESG_TYPE_RET_NVR_DEV_INFO){
//			settingInterface.vRetNVRInfo(iSrcID, data, datasize);
//		}else if(option==MESG_TYPE_RET_FOCUS_ZOOM){
//			if(data[1]==0){
//				int result=data[2];
//				int value=data[3];
//				settingInterface.vRetGetFocusZoom(String.valueOf(iSrcID), result, value);
//			}else if(data[1]==1){
//				int result=data[2];
//				int value=data[3];
//				settingInterface.vRetSetFocusZoom(String.valueOf(iSrcID), result, value);
//			}
//		}else if (option==MESG_GET_DEFENCE_WORK_GROUP) {
//			settingInterface.vRetGetDefenceWorkGroup(String.valueOf(iSrcID), data);
//		}else if (option==MESG_SET_DEFENCE_WORK_GROUP) {
//			settingInterface.vRetSetDefenceWorkGroup(String.valueOf(iSrcID), data);
//		}else if (option==MESG_FTP_CONFIG_INFO) {
//			settingInterface.vRetFTPConfigInfo(String.valueOf(iSrcID), data);
//		}else if (option==MESG_DEFENCE_AREA_NAME) {
//			settingInterface.vRetDefenceAreaName(String.valueOf(iSrcID), data);
//		} else if (option == MESG_TYPE_USER_GPIO_CONTROL_RET) {
//			settingInterface.vRecvSetGPIOStatus(String.valueOf(iSrcID), data);
//		}else if (option == MESG_TYPE_USER_GET_GPIO_STAT_RET) {
//			settingInterface.vRetGPIOStatus(String.valueOf(iSrcID), data);
//		}
//
//
    }

    public static native void vSendWiFiCmd(int iType, byte[] SSID,
                                           int iSSIDLen, byte[] Password, int iPasswordLen);

    public static native void SetSystemMessageIndex(int iSystemMessageType,
                                                    int iSystemMessageIndex);

    // jni实验
    public native long des_password();

    /**
     * @param iSystemMessageType
     * @param iSystemMessageIndex 商城版有没有新消息标记
     */
    public static void RetNewSystemMessage(int iSystemMessageType,
                                           int iSystemMessageIndex) {
        if (iSystemMessageType == Constants.SystemMessgeType.MALL_NEW
                && p2pInterface != null) {
            p2pInterface.vRetNewSystemMessage(iSystemMessageType,
                    iSystemMessageIndex);
        }
        Log.e("systemmessage", "type=" + iSystemMessageType + "  index"
                + iSystemMessageIndex);
    }

    /**
     * 监控暂停(需要库支持，不可用)
     *
     * @param supperdrop
     */
    public static native void SetSupperDrop(boolean supperdrop);

    /*
     * pwd:~##00~以##结束不足8位补齐0
     */
    public static void vRetEmailWithSMTP(int srcID, byte boption,
                                         String emailaddress, int port, String server, String user,
                                         byte[] pwd, String subject, String content, byte Entry,
                                         byte reserve1, int reserve2, int pwdlen) {
        Log.e("fang", "------------");
        String[] SmtpMessage = null;
        String pwds = null;
        try {
            String pwdTemp = new String(DES.des(pwd, 1));
            pwds = pwdTemp.substring(0, pwdTemp.lastIndexOf("##"));
            SmtpMessage = new String[]{server, user, pwds, subject, content,
                    String.valueOf(srcID)};

        } catch (Exception e) {
            SmtpMessage = new String[]{"", "", "", "", "",
                    String.valueOf(srcID)};
            e.printStackTrace();
        }
        settingInterface.vRetAlarmEmailResultWithSMTP(boption, emailaddress,
                port, Entry, SmtpMessage, reserve1);
        // Log.e("" +
        // "",
        // "srcID-->"+srcID+"--boption-->"+boption+"--emailaddress-->"+emailaddress+"--port-->"+port+"--server-->"+server+"--user-->"+user+"--Entry-->"+Entry);
    }

    public static native int SetRobortEmailNew(int iNpcID, int iPassword,
                                               int iMesgID, byte boption, String emailaddress, int port,
                                               String server, String user, byte[] pwd, String subject,
                                               String content, byte Entry, byte reserve1, int reserve2, int pwdlen);

    public static void vRetAlarmWithTime(int iSrcId, int iType,
                                         int isSupportExternAlarm, int iGroup, int iItem, int capNums,
                                         byte[] Times, byte[] alarmCapDirs, byte[] vedioPaths, byte[] SensorName, int DeviceType) {
        String TimeTemp = new String(Times);
        String Time = TimeTemp.replace("-", "");
        String alarmCapDir = new String(alarmCapDirs);
        String vedioPath = new String(vedioPaths);
        String filename = "G" + MyUtils.compleInteger(iGroup, 2)
                + MyUtils.compleInteger(iItem, 2) + Time
                + MyUtils.compleInteger(capNums, 2);
        alarmCapDir = alarmCapDir + filename;
        String name = new String(SensorName);
        p2pInterface.vAllarmingWitghTime(String.valueOf(iSrcId), iType,
                isSupportExternAlarm, iGroup, iItem, capNums, Time,
                alarmCapDir, vedioPath, name, DeviceType);
    }

    public static native int native_rtsp_call(long id, String rstp);

    public static native int SetScreenShotPath(String path, String name);

    public static native int SendUserData(int cmd, int option, byte[] data,
                                          int datalength);

    public static native int EntryPwd(String password);

    public static native int GetAllarmImage(int id, int password,
                                            String filename, String LocalName);

    public static void RetGetAllarmImage(int id, byte[] filename, int errorcode) {
        String file = new String(filename);
        settingInterface.vRetGetAllarmImage(id, file, errorcode);
    }

    public static native int GetFileProgress();

    public static native void CancelGetRemoteFile();

    public static native String RTSPEntry(String pwd);

    public static native String HTTPDecrypt(String userID, String pwd,
                                            int backLen);

    public static native String HTTPEncrypt(String userID, String pwd,
                                            int backLen);

    public static native byte[] P2PEntryPassword(byte[] pwd);

    public static native int iSetDevicePwd(int iNPCID, int iPassword,
                                           int iMsgID, int iSettingID, int iSettingValue, byte[] RTSPpwd, int pwdLen, byte[] Entrypwd);
}
