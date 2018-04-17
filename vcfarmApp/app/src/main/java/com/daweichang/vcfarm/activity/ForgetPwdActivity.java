package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/21.
 */

public class ForgetPwdActivity extends BaseActivity {
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editCode)
    EditText editCode;
    @BindView(R.id.textCode)
    TextView textCode;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.editPwd)
    EditText editPwd;

    public static void open(Context context) {
        Intent intent = new Intent(context, ForgetPwdActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_pwd);
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

    @OnClick({R.id.textCode, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textCode:
                sendEmailCaptcha();
                break;
            case R.id.btnLogin:
                findPwd();
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
        if (TextUtils.isEmpty(text)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            return;
        }else if (!OtherUtils.isEmail(text)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            return;
        }
        UserConfig.setCodeTime(new Date().getTime());
        codeTime = 60;
        codeTime();
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().findPwd(text);
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                BaseRet body = response.body();
                if (body != null)
                    if (body.isOk()) {
//                    String data = (String) body.data;
//                    UserConfig.setEmailCaptcha(data);
                        Sysout.v("--sendEmailCaptcha--", body.toString());
                        ShowToast.alertShortOfWhite(ForgetPwdActivity.this, R.string.hqyzmcg);
                    } else {
                        codeTime = -1;
                        UserConfig.setCodeTime(0);
                        ShowToast.alertShortOfWhite(ForgetPwdActivity.this, body.msg);
                    }

                Sysout.out("==发送邮箱验证码接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(ForgetPwdActivity.this, R.string.wangluoyichang);
                UserConfig.setCodeTime(0);
                codeTime = -1;
            }
        });
    }

    //找回密码
    private void findPwd() {
        String username = editName.getText().toString();
        if (com.xcc.mylibrary.TextUtils.isEmpty(username)) {
            ShowToast.alertShortOfWhite(this, R.string.qsryhm);
            KeyBoardUtils.setFocusab(this, editName);
            return;
        }else if (!OtherUtils.isEmail(username)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
            return;
        }
        String s = editCode.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ShowToast.alertShortOfWhite(this, R.string.qsryzm);
            return;
        }
        s = s.toUpperCase();
        String newPwd = editPwd.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrxmm);
            KeyBoardUtils.setFocusab(this, editPwd);
            return;
        }
        try {
            AESUtils aesUtils = new AESUtils(this);
            newPwd = aesUtils.encrypt(newPwd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Call<BaseRet> pwd = BaseService.getInstance().getServiceUrl().findPwdVerify(username, s, newPwd);
        openLoadDialog(pwd);
        pwd.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                BaseRet body = response.body();
                if (body != null)
                    if (body.isOk()) {
                        Sysout.v("--findPwd--", body.toString());
                        ShowToast.alertShortOfWhite(ForgetPwdActivity.this, R.string.mmxgcg);
                        onBackPressed();
                    } else {
                        ShowToast.alertShortOfWhite(ForgetPwdActivity.this, body.msg);
                    }
                Sysout.out("==找回密码接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                t.printStackTrace();
                ShowToast.alertShortOfWhite(ForgetPwdActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
