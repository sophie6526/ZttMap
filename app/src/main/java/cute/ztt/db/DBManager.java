package cute.ztt.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cute.ztt.R;
import cute.ztt.javabean.RecordInfo;
import cute.ztt.utils.IOUtils;
import cute.ztt.utils.ToastUtils;

/**
 * Created by 晖仔(Milo) on 2017-03-31.
 * email:303767416@qq.com
 */

public class DBManager {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private static DBManager instance;

    private Context context;

    public static final int DEFAULT_PAGESIZE = 10;
    private static final int INVALID_ID = 0;//无效id
    private int recordId = INVALID_ID;
    private boolean isRecording = false;//是否正在记录

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context.getApplicationContext());
        }
        return instance;
    }

    private DBManager() {

    }

    private DBManager(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 开始记录
     */
    public void startRecord() {
        if (isRecording) {
            ToastUtils.showToast(context, R.string.recording_no_repeaet);
            return;
        }
        isRecording = true;
        db.beginTransaction();

        try {
            //开始记录前，将索引id改为1
            setRecordId(getRecordId() + 1);

            ContentValues values = new ContentValues();
            values.put("starttime", System.currentTimeMillis());
            values.put("endtime", 0);
            values.put("locationcount", 0);

            db.insert(DBHelper.TABLE_RECORYD, "id", values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 结束记录
     */
    public void stopRecord() {
        if (!isRecording) {
            ToastUtils.showToast(context, R.string.no_recording_click_start);
            return;
        }
        isRecording = false;
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("endtime", System.currentTimeMillis());
            values.put("locationcount", getLocationCountByIndex(getRecordId()));

            db.update(DBHelper.TABLE_RECORYD, values, "id = " + getRecordId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取记录列表
     *
     * @return
     */
    public List<RecordInfo> getRecordList(int page) {
        db.beginTransaction();

        List<RecordInfo> listData = null;
        Cursor cursor = null;
        try {
            String sqlStr = "select * from " + DBHelper.TABLE_RECORYD + " order by id desc limit " + ((page - 1) * DEFAULT_PAGESIZE) + "," + DEFAULT_PAGESIZE + ";";
            cursor = db.rawQuery(sqlStr, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (listData == null) {
                        listData = new ArrayList<>();
                    }

                    RecordInfo info = new RecordInfo();
                    info.id = cursor.getInt(cursor.getColumnIndex("id"));
                    info.startTime = cursor.getInt(cursor.getColumnIndex("starttime"));
                    info.endTime = cursor.getInt(cursor.getColumnIndex("endtime"));
                    info.locationCount = cursor.getInt(cursor.getColumnIndex("locationcount"));

                    listData.add(info);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(cursor);
            db.endTransaction();
        }

        return listData;
    }

    /**
     * 获取定位列表
     *
     * @param recordId 索引
     * @return
     */
    public ArrayList<LatLng> getLocationList(int recordId) {
        db.beginTransaction();

        ArrayList<LatLng> listData = null;
        Cursor cursor = null;
        try {
            String query = "select * from " + DBHelper.TABLE_LOCATION + " where recordId = " + recordId + ";";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (listData == null) {
                        listData = new ArrayList<>();
                    }

                    double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    LatLng latLng = new LatLng(latitude, longitude);
                    listData.add(latLng);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(cursor);
            db.endTransaction();
        }

        return listData;
    }

    protected int getLocationCountByIndex(int recordId) {
        int count = 0;
        db.beginTransaction();

        Cursor cursor = null;
        try {
            String query = "select count(recordId) from " + DBHelper.TABLE_LOCATION + " where recordId = " + recordId + ";";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(cursor);
            db.endTransaction();
        }

        return count;
    }

    public void addLocationItem(LatLng latLng) {
        if (null != latLng) {
            db.beginTransaction();

            try {
                ContentValues values = new ContentValues();
                values.put("recordId", getRecordId());
                values.put("longitude", latLng.longitude);
                values.put("latitude", latLng.latitude);
                values.put("timestamp", System.currentTimeMillis());

                db.insert(DBHelper.TABLE_LOCATION, "id", values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

        }
    }

    protected int getRecordId() {
        if (recordId == INVALID_ID) {
            String query = "select max(id) from " + DBHelper.TABLE_RECORYD;
            db.beginTransaction();

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {
                    recordId = cursor.getInt(0);
                }
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(cursor);
                db.endTransaction();
            }
        }

        return recordId;
    }

    private void setRecordId(int id) {
        if (id <= INVALID_ID) {
            throw new IllegalArgumentException("index必须为大于0的整数");
        }
        this.recordId = id;
    }

    public boolean isRecording() {
        return isRecording;
    }

}
