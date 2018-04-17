package com.daweichang.vcfarm.test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daweichang.vcfarm.ActivityManager;

import java.util.ArrayList;

/**
 *
 */
public class TestActivity extends ListActivity {
    private ArrayList<String> titles;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titles = new ArrayList<>();
        titles.add("=======测试页面=======");
        titles.add("关闭所有Activity");//1
        titles.add("档案详情");//2
        titles.add("摄像头全屏测试");//3
        titles.add("关闭其他Activity");//4

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles));
    }

    private static final int REQUEST_CODE_GALLERY = 200;

    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 1:
                ActivityManager.getManager().closeAll();
                onBackPressed();
                break;
            case 2:
                break;
            case 3:
                ActivityManager.getManager().closeAll();
                startActivity(new Intent(this, PlayerActivity.class));
                break;
            case 4:
                ActivityManager.getManager().closeAll();
                break;
        }
    }
}
