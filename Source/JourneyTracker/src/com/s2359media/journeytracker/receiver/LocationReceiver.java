package com.s2359media.journeytracker.receiver;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.internal.ca;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonConstant;
import com.s2359media.journeytracker.ulti.CommonUlti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("LocationBroadcastReceiver",
				"onReceive: received location update");

		LocationInfo locationInfo = (LocationInfo) intent
				.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
		JourneyModel model = new JourneyModel(
				null,
				locationInfo.lastLat,
				locationInfo.lastLong,
				"location name", // TODO get location name here
				CommonUlti
						.getDateWithoutTime(locationInfo.lastLocationUpdateTimestamp),
				locationInfo.lastLocationUpdateTimestamp);

		Uri uri = context.getContentResolver()
				.insert(JourneyContentProvider.CONTENT_URI, model.getContentValues());

		Toast.makeText(context,"LocationBroadcastReceiver: " + uri.toString() + " inserted!", Toast.LENGTH_LONG)
				.show();

	}
}
