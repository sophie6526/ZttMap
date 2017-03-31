package cute.ztt;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

import com.baidu.mapapi.SDKInitializer;

import java.util.Stack;

import cute.ztt.service.LocationService;

/**
 * Created by 晖仔(Milo) on 2017-03-30.
 * email:303767416@qq.com
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private static Stack<Activity> activityStack = new Stack<>();

    private Handler handler;
    public LocationService locationService;

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
            instance.onCreate();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initBaiduMap();
    }

    private void initBaiduMap() {
        locationService = new LocationService(this);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

    public Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            activityStack.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    private void finishActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null && !activityStack.get(i).isFinishing()) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


}
