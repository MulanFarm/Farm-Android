package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.data.CityMode;
import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.ImgSelectConfig;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.CitySelectView;
import com.daweichang.vcfarm.widget.SexSelectView;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.Sysout;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/28.
 * 个人信息
 */

public class UserMsgActivity extends BaseActivity {
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.textSex)
    TextView textSex;
    @BindView(R.id.layoutSex)
    LinearLayout layoutSex;
    @BindView(R.id.textAddr)
    TextView textAddr;
    @BindView(R.id.layoutAddr)
    LinearLayout layoutAddr;
    @BindView(R.id.editSignin)
    EditText editSignin;
    @BindView(R.id.textAddrD)
    EditText textAddrD;
    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;
    @BindView(R.id.textUserName)
    TextView textUserName;
    private AppVc appVc;
    private int sex;
    private SexSelectView sexSelectView;
    private CityMode cityMode1, cityMode2, cityMode3;
    private CitySelectView citySelectView;

    public static void open(Context context) {
        Intent intent = new Intent(context, UserMsgActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usermsg);
        ButterKnife.bind(this);

        appVc = AppVc.getAppVc();

        LoginMode mode = LoginMode.getMode(this);
        GlideUtils.displayOfUrl(UserMsgActivity.this, icon, mode.avatar, R.drawable.defaultpic);
        sex = mode.sex;
        textSex.setText(sex == 1 ? R.string.nan : R.string.nv);
        editName.setText(mode.getNickName());
        if (!TextUtils.isEmpty(mode.area)) textAddr.setText(mode.area);
        if (!TextUtils.isEmpty(mode.address)) textAddrD.setText(mode.address);
        if (!TextUtils.isEmpty(mode.signature)) editSignin.setText(mode.signature);
        textUserName.setText(mode.user_name);
    }

    @OnClick({R.id.textR, R.id.icon, R.id.layoutSex, R.id.layoutAddr})
    public void onClick(View view) {
        if (!appVc.isLogin()) {
            LoginActivity.open(this);
            return;
        }
        switch (view.getId()) {
            case R.id.textR:// 提交修改的信息
                updateProfile();
                break;
            case R.id.icon: {
                //取头像
                FunctionConfig squareConfig = ImgSelectConfig.getSquareConfig(this, true, 300, 300);
                GalleryFinal.openGallerySingle(200, squareConfig, new GalleryFinal.OnHanlderResultCallback() {
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        int size = resultList.size();
                        if (size > 0) {
                            PhotoInfo photoInfo = resultList.get(0);
                            String photoPath = photoInfo.getPhotoPath();
                            File file = new File(photoPath);
                            GlideUtils.displayOfFile(UserMsgActivity.this, icon, file);
                            uploadAvatar(file);
                        }
                    }

                    public void onHanlderFailure(int requestCode, String errorMsg) {
                    }
                });
            }
            break;
            case R.id.layoutSex://选择性别
                if (sexSelectView == null) {
                    sexSelectView = new SexSelectView(this);
                    sexSelectView.setOnSexSelectListener(new SexSelectView.OnSexSelectListener() {
                        public void selectSex(int sex) {
                            UserMsgActivity.this.sex = sex;
                            textSex.setText(sex == 1 ? R.string.nan : R.string.nv);
                        }
                    });
                }
                sexSelectView.showPopupWindow(layoutTitle);
                break;
            case R.id.layoutAddr://选择地址
                if (citySelectView == null) {
                    citySelectView = new CitySelectView(this);
                    citySelectView.setOnCitySelectListener(new CitySelectView.OnCitySelectListener() {
                        public void selectCity(CityMode cityMode1, CityMode cityMode2, CityMode cityMode3) {
                            UserMsgActivity.this.cityMode1 = cityMode1;
                            UserMsgActivity.this.cityMode2 = cityMode2;
                            UserMsgActivity.this.cityMode3 = cityMode3;
                            CityMode[] cityModes = CityMode.addressToArray(cityMode1, cityMode2, cityMode3);
                            String[] strings = CityMode.addressToString(cityModes);
                            textAddr.setText(strings[0]);
                        }
                    });
                }
                citySelectView.showPopupWindow(layoutTitle);
                break;
        }
    }

    private void uploadErr() {
        LoginMode mode = LoginMode.getMode(this);
        GlideUtils.displayOfUrl(UserMsgActivity.this, icon, mode.avatar, R.drawable.defaultpic);
    }

    //上传头像
    private void uploadAvatar(File file) {
        // 调试使用
        //保存文件
//        String folderPath = FileOperateUtil.getFolderPath(1) + File.separator + "1.jpg";
//        try {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.welcome1);
//            FileOutputStream fileOutputStream = new FileOutputStream(folderPath);
//            boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            Sysout.out("图片保存结果:" + compress);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        File file = new File(folderPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Call<BaseRet<String>> baseRetCall = BaseService.getInstance().getServiceUrl().uploadAvatar(UserConfig.getToken(), requestBody);
        baseRetCall.enqueue(new Callback<BaseRet<String>>() {
            public void onResponse(Call<BaseRet<String>> call, Response<BaseRet<String>> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet<String> body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        LoginMode mode = LoginMode.getMode(UserMsgActivity.this);
                        mode.avatar = body.data;
                        LoginMode.setMode(UserMsgActivity.this, mode);
                        ShowToast.alertShortOfWhite(UserMsgActivity.this, R.string.shangchuanchenggong);
                    } else {
                        uploadErr();
                        ShowToast.alertShortOfWhite(UserMsgActivity.this, body.msg);
                    }
                } else {
                    uploadErr();
                    ShowToast.alertShortOfWhite(UserMsgActivity.this, R.string.wangluoyichang);
                }

                Sysout.out("==上传头像接口返回成功==");
            }

            public void onFailure(Call<BaseRet<String>> call, Throwable t) {
                dismissDialog();
                uploadErr();
                ShowToast.alertShortOfWhite(UserMsgActivity.this, R.string.wangluoyichang);
            }
        });
    }

    private LoginMode copy;

    //修改信息
    private void updateProfile() {
        LoginMode mode = LoginMode.getMode(this);
        String nickName = editName.getText().toString();
        if (!TextUtils.isEmpty(nickName) && nickName.equals(mode.getNickName())) nickName = null;
        String cityStr = null;
        if (cityMode1 != null) {
            CityMode[] cityModes = CityMode.addressToArray(cityMode1, cityMode2, cityMode3);
            cityStr = CityMode.addressToString(cityModes)[0];
        }
        if (!TextUtils.isEmpty(cityStr) && cityStr.equals(mode.area)) cityStr = null;
        String address = textAddrD.getText().toString();
        if (!TextUtils.isEmpty(address) && address.equals(mode.address)) address = null;
        String signin = editSignin.getText().toString();
        if (!TextUtils.isEmpty(signin) && signin.equals(mode.signature)) signin = null;
        if (sex == mode.sex && TextUtils.isEmpty(nickName) && TextUtils.isEmpty(cityStr)
                && TextUtils.isEmpty(address) && TextUtils.isEmpty(signin)) {
            onBackPressed();
            return;
        }
        copy = mode.copy();
        copy.sex = sex;
        if (!TextUtils.isEmpty(nickName)) copy.setNickName(nickName);
        if (!TextUtils.isEmpty(cityStr)) copy.area = cityStr;
        if (!TextUtils.isEmpty(address)) copy.address = address;
        if (!TextUtils.isEmpty(signin)) copy.signature = signin;

        openLoadDialog();
        Call<BaseRet> updateProfile = BaseService.getInstance().getServiceUrl().updateProfile(
                UserConfig.getToken(), nickName, sex, cityStr, address, signin);
        updateProfile.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        LoginMode.setMode(UserMsgActivity.this, copy);
                        onBackPressed();
                        ShowToast.alertShortOfWhite(UserMsgActivity.this, R.string.xiugaichenggong);
                    } else
                        ShowToast.alertShortOfWhite(UserMsgActivity.this, body.msg);
                } else
                    ShowToast.alertShortOfWhite(UserMsgActivity.this, R.string.wangluoyichang);
                Sysout.out("==修改信息接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(UserMsgActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
