package com.dkzy.areaparty.phone.fragment06.zhuyulin;

/**
 * Created by zhuyulin on 2018/1/14.
 */

public class DownloadBean {
    private String name;
    private String path;
    private String id;
    private String state;

    public DownloadBean(ReceiveData data, String state) {
        this.name = data.getName();
        this.path = data.getPath();
        this.id = data.getId();
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public ReceiveData getReceiveData(){
        return new ReceiveData(name,path,id);
    }
}
