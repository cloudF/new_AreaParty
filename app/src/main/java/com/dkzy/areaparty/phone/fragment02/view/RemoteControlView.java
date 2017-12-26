package com.dkzy.areaparty.phone.fragment02.view;

/**
 * Created by ervincm on 2017/9/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 仿遥控器上下左右ok圆形菜单
 * Created by 庞光渝 on 2017/3/9.博客：https://my.oschina.net/u/1462828/blog
 */

public class RemoteControlView extends View {

    /**
     * 变量
     */
    private int coreX;//中心点的坐标X
    private int coreY;//中心点的坐标Y

    private List<RoundMenu> roundMenus;//菜单列表

    private boolean isCoreMenu = false;//是否有中心按钮
    private boolean isPowerMenu = false;//是否有电源按钮
    private boolean isBackMenu = false;//是否有返回按钮

    private int coreMenuColor;//中心按钮的默认背景--最好不要透明色
    private int coreMenuStrokeColor;//中心按钮描边颜色
    private int coreMenuStrokeSize;//中心按钮描边粗细
    private int coreMenuSelectColor;//中心按钮选中时的背景颜色
    public  static Bitmap coreBitmap;//OK图片
    private Bitmap powerBitmap;//电源图片
    private Bitmap backBitmap;//返回图片
    private OnClickListener onCoreClickListener;//中心按钮的点击回调
    private OnClickListener onPowerClickListener;//电源按钮的点击回调
    private OnClickListener onBackClickListener;//返回按钮的点击回调


    private float deviationDegree;//偏移角度
    private int onClickState = -2;//-2是无点击，-1是点击中心圆，其他是点击菜单
    private float roundRadius;//中心圆的半径
    private double radiusDistance;//半径的长度比（中心圆半径=大圆半径*radiusDistance）
    private long touchTime;//按下时间，抬起的时候判定一下，超过300毫秒算点击

    public RemoteControlView(Context context) {
        super(context);
    }

