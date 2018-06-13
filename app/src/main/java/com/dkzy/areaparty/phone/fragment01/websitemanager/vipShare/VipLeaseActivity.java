package com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_help;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_reName;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_vipLease;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.websitemanager.readSms.ReadSmsService;
import com.dkzy.areaparty.phone.fragment05.accessible_service.AutoLoginService;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.floatview.FloatView;
import com.dkzy.areaparty.phone.utilseverywhere.utils.AccessibilityUtils;
import com.dkzy.areaparty.phone.utilseverywhere.utils.IntentUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import protocol.Msg.VipMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.Login.getAdresseMAC;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.getAppName;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.getVersionCode;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.iqiyiVersionCode;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.openApp;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.serviceEnabled;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.tencentVersionCode;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity.youkuVersionCode;

public class VipLeaseActivity extends AppCompatActivity {
    private int shareCount = 0;
    private int app = -1;
    private int status = -1;

    private String userMobile = "";
    private boolean smsCanReceive = false;
    private String zfbAccount = "";

    private MyCount mc = null;
    private boolean wait = false;

    @BindView(R.id.accountET)
    EditText accountET;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.applicationSP)
    Spinner applicationSP;
    @BindView(R.id.totalCountSP)
    Spinner totalCountSP;
    @BindView(R.id.submitBT)
    Button submitBT;
    @BindView(R.id.floatViewTV)
    TextView floatViewTV;
    @BindView(R.id.autoLoginServiceTV)
    TextView autoLoginServiceTV;
    @BindView(R.id.smsReaderTV)
    TextView smsReaderTV;
    @BindView(R.id.settingTV)
    TextView settingTV;
    @BindView(R.id.messageTV)
    TextView messageTV;
    @BindView(R.id.messageTV1)
    TextView messageTV1;
    @BindView(R.id.messageTV2)
    TextView messageTV2;

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceEnabled = AccessibilityUtils.isAccessibilitySettingsOn();
        autoLoginServiceTV.setText(serviceEnabled ? "已开启" : "未开启");
        iqiyiVersionCode = getVersionCode(AutoLoginService.IQIYI);//获取爱奇艺的版本号
        youkuVersionCode = getVersionCode(AutoLoginService.YOUKU);//获取优酷的版本号
        tencentVersionCode = getVersionCode(AutoLoginService.TENCENT);//获取腾讯视频版本号

        if (app >= 0 && app <= 3) {
            if (status == 0) {
                Toasty.error(this, "检测到你提供的" + getAppName(app) + "账号未登录成功，暂不能出租", Toast.LENGTH_LONG).show();
            } else if (status == 1) {
                Toasty.error(this, "检测到你提供的" + getAppName(app) + "账号密码匹配，但不是VIP账号，不能出租", Toast.LENGTH_LONG).show();
            } else if (status == 2) {
                Toasty.success(this, "你提供的" + getAppName(app) + "账号密码通过了验证，正在发送到服务器", Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "你提供的" + getAppName(app) + "账号密码通过了验证，正在发送到服务器", Toast.LENGTH_SHORT).show();
                senVipLeaseInfo();
            }
            status = -1;
            app = -1;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_lease);

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(VipLeaseActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VipLeaseActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        initViews();

        getVipLeaseHistory();
    }

    private void initViews() {
        floatViewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyApplication.mFloatView.isShow()) {
                    MyApplication.mFloatView.showAWhile();
                } else {
                    MyApplication.mFloatView.close();
                }
            }
        });
        floatViewTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        autoLoginServiceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.gotoAccessibilitySetting();
            }
        });
        autoLoginServiceTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        settingTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        settingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ActionDialog_reName dialog = new ActionDialog_reName(VipLeaseActivity.this);
                dialog.setCancelable(true);
                dialog.show();
                dialog.setTitleText("支付宝账号设置");
                String message = "";
                if (TextUtils.isEmpty(zfbAccount)) {
                    message = "您当前未设置支付宝账号";
                } else {
                    message = "你当前设置的支付宝账号为：" + zfbAccount;
                }
                dialog.setNameInfo("你出租的账号被人租用时，你会获得租金，每月月底自动转到你设置的支付宝账号 \n支付宝账号支持邮件和手机号\n" + message);
                dialog.setValueEditTextHint("支付宝账号设置");
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String zfb = dialog.getValueEditTextView().getText().toString();
                        if (TextUtils.isEmpty(zfb)) {
                            Toasty.error(VipLeaseActivity.this, "账号不能为空").show();
                        } else {
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        VipMsg.zmfAccountSettingReq.Builder builder = VipMsg.zmfAccountSettingReq.newBuilder();
                                        builder.setUserId(Login.userId);
                                        builder.setZfbAccount(zfb);
                                        byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ZFB_ACCOUNT_SETTING_VALUE, builder.build().toByteArray());
                                        if (Login.base != null) {
                                            Login.base.writeToServer(Login.outputStream, byteArray);
                                            EventBus.getDefault().post(true, "smsVerify");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }

                    }
                });
            }
        });
        messageTV1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        messageTV2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        smsReaderTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        smsReaderTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DeleteDialog dialog = new DeleteDialog(VipLeaseActivity.this);
                dialog.setCancelable(true);
                dialog.show();
                dialog.setTitleText("短信验证码读取功能验证");
                dialog.setMessageTV("爱奇艺账号，优酷视频账号在新设备上登录时需要验证码，若你要分享爱奇艺和优酷视频VIP账号，需要验证你的手机是否按说明进行了设置相关权限\n短信验证码读取功能：" + smsReaderTV.getText().toString());
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (!smsCanReceive) {
                    dialog.setPositiveButtonText("验证");
                    dialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            VipLeaseActivity.this.startService(new Intent(VipLeaseActivity.this, ReadSmsService.class));
                            if (wait) {
                                if (mc != null) {
                                    Toast.makeText(VipLeaseActivity.this, "你的操作太频繁，请" + mc.getSecond() + "秒后再试", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            mc = new MyCount(60000, 1000);
                            mc.start();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        VipMsg.SmsTestReq.Builder builder = VipMsg.SmsTestReq.newBuilder();
                                        builder.setUserId(Login.userId);
                                        byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SMS_TEST_VALUE, builder.build().toByteArray());
                                        if (Login.base != null) {
                                            Login.base.writeToServer(Login.outputStream, byteArray);

                                            EventBus.getDefault().post(true, "smsVerify");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    });

                } else {
                    dialog.setPositiveInVisibility();
                }
            }
        });
    }

    private void getVipLeaseHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.VipLeaseReq.Builder builder = VipMsg.VipLeaseReq.newBuilder();
                    builder.setUserId(Login.userId);
                    builder.setUserMac(getAdresseMAC(VipLeaseActivity.this));
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.VIP_LEASE_VALUE, builder.build().toByteArray());
                    if (Login.base != null) {
                        Login.base.writeToServer(Login.outputStream, byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                    Toast.makeText(this, "你启动了短信权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "你没启动权限", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }

    @OnClick(R.id.submitBT)
    public void onClick() {
        if (!serviceEnabled) {
            //Toasty.warning(this, "请先开启自助登录服务").show();
            showDialog("提示：","请先开启自助登录服务");
            return;
        }
        if (TextUtils.isEmpty(accountET.getText().toString())) {
            Toast.makeText(this, "账号为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordET.getText().toString())) {
            Toast.makeText(this, "密码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(zfbAccount)) {
            //Toast.makeText(this, "请先设置支付宝收款账号", Toast.LENGTH_SHORT).show();
            showDialog("提示：","请先设置支付宝收款账号");
            return;
        }
        if (applicationSP.getSelectedItemPosition() > 0 && !accountET.getText().toString().equals(userMobile)) {
            //Toast.makeText(this, getAppName(applicationSP.getSelectedItemPosition()) + "账号仅支持AreaParty注册电话号码", Toast.LENGTH_SHORT).show();
            showDialog("提示：",getAppName(applicationSP.getSelectedItemPosition()) + "账号仅支持AreaParty注册电话号码");
            return;
        }
        if (applicationSP.getSelectedItemPosition() > 0 && !smsCanReceive) {
            //Toast.makeText(this, "分享" + getAppName(applicationSP.getSelectedItemPosition()) + "账号需要先验证短信验证码读取功能", Toast.LENGTH_SHORT).show();
            showDialog("提示：","分享" + getAppName(applicationSP.getSelectedItemPosition()) + "账号需要先验证短信验证码读取功能");
            return;
        }
        final ActionDialog_help dialog = new ActionDialog_help(this);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setTitleText(getAppName(applicationSP.getSelectedItemPosition()) + "  VIP账号出租");
        dialog.setMessageTV("为了检测你提供的密码是否正确，请按说明执行一次登录操作");
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (getVersionCode(applicationSP.getSelectedItemPosition()) == 0) {
                    Toasty.error(VipLeaseActivity.this, "你未安装 " + getAppName(applicationSP.getSelectedItemPosition())).show();
                } else {
                    AutoLoginService.state = getAccessibilityState(applicationSP.getSelectedItemPosition());
                    openApp(applicationSP.getSelectedItemPosition());

                    FloatView.name = accountET.getText().toString();
                    FloatView.password = passwordET.getText().toString();
                    FloatView.appId = applicationSP.getSelectedItemPosition();
                    shareCount = totalCountSP.getSelectedItemPosition() + 1;
                }
            }
        });


    }

    private void senVipLeaseInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.VipLeaseInfoReq.Builder builder1 = VipMsg.VipLeaseInfoReq.newBuilder();
                    builder1.setUserId(Login.userId);
                    builder1.setUserMac(getAdresseMAC(getApplicationContext()));
                    builder1.setAccount(FloatView.name);
                    builder1.setPassword(FloatView.password);
                    builder1.setApplication(FloatView.appId);
                    builder1.setTotalCount(shareCount);
                    builder1.setAddress(MyApplication.location);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.VIP_LEASEINFO_VALUE, builder1.build().toByteArray());
                    if (Login.base != null) {
                        Login.base.writeToServer(Login.outputStream, byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static int getAccessibilityState(int appId) {
        switch (appId) {
            case 0:
                return AutoLoginService.TENCENT_LOGIN_TEST;
            case 1:
                return AutoLoginService.IQIYI_LOGIN_TEST;
            case 2:
                return AutoLoginService.YOUKU_LOGIN_TEST;
            default:
                return AutoLoginService.NO_ACTION;
        }
    }

    @Subscriber(tag = "vipLeaseInfo", mode = ThreadMode.MAIN)
    private void vipLeaseInfo(VipMsg.VipLeaseInfoRsp response) {
        Log.w("vipLease", response.toString());
        //Toast.makeText(this, "response:" + response, Toast.LENGTH_SHORT).show();
        if (response.getResultCode().equals(VipMsg.VipLeaseInfoRsp.ResultCode.SUCCESS)) {
            getVipLeaseHistory();
            accountET.setText("");
            passwordET.setText("");
        } else {
            /*Toasty.error(this,response.getMessage(),Toast.LENGTH_LONG).show();*/
            showDialog("出租失败：",response.getMessage());
        }
    }

    @Subscriber(tag = "vipLease", mode = ThreadMode.MAIN)
    private void vipLease(VipMsg.VipLeaseRsp response) {
        //Toast.makeText(this, "response:" + response, Toast.LENGTH_SHORT).show();
        Log.w("vipLease", response.toString());
        userMobile = response.getUerMobile();
        smsCanReceive = response.getSmsCanReceive();
        zfbAccount = response.getZfbAccount();
        if (smsCanReceive) {
            smsReaderTV.setText("已验证");
        } else {
            smsReaderTV.setText("未验证");
        }
        messageTV.setText(response.getMessage());

        if (response.getSmsReaderShouldOpen()) {
            startService(new Intent(this, ReadSmsService.class));
        }
    }

    @Subscriber(tag = "iqiyiLoginTest", mode = ThreadMode.MAIN)
    private void iqiyiLoginTest(int i) {
        app = 1;
        status = i;
    }

    @Subscriber(tag = "youkuLoginTest", mode = ThreadMode.MAIN)
    private void youkuLoginTest(int i) {
        app = 2;
        status = i;
    }

    @Subscriber(tag = "tencentLoginTest", mode = ThreadMode.MAIN)
    private void tencentLoginTest(int i) {
        app = 0;
        status = i;
    }

    @Subscriber(tag = "smsTestVerify", mode = ThreadMode.MAIN)
    private void smsTestVerify(VipMsg.SmsTestVerifyRsp response) {
        if (response.getVerify()) {
            smsCanReceive = true;
            Toast.makeText(this, "验证码短信接收已验证", Toast.LENGTH_SHORT).show();
            smsReaderTV.setText("已验证");
        } else {
            Toast.makeText(this, "验证码接收错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscriber(tag = "zfnAccountSetting", mode = ThreadMode.MAIN)
    private void zfnAccountSetting(VipMsg.zmfAccountSettingRsp response) {
        if (response.getResult()) {
            Toast.makeText(this, "支付宝收款账户设置成功", Toast.LENGTH_SHORT).show();
            zfbAccount = response.getZfbAccount();
        } else {
            Toast.makeText(this, "支付宝收款账户设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscriber(tag = "leaseMessage", mode = ThreadMode.MAIN)
    private void leaseMessage(VipMsg.vipLeaseMessageRsp response){
        if (response.getType().equals(VipMsg.vipLeaseMessageRsp.Type.Accountlog)){
            showMessage("出租账号信息",response.getText());
        }else if (response.getType().equals(VipMsg.vipLeaseMessageRsp.Type.Leaselog)){
            showMessage("账号出租记录",response.getText());
        }
    }

    @OnClick({R.id.messageTV1, R.id.messageTV2})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.messageTV1:
                sendLeaseMessageReq(1);
                break;
            case R.id.messageTV2:
                sendLeaseMessageReq(2);
                break;
        }
    }
    private void sendLeaseMessageReq(final int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VipMsg.vipLeaseMessageReq.Builder builder1 = VipMsg.vipLeaseMessageReq.newBuilder();
                    if (type == 1){
                        builder1.setType(VipMsg.vipLeaseMessageReq.Type.Accountlog);
                    }else if (type == 2){
                        builder1.setType(VipMsg.vipLeaseMessageReq.Type.Leaselog);
                    }
                    builder1.setUserId(Login.userId);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.LEASSEMESSAGE_VALUE, builder1.build().toByteArray());
                    if (Login.base != null) {
                        Login.base.writeToServer(Login.outputStream, byteArray);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showMessage(String title, String text){
        final ActionDialog_vipLease dialog = new ActionDialog_vipLease(VipLeaseActivity.this);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setText(text);
        dialog.setTitle(title);
    }

    private void showDialog(String title, String text){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.show();
    }

    class MyCount extends CountDownTimer {
        private long second;

        /**
         * MyCount的构造方法
         *
         * @param millisInFuture    要倒计时的时间
         * @param countDownInterval 时间间隔
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            wait = true;
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {//在进行倒计时的时候执行的操作
            second = millisUntilFinished / 1000;
            //btn_send_code.setText(second+"秒");
            if (second == 60) {
                //   btn_send_code.setText(59+"秒");
            }
        }

        @Override
        public void onFinish() {//倒计时结束后要做的事情
            // TODO Auto-generated method stub
            /*btn_send_code.setText("重新获取");
            btn_send_code.setBackgroundResource(R.drawable.buttonradius);
            btn_send_code.setEnabled(true);*/
            wait = false;
        }

        public long getSecond() {
            return second;
        }
    }
}
