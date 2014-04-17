package com.s2359media.journeytracker.ulti;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;

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
			return false;
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
		double distance= 6366000 * c;
		Log.d("distance", ""+distance);
		return distance;
	}

	private static String handleData(JSONObject data) {
		if (data == null)
			return null;
		JSONArray arrayData = data.optJSONArray("results");
		if (arrayData != null && arrayData.length() > 0) {
			JSONObject temp = arrayData.optJSONObject(0);
			return temp.optString("formatted_address");
		}
		return null;
	}

	public static JsonObjectRequest getAddressByLocation(LatLng latlng, final int id, final Context context,final Handler handler) {
		String requestLink = "http://maps.google.com/maps/api/geocode/json?latlng="
				+ latlng.latitude + "," + latlng.longitude + "&sensor=false";
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.GET, requestLink, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						String name=handleData(response);
						if(handler!=null){
							Message msg=new Message();
							msg.arg1=id;
							msg.obj=name;
							handler.sendMessage(msg);
						}
						String sItemUri= JourneyContentProvider.URL_GETITEM+id;
						Uri itemUri=Uri.parse(sItemUri);
						Cursor c=context.getContentResolver().query(itemUri, null, null, null, null);
						if(c!=null && c.moveToNext()){
							JourneyModel model=new JourneyModel(c);
							model.setName(name);
							context.getContentResolver().update(itemUri, model.getContentValues(), null, null);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});

		return jsObjRequest;

		// return handleData(jsonObject);
	}
}
