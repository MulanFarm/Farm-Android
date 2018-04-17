package com.daweichang.vcfarm.mode;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ArchiveMode {
    /**
     * id : 1234567890
     * variety : 猫科0
     * name : 喵大人0
     * age : 0
     * weight : 0.6
     * height : 1.2
     * address : 浦东新区0
     * hobby : 睡觉0
     * hate : 抓老鼠0
     * adop_time : 2017-03-13 21:38:56
     * create_date : 2017-03-13 21:38:56
     */
    public String id;
    public String variety;
    private String name;
    public String age;
    public double weight;
    public double height;
    public String address;
    public String hobby;
    public String hate;
    public String adop_time;
    public String create_date;
    public List<AlbumsMode> albums;

    private transient String createDate;
    private transient String adopTime;

    public String getName() {
        if (TextUtils.isEmpty(name)) name = "未完善";
        return name;
    }

    public String getCreateDate() {
        if (TextUtils.isEmpty(createDate) && !TextUtils.isEmpty(create_date)) {
            createDate = create_date.split(" ")[0];
        }
        return createDate;
    }

    public String getAdopTime() {
        if (TextUtils.isEmpty(adopTime) && !TextUtils.isEmpty(adop_time)) {
            adopTime = adop_time.split(" ")[0];
        }
        return adopTime;
    }
}
