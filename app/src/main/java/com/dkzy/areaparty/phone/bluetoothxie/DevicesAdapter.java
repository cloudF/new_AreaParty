package com.dkzy.areaparty.phone.bluetoothxie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

import java.util.List;

/**
 * Created by XIE on 2017/3/13.
 * 可连接设备列表listView容器
 */

public class DevicesAdapter extends ArrayAdapter<DeviceList> {

    private int resourceId;
    public DevicesAdapter(Context context, int textViewResourceId, List<DeviceList> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        DeviceList devicesList = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.devicesName = (TextView) view.findViewById(R.id.devices_name);
            viewHolder.devicesImage = (ImageView) view.findViewById(R.id.devices_image);
            view.setTag(viewHolder);
        }else {
             view = convertView;
             viewHolder = (ViewHolder) view.getTag();
        }
        if (devicesList.getName().equals(devicesList.getIdName()))
            viewHolder.devicesName.setText(devicesList.getName());
        else
            viewHolder.devicesName.setText(devicesList.getIdName()+"("+devicesList.getName()+")");
        switch (devicesList.getType()){
            case 0:
                viewHolder.devicesImage.setImageResource(R.drawable.bluetoothdevicemouse);
                break;
            case 1:
                viewHolder.devicesImage.setImageResource(R.drawable.bluetoothdevicekeyboard);
                break;
            case 2:
                viewHolder.devicesImage.setImageResource(R.drawable.bluetoothdeviceaudio);
                break;
            case 3:
                viewHolder.devicesImage.setImageResource(R.drawable.bluetoothdevicegamepad);
                break;
            default:
                viewHolder.devicesImage.setImageResource(R.drawable.bluetoothdevice);
        }
        return view;
    }
    class ViewHolder{
        TextView devicesName;
        ImageView devicesImage;
    }
}
