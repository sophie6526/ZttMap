package cute.ztt.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cute.ztt.R;
import cute.ztt.base.AppActivity;
import cute.ztt.db.Extra;
import cute.ztt.utils.LogUtils;
import cute.ztt.utils.ToastUtils;


/**
 * Created by 晖仔(Milo) on 2017-03-30.
 * email:303767416@qq.com
 */

public class DrawActivity extends AppActivity {

    protected MapView mapView;
    protected BaiduMap mBaiduMap;
    private Polyline polyline;

    public static Intent createIntent(Context context, ArrayList<LatLng> dataList) {
        Intent intent = new Intent(context, DrawActivity.class);
        intent.putExtra(Extra.KEY_1, dataList);
        return intent;
    }

    private List<LatLng> getPointList() {
        return getIntent().getParcelableArrayListExtra(Extra.KEY_1);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_draw;
    }

    @Override
    protected void init() {
        mapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mapView.getMap();

        drawPath(getResources().getColor(R.color.green_dft), 10, getPointList());
    }

    /**
     * 绘制普通折线
     *
     * @param color
     * @param optionsWidth
     */
    public void drawPath(@ColorInt int color, int optionsWidth, List<LatLng> dataList) {
        if (dataList == null || dataList.size() < 2) {
            ToastUtils.showToast(this, "dataList.size must be more then 2");
            finish();
            return;
        }
        setCenterPointer(dataList.get(0));

        OverlayOptions ooPolyline = new PolylineOptions().width(optionsWidth)
                .color(color).points(dataList);
        polyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        for (LatLng item : dataList) {
            LogUtils.i("收到的数据:" + item.latitude + "," + item.longitude);
        }
    }

    /**
     * 设置中心点
     *
     * @param centerLatLng
     */
    private void setCenterPointer(LatLng centerLatLng) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                //要移动的点
                .target(centerLatLng)
                //放大地图到20倍
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

}
