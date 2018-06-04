package com.dkzy.areaparty.phone.fragment06;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dkzy.areaparty.phone.R;

public class GroupSetting extends AppCompatActivity {
    private ImageButton groupSettingBackBtn;
    private LinearLayout groupChangeGroupName;
    //private LinearLayout groupDeleteGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_groupsetting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        initView();
        initEvent();
    }
    private void initView() {
        groupSettingBackBtn = (ImageButton)findViewById(R.id.groupSettingBackBtn);
        groupChangeGroupName = (LinearLayout)findViewById(R.id.groupChangeGroupName);
        //groupDeleteGroup = (LinearLayout)findViewById(R.id.groupDeleteGroup);
    }

    private void initEvent() {
        groupSettingBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupSetting.this.finish();
            }
        });
        groupChangeGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        groupDeleteGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GroupSetting.this.finish();
//            }
//        });
    }

    private void initData() {

    }


}
