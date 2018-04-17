package com.p2p.core.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class GetDeviceListResult {
	public String error_code;
	public String bindFlag;
	public List<String> contactIds = new ArrayList<String>();
	public List<String> flags = new ArrayList<String>();
	public List<String> nikeNames = new ArrayList<String>();
	public GetDeviceListResult(JSONObject json){
		init(json);
	}
	
	private void init(JSONObject json){
		try{
			error_code = json.getString("error_code");
			bindFlag = json.getString("BindFlag");
			String RL = json.getString("RL");
			String[] list = RL.split(",");
			for(int i=0;i<list.length;i++){
				String[] datas = list[i].split(":");
				contactIds.add(datas[0]);
				flags.add(datas[1]);
				nikeNames.add(datas[2]);
			}
		}catch(Exception e){
			if(!MyUtils.isNumeric(error_code)){
				Log.e("my","GetAccountInfoResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
			
		}
	}
}
