package com.daweichang.vcfarm.fragment;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseFragment;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.GuideDetailsActivity;
import com.daweichang.vcfarm.adapter.GuideAdapter;
import com.daweichang.vcfarm.mode.ArticleMode;
import com.daweichang.vcfarm.netret.ArticleListRet;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.utils.UserConfig;
import com.daweichang.vcfarm.widget.ShowToast;
import com.xcc.mylibrary.Sysout;
import com.xcc.mylibrary.widget.indicaor.FlycoPageIndicaor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/3/25.
 * 指南
 */
public class GuideFrgment extends BaseFragment {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.refreshLayout)
    BGARefreshLayout refreshLayout;
    private ViewHolder holder;
    private List<ArticleMode> articleModeList;
    private List<ArticleMode> imgList;
    private GuideAdapter adapter;

    public static GuideFrgment newInstance() {
        GuideFrgment fragment = new GuideFrgment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.head_guide_list, null);
        imgList = new ArrayList<>();
        holder = new ViewHolder(inflate);
        listView.addHeaderView(inflate);
        articleModeList = new ArrayList<>();
        adapter = new GuideAdapter(getActivity(), articleModeList);
        listView.setAdapter(adapter);

        articleList();
    }

    public class ViewHolder implements ViewPager.OnPageChangeListener {
        @BindView(R.id.viewPager)
        ViewPager viewPager;
        @BindView(R.id.textName)
        TextView textName;
        @BindView(R.id.indicator)
        FlycoPageIndicaor indicator;
        public ViewPagerAdapter adapter;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            adapter = new ViewPagerAdapter();
            viewPager.setAdapter(adapter);
            viewPager.setOnPageChangeListener(this);
            //indicator.setViewPager(viewPager);
        }

        public void notifyDataSetChanged() {
            indicator.setViewPager(viewPager, imgList.size());
            adapter.notifyDataSetChanged();
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            textName.setText(imgList.get(position).title);
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    public class ViewPagerAdapter extends PagerAdapter implements View.OnClickListener {
        private Map<Integer, ImageView> map = new HashMap<>();

        // 销毁arg1位置的界面
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (map.containsKey(position)) container.removeView(map.get(position));
        }

        // 获得当前界面数
        public int getCount() {
            return imgList.size();
        }

        // 初始化arg1位置的界面
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView;
            if (!map.containsKey(position)) {
                imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setId(position);
                map.put(position, imageView);
                imageView.setOnClickListener(this);
            } else imageView = map.get(position);
            GlideUtils.displayOfUrl(getActivity(), imageView, imgList.get(position).thumbnail, R.color.activityBg);
            container.addView(imageView);
            return imageView;
        }

        // 判断是否由对象生成界面
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        public void onClick(View v) {
            int id = v.getId();
            if (imgList.size() > id) {
                GuideDetailsActivity.open(getActivity(), imgList.get(id));
//                WebViewActivity.open(getActivity(), "指南详情", imgList.get(id).url);
            }
        }
    }

    //文章列表
    private void articleList() {
        openLoadDialog();
        Call<ArticleListRet> baseRetCall = BaseService.getInstance().getServiceUrl().articleList(UserConfig.getToken());
        baseRetCall.enqueue(new Callback<ArticleListRet>() {
            public void onResponse(Call<ArticleListRet> call, Response<ArticleListRet> response) {
                dismissDialog();
                if (AppVc.isLoginOut(response)) return;
                ArticleListRet body = response.body();
                if (body != null) {
                    if (body.isOk()) {
                        final String type = "text";
                        List<ArticleMode> data = body.data;
                        for (int i = 0; i < data.size(); i++) {
                            ArticleMode articleMode = data.get(i);
                            if (type.equals(articleMode.type)) articleModeList.add(articleMode);
                            else imgList.add(articleMode);
                        }
                        adapter.notifyDataSetChanged();
                        holder.notifyDataSetChanged();
                    } else ShowToast.alertShortOfWhite(getActivity(), body.msg);
                } else ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
                //Sysout.v("--articleList--", body.toString());
                Sysout.out("==文章列表接口返回成功==");
            }

            public void onFailure(Call<ArticleListRet> call, Throwable t) {
                dismissDialog();
                ShowToast.alertShortOfWhite(getActivity(), R.string.wangluoyichang);
            }
        });
    }
}
