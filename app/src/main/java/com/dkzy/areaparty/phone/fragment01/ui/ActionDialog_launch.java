package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.AutoLoginHelperActivity;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_launch extends Dialog {
    private TextView title;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;
    private RadioButton radioButton;
    private TextView autoLogin_help;
    private boolean isRadioButtonChecked = false;
    private TextView text1,text2,text3;

    public ActionDialog_launch(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_launch, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        positiveButton = (Button) mView.findViewById(R.id.exitpositiveButton);
        negativeButton = (Button) mView.findViewById(R.id.exitnegativeButton);
        radioButton = (RadioButton) mView.findViewById(R.id.radioButton);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRadioButtonChecked){
                    radioButton.setChecked(false);
                    isRadioButtonChecked = false;
                }else {
                    radioButton.setChecked(true);
                    isRadioButtonChecked = true;
                }
            }
        });

        text1 = (TextView) mView.findViewById(R.id.text1);
        text2 = (TextView) mView.findViewById(R.id.text2);
        text3 = (TextView) mView.findViewById(R.id.text3);

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

    public boolean isRadioButtonChecked(){
        return isRadioButtonChecked;
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
    public void setText1(String text){
        text1.setText(text);
    }
    public void setText2(String text){
        text2.setText(text);
    }
    public void setText3InVisible(){
        text3.setVisibility(View.GONE);
    }
    public void setRadioButtonText(String text){
        radioButton.setText(text);
    }
}
