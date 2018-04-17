package com.p2p.core.network;

import org.json.JSONObject;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

public class GetAccountInfoResult {
	public String email;
	public String countryCode;
	public String phone;
	public String error_code;
	public GetAccountInfoResult(JSONObject json){
		init(json);
	}
	
	private void init(JSONObject json){
		try{
			error_code = json.getString("error_code");
			email = json.getString("Email");
			countryCode = json.getString("CountryCode");
			phone = json.getString("PhoneNO");
			
		}catch(Exception e){
			if(!MyUtils.isNumeric(error_code)){
				Log.e("my","GetAccountInfoResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
			
		}
	}
}
