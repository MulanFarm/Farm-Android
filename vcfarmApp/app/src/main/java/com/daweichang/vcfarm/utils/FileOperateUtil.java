package com.daweichang.vcfarm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName: FileOperateUtil
 * @Description: 文件操作工具类
 */
public class FileOperateUtil {
    public final static String TAG = "FileOperateUtil";

    public final static int ROOT = 0;// 根目录
    public final static int TYPE_IMAGE = 1;// 图片
    public final static int TYPE_THUMBNAIL = 2;// 缩略图
    public final static int TYPE_VIDEO = 3;// 视频
    public final static int TYPE_TEXT = 4;// 文本
    public final static int TYPE_ERR = 5;// 错误日志
    public final static int TYPE_Cache = 6;// 缓存
    public final static int TYPE_NetRes = 7;// 网络资源
    public final static int TYPE_Sound = 8;// 声音
    public final static int TYPE_Tmp = 9;// 临时文件

    /**
     * 向文本文件写入数据<br>
     * 文件不存在新建
     *
     * @return 保存成功或失败
     */
    public static boolean textWrite(String fileName, String msg) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(msg.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getSDCard() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取文件夹路径
     *
     * @param type 文件夹类别
     */
    public static String getFolderPath(int type) {
        // 本业务文件主目录
        StringBuilder pathBuilder = new StringBuilder();
        // 添加应用存储路径
        pathBuilder.append(getSDCard());
        pathBuilder.append(File.separator);
        // 添加文件总目录
        pathBuilder.append("Vcfarm");
        pathBuilder.append(File.separator);
        // 添加当然文件类别的路径
        switch (type) {
            case TYPE_IMAGE:
                pathBuilder.append("Image");
                break;
            case TYPE_VIDEO:
                pathBuilder.append("Video");
                break;
            case TYPE_TEXT:
                pathBuilder.append("Text");
                break;
            case TYPE_ERR:
                pathBuilder.append("Err");
                break;
            case TYPE_Cache:
                pathBuilder.append("Cache");
                break;
            case TYPE_NetRes:
                pathBuilder.append("NetRes");
                break;
            case TYPE_Sound:
                pathBuilder.append("Sound");
                break;
            case TYPE_THUMBNAIL:
                pathBuilder.append("Thumbnail");
                break;
            case TYPE_Tmp:
                pathBuilder.append("Temp");
                break;
            default:
                break;
        }
        String folderPath = pathBuilder.toString();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath;
    }

    /**
     * 默认删除569下的文件
     */
    public static void deleteDirFile() {
        deleteDirFile(5);
        deleteDirFile(6);
        deleteDirFile(9);
    }

    /**
     * 删除文件夹下的所有文件
     */
    public static void deleteDirFile(int type) {
        String folderPath = getFolderPath(type);
        File file = new File(folderPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files)
                file1.delete();
        }
    }

    public static long getDataSize() {
        long total = getDataSize(5);
        total += getDataSize(6);
        total += getDataSize(9);
        return total;
    }

    public static long getDataSize(int type) {
        long total = 0;
        String folderPath = getFolderPath(type);
        File file = new File(folderPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files)
                total += file1.length();
            //file1.delete();
        }
        return total;
    }

    /**
     * 通过图片文件名获取完整文件路径
     */
    public static String getPathOfName(String imgName) {
        String filesD = FileOperateUtil.getFolderPath(FileOperateUtil.TYPE_IMAGE);
        String path = filesD + File.separator + imgName;
        return path;
    }

    /**
     * 通过文件名获取bitmap <br>
     * 该文件必须是图片文件
     */
    public static Bitmap getBitmapOfFileName(String imgName) {
        Bitmap bitmap = BitmapFactory.decodeFile(getPathOfName(imgName));
        return bitmap;
    }

    /**
     * 通过文件绝对路径获取bitmap <br>
     * 该文件必须是图片文件
     */
    public static Bitmap getBitmapOfFilePath(String imgPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        return bitmap;
    }

    /**
     * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
     *
     * @param file    目标文件夹路径
     * @param format  指定后缀名
     * @param content 包含的内容,用以查找视频缩略图
     * @return
     */
    public static List<File> listFiles(String file, String format,
                                       String content) {
        return listFiles(new File(file), format, content);
    }

    public static List<File> listFiles(String file, final String format) {
        return listFiles(new File(file), format, null);
    }

    /**
     * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
     *
     * @param file      目标文件夹
     * @param extension 指定后缀名
     * @param content   包含的内容,用以查找视频缩略图
     * @return
     */
    public static List<File> listFiles(File file, final String extension,
                                       final String content) {
        File[] files = null;
        if (file == null || !file.exists() || !file.isDirectory())
            return null;
        files = file.listFiles(new FilenameFilter() {
            public boolean accept(File arg0, String arg1) {
                if (content == null || content.equals(""))
                    return arg1.endsWith(extension);
                else {
                    return arg1.contains(content) && arg1.endsWith(extension);
                }
            }
        });
        if (files != null) {
            List<File> list = new ArrayList<File>(Arrays.asList(files));
            sortList(list, false);
            return list;
        }
        return null;
    }

    /**
     * 根据修改时间为文件列表排序
     *
     * @param list 排序的文件列表
     * @param asc  是否升序排序 true为升序 false为降序
     */
    public static void sortList(List<File> list, final boolean asc) {
        // 按修改日期排序
        Collections.sort(list, new Comparator<File>() {
            public int compare(File file, File newFile) {
                if (file.lastModified() > newFile.lastModified()) {
                    if (asc) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    if (asc) {
                        return -1;
                    } else {
                        return 1;
                    }
                }

            }
        });
    }

    /**
     * @param extension 后缀名 如".jpg"
     * @return
     */
    public static String createFileNmae(String extension) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault());
        // 转换为字符串
        String formatDate = format.format(new Date());
        // 查看是否带"."
        if (!extension.startsWith("."))
            extension = "." + extension;
        return formatDate + extension;
    }

    /**
     * 删除缩略图 同时删除源图或源视频
     *
     * @param thumbPath 缩略图路径
     * @return
     */
    public static boolean deleteThumbFile(String thumbPath) {
        boolean flag = false;

        File file = new File(thumbPath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }

        flag = file.delete();
        // 源文件路径
        String sourcePath = thumbPath.replace("Thumbnail", "Image");
        file = new File(sourcePath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }
        flag = file.delete();
        return flag;
    }

    /**
     * 删除源图或源视频 同时删除缩略图
     *
     * @param sourcePath 缩略图路径
     * @return
     */
    public static boolean deleteSourceFile(String sourcePath) {
        boolean flag = false;

        File file = new File(sourcePath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }

        flag = file.delete();
        // 缩略图文件路径
        String thumbPath = sourcePath.replace("Image", "Thumbnail");
        file = new File(thumbPath);
        if (!file.exists()) { // 文件不存在直接返回
            return flag;
        }
        flag = file.delete();
        return flag;
    }

    /**
     * 打开内部储存位置
     */
    public static String getPrivatePath(Context context) {
        return getPrivateDir(context).getAbsolutePath();
    }

    public static File getPrivateDir(Context context) {
//        FileOutputStream fileOutputStream = context.openFileOutput(fileName, 0);
        return context.getFilesDir();

    }
}