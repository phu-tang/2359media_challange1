package com.s2359media.journeytracker.app;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.drive.internal.i;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.adapter.JourneyAdapter;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonConstant;
import com.s2359media.journeytracker.ulti.CommonUlti;

public class MainActivity extends ActionBarActivity {

	private Context mContext;
	private ListView list;
	private JourneyAdapter adapter;
	private EditText etSearch;

	private List<JourneyModel> listJourneyModelsOrginal;
	private List<JourneyModel> listJourneyModelsSearch;
	private Long currentSelectDate;
	private String[] slistDates;
	private List<Long> listDates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		currentSelectDate = (long) 0;
		list = (ListView) findViewById(R.id.list_item);
		new InitData().execute();
		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(getTextSearch());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if (listDates.isEmpty()) {
				ShowDialogNoData();
			} else {
				showChooseDateDialog();
			}
			return true;
		}
		if (id == R.id.action_refresh) {
			new InitData().execute();
		}

		if (id == R.id.action_location_found) {
			LocationLibrary.forceLocationUpdate(mContext);
		}
		
		if(id==R.id.action_show_map){
			Intent intent=new Intent(this,MapActivity.class);
			intent.putExtra(CommonConstant.KEY_CURRENT_DATE, currentSelectDate);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class InitData extends AsyncTask<Void, Void, List<Integer>> {
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
		protected List<Integer> doInBackground(Void... params) {
			String url = JourneyContentProvider.URL;
			Uri uriDate = Uri.parse(url);
			Cursor c = getContentResolver().query(uriDate, null, null, null, null);
			listDates = new ArrayList<Long>();
			if (c != null) {
				slistDates = new String[c.getCount()];
				int i = 0;
				while (c.moveToNext()) {
					long date = c.getLong(c.getColumnIndex(JourneyContentProvider.DATE));
					listDates.add(date);
					try {
						slistDates[i] = CommonUlti.getFormatDate(date);
					} catch (ParseException e) {
						slistDates[i] = "" + date;
					}
					i++;
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Integer> result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (listDates.isEmpty()) {
				list.setVisibility(View.GONE);
				findViewById(R.id.tvNodata).setVisibility(View.VISIBLE);
				return;
			}
			list.setVisibility(View.VISIBLE);
			findViewById(R.id.tvNodata).setVisibility(View.GONE);
			if (currentSelectDate == 0) {
				currentSelectDate = listDates.get(0);
			}
			getJourneyFromDb(slistDates[0]);
		}
	}

	private void showChooseDateDialog() {
		new AlertDialog.Builder(this).setSingleChoiceItems(slistDates, -1, null)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
						if (selectedPosition >= 0) {
							currentSelectDate = listDates.get(selectedPosition);
							getJourneyFromDb(slistDates[selectedPosition]);
						}
					}
				}).setTitle(getString(R.string.title_select_date)).show();
	}

	private void getJourneyFromDb(String title) {
		String url = JourneyContentProvider.URL + "/" + currentSelectDate;
		Uri uriData = Uri.parse(url);
		Cursor c = getContentResolver().query(uriData, null, null, null, null);
		if (c != null) {
			listJourneyModelsOrginal = new ArrayList<JourneyModel>();
			while (c.moveToNext()) {
				listJourneyModelsOrginal.add(new JourneyModel(c));
			}
		}
		if (adapter == null) {
			adapter = new JourneyAdapter(mContext, listJourneyModelsOrginal);
			list.setAdapter(adapter);
		} else {
			adapter.updateData(listJourneyModelsOrginal);
			adapter.notifyDataSetChanged();
		}
	}

	private void ShowDialogNoData() {
		new AlertDialog.Builder(mContext).setTitle(android.R.string.dialog_alert_title).setMessage(R.string.nodata).show();
	}

	private TextWatcher getTextSearch() {
		return new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (adapter != null) {
					if (TextUtils.isEmpty(s)) {
						adapter.updateData(listJourneyModelsOrginal);
					} else {
						listJourneyModelsSearch = new ArrayList<JourneyModel>();
						for (JourneyModel model : listJourneyModelsOrginal) {
							if (model.toString().contains(s)) {
								listJourneyModelsSearch.add(model);
							}
						}
						adapter.updateData(listJourneyModelsSearch);
					}

					adapter.notifyDataSetChanged();
				}
			}
		};
	}

}
