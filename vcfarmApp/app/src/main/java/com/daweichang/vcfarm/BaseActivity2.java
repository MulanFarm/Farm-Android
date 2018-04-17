package com.daweichang.vcfarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xcc.mylibrary.widget.LoadDialog;

import java.util.Date;

import retrofit2.Call;

public class BaseActivity2 extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getManager().addActivity(this);

//        getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        getWindow().getDecorView().setBackgroundDrawable(null);
        decorView = getWindow().getDecorView();
        ViewTreeObserver vto = decorView.getViewTreeObserver();
        // view绘制完毕监听
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                decorView.getViewTreeObserver().removeOnPreDrawListener(this);
                onViewInitOver();
                return true;
            }
        });
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }

    private void init() {
        if (finishBtn() != 0) {
            findViewById(finishBtn()).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getManager().closeActivity(this);
    }

    private LoadDialog dialog;
    private Call request;
    private View decorView;

    public void onViewInitOver() {
    }

    public void openLoadDialog(Call request) {
        this.request = request;
        openLoadDialog();
    }

    public void openLoadDialog() {
        if (dialog == null) {
            dialog = new LoadDialog(this);
            dialog.setColorSchemeResources(R.color.colorAccent);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    loadDialogCancel();
                }
            });
        }
        dialog.show();
    }

    protected void loadDialogCancel() {
        if (request != null) {
            request.cancel();//stop();
            request = null;
        }
    }

    public void dismissDialog() {
        if (dialog != null) dialog.dismiss();
    }

    protected int finishBtn() {
        return 0;
    }

    private long oldTouchTime;
    private boolean isCanTouch;

    //防止短时间内多次启动同一个Activity
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        long time = new Date().getTime();
        if (time - oldTouchTime < 500) isCanTouch = false;
        else {
            oldTouchTime = time;
            isCanTouch = true;
        }
        if (isCanTouch) super.startActivityForResult(intent, requestCode, options);
    }
}
