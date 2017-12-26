package com.dkzy.areaparty.phone.fragment01.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

/**
 * Created by zhuyulin on 2017/11/27.
 */

public class ActionDialog_reName extends Dialog {
    private TextView title;
    private TextView oldName;
    private EditText valueEditText;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;

    public ActionDialog_reName(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        //setCustomDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.tab01_computersetting_rename, null);
        title = (TextView) mView.findViewById(R.id.title);
        oldName = (TextView) mView.findViewById(R.id.old_name);
        valueEditText = (EditText) mView.findViewById(R.id.valueText);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100; //设置dialog的宽度为当前手机屏幕的宽度-100
        getWindow().setAttributes(p);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
    public void setOldName(String name) {
        oldName.append(name);
    }
//    public String getPathEditText() {
//        return pathEditText.getText().toString();
//    }
//    public EditText getPathEditTextView() {
//        return pathEditText;
//    }
    public EditText getValueEditTextView() { return valueEditText; }
    public void setPositiveButtonText(String text) {
        this.positiveButton.setText(text);
    }
    public void setNegativeButtonText(String text) {
        this.negativeButton.setText(text);
    }
}
