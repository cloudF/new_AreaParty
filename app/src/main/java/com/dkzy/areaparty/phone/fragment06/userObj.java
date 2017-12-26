package com.dkzy.areaparty.phone.fragment06;

import java.util.List;

import protocol.Data.FileData;

/**
 * Created by SnowMonkey on 2017/1/6.
 */

public class userObj {
    private String userId;
    private String userName;
    private boolean isFriend;
    private int fileNum;
    private boolean isOnline;
    private int headIndex;
    private List<FileData.FileItem> shareFiles;
    private String chatMsg;

    public void setChatMsg(String chatMsg) {
        this.chatMsg = chatMsg;
    }
    public String getChatMsg() {
        return chatMsg;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public boolean getIsFriend() { return  isFriend;}
    public int getFileNum() { return  fileNum;}
    public boolean getIsOnline() { return isOnline; }
    public int getHeadIndex() { return headIndex; }
    public List<FileData.FileItem> getShareFiles() { return shareFiles; }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setIsFriend(boolean isFriend) { this.isFriend = isFriend; }
    public void setFileNum(int fileNum) { this.fileNum = fileNum; }
    public void setIsOnline(boolean isOnline) { this.isOnline = isOnline; }
    public void setHeadIndex(int headIndex) { this.headIndex = headIndex; }
    public void setShareFiles(List<FileData.FileItem> shareFiles ){ this.shareFiles = shareFiles;}
}
