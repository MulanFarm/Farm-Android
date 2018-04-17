package com.daweichang.vcfarm.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.ArchivesDetailsActivity;
import com.daweichang.vcfarm.mode.ArchiveMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/25.
 */
public class ArchivesAdapter extends BaseAdapter {
    public ArchivesAdapter(Context context, List<ArchiveMode> modeList) {
        this.context = context;
        this.modeList = modeList;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return modeList.size();
    }

    public Object getItem(int position) {
        return modeList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private Context context;
    private List<ArchiveMode> modeList;
    private LayoutInflater inflater;

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_archives_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.setData(modeList.get(position));
        return convertView;
    }

    public class ViewHolder implements View.OnClickListener {
        @BindView(R.id.textType)
        TextView textType;
        @BindView(R.id.textTitle)
        TextView textTitle;
        @BindView(R.id.textMsg)
        TextView textMsg;
        @BindView(R.id.textAge)
        TextView textAge;
        @BindView(R.id.textTime)
        TextView textTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        private ArchiveMode mode;

        public void setData(ArchiveMode mode) {
            this.mode = mode;
            Resources res = context.getResources();
//            textType.setText(mode.variety);//类型
            textTitle.setText(mode.getName());//类型
            String s = TextUtils.isEmpty(mode.variety) ? "未完善" : mode.variety;
            textMsg.setText(res.getString(R.string.pinzhong_) + s);
            textTime.setText(res.getString(R.string.chuangjianshijian_) + mode.getCreateDate());
            s = TextUtils.isEmpty(mode.age) ? "未完善" : mode.age + res.getString(R.string.sui);
            textAge.setText(s);
        }

        public void onClick(View v) {// 跳转
            ArchivesDetailsActivity.open(context, mode);
        }
    }
}
