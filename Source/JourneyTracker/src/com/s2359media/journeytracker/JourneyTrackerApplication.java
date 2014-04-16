package com.s2359media.journeytracker;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.s2359media.journeytracker.ulti.CommonConstant;

import android.app.Application;
import android.util.Log;

public class JourneyTrackerApplication extends Application {
	 @Override
	    public void onCreate() {
	        super.onCreate();
	        Log.d("onCreateApplication", "JourneyTrackerApplication");
	        LocationLibrary.showDebugOutput(true);

	        try {
	            LocationLibrary.initialiseLibrary(getBaseContext(), CommonConstant.REFRESH_TIME_INTERVAL, (int)CommonConstant.REFRESH_TIME_INTERVAL*2, getPackageName());
	        }
	        catch (UnsupportedOperationException ex) {
	            Log.d("TestApplication", "UnsupportedOperationException thrown - the device doesn't have any location providers");
	        }
	    }
}
