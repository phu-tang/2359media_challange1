package com.s2359media.journeytracker.model;

import com.google.android.gms.maps.model.LatLng;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.ulti.AccentRemover;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class JourneyModel {

	private int id;
	private double lat;
	private double lng;
	private String name;
	private long date;
	private long time;

	public JourneyModel(String id, double lat, double lon, String name, long date, long time) {
		super();
		this.lat = lat;
		this.lng = lon;
		this.name = name;
		this.date = date;
		this.time = time;
	}

	public JourneyModel(Cursor c) {
		id = c.getInt(c.getColumnIndex(JourneyContentProvider.ID));
		lng = c.getDouble(c.getColumnIndex(JourneyContentProvider.LNG));
		lat = c.getDouble(c.getColumnIndex(JourneyContentProvider.LAT));
		name = c.getString(c.getColumnIndex(JourneyContentProvider.NAME));
		date = c.getLong(c.getColumnIndex(JourneyContentProvider.DATE));
		time = c.getLong(c.getColumnIndex(JourneyContentProvider.TIME));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
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

	public LatLng getLocation() {
		return new LatLng(lat, lng);
	}

	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		if (id != 0) {
			contentValues.put(JourneyContentProvider.ID, id);
		}
		contentValues.put(JourneyContentProvider.LAT, lat);
		contentValues.put(JourneyContentProvider.LNG, lng);
		if (!TextUtils.isEmpty(name)) {
			contentValues.put(JourneyContentProvider.NAME, name);
		}
		contentValues.put(JourneyContentProvider.DATE, date);
		contentValues.put(JourneyContentProvider.TIME, time);
		contentValues.put(JourneyContentProvider.TITLE, AccentRemover.removeAccent(this.toString().toLowerCase()));
		return contentValues;
	}

	@Override
	public String toString() {
		if (TextUtils.isEmpty(name)) {
			return lat + "," + lng;
		}
		return name;
	}

}
