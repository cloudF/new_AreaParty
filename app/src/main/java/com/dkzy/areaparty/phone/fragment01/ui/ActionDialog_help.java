package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class ActionDialog_help extends Dialog {
    private TextView title;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;
    private RadioButton radioButton;
    private TextView autoLogin_help;
    private TextView messageTV;
    private boolean isRadioButtonChecked = false;
    private LinearLayout select;
    private TextView free;
    private TextView pay;
    private String s1;
    private String s2;
    private boolean b = true;

    public ActionDialog_help(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.tab04_helpinfo_dialog, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        positiveButton = (Button) mView.findViewById(R.id.exitpositiveButton);
        negativeButton = (Button) mView.findViewById(R.id.exitnegativeButton);
        radioButton = (RadioButton) mView.findViewById(R.id.radioButton);
        messageTV = (TextView) mView.findViewById(R.id.messageTV);
        select = (LinearLayout) mView.findViewById(R.id.select);
        free = (TextView) mView.findViewById(R.id.free);
        pay = (TextView) mView.findViewById(R.id.pay);
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
        autoLogin_help = (TextView) mView.findViewById(R.id.autoLogin_help);
        autoLogin_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        autoLogin_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionDialog_help.this.dismiss();
                context.startActivity(new Intent(context ,AutoLoginHelperActivity.class));
            }
        });
        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b = true;
                messageTV.setText(s1);

                free.setTextColor(Color.parseColor("#FF5050"));
                free.setBackgroundResource(R.drawable.barback03_left_pressed);
                pay.setTextColor(Color.parseColor("#707070"));
                pay.setBackgroundResource(R.drawable.barback03_left_normal);
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b = false;
                messageTV.setText(s2);

                pay.setTextColor(Color.parseColor("#FF5050"));
                pay.setBackgroundResource(R.drawable.barback03_right_pressed);
                free.setTextColor(Color.parseColor("#707070"));
                free.setBackgroundResource(R.drawable.barback03_right_normal);
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
    public void setMessageTV(String message){
        messageTV.setText(message);
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
    public void setSelectVisible(int i){
        select.setVisibility(i);
    }
    public void setFreeText(String s){
        s1 = s;
        messageTV.setText(s1);
    }
    public void setPayText(String s){
        s2 = s;
    }

    public boolean isFree(){
        return b;
    }

}
