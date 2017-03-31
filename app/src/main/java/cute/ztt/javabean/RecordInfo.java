package cute.ztt.javabean;

import cute.ztt.utils.DateUtils;

/**
 * Created by 晖仔(Milo) on 2017-03-31.
 * email:303767416@qq.com
 */

public class RecordInfo {

    public int id;
    public long startTime;
    public long endTime;
    public int locationCount;

    public String getStartTime() {
        return DateUtils.getSampleDate(startTime);
    }

    public String getEndTime() {
        return DateUtils.getSampleDate(endTime);
    }

}
