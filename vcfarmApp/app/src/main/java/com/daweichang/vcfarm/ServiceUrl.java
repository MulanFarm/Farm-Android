package com.daweichang.vcfarm;

import com.daweichang.vcfarm.mode.ArchiveMode;
import com.daweichang.vcfarm.mode.ArticleMode;
import com.daweichang.vcfarm.mode.PayMode;
import com.daweichang.vcfarm.netret.ArchiveRet;
import com.daweichang.vcfarm.netret.ArticleListRet;
import com.daweichang.vcfarm.netret.CameraListRet;
import com.daweichang.vcfarm.netret.LoginRet;
import com.daweichang.vcfarm.netret.MessageRet;
import com.daweichang.vcfarm.netret.NoteRet;
import com.daweichang.vcfarm.netret.WalletGetRet;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/3/2.
 */

public interface ServiceUrl {
    //@POST("car/brands/appList/")
    //Call<Map<String, Object>> getCarBrand(@Query("username") String username);
    //BaseService.ServiceUrl serviceUrl = BaseService.getRetrofit().create(BaseService.ServiceUrl.class);
    //Call<Map<String,Object>> carBrand = serviceUrl.getCarBrand();
    //carBrand.enqueue(new Callback<Map<String,Object>>() {
    //public void onResponse(Call<Map<String,Object>> call, Response<Map<String,Object>> response) {
    //ShowToast.alertShortOfWhite(LoginActivity.this, response.body().toString());}
    //public void onFailure(Call<Map<String,Object>> call, Throwable t) {
    //t.printStackTrace();}});

    //------------------------用户接口------------------------>>>

    //微信登录
    @GET("wxLogin.php")
    Call<LoginRet> wxLogin(@Query("code") String code);

    //@Field 必须要 @POST 和 @FormUrlEncoded
    //登录
    @FormUrlEncoded
    @POST("user.php?m=login")
    Call<LoginRet> login(@Field("email") String email, @Field("user_pwd") String pwd);

    //accesstoken 添加进 httpheader
    //签到
    @POST("user.php?m=signIn")
    Call<BaseRet<Integer>> signIn(@Header("accesstoken") String token);

    //用户注册
    @FormUrlEncoded
    @POST("user.php?m=reg")
    Call<BaseRet> reg(@Field("email") String email, @Field("user_pwd") String pwd, @Field("terminal_type") String terminal_type);

    //找回密码
    @FormUrlEncoded
    @POST("user.php?m=findPwd")
    Call<BaseRet> findPwd(@Field("email") String email);

    //修改信息//0:女,1:男
    @FormUrlEncoded
    @POST("user.php?m=updateProfile")
    Call<BaseRet> updateProfile(@Header("accesstoken") String token, @Field("nick_name") String nick_name, @Field("gender") int gender, @Field("area") String area, @Field("address") String address, @Field("signature") String signature);

    //修改密码
    @FormUrlEncoded
    @POST("user.php?m=updatePwd")
    Call<BaseRet<Boolean>> updatePwd(@Header("accesstoken") String token, @Field("old_pwd") String oldPwd, @Field("new_pwd") String newPwd);

    //上传头像
    @Multipart
    @POST("user.php?m=uploadAvatar")
    Call<BaseRet<String>> uploadAvatar(@Header("accesstoken") String token, @Part("avatar[]\";filename=\"tmp.jpg\"") RequestBody avatar);

    //消息列表
    @POST("user.php?m=msgList")
    Call<MessageRet> msgList(@Header("accesstoken") String token);

    //发送邮箱验证码
    @FormUrlEncoded
    @POST("user.php?m=sendEmailCaptcha")
    Call<BaseRet<String>> sendEmailCaptcha(@Field("email") String email);

    //找回密码验证 verify_code 验证码 user_pwd 新密码
    @FormUrlEncoded
    @POST("user.php?m=findPwdVerify")
    Call<BaseRet> findPwdVerify(@Field("email") String email, @Field("verify_code") String verifyCode, @Field("user_pwd") String userPwd);

    //退出登录
    @POST("user.php?m=logout")
    Call<BaseRet> logout(@Header("accesstoken") String token);
    //------------------------用户接口------------------------<<<

    //------------------------摄像头------------------------>>>
    //摄像头列表
    @POST("camera.php?m=list")
    Call<CameraListRet> cameraList(@Header("accesstoken") String token);

    //选择摄像头
    @FormUrlEncoded
    @POST("camera.php?m=select")
    Call<BaseRet> cameraSelect(@Header("accesstoken") String token, @Field("id") String id);

    //绑定摄像头，即添加摄像头
    //*camera_no:摄像头编号
    //*thumbnail[]: 缩略图[图片文件]
    //name: 名称
    @Multipart
    @POST("camera.php?m=bind")
    Call<BaseRet> cameraBind(@Header("accesstoken") String token, @Part() List<MultipartBody.Part> parts);
    //@Part("camera_no") String camera_no, @Part("name") String name,
    //Call<BaseRet> cameraBind(@Header("accesstoken") String token, @Part("thumbnail[]\";filename=\"tmp.jpg\"") RequestBody avatar);

