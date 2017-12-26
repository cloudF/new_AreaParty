package com.dkzy.areaparty.phone.bluetoothxie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.example.action.ControllerDroidAction;
import com.example.connection.DeviceConnection;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by XIE on 2017/4/11.
 */

public class TVBluetoothSet extends Activity {
    String ip_ph;
//    String ip_pc = "192.168.1.134";
    String ip_tv ;
    Button btn_getDevices;
    Button btn_devicesFresh;
    Button btn_getConnectedDevices;
    Switch swt_bluetooth;
    TextView bar01moreaction1;
    TextView bar01moreaction2;
    LinearLayout refresh;
    LinearLayout more;
    LinearLayout computerBluetooth;
    LinearLayout pc_layout;
    LinearLayout pc_conlayout;
    LinearLayout tv_layout;
    LinearLayout tv_conlayout;
    LinearLayout bar01moreroot;
    LinearLayout ph_layout;
    LinearLayout ph_conlayout;
    ImageButton returnlogo;
    ListViewForScrollView listView;
    DevicesAdapter adapter;
    ConDevicesAdapter conAdapter;
    BluetoothAdapter bAdapter;
    BluetoothSocket socket;
    Socket tvSocket;
    Socket phSocket;
    ServerSocket serverSocket;
    ListViewForScrollView conListView;
    ListViewForScrollView getListView;
    ListViewForScrollView tvGetListView;
    ListViewForScrollView tvConListView;
    CheckBox checkTV;
    CheckBox checkPH;
    boolean condevicesChecked = false;
    Receiver receiver;
    long timer =0;
    private boolean filedone;
    public static int deviceType;
    public static final int UPDATE_TEXT = 1;
    public static final int UPDATE_CONTEXT = 2;
    public static final int UPDATE_FINDDEVICE = 3;
    public static final int UPDATE_TV_FINDDEVICE = 4;
    public static final int UPDATE_TV_CONDEVICE=5;
    public static final int CONNECT_DEVICE =6;         //通知tv服务端连接断开设备
    public static final int DISCON_DEVICE =7;
    public static final int FIND_DEVICES = 8;   //tv服务端通知设备列表更新
    public static final int CONNECT_DEVICES = 9;
    public static final int BTN_REFRESH = 10;
    List<String> pcFindDevices = new ArrayList<String>();   //pc可连接已连接list
    List<String> pcConDevices= new ArrayList<String>();
    List<String> findDevices = new ArrayList<>();  //手机可连接已连接list
    ArrayList<String> conList = new ArrayList<String>();
    List<String> tvFindDevices = new ArrayList<>();//tv 可连接已连接list
    List<String> tvConDevices = new ArrayList<>();
    List<DeviceList> templist = new ArrayList<>();
    List<DeviceList> templist1 = new ArrayList<>();
    List<BluetoothDevice> foundDevices = new ArrayList<>();
    Set<BluetoothDevice> devices;
    BluetoothDevice currentDevice;
    String conDeviceName ;
    Thread closeThread;
    protected ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private BluetoothA2dp a2dp;
    private MouseAndBoardAsyncTask myTask;
    private MouseAndBoardCallBack callBack;
    private Activity act ;
    private Context ctx;
    static DataOutputStream dos;
    static ByteArrayOutputStream output;
    static OutputStream outputStream;
    DataInputStream dataInputStream;//PC
    DataInputStream inputStream;//TV
    List<BluetoothDevice> conDevice;
    BluetoothDevice device;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
               /* case UPDATE_TEXT:
                    //pc_findlist
                    *//*txt_getDevices.setText(((List<String>)msg.obj).toString());
                    if(txt_getDevices.getText().toString() == ""){
                        txt_getDevices.setText("无可连接设备");
                    }*//*
                    pcFindDevices = (List<String>) msg.obj;
                    removeDuplicate(pcFindDevices);
                    if(!pcFindDevices.isEmpty()){
                        if (checkTV.isChecked()) {
                            pc_layout.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                        }
                        adapter = new DevicesAdapter(ctx, R.layout.devices_item, pcFindDevices);
                        listView.setAdapter(adapter);
                        TextView txt = (TextView)findViewById(R.id.tvset_get_bluetoothdevices);
                        txt.setText("PC:可连接设备");
                    }else {
                        TextView txt = (TextView)findViewById(R.id.tvset_get_bluetoothdevices);
                        txt.setText("PC:当前无可连接设备");
                    }
                    break;
                case UPDATE_CONTEXT:
                    //pc_connectlist
                    *//*txt_connectedDevices.setText(((List<String>)msg.obj).toString());
                    if(txt_connectedDevices.getText().toString() == ""){
                        txt_connectedDevices.setText("无已连接设备");
                    }*//*
                    pcConDevices = (List<String>) msg.obj;
                    removeDuplicate(pcConDevices);
                    if (!pcConDevices.isEmpty()){
                        if (checkTV.isChecked()){
                            pc_conlayout.setVisibility(View.VISIBLE);
                            conListView.setVisibility(View.VISIBLE);
                        }
                        conAdapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, pcConDevices);
                        conListView.setAdapter(conAdapter);
                        TextView txt = (TextView)findViewById(R.id.tvset_connected_bluetoothdevices);
                        txt.setText("PC:已连接设备");
                    }else{
                        TextView txt = (TextView)findViewById(R.id.tvset_connected_bluetoothdevices);
                        txt.setText("PC:当前无已连接设备");
                    }
                    break;*/
                case UPDATE_FINDDEVICE:
                    //ph_findlist
                    List<String> list = (List<String>)msg.obj;
                    removeDuplicate(list);
                    if (checkPH.isChecked()){
                        ph_layout.setVisibility(View.VISIBLE);
                        getListView.setVisibility(View.VISIBLE);
                    }
//                    List<String> templist1 = ChangeDevice.getInstance().removeDeviceString(deviceType,list,ctx);
                    if (list.isEmpty()) {
                        TextView txt = (TextView)findViewById(R.id.tvset_ph_getDevices);
                        txt.setText("手机:当前无可连接设备");
                    }else {
                        TextView txt = (TextView)findViewById(R.id.tvset_ph_getDevices);
                        txt.setText("手机:可连接设备");
                    }
                    templist = ChangeDevice.getInstance().changeList(list,ctx);
                    DevicesAdapter getAdapter = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                    getListView.setAdapter(getAdapter);
                    break;
                case 0x11:
                    Bundle bundle = msg.getData();
                    Toast.makeText(ctx,bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_TV_FINDDEVICE:
                    tvFindDevices = (List<String>) msg.obj;
                    removeDuplicate(tvFindDevices);
                    templist = ChangeDevice.getInstance().changeList(tvFindDevices,ctx);
                    if (!templist.isEmpty()){
                        if (checkTV.isChecked()) {
                            tv_layout.setVisibility(View.VISIBLE);
                            tvGetListView.setVisibility(View.VISIBLE);
                        }
                        DevicesAdapter getAdapter1 = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                        tvGetListView.setAdapter(getAdapter1);
                        TextView txt = (TextView)findViewById(R.id.tvset_tv_getDevices);
                        txt.setText("TV:可连接设备");
                    }else {
                        DevicesAdapter getAdapter1 = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                        tvGetListView.setAdapter(getAdapter1);
                        TextView txt = (TextView)findViewById(R.id.tvset_tv_getDevices);
                        txt.setText("TV:当前无可连接设备");
                    }
                    break;
                case UPDATE_TV_CONDEVICE:
                    tvConDevices = (List<String>)msg.obj;
                    removeDuplicate(tvConDevices);
                    templist = ChangeDevice.getInstance().changeList(tvConDevices,ctx);
                    if (!tvConDevices.isEmpty()){
                        if (checkTV.isChecked()) {
                            tv_conlayout.setVisibility(View.VISIBLE);
                            tvConListView.setVisibility(View.VISIBLE);
                        }
                        ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, templist);
                        tvConListView.setAdapter(adapter);
                        TextView txt = (TextView)findViewById(R.id.tvset_tv_conDevices);
                        txt.setText("TV:已连接设备");
                    }else {
                        ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, templist);
                        tvConListView.setAdapter(adapter);
                        TextView txt = (TextView)findViewById(R.id.tvset_tv_conDevices);
                        txt.setText("TV:当前无已连接设备");
                    }
                    break;
                case BTN_REFRESH:
                    refresh.callOnClick();
                    break;
                default:
                    break;
            }
        }
    };
    public static void actionStart(Context context){
//        deviceType = type;
        Intent intent = new Intent(context, TVBluetoothSet.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvbluetoothset);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //获取tv可连接设备
        /*if (phSocket !=null);
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        phSocket = new Socket(ip_tv,30003);
                        InputStream in = phSocket.getInputStream();
                        DataInputStream dis = new DataInputStream(in);
                        Gson gson1 = new Gson();
                        while (true) {
                            if (dis != null) {
                                Device device = gson1.fromJson(dis.readUTF(), Device.class);
                                List<String> getDevice = new ArrayList<String>();
                                if (device.getGetDevice() != null)
                                    getDevice = device.getGetDevice();
                                Message msg = new Message();
                                msg.what = UPDATE_TV_FINDDEVICE;
                                msg.obj = getDevice;
                                handler.sendMessage(msg);
                            }else break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if (phSocket!=null){
                            try {
                                phSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }*/
        if (DeviceConnection.getInstance().isAuthentificated())                            //获得连接状态，未连接跳转到重连函数doInBackground
        {
           /* Toast.makeText(TVBluetoothSet.this, "服务器已连接", Toast.LENGTH_SHORT)
                    .show();*/
            dataInputStream = DeviceConnection.getInstance().reInput();

        } else
        {
            /*Toast.makeText(TVBluetoothSet.this, "正在连接...", Toast.LENGTH_SHORT)
                    .show();*/
            doInBackground();
            dataInputStream = DeviceConnection.getInstance().reInput();
        }
        act = this;
        ctx = this;
        ip_ph = getlocalip();
        ip_tv = Constants.getIP_TV();
        initListeners();
    }

    protected void onDestroy(){
        ctx.unregisterReceiver(receiver);
        super.onDestroy();
    }
    public void initListeners(){
        computerBluetooth = (LinearLayout)findViewById(R.id.tvset_computerbluetooth);
        returnlogo = (ImageButton)findViewById(R.id.tvset_returnLogo_imgButton);
        ph_layout = (LinearLayout)findViewById(R.id.tvset_ph_layout);
        ph_conlayout = (LinearLayout)findViewById(R.id.tvset_ph_conlayout);
        pc_layout =(LinearLayout)findViewById(R.id.tvset_pc_layout);
        getListView = (ListViewForScrollView)findViewById(R.id.tvset_list_ph_getDevices);
        tvGetListView = (ListViewForScrollView)findViewById(R.id.tvset_list_tv_getDevices) ;
        tvConListView = (ListViewForScrollView)findViewById(R.id.tvset_list_tv_conDevices);
        listView= (ListViewForScrollView) findViewById(R.id.tvset_list_getDevices);
        conListView = (ListViewForScrollView) findViewById(R.id.tvset_list_conDevices);
        pc_conlayout =(LinearLayout)findViewById(R.id.tvset_pc_conlayout);
        tv_layout =(LinearLayout)findViewById(R.id.tvset_tv_layout);
        tv_conlayout =(LinearLayout)findViewById(R.id.tvset_tv_conlayout);
        refresh = (LinearLayout)findViewById(R.id.tvset_diskListRefreshLL);
//        more = (LinearLayout)findViewById(R.id.tvset_diskListMoreLL);
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        final boolean swt_on =  bAdapter.isEnabled();
        swt_bluetooth = (Switch)findViewById(R.id.tvset_swc_bluetooth);
        checkTV = (CheckBox)findViewById(R.id.tvset_checkpc);
        checkPH = (CheckBox)findViewById(R.id.tvset_checkph);
        swt_bluetooth.setChecked(swt_on);
        swt_bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bAdapter.enable();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.what = BTN_REFRESH;
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    bAdapter.disable();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.what = BTN_REFRESH;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
        if (!checkPH.isChecked()) {
            ph_conlayout.setVisibility(View.GONE);
            ph_layout.setVisibility(View.GONE);
            getListView.setVisibility(View.GONE);
            ListViewForScrollView listView = (ListViewForScrollView) findViewById(R.id.tvset_list_ph_conDevices);
            listView.setVisibility(View.GONE);
        }
        if (!checkTV.isChecked()){
            tv_layout.setVisibility(View.GONE);
            tv_conlayout.setVisibility(View.GONE);
            tvGetListView.setVisibility(View.GONE);
            tvConListView.setVisibility(View.GONE);
        }
        checkPH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    ph_conlayout.setVisibility(View.GONE);
                    ph_layout.setVisibility(View.GONE);
                    getListView.setVisibility(View.GONE);
                    ListViewForScrollView listView = (ListViewForScrollView) findViewById(R.id.tvset_list_ph_conDevices);
                    listView.setVisibility(View.GONE);
                } else {
                    ph_conlayout.setVisibility(View.VISIBLE);
                    ListViewForScrollView listView = (ListViewForScrollView) findViewById(R.id.tvset_list_ph_conDevices);
                    listView.setVisibility(View.VISIBLE);
                    ph_layout.setVisibility(View.VISIBLE);
                    getListView.setVisibility(View.VISIBLE);
                    Message msg = new Message();
                    msg.what = BTN_REFRESH;
                    handler.sendMessage(msg);
                }
            }
        });
        checkTV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked){
                    tv_layout.setVisibility(View.GONE);
                    tv_conlayout.setVisibility(View.GONE);
                    tvGetListView.setVisibility(View.GONE);
                    tvConListView.setVisibility(View.GONE);
                }else {
                    tv_layout.setVisibility(View.VISIBLE);
                    tv_conlayout.setVisibility(View.VISIBLE);
                    tvGetListView.setVisibility(View.VISIBLE);
                    tvConListView.setVisibility(View.VISIBLE);
                    Message msg = new Message();
                    msg.what = BTN_REFRESH;
                    handler.sendMessage(msg);
                }
            }
        });

        receiver = new Receiver();
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果
        ctx.registerReceiver(receiver, intentFilter);
        if (bAdapter.isDiscovering()) {
            bAdapter.cancelDiscovery();
        }
        bAdapter.startDiscovery();
        closeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(60000);
                    bAdapter.cancelDiscovery();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        synchronized (closeThread) {
            closeThread.start();
        }

        tvGetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String msg= "是否与设备" +tvFindDevices.get(position) + "建立连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //配对
                        try {
                            if (ip_tv!=null)
                            new ConThread(ip_tv,tvFindDevices.get(position)).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgressDailog5S("正在连接...");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                msg.what = BTN_REFRESH;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                });
            }
        });

        tvConListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String msg= "是否与设备" +tvConDevices.get(position) + "断开连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //取消配对
                        try {
                            if (ip_tv!=null)
                            new DisThread(ip_tv,tvConDevices.get(position)).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgressDailog5S("断开连接...");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                msg.what = BTN_REFRESH;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                });
            }
        });

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String s = pcFindDevices.get(position);
                String msg = "";
                msg= "是否与设备" + s + "连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        *//**连接*//*
                        ConnectBluetoothDevicesAction connectBluetoothDevicesAction = new ConnectBluetoothDevicesAction(s);
                        sendAction2Remote(connectBluetoothDevicesAction);
                    }
                });

            }
        });

        conListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String s = pcConDevices.get(position);
                String msg = "";
                msg= "是否与设备" + s + "断开连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        *//**断开连接*//*
                        DisconnectBluetoothDevicesAction disconnectBluetoothDevicesAction = new DisconnectBluetoothDevicesAction(s);
                        sendAction2Remote(disconnectBluetoothDevicesAction);
                    }
                });

            }
        });*/
