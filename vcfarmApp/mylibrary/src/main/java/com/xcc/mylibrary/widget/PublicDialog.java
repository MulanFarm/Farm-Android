package com.xcc.mylibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xcc.mylibrary.R;

/**
 * 提供统一的dialog
 */
public class PublicDialog extends Dialog implements OnClickListener {

    public PublicDialog(Context context) {
        super(context, R.style.Dialog_NoBg);
        this.context = context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_public);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textContent = (TextView) findViewById(R.id.textContent);
        textConfirm = (TextView) findViewById(R.id.textConfirm);
        textCancel = (TextView) findViewById(R.id.textCancel);

        textConfirm.setOnClickListener(this);
        textCancel.setOnClickListener(this);
    }

    private Context context;
    private TextView textTitle, textContent, textConfirm, textCancel;

    /**
     * 设置标题，标题可以设置null或""时不显示
     */
    public PublicDialog setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            textTitle.setVisibility(View.GONE);
        } else {
            textTitle.setVisibility(View.VISIBLE);
            textTitle.setText(title);
        }
        return this;
    }

    public PublicDialog setContent(String content) {
        textContent.setText(content);
        return this;
    }

    public PublicDialog setTextSize(int size) {
        textContent.setTextSize(size);
        return this;
    }

    /**
     * 右边确定键
     */
    public PublicDialog setConfirmBtnText(String confirmText) {
        textConfirm.setText(confirmText);
        return this;
    }

    /**
     * 左边取消键<br>
     * 取消左键传""或null
     */
    public PublicDialog setCancelBtnText(String cancelText) {
        if (TextUtils.isEmpty(cancelText)) {
            textCancel.setVisibility(View.GONE);
            findViewById(R.id.viewLine).setVisibility(View.GONE);
            textConfirm.setBackgroundResource(R.drawable.dialog_bott_xml);
            return this;
        }
        textCancel.setText(cancelText);
        return this;
    }

    /**
     * 设置蓝色按钮
     *
     * @param idx 0确定键，1取消键(默认确定键)
     */
    public PublicDialog setBlueBtn(int idx) {
        if (idx == 0) {
            textConfirm.setTextColor(context.getResources().getColor(R.color.onTextBlue));
            textCancel.setTextColor(context.getResources().getColor(R.color.onBlack));
        } else {
            textCancel.setTextColor(context.getResources().getColor(R.color.onTextBlue));
            textConfirm.setTextColor(context.getResources().getColor(R.color.onBlack));
        }
        return this;
    }

    public OnPublicDialogClick onPublicDialogClick;

    public PublicDialog setOnPublicDialogClick(
            OnPublicDialogClick onPublicDialogClick) {
        this.onPublicDialogClick = onPublicDialogClick;
        return this;
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.textConfirm) {
            if (onPublicDialogClick != null)
                onPublicDialogClick.onConfirmClick();
        } else if (i == R.id.textCancel) {
            if (onPublicDialogClick != null)
                onPublicDialogClick.onCancelClick();
        }
        dismiss();
    }

    public interface OnPublicDialogClick {
        /**
         * 右边确定键
         */
        public void onConfirmClick();

        /**
         * 左边取消键
         */
        public void onCancelClick();
    }
}
