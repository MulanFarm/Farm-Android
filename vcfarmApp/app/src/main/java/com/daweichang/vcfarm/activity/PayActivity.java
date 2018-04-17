package com.daweichang.vcfarm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.PayMode;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.daweichang.vcfarm.wxapi.PayTools;
import com.daweichang.vcfarm.wxapi.WXPayEntryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/5/26.
 */
public class PayActivity extends BaseActivity {
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    private String to;
    private String prepay_id;
    private String oldTextMoney;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WXPayEntryActivity.PaySucc.equals(action)) {
                boolean isPay = intent.getBooleanExtra("isPay", false);
                if (isPay) {
                    Intent intent2 = new Intent();
                    intent2.setClassName(context, to);
                    intent2.putExtra("isPay", true);
                    intent2.putExtra("prepay_id", prepay_id);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent2);
                }
            }
        }
    };

    public static void open(Context context) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("to", context.getClass().getName());
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        to = intent.getStringExtra("to");

        IntentFilter filter = new IntentFilter(WXPayEntryActivity.PaySucc);
        registerReceiver(receiver, filter);

        editName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                String s1 = editName.getText().toString();
                if (TextUtils.isEmpty(s1)) {
                    oldTextMoney = s1;
                    return;
                }
                if (".".equals(s1)) {
                    oldTextMoney = "0.";
                    editName.setText(oldTextMoney);
                    editName.setSelection(oldTextMoney.length());
                    return;
                }
                String[] split = s1.split("\\.");
                double now = 0;
                if (split.length != 1) {
                    if (split[1].length() > 2) {
                        editName.setText(oldTextMoney);
                        editName.setSelection(oldTextMoney.length());
                        s1 = oldTextMoney;
                        split = s1.split("\\.");
                    }
                }
                if (split[0].length() > 1) {
                    char c = s1.charAt(0);
                    if ('0' == c) {
                        s1 = s1.substring(1, s1.length());
                        split = s1.split("\\.");
                        editName.setText(s1);
                        editName.setSelection(s1.length());
                    }
                }
                if (split.length == 1) {
                    now = Double.parseDouble(split[0]);
                } else {
                    now = Double.parseDouble(s1);
                }
//                if (now > totalMoney) {
//                    s1 = "" + totalMoney;
//                    editName.setText(s1);
//                    editName.setSelection(s1.length());
//                }
                oldTextMoney = s1;
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @OnClick(R.id.btnLogin)
    public void onViewClicked() {
        String s = editName.getText().toString();
        if (TextUtils.isEmpty(s)) return;
        openLoadDialog();
        String token = UserConfig.getToken();
        //    appid = wx55bc4907e6c26338;
//    spbill_create_ip = 10.0.35.216;
//    total_fee = 1;
        Call<BaseRet<PayMode>> baseRetCall = BaseService.getInstance().getServiceUrl().accountTopup(token, "wx55bc4907e6c26338", AppVc.getAppVc().getIp(), s);
        baseRetCall.enqueue(new Callback<BaseRet<PayMode>>() {
            public void onResponse(Call<BaseRet<PayMode>> call, Response<BaseRet<PayMode>> response) {
                dismissDialog();
                BaseRet<PayMode> body = response.body();
                if (body != null)
                    if (body.isOk()) {
                        PayMode data = body.data;
                        pay(data);
                    } else ShowToast.alertShortOfWhite(PayActivity.this, body.msg);
                else ShowToast.alertShortOfWhite(PayActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<BaseRet<PayMode>> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(PayActivity.this, R.string.wangluoyichang);
            }
        });
    }

    private void pay(PayMode data) {
        prepay_id = data.prepayid;
        PayTools payTools = new PayTools(this, data);
        payTools.payStart();
    }
}
