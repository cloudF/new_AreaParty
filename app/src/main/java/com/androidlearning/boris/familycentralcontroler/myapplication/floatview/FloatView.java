package com.androidlearning.boris.familycentralcontroler.myapplication.floatview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.start.StartActivity;
import com.androidlearning.boris.familycentralcontroler.fragment05.accessible_service.AutoLoginService;
import com.androidlearning.boris.familycentralcontroler.fragment05.accessible_service.Util;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;
import com.androidlearning.boris.familycentralcontroler.utils_comman.netWork.NetUtil;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication.mFloatView;
import static com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication.mFloatView2;

/**
 * Created by zhuyulin on 2017/6/23.
 */

public class FloatView extends View {
    private final static String TAG = "FloatView";

    public static String name = "";
    public static String password = "";

    private Context mContext;
    private WindowManager wm;
    private static WindowManager.LayoutParams wmParams;
    public View mContentView;
    private float mRelativeX;
    private float mRelativeY;
    private float mScreenX;
    private float mScreenY;
    private float mScreenX1;
    private float mScreenY1;
    private int x = 0;
    private int y = 500;
    private boolean bShow = false;
    private int statusBarHeight = -1;
    private int titleBarHeight = -1;
    private boolean isDragModel = false;

    List<AccessibilityNodeInfo> node = new ArrayList<>();//用于遍历

    public FloatView(Context context) {
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

        setLayout(R.layout.float_static);
    }

