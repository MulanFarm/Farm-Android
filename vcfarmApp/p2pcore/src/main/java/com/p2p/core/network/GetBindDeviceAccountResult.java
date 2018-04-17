package com.p2p.core.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class GetBindDeviceAccountResult {
	public String error_code;
	public List<String> contactIds = new ArrayList<String>();
	public List<String> flags = new ArrayList<String>();
	public List<String> phones = new ArrayList<String>();
	public List<String> country_codes = new ArrayList<String>();
	
	public GetBindDeviceAccountResult(JSONObject json){
		init(json);
	}
	
	private void init(JSONObject json){
		try{
			error_code = json.getString("error_code");
			String RL = json.getString("RL");
			String[] list = RL.split(",");
			for(int i=0;i<list.length;i++){
				String[] datas = list[i].split(":");
				country_codes.add(datas[0]);
				phones.add(datas[1]);
				flags.add(datas[2]);
				String contactId = "";
				try{
					contactId = "0"+String.valueOf((Integer.parseInt(datas[3])&0x7fffffff));
				}catch(Exception e){
					
				}
				contactIds.add(contactId);
			}
		}catch(Exception e){
			if(!MyUtils.isNumeric(error_code)){
				Log.e("my","GetAccountInfoResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
			
		}
	}
}
