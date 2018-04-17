package com.p2p.core.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class AlarmRecordResult implements Serializable {
   public String error_code;
   public String Surplus;
   public List<SAlarmRecord> alarmRecords=new ArrayList<SAlarmRecord>();
   public AlarmRecordResult(JSONObject json){ 
	   init(json);
   }
   private void init(JSONObject json){
	 try {
		error_code=json.getString("error_code");
		Surplus=json.getString("Surplus");
		String RL=json.getString("RL");
		String[] list=RL.split(";");
		for(String str:list){
			if(str.equals("")){
				continue;
			}
			String[] data=str.split("&");
			SAlarmRecord ar=new SAlarmRecord();
			ar.messgeId=data[0];
			ar.sourceId=data[1];
			String dTime=data[2];
			ar.alarmTime=MyUtils.convertTimeStringToInterval(dTime);
			ar.pictureUrl=data[3];
			ar.alarmType=Integer.parseInt(data[4]);
			ar.defenceArea=Integer.parseInt(data[5]);
			ar.channel=Integer.parseInt(data[6]);
			String sTime=data[7];
			ar.serverReceiveTime=MyUtils.convertTimeStringToInterval(sTime);
			alarmRecords.add(ar);
		}
	} catch (Exception e) {
		if(!MyUtils.isNumeric(error_code)){
			Log.e("my","GetAccountInfoResult json解析错误");
			error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
		}
	}
   }
   public static class SAlarmRecord implements Serializable,Comparable{
	  public String messgeId;
	  public String sourceId;
	  public long alarmTime;
	  public String pictureUrl;
	  public int alarmType;
	  public int defenceArea;
	  public int channel;
	  public long serverReceiveTime;
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		SAlarmRecord o=(SAlarmRecord) arg0;
		if(this.serverReceiveTime>o.serverReceiveTime){
			return -1;
		}else if(this.serverReceiveTime<o.serverReceiveTime){
			return 1;
		}else{
			return 0;
		}
	}
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		SAlarmRecord o=(SAlarmRecord)arg0;
		if(this.messgeId.endsWith(o.messgeId)){
			return true;
		}else{
			return false;
		}
	}
   }
}
