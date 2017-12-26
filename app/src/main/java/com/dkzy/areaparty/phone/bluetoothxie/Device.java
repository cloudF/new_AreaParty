package com.dkzy.areaparty.phone.bluetoothxie;

import java.util.List;

/**
 * Created by XIE on 2017/2/24.
 * json类
 */
public class Device {
	public String ip;
	public List<String> getDevice;
	public List<String> getConDevice;
	public String conDevice;
	public String disDevice;
	public List<String> getGetDevice() {
		return getDevice;
	}
	public void setGetDevice(List<String> getDevice) {
		this.getDevice = getDevice;
	}
	public List<String> getGetConDevice() {
		return getConDevice;
	}
	public void setGetConDevice(List<String> getConDevice) {
		this.getConDevice = getConDevice;
	}
	public String getConDevice() {
		return conDevice;
	}
	public void setConDevice(String conDevice) {
		this.conDevice = conDevice;
	}
	public String getDisDevice() {
		return disDevice;
	}
	public void setDisDevice(String disDevice) {
		this.disDevice = disDevice;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
