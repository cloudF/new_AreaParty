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

public class ActionDialog_help extends Dialog {
    private TextView title;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;
    private RadioButton radioButton;
    private TextView autoLogin_help;
    private boolean isRadioButtonChecked = false;

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
}
