package com.evry.service;

import java.sql.Timestamp;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evry.api.Aisle;
import com.evry.api.Vision;
import com.evry.api.VisionQuery;
import com.evry.db.DBConnector;
import com.google.gson.Gson;
import com.google.gson.JsonElement;


@Path("/AisleMonitor")
public class ServiceLayer {

	@POST
	@Path("/addAisle")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAisleData(Aisle aisle) {
		aisle.print();
		System.out.println(aisle.toString());
		DBConnector.getInstance().addAisle(aisle.getUrl(), aisle.getDetails(), aisle.getCallabckURL(), 
				aisle.getAlertTimeout(), aisle.getThreashold(), aisle.getApplicationId(), aisle.getTenantId());
		return Response.ok().build();
	}
	
	
	@POST
	@Path("/getAllAisle")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAisleData() {
		
		try {
			ArrayList<Aisle> aisleList = DBConnector.getInstance().getAllAisle();
			Gson gson = new Gson();
			JSONArray array = new JSONArray();
			for (Aisle obj : aisleList) {
				JSONObject temp = new JSONObject(gson.toJson(obj));
				array.put(temp);
			}
			return Response.ok(array.toString(), MediaType.APPLICATION_JSON).build();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
	}
	
	@POST
	@Path("/getVisionResults")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVisionResults(VisionQuery vision) {
		try {
	
		System.out.println("Query parameters are " + vision.toString());
		ArrayList<Vision> visionList = DBConnector.getInstance().getAllVisionResultsForAisleWithinDate(vision.getId(), vision.getFromDate(), vision.getToDate());
		Gson gson = new Gson();
		JSONArray array = new JSONArray();
		for (Vision obj : visionList) {
			JSONObject temp = new JSONObject(gson.toJson(obj));
			array.put(temp);
		}
		return Response.ok(array.toString(), MediaType.APPLICATION_JSON).build();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	
	}
	
	@POST
	@Path("/getAllVisionResults")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllVisionResults() {
		try {
		ArrayList<Vision> visionList = DBConnector.getInstance().getAllVisionResults();
		
		Gson gson = new Gson();
		JSONArray array = new JSONArray();
		for (Vision obj : visionList) {
			JSONObject temp = new JSONObject(gson.toJson(obj));
			array.put(temp);
		}
		return Response.ok(array.toString(), MediaType.APPLICATION_JSON).build();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	
	
	

}
