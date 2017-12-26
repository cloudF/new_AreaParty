package com.dkzy.areaparty.phone.fragment01.utorrent.model;

/**
 * Created by zhuyulin on 2017/10/18.
 */

public class Torrent {
    private String hash;
    private String name;
    private String size;
    private String status;
    private String downloadSpeed;
    private String ETA; //剩余时间
    private int pgress;

    public Torrent(String hash, String name, String size, String status, String downloadSpeed, String ETA, int pgress) {
        this.hash = hash;
        this.name = name;
        this.size = size;
        this.status = status;
        this.downloadSpeed = downloadSpeed;
        this.ETA = ETA;
        this.pgress = pgress;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getDownloadSpeed() {
        return downloadSpeed;
    }

    public String getETA() {
        return ETA;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public void setETA(String ETA) {
        this.ETA = ETA;
    }

    public int getPgress() {
        return pgress;
    }

    public void setPgress(int pgress) {
        this.pgress = pgress;
    }
}
