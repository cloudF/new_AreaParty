package com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.adapter;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class TorrentFile {
    private String torrentFileName;
    private String torrentPath;
    private boolean isShow;
    private boolean isChecked;

    public TorrentFile(String torrentFileName, String torrentPath) {
        this.torrentFileName = torrentFileName;
        this.torrentPath = torrentPath;
        this.isShow = false;
        this.isChecked = false;
    }

    public String getTorrentFileName() {
        return torrentFileName;
    }

    public String getTorrentPath() {
        return torrentPath;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
