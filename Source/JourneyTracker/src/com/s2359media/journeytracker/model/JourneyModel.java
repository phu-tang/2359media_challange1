package com.s2359media.journeytracker.model;

import com.s2359media.journeytracker.database.JourneyContentProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class JourneyModel {

	private String id;
	private double lat;
	private double lon;
	private String name;
	private long date;
	private long time;

	public JourneyModel(String id, double lat, double lon, String name,
			long date, long time) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.date = date;
		this.time = time;
	}
	
	public JourneyModel (Cursor cursor){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		if (!TextUtils.isEmpty(id)) {
			contentValues.put(JourneyContentProvider.ID, id);
		}
		contentValues.put(JourneyContentProvider.LAT, lat);
		contentValues.put(JourneyContentProvider.LNG, lon);
		if (!TextUtils.isEmpty(name)) {
			contentValues.put(JourneyContentProvider.NAME, name);
		}
		contentValues.put(JourneyContentProvider.DATE, date);
		contentValues.put(JourneyContentProvider.TIME, time);
		return contentValues;
	}

}
