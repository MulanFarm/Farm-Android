package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.adapter.MessageAdapter;
import com.daweichang.vcfarm.mode.MessageMode;
import com.daweichang.vcfarm.netret.MessageRet;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.Sysout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Administrator on 2017/3/28.
 * 信息列表
 */
public class MessageListActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    private ArrayList<MessageMode> modeList;
    private MessageAdapter adapter;

    public static void open(Context context) {
        Intent intent = new Intent(context, MessageListActivity.class);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);

        modeList = new ArrayList<>();
        adapter = new MessageAdapter(this, modeList);
        listView.setAdapter(adapter);

        //TODO 待完善
        //initRefreshLayout();
        msgList();
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

    //消息列表
    private void msgList() {
        Call<MessageRet> baseRetCall = BaseService.getInstance().getServiceUrl().msgList(UserConfig.getToken());
        baseRetCall.enqueue(new Callback<MessageRet>() {
            public void onResponse(Call<MessageRet> call, Response<MessageRet> response) {
                if (AppVc.isLoginOut(response)) return;
                MessageRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        if (body.data != null && body.data.size() > 0) {
                            ArrayList<MessageMode> modeArrayList = body.data;
                            modeList.addAll(modeArrayList);
                            adapter.notifyDataSetChanged();
                            int size = modeArrayList.size();
                            UserConfig.setMaxMsgId(size);
                        }
                    } else {
                        ShowToast.alertShortOfWhite(MessageListActivity.this, body.msg);
                    }
                } else
                    ShowToast.alertShortOfWhite(MessageListActivity.this, R.string.wangluoyichang);
                Sysout.out("==消息列表接口返回成功==");
            }

            public void onFailure(Call<MessageRet> call, Throwable t) {
                ShowToast.alertShortOfWhite(MessageListActivity.this, R.string.wangluoyichang);
            }
        });
    }
}
