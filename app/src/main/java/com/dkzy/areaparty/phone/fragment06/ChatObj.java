package com.dkzy.areaparty.phone.fragment06;

/**
 * Created by SnowMonkey on 2017/5/3.
 */

public class ChatObj {
    public int id;
    public String sender_id;
    public String receiver_id;
    public String msg;
    public long date;


    public ChatObj(int id, String sender_id, String receiver_id, String msg, long date) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.msg = msg;
        this.date = date;
    }

    public ChatObj(String sender_id, String receiver_id, String msg, long date) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.msg = msg;
        this.date = date;
    }

    public ChatObj() {}
}
