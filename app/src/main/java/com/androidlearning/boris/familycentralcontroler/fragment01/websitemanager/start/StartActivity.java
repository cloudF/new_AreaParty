package com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.start;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.Login;
import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment01.utorrent.utils.OkHttpUtils;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.hdhome.WelcomeActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.MainActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.androidlearning.boris.familycentralcontroler.fragment05.accessible_service.AutoLoginService;
import com.androidlearning.boris.familycentralcontroler.fragment05.accessible_service.Util;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;
import com.androidlearning.boris.familycentralcontroler.myapplication.floatview.FloatView;
import com.androidlearning.boris.familycentralcontroler.utils_comman.PreferenceUtil;
import com.androidlearning.boris.familycentralcontroler.utils_comman.netWork.NetUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import es.dmoral.toasty.Toasty;
import info.hoang8f.widget.FButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    //图片按钮
    private ImageButton web1080;
    private ImageButton blufans;
    private ImageButton hdhome;
    private ImageButton hdchd;
    private ImageButton hdchd1;

    //链接按钮
    private Button buttonWeb1080;
    private Button buttonBlufans;
    private Button buttonHdhome;
    private Button buttonHdchd;
    private Button buttonHdchd1;
    //下载管理
    private Button downloadManagement;

    private TextView share_tv;
    private TextView floatViewTV, autoLoginServiceTV, autoLoginHelper;

    //网站地址
    private String urlWeb1080="http://www.1080.net";
    private String urlBlufans="http://www.longbaidu.com/forum.php?forumlist=1&mobile=2";

    private String urlHdchd="http://www.vkugq.com";
    private String urlHdchd1 = "http://www.hdchd.cc";
    private String urlXunleicun="http://www.webmanager_xunleicun.com/misc.php?mod=mobile";

    ///phoneVIPAppActivity的变量
    public static final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    public static boolean serviceEnabled;
    private IntentFilter intentFilter;

    public static int iqiyiVersionCode;
    public static int youkuVersionCode;
    public static int tencentVersionCode;


    private FButton btn_login_iqiyi, btn_logout_iqiyi, btn_login_youku, btn_logout_youku, btn_login_tencent, btn_logout_tencent;
    private ImageView tagIqiyi,tagYouku,tagTencent;
    private RelativeLayout vipContent;

    public static String logined;
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
        hdchd1.setOnClickListener(this);

        buttonHdhome.setOnClickListener(this);
        buttonBlufans.setOnClickListener(this);
        buttonWeb1080.setOnClickListener(this);
        buttonHdchd.setOnClickListener(this);
        buttonHdchd1.setOnClickListener(this);

        downloadManagement.setOnClickListener(this);
        share_tv.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
        setTag();

    }

    @Override
    protected void onDestroy() {
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
           case R.id.hdchd1:
           case R.id.ButtonHdchd1:
               Intent intent5=new Intent(StartActivity.this, MainActivity.class);
               intent5.putExtra("URL",urlHdchd1);
               startActivity(intent5);
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
           case R.id.floatViewTV:
               if (!MyApplication.mFloatView.isShow()){
                   MyApplication.mFloatView.showAWhile();

               }else {
                   MyApplication.mFloatView.close();

               }
               break;
           case R.id.autoLoginServiceTV:
               startActivity(mAccessibleIntent);
               break;
           case R.id.autoLogin_help:
               startActivity(new Intent(StartActivity.this,AutoLoginHelperActivity.class));
               break;
           case R.id.btn_login_iqiyi:
           case R.id.btn_logout_iqiyi:
           case R.id.btn_login_youku:
           case R.id.btn_logout_youku:
           case R.id.btn_login_tencent:
           case R.id.btn_logout_tencent:
               if (TextUtils.isEmpty(logined)){
                   checkInfo();
                   return;
               }
               /*if (TextUtils.isEmpty(Login.userName)){
                   Toasty.error(this,"你处于离线登录状态，无法执行此功能").show();
                   return;
               }*/
               if (serviceEnabled){
                   if (NetUtil.getNetWorkState(this) != NetUtil.NETWORK_NONE){
                       switch (v.getId()){
                           case R.id.btn_login_iqiyi://爱奇艺登录
                               if (!(logined.equals(AutoLoginService.IQIYI) || (!logined.equals(AutoLoginService.IQIYI) && !TextUtils.isEmpty(new PreferenceUtil(getApplicationContext()).read("lastChosenPC"))))){
                                   Toasty.error(StartActivity.this, "安装AreaParty电视端并与手机连接后获得爱奇艺的使用权限").show();
                                    break;
                               }
                               if (logined.equals(AutoLoginService.IQIYI) || logined.equals("null")){
                                   if (iqiyiVersionCode != 0){
                                       openPackage(this,AutoLoginService.IQIYI);
                                       AutoLoginService.state = AutoLoginService.IQIYI_LOGIN;
                                       getVipUserCount("iqiyi");
                                   }else {
                                       Toasty.error(StartActivity.this, "你未安装爱奇艺").show();
                                   }
                               }else {
                                    toast();
                               }

                               break;
                           case R.id.btn_logout_iqiyi://爱奇艺登出
                               if (iqiyiVersionCode != 0){
                                   openPackage(this,AutoLoginService.IQIYI);
                                   AutoLoginService.state = AutoLoginService.IQIYI_LOGOUT;
                                   if (!MyApplication.mFloatView.isShow()){
                                       MyApplication.mFloatView.show();

                                   }
                               }else {
                                   Toasty.error(StartActivity.this, "你未安装爱奇艺").show();

                               }
                               break;
                           case R.id.btn_login_youku://优酷登录
                               if (!(logined.equals(AutoLoginService.YOUKU) || (!logined.equals(AutoLoginService.YOUKU) && !TextUtils.isEmpty(new PreferenceUtil(getApplicationContext()).read("lastChosenTV"))))){
                                   Toasty.error(StartActivity.this, "安装AreaParty电脑端并与手机连接后获得优酷的使用权限").show();
                                   break;
                               }
                               if (logined.equals(AutoLoginService.YOUKU) || logined.equals("null")){
                                   if (youkuVersionCode != 0){
                                       openPackage(this,AutoLoginService.YOUKU);
                                       AutoLoginService.state = AutoLoginService.YOUKU_LOGIN;
                                       getVipUserCount("youku");
                                   }else {
                                       Toasty.error(StartActivity.this, "你未安装优酷视频").show();
                                   }
                               }else {
                                   toast();
                               }

                               break;
                           case R.id.btn_logout_youku://优酷登出
                               if (youkuVersionCode != 0){
                                   openPackage(this,AutoLoginService.YOUKU);
                                   AutoLoginService.state = AutoLoginService.YOUKU_LOGOUT;
                                   if (!MyApplication.mFloatView.isShow()){
                                       MyApplication.mFloatView.show();

                                   }
                               }else {
                                   Toasty.error(StartActivity.this, "你未安装优酷视频").show();
                               }
                               break;
                           case R.id.btn_login_tencent://腾讯登录
                               if (logined.equals(AutoLoginService.TENCENT) || logined.equals("null")){
                                   if (tencentVersionCode != 0){
                                       openPackage(this,AutoLoginService.TENCENT);
                                       AutoLoginService.state = AutoLoginService.TENCENT_LOGIN;
                                       getVipUserCount("tencent");
                                   }else {
                                       Toasty.error(StartActivity.this, "你未安装腾讯视频").show();
                                   }
                               }else {
                                   toast();
                               }

                               break;
                           case R.id.btn_logout_tencent://腾讯登出
                               if (tencentVersionCode != 0){
                                   openPackage(this,AutoLoginService.TENCENT);
                                   AutoLoginService.state = AutoLoginService.TENCENT_LOGOUT;
                                   if (!MyApplication.mFloatView.isShow()){
                                       MyApplication.mFloatView.show();

                                   }
                               }else {
                                   Toasty.error(StartActivity.this, "你未安装腾讯视频").show();
                               }
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
        hdchd1=(ImageButton)findViewById(R.id.hdchd1);

        buttonWeb1080=(Button)findViewById(R.id.ButtonWeb1080);
        buttonBlufans=(Button)findViewById(R.id.ButtonBlufans);
        buttonHdhome=(Button)findViewById(R.id.ButtonHdhome);
        buttonHdchd=(Button)findViewById(R.id.ButtonHdchd) ;
        buttonHdchd1=(Button)findViewById(R.id.ButtonHdchd1) ;

        downloadManagement=(Button)findViewById(R.id.downloadManagement);
        share_tv = (TextView) findViewById(R.id.tv_share);

    }


    private void phoneVIPAppActivity() {
        initViews();//初始化界面

        logined = Util.getRecordWebsit(getApplicationContext());
        if (TextUtils.isEmpty(logined)){
            checkInfo();
        }else {
            setTag();
        }

        //updateServiceStatus();//检测自助登录服务是否开启
        iqiyiVersionCode = getVersionCode(AutoLoginService.IQIYI);//获取爱奇艺的版本号
        youkuVersionCode = getVersionCode(AutoLoginService.YOUKU);//获取优酷的版本号
        tencentVersionCode = getVersionCode(AutoLoginService.TENCENT);//获取腾讯视频版本号
    }

    public  void checkInfo(){
        String url = "http://192.168.1.107:8080/AreaParty/GetUserInfo?userName="+ Login.userName+"&userMac="+Login.getAdresseMAC(MyApplication.getContext());
        Log.w("StartActivity",url);
        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MyApplication.getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.w("StartActivity",responseData);
                if (!TextUtils.isEmpty(responseData)){
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String  iqiyiVipInfo = jsonObject.getJSONObject("iqiyiVipInfo").getString("useState");
                        String  tencentVipInfo = jsonObject.getJSONObject("tencentVipInfo").getString("useState");
                        String  youkuVipInfo = jsonObject.getJSONObject("youkuVipInfo").getString("useState");
                        if (iqiyiVipInfo.equals("在线")){
                            logined = AutoLoginService.IQIYI;
                        }else if (tencentVipInfo.equals("在线")){
                            logined = AutoLoginService.YOUKU;
                        }else if (youkuVipInfo.equals("在线")){
                            logined = AutoLoginService.TENCENT;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(logined)) logined = "null";
                Util.setRecord(MyApplication.getContext(),logined);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTag();
                    }
                });

            }
        });
    }
    public  void getVipUserCount(final String type) {
        String url = "http://192.168.1.107:8080/AreaParty/LoginVip?userName="+ Login.userName+"&userMac="+Login.getAdresseMAC(MyApplication.getContext())+"&vipType="+type;
        Log.w("StartActivity",url);
        FloatView.password = "";
        FloatView.name = "";
        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.w("StartActivity",responseData);
                if (responseData.startsWith("fail")){
                    registerVip(type);
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        FloatView.name = jsonObject.getString("name");
                        FloatView.password = jsonObject.getString("password");
                        Log.w("StartActivity",FloatView.name+"**"+FloatView.password);
                        switch (type){
                            case "iqiyi":
                                Util.setRecord(MyApplication.getContext(),AutoLoginService.IQIYI);
                                logined = AutoLoginService.IQIYI;
                                break;
                            case "youku":
                                Util.setRecord(MyApplication.getContext(),AutoLoginService.YOUKU);
                                logined = AutoLoginService.YOUKU;
                                break;
                            case "tencemt":
                                Util.setRecord(MyApplication.getContext(),AutoLoginService.TENCENT);
                                logined = AutoLoginService.TENCENT;
                                break;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTag();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public static void logoutVip(final String type){
        String url = "http://192.168.1.107:8080/AreaParty/LogoutVip?userName="+ Login.userName+"&userMac="+Login.getAdresseMAC(MyApplication.getContext())+"&vipType="+type;
        Log.w("StartActivity",url);

        Util.setRecord(MyApplication.getContext(),"null");
        logined = "null";


        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void registerVip(final String type){
        String url = "http://192.168.1.107:8080/AreaParty/RegisterVip?userName=" + Login.userName
                +"&userMac=" +Login.getAdresseMAC(MyApplication.getContext())
                +"&ip="+"223.85.200.129"
                +"&vipType="+type
                +"&registerTime="+getNowDate()
                +"&deadlineTime="+getMonthLaterDate(1);
        Log.w("StartActivity",url);
        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.w("StartActivity",responseData);
                if (responseData.equals("success")){
                    getVipUserCount(type);
                }
            }
        });
    }

    /*private void logout_iqiyi() {
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
    */


    private int getVersionCode(String packageName){
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            int versionCode = packageInfo.versionCode;
//            Log.w("chg",""+"packageName-->"+packageName+"--versionName-->"+versionName+"--versionCode-->"+versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initViews() {
        vipContent = (RelativeLayout) findViewById(R.id.vipContent); if (TextUtils.isEmpty(this.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", ""))){vipContent.setVisibility(View.GONE);}else {vipContent.setVisibility(View.VISIBLE);}
        autoLoginHelper = (TextView) findViewById(R.id.autoLogin_help);  autoLoginHelper.setOnClickListener(this);
        floatViewTV = (TextView) findViewById(R.id.floatViewTV); floatViewTV.setOnClickListener(this);floatViewTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        autoLoginServiceTV = (TextView) findViewById(R.id.autoLoginServiceTV); autoLoginServiceTV.setOnClickListener(this);autoLoginServiceTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btn_login_iqiyi = (FButton) findViewById(R.id.btn_login_iqiyi);  btn_login_iqiyi.setOnClickListener(this);
        btn_logout_iqiyi = (FButton) findViewById(R.id.btn_logout_iqiyi);    btn_logout_iqiyi.setOnClickListener(this);
        btn_login_youku = (FButton) findViewById(R.id.btn_login_youku);  btn_login_youku.setOnClickListener(this);
        btn_logout_youku = (FButton) findViewById(R.id.btn_logout_youku);    btn_logout_youku.setOnClickListener(this);
        btn_login_tencent = (FButton) findViewById(R.id.btn_login_tencent);  btn_login_tencent.setOnClickListener(this);
        btn_logout_tencent = (FButton) findViewById(R.id.btn_logout_tencent);    btn_logout_tencent.setOnClickListener(this);
        tagIqiyi = (ImageView) findViewById(R.id.tag_iqiyi);
        tagTencent = (ImageView) findViewById(R.id.tag_tencent);
        tagYouku = (ImageView) findViewById(R.id.tag_youku);

        //floatViewTV.setText(MyApplication.mFloatView.isShow()?"已开启" : "已关闭");

    }

    private void updateServiceStatus() {
        serviceEnabled = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager == null) return;
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.fragment05.accessible_service.AutoLoginService")) {
                serviceEnabled = true;
                break;
            }
        }
        autoLoginServiceTV.setText(serviceEnabled ? "已开启" : "去开启");

    }

    /**
     * <功能描述> 重新启动应用程序
     *
     * @return void [返回类型说明]
     */
    private void startUpApplication(String pkg) {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            // 获取指定包名的应用程序的PackageInfo实例
            packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // 未找到指定包名的应用程序
            e.printStackTrace();
            // 提示没有GPS Test Plus应用
            return;
        }
        if (packageInfo != null) {
            // 已安装应用
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageInfo.packageName);
            List<ResolveInfo> apps = packageManager.queryIntentActivities(
                    resolveIntent, 0);
            ResolveInfo ri = null;
            try {
                ri = apps.iterator().next();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (ri != null) {
                // 获取应用程序对应的启动Activity类名
                String className = ri.activityInfo.name;
                // 启动应用程序对应的Activity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName componentName = new ComponentName(pkg, className);
                intent.setComponent(componentName);
                startActivity(intent);
            }
        }
    }

    public static Intent getAppOpenIntentByPackageName(Context context,String packageName){
        // MainActivity完整名
        String mainAct = null;
        // 根据包名寻找MainActivity
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, 0);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }
    //打开第三方应用，
    public static boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    public static String getMonthLaterDate(int a) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.DATE, curr.getActualMaximum(Calendar.DATE));
        Date date = curr.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    public void toast(){
        String s = "";
        switch (logined){
            case AutoLoginService.IQIYI:
                s = "爱奇艺";
                break;

            case AutoLoginService.YOUKU:
                s = "优酷";
                break;

            case AutoLoginService.TENCENT:
                s = "腾讯视频";
                break;
        }
        if (!TextUtils.isEmpty(s)){
            Toasty.info(this,"您有"+s+"登录记录，你需要退出该账号才能使用此平台账号").show();
        }
    }
    public void setTag(){

        tagIqiyi.setVisibility(View.INVISIBLE);
        tagYouku.setVisibility(View.INVISIBLE);
        tagTencent.setVisibility(View.INVISIBLE);
        switch (logined){
            case AutoLoginService.IQIYI:
                tagIqiyi.setVisibility(View.VISIBLE);
                break;

            case AutoLoginService.YOUKU:
                tagYouku.setVisibility(View.VISIBLE);
                break;

            case AutoLoginService.TENCENT:
                tagTencent.setVisibility(View.VISIBLE);
                break;
        }
    }

}
