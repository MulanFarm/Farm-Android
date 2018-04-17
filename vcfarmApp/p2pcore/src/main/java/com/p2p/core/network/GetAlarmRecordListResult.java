package com.p2p.core.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class GetAlarmRecordListResult {
	public String error_code;
	public List<GXAlarmRecord> datas = new ArrayList<GXAlarmRecord>();
	
	public GetAlarmRecordListResult(JSONObject json){
		init(json);
	}
	
	private void init(JSONObject json){
		try{
			error_code = json.getString("error_code");
			String RL = json.getString("RL");
			String[] list = RL.split(";");
			for(String str : list){
				if(str.equals("")){
					continue;
				}
				
				String[] data = str.split("&");
				GXAlarmRecord gxar = new GXAlarmRecord();
				gxar.index = data[0];
				gxar.sendContactId = data[1];
				
				String dTime = data[2];
				gxar.deviceTime = MyUtils.convertTimeStringToInterval(dTime);
				gxar.imageUrl = data[3];
				gxar.type = Integer.parseInt(data[4]);
				gxar.group = Integer.parseInt(data[5]);
				gxar.item = Integer.parseInt(data[6]);
				
				String sTime = data[7];
				gxar.serverTime = MyUtils.convertTimeStringToInterval(sTime);
				
				datas.add(gxar);
			}
			
			Collections.sort(datas);
		}catch(Exception e){
			if(!MyUtils.isNumeric(error_code)){
				Log.e("my","GetAccountInfoResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
			
		}
	}
	

	public static class GXAlarmRecord implements Serializable,Comparable{
		public String index;
		public String sendContactId;
		public long deviceTime;
		public long serverTime;
		public String imageUrl;
		public int type;
		public int item;
		public int group;
		
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			GXAlarmRecord o = (GXAlarmRecord) arg0;
			if(this.serverTime>o.serverTime){
				return -1;
			}else if(this.serverTime<o.serverTime){
				return 1;
			}else{
				return 0;
			}
		}

		@Override
		public boolean equals(Object arg0) {
			// TODO Auto-generated method stub
			GXAlarmRecord o = (GXAlarmRecord) arg0;
			if(this.index.equals(o.index)){
				return true;
			}else{
				return false;
			}
		}
		
		
	}
	
}
