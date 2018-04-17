package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity2;
import com.daweichang.vcfarm.BaseFragment;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.fragment.ArchivesFragment;
import com.daweichang.vcfarm.fragment.FarmFragment;
import com.daweichang.vcfarm.fragment.GuideFrgment;
import com.daweichang.vcfarm.fragment.ZoneFrgment;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.daweichang.vcfarm.widget.SigninDialog;
import com.xcc.mylibrary.Sysout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 摄像头 ：7019032
 * 密码：abc123
 * <p>
 * 1.进行音视频监控等操作要在登陆之后
 * 2.如果用到ElianNative，ElianNative只能放在com.mediatek.elian的包名下面
 */
public class MainActivity extends BaseActivity2 {
    public static boolean isP = true;//是否竖屏
    @BindView(R.id.tab1)
    LinearLayout tab1;
    @BindView(R.id.tab2)
    LinearLayout tab2;
    @BindView(R.id.tab3)
    LinearLayout tab3;
    @BindView(R.id.tab4)
    LinearLayout tab4;
    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;
    private View oldView;
    private BaseFragment oldFragment;
    private AppVc appVc;
    private BaseFragment fragments[];
    private boolean isExit = false;
    private static final int Handler_IsExit = 1;//是否退出
    public static final int Handler_SignIn = 2;//签到
    public static final int Handler_TO = 3;//跳转
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Handler_IsExit:
                    isExit = false;
                    break;
                case Handler_SignIn:
                    signIn();
                    break;
                case Handler_TO:
                    tab1.performClick();
                    break;
            }
        }
    };

    public static void openTop(Context context) {
        Intent intent = new Intent();
        intent.setClassName(context, MainActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isP) {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragments = new BaseFragment[]{FarmFragment.newInstance(),
                ArchivesFragment.newInstance(),
                GuideFrgment.newInstance(),
                ZoneFrgment.newInstance(),};
        if (savedInstanceState != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            Fragment fragment = fm.findFragmentByTag(FarmFragment.class.getName());
            if (fragment != null){
//                fragmentTransaction.remove(fragment);
                fragments[0] = (BaseFragment) fragment;
            }

            fragment = fm.findFragmentByTag(ArchivesFragment.class.getName());
            if (fragment != null) fragments[1] = (BaseFragment) fragment;

            fragment = fm.findFragmentByTag(GuideFrgment.class.getName());
            if (fragment != null) fragments[2] = (BaseFragment) fragment;

            fragment = fm.findFragmentByTag(ZoneFrgment.class.getName());
            if (fragment != null) fragments[3] = (BaseFragment) fragment;

            for (BaseFragment f : fragments)
                fragmentTransaction.hide(f);
            fragmentTransaction.commitAllowingStateLoss();
        }

        for (BaseFragment fragment : fragments) {
            fragment.setHandler(handler);
        }
//        tab1.performClick();
        handler.sendEmptyMessageDelayed(Handler_TO, 10);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("isGoto", false)) {
            LinearLayout layouts[] = new LinearLayout[]{tab1, tab2, tab3, tab4};
            int idx = intent.getIntExtra("idx", 0);
            layouts[idx].performClick();
        }
        AppVc appVc = AppVc.getAppVc();
        if (!appVc.isLogin()) LoginActivity.open(this);
    }

    @OnClick({R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4})
    public void onClick(View view) {
        if (oldView != null) {
            if (view == oldView) return;
            else oldView.setSelected(false);
        }
        oldView = view;
        view.setSelected(true);
        switch (view.getId()) {
            case R.id.tab1:
                switchFragment(fragments[0]);
                break;
            case R.id.tab2:
                switchFragment(fragments[1]);
                break;
            case R.id.tab3:
                switchFragment(fragments[2]);
                break;
            case R.id.tab4:
                switchFragment(fragments[3]);
                break;
        }
    }

    public void switchFragment(BaseFragment fragment) {
        if (fragment == oldFragment) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (!fragment.isAdded()) {
            transaction.add(R.id.layoutFrame, fragment, fragment.getClass().getName());
            transaction.show(fragment);
        } else {
            transaction.show(fragment);
            fragment.onResume();
        }
        if (oldFragment != null) transaction.hide(oldFragment);
        oldFragment = fragment;
        transaction.commitAllowingStateLoss();
    }

    protected void onResume() {
        super.onResume();
//        if (!UserConfig.isWelcome()) {
//            startActivity(new Intent(this, WelcomeActivity.class));
//            return;
//        }
        AppVc appVc = AppVc.getAppVc();
        if (!appVc.isLogin()) LoginActivity.open(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) //在横屏
            //隐藏底部
            layoutBottom.setVisibility(View.GONE);
        else layoutBottom.setVisibility(View.VISIBLE);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                onBackPressed();
            } else {
                isExit = true;
                ShowToast.alertShortOfWhite(this, R.string.zayctc);
                handler.sendEmptyMessageDelayed(Handler_IsExit, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    //刷新签到
    public static final String RefreshSignin = "com.daweichang.vcfarm.activity.MainActivity.RefreshSignin";

    //需要添加时间显示 用户签到
    private void signIn() {
        openLoadDialog();
        Call<BaseRet<Integer>> loginRetCall = BaseService.getInstance().getServiceUrl().signIn(UserConfig.getToken());
        loginRetCall.enqueue(new Callback<BaseRet<Integer>>() {
            public void onResponse(Call<BaseRet<Integer>> call, Response<BaseRet<Integer>> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet<Integer> body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        SigninDialog dialog = new SigninDialog(MainActivity.this);
                        if (body.data != null) {
                            dialog.setNumb(body.data);
                            UserConfig.setSignInTime(body.data);
                            sendBroadcast(new Intent(RefreshSignin));
                        }
                        dialog.show();
                    } else {
                        ShowToast.alertShortOfWhite(MainActivity.this, body.msg);
                    }
                } else ShowToast.alertShortOfWhite(MainActivity.this, R.string.wangluoyichang);
                Sysout.v("--signIn--", body.toString());
                Sysout.out("==签到接口返回成功==");
            }

            public void onFailure(Call<BaseRet<Integer>> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(MainActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
