package com.dkzy.areaparty.phone.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Base;
import com.dkzy.areaparty.phone.R;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.lljjcoder.citypickerview.widget.CityPicker.OnCityItemClickListener;

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
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protocol.Msg.RegisterMsg;
import protocol.Msg.SendCode;
import protocol.ProtoHead;
import server.NetworkPacket;
import tools.DataTypeTranslater;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.GetInetAddress;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.domain;

/**
 * Created by SnowMonkey on 2017/5/22.
 *
 */

public class RegisterPersonalInfo extends BaseActivity {
    private TextView tv_selectAddress;
    private EditText et_mobileNo;
    private EditText et_userCode;
    private EditText et_userStreet;
    private EditText et_userCommunity;
    private Button btn_send_code;
    private Button btn_register;
    private MyCount mc;
    private Handler mHandle;
    private String userId;
    private String userName;
    private String userKeyword;
    private String userMobile;
    private String inputCode;
    private boolean isSendCode = false;
    private String host;
    private int port = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_personalinfo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        userKeyword = intent.getStringExtra("userKeyword");

        SharedPreferences sp;
        sp = RegisterPersonalInfo.this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        port = Integer.parseInt(sp.getString("SERVER_PORT", "3333"));
        host = sp.getString("SERVER_IP", AREAPARTY_NET);
        initView();
        initEvent();
    }
    private void initView(){
        SpannableString userStreetHint = new SpannableString("请输入您居住地所在街道");
        AbsoluteSizeSpan userStreetSpan = new AbsoluteSizeSpan(12,true);
        userStreetHint.setSpan(userStreetSpan, 0, userStreetHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_userStreet = (EditText) this.findViewById(R.id.et_userStreet);
        et_userStreet.setHint(new SpannedString(userStreetHint));

        SpannableString userCommunityHint = new SpannableString("请输入您居住地所在小区");
        AbsoluteSizeSpan userCommunitySpan = new AbsoluteSizeSpan(12,true);
        userCommunityHint.setSpan(userCommunitySpan, 0, userCommunityHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_userCommunity = (EditText) this.findViewById(R.id.et_userCommunity);
        et_userCommunity.setHint(new SpannedString(userCommunityHint));

        SpannableString userMobileHint = new SpannableString("请输入手机号");
        AbsoluteSizeSpan userMobileSpan = new AbsoluteSizeSpan(12,true);
        userMobileHint.setSpan(userMobileSpan, 0, userMobileHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_mobileNo = (EditText) this.findViewById(R.id.et_mobileNo);
        et_mobileNo.setHint(new SpannedString(userMobileHint));

        SpannableString userCodeHint = new SpannableString("请输入验证码");
        AbsoluteSizeSpan userCodeSpan = new AbsoluteSizeSpan(12,true);
        userCodeHint.setSpan(userCodeSpan, 0, userCodeHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_userCode = (EditText) this.findViewById(R.id.et_userCode);
        et_userCode.setHint(new SpannedString(userCodeHint));

        SpannableString userAddressHint = new SpannableString("点击选择所在区域");
        AbsoluteSizeSpan userAddressSpan = new AbsoluteSizeSpan(12,true);
        userAddressHint.setSpan(userAddressSpan, 0, userAddressHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_selectAddress = (TextView) this.findViewById(R.id.tv_selectAddress);
        tv_selectAddress.setHint(new SpannedString(userAddressHint));

        btn_send_code = (Button) this.findViewById(R.id.btn_send_code);
        btn_register = (Button) this.findViewById(R.id.btn_register);
    }
    private void initEvent(){
        tv_selectAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chooseArea(v);
            }
        });
        btn_send_code.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String mobile = et_mobileNo.getText().toString();
                if (!isMobileNO(mobile)) {
//                    Toast.makeText(RegisterPersonalInfo.this, "请正确填写手机号", Toast.LENGTH_LONG).show();
                    et_mobileNo.clearFocus();
                    et_mobileNo.requestFocus();
                    et_mobileNo.setError("请正确填写手机号");
                }
                if (isMobileNO(mobile)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            try {
//                                code = SendCode.sendSms(mobile);
//                            } catch (ClientException e) {
//                                e.printStackTrace();
//                            }
//                            isSendCode = true;
//                            mHandle.sendEmptyMessage(1);
//                            mc = new MyCount(60000, 1000);
//                            mc.start();
                            try{
                                SendCode.SendCodeSync.Builder builder = SendCode.SendCodeSync.newBuilder();
                                builder.setChangeType(SendCode.SendCodeSync.ChangeType.REGISTER);
                                builder.setMobile(mobile);
                                SharedPreferences sp;
                                sp = RegisterPersonalInfo.this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
                                int port = Integer.parseInt(sp.getString("SERVER_PORT", "3333"));
                                String host = sp.getString("SERVER_IP", AREAPARTY_NET);
                                Socket socket = new Socket(host, port);
                                InputStream inputStream = socket.getInputStream();
                                OutputStream outputStream = socket.getOutputStream();
                                Base base = new Base(socket, inputStream, outputStream);

                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CODE.getNumber(), builder.build().toByteArray());
                                System.out.println("MessageID : " + NetworkPacket.getMessageID(byteArray));
                                base.writeToServer(outputStream, byteArray);
                                socket.close();
                                mHandle.sendEmptyMessage(1);
                            }catch (IOException e){
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCode = et_userCode.getText().toString();
                userMobile = et_mobileNo.getText().toString();
                String userAddress = tv_selectAddress.getText().toString();
                String userStreet = et_userStreet.getText().toString();
                String userCommunity = et_userCommunity.getText().toString();
                String mobile = et_mobileNo.getText().toString();
                String code = et_userCode.getText().toString();
                if(userAddress.isEmpty()){
//                    tv_selectAddress.requestFocus();
//                    tv_selectAddress.setError("请选择您当前所在地");
                    Toast.makeText(RegisterPersonalInfo.this, "请选择您当前所在地", Toast.LENGTH_LONG).show();
                    return;
                }
                /*if(userStreet.isEmpty()){
                    //Toast.makeText(RegisterPersonalInfo.this, "请选择您居住地所在街道", Toast.LENGTH_LONG).show();
                    et_userStreet.requestFocus();
                    et_userStreet.setError("请选择您居住地所在街道");
                    return;
                }
                if(userCommunity.isEmpty()){
                    //Toast.makeText(RegisterPersonalInfo.this, "请选择您居住地所在小区", Toast.LENGTH_LONG).show();
                    et_userCommunity.requestFocus();
                    et_userCommunity.setError("请选择您居住地所在小区");
                    return;
                }*/
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
                new Thread(register).start();
//                if(inputCode.equals(String.valueOf(code)) && isSendCode){
//                    userMobile = et_mobileNo.getText().toString();
//                    new Thread(register).start();
//                }else{
//                    Toast.makeText(RegisterPersonalInfo.this, "验证码错误，请仔细核对", Toast.LENGTH_LONG).show();
//                }
            }
        });

        mHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        btn_send_code.setBackgroundResource(R.drawable.disabledbuttonradius);
                        btn_send_code.setEnabled(false);
                        mc = new MyCount(60000, 1000);
                        mc.start();
                        break;
                    case 2:
                        Toast.makeText(RegisterPersonalInfo.this, "注册成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("userId", userId);
                        intent.putExtra("psw", userKeyword);
                        intent.setClass(RegisterPersonalInfo.this, RegisterFinish.class);
                        startActivity(intent);
                        break;
                    case 3:
//                        Toast.makeText(RegisterPersonalInfo.this, "该手机号已被注册", Toast.LENGTH_LONG).show();
                        et_mobileNo.requestFocus();
                        et_mobileNo.setError("该手机号已被注册");
                        break;
                    case 4:
                        Toast.makeText(RegisterPersonalInfo.this, "该用户名或手机号已被注册", Toast.LENGTH_LONG).show();
                        break;
                    case 5:
//                      Toast.makeText(RegisterPersonalInfo.this, "验证码错误", Toast.LENGTH_LONG).show();
                        et_userCode.requestFocus();
                        et_userCode.setError("验证码错误");
                        break;
                }
            }
        };
    }

    public void chooseArea(View view) {
        //判断输入法的隐藏状态
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            selectAddress();//调用CityPicker选取区域
        }
    }

    private void selectAddress() {
        String province = "";
        String city = "";
        String district = "";
        if(tv_selectAddress.getText().toString().equals("")){
            province = "四川省";
            city = "成都市";
            district = "郫县";
        }else{
            province = tv_selectAddress.getText().toString().split("-")[0];
            city = tv_selectAddress.getText().toString().split("-")[1];
            district = tv_selectAddress.getText().toString().split("-")[2];
        }
        CityPicker cityPicker = new CityPicker.Builder(RegisterPersonalInfo.this)
                .textSize(14)
                .title("地址选择")
                .titleBackgroundColor("#FFFFFF")
                .titleTextColor("#696969")
                .confirTextColor("#696969")
                .cancelTextColor("#696969")
                .province(province)
                .city(city)
                .district(district)
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();
        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];
                //为TextView赋值
                tv_selectAddress.setText(province.trim() + "-" + city.trim() + "-" + district.trim());
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterPersonalInfo.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }

    Runnable register = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sp;
            sp = RegisterPersonalInfo.this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
            if (TextUtils.isEmpty(host)){
                host = sp.getString("SERVER_IP", AREAPARTY_NET);
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
                port = Integer.parseInt(sp.getString("SERVER_PORT", "3333"));
                if (port == 0){
                    port = 3333;
                }
            }
            RegisterMsg.RegisterReq.Builder builder = RegisterMsg.RegisterReq.newBuilder();
            builder.setRequestCode(RegisterMsg.RegisterReq.RequestCode.CHECKMOBILE);
