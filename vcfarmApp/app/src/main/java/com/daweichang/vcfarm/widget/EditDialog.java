package com.daweichang.vcfarm.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daweichang.vcfarm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/4/5.
 */

public class EditDialog extends Dialog {
    @BindView(R.id.editContent)
    EditText editContent;
    @BindView(R.id.textConfirm)
    TextView textConfirm;
    @BindView(R.id.textCancel)
    TextView textCancel;
    private Context context;
    private OnEditClick onEditClick;
    private String content;
    private int tag;

    public EditDialog(Context context) {
        super(context, R.style.Dialog_NoBg);
        this.context = context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_edit);
        ButterKnife.bind(this);
    }

    public EditDialog setContent(String content) {
        this.content = content;
        editContent.setText(content);
        return this;
    }

    public EditDialog setTag(int tag) {
        this.tag = tag;
        return this;
    }

    public EditDialog setOnEditClick(OnEditClick onEditClick) {
        this.onEditClick = onEditClick;
        return this;
    }

    @OnClick({R.id.textConfirm, R.id.textCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textConfirm:
                String s = editContent.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    ShowToast.alertShortOfWhite(context, R.string.qsrlr);
                    return;
                }
                if (!s.equals(content) && onEditClick != null)
                    onEditClick.onConfirmClick(s, tag);
                break;
            case R.id.textCancel:
                if (onEditClick != null)
                    onEditClick.onCancelClick();
                break;
        }
        dismiss();
    }

    public interface OnEditClick {
        /**
         * 右边确定键
         */
        public void onConfirmClick(String text, int tag);

        /**
         * 左边取消键
         */
        public void onCancelClick();
    }
}