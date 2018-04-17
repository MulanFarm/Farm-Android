package com.p2p.core.network;

import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class ModifyLoginPasswordResult {
	public String error_code;
	public String sessionId;
	
	public ModifyLoginPasswordResult(JSONObject json){
		init(json);
	}
	
	private void init(JSONObject json){
		try{
			error_code = json.getString("error_code");
			sessionId = json.getString("SessionID");
		}catch(Exception e){
			if(!MyUtils.isNumeric(error_code)){
				Log.e("my","ModifyLoginPasswordResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
		}
	}
}
