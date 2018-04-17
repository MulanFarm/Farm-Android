package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.widget.ShowToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/29.
 * 关于我们
 */
public class AboutActivity extends BaseActivity {
    @BindView(R.id.layoutAbout)
    LinearLayout layoutAbout;
    @BindView(R.id.layoutAgree)
    LinearLayout layoutAgree;

    public static void open(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.layoutAbout, R.id.layoutAgree, R.id.layoutMap})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutAbout:
                aboutUs();
                break;
            case R.id.layoutAgree:
                toTreaty();
                break;
            case R.id.layoutMap:
                MapActivity.open(this);
                break;
        }
    }

    //关于我们
    private void aboutUs() {
        Call<BaseRet<String>> baseRetCall = BaseService.getInstance().getServiceUrl().aboutUs();
        openLoadDialog(baseRetCall);
        baseRetCall.enqueue(new Callback<BaseRet<String>>() {
            public void onResponse(Call<BaseRet<String>> call, Response<BaseRet<String>> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet<String> body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        String msg = body.data;
                        //textContent.setText(msg);
                        Resources res = getResources();
                        WebViewActivity.open(AboutActivity.this, res.getString(R.string.gymlnc), msg);
                    } else ShowToast.alertShortOfWhite(AboutActivity.this, body.msg);
                } else
                    ShowToast.alertShortOfWhite(AboutActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<BaseRet<String>> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(AboutActivity.this, R.string.wangluoyichang);
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
                    WebViewActivity.open(AboutActivity.this, res.getString(R.string.yonghuxieyi), msg);
                } else
                    ShowToast.alertShortOfWhite(AboutActivity.this, R.string.wangluoyichang);
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                t.printStackTrace();
                ShowToast.alertShortOfWhite(AboutActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
