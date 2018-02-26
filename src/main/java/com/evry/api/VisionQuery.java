package com.evry.api;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class VisionQuery {
	
	@JsonPropertyDescription
	int id;
	
	@Override
	public String toString() {
		return "VisionQuery [id=" + id + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	@JsonPropertyDescription
	Timestamp fromDate;
	
	@JsonPropertyDescription
	Timestamp toDate;
	
	
	
	

}
