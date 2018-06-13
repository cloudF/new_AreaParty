package com.dkzy.areaparty.phone.fragment05.accessible_service;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.StartActivity;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.floatview.FloatView;
import com.dkzy.areaparty.phone.utilseverywhere.tools.AccessibilityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import protocol.Msg.VipMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.mFloatView;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.mFloatView2;
import static com.dkzy.areaparty.phone.myapplication.floatview.FloatView.allocationId;

/**
 * Created by zhuyulin on 2017/7/13.
 */

public class AutoLoginService extends AccessibilityService {
    private AccessibilityHelper helper;

    public String t1 = "";
    public String t2 = "";

    public static final String IQIYI = "com.qiyi.video";
    public static final String YOUKU = "com.youku.phone";
    public static final String TENCENT = "com.tencent.qqlive";
    public static final String QQ = "com.tencent.mobileqq";
    public static final String LESHI = "com.letv.android.client";


    public static final int NO_ACTION = 0;
    public static final int IQIYI_LOGIN = 1;
    public static final int IQIYI_LOGIN_TEST = -1;
    public static final int IQIYI_LASTCHECT = 11;
    public static final int IQIYI_LASTCHECT_TEST = -11;
    public static final int IQIYI_LOGOUT = 2;
    public static final int YOUKU_LOGIN = 3;
    public static final int YOUKU_LOGIN_TEST = -3;
    public static final int YOUKU_LASTCHECK = 31;
    public static final int YOUKU_LASTCHECK_TEST = -31;
    public static final int YOUKU_YZM = 32;
    public static final int YOUKU_YZM_WAIT = 33;
    public static final int YOUKU_LOGOUT = 4;
    public static final int TENCENT_LOGIN = 5;
    public static final int TENCENT_LOGIN_TEST = -5;
    public static final int TENCENT_LASTCHECK = 51;
    public static final int TENCENT_LASTCHECK_TEST = -51;
    public static final int TENCENT_LOGOUT = 6;
    public static final int LESHI_LOGIN = 7;
    public static final int LESHI_LOGOUT = 8;
    public static final int IQIYI_YZM = 9;//向账号提供者请求爱奇艺验证码
    public static final int IQIYI_YZM_WAIT = 10;//等待爱奇艺验证码
    public static int state = NO_ACTION;

    public static AutoLoginService inatance;
    public static AccessibilityNodeInfo eye;


    private String TAG = getClass().getSimpleName();

    List<AccessibilityNodeInfo> node = new ArrayList<>();//用于遍历

    int count = 0;

    public static AutoLoginService getInstance(){
        return inatance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        helper = new AccessibilityHelper(this);

        if (inatance == null){inatance = this;}

        state = NO_ACTION;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    getNodeIds();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }

