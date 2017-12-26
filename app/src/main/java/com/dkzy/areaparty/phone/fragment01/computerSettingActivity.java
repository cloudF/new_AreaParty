package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.DialogMessageConst;
import com.dkzy.areaparty.phone.MyConnector;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class computerSettingActivity extends Activity implements View.OnClickListener{

    private ImageButton returnLogo_imgButton;
    private LinearLayout rebootComputer_linearLayout;
    private LinearLayout checkExe_linearLayout;
    private LinearLayout regEdit_linearLayout;
    private ImageView rebootComputer_moreInforIV;
    private ImageView checkExe_moreInforIV;
    private ImageView regEdit_moreInforIV;

    private View loadingView;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_computersetting_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
    }

    private void initEvent() {
        returnLogo_imgButton.setOnClickListener(this);
        rebootComputer_linearLayout.setOnClickListener(this);
        checkExe_linearLayout.setOnClickListener(this);
        regEdit_linearLayout.setOnClickListener(this);
        rebootComputer_moreInforIV.setOnClickListener(this);
        checkExe_moreInforIV.setOnClickListener(this);
        regEdit_moreInforIV.setOnClickListener(this);
    }

    private void initView() {
        returnLogo_imgButton = (ImageButton) findViewById(R.id.returnLogo_imgButton);
        rebootComputer_linearLayout = (LinearLayout) findViewById(R.id.rebootComputer_linearLayout);
        checkExe_linearLayout = (LinearLayout) findViewById(R.id.checkExe_linearLayout);
        regEdit_linearLayout = (LinearLayout) findViewById(R.id.regEdit_linearLayout);
        rebootComputer_moreInforIV = (ImageView) findViewById(R.id.rebootComputer_moreInforIV);
        checkExe_moreInforIV = (ImageView) findViewById(R.id.checkExe_moreInforIV);
        regEdit_moreInforIV = (ImageView) findViewById(R.id.regEdit_moreInforIV);

        loadingView  = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView);
        builder.setCancelable(false);
        dialog = builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rebootComputer_moreInforIV:
                Toasty.info(this, "本操作将会重启电脑并再次尝试连接电脑", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.checkExe_moreInforIV:
                Toasty.info(this, "本操作将获取主机已安装的非微软应用程序", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.regEdit_moreInforIV:
                Toasty.info(this, "本操作将允许您编辑主机注册表", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.returnLogo_imgButton:
                computerSettingActivity.this.finish();
                break;
            case R.id.rebootComputer_linearLayout:
                boolean state = MyConnector.getInstance().getConnetedState();
                if(state) {
                    rebootNoticeDialog();
                } else {
                    Toasty.error(this, "主机端未打开服务程序\n请先打开服务程序", Toast.LENGTH_SHORT, true).show();
                }
                break;
            case R.id.checkExe_linearLayout:
                if(MyConnector.getInstance().getConnetedState()) {
                    Intent intent = new Intent(computerSettingActivity.this, exeContentActivity.class);
                    startActivity(intent);
                } else {
                    Toasty.error(this, "主机端未打开服务程序\n请先打开服务程序", Toast.LENGTH_SHORT, true).show();
                }
                break;
            case R.id.regEdit_linearLayout:
                changeRegDialog();
                break;
        }
    }

    private void rebootNoticeDialog() {
        SpannableString ss = new SpannableString(DialogMessageConst.autoLoginNote);
        ss.setSpan(new URLSpan(DialogMessageConst.autoLoginLink),
                0, DialogMessageConst.autoLoginNote.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        View titleView = LayoutInflater.from(this).inflate(R.layout.tab01_computermonitor_noticetitle, null);
        View contentView = LayoutInflater.from(this).inflate(R.layout.tab01_computermonitor_reboot_dialogcontent, null);
        TextView textView = (TextView) contentView.findViewById(R.id.autoLoginHelper_tv);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(titleView);
        builder.setView(contentView);
        builder.setNegativeButton("取消重启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("确定重启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread() {
                    @Override
                    public void run() {
                        prepareDataForFragment.getActionStateData(OrderConst.computerAction_name,
                                OrderConst.computerAction_reboot_command, "");
                    }
                }.start();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void changeRegDialog() {
        final ActionDialog actionDialog = new ActionDialog(this);
        actionDialog.setCanceledOnTouchOutside(false);
        actionDialog.show();
        final EditText pathEditText = actionDialog.getPathEditTextView();
        final EditText valueEditText = actionDialog.getValueEditTextView();
        actionDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regPathName = pathEditText.getText().toString();
                String value = valueEditText.getText().toString();
                if(regPathName.equals("") || regPathName.endsWith(".") ||
                        regPathName.contains("\\") || regPathName.contains("/") ||
                        regPathName.contains(":")  || regPathName.contains("*") ||
                        regPathName.contains("?")  || regPathName.contains("\"") ||
                        regPathName.contains("<")  || regPathName.contains(">") ||
                        regPathName.contains("|")){
                    Toasty.error(computerSettingActivity.this, "注册表表项路径不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    pathEditText.setText("");
                } else if(value.equals("")) {
                    Toasty.error(computerSettingActivity.this, "表项值不能为空，请先输入合法字符串", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                    dialog.show();
                    changeReg(regPathName, value);
                    actionDialog.dismiss();
                }
            }
        });
        actionDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDialog.dismiss();
            }
        });
    }

    private void changeReg(String regPathName, String value) {
    }
}
