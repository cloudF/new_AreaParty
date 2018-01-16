package com.dkzy.areaparty.phone.fragment06.zhuyulin;

import java.util.List;

/**
 * Created by zhuyulin on 2018/1/14.
 */

public class ReceiveDataFormat {
    private List<ReceiveData> pause_files;
    private List<ReceiveData> downloading_files;

    public List<ReceiveData> getPause_files() {
        return pause_files;
    }

    public void setPause_files(List<ReceiveData> pause_files) {
        this.pause_files = pause_files;
    }

    public List<ReceiveData> getDownloading_files() {
        return downloading_files;
    }

    public void setDownloading_files(List<ReceiveData> downloading_files) {
        this.downloading_files = downloading_files;
    }
}
