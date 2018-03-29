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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import protocol.Data.ChatData;
import protocol.Data.FileData;
import protocol.Data.UserData;
import protocol.Msg.AccreditMsg;
import protocol.Msg.GetUserInfoMsg;
import protocol.Msg.LoginMsg;
import protocol.Msg.SendCode;
import protocol.ProtoHead;
import server.NetworkPacket;
import tools.DataTypeTranslater;

import static com.dkzy.areaparty.phone.Login.HEAD_INT_SIZE;
import static com.dkzy.areaparty.phone.Login.base;
import static com.dkzy.areaparty.phone.Login.files;
import static com.dkzy.areaparty.phone.Login.getAdresseMAC;
import static com.dkzy.areaparty.phone.Login.inputStream;
import static com.dkzy.areaparty.phone.Login.mainMobile;
import static com.dkzy.areaparty.phone.Login.marshmallowMacAddress;
import static com.dkzy.areaparty.phone.Login.myChats;
import static com.dkzy.areaparty.phone.Login.outline;
import static com.dkzy.areaparty.phone.Login.outputStream;
import static com.dkzy.areaparty.phone.Login.socket;
import static com.dkzy.areaparty.phone.Login.userFriend;
import static com.dkzy.areaparty.phone.Login.userHeadIndex;
import static com.dkzy.areaparty.phone.Login.userId;
import static com.dkzy.areaparty.phone.Login.userMac;
import static com.dkzy.areaparty.phone.Login.userName;
import static com.dkzy.areaparty.phone.Login.userNet;
import static com.dkzy.areaparty.phone.Login.userShare;
import static com.dkzy.areaparty.phone.bluetoothxie.Utils.isMobileNO;
import static com.dkzy.areaparty.phone.bluetoothxie.Utils.isUserCode;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.GetInetAddress;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.domain;

/**
 * Created by SnowMonkey on 2016/12/29.
 */

