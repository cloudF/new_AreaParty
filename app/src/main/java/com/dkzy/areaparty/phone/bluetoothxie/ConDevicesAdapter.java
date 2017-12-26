package com.dkzy.areaparty.phone.bluetoothxie;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

import java.util.List;

/**
 * Created by a c e r on 2017/4/10.
 * 已连接设备listView容器
 */

public class ConDevicesAdapter extends ArrayAdapter<DeviceList> {
    private int resourceId;
    List<DeviceList> list;
    private MyDatabaseHelper dbHelper;
    ViewHolder viewHolder;
    Context ctx;
    public ConDevicesAdapter(Context context, int textViewResourceId, List<DeviceList> objects){
        super(context,textViewResourceId,objects);
        list = objects;
        ctx = context;
        resourceId = textViewResourceId;
    }
    public View getView(final int position, View convertView, ViewGroup parent){
        DeviceList deviceList = getItem(position);
        View view;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.devicesName = (TextView) view.findViewById(R.id.condevices_name);
            viewHolder.devicesChecked = (CheckBox) view.findViewById(R.id.condevices_check) ;
            viewHolder.deviceImage = (ImageView) view.findViewById(R.id.condevices_image) ;
            viewHolder.connected = (TextView) view.findViewById(R.id.txt_connected);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (viewHolder.devicesChecked!=null){
            /*dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("Device", null, null, null, null, null ,null);
            if (cursor.moveToFirst()){
                do {
                    String s = cursor.getString(cursor.getColumnIndex("idname"));
                    String name = viewHolder.devicesName.getText().toString();
                    if (s.equals(name)) {
                       if (cursor.getInt(cursor.getColumnIndex("checked"))>0)
                           viewHolder.devicesChecked.setChecked(true);
                        else viewHolder.devicesChecked.setChecked(false);
                        break;
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();*/
            viewHolder.devicesChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String name = list.get(position).getIdName();
//                               showDialog(name);
                                dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("checked",1);
                                db.update("Device",values,"idname = ?",new String[]{list.get(position).getIdName()});
                            }
                        }).start();
//                        Toast.makeText(ctx,"选中", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.e("change","disChecked");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("checked",0);
                                db.update("Device",values,"idname = ?",new String[]{list.get(position).getIdName()});
                            }
                        }).start();
                    }
                }
            });
            /*viewHolder.devicesChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("click","click");
                    *//*if (!viewHolder.devicesChecked.isChecked()){
                        String name = viewHolder.devicesName.getText().toString();
//                        showDialog(name);
                        dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("checked",1);
                        db.update("Device",values,"idname = ?",new String[]{viewHolder.devicesName.getText().toString()});
                        Toast.makeText(ctx,"选中", Toast.LENGTH_SHORT).show();
                    }else {
                        Log.e("change","disChecked");
                        dbHelper = new MyDatabaseHelper(ctx, "Device.db", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("checked",0);
                        db.update("Device",values,"idname = ?",new String[]{viewHolder.devicesName.getText().toString()});
                    }*//*
                }
            });*/
            viewHolder.devicesChecked.setChecked(deviceList.getChecked()>0);

        }
        if (deviceList.getName().equals(deviceList.getIdName()))
            viewHolder.devicesName.setText(deviceList.getName());
        else
            viewHolder.devicesName.setText(deviceList.getIdName()+"("+deviceList.getName()+")");
        switch (deviceList.getType()){
            case 0:
                viewHolder.deviceImage.setImageResource(R.drawable.bluetoothdevicemousered);
                break;
            case 1:
                viewHolder.deviceImage.setImageResource(R.drawable.bluetoothdevicekeyboardred);
                break;
            case 2:
                viewHolder.deviceImage.setImageResource(R.drawable.bluetoothdeviceaudiored);
                break;
            case 3:
                viewHolder.deviceImage.setImageResource(R.drawable.bluetoothdevicegamepadred);
                break;
            default:
                viewHolder.deviceImage.setImageResource(R.drawable.bluetoothdevicered);
        }
        if (deviceList.getConnected()<1){
            viewHolder.connected.setText("已配对");
        }else {
            viewHolder.connected.setText("已连接");
        }
        return view;
    }
    class ViewHolder{
        TextView connected;
        TextView devicesName;
        CheckBox devicesChecked;
        ImageView deviceImage;
    }
   /* public void showDialog(final String name) {
        final EditDialog editDialog = new EditDialog(ctx);
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
                    if (type!=-1)
                    values.put("type",type);
                    values.put("checked",1);
                    db.update("Device",values,"idname = ?",new String[]{name});
                }else {
                    //组装Device数据
                    values.put("name", name);
                    if (!value.isEmpty())
                    values.put("idname", value);
                    values.put("checked", 1);
                    if (type!=-1)
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
    }*/
}