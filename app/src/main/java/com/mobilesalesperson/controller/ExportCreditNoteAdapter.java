package com.mobilesalesperson.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;

public class ExportCreditNoteAdapter extends ArrayAdapter<HhTran01> {
	private LayoutInflater inflater;
	private List<HhTran01> transList;
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
	private List<ExpInv> expInvLst;

	public ExportCreditNoteAdapter(Activity context, List<HhTran01> objects,
			SharedPreferences mPrefs, ListView exportItems, CheckBox selectAll) {
		super(context, R.layout.export_item_list, objects);
		inflater = LayoutInflater.from(context);
		transList = objects;
		this.contxt = context;
		this.mPrefs = mPrefs;
		dbHelper = new MspDBHelper(context);
		this.supporter = new Supporter(context, dbHelper);
		this.exportItems = exportItems;
		this.selectAll = selectAll;
		
		expInvLst = new ArrayList<ExpInv>();
		
		for(HhTran01 hTran:transList){
			
			ExpInv expInv = new ExpInv();
			expInv.setHhTran_referenceNumber(hTran.getHhTran_referenceNumber());
			expInv.setHhTran_transDay(hTran.getHhTran_transDay());
			expInv.setHhTran_transMonth(hTran.getHhTran_transMonth());
			expInv.setHhTran_transYear(hTran.getHhTran_transYear());
			expInv.setHhTran_status(hTran.getHhTran_status());
			
			boolean isExist = isCNExist(hTran.getHhTran_referenceNumber());
			
			expInv.setSelected(isExist);
			
			expInvLst.add(expInv);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CreditNoteViewHolder viewHolder = null;
		try{
		if (expInvLst != null) {
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.export_item_list, null);

			viewHolder = new CreditNoteViewHolder();

			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.export_chk);
			viewHolder.txtOrdNo = (TextView) convertView
					.findViewById(R.id.txt_exportOrdNo);
			viewHolder.txtOrdDate = (TextView) convertView
					.findViewById(R.id.txt_exportDate);
			viewHolder.txtOrdStatus = (TextView) convertView
					.findViewById(R.id.txt_exportStatus);
					
			
			viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					CheckBox cb = (CheckBox) v;
					
					int lPos = (Integer)cb.getTag();

					if (cb.isChecked()) {
						
						expInvLst.get(lPos).setSelected(true);
						
						// save item in preference
						String itemName = expInvLst.get(lPos)
								.getHhTran_referenceNumber();
						supporter.saveCreditNoteInPreference(
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
								supporter.setSelectAllCheckedForCN();
							}

						}

					} else {
						
						expInvLst.get(lPos).setSelected(false);
						supporter.removeCreditNoteFromPreference(expInvLst
										.get(lPos)
										.getHhTran_referenceNumber());

						// to check and uncheck selectAll
						// checkbox
						if (selectAll.isChecked()) {

							selectAll.setChecked(false);
							supporter
									.setSelectAllNotCheckedForCN();

						}
					}

				}
			});
			
			convertView.setTag(viewHolder);
			
			convertView.setTag(R.id.export_chk, viewHolder.checkBox);
			convertView.setTag(R.id.txt_exportOrdNo, viewHolder.txtOrdNo);
			convertView.setTag(R.id.txt_exportDate, viewHolder.txtOrdDate);
			convertView.setTag(R.id.txt_exportStatus, viewHolder.txtOrdStatus);
		} else {
			viewHolder = (CreditNoteViewHolder) convertView.getTag();
		}
		viewHolder.checkBox.setTag(position);

		int day = expInvLst.get(position).getHhTran_transDay();
		int month = expInvLst.get(position).getHhTran_transMonth();
		int year = expInvLst.get(position).getHhTran_transYear();
		String strDate = supporter.getStringDate(year, month, day);

		int stusVal = expInvLst.get(position).getHhTran_status();
		System.out.println("CNote Status:" + stusVal);
		String strStaus = "";
		if (stusVal == 0) {
			strStaus = "New";
		} else {
			strStaus = "Exported";
		}

		
		viewHolder.checkBox.setChecked(expInvLst.get(position).isSelected());

		viewHolder.txtOrdNo.setText(expInvLst.get(position)
				.getHhTran_referenceNumber());
		viewHolder.txtOrdDate.setText(strDate);
		viewHolder.txtOrdStatus.setText(strStaus);

		}
		
		} catch (Exception exe) {
			exe.printStackTrace();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(baos);
			exe.printStackTrace(stream);
			stream.flush();

			LogfileCreator.appendLog("Export Invoice List Display Error: "
					+ new String(baos.toByteArray()));
		}
		
		return convertView;
		
		
	}

	private boolean isCNExist(String cnNum) {
		boolean isExist = false;
		int mChkd = mPrefs.getInt(cnNum, -1);

		if (mChkd != -1) {
			isExist = true;
		} else {
			isExist = false;
		}

		return isExist;
	}

	/** Holds child views for one row. */
	private static class CreditNoteViewHolder {
		private CheckBox checkBox;
		private TextView txtOrdNo;
		private TextView txtOrdDate;
		private TextView txtOrdStatus;
	}
	
	private class ExpInv extends HhTran01{
		
		private boolean selected;

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
	}
}
