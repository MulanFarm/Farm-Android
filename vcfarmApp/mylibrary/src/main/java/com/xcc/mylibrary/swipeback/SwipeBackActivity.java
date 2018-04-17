package com.xcc.mylibrary.swipeback;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SwipeBackActivity extends AppCompatActivity {

    private SwipeBackLayout mSwipeBackLayout;

    private boolean mOverrideExitAniamtion = true;

    private boolean mIsFinishing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSwipeBackLayout.setEdgeSize(200);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this);
    }

    /**
     * #SwipeBackLayout.EDGE_LEFT
     * #SwipeBackLayout.EDGE_RIGHT
     * #SwipeBackLayout.EDGE_BOTTOM
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlags);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mSwipeBackLayout.findViewById(id);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    /**
     * Override Exit Animation
     *
     * @param override
     */
    public void setOverrideExitAniamtion(boolean override) {
        mOverrideExitAniamtion = override;
    }

    /**
     * Scroll out contentView and finish the activity
     */
    public void scrollToFinishActivity() {
        mSwipeBackLayout.scrollToFinishActivity();
    }

    public void finish() {
        if (mOverrideExitAniamtion && !mIsFinishing) {
            scrollToFinishActivity();
            mIsFinishing = true;
            return;
        }
        mIsFinishing = false;
        super.finish();
    }
}
