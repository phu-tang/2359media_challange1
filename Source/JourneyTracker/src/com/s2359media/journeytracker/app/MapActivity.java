package com.s2359media.journeytracker.app;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.adapter.JourneyAdapter;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonConstant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * @author Phu Tang (phutang@2359media.com.vn)
 * 
 * 
 */
public class MapActivity extends FragmentActivity {
	GoogleMap googleMap;
	long currentSelectDate;
	List<JourneyModel> listJourneyModels;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		FragmentManager manager = getSupportFragmentManager();
		SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.map);
		googleMap = mapFragment.getMap();
		googleMap.setMyLocationEnabled(true);
		mContext=this;
		initData();
	}

	private void initData() {
		currentSelectDate = getIntent().getLongExtra(CommonConstant.KEY_CURRENT_DATE, 0);
		new InitJourneyData().execute();
	}

	private void getJourneyFromDb() {
		String url = JourneyContentProvider.URL + "/" + currentSelectDate;
		Uri uriData = Uri.parse(url);
		Cursor c = getContentResolver().query(uriData, null, null, null, null);
		if (c != null) {
			listJourneyModels = new ArrayList<JourneyModel>();
			while (c.moveToNext()) {
				listJourneyModels.add(new JourneyModel(c));
			}
			c.close();
		}
	}

	private void addDataToMap() {
		if (listJourneyModels != null && !listJourneyModels.isEmpty()) {
			for (JourneyModel model : listJourneyModels) {
				MarkerOptions marker = new MarkerOptions().position(model.getLocation()).title(model.getName())
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				googleMap.addMarker(marker);
			}
		}
	}
	
	private class InitJourneyData extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(mContext);
			if (!isFinishing()) {
				dialog.setMessage(getString(R.string.loading));
				dialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			getJourneyFromDb();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			addDataToMap();
			if (listJourneyModels != null && !listJourneyModels.isEmpty()) {
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(listJourneyModels.get(0).getLocation(), 15);
				googleMap.animateCamera(cameraUpdate);
			}
		}
	}
}
