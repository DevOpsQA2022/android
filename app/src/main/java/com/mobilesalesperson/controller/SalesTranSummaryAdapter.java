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

public class SalesTranSummaryAdapter extends ArrayAdapter<HhTran01> {

	private final List<HhTran01> list;
	private final Activity context;
	private Supporter supporter;
	private MspDBHelper dbHelper;

	static class ViewHolder {
		protected TextView txtOrdNo;
		protected TextView txtCusName;
		protected TextView txtPaymentNo;
		protected TextView txtAmount;
	}

	public SalesTranSummaryAdapter(Activity context, List<HhTran01> list) {
		super(context, R.layout.sales_summary_list_items, list);
		this.context = context;
		this.list = list;
		dbHelper = new MspDBHelper(context);
		this.supporter = new Supporter(context, dbHelper);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.sales_summary_list_items, null);

			final ViewHolder viewHolder = new ViewHolder();

			viewHolder.txtOrdNo = (TextView) view
					.findViewById(R.id.txt_SalesSummaryListOrdNo);
			viewHolder.txtCusName = (TextView) view
					.findViewById(R.id.txt_SalesSummaryListCusName);
			viewHolder.txtPaymentNo = (TextView) view
					.findViewById(R.id.txt_SalesSummaryListPayNo);
			viewHolder.txtAmount = (TextView) view
					.findViewById(R.id.txt_SalesSummaryListAmt);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtOrdNo.setText(list.get(position).getHhTran_referenceNumber());

		dbHelper.openReadableDatabase();
		String cusName = dbHelper.getCustomerName(list.get(position)
				.getHhTran_customerNumber(), supporter.getCompanyNo());
		dbHelper.closeDatabase();

		holder.txtCusName.setText(cusName);
		holder.txtPaymentNo.setText(" ");
		double amount = list.get(position).getHhTran_extPrice();
		holder.txtAmount.setText(supporter.getCurrencyFormat(amount));

		return view;
	}

}
