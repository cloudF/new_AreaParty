package com.dkzy.areaparty.phone.register;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;

/**
 * Created by SnowMonkey on 2017/5/2.
 */

public class RegisterFinish extends BaseActivity {
    private TextView returnLogin;
    private MyCount mc;
    private String userId;
    private String psw;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_finish);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        psw = intent.getStringExtra("psw");
        returnLogin = (TextView) findViewById(R.id.returnLogin);
        mc = new MyCount(3000, 1000);
        mc.start();
    }

    /**自定义一个继承CountDownTimer的内部类，用于实现计时器的功能*/
    class MyCount extends CountDownTimer {
        /**
         * MyCount的构造方法
         * @param millisInFuture 要倒计时的时间
         * @param countDownInterval 时间间隔
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {//在进行倒计时的时候执行的操作
            long second = millisUntilFinished /1000;
            returnLogin.setText(second+"秒后返回登录界面");
        }

        @Override
        public void onFinish() {//倒计时结束后要做的事情
            Message registerMsg = Login.mHandler.obtainMessage();
            String str = userId + ":" + psw;
            registerMsg.obj = str;
            registerMsg.what = 6;
            Login.mHandler.sendMessage(registerMsg);
            ActivityCollector.finishAll();
        }
    }
}
