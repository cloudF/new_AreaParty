package com.dkzy.areaparty.phone.myapplication.inforUtils;

/**
 * Created by borispaul on 17-7-17.
 */

public class StatisticsMsgBean {
    public String type;
    public String id;  // 名字
    public String mac;
    public long time;

    public StatisticsMsgBean(String type, String id, String mac, long time) {
        this.type = type;
        this.id = id;
        this.mac = mac;
        this.time = time;
    }
}
