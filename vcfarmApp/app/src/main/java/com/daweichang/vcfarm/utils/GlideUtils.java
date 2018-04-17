package com.daweichang.vcfarm.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daweichang.vcfarm.AppVc;
import com.daweichang.vcfarm.R;
import com.xcc.mylibrary.Sysout;
import com.xcc.mylibrary.TextUtils;

import java.io.File;

public class GlideUtils {
    public static void displayOfUrl(Context context, ImageView imgView, String url) {
        Sysout.v("---显示图像地址---", url);
        if (TextUtils.isEmpty(url)) return;
//        if (!url.contains("://"))
//            url = BaseUrl.ImageShow + url;

        Glide.with(context).
                load(url).
                asBitmap(). //强制处理为bitmap
//                placeholder(R.drawable.bg_loading).//加载中显示的图片
//                error(R.drawable.bg_error).//加载失败时显示的图片
//                crossFade().//淡入显示,注意:如果设置了这个,则必须要去掉asBitmap
//                crossFade(1000).//淡入显示的时间,注意:如果设置了这个,则必须要去掉asBitmap
//                override(80,80).//设置最终显示的图片像素为80*80,注意:这个是像素,而不是控件的宽高
//                centerCrop().//中心裁剪,缩放填充至整个ImageView
//                skipMemoryCache(true).//跳过内存缓存
//                thumbnail(0.1f).//10%的原图大小
        error(R.mipmap.ic_launcher).
        diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
                into(imgView);//显示到目标View中
    }

    public static void displayOfFile(Context context, ImageView imgView, File file) {
        if (file.exists()) {
            Glide.with(context).
                    load(file).
                    asBitmap().
                    error(R.mipmap.ic_launcher).
                    into(imgView);
        }
    }

    public static void displayOfUrl(Context context, ImageView imgView, String url, int errId) {
        Sysout.v("---显示图像地址---", url);
        if (TextUtils.isEmpty(url)) {
            imgView.setImageResource(errId);
            return;
        }
//        if (!url.contains("://"))
//            url = BaseUrl.ImageShow + url;
        Glide.with(context).
                load(url).
                asBitmap().
                error(errId).//加载失败时显示的图片
                diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
                into(imgView);//显示到目标View中
    }

    //gif慎用
//    public static void displayOfGif(Context context, ImageView imgView, File file) {
//        if (file.exists()) {
//            Glide.with(context).
//                    load(file).
//                    asGif().
//                    into(imgView);
//        }
//    }

    public static void clearMemory() {
        AppVc appVc = AppVc.getAppVc();
        clearMemory(appVc);
    }

    public static void clearView(Context context, ImageView view) {
        Glide.clear(view);
        clearMemory(context);
    }

    public static void clearMemory(Context context) {
        Glide glide = Glide.get(context);
        //Glide.get(context).clearMemory();
        //Glide.get(context).clearDiskCache();
        glide.clearMemory();
        new ClearDiskCacheThread(Glide.get(context)).start();
    }

    public static class ClearDiskCacheThread extends Thread {
        public ClearDiskCacheThread(Glide glide) {
            this.glide = glide;
        }

        private Glide glide;

        public void run() {
            glide.clearDiskCache();
        }
    }
}
