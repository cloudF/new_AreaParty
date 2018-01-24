package com.dkzy.areaparty.phone.fragment01.websitemanager.start;

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

import com.bumptech.glide.Glide;
import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_help;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_page;
import com.dkzy.areaparty.phone.fragment01.utorrent.utils.OkHttpUtils;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.WelcomeActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.MainActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.dkzy.areaparty.phone.fragment05.accessible_service.AutoLoginService;
import com.dkzy.areaparty.phone.fragment05.accessible_service.Util;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.floatview.FloatView;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;
import com.dkzy.areaparty.phone.utilseverywhere.utils.AccessibilityUtils;
import com.dkzy.areaparty.phone.utilseverywhere.utils.IntentUtils;

import org.json.JSONArray;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    //图片按钮
    private ImageView[] image = new ImageView[5];

    //链接按钮
    private Button[] btn = new Button[5];
    //下载管理
    private Button downloadManagement;

    private TextView share_tv;
    private TextView floatViewTV, autoLoginServiceTV, autoLoginHelper;
    private TextView helpInfo;

    //网站地址
    //http://www.87lou.com/
    //http://www.xixizh.com/
    //http://www.1080.net
    //http://www.dayangd.com/
    //http://www.vkugq.com
    //http://www.dyttbbs.com
    //http://www.btshoufa.net/forum.php
    //http://www.1080.cn/
    public static String[] url;
    public static String[] imageUrl;
    private String urlWeb1080="http://www.dayangd.com";
    private String urlBlufans="http://www.longbaidu.com";

    private String urlHdchd="http://www.87lou.com";
    private String urlHdchd1 = "http://www.hdchd.cc";
    private String urlXunleicun="http://www.webmanager_xunleicun.com/misc.php?mod=mobile";

    ///phoneVIPAppActivity的变量
    public static final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    public static boolean serviceEnabled;
    private IntentFilter intentFilter;

    public static int iqiyiVersionCode;
    public static int youkuVersionCode;
    public static int tencentVersionCode;
    public static int leshiVersionCode;
    public static int QQVersionCode;


    private FButton btn_login_iqiyi, btn_logout_iqiyi, btn_login_youku, btn_logout_youku, btn_login_tencent, btn_logout_tencent, btn_login_leshi, btn_logout_leshi;
    private ImageView tagIqiyi,tagYouku,tagTencent,tagLeshi;
    private RelativeLayout vipContent;

    private boolean isHelpDialogShow;

    public static String logined = "";

    public static String userName = "";
    public static boolean mainMobile;
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

        /*//绑定监听
        web1080.setOnClickListener(this);
        blufans.setOnClickListener(this);
        hdhome.setOnClickListener(this);
        hdchd.setOnClickListener(this);
        hdchd1.setOnClickListener(this);

        buttonHdhome.setOnClickListener(this);
        buttonBlufans.setOnClickListener(this);
        buttonWeb1080.setOnClickListener(this);
        buttonHdchd.setOnClickListener(this);
        buttonHdchd1.setOnClickListener(this);*/

        downloadManagement.setOnClickListener(this);
        share_tv.setOnClickListener(this);
        helpInfo.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
        iqiyiVersionCode = getVersionCode(AutoLoginService.IQIYI);//获取爱奇艺的版本号
        youkuVersionCode = getVersionCode(AutoLoginService.YOUKU);//获取优酷的版本号
        tencentVersionCode = getVersionCode(AutoLoginService.TENCENT);//获取腾讯视频版本号
        leshiVersionCode = getVersionCode(AutoLoginService.LESHI);
        QQVersionCode = getVersionCode(AutoLoginService.QQ);
        setTag();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.image1:
           case R.id.url1:
               Intent intent=new Intent(StartActivity.this, MainActivity.class);
               intent.putExtra("URL",url[0]);
               startActivity(intent);
               break;
           case R.id.image2:
           case R.id.url2:
               Intent intent1=new Intent(StartActivity.this, MainActivity.class);
               intent1.putExtra("URL",url[1]);
               startActivity(intent1);
               break;
           case R.id.image3:
           case R.id.url3:
               Intent intent2=new Intent(StartActivity.this, MainActivity.class);
               intent2.putExtra("URL",url[2]);
               startActivity(intent2);
               break;
           case R.id.image4:
           case R.id.url4:
               Intent intent5=new Intent(StartActivity.this, MainActivity.class);
               intent5.putExtra("URL",url[3]);
               startActivity(intent5);
               break;
           case R.id.image5:
           case R.id.url5:
               Intent intent3=new Intent(StartActivity.this, MainActivity.class);
               intent3.putExtra("URL",url[4]);
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
               IntentUtils.gotoAccessibilitySetting();
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
           case R.id.btn_login_leshi:
           case R.id.btn_logout_leshi:
               if (!mainMobile){
                   Toast.makeText(StartActivity.this, "当前设备不是主设备，无法使用此功能",Toast.LENGTH_SHORT).show();
                   return;
               }
               if (TextUtils.isEmpty(logined)){
                   checkInfo();
                   return;
               }
               if (!isHelpDialogShow){
                   showDialog(v);
               }else {
                   onClickListener(v);
               }

               break;
           case R.id.helpInfo:
               showHelpInfoDialog(R.layout.dialog_web);
               break;
           case R.id.img_tencent:
               if (tencentVersionCode != 0){
                   openPackage(this,AutoLoginService.TENCENT);

               }else {
                   Toasty.error(StartActivity.this, "你未安装腾讯视频").show();

               }
               break;
           case R.id.img_youku:
               if (youkuVersionCode != 0){
                   openPackage(this,AutoLoginService.YOUKU);

               }else {
                   Toasty.error(StartActivity.this, "你未安装优酷视频").show();

               }
               break;
           case R.id.img_leshi:
               if (leshiVersionCode != 0){
                   openPackage(this,AutoLoginService.LESHI);

               }else {
                   Toasty.error(StartActivity.this, "你未安装乐视视频").show();

               }
               break;


           default:
               break;

       }
    }
    public  void showHelpInfoDialog(int layout){
        final ActionDialog_page dialog = new ActionDialog_page(this,layout);
        dialog.setCancelable(true);
        dialog.show();
    }

