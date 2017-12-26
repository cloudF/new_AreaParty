package com.dkzy.areaparty.phone.fragment02.Model;

/**
 * Created by poxiaoge on 2016/12/24.
 */

public class MediaItem {
    private String name;
    private String pathName;
    private String thumbnailurl;
    private String type;   // 视频, 音频, 图片, 文件夹
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean equals(MediaItem item) {
        return item.name.equals(this.name) && item.pathName.equals(this.pathName);
    }

    public MediaItem(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
