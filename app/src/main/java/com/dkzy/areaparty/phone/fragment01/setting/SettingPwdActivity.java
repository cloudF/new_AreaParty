package com.dkzy.areaparty.phone.fragment01.setting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import protocol.Msg.PersonalSettingsMsg;
import protocol.Msg.SendCode;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/7/14.
 */

public class SettingPwdActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton settingPwdBackBtn;
    private Button getChangePwdCodeBtn;
    private Button sendChangeMsgBtn;
    private EditText setting_oldPwd;
    private EditText setting_newPwd;
    private EditText setting_codePwd;
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_setting_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyApplication.getInstance().addActivity(this);

        getData();
        initView();
        initEvent();
    }
    private void getData(){

    }
    private void initView(){
        settingPwdBackBtn = (ImageButton) this.findViewById(R.id.settingPwdBackBtn);
        getChangePwdCodeBtn = (Button) this.findViewById(R.id.getChangePwdCodeBtn);
        sendChangeMsgBtn = (Button) this.findViewById(R.id.sendChangeMsgBtn);
        setting_oldPwd = (EditText) this.findViewById(R.id.setting_oldPwd);
        setting_newPwd = (EditText) this.findViewById(R.id.setting_newPwd);
        setting_codePwd = (EditText) this.findViewById(R.id.setting_codePwd);

    }
    private void initEvent(){
        settingPwdBackBtn.setOnClickListener(this);
        getChangePwdCodeBtn.setOnClickListener(this);
        sendChangeMsgBtn.setOnClickListener(this);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        Toast.makeText(SettingPwdActivity.this, "修改失败，请重试", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SettingPwdActivity.this, "修改成功，请重新登录", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(SettingPwdActivity.this, "旧密码错误，请重新输入", Toast.LENGTH_LONG).show();
                        setting_oldPwd.setText("");
                        break;
                    case 3:
                        Toast.makeText(SettingPwdActivity.this, "修改成功，请重新登录", Toast.LENGTH_LONG).show();
                        try {
                            Login.socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(SettingPwdActivity.this, Login.class));
                        MyApplication.getInstance().closeAll();

                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingPwdBackBtn:
                this.finish();
                break;
            case R.id.getChangePwdCodeBtn:
                String oldPwd = setting_oldPwd.getText().toString();
                String newPwd = setting_newPwd.getText().toString();
                if(oldPwd.isEmpty()){
                    Toast.makeText(SettingPwdActivity.this, "请填写旧密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(newPwd.isEmpty()){
                    Toast.makeText(SettingPwdActivity.this, "请填写新密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isKeyWord(oldPwd)) {
                    Toast.makeText(SettingPwdActivity.this, "请按规则正确填写旧密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isKeyWord(newPwd)){
                    Toast.makeText(SettingPwdActivity.this, "请按规则正确填写新密码", Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            SendCode.SendCodeSync.Builder builder = SendCode.SendCodeSync.newBuilder();
                            builder.setChangeType(SendCode.SendCodeSync.ChangeType.PASSWORD);
                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CODE.getNumber(), builder.build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                getChangePwdCodeBtn.setBackgroundResource(R.drawable.disabledbuttonradius);
                getChangePwdCodeBtn.setEnabled(false);
                MyCount mc = new MyCount(60000, 1000);
                mc.start();
                break;
            case R.id.sendChangeMsgBtn:
                final String oldPwd1 = setting_oldPwd.getText().toString();
                final String newPwd1 = setting_newPwd.getText().toString();
                final String confirmCode =  setting_codePwd.getText().toString();
                if(oldPwd1.isEmpty()){
                    Toast.makeText(SettingPwdActivity.this, "请填写旧密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(newPwd1.isEmpty()){
                    Toast.makeText(SettingPwdActivity.this, "请填写新密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isKeyWord(oldPwd1)) {
                    Toast.makeText(SettingPwdActivity.this, "请按规则正确填写旧密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isKeyWord(newPwd1)){
                    Toast.makeText(SettingPwdActivity.this, "请按规则正确填写新密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(confirmCode.isEmpty()){
                    Toast.makeText(SettingPwdActivity.this, "请填写验证码", Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            PersonalSettingsMsg.PersonalSettingsReq.Builder builder = PersonalSettingsMsg.PersonalSettingsReq.newBuilder();
                            builder.setCode(Integer.valueOf(confirmCode));
                            builder.setUserPassword(newPwd1);
                            builder.setUserOldPassword(oldPwd1);
                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.PERSONALSETTINGS_REQ.getNumber(), builder.build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    private boolean isKeyWord (String keyWord) {
        Pattern p = Pattern.compile("^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,20}$");
        Matcher m = p.matcher(keyWord);
        return m.matches();
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
            getChangePwdCodeBtn.setText(second+"秒");
            if(second == 60){
                getChangePwdCodeBtn.setText(59+"秒");
            }
        }

        @Override
        public void onFinish() {//倒计时结束后要做的事情
            // TODO Auto-generated method stub
            getChangePwdCodeBtn.setText("重新获取");
            getChangePwdCodeBtn.setBackgroundResource(R.drawable.buttonradius);
            getChangePwdCodeBtn.setEnabled(true);
        }

    }

}
