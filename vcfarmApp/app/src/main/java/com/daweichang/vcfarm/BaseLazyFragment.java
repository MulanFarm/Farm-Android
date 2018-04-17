package com.daweichang.vcfarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcc.mylibrary.Sysout;

public abstract class BaseLazyFragment extends BaseFragment {
    private View view;
    protected boolean isInit;
    protected boolean isVisibleToUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isInit = false;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        if (!isInit && isVisibleToUser) {
            isInit = true;
            initVIew(view);
            initVIewData();
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Sysout.d("------" + this.getClass().getName() + "------", "BaseLazyFragment-setUserVisibleHint-isVisibleToUser:" + isVisibleToUser);
            this.isVisibleToUser = isVisibleToUser;
            if (!isInit && view != null) {
                isInit = true;
                initVIew(view);
                initVIewData();
            }
            if (isInit)
                viewVisible();
        }
    }

    public abstract void initVIew(View view);

    public abstract void initVIewData();

    public void viewVisible() {
    }
}
