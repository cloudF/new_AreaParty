package com.dkzy.areaparty.phone.bluetoothxie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.dkzy.areaparty.phone.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.ctx;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.pcConDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.pcFindDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.removeDuplicate;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.tvConDevices;
import static com.dkzy.areaparty.phone.bluetoothxie.ComputerBluetooth.tvFindDevices;


/**
 * Created by a c e r on 2017/4/10.
 */

public class MyHandler extends Handler {
    public static final int UPDATE_TEXT = 1;
    public static final int UPDATE_CONTEXT = 2;
    public static final int UPDATE_FINDDEVICE = 3;
    public static final int UPDATE_TV_FINDDEVICE = 4;
    public static final int UPDATE_TV_CONDEVICE=5;
    public static final int BTN_REFRESH = 6;
    List<DeviceList> templist = new ArrayList<>();
    List<String> templist1 = new ArrayList<>();
    PlaceholderFragment01 fragment01;
    PlaceholderFragment02 fragment02;
    public MyHandler(){}


    MyHandler(PlaceholderFragment01 fragment){
        this.fragment01 = new WeakReference<>(fragment).get();
    }

    MyHandler(PlaceholderFragment02 fragment){
        this.fragment02 = new WeakReference<>(fragment).get();
    }
    public void handleMessage(Message msg){
        switch (msg.what){
            case UPDATE_TEXT:
                //pc_findlist
                pcFindDevices = (List<String>) msg.obj;
                removeDuplicate(pcFindDevices);
                for (int i=0;i<pcFindDevices.size();++i){
                    if (pcFindDevices.get(i)==null||pcFindDevices.get(i).equals("")){
                        pcFindDevices.remove(i);
                    }
                }
//                templist1 =  ChangeDevice.getInstance().changeNameString(pcFindDevices,ctx);//查询数据库替换用户自定义id
                templist = ChangeDevice.getInstance().changeList(pcFindDevices,ctx);
                if(!templist.isEmpty()){
                    fragment01.pc_layout.setVisibility(View.VISIBLE);
                    DevicesAdapter adapter = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                    fragment01.listView.setAdapter(adapter);
                }else {
                    fragment01.pc_layout.setVisibility(View.GONE);
                    DevicesAdapter adapter = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                    fragment01.listView.setAdapter(adapter);
                }
                break;
            case UPDATE_CONTEXT:
                //pc_connectlist
                pcConDevices = (List<DeviceList>) msg.obj;
                removeDuplicate(pcConDevices);
                templist =  ChangeDevice.getInstance().changeName(pcConDevices,ctx);//查询数据库替换用户自定义id
                if (!templist.isEmpty()){
                    fragment01.pc_conlayout.setVisibility(View.VISIBLE);
                    ConDevicesAdapter conAdapter = new ConDevicesAdapter(ctx, R.layout.condeviceswithbox_item, templist);
                    fragment01.conListView.setAdapter(conAdapter);
                }else {
                    fragment01.pc_conlayout.setVisibility(View.GONE);
                    ConDevicesAdapter conAdapter = new ConDevicesAdapter(ctx, R.layout.condeviceswithbox_item, templist);
                    fragment01.conListView.setAdapter(conAdapter);
                }
                break;
            case UPDATE_FINDDEVICE:
                //ph_findlist
                List<String> list = (List<String>)msg.obj;
                removeDuplicate(list);
                for (int i=0;i<list.size();++i){
                    if (list.get(i)==null||list.get(i).equals("")){
                        list.remove(i);
                    }
                }
//                templist1 =  ChangeDevice.getInstance().changeNameString(list,ctx);//查询数据库替换用户自定义id
                templist = ChangeDevice.getInstance().changeList(list,ctx);
                DevicesAdapter getAdapter = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                fragment01.getListView.setAdapter(getAdapter);
                break;
            case 0x11:
                Bundle bundle = msg.getData();
                Toast.makeText(ctx,bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                break;
            case UPDATE_TV_FINDDEVICE:
                tvFindDevices = (List<String>) msg.obj;
                removeDuplicate(tvFindDevices);
                for (int i=0;i<tvFindDevices.size();++i){
                    if (tvFindDevices.get(i)==null||tvFindDevices.get(i).equals("")){
                        tvFindDevices.remove(i);
                    }
                }
//                templist1 =  ChangeDevice.getInstance().changeNameString(tvFindDevices,ctx);//查询数据库替换用户自定义id
                templist = ChangeDevice.getInstance().changeList(tvFindDevices,ctx);
                if (!templist.isEmpty()){
                    fragment01.tv_layout.setVisibility(View.VISIBLE);
                    DevicesAdapter getAdapter1 = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                    fragment01.tvGetListView.setAdapter(getAdapter1);
                }else {
                    fragment01.tv_layout.setVisibility(View.GONE);
                    DevicesAdapter getAdapter1 = new DevicesAdapter(ctx, R.layout.devices_item, templist);
                    fragment01.tvGetListView.setAdapter(getAdapter1);
                }
                break;
            case UPDATE_TV_CONDEVICE:
                tvConDevices = (List<DeviceList>)msg.obj;
                removeDuplicate(tvConDevices);
                templist =  ChangeDevice.getInstance().changeName(tvConDevices,ctx);//查询数据库替换用户自定义id
                if (!templist.isEmpty()){
                    fragment01.tv_conlayout.setVisibility(View.VISIBLE);
                    ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condeviceswithbox_item, templist);
                    fragment01.tvConListView.setAdapter(adapter);
                }else {
                    fragment01.tv_conlayout.setVisibility(View.GONE);
                    ConDevicesAdapter adapter = new ConDevicesAdapter(ctx, R.layout.condeviceswithbox_item, templist);
                    fragment01.tvConListView.setAdapter(adapter);
                }
                break;
            default:
                break;
        }
    }

}
