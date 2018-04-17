package com.daweichang.vcfarm.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daweichang.vcfarm.BaseFragment;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.AboutActivity;
import com.daweichang.vcfarm.activity.MainActivity;
import com.daweichang.vcfarm.activity.MessageListActivity;
import com.daweichang.vcfarm.activity.NotsListActivity;
import com.daweichang.vcfarm.activity.SettingActivity;
import com.daweichang.vcfarm.activity.UserMsgActivity;
import com.daweichang.vcfarm.activity.WalletActivity;
import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.UserConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/25.
 * 我的界面
 */
public class ZoneFrgment extends BaseFragment {
    @BindView(R.id.dot)
    TextView dot;
    @BindView(R.id.imgR)
    TextView imgR;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.textName)
    TextView textName;
    @BindView(R.id.textEitd)
    TextView textEitd;
    @BindView(R.id.layoutMsg)
    LinearLayout layoutMsg;
    @BindView(R.id.layoutWallet)
    LinearLayout layoutWallet;
    @BindView(R.id.layoutAbout)
    LinearLayout layoutAbout;
    @BindView(R.id.layoutSetting)
    LinearLayout layoutSetting;
    @BindView(R.id.layoutNotes)
    LinearLayout layoutNotes;
    @BindView(R.id.textTime)
    TextView textTime;
    private String timeStr;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainActivity.RefreshSignin.equals(action)) {
                int signInTime = UserConfig.getSignInTime();
                textTime.setVisibility(View.VISIBLE);
                textTime.setText(String.format(timeStr, "" + signInTime));
            }
        }
    };

    public static ZoneFrgment newInstance() {
        ZoneFrgment fragment = new ZoneFrgment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zone, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timeStr = getResources().getString(R.string.yiqiandao_tian);
        int signInTime = UserConfig.getSignInTime();
        if (signInTime == 0) {
            textTime.setVisibility(View.GONE);
        } else {
            textTime.setVisibility(View.VISIBLE);
            textTime.setText(String.format(timeStr, "" + signInTime));
        }

        IntentFilter filter = new IntentFilter(MainActivity.RefreshSignin);
        getActivity().registerReceiver(receiver, filter);
    }

    public void onResume() {
        super.onResume();
        LoginMode mode = LoginMode.getMode(getActivity());
        GlideUtils.displayOfUrl(getActivity(), icon, mode.avatar, R.drawable.defaultpic);
        textName.setText(mode.getNickName());
        msgList();
    }

    @OnClick({R.id.imgBack, R.id.imgR, R.id.layoutMsg, R.id.layoutWallet, R.id.layoutAbout, R.id.layoutSetting, R.id.layoutNotes})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack://信息
                dot.setVisibility(View.GONE);
                MessageListActivity.open(getActivity());
                break;
            case R.id.imgR://签到
                if (handler != null) handler.sendEmptyMessage(MainActivity.Handler_SignIn);
                break;
            case R.id.layoutMsg://个人信息
                UserMsgActivity.open(getActivity());
                break;
            case R.id.layoutWallet:
                WalletActivity.open(getActivity());
                break;
            case R.id.layoutAbout:
                AboutActivity.open(getActivity());
                break;
            case R.id.layoutSetting:
                SettingActivity.open(getActivity());
                break;
            case R.id.layoutNotes:
                NotsListActivity.open(getActivity());
                break;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    //消息列表
    private void msgList() {
//        Call<MessageRet> baseRetCall = BaseService.getInstance().getServiceUrl().msgList(UserConfig.getToken());
//        baseRetCall.enqueue(new Callback<MessageRet>() {
//            public void onResponse(Call<MessageRet> call, Response<MessageRet> response) {
//                if (AppVc.isLoginOut(response)) return;
//                MessageRet body = response.body();
//                if (body != null) {
//                    if (body.isOk()) {
//                        if (body.data != null && body.data.size() > 0) {
//                            long id = UserConfig.getMaxMsgId();
//                            ArrayList<MessageMode> modeArrayList = body.data;
////                            for (MessageMode mode:modeArrayList){
////                                mode.id
////                            }
//                            id = modeArrayList.size() - id;
//                            dot.setVisibility(id > 0 ? View.VISIBLE : View.GONE);
//                            dot.setText("" + id);
//                        } else dot.setVisibility(View.GONE);
//                    }
//                }
//                Sysout.out("==消息列表接口返回成功==");
//            }
//
//            public void onFailure(Call<MessageRet> call, Throwable t) {
//            }
//        });
    }
}
