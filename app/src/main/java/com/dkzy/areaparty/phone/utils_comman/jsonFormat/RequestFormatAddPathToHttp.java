package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

/**
 * Created by boris on 2017/05/11.
 * 请求消息的格式
 * name: 消息类型
 * command: 操作类型
 * param: 相关参数
 */

public class RequestFormatAddPathToHttp {
    private String name;
    private String command;
    private AddPathToHttpParamBean param;

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

    public AddPathToHttpParamBean getParam() {
        return param;
    }

    public void setParam(AddPathToHttpParamBean param) {
        this.param = param;
    }
}
