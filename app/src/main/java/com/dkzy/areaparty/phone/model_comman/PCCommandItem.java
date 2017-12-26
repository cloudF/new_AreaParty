package com.dkzy.areaparty.phone.model_comman;

import java.util.Map;

/**
 * Created by poxiaoge on 2016/12/30.
 */

public class PCCommandItem {
    private String name;
    private String command;
    public Map<String,String> param;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
