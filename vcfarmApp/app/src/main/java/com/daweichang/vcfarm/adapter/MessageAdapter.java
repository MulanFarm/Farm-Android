package com.daweichang.vcfarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.activity.WebViewActivity;
import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.mode.MessageMode;
import com.daweichang.vcfarm.utils.GlideUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/2.
 */
public class MessageAdapter extends BaseAdapter {
    public MessageAdapter(Context context, List<MessageMode> modeList) {
        this.context = context;
        this.modeList = modeList;
        inflater = LayoutInflater.from(context);
        loginMode = LoginMode.getMode(context);
    }

    public int getCount() {
        return modeList.size();
    }

    public Object getItem(int i) {
        return modeList.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    private Context context;
    private List<MessageMode> modeList;
    private LayoutInflater inflater;
    private LoginMode loginMode;

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_msg_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else holder = (ViewHolder) view.getTag();
        holder.setData(modeList.get(i));
        return view;
    }

    public class ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.textTitle)
        TextView textTitle;
        @BindView(R.id.textContent)
        TextView textContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        private MessageMode mode;

        public void setData(MessageMode mode) {
            this.mode = mode;
            GlideUtils.displayOfUrl(context, image, loginMode.avatar);
            textTitle.setText(mode.title);
            textContent.setText(mode.content);
        }

        public void onClick(View view) {
            // 消息详情-暂无效果图
            WebViewActivity.open(context, mode.title, mode.content, true);
        }
    }
}
