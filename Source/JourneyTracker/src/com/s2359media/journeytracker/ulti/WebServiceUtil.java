package com.s2359media.journeytracker.ulti;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

public class WebServiceUtil {
	public static final String TAG = WebServiceUtil.class.getSimpleName();




	public static JSONObject sendGet(String requestlink) throws Exception {
		return sendGet(requestlink, null);
	}

	public static String sendGet_String(String requestlink, Map<String, String> cookies) throws Exception {
		HttpsURLConnection con = null;
		int responseCode = 0;
		try {
			String url = requestlink;

			URL obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");
			if (cookies != null) {
				con.setRequestProperty("Cookie", builbCookiesString(cookies));
			}

			responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch (Exception e) {
			JSONObject result = new JSONObject();
			result.put("responseCode", responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			result.put("error_message", response);
			// Toast.makeText(, response, Toast.LENGTH_LONG).s
			e.printStackTrace();
			return result.toString();
		}
	}

	public static JSONObject sendGet(String requestlink, Map<String, String> cookies) throws Exception {
		HttpsURLConnection con = null;
		int responseCode = 0;
		try {
			String url = requestlink;

			URL obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");
			if (cookies != null) {
				con.setRequestProperty("Cookie", builbCookiesString(cookies));
			}

			responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			if (response.length() > 0) {
				JSONObject result = new JSONObject(response.toString());
				result.put("responseCode", responseCode);
				return result;
			} else {
				JSONObject result = new JSONObject();
				result.put("responseCode", responseCode);
				return result;
			}
		} catch (Exception e) {
			JSONObject result = new JSONObject();
			result.put("responseCode", responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			result.put("error_message", response);
			// Toast.makeText(, response, Toast.LENGTH_LONG).s
			e.printStackTrace();
			return result;
		}
	}

	public static JSONObject sendPost(String requestlink, JSONObject headerData) throws Exception {
		return sendPost(requestlink, headerData, null);
	}

	// HTTP POST request
	public static JSONObject sendPost(String requestlink, JSONObject headerData, Map<String, String> cookies) throws Exception {
		int responseCode = 0;
		HttpsURLConnection con = null;
		try {
			// headerData.put("allow_sending_email", 1);
			String url = requestlink;
			URL obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();

			// log

			if (cookies != null) {
				con.setRequestProperty("Cookie", builbCookiesString(cookies));
			}

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			// Send post request
			con.setDoOutput(true);
			if (headerData != null) {
				// for object
//				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//				wr.writeBytes(headerData.toString());
				// for text
				OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
				wr.write(headerData.toString());
				wr.flush();
				wr.close();
			}
			responseCode = con.getResponseCode();
			// System.out.println("\nSending 'POST' request to URL : " + url);
			// System.out.println("Response Code : " + responseCode);
			String cookie = con.getHeaderField("Set-Cookie");

			// System.out.println("Response content : " + con.getc);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			if (response.length() > 0) {
				JSONObject result = new JSONObject(response.toString());
				result.put("responseCode", responseCode);
				result.put("cookie", cookie);
				return result;
			} else {
				JSONObject result = new JSONObject();
				String location = con.getHeaderField("Location");
				if(location!=null)
					result.put("data", location);
				result.put("responseCode", responseCode);
				result.put("cookie", cookie);
				return result;
			}
		} catch (Exception e) {
			JSONObject result = new JSONObject();
			result.put("responseCode", responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			result.put("error_message", response);
			// Toast.makeText(, response, Toast.LENGTH_LONG).s
			e.printStackTrace();
			return result;
		}
	}


	private static String builbCookiesString(Map<String, String> cookies) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, String> cookieEntry : cookies.entrySet()) {
			stringBuilder.append(cookieEntry.getKey()).append("=").append(cookieEntry.getValue()).append(";");
		}
		String cookieStr = stringBuilder.toString();
		return cookieStr.substring(0, cookieStr.length() - 1);
	}


	public static void pushCertificateSSL() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		} catch (Exception e) {

		}
	}
}
