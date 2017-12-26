package com.dkzy.areaparty.phone.fragment03;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class pcGameHelpActivity extends Activity implements View.OnClickListener{

    private ImageButton returnLogoIB;
    private LinearLayout closeStreamLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_pcgamehelp_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        closeStreamLL.setOnClickListener(this);
    }

    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        closeStreamLL = (LinearLayout) findViewById(R.id.closeStreamLL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
            case R.id.closeStreamLL:
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        TVAppHelper.closeGameStream(MyApplication.getSelectedPCIP().ip, MyApplication.getSelectedPCIP().mac);
                        Toasty.info(this, "即将尝试断开串流", Toast.LENGTH_SHORT, true).show();
                    } else {
                        Toasty.warning(this, "当前屏幕不在线", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.warning(this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
                break;
        }
    }
}
