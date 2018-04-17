package com.daweichang.vcfarm.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.daweichang.vcfarm.widget.ShowToast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
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

	// public static final String APP_ID = "wx884b7f38ce95c1a6";// 微信APP_ID

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
			// 分享成功
			ShowToast.alertShortOfWhite(this, "分享成功");
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// 分享取消
			ShowToast.alertShortOfWhite(this, "分享取消");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// 分享拒绝
			ShowToast.alertShortOfWhite(this, "分享拒绝");
			break;
		}
		finish();
	}
}
