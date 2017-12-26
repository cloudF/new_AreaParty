package com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.object;

/**
 * Created by zhuyulin on 2017/6/7.
 */

public class TorrentFile {
    private String torrentFileName;
    private String torrentPath;

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
}
