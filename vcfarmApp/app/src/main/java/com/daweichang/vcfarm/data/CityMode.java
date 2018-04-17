package com.daweichang.vcfarm.data;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xcc.mylibrary.widget.Pickers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
@DatabaseTable(tableName = "tb_area_code")
public class CityMode implements Pickers {
    @DatabaseField(columnName = "area_code")
    public String area_code;//       VARCHAR( 32 )   NOT NULL,
    @DatabaseField(columnName = "area_name")
    public String area_name;//     VARCHAR( 64 )   NOT NULL
    @DatabaseField(columnName = "area_name_py")
    public String area_name_py;//   VARCHAR( 256 )  NOT NULL
    @DatabaseField(columnName = "area_name_short")
    public String area_name_short;//  VARCHAR( 32 )   NOT NULL
    @DatabaseField(columnName = "area_level")
    public int area_level;//     INT( 11 )       NOT NULL,
    @DatabaseField(columnName = "parent_area_code")
    public String parent_area_code;//  VARCHAR( 32 )   NOT NULL
    //    PRIMARY KEY(area_code)

    //>>一下Adapter使用
    public transient boolean isCheck;
    //自定义area_code时使用
    public static final String selectEnd = "selectEnd";

    public String getShowConetnt() {
        return area_name;
    }

    /**
     * 将citymode放进数组
     */
    public static CityMode[] addressToArray(CityMode cityMode1, CityMode cityMode2, CityMode cityMode3) {
        CityMode userAddrTmp[];
        if (cityMode3 != null) {
            userAddrTmp = new CityMode[3];
            userAddrTmp[2] = cityMode3;
            userAddrTmp[1] = cityMode2;
        } else if (cityMode2 != null) {
            userAddrTmp = new CityMode[2];
            userAddrTmp[1] = cityMode2;
        } else {
            userAddrTmp = new CityMode[1];
        }
        userAddrTmp[0] = cityMode1;
        return userAddrTmp;
    }

    /**
     * 获取地址字符串
     *
     * @return array[0] 显示文本，array[1] 请求服务器文本
     */
    public static String[] addressToString(CityMode[] cityModes) {
        String text = "";
        String addrUpString = "";
        for (CityMode cityMode : cityModes) {
            text += cityMode.area_name;
            addrUpString += cityMode.area_code + ":" + cityMode.area_name + ";";
        }
        if (!TextUtils.isEmpty(addrUpString))
            addrUpString = addrUpString.substring(0, addrUpString.length() - 1);
        return new String[]{text, addrUpString};
    }

    public static CityMode[] addressToMode(String userAddr) {
        CityMode cityModes[];
        if (userAddr == null) {
            cityModes = new CityMode[0];
        } else {
            String[] split = userAddr.split(";");//:;
            cityModes = new CityMode[split.length];
            for (int idx = 0; idx < split.length; idx++) {
                String sp = split[idx];
                String[] split1 = sp.split(":");
                if (split1.length <= 1) return new CityMode[0];
                CityMode cityMode = new CityMode();
                cityModes[idx] = cityMode;
                cityMode.area_code = split1[0];
                cityMode.area_name = split1[1];
            }
        }
        return cityModes;
    }

    /**
     * @param cityModeLists
     * @return array[0] area_codeList，array[1] area_nameList
     */
    public static String[] addressToString(List<CityMode[]> cityModeLists) {
        String area_codeList = "";
        String area_nameList = "";
        for (CityMode[] cityModes : cityModeLists) {
//            String area_codeListTmp = "";
//            String area_nameListTmp = "";
            for (CityMode cityMode : cityModes) {
                area_codeList += cityMode.area_code + ",";
                area_nameList += cityMode.area_name + ",";
            }
            area_codeList = area_codeList.substring(0, area_codeList.length() - 1) + ";";
            area_nameList = area_nameList.substring(0, area_nameList.length() - 1) + ";";
        }
        if (!TextUtils.isEmpty(area_codeList))
            area_codeList = area_codeList.substring(0, area_codeList.length() - 1);
        if (!TextUtils.isEmpty(area_nameList))
            area_nameList = area_nameList.substring(0, area_nameList.length() - 1);
        return new String[]{area_codeList, area_nameList};
    }

    public static List<CityMode[]> addressToArray(String area_codeList, String area_nameList) {
        List<CityMode[]> cityModeLists = new ArrayList<>();
        if (TextUtils.isEmpty(area_codeList) || TextUtils.isEmpty(area_nameList)) return null;
        String[] split = area_codeList.split(";");
        String[] split1 = area_nameList.split(";");
        if (split.length != split1.length) return null;
        CityMode[] cityModes;
        CityMode cityMode;
        String[] split2;
        String[] split3;
        for (int i = 0; i < split.length; i++) {
            split2 = split[i].split(",");
            split3 = split1[i].split(",");
            cityModes = new CityMode[split2.length];
            for (int j = 0; j < split2.length; j++) {
                cityMode = new CityMode();
                cityMode.area_code = split2[j];
                cityMode.area_name = split3[j];
                cityModes[j] = cityMode;
            }
            cityModeLists.add(cityModes);
        }
        return cityModeLists;
    }

    /**
     * 对比列表获取位置，列表1包含列表2
     */
    public static int[] getIndexOfContrast(List<CityMode[]> cityModeLine, List<CityMode[]> cityModes) {
        //只取开头和结尾对比
        int start = 0, end = 1;
        if (cityModes.size() >= 2) {
            CityMode[] cityModes1 = cityModes.get(0);
            CityMode[] cityModes2;
            For1:
            for (int i = 0; i < cityModeLine.size(); i++) {
                cityModes2 = cityModeLine.get(i);
                if (cityModes1.length == cityModes2.length) {
                    for (int j = 0; j < cityModes2.length; j++) {
                        if (!cityModes1[j].area_code.equals(cityModes2[j].area_code))
                            continue For1;
                    }
                    start = i;
                    break;
                }
            }
            int i = cityModes.size() + start - 1;
            if (i < cityModeLine.size()) {
                cityModes2 = cityModeLine.get(i);
                cityModes1 = cityModes.get(cityModes.size() - 1);
                if (cityModes1.length == cityModes2.length) {
                    int len = 0;
                    for (int j = 0; j < cityModes2.length; j++) {
                        if (cityModes1[j].area_code.equals(cityModes2[j].area_code))
                            len++;
                    }
                    if (len == cityModes2.length) end = i;
                }
            }
            if (end == 0) end = cityModeLine.size() - 1;
            if (start >= end) start--;
        }
        return new int[]{start, end};
    }
}