package com.daweichang.vcfarm.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.daweichang.vcfarm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/24.
 */
public class SexSelectView extends PopupWindow {
    @BindView(R.id.textSex1)
    TextView textSex1;
    @BindView(R.id.textSex2)
    TextView textSex2;
    @BindView(R.id.textCancel)
    TextView textCancel;

    public SexSelectView(Activity context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        conentView = inflater.inflate(R.layout.sex_select_layout, null);
        ButterKnife.bind(this, conentView);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 刷新状态
        this.update();

        setBackgroundDrawable(new BitmapDrawable());
        conentView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) dismiss();
                return false;
            }
        });
    }

    private Activity context;
    private View conentView;
    private OnSexSelectListener onSexSelectListener;

    public void setOnSexSelectListener(OnSexSelectListener onSexSelectListener) {
        this.onSexSelectListener = onSexSelectListener;
    }

    //显示
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            showAtLocation(parent, Gravity.LEFT, 0, 0);
        } else {
            super.dismiss();
        }
    }

    @OnClick({R.id.textSex1, R.id.textSex2, R.id.textCancel})
    public void onClick(View view) {
        if (onSexSelectListener != null)
            switch (view.getId()) {
                case R.id.textSex1:
                    onSexSelectListener.selectSex(1);
                    break;
                case R.id.textSex2:
                    onSexSelectListener.selectSex(2);
                    break;
            }
        dismiss();
    }


    public interface OnSexSelectListener {
        /**
         * @param sex 1男，0女
         */
        void selectSex(int sex);
    }
}
