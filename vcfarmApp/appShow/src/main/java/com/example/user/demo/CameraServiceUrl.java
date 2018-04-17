package com.example.user.demo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/4/8.
 */
public interface CameraServiceUrl {
    //------------------------摄像头------------------------>>>
    // 登录
    @FormUrlEncoded
    @POST("Users/LoginCheck.ashx")
    Call<CameraLoginMode> loginCheck(@Field("User") String User, @Field("Pwd") String Pwd, @Field("VersionFlag") String VersionFlag, @Field("AppOS") String AppOS, @Field("AppVersion") String AppVersion);
    //User=-2146673558&Pwd=00B7691D86D96AEBD21DD9E138F90840&VersionFlag=1&AppOS=3&AppVersion=3014666
    //------------------------摄像头------------------------<<<
}
