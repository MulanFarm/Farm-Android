package com.daweichang.vcfarm.wxapi;

import android.content.Context;

import com.daweichang.vcfarm.mode.PayMode;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xcc.mylibrary.Sysout;

import java.util.List;
import java.util.Random;

/**
 * Created by xcc on 2017/2/21
 * 微信支付调用.
 */
public class PayTools {
    private Context context;
    private IWXAPI api;
    private PayReq req;
    //    private OnPayErrListener onPayErrListener;
    private PayMode mode;

    public PayTools(Context context, PayMode mode) {
        this.context = context;
        this.mode = mode;
        api = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);
    }

    public void payStart() {
        genPayReq();
        sendPayReq();
    }

    private void genPayReq() {
        req = new PayReq();
        req.appId =mode.appid;// Constants.APP_ID;
        req.partnerId =mode.partnerid;// Constants.MCH_ID;
        req.prepayId =mode.prepayid;// wxBean.prepay_id;
        req.packageValue =mode.packageX;// "prepay_id=" + wxBean.prepay_id;
        req.nonceStr =mode.noncestr;// genNonceStr();
        req.timeStamp =mode.timestamp;// String.valueOf(wxBean.timeStamp);
//        List<NameValuePair> signParams = new LinkedList<>();
//        signParams.add(new NameValuePair("appid", req.appId));
//        signParams.add(new NameValuePair("noncestr", req.nonceStr));
//        signParams.add(new NameValuePair("package", req.packageValue));
//        signParams.add(new NameValuePair("partnerid", req.partnerId));
//        signParams.add(new NameValuePair("prepayid", req.prepayId));
//        signParams.add(new NameValuePair("timestamp", req.timeStamp));
        req.sign =mode.sign;// genAppSign(signParams);
//        Sysout.e("orion", signParams.toString());
    }

    private void sendPayReq() {
        api.registerApp(Constants.APP_ID);
        api.sendReq(req);
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }


    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        Sysout.e("orion", appSign);
        return appSign;
    }
}