//        btn_devicesFresh =(Button) findViewById(R.id.tvset_btn_devicesfresh);
        //btn_conDevices =(Button) findViewById(R.id.tvset_connect_bluetoothdevices);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*listView.removeAllViews();
                conListView.removeAllViews();*/
                /*pc_conlayout.setVisibility(View.GONE);
                pc_layout.setVisibility(View.GONE);
                tv_layout.setVisibility(View.GONE);
                tv_conlayout.setVisibility(View.GONE);*/
//                new MyThread(ip_tv).start();   //与tv端交互线程
                if (DeviceConnection.getInstance().isAuthentificated())                            //获得连接状态，未连接跳转到重连函数doInBackground
                {

                } else
                {
                  /*  Toast.makeText(ctx, "正在连接...", Toast.LENGTH_SHORT)
                            .show();*/
                    doInBackground();
                }
                swt_bluetooth.setChecked(bAdapter.isEnabled());
//                Date now = new Date();
//                if((now.getTime()-timer)>3000){

                    /*pc_conlayout.setVisibility(View.GONE);
                    pc_layout.setVisibility(View.GONE);*/
                    /*tv_layout.setVisibility(View.GONE);
                    tv_conlayout.setVisibility(View.GONE);*/
//                    timer = now.getTime();
                if (ip_tv!=null)
                    new GetConThread(ip_tv).start();//与tv连接获取已连接设备列表
                //获取TV可连接设备列表
                    if (phSocket==null){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (ip_tv!=null){
                                    try {
                                        phSocket = new Socket(ip_tv,30003);
                                        InputStream in = phSocket.getInputStream();
                                        DataInputStream dis = new DataInputStream(in);
                                        Gson gson1 = new Gson();
                                        while (true) {
                                            if (dis != null) {
                                                Device device1 = gson1.fromJson(dis.readUTF(), Device.class);
                                                List<String> getDevice = new ArrayList<String>();
                                                if (device1.getGetDevice() != null)
                                                    getDevice = device1.getGetDevice();
                                                Message msg = new Message();
                                                msg.what = UPDATE_TV_FINDDEVICE;
                                                msg.obj = getDevice;
                                                handler.sendMessage(msg);
                                            }else break;
                                        }
                                    } catch (IOException e) {
                                        List<String> getDevice = new ArrayList<String>();
                                        Message msg = new Message();
                                        msg.what = UPDATE_TV_FINDDEVICE;
                                        msg.obj = getDevice;
                                        handler.sendMessage(msg);
                                        e.printStackTrace();
                                    }finally {
                                        if(phSocket!=null){
                                            try {
                                                phSocket.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }else {
                                    List<String> getDevice = new ArrayList<String>();
                                    Message msg = new Message();
                                    msg.what = UPDATE_TV_FINDDEVICE;
                                    msg.obj = getDevice;
                                    handler.sendMessage(msg);
                                }
                            }
                        }).start();
                    }
                    //获取电脑蓝牙设备列表
                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Socket socket1 = null;
                            try {
                                socket1 = new Socket(ip_pc,56565);
                                InputStream in = socket1.getInputStream();
                                DataInputStream is = new DataInputStream(in);
                                Gson gson = new Gson();
                                Device d=gson.fromJson(is.readUTF(), Device.class);
                                List<String> getDevice = new ArrayList<String>();
                                if(d.getGetDevice()!=null)
                                    getDevice = d.getGetDevice();
                                List<String> getConDevice = new ArrayList<String>();
                                if(d.getGetConDevice()!=null)
                                    getConDevice = d.getGetConDevice();
                                Message msg = new Message();
                                msg.what = UPDATE_TEXT;
                                msg.obj = getDevice;
                                handler.sendMessage(msg);
                                Message msg1 = new Message();
                                msg1.what=UPDATE_CONTEXT;
                                msg1.obj=getConDevice;
                                handler.sendMessage(msg1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                try {
                                    if (socket1!=null)
                                        socket1.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();*/
//                bAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!bAdapter.isEnabled()){
                        findDevices = new ArrayList<String>();
                        Message message = new Message();
                        message.what = UPDATE_FINDDEVICE;
                        message.obj = findDevices;
                        handler.sendMessage(message);
                        conList = new ArrayList<String>();
                        templist1 = ChangeDevice.getInstance().changeList(conList,ctx);
                        ListViewForScrollView listView = (ListViewForScrollView)findViewById(R.id.tvset_list_ph_conDevices);
                        ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, templist1);
                        listView.setAdapter(adapter);
                        Toast.makeText(ctx,"手机未开启蓝牙", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    ctx.startActivity(intent);*/
                    }else {
                        //获取手机已配对蓝牙设备
                        conList = new ArrayList<String>();
                        devices = bAdapter.getBondedDevices();
                        for (BluetoothDevice device : devices) {
                            conList.add(device.getName());
                        }
                        templist1 = ChangeDevice.getInstance().changeList(conList,ctx);
                        ListViewForScrollView listView = (ListViewForScrollView)findViewById(R.id.tvset_list_ph_conDevices);
                        if (!conList.isEmpty()){
                            if (checkPH.isChecked()) {
                                ph_conlayout.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, templist1);
                            listView.setAdapter(adapter);
                            TextView txt = (TextView)findViewById(R.id.tvset_ph_conDevices);
                            txt.setText("手机:已连接设备");
                        }else {
                            ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, templist);
                            listView.setAdapter(adapter);
                            TextView txt = (TextView)findViewById(R.id.tvset_ph_conDevices);
                            txt.setText("手机:当前无已连接设备");
                        }
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String s = conList.get(position);
                                String msg = "";
                                conDevice = new ArrayList<BluetoothDevice>(devices);
                                for (int i = 0; i < devices.size(); i++) {
                                    if (s.equals(conDevice.get(i).getName())) {
                                        msg = "是否与设备" + conDevice.get(i).getName() + "断开连接？";
                                        device = conDevice.get(i);
                                    }
                                }
                                showDailog(msg, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        /**取消配对*/
                                        alertDialog.dismiss();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Method createBondMethod = BluetoothDevice.class.getMethod("removeBond");
                                                    createBondMethod.invoke(device);
                                                    Thread.sleep(3000);
                                                    Message msg = new Message();
                                                    msg.what = BTN_REFRESH;
                                                    handler.sendMessage(msg);
//                                                    refresh.callOnClick();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        showProgressDailog5S("断开连接...");
                                    }
                                });
                            }
                        });
                        findDevices = new ArrayList<String>();
                        Message message = new Message();
                        message.what = UPDATE_FINDDEVICE;
                        message.obj = findDevices;
                        handler.sendMessage(message);
                        foundDevices = new ArrayList<BluetoothDevice>();
                        ctx.unregisterReceiver(receiver);
                        final IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

                        // 注册广播接收器，接收并处理搜索结果
                        ctx.registerReceiver(receiver, intentFilter);
                        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
                        if (bAdapter.isDiscovering()) {
                            bAdapter.cancelDiscovery();
                        }
                        bAdapter.startDiscovery();
                        closeThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(60000);
                                    bAdapter.cancelDiscovery();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                        synchronized (closeThread) {
                            closeThread.start();
                        }
                        getListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                conDeviceName = findDevices.get(position);
                                String msg="";
                                for(int i=0;i <foundDevices.size();i++){
                                    if (conDeviceName.equals(foundDevices.get(i).getName())){
                                        currentDevice = foundDevices.get(i);
                                        if(foundDevices.get(i).getBondState() == BluetoothDevice.BOND_BONDED) {
                                            msg= "是否与设备" +foundDevices.get(i).getName() + "连接？";

                                        }else {
                                            msg="是否与设备" + foundDevices.get(i).getName() + "配对并连接？";

                                        }
                                    }
                                }
                                showDailog(msg, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        /**当前需要配对的蓝牙设备*/

                                        /**还没有配对*/
                                        if (currentDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                            try {
                                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                                createBondMethod.invoke(currentDevice);
                                                Thread.sleep(3000);
                                                Message msg = new Message();
                                                msg.what = BTN_REFRESH;
                                                handler.sendMessage(msg);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            /**完成配对的,直接连接*/
                                            try {
                                                connectBlueDevices();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        showProgressDailog();
                                    }
                                });
                            /*if(foundDevices.get(position).getBondState() == BluetoothDevice.BOND_BONDED) {
                                try {
                                    connect(foundDevices.get(position));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }*//*else {
                                try {
                                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                    createBondMethod.invoke(foundDevices.get(position));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }*/
                            }
                        });

                    }
