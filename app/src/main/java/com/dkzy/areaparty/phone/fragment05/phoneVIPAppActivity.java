package com.dkzy.areaparty.phone.fragment05;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment05.accessible_service.Util;

import java.util.List;

import info.hoang8f.widget.FButton;


public class phoneVIPAppActivity extends AppCompatActivity implements View.OnClickListener{
    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private boolean serviceEnabled;
    private boolean networkAvailable = false;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    public static int iqiyiVersionCode;
    public static int youkuVersionCode;


    private Button btn_autoLoginService;
    private FButton btn_login_iqiyi, btn_logout_iqiyi, btn_login_youku, btn_logout_youku ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab05_phonevip_main_activity);


        initViews();//初始化界面

        updateServiceStatus();//检测自助登录服务是否开启

        //动态注册监听网络变化
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
        checkOnlineState();//检测网络是否可用

        iqiyiVersionCode = getVersionCode("com.qiyi.video");//获取爱奇艺的版本号
        youkuVersionCode = getVersionCode("com.youku.phone");//获取优酷的版本号
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
        checkOnlineState();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_auto_login_service){
            startActivity(mAccessibleIntent);
            return;
        }
        if (serviceEnabled){
            if (networkAvailable){
                switch (view.getId()){
                    case R.id.btn_login_iqiyi://爱奇艺登录
                        login_iqiyi();
                        break;
                    case R.id.btn_logout_iqiyi://爱奇艺登出
                        logout_iqiyi();
                        break;
                    case R.id.btn_login_youku://优酷登录
                        login_youku();
                        break;
                    case R.id.btn_logout_youku://优酷登出
                        logout_youku();
                        break;
                    default:
                        break;
                }
            }else {
                Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "请先开启自助登录服务", Toast.LENGTH_SHORT).show();
        }

    }


    private void logout_iqiyi() {
        if (iqiyiVersionCode == 0){
            Toast.makeText(this, "您未安装爱奇艺", Toast.LENGTH_SHORT).show();
            return;
        }else if (iqiyiVersionCode < 80890 ){//8.6.0版本
            Toast.makeText(this, "您的爱奇艺版本过低，请更新至最新版本", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //跳转到爱奇艺的主界面
            String packageName = "com.qiyi.video";
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName(packageName, "org.qiyi.android.video.SubTitleUtil");
            intent.setComponent(comp);
            startActivity(intent);
//            Log.w("###","进入爱奇艺");
        }catch (Exception e){e.printStackTrace();}
    }

    private void login_iqiyi() {
        if (iqiyiVersionCode == 0){
            Toast.makeText(this, "您未安装爱奇艺", Toast.LENGTH_SHORT).show();
            return;
        }else if (iqiyiVersionCode < 80890 ){//8.6.0版本
            Toast.makeText(this, "您的爱奇艺版本过低，请更新至最新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Util.getRecordWebsit(getApplicationContext()).equals("")){
            Log.w("@@@@@", Util.getRecordWebsit(getApplicationContext())+"123");
            Toast.makeText(this, "您有"+Util.getRecordWebsit(getApplicationContext())+"的登录记录，请点击右侧的退出登录按钮检验是否已退出登录", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //跳转到爱奇艺的登录
            String packageName = "com.qiyi.video";
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName(packageName, "org.qiyi.android.video.ui.account.PhoneAccountActivity");
            intent.setComponent(comp);
            startActivity(intent);

        }catch (Exception e){e.printStackTrace();}
    }

    private void login_youku() {
        if (iqiyiVersionCode == 0){
            Toast.makeText(this, "您未安装优酷", Toast.LENGTH_SHORT).show();
            return;
        }else if (iqiyiVersionCode < 128 ){//6.8.1版本
            Toast.makeText(this, "您的优酷版本过低，请更新至最新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Util.getRecordWebsit(getApplicationContext()).equals("")){
            Log.w("@@@@@", Util.getRecordWebsit(getApplicationContext())+"123");
            Toast.makeText(this, "您有"+Util.getRecordWebsit(getApplicationContext())+"的登录记录，请点击右侧的退出登录按钮检验是否已退出登录", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //跳转到优酷的登录界面
            String packageName = "com.youku.phone";
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName(packageName,"com.youku.fan.share.activity.FanShareActivity");
            intent.setComponent(comp);
            startActivity(intent);
        }catch (Exception e){e.printStackTrace();}
    }

    private void logout_youku() {
        if (iqiyiVersionCode == 0){
            Toast.makeText(this, "您未安装优酷", Toast.LENGTH_SHORT).show();
            return;
        }else if (iqiyiVersionCode < 128 ){//6.8.1版本
            Toast.makeText(this, "您的优酷版本过低，请更新至最新版本", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //跳转到优酷的主界面
            String packageName = "com.youku.phone";
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName(packageName, "com.youku.phone.ActivityWelcome");
            intent.setComponent(comp);
            startActivity(intent);
        }catch (Exception e){e.printStackTrace();}

    }

    private void initViews() {
        btn_autoLoginService = (Button) findViewById(R.id.btn_auto_login_service);  btn_autoLoginService.setOnClickListener(this);
        btn_login_iqiyi = (FButton) findViewById(R.id.btn_login_iqiyi);  btn_login_iqiyi.setOnClickListener(this);
        btn_logout_iqiyi = (FButton) findViewById(R.id.btn_logout_iqiyi);    btn_logout_iqiyi.setOnClickListener(this);
        btn_login_youku = (FButton) findViewById(R.id.btn_login_youku);  btn_login_youku.setOnClickListener(this);
        btn_logout_youku = (FButton) findViewById(R.id.btn_logout_youku);    btn_logout_youku.setOnClickListener(this);



    }

    private void updateServiceStatus() {
        serviceEnabled = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.fragment05.accessible_service.AutoLoginService")) {
                serviceEnabled = true;
                break;
            }
        }
        btn_autoLoginService.setText(serviceEnabled ? "已开启" : "已关闭");

    }
//获取前台应用 高版本运行有错误
//    public String getForegroundApp(Context context) {
//        ActivityManager am =
//                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> lr = am.getRunningAppProcesses();
//        if (lr == null) {
//            return null;
//        }
//
//        for (ActivityManager.RunningAppProcessInfo ra : lr) {
//            if (ra.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE || ra.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                return ra.processName;
//            }
//        }
//
//        return null;
//    }

    //    public boolean isNetworkAvailable() { //检测网络状态
//        ConnectivityManager CManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
//        try{
//            if (NInfo != null && NInfo.isConnected() && NInfo.getState() == NetworkInfo.State.CONNECTED) {
//                return true;
//            }else{
//                return false;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
    public void checkOnlineState() {//判断网络是否可用
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager CManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo NInfo = CManager.getActiveNetworkInfo();

                if (NInfo != null && NInfo.getState() == NetworkInfo.State.CONNECTED) {
                    networkAvailable = true;
                } else {
                    networkAvailable = false;
                }


            }

        }).start();
    }

    private int getVersionCode(String packageName){
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
//            Log.w("chg",""+"packageName-->"+packageName+"--versionName-->"+versionName+"--versionCode-->"+versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class NetworkChangeReceiver extends BroadcastReceiver{//动态监听网络变化

        @Override
        public void onReceive(Context context, Intent intent) {
            checkOnlineState();
        }
    }
}
