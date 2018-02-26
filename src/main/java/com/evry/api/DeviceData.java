package com.evry.api;

import java.sql.Timestamp;

import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeviceData {
	
	
	private String  deviceId;
	
	private JSONArray deviceData;
	
	
	public JSONArray getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(JSONArray deviceData) {
		this.deviceData = deviceData;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



}
