package com.dkzy.areaparty.phone.fragment01.model;

import java.text.SimpleDateFormat;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/2/19 21:38
 */

public class SharedfileBean {
    public int id;
    public String name;
    public String path;
    public int size;
    public String des;
    public String timeStr;   // 获取数据时保存
    public long timeLong;    // 存储数据时保存
    public String url;
    public String pwd;


    public SharedfileBean(int id, String name, String path, int size, String des, String time) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.size = size;
        this.timeStr = time;
        this.des = des;
    }

    public SharedfileBean(String name, String path, int size, String des, long time, String url, String pwd) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.des = des;
        this.timeLong = time;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        this.timeStr = format.format(timeLong);
        this.url = url;
        this.pwd = pwd;
    }

    public SharedfileBean(String name, int size, String des, long timeLong) {
        this.name = name;
        this.size = size;
        this.des = des;
        this.timeLong = timeLong;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        this.timeStr = format.format(timeLong);
    }

    public SharedfileBean() {}

    @Override
    public String toString() {
        return "SharedfileBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", des='" + des + '\'' +
                ", timeStr='" + timeStr + '\'' +
                ", timeLong=" + timeLong +
                ", url='" + url + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
