package com.daweichang.vcfarm.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daweichang.vcfarm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/25.
 */

public class SigninDialog extends Dialog {
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.textNumb)
    TextView textNumb;
    @BindView(R.id.textOk)
    TextView textOk;

    public SigninDialog(Context context) {
        super(context, com.xcc.mylibrary.R.style.Dialog_NoBg);
        this.context = context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_signin);
        ButterKnife.bind(this);
    }

    private Context context;

    public void setNumb(int numb) {
        textNumb.setText("" + numb);
    }

    @OnClick({R.id.textOk})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textOk:
                dismiss();
                break;
        }
    }
}
