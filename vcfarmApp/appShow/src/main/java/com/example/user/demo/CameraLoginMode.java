package com.example.user.demo;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/4/8.
 */

public class CameraLoginMode {
    /**
     * error_code : 0
     * UserID : -2146673558
     * P2PVerifyCode1 : 766336851
     * P2PVerifyCode2 : 683660335
     * Email : lx_fox_2010@sina.co.jp
     * NickName :
     * CountryCode :
     * PhoneNO :
     * ImageID :
     * SessionID : 1466291507
     * DomainList :
     * UserLevel : 0
     * SessionID2 :
     */
    public String error_code;
    public String UserID;
    public String P2PVerifyCode1;
    public String P2PVerifyCode2;
    public String Email;
    public String NickName;
    public String CountryCode;
    public String PhoneNO;
    public String ImageID;
    public String SessionID;
    public String DomainList;
    public String UserLevel;
    public String SessionID2;

    public boolean isOk() {
        return "0".equals(error_code);
        //return result;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
