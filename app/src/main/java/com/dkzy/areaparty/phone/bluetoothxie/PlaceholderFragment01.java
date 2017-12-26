package com.dkzy.areaparty.phone.bluetoothxie;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.example.action.ConnectBluetoothDevicesAction;
import com.example.action.DisconnectBluetoothDevicesAction;
import com.example.connection.DeviceConnection;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.alertDialog;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.bAdapter;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.closeThread;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.conList;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.conListString;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.ctx;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.currentDevice;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.devices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.doInBackground;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.findDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.foundDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.fragments;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.ip_pc;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.ip_tv;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.pcConDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.pcConDevicesString;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.pcFindDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.phSocket;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.receiver;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.sendAction2Remote;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.showDailog;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.showProgressDailog5S;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.swt_on;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.tvConDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.tvConDevicesString;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.tvFindDevices;

/**
 * Created by a c e r on 2017/4/10.
 */

public class PlaceholderFragment01 extends Fragment {
    public static final int UPDATE_TEXT = 1;
    public static final int UPDATE_CONTEXT = 2;
    public static final int UPDATE_FINDDEVICE = 3;
    public static final int UPDATE_TV_FINDDEVICE = 4;
    public static final int UPDATE_TV_CONDEVICE=5;
    public static final int BTN_REFRESH = 6;
    Switch swt ;
    Switch swt_bluetooth;
    TextView bar01moreaction1;
    TextView bar01moreaction2;
    LinearLayout refresh;
    LinearLayout more;
    LinearLayout pc_layout;
    LinearLayout pc_conlayout;
    LinearLayout tv_layout;
    LinearLayout tv_conlayout;
    LinearLayout bar01moreroot;
    LinearLayout ph_layout;
    ListViewForScrollView listView;
    ListViewForScrollView conListView;
    ListViewForScrollView getListView;
    ListViewForScrollView tvGetListView;
    ListViewForScrollView tvConListView;
    ListViewForScrollView phConListView;
    List<BluetoothDevice> conDevice;
    BluetoothDevice device;
    MyDatabaseHelper dbHelper;
    long timer;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case BTN_REFRESH:
                    refresh.callOnClick();
                    break;
            }
            return false;
        }
    });

    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
        Log.e("Xie","initView");
        /*ip_pc = Constants.getIP_PC();
        ip_tv = Constants.getIP_TV();*/
        final MyHandler handler = new MyHandler((PlaceholderFragment01) fragments.get(0));
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
                            handler1.sendMessage(msg);
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
                            handler1.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
        tvGetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String msg= "是否与设备" +tvFindDevices.get(position) + "建立连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**配对*/
                        try {
                            if (ip_tv!=null)
                            new ComputerBluetooth.ConThread(ip_tv,tvFindDevices.get(position)).start();
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
                                handler1.sendMessage(msg);
                            }
                        }).start();
                    }
                });
            }
        });
        tvConListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String msg= "是否与设备" +tvConDevicesString.get(position) + "断开连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**取消配对*/
                        try {
                            if (ip_tv!=null)
                            new ComputerBluetooth.DisThread(ip_tv,tvConDevicesString.get(position)).start();
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
                                handler1.sendMessage(msg);
                            }
                        }).start();
                    }
                });
            }
        });
        tvConListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showNamedDialog(tvConDevices.get(position).getIdName());
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String s = pcFindDevices.get(position);
                String msg = "";
                msg= "是否与设备" + s + "连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**连接*/
                        ConnectBluetoothDevicesAction connectBluetoothDevicesAction = new ConnectBluetoothDevicesAction(s);
                        sendAction2Remote(connectBluetoothDevicesAction);
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
                                handler1.sendMessage(msg);
                            }
                        }).start();
                    }
                });

            }
        });

        conListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String s = pcConDevices.get(position).getName();
                String msg = "";
                msg= "是否与设备" + s + "断开连接？";
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**断开连接*/
                        DisconnectBluetoothDevicesAction disconnectBluetoothDevicesAction = new DisconnectBluetoothDevicesAction(s);
                        sendAction2Remote(disconnectBluetoothDevicesAction);
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
                                handler1.sendMessage(msg);
                            }
                        }).start();
                    }
                });

            }
        });
        conListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showNamedDialog(pcConDevicesString.get(i));
                return true;
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceConnection.getInstance().isAuthentificated())                            //获得连接状态，未连接跳转到重连函数doInBackground
                {

                } else
                {
                    Toast.makeText(ctx, "正在连接...", Toast.LENGTH_SHORT)
                            .show();
                    doInBackground();
                }
                swt_bluetooth.setChecked(bAdapter.isEnabled());
                //如果获取tv可连接设备线程未启动，则启动线程获取TV可连接设备
                if (phSocket !=null) {
                    try {
                        phSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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
                                List<String> getDevice = new ArrayList<String>();
                                Message msg = new Message();
                                msg.what = UPDATE_TV_FINDDEVICE;
                                msg.obj = getDevice;
                                handler.sendMessage(msg);
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
                        }else
                        Log.e("IP","ip_tv为空");
                    }
                }).start();

                //间隔三秒可以点击刷新按钮
