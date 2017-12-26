package com.dkzy.areaparty.phone.myapplication.inforUtils;

/**
 * Created by borispaul on 17-7-17.
 */

public class Update_ReceiveMsgBean {
    public boolean isNew = true;
    public String version = "";
    public String url = "";

    public Update_ReceiveMsgBean(boolean isNew, String version, String url) {
        this.isNew = isNew;
        this.version = version;
        this.url = url;
    }

    public Update_ReceiveMsgBean() {
    }

    public boolean isEmpty() {
        return isNew && version.equals("") && url.equals("");
    }
}
