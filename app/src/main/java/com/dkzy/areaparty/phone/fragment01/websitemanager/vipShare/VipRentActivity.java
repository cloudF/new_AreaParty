package com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_help;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_vipLease;
import com.dkzy.areaparty.phone.fragment01.websitemanager.alipay.PayResult;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.AutoLoginHelperActivity;
import com.dkzy.areaparty.phone.fragment05.accessible_service.AutoLoginService;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.floatview.FloatView;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;
import com.dkzy.areaparty.phone.utilseverywhere.utils.AccessibilityUtils;
import com.dkzy.areaparty.phone.utilseverywhere.utils.IntentUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import info.hoang8f.widget.FButton;
import protocol.Msg.VipMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.Login.getAdresseMAC;
import static com.dkzy.areaparty.phone.myapplication.floatview.FloatView.clearDataDelay;

public class VipRentActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int SDK_PAY_FLAG = 1;

    public static int appId = -1;
    public static String account;
    public static String password;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Log.w("VipRent",payResult.toString());
                        Toast.makeText(VipRentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        sendVipRentRequest(msg.arg1,false,false);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(VipRentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    private FButton btn_login_iqiyi, btn_logout_iqiyi, btn_login_youku, btn_logout_youku, btn_login_tencent, btn_logout_tencent;
    private ImageView tagIqiyi,tagYouku,tagTencent;

    private TextView floatViewTV, autoLoginServiceTV, autoLoginHelper,messageTV;
    private boolean newUser = false;
    private boolean mainPhoneChanged = false;

    public static int iqiyiVersionCode;//81070测试版本号
    public static int youkuVersionCode;//155测试版本号
    public static int tencentVersionCode;//15689测试版本号
    public static int QQVersionCode;

    public static boolean serviceEnabled;

    private int tentcentStatus = -1;
    private int iqiyiStatus = -1;
    private int youkuStatus = -1;

    private int tentcentAllocationId = -1;
    private int iqiyiAllocationId = -1;
    private int youkuAllocationId = -1;

    private String outTradeNo = "";
    private String money = "";

    private boolean hasFreeAccount = false;
    private boolean hasPayAccount = false;

    @Override
    protected void onStart() {
        super.onStart();
        getRentHistory();
        updateServiceStatus();
        iqiyiVersionCode = getVersionCode(AutoLoginService.IQIYI);//获取爱奇艺的版本号
        youkuVersionCode = getVersionCode(AutoLoginService.YOUKU);//获取优酷的版本号
        tencentVersionCode = getVersionCode(AutoLoginService.TENCENT);//获取腾讯视频版本号
        QQVersionCode = getVersionCode(AutoLoginService.QQ);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_rent);
        EventBus.getDefault().register(this);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                startActivity(new Intent(VipRentActivity.this,AutoLoginHelperActivity.class));
                break;
            case R.id.btn_login_iqiyi:
                if (((FButton)view).getText().equals("申请")){
                    testVipRent(1);
                }else if (((FButton)view).getText().equals("登录")){
                    AutoLoginService.state = AutoLoginService.IQIYI_LOGIN;
                    openApp(1);
                }

                break;
            case R.id.btn_logout_iqiyi:
                if (tagIqiyi.getVisibility() == View.VISIBLE){
                    complain(1);
                }else {
                    Toast.makeText(this, "你当月未租用 "+getAppName(1)+" VIP账号", Toast.LENGTH_SHORT).show();
                }
                
                break;
            case R.id.btn_login_youku:
                if (((FButton)view).getText().equals("申请")){
                    testVipRent(2);
                }else if (((FButton)view).getText().equals("登录")){
                    AutoLoginService.state = AutoLoginService.YOUKU_LOGIN;
                    openApp(2);
                }
                break;
            case R.id.btn_logout_youku:
                if (tagYouku.getVisibility() == View.VISIBLE){
                    complain(2);
                }else {
                    Toast.makeText(this, "你当月未租用 "+getAppName(2)+" VIP账号", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_login_tencent:
                if (((FButton)view).getText().equals("申请")){
                    testVipRent(0);
                }else if (((FButton)view).getText().equals("登录")){
                    AutoLoginService.state = AutoLoginService.TENCENT_LOGIN;
                    openApp(0);
                }
                break;
            case R.id.btn_logout_tencent:
                if (tagTencent.getVisibility() == View.VISIBLE){
                    complain(0);
                }else {
                    Toast.makeText(this, "你当月未租用 "+getAppName(3)+" VIP账号", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.helpInfo:
                //showHelpInfoDialog(R.layout.dialog_web);
                break;
            case R.id.img_tencent:
                openApp(0);
                break;
            case R.id.img_youku:
                openApp(2);
                break;
            case R.id.img_iqiyi:
                openApp(1);
                break;
        }

    }

    private void initViews(){
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
        findViewById(R.id.img_tencent).setOnClickListener(this);
        findViewById(R.id.img_iqiyi).setOnClickListener(this);
        findViewById(R.id.img_youku).setOnClickListener(this);
        messageTV = (TextView) findViewById(R.id.messageTV);
    }

    private void complain(final int appId){
        final ActionDialog_help dialog = new ActionDialog_help(this);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setTitleText(getAppName(appId) + "账号申诉");
        switch (getStatus(appId)){
            case 1: {
                dialog.setMessageTV("若你申请的账号有以下问题：1.登录过程中提示账号或密码错误； 2.验证码无法获取导致不能登录； 3.登录成功但该账号未开通VIP； 则你可以申请退款。\n你当前符合退款条件\n上次登录的检测结果为：申请账号后未执行登录操作");
                dialog.setPositiveButtonText("申请退款");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        sendRedundRequest(getAllocationId(appId));
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
                break;
            case 2:{
                dialog.setMessageTV("若你申请的账号有以下问题：1.登录过程中提示账号或密码错误； 2.验证码无法获取导致不能登录； 3.登录成功但该账号未开通VIP； 则你可以申请退款。\n你当前不符合退款条件，若您的账号存在以上问题，请您重新严格按照登录说明进行登录操作，以便系统进行检测\n上次登录的检测结果为：操作步骤未完成");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
                break;
            case 3:{
                dialog.setMessageTV("若你申请的账号有以下问题：1.登录过程中提示账号或密码错误； 2.验证码无法获取导致不能登录； 3.登录成功但该账号未开通VIP； 则你可以申请退款。\n你当前不符合退款条件，若您的账号存在以上问题，请您重新严格按照登录说明进行登录操作，以便系统进行检测\n上次登录的检测结果为：登录成功");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
                break;
            case 4:{
                dialog.setMessageTV("若你申请的账号有以下问题：1.登录过程中提示账号或密码错误； 2.验证码无法获取导致不能登录； 3.登录成功但该账号未开通VIP； 则你可以申请退款。\n你当前符合退款条件\n上次登录的检测结果为：登录成功但该账号未开通VIP");
                dialog.setPositiveButtonText("申请退款");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        sendRedundRequest(getAllocationId(appId));
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
                break;
            case 5:{
                dialog.setMessageTV("若你申请的账号有以下问题：1.登录过程中提示账号或密码错误； 2.验证码无法获取导致不能登录； 3.登录成功但该账号未开通VIP； 则你可以申请退款。\n你当前符合退款条件\n上次登录的检测结果为：登录过程失败");
                dialog.setPositiveButtonText("申请退款");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        sendRedundRequest(getAllocationId(appId));
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
                break;
            default: {
                dialog.setMessageTV("若你申请的账号有以下问题：1.登录过程中提示账号或密码错误； 2.验证码无法获取导致不能登录； 3.登录成功但该账号未开通VIP； 则你可以申请退款。\n你当前不符合退款条件，若您的账号存在以上问题，请您严格按照登录说明进行登录操作，以便系统进行检测");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
            break;
        }

    }
    private int getStatus(int appId){
        switch (appId){
            case 0:
                return tentcentStatus;
            case 1:
                return iqiyiStatus;
            case 2:
                return  youkuStatus;
            default: return -1;
        }
    }
    private int getAllocationId(int appId){
        switch (appId){
            case 0:
                return tentcentAllocationId;
            case 1:
                return iqiyiAllocationId;
            case 2:
                return  youkuAllocationId;
            default: return -1;
        }
    }

    private void updateServiceStatus() {
        serviceEnabled = AccessibilityUtils.isAccessibilitySettingsOn();
        autoLoginServiceTV.setText(serviceEnabled ? "已开启" : "未开启");

    }
    public static int getVersionCode(String packageName){
        PackageManager packageManager = MyApplication.getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            int versionCode = packageInfo.versionCode;
            Log.w("chg",""+"packageName-->"+packageName+"--versionCode-->"+versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }
        return 0;
    }

    public static void openApp(int appId){
        if (getVersionCode(appId)!=0){
            openPackage(MyApplication.context,getPackageName(appId));
        }else {
            Toasty.error(MyApplication.context, "你未安装 "+getAppName(appId)).show();
        }
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

    public void testVipRent(int applicationId){
        if (serviceEnabled){
            if (AutoLoginService.getInstance() == null){
                Toast.makeText(this, "自助登录服务发生故障，你需要重启本应用", Toast.LENGTH_LONG).show();
                return;
            }
            if (NetUtil.getNetWorkState(this) != NetUtil.NETWORK_NONE){
                switch (applicationId){
                    case 0:
                        if (tencentVersionCode == 0){
                            Toasty.error(VipRentActivity.this, "你未安装腾讯视频").show();
                            return;
                        }else {
                            if (tagTencent.getVisibility() == View.VISIBLE){
                                vipRent(applicationId);
                            }else {
                                sendVipRentRequest(applicationId,false,true);
                            }
                        }
                        break;
                    case 1:
                        if (iqiyiVersionCode == 0){
                            Toasty.error(VipRentActivity.this, "你未安装爱奇艺").show();
                            return;
                        }else {
                            if (tagIqiyi.getVisibility() == View.VISIBLE){
                                vipRent(applicationId);
                            }else {
                                sendVipRentRequest(applicationId,false,true);
                            }
                        }
                        break;
                    case 2:
                        if (youkuVersionCode == 0){
                            Toasty.error(VipRentActivity.this, "你未安装优酷视频").show();
                            return;
                        }else {
                            if (tagYouku.getVisibility() == View.VISIBLE){
                                vipRent(applicationId);
                            }else {
                                sendVipRentRequest(applicationId,false,true);
                            }
                        }
                        break;
                }
            }
            else {
                Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toasty.warning(this, "请先开启自助登录服务").show();
        }
    }

    public void vipRent(final int applicationId){
        if (serviceEnabled){
            if (AutoLoginService.getInstance() == null){
                Toast.makeText(this, "自助登录服务发生故障，你需要重启本应用", Toast.LENGTH_LONG).show();
                return;
            }
            if (NetUtil.getNetWorkState(this) != NetUtil.NETWORK_NONE){
                String appName = getAppName(applicationId);
                if (appId!=-1){
                    Toast.makeText(this, "正在获取 "+appName+"VIP账号", Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean oldRent = null;
                switch (applicationId){
                    case 0:
                        if (tencentVersionCode == 0){
                            Toasty.error(VipRentActivity.this, "你未安装腾讯视频").show();
                            return;
                        }else {
                            if (tagTencent.getVisibility() == View.VISIBLE){
                                oldRent = true;
                            }else oldRent = false;
                        }
                        break;
                    case 1:
                        if (iqiyiVersionCode == 0){
                            Toasty.error(VipRentActivity.this, "你未安装爱奇艺").show();
                            return;
                        }else {
                            if (tagIqiyi.getVisibility() == View.VISIBLE){
                                oldRent = true;
                            }else oldRent = false;
                        }
                        break;
                    case 2:
                        if (youkuVersionCode == 0){
                            Toasty.error(VipRentActivity.this, "你未安装优酷视频").show();
                            return;
                        }else {
                            if (tagYouku.getVisibility() == View.VISIBLE){
                                oldRent = true;
                            }else oldRent = false;
                        }
                        break;
                }
                final ActionDialog_help dialog = new ActionDialog_help(this);
                dialog.setCancelable(false);
                dialog.show();
                dialog.setTitleText(appName+"  VIP账号租用");

                /*if (newUser){
                    dialog.setMessageTV("您之前未租用过VIP账号，此次租用VIP账号将免费");
                    dialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            sendVipRentRequest(applicationId,false,false);
                        }
                    });
                }*/
                if (mainPhoneChanged){
                    dialog.setMessageTV("您本月租用VIP账号后修改了主设备，在当前设备禁止再次租用账号，请在申请VIP账号的设备上使用此功能或下个月再在当前设备使用此功能");
                    dialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else {
                    if (oldRent!=null && oldRent){
                        dialog.setMessageTV("您本月租用过"+appName+" VIP账号，确认可重新获取该账号进行登录");
                        dialog.setOnNegativeListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                sendVipRentRequest(applicationId,true,false);
                            }
                        });
                    }
                    else {

                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        final String money;
                        if (day < 15) money = "3.0";
                        else money = "1.5";

                        String freeText = "";
                        String payText = "";
                        if (applicationId != 0){
                            freeText = appName+ "暂不提供免费VIP账号";
                        }else {
                            if (hasFreeAccount){
                                freeText = "当前有免费"+appName+"VIP账号，点击确定获取";
                            }else {
                                freeText = "当前"+appName+"免费VIP账号已分配完毕，请下月1号再来申请免费账号。\n你也可以选择付费申请";
                            }

                        }

                        if (hasPayAccount){
                            payText = "您本月未租用过"+appName+" VIP账号，支付后可获取VIP账号并进行登录。\n支付金额： ￥"+money+"\n使用期限：至本月月末";
                        }

                        dialog.setSelectVisible(View.VISIBLE);
                        dialog.setFreeText(freeText);
                        dialog.setPayText(payText);

                        //dialog.setMessageTV("您本月未租用过"+appName+" VIP账号，支付后可获取VIP账号并进行登录。\n支付金额： ￥"+money+"\n使用期限：至本月月末");
                        dialog.setOnNegativeListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dialog.isFree()){
                                    if (applicationId==0 && hasFreeAccount){
                                        sendVipRentRequest(applicationId,false,false);
                                    }
                                }else{
                                    sendPayMessageToServer(applicationId,"0.01");
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
            else {
                Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toasty.warning(this, "请先开启自助登录服务").show();
        }


    }

    //isTest=true表示预分配,即查询数据库有无合适的账号供分配,有账号可分配时才需要付款，再执行实际的分配
    public  void sendVipRentRequest(final int applicationId, final boolean oldRent,final boolean isTest){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.VipRentInfoReq.Builder builder = VipMsg.VipRentInfoReq.newBuilder();
                    builder.setNewUser(newUser);
                    builder.setUserId(Login.userId);
                    builder.setUserMac(getAdresseMAC(MyApplication.context));
                    builder.setApplication(applicationId);
                    builder.setAddress(MyApplication.location);
                    builder.setTime(String.valueOf(System.currentTimeMillis()));
                    builder.setOldRent(oldRent);
                    if (oldRent){
                        builder.setOutTradeNo("");
                        builder.setMoney("");
                    }else {
                        builder.setOutTradeNo(outTradeNo);
                        builder.setMoney(money);
                        outTradeNo = "";
                        money = "";
                    }
                    int messageType;
                    if (!isTest){
                        messageType = ProtoHead.ENetworkMessage.VIP_RENTINFO_VALUE;
                    }else {
                        messageType = ProtoHead.ENetworkMessage.VIP_SHARE_VALUE;//查询有无可分配账号
                    }
                    byte[] byteArray = NetworkPacket.packMessage(messageType, builder.build().toByteArray());
                    Login.base.writeToServer(Login.outputStream,byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getRentHistory(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.VipRentReq.Builder builder = VipMsg.VipRentReq.newBuilder();
                    builder.setUserId(Login.userId);
                    builder.setUserMac(getAdresseMAC(VipRentActivity.this));
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.VIP_RENT_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Subscriber(tag = "vipRentInfo",mode = ThreadMode.MAIN)
    private void vipRentInfo(VipMsg.VipRentInfoRsp response){
        //Toast.makeText(this, "response:"+response, Toast.LENGTH_SHORT).show();
        if (response.getResultCode().equals(VipMsg.VipRentInfoRsp.ResultCode.FAIL)){
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        }else if (response.getResultCode().equals(VipMsg.VipRentInfoRsp.ResultCode.SUCCESS)){
            FloatView.allocationId = response.getAllocationId();
            FloatView.appId = response.getAppId();
            appId = response.getAppId();
            FloatView.name = response.getAccount();
            account = response.getAccount();
            FloatView.password = response.getPassword();
            password = response.getPassword();
            FloatView.providerId = response.getProviderId();
            FloatView.accountId = response.getAccountId();
            Log.w("vipRent",response.getMessage());
            Log.w("vipRent",response.getResultCode().toString());
            Log.w("vipRent","account:"+account);
            Log.w("vipRent","password:"+password);
            Log.w("vipRent","application:"+appId);
            AutoLoginService.state = getLoginState(appId);
            openApp(appId);
            /*final FButton btn = getAppLoginBtn(appId);
            btn.setText("登录");
            Toasty.success(this,getAppName(appId)+" VIP账号获取成功，请点击对应的登录按钮").show();*/

            clearDataDelay();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getRentHistory();
                    appId = -1;
                    password = "";
                    account = "";
                }
            }, 5*1000);
        }

    }

    @Subscriber(tag = "vipRentTest",mode = ThreadMode.MAIN)
    private void vipRentTest(VipMsg.VipRentInfoRsp response){
        //Toast.makeText(this, "response:"+response, Toast.LENGTH_SHORT).show();
        if (response.getResultCode().equals(VipMsg.VipRentInfoRsp.ResultCode.FAIL)){
            //Toast.makeText(this, response.getMessage()+",请稍后再试", Toast.LENGTH_SHORT).show();
            showMessage("申请失败",response.getMessage()+",请稍后再试");
        }else if (response.getResultCode().equals(VipMsg.VipRentInfoRsp.ResultCode.SUCCESS)){
            hasFreeAccount = response.getHasFreeAccount();
            hasPayAccount = response.getHasPayAccount();
            vipRent(response.getAppId());

        }

    }

    @Subscriber(tag = "vipRent",mode = ThreadMode.MAIN)
    private void VipRentRsp(VipMsg.VipRentRsp response){
        //Toast.makeText(this, "response:"+response, Toast.LENGTH_SHORT).show();
        Log.w("vipRent",response.getMessage());
        messageTV.setText(response.getMessage());
        if (response.getResultCode().equals(VipMsg.VipRentRsp.ResultCode.REGISTERED)){
            newUser = false;
            mainPhoneChanged = response.getMainPhoneChanged();
            Log.w("vipRent",mainPhoneChanged+"");
            if (response.getIqiyi()){
                tagIqiyi.setVisibility(View.VISIBLE);
                iqiyiStatus = response.getIqiyiStatus();
                iqiyiAllocationId = response.getIqiyiAllocationId();
            }else {
                tagIqiyi.setVisibility(View.INVISIBLE);
                iqiyiStatus = -1;
                iqiyiAllocationId = -1;
            }
            if (response.getTencent()){
                tagTencent.setVisibility(View.VISIBLE);
                tentcentStatus = response.getTencentStatus();
                tentcentAllocationId = response.getTencentAllocationId();
            }else {
                tagTencent.setVisibility(View.INVISIBLE);
                tentcentStatus = -1;
                tentcentAllocationId = -1;
            }
            if (response.getYouku()){
                tagYouku.setVisibility(View.VISIBLE);
                youkuStatus = response.getYoukuStatus();
                youkuAllocationId = response.getYoukuAllocationId();
            }else {
                tagYouku.setVisibility(View.INVISIBLE);
                youkuStatus = -1;
                youkuAllocationId = -1;
            }
        }else if (response.getResultCode().equals(VipMsg.VipRentRsp.ResultCode.NOTREGISTER)){
            newUser = true;
        }

    }

    @Subscriber(tag = "alipay", mode = ThreadMode.MAIN)
    private void alipay(final VipMsg.AliPayRsp response){
        if (response.getType().equals(VipMsg.AliPayRsp.Type.Pay)){
            if (response.getResultCode().equals(VipMsg.AliPayRsp.ResultCode.SUCCESS)){
                final String orderInfo = response.getOrderInfo();
                outTradeNo = response.getOutTradeNo();
                money = response.getMoney();
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(VipRentActivity.this);
                        Map<String, String> result = alipay.payV2(orderInfo, true);
                        Log.i("msp", result.toString());
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        msg.arg1 = response.getAppId();
                        mHandler.sendMessage(msg);
                    }
                };

                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }else {
                Log.w("vipRent", response.toString());
            }
        }
        else if (response.getType().equals(VipMsg.AliPayRsp.Type.Refund)){
            Toast.makeText(this, response.getOrderInfo(), Toast.LENGTH_SHORT).show();
            getRentHistory();
        }

    }

    public static int getVersionCode(int appId){
        switch (appId){
            case 0:
                return tencentVersionCode;
            case 1:
                return iqiyiVersionCode;
            case 2:
                return youkuVersionCode;
            default:
                return 0;
        }
    }
    public static String getAppName(int appId){
        switch (appId){
            case 0:
                return "腾讯视频";
            case 1:
                return "爱奇艺";
            case 2:
                return "优酷视频";
            default:
                return "";
        }
    }
    public static String getPackageName(int appId){
        switch (appId){
            case 0:
                return AutoLoginService.TENCENT;
            case 1:
                return AutoLoginService.IQIYI;
            case 2:
                return AutoLoginService.YOUKU;
            default:
                return "";
        }
    }
    public static int getLoginState(int appId){
        switch (appId){
            case 0:
                return AutoLoginService.TENCENT_LOGIN;
            case 1:
                return AutoLoginService.IQIYI_LOGIN;
            case 2:
                return AutoLoginService.YOUKU_LOGIN;
            default:
                return AutoLoginService.NO_ACTION;
        }
    }

    private FButton getAppLoginBtn(int appId){
        switch (appId){
            case 0:
                return btn_login_tencent;
            case 1:
                return btn_login_iqiyi;
            case 2:
                return btn_login_youku;
            default:
                return btn_login_tencent;
        }
    }
    /**
     * 支付宝支付业务

    public void payV2(String money, final int applicationId) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         *
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, money);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);//本地加签
        final String orderInfo = orderParam + "&" + sign;//价签后的订单信息

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(VipRentActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                msg.arg1 = applicationId;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }*/

    public void sendPayMessageToServer(final int appId,final String money){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.AliPayReq.Builder builder = VipMsg.AliPayReq.newBuilder();
                    builder.setType(VipMsg.AliPayReq.Type.Pay);
                    builder.setUserId(Login.userId);
                    builder.setAppId(appId);
                    builder.setMoney(money);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ALIPAY_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendRedundRequest(final int allocationId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.AliPayReq.Builder builder = VipMsg.AliPayReq.newBuilder();
                    builder.setType(VipMsg.AliPayReq.Type.Refund);
                    builder.setAllocationId(allocationId);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ALIPAY_VALUE, builder.build().toByteArray());
                    if (Login.base != null){
                        Login.base.writeToServer(Login.outputStream,byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showMessage(String title, String text){
        final ActionDialog_vipLease dialog = new ActionDialog_vipLease(this);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setText(text);
        dialog.setTitle(title);
    }
}
