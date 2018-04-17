package com.daweichang.vcfarm.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.AlbumsMode;
import com.daweichang.vcfarm.mode.ArchiveMode;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.google.gson.Gson;
import com.xcc.mylibrary.Sysout;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
 * Created by Administrator on 2017/3/27.
 * 档案详情
 */
public class ArchivesDetailsActivity extends BaseActivity {
    @BindView(R.id.textTitle)
    TextView textTitle;
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.img3)
    ImageView img3;
    @BindView(R.id.img4)
    ImageView img4;
    @BindView(R.id.img5)
    ImageView img5;
    @BindView(R.id.textTime)
    TextView textTime;
    @BindView(R.id.textAge)
    EditText textAge;
    @BindView(R.id.textWeight)
    EditText textWeight;
    @BindView(R.id.textHeight)
    EditText textHeight;
    @BindView(R.id.textLike)
    EditText textLike;
    @BindView(R.id.textHate)
    EditText textHate;
    @BindView(R.id.textType)
    EditText textType;
    //    @BindView(R.id.textType)
//    TextView textType;
//    @BindView(R.id.textTime)
//    TextView textTime;
//    @BindView(R.id.textAge)
//    TextView textAge;
//    @BindView(R.id.textWeight)
//    TextView textWeight;
//    @BindView(R.id.textHeight)
//    TextView textHeight;
//    @BindView(R.id.textLike)
//    TextView textLike;
//    @BindView(R.id.textHate)
//    TextView textHate;
    private ArchiveMode mode;
    // private EditDialog dialog;
    private DatePickerDialog dialog;
    private int year, monthOfYear, dayOfMonth;

    public static void open(Context context, ArchiveMode mode) {
        Intent intent = new Intent(context, ArchivesDetailsActivity.class);
        intent.putExtra("json", new Gson().toJson(mode));
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_archives_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        mode = new Gson().fromJson(json, ArchiveMode.class);


        archiveDetail();
//        dialog = new EditDialog(this);
//        dialog.setOnEditClick(this);
    }

    private ArchiveMode modeD;

    private void initData(ArchiveMode mode) {
        modeD = mode;
        //Resources res = getResources();
        textTitle.setText(mode.getName());
        textType.setText(mode.variety);
        textTime.setText(mode.getAdopTime());
        textAge.setText(mode.age);
        textWeight.setText(mode.weight + "");
        textHeight.setText(mode.height + "");
        textLike.setText(mode.hobby);
        textHate.setText(mode.hate);

        List<AlbumsMode> albums = mode.albums;
        if (albums != null && albums.size() > 0) {
            ImageView imageViews[] = new ImageView[]{img1, img2, img3, img4, img5};
            for (int i = 0; i < albums.size(); i++) {
                AlbumsMode albumsMode = albums.get(i);
                GlideUtils.displayOfUrl(ArchivesDetailsActivity.this, imageViews[i], albumsMode.pic_path);
            }
        }
    }

    @OnClick({R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.layoutTime, R.id.textR})
    public void onClick(View view) {
        int id = view.getId();
        //dialog.setTag(id);
        switch (id) {
            case R.id.textR:
                archiveUpdate();
                break;
            case R.id.img1:
            case R.id.img2:
            case R.id.img3:
            case R.id.img4:
            case R.id.img5: {
                FunctionConfig config = new FunctionConfig.Builder()
                        .setMutiSelectMaxSize(5).build();
                GalleryFinal.openGalleryMuti(200, config, new GalleryFinal.OnHanlderResultCallback() {
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        int size = resultList.size();
                        if (size > 0) {
                            size = size > 5 ? 5 : size;
                            List<File> files = new ArrayList<>();
                            ImageView imageViews[] = new ImageView[]{img1, img2, img3, img4, img5};
                            for (int i = 0; i < size; i++) {
                                String photoPath = resultList.get(i).getPhotoPath();
                                File file = new File(photoPath);
                                files.add(file);
                                GlideUtils.displayOfFile(ArchivesDetailsActivity.this, imageViews[i], file);
                            }
                            archiveUploadAlbum(files);
                        }
                    }

                    public void onHanlderFailure(int requestCode, String errorMsg) {
                    }
                });
            }
            break;
//            case R.id.textTitle:
//                dialog.setContent(mode.name).show();
//                break;
//            case R.id.layoutType:
//                dialog.setContent(mode.variety).show();
//                break;
            case R.id.layoutTime:
                /**
                 * 实例化一个DatePickerDialog的对象
                 * 第二个参数是一个DatePickerDialog.OnDateSetListener匿名内部类，当用户选择好日期点击done会调用里面的onDateSet方法
                 */
                if (dialog == null) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    monthOfYear = calendar.get(Calendar.MONTH);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                    hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
//                    minute = calendar.get(Calendar.MINUTE);
                    dialog = new DatePickerDialog(ArchivesDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            textTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, year, monthOfYear, dayOfMonth);
                }
                dialog.show();
//                dialog.setContent(mode.getAdopTime()).show();
                break;
