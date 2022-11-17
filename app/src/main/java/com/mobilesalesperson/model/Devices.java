package com.mobilesalesperson.model;

import java.io.Serializable;

public class Devices implements Serializable {

	private String name;
	private String deviceIP;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
}
