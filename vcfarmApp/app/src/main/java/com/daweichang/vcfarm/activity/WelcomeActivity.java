package com.daweichang.vcfarm.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.daweichang.vcfarm.ActivityManager;
import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.utils.UserConfig;
import com.xcc.mylibrary.widget.indicaor.FlycoPageIndicaor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xcc on 2017/2/23.
 * 欢迎界面
 */
public class WelcomeActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.indicator)
    FlycoPageIndicaor indicator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        setSwipeBackEnable(false);

        viewPager.setAdapter(new ViewPagerAdapter());
        indicator.setViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                btnLogin.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });

        if (UserConfig.isWelcome()) {
            viewPager.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnLogin)
    public void onClick() {
        UserConfig.setWelcome(true);
        AppVc appVc = AppVc.getAppVc();
        if (!appVc.isLogin()) {
            LoginActivity.open(this);
            finish();
        } else MainActivity.openTop(WelcomeActivity.this);
        //onBackPressed();
    }

    public void onBackPressed() {
        ActivityManager manager = ActivityManager.getManager();
        manager.closeOther(this);
        manager.closeAll();
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private ImageView imageViews[] = new ImageView[3];
        private int retInt[] = new int[]{R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3};

        // 销毁arg1位置的界面
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (imageViews[position] != null) container.removeView(imageViews[position]);
        }

        // 获得当前界面数
        public int getCount() {
            return 3;
        }

        // 初始化arg1位置的界面
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView;
            if (imageViews[position] == null) {
                imageView = new ImageView(WelcomeActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setId(position);
                imageViews[position] = imageView;
            } else imageView = imageViews[position];
            imageView.setImageResource(retInt[position]);
            container.addView(imageView);
            return imageView;
        }

        // 判断是否由对象生成界面
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }
    }
}
