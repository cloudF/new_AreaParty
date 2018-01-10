package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_verify;
import com.dkzy.areaparty.phone.fragment01.utils.IdentityVerify;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.DiffuseView;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的TV设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class TVDevicesActivity extends Activity implements View.OnClickListener,
        NetBroadcastReceiver.netEventHandler, SwipeRefreshLayout.OnRefreshListener{

    private final String tag = this.getClass().getSimpleName();
    private final int TVS_RESULTCODE  = 0x3;
    private final String ISTVCHANGEDKEY = "isTVChanged";

    private DiffuseView wifiExistDV;  // 处于Wifi下动态图
    private ImageView   noWifibgIV;  // 不处于Wifi下的静态图
    private SwipeRefreshLayout devicesRefreshSRL;
    private ListView devicesLV;
    private LinearLayout noWifiNoticeLL;
    private TextView noWifiNoticeTxtTV;
    private ImageButton returnLogoIB;
    private TextView wifiStateTV;   // wifi名称

    private View loadingView;
    private AlertDialog dialog;

    private Intent intent;
    MyAdapter<IPInforBean> tvAdapter;
    ArrayList<IPInforBean> tvList = new ArrayList<>();
    IPInforBean selectedTVIPInfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_devices_acitivity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 设置状态栏透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }

        initData();
        initView();
        initEvent();
    }


    /**
     * <summary>
     *  下滑刷新
     * </summary>
     */
    @Override
    public void onRefresh() {

        if (NetUtil.getNetWorkState(getApplicationContext()) == NetUtil.NETWORK_WIFI && ((FillingIPInforList.getThreadBroadCast()!= null && !FillingIPInforList.getThreadBroadCast().isAlive())||(FillingIPInforList.getThreadReceiveMessage()!= null && !FillingIPInforList.getThreadReceiveMessage().isAlive()))){
            FillingIPInforList.setCloseSignal(false);
            FillingIPInforList.startBroadCastAndListen(10000, 10000);
        }
        if (MyApplication.getTVIPInforList().size() > 0){
            tvList.clear();
            tvList.addAll(MyApplication.getTVIPInforList());
            Log.e(tag, "终端个数" + tvList.size());
            tvAdapter.notifyDataSetChanged();
        }
        devicesRefreshSRL.setRefreshing(false);
    }

    /**
     * <summary>
     *  网络状态发生改变触发
     * </summary>
     */
    @Override
    public void onNetChange() {
        if(NetUtil.getNetWorkState(this) == NetUtil.NETWORK_NONE) {
            tvList.clear();
            tvAdapter.notifyDataSetChanged();
            wifiExistDV.setVisibility(View.GONE);
            devicesRefreshSRL.setVisibility(View.GONE);
            noWifibgIV.setVisibility(View.VISIBLE);
            noWifiNoticeLL.setVisibility(View.VISIBLE);
            wifiStateTV.setText("网络不可用");
            Log.e(tag, "网络不可用");
        } else if(NetUtil.getNetWorkState(this) == NetUtil.NETWORK_WIFI) {
            String wifiNotice = "当前连接了\"" + MyApplication.getWifiName() + "\"";
            onRefresh();
            devicesRefreshSRL.setVisibility(View.VISIBLE);
            wifiExistDV.setVisibility(View.VISIBLE);
            noWifibgIV.setVisibility(View.GONE);
            noWifiNoticeLL.setVisibility(View.GONE);
            wifiStateTV.setText(wifiNotice);
            Log.e(tag, "WIFI已连接");
        } else {
            tvList.clear();
            tvAdapter.notifyDataSetChanged();
            wifiExistDV.setVisibility(View.GONE);
            devicesRefreshSRL.setVisibility(View.GONE);
            noWifibgIV.setVisibility(View.VISIBLE);
            noWifiNoticeLL.setVisibility(View.VISIBLE);
            wifiStateTV.setText("网络不可用");
            Log.e(tag, "当前是移动网");
        }
    }

    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
        intent = getIntent();
        selectedTVIPInfor = MyApplication.getSelectedTVIP();
        tvList.clear();
        tvList.addAll(MyApplication.getTVIPInforList());

        tvAdapter = new MyAdapter<IPInforBean>(tvList, R.layout.tab01_devices_item) {
            @Override
            public void bindView(ViewHolder holder, IPInforBean obj) {
                holder.setImageResource(R.id.iconIV, R.drawable.tvitemlogo);
                holder.setText(R.id.nameTV, obj.nickName);
                holder.setText(R.id.iPTV, obj.ip);
                if(selectedTVIPInfor != null) {
                    if(selectedTVIPInfor.mac.equals(obj.mac)) {
                        holder.setVisibility(R.id.isSelectedIV, View.VISIBLE);
                    } else holder.setVisibility(R.id.isSelectedIV, View.GONE);
                } else holder.setVisibility(R.id.isSelectedIV, View.GONE);
            }
        };
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    boolean isTVChanged = false;
    IPInforBean temp;
    private void initEvent() {
        NetBroadcastReceiver.mListeners.add(this);
        returnLogoIB.setOnClickListener(this);
        devicesRefreshSRL.setOnRefreshListener(this);
        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                temp = tvList.get(i);
                if(selectedTVIPInfor != null) {
                    if(selectedTVIPInfor.mac.equals(temp.mac)) {
                        isTVChanged = false;
                        if(!MyApplication.selectedTVVerified) {
                            verifyDialog(temp);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ISTVCHANGEDKEY, isTVChanged);
                            intent.putExtras(bundle);
                            setResult(TVS_RESULTCODE, intent);
                            finish();
                        }
                    } else {
                        if(MyApplication.isTVMacContains(temp.mac)) {
                            code = MyApplication.TVMacs.get(temp.mac);
                            IdentityVerify.identifyTV(myHandler, MyApplication.TVMacs.get(temp.mac), temp.ip, temp.port);
                        } else verifyDialog(temp);
                    }
                } else {
                    isTVChanged = true;
                    selectedTVIPInfor = tvList.get(i);
                    verifyDialog(temp);
                }
            }
        });
    }

    /**
     * <summary>
     *  显示密码输入对话框并执行相应监听操作
     * </summary>
     */
    String code = "";
    private void verifyDialog(final IPInforBean infor) {
        final ActionDialog_verify actionDialogAddFolder = new ActionDialog_verify(this);
        actionDialogAddFolder.setCanceledOnTouchOutside(true);
        actionDialogAddFolder.show();
        actionDialogAddFolder.setTitleText("验证");
        actionDialogAddFolder.setEditHintText("请输入验证码");
        actionDialogAddFolder.setPositiveButtonText("确定");
        actionDialogAddFolder.setNegativeButtonText("取消");
        final EditText editText = actionDialogAddFolder.getEditTextView();
        actionDialogAddFolder.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = editText.getText().toString();
                if(code.equals("")){
                    Toasty.error(TVDevicesActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialogAddFolder.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                    dialog.show();
                    IdentityVerify.identifyTV(myHandler, code, infor.ip, infor.port);
                    if(MyApplication.getSelectedPCIP()!=null){
                        TVAppHelper.currentPcInfo2TV();
                    }

                    actionDialogAddFolder.dismiss();
                }
            }
        });
        actionDialogAddFolder.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDialogAddFolder.dismiss();
            }
        });
    }

    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {
        wifiExistDV = (DiffuseView) findViewById(R.id.wifiExistDV);
        noWifibgIV  = (ImageView) findViewById(R.id.noWifibgIV);
        devicesLV = (ListView) findViewById(R.id.devicesLV);
        noWifiNoticeLL = (LinearLayout) findViewById(R.id.noWifiNoticeLL);
        noWifiNoticeTxtTV = (TextView) findViewById(R.id.noWifiNoticeTxtTV);
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        wifiStateTV  = (TextView) findViewById(R.id.wifiStateTV);
        devicesRefreshSRL = (SwipeRefreshLayout)findViewById(R.id.devicesRefreshSRL);

        loadingView = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView).setCancelable(true);
        dialog = builder.create();

        noWifiNoticeTxtTV.setText("需和TV设备处于同一WiFi才能检测TV");
        devicesRefreshSRL.setColorSchemeResources(R.color.colorPrimary);

        devicesLV.setAdapter(tvAdapter);
        wifiExistDV.start();
        onNetChange();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                finish();
                break;
        }
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200: {
                    String t = (String)msg.obj;
                    if(t.equals("true")) {
                        MyApplication.selectedTVVerified = true;
                        MyApplication.setSelectedTVIP(temp);
                        MyApplication.addTVMac(temp.mac, code);
                        tvAdapter.notifyDataSetChanged();
                        Toasty.success(TVDevicesActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        isTVChanged = true;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ISTVCHANGEDKEY, isTVChanged);
                        intent.putExtras(bundle);
                        setResult(TVS_RESULTCODE, intent);
                        finish();
                    } else {
                        MyApplication.selectedTVVerified = false;
                        MyApplication.removeTVMac(temp.mac);
                        Toasty.error(TVDevicesActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
                break;
                case 404: {
                    dialog.dismiss();
                    MyApplication.selectedTVVerified = false;
                    MyApplication.removeTVMac(temp.mac);
                    Toasty.info(TVDevicesActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    };
}