    //取消绑定摄像头
    @FormUrlEncoded
    @POST("camera.php?m=delete")
    Call<BaseRet> cameraDelete(@Header("accesstoken") String token, @Field("id") String id);
    //------------------------摄像头------------------------<<<

    //------------------------随手笔记------------------------>>>
    //保存笔记
    @FormUrlEncoded
    @POST("note.php?m=save")
    Call<BaseRet> noteSave(@Header("accesstoken") String token, @Field("title") String title, @Field("content") String content);

    //笔记列表
    @POST("note.php?m=list")
    Call<NoteRet> noteList(@Header("accesstoken") String token);
    //------------------------随手笔记------------------------<<<

    //------------------------档案------------------------>>>
    //档案列表
    @POST("archive.php?m=list")
    Call<ArchiveRet> archiveList(@Header("accesstoken") String token);

    //上传图册 imgs[]:  图片文件,可传多个最多5个
    @Multipart
    @POST("archive.php?m=uploadAlbum")
    Call<BaseRet> archiveUploadAlbum(@Header("accesstoken") String token, /*@Part("id") String id,*/ @Part() List<MultipartBody.Part> parts);
    //  String key = "imgs[]\";filename=\"tmp" + i + ".jpg\"";
    //Call<BaseRet> archiveUploadAlbum(@Header("accesstoken") String token,@Part("id") String id, @Part("imgs[]\";filename=\"tmp.jpg\"") RequestBody avatar);

    //修改档案

    /**
     * *id: 要修改的档案 id
     * variety: 品种
     * adop_time: 领养时间 "2017-03-10 12:16:00"
     * name: 名称
     * age: 年龄 (正整数)
     * weight: 体重 (浮点数)
     * height: 身高 (浮点数)
     * address: 地址
     * hobby: 爱好
     * hate: 讨厌
     */
    @FormUrlEncoded
    @POST("archive.php?m=update")
    Call<BaseRet> archiveUpdate(@Header("accesstoken") String token, @Field("id") String id, @Field("variety") String variety
            , @Field("adop_time") String adop_time, @Field("name") String name, @Field("age") String age, @Field("weight") String weight
            , @Field("height") String height, @Field("address") String address, @Field("hobby") String hobby, @Field("hate") String hate);

    //删除档案
    @FormUrlEncoded
    @POST("archive.php?m=delete")
    Call<BaseRet> archiveDelete(@Header("accesstoken") String token, @Field("id") String id);

    //档案详细
    @FormUrlEncoded
    @POST("archive.php?m=detail")
    Call<BaseRet<ArchiveMode>> archiveDetail(@Header("accesstoken") String token, @Field("id") String id);

    //删除相册图片
    @FormUrlEncoded
    @POST("archive.php?m=deleteAlbum")
    Call<BaseRet> archiveDeleteAlbum(@Header("accesstoken") String token, @Field("id") String id);
    //------------------------档案------------------------<<<

    //------------------------文章------------------------>>>
    //文章列表
    @POST("article.php?m=list")
    Call<ArticleListRet> articleList(@Header("accesstoken") String token);

    //文章详情
    @FormUrlEncoded
    @POST("article.php?m=detail")
    Call<BaseRet<ArticleMode>> articleDetail(@Header("accesstoken") String token, @Field("id") String id);
    //------------------------文章------------------------<<<

    //------------------------账户------------------------<<<
    //账户充值
//    http://180.101.72.94:8888/farm/controller/api/account.php?m=topup{
//    appid = wx55bc4907e6c26338;
//    spbill_create_ip = 10.0.35.216;
//    total_fee = 1;
//}
    @FormUrlEncoded
    @POST("account.php?m=topup")
    Call<BaseRet<PayMode>> accountTopup(@Header("accesstoken") String token, @Field("appid") String appid
            , @Field("spbill_create_ip") String spbill_create_ip, @Field("total_fee") String total_fee);

    //转账
    @FormUrlEncoded
    @POST("account.php?m=transfer")
    Call<BaseRet> accountToPay(@Header("accesstoken") String token, @Field("amount") String amount, @Field("payee") String payee);

    //查询充值结果
    @FormUrlEncoded
    @POST("account.php?m=payQuery")
    Call<BaseRet> accountpayQuery(@Header("accesstoken") String token, @Field("prepay_id") String prepay_id);

    // 账户查询
//    @FormUrlEncoded
    @POST("account.php?m=query")
    Call<WalletGetRet> accountQuery(@Header("accesstoken") String token);

    //用户协议
    @POST("appinfo.php?m=agreement")
    Call<BaseRet> appinfoAgreement();    //关于我们

    @POST("appinfo.php?m=aboutUs")
    Call<BaseRet<String>> aboutUs();

    @POST("appinfo.php?m=appInfo")
    Call<BaseRet<String>> appInfo();

    //------------------------账户------------------------<<<
}