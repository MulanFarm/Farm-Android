package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.mode.WalletMode;
import com.daweichang.vcfarm.netret.WalletGetRet;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.widget.photoView.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/29.
 * 我的钱包页
 */
public class WalletActivity extends BaseActivity {
    @BindView(R.id.icon)
    RoundedImageView icon;
    @BindView(R.id.textName)
    TextView textName;
    @BindView(R.id.textMoney)
    TextView textMoney;
    private String moneyStr;

    public static void open(Context context) {
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);

        LoginMode mode1 = LoginMode.getMode(this);
        GlideUtils.displayOfUrl(this, icon, mode1.avatar, R.drawable.defaultpic);
        textName.setText(mode1.getNickName());

        moneyStr = getResources().getString(R.string.zongzichan_);
        WalletMode mode = WalletMode.getMode(this);
        textMoney.setText(moneyStr + mode.balance);
        getMoney();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("isPay", false)) {
            String prepay_id = intent.getStringExtra("prepay_id");
            if (!TextUtils.isEmpty(prepay_id)) accountpayQuery(prepay_id);
            getMoney();
        }
    }

    @OnClick({R.id.btnLogin, R.id.btnPay})
    public void onClick(View view) {
        // 钱包充值付款
        switch (view.getId()) {
            case R.id.btnLogin:
                PayActivity.open(this);
                break;
            case R.id.btnPay:
                WalletMode mode = WalletMode.getMode(getActivity());
                if (mode == null || mode.status == 1) {
                    ShowToast.alertShortOfWhite(getActivity(), R.string.zhanghuyichang);
                } else if (mode.balance > 0)
                    ToPayActivity.open(this);
                else {
                    ShowToast.alertShortOfWhite(getActivity(), R.string.yuebuzu);
                }
                break;
        }
    }

    //查看金钱数
    private void getMoney() {
        openLoadDialog();
        String token = UserConfig.getToken();
        Call<WalletGetRet> baseRetCall = BaseService.getInstance().getServiceUrl().accountQuery(token);
        baseRetCall.enqueue(new Callback<WalletGetRet>() {
            public void onResponse(Call<WalletGetRet> call, Response<WalletGetRet> response) {
                dismissDialog();
                WalletGetRet body = response.body();
                if (body != null)
                    if (body.isOk()) {
                        WalletMode data = body.data;
                        WalletMode.setMode(WalletActivity.this, data);
                        textMoney.setText(moneyStr + data.balance);
                    } else ShowToast.alertShortOfWhite(WalletActivity.this, body.msg);
                else ShowToast.alertShortOfWhite(WalletActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<WalletGetRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(WalletActivity.this, R.string.wangluoyichang);
            }
        });
    }

    //查看金钱数
    private void accountpayQuery(String prepay_id) {
        openLoadDialog();
        String token = UserConfig.getToken();
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().accountpayQuery(token, prepay_id);
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                BaseRet body = response.body();
//                if (body != null)
//                    if (body.isOk()) {
//                        WalletMode data = body.data;
//                        WalletMode.setMode(WalletActivity.this, data);
//                        textMoney.setText(moneyStr + data.balance);
//                    } else ShowToast.alertShortOfWhite(WalletActivity.this, body.msg);
                ShowToast.alertShortOfWhite(WalletActivity.this, body.msg);
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(WalletActivity.this, R.string.wangluoyichang);
            }
        });
    }
}