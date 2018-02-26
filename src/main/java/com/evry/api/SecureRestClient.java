package com.evry.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;


public class SecureRestClient {

	private static final String AUTH_SERVER_URI = Util.getConfigParam("authServerUrl");

	private static final String QPM_ACCESS_TOKEN = "?access_token=";
	
	private static final String FROM_ACCESS_TOKEN = "&fromDate=";
	
	private static final String TO_ACCESS_TOKEN = "&toDate=";

	private static final String NO_RESPONSE = "Unable to get the response from =";

	//static final String userName = Util.getConfigParam("loginWebApiUserName");
	static final String userName = "IotApiUser";
	static final String pwd = "IotApiUser@1234";
	//static final String pwd = Util.getConfigParam("loginWebApiPassword");



	/*
	 * Prepare HTTP Headers.
	 */
/*	private static HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}*/

	/*
	 * Add HTTP Authorization header, using Basic-Authentication to send
	 * client-credentials.
	 */
	private static String getHeadersWithClientCredentials() {
		String plainClientCredentials = "my-trusted-client:secret";
		String base64ClientCredentials = new String(
				Base64.encodeBase64(plainClientCredentials.getBytes()));
		return base64ClientCredentials;

		
	}

	/*
	 * Send a POST request [on /oauth/token] to get an access-token, which will
	 * then be send with each request.
	 */
	/**
	 * @param userName
	 * @param pwd
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	private static AuthTokenInfo sendTokenRequest(String userName, String pwd) {

		String oAuthPwdGrant = new StringBuilder(
				"?grant_type=password&username=").append(userName)
				.append("&password=").append(pwd).toString();
		
		
		String base64CredentialString = getHeadersWithClientCredentials();
		
		URL url;
		AuthTokenInfo tokenInfo = null;
		try {
			//url = new URL(AUTH_SERVER_URI + oAuthPwdGrant);
			url = new URL("http://172.18.0.28:8080/core/oauth/token"+ oAuthPwdGrant);
			
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Basic " + base64CredentialString);
		conn.setRequestProperty("Accept", "application/json");
		
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		
		
		InputStream is = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);

		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((numCharsRead = isr.read(charArray)) > 0) {
			sb.append(charArray, 0, numCharsRead);
		}
		String result = sb.toString();
		System.out.println("Token info is " + result);
		JSONObject obj =  new JSONObject(result);
		if (obj != null) {
			tokenInfo = new AuthTokenInfo();
			tokenInfo.setAccess_token((String) obj.get("access_token"));
			tokenInfo.setToken_type((String) obj.get("token_type"));
			tokenInfo.setRefresh_token((String) obj.get("refresh_token"));
			// tokenInfo.setExpires_in((int)
			// Integer.parseInt(map.get("expires_in").toString()));
			tokenInfo.setScope((String) obj.get("scope"));
		} else {
			System.out.println("No user exists");
		}
		
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tokenInfo;
	}

	/*
	 * Send a GET request to get a specific user.
	 */
	public String getMethod(String api, long fromDate, long toDate) {
		String output = "";
		String dataReceived = null;
		String accessToken = null;
		try {
			AuthTokenInfo tokenInfo = sendTokenRequest(userName, pwd);
			if(tokenInfo != null)
				accessToken = tokenInfo.getAccess_token();
			String securedApi = api + QPM_ACCESS_TOKEN	+ accessToken;
			
			if (fromDate != -1) {
				securedApi = securedApi+ FROM_ACCESS_TOKEN + Long.toString(fromDate);
				securedApi = securedApi+ TO_ACCESS_TOKEN + Long.toString(toDate);		
			}
			URL url = new URL(securedApi);

			System.out.println("waiting to get the response from =" + securedApi);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() == 706) {
				System.out.println("Object not present/found =" + securedApi);
				return null;
			}
			System.out.println("Connection response is " + conn.getResponseCode());

			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
				System.out.println(NO_RESPONSE + securedApi);
				return null;			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			while ((output = br.readLine()) != null) {
				System.out.println(output);
				dataReceived = output;
			}

			conn.disconnect();
			return dataReceived;
		} catch (IOException e) {
			System.out.println("Failed to get the url " + api);
			System.out.println("Failed to get the response" + e.toString());
			return dataReceived;
		}
	}

	/*public ResponseEntity<String> updateMethod(String api, String data,
			String contentType, String methodType) {
		String accessToken = null;
		try {

			AuthTokenInfo tokenInfo = sendTokenRequest(userName, pwd);
			if(tokenInfo != null)
				accessToken = tokenInfo.getAccess_token();
			String securedApi = api + QPM_ACCESS_TOKEN
					+ accessToken;
			URL url = new URL(securedApi);

			System.out.println("waiting to get the response from =" + securedApi);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(methodType);
			conn.setRequestProperty("Content-Type", contentType);

			String input = data;
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() == 707) {

				return ResponseEntity.status(707).body(
						(conn.getResponseMessage()));
			}

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK
					&& conn.getResponseCode() != HttpURLConnection.HTTP_CREATED
					&& conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
				System.out.println(NO_RESPONSE + api);
			}

		} catch (IOException e) {
			System.out.println("Failed to post to the url " + api);
			System.out.println("Failed to post the response "+ e);
		}
		return ResponseEntity.status(200).body("Success Response");
	}*/


	public static void main(String []args) {
		//AuthTokenInfo info = SecureRestClient.sendTokenRequest(userName, pwd);
		//System.out.println("Token info is " + info.toString());
		SecureRestClient client = new SecureRestClient();
		String dataReceived = client.getMethod("http://172.18.0.28:8080/core/devicemanagement/api/v2.0/dataStreaming/1/1/1921681210", -1, -1);
		System.out.println("Data recieved is " + dataReceived);
	}
}