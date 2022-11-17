package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

public class ReceiptSelection extends AppBaseActivity {

	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private EditText edtReciptTxtSearch;
	private ListView reciptsList;
	private List<HhReceipt01> reciptSelectionList;
	private ArrayAdapter<HhReceipt01> adptReciptSelection;
	private String cmpnyNo; // added for multicompany

	private EditText fromDate;
	private EditText toDate;
	private Button loadHistory;
	private ToastMessage toastMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.receipt_selection_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);

		edtReciptTxtSearch = (EditText) findViewById(R.id.edtTxt_ReciptSearch);
		reciptsList = (ListView) findViewById(R.id.lst_ReciptSelectionList);

		fromDate = (EditText) findViewById(R.id.edt_RecFromDate);
		toDate = (EditText) findViewById(R.id.edt_RecToDate);
		loadHistory = findViewById(R.id.btn_RReport);

		toastMsg = new ToastMessage();

		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013

		reciptSelectionList = new ArrayList<HhReceipt01>();

		dbhelpher.openReadableDatabase();
		reciptSelectionList = dbhelpher.getReceiptListData(cmpnyNo);
		dbhelpher.closeDatabase();

		if (reciptSelectionList.size() == 0) {
			AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
			alertUser.setTitle("Information");
			alertUser.setIcon(R.drawable.tick);
			alertUser.setCancelable(false);
			alertUser.setMessage("Data not available");
			alertUser.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							supporter.simpleNavigateTo(MainMenu.class);
							dialog.cancel();
						}
					});
				//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
		} else {

			// Select document Custom adapter loading datas
			adptReciptSelection = new ReceiptSelectionAdapter(
					ReceiptSelection.this, reciptSelectionList);
			reciptsList.setAdapter(adptReciptSelection);
			adptReciptSelection.notifyDataSetChanged();

		}

		edtReciptTxtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				ReceiptSelection.this.adptReciptSelection.getFilter()
						.filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		reciptsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				HhReceipt01 reciptData = (HhReceipt01) adptReciptSelection
						.getItem(position);
				String reciptNo = reciptData.getHhReceipt_receiptnumber();

				navigationProcess(reciptNo, ReceiptDetail.class);
			}
		});
		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);

		fromDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog  = new DatePickerDialog(ReceiptSelection.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int month, int day) {
								month = month + 1;
								String date = month + "/" + day + "/" + year;
								fromDate.setText(date);
								loadHistory.setEnabled(true);

							}
						},year,month,day);
				datePickerDialog.show();
			}
		});
		toDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog  = new DatePickerDialog(ReceiptSelection.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int month, int day) {
								month = month + 1;
								String date = month + "/" + day + "/" + year;
								toDate.setText(date);
								loadHistory.setEnabled(true);

							}
						},year,month,day);
				datePickerDialog.show();
			}
		});

		loadHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar c = Calendar.getInstance();
				int currentYear = c.get(Calendar.YEAR);
				int currentMonth = c.get(Calendar.MONTH);
				int currentDay = c.get(Calendar.DAY_OF_MONTH);
				String fromdate = fromDate.getText().toString();
				String Todate = toDate.getText().toString();

				String frmdate[] = fromdate.split("/");
				String todate[] = Todate.split("/");

				String month = frmdate[0];
				String day = frmdate[1];
				String year = frmdate[2];

				String tmonth = todate[0];
				String tday = todate[1];
				String tyear = todate[2];
				int frmonth = Integer.parseInt(month);
				int frday = Integer.parseInt(day);
				int fryear = Integer.parseInt(year);

				int Tmonth = Integer.parseInt(tmonth);
				int Tday = Integer.parseInt(tday);
				int Tyear = Integer.parseInt(tyear);

				int currentMONTH = currentMonth + 1;


				if ((fryear > currentYear) || (Tyear > currentYear)) {
					reciptSelectionList.clear();
					adptReciptSelection.clear();
					adptReciptSelection.notifyDataSetChanged();
					toastMsg.showToast(ReceiptSelection.this, "Enter valid from and to date");
				} else if (fryear > Tyear) {
					reciptSelectionList.clear();
					adptReciptSelection.clear();
					adptReciptSelection.notifyDataSetChanged();
					toastMsg.showToast(ReceiptSelection.this, "Enter valid from and to date");
				} else if ((fryear == Tyear) && (frmonth > Tmonth)) {
					reciptSelectionList.clear();
					adptReciptSelection.clear();
					adptReciptSelection.notifyDataSetChanged();
					toastMsg.showToast(ReceiptSelection.this, "Enter valid from and to date");

				} else if ((fryear == Tyear) && (frday > Tday) && (frmonth > Tmonth)) {
					reciptSelectionList.clear();
					adptReciptSelection.clear();
					adptReciptSelection.notifyDataSetChanged();

					toastMsg.showToast(ReceiptSelection.this, "Enter valid from and to date");
				} else if ((fryear == Tyear) && (frday > Tday) && (frmonth == Tmonth)) {
					reciptSelectionList.clear();
					adptReciptSelection.clear();
					adptReciptSelection.notifyDataSetChanged();

					toastMsg.showToast(ReceiptSelection.this, "Enter valid from and to date");
				}else if (((frmonth > currentMONTH) || (Tmonth > currentMONTH))
						&& (fryear == Tyear)) {
					reciptSelectionList.clear();
					adptReciptSelection.clear();
					adptReciptSelection.notifyDataSetChanged();
					toastMsg.showToast(ReceiptSelection.this, "Enter valid from and to date");
				} else {
					listLoaded(frday,
							frmonth, fryear, Tday, Tmonth, Tyear);
				}
			}

		});

	}

	private void listLoaded(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear) {

		dbhelpher.openWritableDatabase();
		reciptSelectionList= dbhelpher.getReceiptSelectedData(frday,
				frmonth, fryear, tday, tmonth, tyear,cmpnyNo);
		dbhelpher.closeDatabase();

		if(reciptSelectionList !=  null)  {
			adptReciptSelection = new ReceiptSelectionAdapter(ReceiptSelection.this, reciptSelectionList);
			reciptsList.setAdapter(adptReciptSelection);

		}else{
			reciptsList.setAdapter(null);
			adptReciptSelection.notifyDataSetChanged();
			toastMsg.showToast(ReceiptSelection.this, "No Receipt Data Available");
		}

	}



	protected void navigationProcess(String reciptNo, Class<?> cls) {

		Intent intent = new Intent(ReceiptSelection.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("receiptnumber", reciptNo);
		startActivity(intent);
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			supporter.simpleNavigateTo(MainMenu.class);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
