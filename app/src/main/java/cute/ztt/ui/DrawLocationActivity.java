package cute.ztt.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.OnCheckedChanged;
import cute.ztt.MyApplication;
import cute.ztt.R;
import cute.ztt.base.AppActivity;
import cute.ztt.db.DBManager;
import cute.ztt.service.LocationService;
import cute.ztt.utils.LogUtils;
import cute.ztt.utils.ToastUtils;


/**
 * Created by 晖仔(Milo) on 2017-03-31.
 * email:303767416@qq.com
 */

public class DrawLocationActivity extends AppActivity implements View.OnClickListener {

    private MapView mapView;
    protected BaiduMap mBaiduMap;

    private Button btnRecord;
    private Button btnClear;
    private Polyline polyline;

    boolean isSim = false;//是否开启模拟
    boolean isFirstLoc = true; // 是否首次定位
    protected LocationService locationService;

    private List<LatLng> cacheList = new ArrayList<>();

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, DrawLocationActivity.class);
        return intent;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_record;
    }

    @Override
    protected void init() {
        btnRecord = (Button) findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(this);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(false);
        btnRecord.setText(DBManager.getInstance(this).isRecording() ? R.string.stop_record : R.string.start_record);
    }

    @OnCheckedChanged(R.id.cbox_sim)
    public void onSimCheckedChanged(boolean checked) {
        isSim = checked;
        ToastUtils.showToast(this, checked ? "开启模拟" : "关闭模拟");

        if (locationService != null) {
            LocationClientOption option = locationService.getDefaultLocationClientOption();
            option.setScanSpan(checked ? 3000 : 10000);
            locationService.setLocationOption(option);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.menu_draw_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.record_list:
                startActivity(RecordListActivity.createIntent(this));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                if (DBManager.getInstance(this).isRecording()) {
                    //停止录入
                    DBManager.getInstance(this).stopRecord();
                    stopLocationService();
                    btnRecord.setText(R.string.start_record);
                } else {
                    //开启录入
                    DBManager.getInstance(this).startRecord();
                    startLocaionService();
                    btnRecord.setText(R.string.stop_record);
                }
                break;
            case R.id.btn_clear:
                // 清除所有图层
                mapView.getMap().clear();
                cacheList.clear();
                break;
        }
    }

    /**
     * 开启定位服务
     */
    private void startLocaionService() {
        if (locationService == null) {
            // -----------location config ------------
            locationService = MyApplication.getInstance().locationService;
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            locationService.registerListener(mListener);
            //注册监听
            int type = getIntent().getIntExtra("from", 0);
            if (type == 0) {
                LocationClientOption option = locationService.getDefaultLocationClientOption();
                option.setScanSpan(10 * 1000);
                locationService.setLocationOption(option);
            } else if (type == 1) {
                locationService.setLocationOption(locationService.getOption());
            }
        }

        locationService.start();// 定位SDK
    }

    /**
     * 停止定位服务
     */
    private void stopLocationService() {
        if (locationService != null) {
            locationService.stop();
        }
    }

    /**
     * 绘制普通折线
     *
     * @param color
     * @param optionsWidth
     * @param onlyDrawLast 是否只绘制最后一次记录
     */
    public void drawPath(@ColorInt int color, int optionsWidth, List<LatLng> points, boolean onlyDrawLast) {
        if (points == null || points.size() == 0) {
            return;
        }
        if (points.size() <= 2) {
            LogUtils.e("points 的长度必须大于2");
            return;
        }

        OverlayOptions ooPolyline = null;
        if (onlyDrawLast) {
            points = points.subList(points.size() - 2, points.size());
            ooPolyline = new PolylineOptions().width(optionsWidth)
                    .color(color).points(points);
        } else {
            ooPolyline = new PolylineOptions().width(optionsWidth)
                    .color(color).points(points);
        }

        polyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                LatLng latLng = null;
                if (isSim) { //开启模拟
                    if (cacheList != null && cacheList.size() != 0) {
                        LatLng lastLatLng = cacheList.get(cacheList.size() - 1);
                        double lati = new Random().nextInt(10) * 0.0001 + lastLatLng.latitude;
                        double loni = new Random().nextInt(10) * 0.0001 + lastLatLng.longitude;
                        latLng = new LatLng(lati, loni);
                    } else {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                } else { //关闭模拟
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                //加入到缓存
                cacheList.add(latLng);
                //将定位信息添加到数据库
                DBManager.getInstance(DrawLocationActivity.this).addLocationItem(latLng);
                //绘制
                drawPath(getResources().getColor(R.color.green_dft), 10, cacheList, false);

                //更新地图
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
                if (isFirstLoc) {
                    //首次定位，将地图移动到定位地点
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        }

    };

}
