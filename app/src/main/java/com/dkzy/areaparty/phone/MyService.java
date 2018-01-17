package com.dkzy.areaparty.phone;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;

import protocol.Msg.AccreditMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/6/19.
 */

public class MyService extends Service {
    private MyBinder mBinder = new MyBinder();
    public static View accreditView;
    public static android.app.AlertDialog dialog = null;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(intent, flags, startId);
        String style = intent.getStringExtra("style");
        System.out.println("授权消息");
        //************此段代码已废弃，已使用AlertActivity.java实现授权登录功能*******************
        if(style.equals("accreditRequest")){
            System.out.println("授权消息");
            String mobileInfo = intent.getStringExtra("mobileInfo");
            final String mobileMac = intent.getStringExtra("mobileMac");
            final String deviceType = intent.getStringExtra("deviceType");
            accreditView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.request_accredit_dialog, null);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyApplication.getContext());
            builder.setView(accreditView);
            dialog = builder.create();
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
                        }
                    }).start();
                }
            });
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//android6.0以上需要手动开启悬浮窗权限
            dialog.show();
            //****************************************************************************************************************************************
        }else if(style.equals("accreditFail")){
            Toast.makeText(MyApplication.getContext(), "处理失败",Toast.LENGTH_SHORT).show();
            //dialog.dismiss();
            MyApplication.getContext().stopService(intent);
        }else if(style.equals("accreditSuccess")){
            Toast.makeText(MyApplication.getContext(), "处理成功",Toast.LENGTH_SHORT).show();
            //dialog.dismiss();
            MyApplication.getContext().stopService(intent);
        }else if(style.equals("holeMsg")){
            Toast.makeText(MyApplication.getContext(), intent.getStringExtra("msgInfo"), Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyBinder extends Binder {

    }
}
