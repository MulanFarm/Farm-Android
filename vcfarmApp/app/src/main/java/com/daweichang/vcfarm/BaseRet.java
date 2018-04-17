package com.daweichang.vcfarm;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/3/3.
 */

public class BaseRet<T> {
    public String msg;//诸如：邮箱格式错误、用户已注册等错误信息,可直接抛给用户
    public boolean result;//false时需要取msg里面的错误信息
    public T data;//当result为true时可取data里面的数据

    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean isOk() {
        // return status == 0;
        return result;
    }

    public T getData() {
        return data;
    }
}
