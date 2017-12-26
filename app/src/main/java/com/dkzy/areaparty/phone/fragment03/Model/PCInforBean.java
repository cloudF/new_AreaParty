package com.dkzy.areaparty.phone.fragment03.Model;

/**
 * Created by borispaul on 17-6-28.
 */

public class PCInforBean {
    public String systemVersion = "";
    public String systemType = "";
    public String totalmemory = "";
    public String cpuName  = "";
    public String totalStorage = "";
    public String freeStorage = "";
    public String pcName = "";
    public String pcDes = "";
    public String workGroup = "";

    public boolean isEmpty() {
        return systemVersion.equals("") && systemType.equals("") && totalStorage.equals("") && freeStorage.equals("") &&
               totalmemory.equals("") && cpuName.equals("");
    }
}
