package com.xcc.mylibrary.widget;

import android.app.Dialog;
import android.content.Context;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.xcc.mylibrary.R;

/**
 *
 */
public class LoadDialog extends Dialog {
    private CircleProgressBar progressBar;

    public LoadDialog(Context context) {
        super(context, R.style.Dialog_NoBg);
        this.context = context;
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_load);

        progressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        //progressBar.setShowArrow(true);
        progressBar.setCircleBackgroundEnabled(false);
        progressBar.setColorSchemeResources(R.color.onTextBlue);
    }

    private Context context;

    public void setColorSchemeResources(int... colorRes) {
        progressBar.setColorSchemeResources(colorRes);
    }
}
