package com.dkzy.areaparty.phone.fragment03;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.uninstalledTVAppEvent;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import org.simple.eventbus.EventBus;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class uninstallTVAppActivity extends Activity implements View.OnClickListener{

    private ImageButton returnLogoIB;
    private ListView appListLV;

    MyAdapter<AppItem> appAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_uninstalltvapp_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initData();
        initView();
        initEvent();
    }

    private void initData() {
        appAdapter = new MyAdapter<AppItem>(TVAppHelper.installedAppList, R.layout.tab03_uninstallapp_item) {
            @Override
            public void bindView(ViewHolder holder, AppItem obj) {
                final int position = holder.getItemPosition();
                holder.setText(R.id.nameTV, obj.getAppName());
                holder.setImage(R.id.thumbnailIV, obj.getIconURL(), R.drawable.logo_loading, uninstallTVAppActivity.this);
                holder.setOnClickListener(R.id.unInstallIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(MyApplication.isSelectedTVOnline()) {
                            TVAppHelper.uninstallApp(TVAppHelper.installedAppList.get(position).getPackageName());
                            TVAppHelper.installedAppList.remove(position);
                            appAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new uninstalledTVAppEvent(), "uninstalledTVAPP");
                            Toasty.success(uninstallTVAppActivity.this, "应用即将卸载, 请操作电视", Toast.LENGTH_SHORT, true).show();
                        } else Toasty.warning(uninstallTVAppActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    }
                });
            }
        };
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);

    }

    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        appListLV = (ListView) findViewById(R.id.appListLV);
        appListLV.setAdapter(appAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
        }
    }
}
