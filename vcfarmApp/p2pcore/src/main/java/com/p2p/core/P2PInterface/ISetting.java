package com.p2p.core.P2PInterface;

import java.util.ArrayList;

public interface ISetting {
	// ACK 回调函数

	public void ACK_vRetSetDeviceTime(int msgId, int result);

	public void ACK_vRetGetDeviceTime(int msgId, int result);

	public void ACK_vRetGetNpcSettings(String contactId, int msgId, int result);

	public void ACK_vRetSetRemoteDefence(String contactId, int msgId, int result);

	public void ACK_vRetSetRemoteRecord(int msgId, int result);

	public void ACK_vRetSetNpcSettingsVideoFormat(int msgId, int result);

	public void ACK_vRetSetNpcSettingsVideoVolume(int msgId, int result);

	public void ACK_vRetSetNpcSettingsBuzzer(int msgId, int result);

	public void ACK_vRetSetNpcSettingsMotion(int msgId, int result);

	public void ACK_vRetSetNpcSettingsRecordType(int msgId, int result);

	public void ACK_vRetSetNpcSettingsRecordTime(int msgId, int result);

	public void ACK_vRetSetNpcSettingsRecordPlanTime(int msgId, int result);

	public void ACK_vRetSetNpcSettingsNetType(int msgId, int result);

	public void ACK_vRetSetAlarmEmail(int msgId, int result);

	public void ACK_vRetGetAlarmEmail(int msgId, int result);

	public void ACK_vRetSetAlarmBindId(int srcID, int result);

	public void ACK_vRetGetAlarmBindId(int srcID, int result);

	public void ACK_vRetSetInitPassword(int msgId, int result);

	public void ACK_vRetSetDevicePassword(int msgId, int result);

	public void ACK_vRetCheckDevicePassword(int msgId, int result,String deviceId);

	public void ACK_vRetSetWifi(int msgId, int result);

	public void ACK_vRetGetWifiList(int msgId, int result);

	public void ACK_vRetSetDefenceArea(int msgId, int result);

	public void ACK_vRetGetDefenceArea(int msgId, int result);

	public void ACK_vRetGetRecordFileList(int msgId, int result);

	public void ACK_vRetMessage(int msgId, int result);

	public void ACK_vRetCustomCmd(int msgId, int result);

	public void ACK_vRetGetDeviceVersion(int msgId, int result);

	public void ACK_vRetCheckDeviceUpdate(int msgId, int result);

	public void ACK_vRetDoDeviceUpdate(int msgId, int result);

	public void ACK_vRetCancelDeviceUpdate(int msgId, int result);

	public void ACK_vRetClearDefenceAreaState(int msgId, int result);

	public void ACK_vRetGetDefenceStates(String contactId, int msgId, int result);

	public void ACK_vRetSetImageReverse(int msgId, int result);

	public void ACK_vRetSetInfraredSwitch(int msgId, int result);

	public void ACK_vRetSetWiredAlarmInput(int msgId, int state);

	public void ACK_vRetSetWiredAlarmOut(int msgId, int state);

	public void ACK_vRetSetAutomaticUpgrade(int msgId, int state);

	public void ACK_VRetSetVisitorDevicePassword(int msgId, int state);

	public void ACK_vRetSetTimeZone(int msgId, int state);

	public void ACK_vRetGetSDCard(int msgId, int state);

	public void ACK_vRetSdFormat(int msgId, int state);

	public void ACK_vRetSetGPIO(int msgId, int state);

	public void ACK_vRetSetGPIO1_0(int msgId, int state);

	public void ACK_vRetSetPreRecord(int msgId, int state);

	public void ACK_vRetGetSensorSwitchs(int msgId, int state);

	public void ACK_vRetSetSensorSwitchs(int msgId, int state);
	
	public void ACK_vRetGetAlarmCenter(int msgId,int state);
	
	public void ACK_vRetSetAlarmCenter(int msgId,int state);
	
	public void ACK_VRetGetNvrIpcList(int msgId,int state);
	public void ACK_VRetGetNvrInfo(int msgId,int state);
	public void ACK_OpenDoor(int msgId,int state);
	public void ACK_vRetGetFTPInfo(int msgId,int state);

	// 设置结果回调
	public void vRetGetRemoteDefenceResult(String contactId, int state);

	public void vRetGetRemoteRecordResult(int state);

	public void vRetGetBuzzerResult(int state);

	public void vRetGetMotionResult(int state);

	public void vRetGetVideoFormatResult(int type);

	public void vRetGetRecordTypeResult(int type);

	public void vRetGetRecordTimeResult(int time);

	public void vRetGetNetTypeResult(int type);

	public void vRetGetVideoVolumeResult(int value);

	public void vRetGetRecordPlanTimeResult(String time);

	public void vRetGetImageReverseResult(int type);

	public void vRetGetInfraredSwitch(int state);

	public void vRetGetWiredAlarmInput(int state);

	public void vRetGetWiredAlarmOut(int state);

	public void vRetGetAutomaticUpgrade(int state);

	public void vRetGetTimeZone(int state);

	public void vRetGetAudioDeviceType(int type);

	public void vRetGetPreRecord(int state);

	public void vRetGetSensorSwitchs(int result, ArrayList<int[]> data);

	public void vRetSetRemoteDefenceResult(String contactId,int result);

