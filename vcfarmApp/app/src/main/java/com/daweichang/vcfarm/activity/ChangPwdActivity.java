package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.utils.AESUtils;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.KeyBoardUtils;
import com.xcc.mylibrary.Sysout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/25.
 */

public class ChangPwdActivity extends BaseActivity {
    @BindView(R.id.editOldPwd)
    EditText editOldPwd;
    @BindView(R.id.editPwd)
    EditText editPwd;
    @BindView(R.id.editPwd2)
    EditText editPwd2;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    public static void open(Context context) {
        Intent intent = new Intent(context, ChangPwdActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chang_pwd);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnLogin)
    public void onClick() {
        String oldPwd = editOldPwd.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrjmm);
            KeyBoardUtils.setFocusab(this, editOldPwd);
            return;
        }
        String newPwd = editPwd.getText().toString();
        if (TextUtils.isEmpty(newPwd)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrxmm);
            KeyBoardUtils.setFocusab(this, editPwd);
            return;
        }
        String newPwd2 = editPwd2.getText().toString();
        if (TextUtils.isEmpty(newPwd2)) {
            ShowToast.alertShortOfWhite(this, R.string.qzcsrxmm);
            KeyBoardUtils.setFocusab(this, editPwd2);
            return;
        }
        if (!newPwd.equals(newPwd2)) {
            ShowToast.alertShortOfWhite(this, R.string.lcsrdmmbt);
            return;
        }
        try {
            AESUtils aesUtils = new AESUtils(this);
            oldPwd = aesUtils.encrypt(oldPwd);
            newPwd = aesUtils.encrypt(newPwd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Call<BaseRet<Boolean>> baseRetCall = BaseService.getInstance().getServiceUrl().updatePwd(UserConfig.getToken(), oldPwd, newPwd);
        openLoadDialog();
        baseRetCall.enqueue(new Callback<BaseRet<Boolean>>() {
            public void onResponse(Call<BaseRet<Boolean>> call, Response<BaseRet<Boolean>> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        AppVc.getAppVc().setLogin(false);
                        MainActivity.openTop(ChangPwdActivity.this);
                        ShowToast.alertShortOfWhite(ChangPwdActivity.this, R.string.mimagenggaichenggong);
                    } else ShowToast.alertShortOfWhite(ChangPwdActivity.this, body.msg);
                } else ShowToast.alertShortOfWhite(ChangPwdActivity.this, R.string.wangluoyichang);
                //Sysout.v("--updatePwd--", body.toString());
                Sysout.out("==修改密码接口返回成功==");
            }

            public void onFailure(Call<BaseRet<Boolean>> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(ChangPwdActivity.this, R.string.wangluoyichang);
            }
        });
    }

}
