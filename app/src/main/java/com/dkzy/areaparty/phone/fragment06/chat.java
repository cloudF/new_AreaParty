package com.dkzy.areaparty.phone.fragment06;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

import java.util.List;

import protocol.Data.FileData;
import protocol.Msg.GetUserInfoMsg;

/**
 * Created by SnowMonkey on 2017/2/16.
 */

public class chat extends AppCompatActivity implements View.OnClickListener{
    private GetUserInfoMsg.GetUserInfoReq.Builder builder = GetUserInfoMsg.GetUserInfoReq.newBuilder();
    private TextView chatObj;
    private ImageButton chat_btn_back;
    private ImageButton friendFileList;
    private Button chat_btn_send;
    private EditText et_sendmessage;
    private String userId;
    public static Handler mHandler;
    public static List<FileData.FileItem> lf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String userName = bundle.getString("userName");
        userId = bundle.getString("userId");
        initView(userName);
        initEvent();
    }

    private void initView(String userName){
        //chatObj = (TextView) this.findViewById(R.id.chatObj);
        chat_btn_send = (Button) this.findViewById(R.id.chat_btn_send);
        et_sendmessage = (EditText) this.findViewById(R.id.et_sendmessage);
        chatObj.setText(userName);
    }

    private void initEvent(){
    }

    @Override
    public void onClick(View view) {
    }


}