    public void setLayout(int layout_id) {
        mContentView = LayoutInflater.from(mContext).inflate(layout_id, null);
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StartActivity.serviceEnabled){
                    login();
                }else {
                    Toasty.warning(getContext(), "请到设置>辅助功能>AreaParty打开自助登录服务").show();
                }
            }
        });
        mContentView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isDragModel = true;
                return true;
            }
        });
        mContentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (isDragModel){
                    //Log.w(TAG,event.getAction()+":"+mScreenX+"__"+mScreenY);
                    mScreenX = event.getRawX();
                    mScreenY = event.getRawY();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN://按住事件发生后执行代码的区域 0
                            mRelativeX = event.getX();
                            mRelativeY = event.getY();
                            //Log.w(TAG,mRelativeX+"__"+mRelativeY);
                            break;
                        case MotionEvent.ACTION_MOVE: //移动事件发生后执行代码的区域2
                            updateViewPosition1();
                            break;
                        case MotionEvent.ACTION_UP://1
                            updateViewPosition1();
                            mRelativeX = mRelativeY = 0;//松开事件发生后执行代码的区域
                            isDragModel = false;
                            break;
                    }
                }else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN://按住事件发生后执行代码的区域 0
                            mScreenX1 = event.getRawX();
                            mScreenY1 = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP://1
                            mScreenX = event.getRawX();
                            mScreenY = event.getRawY();
                            //Log.w(TAG,"UP:"+mScreenX+"__"+mScreenY);
                            if (Math.abs(mScreenX-mScreenX1) < 80 && (mScreenY1-mScreenY) > 100){
                                close();
                            }else if (Math.abs(mScreenX-mScreenX1) < 80 && (mScreenY-mScreenY1) > 100){
                                StartActivity.openPackage(getContext(), MyApplication.getContext().getPackageName());
                            }
                            mRelativeX = mRelativeY = 0;//松开事件发生后执行代码的区域
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
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
//            --TYPE_TOAST : 短暂通知ToastXiaomi23
            if (Build.MANUFACTURER.equals("Xiaomi") && Build.VERSION.SDK_INT >= 23){
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
        },10000);
    }
    public boolean isShouldClose(){
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return true;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return true;
        switch (root.getPackageName().toString()){
            case AutoLoginService.YOUKU:
            case AutoLoginService.TENCENT:
            case AutoLoginService.QQ:
            case AutoLoginService.LESHI:
                return false;
        }
        return true;
    }
    public void showAWhile() {
        if (mContentView != null) {
//            窗口的显示类型，常用的类型说明如下：
//            --TYPE_SYSTEM_ALERT : 系统警告提示。
//            --TYPE_SYSTEM_ERROR : 系统错误提示。
//            --TYPE_SYSTEM_OVERLAY : 页面顶层提示。
//            --TYPE_SYSTEM_DIALOG : 系统对话框。
//            --TYPE_STATUS_BAR : 状态栏
//            --TYPE_TOAST : 短暂通知Toast
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;

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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isShow()){
                        close();
                    }
                }
            },2000);
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

    public int getPositionX(){
        return wmParams.x + 8;
    }
    public int getPositionY(){
        return wmParams.y + statusBarHeight - 8;
    }

    public void  updateMousePointer(float X,float Y){
        x = x + (int)(1.5*X);if (x<0) x = 0;
        y = y + (int)(1.5*Y);if (y<0) y = 0;
    }

    private void login_iqiyi() {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toasty.error(getContext(),"服务器故障，未获取到账号").show();
            return;
        }
        if (AutoLoginService.state != AutoLoginService.IQIYI_LOGIN) return;
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;

        List<AccessibilityNodeInfo> nodeList_et_pwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/et_pwd");
        List<AccessibilityNodeInfo> nodeList_et_phone = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/et_phone");
        List<AccessibilityNodeInfo> nodeList_tv_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_login");
        List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/cb_show_passwd");
        if (nodeList_et_pwd.size() > 0 && nodeList_et_phone.size() > 0 && nodeList_tv_login.size() > 0 &&  nodeList_cb_show_passwd.size() >0){
            Log.w(TAG, "nodeList_et_pwd" + nodeList_et_pwd.size());
            Log.w(TAG, "nodeList_et_phone" + nodeList_et_phone.size());
            Log.w(TAG, "nodeList_tv_login" + nodeList_tv_login.size());
            Log.w(TAG, "nodeList_cb_show_passwd" + nodeList_cb_show_passwd.size());
            AccessibilityNodeInfo node_cb_show_passwd = nodeList_cb_show_passwd.get(0);
            AccessibilityNodeInfo node_et_phone = nodeList_et_phone.get(0);
            AccessibilityNodeInfo node_et_pwd = nodeList_et_pwd.get(0);
            AccessibilityNodeInfo node_tv_login = nodeList_tv_login.get(0);
            //设置密码显示状态为***
            boolean is = node_cb_show_passwd.isChecked();
            if (is){
                node_cb_show_passwd.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            Rect outBounds = new Rect();
            node_cb_show_passwd.getBoundsInScreen(outBounds);
            if (!mFloatView2.isShow()){
                mFloatView2.setPosition(outBounds.left, outBounds.top);
                mFloatView2.show();
            }

            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);
            node_et_phone.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            node_et_pwd.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            node_tv_login.performAction(AccessibilityNodeInfo.ACTION_CLICK);

//            AutoLoginService.state = AutoLoginService.NO_ACTION;
            name = "";
            password = "";

            if (isShow()) close();
        }else {
            Toasty.warning(getContext(),"请切换到账号密码登录界面").show();
            Log.w(TAG, "null");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void login_leshi() {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toasty.error(getContext(),"服务器故障，未获取到账号").show();
            return;
        }
        if (AutoLoginService.state != AutoLoginService.LESHI_LOGIN) return;
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;

        List<AccessibilityNodeInfo> nodeList_et_pwd = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/password_edittext");
        List<AccessibilityNodeInfo> nodeList_et_phone = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/account_edittext");
        List<AccessibilityNodeInfo> nodeList_tv_login = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/login_button");
        List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/plaintext_imageview");
        if (nodeList_et_pwd.size() > 0 && nodeList_et_phone.size() > 0 && nodeList_tv_login.size() > 0 &&  nodeList_cb_show_passwd.size() >0){
            AccessibilityNodeInfo node_cb_show_passwd = nodeList_cb_show_passwd.get(0);
            AccessibilityNodeInfo node_et_phone = nodeList_et_phone.get(0);
            AccessibilityNodeInfo node_et_pwd = nodeList_et_pwd.get(0);
            AccessibilityNodeInfo node_tv_login = nodeList_tv_login.get(0);
            /*//设置密码显示状态为***
            boolean is = node_cb_show_passwd.isChecked();
            if (is){
                node_cb_show_passwd.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            Rect outBounds = new Rect();
            node_cb_show_passwd.getBoundsInScreen(outBounds);
            if (!mFloatView2.isShow()){
                mFloatView2.setPosition(outBounds.left, outBounds.top);
                mFloatView2.show();
            }*/

            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);
            node_et_phone.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            node_et_pwd.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            node_tv_login.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            AutoLoginService.state = AutoLoginService.NO_ACTION;

            Util.setRecord(MyApplication.getContext(),AutoLoginService.LESHI);
            StartActivity.logined = AutoLoginService.LESHI;
            name = "";
            password = "";

            if (isShow()) close();
        }else {
            Toasty.warning(getContext(),"请切换到账号密码登录界面").show();
            Log.w(TAG, "null");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void login_youku() {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toasty.error(getContext(),"服务器故障，未获取到账号").show();
            return;
        };
        if (AutoLoginService.state != AutoLoginService.YOUKU_LOGIN) return;
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;

        List<AccessibilityNodeInfo> nodeList_userName = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_username");
        List<AccessibilityNodeInfo> nodeList_pwd = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_password");
        List<AccessibilityNodeInfo> nodeList_login = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_login");

        if (nodeList_userName.size()>0 && nodeList_pwd.size()>0 && nodeList_login.size()>0){
            AccessibilityNodeInfo node_userName = nodeList_userName.get(0);
            AccessibilityNodeInfo node_pwd = nodeList_pwd.get(0);
            AccessibilityNodeInfo node_login = nodeList_login.get(0);

            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);
            node_userName.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            node_pwd.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            node_login.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            AutoLoginService.state = AutoLoginService.NO_ACTION;
            Util.setRecord(MyApplication.getContext(),AutoLoginService.YOUKU);
            StartActivity.logined = AutoLoginService.YOUKU;
            name = "";
            password = "";
            if (isShow()) close();
        }else {

            Toasty.warning(getContext(), "请切换到账号密码登录界面").show();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void login_tencent(){
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toasty.error(getContext(),"服务器故障，未获取到账号").show();
            return;
        };
        if (AutoLoginService.state != AutoLoginService.TENCENT_LOGIN) return;
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;

        List<AccessibilityNodeInfo> nodeInfoList_qq = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/account");
        List<AccessibilityNodeInfo> nodeInfoList_pwd = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/password");
        List<AccessibilityNodeInfo> nodeInfoList_login = root.findAccessibilityNodeInfosByText("登录");
        if (nodeInfoList_qq != null && nodeInfoList_pwd != null && nodeInfoList_login!=null){
            Log.w(TAG,nodeInfoList_qq.size()+"//");
            Log.w(TAG,nodeInfoList_pwd.size()+"//");
            Log.w(TAG,nodeInfoList_login.size()+"//");
            if (nodeInfoList_qq.size()>0 && nodeInfoList_pwd.size()>0 && nodeInfoList_login.size() >1){
                AccessibilityNodeInfo qq = nodeInfoList_qq.get(0);
                AccessibilityNodeInfo pwd = nodeInfoList_pwd.get(0);
                AccessibilityNodeInfo login = nodeInfoList_login.get(1);

                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);
                qq.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
                pwd.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                login.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                AutoLoginService.state = AutoLoginService.NO_ACTION;
                Util.setRecord(MyApplication.getContext(),AutoLoginService.TENCENT,FloatView.name);
                StartActivity.logined = AutoLoginService.TENCENT;
                name = "";
                password = "";
                if (isShow()) close();
            }else {
                Toasty.warning(getContext(), "请切换到账号密码登录界面").show();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void login_tencent1(){
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toasty.error(getContext(),"服务器故障，未获取到账号").show();
            return;
        };
        if (AutoLoginService.state != AutoLoginService.TENCENT_LOGIN) return;

        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;

        bianli(root);
        if (node.size() == 8 && node.get(3).getViewIdResourceName().equals("u") && node.get(4).getViewIdResourceName().equals("p") && node.get(5).getViewIdResourceName().equals("go")){
            AccessibilityNodeInfo qq = node.get(3);
            AccessibilityNodeInfo pwd = node.get(4);
            AccessibilityNodeInfo login = node.get(5);

            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name);
            qq.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            pwd.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            login.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            AutoLoginService.state = AutoLoginService.NO_ACTION;
            Util.setRecord(MyApplication.getContext(),AutoLoginService.TENCENT,FloatView.name);
            StartActivity.logined = AutoLoginService.TENCENT;
            name = "";
            password = "";
            if (isShow()) close();
        }else {
            Toasty.warning(getContext(), "请切换到账号密码登录界面").show();
        }
        node.clear();
    }

    private void check_iqiyi(){
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;
        if (!root.getPackageName().toString().equals(AutoLoginService.IQIYI)) return;
        List<AccessibilityNodeInfo> nodeList_navi3 = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/navi3");
        List<AccessibilityNodeInfo> nodeList_head = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_main_head_layout");
        List<AccessibilityNodeInfo> nodeList_main_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/my_main_login");
        if (!nodeList_navi3.isEmpty() && nodeList_head.isEmpty()){
            nodeList_navi3.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    check_iqiyi();
                }
            },500);
        }else if (!nodeList_navi3.isEmpty() && !nodeList_head.isEmpty()){
            if (!nodeList_main_login.isEmpty()){
                Toasty.success(getContext(),"AreaParty:爱奇艺已退出登录").show();
                AutoLoginService.state = AutoLoginService.NO_ACTION;
                StartActivity.logoutVip("iqiyi");
                if (mFloatView.isShow()) mFloatView.close();
            }else {
                Toasty.info(getContext(),"你未退出登录，请执行退出登录操作").show();
            }
        }
    }
    private void check_leshi(){
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;
        if (!root.getPackageName().toString().equals(AutoLoginService.LESHI)) return;
        List<AccessibilityNodeInfo> nodeList_navi = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/main_my_radio");
        if (!nodeList_navi.isEmpty()){
            AccessibilityNodeInfo node = nodeList_navi.get(0).getParent();
            List<AccessibilityNodeInfo> nodeInfoList = node.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/main_bottom_navigation_item_text");
            if (nodeInfoList.size() > 0){
                AccessibilityNodeInfo nodeText = nodeInfoList.get(0);
                if (nodeText != null && nodeText.getText()!=null){
                    if (nodeText.getText().toString().equals("未登录")){
                        Toasty.success(getContext(),"AreaParty:乐视视频已退出登录").show();
                        AutoLoginService.state = AutoLoginService.NO_ACTION;
                        StartActivity.logoutVip("iqiyi");
                        if (mFloatView.isShow()) mFloatView.close();
                    }else if (nodeText.getText().toString().equals("我的")){
                        Toasty.info(getContext(),"你未退出登录，请执行退出登录操作").show();
                    }
                }
            }
        }/*else if (!nodeList_navi.isEmpty() && !nodeList_head.isEmpty()){
            if (!nodeList_main_login.isEmpty()){
                Toasty.success(getContext(),"AreaParty:爱奇艺已退出登录").show();
                AutoLoginService.state = AutoLoginService.NO_ACTION;
                StartActivity.logoutVip("iqiyi");
                if (mFloatView.isShow()) mFloatView.close();
            }else {

            }
        }*/
    }

    private void check_youku(){
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;
        if (!root.getPackageName().toString().equals(AutoLoginService.YOUKU)) return;
        List<AccessibilityNodeInfo> nodeList_user = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/layout_user");
        if (!nodeList_user.isEmpty() && !nodeList_user.get(0).isSelected()){
            nodeList_user.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    check_youku();
                }
            },500);
        }else if (!nodeList_user.isEmpty() && nodeList_user.get(0).isSelected()){
            if (bianli1(root)){
                Toasty.success(getContext(),"AreaParty:优酷已退出登录").show();
                AutoLoginService.state = AutoLoginService.NO_ACTION;
                StartActivity.logoutVip("youku");
                if (mFloatView.isShow()) mFloatView.close();
            }else {
                Toasty.info(getContext(),"你未退出登录，请执行退出登录操作").show();
            }
        }
    }

    private void check_tencent1(){
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;
        List<AccessibilityNodeInfo> nodeList_LoginTxt = root.findAccessibilityNodeInfosByViewId("com.tencent.qqlive:id/login_text");
        if (!nodeList_LoginTxt.isEmpty()){
            if (nodeList_LoginTxt.get(0).getText().toString().equals("点击登录")){
                if (StartActivity.QQVersionCode == 0 || TextUtils.isEmpty(Util.getRecordId(getContext()))){
                    Toasty.success(getContext(),"AreaParty:腾讯视频已退出登录").show();
                    AutoLoginService.state = AutoLoginService.NO_ACTION;
                    StartActivity.logoutVip("tencent");
                    if (mFloatView.isShow()) mFloatView.close();
                }else {
                    Toasty.success(getContext(),"AreaParty:由于你安装了手机QQ,你还需要在登录界面进行检测来清除使用记录", Toast.LENGTH_LONG).show();
                }
            }else {
                Toasty.info(getContext(),"你未退出登录，请执行退出登录操作").show();
            }
        }
    }
    private void check_tencent(){

        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/ivTitleName");
        if (!nodeList.isEmpty()){
            String record = Util.getRecordId(getContext());
            if (TextUtils.isEmpty(record)){
                Toasty.success(getContext(),"AreaParty:腾讯视频退出登录").show();
                AutoLoginService.state = AutoLoginService.NO_ACTION;
                StartActivity.logoutVip("tencent");
                if (mFloatView.isShow()) mFloatView.close();
            }
            node.clear();
            bianli2(root);
            Log.w(TAG,node.size()+"》》》"+record);
            if (node.size() == 5 || node.size()==6){
                AccessibilityNodeInfo nodeInfo = getFirstImageViewNodeInfo(node);
                if (nodeInfo!= null && nodeInfo.isClickable()){
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            check_tencent();
                        }
                    },500);
                }
            }else if (node.size() > 6){
                for (int i = 4; i< node.size(); i++){
                    AccessibilityNodeInfo nodeInfo = node.get(i);
                    if (nodeInfo.getClassName().toString().equals("android.widget.TextView")){
                        if (nodeInfo.getText()!=null && nodeInfo.getText().toString().equals(record)){
                            node.get(i+1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return;
                        }
                    }
                }
                Toasty.success(getContext(),"AreaParty:腾讯视频退出登录").show();
                AutoLoginService.state = AutoLoginService.NO_ACTION;
                StartActivity.logoutVip("tencent");
                if (mFloatView.isShow()) mFloatView.close();
            }
            //com.tencent.mobileqq:id/dialogText
            //com.tencent.mobileqq:id/dialogRightBtn
        }
    }

    private void login(){
        AutoLoginService autoLoginService = AutoLoginService.getInstance();
        if (autoLoginService == null) return;
        AccessibilityNodeInfo root = autoLoginService.getRootInActiveWindow();
        if (root == null) return;
        if (NetUtil.getNetWorkState(getContext()) == NetUtil.NETWORK_NONE){
            Toasty.error(getContext(), "当前网络不可用").show();
            return;
        }
        switch (root.getPackageName().toString()) {
            case AutoLoginService.IQIYI:
                if (AutoLoginService.state == AutoLoginService.IQIYI_LOGIN){
                    login_iqiyi();
                }else if (AutoLoginService.state == AutoLoginService.IQIYI_LOGOUT){
                    check_iqiyi();
                }

                break;
            case AutoLoginService.LESHI:
                if (AutoLoginService.state == AutoLoginService.LESHI_LOGIN){
                    login_leshi();
                }else if (AutoLoginService.state == AutoLoginService.LESHI_LOGOUT){
                    check_leshi();
                }

                break;
            case AutoLoginService.YOUKU:
                if (AutoLoginService.state == AutoLoginService.YOUKU_LOGIN){
                    login_youku();
                }else if (AutoLoginService.state == AutoLoginService.YOUKU_LOGOUT){
                    check_youku();
                }
                break;
            case AutoLoginService.TENCENT:
                if (AutoLoginService.state == AutoLoginService.TENCENT_LOGIN){
                    login_tencent1();
                }else if (AutoLoginService.state == AutoLoginService.TENCENT_LOGOUT){
                    check_tencent1();
                }
                break;
            case AutoLoginService.QQ:
                if (AutoLoginService.state == AutoLoginService.TENCENT_LOGIN){
                    login_tencent();
                }else if (AutoLoginService.state == AutoLoginService.TENCENT_LOGOUT){
                    check_tencent();
                }
                break;
            default:
//                Toasty.warning(getContext(), "请在视频网站登录界面尝试该功能").show();
                break;
        }
    }
    public void bianli(AccessibilityNodeInfo nodeInfo){//递归遍历根节点找所需节点
        if (nodeInfo == null) return;
        if (nodeInfo.getChildCount() == 0){
            node.add(nodeInfo);
        }else {
            for (int i = 0; i<nodeInfo.getChildCount(); i++){
                bianli(nodeInfo.getChild(i));
            }
        }
    }
    public boolean bianli1(AccessibilityNodeInfo nodeInfo){//递归遍历根节点找所需节点,优酷使用
        if (nodeInfo == null) return false;
        if (nodeInfo.getChildCount() == 0){
            if ((nodeInfo.getText()+"").equals("登录/注册")){//检测到为未登录状态
                return true;
            }
            return false;
        }else {
            for (int i = 0; i<nodeInfo.getChildCount(); i++){
                if (bianli1(nodeInfo.getChild(i))){
                    return true;
                }
            }
        }
        return false;
    }
    public void bianli2(AccessibilityNodeInfo nodeInfo){//递归遍历根节点找所需节点
        if (nodeInfo == null) return;
        if (nodeInfo.getChildCount() == 0){
            if (nodeInfo.getViewIdResourceName().equals("com.tencent.mobileqq:id/name")){
                node.add(nodeInfo);
                Log.w(TAG,nodeInfo.toString());
            }

        }else {
            for (int i = 0; i<nodeInfo.getChildCount(); i++){
                bianli2(nodeInfo.getChild(i));
            }
        }
    }
    public AccessibilityNodeInfo getFirstImageViewNodeInfo(List<AccessibilityNodeInfo> nodeInfoList){
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList){
            if (nodeInfo.getClassName().equals("android.widget.ImageView")){
                return nodeInfo;
            }
        }
        return null;
    }
}