//            builder.setUserId(userId);
//            builder.setUserPassword(userKeyword);
//            builder.setUserName(userName);
            builder.setUserMobile(userMobile);
            builder.setRegisterCode(Integer.valueOf(inputCode));
            try {
                Socket socket = new Socket(host, port);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                Base base = new Base(socket, inputStream, outputStream);

                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.REGISTER_REQ.getNumber(), builder.build().toByteArray());
                base.writeToServer(outputStream, byteArray);
                byteArray = base.readFromServer(inputStream);
                if(byteArray.length == 0){
                    mHandle.sendEmptyMessage(0);
                    socket.close();
                    return;
                }
                int size = DataTypeTranslater.bytesToInt(byteArray, 0);
                ProtoHead.ENetworkMessage type = ProtoHead.ENetworkMessage.valueOf(DataTypeTranslater.bytesToInt(byteArray,4));
                if (type == ProtoHead.ENetworkMessage.REGISTER_RSP) {
                    byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                    for (int i = 0; i < objBytes.length; i++)
                        objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];

                    RegisterMsg.RegisterRsp response = RegisterMsg.RegisterRsp.parseFrom(objBytes);
                    if(response.getResultType().equals(RegisterMsg.RegisterRsp.ResultType.MOBILE)){
                        if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.SUCCESS)){
                            builder.setRequestCode(RegisterMsg.RegisterReq.RequestCode.REGISTER);
                            builder.setUserId(userId);
                            builder.setUserPassword(userKeyword);
                            builder.setUserName(userName);
                            builder.setUserMobile(userMobile);
                            builder.setUserMac(getAdresseMAC(RegisterPersonalInfo.this));
                            builder.setUserAddress(tv_selectAddress.getText().toString());
                            builder.setUserStreet(et_userStreet.getText().toString());
                            builder.setUserCommunity(et_userCommunity.getText().toString());
                            byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.REGISTER_REQ.getNumber(), builder.build().toByteArray());
                            base.writeToServer(outputStream, byteArray);
                            byteArray = base.readFromServer(inputStream);
                            if(byteArray.length == 0){
                                mHandle.sendEmptyMessage(0);
                                socket.close();
                                return;
                            }
                            size = DataTypeTranslater.bytesToInt(byteArray, 0);
                            type = ProtoHead.ENetworkMessage.valueOf(DataTypeTranslater.bytesToInt(byteArray,4));
                            if (type == ProtoHead.ENetworkMessage.REGISTER_RSP) {
                                objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                                for (int i = 0; i < objBytes.length; i++)
                                    objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];

                                response = RegisterMsg.RegisterRsp.parseFrom(objBytes);
                                if(response.getResultType().equals(RegisterMsg.RegisterRsp.ResultType.REGISTER)){
                                    if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.SUCCESS)){
                                        SharedPreferences sp1 = RegisterPersonalInfo.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp1.edit();
                                        editor.putString("USER_MAC", getAdresseMAC(RegisterPersonalInfo.this));
                                        editor.commit();
                                        mHandle.sendEmptyMessage(2);
                                        socket.close();
                                    }else if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.EXIST)){
                                        mHandle.sendEmptyMessage(4);
                                        socket.close();
                                    }
                                }
                            }
                        }else if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.EXIST)){
                            mHandle.sendEmptyMessage(3);
                            socket.close();
                        }else if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.CODEWRONG)){
                            mHandle.sendEmptyMessage(5);
                            socket.close();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
    };

//    public String getMacAddress(){
//        String macSerial = null;
//        String str = "";
//        try {
//            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
//            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//
//
//            for (; null != str;) {
//                str = input.readLine();
//                if (str != null) {
//                    macSerial = str.trim();// 去空格
//                    break;
//                }
//            }
//        } catch (IOException ex) {
//            // 赋予默认值
//            ex.printStackTrace();
//        }
//        return macSerial;
//    }

    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";
    public static String getAdresseMAC(Context context) {
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

    private boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    private  boolean isUserCode(String code){
        Pattern p = Pattern.compile("^\\d{6}$");
        Matcher m = p.matcher(code);
        return m.matches();
    }
}
