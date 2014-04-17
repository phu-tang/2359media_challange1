package com.s2359media.journeytracker.ulti;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceHelper {
	// default value
	public static String DEFAULT_STRING = "";
	public static final int DEFAULT_NUMBER = 0;
	public static final boolean DEFAULT_BOOLEAN = false;

	private static final String PREFERENCE_NAME = "Social help";
	protected static SharedPreferences settingPreferences;
	protected static PreferenceHelper instance = new PreferenceHelper();

	public static PreferenceHelper getInstance(Context context) {
		init(context);
		return instance;
	}

	private static void putString(String key, String value) {
		Editor editor = settingPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key, String defaultValue) {
		return settingPreferences.getString(key, defaultValue);
	}

	private static void init(Context context) {
		if (settingPreferences == null) {
			settingPreferences = context.getSharedPreferences(PREFERENCE_NAME,
					Context.MODE_PRIVATE);
		}
	}

	private static void putInteger(String key, int value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	private static Long getLong(String key, long defaultValue) {
		if (settingPreferences != null) {
			return settingPreferences.getLong(key, defaultValue);
		} else
			return defaultValue;
	}

	private static void putLong(String key, long value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}
	
	private static double getDouble(String key, double defaultValue) {
		if (settingPreferences != null) {
			return (double)settingPreferences.getFloat(key, (float) defaultValue);
		} else
			return defaultValue;
	}

	private static void putDouble(String key, double value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putFloat(key, (float) value);
			editor.commit();
		}
	}

	private static Integer getInteger(String key, int defaultValue) {
		if (settingPreferences != null) {
			return settingPreferences.getInt(key, defaultValue);
		} else
			return defaultValue;
	}

	private static void putBoolean(String key, boolean value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}

	private static Boolean getBoolean(String key, boolean defaultValue) {
		if (settingPreferences != null) {
			return settingPreferences.getBoolean(key, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public void storeLocation(double lat,double lng){
		putDouble(CommonConstant.PREF_LAT, lat);
		putDouble(CommonConstant.PREF_LNG, lng);
	}
	
	public LatLng getLastLatLng(){
		return new LatLng(getDouble(CommonConstant.PREF_LAT, 0), getDouble(CommonConstant.PREF_LNG, 0));
	}
}