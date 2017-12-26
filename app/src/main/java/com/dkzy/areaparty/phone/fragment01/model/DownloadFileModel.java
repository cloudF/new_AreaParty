package com.dkzy.areaparty.phone.fragment01.model;

import java.io.Serializable;

/**
 * Created by borispaul on 17-5-9.
 */

public class DownloadFileModel implements Serializable {
    private static final long serialVersionUID = 2072893447591548402L;

    private String name;
    private String url;
    private long createTime;

    public DownloadFileModel() {
    }

    public DownloadFileModel(String name, String url, long createTime) {
        this.name = name;
        this.url = url;
        this.createTime = createTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
