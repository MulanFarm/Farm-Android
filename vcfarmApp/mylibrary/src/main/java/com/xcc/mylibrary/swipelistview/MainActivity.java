//
//<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent">
//<com.likebamboo.widget.SwipeListView
//        android:id="@+id/listview"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent" />
//</RelativeLayout>
//package com.xcc.mylibrary.swipelistview;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Toast;
//
//import com.xcc.mylibrary.R;
//
//
//public class MainActivity extends Activity {
//    /**
//     * 列表
//     */
//    private SwipeListView mListView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initView();
//    }
//
//    /**
//     * 初始化界面
//     */
//    private void initView() {
//        mListView = (SwipeListView)findViewById(R.id.listview);
//        SwipeAdapter adapter = new SwipeAdapter(this, mListView.getRightViewWidth(),
//                new SwipeAdapter.IOnItemRightClickListener() {
//                    @Override
//                    public void onRightClick(View v, int position) {
//                        // TODO Auto-generated method stub
//                        Toast.makeText(MainActivity.this, "right onclick " + position,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_footer, null));
//        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "item onclick " + position, Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//    }
//}