//            case R.id.layoutAge:
//                dialog.setContent(mode.age).show();
//                break;
//            case R.id.layoutWeight:
//                dialog.setContent(mode.weight + "").show();
//                break;
//            case R.id.layoutHeight:
//                dialog.setContent(mode.height + "").show();
//                break;
//            case R.id.layoutLike:
//                dialog.setContent(mode.hobby).show();
//                break;
//            case R.id.layoutHate:
//                dialog.setContent(mode.hate).show();
//                break;
        }
    }

    // 返回数据需要修改 档案详细
    private void archiveDetail() {
        openLoadDialog();
        Call<BaseRet<ArchiveMode>> baseRetCall = BaseService.getInstance().getServiceUrl().archiveDetail(UserConfig.getToken(), mode.id);
        baseRetCall.enqueue(new Callback<BaseRet<ArchiveMode>>() {
            public void onResponse(Call<BaseRet<ArchiveMode>> call, Response<BaseRet<ArchiveMode>> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet<ArchiveMode> body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        ArchiveMode data = body.getData();
                        initData(data);
                    } else {
                        ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, body.msg);
                        onBackPressed();
                    }
                } else {
                    ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.wangluoyichang);
                    onBackPressed();
                }
                Sysout.out("==档案详细接口返回成功==");
            }

            public void onFailure(Call<BaseRet<ArchiveMode>> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.wangluoyichang);
                onBackPressed();
            }
        });
    }

    //修改档案
    private void archiveUpdate(/*Call<BaseRet> baseRetCall*/) {
        openLoadDialog();
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().archiveUpdate(UserConfig.getToken(), mode.id
                , textType.getText().toString(), textTime.getText().toString(), textTitle.getText().toString(), textAge.getText().toString(),
                textWeight.getText().toString(), textHeight.getText().toString(), null, textLike.getText().toString(), textHate.getText().toString());
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        archiveDetail();
                    } else {
                        ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, body.msg);
                    }
                } else {
                    ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.wangluoyichang);
                }
                Sysout.out("==修改档案接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.wangluoyichang);
            }
        });
    }

    public MultipartBody filesToMultipartBody(List<File> files) {
        //Part("id") String id,
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (int i = 1; i <= files.size(); i++) {
            File file = files.get(i - 1);
            //这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
//            builder.addFormDataPart("file", file.getName(), requestBody);
            builder.addFormDataPart("imgs[]", file.getName(), requestBody);
        }
        builder.addFormDataPart("id", mode.id);
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    // 需要再次调试完善 上传图册
    private void archiveUploadAlbum(List<File> files) {
        openLoadDialog();
        //调试使用
        //保存文件
        MultipartBody imgs = filesToMultipartBody(files);
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().archiveUploadAlbum(UserConfig.getToken(), imgs.parts());
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
//                        ArchiveMode data = body.getData();
//                        initData(data);
                        ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.shangchuanchenggong);
                    } else {
                        ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, body.msg);
                    }
                } else
                    ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.wangluoyichang);
                Sysout.out("==上传图册接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(ArchivesDetailsActivity.this, R.string.wangluoyichang);
            }
        });
    }

//    public void onConfirmClick(String text, int tag) {
//        String token = UserConfig.getToken();
//        Call<BaseRet> baseRetCall = null;
//        ServiceUrl serviceUrl = BaseService.getInstance().getServiceUrl();
//        //= BaseService.getInstance().getServiceUrl().archiveUpdate(UserConfig.getToken(), "1234567890", null, null, "磁磁帅", null, null, null, null, null, null);
//        switch (tag) {
//            case R.id.layoutType:
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, text, null, null, null, null, null, null, null, null);
//                break;
//            case R.id.layoutTime:
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, text, null, null, null, null, null, null, null);
//                break;
//            case R.id.textTitle:
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, null, text, null, null, null, null, null, null);
//                break;
//            case R.id.layoutAge:
//                if (!OtherUtils.isNumeric(text)) {
//                    ShowToast.alertShortOfWhite(this, R.string.qsrsz);
//                    return;
//                }
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, null, null, text, null, null, null, null, null);
//                break;
//            case R.id.layoutWeight:
//                try {
//                    Double.parseDouble(text);
//                } catch (Exception e) {
//                    ShowToast.alertShortOfWhite(this, R.string.qsrsz);
//                    return;
//                }
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, null, null, null, text, null, null, null, null);
//                break;
//            case R.id.layoutHeight:
//                try {
//                    Double.parseDouble(text);
//                } catch (Exception e) {
//                    ShowToast.alertShortOfWhite(this, R.string.qsrsz);
//                    return;
//                }
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, null, null, null, null, text, null, null, null);
//                break;
//            case R.id.layoutLike:
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, null, null, null, null, null, null, text, null);
//                break;
//            case R.id.layoutHate:
//                baseRetCall = serviceUrl.archiveUpdate(token, mode.id, null, null, null, null, null, null, null, null, text);
//                break;
//        }
////        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().archiveUpdate(UserConfig.getToken(), "1234567890"
////                , null, null, "磁磁帅", null, null, null, null, null, null);
//        if (baseRetCall != null)
//            archiveUpdate(baseRetCall);
//    }
}
