package com.dkzy.areaparty.phone.fragment06;

/**
 * Created by SnowMonkey on 2017/5/3.
 */

public class RequestFriendObj {
    public String friend_id;
    public String friend_name;
    public int friend_headindex;
    public int friend_filenum;
    public int isagree;


    public RequestFriendObj(String friend_id, String friend_name, int friend_headindex, int friend_filenum, int isagree) {
        this.friend_id = friend_id;
        this.friend_name = friend_name;
        this.friend_headindex = friend_headindex;
        this.friend_filenum = friend_filenum;
        this.isagree = isagree;
    }
    public RequestFriendObj() {}
}
