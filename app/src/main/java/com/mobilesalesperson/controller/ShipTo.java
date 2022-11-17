package com.mobilesalesperson.controller;

import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author TISN class to handle shipto details
 */
public class ShipTo extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbhelpher;
	private TextView txtShipToCusNm;
	private Spinner spnShipToLocDet;
	private Spinner spnShipToVia;
	private EditText edtTxtShipDt;
	private Button btnShipOk;

	private String custNo;
	private String custName;
	private ToastMessage toastMsg;
	private String cmpnyNo; // added for multicompany

	private List<String> lstShipToLocDet;
	private ArrayAdapter<String> adptLocDetailsSpn;
	private List<String> lstShipVia;
	private ArrayAdapter<String> adptShipViaSpn;
	private String mCustShipViacode;// 11-11-2013 Added by TISN
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID_SHIP = 0;

	private DatePickerDialog.OnDateSetListener shipDateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			edtTxtShipDt.setText(supporter.getStringDate(mYear, mMonth + 1,
					mDay));
		}
	};

	/**
	 * method to initialize the class activity
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.shipto_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		txtShipToCusNm = (TextView) findViewById(R.id.txt_ShipToCusNm);
		spnShipToLocDet = (Spinner) findViewById(R.id.spn_ShipToLocDet);
		spnShipToVia = (Spinner) findViewById(R.id.spn_ShipToVia);
		edtTxtShipDt = (EditText) findViewById(R.id.edtTxt_ShipDt);
		btnShipOk = (Button) findViewById(R.id.btn_ShipOk);
		btnShipOk.setEnabled(true);

		cmpnyNo = supporter.getCompanyNo(); // added jul 12/2013

		currentDate();
		edtTxtShipDt.setText(supporter.getStringDate(mYear, mMonth + 1, mDay));

		custNo = getIntent().getStringExtra("customerNumber");

		dbhelpher.openReadableDatabase();
		custName = dbhelpher.getCustomerName(custNo, cmpnyNo);
		dbhelpher.closeDatabase();

		txtShipToCusNm.setText(custName);

		dbhelpher.openReadableDatabase();
		lstShipToLocDet = dbhelpher.getShipToLocDet(custNo, cmpnyNo);
		dbhelpher.closeDatabase();
		adptLocDetailsSpn = new ArrayAdapter<String>(ShipTo.this,
				android.R.layout.simple_dropdown_item_1line, lstShipToLocDet);
		// adptLocDetailsSpn.setDropDownViewResource(R.layout.spinner_item_layout);

		spnShipToLocDet.setAdapter(adptLocDetailsSpn);

		dbhelpher.openReadableDatabase();
		mCustShipViacode = dbhelpher.getShipViaCode(custNo, cmpnyNo);
		lstShipVia = dbhelpher.getShipViaList(cmpnyNo, mCustShipViacode);
		dbhelpher.closeDatabase();

		adptShipViaSpn = new ArrayAdapter<String>(ShipTo.this,
				android.R.layout.simple_dropdown_item_1line, lstShipVia);
		// adptShipViaSpn.setDropDownViewResource(R.layout.spinner_item_layout);

		spnShipToVia.setAdapter(adptShipViaSpn);

		// to handle editor click funtion
		edtTxtShipDt.setOnTouchListener(new OnTouchListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				showDialog(DATE_DIALOG_ID_SHIP);
				return true;
			}
		});

		// to handle ok button click function
		btnShipOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {

					btnShipOk.setEnabled(false);

					String strShipDate = edtTxtShipDt.getText().toString();

					if (!strShipDate.equals("")) {
						String strLocDet = (String) spnShipToLocDet
								.getItemAtPosition(spnShipToLocDet
										.getSelectedItemPosition());

						String strShipVia = (String) spnShipToVia
								.getItemAtPosition(spnShipToVia
										.getSelectedItemPosition());
						String[] shipVia = strShipVia.split(" ");
						String strViaCode = shipVia[0].trim();

						if (strLocDet.equals("DEFAULT")) { // no need to set
															// value
															// in ship to
															// location
															// if default comes
							strLocDet = "";
						}

						if (strViaCode.equals("DEFAULT")) { // no need to set
															// value in ship via
															// if default comes
							strViaCode = "";
						}
						storeShipDetailInPreference(strLocDet, strViaCode,
								strShipDate);

						supporter.simpleNavigateTo(ItemSelection.class);

					} else {
						toastMsg.showToast(ShipTo.this, "Enter ship date");
						btnShipOk.setEnabled(true);
					}

				} catch (Exception e) {
					Toast.makeText(ShipTo.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					LogfileCreator.appendLog(e.getMessage());
					btnShipOk.setEnabled(true);
				}
			}
		});

	}

	// to store shipping details in shared preference
	protected void storeShipDetailInPreference(String strLocDet,
			String strViaCode, String strShipDate) {
		supporter.addShippingDetails(custNo, custName, strLocDet, strViaCode,
				strShipDate);
	}

	// to create date picker dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID_SHIP:
			return new DatePickerDialog(this, shipDateListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	// to set current date
	private void currentDate() {
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
