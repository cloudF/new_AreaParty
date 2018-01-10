package com.dkzy.areaparty.phone.fragment01.websitemanager.start;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import com.dkzy.areaparty.phone.BuildConfig;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utilseverywhere.utils.IntentUtils;

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
                Log.w("AutoLoginHelperActivity",Build.MANUFACTURER);
                IntentUtils.gotoPermissionSetting();
                /*switch (Build.MANUFACTURER){
                    case "Xiaomi":{
                        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                        intent.setComponent(componentName);
                        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
                        startActivity(intent);
                        break;
                    }
                    case "Huawei":
                    case "HUAWEI":{
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SubTitleUtil");
                        intent.setComponent(comp);
                        startActivity(intent);
                        break;
                    }
                    case "Meizu":{
                        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                        startActivity(intent);
                        break;
                    }
                    case "Sony":{
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
                        intent.setComponent(comp);
                        startActivity(intent);
                        break;
                    }
                    case "OPPO":{
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
                        intent.setComponent(comp);
                        startActivity(intent);
                        break;
                    }
                    case "LG":{
                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
                        intent.setComponent(comp);
                        startActivity(intent);
                        break;
                    }
                    case "vivo":{

                    }
                    case "samsung":{

                    }
                    case "Letv":{
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
                        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
                        intent.setComponent(comp);
                        break;
                    }
                    case "ZTE":{

                    }
                    case "YuLong":{

                    }
                    case "LENOVO":{

                    }
                    default:{
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }*/

                break;
            default:
                IntentUtils.gotoAccessibilitySetting();
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
        autoLoginServiceTV.setText(serviceEnabled ? "已开启" : "未开启");

    }
}
