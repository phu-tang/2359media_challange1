package com.s2359media.journeytracker.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.cp;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.app.MainActivity;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonConstant;
import com.s2359media.journeytracker.ulti.CommonUlti;
import com.s2359media.journeytracker.ulti.PreferenceHelper;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LocationBroadcastReceiver", "onReceive: received location update");
		RequestQueue queue = Volley.newRequestQueue(context);

		LocationInfo locationInfo = (LocationInfo) intent
				.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
		JourneyModel model = new JourneyModel(null, locationInfo.lastLat, locationInfo.lastLong, null,
				CommonUlti.getDateWithoutTime(locationInfo.lastLocationUpdateTimestamp),
				locationInfo.lastLocationUpdateTimestamp);
		try {
			if (!CommonUlti.compareLatLng(model.getLocation(), context)) {
				ShowNotifcation(locationInfo, context);
				Uri uri = context.getContentResolver().insert(JourneyContentProvider.CONTENT_URI, model.getContentValues());
				int id = Integer.parseInt(uri.getLastPathSegment());
				queue.add(CommonUlti.getAddressByLocation(model.getLocation(), id, context, null));
				PreferenceHelper.getInstance(context).storeLocation(model.getLat(), model.getLng());
			}
		} catch (Exception e) {
			// empty location no need to store;
			e.printStackTrace();
		}
	}

	private void ShowNotifcation(LocationInfo locationInfo, Context context) {

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent contentIntent = new Intent(context, MainActivity.class);
		PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(context.getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.new_location)))
				.setContentText(LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true));
		mBuilder.setContentIntent(contentPendingIntent);
		mNotificationManager.notify(CommonConstant.NOTIFICATION_ID, mBuilder.build());

	}
}
