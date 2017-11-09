/*
 * =========================================================================
 * Copyright (c) 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 * =========================================================================
 * @file UsernameAdapter.java
 */

package layout.face.sidface.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ei.opensrp.indonesia.R;

public class PersonAdapter extends BaseAdapter {

	private Context mContext;
	String[] mNames;

	public PersonAdapter(Context context, String[] names) {
		mContext = context;
		mNames = names;
	}
	
	@Override
	public int getCount() {
		return mNames.length;
	}
	
	@Override
	public Object getItem(int position) {
		return mNames[position];
	}
	
	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View gridView;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) { // if it's not recycled, initialize some attributes
		
			new View(mContext);
//			gridView = inflater.inflate(R.layout.usernames, null);
			gridView = inflater.inflate(R.layout.persons, null);

		} else {
			gridView = convertView;
		}
		
		TextView tv = (TextView) gridView.findViewById(R.id.textView1);
		tv.setBackgroundColor(Color.BLACK);
		tv.setText(" " + (position + 1) + ".   " + mNames[position]);
		
		return gridView;
	}
	
}
