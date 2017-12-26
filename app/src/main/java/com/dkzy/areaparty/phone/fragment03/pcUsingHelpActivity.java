package com.dkzy.areaparty.phone.fragment03;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dkzy.areaparty.phone.R;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class pcUsingHelpActivity extends Activity implements View.OnClickListener{

    private ImageButton returnLogoIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_usinghelp_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
    }

    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
        }
    }
}
