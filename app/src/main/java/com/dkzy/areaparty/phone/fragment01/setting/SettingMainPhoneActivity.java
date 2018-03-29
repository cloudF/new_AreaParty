package com.dkzy.areaparty.phone.fragment01.setting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;

import protocol.Msg.PersonalSettingsMsg;
import protocol.Msg.SendCode;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.bluetoothxie.Utils.isUserCode;

public class SettingMainPhoneActivity extends AppCompatActivity {
    private ImageButton settingMainPBackBtn;
    private Button getChangeMainPCodeBtn;
    private Button sendChangeMainPBtn;
    private EditText codeET;

    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main_phone);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyApplication.getInstance().addActivity(this);

        settingMainPBackBtn = (ImageButton) this.findViewById(R.id.settingMainPBackBtn);
        getChangeMainPCodeBtn = (Button) this.findViewById(R.id.getChangeMainPCodeBtn);
        sendChangeMainPBtn = (Button) this.findViewById(R.id.sendChangeMainPBtn);
        codeET = (EditText) this.findViewById(R.id.setting_codeMainP);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        Toast.makeText(SettingMainPhoneActivity.this, "修改失败，请重试", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SettingMainPhoneActivity.this, "修改成功，请重新登录", Toast.LENGTH_LONG).show();
                        Login.mainMobile = true;
                        break;
                    case 2:
                        Toast.makeText(SettingMainPhoneActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                        codeET.setText("");
                        break;
                }
            }
        };

        settingMainPBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingMainPhoneActivity.this.finish();
            }
        });

        getChangeMainPCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SendCode.SendCodeSync.Builder builder = SendCode.SendCodeSync.newBuilder();
                                builder.setChangeType(SendCode.SendCodeSync.ChangeType.MAINPHONE);
                                builder.setUserId(Login.userId);
                                byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CODE.getNumber(), builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, reByteArray);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    getChangeMainPCodeBtn.setBackgroundResource(R.drawable.disabledbuttonradius);
                    getChangeMainPCodeBtn.setEnabled(false);
                    MyCount mc = new MyCount(60000, 1000);
                    mc.start();
                }
            }
        });

        sendChangeMainPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    final String code = codeET.getText().toString();
                    if(code.isEmpty()){
                        codeET.requestFocus();
                        codeET.setError("请填写验证码");
                        return;
                    }
                    if(!isUserCode(code)){
                        codeET.requestFocus();
                        codeET.setError("请正确填写验证码");
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                PersonalSettingsMsg.PersonalSettingsReq.Builder builder = PersonalSettingsMsg.PersonalSettingsReq.newBuilder();
                                builder.setCode(Integer.valueOf(code));
                                builder.setUserId(Login.userId);
                                builder.setUserMainMac(Login.getAdresseMAC(MyApplication.getContext()));
                                byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.PERSONALSETTINGS_REQ.getNumber(), builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, reByteArray);
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
            getChangeMainPCodeBtn.setText(second+"秒");
            if(second == 60){
                getChangeMainPCodeBtn.setText(59+"秒");
            }
        }

        @Override
        public void onFinish() {//倒计时结束后要做的事情
            // TODO Auto-generated method stub
            getChangeMainPCodeBtn.setText("重新获取");
            getChangeMainPCodeBtn.setBackgroundResource(R.drawable.buttonradius);
            getChangeMainPCodeBtn.setEnabled(true);
        }

    }
}
