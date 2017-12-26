package com.dkzy.areaparty.phone.bluetoothxie;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 悬浮球管理类
 */
public class FloatManager {
    private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    private FloatView floatView = null;
    private boolean isDisplay = false;
    private Context context;
    private PreferebceManager mPreferenceManager;
    private boolean isRight;
    public static int width;
    private PopupWindow popupWindow;
    boolean mousePointMain = true;  //标识鼠标指针在主屏还是扩展屏

    private static FloatManager instance = null;

    public static FloatManager getFloatManager(Context context) {
        if (instance == null) {
            instance = new FloatManager(context);
        }
        return instance;
    }

    private FloatManager(Context context) {
        this.context = context;
        mPreferenceManager = new PreferebceManager(context);

    }

    public WindowManager.LayoutParams getWindowParams() {
        return windowParams;
    }

    public void removeView() {
        if (!isDisplay)
            return;
        if (floatView != null) {
            floatView.cancelTimerCount();
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(floatView);
        floatView = null;
        isDisplay = false;

    }

    public void createView() {
        if (isDisplay)
            return;
        floatView = new FloatView(context, windowParams);
        floatView.setOnClickListener(floatViewClick);
        floatView.setOnMoveListener(new FloatView.OnMoveListener() {
            @Override
            public void onMove() {
                dismisPopupWindow();
                cancelPopUpWindowTimerCount();
            }
        });
        // 获取WindowManager
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(floatView, windowParams);
        isDisplay = true;
    }

    private OnClickListener floatViewClick = new OnClickListener() {

        public void onClick(View v) {


            isRight = mPreferenceManager.isDisplayRight();
            View popupView = View.inflate(context, Utils.getLayout(context, "float_window_big"), null);
            /*RelativeLayout centerlayout = (RelativeLayout) popupView.findViewById(Utils.getId(context, "center"));
            RelativeLayout giftlayout = (RelativeLayout) popupView.findViewById(Utils.getId(context, "gift"));
            RelativeLayout paylayout = (RelativeLayout) popupView.findViewById(Utils.getId(context, "pay"));*/
            RelativeLayout eventlayout = (RelativeLayout) popupView.findViewById(Utils.getId(context, "event"));
            RelativeLayout switchlayout = (RelativeLayout) popupView.findViewById(Utils.getId(context, "switch"));
//            RelativeLayout bluetoothlayout = (RelativeLayout) popupView.findViewById(Utils.getId(context, "bigbluetooth"));
//            View redView = (ImageView) popupView.findViewById(Utils.getId(context, "redview"));

            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                floatView.startTimerCount(3000);
                cancelPopUpWindowTimerCount();
                //floatView.setImageResource(Utils.getDrawable(context,"zhangyu"));
            } else {
                floatView.cancelTimerCount();

                //floatView.setImageResource(Utils.getDrawable(context,"zhangyu_selected"));
                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				/*popupWindow.setFocusable(true);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());*/
                popupWindow.setClippingEnabled(false);
                //popupWindow.showAsDropDown(v);
                if (isRight) {
//                    popupView.setBackgroundResource(Utils.getDrawable(context, "dropzonebg"));
                    popupView.setBackgroundResource(context.getResources().getIdentifier("dropzone_background", "drawable", context.getPackageName()));
                    popupWindow.showAtLocation(floatView, Gravity.RIGHT, floatView.getWidth(), 0);
                } else {
                    popupView.setPadding(Utils.dip2px(context, 10), 0, 0, 0);
//                    popupView.setBackgroundResource(Utils.getDrawable(context, "dropzonebg_left"));
                    popupView.setBackgroundResource(context.getResources().getIdentifier("dropzone_background_left", "drawable", context.getPackageName()));
                    popupWindow.showAtLocation(floatView, Gravity.LEFT, floatView.getWidth(), 0);
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                /*boolean clickRedPoint = false;
                if (!clickRedPoint) {
                    redView.setVisibility(View.VISIBLE);
                } else {
                    redView.setVisibility(View.GONE);
                }*/

               /* centerlayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //个人中心
                        Toast.makeText(context, "个人中心", Toast.LENGTH_SHORT).show();
                        dismisPopupWindow();

                    }
                });
                giftlayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "礼包", Toast.LENGTH_SHORT).show();
                        dismisPopupWindow();
                    }
                });
                paylayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Toast.makeText(context, "充值中心", Toast.LENGTH_SHORT).show();
                        dismisPopupWindow();

                    }
                });*/
                switchlayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mousePointMain){
                            BlueMousetListener.setOffset(width);
                            mousePointMain = false;
                        }else {
                            BlueMousetListener.setOffset(0);
                            mousePointMain = true;
                        }
                        dismisPopupWindow();
                    }
                });
                /*bluetoothlayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PCBluetoothSet.actionStart(context,1);
                        FloatManager.getFloatManager(context).destroyFloat();
                        dismisPopupWindow();
                    }
                });*/
                eventlayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
//                        Toast.makeText(context, "活动", Toast.LENGTH_SHORT).show();
                        /*new Thread () {
                            public void run () {
                                try {
                                    Instrumentation inst= new Instrumentation();
                                    inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();*/
                        ((Activity)context).finish();
                        dismisPopupWindow();
                    }
                });


                startPopUpWindowTimerCount();
            }

        }
    };


    private void dismisPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private Timer popUpWindowTimer;
    //定时器取消
    private boolean isCancelpopUpWindow;
    private TimerTask popUpWindowTimerTask;
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    dismisPopupWindow();
                    if (floatView != null) {
                        floatView.startTimerCount(3000);
                    }

                    break;
            }
        }

        ;
    };

    public void startPopUpWindowTimerCount() {
        isCancelpopUpWindow = false;
        popUpWindowTimer = new Timer();
        popUpWindowTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isCancelpopUpWindow) {
                    handler.sendEmptyMessage(0);
                }
            }
        };
        popUpWindowTimer.schedule(popUpWindowTimerTask, 3000);
    }

    public void cancelPopUpWindowTimerCount() {
        isCancelpopUpWindow = true;
        if (popUpWindowTimer != null) {
            popUpWindowTimer.cancel();
            popUpWindowTimer = null;
        }
        if (popUpWindowTimerTask != null) {
            popUpWindowTimerTask.cancel();
            popUpWindowTimerTask = null;
        }
    }

    /**
     * 程序进入后台或者退出事调用
     */
    public void cancelTimerCount() {
        if (floatView != null) {
            floatView.cancelTimerCount();
        }
    }
    public static void setWidth(int i){
        width = i;
    }

    public void destroyFloat() {
        if (!isDisplay)
            return;
        if (floatView != null) {
            floatView.cancelTimerCount();
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(floatView);
        windowManager = null;
        floatView = null;
        floatViewClick = null;
        isDisplay = false;
        instance = null;
    }

}
