package cute.ztt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 晖仔(Milo) on 2017-03-31.
 * email:303767416@qq.com
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME_DFT = "dft_db";
    private static final int VERSION = 1;

    /**
     * 索引表
     */
    public static final String TABLE_RECORYD = "record";
    /**
     * 定为记录表
     */
    public static final String TABLE_LOCATION = "location";

    public DBHelper(Context context) {
        super(context, DBNAME_DFT, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable1 = "CREATE TABLE IF NOT EXISTS " + TABLE_RECORYD + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "starttime INTEGER, endtime INTEGER, locationcount INTEGER default 0)";
        db.execSQL(createTable1);

        String createTable2 = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "recordId Integer not null, longitude double, latitude double, timestamp INTEGER)";
        db.execSQL(createTable2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
