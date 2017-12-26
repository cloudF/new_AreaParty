package com.dkzy.areaparty.phone.fragment01.websitemanager.start;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dkzy.areaparty.phone.fragment01.websitemanager.start.StartActivity.mAccessibleIntent;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.start.StartActivity.serviceEnabled;

public class AutoLoginHelperActivity extends AppCompatActivity {
    @BindView(R.id.floatViewTV)
    TextView floatViewTV;
    @BindView(R.id.autoLoginServiceTV)
    TextView autoLoginServiceTV;
    @OnClick({R.id.autoLoginServiceTV, R.id.goSetting,R.id.floatViewTV, R.id.goback, R.id.goSetting3})
    void onclick(View view){
        switch(view.getId()){
            case R.id.floatViewTV:
                if (!MyApplication.mFloatView.isShow()){
                    MyApplication.mFloatView.showAWhile();
                }else {
                    MyApplication.mFloatView.close();
                }
                break;
            case R.id.goback:
                finish();
                break;
            case R.id.goSetting3:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                break;
            default:
                startActivity(mAccessibleIntent);
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login_helper);
        ButterKnife.bind(this);
        floatViewTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        autoLoginServiceTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
    }
    private void updateServiceStatus() {
        serviceEnabled = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.fragment05.accessible_service.AutoLoginService")) {
                serviceEnabled = true;
                break;
            }
        }
        autoLoginServiceTV.setText(serviceEnabled ? "已开启" : "已关闭");

    }
}
