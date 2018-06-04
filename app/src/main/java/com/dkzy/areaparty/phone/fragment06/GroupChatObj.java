package com.dkzy.areaparty.phone.fragment06;

/**
 * Created by SnowMonkey on 2017/5/3.
 */

public class GroupChatObj {
    public int id;
    public String sender_id;
    public String receiver_id;
    public String msg;
    public long date;
    public String group_id;


    public GroupChatObj(int id, String sender_id, String receiver_id, String msg, long date,String group_id) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.msg = msg;
        this.date = date;
        this.group_id = group_id;
    }

    public GroupChatObj(String sender_id, String receiver_id, String msg, long date,String group_id) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.msg = msg;
        this.date = date;
        this.group_id=group_id;
    }

    public GroupChatObj() {}
}
