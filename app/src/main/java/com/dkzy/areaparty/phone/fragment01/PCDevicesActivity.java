package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_VirtualMachine;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_verify;
import com.dkzy.areaparty.phone.fragment01.ui.DiffuseView;
import com.dkzy.areaparty.phone.fragment01.ui.SharedFileDialog;
import com.dkzy.areaparty.phone.fragment01.utils.IdentityVerify;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.model_comman.PCCommandItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;

import org.apache.commons.io.IOUtils;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import protocol.Msg.LoginMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.Login.getAdresseMAC;
import static com.dkzy.areaparty.phone.Login.getPhoneInfo;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.getContext;
import static com.dkzy.areaparty.phone.register.RegisterUserInfo.isKeyWord;

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
    private Button virtualMachineBtn;

    private Intent intent;
    MyAdapter<IPInforBean> pcAdapter;
    ArrayList<IPInforBean> pcList = new ArrayList<>();
    IPInforBean selectedPCIPInfor;

    public static boolean verifying = false;
    public static String serveIp = "";
    public static int port = 16540;

    public static String pattern =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_devices_acitivity);
        EventBus.getDefault().register(this);
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



        if (TextUtils.isEmpty(DirectToPC.PC_Ip)){
            getVMIP();
        }else {
            new DirectToPC().start();
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

        if (TextUtils.isEmpty(DirectToPC.PC_Ip)){
            getVMIP();
        }else {
            new DirectToPC().start();
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
            virtualMachineBtn.setVisibility(View.GONE);
            wifiStateTV.setText("网络不可用");
            Log.e(tag, "网络不可用");
        } else if(NetUtil.getNetWorkState(this) == NetUtil.NETWORK_WIFI) {
            String wifiNotice = "当前连接了\"" + MyApplication.getWifiName() + "\"";
            onRefresh();
            wifiExistDV.setVisibility(View.VISIBLE);
            devicesRefreshSRL.setVisibility(View.VISIBLE);
            noWifibgIV.setVisibility(View.GONE);
            noWifiNoticeLL.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(Login.userId)){
                virtualMachineBtn.setVisibility(View.VISIBLE);
            }
            wifiStateTV.setText(wifiNotice);
            Log.e(tag, "WIFI已连接");
        } else {
            pcList.clear();
            pcAdapter.notifyDataSetChanged();
            wifiExistDV.setVisibility(View.GONE);
            devicesRefreshSRL.setVisibility(View.GONE);
            noWifibgIV.setVisibility(View.VISIBLE);
            noWifiNoticeLL.setVisibility(View.VISIBLE);
            virtualMachineBtn.setVisibility(View.GONE);
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
        virtualMachineBtn.setOnClickListener(this);
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
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedFileDialog dialog = new SharedFileDialog(PCDevicesActivity.this);
                dialog.setCancelable(true);
                dialog.show();
                dialog.setTitleText("连接电脑");
                dialog.hindFileName();
                dialog.setEdit1HintText("IP");
                dialog.setEdit2HintText("port");
                dialog.setEdit3HintText("验证码");
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IdentityVerify.identifyPC(myHandler, dialog.getPwdEditText(), dialog.getEditText(), Integer.valueOf(dialog.getUrlEditText()));
                    }
                });
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
        virtualMachineBtn = (Button) findViewById(R.id.virtualMachine);
        if (TextUtils.isEmpty(Login.userId)){virtualMachineBtn.setVisibility(View.GONE);}

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
            case R.id.virtualMachine:
                showVirtualMachineDialog();
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
                        if (temp == null){
                            temp = new IPInforBean("192.168.1.112","18888","胡国勇","909090");
                        }
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
                case 1:{
                    showVerifyPasswordDialog();
                }
                break;
            }
        }
    };

    private void showVirtualMachineDialog(){
        final ActionDialog_addFolder dialog = new ActionDialog_addFolder(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitleText("小区服务器IP");
        dialog.setPositiveButtonText("确定");
        dialog.setEditHintText("请输入小区服务器IP");
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ip = dialog.getEditText();
                if (!ip.matches(pattern)){
                    Toast.makeText(PCDevicesActivity.this, "qing'sh请输入合法的IP地址", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    dialog.dismiss();
                    serveIp = ip;
                    sendMessageToService(serveIp,port);
                }

            }
        });
    }
    private void sendMessageToService(final String ip, final int port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Send2PCThread();
                String mac = getAdresseMAC(PCDevicesActivity.this);
                String cmd = "{\"command\":\"\",\"name\":\"CONNECT\",\"param\":\""+mac+"\"}";
                Log.w("sendMessageToService1",cmd);

                String dataReceived = "";
                Socket client = new Socket();

                try {
                    client.connect(new InetSocketAddress(ip, port), 5000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    writer.write(cmd);
                    writer.newLine();
                    writer.flush();
                    dataReceived = reader.readLine();
                    Log.w("sendMessageToService2",dataReceived);
                    ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(dataReceived, ReceivedActionMessageFormat.class);
                    if (receivedMsg.getStatus() == 200){
                        Log.w("sendMessageToService2","连接成功");

                        if (receivedMsg.getMessage().equals("NONEVM")){
                            myHandler.sendEmptyMessage(1);
                        }else if(receivedMsg.getMessage().equals("hasVM")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PCDevicesActivity.this, "你已经拥有个人虚拟机，不能重复创建", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        SharedPreferences.Editor editor = getContext().getSharedPreferences("XQServeInfo", Context.MODE_PRIVATE).edit();
                        editor.putString("serveIP",serveIp);
                        editor.apply();

                        sendMacToService();
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PCDevicesActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("sendMessageToService3","超时");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PCDevicesActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    if (!client.isClosed()) {
                        IOUtils.closeQuietly(client);
                    }
                }

            }
        }).start();
    }

    private void showVerifyPasswordDialog(){
        final ActionDialog_addFolder dialog = new ActionDialog_addFolder(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitleText("安全验证："+ Login.userId);
        dialog.setPositiveButtonText("确定");
        dialog.setEditHintText("请输入登录密码");
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog.dismiss();
                final String pwd = dialog.getEditText();
                if(pwd.isEmpty()||(!isKeyWord(pwd))){
                    Toast.makeText(PCDevicesActivity.this, "请重新正确输入6-20位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String userMac =getAdresseMAC(PCDevicesActivity.this);
                        userMac = userMac.toLowerCase();
                        LoginMsg.LoginReq.Builder builder = LoginMsg.LoginReq.newBuilder();
                        builder.setUserId(Login.userId);
                        builder.setUserPassword(pwd);
                        builder.setLoginType(LoginMsg.LoginReq.LoginType.MOBILE);
                        builder.setUserMac(userMac);
                        builder.setMobileInfo(getPhoneInfo());
                        try {
                            byte[] byteArray  = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.LOGIN_REQ_VALUE, builder.build().toByteArray());
                            if (Login.base != null){
                                Login.base.writeToServer(Login.outputStream,byteArray);
                                verifying = true;

                                Thread.sleep(3000);
                                verifying = false;

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Subscriber(tag = "loginVerify")
    private void loginVerify(LoginMsg.LoginRsp response){
        if (response.getResultCode().equals(LoginMsg.LoginRsp.ResultCode.SUCCESS)){
            Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show();
            showVirtualMTypeDialog();
        }else {
            Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showVirtualMTypeDialog(){
        final ActionDialog_VirtualMachine dialog = new ActionDialog_VirtualMachine(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String select = dialog.getSelectType();
                String hostName = dialog.getEditText();
                if(TextUtils.isEmpty(hostName)){
                    Toast.makeText(PCDevicesActivity.this, "请设置主机名", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                sendCreateVirtualMachineRequest(select, hostName);
            }
        });

    }
    private void sendCreateVirtualMachineRequest(final String select, final String hostName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cmd = "{\"command\":\""+select+"\",\"name\":\"TEMPLATE\",\"param\":\""+hostName+"\"}";
                Log.w("sendMessageToService1",cmd);

                String dataReceived = "";
                Socket client = new Socket();

                try {
                    client.connect(new InetSocketAddress(serveIp, port), 5000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    writer.write(cmd);
                    writer.newLine();
                    writer.flush();
                    dataReceived = reader.readLine();
                    Log.w("sendMessageToService2",dataReceived);
                    ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(dataReceived, ReceivedActionMessageFormat.class);
                    if (receivedMsg.getStatus() == OrderConst.failure){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PCDevicesActivity.this, "当前有其他用户正在创建虚拟机，请稍后再试", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("sendMessageToService3","超时");
                }finally {
                    if (!client.isClosed()) {
                        IOUtils.closeQuietly(client);
                    }
                }

            }
        }).start();
    }

    public static void getVMIP(){
        if (TextUtils.isEmpty(serveIp)){
            SharedPreferences sp = MyApplication.getContext().getSharedPreferences("XQServeInfo", Context.MODE_PRIVATE);
            serveIp = sp.getString("serveIP","");
        }
        if (!TextUtils.isEmpty(serveIp) && serveIp.matches(pattern)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String cmd = "{\"command\":\"\",\"name\":\"GETVMIP\",\"param\":\""+Login.getAdresseMAC(MyApplication.getContext())+"\"}";
                    Log.w("sendMessageToService1",cmd);

                    String dataReceived = "";
                    Socket client = new Socket();

                    try {
                        client.connect(new InetSocketAddress(serveIp, port), 5000);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        writer.write(cmd);
                        writer.newLine();
                        writer.flush();
                        dataReceived = reader.readLine();
                        Log.w("sendMessageToService2",dataReceived);
                        ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(dataReceived, ReceivedActionMessageFormat.class);
                        if (!TextUtils.isEmpty(receivedMsg.getMessage()) && receivedMsg.getMessage().matches(pattern)){
                            DirectToPC.PC_Ip = receivedMsg.getMessage();
                            new DirectToPC().start();

                            sendMacToService();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("sendMessageToService3","超时");
                    }finally {
                        if (!client.isClosed()) {
                            IOUtils.closeQuietly(client);
                        }
                    }

                }
            }).start();
        }
    }

    public static void sendMacToService(){
        if (TextUtils.isEmpty(serveIp)){
            SharedPreferences sp = MyApplication.getContext().getSharedPreferences("XQServeInfo", Context.MODE_PRIVATE);
            serveIp = sp.getString("serveIP","");
        }
        if (!TextUtils.isEmpty(serveIp) && serveIp.matches(pattern)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String tvMac = "";
                    if (MyApplication.isSelectedTVOnline()){
                        tvMac = MyApplication.getSelectedTVIP().mac;
                    }
                    String cmd = "{\"command\":\""+tvMac+"\",\"name\":\"TVMAC\",\"param\":\""+Login.getAdresseMAC(MyApplication.getContext())+"\"}";
                    Log.w("sendMessageToService1",cmd);

                    String dataReceived = "";
                    Socket client = new Socket();

                    try {
                        client.connect(new InetSocketAddress(serveIp, port), 5000);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        writer.write(cmd);
                        writer.newLine();
                        writer.flush();
                        dataReceived = reader.readLine();
                        Log.w("sendMessageToService2",dataReceived);
                        ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(dataReceived, ReceivedActionMessageFormat.class);
                        if (!TextUtils.isEmpty(receivedMsg.getMessage()) && receivedMsg.getMessage().matches(pattern)){
                            DirectToPC.PC_Ip = receivedMsg.getMessage();
                            new DirectToPC().start();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("sendMessageToService3","超时");
                    }finally {
                        if (!client.isClosed()) {
                            IOUtils.closeQuietly(client);
                        }
                    }

                }
            }).start();
        }
    }
}
