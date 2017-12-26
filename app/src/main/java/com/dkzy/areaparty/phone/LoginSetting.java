package com.dkzy.areaparty.phone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

/**
 * Created by SnowMonkey on 2017/3/1.
 */

public class LoginSetting extends AppCompatActivity {
    private Switch setting_outline;
    private EditText setting_ip;
    private EditText setting_port;
    private ImageView setting_get;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginsetting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
        initEvent();
    }

    private void initView(){
        setting_outline = (Switch) findViewById(R.id.setting_outline);
        setting_ip = (EditText) findViewById(R.id.setting_ip);
        setting_port = (EditText) findViewById(R.id.setting_port);
        setting_get = (ImageView) findViewById(R.id.setting_get);
        sp = this.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        setting_ip.setText(sp.getString("SERVER_IP", bundle.getString("ip")));
        setting_port.setText(sp.getString("SERVER_PORT", bundle.getString("port")));
    }

    private void initEvent(){
//        setting_outline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//                if (isChecked) {
//                    Login.mHandler.sendEmptyMessage(4);
//                } else {
//                    Login.mHandler.sendEmptyMessage(5);
//                }
//            }
//        });
        setting_get.setOnClickListener(new ButtonListener());
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.setting_get:
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("SERVER_IP", String.valueOf(setting_ip.getText()));
                    editor.putString("SERVER_PORT",String.valueOf(setting_port.getText()));
                    editor.commit();
                    Intent intent =new Intent();
                    Bundle bundle =new Bundle();
                    bundle.putString("ip", String.valueOf(setting_ip.getText()));
                    bundle.putString("port", String.valueOf(setting_port.getText()));
                    intent.putExtras(bundle);
                    setResult(1,intent);
                    finish();
                    break;
            }
        }
    }
}
