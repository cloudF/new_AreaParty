package com.dkzy.areaparty.phone.fragment06;

import java.io.Serializable;
import java.util.List;

import protocol.Data.ChatData;

/**
 * Created by SnowMonkey on 2017/5/5.
 */

public class myChatList implements Serializable {
    private List<ChatData.ChatItem> list;

    public List<ChatData.ChatItem> getList() {
        return list;
    }

    public void setList(List<ChatData.ChatItem> list) {
        this.list = list;
    }
}
