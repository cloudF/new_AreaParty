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
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/6 13:00
 */

public class SharedFileDialog extends Dialog {
    private TextView title;
    private TextView shareFileName;
    private EditText editText;
    private EditText shareFileUrlET;
    private EditText shareFilePwdET;
    private Button positiveButton;
    private Button negativeButton;
    private Context context;

    public SharedFileDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.tab04_sharefile_dialog, null);
        title = (TextView) mView.findViewById(R.id.shareFileTitleTV);
        shareFileName = (TextView) mView.findViewById(R.id.shareFileNameTV);
        editText = (EditText) mView.findViewById(R.id.shareFileDesET);
        shareFileUrlET = (EditText) mView.findViewById(R.id.shareFileUrlET);
        shareFilePwdET = (EditText) mView.findViewById(R.id.shareFilePwdET);
        positiveButton = (Button) mView.findViewById(R.id.shareFilePositiveButton);
        negativeButton = (Button) mView.findViewById(R.id.shareFileNegativeButton);
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

    public void setFileName(String text) {
        this.shareFileName.setText(text);
    }
    public void setTitleText(String text) {
        this.title.setText(text);
    }
    public void setEditHintText(String hint) {
        editText.setHint(hint);
    }
    public String getEditText() {
        return editText.getText().toString();
    }
    public EditText getEditTextView() {
        return editText;
    }
    public String getUrlEditText() {
        return shareFileUrlET.getText().toString();
    }
    public EditText getUrlEditTextView() {
        return shareFileUrlET;
    }
    public String getPwdEditText() {
        return shareFilePwdET.getText().toString();
    }
    public EditText getPwdEditTextView() {
        return shareFilePwdET;
    }
    public void setPositiveButtonText(String text) {
        this.positiveButton.setText(text);
    }
    public void setNegativeButtonText(String text) {
        this.negativeButton.setText(text);
    }
}
