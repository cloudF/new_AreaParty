package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import java.util.List;

/**
 * Created by boris on 2016/12/16.
 * 指定路径下文件、文件夹信息bean
 */

public class NodeFormat {
    private String path;
    private List<FileInforFormat> files;
    private List<FolderInforFormat> folders;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FileInforFormat> getFiles() {
        return files;
    }

    public void setFiles(List<FileInforFormat> files) {
        this.files = files;
    }

    public List<FolderInforFormat> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderInforFormat> folders) {
        this.folders = folders;
    }
}
