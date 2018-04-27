package com.moos.marker.Sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.moos.marker.R;
import com.moos.marker.view.MyMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author moos
 * @date 2018/01/13
 * @function display ui
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mapView = null;
    private Button  bt_add_custom_marker;
    private AMap aMap;
    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<Integer, Drawable>();
    private Map<Integer, Drawable> mBackDrawAblesMerchant = new HashMap<Integer, Drawable>();
    private final LatLng centerLocation = new LatLng(31.206078, 121.602948);
    private final String TYPE_MERCHANT = "02";
    private final String TYPE_USER = "01";
    private final String TAG = "Moos";
    private int clusterRadius = 48;

    BitmapDescriptor bitmapDescriptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        initView();
        initMap();
    }


    private void initView() {
        bt_add_custom_marker = (Button) findViewById(R.id.add_custom_marker);
        bt_add_custom_marker.setOnClickListener(this);
    }

    private void initMap() {
        aMap.setMinZoomLevel(8);
        aMap.setMaxZoomLevel(20);
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {


            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });

        moveMapToPosition(centerLocation);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * by moos on 2018/01/12
     * func:移动地图视角到某个精确位置
     *
     * @param latLng 坐标
     */
    private void moveMapToPosition(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(
                        latLng,//新的中心点坐标
                        16,    //新的缩放级别
                        0,     //俯仰角0°~45°（垂直与地图时为0）
                        0      //偏航角 0~360° (正北方为0)
                ));
        aMap.animateCamera(cameraUpdate, 300, new AMap.CancelableCallback() {
            @Override
            public void onFinish() {


            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * by moos on 2018/01/12
     * func:添加位置模拟数据
     *
     * @param centerPoint 中心点
     * @param num         数量
     * @param offset      经纬度模拟的可调偏移参数
     * @return
     */
    private List<LatLng> addSimulatedData(LatLng centerPoint, int num, double offset) {
        List<LatLng> data = new ArrayList<>();
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                double lat = centerPoint.latitude + (Math.random() - 0.5) * offset;
                double lon = centerPoint.longitude + (Math.random() - 0.5) * offset;
                LatLng latlng = new LatLng(lat, lon);
                data.add(latlng);
            }
        }
        return data;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_custom_marker:
                //添加自定义Marker
                addCustomMarkersToMap();
                break;
        }
    }

    /**
     * by moos on 2018/01/12
     * func:添加单个自定义marker
     *
     * @param latLng 位置
     */
    private void addCustomMarker(final LatLng latLng) {

        MyMarker myMarker = new MyMarker(this, latLng, aMap, mapView);
        myMarker.setOnClickMyMarker(new MyMarker.OnClickMyMarker() {
            @Override
            public void doOnClic(View view) {
                Toast.makeText(MainActivity.this,"点击了",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * by moos on 2018/01/12
     * func:批量添加自定义marker到地图上
     */
    private void addCustomMarkersToMap() {

        List<LatLng> locations = new ArrayList<>();
        locations = addSimulatedData(centerLocation, 20, 0.02);
        for (int i = 0; i < locations.size(); i++) {
            addCustomMarker(locations.get(i));
        }

    }


}
