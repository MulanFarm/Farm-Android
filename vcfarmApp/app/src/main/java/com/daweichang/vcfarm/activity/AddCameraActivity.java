package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.ImgSelectConfig;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.Sysout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/6.
 * 添加摄像头
 */
public class AddCameraActivity extends BaseActivity {
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.editNumb)
    EditText editNumb;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    //    @BindView(R.id.editName)
//    EditText editName;
    @BindView(R.id.editPwd)
    EditText editPwd;

    public static void open(Context context) {
        Intent intent = new Intent(context, AddCameraActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_camera);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.icon, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.icon://
                //取头像
                FunctionConfig squareConfig = ImgSelectConfig.getSquareConfig(this, true, 300, 300);
                GalleryFinal.openGallerySingle(200, squareConfig, new GalleryFinal.OnHanlderResultCallback() {
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        int size = resultList.size();
                        if (size > 0) {
                            PhotoInfo photoInfo = resultList.get(0);
                            String photoPath = photoInfo.getPhotoPath();
                            imgUrl = photoPath;
                            File file = new File(photoPath);
                            GlideUtils.displayOfFile(AddCameraActivity.this, icon, file);
                        }
                    }

                    public void onHanlderFailure(int requestCode, String errorMsg) {
                    }
                });
                break;
            case R.id.btnLogin:
                cameraBind();
                break;
        }
    }

    private String imgUrl;

    //绑定摄像头，即添加摄像头
    private void cameraBind() {
        if (TextUtils.isEmpty(imgUrl)) {
            ShowToast.alertShortOfWhite(this, R.string.qxztp);
            return;
        }
//        name = editName.getText().toString();
        numb = editNumb.getText().toString();
        pwd = editPwd.getText().toString();
        if (TextUtils.isEmpty(numb)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrsxtbh);
            return;
        }
//        if (TextUtils.isEmpty(name)) {
//            ShowToast.alertShortOfWhite(this, R.string.qsrsxtmc);
//            return;
//        }
        if (TextUtils.isEmpty(pwd)) {
            ShowToast.alertShortOfWhite(this, R.string.qsrsxtmm);
            return;
        }

        File file = new File(imgUrl);
        List<File> list = new ArrayList<>();
        list.add(file);
        MultipartBody multipartBody = filesToMultipartBody(list);
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().cameraBind(UserConfig.getToken(), multipartBody.parts());
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        sendBroadcast(new Intent(AppVc.Refresh));
                        onBackPressed();
                    }
                    ShowToast.alertShortOfWhite(AddCameraActivity.this, body.msg);
                } else
                    ShowToast.alertShortOfWhite(AddCameraActivity.this, R.string.wangluoyichang);
                Sysout.out("==绑定摄像头接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(AddCameraActivity.this, R.string.wangluoyichang);
            }
        });
    }

    private String /*name,*/ numb, pwd;

    public MultipartBody filesToMultipartBody(List<File> files) {
        //Part("id") String id,
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (int i = 1; i <= files.size(); i++) {
            File file = files.get(i - 1);
            //这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            builder.addFormDataPart("thumbnail[]", file.getName(), requestBody);
        }
        builder.addFormDataPart("camera_no", numb);
        builder.addFormDataPart("camera_device_pwd", pwd);
//        builder.addFormDataPart("name", name);
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }
}
