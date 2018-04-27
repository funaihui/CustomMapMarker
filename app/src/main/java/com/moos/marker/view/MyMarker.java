package com.moos.marker.view;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.LatLng;
import com.moos.marker.R;
import com.moos.marker.Utils.ScreenUtils;

/**
 * @describe describe
 * @anthor 符乃辉 QQ:1151631351
 * @time 2018/4/27 下午3:50
 */
public class MyMarker {

    private Context mContext;
    //x点,y点
    private int mMarkerX,mMarderY;
    //布局
    private View markerView;
    private MapView mapView;
    //marker数据来源
    private LatLng latLng;
    private AMap aMap;
    private OnClickMyMarker listenter;

    /**
     * 构造方法,传入必须的参数
     *
     * @param mapView 这是地图的父View
     *
     * @param aMap
     * */
    public MyMarker (Context mContext,LatLng latLng,
                     AMap aMap,MapView mapView){
        this.mContext = mContext;
        this.latLng = latLng;
        this.aMap = aMap;
        this.mapView = mapView;
        getScreenPix(aMap);
    }

    /**
     * 将点传进来,转化为屏幕上的点
     *
     * */
    public void getScreenPix(AMap aMap){

        if (latLng == null){
            return;
        }
        //小范围，小缩放级别（比例尺较大），有精度损失
        Projection projection = aMap.getProjection();
        //将地图的上的点，转换为屏幕上的点
        Point center = projection.toScreenLocation(latLng);
        //获取距离中心点（屏幕上的点 )
        mMarkerX = center.x;
        mMarderY = center.y;
        // 加载布局
        markerView = LayoutInflater.from(mContext).inflate(
                R.layout.marker_bg, null);
        //显示图标
        if ((mMarkerX > 0 || mMarderY>0) && mMarkerX< ScreenUtils.getScreenHeight(mContext)
                -(mContext.getResources().getDimensionPixelOffset(R.dimen.my_margin_30))){
            setLayout(markerView, mMarkerX, mMarderY);
        }
    }

    /**
     * 设置数字Tag,相应点击事件
     * */
    public void setMarkerTag(int position){
        if (markerView != null){
            markerView.setTag(position);
        }
    }

    /**
     * 根据X,Y设置view的位置
     *
     * */
    public void setMarkerXY(){
        if (mMarkerX == 0 && mMarderY>0){
            setLayoutY(markerView, mMarderY);
        }else if (mMarkerX > 0 && mMarderY==0){
            setLayoutX(markerView,mMarkerX);
        }else if ((mMarkerX > 0 || mMarderY>0) && mMarkerX< ScreenUtils.getScreenHeight(mContext)
                -(mContext.getResources().getDimensionPixelOffset(R.dimen.my_margin_30))){
            setLayout(markerView, mMarkerX,mMarderY);
        }
    }

    /**
     * 设置控件所在的位置X，并且不改变宽高，
     * X为绝对位置，此时Y可能归0
     */
    public void setLayoutX(View view,int x)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(x,params.topMargin, x+params.width, params.bottomMargin);
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);
    }
    /**
     * 设置控件所在的位置Y，并且不改变宽高，
     * Y为绝对位置，此时X可能归0
     */
    public void setLayoutY(View view,int y)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(params.leftMargin,y, params.rightMargin, y+params.height);
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);
    }
    /**
     * 设置控件所在的位置YY，并且不改变宽高，
     * XY为绝对位置
     */
    public void setLayout(View view,int x,int y)
    {

        //设置自定义marker显示位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mapView.getLayoutParams());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //view是以左上角为锚点,而我现在是结合view 的尺寸来计算,让view偏移到中心位置
        params.setMargins(x-(mContext.getResources().getDimensionPixelOffset(R.dimen.my_margin_30)),
                y - (mContext.getResources().getDimensionPixelOffset(R.dimen.my_margin_35)), 0, 0);
        view.setLayoutParams(params);
        mapView.addView(view);
        view.setOnClickListener(new clickMarker());
    }

    /**
     * 创建接口
     * */
    public interface OnClickMyMarker {
        void doOnClic(View view);
    }

    /**
     * 提供接口方法
     * */
    public void setOnClickMyMarker(OnClickMyMarker listenter){
        this.listenter = listenter;
    }

    /**
     * 添加点击监听
     * */
    class clickMarker implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            listenter.doOnClic(view);
        }
    }
}
