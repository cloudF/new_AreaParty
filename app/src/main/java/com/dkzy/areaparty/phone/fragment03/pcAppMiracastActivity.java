package com.dkzy.areaparty.phone.fragment03;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.bluetoothxie.BluetoothKeyboard;
import com.dkzy.areaparty.phone.bluetoothxie.KeyboardActivity;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.ui.ActionNoticeDialog;
import com.dkzy.areaparty.phone.fragment03.utils.PCAppHelper;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class pcAppMiracastActivity extends AppCompatActivity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageButton returnLogoIB, moreIB;
    private RecyclerView appRV;

    private View loadingView;
    private AlertDialog dialog;
    AppItem currentItem;

    Adapter appAdapter;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(PCAppHelper.getCurrentMode() == PCAppHelper.MIRACAST)
                    showActionNoticeDialog();
                else this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_pcapphelper_miracast_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadingView = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView).setCancelable(false);
        dialog = builder.create();
        initData();
        initView();
        initEvent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                if(PCAppHelper.getCurrentMode() == PCAppHelper.MIRACAST)
                    showActionNoticeDialog();
                else this.finish();
                break;
            case R.id.moreIB:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_miracast, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.PCVitrualKMLL:
                                if(MyApplication.isSelectedPCOnline())
                                    KeyboardActivity.actionStart(pcAppMiracastActivity.this);
                                else Toasty.warning(pcAppMiracastActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                                break;
                            case R.id.PCMobileBluetoothKMLL:
                                if(MyApplication.isSelectedPCOnline())
                                    BluetoothKeyboard.actionStart(pcAppMiracastActivity.this);
                                else Toasty.warning(pcAppMiracastActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                                break;
                        }
                        return false;
                    }
                });
                break;
        }
    }



    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
        if(PCAppHelper.appList.size() <= 0) {
            ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
            dialog.show();
            PCAppHelper.loadList(myHandler);
        }
        appAdapter = new Adapter(PCAppHelper.appList, this);
        appAdapter.isFirstOnly(false);
        appAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                currentItem = PCAppHelper.getAppList().get(i);
                if(MyApplication.isSelectedPCOnline()) {
                    if(PCAppHelper.getCurrentMode() == PCAppHelper.MIRACAST) {
                        dialog.show();
                        ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                        PCAppHelper.openApp_Miracast(PCAppHelper.appList.get(i).getPackageName(),
                                PCAppHelper.appList.get(i).getAppName(),
                                MyApplication.getSelectedTVIP().name, myHandler);
                    } else {
                        if(MyApplication.isSelectedTVOnline()) {
                            //判断是否锁屏
                            dialog.show();
                            ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                            PCAppHelper.isPCScreenLocked(myHandler);
                        } else Toasty.info(pcAppMiracastActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    }
                } else Toasty.info(pcAppMiracastActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
            }
        });
    }


    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        moreIB = (ImageButton) findViewById(R.id.moreIB);
        appRV = (RecyclerView) findViewById(R.id.appRV);

        appRV.setItemAnimator(new DefaultItemAnimator());
        appRV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        appRV.setAdapter(appAdapter);
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        moreIB.setOnClickListener(this);
    }

    private void showActionNoticeDialog() {
        final ActionNoticeDialog actionDialog = new ActionNoticeDialog(this);
        actionDialog.setCanceledOnTouchOutside(false);
        actionDialog.show();
        actionDialog.setTitleText("请手动关闭TV端miracast接收程序以恢复联网");
        actionDialog.setPositiveButtonText("已关闭");
        actionDialog.setNegativeButtonText("暂不关闭");
        actionDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDialog.dismiss();
            }
        });
        actionDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PCAppHelper.setCurrentMode(PCAppHelper.NONEMODE);
                actionDialog.dismiss();
                pcAppMiracastActivity.this.finish();
            }
        });
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCApp_OK:
                    appAdapter.notifyDataSetChanged();
                    dialog.hide();
                    break;
                case OrderConst.getPCApp_Fail:
                    dialog.hide();
                    Toasty.info(pcAppMiracastActivity.this, "获取PC应用失败", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.openPCApp_OK:
                    PCAppHelper.setCurrentMode(PCAppHelper.MIRACAST);
                    dialog.hide();
                    Toasty.success(pcAppMiracastActivity.this, "即将在当前电视上打开指定应用, 请操作电视", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.openPCApp_Fail:
                    dialog.hide();
                    Toasty.info(pcAppMiracastActivity.this, "打开应用失败", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.PCScreenLocked:
                    dialog.hide();
                    Toasty.info(pcAppMiracastActivity.this, "当前电脑处于锁屏状态, 请手动解锁或重启电脑解锁", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.PCScreenNotLocked:
                    TVAppHelper.openTVMiracast();
                    PCAppHelper.openApp_Miracast(currentItem.getPackageName(),
                            currentItem.getAppName(), MyApplication.getSelectedTVIP().name, myHandler);
                    break;
            }
        }
    };

    private class Adapter extends BaseQuickAdapter<AppItem> {
        private Context context;

        public Adapter(List<AppItem> data, Context context) {
            super(R.layout.tab03_pcapphelper_app_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, AppItem app) {
            ImageView thumbnailIV = baseViewHolder.getView(R.id.thumbnailIV);
            Glide.with(context).asBitmap()
                    .load(app.getIconURL()).apply(new RequestOptions().placeholder(R.drawable.logo_loading).centerCrop())
//                    .asBitmap()
//                    .placeholder(R.drawable.logo_loading)
//                    .centerCrop()
                    .into(thumbnailIV);
            baseViewHolder.setText(R.id.nameTV, app.getAppName());
        }
    }
}
