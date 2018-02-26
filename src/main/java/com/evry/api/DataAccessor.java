package com.evry.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evry.db.DBConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;


/**
 * Class to access iot data from the database
 * 
 * @author ravindra_d
 *
 */
public class DataAccessor {

	// private String[] parameter;
	private static ArrayList<Aisle> aisle;
	private static DataAccessor instance;
	private SecureRestClient client;

	public static DataAccessor getInstance() throws SQLException, NamingException {
		if (instance == null) {
			instance = new DataAccessor();
		}
		return instance;
	}

	private DataAccessor() throws SQLException, NamingException {
		client = new SecureRestClient();
		aisle = DBConnector.getInstance().getAllAisle();
	}

	public boolean addDeviceDataToWorkerQueue(Aisle aisle) {
		try {
			long fromDate = -1;
			long toDate = -1;
			
			
			System.out.println("Last queried timestamp " + aisle.getLastQueriedTimeStamp());
			fromDate = aisle.getLastQueriedTimeStamp();
			if (fromDate == -1) {
				fromDate = System.currentTimeMillis();
			}
			toDate = System.currentTimeMillis();
			aisle.setLastQueriedTimeStamp(toDate);
			//Ravi : Quick fix to check whether vision algorithm is running or not
			/*Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			System.out.println("todays time in date time " + today.toString());
			fromDate = today.getTimeInMillis(); 
			System.out.println("todays time in timestamp " + fromDate);
			System.out.println("current time in timestamp " + fromDate);
			
			aisle.setLastQueriedTimeStamp(toDate);*/
			//Note : Used deviceId in the callback url since we are not using it yet.
			//To-Do : create a new field for deviceid
			String api = Util.getConfigParam("iotDataFetchUrl") + "/" + aisle.getApplicationId() + "/"
					+ aisle.getTenantId() + "/" + aisle.getCallabckURL();
			String output = client.getMethod(api, fromDate, toDate);
			if(output == null) {
				return false;
			}
			//String output = client.getMethod("http://172.18.0.28:8080/core/devicemanagement/api/v2.0/dataStreaming/1/1/1921681210");
			JSONArray array = new JSONArray(output);
			System.out.println("The data received from the iot is " + array.toString());
			DeviceData data = new DeviceData();
			data.setDeviceId(aisle.getDeviceId());
			data.setDeviceData(array);
			CoreWorkerManager.getInstance().addMessageRequest(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public void addAisle(Aisle obj) {
		aisle.add(obj);
	}

}
