package com.s2359media.journeytracker.adapter;

import java.text.ParseException;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.s2359media.journeytracker.R;
import com.s2359media.journeytracker.model.JourneyModel;
import com.s2359media.journeytracker.ulti.CommonUlti;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Phu Tang (phutang@2359media.com.vn)
 * 
 * 
 */
@Deprecated
public class JourneyAdapter extends BaseAdapter {

	Context mContext;
	List<JourneyModel> mData;
	LayoutInflater inflater;
	RequestQueue queue;

	public JourneyAdapter(Context context, List<JourneyModel> data) {
		mContext = context;
		mData = data;
		inflater = LayoutInflater.from(mContext);
		queue = Volley.newRequestQueue(context);
		updateData();
	}

	public void updateData(List<JourneyModel> data) {
		mData = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.inflater_item, null);
		}
		TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
		TextView tvTime = (TextView) convertView.findViewById(R.id.time);

		tvTitle.setText(mData.get(position).toString());
		try {
			tvTime.setText(CommonUlti.getFormatTime(mData.get(position).getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

	public void updateData() {
		for (JourneyModel model : mData) {
			if (TextUtils.isEmpty(model.getName())) {
				queue.add(CommonUlti.getAddressByLocation(model.getLocation(), model.getId(), mContext, mHandler));
			}
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int id = msg.arg1;
			String name = (String) msg.obj;
			for (JourneyModel model : mData) {
				if (model.getId() == id) {
					model.setName(name);
					notifyDataSetChanged();
				}
			}
		};
	};

}
