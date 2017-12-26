package com.dkzy.areaparty.phone.fragment01.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;

import protocol.Msg.PersonalSettingsMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/7/16.
 */

public class SettingNameActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton settingNameBackBtn;
    private Button sendChangeNameMsgBtn;
    private EditText setting_name_et;
    private String newName = "";
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_setting_name);
        MyApplication.getInstance().addActivity(this);

        getData();
        initView();
        initEvent();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingNameBackBtn:
                this.finish();
                break;
            case R.id.sendChangeNameMsgBtn:
                newName = setting_name_et.getText().toString();
                if(newName.equals(Login.userName) || newName.equals("")){
                    Toast.makeText(SettingNameActivity.this, "请正确填写用户名", Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PersonalSettingsMsg.PersonalSettingsReq.Builder builder = PersonalSettingsMsg.PersonalSettingsReq.newBuilder();
                        builder.setUserName(newName);
                        byte[] reByteArray;
                        try {
                            reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.PERSONALSETTINGS_REQ.getNumber(), builder.build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
    private void getData(){

    }
    private void initView(){
        settingNameBackBtn = (ImageButton) this.findViewById(R.id.settingNameBackBtn);
        sendChangeNameMsgBtn = (Button) this.findViewById(R.id.sendChangeNameMsgBtn);
        setting_name_et = (EditText) this.findViewById(R.id.setting_name_et);
    }
    private void initEvent(){
        settingNameBackBtn.setOnClickListener(this);
        sendChangeNameMsgBtn.setOnClickListener(this);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(SettingNameActivity.this, "修改失败，请重试", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SettingNameActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                        Message setNameMsg = MainActivity.handlerTab01.obtainMessage();
                        setNameMsg.obj = newName;
                        setNameMsg.what = OrderConst.setUserName;
                        MainActivity.handlerTab01.sendMessage(setNameMsg);
                        SettingNameActivity.this.finish();
                        break;
                }
            }
        };
    }
}
