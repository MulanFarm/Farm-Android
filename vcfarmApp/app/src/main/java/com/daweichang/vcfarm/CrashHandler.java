package com.daweichang.vcfarm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.daweichang.vcfarm.utils.FileOperateUtil;
import com.xcc.mylibrary.Sysout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 */
public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 程序的Context对象
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd=HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Date date = new Date();
        String msg = ex.toString() + "*";
        Sysout.log("==============错误信息==============", msg);

        StackTraceElement[] throwables = ex.getStackTrace();
        for (StackTraceElement throwa : throwables) {
            msg += throwa.toString() + "*";
            Sysout.log("==============错误信息==============", throwa.toString());
        }
        infos.clear();
        collectDeviceInfo(mContext);
        JSONObject jsonObject = new JSONObject();
        try {
            //TODO
//            BDLocation location = LocationUtils.getInstance().getLocation();
//            jsonObject.put("AddrStr", location.getAddrStr());
            jsonObject.put("BRAND", infos.get("BRAND"));
            jsonObject.put("versionCode", infos.get("versionCode"));
            jsonObject.put("CPU_ABI", infos.get("CPU_ABI"));
            jsonObject.put("MANUFACTURER", infos.get("MANUFACTURER"));
            jsonObject.put("versionName", infos.get("versionName"));
            jsonObject.put("MODEL", infos.get("MODEL"));
            jsonObject.put("TIME", formatter.format(date));
            jsonObject.put("errMsg", msg);
        } catch (JSONException e1) {
        }
        String msg1 = jsonObject.toString();
        Sysout.log("------------===错误信息===-------------", msg1);
        // 写入到手机
        String folderPath = FileOperateUtil.getFolderPath(FileOperateUtil.TYPE_ERR);
        long time = date.getTime() % 10000;
        SimpleDateFormat format = new SimpleDateFormat("yy年MM月dd日hh时mm分ss秒");
        String filename = folderPath + File.separator + format.format(date)
                + time + ".log";
        FileOperateUtil.textWrite(filename, msg1);
        // 临时储存
        // Preferences.addErr(jsonObject.toString());
        // TODO 发送错误信息
        // type
        // json
//		SimpleRequest request = new SimpleRequest(
//				"http://123.57.156.76:8080/CarPort/ErrorApp");
//		request.addParm("json", msg1);
//		request.addParm("type", "" + 7);// 1用户端，2技师端,7问卷调查
//		request.request(new SimpleRequestHandler() {
//			public void onSuccess(Object object) {
//			}
//			public void onStart() {
//			}
//			public void onFailure(String content) {
//			}
//		});

        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "抱歉，出现错误。请重启", Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }
        }.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        ActivityManager.getManager().closeAll();
        System.exit(0);
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            // Sysout.log(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Sysout.log(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                // Sysout.log(TAG, "an error occured when collect crash info",
                // e);
            }
        }
    }
}