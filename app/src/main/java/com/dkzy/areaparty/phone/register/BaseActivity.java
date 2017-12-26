package com.dkzy.areaparty.phone.register;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by SnowMonkey on 2017/5/22.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
