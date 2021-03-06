package com.dkzy.areaparty.phone.fragment03;

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
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.fragment03.Model.TVInforBean;
import com.dkzy.areaparty.phone.fragment03.blueTooth.ActionDialog_blueTooth;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class tvInforActivity extends BaseActivity implements View.OnClickListener{
    private ImageButton returnLogoIB;
    private LinearLayout nameLL;
    private TextView nameTV, brandTV, modelTV, storageTV, memoryTV, resolutionTV, androidVersionTV, isRootTV,openBluetooth,closeBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_tvinfor_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
        initData();
    }

    private void initData() {
        if(TVAppHelper.getTvInfor().isEmpty())
            TVAppHelper.loadTVInfor(myHandler);
        else setInforView(true);
    }

    private void setInforView(boolean state) {
        if(state) {
            TVInforBean infor = TVAppHelper.getTvInfor();
            brandTV.setText(infor.brand);
            modelTV.setText(infor.model);
            storageTV.setText(infor.totalStorage + "(" + infor.freeStorage + ")");
            memoryTV.setText(infor.totalMemory);
            resolutionTV.setText(infor.resolution);
            androidVersionTV.setText(infor.androidVersion);
            isRootTV.setText(infor.isRoot);
        }
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        nameLL.setOnClickListener(this);
        openBluetooth.setOnClickListener(this);
        closeBluetooth.setOnClickListener(this);
    }

    private void changeNameDialog() {
        final ActionDialog_addFolder actionDialog = new ActionDialog_addFolder(this);
        actionDialog.setCanceledOnTouchOutside(false);
        actionDialog.show();
        actionDialog.setTitleText("设备名称");
        actionDialog.setEditText(MyApplication.getSelectedTVIP().nickName);
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
                    Toasty.error(tvInforActivity.this, "设备名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    nameTV.setText(tempName);
                    MyApplication.changeSelectedTVName(tempName);
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

    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        nameLL = (LinearLayout) findViewById(R.id.nameLL);
        nameTV = (TextView) findViewById(R.id.nameTV);
        brandTV = (TextView) findViewById(R.id.brandTV);
        modelTV = (TextView) findViewById(R.id.modelTV);
        storageTV = (TextView) findViewById(R.id.storageTV);
        memoryTV = (TextView) findViewById(R.id.totalMemoryTV);
        resolutionTV = (TextView) findViewById(R.id.resolutionTV);
        androidVersionTV = (TextView) findViewById(R.id.androidVersionTV);
        isRootTV = (TextView) findViewById(R.id.isRootTV);
        openBluetooth=(TextView)findViewById(R.id.openBbluetooth);
        closeBluetooth=(TextView)findViewById(R.id.closeBluetooth);

        nameTV.setText(MyApplication.getSelectedTVIP().nickName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openBbluetooth:
                TVAppHelper.vedioPlayControlOpenBlueTooth(myHandler);
                showProgressDialog();
                break;
            case R.id.closeBluetooth:
                TVAppHelper.vedioPlayControlCloseBlueTooth();
                //发送关闭tv蓝牙命令给TV端
                break;
            case R.id.returnLogoIB:
                this.finish();
                break;
            case R.id.nameLL:
                // ...更改名称
                changeNameDialog();
                break;
        }
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getTVInfor_OK:
                    setInforView(true);
                    break;
                case OrderConst.getTVInfor_Fail:
                    setInforView(false);
                    break;
                case 0:
                    hideProgressDialog();
                    Toast.makeText(MyApplication.getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    hideProgressDialog();
                    Toast.makeText(MyApplication.getContext(), "TV应用拒绝了打开蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    hideProgressDialog();
                    Toast.makeText(MyApplication.getContext(), "周围无可见的蓝牙设备", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    hideProgressDialog();
                    showSubTitleDialog();
                    Toast.makeText(MyApplication.getContext(), "可见的蓝牙设备"+TVAppHelper.blueDevices.size(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public  void showSubTitleDialog(){
        final ActionDialog_blueTooth dialog = new ActionDialog_blueTooth(this, TVAppHelper.blueDevices ,myHandler);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setTitleText("蓝牙扫描结果");
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
