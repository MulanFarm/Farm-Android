package com.p2p.core.network;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class SystemMessageResult implements Serializable {
	public String error_code;
	public String RecordCount;
	public String Surplus;
	public String RecommendFlag;
	public List<SystemMessage> systemMessages=new ArrayList<SystemMessage>();
	public SystemMessageResult(JSONObject json) {
		// TODO Auto-generated constructor stub
		init(json);
	}
	public void init(JSONObject json){
		try {
			error_code=json.getString("error_code");
			Log.e("error_code", "error_code="+error_code+"++++++++++++");
			RecordCount=json.getString("RecordCount");
			Surplus=json.getString("Surplus");
			RecommendFlag=json.getString("RecommendFlag");
			String RL=json.getString("RL");
			String [] list=RL.split(";");
			for(String mesg:list){
				if(mesg.equals("")){
					continue;
				}
				String [] data=mesg.split(",");
				SystemMessage s=new SystemMessage();
				s.msgId=data[0];
				s.title=data[1];
				s.content=data[2];
				s.picture_url=data[3];
				s.picture_in_url=data[4];
				String sTime=data[6];
				s.time=MyUtils.convertTimeStringToInterval(sTime);
				systemMessages.add(s);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static class SystemMessage implements Serializable,Comparable{
		public String msgId; 
		public String title;
		public String content;
		public long time;
		public String picture_url;
		public String picture_in_url;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			SystemMessage o=(SystemMessage) arg0;
			if(this.time>o.time){
				return -1;
			}else if(this.time<o.time){
				return 1;
			}else{
				return 0;
			}
		}
		@Override
		public boolean equals(Object arg0) {
			// TODO Auto-generated method stub
			SystemMessage o=(SystemMessage) arg0;
			if(this.msgId.endsWith(o.msgId)){
				return true;
			}else{
				return false;
			}
		}
	}
	
}
