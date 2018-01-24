package com.dkzy.areaparty.phone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_help;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_launch;
import com.dkzy.areaparty.phone.fragment06.myChatList;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.register.RegisterUserInfo;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import protocol.Data.ChatData;
import protocol.Data.FileData;
import protocol.Data.UserData;
import protocol.Msg.AccreditMsg;
import protocol.Msg.GetUserInfoMsg;
import protocol.Msg.LoginMsg;
import protocol.ProtoHead;
import server.NetworkPacket;
import tools.DataTypeTranslater;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.GetInetAddress;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.domain;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.getContext;

/**
 * Created by SnowMonkey on 2016/12/29.
 */

public class Login extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_PHONE_STATE
    };

    public static Base base = null;
    private LoginMsg.LoginReq.Builder builder = LoginMsg.LoginReq.newBuilder();
    private GetUserInfoMsg.GetUserInfoReq.Builder userBuilder = GetUserInfoMsg.GetUserInfoReq.newBuilder();
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private TextView mRegisterButton;                 //注册按钮
    private TextView login_btn_outline;                //离线登录按钮
    private Button mLoginButton;                      //登录按钮
    private ImageView mSettingButton;               //设置按钮
    private ImageView login_userHead;

    private View accreditView;
    private AlertDialog dialog = null;

    private String host;
    private int port = 0;
    //private int port = 3333;
    private SharedPreferences sp;
    private SharedPreferences sp2;
    private long timer = 0;
    public static Handler mHandler;
    public static final int HEAD_INT_SIZE = 4;
    public static Socket socket = null;
    public static InputStream inputStream = null;
    public static OutputStream outputStream = null;
    public static List<UserData.UserItem> userFriend = new ArrayList<>();
    public static List<UserData.UserItem> userNet = new ArrayList<>();
    public static List<UserData.UserItem> userShare = new ArrayList<>();
    public static List<FileData.FileItem> files = new ArrayList<>();
    public static boolean outline = false;
    public static String userId = "";
    public static String userName = "";
    public static String userMac;
    public static boolean mainMobile;
    public static int userHeadIndex;
    public static myChatList myChats = new myChatList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }//
        setContentView(R.layout.login);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyApplication.getInstance().addActivity(this);
        if (!(new PreferenceUtil("isHelpDialogShow",getApplicationContext()).readBoole("launch"))){//提示框
            showDialog();
        }

        mAccount = (EditText) findViewById(R.id.login_edit_account);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (TextView) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        mRegisterButton = (TextView) findViewById(R.id.login_btn_register);
        login_btn_outline = (TextView) findViewById(R.id.login_btn_outline);
        mSettingButton = (ImageView) findViewById(R.id.login_btn_setting);
        login_userHead = (ImageView) findViewById(R.id.login_userHead);

        accreditView = LayoutInflater.from(this).inflate(R.layout.login_accredit_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setView(accreditView);
        dialog = builder.create();

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        sp2 = this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        mAccount.setText(sp.getString("USER_ID", ""));
        mPwd.setText(sp.getString("USER_PWD", ""));
        port = Integer.parseInt(sp2.getString("SERVER_PORT", "3333"));
        host = sp2.getString("SERVER_IP", AREAPARTY_NET);
        if (!TextUtils.isEmpty(host)){
            AREAPARTY_NET = host;
        }
//        if(outline == true){
//            mLoginButton.setText("离线登录");
//            mLoginButton.setBackgroundColor(Color.parseColor("#e65757"));
//        }
//        else{
//            mLoginButton.setText("登录");
//            mLoginButton.setBackgroundColor(Color.parseColor("#34adfd"));
//        }
        mLoginButton.setOnClickListener(mListener);
        mSettingButton.setOnClickListener(mListener);
        login_btn_outline.setOnClickListener(mListener);
        mRegisterButton.setOnClickListener(mListener);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(Login.this, "请输入用户名密码", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(Login.this, "登录失败，用户名或密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //跳转主界面
                        Intent intentMain = new Intent();
                        intentMain.setClass(Login.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        outline = false;
                        bundle.putBoolean("outline", false);
                        bundle.putString("userId", userId);
                        bundle.putString("userName", userName);
                        bundle.putInt("userHeadIndex", userHeadIndex);
                        bundle.putSerializable("chats", myChats);
                        intentMain.putExtras(bundle);
                        startActivity(intentMain);
                        //Login.this.finish();
                        break;
                    case 3:
                        //跳转设置界面
                        Intent intentSetting = new Intent(Login.this, LoginSetting.class);
                        Bundle bundleSetting = new Bundle();
                        bundleSetting.putString("ip", host);
                        bundleSetting.putString("port", String.valueOf(port));
                        bundleSetting.putBoolean("outline", outline);
                        intentSetting.putExtras(bundleSetting);
                        startActivityForResult(intentSetting, 0);
                        break;
                    case 4:
                        //离线登录
                        outline = true;
                        break;
                    case 5:
                        //正常登录
                        outline = false;
                        break;
                    case 6:
                        //接收用户注册后返回的用户名密码
                        String s = (String) msg.obj;
                        String userId = s.split(":")[0];
                        String psw = s.split(":")[1];
                        mAccount.setText(userId);
                        mPwd.setText(psw);
                        break;
                    case 7:
                        Toast.makeText(Login.this, "您的账号已登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        //弹出提示框，告诉用户等待授权
                        ((TextView) accreditView.findViewById(R.id.accreditDialogTitle)).setText("等待主设备授权");
                        ((TextView) accreditView.findViewById(R.id.accreditDialogInfo)).setText("请在注册该账号的设备上登录进行授权");
                        ((Button) accreditView.findViewById(R.id.accreditDialogConfirm)).setOnClickListener(mListener);
                        dialog.show();
                        break;
                    case 9:
                        //弹出提示框，告诉用户主设备不在线
                        ((TextView) accreditView.findViewById(R.id.accreditDialogTitle)).setText("主设备不在线");
                        ((TextView) accreditView.findViewById(R.id.accreditDialogInfo)).setText("请打开主设备进行授权操作");
                        ((Button) accreditView.findViewById(R.id.accreditDialogConfirm)).setOnClickListener(mListener);
                        dialog.show();
                        break;
                    case 10:
                        ((TextView) accreditView.findViewById(R.id.accreditDialogTitle)).setText("授权失败");
                        ((TextView) accreditView.findViewById(R.id.accreditDialogInfo)).setText("主设备拒绝授权您的手机使用该账号登录");
                        ((Button) accreditView.findViewById(R.id.accreditDialogConfirm)).setOnClickListener(mListener);
                        dialog.show();
                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(10000);
                    Socket socket = server.accept();
                    byte[] b = new byte[20000];
                    int read = 0;
                    InputStream is = socket.getInputStream();
                    read = is.read(b);
                    //  = socket.getInputStream().read();
                    System.out.println(read);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void tryLogin() {
        Date now = new Date();
        if (TextUtils.isEmpty(host)){
            host = sp2.getString("SERVER_IP", AREAPARTY_NET);
            if (TextUtils.isEmpty(host)){
                host = AREAPARTY_NET;
                if (TextUtils.isEmpty(host)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AREAPARTY_NET = GetInetAddress(domain);
                        }
                    }).start();
                    return;
                }
            }
        }
        if (port == 0){
            port = Integer.parseInt(sp2.getString("SERVER_PORT", "3333"));
            if (port == 0){
                port = 3333;
            }
        }
        if (outline == false) {
            if (now.getTime() - timer > 3000) {
                try {
                    new Thread(login).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                timer = now.getTime();
            } else {
                Toast.makeText(Login.this, "正在登陆", Toast.LENGTH_SHORT).show();
            }
        }
    }

    OnClickListener mListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    if (verifyStoragePermissions(Login.this)) {
                        tryLogin();
                    }

                    break;
                case R.id.login_btn_setting:                            //登录界面的设置按钮
                    mHandler.sendEmptyMessage(3);
                    break;
                case R.id.login_btn_outline:
                    userId = "";
                    Intent intentMain = new Intent();
                    intentMain.setClass(Login.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("outline", true);
                    intentMain.putExtras(bundle);
                    startActivity(intentMain);
//                    Login.this.finish();
                    break;
                case R.id.login_btn_register:                           //登录界面的注册按钮
                    Intent intentRegister = new Intent();
                    intentRegister.setClass(Login.this, RegisterUserInfo.class);
                    startActivity(intentRegister);
                    break;
                case R.id.accreditDialogConfirm:
                    dialog.dismiss();
                    break;
            }
        }
    };

    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String mtyb = android.os.Build.BRAND;// 手机品牌
        String mtype = android.os.Build.MODEL; // 手机型号
        String carrier = android.os.Build.MANUFACTURER;//手机厂商
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return carrier + " " + mtyb;
        }
        String imei = tm.getDeviceId();
        String imsi = tm.getSubscriberId();
        String numer = tm.getLine1Number(); // 手机号码
        String serviceName = tm.getSimOperatorName(); // 运营商
        System.out.println("品牌: " + mtyb + "\n" + "型号: " + mtype + "\n" + "版本: Android "
                + android.os.Build.VERSION.RELEASE + "\n" + "IMEI: " + imei
                + "\n" + "IMSI: " + imsi + "\n" + "手机号码: " + numer + "\n"
                + "运营商: " + serviceName + "\n" + "手机厂商: " + carrier);
        return carrier + " " + mtyb;
    }

    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    public static String getAdresseMAC(Context context) {
        String mac = getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("USER_MAC","");
        if (TextUtils.isEmpty(mac)){
            WifiManager wifiMan = (WifiManager)context.getSystemService(Context.WIFI_SERVICE) ;
            WifiInfo wifiInf = wifiMan.getConnectionInfo();

            if(wifiInf !=null && marshmallowMacAddress.equals(wifiInf.getMacAddress())){
                String result = null;
                try {
                    result= getAdressMacByInterface();
                    if (result != null){
                        return result;
                    } else {
                        result = getAddressMacByFile(wifiMan);
                        return result;
                    }
                } catch (IOException e) {
                    Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
                } catch (Exception e) {
                    Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
                }
            } else{
                if (wifiInf != null && wifiInf.getMacAddress() != null) {
                    return wifiInf.getMacAddress();
                } else {
                    return "";
                }
            }
            return marshmallowMacAddress;
        }else {
            return mac;
        }

    }

    private static String getAdressMacByInterface(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:",b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==1){
            Bundle bundle = data.getExtras();
            if(bundle!=null){
                host = bundle.getString("ip");
                port = Integer.parseInt(bundle.getString("port"));
                AREAPARTY_NET = host;
                if(outline == true){
                    mLoginButton.setText("离线登录");
                    mLoginButton.setBackgroundColor(Color.parseColor("#e65757"));
                }
                else{
                    mLoginButton.setText("登录");
                    mLoginButton.setBackgroundColor(Color.parseColor("#34adfd"));
                }
            }
        }
    }

    Runnable login = new Runnable() {
        @Override
        public void run() {
            try {
                userMac = sp.getString("USER_MAC","");
                userId = mAccount.getText().toString();
                String userPwd = mPwd.getText().toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USER_PWD", userPwd);
                editor.putString("USER_ID",userId);
                if(userMac.equals("")){
                    userMac = getAdresseMAC(Login.this);
                    if (!TextUtils.isEmpty(userMac) && !userMac.equals(marshmallowMacAddress)){
                        editor.putString("USER_MAC",userMac);
                    }
                }
                userMac = userMac.toLowerCase();
                editor.commit();
                if(userId.equals("") || userPwd.equals("")){
                    mHandler.sendEmptyMessage(0);
                    return;
                }
                socket = new Socket(host, port);
                //System.out.println("buffersize:"+socket.getReceiveBufferSize());
                socket.setReceiveBufferSize(8*1024*1024);
                System.out.println("buffersize:"+socket.getReceiveBufferSize());
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                base = new Base(socket, inputStream, outputStream);
                builder.setUserId(userId);
                builder.setUserPassword(userPwd);
                builder.setLoginType(LoginMsg.LoginReq.LoginType.MOBILE);
                builder.setUserMac(userMac);
                builder.setMobileInfo(getPhoneInfo());

                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.LOGIN_REQ_VALUE, builder.build().toByteArray());
                base.writeToServer(outputStream,byteArray);

                byteArray = base.readFromServer(inputStream);
                if(byteArray.length == 0){
                    mHandler.sendEmptyMessage(1);
                    userId = "";
                    socket.close();
                    return;
                }
                int size = DataTypeTranslater.bytesToInt(byteArray, 0);
                System.out.println("size:" + size);
                ProtoHead.ENetworkMessage type = ProtoHead.ENetworkMessage.valueOf(DataTypeTranslater.bytesToInt(byteArray,HEAD_INT_SIZE));

                if (type == ProtoHead.ENetworkMessage.LOGIN_RSP) {
                    byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                    for (int i = 0; i < objBytes.length; i++) {
                        objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                    }

                    System.out.println(byteArray.length+"-------------------------------");
                    System.out.println(objBytes.length+"-------------------------------");
                    LoginMsg.LoginRsp response = LoginMsg.LoginRsp.parseFrom(objBytes);
                    if(response.getResultCode() == LoginMsg.LoginRsp.ResultCode.FAIL){
                        mHandler.sendEmptyMessage(1);
                        userId = "";
                        socket.close();
                        return;
                    }else if(response.getResultCode() == LoginMsg.LoginRsp.ResultCode.LOGGEDIN){
                        mHandler.sendEmptyMessage(7);
                        userId = "";
                        socket.close();
                        return;
                    }else if(response.getResultCode() == LoginMsg.LoginRsp.ResultCode.NOTMAINPHONE){
                        //此处弹出提示框，告诉用户等待授权
                        mHandler.sendEmptyMessage(8);
                        boolean accreditMsg = false;
                        while(!accreditMsg){
                            byteArray = base.readFromServer(inputStream);
                            size = DataTypeTranslater.bytesToInt(byteArray, 0);
                            type = ProtoHead.ENetworkMessage.valueOf(DataTypeTranslater.bytesToInt(byteArray,HEAD_INT_SIZE));
                            System.out.println(type);
                            if(type == ProtoHead.ENetworkMessage.ACCREDIT_RSP){
                                objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                                for (int i = 0; i < objBytes.length; i++) {
                                    System.out.println(i);
                                    objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                                }
                                AccreditMsg.AccreditRsp accreditResponse = AccreditMsg.AccreditRsp.parseFrom(objBytes);
                                System.out.println(accreditResponse.getResultCode());
                                if(accreditResponse.getResultCode().equals(AccreditMsg.AccreditRsp.ResultCode.RESPONSCODE)){
                                    AccreditMsg.AccreditReq.Builder accreditBuilder = AccreditMsg.AccreditReq.newBuilder();
                                    accreditBuilder.setAccreditCode("11");
                                    accreditBuilder.setAccreditMac(getAdresseMAC(Login.this));
                                    accreditBuilder.setUserId("petter");
                                    accreditBuilder.setType(AccreditMsg.AccreditReq.Type.REQUIRE);
                                    byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ACCREDIT_REQ.getNumber(), accreditBuilder
                                            .build().toByteArray());
                                    base.writeToServer(outputStream, reByteArray);
                                }else if(accreditResponse.getResultCode().equals(AccreditMsg.AccreditRsp.ResultCode.CANLOGIN)){
                                    //accreditMsg = true;
                                    byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.LOGIN_REQ_VALUE, builder.build().toByteArray());
                                    base.writeToServer(outputStream, byteArray);
                                }else if(accreditResponse.getResultCode().equals(AccreditMsg.AccreditRsp.ResultCode.FAIL)){
                                    mHandler.sendEmptyMessage(10);
                                    socket.close();
                                    return;
                                }
                            }else if (type == ProtoHead.ENetworkMessage.LOGIN_RSP) {
                                objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                                for (int i = 0; i < objBytes.length; i++) {
                                    objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                                }

                                response = LoginMsg.LoginRsp.parseFrom(objBytes);
                                dialog.dismiss();
                                break;
                            }
                        }
                    }else if(response.getResultCode() == LoginMsg.LoginRsp.ResultCode.MAINPHONEOUTLINE){
                        mHandler.sendEmptyMessage(9);
                        socket.close();
                        return;
                    }

                    if(response.getMainMobileCode().equals(LoginMsg.LoginRsp.MainMobileCode.YES)){
                        mainMobile = true;
                    }else if(response.getMainMobileCode().equals(LoginMsg.LoginRsp.MainMobileCode.NO)){
                        mainMobile = false;
                    }
                    base.onlineUserId.clear();
                    userFriend.clear();
                    userShare.clear();
                    userNet.clear();
                    base.onlineUserId.add(userId);
                    //用户分类
                    List<UserData.UserItem> lu = response.getUserItemList();
                    if(!lu.isEmpty()){
                        for(UserData.UserItem u : lu){
                            if(u.getUserId().equals(userId))continue;
                            if(u.getIsOnline()){
                                base.onlineUserId.add(u.getUserId());
                            }
                            if(u.getIsFriend()){
                                userFriend.add(u);
                            }
                            else if(u.getFileNum() >= base.FILENUM){
                                userShare.add(u);
                            }
                            else{
                                System.out.println(u.getUserId());
                                userNet.add(u);
                            }
                        }
                    }
                    List<ChatData.ChatItem> chats = response.getChatItemList();
                    myChats.setList(chats);

                    System.out.println("Response : " + LoginMsg.LoginRsp.ResultCode.valueOf(response.getResultCode().getNumber()));
                    userBuilder.addTargetUserId(userId);
                    userBuilder.setFileInfo(true);
                    byte[] filebyteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_USERINFO_REQ.getNumber(), userBuilder.build().toByteArray());
                    base.writeToServer(outputStream, filebyteArray);
                    byteArray = base.readFromServer(inputStream);
                    size = DataTypeTranslater.bytesToInt(byteArray, 0);
                    System.out.println("size: " + size);

                    type = ProtoHead.ENetworkMessage.valueOf(DataTypeTranslater.bytesToInt(byteArray,HEAD_INT_SIZE));
                    System.out.println("Type : " + type.toString());

                    if (type == ProtoHead.ENetworkMessage.GET_USERINFO_RSP){
                        objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                        for (int i = 0; i < objBytes.length; i++)
                            objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                        Log.i("login",objBytes.length+"");
                        GetUserInfoMsg.GetUserInfoRsp fileresponse = GetUserInfoMsg.GetUserInfoRsp.parseFrom(objBytes);
                        System.out.println(fileresponse);
                        userName = fileresponse.getUserItem(0).getUserName();
                        userHeadIndex = fileresponse.getUserItem(0).getHeadIndex();
                        System.out.println("Response : " + GetUserInfoMsg.GetUserInfoRsp.ResultCode.valueOf(fileresponse.getResultCode().getNumber()));
                        if (fileresponse.getResultCode().equals(GetUserInfoMsg.GetUserInfoRsp.ResultCode.SUCCESS)) {
                            if(fileresponse.getUserItem(0).getUserId().equals(Login.userId)){
                                //files = fileresponse.getFilesList();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                                for (FileData.FileItem file:fileresponse.getFilesList()) {
                                    files.add(file);
                                    SharedfileBean sharedFile = new SharedfileBean();
                                    sharedFile.id =  Integer.valueOf(file.getFileId());
                                    sharedFile.name = file.getFileName();
                                    sharedFile.des = file.getFileInfo();
                                    sharedFile.size = Integer.valueOf(file.getFileSize());
                                    long time = Long.parseLong(file.getFileDate());
                                    sharedFile.timeLong = time;
                                    sharedFile.timeStr  = format.format(time);
                                    MyApplication.addMySharedFiles(sharedFile);
                                }
                            }
                        }else{
                            MainActivity.handlerTab06.sendEmptyMessage(OrderConst.getUserMsgFail_order);
                        }
                    }
                    new Thread(base.listen).start();
                    //new Thread(getUserFile).start();
                    mHandler.sendEmptyMessage(2);

                }
                //new Thread(base.listen).start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    tryLogin();
                }else{

                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Login.this);
                    builder.setTitle("权限请求");
                    builder.setMessage("登录需要换取获取手机信息，请开启对应权限");
                    builder.setCancelable(false);
                    builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toasty.info(getApplicationContext(), "拒绝权限无法登录", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                    builder.show();
                }
                break;
            default:break;
        }
    }

    public void showDialog(){
        final ActionDialog_launch dialog = new ActionDialog_launch(this);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Login.this.finish();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isRadioButtonChecked()){
                    new PreferenceUtil("isHelpDialogShow",getApplicationContext()).writeBoole("launch",true);
                }
                dialog.dismiss();
            }
        });
    }

    /*public static List<FileData.FileItem> getFiles() {
        if (files == null){
            return new ArrayList<>();
        }
        return files;
    }*/
}