	public void vRetSetRemoteRecordResult(int result);

	public void vRetSetBuzzerResult(int result);

	public void vRetSetMotionResult(int result);

	public void vRetSetVideoFormatResult(int result);

	public void vRetSetRecordTypeResult(int result);

	public void vRetSetRecordTimeResult(int result);

	public void vRetSetNetTypeResult(int result);

	public void vRetSetVolumeResult(int result);

	public void vRetSetRecordPlanTimeResult(int result);

	public void vRetSetDeviceTimeResult(int result);

	public void vRetGetDeviceTimeResult(String time);

	public void vRetAlarmEmailResult(int result, String email);

	public void vRetAlarmEmailResultWithSMTP(int result, String email,
			int smtpport, byte Entry,String[] SmptMessage,byte reserve);

	public void vRetWifiResult(int result, int currentId, int count,
			int[] types, int[] strengths, String[] names);

	public void vRetDefenceAreaResult(int result, ArrayList<int[]> data,
			int group, int item);

	public void vRetBindAlarmIdResult(int srcID, int result, int maxCount,
			String[] data);

	public void vRetSetInitPasswordResult(int result);

	public void vRetSetDevicePasswordResult(int result);

	public void vRetGetFriendStatus(int count, String[] contactIds,
			int[] status, int[] types);

	public void vRetGetRecordFiles(String[] names);

	public void vRetMessage(String contactId, String msg);

	public void vRetSysMessage(String msg);

	public void vRetCustomCmd(int contactId,int len, byte[] cmd);

	public void vRetGetDeviceVersion(int result, String cur_version,
			int iUbootVersion, int iKernelVersion, int iRootfsVersion);

	public void vRetCheckDeviceUpdate(String contactId,int result, String cur_version,
			String upg_version);

	public void vRetDoDeviceUpdate(String contactId,int result, int value);

	public void vRetCancelDeviceUpdate(int result);

	public void vRetDeviceNotSupport();

	public void vRetClearDefenceAreaState(int result);

	public void vRetSetImageReverse(int result);

	public void vRetSetInfraredSwitch(int result);

	public void vRetSetWiredAlarmInput(int state);

	public void vRetSetWiredAlarmOut(int state);

	public void vRetSetAutomaticUpgrade(int state);

	public void vRetSetVisitorDevicePassword(int result);

	public void vRetSetTimeZone(int result);

	public void vRetGetSdCard(int result1, int result2, int SDcardID, int state);

	public void VRetGetUsb(int result1, int result2, int SDcardID, int state);

	public void vRetSdFormat(int result);

	public void vRetSetGPIO(int result);

	public void vRetSetPreRecord(int result);

	public void vRetSetSensorSwitchs(int result);

	public void vRecvSetLAMPStatus(String deviceId,int result);

	public void vACK_RecvSetLAMPStatus(int result, int value);

	public void vRecvGetLAMPStatus(String deviceId,int result);

	public void vRetPresetMotorPos(byte[] result);

	public void vRetDefenceSwitchStatus(int result);

	public void vRetDefenceSwitchStatusResult(byte[] result);

	public void vRetAlarmPresetMotorPos(byte[] result);

	public void vRetIpConfig(byte[] result);
	
	public void vRetGetAlarmCenter(int result,int state,String ipdress,int port,String userId);
	
	public void vRetSetAlarmCenter(int result);
	
	public void vRetDeviceNotSupportAlarmCenter();

	public void vRetNPCVistorPwd(int pwd);
	
	public void vRetDeleteDeviceAlarmID(int result,int result1);
	
	public void vRetDeviceLanguege(int result,int languegecount,int curlanguege,int[] langueges);
	
	public void vRetFocusZoom(String deviceId,int result);
	public void vRetGetAllarmImage(int id,String filename,int errorCode);
	public void vRetFishEyeData(int iSrcID, byte[] data, int datasize);
	public void vRetGetNvrIpcList(String contactId,String[]date,int number);
	
	public void vRetSetWifiMode(String id,int result);
	public void vRetAPModeSurpport(String id,int result);
	public void vRetDeviceType(String id,int mainType,int subType);
	public void vRetNVRInfo(int iSrcID, byte[] data, int datasize);
	public void vRetGetFocusZoom(String deviceId,int result,int value);
	public void vRetSetFocusZoom(String deviceId,int result,int value);
	
	public void vRetSetGPIO(String contactid, int result);
	public void vRetGetGPIO(String contactid,int result,int bValueNs);
	
	public void vRetGetDefenceWorkGroup(String contactid,byte[] data);
	public void vRetSetDefenceWorkGroup(String contactid,byte[] data);
	
	public void vRetFTPConfigInfo(String contactid,byte[] data);
	//Ehome
	public void vRetGPIOStatus(String contactid,byte[] Level);
	public void vRecvSetGPIOStatus(String contactid,byte[] data);
	//Ehome
	
	public void vRetGetDefenceSwitch(int value);
	public void vRetSetDefenceSwitch(int result);
	
	public void vRetDefenceAreaName(String contactid,byte[] data);

	//SDK
	public void vRetSettingEx(int iSrcID, int iCount,int[] iSettingID, int[] iValue, int iResult);

	public void ACK_vRetSetOrGetEx(int msgId, int result);

	public void vRetExtenedCmd(int iSrcID, byte[] data, int datasize);
}
