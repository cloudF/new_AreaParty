package com.dkzy.areaparty.phone;
//************处理电脑登录账号和非主设备登录账号的授权问题*************
//*************主设备app调用此界面弹出窗口来授权或拒绝***********

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dkzy.areaparty.phone.fragment06.dealFileRequest;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;

import protocol.Msg.AccreditMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

public class AlertRequestActivity extends AppCompatActivity {

    public static View accreditView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("授权消息");
        Intent intent =  getIntent();
        final String FileName = intent.getStringExtra("fileName");
        final String userId = intent.getStringExtra("userId");
        accreditView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.request_dialog, null);
        setContentView(accreditView);
        final String text = "用户："+userId+"\n"+"请求下载文件：\n"+FileName;
        ((TextView)accreditView.findViewById(R.id.accreditRequestDialogInfo)).setText(text);
        ((Button)accreditView.findViewById(R.id.accreditDialogOnly)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertRequestActivity.this.finish();
            }
        });
        ((Button)accreditView.findViewById(R.id.accreditDialogAlways)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), dealFileRequest.class);
                startActivity(intent);
                AlertRequestActivity.this.finish();
            }
        });
    }
}
