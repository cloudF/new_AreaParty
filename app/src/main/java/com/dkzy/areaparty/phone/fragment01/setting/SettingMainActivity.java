package com.dkzy.areaparty.phone.fragment01.setting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.inforUtils.Update_ReceiveMsgBean;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import androidkun.com.versionupdatelibrary.entity.VersionUpdateConfig;
import es.dmoral.toasty.Toasty;

/**
 * Created by SnowMonkey on 2017/7/14.
 */

public class SettingMainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout changeUserNameLL, changeUserPwdLL, changeUserAddressLL, updateVersionLL;
    private TextView newVersionInforTV;
    private ImageButton settingBackBtn;

    private boolean outline = true;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_setting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyApplication.getInstance().addActivity(this);

        initData();
        initView();
        initEvent();
    }

    private void initData() {
        outline = this.getIntent().getExtras().getBoolean("isOutline");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscriber(tag = "newVersionInforChecked")
    private void upDateVersionInfor(Update_ReceiveMsgBean event) {
        if(event.isNew) {
            newVersionInforTV.setText("已更新到最新版本");
        } else {
            newVersionInforTV.setText("监测到新版本,点击更新");
        }
    }

    private void initView(){
        changeUserNameLL = (LinearLayout) this.findViewById(R.id.changeUserNameLL);
        changeUserPwdLL = (LinearLayout) this.findViewById(R.id.changeUserPwdLL);
        changeUserAddressLL = (LinearLayout) this.findViewById(R.id.changeUserAddressLL);
        updateVersionLL = (LinearLayout) findViewById(R.id.updateVersionLL);
        newVersionInforTV = (TextView) findViewById(R.id.newVersionInforTV);
        settingBackBtn = (ImageButton) this.findViewById(R.id.settingBackBtn);

        if(!(MyApplication.getReceiveMsgBean().isEmpty()) && !(MyApplication.getReceiveMsgBean().isNew)) {
            newVersionInforTV.setText("监测到新版本,点击更新");
        } else {
            newVersionInforTV.setText("已更新到最新版本");
        }
    }

    private void initEvent(){
        changeUserNameLL.setOnClickListener(this);
        changeUserPwdLL.setOnClickListener(this);
        changeUserAddressLL.setOnClickListener(this);
        settingBackBtn.setOnClickListener(this);
        updateVersionLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changeUserNameLL:
                if(outline) {
                    Toasty.error(this, "当前用户未登录, 不能修改昵称", Toast.LENGTH_SHORT, true).show();
                } else
                    startActivity(new Intent(this, SettingNameActivity.class));
                break;
            case R.id.changeUserPwdLL:
                if(outline) {
                    Toasty.error(this, "当前用户未登录, 不能修改密码", Toast.LENGTH_SHORT, true).show();
                } else
                    startActivity(new Intent(this, SettingPwdActivity.class));
                break;
            case R.id.changeUserAddressLL:
                if(outline) {
                    Toasty.error(this, "当前用户未登录, 不能修改地址", Toast.LENGTH_SHORT, true).show();
                } else
                    startActivity(new Intent(this, SettingAddressActivity.class));
                break;
            case R.id.settingBackBtn:
                this.finish();
                break;
            case R.id.updateVersionLL:
                if(MyApplication.getReceiveMsgBean().isNew) {
                    Toasty.info(this, "当前已是最新版本", Toast.LENGTH_SHORT, true).show();
                } else {
                    VersionUpdateConfig.getInstance()
                        .setContext(this)
                        .setDownLoadURL(MyApplication.getReceiveMsgBean().url)
                        .setNotificationIconRes(R.mipmap.app_logo)
                        .setNotificationSmallIconRes(R.mipmap.app_logo)
                        .setNotificationTitle("AreaParty应用升级")
                        .startDownLoad();
                    Toasty.info(this, "即将下载最新版本", Toast.LENGTH_SHORT, true).show();
                }
                break;
        }
    }
}
