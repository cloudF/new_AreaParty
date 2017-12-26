package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import java.util.List;

/**
 * Created by boris on 2016/12/16.
 * 主机监控数据类
 * CPU数据(百分比)、已用内存数据(MB)、总内存数据(MB)、上传网速(KB)、下行网速(KB)、进程及其相关信息列表
 */

public class MonitorData {
    private int cpu;
    private double memory_used;
    private double memory_total;
    private double memory_available;
    private double net_up;
    private double net_down;
    private List<ProcessFormat> processes;

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public double getMemory_used() {
        return memory_used;
    }

    public void setMemory_used(double memory_used) {
        this.memory_used = memory_used;
    }

    public double getMemory_total() {
        return memory_total;
    }

    public void setMemory_total(double memory_total) {
        this.memory_total = memory_total;
    }

    public double getNet_up() {
        return net_up;
    }

    public void setNet_up(double net_up) {
        this.net_up = net_up;
    }

    public double getNet_down() {
        return net_down;
    }

    public void setNet_down(double net_down) {
        this.net_down = net_down;
    }

    public List<ProcessFormat> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessFormat> processes) {
        this.processes = processes;
    }

    public double getMemory_available() {
        return memory_available;
    }

    public void setMemory_available(double memory_available) {
        this.memory_available = memory_available;
    }
}
