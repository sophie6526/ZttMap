package cute.ztt.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yanjunhui
 * on 2016/9/24.
 * email:303767416@qq.com
 */
public class DateUtils {

    private static final String TAG = "DateUtils";

    private static final String SPLIT_DATETIME = " ";//间隔符 - 日期&时分
    private static final String SPLIT_DATE = "-";//间隔符 - 日期
    private static final String SPLIT_TIME = ":";//间隔符 - 时分

    public static String getSampleDate(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date(time);
        return sf.format(d);
    }

    public static String getSampleDateCN(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date d = new Date(time);
        return sf.format(d);
    }

    public static String getDateWithoutHHmm(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(time);
        return sf.format(d);
    }

    public static String getDateWithoutHHmmCN(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        Date d = new Date(time);
        return sf.format(d);
    }

    public static String getWeekdayCN(long time) {
        Date d = new Date(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String[] daysCN = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return daysCN[dayIndex];
    }

    public static String getDateMD5() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date(System.currentTimeMillis());
        return sf.format(d);
    }

    /**
     * 限制最小时间，为100年前
     *
     * @return
     */
    public static Date getMinDate() {
        Calendar cal = Calendar.getInstance();
        LogUtils.d(TAG, "[getMinDate] year == " + (cal.get(Calendar.YEAR) - 100));
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 100);
        return cal.getTime();
    }

    /**
     * 显示的时间
     * 1、1分钟内显示：刚刚
     * 2、1小时内显示：xx分钟前
     * 3、24小时内显示：xx小时前
     * 4、未超过一个月：1天前—30天前
     * 5、超过1个月—12个月显示：1个月前-11个月前
     * 5、超过1年显示：1年前，2年前
     *
     * @param dateStr 格式：2015-07-15 16:39:52
     * @return
     */
    public static String getTime(String dateStr, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date now = new Date();
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        long difference = now.getTime() - date.getTime();
        long year = difference / 1000 / 60 / 60 / 24 / 30 / 12;
        long month = difference / 1000 / 60 / 60 / 24 / 30;
        long day = difference / (24 * 60 * 60 * 1000);
        long hour = (difference / (60 * 60 * 1000) - day * 24);
        long min = ((difference / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (difference / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

        /*if(BmApplication.isDebug){
            Log.v("DateUtils", year + "年" + month + "月" + day + "天" + hour + "小时" + min + "分" + second + "秒");
        }*/

        if (year > 0) {
            return year + "年前";
        }
        if (month > 0) {
            return month + "个月前";
        }
        if (day > 0) {
            return day + "天前";
        }
        if (hour > 0) {
            return hour + "小时前";
        }
        if (min > 0) {
            return min + "分钟前";
        }
        return "刚刚";
    }

    /**
     * 字符串转Date
     *
     * @return
     */
    public static Date string2Date(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return new Date();
        }
        int year, month, day, hour = 0, minute = 0;
        String dateText[] = dateString.split(SPLIT_DATETIME);

        String dateArray[] = dateText[0].split(SPLIT_DATE);
        try {
            year = Integer.parseInt(dateArray[0]);
            month = Integer.parseInt(dateArray[1]);
            month = Math.max(0, month - 1);
            day = Integer.parseInt(dateArray[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }

        if (dateText.length == 2) {
            String timeArray[] = dateText[1].split(SPLIT_TIME);
            try {
                hour = Integer.parseInt(timeArray[0]);
                minute = Integer.parseInt(timeArray[1]);
            } catch (Exception e) {
                e.printStackTrace();
                return new Date();
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTime();
    }

    public static String date2String(Date date) {
        return date2String(date, false);
    }

    public static String date2StringWithHHmm(Date date) {
        return date2String(date, true);
    }

    /**
     * Date转字符串
     *
     * @param date
     * @param withHHmm - 是否包含时分
     * @return
     */
    private static String date2String(Date date, boolean withHHmm) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);


        if (withHHmm) {
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            return y + "-" + StringUtils.format10Str(m) + "-" + StringUtils.format10Str(d) + SPLIT_DATE + StringUtils.format10Str(hour) + ":" + StringUtils.format10Str(min);
        } else {
            return y + "-" + StringUtils.format10Str(m) + "-" + StringUtils.format10Str(d);
        }
    }

    /**
     * 把秒转换成字符串
     *
     * @param seconds
     * @return
     */
    public static String seconds2Str(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("seconds 必须大于等于零");
        }
        return StringUtils.format10Str(seconds / 60) + ":" + StringUtils.format10Str(seconds % 60);
    }


}
