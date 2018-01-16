package com.dkzy.areaparty.phone.fragment06.zhuyulin;

/**
 * Created by zhuyulin on 2018/1/15.
 */

public class ReceiveDownloadProcessFormat {
    private int status;
    private String message;
    private DownloadProcess data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DownloadProcess getData() {
        return data;
    }

    public void setData(DownloadProcess data) {
        this.data = data;
    }
}
