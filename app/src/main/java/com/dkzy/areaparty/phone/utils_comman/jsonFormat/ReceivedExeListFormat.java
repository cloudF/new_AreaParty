package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import com.dkzy.areaparty.phone.OrderConst;

import java.util.List;

/**
 * Created by boris on 2016/12/16.
 * 服务器端返回的数据格式类型
 * status: 状态码(成功：200  失败：404)
 * message: 附加信息
 * data: 服务器返回的信息类
 */

public class ReceivedExeListFormat {
    private int status;
    private String message;
    private List<ExeInformat> data;

    public ReceivedExeListFormat() {
        this.status = OrderConst.failure;
    }

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

    public List<ExeInformat> getData() {
        return data;
    }

    public void setData(List<ExeInformat> data) {
        this.data = data;
    }
}
