package cute.ztt.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import cute.ztt.R;


/**
 * Toast工具
 * Created by yanjunhui
 * on 2016/8/18.
 * email:303767416@qq.com
 */
public class ToastUtils {

    private static Toast mToast;

    private static Handler mhandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context context, int strId) {
        showToast(context, context.getString(strId), false);
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, false);
    }

    public static void showToast(Context context, int strId, boolean lengthLong) {
        showToast(context, context.getString(strId), lengthLong);
    }

    public static void showNotEmptyToast(Context context, int keyStrId) {
        showNotEmptyToast(context, context.getString(keyStrId));
    }

    public static void showNotEmptyToast(Context context, String keyText) {
        showToast(context, keyText + context.getString(R.string.cannot_be_empty));
    }

    public static void showToast(Context context, String text, boolean lengthLong) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mhandler.removeCallbacks(r);
        if (null != mToast) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }
        if (text.length() > 5) {
            lengthLong = true;
        }
        mhandler.postDelayed(r, lengthLong ? 1500 : 1000);
        mToast.show();
    }

    /**
     * 取消
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
