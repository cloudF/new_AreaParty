package com.androidlearning.boris.familycentralcontroler.fragment03;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class pcAppHelpActivity extends Activity implements View.OnClickListener{

    private ImageButton returnLogoIB;
    private LinearLayout enterMiracastLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_pcapphelp_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        //enterMiracastLL.setOnClickListener(this);
    }

    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        //enterMiracastLL = (LinearLayout) findViewById(R.id.enterMiracastLL);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
//            case R.id.enterMiracastLL:
//                if(MyApplication.isSelectedPCOnline()) {
//                    if(MyApplication.isSelectedTVOnline()) {
//                        startActivity(new Intent(pcAppHelpActivity.this, pcAppMiracastActivity.class));
//                    } else {
//                        Toasty.warning(this, "当前屏幕不在线", Toast.LENGTH_SHORT, true).show();
//                    }
//                } else {
//                    Toasty.warning(this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
//                }
//                break;
        }
    }
}
