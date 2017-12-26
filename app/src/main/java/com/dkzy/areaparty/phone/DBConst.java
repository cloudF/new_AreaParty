package com.dkzy.areaparty.phone;

/**
 * Created by boris on 2016/12/15.
 */

public class DBConst {
    public static final String sharedPicTB      = "pictureTB";
    public static final String sharedMusicTB    = "musicTB";
    public static final String sharedMovieTB    = "movieTB";
    public static final String sharedDocumentTB = "documentTB";
    public static final String sharedRarTB      = "rarTB";
    public static final String sharedOthersTB   = "otherTB";
    public static final String chatTB           = Login.userId;
    public static final String friendTB         = Login.userId + "friend";
    public static final String fileRequestTB         = Login.userId + "transform";

    public static final String tableItem_id     = "_id";
    public static final String tableItem_senderID   = "sender_id";
    public static final String tableItem_receiverID   = "receiver_id";
    public static final String tableItem_msg   = "msg";
    public static final String tableItem_date    = "date";

    public static final String tableItem_friend_id = "friend_id";
    public static final String tableItem_friend_name = "friend_name";
    public static final String tableItem_friend_headindex = "friend_headindex";
    public static final String tableItem_friend_filenum = "friend_filenum";
    public static final String tableItem_isagree = "isagree";

    public static final String tableItem_peer_id = "peer_id";
    public static final String tableItem_file_name = "file_name";
    public static final String tableItem_file_date = "file_date";
    public static final String tableItem_file_size = "file_size";
}
