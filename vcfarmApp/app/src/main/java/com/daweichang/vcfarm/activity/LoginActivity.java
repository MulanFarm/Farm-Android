package com.daweichang.vcfarm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daweichang.vcfarm.ActivityManager;
import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.netret.LoginRet;
import com.daweichang.vcfarm.utils.AESUtils;
import com.daweichang.vcfarm.widget.ShowToast;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.xcc.mylibrary.KeyBoardUtils;
import com.xcc.mylibrary.OtherUtils;
import com.xcc.mylibrary.Sysout;
import com.xcc.mylibrary.TextUtils;
import com.xcc.mylibrary.widget.photoView.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xcc on 2017/2/23.
 * 登录界面
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.wxLogin)
    ImageView wxLogin;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.editPwd)
    EditText editPwd;
    @BindView(R.id.textReg)
    TextView textReg;
    @BindView(R.id.textForget)
    TextView textForget;
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.textTreaty)
    TextView textTreaty;
    private String to;

    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        //intent.putExtra("to", context.getClass().getName());
        context.startActivity(intent);
    }

    private BroadcastReceiver wxLoginReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (AppVc.WXLogin.equals(intent.getAction())) {
                String code = intent.getStringExtra("code");

                Log.d("微信授权登录code：","code = " + code);

                Call<LoginRet> wxLogin = BaseService.getInstance().getServiceUrl().wxLogin(code);
                openLoadDialog(wxLogin);
                wxLogin.enqueue(new Callback<LoginRet>() {
                    public void onResponse(Call<LoginRet> call, Response<LoginRet> response) {
                        dismissDialog();
                        LoginRet ret = response.body();
                        Sysout.out("==登录接口返回成功==");
                        Sysout.v("--login--", ret.toString());
                        if (ret.result) {
                            LoginMode users = ret.data;
                            AppVc.getAppVc().setLogin(true);
                            AppVc.getAppVc().setLoginMode(users);
                            // 跳转
                            if (TextUtils.isEmpty(to))
                                MainActivity.openTop(LoginActivity.this);
                            else {
                                Intent intent = new Intent();
                                intent.setClassName(LoginActivity.this, to);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        } else ShowToast.alertShortOfWhite(LoginActivity.this, ret.msg);
                    }

                    public void onFailure(Call<LoginRet> call, Throwable t) {
                        dismissDialog();
                        t.printStackTrace();
                        ShowToast.alertShortOfWhite(LoginActivity.this, R.string.wangluoyichang);
                    }
                });
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        to = intent.getStringExtra("to");

        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);

        IntentFilter filter = new IntentFilter(AppVc.WXLogin);
        getActivity().registerReceiver(wxLoginReceiver, filter);

//        editUserName.addTextChangedListener(new TextWatcher() {
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            public void afterTextChanged(Editable s) {
//                String s1 = editUserName.getText().toString();
//                if ("ccshuai".equals(s1)) {
//                    startActivity(new Intent(LoginActivity.this, TestActivity.class));
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(wxLoginReceiver);
    }

    public void onBackPressed() {
        ActivityManager manager = ActivityManager.getManager();
        manager.closeOther(this);
        manager.closeAll();
//        super.onBackPressed();
    }

    @OnClick({R.id.wxLogin, R.id.btnLogin, R.id.textReg, R.id.textForget, R.id.textTreaty, R.id.textCheck})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textReg:
                RegActivity.open(this);
                break;
            case R.id.textForget:
                ForgetPwdActivity.open(this);
                break;
            case R.id.textCheck:
                checkbox.setChecked(!checkbox.isChecked());
                break;
            case R.id.textTreaty:// 协议
                toTreaty();
//                UserTreatyActivity.open(this);
                break;
            case R.id.btnLogin://登录
                if (!checkbox.isChecked()) {
                    ShowToast.alertShortOfWhite(this, R.string.qtyyhxy);
                    return;
                }
                String pwd = editPwd.getText().toString();
                String username = editName.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    ShowToast.alertShortOfWhite(this, R.string.qsryhm);
                    KeyBoardUtils.setFocusab(this, editName);
                    return;
                } else if (!OtherUtils.isEmail(username)) {
                    ShowToast.alertShortOfWhite(this, R.string.qsrndyx);
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ShowToast.alertShortOfWhite(this, R.string.qsrmm);
                    KeyBoardUtils.setFocusab(this, editPwd);
                    return;
                }
                KeyBoardUtils.closeKey(this);
                try {
                    pwd = new AESUtils(this).encrypt(pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Call<LoginRet> login = BaseService.getInstance().getServiceUrl().login(username, pwd);
                openLoadDialog(login);
                login.enqueue(new Callback<LoginRet>() {
                    public void onResponse(Call<LoginRet> call, Response<LoginRet> response) {
                        dismissDialog();
                        LoginRet ret = response.body();
                        Sysout.out("==登录接口返回成功==");
                        Sysout.v("--login--", ret.toString());
                        if (ret.result) {
                            LoginMode users = ret.data;
                            AppVc.getAppVc().setLogin(true);
                            AppVc.getAppVc().setLoginMode(users);
                            // 跳转
                            if (TextUtils.isEmpty(to))
                                MainActivity.openTop(LoginActivity.this);
                            else {
                                Intent intent = new Intent();
                                intent.setClassName(LoginActivity.this, to);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        } else ShowToast.alertShortOfWhite(LoginActivity.this, ret.msg);
                    }

                    public void onFailure(Call<LoginRet> call, Throwable t) {
                        dismissDialog();
                        t.printStackTrace();
                        ShowToast.alertShortOfWhite(LoginActivity.this, R.string.wangluoyichang);
                    }
                });
                break;
            case R.id.wxLogin://微信登录
                if (!checkbox.isChecked()) {
                    ShowToast.alertShortOfWhite(this, R.string.qtyyhxy);
                    return;
                }

                // 调用微信授权登录
                if (!AppVc.mWxApi.isWXAppInstalled()) {
                    ShowToast.alertShortOfWhite(this, "您还未安装微信客户端");
                    return;
                }
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "diandi_wx_login";
                AppVc.mWxApi.sendReq(req);
                break;
        }
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
                    WebViewActivity.open(LoginActivity.this, res.getString(R.string.yonghuxieyi), msg);
                } else
                    ShowToast.alertShortOfWhite(LoginActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                t.printStackTrace();
                ShowToast.alertShortOfWhite(LoginActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
