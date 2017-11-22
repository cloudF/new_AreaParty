package com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.start;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.hdhome.WelcomeActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.MainActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.androidlearning.boris.familycentralcontroler.fragment05.accessible_service.AutoLoginService;
import com.androidlearning.boris.familycentralcontroler.fragment05.accessible_service.Util;
import com.androidlearning.boris.familycentralcontroler.fragment05.phoneVIPAppActivity;

import java.util.List;

import info.hoang8f.widget.FButton;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    //图片按钮
    private ImageButton web1080;
    private ImageButton blufans;
    private ImageButton hdhome;
    private ImageButton hdchd;
    //链接按钮
    private Button buttonWeb1080;
    private Button buttonBlufans;
    private Button buttonHdhome;
    private Button buttonHdchd;
    //下载管理
    private Button downloadManagement;

    private TextView share_tv;

    //网站地址
    private String urlWeb1080="http://www.1080.net";
    private String urlBlufans="http://www.longbaidu.com/forum.php?forumlist=1&mobile=2";

    private String urlHdchd="http://www.vkugq.com";
    private String urlXunleicun="http://www.webmanager_xunleicun.com/misc.php?mod=mobile";

    ///phoneVIPAppActivity的变量
    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private boolean serviceEnabled;
    private boolean networkAvailable = false;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    public static int iqiyiVersionCode;
    public static int youkuVersionCode;


    private SwitchCompat btn_autoLoginService;
    private FButton btn_login_iqiyi, btn_logout_iqiyi, btn_login_youku, btn_logout_youku ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.websitemanager_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        phoneVIPAppActivity();///执行phoneVIPAppActivity的onCreate操作
        initView();


        //获取权限


//        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//        }

        //绑定监听
        web1080.setOnClickListener(this);
        blufans.setOnClickListener(this);
        hdhome.setOnClickListener(this);
        hdchd.setOnClickListener(this);

        buttonHdhome.setOnClickListener(this);
        buttonBlufans.setOnClickListener(this);
        buttonWeb1080.setOnClickListener(this);
        buttonHdchd.setOnClickListener(this);

        downloadManagement.setOnClickListener(this);
        share_tv.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        updateServiceStatus();
        checkOnlineState();
        AutoLoginService.IQIYI_STATUS = AutoLoginService.IQIYI_NO_ACTION;
        AutoLoginService.YOUKU_STATUS = AutoLoginService.YOUKU_NO_ACTION;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkChangeReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.web1080:
           case R.id.ButtonWeb1080:
               Intent intent=new Intent(StartActivity.this, MainActivity.class);
               intent.putExtra("URL",urlWeb1080);
               startActivity(intent);
               break;
           case R.id.blufans:
           case R.id.ButtonBlufans:
               Intent intent1=new Intent(StartActivity.this, MainActivity.class);
               intent1.putExtra("URL",urlBlufans);
               startActivity(intent1);
               break;
           case R.id.hdchd:
           case R.id.ButtonHdchd:
               Intent intent2=new Intent(StartActivity.this, MainActivity.class);
               intent2.putExtra("URL",urlHdchd);
               startActivity(intent2);
               break;
           case R.id.hdhome:
           case R.id.ButtonHdhome:
               Intent intent3=new Intent(StartActivity.this, WelcomeActivity.class);
               startActivity(intent3);
               break;
           case R.id.downloadManagement:
               Intent intent4=new Intent(StartActivity.this, RemoteDownloadActivity.class);
               startActivity(intent4);
               break;
           case R.id.tv_share:
//               startActivity(new Intent(StartActivity.this,phoneVIPAppActivity.class));
               break;
           case R.id.btn_auto_login_service:
               startActivity(mAccessibleIntent);
               break;
           case R.id.btn_login_iqiyi:
           case R.id.btn_logout_iqiyi:
           case R.id.btn_login_youku:
           case R.id.btn_logout_youku:
               if (serviceEnabled){
                   if (networkAvailable){
                       switch (v.getId()){
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
               break;
           default:
               break;

       }
    }

//    private long exitTime = 0;



    private  void initView(){
        web1080=(ImageButton)findViewById(R.id.web1080);
        blufans=(ImageButton)findViewById(R.id.blufans);
        hdhome=(ImageButton)findViewById(R.id.hdhome);
        hdchd=(ImageButton)findViewById(R.id.hdchd);

        buttonWeb1080=(Button)findViewById(R.id.ButtonWeb1080);
        buttonBlufans=(Button)findViewById(R.id.ButtonBlufans);
        buttonHdhome=(Button)findViewById(R.id.ButtonHdhome);
        buttonHdchd=(Button)findViewById(R.id.ButtonHdchd) ;

        downloadManagement=(Button)findViewById(R.id.downloadManagement);
        share_tv = (TextView) findViewById(R.id.tv_share);

    }


    private void phoneVIPAppActivity() {
        initViews();//初始化界面

        //updateServiceStatus();//检测自助登录服务是否开启

        //动态注册监听网络变化
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
        checkOnlineState();//检测网络是否可用

        iqiyiVersionCode = getVersionCode("com.qiyi.video");//获取爱奇艺的版本号
        youkuVersionCode = getVersionCode("com.youku.phone");//获取优酷的版本号
    }
    private void logout_iqiyi() {
        if (iqiyiVersionCode == 0){
            Toast.makeText(this, "您未安装爱奇艺", Toast.LENGTH_SHORT).show();
            return;
        }else if (iqiyiVersionCode < 80890 ){//8.6.0版本
            Toast.makeText(this, "您的爱奇艺版本过低，请更新至最新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        AutoLoginService.IQIYI_STATUS = AutoLoginService.IQIYI_LOGOUT;
        try {
            //跳转到爱奇艺的主界面
            String packageName = "com.qiyi.video";
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName(packageName, "org.qiyi.android.video.MainActivity");
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
        AutoLoginService.IQIYI_STATUS = AutoLoginService.IQIYI_LOGIN;
        AutoLoginService.loginSucceed = false;
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
        AutoLoginService.YOUKU_STATUS = AutoLoginService.YOUKU_LOGIN;
        AutoLoginService.loginSucceed = false;
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
        AutoLoginService.YOUKU_STATUS = AutoLoginService.YOUKU_LOGOUT;
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

    public class NetworkChangeReceiver extends BroadcastReceiver {//动态监听网络变化

        @Override
        public void onReceive(Context context, Intent intent) {
            checkOnlineState();
        }
    }

    private void initViews() {
        btn_autoLoginService = (SwitchCompat) findViewById(R.id.btn_auto_login_service);  btn_autoLoginService.setOnClickListener(this);
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
        btn_autoLoginService.setChecked(serviceEnabled);

    }
}
