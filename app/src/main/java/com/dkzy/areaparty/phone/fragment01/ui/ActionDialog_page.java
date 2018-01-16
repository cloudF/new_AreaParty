package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_page extends Dialog {
    private TextView title;
    private Context context;
    private int layout;

    public ActionDialog_page(Context context, int layout) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(layout, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        if (layout == R.layout.dialog_page01){
            WebView webView = (WebView) mView.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_asset/page01.html");
            /*mView.findViewById(R.id.wangzhi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        Uri uri = Uri.parse("www.areaparty.com");
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(uri);
                        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                        context.startActivity(intent);
                    }catch (Exception e){e.printStackTrace();}
                }
            });*/
        }
        if (mView.findViewById(R.id.close)!=null){
            mView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActionDialog_page.this.dismiss();
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
}
