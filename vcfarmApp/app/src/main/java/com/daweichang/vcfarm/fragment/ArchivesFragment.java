package com.daweichang.vcfarm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseFragment;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.adapter.ArchivesAdapter;
import com.daweichang.vcfarm.mode.ArchiveMode;
import com.daweichang.vcfarm.netret.ArchiveRet;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.Sysout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/25.
 * 档案记录
 */
public class ArchivesFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    private ArchivesAdapter adapter;
    private List<ArchiveMode> modeList;

    public static ArchivesFragment newInstance() {
        ArchivesFragment fragment = new ArchivesFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archives, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        modeList = new ArrayList<>();
        adapter = new ArchivesAdapter(getActivity(), modeList);
        listView.setAdapter(adapter);

        //initRefreshLayout();

    }

    public void onResume() {
        super.onResume();
        modeList.clear();
        archiveList();
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout设置代理
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        // 设置下拉刷新和上拉加载更多的风格
        refreshLayout.setRefreshViewHolder(refreshViewHolder);
        // 可选配置  -------------END
    }

    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //request.postRefresh();
        archiveList();
    }

    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
//        if (request.hasNext()) {
//            request.postNext();
//            return true;
//        }
        return false;
    }

    //档案列表
    private void archiveList() {
        Call<ArchiveRet> baseRetCall = BaseService.getInstance().getServiceUrl().archiveList(UserConfig.getToken());
        baseRetCall.enqueue(new Callback<ArchiveRet>() {
            public void onResponse(Call<ArchiveRet> call, Response<ArchiveRet> response) {
                if (AppVc.isLoginOut(response)) return;
                ArchiveRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        List<ArchiveMode> data = body.getData();
                        if (data != null && data.size() > 0) {
                            modeList.addAll(data);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        ShowToast.alertShortOfWhite(getActivity(), body.msg);
                    }
                } else ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
                Sysout.out("==档案列表接口返回成功==");
            }

            public void onFailure(Call<ArchiveRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
            }
        });
    }
}
