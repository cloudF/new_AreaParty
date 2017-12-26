package com.dkzy.areaparty.phone.bluetoothxie;

/**
 * Created by XIE on 2017/5/18.
 */

public class DeviceList {
    private int type;

    private String idName;

    private String name;

    private int checked;

    private int connected;

    public DeviceList(String name,String idName, int checked,int type,int connected){
        this.idName = idName;
        this.checked = checked;
        this.name = name;
        this.type = type;
        this.connected = connected;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public void setIdName(String name){
        this.idName = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setChecked(int checked){
        this.checked = checked;
    }

    public String getIdName() {
        return idName;
    }

    public int getChecked(){
        return checked;
    }

    public int getConnected() {return connected;}

    public void setConnected(int connected) {this.connected = connected;}
}
