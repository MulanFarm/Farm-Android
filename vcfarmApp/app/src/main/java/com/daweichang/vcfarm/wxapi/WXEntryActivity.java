package com.daweichang.vcfarm.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.widget.ShowToast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * 微信客户端回调activity<br>
 * 微信使用包libammsdk.jar <br>
 * 不做修改，由微信回调
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	private static final int RETURN_MSG_TYPE_LOGIN = 1;
	private static final int RETURN_MSG_TYPE_SHARE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	boolean runing = true;

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		// switch (req.getType()) {
		// case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
		// ShowToast.alertShortOfWhite(this, "11111111111");
		// break;
		// case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
		// ShowToast.alertShortOfWhite(this, "222222222222");
		// break;
		// default:
		// break;
		// }
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			switch (resp.getType()) {
				case RETURN_MSG_TYPE_LOGIN:
					//拿到了微信返回的code,立马再去请求access_token
					String code = ((SendAuth.Resp) resp).code;
					Log.d("微信授权code：","code = " + code);

					//就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
					Intent intent = new Intent(AppVc.WXLogin);
					intent.putExtra("code", code);
					sendBroadcast(intent);
					break;

				case RETURN_MSG_TYPE_SHARE:
					// 分享成功
					ShowToast.alertShortOfWhite(this, "微信分享成功");
					finish();
					break;
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
				// 分享取消
				ShowToast.alertShortOfWhite(this, "分享取消");
			} else {
				// 授权取消
				ShowToast.alertShortOfWhite(this, "授权取消");
			}
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
				// 分享拒绝
				ShowToast.alertShortOfWhite(this, "分享拒绝");
			} else {
				// 授权拒绝
				ShowToast.alertShortOfWhite(this, "授权拒绝");
			}
			break;
		}
		finish();
	}
}
