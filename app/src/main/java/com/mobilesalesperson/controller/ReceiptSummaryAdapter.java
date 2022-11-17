package com.mobilesalesperson.controller;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.util.Supporter;

public class ReceiptSummaryAdapter extends ArrayAdapter<HhReceipt01> {

	private final List<HhReceipt01> list;
	private final Activity context;
	private Supporter supporter;
	private MspDBHelper dbHelper;

	static class ViewHolder {
		protected TextView txtSno;
		protected TextView txtDate;
		protected TextView txtReceiptNo;
		protected TextView txtCustNo;
		protected TextView txtCustName;
		protected TextView txtReceiptType;
		protected TextView txtAmount;
		protected TextView txtUnAmount;
	}

	public ReceiptSummaryAdapter(Activity context, List<HhReceipt01> list) {
		super(context, R.layout.receipt_summary_list_items, list);
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
			view = inflator.inflate(R.layout.receipt_summary_list_items, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtSno = (TextView) view
					.findViewById(R.id.txt_ReceiptSummarySerialNum);
			viewHolder.txtDate = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListDate);
			viewHolder.txtReceiptNo = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListReciptNo);
			viewHolder.txtCustNo = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListCustNo);
			viewHolder.txtCustName = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListCustName);
			viewHolder.txtReceiptType = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListType);
			viewHolder.txtAmount = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListAmount);
			viewHolder.txtUnAmount = (TextView) view
					.findViewById(R.id.txt_ReceiptSummaryListUnAmount);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtSno.setText((position + 1) + "");

		int day = list.get(position).getHhReceipt_receiptday();
		int month = list.get(position).getHhReceipt_receiptmonth();
		int year = list.get(position).getHhReceipt_receiptyear();
		String strDate = supporter.getStringDate(year, month, day);

		holder.txtDate.setText(strDate);
		holder.txtReceiptNo.setText(list.get(position)
				.getHhReceipt_receiptnumber());
		holder.txtCustNo.setText(list.get(position)
				.getHhReceipt_customernumber());

		holder.txtCustName.setText(list.get(position)
				.getHhReceipt_customername());
		holder.txtReceiptType.setText(list.get(position)
				.getHhReceipt_receipttype());
		holder.txtAmount.setText("" + list.get(position).getHhReceipt_amount());
		holder.txtUnAmount.setText(""
				+ list.get(position).getHhReceipt_receiptunapplied());

		return view;
	}

}
