package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.NoteMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/30.
 * 笔记详情
 */
public class NotsDetailsActivity extends BaseActivity {
    @BindView(R.id.textName)
    TextView textName;
    @BindView(R.id.textContent)
    TextView textContent;
    private NoteMode noteMode;

    public static void open(Context context, NoteMode noteMode) {
        Intent intent = new Intent(context, NotsDetailsActivity.class);
        intent.putExtra("mode", noteMode);
        context.startActivity(intent);
    }

    protected int finishBtn() {
        return R.id.imgBack;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nots_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        noteMode = (NoteMode) intent.getSerializableExtra("mode");

        textName.setText(noteMode.title);
        textContent.setText(noteMode.content);
    }
}
