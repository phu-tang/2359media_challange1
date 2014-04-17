package com.s2359media.journeytracker.app;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.adapter.JourneyAdapter;
import com.s2359media.journeytracker.database.JourneyContentProvider;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonUlti;

public class MainActivity extends ActionBarActivity {

	private List<Long> listDates;
	private String[] slistDates;
	private Long currentSelectDate;
	private Context mContext;
	private ListView list;
	private JourneyAdapter adapter;
	private List<JourneyModel> liJourneyModels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		list=(ListView) findViewById(R.id.list_item);
		new InitData().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showChooseDateDialog();
			return true;
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
			Cursor c = getContentResolver().query(uriDate, null, null, null,
					null);
			listDates = new ArrayList<Long>();
			if (c != null) {
				slistDates =new String[c.getCount()];
				int i=0;
				while (c.moveToNext()) {
					long date=c.getLong(c.getColumnIndex(JourneyContentProvider.DATE));
					listDates.add(date);
					try {
						slistDates[i]=CommonUlti.getFormatDate(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						slistDates[i]=""+date;
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
		}
	}
	
	private void showChooseDateDialog(){
		new AlertDialog.Builder(this)
        .setSingleChoiceItems(slistDates, -1, null)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                currentSelectDate= listDates.get(selectedPosition);
                getJourneyFromDb();
            }
        })
        .setTitle(getString(R.string.title_select_date))
        .show();
	}
	
	private void getJourneyFromDb(){
		String url = JourneyContentProvider.URL+"/"+currentSelectDate;
        Uri uriData = Uri.parse(url);
        Cursor c = getContentResolver().query(uriData, null, null, null,
				null);
        if(c!=null){
        	liJourneyModels=new ArrayList<JourneyModel>();
        	while(c.moveToNext()){
        		liJourneyModels.add(new JourneyModel(c));
        	}
        }
        if(adapter==null){
        	adapter=new JourneyAdapter(mContext, liJourneyModels);
        	list.setAdapter(adapter);
        } else {
        	adapter.updateData(liJourneyModels);
        	adapter.notifyDataSetChanged();
        }
	}

}
