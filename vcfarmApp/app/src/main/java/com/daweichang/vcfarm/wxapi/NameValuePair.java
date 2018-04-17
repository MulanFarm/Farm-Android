package com.daweichang.vcfarm.wxapi;

/**
 * Created by Administrator on 2017/3/24.
 */

public class NameValuePair {
    private String name;
    private String value;

    public NameValuePair() {
    }

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
