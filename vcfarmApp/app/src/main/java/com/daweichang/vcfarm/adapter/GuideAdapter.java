package com.daweichang.vcfarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.GuideDetailsActivity;
import com.daweichang.vcfarm.mode.ArticleMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/31.
 */
public class GuideAdapter extends BaseAdapter {
    public GuideAdapter(Context context, List<ArticleMode> articleModeList) {
        this.context = context;
        this.articleModeList = articleModeList;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return articleModeList.size();
    }

    public Object getItem(int position) {
        return articleModeList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private Context context;
    private List<ArticleMode> articleModeList;
    private LayoutInflater inflater;

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_guide_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.setData(articleModeList.get(position));
        return convertView;
    }

    public class ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.textTitle)
        TextView textTitle;
        @BindView(R.id.textContent)
        TextView textContent;
        @BindView(R.id.textNumb)
        TextView textNumb;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        private ArticleMode mode;

        public void setData(ArticleMode mode) {
            this.mode = mode;
//            GlideUtils.displayOfUrl(context, image, mode.thumbnail);
            textTitle.setText(mode.title);
//            textContent.setText(mode.content);
//            textContent.setText(mode.content);
        }

        public void onClick(View v) {// 跳转至详情
            GuideDetailsActivity.open(context, mode);
            //WebViewActivity.open(context, "指南详情", mode.url);
        }
    }
}
