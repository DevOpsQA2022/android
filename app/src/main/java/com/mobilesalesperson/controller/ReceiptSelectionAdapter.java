package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mobilesalesperson.model.HhReceipt01;

public class ReceiptSelectionAdapter extends ArrayAdapter<HhReceipt01>
		implements Filterable {

	private final List<HhReceipt01> list;
	private final Activity context;

	private ModelFilter filter;
	private LayoutInflater inflator;
	private List<HhReceipt01> allModelItemsArray;
	private List<HhReceipt01> filteredModelItemsArray;

	static class ViewHolder {
		protected TextView txtChkNo;
		protected TextView txtCusNo;
		protected TextView txtCusName;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ModelFilter();
		}
		return filter;
	}

	public int getCount() {
		return list.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public ReceiptSelectionAdapter(Activity context, List<HhReceipt01> list) {
		super(context, R.layout.receipt_selection_item_list, list);
		this.context = context;
		this.list = list;
		allModelItemsArray = new ArrayList<HhReceipt01>(list);
		filteredModelItemsArray = new ArrayList<HhReceipt01>(allModelItemsArray);
		inflator = context.getLayoutInflater();
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.receipt_selection_item_list, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtChkNo = (TextView) view
					.findViewById(R.id.txt_ReciptListChkNo);
			viewHolder.txtCusNo = (TextView) view
					.findViewById(R.id.txt_ReciptListCusNo);
			viewHolder.txtCusName = (TextView) view
					.findViewById(R.id.txt_ReciptListCusName);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtChkNo
				.setText(list.get(position).getHhReceipt_receiptnumber());
		holder.txtCusNo.setText(list.get(position)
				.getHhReceipt_customernumber());
		holder.txtCusName.setText(list.get(position)
				.getHhReceipt_customername());

		return view;
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<HhReceipt01> filteredItems = new ArrayList<HhReceipt01>();

				for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
					HhReceipt01 reciptData = allModelItemsArray.get(i);
					String strChkNo = reciptData.getHhReceipt_receiptnumber();
					String strCustNo = reciptData.getHhReceipt_customernumber();
					String cusName = reciptData.getHhReceipt_customername();

					if (strChkNo.toLowerCase().contains(constraint)
							|| strCustNo.toLowerCase().contains(constraint)
							|| cusName.toLowerCase().contains(constraint))
						filteredItems.add(reciptData);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					result.values = allModelItemsArray;
					result.count = allModelItemsArray.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filteredModelItemsArray = (ArrayList<HhReceipt01>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
				add(filteredModelItemsArray.get(i));
			notifyDataSetInvalidated();
		}
	}

}
