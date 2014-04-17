package com.s2359media.journeytracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonUlti;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LocationBroadcastReceiver",
				"onReceive: received location update");

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
				model.setName(CommonUlti.getAddressByLocation(model.getLocation()));
				context.getContentResolver().insert(
						JourneyContentProvider.CONTENT_URI,
						model.getContentValues());
			}
		} catch (Exception e) {
			// empty location no need to store;
			e.printStackTrace();
		}
	}
}