//                Date now = new Date();
//                if((now.getTime()-timer)>3000){
                    /*pc_conlayout.setVisibility(View.GONE);
                    pc_layout.setVisibility(View.GONE);
                    tv_layout.setVisibility(View.GONE);
                    tv_conlayout.setVisibility(View.GONE);*/
//                    timer = now.getTime();
                //与tv连接获取已连接设备列表
                    if (ip_tv!=null)
                    new ComputerBluetooth.GetConThread(ip_tv).start();
                    //获取电脑蓝牙设备列表
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Socket socket1 = null;
                            if (ip_pc!=null){
                                try {
                                    socket1 = new Socket(ip_pc,56565);
                                    InputStream in = socket1.getInputStream();
                                    DataInputStream is = new DataInputStream(in);
                                    Gson gson = new Gson();
                                    Device d=gson.fromJson(is.readUTF(), Device.class);
                                    List<String> getDevice = new ArrayList<String>();
                                    if(d.getGetDevice()!=null)
                                        getDevice = d.getGetDevice();
                                    pcConDevicesString = new ArrayList<String>();
                                    if(d.getGetConDevice()!=null)
                                        pcConDevicesString = d.getGetConDevice();
                                    getDevice.removeAll(pcConDevicesString);
                                    List<DeviceList> list = ChangeDevice.getInstance().changeList(pcConDevicesString,ctx);
                                    Message msg = new Message();
                                    msg.what = UPDATE_TEXT;
                                    msg.obj = getDevice;
                                    handler.sendMessage(msg);
                                    Message msg1 = new Message();
                                    msg1.what=UPDATE_CONTEXT;
                                    msg1.obj=list;
                                    handler.sendMessage(msg1);
                                } catch (IOException e) {
                                    List<String> getDevice = new ArrayList<String>();
                                    List<String> getConDevice = new ArrayList<String>();
                                    List<DeviceList> list = ChangeDevice.getInstance().changeList(getConDevice,ctx);
                                    Message msg = new Message();
                                    msg.what = UPDATE_TEXT;
                                    msg.obj = getDevice;
                                    handler.sendMessage(msg);
                                    Message msg1 = new Message();
                                    msg1.what=UPDATE_CONTEXT;
                                    msg1.obj=list;
                                    handler.sendMessage(msg1);
                                    e.printStackTrace();
                                }finally {
                                    try {
                                        if (socket1!=null)
                                            socket1.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                Log.e("IP","ip_pc为空");
                                List<String> getDevice = new ArrayList<String>();
                                List<String> getConDevice = new ArrayList<String>();
                                List<DeviceList> list = ChangeDevice.getInstance().changeList(getConDevice,ctx);
                                Message msg = new Message();
                                msg.what = UPDATE_TEXT;
                                msg.obj = getDevice;
                                handler.sendMessage(msg);
                                Message msg1 = new Message();
                                msg1.what=UPDATE_CONTEXT;
                                msg1.obj=list;
                                handler.sendMessage(msg1);
                            }
                        }
                    }).start();
