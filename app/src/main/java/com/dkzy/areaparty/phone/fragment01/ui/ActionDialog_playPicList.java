package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.AutoLoginHelperActivity;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_playPicList extends Dialog {
    private TextView title;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView autoLogin_help;
    public static int t = 5;

    public ActionDialog_playPicList(Context context) {
        super(context, R.style.CustomDialog);
        t = 5;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.tab04_playpiclist_dialog, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        positiveButton = (Button) mView.findViewById(R.id.exitpositiveButton);
        negativeButton = (Button) mView.findViewById(R.id.exitnegativeButton);
        radioGroup = (RadioGroup) mView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                switch(radioButtonId){
                    case R.id.radioButton1:
                        t = 5;
                        break;
                    case R.id.radioButton2:
                        t = 6;
                        break;
                    case R.id.radioButton3:
                        t = 7;
                        break;
                    case R.id.radioButton4:
                        t = 8;
                        break;
                    default: t = 5;
                }
                //Toast.makeText(context, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        //p.y = 200;
        getWindow().setAttributes(p);
    }


    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }

    public int getT(){
        return t;
    }

    public void setTitleText(String text) {
        this.title.setText(text);
    }
    public void setPositiveButtonText(String text) {
        this.positiveButton.setText(text);
    }
    public void setNegativeButtonText(String text) {
        this.negativeButton.setText(text);
    }
}