//    private long exitTime = 0;



    private  void initView(){
        image[0]=(ImageView) findViewById(R.id.image1);
        image[1]=(ImageView) findViewById(R.id.image2);
        image[2]=(ImageView) findViewById(R.id.image3);
        image[3]=(ImageView) findViewById(R.id.image4);
        image[4]=(ImageView) findViewById(R.id.image5);

        btn[0]=(Button)findViewById(R.id.url1);
        btn[1]=(Button)findViewById(R.id.url2);
        btn[2]=(Button)findViewById(R.id.url3);
        btn[3]=(Button)findViewById(R.id.url4);
        btn[4]=(Button)findViewById(R.id.url5);

        if (url == null || imageUrl == null) {
            getWebSiteUrl();
        }else{
            initWebSite();
        }

        /*image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);*/

        downloadManagement=(Button)findViewById(R.id.downloadManagement);
        share_tv = (TextView) findViewById(R.id.tv_share);
        helpInfo = (TextView) findViewById(R.id.helpInfo);

    }


    private void phoneVIPAppActivity() {
        if (TextUtils.isEmpty(userName)){
            userName = Login.userName;
            if (!TextUtils.isEmpty(userName)){
                mainMobile = Login.mainMobile;
            }
        }

        initViews();//初始化界面

        logined = Util.getRecordWebsit(getApplicationContext());
        if (TextUtils.isEmpty(logined)){
            checkInfo();
        }else {
            setTag();
        }

        isHelpDialogShow = new PreferenceUtil("isHelpDialogShow",getApplicationContext()).readBoole("isHelpDialogShow");

        //updateServiceStatus();//检测自助登录服务是否开启


    }

    public void showDialog(final View v){
        final ActionDialog_help dialog = new ActionDialog_help(this);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener(v);
                if (dialog.isRadioButtonChecked()){
                    new PreferenceUtil("isHelpDialogShow",getApplicationContext()).writeBoole("isHelpDialogShow",true);
                    isHelpDialogShow = true;
                }
                dialog.dismiss();
            }
        });
    }

    public  void checkInfo(){
        /*if (TextUtils.isEmpty(logined)) logined = "null";
        Util.setRecord(MyApplication.getContext(),logined, "");
        setTag();*/
        String userName = "";
        if (!TextUtils.isEmpty(Login.userId)){userName = Login.userId;}else if(!TextUtils.isEmpty(MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", ""))){userName = MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", "");}
        String url = "http://"+AREAPARTY_NET+"/AreaParty/GetUserInfo?userName="+ userName+"&userMac="+Login.getAdresseMAC(MyApplication.getContext());
        Log.w("StartActivity",url);
        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getContext(), "连接账号服务器失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                String account = "";
                Log.w("StartActivity",responseData);
                if (!TextUtils.isEmpty(responseData)){
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String  iqiyiVipInfo = jsonObject.getJSONObject("iqiyiVipInfo").getString("useState");
                        String  tencentVipInfo = jsonObject.getJSONObject("tencentVipInfo").getString("useState");
                        String  youkuVipInfo = jsonObject.getJSONObject("youkuVipInfo").getString("useState");
                        if (iqiyiVipInfo.equals("在线")){
                            logined = AutoLoginService.LESHI;
                        }else if (tencentVipInfo.equals("在线")){
                            logined = AutoLoginService.TENCENT;
                            account = jsonObject.getString("tencentVipName");
                        }else if (youkuVipInfo.equals("在线")){
                            logined = AutoLoginService.YOUKU;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(logined)) logined = "null";
                Util.setRecord(MyApplication.getContext(),logined, account);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTag();
                    }
                });

            }
        });
    }

    public static void logoutVip(final String type){
        String userName = "";
        if (!TextUtils.isEmpty(Login.userId)){userName = Login.userId;}else if(!TextUtils.isEmpty(MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", ""))){userName = MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_ID", "");}
        String url = "http://"+AREAPARTY_NET+"/AreaParty/LogoutVip?userName="+ userName+"&userMac="+Login.getAdresseMAC(MyApplication.getContext())+"&vipType="+type;
        Log.w("StartActivity",url);

        Util.setRecord(MyApplication.getContext(),"null");
        logined = "null";
        if (type.equals("tencent")){
            Util.clearRecordId(MyApplication.getContext());
        }

        OkHttpUtils.getInstance().setUrl(url).buildNormal().execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }



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
        vipContent = (RelativeLayout) findViewById(R.id.vipContent); if (TextUtils.isEmpty(userName)){vipContent.setVisibility(View.GONE);}else {vipContent.setVisibility(View.VISIBLE);}
        autoLoginHelper = (TextView) findViewById(R.id.autoLogin_help);  autoLoginHelper.setOnClickListener(this);
        floatViewTV = (TextView) findViewById(R.id.floatViewTV); floatViewTV.setOnClickListener(this);floatViewTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        autoLoginServiceTV = (TextView) findViewById(R.id.autoLoginServiceTV); autoLoginServiceTV.setOnClickListener(this);autoLoginServiceTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btn_login_iqiyi = (FButton) findViewById(R.id.btn_login_iqiyi);  btn_login_iqiyi.setOnClickListener(this);
        btn_logout_iqiyi = (FButton) findViewById(R.id.btn_logout_iqiyi);    btn_logout_iqiyi.setOnClickListener(this);
        btn_login_youku = (FButton) findViewById(R.id.btn_login_youku);  btn_login_youku.setOnClickListener(this);
        btn_logout_youku = (FButton) findViewById(R.id.btn_logout_youku);    btn_logout_youku.setOnClickListener(this);
        btn_login_tencent = (FButton) findViewById(R.id.btn_login_tencent);  btn_login_tencent.setOnClickListener(this);
        btn_logout_tencent = (FButton) findViewById(R.id.btn_logout_tencent);    btn_logout_tencent.setOnClickListener(this);
        btn_login_leshi = (FButton) findViewById(R.id.btn_login_leshi);  btn_login_leshi.setOnClickListener(this);
        btn_logout_leshi = (FButton) findViewById(R.id.btn_logout_leshi);    btn_logout_leshi.setOnClickListener(this);
        tagIqiyi = (ImageView) findViewById(R.id.tag_iqiyi);
        tagTencent = (ImageView) findViewById(R.id.tag_tencent);
        tagYouku = (ImageView) findViewById(R.id.tag_youku);
        tagLeshi = (ImageView) findViewById(R.id.tag_leshi);

        findViewById(R.id.img_tencent).setOnClickListener(this);
        findViewById(R.id.img_leshi).setOnClickListener(this);
        findViewById(R.id.img_youku).setOnClickListener(this);

        //floatViewTV.setText(MyApplication.mFloatView.isShow()?"已开启" : "已关闭");

    }

    private void updateServiceStatus() {
        serviceEnabled = AccessibilityUtils.isAccessibilitySettingsOn();
        /*serviceEnabled = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager == null) return;
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.fragment05.accessible_service.AutoLoginService")) {
                serviceEnabled = true;
                break;
            }
        }*/
        autoLoginServiceTV.setText(serviceEnabled ? "已开启" : "未开启");

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
            case AutoLoginService.LESHI:
                s = "乐视视频";
                break;
        }
        if (!TextUtils.isEmpty(s)){
            Toasty.info(this,"您有"+s+"登录记录，你需要执行该应用的退出操作才能使用此应用平台的账号",Toast.LENGTH_LONG).show();
        }
    }
    public void setTag(){
        tagLeshi.setVisibility(View.INVISIBLE);
        tagIqiyi.setVisibility(View.INVISIBLE);
        tagYouku.setVisibility(View.INVISIBLE);
        tagTencent.setVisibility(View.INVISIBLE);
        if (TextUtils.isEmpty(logined)) return;
        switch (logined){
            case AutoLoginService.IQIYI:
                tagLeshi.setVisibility(View.VISIBLE);
                break;

            case AutoLoginService.YOUKU:
                tagYouku.setVisibility(View.VISIBLE);
                break;

            case AutoLoginService.TENCENT:
                tagTencent.setVisibility(View.VISIBLE);
                break;
            case AutoLoginService.LESHI:
                tagLeshi.setVisibility(View.VISIBLE);
                break;
                default:break;
        }
    }
    public void onClickListener(View v){
        if (serviceEnabled){
            if (AutoLoginService.getInstance() == null){
                Toast.makeText(this, "自助登录服务发生故障，你需要重启本应用", Toast.LENGTH_LONG).show();
                return;
            }
            if (NetUtil.getNetWorkState(this) != NetUtil.NETWORK_NONE){
                switch (v.getId()){
                    case R.id.btn_login_leshi://乐视视频登录
                        if (!(logined.equals(AutoLoginService.LESHI) || (!logined.equals(AutoLoginService.LESHI) && !TextUtils.isEmpty(new PreferenceUtil(getApplicationContext()).read("lastChosenTV"))))){
                            Toasty.error(StartActivity.this, "安装AreaParty电视端并与手机连接后获得乐视视频的使用权限",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (logined.equals(AutoLoginService.LESHI) || logined.equals("null")){
                            if (leshiVersionCode != 0){
                                openPackage(this,AutoLoginService.LESHI);
                                AutoLoginService.state = AutoLoginService.LESHI_LOGIN;
                                FloatView.registerVip("iqiyi");
                            }else {
                                Toasty.error(StartActivity.this, "你未安装乐视视频").show();
                            }
                        }else {
                            toast();
                        }

                        break;
                    case R.id.btn_logout_leshi://乐视视频登出
                        if (logined.equals(AutoLoginService.LESHI) || logined.equals("null")){
                            if (leshiVersionCode != 0){
                                openPackage(this,AutoLoginService.LESHI);
                                AutoLoginService.state = AutoLoginService.LESHI_LOGOUT;
                                if (!MyApplication.mFloatView.isShow()){
                                    MyApplication.mFloatView.show();

                                }
                            }else {
                                Toasty.error(StartActivity.this, "你未安装乐视视频").show();

                            }
                        }else {
                            toast();
                        }
                        break;
                    case R.id.btn_login_youku://优酷登录
                        if (!(logined.equals(AutoLoginService.YOUKU) || (!logined.equals(AutoLoginService.YOUKU) && !TextUtils.isEmpty(new PreferenceUtil(getApplicationContext()).read("lastChosenPC"))))){
                            Toasty.error(StartActivity.this, "安装AreaParty电脑端并与手机连接后获得优酷的使用权限",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (logined.equals(AutoLoginService.YOUKU) || logined.equals("null")){
                            if (youkuVersionCode != 0){
                                openPackage(this,AutoLoginService.YOUKU);
                                AutoLoginService.state = AutoLoginService.YOUKU_LOGIN;
                                FloatView.registerVip("youku");
                            }else {
                                Toasty.error(StartActivity.this, "你未安装优酷视频").show();
                            }
                        }else {
                            toast();
                        }

                        break;
                    case R.id.btn_logout_youku://优酷登出
                        if (logined.equals(AutoLoginService.YOUKU) || logined.equals("null")) {
                            if (youkuVersionCode != 0) {
                                openPackage(this, AutoLoginService.YOUKU);
                                AutoLoginService.state = AutoLoginService.YOUKU_LOGOUT;
                                if (!MyApplication.mFloatView.isShow()) {
                                    MyApplication.mFloatView.show();

                                }
                            } else {
                                Toasty.error(StartActivity.this, "你未安装优酷视频").show();
                            }
                        }else {
                            toast();
                        }
                        break;
                    case R.id.btn_login_tencent://腾讯登录
                        if (logined.equals(AutoLoginService.TENCENT) || logined.equals("null")){
                            if (tencentVersionCode != 0){
                                openPackage(this,AutoLoginService.TENCENT);
                                AutoLoginService.state = AutoLoginService.TENCENT_LOGIN;
                                FloatView.registerVip("tencent");
                            }else {
                                Toasty.error(StartActivity.this, "你未安装腾讯视频").show();
                            }
                        }else {
                            toast();
                        }

                        break;
                    case R.id.btn_logout_tencent://腾讯登出
                        if (logined.equals(AutoLoginService.TENCENT) || logined.equals("null")) {
                            if (tencentVersionCode != 0) {
                                openPackage(this, AutoLoginService.TENCENT);
                                AutoLoginService.state = AutoLoginService.TENCENT_LOGOUT;
                                if (!MyApplication.mFloatView.isShow()) {
                                    MyApplication.mFloatView.show();

                                }
                            } else {
                                Toasty.error(StartActivity.this, "你未安装腾讯视频").show();
                            }
                        }else {
                            toast();
                        }
                        break;
                    default:
                        break;
                }
            }else {
                Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toasty.warning(StartActivity.this, "请先开启自助登录服务").show();
        }
    }

    public  void getWebSiteUrl(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+ IPAddressConst.statisticServer_ip+"/bt_website/get_data.json").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                response.close();
                //Log.w("getWebSiteUrl",responseData);
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    StartActivity.url = new String[5];
                    StartActivity.imageUrl = new String[5];
                    for (int i =0 ; i< jsonArray.length() ; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        StartActivity.url[i] = jsonObject.getString("url");
                        StartActivity.imageUrl[i] = "http://"+IPAddressConst.statisticServer_ip+"/bt_website/"+jsonObject.getString("image");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initWebSite();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void initWebSite(){
        for (int i = 0; i< 5; i++){
            Glide.with(this).load(imageUrl[i]).into(image[i]);
            btn[i].setText(url[i].replace("http://",""));
            image[i].setOnClickListener(this);
            btn[i].setOnClickListener(this);
        }
    }

}
