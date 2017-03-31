package cute.ztt.utils;

import android.database.Cursor;

/**
 * Created by 晖仔(Milo) on 2017-03-31.
 * email:303767416@qq.com
 */

public class IOUtils {

    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
