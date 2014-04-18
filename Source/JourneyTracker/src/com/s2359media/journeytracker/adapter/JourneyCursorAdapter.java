package com.s2359media.journeytracker.adapter;

import java.text.ParseException;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonUlti;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Phu Tang (phutang@2359media.com.vn)
 * 
 * 
 */
public class JourneyCursorAdapter extends CursorAdapter {
	LayoutInflater inflater;
	RequestQueue queue;
	public JourneyCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflater = LayoutInflater.from(context);
		queue = Volley.newRequestQueue(context);
	}

	@Override
	public void bindView(View view, Context arg1, Cursor arg2) {
		if (arg2 != null) {
			JourneyModel model = new JourneyModel(arg2);
			TextView tvTitle = (TextView) view.findViewById(R.id.title);
			TextView tvTime = (TextView) view.findViewById(R.id.time);

			tvTitle.setText(model.toString());
			try {
				tvTime.setText(CommonUlti.getFormatTime(model.getTime()));
				if(TextUtils.isEmpty(model.getName())){
					queue.add(CommonUlti.getAddressByLocation(model.getLocation(), model.getId(), arg1, null));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.inflater_item, null);
	}

}
