package com.s2359media.journeytracker.app;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.adapter.JourneyAdapter;
import com.s2359media.journeytracker.adapter.JourneyCursorAdapter;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.AccentRemover;
import com.s2359media.journeytracker.ulti.CommonConstant;
import com.s2359media.journeytracker.ulti.CommonUlti;

public class MainActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

	private Context mContext;
	private ListView list;
	// private JourneyAdapter adapter;
	private EditText etSearch;
	private JourneyCursorAdapter cursorAdapter;
	private LoaderManager loaderManager;

	private Long currentSelectDate;
	private String[] slistDates;
	private List<Long> listDates;
	CursorLoader cursorLoader;

	private final int ID_GET_JOURNEY_BY_DATE = 1;
	private final int ID_SEARCH = 2;
	private final String KEYWORD = "keyword";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		loaderManager = getSupportLoaderManager();
		currentSelectDate = (long) 0;
		list = (ListView) findViewById(R.id.list_item);
		new InitData().execute();
		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(getTextSearch());
		cancelNotification();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void cancelNotification() {
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(CommonConstant.NOTIFICATION_ID);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if (listDates.isEmpty()) {
				showDialogNoData();
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

		if (id == R.id.action_show_map) {
			Intent intent = new Intent(this, MapActivity.class);
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
			cursorAdapter = new JourneyCursorAdapter(mContext, null, 0);
			list.setAdapter(cursorAdapter);
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
				c.close();
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

	// private class InitJourneyData extends AsyncTask<Void, Void, Void> {
	// ProgressDialog dialog;
	//
	// @Override
	// protected void onPreExecute() {
	// dialog = new ProgressDialog(mContext);
	// if (!isFinishing()) {
	// dialog.setMessage(getString(R.string.loading));
	// dialog.show();
	// }
	// }
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// String url = JourneyContentProvider.URL + "/" + currentSelectDate;
	// Uri uriData = Uri.parse(url);
	// Cursor c = getContentResolver().query(uriData, null, null, null, null);
	// if (c != null) {
	// listJourneyModelsOrginal = new ArrayList<JourneyModel>();
	// while (c.moveToNext()) {
	// listJourneyModelsOrginal.add(new JourneyModel(c));
	// }
	// }
	// c.close();
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// if (dialog != null && dialog.isShowing()) {
	// dialog.dismiss();
	// }
	// // if (adapter == null) {
	// // adapter = new JourneyAdapter(mContext, listJourneyModelsOrginal);
	// // // list.setAdapter(adapter);
	// // } else {
	// // adapter.updateData(listJourneyModelsOrginal);
	// // adapter.notifyDataSetChanged();
	// // }
	// }
	// }

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
		// new InitJourneyData().execute();
		loaderManager.restartLoader(ID_GET_JOURNEY_BY_DATE, null, this);
	}

	private void showDialogNoData() {
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
				if (cursorAdapter != null) {
					if (TextUtils.isEmpty(s)) {
						// adapter.updateData(listJourneyModelsOrginal);
						loaderManager.restartLoader(ID_GET_JOURNEY_BY_DATE, null, MainActivity.this);
					} else {
						// listJourneyModelsSearch = new
						// ArrayList<JourneyModel>();
						// for (JourneyModel model : listJourneyModelsOrginal) {
						String input = AccentRemover.removeAccent(s.toString().toLowerCase());
						Bundle bundle = new Bundle();
						bundle.putString(KEYWORD, input);
						// loaderManager.initLoader(ID_SEARCH, bundle,
						// MainActivity.this);
						loaderManager.restartLoader(ID_SEARCH, bundle, MainActivity.this);
						// String name =
						// AccentRemover.removeAccent(model.toString().toLowerCase());
						// if (name.contains(input)) {
						// listJourneyModelsSearch.add(model);
						// }
						// }
						// adapter.updateData(listJourneyModelsSearch);
					}

					// adapter.notifyDataSetChanged();
				}
			}
		};
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String url;
		Uri uriData;
		switch (arg0) {
		case ID_GET_JOURNEY_BY_DATE:
			if (currentSelectDate == -1) {
				return null;
			}
			url = JourneyContentProvider.URL + "/" + currentSelectDate;
			uriData = Uri.parse(url);
			cursorLoader = new CursorLoader(mContext, uriData, null, null, null, null);
			break;
		case ID_SEARCH:
			String keyword = arg1.getString(KEYWORD);
			url = JourneyContentProvider.URL_SEARCH + currentSelectDate;
			uriData = Uri.parse(url);
			cursorLoader = new CursorLoader(mContext, uriData, null, null, new String[] { keyword }, null);
			break;
		default:
			break;
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		arg1.setNotificationUri(mContext.getContentResolver(), JourneyContentProvider.CONTENT_URI);
		if (cursorAdapter == null) {
			cursorAdapter = new JourneyCursorAdapter(mContext, arg1, 0);
			list.setAdapter(cursorAdapter);
		} else {
			cursorAdapter.swapCursor(arg1);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