//                bAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!bAdapter.isEnabled()){
                        //如果未开启蓝牙，将列表置空
                        findDevices = new ArrayList<String>();
                        MyHandler handler = new MyHandler((PlaceholderFragment01) fragments.get(0));
                        Message message = new Message();
                        message.what = UPDATE_FINDDEVICE;
                        message.obj = findDevices;
                        handler.sendMessage(message);
                        conList = new ArrayList<DeviceList>();
                        ph_layout.setVisibility(View.GONE);
                        ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condevices_item, conList);
                        phConListView.setAdapter(adapter);
                        Toast.makeText(ctx,"手机未开启蓝牙", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    ctx.startActivity(intent);*/
                    }else {
                        //获取手机已配对蓝牙设备
                        conList = new ArrayList<DeviceList>();
                        conListString = new ArrayList<String>();
                        devices = bAdapter.getBondedDevices();
                        for (BluetoothDevice device : devices) {
                            ChangeDevice.getInstance().addList(conList,device.getName(),ctx);
                            conListString.add(device.getName());
                        }
                        List<DeviceList> list =  ChangeDevice.getInstance().changeName(conList,ctx);//查询数据库替换用户自定义id
                        if (!list.isEmpty()){
                            ph_layout.setVisibility(View.VISIBLE);
                            ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condeviceswithbox_item, list);
                            phConListView.setAdapter(adapter);
                        }else {
                            ph_layout.setVisibility(View.GONE);
                            ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condeviceswithbox_item, list);
                            phConListView.setAdapter(adapter);
                        }
                        phConListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String s = conListString.get(position);
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
                                                    Thread.sleep(1000);
                                                    Message msg = new Message();
                                                    msg.what = BTN_REFRESH;
                                                    handler1.sendMessage(msg);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();

                                        showProgressDailog5S("断开连接...");
                                               /* new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Thread.sleep(5000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Message msg = new Message();
                                                        msg.what = BTN_REFRESH;
                                                        handler1.sendMessage(msg);
                                                    }
                                                }).start();*/
                                    }
                                });
                            }
                        });
                        phConListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                showNamedDialog(conList.get(position).getIdName());
                                return true;
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
                                String conDeviceName = findDevices.get(position);
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
                                        alertDialog.dismiss();
                                        /**还没有配对*/
                                        if (currentDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                                        createBondMethod.invoke(currentDevice);
                                                        Thread.sleep(1000);
                                                        Message msg = new Message();
                                                        msg.what = BTN_REFRESH;
                                                        handler1.sendMessage(msg);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();

                                        } else {
                                            /**完成配对的,直接连接*/
                                           /* MyHandler handler =new MyHandler((PlaceholderFragment01) fragments.get(0));
                                            Message msg = new Message();
                                            msg.what = BTN_REFRESH;*/
                                        }
                                        ComputerBluetooth.showProgressDailog();
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

            }
        });
        refresh.callOnClick();
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar01moreroot.setVisibility(View.VISIBLE);
            }
        });
        bar01moreaction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (MyApplication.isSelectedPCOnline()) {
                KeyboardActivity.actionStart(ctx);
            }else Toast.makeText(ctx, "PC未在线", Toast.LENGTH_SHORT).show();
            }
        });
        bar01moreaction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (MyApplication.isSelectedPCOnline()) {
                BluetoothKeyboard.actionStart(ctx);
            }else Toast.makeText(ctx, "PC未在线", Toast.LENGTH_SHORT).show();
            }
        });
        bar01moreroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar01moreroot.setVisibility(View.GONE);
            }
        });
    }
    private void initView(){
        swt = (Switch)getActivity().findViewById(R.id.swc_bluetooth);
        bar01moreaction1 = (TextView)getActivity().findViewById(R.id.bar01MoreAction1);
        bar01moreaction2 = (TextView)getActivity().findViewById(R.id.bar01MoreAction2);
        ph_layout = (LinearLayout)getActivity().findViewById(R.id.ph_layout);
        pc_layout =(LinearLayout)getActivity().findViewById(R.id.pc_layout);
        getListView = (ListViewForScrollView)getActivity().findViewById(R.id.list_ph_getDevices);
        tvGetListView = (ListViewForScrollView)getActivity().findViewById(R.id.list_tv_getDevices) ;
        tvConListView = (ListViewForScrollView)getActivity().findViewById(R.id.list_tv_conDevices);
        listView= (ListViewForScrollView) getActivity().findViewById(R.id.list_getDevices);
        conListView = (ListViewForScrollView) getActivity().findViewById(R.id.list_conDevices);
        pc_conlayout =(LinearLayout)getActivity().findViewById(R.id.pc_conlayout);
        tv_layout =(LinearLayout)getActivity().findViewById(R.id.tv_layout);
        tv_conlayout =(LinearLayout)getActivity().findViewById(R.id.tv_conlayout);
        phConListView = (ListViewForScrollView)getActivity().findViewById(R.id.list_ph_conDevices);
        refresh = (LinearLayout)getActivity().findViewById(R.id.diskListRefreshLL);
        more = (LinearLayout)getActivity().findViewById(R.id.diskListMoreLL);
        bar01moreroot = (LinearLayout)getActivity().findViewById(R.id.bar01MoreRootLL);
        swt_bluetooth = (Switch)getActivity().findViewById(R.id.swc_bluetooth);

    }
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public void fresh(){
        refresh.callOnClick();
    }
    public PlaceholderFragment01() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bluetooth_fragment, container, false);
        initView();
        return rootView;
    }
    public void showNamedDialog(final String name) {
        EditDialog editDialog = new EditDialog(ctx);
        editDialog.show();
        editDialog.setOnPosNegClickListener(new EditDialog.OnPosNegClickListener() {
            @Override
            public void posClickListener(String value,int type) {
                boolean exist = false;
                dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query("Device", null, null, null, null, null ,null);
                if (cursor.moveToFirst()){
                    do {
                        String s = cursor.getString(cursor.getColumnIndex("idname"));
                        if (s.equals(name)) {
                            exist = true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                ContentValues values = new ContentValues();
                if (exist){
                    Log.e("type",Integer.toString(type));
                    if (!value.isEmpty())
                        values.put("idname",value);
                    values.put("type",type);
                    values.put("checked",1);
                    db.update("Device",values,"idname = ?",new String[]{name});
                }else {
                    //组装Device数据
                    values.put("name", name);
                    if (!value.isEmpty())
                        values.put("idname", value);
                    values.put("checked", 1);
                    values.put("type", type);
                    db.insert("Device", null, values);//插入数据库
                }
                Toast.makeText(ctx, "确定", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void negCliclListener(String value,int type) {
                dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("checked", 1);
                db.update("Device",values,"idname = ?",new String[]{name});
                Toast.makeText(ctx, "取消", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
