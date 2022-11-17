package com.mobilesalesperson.controller;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.Supporter;

public class TransactionSummaryAdapter extends ArrayAdapter<HhTran01> {

	private final List<HhTran01> list;
	private final Activity context;
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private static final String SharedPreferences = "sharedPrefs";



	static class ViewHolder {
		protected TextView txtSno;
		protected TextView txtDate;
		protected TextView txtDocRefNo;
		protected TextView txtCusNo;
		protected TextView txtCusName;
		protected TextView txtNetAmt;
		protected TextView txtStatus;
		protected EditText txtedtComments;
	}

	public TransactionSummaryAdapter(Activity context, List<HhTran01> list) {
		super(context, R.layout.transaction_summary_list_items, list);
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
			view = inflator.inflate(R.layout.transaction_summary_list_items,
					null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtSno = (TextView) view
					.findViewById(R.id.txt_TransSummarySerialNum);
			viewHolder.txtDate = (TextView) view
					.findViewById(R.id.txt_TransSummaryListDate);
			viewHolder.txtDocRefNo = (TextView) view
					.findViewById(R.id.txt_TransSummaryListDocRefNo);

			viewHolder.txtCusNo = (TextView) view
					.findViewById(R.id.txt_TransSummaryCusNo);
			viewHolder.txtCusName = (TextView) view
					.findViewById(R.id.txt_TransSummaryCusName);
			viewHolder.txtNetAmt = (TextView) view
					.findViewById(R.id.txt_TransSummaryNetAmt);
			viewHolder.txtStatus = (TextView) view
					.findViewById(R.id.txt_TransSummaryStatus);
			viewHolder.txtedtComments = (EditText) view.findViewById(R.id.txt_TransSummaryComments);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		int day = list.get(position).getHhTran_transDay();
		int month = list.get(position).getHhTran_transMonth();
		int year = list.get(position).getHhTran_transYear();
		String _comments = list.get(position).getHhComments();
		String strDate = supporter.getStringDate(year, month, day);
		String refNum = list.get(position).getHhTran_referenceNumber();
		String cusNumbr = list.get(position).getHhTran_customerNumber();
		String cusName = getCustName(cusNumbr);
		String netAmount = supporter.getCurrencyFormat(getSummaryTotal(refNum));
		int status = list.get(position).getHhTran_status();
		String strStatus = "";
		if (status == 0) {
			strStatus = "Not Sent";
		} else {
			strStatus = "Sent";
		}

		/*holder.txtedtComments.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {


				String comment =holder.txtedtComments.getText().toString();
				String ref = refNum;
				if(_comments.equals(_comments)){

						supporter.setcommentsdata(comment);
						supporter.setrefno(refNum);
				}

			}
		});*/


		//holder.txtedtComments.setText(_comments);
		holder.txtSno.setText((position + 1) + "");
		holder.txtDate.setText(strDate);
		holder.txtDocRefNo.setText(refNum);
		holder.txtCusNo.setText(cusNumbr);
		holder.txtCusName.setText(cusName);
		holder.txtNetAmt.setText(netAmount);
		holder.txtStatus.setText(strStatus);


		return view;
	}



	private String getCustName(String cusNumbr) {
		String cusName = "";

		dbHelper.openReadableDatabase();
		cusName = dbHelper.getCustomerName(cusNumbr, supporter.getCompanyNo());
		dbHelper.closeDatabase();

		return cusName;
	}

	private double getSummaryTotal(String refNum) {

		double summaryTotal = 0.00;

		dbHelper.openReadableDatabase();
		summaryTotal = summaryTotal
				+ dbHelper.getSummaryTotal(refNum, supporter.getCompanyNo());
		dbHelper.closeDatabase();

		return summaryTotal;
	}
}
//				refNum
//				HhTran01 tran01 = new HhTran01();
//				tran01.setHhComments(comment);
//				dbHelper.addTransactionDetail(tran01);
//				dbHelper.openWritableDatabase();
//				dbHelper.updateTransComments(comment, refNum);
//
//				if (comment.length() > 2) {
//
//				}
//				dbHelper.closeDatabase();
////
//		if( holder.txtedtComments.getText().toString().isEmpty()){
//			holder.txtedtComments.setEnabled(true);
//		}else
//		{
//			holder.txtedtComments.setEnabled(false);
//		}