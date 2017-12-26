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
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dkzy.areaparty.phone.R;
import com.example.action.ControllerDroidAction;
import com.example.connection.DeviceConnection;
import com.google.gson.Gson;

import net.yanzm.mth.MaterialTabHost;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Created by XIE on 2017/2/24.
 * 蓝牙设备列表Acticity，包含两个HostTabFragment:PlaceholderFragment01 02
 */

public class ComputerBluetooth extends FragmentActivity {
    String ip_ph;
    static String ip_pc ;
    static String ip_tv ;
    Button btn_getDevices;
    Button btn_devicesFresh;
    Button btn_getConnectedDevices;

    ImageButton returnlogo;

    DevicesAdapter adapter;
    DevicesAdapter conAdapter;
    static BluetoothAdapter bAdapter;
    static BluetoothSocket socket;
    Socket tvSocket;
    static Socket phSocket;
    ServerSocket serverSocket;

    private HidConncetUtil mHidConncetUtil;
    static Receiver receiver;
    long timer =0;
    private boolean filedone;
    public static boolean swt_on;
    public static final int UPDATE_TEXT = 1;
    public static final int UPDATE_CONTEXT = 2;
    public static final int UPDATE_FINDDEVICE = 3;
    public static final int UPDATE_TV_FINDDEVICE = 4;
    public static final int UPDATE_TV_CONDEVICE=5;
    public static final int CONNECT_DEVICE =6;         //通知tv服务端连接断开设备
    public static final int DISCON_DEVICE =7;
    public static final int FIND_DEVICES = 8;   //tv服务端通知设备列表更新
    public static final int CONNECT_DEVICES = 9;
    static List<String> pcFindDevices = new ArrayList<String>();   //pc可连接已连接list
    static List<DeviceList> pcConDevices= new ArrayList<DeviceList>();
    static List<String> pcConDevicesString = new ArrayList<>();
    static List<String> findDevices = new ArrayList<>();  //手机可连接已连接list
    static List<DeviceList> conList = new ArrayList<DeviceList>();
    static List<String> conListString = new ArrayList<>();
    static List<String> tvFindDevices = new ArrayList<>();//tv 可连接已连接list
    static List<DeviceList> tvConDevices = new ArrayList<>();
    static List<String> tvConDevicesString = new ArrayList<>();
    static List<BluetoothDevice> foundDevices = new ArrayList<>();
    static List<Fragment> fragments;
    static Set<BluetoothDevice> devices;
    static BluetoothDevice currentDevice;
    String conDeviceName ;
    static Thread closeThread;
    protected static ProgressDialog progressDialog;
    static AlertDialog alertDialog;
    private BluetoothA2dp a2dp;
    private static MouseAndBoardAsyncTask myTask;
    private static MouseAndBoardCallBack callBack;
    private static Activity act ;
    public static Context ctx;
    static DataOutputStream dos;
    static ByteArrayOutputStream output;
    static OutputStream outputStream;
    DataInputStream dataInputStream;//PC
    DataInputStream inputStream;//TV
    static MyHandler handler1 ;
    /*private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_TEXT:
                    //pc_findlist
                    *//*txt_getDevices.setText(((List<String>)msg.obj).toString());
                    if(txt_getDevices.getText().toString() == ""){
                        txt_getDevices.setText("无可连接设备");
                    }*//*
                    pcFindDevices = (List<String>) msg.obj;
                    removeDuplicate(pcFindDevices);
                    if(!pcFindDevices.isEmpty()){
                        pc_layout.setVisibility(View.VISIBLE);
                        adapter = new DevicesAdapter(ComputerBluetooth.this, R.layout.devices_item, pcFindDevices);
                        listView.setAdapter(adapter);
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
                        pc_conlayout.setVisibility(View.VISIBLE);
                        conAdapter = new DevicesAdapter(ComputerBluetooth.this, R.layout.devices_item, pcConDevices);
                        conListView.setAdapter(conAdapter);
                    }
                    break;
                case UPDATE_FINDDEVICE:
                    //ph_findlist
                    List<String> list = (List<String>)msg.obj;
                    removeDuplicate(list);
                    DevicesAdapter getAdapter = new DevicesAdapter(ComputerBluetooth.this, R.layout.devices_item, list);
                    getListView.setAdapter(getAdapter);
                    break;
                case 0x11:
                    Bundle bundle = msg.getData();
                    Toast.makeText(ctx,bundle.getString("msg"),Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_TV_FINDDEVICE:
                    tvFindDevices = (List<String>) msg.obj;
                    removeDuplicate(tvFindDevices);
                    if (!tvFindDevices.isEmpty()){
                        tv_layout.setVisibility(View.VISIBLE);
                        DevicesAdapter getAdapter1 = new DevicesAdapter(ComputerBluetooth.this, R.layout.devices_item, tvFindDevices);
                        tvGetListView.setAdapter(getAdapter1);
                    }
                    break;
                case UPDATE_TV_CONDEVICE:
                    tvConDevices = (List<String>)msg.obj;
                    removeDuplicate(tvConDevices);
                    if (!tvConDevices.isEmpty()){
                        tv_conlayout.setVisibility(View.VISIBLE);
                        DevicesAdapter adapter = new DevicesAdapter(ComputerBluetooth.this, R.layout.devices_item, tvConDevices);
                        tvConListView.setAdapter(adapter);
                    }
                    break;
                default:
                    break;
            }
        }
    };*/
    public static void actionStart(Context context){
        Intent intent = new Intent(context, ComputerBluetooth.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        if (progressDialog!=null)
            progressDialog.dismiss();
        if (alertDialog!=null)
            alertDialog.dismiss();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        // 注册广播接收器，接收并处理搜索结果
        ctx.registerReceiver(receiver, intentFilter);
        super.onResume();
    }

    protected void onPause(){
        if (progressDialog!=null)
        progressDialog.dismiss();
        if (alertDialog!=null)
        alertDialog.dismiss();

        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computerbluetooth);//加载含有TabHost页面
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //TabHost初始化
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        MaterialTabHost tabHost = (MaterialTabHost) findViewById(android.R.id.tabhost);
        tabHost.setType(MaterialTabHost.Type.FullScreenWidth);
//        tabHost.setType(MaterialTabHost.Type.Centered);
//        tabHost.setType(MaterialTabHost.Type.LeftOffset);
        /*Constants.getInstance().setIP_PC();
        Constants.getInstance().setIP_TV();*/
        ip_pc = Constants.getIP_PC();
        ip_tv = Constants.getIP_TV();
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(pagerAdapter.getPageTitle(i));
        }

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(tabHost);

        tabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        fragments = new ArrayList<>();
        final PlaceholderFragment01 tabContent01 = new PlaceholderFragment01();
        PlaceholderFragment02 tabContent02 = new PlaceholderFragment02();
        fragments.add(tabContent01);
        fragments.add(tabContent02);
        handler1 = new MyHandler((PlaceholderFragment01) fragments.get(0));
        //Hid设备连接工具
        mHidConncetUtil = new HidConncetUtil(this);
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
                                handler1.sendMessage(msg);
                            }else break;
                        }
                    } catch (IOException e) {
                        List<String> getDevice = new ArrayList<String>();
                        Message msg = new Message();
                        msg.what = UPDATE_TV_FINDDEVICE;
                        msg.obj = getDevice;
                        handler1.sendMessage(msg);
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
            /*Toast.makeText(ComputerBluetooth.this, "服务器已连接", Toast.LENGTH_SHORT)
                    .show();*/


        } else
        {
            /*Toast.makeText(ComputerBluetooth.this, "正在连接...", Toast.LENGTH_SHORT)
                    .show();*/
            doInBackground();

        }
        act = this;
        ctx = this;
        ip_ph = getlocalip();
        initListeners();
    }

    protected void onDestroy(){
        ctx.unregisterReceiver(receiver);
        progressDialog = null;
        super.onDestroy();
    }
    public void initListeners(){
        returnlogo = (ImageButton)findViewById(R.id.returnLogo_imgButton);
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        swt_on =  bAdapter.isEnabled();
        receiver = new Receiver();
        // 设置广播信息过滤
        Log.e("XIE","注册广播");
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



//        btn_devicesFresh =(Button) findViewById(R.id.btn_devicesfresh);
        //btn_conDevices =(Button) findViewById(R.id.connect_bluetoothdevices);



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
              /*  if (device.getName()!=null&&device.getName()!=""){
                    DeviceList list = new DeviceList(device.getName(),0);
                    ChangeDevice.getInstance().changeChecked(list,context);
                    findDevices.add(list);
                }*/
                MyHandler handler = new MyHandler((PlaceholderFragment01) fragments.get(0));
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
                Log.e("XIE","蓝牙设备状态改变");
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
    static class ConThread extends Thread {
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
    static class DisThread extends Thread {
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
    static class GetConThread extends Thread {

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
                List<DeviceList> getConDevice = new ArrayList<DeviceList>();
                if(d.getGetConDevice()!=null){
                    getConDevice = ChangeDevice.getInstance().changeList(d.getGetConDevice(),ctx);
                }
                tvConDevicesString = d.getGetConDevice();
                /*
                msg.what = UPDATE_TV_FINDDEVICE;
                msg.obj = getDevice;
                handler.sendMessage(msg);*/
                Message msg1 = new Message();
                msg1.what=UPDATE_TV_CONDEVICE;
                msg1.obj=getConDevice;
                handler1.sendMessage(msg1);
            } catch (IOException e) {
                List<DeviceList> getConDevice = new ArrayList<DeviceList>();
                Message msg1 = new Message();
                msg1.what=UPDATE_TV_CONDEVICE;
                msg1.obj=getConDevice;
                handler1.sendMessage(msg1);
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

    public static void sendAction2Remote(final ControllerDroidAction action) {
        if (myTask != null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DeviceConnection.getInstance().sendAction(action);
                } catch (IOException e) {
                    e.printStackTrace();
                 /*   Looper.prepare();
                    Toast.makeText(ctx, "连接已断开,正在重连...", Toast.LENGTH_SHORT).show();
                    Looper.loop();*/
                    doInBackground();
                }
            }
        }).start();
    }

    public static void doInBackground() {
        if (myTask != null) {
            return;
        }
        callBack = new MouseAndBoardCallBack() {
            @Override
            public void callback() {
               /* if (DeviceConnection.getInstance().isAuthentificated()) {
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
    public static void showDailog(String msg, DialogInterface.OnClickListener listenter){
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

    public static void showProgressDailog(){
        if(progressDialog==null)
            progressDialog=new ProgressDialog(ctx);
        progressDialog.setMessage("正在连接...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }
    public static void showProgressDailog5S(String msg){
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

    public static void hideProgressDailog(){
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    public void connectBlueDevices() throws IOException {
        if (currentDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO) {
            if (currentDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER){
                final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
                final UUID uuid = UUID.fromString(SPP_UUID);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = currentDevice.createRfcommSocketToServiceRecord(uuid);
                            socket.connect();
                        } catch (IOException e) {
                            Log.e("XIE","connect异常");
                            e.printStackTrace();
                        }
                    }
                }).start();
                hideProgressDailog();
            }else {
//                mHidConncetUtil.pair(currentDevice);
                mHidConncetUtil.connect(currentDevice);
                hideProgressDailog();
            }
        }else {
            //使用A2DP协议连接设备
            bAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothProfile.A2DP);
            hideProgressDailog();
            ((PlaceholderFragment01)fragments.get(0)).fresh();
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
    //TabHost工具方法
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // 总共两页
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                /*case 2:
                    return getString(R.string.title_section3).toUpperCase(l);*/
            }
            return null;
        }
    }
}
