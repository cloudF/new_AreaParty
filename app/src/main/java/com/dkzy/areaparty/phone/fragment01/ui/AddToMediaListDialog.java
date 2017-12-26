package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

/**
 * Created by zhuyulin on 2017/11/15.
 */

public class AddToMediaListDialog extends Dialog {
    private TextView title;
    private Button videoButton, audioButton, imageButton;
    private Button negativeButton;
    private Context context;

    public AddToMediaListDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.tab04_addtomedialist_dialog, null);
        title = (TextView) mView.findViewById(R.id.deletetitle);
        videoButton = (Button) mView.findViewById(R.id.videoButton);
        audioButton = (Button) mView.findViewById(R.id.audioButton);
        imageButton = (Button) mView.findViewById(R.id.imageButton);
        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        p.y = 200;
        getWindow().setAttributes(p);
    }


    public void setVideoButtonListener(View.OnClickListener listener) {
        videoButton.setOnClickListener(listener);
    }
    public void setAudioButtonListener(View.OnClickListener listener) {
        audioButton.setOnClickListener(listener);
    }
    public void setImageButtonListener(View.OnClickListener listener) {
        imageButton.setOnClickListener(listener);
    }

    public void setTitleText(String text) {
        this.title.setText(text);
    }

}
