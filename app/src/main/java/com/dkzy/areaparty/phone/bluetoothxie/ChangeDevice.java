package com.dkzy.areaparty.phone.bluetoothxie;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.InputDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by XIE on 2017/5/18.
 */

public class ChangeDevice {
    public static ChangeDevice instance;
    private MyDatabaseHelper dbHelper;
    public synchronized static ChangeDevice getInstance(){
        if (null == instance){
            instance = new ChangeDevice();
        }
        return instance;
    }
            //根据数据库修改DeviceList对象中checked,name,idName,type字段
    public DeviceList changeChecked(DeviceList list, Context ctx){
        String pairedDevice= "";
        dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Device", null, null, null, null, null ,null);
        if (cursor.moveToFirst()){
            do {
                String s = cursor.getString(cursor.getColumnIndex("name"));
                String name = list.getName();
                if (s.equals(name)) {
                    if (cursor.getInt(cursor.getColumnIndex("checked"))>0)
                        list.setChecked(1);
                    else list.setChecked(0);
                    list.setName(cursor.getString(cursor.getColumnIndex("name")));
                    list.setIdName(cursor.getString(cursor.getColumnIndex("idname")));
                    list.setType(cursor.getInt(cursor.getColumnIndex("type")));
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> device = mBluetoothAdapter.getBondedDevices();
        //判断已配对设备是否已连接,是否为输入输出设备
        for (BluetoothDevice device1 : device){
            if (device1.getName().equals(list.getName())&&device1.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL){
                pairedDevice = device1.getName();
            }
            int[] devices = InputDevice.getDeviceIds();
            for (int i = 0; i < devices.length; i++) {
                InputDevice device2 = InputDevice.getDevice(devices[i]);
                if (device2 != null&&device2.getName().equals(pairedDevice)) {
                    list.setConnected(1);
                }
            }
        }
        return list;
    }
    //添加设备名字符串到DeviceList对象的list中，并添加checked,name,idname,type字段
    public void addList(List<DeviceList> list, String name, Context ctx){
        DeviceList dlist = new DeviceList(name,name,0,4,0);//获取到tv，手机，pc的设备名称列表，初始化DeviceList，将name和idname均设为原来设备名称，然后访问数据库根据数据库修改
        changeChecked(dlist,ctx);                           //若数据库中没有设备信息，就默认为原设备名，并且设备checked状态为0，type默认为4即未知设备类型
        list.add(dlist);
    }
    //将List<String>转化未List<DeviceList>并添加checked,name,idname,type字段
    public List<DeviceList> changeList(List<String> list, Context ctx){
        List<DeviceList> deviceLists = new ArrayList<DeviceList>();
        for (int i = 0;i<list.size();++i){
            DeviceList dlist = new DeviceList(list.get(i),list.get(i),0,4,0);
            changeChecked(dlist ,ctx);
            deviceLists.add(dlist);
        }
        return deviceLists;
    }
    //查询数据库，将可连接设备名改为用户自定义名称
    public List<String> changeNameString(List<String> list,Context ctx){
        List<String> list1 = new ArrayList<String>();
        list1.addAll(list);
        for (int i = 0;i<list.size();++i){
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("Device", null, null, null, null, null ,null);
            if (cursor.moveToFirst()){
                do {
                    String s = cursor.getString(cursor.getColumnIndex("name"));
                    if (s.equals(list.get(i))) {
                        String idname = cursor.getString(cursor.getColumnIndex("idname"));
                        list1.set(i,idname);
                        break;
                    }
                }while (cursor.moveToNext());
            }
        }
        return list1;
    }
    //查询数据库，将已连接设备名称改为用户自定义名称，已连接设备名包含checked字段，所以使用List<DeviceList>
    public List<DeviceList> changeName(List<DeviceList> list, Context ctx){
        List<DeviceList> list1 = new ArrayList<DeviceList>();
        list1.addAll(list);
        for (int i = 0;i<list.size();++i){
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("Device", null, null, null, null, null ,null);
            if (cursor.moveToFirst()){
                do {
                    String s = cursor.getString(cursor.getColumnIndex("name"));
                    if (s.equals(list.get(i).getName())) {
                        String idname = cursor.getString(cursor.getColumnIndex("idname"));
                        list1.get(i).setIdName(idname);
                        break;
                    }
                }while (cursor.moveToNext());
            }
        }
        return list1;
    }
    //查询数据库,将list里面checked的键鼠设备保留,其余去除,返回新的list1,里面元素复制list并将设备名称替换成用户自定义名称,deviceType表示要显示的设备类型，在Activity启动时传递这个参数
    public List<String> removeDeviceString(int deviceType,List<String> list,Context ctx){
        List<String> list1 = new ArrayList<>();
        list1.addAll(list);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Device", null, null, null, null, null ,null);
        for (int i=0;i<list.size();++i) {
            boolean exist = false;
            if (cursor.moveToFirst()) {
                do {
                    String s = cursor.getString(cursor.getColumnIndex("name"));
                    if (s.equals(list.get(i))) {
                        if (deviceType == 1){
                            if (cursor.getInt(cursor.getColumnIndex("checked"))>0&&cursor.getInt(cursor.getColumnIndex("type"))<2){
                                exist = true;
                                String idname = cursor.getString(cursor.getColumnIndex("idname"));
                                list1.set(i,idname);
                            }
                        }else if (deviceType == 3){
                            if (cursor.getInt(cursor.getColumnIndex("checked"))>0){
                                exist = true;
                                String idname = cursor.getString(cursor.getColumnIndex("idname"));
                                list1.set(i,idname);
                            }
                        }
                        break;
                    }
                } while (cursor.moveToNext());
            }
            if (!exist) {
                list.remove(i);
                list1.remove(i);
                i = i-1;
            }
        }
        cursor.close();
        return list1;
    }
    //查询数据库,将list里面checked的键鼠设备保留,其余去除,并将设备名称替换成用户自定义名称
    public List<DeviceList> removeDevice(int deviceType, List<DeviceList> list, Context ctx){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Device", null, null, null, null, null ,null);
        for (int i=0;i<list.size();++i) {
            boolean exist = false;
            if (cursor.moveToFirst()) {
                do {
                    String s = cursor.getString(cursor.getColumnIndex("name"));
                    if (s.equals(list.get(i).getName())) {
                        if (deviceType == 1){
                            if (cursor.getInt(cursor.getColumnIndex("checked"))>0&&cursor.getInt(cursor.getColumnIndex("type"))<2){
                                exist = true;
                                String idname = cursor.getString(cursor.getColumnIndex("idname"));
                                list.get(i).setIdName(idname);
                            }
                        }else if (deviceType == 3){
                            if (cursor.getInt(cursor.getColumnIndex("checked"))>0){
                                exist = true;
                                String idname = cursor.getString(cursor.getColumnIndex("idname"));
                                list.get(i).setIdName(idname);
                            }
                        }
                        break;
                    }
                } while (cursor.moveToNext());
            }
            if (!exist) {
                list.remove(i);
                i = i-1;
            }
        }
        cursor.close();
        return list;
    }
}
