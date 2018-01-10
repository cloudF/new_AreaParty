package com.dkzy.areaparty.phone.register;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.dkzy.areaparty.phone.R;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class ActionDialog_ServiceAgreement extends Dialog {
    private Context context;
    /*private PDFView pdfView;*/
    private WebView webView;

    public ActionDialog_ServiceAgreement(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_ser, null);
        webView = (WebView) mView.findViewById(R.id.webView);
        displayFromAsset();


        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        //p.y = 200;
        getWindow().setAttributes(p);
    }

    private void displayFromAsset() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/service.html");

        /*pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(0)
                .onPageChange(null)
                .enableAnnotationRendering(true)
                .onLoad(null)
                .scrollHandle(new DefaultScrollHandle(context))
                .spacing(0) // in dp
                .load();*/
    }
}
