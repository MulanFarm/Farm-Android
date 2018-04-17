//package com.daweichang.vcfarm.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.daweichang.vcfarm.BaseActivity;
//import com.daweichang.vcfarm.BaseRet;
//import com.daweichang.vcfarm.BaseService;
//import com.daweichang.vcfarm.R;
//import com.daweichang.vcfarm.widget.ShowToast;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//TODO 已丢弃
///**
// * Created by Administrator on 2017/3/23.
// * 用户协议
// */
//public class UserTreatyActivity extends BaseActivity {
//    @BindView(R.id.textTitle)
//    TextView textTitle;
//    @BindView(R.id.imgBack)
//    ImageView imgBack;
//    @BindView(R.id.textContent)
//    TextView textContent;
//
//    public static void open(Context context) {
//        Intent intent = new Intent(context, UserTreatyActivity.class);
//        context.startActivity(intent);
//    }
//
//    protected int finishBtn() {
//        return R.id.imgBack;
//    }
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_user_treaty);
//        ButterKnife.bind(this);
//
//        getData();
//    }
//
//    @OnClick({R.id.textTitle})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.textTitle:
//                onBackPressed();
//                break;
//        }
//    }
//
//    private void getData() {
//        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().appinfoAgreement();
//        openLoadDialog();
//        baseRetCall.enqueue(new Callback<BaseRet>() {
//            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
//                dismissDialog();
//                // 处理返回数据
//                BaseRet body = response.body();
//                if (body != null && body.isOk()) {
//                    String msg = (String) body.data;
//                    textContent.setText(msg);
//                } else
//                    ShowToast.alertShortOfWhite(UserTreatyActivity.this, R.string.wangluoyichang);
//            }
//
//            public void onFailure(Call<BaseRet> call, Throwable t) {
//                dismissDialog();
//                t.printStackTrace();
//                ShowToast.alertShortOfWhite(UserTreatyActivity.this, R.string.wangluoyichang);
//            }
//        });
//    }
//}
