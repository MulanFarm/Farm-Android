package com.daweichang.vcfarm.mode;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/26.
 */

public class PayMode {
    /**
     * prepayid : wx20170526200551f8706f07ab0123750512
     * noncestr : 006364013437209419967312159763
     * appid : wx55bc4907e6c26338
     * partnerid : 1473823602
     * package : Sign=WXPay
     * timestamp : 1495800752
     * sign : 2A8F2DC8ED3750AAE552F3AC93B910F9
     */
    public String prepayid;
    public String noncestr;
    public String appid;
    public String partnerid;
    @SerializedName("package")
    public String packageX;
    public String timestamp;
    public String sign;
}
