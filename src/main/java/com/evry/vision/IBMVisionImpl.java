package com.evry.vision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evry.api.DeviceData;
import com.evry.api.Util;
import com.evry.db.DBConnector;
//import com.evry.db.FileReaderMongoDB;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

public class IBMVisionImpl implements VisionInterface, Runnable {
	private VisualRecognition service;
	// String classifierId;
	DeviceData data;

	public IBMVisionImpl(DeviceData obj) {
		data = obj;
		initialize();
	}

	public IBMVisionImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int initialize() {
		service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		//service.setApiKey(Util.getConfigParam("apikey"));
		service.setApiKey("f79fdb4ff1c1950edfa49d41da9d4c7c5beff3b5");

		return 0;
	}

	@Override
	public String classifyImages(String imageName, FileInputStream stream) {
		byte[] buffer;
		try {
			buffer = IOUtils.toByteArray(stream);
			return classifyImages(imageName, buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public synchronized String classifyImages(String imageName, byte[] stream) {
		if (stream != null) {
			System.out.println("Inside classify images " + imageName);
			ClassifyImagesOptions options = new ClassifyImagesOptions.Builder().images(stream, imageName)
					.classifierIds(Util.getConfigParam("classifier_id")).build();
			
			/*ClassifyImagesOptions options = new ClassifyImagesOptions.Builder().images(stream, imageName)
					.classifierIds("person_count_889517945").build();*/
			
			VisualClassification result = service.classify(options).execute();
			System.out.println("Results is " + result.toString());
			if (result != null) {
				return getResultClass(result.toString());
			}
		}
		System.out.println("Not able to get the file named as " + imageName + " in the mongo db");
		return null;
	}

	private String getResultClass(String jsonData) {
		try {
			JSONObject obj = new JSONObject(jsonData);
			if (obj == null)
				return null;
			System.out.println("JSON Contents are " + obj.toString());
			JSONArray images = obj.getJSONArray("images");
			if (images == null)
				return null;
			System.out.println("Contents of the json image array are" + images.toString());
			JSONObject obj1 = images.getJSONObject(0);
			if (obj1 == null)
				return null;
			System.out.println("Contents of the array index are " + obj1.toString());
			JSONArray classifierArray = obj1.getJSONArray("classifiers");
			if(classifierArray.length() == 0) return null;
			JSONObject obj2 = classifierArray.getJSONObject(0);
			if (obj2 == null)
				return null;
			JSONArray obj3 = obj2.getJSONArray("classes");
			if (obj3 == null)
				return null;
			System.out.println("The value  of the result is " + obj3.toString());
			JSONObject obj4 = obj3.getJSONObject(0);
			if (obj4 == null)
				return null;
			System.out.println("Class and results are " + obj4.getDouble("score") + "Crowd classification is "
					+ obj4.getString("class"));
			return obj4.getString("class");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		IBMVisionImpl impl = new IBMVisionImpl();
		impl.initialize();

		/*
		 * String jsonData = "{\"images\": [" + "{" + "\"classifiers\"" + ":" +
		 * "[" + "{" + "\"classes\"" + ":" + "[" + "{" + "\"class\"" + ":" +
		 * "\"one_person\"" + "," + "\"score\":" + " 0.523462" + "}" + "]" + ","
		 * + "\"classifier_id\"" + ":" + "\"person_count_889517945\"" + "," +
		 * "\"name\"" + ":" + "\"person_count\"" + "}" + "]," + "\"image\"" +
		 * ":" + "\"IMG_20171025_181025.png\"" + "}" + "]," +
		 * "\"images_processed\"" + ":" + "1" + "}"; try { JSONObject obj = new
		 * JSONObject(jsonData); System.out.println("JSON Contents are " +
		 * obj.toString()); JSONArray images = obj.getJSONArray("images");
		 * System.out.println("Contents of the json image array are" +
		 * images.toString()); JSONObject obj1 = images.getJSONObject(0);
		 * System.out.println("Contents of the array index are "+
		 * obj1.toString()); JSONArray classifierArray =
		 * obj1.getJSONArray("classifiers"); JSONObject obj2 =
		 * classifierArray.getJSONObject(0); JSONArray obj3 =
		 * obj2.getJSONArray("classes"); System.out.println(
		 * "The value  of the result is " + obj3.toString()); JSONObject obj4 =
		 * obj3.getJSONObject(0); System.out.println("Class and results are " +
		 * obj4.getDouble("score") + "Crowd classification is "+
		 * obj4.getString("class")); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } System.out.println(
		 * "JSON Contents are " );
		 */

		
			try {
				FileInputStream stream = new FileInputStream("D:\\images\\1511328353829_192.168.1.210_IMG20171025175218.png");
				//byte[] databytes = FileReaderMongoDB.getInstance().getFileStream("D:\\images\\1511328353829_192.168.1.210_IMG20171025175218.png");
				String result = impl.classifyImages("img.png", stream);
				System.out.println("Result is " + result);
		
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
	}

	public int processMessasage(JSONObject json) {
		System.out.println("Object is " + json.toString());
		return 0;
	}

	@Override
	public void run() {
		try {
			JSONArray array = data.getDeviceData();
			JSONObject object;
			int i = 0;
			while ((object = array.getJSONObject(i)) != null) {
				System.out.println("Details of the json are deviceid:" + object.getString("deviceid") + " File name is "
						+ object.getString("fileName") + " Time is " + object.getString("time"));
				i++;
				String result = FetchAndRunVisionAlgorithm(object);
				if (result != null) {
					DBConnector.getInstance().addVisionResult("192.168.1.210", getDate(object.getString("time")),
							object.getString("fileName"), result, 1);
				}
			}
			System.out.println("Out of the while loop");
		} catch (Exception err) {
			System.out.println("Exception while processing CoreWorkerThread object." + err);
		}
	}

	private synchronized String FetchAndRunVisionAlgorithm(JSONObject object) {
		String fileName;
		try {
			fileName = object.getString("fileName");
			System.out.println("Running visual recogntion algorithm for the file " + fileName);
			FileInputStream stream = new FileInputStream("D:\\images\\" + fileName);
			//byte[] databytes = FileReaderMongoDB.getInstance().getFileStream(fileName);
			String result = classifyImages(fileName, stream);
			stream.close();
			return result;
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private Timestamp getDate(String time) {
		// String[] fields = fileName.split("_");
		Timestamp ts = new Timestamp(Long.valueOf(time));
		return ts;
	}

}
