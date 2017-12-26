package com.dkzy.areaparty.phone.fragment01.utorrent.model;

/**
 * Created by zhuyulin on 2017/10/24.
 */

public class TFile {
    private String name;
    private String size;
    private String progress;
    private int priority;
    private String downloaded;

    public TFile(String name, String size, String downloaded, String progress, int priority) {
        this.name = name;
        this.size = size;
        this.progress = progress;
        this.priority = priority;
        this.downloaded = downloaded;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getProgress() {
        return progress;
    }

    public int getPriority() {
        return priority;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setTFile(TFile f){
        this.name = f.getName();
        this.priority = f.getPriority();
        this.size = f.getSize();
        this.downloaded = f.getDownloaded();
        this.progress = f.getProgress();
    }
}
