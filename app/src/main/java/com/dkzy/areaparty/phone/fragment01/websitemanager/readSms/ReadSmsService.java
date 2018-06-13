package com.dkzy.areaparty.phone.fragment01.websitemanager.readSms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Base;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protocol.Msg.VipMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.Login.REGEX_MOBILE;
import static com.dkzy.areaparty.phone.Login.base;
import static com.dkzy.areaparty.phone.Login.getAdresseMAC;
import static com.dkzy.areaparty.phone.Login.host;
import static com.dkzy.areaparty.phone.Login.login;
import static com.dkzy.areaparty.phone.Login.port;
import static com.dkzy.areaparty.phone.Login.outputStream;
import static com.dkzy.areaparty.phone.Login.socket;
import static com.dkzy.areaparty.phone.Login.userId;
import static com.dkzy.areaparty.phone.Login.userPwd;
import static com.dkzy.areaparty.phone.Login.userMac;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.getContext;

public class ReadSmsService extends Service {
    public static final String ONCLICK = "com.app.onclick";
    private static final String TAG = ReadSmsService.class.getSimpleName();
    public static Timer timer;
    Base base;
    ReadSmsService.SMSBroadcastReceiver  mSMSBroadcastReceiver;
    public ReadSmsService() {
    }

    private int appId = -1;
    private String id = "";
    private String providerId = "";
    private int accountId = -1;

    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ONCLICK)) {
                Log.w("ReadSmsService",ONCLICK);
                VipRentActivity.openPackage(getContext(), getContext().getPackageName());
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        base = Login.base;
        Log.w(TAG,"OnCreat");
        EventBus.getDefault().register(this);

        IntentFilter filter_click = new IntentFilter();
        filter_click.addAction(ONCLICK);
        //注册广播
        registerReceiver(receiver_onclick, filter_click);
        Intent Intent_pre = new Intent(ONCLICK);
        PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);

        Notification notification = new Notification.Builder(this).setContentTitle("AreaParty")
                .setContentText("AreaParty正在运行中...").setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.app_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app_logo))
                .setContentIntent(pendIntent_click).build();
        startForeground(2,notification);
        mSMSBroadcastReceiver = new ReadSmsService.SMSBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(SMSBroadcastReceiver.SMS_RECEIVED_ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);
        mSMSBroadcastReceiver.setOnMesReceiveListener(new ReceiveListener() {
            @Override
            public void onReceive(final String time, final String content) {
                if (appId == 1 && content.startsWith("【爱奇艺】")){
                    String code = getDynamicPassword(content);
                    if (!TextUtils.isEmpty(code)){
                        sendCodeToUser(code);
                    }
                }else if (appId == 2 && content.startsWith("【优酷土豆】")){
                    String code = getDynamicPassword(content);
                    if (!TextUtils.isEmpty(code)){
                        sendCodeToUser(code);
                    }
                }else if (appId == 3 && content.startsWith("【区联科技】")){
                    String code = getDynamicPassword(content);
                    if (!TextUtils.isEmpty(code)){
                        sendSmsVerifyMsg(code);
                    }
                }

            }
        });
    }
    private void sendSmsVerifyMsg(final String code){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.SmsTestVerifyReq.Builder builder = VipMsg.SmsTestVerifyReq.newBuilder();
                    builder.setUserId(userId);
                    builder.setCode(code);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SMS_TEST_VERIFY_VALUE, builder.build().toByteArray());
                    if (base != null){
                        base.writeToServer(outputStream,byteArray);
                        Log.w("vipLeaseCode","获取到验证码"+code);
                    }
                    clearData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void sendCodeToUser(final String code){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.VipLeaseYZMReq.Builder builder = VipMsg.VipLeaseYZMReq.newBuilder();
                    builder.setAccountId(accountId);
                    builder.setAppId(appId);
                    builder.setProviderId(providerId);
                    builder.setResultCode(VipMsg.VipLeaseYZMReq.ResultCode.SUCCESS);
                    builder.setYzmCode(code);
                    builder.setUserId(id);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.VIP_LEASE_CODE_VALUE, builder.build().toByteArray());
                    if (base != null){
                        base.writeToServer(outputStream,byteArray);
                        Log.w("vipLeaseCode","获取到验证码"+code);
                    }
                    clearData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void noCodeRev(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.w("vipLeaseCode","未获取到验证码");
                    VipMsg.VipLeaseYZMReq.Builder builder = VipMsg.VipLeaseYZMReq.newBuilder();
                    builder.setAccountId(accountId);
                    builder.setAppId(appId);
                    builder.setProviderId(providerId);
                    builder.setResultCode(VipMsg.VipLeaseYZMReq.ResultCode.FAIL);
                    builder.setYzmCode("60s之后账号提供方未获取到验证码，请重试");
                    builder.setUserId(id);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.VIP_LEASE_CODE_VALUE, builder.build().toByteArray());
                    if (base != null){
                        base.writeToServer(outputStream,byteArray);
                    }
                    clearData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Subscriber(tag = "vipLeaseCode")
    private void vipLeaseCode(VipMsg.VipLeaseYZMRsp response){
        appId = response.getAppId();
        id = response.getUserId();
        providerId = response.getProviderId();
        accountId = response.getAccountId();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appId != -1){
                    noCodeRev();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MyApplication.getInstance().showNotification("AreaParty:未读取到验证码","未能正确获取到验证码，您的账号不能被租用，请检查是否正确设置验证码权限，然后在账号出租界面重新验证短信读取功能");
                        }
                    }).start();
                }
            }
        },60 * 1000);
    }

    @Subscriber(tag = "smsVerify")
    private void smsVerify(boolean b){
        if (b){
            appId = 3;
        }
    }
    private void clearData(){
        appId = -1;
        id = "";
        providerId = "";
        accountId = -1;
    }

    @Override
    public void onDestroy() {
        if (timer != null){
            try {
                timer.cancel();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        stopForeground(true);
        unregisterReceiver(mSMSBroadcastReceiver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public interface ReceiveListener{
        void onReceive(String time, String content);
    }

    public class SMSBroadcastReceiver extends BroadcastReceiver {
        public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
        public ReceiveListener listener;

        public SMSBroadcastReceiver(){
            super();
        }

        public void setOnMesReceiveListener(ReceiveListener listener){
            this.listener = listener;
        }


        @Override
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                for(Object pdu:pdus) {
                    String format = intent.getStringExtra("format");
                    SmsMessage smsMessage;
                    if(Build.VERSION.SDK_INT < 23){
                        smsMessage = SmsMessage.createFromPdu((byte[])pdu) ;
                    }else{
                        smsMessage = SmsMessage.createFromPdu((byte[])pdu,format) ;
                    }
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    final String content = smsMessage.getDisplayMessageBody();
                    long date = smsMessage.getTimestampMillis();
                    Date tiemDate = new Date(date);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = simpleDateFormat.format(tiemDate);
                    if(!TextUtils.isEmpty(content)){
                        Log.w(TAG,content);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MyApplication.showToast(content);
                            }
                        }).start();

                        listener.onReceive(time,content);
                        //SharedPreferenceUtils.putString(time,content);
                    }else {
                        Log.w("vipLeaseCode","empty message");
                    }

                }
            }
        }

        /**
         * 从字符串中截取连续6位数字组合 ([0-9]{" + 6 + "})截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
         * @param str 短信内容
         * @return 截取得到的6位动态密码
         */

    }
    public  String getDynamicPassword(String str){
        // 6是验证码的位数,一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{" + 6 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            dynamicPassword = m.group();
        }
        return dynamicPassword;
    }


    public static  void timer() {

        // TODO Auto-generated method stub
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (socket == null || !socket.isConnected()){
                    Log.w(TAG, "socket有问题");

                    if (!Login.autoLogin){
                        timer.cancel();

                    }else {
                        if (NetUtil.getNetWorkState(MyApplication.getContext()) != NetUtil.NETWORK_NONE){
                            reLogin();
                            Log.w("login","socket故障而重新登录");
                        }
                    }
                }else {
                    //Log.w(TAG, "socket已连接");
                }
            }
        }, new Date(),1000 * 10);//每10s
    }

    public static void reLogin(){
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        boolean autoLogin = sp.getBoolean("autoLogin",false);
        if (!autoLogin) return;
        if (NetUtil.getNetWorkState(MyApplication.getContext()) == NetUtil.NETWORK_NONE) return;
        SharedPreferences sp2 = MyApplication.getContext().getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        userId = sp.getString("USER_ID", "");
        userPwd = sp.getString("USER_PWD", "");

        port = Integer.parseInt(sp2.getString("SERVER_PORT", "3333"));
        host = sp2.getString("SERVER_IP", AREAPARTY_NET);
        userMac = getAdresseMAC(MyApplication.getContext());
        if (!TextUtils.isEmpty(host)){
            AREAPARTY_NET = host;
        }else {
            host = AREAPARTY_NET;
        }

        if (!TextUtils.isEmpty(userPwd) && !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userMac)) {//自动登录
            if (Pattern.matches(REGEX_MOBILE, userId)) {
                String id = sp.getString(userId, "");
                if (!TextUtils.isEmpty(id)) {
                    userId = id;
                }
            }
            if (NetUtil.getNetWorkState(MyApplication.getContext()) != NetUtil.NETWORK_NONE){
                new Thread(login).start();
            }
        }
    }
}
