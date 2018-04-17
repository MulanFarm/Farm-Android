package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.adapter.NotsListAdapter;
import com.daweichang.vcfarm.mode.NoteMode;
import com.daweichang.vcfarm.netret.NoteRet;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.Sysout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/27.
 * 笔记中心
 */
public class NotsListActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    private List<NoteMode> noteModeList;
    private NotsListAdapter adapter;

    public static void open(Context context) {
        Intent intent = new Intent(context, NotsListActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nots_list);
        ButterKnife.bind(this);

        noteModeList = new ArrayList<>();
        adapter = new NotsListAdapter(this, noteModeList);
        listView.setAdapter(adapter);

//        initRefreshLayout();
        noteList();
    }

    @OnClick(R.id.imgR)
    public void onClick() {
        Intent intent = new Intent();
        intent.setClassName(this, MainActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isGoto", true);
        intent.putExtra("idx", 0);
        startActivity(intent);
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


    //笔记列表
    private void noteList() {//TODO 待测试
        Call<NoteRet> baseRetCall = BaseService.getInstance().getServiceUrl().noteList(UserConfig.getToken());
        baseRetCall.enqueue(new Callback<NoteRet>() {
            public void onResponse(Call<NoteRet> call, Response<NoteRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                NoteRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        if (body.data != null) {
                            List<NoteMode> list = body.data;
                            if (list!=null&&list.size()>0){
                                noteModeList.addAll(list);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        ShowToast.alertShortOfWhite(NotsListActivity.this, body.msg);
                    }
                } else ShowToast.alertShortOfWhite(NotsListActivity.this, R.string.wangluoyichang);
                Sysout.out("==笔记列表接口返回成功==");
            }

            public void onFailure(Call<NoteRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(NotsListActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
