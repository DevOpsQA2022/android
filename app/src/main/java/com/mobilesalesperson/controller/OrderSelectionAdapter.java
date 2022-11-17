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

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.Supporter;

public class OrderSelectionAdapter extends ArrayAdapter<HhTran01> implements
		Filterable {

	private final List<HhTran01> list;
	private final Activity context;
	private final MspDBHelper dbhelpher;
	private Supporter supporter;

	private ModelFilter filter;
	private LayoutInflater inflator;
	private List<HhTran01> allModelItemsArray;
	private List<HhTran01> filteredModelItemsArray;

	static class ViewHolder {
		protected TextView txtDocNo;
		protected TextView txtDocRefNo;
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

	public OrderSelectionAdapter(Activity context, List<HhTran01> list,
			MspDBHelper dbhelpher) {
		super(context, R.layout.order_selection_item_list, list);
		this.context = context;
		this.list = list;
		this.dbhelpher = dbhelpher;
		this.supporter = new Supporter(context, dbhelpher);
		allModelItemsArray = new ArrayList<HhTran01>(list);
		filteredModelItemsArray = new ArrayList<HhTran01>(allModelItemsArray);
		inflator = context.getLayoutInflater();
		getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.order_selection_item_list, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtDocNo = (TextView) view
					.findViewById(R.id.txt_OrdListDocNum);
			viewHolder.txtDocRefNo = (TextView) view
					.findViewById(R.id.txt_OrdListDocRef);
			viewHolder.txtCusName = (TextView) view
					.findViewById(R.id.txt_OrdListCusName);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		dbhelpher.openReadableDatabase();
		String cusName = dbhelpher.getCustomerName(list.get(position)
				.getHhTran_customerNumber(), supporter.getCompanyNo());
		dbhelpher.closeDatabase();

		holder.txtDocNo.setText(list.get(position).getHhTran_refNo());
		holder.txtDocRefNo.setText(list.get(position)
				.getHhTran_referenceNumber());
		holder.txtCusName.setText(cusName);

		return view;
	}

	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<HhTran01> filteredItems = new ArrayList<HhTran01>();

				for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
					HhTran01 tranData = allModelItemsArray.get(i);
					String strDocNo = tranData.getHhTran_refNo();
					String strReferenceNo = tranData
							.getHhTran_referenceNumber();

					dbhelpher.openReadableDatabase();
					String cusName = dbhelpher.getCustomerName(
							tranData.getHhTran_customerNumber(),
							supporter.getCompanyNo());
					dbhelpher.closeDatabase();

					if (strDocNo.toLowerCase().contains(constraint)
							|| strReferenceNo.toLowerCase()
									.contains(constraint)
							|| cusName.toLowerCase().contains(constraint))
						filteredItems.add(tranData);
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

			filteredModelItemsArray = (ArrayList<HhTran01>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
				add(filteredModelItemsArray.get(i));
			notifyDataSetInvalidated();
		}
	}

}
