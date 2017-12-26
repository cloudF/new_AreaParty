package com.dkzy.areaparty.phone.fragment06;

import java.io.Serializable;
import java.util.List;

import protocol.Data.FileData;

/**
 * Created by SnowMonkey on 2017/3/30.
 */

public class myFileList implements Serializable {

    private List<FileData.FileItem> list;

    public List<FileData.FileItem> getList() {
        return list;
    }

    public void setList(List<FileData.FileItem> list) {
        this.list = list;
    }

}