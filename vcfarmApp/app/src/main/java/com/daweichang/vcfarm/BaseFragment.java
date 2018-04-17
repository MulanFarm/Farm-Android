package com.daweichang.vcfarm;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.xcc.mylibrary.widget.LoadDialog;

import retrofit2.Call;

public class BaseFragment extends Fragment {
    protected Handler handler;
    private Call request;
    private LoadDialog dialog;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendMessage(Message msg) {
    }

    public void openLoadDialog(Call request) {
        this.request = request;
        openLoadDialog();
    }

    public void openLoadDialog() {
        if (dialog == null) {
            dialog = new LoadDialog(getActivity());
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
        if (dialog != null)
            dialog.dismiss();
    }
}