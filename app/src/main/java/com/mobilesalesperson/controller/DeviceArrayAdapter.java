package com.mobilesalesperson.controller;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobilesalesperson.model.Devices;

/**
 * @author T.SARAVANAN this adapter class used for getting connected bluetooth
 *         device name and address for printing...
 */
public class DeviceArrayAdapter extends ArrayAdapter<Devices> {

	private final List<Devices> list;
	private final Activity context;

	static class ViewHolder {
		protected TextView txtDevName;
		protected TextView txtDevAddr;
	}

	public DeviceArrayAdapter(Activity context, List<Devices> devList) {
		super(context, R.layout.row_layout, devList);
		this.context = context;
		this.list = devList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtDevName = (TextView) view.findViewById(R.id.name);
			viewHolder.txtDevAddr = (TextView) view.findViewById(R.id.addrs);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtDevName.setText(list.get(position).getName());
		holder.txtDevAddr.setText(list.get(position).getDeviceIP());
		return view;
	}
}
