package com.daweichang.vcfarm.data;

/**
 * Created by Administrator on 2016/11/24.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.daweichang.vcfarm.utils.FileOperateUtil;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 管理附带数据库，处理固定数据，主要用于查找数据
 */
public class DataSqlUtils {
    public static final String SqlDataName = "qhtrecord.db";
    private String dataPath;

    public DataSqlUtils(Context context) {
        this.context = context;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        dataPath = FileOperateUtil.getPrivatePath(context) + File.separator + SqlDataName;
        File file = new File(dataPath);
        if (!file.exists()) {
            InputStream resourceAsStream = context.getClass().getClassLoader().getResourceAsStream("assets/" + SqlDataName);
            FileOutputStream fileOutputStream = context.openFileOutput(SqlDataName, 0);
            int idx = 0;
            byte[] buffer = new byte[1024];
            while ((idx = resourceAsStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, idx);
            }
            resourceAsStream.close();
            fileOutputStream.close();
        }
//        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(s, 0, null);
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from tb_area_code where parent_area_code='110000'", null);
//        if(cursor.moveToNext()){}
//        sqLiteDatabase.close();
    }

    private Context context;

    /**
     * 获取省级城市
     */
    public List<CityMode> getCitys() {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(dataPath, 0, null);
        try {
            AndroidConnectionSource connection = new AndroidConnectionSource(sqLiteDatabase);
            Dao<CityMode, ?> dao = DaoManager.createDao(connection, CityMode.class);
            List<CityMode> cityModes = dao.queryBuilder().orderBy("area_code", true).where().eq("area_level", 1).query();
            //List<CityMode> cityModes1 = dao.queryForEq("area_level", 1);
//            GenericRawResults<String[]> strings = dao.queryRaw("select * from tb_area_code where area_level=1");
//            List<String[]> results = strings.getResults();
//            List<CityMode> cityModes=new Gson().fromJson()
//            Sysout.out(results.toString());
            sqLiteDatabase.close();
            return cityModes;
        } catch (Exception e) {
        }
        sqLiteDatabase.close();
        return null;
    }

    /**
     * 通过code查地址
     *
     * @param area_code
     * @return
     */
    public List<CityMode> getCitysOfCode(String... area_code) {
        Object[] obj = area_code;
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(dataPath, 0, null);
        try {
            AndroidConnectionSource connection = new AndroidConnectionSource(sqLiteDatabase);
            Dao<CityMode, ?> dao = DaoManager.createDao(connection, CityMode.class);
            List<CityMode> cityModes = dao.queryBuilder().orderBy("area_code", true).where().in("area_code", obj).query();
            sqLiteDatabase.close();
            return cityModes;
        } catch (Exception e) {
        }
        sqLiteDatabase.close();
        return null;
    }

    /**
     * 通过父地址查子地址
     *
     * @param parent_area_code
     */
    public List<CityMode> getCitys(String parent_area_code) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(dataPath, 0, null);
        try {
            AndroidConnectionSource connection = new AndroidConnectionSource(sqLiteDatabase);
            Dao<CityMode, ?> dao = DaoManager.createDao(connection, CityMode.class);
            List<CityMode> cityModes = dao.queryBuilder().orderBy("area_code", true).where().eq("parent_area_code", parent_area_code).query();
            sqLiteDatabase.close();
            return cityModes;
        } catch (Exception e) {
        }
        sqLiteDatabase.close();
        return null;
    }
}
