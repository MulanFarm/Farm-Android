package com.p2p.core.network;

import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class SetStoreIdResult {
	public String error_code;
	public SetStoreIdResult(JSONObject json){
		innit(json);
	}
    public void innit(JSONObject json){
    	try{
			error_code = json.getString("error_code");
		}catch(Exception e){
			if(!MyUtils.isNumeric(error_code)){
				Log.e("my","GetAccountInfoResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
			
		}
	}
}
