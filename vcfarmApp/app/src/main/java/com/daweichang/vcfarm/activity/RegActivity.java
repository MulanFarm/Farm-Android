package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.utils.AESUtils;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.KeyBoardUtils;
import com.xcc.mylibrary.OtherUtils;
import com.xcc.mylibrary.Sysout;
import com.xcc.mylibrary.TextUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xcc on 2017/2/23.
 * 注册
 */
public class RegActivity extends BaseActivity {
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editCode)
    EditText editCode;
    @BindView(R.id.textCode)
    TextView textCode;
    @BindView(R.id.editPwd)
    EditText editPwd;
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.textTreaty)
    TextView textTreaty;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    public static void open(Context context) {
        Intent intent = new Intent(context, RegActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reg);
        ButterKnife.bind(this);
    }

    protected void onResume() {
        super.onResume();
        long sendCodeTime = UserConfig.getCodeTime();
        if (sendCodeTime > 0) {
            long lag = new Date().getTime() - sendCodeTime;
            if (lag < 60000) {
                codeTime = (int) ((60000 - lag) / 1000);
                codeTime();
            }
        }
    }

    @OnClick({R.id.textCode, R.id.textTreaty, R.id.btnLogin, R.id.textCheck})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textCheck:
                checkbox.setChecked(!checkbox.isChecked());
                break;
            case R.id.textCode:
                sendEmailCaptcha();
                break;
            case R.id.textTreaty:// 协议
                toTreaty();
//                UserTreatyActivity.open(this);
                break;
            case R.id.btnLogin://注册
                reg();
                break;
        }
    }

    private int codeTime;
    private boolean stop = false;

    public void codeTime() {
        if (stop) return;
        if (codeTime < 0) {
            textCode.setText(getResources().getText(R.string.hqyzm));
            textCode.setEnabled(true);
        } else {
            textCode.setText(codeTime + "s");
            textCode.setEnabled(false);
            textCode.postDelayed(new Runnable() {
                public void run() {
                    codeTime();
                }
            }, 1000);
        }
        codeTime--;
    }

    protected void onDestroy() {
        super.onDestroy();
        stop = true;
    }

    //发送邮箱验证码
    private void sendEmailCaptcha() {
        String text = editName.getText().toString();
        if (android.text.TextUtils.isEmpty(text)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            return;
        } else if (!OtherUtils.isEmail(text)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            return;
        }
        UserConfig.setCodeTime(new Date().getTime());
        codeTime = 60;
        codeTime();
        Call<BaseRet<String>> baseRetCall = BaseService.getInstance().getServiceUrl().sendEmailCaptcha(text);
        baseRetCall.enqueue(new Callback<BaseRet<String>>() {
            public void onResponse(Call<BaseRet<String>> call, Response<BaseRet<String>> response) {
                BaseRet body = response.body();
                if (body != null && body.isOk()) {
                    String data = (String) body.data;
                    UserConfig.setEmailCaptcha(data);
                    Sysout.v("--sendEmailCaptcha--", body.toString());
                } else {
                    ShowToast.alertShortOfWhite(RegActivity.this, body.msg);
                    codeTime = -1;
                    UserConfig.setCodeTime(0);
                }
                Sysout.out("==发送邮箱验证码接口返回成功==");
            }

            public void onFailure(Call<BaseRet<String>> call, Throwable t) {
                ShowToast.alertShortOfWhite(RegActivity.this, R.string.wangluoyichang);
                UserConfig.setCodeTime(0);
                codeTime = -1;
            }
        });
    }

    //用户注册
    private void reg() {
        if (!checkbox.isChecked()) {
            ShowToast.alertShortOfWhite(this, R.string.qtyyhxy);
            return;
        }
        String emailCaptcha = UserConfig.getEmailCaptcha();
        if (android.text.TextUtils.isEmpty(emailCaptcha)) {
            ShowToast.alertShortOfWhite(this, R.string.qhqyzm);
            return;
        }
        emailCaptcha = emailCaptcha.toLowerCase();
        String s = editCode.getText().toString();
        s = s.toLowerCase();
        if (!emailCaptcha.equals(s)) {
            ShowToast.alertShortOfWhite(this, R.string.yzmyw);
            return;
        }
        String pwd = editPwd.getText().toString();
        String username = editName.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            KeyBoardUtils.setFocusab(this, editName);
            return;
        }else if (!OtherUtils.isEmail(username)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrmm);
            KeyBoardUtils.setFocusab(this, editPwd);
            return;
        }
        try {
            AESUtils aesUtils = new AESUtils(this);
            pwd = aesUtils.encrypt(pwd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Call<BaseRet> reg = BaseService.getInstance().getServiceUrl().reg(username, pwd, AppVc.terminalType);
        openLoadDialog(reg);
        reg.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                // 处理返回数据
                BaseRet body = response.body();
                if (body != null && body.isOk()) {
                    Sysout.v("--reg--", body.toString());
                    ShowToast.alertShortOfWhite(RegActivity.this, R.string.zhucechenggong);
                    UserConfig.setEmailCaptcha(null);
                    onBackPressed();
                } else ShowToast.alertShortOfWhite(RegActivity.this, R.string.zhuceshibai);
                Sysout.out("==注册接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                t.printStackTrace();
                ShowToast.alertShortOfWhite(RegActivity.this, R.string.wangluoyichang);
            }
        });
    }

    private void toTreaty() {
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().appinfoAgreement();
        openLoadDialog(baseRetCall);
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                // 处理返回数据
                BaseRet body = response.body();
                if (body != null && body.isOk()) {
                    String msg = (String) body.data;
                    //textContent.setText(msg);
                    Resources res = getResources();
                    WebViewActivity.open(RegActivity.this, res.getString(R.string.yonghuxieyi), msg);
                } else
                    ShowToast.alertShortOfWhite(RegActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                t.printStackTrace();
                ShowToast.alertShortOfWhite(RegActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
