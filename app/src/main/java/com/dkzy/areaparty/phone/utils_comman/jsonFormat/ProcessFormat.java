package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

/**
 * Created by boris on 2016/12/16.
 * 主机进程相关信息
 * id: 进程id
 * name: 进程名
 * cpu: 进程使用的CPU百分比
 * memory: 进程使用的内存(KB)
 * path: 进程主模块完整路径
 */

public class ProcessFormat {
    private int id;
    private int cpu;
    private long memory;
    private String path;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
