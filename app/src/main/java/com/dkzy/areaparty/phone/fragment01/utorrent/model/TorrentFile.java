package com.dkzy.areaparty.phone.fragment01.utorrent.model;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class TorrentFile {
    private String torrentFileName;
    private String torrentPath;
    private boolean isSelected = false;

    public TorrentFile(String torrentFileName, String torrentPath) {
        this.torrentFileName = torrentFileName;
        this.torrentPath = torrentPath;
    }

    public String getTorrentFileName() {
        return torrentFileName;
    }

    public String getTorrentPath() {
        return torrentPath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {

        isSelected = selected;
    }
}
