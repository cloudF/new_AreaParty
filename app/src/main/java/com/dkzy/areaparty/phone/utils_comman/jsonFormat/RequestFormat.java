package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

/**
 * Created by boris on 2016/12/16.
 * 请求消息的格式
 * name: 消息类型
 * command: 操作类型
 * param: 相关参数
 */

public class RequestFormat {
    private String name;
    private String command;
    private String param;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
