package com.s2359media.journeytracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.cp;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonUlti;
import com.s2359media.journeytracker.ulti.PreferenceHelper;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LocationBroadcastReceiver",
				"onReceive: received location update");
		RequestQueue queue = Volley.newRequestQueue(context);

		LocationInfo locationInfo = (LocationInfo) intent
				.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
		JourneyModel model = new JourneyModel(
				null,
				locationInfo.lastLat,
				locationInfo.lastLong,
				null, // TODO get location name here
				CommonUlti
						.getDateWithoutTime(locationInfo.lastLocationUpdateTimestamp),
				locationInfo.lastLocationUpdateTimestamp);
		try {
			if (!CommonUlti.compareLatLng(model.getLocation(), context)) {
				Uri uri = context.getContentResolver().insert(
						JourneyContentProvider.CONTENT_URI,
						model.getContentValues());
				int id=Integer.parseInt(uri.getLastPathSegment());
				queue.add(CommonUlti.getAddressByLocation(model.getLocation(), id,context,null));
				PreferenceHelper.getInstance(context).storeLocation(model.getLat(), model.getLng());
			}
		} catch (Exception e) {
			// empty location no need to store;
			e.printStackTrace();
		}
	}
}
