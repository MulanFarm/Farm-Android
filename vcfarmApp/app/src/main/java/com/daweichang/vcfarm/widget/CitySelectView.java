package com.daweichang.vcfarm.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.data.CityMode;
import com.daweichang.vcfarm.data.DataSqlUtils;
import com.xcc.mylibrary.widget.PickerScrollView;
import com.xcc.mylibrary.widget.Pickers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public class CitySelectView extends PopupWindow implements PickerScrollView.onSelectListener, View.OnClickListener {
    public CitySelectView(Activity context) {
        super(context);
        this.context = context;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(context);
        conentView = inflater.inflate(R.layout.city_select_layout, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 刷新状态
        this.update();

        iniData();
        setBackgroundDrawable(new BitmapDrawable());
        conentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) dismiss();
                return false;
            }
        });
    }


    private void iniData() {
        pickerView1 = (PickerScrollView) conentView.findViewById(R.id.pickerView1);
        pickerView2 = (PickerScrollView) conentView.findViewById(R.id.pickerView2);
        pickerView3 = (PickerScrollView) conentView.findViewById(R.id.pickerView3);
        textCancel = (TextView) conentView.findViewById(R.id.textCancel);
        textConfirm = (TextView) conentView.findViewById(R.id.textConfirm);
        textCancel.setOnClickListener(this);
        textConfirm.setOnClickListener(this);

        pickerView1.setOnSelectListener(this);
        pickerView2.setOnSelectListener(this);
        pickerView3.setOnSelectListener(this);

        dataSqlUtils = new DataSqlUtils(context);
        List citys = dataSqlUtils.getCitys();
        pickerView1.setData(citys);
    }

    private PickerScrollView pickerView1;
    private PickerScrollView pickerView2;
    private PickerScrollView pickerView3;
    private TextView textCancel;
    private TextView textConfirm;
    private DataSqlUtils dataSqlUtils;
    private Activity context;
    private View conentView;
    private CityMode cityMode1, cityMode2, cityMode3;
    private OnCitySelectListener onCitySelectListener;

    public void setOnCitySelectListener(OnCitySelectListener onCitySelectListener) {
        this.onCitySelectListener = onCitySelectListener;
    }

    //显示
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            showAtLocation(parent, Gravity.LEFT, 0, 0);
        } else {
            super.dismiss();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textConfirm:
                if (onCitySelectListener != null)
                    onCitySelectListener.selectCity(cityMode1, cityMode2, cityMode3);
                break;
        }
        dismiss();
    }

    @Override
    public void onSelect(PickerScrollView v, Pickers pickers) {
        CityMode cityMode = null;
        if (pickers != null)
            cityMode = (CityMode) pickers;
        switch (v.getId()) {
            case R.id.pickerView1: {
                cityMode1 = cityMode;
                if (cityMode == null) {
                    pickerView2.setData(new ArrayList<Pickers>());
                } else {
                    List citys = dataSqlUtils.getCitys(cityMode.area_code);
                    pickerView2.setData(citys);
                }
            }
            break;
            case R.id.pickerView2: {
                cityMode2 = cityMode;
                if (cityMode == null) {
                    pickerView3.setData(new ArrayList<Pickers>());
                } else {
                    List citys = dataSqlUtils.getCitys(cityMode.area_code);
                    pickerView3.setData(citys);
                }
            }
            break;
            case R.id.pickerView3: {
                cityMode3 = cityMode;
            }
            break;
        }
    }

    public interface OnCitySelectListener {
        void selectCity(CityMode cityMode1, CityMode cityMode2, CityMode cityMode3);
    }
}