//                }
            }
        });
        refresh.callOnClick();
        /*more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothKeyboard.actionStart(ctx);
            }
        });*/
        computerBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComputerBluetooth.actionStart(ctx);
            }
        });
        returnlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)ctx).finish();
            }
        });

    }
    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取查找到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundDevices.add(device);
                if (device.getName()!=null&&device.getName()!="")
                findDevices.add(device.getName());
                Message message = new Message();
                message.what = UPDATE_FINDDEVICE;
                message.obj = findDevices;
                handler.sendMessage(message);

                // 如果查找到的设备符合要连接的设备，处理
                /*if (device.getName().equalsIgnoreCase(conDeviceName)) {
                    // 搜索蓝牙设备的过程占用资源比较多，一旦找到需要连接的设备后需要及时关闭搜索

                    // 获取蓝牙设备的连接状态
                    int connectState = device.getBondState();
                    switch (connectState) {
                        // 未配对
                        case BluetoothDevice.BOND_NONE:
                            // 配对
                            try {
                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        // 已配对
                        case BluetoothDevice.BOND_BONDED:
                            try {
                                // 连接
                                connect(device);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }*/
            } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 状态改变的广播
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int connectState = device.getBondState();
                switch (connectState) {
                    case BluetoothDevice.BOND_NONE:
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        currentDevice = device;
                        try {
                            connectBlueDevices();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        hideProgressDailog();
                        break;
                }
            }
        }
    }
    class ConThread extends Thread {
        public String ip;
        public String deviceName;
        Gson gson = new Gson();
        Device device = new Device();
        OutputStream outputStream;
        public ConThread(String ip, String deviceName){
            this.ip = ip;
            this.deviceName = deviceName;
        }
        public void run(){
            Socket socket3 = null;
            try {
                socket3 = new Socket(ip,30001);
                outputStream = socket3.getOutputStream();
                device.setConDevice(deviceName);
                String s = gson.toJson(device, Device.class);
                DataOutputStream dos = new DataOutputStream(outputStream);
                try {
                    if(dos!=null){
                        dos.writeUTF(s);
                        dos.flush();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try {
                    socket3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class DisThread extends Thread {
        public String ip;
        public String deviceName;
        Gson gson = new Gson();
        Device device = new Device();
        OutputStream outputStream;
        public DisThread(String ip, String deviceName){
            this.ip = ip;
            this.deviceName = deviceName;
        }
        public void run(){
            Socket socket3 = null;
            try {
                socket3 = new Socket(ip,30001);
                outputStream = socket3.getOutputStream();
                device.setDisDevice(deviceName);
                String s = gson.toJson(device, Device.class);
                DataOutputStream dos = new DataOutputStream(outputStream);
                try {
                    if(dos!=null){
                        dos.writeUTF(s);
                        dos.flush();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(socket3!=null){
                    try {
                        socket3.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    class GetConThread extends Thread {

        public String ip;

        public GetConThread(String str) {
            ip = str;
        }
        @Override
        public void run() {
            Socket socket2 = null;
            try {
                socket2 = new Socket(ip,30000);
                InputStream in = socket2.getInputStream();
                DataInputStream is = new DataInputStream(in);
                Gson gson = new Gson();
                Device d=gson.fromJson(is.readUTF(), Device.class);
               /* List<String> getDevice = new ArrayList<String>();
                if(d.getGetDevice()!=null)
                    getDevice = d.getGetDevice();*/
                List<String> getConDevice = new ArrayList<String>();
                if(d.getGetConDevice()!=null)
                    getConDevice = d.getGetConDevice();
                /*
                msg.what = UPDATE_TV_FINDDEVICE;
                msg.obj = getDevice;
                handler.sendMessage(msg);*/
                Message msg1 = new Message();
                msg1.what=UPDATE_TV_CONDEVICE;
                msg1.obj=getConDevice;
                handler.sendMessage(msg1);
            } catch (IOException e) {
                List<String> getConDevice = new ArrayList<String>();
                Message msg1 = new Message();
                msg1.what=UPDATE_TV_CONDEVICE;
                msg1.obj=getConDevice;
                handler.sendMessage(msg1);
                e.printStackTrace();
            }finally {
                if (socket2!=null){
                    try {
                        socket2.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private void sendAction2Remote(final ControllerDroidAction action) {
        if (myTask != null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DeviceConnection.getInstance().sendAction(action);
                } catch (IOException e) {
                    /*Looper.prepare();
                    Toast.makeText(ctx, "连接已断开,正在重连...", Toast.LENGTH_SHORT).show();
                    Looper.loop();*/
                    doInBackground();
                }
            }
        }).start();
    }

    private void doInBackground() {
        if (myTask != null) {
            return;
        }
        callBack = new MouseAndBoardCallBack() {
            @Override
            public void callback() {
                /*if (DeviceConnection.getInstance().isAuthentificated()) {
                    Toast.makeText(ctx, "连接成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "连接失败", Toast.LENGTH_SHORT).show();
                }*/

                myTask = null;
            }
        };


        myTask = new MouseAndBoardAsyncTask(callBack);

        myTask.execute();
    }
    public   static   void  removeDuplicate(List list)   {
        HashSet h  =   new HashSet(list);
        list.clear();
        list.addAll(h);
    }
    public void showDailog(String msg, DialogInterface.OnClickListener listenter){
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"确认",listenter);
        alertDialog.show();
    }
    public void showProgressDailog5S(String msg){
        if(progressDialog ==null)
            progressDialog=new ProgressDialog(ctx);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    if(progressDialog!=null){
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void showProgressDailog(){
        if(progressDialog==null)
            progressDialog=new ProgressDialog(ctx);
        progressDialog.setMessage("正在连接...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    public void hideProgressDailog(){
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    private void connectBlueDevices() throws IOException {
        if (currentDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO) {
            final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            UUID uuid = UUID.fromString(SPP_UUID);
            socket = currentDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            hideProgressDailog();
        }else {
            /**使用A2DP协议连接设备*/
            bAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothProfile.A2DP);
            hideProgressDailog();
            refresh.callOnClick();
        }
    }

    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceDisconnected(int profile) {

        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            try {
                if (profile == BluetoothProfile.HEADSET) {
//                    bh = (BluetoothHeadset) proxy;
//                    if (bh.getConnectionState(mTouchObject.bluetoothDevice) != BluetoothProfile.STATE_CONNECTED){
//                        bh.getClass()
//                                .getMethod("connect", BluetoothDevice.class)
//                                .invoke(bh, mTouchObject.bluetoothDevice);
//                    }

                } else if (profile == BluetoothProfile.A2DP) {
                    /**使用A2DP的协议连接蓝牙设备（使用了反射技术调用连接的方法）*/
                    a2dp = (BluetoothA2dp) proxy;
                    if (a2dp.getConnectionState(currentDevice) != BluetoothProfile.STATE_CONNECTED) {
                        a2dp.getClass()
                                .getMethod("connect", BluetoothDevice.class)
                                .invoke(a2dp, currentDevice);


                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private String getlocalip(){
        WifiManager wifiManager = (WifiManager)ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        //  Log.d(Tag, "int ip "+ipAddress);
        if(ipAddress==0)return null;
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }
}
