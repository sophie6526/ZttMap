package cute.ztt.utils;

import android.util.Log;

import cute.ztt.Config;

/**
 * Created by yanjunhui
 * 2016/11/16
 * email:303767416@qq.com
 */

public class LogUtils {

    private static final String TAG = "LogUtils";

    public static void d(String text) {
        if (Config.isDebug) {
            Log.d(TAG, text);
        }
    }

    public static void i(String text) {
        if (Config.isDebug) {
            Log.i(TAG, text);
        }
    }

    public static void e(String text) {
        if (Config.isDebug) {
            Log.e(TAG, text);
        }
    }

    public static void v(String text) {
        if (Config.isDebug) {
            Log.v(TAG, text);
        }
    }

    public static void d(String tag, String text) {
        if (Config.isDebug) {
            Log.d(tag, text);
        }
    }

    public static void i(String tag, String text) {
        if (Config.isDebug) {
            Log.i(tag, text);
        }
    }

    public static void e(String tag, String text) {
        if (Config.isDebug) {
            Log.e(tag, text);
        }
    }

    public static void v(String tag, String text) {
        if (Config.isDebug) {
            Log.v(tag, text);
        }
    }

}
