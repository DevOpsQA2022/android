package com.mobilesalesperson.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesalesperson.controller.ImportSummary.SummaryResult;

public class SummaryResultAdapter extends ArrayAdapter<SummaryResult> {

	Context context;
	int layoutResourceId;
	ArrayList<SummaryResult> data = null;

	public SummaryResultAdapter(Context context, int layoutResourceId,
			ArrayList<SummaryResult> mylist) {
		super(context, layoutResourceId, mylist);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = mylist;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ResultHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new ResultHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			holder.imgResult = (ImageView) row.findViewById(R.id.imgIcon);

			row.setTag(holder);
		} else {
			holder = (ResultHolder) row.getTag();
		}

		SummaryResult weather = data.get(position);
		holder.txtTitle.setText(weather.name);
		boolean result = weather.result;
		if (result == false) {
			holder.imgResult.setImageResource(R.drawable.delete);
		} else {
			holder.imgResult.setImageResource(R.drawable.menu_ok);
		}

		return row;
	}

	static class ResultHolder {
		TextView txtTitle;
		ImageView imgResult;
	}
}