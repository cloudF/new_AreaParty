package com.dkzy.areaparty.phone.fragment03.blueTooth;

public class BlueDevice {
    private String address;
    private String name;
    private String status;
    private boolean connected;

    public BlueDevice(String address, String name, String status, boolean connected) {
        this.address = address;
        this.name = name;
        this.status = status;
        this.connected = connected;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
