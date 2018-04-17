package com.daweichang.vcfarm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseRet;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.adapter.CameraAdapter;
import com.daweichang.vcfarm.fragment.FarmFragment;
import com.daweichang.vcfarm.mode.CameraMode;
import com.daweichang.vcfarm.netret.CameraListRet;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.google.gson.Gson;
import com.xcc.mylibrary.Sysout;
import com.xcc.mylibrary.widget.PublicDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/6.
 */
public class CameraListActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, CameraAdapter.OnDelectListener {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    private ArrayList<CameraMode> modeList;
    private CameraAdapter adapter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            msgList();
        }
    };

    public static void open(Context context) {
        Intent intent = new Intent(context, CameraListActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_list);
        ButterKnife.bind(this);

        setSwipeBackEnable(false);

        modeList = new ArrayList<>();
        adapter = new CameraAdapter(this, modeList);
        listView.setAdapter(adapter);
        adapter.setOnDelectListener(this);

        // 待完善
        //initRefreshLayout();
        msgList();

        IntentFilter filter = new IntentFilter(AppVc.Refresh);
        registerReceiver(receiver, filter);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout设置代理
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(refreshViewHolder);
        // 可选配置  -------------END
    }

    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //request.postRefresh();
    }

    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
//        if (request.hasNext()) {
//            request.postNext();
//            return true;
//        }
        return false;
    }

    //摄像头列表
    private void msgList() {//is_selected
        modeList.clear();
        Call<CameraListRet> baseRetCall = BaseService.getInstance().getServiceUrl().cameraList(UserConfig.getToken());
        baseRetCall.enqueue(new Callback<CameraListRet>() {
            public void onResponse(Call<CameraListRet> call, Response<CameraListRet> response) {
                if (AppVc.isLoginOut(response)) return;
                CameraListRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        if (body.data != null && body.data.size() > 0) {
                            ArrayList<CameraMode> modeArrayList = body.data;
                            modeList.addAll(modeArrayList);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        ShowToast.alertShortOfWhite(CameraListActivity.this, body.msg);
                    }
                } else
                    ShowToast.alertShortOfWhite(CameraListActivity.this, R.string.wangluoyichang);
                Sysout.out("==摄像头列表接口返回成功==");
            }

            public void onFailure(Call<CameraListRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(CameraListActivity.this, R.string.wangluoyichang);
            }
        });
    }

    @OnClick(R.id.btnPay)
    public void onViewClicked() {
        AddCameraActivity.open(this);
    }

    //取消绑定摄像头
    public void onDelect(CameraMode mode) {
        modeDelect = mode;
        PublicDialog dialog = new PublicDialog(this);
        String s = getResources().getString(R.string.querenshanchu);
        dialog.setTitle(null).setContent(s).setTextSize(18).setOnPublicDialogClick(new PublicDialog.OnPublicDialogClick() {
            public void onConfirmClick() {
                cameraDelete();
            }

            public void onCancelClick() {
                modeDelect = null;
            }
        }).show();
    }

    private CameraMode modeSelect;

    public void onSelect(CameraMode mode) {//选择摄像头
        modeSelect = mode;
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().cameraSelect(UserConfig.getToken(), mode.id);
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        Intent intent = new Intent(FarmFragment.action);
                        intent.putExtra("json", new Gson().toJson(modeSelect));
                        sendBroadcast(intent);
                        onBackPressed();
                    }
                    //ShowToast.alertShortOfWhite(CameraListActivity.this, body.msg);
                } else
                    ShowToast.alertShortOfWhite(CameraListActivity.this, R.string.wangluoyichang);
                Sysout.out("==取消绑定摄像头接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(CameraListActivity.this, R.string.wangluoyichang);
            }
        });

    }

    private CameraMode modeDelect;

    public void cameraDelete() {
        Call<BaseRet> baseRetCall = BaseService.getInstance().getServiceUrl().cameraDelete(UserConfig.getToken(), modeDelect.id);
        baseRetCall.enqueue(new Callback<BaseRet>() {
            public void onResponse(Call<BaseRet> call, Response<BaseRet> response) {
                if (AppVc.isLoginOut(response)) return;
                BaseRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        msgList();
                    }
                    ShowToast.alertShortOfWhite(CameraListActivity.this, body.msg);
                } else
                    ShowToast.alertShortOfWhite(CameraListActivity.this, R.string.wangluoyichang);
                Sysout.out("==取消绑定摄像头接口返回成功==");
            }

            public void onFailure(Call<BaseRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(CameraListActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
