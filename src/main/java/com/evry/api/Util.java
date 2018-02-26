package com.evry.api;

import java.util.HashMap;

public class Util {
	private static HashMap<String, String> configParams = new HashMap<String, String>();
	
	public static String getConfigParam(String key) {
		return configParams.get(key);
	}
	public static void addConfigParam(String key, String value){
		configParams.put(key, value);
	}

}
