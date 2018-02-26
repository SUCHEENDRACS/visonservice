package com.evry.api;

import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class Vision {
	
	@JsonPropertyDescription
	protected int id;
	
	@JsonPropertyDescription
	protected String aisleID;
	
	@JsonPropertyDescription
	protected String imageName;
	
	@JsonPropertyDescription
	protected String imageResults;
	
	@JsonPropertyDescription
	protected Timestamp dateTime;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAisleID() {
		return aisleID;
	}
	public void setAisleID(String aisleID) {
		this.aisleID = aisleID;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageResults() {
		return imageResults;
	}
	public void setImageResults(String imageResults) {
		this.imageResults = imageResults;
	}
	public Timestamp getDateTime() {
		return dateTime;
	}
	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}
	@Override
	public String toString() {
		return "Vision [id=" + id + ", aisleID=" + aisleID + ", imageName=" + imageName + ", imageResults="
				+ imageResults + ", dateTime=" + dateTime.toString() + "]";
	}
	
	
	
	
}
