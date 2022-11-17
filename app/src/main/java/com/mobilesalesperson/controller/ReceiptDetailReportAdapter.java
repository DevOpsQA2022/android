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

public class ReceiptDetailReportAdapter extends ArrayAdapter<HhReceipt01> {

	private final List<HhReceipt01> list;
	private final Activity context;
	private Supporter supporter;
	private MspDBHelper dbHelper;

	static class ViewHolder {
		protected TextView txtApply;
		protected TextView txtDocNo;
		protected TextView txtApplAmt;
		protected TextView txtPendAmt;
		protected TextView txtNetAmt;
	}

	public ReceiptDetailReportAdapter(Activity context, List<HhReceipt01> list) {
		super(context, R.layout.receipt_detail_list_items, list);
		this.context = context;
		this.list = list;
		dbHelper = new MspDBHelper(context);
		supporter = new Supporter(context, dbHelper);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.receipt_detail_list_items, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtApply = (TextView) view
					.findViewById(R.id.txt_ReciptDetailListApply);
			viewHolder.txtDocNo = (TextView) view
					.findViewById(R.id.txt_ReciptDetailListDocNo);
			viewHolder.txtApplAmt = (TextView) view
					.findViewById(R.id.txt_ReciptDetailListAppliedAmt);
			viewHolder.txtPendAmt = (TextView) view
					.findViewById(R.id.txt_ReciptDetailListPendingAmt);
			viewHolder.txtNetAmt = (TextView) view
					.findViewById(R.id.txt_ReciptDetailListNetAmt);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		holder.txtApply.setText("Yes");

		holder.txtDocNo.setText(list.get(position).getHhReceipt_docnumber());

		holder.txtApplAmt.setText(""
				+ list.get(position).getHhReceipt_appliedamount());
		holder.txtPendAmt.setText(list.get(position).getHhReceipt_pendingbal());
		holder.txtNetAmt.setText("" + list.get(position).getHhReceipt_amount());

		return view;
	}

}
