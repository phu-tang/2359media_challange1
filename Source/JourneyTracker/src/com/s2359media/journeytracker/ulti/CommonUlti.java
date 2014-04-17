package com.s2359media.journeytracker.ulti;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Phu Tang
 * 
 */
public class CommonUlti {

	public static long getDateWithoutTime(long time) {
		long millisInDay = 60 * 60 * 24 * 1000;

		long dateOnly = (time / millisInDay) * millisInDay;
		return dateOnly;
	}

	public static String getFormatDate(long time) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(
				CommonConstant.FORMAT_DATE);

		Date today = new Date(time);

		return formatter.format(today);

	}

	public static String getFormatTime(long time) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(
				CommonConstant.FORMAT_TIME);

		Date today = new Date(time);

		return formatter.format(today);

	}

	/**
	 * @param latLng
	 * @param context
	 * @return return true if this location same as the previous location
	 * @throws Exception
	 */
	public static boolean compareLatLng(LatLng latLng, Context context)
			throws Exception {
		if (isEmptyLocation(latLng)) {
			throw new Exception("empty location");
		}
		LatLng lastLatlng = PreferenceHelper.getInstance(context)
				.getLastLatLng();
		if (isEmptyLocation(lastLatlng)) {
			return true;
		}
		if (distance(lastLatlng, latLng) < 1000) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isEmptyLocation(LatLng latLng) {
		if (latLng.latitude == 0 && latLng.longitude == 0) {
			return true;
		}
		return false;
	}

	private static double distance(LatLng StartP, LatLng EndP) {
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return 6366000 * c;
	}

	private static String handleData(JSONObject data) {
		if (data == null)
			return null;
		JSONArray arrayData = data.optJSONArray("results");
		if (arrayData.length() > 0) {
			JSONObject temp = arrayData.optJSONObject(0);
			return temp.optString("formatted_address");
		}
		return null;
	}

	public static String getAddressByLocation(LatLng latlng) {
		String requestLink = "http://maps.google.com/maps/api/geocode/json?latlng="
				+ latlng.latitude + "," + latlng.longitude + "&sensor=false";

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = WebServiceUtil.sendGet(requestLink, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return handleData(jsonObject);
	}
}
