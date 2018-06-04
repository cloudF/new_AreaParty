package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_vipLease extends Dialog {
    private TextView title;
    private TextView text;
    private Context context;

    public ActionDialog_vipLease(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.vip_leaseinfo, null);
        title = (TextView) mView.findViewById(R.id.title);
        text = (TextView) mView.findViewById(R.id.text);
        if (mView.findViewById(R.id.close)!=null){
            mView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActionDialog_vipLease.this.dismiss();
                }
            });
        }
        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        //p.y = 200;
        getWindow().setAttributes(p);
    }

    public void setText(String text){
        this.text.setText(text);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }
}
