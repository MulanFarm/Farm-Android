package com.daweichang.vcfarm.utils;

import android.content.Intent;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.fragment.FarmFragment;
import com.p2p.core.P2PInterface.IP2P;
import com.xcc.mylibrary.Sysout;

/**
 * Created by wzy on 2016/6/13.
 */
public class P2PListener implements IP2P {
    public void vCalling(boolean isOutCall, String threeNumber, int type) {
        Sysout.e("--P2PListener--vCalling-->", threeNumber);
    }

    public void vReject(int reason_code) {
        Intent intent = new Intent();
        intent.setAction(FarmFragment.P2P_REJECT);
        intent.putExtra("reason_code", reason_code);
        AppVc.getAppVc().sendBroadcast(intent);
    }

    public void vAccept(int type, int state) {
        Intent accept = new Intent();
        accept.setAction(FarmFragment.P2P_ACCEPT);
        accept.putExtra("type", new int[]{type, state});
        AppVc.getAppVc().sendBroadcast(accept);
    }

    public void vConnectReady() {
        Intent intent = new Intent();
        intent.setAction(FarmFragment.P2P_READY);
        AppVc.getAppVc().sendBroadcast(intent);
    }

    public void vAllarming(String srcId, int type, boolean isSupportExternAlarm, int iGroup, int iItem, boolean isSurpportDelete) {
        Sysout.e("--P2PListener--vAllarming-->", srcId);
    }

    public void vChangeVideoMask(int state) {
        Sysout.e("--P2PListener--vChangeVideoMask-->", "" + state);
    }

    public void vRetPlayBackPos(int length, int currentPos) {
        Sysout.e("--P2PListener--vRetPlayBackPos-->", "" + currentPos);
    }

    public void vRetPlayBackStatus(int state) {
        Sysout.e("--P2PListener--vRetPlayBackStatus-->", "" + state);
    }

    public void vGXNotifyFlag(int flag) {
        Sysout.e("--P2PListener--vGXNotifyFlag-->", "" + flag);
    }

    public void vRetPlaySize(int iWidth, int iHeight) {
        Sysout.e("--P2PListener--vRetPlaySize-->", "" + iWidth);
    }

    public void vRetPlayNumber(int iNumber) {
        Sysout.e("--P2PListener--vRetPlayNumber-->", iNumber + "");
    }

    public void vRecvAudioVideoData(byte[] AudioBuffer, int AudioLen, int AudioFrames, long AudioPTS, byte[] VideoBuffer, int VideoLen, long VideoPTS) {
        Sysout.e("--P2PListener--vRecvAudioVideoData-->", "" + AudioLen);
    }

    public void vAllarmingWitghTime(String srcId, int type, int option, int iGroup, int iItem, int imagecounts, String imagePath, String alarmCapDir, String VideoPath, String sensorName, int deviceType) {
        Sysout.e("--P2PListener--vAllarmingWitghTime-->", "" + srcId);
    }

    public void vRetNewSystemMessage(int iSystemMessageType, int iSystemMessageIndex) {
        Sysout.e("--P2PListener--vRetNewSystemMessage-->", "" + iSystemMessageType);
    }

    public void vRetRTSPNotify(int arg2, String msg) {
        Sysout.e("--P2PListener--vRetRTSPNotify-->", msg);
    }

    public void vRetPostFromeNative(int what, int iDesID, int arg1, int arg2, String msgStr) {
        Sysout.e("--P2PListener--vRetPostFromeNative-->", msgStr);
    }
}
