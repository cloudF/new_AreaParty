package com.dkzy.areaparty.phone.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Base;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_page;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protocol.Msg.RegisterMsg;
import protocol.ProtoHead;
import server.NetworkPacket;
import tools.DataTypeTranslater;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.GetInetAddress;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.domain;

/**
 * Created by SnowMonkey on 2017/5/22.
 */

public class RegisterUserInfo extends BaseActivity {
    private EditText et_userId;
    private EditText et_userName;
    private EditText et_keyword;
    private EditText et_keywordAgain;
    private Button btn_register;
    private CheckBox chk_agree;
    private String userId;
    private String userName;
    private String userKeyword;
    private Handler mHandle;
    private String host;
    private int port = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_userinfo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sp;
        sp = RegisterUserInfo.this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        port = Integer.parseInt(sp.getString("SERVER_PORT", "3333"));
        host = sp.getString("SERVER_IP", AREAPARTY_NET);
        initView();
        initEvent();
    }
    private void initView(){
        SpannableString userIdHint = new SpannableString("请输入以字母开头的6-20位字母或数字");
        AbsoluteSizeSpan userIdSpan = new AbsoluteSizeSpan(12,true);
        userIdHint.setSpan(userIdSpan, 0, userIdHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_userId = (EditText) this.findViewById(R.id.et_userId);
        et_userId.setHint(new SpannedString(userIdHint));

        SpannableString userNameHint = new SpannableString("请输入16字符以内的昵称");
        AbsoluteSizeSpan userNameSpan = new AbsoluteSizeSpan(12,true);
        userNameHint.setSpan(userNameSpan, 0, userNameHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_userName = (EditText) this.findViewById(R.id.et_userName);
        et_userName.setHint(new SpannedString(userNameHint));

        SpannableString userKeyWordHint = new SpannableString("请输入6-20位密码");
        AbsoluteSizeSpan userKeyWordSpan = new AbsoluteSizeSpan(12,true);
        userKeyWordHint.setSpan(userKeyWordSpan, 0, userKeyWordHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_keyword = (EditText) this.findViewById(R.id.et_keyword);
        et_keyword.setHint(new SpannedString(userKeyWordHint));

        SpannableString userKeyWordAgainHint = new SpannableString("请再次输入密码");
        AbsoluteSizeSpan userKeyWordAgainSpan = new AbsoluteSizeSpan(12,true);
        userKeyWordAgainHint.setSpan(userKeyWordAgainSpan, 0, userKeyWordAgainHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_keywordAgain = (EditText) this.findViewById(R.id.et_keywordAgain);
        et_keywordAgain.setHint(new SpannedString(userKeyWordAgainHint));

        btn_register = (Button) this.findViewById(R.id.btn_register);
        chk_agree = (CheckBox) this.findViewById(R.id.chk_agree);
        chk_agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    btn_register.setBackgroundResource(R.drawable.buttonradius);
                } else {
                    btn_register.setBackgroundResource(R.drawable.disabledbuttonradius);
                }
            }
        });
    }
    public  void showHelpInfoDialog(){
        final ActionDialog_ServiceAgreement dialog = new ActionDialog_ServiceAgreement(this);
        dialog.setCancelable(true);
        dialog.show();
    }
    private void initEvent(){
//        et_userId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (et_userId.hasFocus() == false) {
//                    userId = et_userId.getText().toString();
//                    boolean checkUserId = isUserIdNO(userId);
//                    if(!checkUserId){
//                        et_userId.setText("");
//                        et_userId.clearFocus();
//                        et_userName.clearFocus();
//                        et_keyword.clearFocus();
//                        et_keywordAgain.clearFocus();
//                        et_userId.requestFocus();
////                            Toast.makeText(RegisterUserInfo.this, "请正确输入用户名", Toast.LENGTH_LONG).show();
//                        et_userId.setError("请正确输入用户名");
//                    }
//                }
//                }
//        });
        findViewById(R.id.tv_QQ_Server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpInfoDialog();
            }
        });
        btn_register.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    if(!chk_agree.isChecked()){
                        Toast.makeText(RegisterUserInfo.this, "请先阅读服务条款", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String keyword = et_keyword.getText().toString();
                    String keywordAgain = et_keywordAgain.getText().toString();
                    userId = et_userId.getText().toString();
                    userName = et_userName.getText().toString();
                    if(!userId.isEmpty()){
                        boolean checkUserId = isUserIdNO(userId);
                        if(!checkUserId){
                            et_userId.setText("");
                            et_userId.setFocusableInTouchMode(true);
                            et_userId.clearFocus();
                            et_userId.requestFocus();
//                            Toast.makeText(RegisterUserInfo.this, "请正确输入用户名", Toast.LENGTH_LONG).show();
                            et_userId.setError("请正确输入6-20位用户名");
                            return;
                        }
                        if(!userName.isEmpty()){
                            boolean checkUserName = isUserNameNO(userName);
                            if(!checkUserName){
                                et_userName.setText("");
                                et_userName.setFocusableInTouchMode(true);
                                et_userId.clearFocus();
                                et_userName.requestFocus();
//                                Toast.makeText(RegisterUserInfo.this, "请正确输入昵称", Toast.LENGTH_LONG).show();
                                et_userName.setError("请正确输入昵称");
                                return;
                            }
                            if(keyword.isEmpty()||(!isKeyWord(keyword))){
                                et_keyword.setFocusableInTouchMode(true);
                                et_userId.clearFocus();
                                et_userName.clearFocus();
                                et_keyword.requestFocus();
//                                        Toast.makeText(RegisterUserInfo.this, "请正确输入密码", Toast.LENGTH_LONG).show();
                                et_keyword.setError("请正确输入6-20位密码");
                                return;
                            }
                            if(keywordAgain.isEmpty()||(!isKeyWord(keywordAgain))){
                                et_keyword.setFocusableInTouchMode(true);
                                et_userId.clearFocus();
                                et_userName.clearFocus();
                                et_keywordAgain.requestFocus();
//                                        Toast.makeText(RegisterUserInfo.this, "请正确输入密码", Toast.LENGTH_LONG).show();
                                et_keywordAgain.setError("请重新正确输入6-20位密码");
                                return;
                            }
                            if(keyword.equals(keywordAgain)){
                                if((!keyword.isEmpty()) && (!keywordAgain.isEmpty())){
                                    boolean checkKeyWord = isKeyWord(keyword);
                                    if(!checkKeyWord){
                                        et_keyword.setText("");
                                        et_keywordAgain.setText("");
                                        et_keyword.setFocusableInTouchMode(true);
                                        et_userId.clearFocus();
                                        et_userName.clearFocus();
                                        et_keyword.requestFocus();
//                                        Toast.makeText(RegisterUserInfo.this, "请正确输入密码", Toast.LENGTH_LONG).show();
                                        et_keyword.setError("请正确输入6-20位密码");
                                        return;
                                    }
                                    userKeyword = keyword;
                                    new Thread(register).start();
                                }else{
//                                    Toast.makeText(RegisterUserInfo.this, "请输入密码", Toast.LENGTH_LONG).show();
                                    et_userId.clearFocus();
                                    et_userName.clearFocus();
                                    et_keyword.clearFocus();
                                    et_keyword.requestFocus();
                                    et_keyword.setError("请输入密码");
                                }
                            }else{
//                                Toast.makeText(RegisterUserInfo.this, "两次密码输入不一致，请重新输入", Toast.LENGTH_LONG).show();
                                et_userId.clearFocus();
                                et_userName.clearFocus();
                                et_keyword.requestFocus();
                                et_keyword.setError("两次密码输入不一致，请重新输入");
                                et_keyword.setText("");
                                et_keywordAgain.setText("");
                            }
                        }else{
//                            Toast.makeText(RegisterUserInfo.this, "请输入昵称", Toast.LENGTH_LONG).show();
                            et_userId.clearFocus();
                            et_userName.requestFocus();
                            et_userName.setError("请输入昵称");
                        }
                    }else{
//                        Toast.makeText(RegisterUserInfo.this, "请输入用户名", Toast.LENGTH_LONG).show();
                        et_userId.clearFocus();
                        et_userId.requestFocus();
                        et_userId.setError("请输入用户名");
                    }
                }
            }
        );
        mHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(RegisterUserInfo.this, "连接断开，请检查网络后重试",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent intent = new Intent();
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", userName);
                        intent.putExtra("userKeyword", userKeyword);
                        intent.setClass(RegisterUserInfo.this, RegisterPersonalInfo.class);
                        startActivity(intent);
                        break;
                    case 2:
                        et_userId.setText("");
                        Toast.makeText(RegisterUserInfo.this, "该用户名已被注册，请重新输入", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }
    Runnable register = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sp;
            sp = RegisterUserInfo.this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
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
            builder.setRequestCode(RegisterMsg.RegisterReq.RequestCode.CHECKUSERID);
            builder.setUserId(userId);
            builder.setUserPassword(userKeyword);
            builder.setUserName(userName);
            try {
                Socket socket = new Socket(host, port);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                Base base = new Base(socket, inputStream, outputStream);

                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.REGISTER_REQ.getNumber(), builder.build().toByteArray());
                System.out.println("MessageID : " + NetworkPacket.getMessageID(byteArray));
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
                    if(response.getResultType().equals(RegisterMsg.RegisterRsp.ResultType.USERID)){
                        if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.SUCCESS)){
                            mHandle.sendEmptyMessage(1);
                            socket.close();
                        }else if(response.getResultCode().equals(RegisterMsg.RegisterRsp.ResultCode.EXIST)){
                            mHandle.sendEmptyMessage(2);
                            socket.close();
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
                return;
            }
        }
    };
    private boolean isUserIdNO(String userId) {
        Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{5,19}$");
        Matcher m = p.matcher(userId);
        return m.matches();
    }
    private boolean isUserNameNO(String userName) {
        Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5_a-zA-Z0-9-]{1,16}$");
        Matcher m = p.matcher(userName);
        return m.matches();
    }
    private boolean isKeyWord (String keyWord) {
        Pattern p = Pattern.compile("^([A-Z]|[a-z]|[0-9]|[_`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,20}$");
        Matcher m = p.matcher(keyWord);
        return m.matches();
    }
}
