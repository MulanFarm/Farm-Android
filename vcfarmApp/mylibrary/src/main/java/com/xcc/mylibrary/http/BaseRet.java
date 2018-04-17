package com.xcc.mylibrary.http;

/**
 * Created by Administrator on 2016/11/23.
 * 网络返回基本类型
 */
public class BaseRet {
    //{"addSign":true,"ret_code":"9999","ret_msg":"登录密码错误. ","sign":"01789b525555119dbfe152521fc2f035","sign_type":"MD5"}
    //{"addSign":true,"ret_code":"0000","ret_msg":"注册成功","sign":"debc1571536064bde7c696017fb47a62","sign_type":"MD5"}
//{"addSign":true,"ret_code":"0000","ret_msg":"短信验证码已发送","sign":"4dfc00db85d99e76c70ec71a5cbad54e","sign_type":"MD5"}
//{"addSign":true,"ret_code":"9999","ret_msg":"手机号已存在","sign":"43ec438c1c99fb534118e1557950565b","sign_type":"MD5"}
    public String ret_code;// String是返回码
    public String ret_msg;//string是返回说明
    public String sign;//string是签名内容，根据请求中signType签名后的内容
    public String addSign;//String是是否加签，默认为true
    public String sign_type;//String是是否加签，默认为true
    public String bizRes;//string否业务返回信息，json格式

    public boolean isOk() {
        return "0000".equals(ret_code);
    }
    public boolean isSkipSign(){
        return "1111".equals(ret_code);
    }
}