    public  void getNodeIds(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+ IPAddressConst.statisticServer_ip+"/bt_website/nodeIds.json").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                //Log.w("nodeIds",responseData);
                try {
                    JSONObject object = new JSONObject(responseData);
                    JSONObject jsonObject = object.getJSONObject("tencent");
                    t1 = jsonObject.getString("t1");
                    //Log.w("nodeIds",t1);
                    t2 = jsonObject.getString("t2");
                    //Log.w("nodeIds",t2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        switch(event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                //Log.w(TAG+"CHANGED", event.getPackageName().toString());
                switch (state){
                    case IQIYI_LOGIN:
                    case IQIYI_LOGIN_TEST:
                        if (event.getPackageName().toString().equals(IQIYI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_et_pwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/et_pwd");
                            List<AccessibilityNodeInfo> nodeList_et_phone = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/et_phone");
                            List<AccessibilityNodeInfo> nodeList_tv_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_login");
                            List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/cb_show_passwd");
                            if (nodeList_et_pwd.size() > 0 && nodeList_et_phone.size() > 0 && nodeList_tv_login.size() > 0 &&  nodeList_cb_show_passwd.size() >0){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }else {
                                if (mFloatView.isShow()) mFloatView.close();
                            }
                        }
                        break;
                    case IQIYI_YZM:
                        if (event.getPackageName().toString().equals(IQIYI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo e1 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block1");
                            if (e1!=null){//在等待验证码阶段，发送获取请求
                                state = IQIYI_YZM_WAIT;
                                sendYZMRequest();
                            }
                        }
                       // break;
                    case IQIYI_YZM_WAIT:
                    case IQIYI_LASTCHECT:
                        if (event.getPackageName().toString().equals(IQIYI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_navi3 = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/navi3");
                            List<AccessibilityNodeInfo> nodeList_head = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_main_head_layout");
                            List<AccessibilityNodeInfo> nodeList_main_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/my_main_login");
                            List<AccessibilityNodeInfo> vip_status_opened = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/vip_status_opened");
                            List<AccessibilityNodeInfo> vip_status = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/vip_status");
                            if (!nodeList_navi3.isEmpty() && !nodeList_head.isEmpty()){
                                if (!nodeList_main_login.isEmpty()){
                                    if (count < 1){
                                        count ++;
                                    }else {
                                        if (count > 1){
                                            return;
                                        }
                                        count ++;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.w(TAG,"maybe确定登录失11败"+count);
                                                if (state == IQIYI_LASTCHECT || state == IQIYI_YZM){
                                                    state = NO_ACTION;
                                                    Log.w(TAG,"等待2s后再次确定登录失败");
                                                    count = 0;
                                                    sendLoginInfo5();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MyApplication.getInstance().showNotification("AreaParty:登录过程发生故障","请尝试重新执行登录过程，若多次尝试仍不能登录成功，可申诉退款");
                                                        }
                                                    }).start();
                                                }
                                            }
                                        }, 2000);
                                    }
                                }else {
                                    //登录成功
                                    state = NO_ACTION;
                                    if (vip_status_opened.isEmpty()){
                                        if (!vip_status.isEmpty()){
                                            if (vip_status.get(0).getText().toString().equals("查看")){
                                                Log.w(TAG,"IQIYI登录成功,是vip账号");
                                                sendLoginInfo3();
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MyApplication.getInstance().showNotification("AreaParty:登录成功","爱奇艺VIP账号登录成功");
                                                    }
                                                }).start();
                                            }else {
                                                Log.w(TAG,"IQIYI登录成功,但不是vip账号");
                                                sendLoginInfo4();
                                            }
                                        }else {

                                        }
                                    }else {
                                        Log.w(TAG,"IQIYI登录成功,是vip账号");
                                        sendLoginInfo3();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录成功","爱奇艺VIP账号登录成功");
                                            }
                                        }).start();
                                    }
                                }

                            }else {
                                count = 0;
                            }
                        }
                        break;
                    case IQIYI_LASTCHECT_TEST:
                        if (event.getPackageName().toString().equals(IQIYI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_navi3 = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/navi3");
                            List<AccessibilityNodeInfo> nodeList_head = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_main_head_layout");
                            List<AccessibilityNodeInfo> nodeList_main_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/my_main_login");
                            List<AccessibilityNodeInfo> vip_status_opened = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/vip_status_opened");
                            List<AccessibilityNodeInfo> vip_status = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/vip_status");

                            if (!nodeList_navi3.isEmpty() && !nodeList_head.isEmpty()){
                                if (!nodeList_main_login.isEmpty()){
                                    if (count < 1){
                                        count ++;
                                    }else {
                                        if (count > 1){
                                            return;
                                        }
                                        count ++;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.w(TAG,"maybe确定登录失11败"+count);
                                                if (state == IQIYI_LASTCHECT_TEST){
                                                    state = NO_ACTION;
                                                    Log.w(TAG,"等待2s后再次确定登录失败");
                                                    count = 0;
                                                    EventBus.getDefault().post(0,"iqiyiLoginTest");//密码验证未通过
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到爱奇艺账号未登录成功，暂不能出租");
                                                        }
                                                    }).start();
                                                }
                                            }
                                        }, 2000);
                                    }
                                }else {
                                    //登录成功
                                    state = NO_ACTION;
                                    if (vip_status_opened.isEmpty()){
                                        if (!vip_status.isEmpty()){
                                            if (vip_status.get(0).getText().toString().equals("查看")){
                                                EventBus.getDefault().post(2,"iqiyiLoginTest");//密码验证通过，且是vip账号
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到爱奇艺VIP账号登录成功，可以出租");
                                                    }
                                                }).start();
                                            }else {
                                                EventBus.getDefault().post(1,"iqiyiLoginTest");//密码验证通过，但不是vip账号
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到爱奇艺账号登录成功，但未开通VIP会员，暂不能出租");
                                                    }
                                                }).start();
                                            }
                                        }
                                    }else {
                                        EventBus.getDefault().post(2,"iqiyiLoginTest");//密码验证通过，且是vip账号
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到爱奇艺VIP账号登录成功，可以出租");
                                            }
                                        }).start();
                                    }
                                }

                            }else {
                                count = 0;
                            }
                        }
                        break;
                    case IQIYI_LOGOUT:
                        if (event.getPackageName().toString().equals(IQIYI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_right");
                            if (nodeInfoList.size() > 0 ){
                                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                                if (nodeInfo.getText().toString().equals("退出登录") && nodeInfo.isClickable()){
                                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Log.w(TAG,"AreaParty:爱奇艺退出登录");
                                    Toasty.success(getApplicationContext(),"AreaParty:爱奇艺退出登录").show();
                                    state = NO_ACTION;
                                    StartActivity.logoutVip("iqiyi");
                                    if (mFloatView.isShow()) mFloatView.close();
                                }
                            }
                        }
                        break;
                    case LESHI_LOGIN:
                        if (event.getPackageName().toString().equals(LESHI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_et_pwd = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/password_edittext");
                            List<AccessibilityNodeInfo> nodeList_et_phone = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/account_edittext");
                            List<AccessibilityNodeInfo> nodeList_tv_login = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/login_button");
                            List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/plaintext_imageview");
                            if (nodeList_et_pwd.size() > 0 && nodeList_et_phone.size() > 0 && nodeList_tv_login.size() > 0 &&  nodeList_cb_show_passwd.size() >0){
                                if (!mFloatView.isShow()) mFloatView.show();
                                Rect outBounds = new Rect();
                                nodeList_cb_show_passwd.get(0).getBoundsInScreen(outBounds);
                                if (!mFloatView2.isShow()){
                                    mFloatView2.setPosition(outBounds.left, outBounds.top);
                                    mFloatView2.show();
                                }
                            }else {
                                if (mFloatView.isShow()) mFloatView.close();
                                if (mFloatView2.isShow()) mFloatView2.close();
                            }
                        }
                        break;
                    case LESHI_LOGOUT:
                        if (event.getPackageName().toString().equals(LESHI)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_navi = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/main_my_radio");
                            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.letv.android.client:id/verficationcode_logout_ensure_tx");
                            if (nodeList_navi.size() > 0 && nodeList_navi.get(0).isSelected()){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }
                            else if (nodeInfoList.size() > 0 ){
                                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                                if (nodeInfo.isEnabled()){
                                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Log.w(TAG,"AreaParty:乐视视频退出登录");
                                    Toasty.success(getApplicationContext(),"AreaParty:乐视视频退出登录").show();
                                    state = NO_ACTION;
                                    StartActivity.logoutVip("iqiyi");
                                    if (mFloatView.isShow()) mFloatView.close();
                                }
                            }
                        }
                        break;
                    case YOUKU_LOGIN:
                    case YOUKU_LOGIN_TEST:
                        if (event.getPackageName().toString().equals(YOUKU)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_userName = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_username");
                            List<AccessibilityNodeInfo> nodeList_pwd = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_password");
                            List<AccessibilityNodeInfo> nodeList_login = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_login");
                            if (nodeList_userName.size()>0 && nodeList_pwd.size()>0 && nodeList_login.size()>0){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }else {
                                if (mFloatView.isShow()) mFloatView.close();
                            }
                        }
                        break;
                    case YOUKU_YZM:
                        if (event.getPackageName().toString().equals(YOUKU)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo passport_sms_code = helper.findFirstNodeInfoByViewId("com.youku.phone:id/passport_sms_code");
                            AccessibilityNodeInfo passport_get_sms = helper.findFirstNodeInfoByViewId("com.youku.phone:id/passport_get_sms");
                            AccessibilityNodeInfo passport_button = helper.findFirstNodeInfoByViewId("com.youku.phone:id/passport_button");
                            if (passport_sms_code != null && passport_get_sms!=null){
                                if (passport_get_sms.getText().toString().equals("获取验证码")){
                                    passport_get_sms.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    state = YOUKU_YZM_WAIT;
                                    sendYZMRequest();
                                }
                            }else {
                                //Log.w(TAG,"nodeInfo is null");
                            }
                        }
                    case YOUKU_YZM_WAIT:
                    case YOUKU_LASTCHECK:
                        if (event.getPackageName().toString().equals(YOUKU)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo text_user = helper.findFirstNodeInfoByViewId("com.youku.phone:id/text_user");
                            if (text_user == null){
                                count = 0;
                                return;
                            }
                            if (!text_user.isSelected()){
                                count = 0;
                                helper.performClick(text_user,true);
                                return;
                            }
                            boolean verifyed = false;
                            //boolean isVip = false;
                            List<String> strings = helper.findAllTexts();
                            for (int i = 0; i <= strings.size() ; i++){
                                if (i < strings.size()){
                                    String s = strings.get(i);
                                    if (s.equals("登录/注册")){
                                        Log.w(TAG,"可能登录失败");
                                        verifyed = false;
                                        break;
                                    }
                                }else {
                                    verifyed = true;
                                    Log.w(TAG,"登录成功");
                                }

                            }
                            if (verifyed){
                                for (int i = 0; i <= strings.size() ; i++){
                                    if (i < strings.size()){
                                        String s = strings.get(i);
                                        if (s.startsWith("[开通会员]")){
                                            Log.w(TAG,"maybe不是vip账号");
                                            //isVip = false;
                                            //解决页面刷新不及时的问题
                                            youkuLastCheckVip();
                                            break;
                                        }
                                    }else {
                                        //isVip = true;
                                        Log.w(TAG,"是vip账号");
                                        sendLoginInfo3();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录成功","优酷视频VIP账号登录成功");
                                            }
                                        }).start();
                                    }
                                }
                                state = NO_ACTION;
                                //发送通知；
                            }else {
                                if (count < 1){
                                    count ++;
                                }else {
                                    //state = NO_ACTION;
                                    Log.w(TAG,"maybe确定登录失败"+count);
                                    if (count > 1){
                                        return;
                                    }
                                    count ++;
                                    //解决页面刷新不及时的引起的问题
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.w(TAG,"maybe确定登录失11败"+count);
                                            if (state == YOUKU_LASTCHECK || state == YOUKU_YZM){
                                                state = NO_ACTION;
                                                Log.w(TAG,"等待2s后再次确定登录失败");
                                                count = 0;
                                                sendLoginInfo5();
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MyApplication.getInstance().showNotification("AreaParty:登录过程发生故障","请尝试重新执行登录过程，若多次尝试仍不能登录成功，可申诉退款");
                                                    }
                                                }).start();
                                            }
                                        }
                                    }, 2000);
                                }
                            }
                            //Log.w(TAG,strings.size()+"");
                        }
                        break;
                    case YOUKU_LASTCHECK_TEST:
                        if (event.getPackageName().toString().equals(YOUKU)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo text_user = helper.findFirstNodeInfoByViewId("com.youku.phone:id/text_user");
                            if (text_user == null){
                                count = 0;
                                return;
                            }
                            if (!text_user.isSelected()){
                                count = 0;
                                helper.performClick(text_user,true);
                                return;
                            }
                            boolean verifyed = false;
                            boolean isVip = false;
                            List<String> strings = helper.findAllTexts();
                            for (int i = 0; i <= strings.size() ; i++){
                                if (i < strings.size()){
                                    String s = strings.get(i);
                                    if (s.equals("登录/注册")){
                                        Log.w(TAG,"可能登录失败");
                                        verifyed = false;
                                        break;
                                    }
                                }else {
                                    verifyed = true;
                                    Log.w(TAG,"登录成功");
                                }

                            }
                            if (verifyed){
                                for (int i = 0; i <= strings.size() ; i++){
                                    if (i < strings.size()){
                                        String s = strings.get(i);
                                        if (s.startsWith("[开通会员]")){
                                            Log.w(TAG,"不是vip账号");
                                            isVip = false;
                                            //解决页面刷新不及时的引起的问题
                                            youkuLastCheckVip_TEST();

                                            break;
                                        }
                                    }else {
                                        isVip = true;
                                        Log.w(TAG,"是vip账号");
                                        EventBus.getDefault().post(2,"youkuLoginTest");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到优酷VIP账号登录成功，可以出租");
                                            }
                                        }).start();
                                    }
                                }
                                state = NO_ACTION;
                                //发送通知；
                            }else {
                                if (count < 1){
                                    count ++;
                                }else {
                                    Log.w(TAG,"maybe确定登录失败");
                                    if (count > 1){
                                        return;
                                    }
                                    count ++;
                                    //解决页面刷新不及时的引起的问题
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (state == YOUKU_LASTCHECK_TEST){
                                                state = NO_ACTION;
                                                Log.w(TAG,"等待2s后再次确定登录失败");
                                                EventBus.getDefault().post(0,"youkuLoginTest");
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到优酷视频账号未登录成功，暂不能出租");
                                                    }
                                                }).start();
                                            }
                                        }
                                    }, 2000);


                                }
                            }
                            //Log.w(TAG,strings.size()+"");
                        }
                        break;
                    case YOUKU_LOGOUT:
                        if (event.getPackageName().toString().equals(YOUKU)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_user = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/layout_user");
                            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/negtive_btn_layout");
                            if (nodeList_user.size() > 0 && nodeList_user.get(0).isSelected()){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }
                            else if (nodeInfoList.size() > 0 ){
                                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                                if (nodeInfo.isClickable()){
                                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Log.w(TAG,"AreaParty:优酷退出登录");
                                    Toasty.success(getApplicationContext(),"AreaParty:优酷退出登录").show();
                                    state = NO_ACTION;
                                    StartActivity.logoutVip("youku");
                                    if (mFloatView.isShow()) mFloatView.close();
                                }
                            }
                        }
                        break;
                    case TENCENT_LOGIN:
                    case TENCENT_LOGIN_TEST:
                        if (event.getPackageName().toString().equals(TENCENT)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            node.clear();
                            bianli(root);
                            if (node.size() == 8 && node.get(3).getViewIdResourceName().equals("u") && node.get(4).getViewIdResourceName().equals("p") && node.get(5).getViewIdResourceName().equals("go")){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }else {
                                if (mFloatView.isShow()) mFloatView.close();
                            }
                        }
                        else if (event.getPackageName().toString().equals(QQ)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeInfoList_qq = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/account");
                            List<AccessibilityNodeInfo> nodeInfoList_pwd = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/password");
                            List<AccessibilityNodeInfo> nodeInfoList_login = root.findAccessibilityNodeInfosByText("登录");
                            if (nodeInfoList_qq.size()>0 && nodeInfoList_pwd.size()>0 && nodeInfoList_login.size() >1){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }else {
                                if (mFloatView.isShow()) mFloatView.close();
                            }
                        }
                        break;
                    case TENCENT_LASTCHECK:
                        if (event.getPackageName().toString().equals(TENCENT)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo login_text = helper.findFirstNodeInfoByViewId(t1);
                            AccessibilityNodeInfo vip_open_title = helper.findFirstNodeInfoByViewId(t2);
                            if (login_text != null){
                                if (!login_text.getText().toString().equals("点击登录")){
                                    Log.w(TAG,"腾讯视频登录成功1");
                                    state = NO_ACTION;
                                    if (vip_open_title != null){
                                        //Log.w("TENCENT_LASTCHECK",vip_open_title.getText().toString());
                                        if (vip_open_title.getText().toString().equals("开通VIP会员")){
                                            Log.w(TAG,"开通VIP会员");
                                            sendLoginInfo4();
                                        }else if (vip_open_title.getText().toString().equals("我的VIP会员")){
                                            Log.w(TAG,"我的VIP会员");
                                            sendLoginInfo3();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyApplication.getInstance().showNotification("AreaParty:登录成功","腾讯视频VIP账号登录成功");
                                                }
                                            }).start();
                                        }else {
                                            Log.w(TAG,"会员信息去哪了"+vip_open_title.toString());
                                            //解决界面刷新不及时的问题
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    boolean verify = false;
                                                    AccessibilityNodeInfo root = getRootInActiveWindow();
                                                    if (root != null && root.getPackageName().toString().equals(TENCENT)){
                                                        AccessibilityNodeInfo vip_open_title = helper.findFirstNodeInfoByViewId(t2);
                                                        if (vip_open_title != null){
                                                            verify = true;
                                                            if (vip_open_title.getText().toString().equals("开通VIP会员")){
                                                                Log.w(TAG,"0.5s后确认--开通VIP会员");
                                                                sendLoginInfo4();
                                                            }else if (vip_open_title.getText().toString().equals("我的VIP会员")){
                                                                Log.w(TAG,"0.5s后确认--我的VIP会员");
                                                                sendLoginInfo3();
                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        MyApplication.getInstance().showNotification("AreaParty:登录成功","腾讯视频VIP账号登录成功");
                                                                    }
                                                                }).start();
                                                            }
                                                        }
                                                    }
                                                    if (!verify){
                                                        Log.w(TAG,"0.5s后未确认到会员信息");
                                                    }
                                                }
                                            },500);
                                        }
                                    }
                                }else {
                                    Log.w(TAG,"腾讯视频登录失败");
                                    if (count < 3){
                                        count ++;
                                    }else {
                                        Log.w(TAG,"腾讯视频登录失败1");
                                        count = 0;
                                        state = NO_ACTION;
                                        sendLoginInfo5();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录过程发生故障","请尝试重新执行登录过程，若多次尝试仍不能登录成功，可申诉退款");
                                            }
                                        }).start();
                                    }

                                }
                            }
                        }
                        else if (event.getPackageName().toString().equals(QQ)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo dialogTitle = helper.findFirstNodeInfoByViewId("com.tencent.mobileqq:id/dialogTitle");
                            if (dialogTitle!=null && dialogTitle.getText().equals("登录失败")){
                                Log.w(TAG,"腾讯视频登录失败qq");
                                sendLoginInfo5();
                                AutoLoginService.state = NO_ACTION;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyApplication.getInstance().showNotification("AreaParty:登录过程发生故障","请尝试重新执行登录过程，若多次尝试仍不能登录成功，可申诉退款");
                                    }
                                }).start();
                            }
                        }
                        break;
                    case TENCENT_LASTCHECK_TEST:
                        if (event.getPackageName().toString().equals(TENCENT)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo login_text = helper.findFirstNodeInfoByViewId(t1);
                            AccessibilityNodeInfo vip_open_title = helper.findFirstNodeInfoByViewId(t2);
                            if (login_text != null){
                                if (!login_text.getText().toString().equals("点击登录")){
                                    Log.w(TAG,"腾讯视频登录成功1");
                                    if (vip_open_title != null){
                                        //Log.w("TENCENT_LASTCHECK",vip_open_title.getText().toString());
                                        if (vip_open_title.getText().toString().equals("开通VIP会员")){
                                            Log.w(TAG,"开通VIP会员");
                                            EventBus.getDefault().post(1,"tencentLoginTest");
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频账号登录成功，但未开通VIP会员，暂不能出租");
                                                }
                                            }).start();
                                        }else if (vip_open_title.getText().toString().equals("我的VIP会员")){
                                            Log.w(TAG,"我的VIP会员");
                                            EventBus.getDefault().post(2,"tencentLoginTest");
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频VIP账号登录成功，可以出租");
                                                }
                                            }).start();
                                        }else {
                                            Log.w(TAG,"会员信息去哪了"+vip_open_title.toString());
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    boolean verify = false;
                                                    AccessibilityNodeInfo root = getRootInActiveWindow();
                                                    if (root != null && root.getPackageName().toString().equals(TENCENT)){
                                                        AccessibilityNodeInfo vip_open_title = helper.findFirstNodeInfoByViewId(t2);
                                                        if (vip_open_title != null){
                                                            verify = true;
                                                            if (vip_open_title.getText().toString().equals("开通VIP会员")){
                                                                Log.w(TAG,"0.5s后确认--开通VIP会员");
                                                                EventBus.getDefault().post(1,"tencentLoginTest");
                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频账号登录成功，但未开通VIP会员，暂不能出租");
                                                                    }
                                                                }).start();
                                                            }else if (vip_open_title.getText().toString().equals("我的VIP会员")){
                                                                Log.w(TAG,"0.5s后确认--我的VIP会员");
                                                                EventBus.getDefault().post(2,"tencentLoginTest");
                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频VIP账号登录成功，可以出租");
                                                                    }
                                                                }).start();
                                                            }
                                                        }
                                                    }
                                                    if (!verify){
                                                        EventBus.getDefault().post(1,"tencentLoginTest");
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频账号登录成功，但未开通VIP会员，暂不能出租");
                                                            }
                                                        }).start();
                                                    }
                                                }
                                            },500);
                                        }
                                        state = NO_ACTION;
                                    }
                                }else {
                                    Log.w(TAG,"腾讯视频登录失败");
                                    if (count < 3){
                                        count ++;
                                    }else {
                                        count = 0;
                                        state = NO_ACTION;
                                        EventBus.getDefault().post(0,"tencentLoginTest");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频账号未登录成功，暂不能出租");
                                            }
                                        }).start();
                                    }

                                }
                            }
                        }
                        else if (event.getPackageName().toString().equals(QQ)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            AccessibilityNodeInfo dialogTitle = helper.findFirstNodeInfoByViewId("com.tencent.mobileqq:id/dialogTitle");
                            if (dialogTitle!=null && dialogTitle.getText().equals("登录失败")){
                                Log.w(TAG,"腾讯视频登录失败qq");
                                AutoLoginService.state = NO_ACTION;
                                EventBus.getDefault().post(0,"tencentLoginTest");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到腾讯视频账号未登录成功，暂不能出租");
                                    }
                                }).start();
                            }
                        }
                        break;
                    case TENCENT_LOGOUT:
                        if (event.getPackageName().toString().equals(TENCENT)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeList_LoginTxt = root.findAccessibilityNodeInfosByViewId(t1);
                            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.tencent.qqlive:id/button2");
                            if (nodeList_LoginTxt.size() > 0 ){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }
                            if (nodeInfoList.size() > 0 ){
                                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                                if (nodeInfo.isClickable()){
                                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    /*if (StartActivity.QQVersionCode == 0 || TextUtils.isEmpty(Util.getRecordId(getApplicationContext()))){*/
                                        Log.w(TAG,"AreaParty:腾讯视频退出登录");
                                        Toasty.success(getApplicationContext(),"AreaParty:腾讯视频退出登录").show();
                                        state = NO_ACTION;
                                        StartActivity.logoutVip("tencent");
                                        if (mFloatView.isShow()) mFloatView.close();
                                    /*}else{
                                        Toasty.success(getApplicationContext(),"AreaParty:由于你安装了手机QQ,你还需要在登录界面进行检测来清除使用记录", Toast.LENGTH_LONG).show();
                                    }*/
                                }
                            }
                        }
                        else if (event.getPackageName().toString().equals(QQ)){
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/ivTitleName");
                            List<AccessibilityNodeInfo> nodeInfoList_dialogText = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/dialogText");
                            List<AccessibilityNodeInfo> nodeInfoList_dialogRightBtn = root.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/dialogRightBtn");
                            Log.w(TAG,nodeInfoList.size()+">"+nodeInfoList_dialogText.size()+">"+nodeInfoList_dialogRightBtn.size()+"");
                            if (nodeInfoList.size() > 0){
                                if (!mFloatView.isShow()) mFloatView.show();
                            }
                            else if (nodeInfoList_dialogText.size() > 0 && nodeInfoList_dialogRightBtn.size() > 0){
                                AccessibilityNodeInfo dialogText = nodeInfoList_dialogText.get(0);
                                AccessibilityNodeInfo dialogRightBtn = nodeInfoList_dialogRightBtn.get(0);
                                String record = Util.getRecordId(getApplicationContext());
                                if (dialogRightBtn.getText().toString().equals("删除") && dialogRightBtn.isClickable()){
                                    if (dialogRightBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
                                        if (dialogText.getText().toString().equals(record+"?")){
                                            Toasty.success(getApplicationContext(),"AreaParty:腾讯视频退出登录").show();
                                            state = NO_ACTION;
                                            StartActivity.logoutVip("tencent");
                                            if (mFloatView.isShow()) mFloatView.close();
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        /**/
                        break;
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                //Log.w(TAG+"CLICKED", event.getPackageName().toString());
                switch (state){
                    case IQIYI_LOGIN:
                        if (event.getPackageName().toString().equals(IQIYI)){
                            AccessibilityNodeInfo eventNode = event.getSource();//让爱奇艺登录界面的小眼睛不可点击
                            if (eventNode == null) return;
                            if (eventNode.getClassName().toString().equals("android.widget.CheckBox")){
                                AccessibilityNodeInfo root = getRootInActiveWindow();
                                if (root == null) return;
                                List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/cb_show_passwd");
                                List<AccessibilityNodeInfo> nodeList_tv_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_login");
                                if (nodeList_cb_show_passwd.size() > 0 && nodeList_tv_login.size() > 0){
                                    AccessibilityNodeInfo node_cb_show_passwd = nodeList_cb_show_passwd.get(0);
                                    Log.w(TAG, eventNode.toString());
                                    Log.w(TAG, node_cb_show_passwd.toString());
                                    Log.w(TAG, eventNode.equals(node_cb_show_passwd)+"");

                                    if (eventNode.equals(node_cb_show_passwd)){
                                        if (node_cb_show_passwd.isChecked()){
                                            node_cb_show_passwd.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        }

                                        Rect outBounds = new Rect();
                                        node_cb_show_passwd.getBoundsInScreen(outBounds);
                                        if (!mFloatView2.isShow()){
                                            mFloatView2.setPosition(outBounds.left, outBounds.top);
                                            mFloatView2.show();
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case IQIYI_LOGOUT:
                        /*if (event.getPackageName().toString().equals(IQIYI)){//监控退出登录
                            AccessibilityNodeInfo eventNode = event.getSource();
                            if (eventNode == null) return;
                            if (eventNode.getText()!= null){
                                Log.w(TAG, eventNode.getText().toString());
                                if (eventNode.getText().toString().equals("退出登录")){
                                    AccessibilityNodeInfo root = getRootInActiveWindow();
                                    if (root == null) return;
                                    List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_right");
                                    Log.w(TAG, nodeInfoList.size()+"");
                                    if (nodeInfoList.size() > 0){
                                        AccessibilityNodeInfo node = nodeInfoList.get(0);
                                        Log.w(TAG,eventNode.equals(node)+"");
                                        if (eventNode.equals(node)){
                                            Log.w(TAG,"AreaParty:爱奇艺退出登录2");
                                            Toasty.success(getApplicationContext(),"AreaParty:爱奇艺退出登录2").show();
                                            state = NO_ACTION;
                                        }
                                    }
                                }
                            }


                            //if (eventNode.getClassName().toString().equals("android.widget.TextView") && )
                        }*/
                        break;
                    case YOUKU_LOGOUT:
                        /*if (event.getPackageName().toString().equals(YOUKU)){
                            AccessibilityNodeInfo eventNode = event.getSource();
                            if (eventNode == null) return;
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root == null) return;
                            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/negtive_btn_layout");
                            Log.w(TAG, nodeInfoList.size()+"");
                            if (nodeInfoList.size() > 0){
                                AccessibilityNodeInfo node = nodeInfoList.get(0);
                                Log.w(TAG,eventNode.equals(node)+"");
                                if (eventNode.equals(node)){
                                    Log.w(TAG,"AreaParty:优酷退出登录2");
                                    Toasty.success(getApplicationContext(),"AreaParty:优酷退出登录2").show();
                                    state = NO_ACTION;
                                }
                            }
                        }*/
                        break;
                    case TENCENT_LOGOUT:
                        /*if (event.getPackageName().toString().equals(TENCENT)){
                            AccessibilityNodeInfo eventNode = event.getSource();
                            if (eventNode == null) return;
                            if (eventNode.getText()!=null && eventNode.getText().toString().equals("确定")){
                                AccessibilityNodeInfo root = getRootInActiveWindow();
                                if (root == null) return;
                                List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId("com.tencent.qqlive:id/button2");
                                Log.w(TAG, nodeInfoList.size()+"");
                                if (nodeInfoList.size() > 0){
                                    AccessibilityNodeInfo node = nodeInfoList.get(0);
                                    Log.w(TAG,eventNode.equals(node)+"");
                                    if (eventNode.equals(node)){
                                        Log.w(TAG,"AreaParty:优酷退出登录2");
                                        Toasty.success(getApplicationContext(),"AreaParty:优酷退出登录2").show();
                                        state = NO_ACTION;
                                    }
                                }
                            }
                        }*/
                        break;

                }
                break;
            default:
                break;
        }
    }

    private void youkuLastCheckVip() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean verify = false;
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root != null){
                    if (root.getPackageName().toString().equals(YOUKU)){
                        AccessibilityNodeInfo text_user = helper.findFirstNodeInfoByViewId("com.youku.phone:id/text_user");
                        if (text_user!=null && text_user.isSelected()){
                            verify = true;
                            List<String> strings = helper.findAllTexts();
                            for (int i = 0; i <= strings.size() ; i++){
                                if (i < strings.size()){
                                    String s = strings.get(i);
                                    if (s.startsWith("[开通会员]")){
                                        Log.w(TAG,"0.5s后确定不是vip账号");
                                        sendLoginInfo4();
                                        break;
                                    }
                                }else {
                                    Log.w(TAG,"0.5s后才确定是vip账号");
                                    sendLoginInfo3();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyApplication.getInstance().showNotification("AreaParty:登录成功","优酷视频VIP账号登录成功");
                                        }
                                    }).start();
                                }
                            }
                        }
                    }
                }
                if (!verify){
                    Log.w(TAG,"0.1s后确定不是vip账号"+"页面被用户改变");
                }
            }
        },500);
    }

    private void youkuLastCheckVip_TEST() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean verify = false;
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root != null){
                    if (root.getPackageName().toString().equals(YOUKU)){
                        AccessibilityNodeInfo text_user = helper.findFirstNodeInfoByViewId("com.youku.phone:id/text_user");
                        if (text_user!=null && text_user.isSelected()){
                            verify = true;
                            List<String> strings = helper.findAllTexts();
                            for (int i = 0; i <= strings.size() ; i++){
                                if (i < strings.size()){
                                    String s = strings.get(i);
                                    if (s.startsWith("[开通会员]")){
                                        Log.w(TAG,"0.5s后确定不是vip账号");
                                        EventBus.getDefault().post(1,"youkuLoginTest");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到优酷视频账号登录成功，但未开通VIP会员，暂不能出租");
                                            }
                                        }).start();
                                        break;
                                    }
                                }else {
                                    Log.w(TAG,"0.5s后才确定是vip账号");
                                    EventBus.getDefault().post(2,"youkuLoginTest");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到优酷视频VIP账号登录成功，可以出租");
                                        }
                                    }).start();
                                }
                            }
                        }
                    }
                }
                if (!verify){
                    Log.w(TAG,"0.1s后确定不是vip账号"+"页面被用户改变");
                    EventBus.getDefault().post(1,"youkuLoginTest");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().showNotification("AreaParty:登录测试","检测到优酷视频账号登录成功，但未开通VIP会员，暂不能出租");
                        }
                    }).start();
                }
            }
        },500);
    }

    private void sendYZMRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.w("vipRent","从账号分享者去获取验证码");
                    VipMsg.VipRentYZMReq.Builder builder = VipMsg.VipRentYZMReq.newBuilder();
                    builder.setUserId(Login.userId);
                    builder.setAppId(FloatView.appId);
                    builder.setAccountId(FloatView.accountId);
                    builder.setProviderId(FloatView.providerId);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.VIP_RENT_CODE_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void iqiyiYZM(String code){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null && root.getPackageName().equals(IQIYI)){
            AccessibilityNodeInfo e1 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block1");
            AccessibilityNodeInfo e2 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block2");
            AccessibilityNodeInfo e3 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block3");
            AccessibilityNodeInfo e4 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block4");
            AccessibilityNodeInfo e5 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block5");
            AccessibilityNodeInfo e6 = helper.findFirstNodeInfoByViewId("com.qiyi.video:id/enter_pwd_block6");
            if (e1!=null){
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, String.valueOf(code.charAt(0)));
                e1.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, String.valueOf(code.charAt(1)));
                e2.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, String.valueOf(code.charAt(2)));
                e3.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, String.valueOf(code.charAt(3)));
                e4.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, String.valueOf(code.charAt(4)));
                e5.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, String.valueOf(code.charAt(5)));
                e6.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                state = IQIYI_LASTCHECT;
            }
        }
    }

    private void youkuYZM(String code){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null && root.getPackageName().equals(YOUKU)){
            AccessibilityNodeInfo passport_sms_code = helper.findFirstNodeInfoByViewId("com.youku.phone:id/passport_sms_code");
            AccessibilityNodeInfo passport_get_sms = helper.findFirstNodeInfoByViewId("com.youku.phone:id/passport_get_sms");
            AccessibilityNodeInfo passport_button = helper.findFirstNodeInfoByViewId("com.youku.phone:id/passport_button");
            if (passport_sms_code != null && passport_get_sms!=null){
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, code);
                passport_sms_code.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

                if (passport_button.isClickable()){
                    passport_button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                state = YOUKU_LASTCHECK;
            }
        }
    }

    @Subscriber(tag = "vipRentCode", mode = ThreadMode.MAIN)
    private void vipRentCode(final VipMsg.VipRentYZMRsp response ){
           if (response.getResultCode().equals(VipMsg.VipRentYZMRsp.ResultCode.SUCCESS)){
               String code = response.getYzmCode();
               Log.w("vipRentCode","验证码获取成功："+code);
               if (!TextUtils.isEmpty(code)){
                   if (response.getAppId() == 1 && state == IQIYI_YZM_WAIT){
                       iqiyiYZM(code);
                   }else if (response.getAppId() == 2 && state == YOUKU_YZM_WAIT){
                       //优酷自动填写验证码
                       youkuYZM(code);
                   }
               }
               else {
                   Toasty.error(this,"验证码获取失败").show();
               }
           }else {
               Log.w("vipRentCode","验证码获取失败");
               Toasty.error(this,response.getYzmCode()).show();
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       MyApplication.getInstance().showNotification("AreaParty:验证码获取失败", response.getYzmCode());
                   }
               }).start();

           }
    }
    private void sendLoginInfo3(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.RentLoginInfo.Builder builder = VipMsg.RentLoginInfo.newBuilder();
                    builder.setType(VipMsg.RentLoginInfo.Type.SuccessVip);
                    builder.setUserId(Login.userId);
                    builder.setAllocationId(allocationId);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.RENTLOGININFO_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendLoginInfo4(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.RentLoginInfo.Builder builder = VipMsg.RentLoginInfo.newBuilder();
                    builder.setType(VipMsg.RentLoginInfo.Type.SuccessNotVip);
                    builder.setUserId(Login.userId);
                    builder.setAllocationId(allocationId);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.RENTLOGININFO_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendLoginInfo5(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.RentLoginInfo.Builder builder = VipMsg.RentLoginInfo.newBuilder();
                    builder.setType(VipMsg.RentLoginInfo.Type.Fail);
                    builder.setUserId(Login.userId);
                    builder.setAllocationId(allocationId);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.RENTLOGININFO_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME){
            Log.w(TAG,"KEYCODE_HOME");
        }
        return super.onKeyEvent(event);
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
    /*public void bianli1(AccessibilityNodeInfo nodeInfo){//递归遍历根节点找所需节点
        if (nodeInfo == null) return;
        if (nodeInfo.getChildCount() == 0){
            Log.w(TAG,nodeInfo.toString());
            node.add(nodeInfo);
        }else {
            for (int i = 0; i<nodeInfo.getChildCount(); i++){
                bianli1(nodeInfo.getChild(i));
            }
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
/*
public class AutoLoginService extends AccessibilityService {
    //爱奇艺用到的变量
    public static final int IQIYI_NO_ACTION = 0;
    public static final int IQIYI_LOGIN = 1;
    public static final int IQIYI_LOGOUT = 2;
    public static int IQIYI_STATUS = IQIYI_NO_ACTION;

    public static boolean loginSucceed = false;
//    private boolean jumpToMainActivity = false;
    //优酷用到的变量
    public static final int YOUKU_NO_ACTION = 0;
    public static final int YOUKU_LOGIN = 1;
    public static final int YOUKU_LOGOUT = 2;
    public static int YOUKU_STATUS = YOUKU_NO_ACTION;

    List<AccessibilityNodeInfo> imageNode = new ArrayList<>();
    private boolean isLogined;
    private AccessibilityNodeInfo loginOrRegister;

    Bundle arguments;//用于清空输入框的内容
    private boolean isRequestServer = false;

    String serialNumber;//设备标识

    //处理网络超时
    private static final int NETWORK_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NETWORK_ERROR:
                    Toast.makeText(AutoLoginService.this, "网络故障", Toast.LENGTH_SHORT).show();
                    IQIYI_STATUS = IQIYI_NO_ACTION;
                    YOUKU_STATUS = YOUKU_NO_ACTION;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        serialNumber = android.os.Build.SERIAL;

        arguments = new Bundle();
        arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT, AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
        arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        IQIYI_STATUS = IQIYI_NO_ACTION;
        YOUKU_STATUS = YOUKU_NO_ACTION;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getPackageName().toString()){
            case "com.qiyi.video":
                if (IQIYI_STATUS == IQIYI_NO_ACTION){
                    break;
                }else if (IQIYI_STATUS == IQIYI_LOGIN){
                    handle_iqiyi_login(event);
                    break;
                }else if (IQIYI_STATUS == IQIYI_LOGOUT){
                    handle_iqiyi_logout(event);
                    break;
                }

                break;
            case "com.youku.phone":
                if (YOUKU_STATUS == YOUKU_NO_ACTION){
                    break;
                }else if (YOUKU_STATUS == YOUKU_LOGIN){
                    handle_youku_login(event);
                    break;
                }else if (YOUKU_STATUS == YOUKU_LOGOUT){
                    handle_youku_logout(event);
                    break;
                }
                break;
            default:break;
        }

//        switch (event.getEventType()){
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//                imageNode.clear();
//                Log.w("####",(rootNode!=null)+"  ");
//                if (rootNode != null){
//                    bianli(rootNode);
////
//                }
//                if (imageNode.size() > 3){
//                    Log.w("#####",imageNode.size()+"");
//                    AccessibilityNodeInfo settingNode = imageNode.get(3);
//                    if (settingNode != null && settingNode.isClickable()){
//                        settingNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    }
//
//                }


//                if (rootNode != null){
//                    List<AccessibilityNodeInfo> nodeInfo = rootNode.findAccessibilityNodeInfosByText("我的星球");
//  //                  Log.w("###", nodeInfo.size()+"@");
//                }
//        }

    }


    @Override
    public void onInterrupt() {

    }

    public void bianli(AccessibilityNodeInfo nodeInfo){//递归遍历根节点找所需节点
        if (nodeInfo == null) return;
        if (nodeInfo.getChildCount() == 0){
            if (nodeInfo.getClassName().toString().equals("android.widget.ImageView")){
                imageNode.add(nodeInfo);
//                Log.w("",nodeInfo.toString());
            }
            if ((nodeInfo.getText()+"").equals("登录/注册")){//检测到为未登录状态
//                Log.w("qax",nodeInfo.toString());
                isLogined = false;
                loginOrRegister = nodeInfo;
            }
        }else {

            for (int i = 0; i<nodeInfo.getChildCount(); i++){
                bianli(nodeInfo.getChild(i));
            }
        }
    }

    private void handle_iqiyi_login(AccessibilityEvent event) {
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Log.w("#########","TYPE_WINDOW_STATE_CHANGED");
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root == null){return;}
//                Log.w("root", root.toString());
                List<AccessibilityNodeInfo> nodeList_et_pwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/et_pwd");
                List<AccessibilityNodeInfo> nodeList_et_phone = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/et_phone");
                List<AccessibilityNodeInfo> nodeList_tv_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_login");
                List<AccessibilityNodeInfo> nodeList_cb_show_passwd = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/cb_show_passwd");
                List<AccessibilityNodeInfo> nodeList_tv_problems = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_problems");
                List<AccessibilityNodeInfo> nodeList_tv_problem = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_problem");
                List<AccessibilityNodeInfo> nodeList_dialog_title = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/dialog_title");
                List<AccessibilityNodeInfo> nodeList_navi3 = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/navi3");
                List<AccessibilityNodeInfo> nodeList_head = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_main_head_layout");
                List<AccessibilityNodeInfo> nodeList_userName = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/ugc_feed_friends_name");
                List<AccessibilityNodeInfo> nodeList_main_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/my_main_login");
//                Log.w("###",nodeList_et_pwd.size()+""+nodeList_et_phone.size()+nodeList_tv_problems.size()+nodeList_tv_problem.size()+nodeList_dialog_title.size());
                ////在登录界面1
                if (!loginSucceed && nodeList_et_phone.isEmpty() && !nodeList_et_pwd.isEmpty() && nodeList_tv_problems.isEmpty()){
                    performGlobalAction(GLOBAL_ACTION_BACK);//异常：容易在跳到主界面后执行返回
                    try{Thread.sleep(700);}catch (Exception e){e.printStackTrace();}
                    root = getRootInActiveWindow();
                    if (root == null) return;
                    nodeList_tv_problems = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_problems");
//                    Log.w("####", nodeList_tv_problems.size()+"");
                    if (!nodeList_tv_problems.isEmpty() && nodeList_tv_problems.get(0).isClickable()){
                        nodeList_tv_problems.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    return;
                }
                //点击切换账号
                if (nodeList_tv_problem.size() == 4){
                    nodeList_tv_problem.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //账号密码登录界面
                if (!nodeList_et_phone.isEmpty() && !nodeList_et_pwd.isEmpty() && !nodeList_tv_login.isEmpty() && !nodeList_cb_show_passwd.isEmpty()){
                    if (isRequestServer || loginSucceed){return;}
                    isRequestServer = true;
                    AccessibilityNodeInfo node_cb_show_passwd = nodeList_cb_show_passwd.get(0);
                    final AccessibilityNodeInfo node_et_phone = nodeList_et_phone.get(0);
                    final AccessibilityNodeInfo node_et_pwd = nodeList_et_pwd.get(0);
                    final AccessibilityNodeInfo node_tv_login = nodeList_tv_login.get(0);

                    final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);//借用粘贴板
                    //设置密码显示状态为***
                    if (node_cb_show_passwd.isChecked()){
                        node_cb_show_passwd.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
//                    Log.w("1###","准备访问数据库");
                    Util.sendRequestLogin("true", "iqiyi", serialNumber, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = new Message();
                            message.what = NETWORK_ERROR;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String responseText = response.body().string();
                            response.close();
                            Log.w("1####",(responseText == null)+"");
                            Log.w("1####",responseText);
                            try{
                                JSONObject jsonObject = new JSONObject(responseText);
                                String account_id = jsonObject.getString("account_id");
                                String account_name = jsonObject.getString("account_name");
                                String account_password = jsonObject.getString("account_password");
                                if (account_id != null){
                                    //填充手机号
                                    ClipData clipData_phone =ClipData.newPlainText("phone", "18030637727");//进行网络请求获取用户名
                                    ClipData clipData_null =ClipData.newPlainText("null", "");
                                    node_et_phone.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                                    node_et_phone.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
                                    clipboardManager.setPrimaryClip(clipData_phone);
                                    node_et_phone.performAction(AccessibilityNodeInfo.ACTION_PASTE);//粘贴
                                    clipboardManager.setPrimaryClip(clipData_null);//清除粘贴板数据
                                    //填充密码
                                    ClipData clipData_password =ClipData.newPlainText("password", "ZHUmaomao123301");//进行网络请求获取密码
                                    node_et_pwd.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                                    node_et_pwd.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
                                    clipboardManager.setPrimaryClip(clipData_password);
                                    node_et_pwd.performAction(AccessibilityNodeInfo.ACTION_PASTE);//粘贴
                                    clipboardManager.setPrimaryClip(clipData_null);
                                    //点击登录按钮
                                    if (node_tv_login.isClickable()){
                                        node_tv_login.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        loginSucceed = true;
                                        Util.setRecord(getApplicationContext() , "iqiyi" , account_id);
                                        try{Thread.sleep(2000);}catch (Exception e){e.printStackTrace();}
                                    }
                                }
                                isRequestServer = false;
                            }catch (Exception e){
                                //解析失败
                                Log.w("####","解析失败");
                                e.printStackTrace();
                            }
                        }
                    });
                    return;

                }
                //输入验证码界面
                if (!nodeList_dialog_title.isEmpty()){ loginSucceed = true;  return;}
                //查看登录结果
                if (loginSucceed && !nodeList_navi3.isEmpty() && nodeList_head.isEmpty()){
                    nodeList_navi3.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //查看到登录成功
                if (loginSucceed && !nodeList_navi3.isEmpty() && !nodeList_head.isEmpty() && !nodeList_userName.isEmpty()){
                    IQIYI_STATUS = IQIYI_NO_ACTION;
                    loginSucceed = false;
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "自动登录成功", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.autologin_smile);
                    toastView.addView(imageCodeProject, 0);
                    toast.show();


                    //给服务器发出登录成功反馈


                    return;
                }
                //登录异常
                if (loginSucceed && !nodeList_navi3.isEmpty() && !nodeList_head.isEmpty() && !nodeList_main_login.isEmpty()){
                    IQIYI_STATUS = IQIYI_NO_ACTION;
                    loginSucceed = false;
                    Toast.makeText(this, "登录异常", Toast.LENGTH_SHORT).show();                        
                    if (Util.getRecordWebsit(getApplicationContext()).equals("iqiyi")){
                        Util.sendRequestLogout("true", "iqiyi", Util.getRecordId(getApplicationContext()), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Message message = new Message();
                                message.what = NETWORK_ERROR;
                                handler.sendMessage(message);
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Util.clearRecord(getApplicationContext());
                            }
                        });
                    }
                    return;

                }

                if (!loginSucceed && !nodeList_navi3.isEmpty() && !nodeList_head.isEmpty() && !nodeList_userName.isEmpty()){
                    IQIYI_STATUS = IQIYI_NO_ACTION;
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "自动登录失败，你可能登录了其他账号或进行了界面操作，请重试", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.autologin_sad);
                    toastView.addView(imageCodeProject, 0);
                    toast.show();
                    Util.clearRecord(getApplicationContext());
                    return;
                }
                if (!loginSucceed && !nodeList_navi3.isEmpty()){
                    nodeList_navi3.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }


//                if (loginSucceed){
//                    if (jumpToMainActivity) return;
//                    try {
//                        //跳转到爱奇艺的主界面
//                        String packageName = "com.qiyi.video";
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        ComponentName comp = new ComponentName(packageName, "org.qiyi.android.video.SubTitleUtil");
//                        intent.setComponent(comp);
//                        startActivity(intent);
////                        Log.w("###","进入爱奇艺");
//                        jumpToMainActivity = true;
//                    }catch (Exception e){e.printStackTrace();}
//                    return;
//                }
                break;

            default:
                break;
        }
    }
    private void handle_iqiyi_logout(AccessibilityEvent event) {
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Log.w("#########","TYPE_WINDOW_STATE_CHANGED"+event.getEventType());
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root == null) return;
                List<AccessibilityNodeInfo> nodeList_navi3 = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/navi3");
                List<AccessibilityNodeInfo> nodeList_head = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_main_head_layout");
                List<AccessibilityNodeInfo> nodeList_userName = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/ugc_feed_friends_name");
                List<AccessibilityNodeInfo> nodeList_main_login = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/my_main_login");
                List<AccessibilityNodeInfo> nodeList_others = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/mymain_others_grid_item_bg");
                List<AccessibilityNodeInfo> nodeList_account_management = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_setting_account_management");
                List<AccessibilityNodeInfo> nodeList_logout = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/phone_my_setting_exit_login");
                List<AccessibilityNodeInfo> nodeList_logout_right = root.findAccessibilityNodeInfosByViewId("com.qiyi.video:id/tv_right");
//                Log.w("###",""+nodeList_navi3.size()+"*"+nodeList_head.size()+"*"+nodeList_userName.size()+"*"+nodeList_main_login.size()+"*"+nodeList_account_management.size()+"*"+nodeList_logout.size()+"*"+nodeList_others.size()+"*"+nodeList_logout_right.size());
                if (!nodeList_navi3.isEmpty() && nodeList_head.isEmpty()){
                    nodeList_navi3.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                if (!nodeList_navi3.isEmpty() && !nodeList_head.isEmpty() && !nodeList_main_login.isEmpty()){
                    Toast.makeText(this, "已经退出登录", Toast.LENGTH_SHORT).show();
                    IQIYI_STATUS = IQIYI_NO_ACTION;
                    //给服务器发送反馈
                    if (Util.getRecordWebsit(getApplicationContext()).equals("iqiyi")){
                        Util.sendRequestLogout("true", "iqiyi", Util.getRecordId(getApplicationContext()), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Message message = new Message();
                                message.what = NETWORK_ERROR;
                                handler.sendMessage(message);
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
//                                Log.w("###" , response.body().string());
                                Util.clearRecord(getApplicationContext());
                            }
                        });
                    }
                    return;
                }
                //get(9)得到设置按钮
                if (!nodeList_navi3.isEmpty() && !nodeList_head.isEmpty() && !nodeList_userName.isEmpty() && nodeList_others.size()>11){
                    nodeList_others.get(9).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //账号与安全
                if (!nodeList_account_management.isEmpty()){
                    nodeList_account_management.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //退出登录
                if (!nodeList_logout.isEmpty()){
                    nodeList_logout.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //退出登录（弹窗）
                if (!nodeList_logout_right.isEmpty()){
                    if (nodeList_logout_right.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)){
                        IQIYI_STATUS = IQIYI_NO_ACTION;
                        //给服务器发送退出登录通知
                        if (Util.getRecordWebsit(getApplicationContext()).equals("iqiyi")){
                            Util.sendRequestLogout("true", "iqiyi", Util.getRecordId(getApplicationContext()), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message message = new Message();
                                    message.what = NETWORK_ERROR;
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
//                                    Log.w("11111",response.body().string());
                                    Util.clearRecord(getApplicationContext());
                                }
                            });
                        }
                        try {
                            //跳转到爱奇艺的主界面
                            String packageName = "com.qiyi.video";
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ComponentName comp = new ComponentName(packageName, "org.qiyi.android.video.SubTitleUtil");
                            intent.setComponent(comp);
                            startActivity(intent);
//                            Log.w("###","进入爱奇艺");
                        }catch (Exception e){e.printStackTrace();}
                        Toast.makeText(this, "成功退出登录", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }
                break;
            default:
                break;
        }
    }

    private void handle_youku_login(AccessibilityEvent event) {
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root == null){return;}
                List<AccessibilityNodeInfo> nodeList_user = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/layout_user");
                List<AccessibilityNodeInfo> nodeList_userName = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_username");
                List<AccessibilityNodeInfo> nodeList_pwd = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_password");
                List<AccessibilityNodeInfo> nodeList_login = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_login");
                List<AccessibilityNodeInfo> nodeList_login_type = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/passport_login_type");
                List<AccessibilityNodeInfo> nodeList_share = root.findAccessibilityNodeInfosByText("分享到星球");
//                Log.w("###", nodeList_userName.size()+"*"+nodeList_pwd.size()+"*"+nodeList_login.size()+"*"+nodeList_share.size());
                //非我的主界面
                if (!nodeList_user.isEmpty() && !nodeList_user.get(0).isSelected()){
                    nodeList_user.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //我的——主界面--登录前
                if (!loginSucceed && !nodeList_user.isEmpty() && nodeList_user.get(0).isSelected()){
                    loginOrRegister = null;
                    try{Thread.sleep(500);}catch (Exception e){e.printStackTrace();}
                    bianli(getRootInActiveWindow());
                    if (loginOrRegister != null){
                        Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
                        loginOrRegister.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }else{
                        YOUKU_STATUS = YOUKU_NO_ACTION;
                        Toast.makeText(this, "请先退出已登录账号", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                //我的主界面——登录后
                if (loginSucceed && !nodeList_user.isEmpty() && nodeList_user.get(0).isSelected()){
                    YOUKU_STATUS = YOUKU_NO_ACTION;
                    loginOrRegister = null;
                    Log.w("qaaaaaaa",( loginOrRegister != null )+"");
                    try{Thread.sleep(1000);}catch (Exception e){e.printStackTrace();}
                    bianli(getRootInActiveWindow());
                    if ( loginOrRegister != null ){
                        YOUKU_STATUS = YOUKU_NO_ACTION;
                        loginSucceed = false;
                        Toast.makeText(this, "登录异常", Toast.LENGTH_SHORT).show();//
                        if (Util.getRecordWebsit(getApplicationContext()).equals("youku")){
                            Util.sendRequestLogout("true", "youku", Util.getRecordId(getApplicationContext()), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message message = new Message();
                                    message.what = NETWORK_ERROR;
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Util.clearRecord(getApplicationContext());
                                }
                            });
                        }
                        return;
                    }else {
                        YOUKU_STATUS = YOUKU_NO_ACTION;
                        loginSucceed = false;
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                //分享到星球界面--登录前
                if (!loginSucceed && !nodeList_share.isEmpty()){
                    YOUKU_STATUS = YOUKU_NO_ACTION;
                    Toast.makeText(this, "请先退出已登录账号", Toast.LENGTH_SHORT).show();
                    performGlobalAction(GLOBAL_ACTION_BACK);
//                    try {
//                        //跳转到优酷的主界面
//                        String packageName = "com.youku.phone";
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        ComponentName comp = new ComponentName(packageName,  "com.youku.phone.ActivityWelcome");
//                        intent.setComponent(comp);
//                        startActivity(intent);
//                    }catch (Exception e){e.printStackTrace();}
                    return;
                }
                //分享到星球界面--登录后
                if (loginSucceed && !nodeList_share.isEmpty()){
                    performGlobalAction(GLOBAL_ACTION_BACK);
//                    try {
//                        //跳转到优酷的主界面
//                        String packageName = "com.youku.phone";
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        ComponentName comp = new ComponentName(packageName,  "com.youku.phone.ActivityWelcome");
//                        intent.setComponent(comp);
//                        startActivity(intent);
//                    }catch (Exception e){e.printStackTrace();}
                    return;
                }
                //手机快捷登陆界面
                if (!nodeList_login_type.isEmpty() && (nodeList_login_type.get(0).getText()+"").equals("账号登录")){
                    nodeList_login_type.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //账号登录界面
                if (!nodeList_userName.isEmpty() && !nodeList_pwd.isEmpty() && !nodeList_login.isEmpty()){
                    if (isRequestServer || loginSucceed){
                        return;
                    }
                    isRequestServer = true;
                    final AccessibilityNodeInfo node_userName = nodeList_userName.get(0);
                    final AccessibilityNodeInfo node_pwd = nodeList_pwd.get(0);
                    final AccessibilityNodeInfo node_login = nodeList_login.get(0);
                    final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    //填充手机/邮箱输入框
                    Util.sendRequestLogin("true", "youku", serialNumber, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = new Message();
                            message.what = NETWORK_ERROR;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseText = response.body().string();
                            response.close();
//                            Log.w("1####",responseText);
                            try{
                                JSONObject jsonObject = new JSONObject(responseText);
                                String account_id = jsonObject.getString("account_id");
                                String account_name = jsonObject.getString("account_name");
                                String account_password = jsonObject.getString("account_password");
                                if (account_id != null){
                                    node_userName.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                                    node_userName.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
                                    ClipData clipData_username =ClipData.newPlainText("username", "18030637727");
                                    clipboardManager.setPrimaryClip(clipData_username);
                                    node_userName.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                                    //填充密码框
                                    ClipData clipData_password =ClipData.newPlainText("password", "ZHUmaomao123301");
                                    node_pwd.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                                    node_pwd.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
                                    clipboardManager.setPrimaryClip(clipData_password);
                                    node_pwd.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                                    ClipData clipData_null =ClipData.newPlainText("null", "");
                                    clipboardManager.setPrimaryClip(clipData_null);
                                    //点击登录按钮
                                    if (node_login.isClickable()){
                                        if (node_login.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
                                            loginSucceed = true;
                                        }
                                    }
                                    Util.setRecord(getApplicationContext() , "youku" , account_id);
                                }
                                isRequestServer = false;
                            }catch (Exception e){e.printStackTrace();}
                        }
                    });
                }
        }
    }
    private void handle_youku_logout(AccessibilityEvent event) {
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root == null){return;}
                imageNode.clear();
                List<AccessibilityNodeInfo> nodeList_user = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/layout_user");
                List<AccessibilityNodeInfo> nodeList_logout = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/exit");
                List<AccessibilityNodeInfo> nodeList_logout_right = root.findAccessibilityNodeInfosByViewId("com.youku.phone:id/negtive_btn_layout");
                Log.w("###1", "***"+nodeList_user.size()+"*"+nodeList_logout.size()+"*"+nodeList_logout_right.size());
                //主页的非我的界面
                if (!nodeList_user.isEmpty() && !nodeList_user.get(0).isSelected()){
                    nodeList_user.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }

                //主页我的界面
                if (!nodeList_user.isEmpty() && nodeList_user.get(0).isSelected()){
                    Log.w("###1","here");
                    isLogined = true;
                    try{Thread.sleep(1000);}catch (Exception e){}
                    bianli(root);
                    if (!isLogined){
                        YOUKU_STATUS = YOUKU_NO_ACTION;
                        Toast.makeText(this, "您未登录账号", Toast.LENGTH_SHORT).show();
                        //向服务器返回数据
                        if (Util.getRecordWebsit(getApplicationContext()).equals("youku")){
                            Util.sendRequestLogout("true", "youku", Util.getRecordId(getApplicationContext()), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message message = new Message();
                                    message.what = NETWORK_ERROR;
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Util.clearRecord(getApplicationContext());
                                }
                            });
                        }
                    }else if (imageNode.size()>2){
                        imageNode.get(imageNode.size()-2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    return;
                }
                //设置界面
                if (!nodeList_logout.isEmpty()){
                    nodeList_logout.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return;
                }
                //退出登录弹窗
                if (!nodeList_logout_right.isEmpty()){
                    YOUKU_STATUS = YOUKU_NO_ACTION;
                    nodeList_logout_right.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show();
                    //向服务器返回数据
                    if (Util.getRecordWebsit(getApplicationContext()).equals("youku")){
//                        Log.w("1###","loouting");
                        Util.sendRequestLogout("true", "youku", Util.getRecordId(getApplicationContext()), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Message message = new Message();
                                message.what = NETWORK_ERROR;
                                handler.sendMessage(message);
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
//                                Log.w("1###" , response.body().string());
                                Util.clearRecord(getApplicationContext());
                            }
                        });
                    }
                }


        }
    }
}
*/
