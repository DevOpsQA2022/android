package com.mobilesalesperson.controller;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.Supporter;

public class TransDetailReportAdapter extends ArrayAdapter<HhTran01> {

	private final List<HhTran01> list;
	private final Activity context;
	private Supporter supporter;
	private MspDBHelper dbHelper;

	static class ViewHolder {
		protected TextView txtSno;
		protected TextView txtItem;
		protected TextView txtPrice;
		protected TextView txtQty;

	}

	public TransDetailReportAdapter(Activity context, List<HhTran01> list) {
		super(context, R.layout.transaction_detail_list_items, list);
		this.context = context;
		this.list = list;
		dbHelper = new MspDBHelper(context);
		supporter = new Supporter(context,dbHelper);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.transaction_detail_list_items,
					null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtSno = (TextView) view
					.findViewById(R.id.txt_TransDetailSerialNum);
			viewHolder.txtItem = (TextView) view
					.findViewById(R.id.txt_TransDetailListItem);
			viewHolder.txtPrice = (TextView) view
					.findViewById(R.id.txt_TransDetailListPrize);
			viewHolder.txtQty = (TextView) view
					.findViewById(R.id.txt_TransDetailListQty);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		String tranType = list.get(position).getHhTran_transType();
		String qty = list.get(position).getHhTran_qty()+"";
		if(tranType.equals("CN")){
			qty = "-"+qty;
		}
		
		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtSno.setText((position + 1) + "");

		holder.txtItem.setText(list.get(position).getHhTran_itemNumber());

		String strTranItmPrz = supporter.getCurrencyFormat(list.get(position)
				.getHhTran_price());

		holder.txtPrice.setText(strTranItmPrz);
		holder.txtQty.setText(qty);

		return view;
	}

}
