package com.dkzy.areaparty.phone.fragment06.zhuyulin;

/**
 * Created by zhuyulin on 2018/1/14.
 */

public class ReceivedDownloadListFormat {
    private int status;
    private String message;
    private ReceiveDataFormat data;

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

    public ReceiveDataFormat getData() {
        return data;
    }

    public void setData(ReceiveDataFormat data) {
        this.data = data;
    }
}
