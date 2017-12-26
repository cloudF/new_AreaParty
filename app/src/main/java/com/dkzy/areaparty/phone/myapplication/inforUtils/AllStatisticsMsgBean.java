package com.dkzy.areaparty.phone.myapplication.inforUtils;

import java.util.List;

/**
 * Created by borispaul on 17-7-17.
 */

public class AllStatisticsMsgBean {
    public String type;     // app
    public String userId;
    public String id;       // 手机唯一标识
    public String mac;      // 手机MAC
    public long time;
    public List<StatisticsMsgBean> pcMsg;
    public List<StatisticsMsgBean> tvMsg;

    public AllStatisticsMsgBean(String type, String userId, String id, String mac, long time, List<StatisticsMsgBean> pcMsg, List<StatisticsMsgBean> tvMsg) {
        this.type = type;
        this.userId = userId;
        this.id = id;
        this.mac = mac;
        this.time = time;
        this.pcMsg = pcMsg;
        this.tvMsg = tvMsg;
    }
}
