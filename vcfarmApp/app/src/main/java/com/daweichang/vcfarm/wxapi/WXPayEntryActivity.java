package com.daweichang.vcfarm.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.widget.ShowToast;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xcc.mylibrary.Sysout;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "com.daweichang.vcfarm.wxapi.WXPayEntryActivity";
    public static final String PaySucc = "com.daweichang.vcfarm.wxapi.PaySucc";
    private IWXAPI api;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    public void onReq(BaseReq req) {
    }

    public void onResp(BaseResp resp) {
        Sysout.d(TAG, "onPayFinish, errCode = " + resp.errCode);
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            PublicDialog dialog = new PublicDialog(this);
//            dialog.setTitle("支付结果Code").setContent("code:" + resp.errCode).show();
//            //成功 展示成功页面
//            //-1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//            //-2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
//        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0: {
                    //  支付成功
                    ShowToast.alertShortOfWhite(this, "支付成功...");
                    //TODO  添加跳转
                    // setResult(RetInt, null);
                    //App.getInstance().setWeiPay(true);
                    Intent intent = new Intent(PaySucc);
                    intent.putExtra("isPay", true);
                    intent.setPackage(getPackageName());
                    sendBroadcast(intent);
                }
                break;
                case -1: {
                    //  签名错误...
                    ShowToast.alertShortOfWhite(this, "签名错误...");
                }
                break;
                case -2: {
                    //  用户取消
                    ShowToast.alertShortOfWhite(this, "用户取消...");// 可用
                }
                break;
            }
        }
        finish();
    }
}