    public RemoteControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RemoteControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    protected void onDraw(Canvas canvas) {





        coreX = getWidth() / 2;
        coreY = getHeight() / 2;


        float x = (getWidth() /2) /3;
        float y = getHeight() / 2/3;
        roundRadius =x ;//计算中心圆圈半径

      /*  coreX=getWidth()/2;
        coreY=getHeight()/2;
        roundRadius = (int) (getWidth()/2 * radiusDistance);//计算中心圆圈半径

        float x = (getWidth() - getMeasuredHeight() / 2) / 2;
        float y = getMeasuredHeight() / 4;
        RectF rect= new RectF( x, y,getWidth() - x, getMeasuredHeight() - y);*/
        //RectF rect = new RectF(0, 0, getWidth(), getHeight());


        RectF rect = new RectF( coreX-2*x, coreY-2*x,coreX + 2*x, coreY+2*x);

        if (roundMenus != null && roundMenus.size() > 0) {
            float sweepAngle = 360 / roundMenus.size();//每个弧形的角度
            deviationDegree = sweepAngle / 2;//其实的偏移角度，如果4个扇形的时候是X形状，而非+,设为0试试就知道什么意思了
            for (int i = 0; i < roundMenus.size(); i++) {
                RoundMenu roundMenu = roundMenus.get(i);
                //填充
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                if (onClickState == i) {
                    //选中
                    paint.setColor(Color.rgb(247,203,213));
                } else {
                    //未选中
                    paint.setColor(Color.WHITE);
                }
                canvas.drawArc(rect, deviationDegree + (i * sweepAngle), sweepAngle, true , paint);





                //画描边
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(roundMenu.strokeSize);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(roundMenu.strokeColor);
                canvas.drawArc(rect, deviationDegree + (i * sweepAngle), sweepAngle, roundMenu.useCenter, paint);


                //画图案
                Matrix matrix = new Matrix();
                Log.i("ervicm",String.valueOf(i));
                switch (i){
                    case 0:
                        matrix.postTranslate((float)(coreX-roundMenu.icon.getWidth()/2),(float)(coreY+2*x*roundMenu.iconDistance)-(roundMenu.icon.getHeight()/2));
                        break;
                    case 1:
                        matrix.postTranslate((float) ((coreX -2*x*roundMenu.iconDistance) - (roundMenu.icon.getWidth() / 2)), coreY - (roundMenu.icon.getHeight() / 2));
                        break;

                    case 2:
                        matrix.postTranslate((float)(coreX-roundMenu.icon.getWidth()/2),(float)(coreY-2*x*roundMenu.iconDistance)-(roundMenu.icon.getHeight()/2));
                        break;
                    case 3:
                        matrix.postTranslate((float) ((coreX + 2*x*roundMenu.iconDistance) - (roundMenu.icon.getWidth() / 2)), coreY - (roundMenu.icon.getHeight() / 2));
                        break;
                }

//                if(i%2==1){
//                    matrix.postTranslate((float) ((coreX + getWidth() / 2 * roundMenu.iconDistance) - (roundMenu.icon.getWidth() / 2)), coreY - (roundMenu.icon.getHeight() / 2));
//                    matrix.postRotate(((i-1 ) * sweepAngle), coreX, coreY);
//                }else {
//                    matrix.postTranslate((float)(coreX-roundMenu.icon.getWidth()/2),(float)(coreY+getWidth()/2*roundMenu.iconDistance)-(roundMenu.icon.getHeight()/2));
//                    matrix.postRotate(((i ) * sweepAngle), coreX, coreY);
//                }

                canvas.drawBitmap(roundMenu.icon, matrix, null);
            }
        }

        //画中心圆圈
        if (isCoreMenu) {
            //填充
            RectF rect1 = new RectF(coreX - roundRadius, coreY - roundRadius, coreX + roundRadius, coreY + roundRadius);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            if (onClickState == -1) {
                paint.setColor(Color.rgb(247,203,213));
            } else {
                paint.setColor(coreMenuColor);
            }
            canvas.drawArc(rect1, 0, 360, true, paint);

            //画描边
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(coreMenuStrokeColor);
            canvas.drawArc(rect1, 0, 360, true, paint);
            if (coreBitmap != null) {
                //画中心圆圈的“OK”图标
                canvas.drawBitmap(coreBitmap, coreX - coreBitmap.getWidth() / 2, coreY - coreBitmap.getHeight() / 2, null);//在 0，0坐标开始画入src
            }
        }

        //画停止圆圈
        if (isPowerMenu) {
            //填充
            RectF rect1 = new RectF(100, 50, 250 , 200 );
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            if (onClickState == -3) {
                paint.setColor(Color.rgb(247,203,213));
            } else {
                paint.setColor(coreMenuColor);
            }
            canvas.drawArc(rect1, 0, 360, true, paint);

            //画描边
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(coreMenuStrokeColor);
            canvas.drawArc(rect1, 0, 360, true, paint);
            if (powerBitmap != null) {
                //画中心圆圈的“OK”图标
                //圆形是（175，125）.因为图片不规则。在X方向右移25，使之位于中间
                canvas.drawBitmap(powerBitmap, 200 - coreBitmap.getWidth() / 2, 125 - powerBitmap.getHeight() / 2, null);//在 0，0坐标开始画入src
            }
        }

        //画返回圆圈
        if (isBackMenu) {
            //填充

            //TODO：10.11
            RectF rect1 = new RectF(getWidth()-250, 50, getWidth()-100 , 200 );
          //  RectF rect1 = new RectF(800, 50, 950 , 200 );
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            if (onClickState == -4) {
                paint.setColor(Color.rgb(247,203,213));
            } else {
                paint.setColor(coreMenuColor);
            }
            canvas.drawArc(rect1, 0, 360, true, paint);

            //画描边
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(coreMenuStrokeColor);
            canvas.drawArc(rect1, 0, 360, true, paint);
            if (backBitmap != null) {
                //画中心圆圈的图标//同上   因为图片不规则。在X方向右移25，使之位于中间
                //TODO：10.11
                canvas.drawBitmap(backBitmap, getWidth()-150 - coreBitmap.getWidth() / 2, 125 - backBitmap.getHeight() / 2, null);//在 0，0坐标开始画入src
            }
        }


    }

//TODO:by ervincm 10.11
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchTime = new Date().getTime();
                float textX = event.getX();
                float textY = event.getY();
                onClickState=-2;
                int distanceLine = (int) getDisForTwoSpot(coreX, coreY, textX, textY);//距离中心点之间的直线距离
                if (distanceLine <= roundRadius) {
                    //点击的是中心圆；按下点到中心点的距离小于中心园半径，那就是点击中心园了
                    onClickState = -1;
                } else if (distanceLine <= (getWidth()) /3) { //(getWidth()  / 2) /3为x的值
                    //点击的是某个扇形；按下点到中心点的距离大于中心圆半径小于大圆半径，那就是点击某个扇形了
                    float sweepAngle = 360 / roundMenus.size();//每个弧形的角度
                    int angle = getRotationBetweenLines(coreX, coreY, textX, textY);
                    //这个angle的角度是从正Y轴开始，而我们的扇形是从正X轴开始，再加上偏移角度，所以需要计算一下
                    angle = (angle + 360 - 90 - (int) deviationDegree) % 360;
                    onClickState = (int) (angle / sweepAngle);//根据角度得出点击的是那个扇形
                }

