package com.dkzy.areaparty.phone.myapplication.inforUtils;

/**
 * Created by borispaul on 17-7-17.
 */

public class Update_ReceiveMsgBean {
    public boolean isNew = true;
    public String version = "";
    public String url = "";
    public String updataContent = "";
    public int isforceNew = -1;

    public Update_ReceiveMsgBean(boolean isNew, String version, String url,String updateContent) {
        this.isNew = isNew;
        this.version = version;
        this.url = url;
        this.updataContent = updateContent;
    }

    public Update_ReceiveMsgBean() {
    }

    public boolean isEmpty() {
        return isNew && version.equals("") && url.equals("");
    }
}
