package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.WalletMode;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.widget.PublicDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/5/26.
 */
public class ToPayActivity extends BaseActivity {
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    private String to;
    private String oldTextMoney;
    private double totalMoney;

    public static void open(Context context) {
        Intent intent = new Intent(context, ToPayActivity.class);
        intent.putExtra("to", context.getClass().getName());
        context.startActivity(intent);
    }

    private void toOpen() {
        Intent intent2 = new Intent();
        intent2.setClassName(this, to);
        intent2.putExtra("isPay", true);
        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topay);
        ButterKnife.bind(this);

        WalletMode mode = WalletMode.getMode(this);
        totalMoney = mode.balance;

        Intent intent = getIntent();
        to = intent.getStringExtra("to");
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

    @OnClick(R.id.btnLogin)
    public void onViewClicked() {
        String s = editName.getText().toString();
        if (TextUtils.isEmpty(s)) return;
        PublicDialog dialog = new PublicDialog(this);
        dialog.setTextSize(18).setContent("确定转账?").setTitle(null).setOnPublicDialogClick(new PublicDialog.OnPublicDialogClick() {
            public void onConfirmClick() {
                toPay();
            }

            public void onCancelClick() {
            }
        }).show();
    }

    public void toPay() {
        String s = editName.getText().toString();
        if (TextUtils.isEmpty(s)) return;
        openLoadDialog();
        String token = UserConfig.getToken();
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().accountToPay(token, s, "admin");
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        ShowToast.alertShortOfWhite(ToPayActivity.this, "转账成功");
                        toOpen();
                    } else ShowToast.alertShortOfWhite(ToPayActivity.this, body.msg);
                } else ShowToast.alertShortOfWhite(ToPayActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(ToPayActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
