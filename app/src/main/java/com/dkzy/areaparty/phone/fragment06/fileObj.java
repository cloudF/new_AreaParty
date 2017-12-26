package com.dkzy.areaparty.phone.fragment06;

/**
 * Created by SnowMonkey on 2017/1/4.
 */

public class fileObj {
    private String fileName;
    private String fileInfo;
    private int fileSize;
    private String senderId;
    private String receiverId;
    private String fileDate;

    public String getSenderId() {
        return senderId;
    }
    public String getFileName() {
        return fileName;
    }
    public String getReceiverId() {
        return receiverId;
    }
    public String getFileInfo() {
        return fileInfo;
    }
    public int getFileSize() {
        return fileSize;
    }
    public String getFileDate() {
        return fileDate;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
}

