package com.daweichang.vcfarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.utils.UserConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 磁磁帅 on 2017/6/7.
 * 功能：
 */

public class StartupActivity extends BaseActivity {
    @BindView(R.id.icon)
    ImageView icon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        icon.postDelayed(new Runnable() {
            public void run() {
                goTO();
            }
        }, 3000);
    }

    protected void goTO() {
        if (!UserConfig.isWelcome()) {
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            AppVc appVc = AppVc.getAppVc();
            if (appVc.isLogin()) MainActivity.openTop(this);
            else LoginActivity.open(this);
        }
        finish();
    }
}
