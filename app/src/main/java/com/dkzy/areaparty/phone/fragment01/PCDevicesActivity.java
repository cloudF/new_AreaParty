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
import android.text.TextUtils;
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

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_verify;
import com.dkzy.areaparty.phone.fragment01.ui.DiffuseView;
import com.dkzy.areaparty.phone.fragment01.utils.IdentityVerify;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class PCDevicesActivity extends Activity implements View.OnClickListener,
        NetBroadcastReceiver.netEventHandler, SwipeRefreshLayout.OnRefreshListener{

    private final String tag = this.getClass().getSimpleName();
    private final int PCS_RESULTCODE = 0x1;
    private final String ISPCCHANGEDKEY = "isPCChanged";

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
    MyAdapter<IPInforBean> pcAdapter;
    ArrayList<IPInforBean> pcList = new ArrayList<>();
    IPInforBean selectedPCIPInfor;

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
        if (NetUtil.getNetWorkState(getApplicationContext()) == NetUtil.NETWORK_WIFI&& ((FillingIPInforList.getThreadBroadCast()!= null && !FillingIPInforList.getThreadBroadCast().isAlive())||(FillingIPInforList.getThreadReceiveMessage()!= null && !FillingIPInforList.getThreadReceiveMessage().isAlive()))){
            FillingIPInforList.setCloseSignal(false);
            FillingIPInforList.startBroadCastAndListen(10000, 10000);
        }

        if (MyApplication.getPC_YInforList().size() > 0){
            pcList.clear();
            pcList.addAll(MyApplication.getPC_YInforList());
            Log.e(tag, "终端个数" + pcList.size());
            pcAdapter.notifyDataSetChanged();

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
            pcList.clear();
            pcAdapter.notifyDataSetChanged();
            wifiExistDV.setVisibility(View.GONE);
            devicesRefreshSRL.setVisibility(View.GONE);
            noWifibgIV.setVisibility(View.VISIBLE);
            noWifiNoticeLL.setVisibility(View.VISIBLE);
            wifiStateTV.setText("网络不可用");
            Log.e(tag, "网络不可用");
        } else if(NetUtil.getNetWorkState(this) == NetUtil.NETWORK_WIFI) {
            String wifiNotice = "当前连接了\"" + MyApplication.getWifiName() + "\"";
            onRefresh();
            wifiExistDV.setVisibility(View.VISIBLE);
            devicesRefreshSRL.setVisibility(View.VISIBLE);
            noWifibgIV.setVisibility(View.GONE);
            noWifiNoticeLL.setVisibility(View.GONE);
            wifiStateTV.setText(wifiNotice);
            Log.e(tag, "WIFI已连接");
        } else {
            pcList.clear();
            pcAdapter.notifyDataSetChanged();
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
        selectedPCIPInfor = MyApplication.getSelectedPCIP();
        pcList.clear();
        pcList.addAll(MyApplication.getPC_YInforList());

        pcAdapter = new MyAdapter<IPInforBean>(pcList, R.layout.tab01_devices_item) {
            @Override
            public void bindView(ViewHolder holder, IPInforBean obj) {
                holder.setImageResource(R.id.iconIV, R.drawable.pcitemlogo);
                holder.setText(R.id.nameTV, obj.nickName);
                holder.setText(R.id.iPTV, obj.ip);
                if(selectedPCIPInfor != null && !TextUtils.isEmpty(selectedPCIPInfor.mac)) {
                    if(selectedPCIPInfor.mac.equals(obj.mac)) {
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
    boolean isPCChanged = false;
    IPInforBean temp;
    private void initEvent() {
        NetBroadcastReceiver.mListeners.add(this);
        returnLogoIB.setOnClickListener(this);
        devicesRefreshSRL.setOnRefreshListener(this);
        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                temp = pcList.get(i);
                if(selectedPCIPInfor != null && !TextUtils.isEmpty(selectedPCIPInfor.mac)) {
                    if(selectedPCIPInfor.mac.equals(temp.mac)) {
                        isPCChanged = false;
                        if(!MyApplication.selectedPCVerified) {
                            verifyDialog(temp);
                        } else  {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ISPCCHANGEDKEY, isPCChanged);
                            intent.putExtras(bundle);
                            setResult(PCS_RESULTCODE, intent);
                            finish();
                        }
                    } else {
                        if(MyApplication.isPCMacContains(pcList.get(i).mac)) {
                            code = MyApplication.PCMacs.get(temp.mac);
                            IdentityVerify.identifyPC(myHandler, MyApplication.PCMacs.get(temp.mac), temp.ip, temp.port);
                        } else verifyDialog(temp);
                    }
                } else {
                    isPCChanged = true;
                    selectedPCIPInfor = pcList.get(i);
                    verifyDialog(temp);
                }
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

        noWifiNoticeTxtTV.setText("需和PC设备处于同一WiFi才能检测PC");
        devicesRefreshSRL.setColorSchemeResources(R.color.colorPrimary);

        devicesLV.setAdapter(pcAdapter);
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
                    Toasty.error(PCDevicesActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialogAddFolder.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");


                    IdentityVerify.identifyPC(myHandler, code, infor.ip, infor.port);

                    actionDialogAddFolder.dismiss();

                    try {
                        dialog.show();
                    }catch (Exception e){e.printStackTrace();}
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

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200: {
                    String t = (String)msg.obj;
                    if(t.equals("true")) {
                        MyApplication.selectedPCVerified = true;
                        MyApplication.setSelectedPCIP(temp);
                        MyApplication.addPCMac(temp.mac, code);
                        pcAdapter.notifyDataSetChanged();
                        Toasty.success(PCDevicesActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        if(MyApplication.getSelectedTVIP()!=null && MyApplication.getSelectedPCIP()!=null) {
                            TVAppHelper.currentPcInfo2TV();
                        }

                        isPCChanged = true;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ISPCCHANGEDKEY, isPCChanged);
                        intent.putExtras(bundle);
                        setResult(PCS_RESULTCODE, intent);
                        finish();
                    } else {
                        MyApplication.selectedPCVerified = false;
                        MyApplication.removePCMac(temp.mac);
                        Toasty.error(PCDevicesActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                    dialog.hide();
                }
                break;
                case 404: {
                    dialog.hide();
                    MyApplication.selectedPCVerified = false;
                    MyApplication.removePCMac(temp.mac);
                    Toasty.info(PCDevicesActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    };
}
