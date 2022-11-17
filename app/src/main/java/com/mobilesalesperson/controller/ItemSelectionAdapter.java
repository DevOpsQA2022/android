package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.List;

import com.mobilesalesperson.controller.CustomerSelectionAdapter.ViewHolder;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.Manf_Number01;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ItemSelectionAdapter extends ArrayAdapter<HhItem01> implements
		Filterable {

	private final List<HhItem01> list;
	private final Activity context;

	private List<HhItem01> allModelItemsArray;
	private List<HhItem01> filteredModelItemsArray;
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
		protected TextView itemNumber;
		protected TextView name;
		
	}

	public ItemSelectionAdapter(Activity context, List<HhItem01> list) {
		super(context, R.layout.single_list_item, list);
		this.context = context;
		this.list = list;
		this.allModelItemsArray = new ArrayList<HhItem01>(list);
		this.filteredModelItemsArray = new ArrayList<HhItem01>(allModelItemsArray);
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
			viewHolder.itemNumber = (TextView) view.findViewById(R.id.txt_Number);
			viewHolder.name = (TextView) view.findViewById(R.id.txt_Name);
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.itemNumber.setText(list.get(position).getHhItem_number());
		holder.name.setText(list.get(position).getHhItem_description());
		
		return view;
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<HhItem01> filteredItems = new ArrayList<HhItem01>();
           
				for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
					HhItem01 item = allModelItemsArray.get(i);
					
					String strNum = item.getHhItem_number();
					String strDesc = item.getHhItem_description();
					
					if (strNum.toLowerCase().contains(constraint)|| strDesc.toLowerCase().contains(constraint))
						filteredItems.add(item);
					  
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

			filteredModelItemsArray = (ArrayList<HhItem01>) results.values;
			
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
				add(filteredModelItemsArray.get(i));
			notifyDataSetInvalidated();
			
		}
	}
}
