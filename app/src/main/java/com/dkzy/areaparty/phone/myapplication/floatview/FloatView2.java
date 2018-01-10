package com.dkzy.areaparty.phone.myapplication.floatview;

import android.app.AppOpsManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment05.accessible_service.AutoLoginService;

import java.lang.reflect.Method;
import java.util.List;

import static com.dkzy.areaparty.phone.myapplication.floatview.FloatView.checkAlertWindowsPermission;

/**
 * Created by zhuyulin on 2017/6/23.
 */

public class FloatView2 extends View {
    private final static String TAG = "FloatView";

    private Context mContext;
    private WindowManager wm;
    private static WindowManager.LayoutParams wmParams;
    public View mContentView;
    private float mRelativeX;
    private float mRelativeY;
    private float mScreenX;
    private float mScreenY;
    private int x = 0;
    private int y = 500;
    private boolean bShow = false;
    private int statusBarHeight = -1;
    private int titleBarHeight = -1;
    private boolean isDragModel = false;

    public FloatView2(Context context) {
        super(context);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wmParams == null) {
            wmParams = new WindowManager.LayoutParams();
        }
        mContext = context;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            Log.w("statusBarHeight", statusBarHeight+"");
        }

        setLayout(R.layout.float_static2);
    }

    public void setLayout(int layout_id) {
        mContentView = LayoutInflater.from(mContext).inflate(layout_id, null);
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "点击", Toast.LENGTH_SHORT).show();
            }
        });
//        mContentView.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                isDragModel = true;
//                Toast.makeText(getContext(), "长按", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
//        mContentView.setOnTouchListener(new OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                if (isDragModel){
//                    //Log.w(TAG,event.getAction()+":"+mScreenX+"__"+mScreenY);
//                    mScreenX = event.getRawX();
//                    mScreenY = event.getRawY();
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN://按住事件发生后执行代码的区域 0
//                            mRelativeX = event.getX();
//                            mRelativeY = event.getY();
//                            //Log.w(TAG,mRelativeX+"__"+mRelativeY);
//                            break;
//                        case MotionEvent.ACTION_MOVE: //移动事件发生后执行代码的区域2
//                            updateViewPosition1();
//                            break;
//                        case MotionEvent.ACTION_UP://1
//                            updateViewPosition1();
//                            mRelativeX = mRelativeY = 0;//松开事件发生后执行代码的区域
//                            isDragModel = false;
//                            break;
//                    }
//                }
//                return false;
//            }
//        });
    }

    private void updateViewPosition1() {
        wmParams.x = (int) (mScreenX - mRelativeX);
        x = wmParams.x;
        wmParams.y = (int) (mScreenY - mRelativeY - statusBarHeight);
        y = wmParams.y;
        wm.updateViewLayout(mContentView, wmParams);
    }
    public void updateViewPosition(int mScreenX,int mScreenY) {
        wmParams.x = (int)(1.5*mScreenX) + x;
        wmParams.y = (int)(1.5*mScreenY) + y;

        wm.updateViewLayout(mContentView, wmParams);
    }

    public void show() {
        if (mContentView != null) {
//            窗口的显示类型，常用的类型说明如下：
//            --TYPE_SYSTEM_ALERT : 系统警告提示。
//            --TYPE_SYSTEM_ERROR : 系统错误提示。
//            --TYPE_SYSTEM_OVERLAY : 页面顶层提示。
//            --TYPE_SYSTEM_DIALOG : 系统对话框。
//            --TYPE_STATUS_BAR : 状态栏
//            --TYPE_TOAST : 短暂通知Toast
            if ((Build.MANUFACTURER.equals("Xiaomi") && Build.VERSION.SDK_INT >= 23) || Build.VERSION.SDK_INT >= 25 || checkAlertWindowsPermission(getContext())){
                wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }

            wmParams.format = PixelFormat.RGBA_8888;//窗口的像素点格式
//            flags : 窗口的行为准则，常用的标志位如下说明（对于悬浮窗来说，一般只需设置FLAG_NOT_FOCUSABLE）：
//            --FLAG_NOT_FOCUSABLE : 不能抢占焦点，即不接受任何按键或按钮事件。
//            --FLAG_NOT_TOUCHABLE : 不接受触摸屏事件。悬浮窗一般不设置该标志，因为一旦设置该标志，将无法拖动悬浮窗。
//            --FLAG_NOT_TOUCH_MODAL : 当窗口允许获得焦点时（即没有设置FLAG_NOT_FOCUSALBE标志），仍然将窗口之外的按键事件发送给后面的窗口处理。否则它将独占所有的按键事件，而不管它们是不是发生在窗口范围之内。
//            -- :
//            --FLAG_LAYOUT_IN_SCREEN : 允许窗口占满整个屏幕。
//            --FLAG_LAYOUT_NO_LIMITS : 允许窗口扩展到屏幕之外。
//            --FLAG_WATCH_OUTSIDE_TOUCH : 如果设置了FLAG_NOT_TOUCH_MODAL标志，则当按键动作发生在窗口之外时，将接收到一个MotionEvent.ACTION_OUTSIDE事件。
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.alpha = 1.0f;
            wmParams.gravity = Gravity.START| Gravity.TOP;
            wmParams.x = x;
            wmParams.y = y;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 显示自定义悬浮窗口
            wm.addView(mContentView, wmParams);
            bShow = true;
            //5秒后自动关闭悬浮窗
            checkToClose();
        }
    }
    public void checkToClose(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShouldClose()){
                    if (isShow()){
                        close();
                    }
                }else {
                    checkToClose();
                }
            }
        },5000);
    }
    public boolean isShouldClose(){
        if (AutoLoginService.state != AutoLoginService.LESHI_LOGIN) return true;
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return true;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return true;
        if (!root.getPackageName().toString().equals(AutoLoginService.LESHI)) return true;
        List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/plaintext_imageview");
        if (nodeList_cb_show_passwd.size() > 0) {
            return false;
        }else {
            return true;
        }

    }

    public void close() {
        if (mContentView != null) {
            wm.removeView(mContentView);
            bShow = false;
        }
    }

    public boolean isShow() {
        return bShow;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y - statusBarHeight;
    }

    /**
     * 判断 悬浮窗口权限是否打开
     * @param context
     * @return true 允许  false禁止
     */

}
