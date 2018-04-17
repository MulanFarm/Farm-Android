package com.daweichang.vcfarm;

import android.app.Activity;

import com.xcc.mylibrary.Sysout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/6.
 */
public class ActivityManager {
    private ActivityManager() {
        activityList = new ArrayList<>();
    }

    private static ActivityManager manager;
    private List<Activity> activityList;

    public static ActivityManager getManager() {
        if (manager == null) manager = new ActivityManager();
        return manager;
    }

    public Activity getTopActivity() {
        if (activityList.size() != 0) return activityList.get(activityList.size() - 1);
        else return null;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
        Sysout.v("--打开--", activity.getClass().getName());
    }

    public void closeActivity(Activity activity) {
        activityList.remove(activity);
        Sysout.v("--关闭--", activity.getClass().getName());
    }

    public void closeAll() {
        List<Activity> list = new ArrayList<>();
        list.addAll(activityList);
        for (Activity activity : list) activity.finish();
    }

    public void closeOther(Activity act) {
        List<Activity> list = new ArrayList<>();
        list.addAll(activityList);
        list.remove(act);
        for (Activity activity : list) activity.finish();
    }
}
