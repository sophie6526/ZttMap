package cute.ztt.ui;

import android.view.View;
import android.widget.Button;

import cute.ztt.R;
import cute.ztt.base.AppActivity;
import cute.ztt.db.SampleConstants;

/**
 * Created by 晖仔(Milo) on 2017/3/1.
 * email:303767416@qq.com
 */

public class MainActivity extends AppActivity implements View.OnClickListener {

    Button btnLocation;
    Button btnDraw;
    Button btnDrawLoc;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(this);
        btnDraw = (Button) findViewById(R.id.btn_draw);
        btnDraw.setOnClickListener(this);
        btnDrawLoc = (Button) findViewById(R.id.btn_draw_location);
        btnDrawLoc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_location:
                startActivity(LocationActivity.createIntent(this));
                break;
            case R.id.btn_draw:
                startActivity(DrawActivity.createIntent(this, SampleConstants.getSamplePoints()));
                break;
            case R.id.btn_draw_location:
                startActivity(DrawLocationActivity.createIntent(this));
                break;
        }
    }

}