public class LoginByVerificationCode extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_PHONE_STATE
    };

    //public static Base base = null;
    private LoginMsg.LoginReq.Builder builder = LoginMsg.LoginReq.newBuilder();
    private GetUserInfoMsg.GetUserInfoReq.Builder userBuilder = GetUserInfoMsg.GetUserInfoReq.newBuilder();

    private Button mLoginButton;                      //登录按钮
    private AlertDialog dialog = null;

    private String host;
    private int port = 0;
    //private int port = 3333;
    private SharedPreferences sp;
    private SharedPreferences sp2;
    private long timer = 0;
    //public static Handler mHandler;
    //public static final int HEAD_INT_SIZE = 4;
    //public static Socket socket = null;
    //public static InputStream inputStream = null;
    //public static OutputStream outputStream = null;
    //public static List<UserData.UserItem> userFriend = new ArrayList<>();
    //public static List<UserData.UserItem> userNet = new ArrayList<>();
    //public static List<UserData.UserItem> userShare = new ArrayList<>();
    //public static List<FileData.FileItem> files = new ArrayList<>();
    //public static boolean outline = false;
    //public static String userId = "";
    //public static String userName = "";
    //public static String userMac;
    //public static boolean mainMobile;
    //public static int userHeadIndex;
    //public static myChatList myChats = new myChatList();


    private Button btn_send_code;
    private EditText et_mobileNo;
    private EditText et_userCode;
    private Handler mHandler;
    private MyCount mc;
    Runnable login = new Runnable() {
        @Override
        public void run() {
            userMac = sp.getString("USER_MAC","");
            userId = et_mobileNo.getText().toString();
            String code = et_userCode.getText().toString();
            if(userMac.equals("")){
                userMac = getAdresseMAC(LoginByVerificationCode.this);
                if (!TextUtils.isEmpty(userMac) && !userMac.equals(marshmallowMacAddress)){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("USER_MAC",userMac);
                    editor.commit();
                }
            }
            userMac = userMac.toLowerCase();

            builder.setUserId(userId);
            builder.setUserCode(code);
            builder.setLoginType(LoginMsg.LoginReq.LoginType.MOBILE_CODE);
            builder.setUserMac(userMac);

            try {
                socket = new Socket(host, port);

                //System.out.println("buffersize:"+socket.getReceiveBufferSize());
                socket.setReceiveBufferSize(8*1024*1024);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                base = new Base(socket, inputStream, outputStream);
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
                        mHandler.sendEmptyMessage(11);
                        userId = "";
                        socket.close();
                        return;
                    }else if (response.getResultCode() == LoginMsg.LoginRsp.ResultCode.USERCODEWRONG){
                        mHandler.sendEmptyMessage(1);
                        userId = "";
                        socket.close();
                        return;
                    }
                    else if(response.getResultCode() == LoginMsg.LoginRsp.ResultCode.LOGGEDIN){
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
                                    accreditBuilder.setAccreditMac(getAdresseMAC(LoginByVerificationCode.this));
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
                    }
                    else if(response.getResultCode() == LoginMsg.LoginRsp.ResultCode.MAINPHONEOUTLINE){
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
                            if(u.getIsSpeed()&&u.getIsFriend()){
                                userNet.add(u);
                            }
                            if(u.getIsRecommend()){
                                userShare.add(u);
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
                        userId = fileresponse.getUserItem(0).getUserId();
                        userName = fileresponse.getUserItem(0).getUserName();
                        userHeadIndex = fileresponse.getUserItem(0).getHeadIndex();
                        System.out.println("Response : " + GetUserInfoMsg.GetUserInfoRsp.ResultCode.valueOf(fileresponse.getResultCode().getNumber()));
                        if (fileresponse.getResultCode().equals(GetUserInfoMsg.GetUserInfoRsp.ResultCode.SUCCESS)) {
                            if(fileresponse.getUserItem(0).getUserId().equals(userId)){
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }//
        setContentView(R.layout.login_by_vertificarion_code);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyApplication.getInstance().addActivity(this);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        sp2 = this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        btn_send_code.setBackgroundResource(R.drawable.disabledbuttonradius);
                        btn_send_code.setEnabled(false);
                        mc = new MyCount(60000, 1000);
                        mc.start();
                        break;
                    case 1:
                        Toast.makeText(LoginByVerificationCode.this, "登录失败，验证码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //跳转主界面
                        Intent intentMain = new Intent();
                        intentMain.setClass(LoginByVerificationCode.this, MainActivity.class);
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
                        Intent intentSetting = new Intent(LoginByVerificationCode.this, LoginSetting.class);
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
                        /*//接收用户注册后返回的用户名密码
                        String s = (String) msg.obj;
                        String userId = s.split(":")[0];
                        String psw = s.split(":")[1];
                        mAccount.setText(userId);
                        mPwd.setText(psw);*/
                        break;
                    case 7:
                        /*Toast.makeText(LoginByVerificationCode.this, "您的账号已登录", Toast.LENGTH_SHORT).show();*/
                        break;
                    case 8:
                        //弹出提示框，告诉用户等待授权
                        /*((TextView) accreditView.findViewById(R.id.accreditDialogTitle)).setText("等待主设备授权");
                        ((TextView) accreditView.findViewById(R.id.accreditDialogInfo)).setText("请在注册该账号的设备上登录进行授权");
                        ((Button) accreditView.findViewById(R.id.accreditDialogConfirm)).setOnClickListener(mListener);
                        ((TextView)accreditView.findViewById(R.id.verificationBtn)).setVisibility(View.GONE);
                        dialog.show();*/
                        break;
                    case 9:
                        /*//弹出提示框，告诉用户主设备不在线
                        ((TextView) accreditView.findViewById(R.id.accreditDialogTitle)).setText("主设备不在线");
                        ((TextView) accreditView.findViewById(R.id.accreditDialogInfo)).setText("请打开主设备进行授权操作");
                        ((Button) accreditView.findViewById(R.id.accreditDialogConfirm)).setOnClickListener(mListener);
                        ((TextView)accreditView.findViewById(R.id.verificationBtn)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        ((TextView)accreditView.findViewById(R.id.verificationBtn)).setVisibility(View.VISIBLE);
                        ((TextView)accreditView.findViewById(R.id.verificationBtn)).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Login.this,LoginByVerificationCode.class));
                                if (dialog.isShowing()) dialog.dismiss();
                            }
                        });
                        dialog.show();*/
                        break;
                    case 10:
                        /*((TextView) accreditView.findViewById(R.id.accreditDialogTitle)).setText("授权失败");
                        ((TextView) accreditView.findViewById(R.id.accreditDialogInfo)).setText("主设备拒绝授权您的手机使用该账号登录");
                        ((Button) accreditView.findViewById(R.id.accreditDialogConfirm)).setOnClickListener(mListener);
                        dialog.show();*/
                        break;
                    case 11:
                        Toast.makeText(LoginByVerificationCode.this, "登录失败，该用户未注册", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };

        et_mobileNo = (EditText) this.findViewById(R.id.et_mobileNo);
        btn_send_code = (Button) this.findViewById(R.id.btn_send_code);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        et_userCode = (EditText) this.findViewById(R.id.et_userCode);

        btn_send_code.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String mobile = et_mobileNo.getText().toString();
                if (!isMobileNO(mobile)) {
                    et_mobileNo.clearFocus();
                    et_mobileNo.requestFocus();
                    et_mobileNo.setError("请正确填写手机号");
                }
                if (isMobileNO(mobile)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                SendCode.SendCodeSync.Builder builder = SendCode.SendCodeSync.newBuilder();
                                builder.setChangeType(SendCode.SendCodeSync.ChangeType.LOGIN);
                                builder.setMobile(mobile);
                                SharedPreferences sp;
                                sp = LoginByVerificationCode.this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
                                int port = Integer.parseInt(sp.getString("SERVER_PORT", "3333"));
                                String host = sp.getString("SERVER_IP", AREAPARTY_NET);
                                if (TextUtils.isEmpty(host)) host = AREAPARTY_NET;
                                Socket socket = new Socket(host, port);
                                InputStream inputStream = socket.getInputStream();
                                OutputStream outputStream = socket.getOutputStream();
                                Base base = new Base(socket, inputStream, outputStream);

                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CODE.getNumber(), builder.build().toByteArray());
                                System.out.println("MessageID : " + NetworkPacket.getMessageID(byteArray));
                                base.writeToServer(outputStream, byteArray);
                                socket.close();
                                mHandler.sendEmptyMessage(0);
                            }catch (IOException e){
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    et_userCode.requestFocus();

                }
            }
        });
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = et_mobileNo.getText().toString();
                String code = et_userCode.getText().toString();
                if(mobile.isEmpty()){
                    et_mobileNo.requestFocus();
                    et_mobileNo.setError("请填写手机号");
                    return;
                }
                if (!isMobileNO(mobile)) {
//                    Toast.makeText(RegisterPersonalInfo.this, "请正确填写手机号", Toast.LENGTH_LONG).show();
                    et_mobileNo.requestFocus();
                    et_mobileNo.setError("请正确填写手机号");
                    return;
                }
                if(code.isEmpty()){
                    et_userCode.requestFocus();
                    et_userCode.setError("请填写验证码");
                    return;
                }
                if(!isUserCode(code)){
                    et_userCode.requestFocus();
                    et_userCode.setError("请正确填写验证码");
                    return;
                }

                if (verifyStoragePermissions(LoginByVerificationCode.this)) {
                    tryLogin();
                }
            }
        });


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
                Toast.makeText(this, "正在登陆", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //tryLogin();
                }else{

                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginByVerificationCode.this);
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

    class MyCount extends CountDownTimer {
        /**
         * MyCount的构造方法
         * @param millisInFuture 要倒计时的时间
         * @param countDownInterval 时间间隔
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {//在进行倒计时的时候执行的操作
            long second = millisUntilFinished /1000;
            btn_send_code.setText(second+"秒");
            if(second == 60){
                btn_send_code.setText(59+"秒");
            }
        }

        @Override
        public void onFinish() {//倒计时结束后要做的事情
            // TODO Auto-generated method stub
            btn_send_code.setText("重新获取");
            btn_send_code.setBackgroundResource(R.drawable.buttonradius);
            btn_send_code.setEnabled(true);
        }

    }

}

