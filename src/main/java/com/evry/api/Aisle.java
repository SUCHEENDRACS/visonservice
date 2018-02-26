package com.evry.api;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class Aisle {
	
	
	@JsonPropertyDescription
	protected String details;
	
	@JsonPropertyDescription
	protected int id;
	
	@JsonPropertyDescription
	protected String deviceId;

	
	@JsonPropertyDescription
	protected long lastQueriedTimeStamp;
	
	public long getLastQueriedTimeStamp() {
		return lastQueriedTimeStamp;
	}

	public void setLastQueriedTimeStamp(long lastQueriedTimeStamp) {
		this.lastQueriedTimeStamp = lastQueriedTimeStamp;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonPropertyDescription
	protected String callabckURL;
	
	@JsonPropertyDescription
	protected int alertTimeout;
	
	@JsonPropertyDescription
	protected int threashold;
	
	@JsonPropertyDescription
	protected int applicationId;
	
	@JsonPropertyDescription
	protected int tenantId;
	
	@JsonPropertyDescription
    protected String url;
	
	@JsonPropertyDescription
	private String name;
	
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getCallabckURL() {
		return callabckURL;
	}

	public void setCallabckURL(String callabckURL) {
		this.callabckURL = callabckURL;
	}

	public int getAlertTimeout() {
		return alertTimeout;
	}

	public void setAlertTimeout(int alertTimeout) {
		this.alertTimeout = alertTimeout;
	}

	public int getThreashold() {
		return threashold;
	}

	public void setThreashold(int threashold) {
		this.threashold = threashold;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	
	public Aisle() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

		
	public void print() {
		System.out.print("url : " + url);
		System.out.print("Name : " + name);
		
	}

	@Override
	public String toString() {
		return "details:" + details + ", callabckURL:" + callabckURL + ", alertTimeout:" + alertTimeout
				+ ", threashold:" + threashold + ", applicationId:" + applicationId + ", tenantId:" + tenantId
				+ ", url:" + url + ", name:" + name + "";
	}
	
	

}
