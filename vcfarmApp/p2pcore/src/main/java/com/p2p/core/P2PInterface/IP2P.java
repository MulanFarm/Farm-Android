package com.p2p.core.P2PInterface;

public interface IP2P {
	public void vCalling(boolean isOutCall, String threeNumber, int type);

	public void vReject(int reason_code);

	public void vAccept(int type,int state);

	public void vConnectReady();

	public void vAllarming(String srcId, int type, boolean isSupportExternAlarm, int iGroup, int iItem,boolean isSurpportDelete);

	public void vChangeVideoMask(int state);

	public void vRetPlayBackPos(int length, int currentPos);

	public void vRetPlayBackStatus(int state);

	public void vGXNotifyFlag(int flag);

	public void vRetPlaySize(int iWidth, int iHeight);

	public void vRetPlayNumber(int iNumber);

	public void vRecvAudioVideoData(byte[] AudioBuffer, int AudioLen, int AudioFrames, long AudioPTS,
			byte[] VideoBuffer, int VideoLen, long VideoPTS);
	public void vAllarmingWitghTime(String srcId, int type, int option, int iGroup, int iItem,int imagecounts,String imagePath,String alarmCapDir,String VideoPath,String sensorName,int deviceType);
	public void vRetNewSystemMessage(int iSystemMessageType,int iSystemMessageIndex);
	public void vRetRTSPNotify(int arg2,String msg);
	public void vRetPostFromeNative(int what,int iDesID, int arg1, int arg2, String msgStr);

}
