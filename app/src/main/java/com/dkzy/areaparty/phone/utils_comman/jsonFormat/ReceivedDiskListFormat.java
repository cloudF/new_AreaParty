package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import java.util.List;

/**
 * Created by boris on 2016/12/16.
 * PC端返回的磁盘列表信息
 * status: 状态码(成功：200  失败：404)
 * message: 附加信息
 * data: 服务器返回的信息类
 */

public class ReceivedDiskListFormat {
    private int status;
    private String message;
    private List<DiskInformat> data;

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


    public List<DiskInformat> getData() {
        return data;
    }

    public void setData(List<DiskInformat> data) {
        this.data = data;
    }
}
