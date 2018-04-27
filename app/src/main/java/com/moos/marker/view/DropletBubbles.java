package com.moos.marker.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.moos.marker.R;


/**
 * @describe 自定义气泡
 * @anthor 符乃辉 QQ:1151631351
 * @time 2018/4/13 上午9:36
 */
public class DropletBubbles extends View implements SensorEventListener {
    private Paint mBackgroundPaint;//背景画笔
    private Paint mBubblesPaint;//气泡画笔
    private Path mBackgroundPath;
    private Path mBubblesPath;
    private int mInnerRadius;
    private int mOutRadius;
    private int mResultWidth;
    private int mResultHeight;
    private Context mContext;
    private static final String TAG = "DropletBubbles";

    private float baseLine = 0;// 基线，用于控制水位上涨的，这里是写死了没动，你可以不断的设置改变。
    private int waveHeight;// 波浪的最高度
    private int waveWidth;//波长
    private float offset = 0f;//偏移量
    private int width = 0;
    private int height = 0;
    private int mBackgroundColor;
    private int mWaveColor;
    private float mStartY;

    private SensorManager sensorManager;

    private Sensor gyroscopeSensor;

    private static final float NS2S = 1.0f / 1000000000.0f;

    private float timestamp;

    private float angle[] = new float[3];
    private float mAnglez;

    public DropletBubbles(Context context) {
        this(context, null);
    }

    public DropletBubbles(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropletBubbles(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DropletBubbles, 0, defStyleAttr);
        mInnerRadius = typedArray.getDimensionPixelSize(R.styleable.DropletBubbles_innerRadius, 10);
        mOutRadius = typedArray.getDimensionPixelSize(R.styleable.DropletBubbles_outRadius, 12);
        mBackgroundColor = typedArray.getColor(R.styleable.DropletBubbles_backgroundColor, getResources().getColor(R.color.colorPrimary));
        mWaveColor = typedArray.getColor(R.styleable.DropletBubbles_waveColor, getResources().getColor(R.color.colorAccent));
        typedArray.recycle();

        init(context);//初始化画笔

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mResultWidth = widthSize;
        mResultHeight = heightSize;
        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth = mOutRadius * 2 + getPaddingLeft() + getPaddingRight();
            mResultWidth = (contentWidth < widthSize) ? contentWidth : mResultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = mOutRadius * 2 + getPaddingTop() + getPaddingBottom() + mOutRadius / 4;
            mResultHeight = (contentHeight < heightSize) ? contentHeight : mResultHeight;
        }

        setMeasuredDimension(mResultWidth, mResultHeight);
    }

    private void init(Context context) {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);


        mBubblesPaint = new Paint();
        mBubblesPaint.setAntiAlias(true);
        mBubblesPaint.setColor(mWaveColor);
        mBubblesPaint.setStyle(Paint.Style.FILL);


        mBackgroundPath = new Path();
        mBubblesPath = new Path();
        waveHeight = dp2px(mContext, 8);


        //注册陀螺仪
//        registerSensor(context);

    }

    private void registerSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, gyroscopeSensor,

                SensorManager.SENSOR_DELAY_GAME);
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBubblesPath.reset();

        Shader shader = new LinearGradient(mResultWidth / 2, mResultWidth / 2-mInnerRadius, mResultWidth / 2, mResultWidth / 2 + mInnerRadius , Color.parseColor("#9592FB"),
                Color.parseColor("#3831D4"), Shader.TileMode.CLAMP);
        mBubblesPaint.setShader(shader);


        mBackgroundPath.moveTo(mResultWidth / 2 - mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2);
        mBackgroundPath.lineTo(mResultWidth / 2, mResultWidth / 2 + mOutRadius + mOutRadius / 4);
        mBackgroundPath.lineTo(mResultWidth / 2 + mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2);


        mBubblesPath.moveTo(mResultWidth / 2 - mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2 - dp2px(mContext, 5));
        mBubblesPath.lineTo(mResultWidth / 2, mResultWidth / 2 + mOutRadius + mOutRadius / 4 - dp2px(mContext, 5));
        mBubblesPath.lineTo(mResultWidth / 2 + mOutRadius / 2, mResultWidth / 2 + mOutRadius / 2 - dp2px(mContext, 5));
        //画外部背景
        canvas.drawPath(mBackgroundPath, mBackgroundPaint);
        canvas.drawCircle(mResultWidth / 2, mResultWidth / 2, mOutRadius, mBackgroundPaint);
        Log.d(TAG, "cx: " + mResultWidth / 2);

//        canvas.drawPath(mBubblesPath,mBubblesPaint);
//        canvas.drawCircle(mResultWidth/2,mResultWidth/2,mInnerRadius,mBubblesPaint);


        //切割画布，画水波
        canvas.save();
        mBubblesPath.addCircle(mResultWidth / 2, mResultWidth / 2, mInnerRadius, Path.Direction.CCW);
        Log.d(TAG, "mAnglez: "+mAnglez);
        canvas.clipPath(mBubblesPath);
        //旋转画布
//        canvas.rotate(mAnglez,mResultWidth / 2, mResultWidth / 2);

        canvas.drawPath(getPath(), mBubblesPaint);
        canvas.restore();

        Log.d(TAG, "onDraw: ");

    }


    /**
     * 不断的更新偏移量，并且循环。
     */
    public void updateXControl() {
        //设置一个波长的偏移
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, waveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                offset = animatorValue;//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimator.setDuration(1500);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = mResultWidth;
        height = mResultHeight;//获取视图高度
        waveWidth = width;
        setBaseLine(height/2);
        updateXControl();


    }

    public void setBaseLine(float value) {

        baseLine = height - value;

    }


    /**
     * 核心代码，计算path
     *
     * @return
     */
    private Path getPath() {
        int itemWidth = waveWidth / 2;//半个波长
        Path mPath = new Path();
        mPath.moveTo(-itemWidth * 3, baseLine);//起始坐标
        Log.d(TAG, "getPath: " + baseLine);
        mStartY = baseLine + waveHeight;

        //核心的代码就是这里
        for (int i = -3; i < 2; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth / 2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeigh(i),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine//结束点的Y
            );//只需要处理完半个波长，剩下的有for循环自已就添加了。
        }
        Log.d(TAG, "getPath: ");
        //下面这三句话很重要，它是形成了一封闭区间，让曲线以下的面积填充一种颜色，大家可以把这3句话注释了看看效果。
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();
        return mPath;
    }

    //奇数峰值是正的，偶数峰值是负数
    private float getWaveHeigh(int num) {
        if (num % 2 == 0) {
            return baseLine + waveHeight;
        }
        return baseLine - waveHeight;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值

        if (timestamp != 0) {

// 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒

            final float dT = (event.timestamp - timestamp) * NS2S;

// 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度

            angle[0] += event.values[0] * dT;

            angle[1] += event.values[1] * dT;

            angle[2] += event.values[2] * dT;

// 将弧度转化为角度

            float anglex = (float) Math.toDegrees(angle[0]);

            float angley = (float) Math.toDegrees(angle[1]);

            mAnglez = (float) Math.toDegrees(angle[2]);


            System.out.println("anglex------------>" + anglex);

            System.out.println("angley------------>" + angley);

            System.out.println("anglez------------>" + mAnglez);


            System.out.println("gyroscopeSensor.getMinDelay()----------->" +

                    gyroscopeSensor.getMinDelay());

        }

        timestamp = event.timestamp;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
