package com.daweichang.vcfarm.utils;

import android.content.Context;

import com.daweichang.vcfarm.R;

import java.io.File;
import java.util.ArrayList;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;

public class ImgSelectConfig {
    /**
     * 自由裁剪
     */
    public static CoreConfig init(Context context) {
        ThemeConfig theme = getThemeConfig(context).build();
        //配置功能
        FunctionConfig functionConfig = getFunctionConfig(context).build();
        CoreConfig coreConfig = getCoreConfig(context, theme, functionConfig).build();
        GalleryFinal.init(coreConfig);
        return coreConfig;
    }

    /**
     * @param context
     * @param cropSquare 是否正方形的
     * @param width      <=0属于放弃设置
     * @param height     <=0属于放弃设置
     */
    public static FunctionConfig getSquareConfig(Context context, boolean cropSquare, int width, int height) {
        ThemeConfig theme = getThemeConfig(context).build();
        FunctionConfig.Builder builder = getFunctionConfig(context);
        builder.setCropSquare(cropSquare);
        if (width > 0 && height > 0) builder.setCropWidth(width).setCropHeight(height);
        FunctionConfig functionConfig = builder.build();
        return functionConfig;
    }

    private static ThemeConfig.Builder getThemeConfig(Context context) {
        //设置主题
        ThemeConfig.Builder theme = new ThemeConfig.Builder()
//                .setTitleBarTextColor(context.getResources().getColor(R.color.activityStageBg))//标题栏文本字体颜色
                .setTitleBarBgColor(context.getResources().getColor(R.color.colorAccent))//标题栏背景颜色
//                .setTitleBarIconColor(context.getResources().getColor(R.color.activityStageBg))//标题栏icon颜色，如果设置了标题栏icon，设置setTitleBarIconColor将无效
//                .setCheckNornalColor(context.getResources().getColor(R.color.edit_text_color))//选择框未选颜色
                .setCheckSelectedColor(context.getResources().getColor(R.color.colorAccent))//选择框选中颜色
                .setCropControlColor(context.getResources().getColor(R.color.colorAccent))//设置裁剪控制点和裁剪框颜色
//                .setFabNornalColor(context.getResources().getColor(R.color.btn_usable))//设置Floating按钮Nornal状态颜色
//                .setFabPressedColor(context.getResources().getColor(R.color.btn_disable))//设置Floating按钮Pressed状态颜色
//                .setIconBack(R.mipmap.img_back)//设置返回按钮icon
                //setIconCamera//设置相机icon
                //setIconCrop//设置裁剪icon
                //setIconRotate//设置旋转icon
                //setIconClear//设置清楚选择按钮icon（标题栏清除选择按钮）
                //setIconFolderArrow//设置标题栏文件夹下拉arrow图标
                //setIconDelete//设置多选编辑页删除按钮icon
                //setIconCheck//设置checkbox和文件夹已选icon
                //setIconFab//设置Floating按钮icon
                //setEditPhotoBgTexture//设置图片编辑页面图片margin外背景
                //setIconPreview设置预览按钮icon
                //setPreviewBg设置预览页背景
                // .build();
                ;
        return theme;
    }

    private static FunctionConfig.Builder getFunctionConfig(Context context) {
        ArrayList<String> list = new ArrayList<>();
        list.add(".webp");
        list.add(".ico");
        FunctionConfig.Builder functionConfig = new FunctionConfig.Builder()
                //.setMutiSelect(t)//配置是否多选
                .setMutiSelectMaxSize(10)//配置多选数量
                .setEnableEdit(true)//开启编辑功能
                .setEnableCrop(true)//开启裁剪功能
                .setEnableRotate(true)//开启旋转功能
                .setEnableCamera(true)//开启相机功能
                //.setFilter(list)
                //setCropWidth(int width)//裁剪宽度
                //setCropHeight(int height)//裁剪高度
                //setCropSquare(boolean)//裁剪正方形
                //setSelected(List)//添加已选列表,只是在列表中默认呗选中不会过滤图片
                //.setFilter(List list)//添加图片过滤，也就是不在GalleryFinal中显示
                //.takePhotoFolter(new File(FileOperateUtil.getFolderPath(1)))//配置拍照保存目录，不做配置的话默认是/sdcard/DCIM/GalleryFinal/
                //.setRotateReplaceSource(boolean)//配置选择图片时是否替换原始图片，默认不替换
                //setCropReplaceSource(boolean)//配置裁剪图片时是否替换原始图片，默认不替换

                .setForceCrop(true)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
                .setForceCropEdit(true)//在开启强制裁剪功能时是否可以对图片进行编辑（也就是是否显示旋转图标和拍照图标）
                .setEnablePreview(true)//是否开启预览功能
//                .build();
                ;
        return functionConfig;
    }

    private static CoreConfig.Builder getCoreConfig(Context context, ThemeConfig theme, FunctionConfig functionConfig) {
        //配置imageloader
        ImageLoader imageloader = new FinalImageLoader();
        CoreConfig.Builder coreConfig = new CoreConfig.Builder(context, imageloader, theme)
                //.setDebug(BuildConfig.DEBUG)
                .setFunctionConfig(functionConfig)//配置全局GalleryFinal功能
                .setEditPhotoCacheFolder(new File(FileOperateUtil.getFolderPath(9)))//配置编辑（裁剪和旋转）功能产生的cache文件保存目录，不做配置的话默认保存在/sdcard/GalleryFinal/edittemp/
                .setNoAnimcation(true)//关闭动画
                .setTakePhotoFolder(new File(FileOperateUtil.getFolderPath(1)))//设置拍照保存目录，默认是/sdcard/DICM/GalleryFinal/
                //.setPauseOnScrollListener()//设置imageloader滑动加载图片优化OnScrollListener,根据选择的ImageLoader来选择PauseOnScrollListener
                //.build();
                ;
        return coreConfig;
    }

    /**
     * readMe
     */
//    单选打开相册
//    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY,mOnHanlderResultCallback);
//带配置
//    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY,functionConfig,mOnHanlderResultCallback);
//    多选打开相册
//    GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY,mOnHanlderResultCallback);
//带配置
//    FunctionConfig config = new FunctionConfig.Builder(MainActivity.this)
//            .setMutiSelectMaxSize(8)
//            .build();
//    GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY,functionConfig,mOnHanlderResultCallback);
//    使用拍照
//    GalleryFinal.openCamera(REQUEST_CODE_CAMERA,mOnHanlderResultCallback);
//带配置
//    GalleryFinal.openCamera(REQUEST_CODE_CAMERA,functionConfig,mOnHanlderResultCallback);
//    使用裁剪
//    GalleryFinal.openCrop(REQUEST_CODE_CROP,mOnHanlderResultCallback);
//带配置
//    GalleryFinal.openCrop(REQUEST_CODE_CROP,functionConfig,mOnHanlderResultCallback);
//    使用图片编辑
//    GalleryFinal.openEdit(REQUEST_CODE_EDIT,mOnHanlderResultCallback);
//带配置
//    GalleryFinal.openEdit(REQUEST_CODE_EDIT,functionConfig,mOnHanlderResultCallback);
}
