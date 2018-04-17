package com.daweichang.vcfarm.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.CameraMode;
import com.daweichang.vcfarm.mode.LoginMode;
import com.daweichang.vcfarm.utils.GlideUtils;
import com.daweichang.vcfarm.widget.ShowToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/2.
 */
public class CameraAdapter extends BaseAdapter {
    public CameraAdapter(Context context, ArrayList<CameraMode> modeList) {
        this.context = context;
        this.modeList = modeList;
        inflater = LayoutInflater.from(context);
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
    private ArrayList<CameraMode> modeList;
    private LayoutInflater inflater;
    private OnDelectListener onDelectListener;

    public void setOnDelectListener(OnDelectListener onDelectListener) {
        this.onDelectListener = onDelectListener;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_msg_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else holder = (ViewHolder) view.getTag();
        CameraMode mode = modeList.get(i);
        holder.setData(mode);
        return view;
    }

    public class ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.textTitle)
        TextView textTitle;
        @BindView(R.id.textContent)
        TextView textContent;
        @BindView(R.id.itemRightTxt)
        TextView itemRightTxt;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        private CameraMode mode;
        private String imgUrl;

        public void setData(CameraMode mode) {
            this.mode = mode;
            if (!TextUtils.isEmpty(mode.thumbnail) && !mode.thumbnail.equals(imgUrl)) {
                GlideUtils.displayOfUrl(context, image, mode.thumbnail);
                imgUrl = mode.thumbnail;
            }
            textTitle.setText(mode.camera_no);
            textContent.setText(mode.name);
        }

        @OnClick({R.id.itemRightTxt})
        public void onClick(View view) {
            LoginMode mode = LoginMode.getMode(context);
            if (!mode.id.equals(this.mode.user_id)) {
                ShowToast.alertShortOfWhite(context, R.string.nwqczgsxt);
                return;
            }
            // 消息详情-暂无效果图
            switch (view.getId()) {
                case R.id.itemRightTxt:
                    if (onDelectListener != null) onDelectListener.onDelect(this.mode);
                    break;
                default:
                    if (onDelectListener != null) onDelectListener.onSelect(this.mode);
                    break;
            }
        }
    }

    public interface OnDelectListener {
        public void onDelect(CameraMode mode);

        public void onSelect(CameraMode mode);
    }
}
