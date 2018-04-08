package com.dkzy.areaparty.phone.fragment03;

import android.app.Activity;
import android.app.ProgressDialog;

public class BaseActivity extends Activity {
    protected ProgressDialog progressDialog;
    public void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(BaseActivity.this);
            progressDialog.setMessage("TV正在搜索蓝牙设备");
            progressDialog.show();
        }
    }

    public  void  hideProgressDialog(){
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

}