                //TODO：9.22
                int distanceLine1 = (int) getDisForTwoSpot(175, 125, textX, textY);//距离中心点之间的直线距离
                if(distanceLine1<=75){
                    onClickState=-3;
                }
                int distanceLine2 = (int) getDisForTwoSpot(getWidth()-175, 125, textX, textY);//距离中心点之间的直线距离
                if(distanceLine2<=75){
                    onClickState=-4;
                }

                OnTouchListener onTouchListener=null;
                if(onClickState >= 0 && onClickState < roundMenus.size()){
                    onTouchListener=roundMenus.get(onClickState).onTouchListener;
                }
                if (onTouchListener != null) {

                    onTouchListener.onTouch(this,event);
                }



                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                OnTouchListener onTouchListener1=null;
                if(onClickState >= 0 && onClickState < roundMenus.size()){
                    onTouchListener1=roundMenus.get(onClickState).onTouchListener;
                }
                if (onTouchListener1 != null) {

                    onTouchListener1.onTouch(this,event);
                }

                if ((new Date().getTime() - touchTime) < 300) {
                    //点击小于300毫秒算点击
                    OnClickListener onClickListener = null;
                    if (onClickState == -1) {
                        onClickListener = onCoreClickListener;
                    } else if (onClickState == -3) {
                        onClickListener = onPowerClickListener;
                    } else if (onClickState == -4) {
                        onClickListener = onBackClickListener;
                    } else if (onClickState >= 0 && onClickState < roundMenus.size()) {
                        onClickListener = roundMenus.get(onClickState).onClickListener;
                    }
                    if (onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                }
                    onClickState = -2;
                invalidate();
                break;
        }
        return true;
    }

