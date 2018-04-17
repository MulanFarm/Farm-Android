package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.fragment.FarmFragment;
import com.daweichang.vcfarm.test.TestActivity;
import com.daweichang.vcfarm.utils.UserConfig;
import com.xcc.mylibrary.NetWorkUtil;
import com.xcc.mylibrary.OtherUtils;
import com.xcc.mylibrary.Sysout;
import com.xcc.mylibrary.widget.PublicDialog;
import com.xcc.mylibrary.widget.ShSwitchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/25.
 * 设置界面
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.layoutChangePwd)
    LinearLayout layoutChangePwd;
    @BindView(R.id.textClear)
    TextView textClear;
    @BindView(R.id.layoutClear)
    LinearLayout layoutClear;
    @BindView(R.id.textLev)
    TextView textLev;
    @BindView(R.id.layoutUpdata)
    LinearLayout layoutUpdata;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.switchView)
    ShSwitchView switchView;
    private AppVc appVc;

    public static void open(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        String appVerName = OtherUtils.getAppVerName(this);
        appVc = AppVc.getAppVc();
        //TODO 程序版本
//        textLev.setText(appVerName);
        textClear.setText(appVc.getDataSize());

        switchView.setOn(UserConfig.hasWifi());

        switchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            public void onSwitchStateChange(ShSwitchView switchView, boolean isOn) {
                UserConfig.setHasWifi(isOn);
                if (isOn && !NetWorkUtil.isWifiConnected(SettingActivity.this)) {
                    Intent intent = new Intent(FarmFragment.CameraDis);
                    intent.setPackage(getPackageName());
                    sendBroadcast(intent);
                }
            }
        });
    }

    @OnClick({R.id.layoutChangePwd, R.id.textTitle, R.id.layoutClear, R.id.layoutUpdata, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutChangePwd:
                ChangPwdActivity.open(this);
                break;
            case R.id.layoutClear: {//清楚缓存
                PublicDialog dialog = new PublicDialog(this);
                dialog.setTitle(null).setTextSize(16).setContent(getResources().getString(R.string.qingchuhuancun_)).setOnPublicDialogClick(new PublicDialog.OnPublicDialogClick() {
                    public void onConfirmClick() {
                        appVc.clearConfigAndData(1);
                        textClear.setText(appVc.getDataSize());
                    }

                    public void onCancelClick() {
                    }
                }).show();
            }
            break;
            case R.id.layoutUpdata://TODO 版本更新
                appMsg();
                break;
            case R.id.btnLogin: {
                PublicDialog dialog = new PublicDialog(this);
                dialog.setTitle(null).setTextSize(16).setContent(getResources().getString(R.string.tuichudenglu))
                        .setOnPublicDialogClick(new PublicDialog.OnPublicDialogClick() {
                            public void onConfirmClick() {
                                appVc.setLogin(false);
                                onBackPressed();
                            }

                            public void onCancelClick() {
                            }
                        }).show();
            }
            break;
            case R.id.textTitle:
                if (idx++ == 10)
                    startActivity(new Intent(this, TestActivity.class));
                break;
        }
    }

    private int idx = 0;

    //TODO 退出登录
    private void logout() {
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().logout(UserConfig.getToken());
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    Sysout.v("--logout--", body.toString());
                    if (body.isOk()) {
                        AppVc.getAppVc().setLogin(false);
                    }
                }
                Sysout.out("==退出登录接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
            }
        });
    }

    //    private void updata() {
//    }
    private void appMsg() {//程序信息
        openLoadDialog();
        Call<BaseRet<String>> baseRetCall = BaseService.getInstance().getServiceUrl().appInfo();
        baseRetCall.enqueue(new Callback<BaseRet<String>>() {
            public void onResponse(Call<BaseRet<String>> call, Response<BaseRet<String>> response) {
                dismissDialog();
                BaseRet<String> body = response.body();
                if (body != null) {
                    String string = getResources().getString(R.string.chengxuxinxi);
                    WebViewActivity.open(SettingActivity.this, string, body.data);
                }
            }

            public void onFailure(Call<BaseRet<String>> call, Throwable t) {
                dismissDialog();
            }
        });
    }
}
