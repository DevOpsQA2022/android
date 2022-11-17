package com.mobilesalesperson.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;

public class ExportReceiptAdapter extends ArrayAdapter<HhReceipt01> {

	private LayoutInflater inflater;
	private List<HhReceipt01> reciptList;
	private Activity contxt = null;
	private SharedPreferences mPrefs;
	private CheckBox checkBox;
	private TextView txtOrdNo;
	private TextView txtOrdDate;
	private TextView txtOrdStatus;
	private MspDBHelper dbHelper;
	private Supporter supporter;
	private ListView exportItems;
	private CheckBox selectAll;

	private List<ExpReceipt> expRecptList;

	public ExportReceiptAdapter(Activity context, List<HhReceipt01> objects,
			SharedPreferences mPrefs, ListView exportItems, CheckBox selectAll) {
		super(context, R.layout.export_item_list, objects);
		inflater = LayoutInflater.from(context);
		reciptList = objects;
		this.contxt = context;
		this.mPrefs = mPrefs;
		dbHelper = new MspDBHelper(context);
		this.supporter = new Supporter(context, dbHelper);
		this.exportItems = exportItems;
		this.selectAll = selectAll;

		expRecptList = new ArrayList<ExpReceipt>();

		for (HhReceipt01 hRecpt : reciptList) {

			ExpReceipt expRecpt = new ExpReceipt();

			expRecpt.setHhReceipt_receiptnumber(hRecpt
					.getHhReceipt_receiptnumber());
			expRecpt.setHhReceipt_receiptday(hRecpt.getHhReceipt_receiptday());
			expRecpt.setHhReceipt_receiptmonth(hRecpt
					.getHhReceipt_receiptmonth());
			expRecpt.setHhReceipt_receiptyear(hRecpt.getHhReceipt_receiptyear());
			expRecpt.setHhReceipt_status(hRecpt.getHhReceipt_status());

			boolean isExist = isReceiptExist(hRecpt
					.getHhReceipt_receiptnumber());

			expRecpt.setSelected(isExist);

			expRecptList.add(expRecpt);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ReceiptViewHolder viewHolder = null;

		try {

			if (expRecptList != null) {

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.export_item_list,
							null);

					viewHolder = new ReceiptViewHolder();
					viewHolder.checkBox = (CheckBox) convertView
							.findViewById(R.id.export_chk);
					viewHolder.txtOrdNo = (TextView) convertView
							.findViewById(R.id.txt_exportOrdNo);
					viewHolder.txtOrdDate = (TextView) convertView
							.findViewById(R.id.txt_exportDate);
					viewHolder.txtOrdStatus = (TextView) convertView
							.findViewById(R.id.txt_exportStatus);

					// checkbox click function
					viewHolder.checkBox
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									CheckBox cb = (CheckBox) v;

									int lPos = (Integer) cb.getTag();

									if (cb.isChecked()) {

										// save item in preference
										String itemName = expRecptList
												.get(lPos)
												.getHhReceipt_receiptnumber();
										supporter.saveReceiptInPreference(
												itemName, 1);

										// to check and uncheck selectAll
										// checkbox
										if (!selectAll.isChecked()) {

											boolean selectCheck = true;

											for (int i = 0; i < exportItems
													.getChildCount(); i++) {
												LinearLayout listViewLayout = (LinearLayout) exportItems
														.getChildAt(i);
												CheckBox chkBox = (CheckBox) listViewLayout
														.findViewById(R.id.export_chk);

												if (!chkBox.isChecked()) {
													selectCheck = false;
													break;
												}
											}

											if (selectCheck) {
												selectAll.setChecked(true);
												supporter
														.setSelectAllCheckedForReceipt();
											}

										}

									} else {

										supporter
												.removeReceiptFromPreference(expRecptList
														.get(lPos)
														.getHhReceipt_receiptnumber());

										// to check and uncheck selectAll
										// checkbox
										if (selectAll.isChecked()) {

											selectAll.setChecked(false);
											supporter
													.setSelectAllNotCheckedForReceipt();

										}

									}
								}
							});

					convertView.setTag(viewHolder);

					convertView.setTag(R.id.export_chk, viewHolder.checkBox);
					convertView.setTag(R.id.txt_exportOrdNo,
							viewHolder.txtOrdNo);
					convertView.setTag(R.id.txt_exportDate,
							viewHolder.txtOrdDate);
					convertView.setTag(R.id.txt_exportStatus,
							viewHolder.txtOrdStatus);
				} else {
					viewHolder = (ReceiptViewHolder) convertView.getTag();
				}
				viewHolder.checkBox.setTag(position);

				int day = expRecptList.get(position).getHhReceipt_receiptday();
				int month = expRecptList.get(position)
						.getHhReceipt_receiptmonth();
				int year = expRecptList.get(position)
						.getHhReceipt_receiptyear();
				String strDate = supporter.getStringDate(year, month, day);

				int stusVal = expRecptList.get(position).getHhReceipt_status();
				String strStaus = "";
				if (stusVal == 0) {
					strStaus = "New";
				} else {
					strStaus = "Exported";
				}

				viewHolder.checkBox.setChecked(expRecptList.get(position)
						.isSelected());

				viewHolder.txtOrdNo.setText(expRecptList.get(position)
						.getHhReceipt_receiptnumber());
				viewHolder.txtOrdDate.setText(strDate);
				viewHolder.txtOrdStatus.setText(strStaus);

			}

		} catch (Exception exe) {
			exe.printStackTrace();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(baos);
			exe.printStackTrace(stream);
			stream.flush();

			LogfileCreator.appendLog("Export Receipt List Display Error: "
					+ new String(baos.toByteArray()));
		}

		return convertView;
	}

	private boolean isReceiptExist(String receiptNum) {
		boolean isExist = false;
		int mqty = mPrefs.getInt(receiptNum, -1);

		if (mqty != -1) {
			isExist = true;
		} else {
			isExist = false;
		}

		return isExist;
	}

	/** Holds child views for one row. */
	private static class ReceiptViewHolder {
		private CheckBox checkBox;
		private TextView txtOrdNo;
		private TextView txtOrdDate;
		private TextView txtOrdStatus;
	}

	private class ExpReceipt extends HhReceipt01 {

		private boolean selected;

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

	}
}
