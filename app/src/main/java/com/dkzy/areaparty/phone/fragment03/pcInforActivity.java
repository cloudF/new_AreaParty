package com.dkzy.areaparty.phone.fragment03;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment03.Model.PCInforBean;
import com.dkzy.areaparty.phone.fragment03.utils.PCAppHelper;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class pcInforActivity extends Activity implements View.OnClickListener{

    private LinearLayout nameLL;
    private ImageButton returnLogoIB;
    private TextView nameTV, systemVersionTV, systemTypeTV, memoryTV, cpuNameTV, storageTV, pcName, pcDes, workGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_pcinfor_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
            case R.id.nameLL:
                changeNameDialog();
                break;
        }
    }

    private void initData() {
        if(PCAppHelper.getPcInfor().isEmpty())
            PCAppHelper.loadPCInfor(myHandler);
        else setInforView(true);
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        nameLL.setOnClickListener(this);
    }

    private void initView() {
        nameLL = (LinearLayout) findViewById(R.id.nameLL);
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        nameTV = (TextView) findViewById(R.id.nameTV);
        systemVersionTV = (TextView) findViewById(R.id.systemVersionTV);
        systemTypeTV = (TextView) findViewById(R.id.systemTypeTV);
        memoryTV = (TextView) findViewById(R.id.memoryTV);
        cpuNameTV = (TextView) findViewById(R.id.cpuNameTV);
        storageTV = (TextView) findViewById(R.id.storageTV);
        pcName = (TextView) findViewById(R.id.pcNameTV);
        pcDes = (TextView) findViewById(R.id.pcDes);
        workGroup = (TextView) findViewById(R.id.workGroupIV);

        nameTV.setText(MyApplication.getSelectedPCIP().nickName);
    }

    private void setInforView(boolean state) {
        if(state) {
            PCInforBean infor = PCAppHelper.getPcInfor();
            systemVersionTV.setText(infor.systemVersion);
            systemTypeTV.setText(infor.systemType);
            memoryTV.setText(infor.totalmemory);
            cpuNameTV.setText(infor.cpuName);
            storageTV.setText(infor.totalStorage + "(" + infor.freeStorage + "空闲)");
            pcName.setText(infor.pcName);
            pcDes.setText(infor.pcDes);
            workGroup.setText(infor.workGroup);
        }
    }

    private void changeNameDialog() {
        final ActionDialog_addFolder actionDialog = new ActionDialog_addFolder(this);
        actionDialog.setCanceledOnTouchOutside(false);
        actionDialog.show();
        actionDialog.setTitleText("设备名称");
        actionDialog.setEditText(MyApplication.getSelectedPCIP().nickName);
        final EditText editText = actionDialog.getEditTextView();
        actionDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempName = editText.getText().toString();
                if(tempName.equals("") || tempName.endsWith(".") ||
                        tempName.contains("\\") || tempName.contains("/") ||
                        tempName.contains(":")  || tempName.contains("*") ||
                        tempName.contains("?")  || tempName.contains("\"") ||
                        tempName.contains("<")  || tempName.contains(">") ||
                        tempName.contains("|")){
                    Toasty.error(pcInforActivity.this, "设备名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    nameTV.setText(tempName);
                    MyApplication.changeSelectedPCName(tempName);
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

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCInfor_OK:
                    setInforView(true);
                    break;
                case OrderConst.getPCInfor_Fail:
                    setInforView(false);
                    break;
            }
        }
    };
}
