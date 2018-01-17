package com.dkzy.areaparty.phone;
//************处理电脑登录账号和非主设备登录账号的授权问题*************
//*************主设备app调用此界面弹出窗口来授权或拒绝***********
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;

import protocol.Msg.AccreditMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

public class AlertAccreditActivity extends AppCompatActivity {

    public static View accreditView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("授权消息");
        Intent intent =  getIntent();
        String mobileInfo = intent.getStringExtra("mobileInfo");
        final String mobileMac = intent.getStringExtra("mobileMac");
        final String deviceType = intent.getStringExtra("deviceType");
        accreditView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.request_accredit_dialog, null);
        setContentView(accreditView);
        ((TextView)accreditView.findViewById(R.id.accreditRequestDialogTitle)).setText("请求授权");
        String info = "";
        System.out.println("授权消息");
        if(deviceType.equals("mobile")) {
            info = "手机(" + mobileInfo + ")请求授权登录";
            ((Button)accreditView.findViewById(R.id.accreditDialogOnly)).setVisibility(View.VISIBLE);
        }
        else if(deviceType.equals("pc")) {
            System.out.println("授权消息");
            info = "电脑(" + mobileInfo + ")请求授权登录";
            ((Button)accreditView.findViewById(R.id.accreditDialogOnly)).setVisibility(View.GONE);
        }
        ((TextView)accreditView.findViewById(R.id.accreditRequestDialogInfo)).setText(info);
        ((Button)accreditView.findViewById(R.id.accreditDialogOnly)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            AccreditMsg.AccreditReq.Builder accreditBuilder = AccreditMsg.AccreditReq.newBuilder();
                            accreditBuilder.setAccreditCode("11");
                            accreditBuilder.setAccreditMac(mobileMac);
                            accreditBuilder.setUserId(Login.userId);
                            if(deviceType.equals("mobile"))
                                accreditBuilder.setDeviceType(AccreditMsg.AccreditReq.DeviceType.MOBILE);
                            else if(deviceType.equals("pc"))
                                accreditBuilder.setDeviceType(AccreditMsg.AccreditReq.DeviceType.PC);
                            accreditBuilder.setType(AccreditMsg.AccreditReq.Type.ONLYONETIME);
                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ACCREDIT_REQ.getNumber(), accreditBuilder
                                    .build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        AlertAccreditActivity.this.finish();
                    }
                }).start();
            }
        });
        ((Button)accreditView.findViewById(R.id.accreditDialogAlways)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            AccreditMsg.AccreditReq.Builder accreditBuilder = AccreditMsg.AccreditReq.newBuilder();
                            accreditBuilder.setAccreditCode("11");
                            accreditBuilder.setAccreditMac(mobileMac);
                            accreditBuilder.setUserId(Login.userId);
                            if(deviceType.equals("mobile"))
                                accreditBuilder.setDeviceType(AccreditMsg.AccreditReq.DeviceType.MOBILE);
                            else if(deviceType.equals("pc"))
                                accreditBuilder.setDeviceType(AccreditMsg.AccreditReq.DeviceType.PC);
                            accreditBuilder.setType(AccreditMsg.AccreditReq.Type.AGREE);
                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ACCREDIT_REQ.getNumber(), accreditBuilder
                                    .build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        AlertAccreditActivity.this.finish();
                    }
                }).start();
            }
        });
        ((Button)accreditView.findViewById(R.id.accreditDialogRefuse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            AccreditMsg.AccreditReq.Builder accreditBuilder = AccreditMsg.AccreditReq.newBuilder();
                            accreditBuilder.setAccreditCode("11");
                            accreditBuilder.setAccreditMac(mobileMac);
                            accreditBuilder.setUserId(Login.userId);
                            if(deviceType.equals("mobile"))
                                accreditBuilder.setDeviceType(AccreditMsg.AccreditReq.DeviceType.MOBILE);
                            else if(deviceType.equals("pc"))
                                accreditBuilder.setDeviceType(AccreditMsg.AccreditReq.DeviceType.PC);
                            accreditBuilder.setType(AccreditMsg.AccreditReq.Type.DISAGREE);
                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ACCREDIT_REQ.getNumber(), accreditBuilder
                                    .build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        AlertAccreditActivity.this.finish();
                    }
                }).start();
            }
        });



    }
}
