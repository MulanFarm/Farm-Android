package com.daweichang.vcfarm.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.NotsDetailsActivity;
import com.daweichang.vcfarm.mode.NoteMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/29.
 */

public class NotsListAdapter extends BaseAdapter {
    public NotsListAdapter(Context context, List<NoteMode> noteModeList) {
        this.context = context;
        this.noteModeList = noteModeList;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return noteModeList.size();
    }

    public Object getItem(int position) {
        return noteModeList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private Context context;
    private LayoutInflater inflater;
    private List<NoteMode> noteModeList;

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_nost_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.setData(noteModeList.get(position));
        return convertView;
    }

    public class ViewHolder implements View.OnClickListener {
        @BindView(R.id.textTitle)
        TextView textTitle;
        @BindView(R.id.textContent)
        TextView textContent;
        @BindView(R.id.textTime)
        TextView textTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        private NoteMode noteMode;

        public void setData(NoteMode noteMode) {
            this.noteMode = noteMode;
            if (!TextUtils.isEmpty(noteMode.title))
                textTitle.setText(noteMode.title);
            textContent.setText(noteMode.content);
            textTime.setText(noteMode.create_date);
        }

        public void onClick(View v) {
            NotsDetailsActivity.open(context, noteMode);
        }
    }
}
