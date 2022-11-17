package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.List;

import com.mobilesalesperson.model.HhCustomer01;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class CustomerSelectionAdapter extends ArrayAdapter<HhCustomer01> implements
		Filterable {

	private final List<HhCustomer01> list;
	private final Activity context;

	private List<HhCustomer01> allModelItemsArray;
	private List<HhCustomer01> filteredModelItemsArray;
	private ModelFilter filter;
	private LayoutInflater inflator;

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ModelFilter();
		}
		return filter;
	}

	static class ViewHolder {
		protected TextView number;
		protected TextView name;
	}

	public CustomerSelectionAdapter(Activity context, List<HhCustomer01> list) {
		super(context, R.layout.single_list_item, list);
		this.context = context;
		this.list = list;
		this.allModelItemsArray = new ArrayList<HhCustomer01>(list);
		this.filteredModelItemsArray = new ArrayList<HhCustomer01>(allModelItemsArray);
		inflator = context.getLayoutInflater();
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.single_list_item, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.number = (TextView) view.findViewById(R.id.txt_Number);
			viewHolder.name = (TextView) view.findViewById(R.id.txt_Name);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.number.setText(list.get(position).getHhCustomer_number());
		holder.name.setText(list.get(position).getHhCustomer_name());
		return view;
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<HhCustomer01> filteredItems = new ArrayList<HhCustomer01>();

				for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
					HhCustomer01 customer = allModelItemsArray.get(i);
					String strNum = customer.getHhCustomer_number();
					String strName = customer.getHhCustomer_name();
					if (strNum.toLowerCase().contains(constraint)|| strName.toLowerCase().contains(constraint))
						filteredItems.add(customer);
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

			filteredModelItemsArray = (ArrayList<HhCustomer01>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
				add(filteredModelItemsArray.get(i));
			notifyDataSetInvalidated();
		}
	}
}
