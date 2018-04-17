package com.daweichang.vcfarm.widget;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 也可以使用Snackbar
 */
public class ShowToast {
    private static void alertShort(Context context, String msg) {
        if (context != null && msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private static void alertLong(Context context, String msg) {
        if (context != null && msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    private ShowToast() {
    }

    private static ShowToast showToast;

    private synchronized static void init() {
        if (showToast == null)
            showToast = new ShowToast();
    }

    public static void alertShortOfWhite(Context context, int msgRes) {
        String string = context.getResources().getString(msgRes);
        alertShortOfWhite(context, string);
    }

    public static void alertShortOfWhite(Context context, String msg) {
        init();
        showToast.alertOfWhite(context, msg, 0);
    }

    public static void alertLongOfWhite(Context context, String msg) {
        init();
        showToast.alertOfWhite(context, msg, 1);
    }

    private Toast toast;
    private TextView textView;

    private void alertOfWhite(Context context, String msg, int duration) {
        //TODO
        alertShort(context, msg);
//        if (toast == null) {//初始化Toast样式
//            toast = new Toast(context);
//            View view = LayoutInflater.from(context).inflate(
//                    R.layout.toast_show, null);
//            textView = (TextView) view.findViewById(R.id.textView1);
//            toast.setView(view);
//            int v = (int) /*(ScreenUtils.getStatusHeight(context) + */context.getResources().getDimension(R.dimen.title_height);
//            int screenWidth = ScreenUtils.getScreenWidth(context);
//            textView.getLayoutParams().width = screenWidth;
//            toast.setGravity(Gravity.TOP, 0, v);
//        }
//        if (context != null && msg != null) {
//            textView.setText(msg);
//            toast.setDuration(duration);
//            toast.show();
//        }
    }
}