//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                touchTime = new Date().getTime();
//                float textX = event.getX();
//                float textY = event.getY();
//                onClickState=-2;
//                int distanceLine = (int) getDisForTwoSpot(coreX, coreY, textX, textY);//距离中心点之间的直线距离
//                if (distanceLine <= roundRadius) {
//                    //点击的是中心圆；按下点到中心点的距离小于中心园半径，那就是点击中心园了
//                    onClickState = -1;
//                } else if (distanceLine <= getWidth() / 2) {
//                    //点击的是某个扇形；按下点到中心点的距离大于中心圆半径小于大圆半径，那就是点击某个扇形了
//                    float sweepAngle = 360 / roundMenus.size();//每个弧形的角度
//                    int angle = getRotationBetweenLines(coreX, coreY, textX, textY);
//                    //这个angle的角度是从正Y轴开始，而我们的扇形是从正X轴开始，再加上偏移角度，所以需要计算一下
//                    angle = (angle + 360 - 90 - (int) deviationDegree) % 360;
//                    onClickState = (int) (angle / sweepAngle);//根据角度得出点击的是那个扇形
//                }
//
//                //TODO：9.22
//                int distanceLine1 = (int) getDisForTwoSpot(175, 125, textX, textY);//距离中心点之间的直线距离
//                if(distanceLine1<=75){
//                    onClickState=-3;
//                }
//                int distanceLine2 = (int) getDisForTwoSpot(getWidth()-175, 125, textX, textY);//距离中心点之间的直线距离
//                if(distanceLine2<=75){
//                    onClickState=-4;
//                }
//
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                if ((new Date().getTime() - touchTime) < 300) {
//                    //点击小于300毫秒算点击
//                    OnClickListener onClickListener = null;
//                    if (onClickState == -1) {
//                        onClickListener = onCoreClickListener;
//                    } else if (onClickState == -3) {
//                        onClickListener = onPowerClickListener;
//                    } else if (onClickState == -4) {
//                        onClickListener = onBackClickListener;
//                    } else if (onClickState >= 0 && onClickState < roundMenus.size()) {
//                        onClickListener = roundMenus.get(onClickState).onClickListener;
//                    }
//                    if (onClickListener != null) {
//                        onClickListener.onClick(this);
//                    }
//                }
//                onClickState = -2;
//                invalidate();
//                break;
//        }
//        return true;
//    }

    /**
     * 添加菜单
     *
     * @param roundMenu
     */
    public void addRoundMenu(RoundMenu roundMenu) {
        if (roundMenu == null) {
            return;
        }
        if (roundMenus == null) {
            roundMenus = new ArrayList<>();
        }
        roundMenus.add(roundMenu);
        invalidate();
    }

    /**
     * 添加中心菜单按钮
     *
     * @param coreMenuColor
     * @param coreMenuSelectColor
     * @param onClickListener
     */
    public void setCoreMenu(int coreMenuColor, int coreMenuSelectColor, int coreMenuStrokeColor,
                            int coreMenuStrokeSize, double radiusDistance,Bitmap bitmap, OnClickListener onClickListener) {
        isCoreMenu = true;
        this.coreMenuColor = coreMenuColor;
        this.radiusDistance = radiusDistance;
        this.coreMenuSelectColor = coreMenuSelectColor;
        this.coreMenuStrokeColor = coreMenuStrokeColor;
        this.coreMenuStrokeSize = coreMenuStrokeSize;
        this.coreBitmap = bitmap;
        this.onCoreClickListener = onClickListener;
        invalidate();
    }

    public void setBackMenu(int coreMenuColor, int coreMenuSelectColor, int coreMenuStrokeColor,
                            int coreMenuStrokeSize, double radiusDistance,Bitmap bitmap, OnClickListener onClickListener) {
        isPowerMenu = true;
        this.coreMenuColor = coreMenuColor;
        this.radiusDistance = radiusDistance;
        this.coreMenuSelectColor = coreMenuSelectColor;
        this.coreMenuStrokeColor = coreMenuStrokeColor;
        this.coreMenuStrokeSize = coreMenuStrokeSize;
        this.powerBitmap = bitmap;
        this.onPowerClickListener = onClickListener;
        invalidate();
    }

    public void setStopMenu(int coreMenuColor, int coreMenuSelectColor, int coreMenuStrokeColor,
                            int coreMenuStrokeSize, double radiusDistance,Bitmap bitmap, OnClickListener onClickListener) {
        isBackMenu = true;
        this.coreMenuColor = coreMenuColor;
        this.radiusDistance = radiusDistance;
        this.coreMenuSelectColor = coreMenuSelectColor;
        this.coreMenuStrokeColor = coreMenuStrokeColor;
        this.coreMenuStrokeSize = coreMenuStrokeSize;
        this.backBitmap = bitmap;
        this.onBackClickListener = onClickListener;
        invalidate();
    }

    /**
     * 获取两条线的夹角
     *
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    public static int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) //第二象限
        {
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }
        return (int) rotation;
    }

    /**
     * 求两个点之间的距离
     *
     * @return
     */
    public static double getDisForTwoSpot(float x1, float y1, float x2, float y2) {
        float width, height;
        if (x1 > x2) {
            width = x1 - x2;
        } else {
            width = x2 - x1;
        }

        if (y1 > y2) {
            height = y2 - y1;
        } else {
            height = y2 - y1;
        }
        return Math.sqrt((width * width) + (height * height));
    }

    /**
     * 扇形的对象类
     */
    public static class RoundMenu {
        public boolean useCenter = true;//扇形是否画连接中心点的直线
        public int solidColor = 0x000000ff;//背景颜色,默认透明
        public int selectSolidColor = 0x00000000;//背景颜色,默认透明
        public int strokeColor = 0x000000ff;//描边颜色,默认透明
        public int strokeSize = 2;//描边的宽度,默认1
        public Bitmap icon;//菜单的图片
        public OnClickListener onClickListener;//点击监听
        public  OnTouchListener onTouchListener;
        public double iconDistance = 0.75;//图标距离中心点的距离
    }

    /**
     * 中心圆的对象类
     */
    public static class CoreMenu {
        public boolean useCenter = true;//扇形是否画连接中心点的直线
        public int coreMenuColor;
        public int selectSolidColor = 0x00000000;//背景颜色,默认透明
        public int strokeColor = 0x000000ff;//描边颜色,默认透明
        public int strokeSize = 1;//描边的宽度,默认1
        public Bitmap icon;//菜单的图片
        public OnClickListener onClickListener;//点击监听
        public double iconDistance = 0.63;//图标距离中心点的距离
    }
}