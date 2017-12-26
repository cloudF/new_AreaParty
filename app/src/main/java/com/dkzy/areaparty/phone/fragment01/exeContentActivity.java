package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ExeInformat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedExeListFormat;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class exeContentActivity extends Activity implements View.OnClickListener{

    private ImageButton returnLogo_imgButton;
    private ListView exeContentLV;
    private android.support.v7.app.AlertDialog dialog;

    ExeInformat[] exeDatas = new ExeInformat[0];
    private MyExeAdapter exeAdapter;

    class ViewHolderExe {
        TextView nameView;
        TextView publisherInforView;
        TextView versionInforView;
    }
    private class MyExeAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        public MyExeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return exeDatas.length;
        }

        @Override
        public Object getItem(int i) {
            return exeDatas[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolderExe holder;
            if(view == null) {
                view = mInflater.inflate(R.layout.tab01_exe_item, null);
                holder = new ViewHolderExe();
                holder.nameView  = (TextView) view.findViewById(R.id.exe_name);
                holder.publisherInforView  = (TextView) view.findViewById(R.id.publisherInformation);
                holder.versionInforView = (TextView) view.findViewById(R.id.versionInformation);
                view.setTag(holder);
            } else {
                holder = (ViewHolderExe) view.getTag();
            }

            holder.nameView.setText(exeDatas[i].displayName);
            holder.publisherInforView.setText(exeDatas[i].publisher);
            holder.versionInforView.setText(exeDatas[i].displayVersion);
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_computermonitor_exechecking_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initEvent();
        loadingExe();
    }

    private void loadingExe() {
        dialog.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    ReceivedExeListFormat exes = (ReceivedExeListFormat)
                            prepareDataForFragment.getExeActionStateData(
                                    OrderConst.exeAction_name,
                                    OrderConst.appAction_get_command, "");
                    if(exes.getStatus() == OrderConst.success) {
                        exeDatas = new ExeInformat[exes.getData().size()];
                        for(int i = 0; i < exes.getData().size(); i++) {
                            exeDatas[i] = new ExeInformat();
                            exeDatas[i].displayName = exes.getData().get(i).displayName;
                            exeDatas[i].displayVersion = exes.getData().get(i).displayVersion;
                            exeDatas[i].publisher = exes.getData().get(i).publisher;
                        }
                        exes.getData().toArray(exeDatas);

                        Message message = new Message();
                        message.what = OrderConst.getExeList_order_successful;
                        myHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = OrderConst.getExeList_order_fail;
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = OrderConst.getExeList_order_fail;
                    myHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void initEvent() {
        returnLogo_imgButton.setOnClickListener(this);
    }

    private void initView() {
        returnLogo_imgButton = (ImageButton) findViewById(R.id.returnLogo_imgButton);
        exeContentLV = (ListView) findViewById(R.id.exeContentLV);

        View loadingView  = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(loadingView);
        builder.setCancelable(false);
        dialog = builder.create();

        exeAdapter = new MyExeAdapter(this);
        exeContentLV.setAdapter(exeAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogo_imgButton:
                exeContentActivity.this.finish();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case OrderConst.getExeList_order_successful:
                    dialog.hide();
                    exeAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getExeList_order_fail:
                    dialog.hide();
                    Toasty.error(exeContentActivity.this, "获取应用列表失败，请刷新", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };
}
