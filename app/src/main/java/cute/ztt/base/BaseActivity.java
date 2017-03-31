package cute.ztt.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cute.ztt.MyApplication;

/**
 * Created by 晖仔(Milo) on 2017-03-30.
 * email:303767416@qq.com
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = "BaseActivity";

    protected abstract int getContentViewId();

    protected abstract void init();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        TAG = getClass().getName();
    }

}
