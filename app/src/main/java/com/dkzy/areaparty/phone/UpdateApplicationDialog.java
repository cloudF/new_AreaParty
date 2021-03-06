package com.dkzy.areaparty.phone;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class UpdateApplicationDialog extends Dialog {
    private TextView title;
    private TextView updataContent;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;

    public UpdateApplicationDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.update_dialog, null);
        title = (TextView) mView.findViewById(R.id.exittitle);
        updataContent = (TextView) mView.findViewById(R.id.updataContentTV);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
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
    public void setTitleText(String text) {
        this.title.setText(text);
    }
    public void setUpdataContent(String text){this.updataContent.setText(Html.fromHtml(text));}
    public void setPositiveButtonText(String text) {
        this.positiveButton.setText(text);
    }
    public void setNegativeButtonText(String text) {
        this.negativeButton.setText(text);
    }
}
