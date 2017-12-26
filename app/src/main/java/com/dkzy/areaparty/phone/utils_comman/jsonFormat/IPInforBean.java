package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import java.io.Serializable;

/**
 * Project Name： FamilyCentralControler
 * Description:  IP信息BEAN
 * Author: boris
 * Time: 2017/3/2 16:24
 */

public class IPInforBean implements Serializable {
    public String ip = "";
    public int port;
    public String function = "";
    public String launch_time_id = "";
    public String mac = "";
    public String name = "";      // 设备名称
    public String nickName = "";  // 虚拟名称

    // 以下三个字段只有逆序列化TV传过来的IP信息的时候才启用
    public String dlnaOk = "";     // "true"或者"false"
    public String miracastOk = ""; // "true"或者"false"
    public String rdpOk = "";      // "true"或者"false"

    public IPInforBean(String ip, int port, String function, String launch_time_id) {
        this.ip = ip;
        this.port = port;
        this.function = function;
        this.launch_time_id = launch_time_id;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

//    public String getMac() {
//        return mac;
//    }
}
