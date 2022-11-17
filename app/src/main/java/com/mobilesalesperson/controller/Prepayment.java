package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhPrepayment01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

/**
 * @author TIVM class to process prepayments
 */
public class Prepayment extends AppBaseActivity {

	private Supporter supporter;
	private MspDBHelper dbhelpher;
	private HhSetting setting;
	private int deciValue;
	private TextView txtPrePayCusNo;
	private TextView txtPrePayCusName;
	private TextView txtPrePayCurrency;
	private TextView lblPrePayOrdTotal;
	private TextView txtPrePayOrdTotal;
	private TextView txtPrePayAmtDue;
	private EditText edtTxtPrePayChkNo;
	private EditText edtTxtPrePayRecptAmt;
	private Spinner spnPrePaymentCode;
	private TextView txtPrePayCodeDesc;
	private EditText edtTxtPrePayRecptDate;
	private Button btnPrePayOk;
	private ToastMessage toastMsg;
	private Spinner spnPrePaymentCode2;
	private EditText edtTxtPrePayRecptAmt1;

	private List<HhTran01> transAllList;
	private HhTran01 tran;
	private String cmpnyNo; // added for multicompany

	private List<String> payCodeList;
	private ArrayAdapter<String> adptPayCodeSpn;
	private SharedPreferences shippingDetails;
	private SharedPreferences entryDetails;
	private int autoReportSetting;
	private String pCode;
	private String refNo;
	private String mode;
	private String custNo;
	private String custName;
	private String shipDate;
	private double total;
	private double dueAmt;

	private String strDueAmt;
	private String strTotal;
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID_SHIP = 0;

	//</History> Suresh 05-Oct-2017 Added for Print model Select
private String printerModel;

	private DatePickerDialog.OnDateSetListener shipDateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			edtTxtPrePayRecptDate.setText(supporter.getStringDate(mYear,
					mMonth + 1, mDay));

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.prepayment_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		txtPrePayCusNo = (TextView) findViewById(R.id.txt_prePayCusNo);
		txtPrePayCusName = (TextView) findViewById(R.id.txt_prePayCusName);
		txtPrePayCurrency = (TextView) findViewById(R.id.txt_prePayCurrency);

		lblPrePayOrdTotal = (TextView) findViewById(R.id.lbl_prePayOrdTotal);
		txtPrePayOrdTotal = (TextView) findViewById(R.id.txt_prePayOrdTotal);
		txtPrePayAmtDue = (TextView) findViewById(R.id.txt_prePayAmtDue);
		edtTxtPrePayChkNo = (EditText) findViewById(R.id.edtTxt_prePayChkNo);
		edtTxtPrePayRecptAmt = (EditText) findViewById(R.id.edtTxt_prePayRecptAmt);

		edtTxtPrePayRecptAmt1 = (EditText) findViewById(R.id.edtTxt_prePayRecptAmt1);
		/*edtTxtPrePayRecptAmt2 = (EditText) findViewById(R.id.edtTxt_prePayRecptAmt2);*/
		spnPrePaymentCode2 = (Spinner) findViewById(R.id.spn_prePaymentCode2);

		spnPrePaymentCode = (Spinner) findViewById(R.id.spn_prePaymentCode);
		txtPrePayCodeDesc = (TextView) findViewById(R.id.txt_prePayCodeDesc);
		edtTxtPrePayRecptDate = (EditText) findViewById(R.id.edtTxt_prePayRecptDate);
		btnPrePayOk = (Button) findViewById(R.id.btn_prePayOk);
		btnPrePayOk.setEnabled(true);
		edtTxtPrePayRecptAmt1.setText("0");

		shippingDetails = getSharedPreferences("shippingDetails",
				Context.MODE_PRIVATE);
		entryDetails = getSharedPreferences("entryDetails",
				Context.MODE_PRIVATE);

		mode = supporter.getMode();
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013
		refNo = getIntent().getStringExtra("refNo");
		total = getIntent().getDoubleExtra("netTotal", 0.0);

		custNo = shippingDetails.getString("customerNumber", "");
		custName = shippingDetails.getString("customerName", "");
		shipDate = shippingDetails.getString("shipDate", "");

		// set current date
		dbhelpher.openReadableDatabase();
		Calendar c = supporter.stringDateToCalender(shipDate);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		dbhelpher.closeDatabase();

		dbhelpher.openReadableDatabase();
		setting = dbhelpher.getSettingData();
		transAllList = dbhelpher.getTransactionList(refNo, cmpnyNo);
		//transAllList = dbhelpher.getTransactionList("BB-1-000000032", cmpnyNo);

		tran = transAllList.get(0);
		dbhelpher.closeDatabase();

		deciValue = Integer.parseInt(setting.getHhSetting_decimalformat());
		autoReportSetting = setting.getHhSetting_autoreportgen();

		// set values to text boxes
		txtPrePayCusNo.setText(custNo);
		txtPrePayCusName.setText(tran.getHhTran_editedcustomername());

		// 10-04-13 company currency issue fixed by TISN
		txtPrePayCurrency.setText(supporter.getCompanyCurrency());

		if (mode.equals("ie")) {
			lblPrePayOrdTotal.setText("Invoice Total");
		}

		if (mode.equals("orderEdit") || mode.equals("invoiceEdit")) {
			double receiptAmount = 0.0;
			String refNumb = entryDetails.getString("referencenumber", "");
			dbhelpher.openReadableDatabase();
			receiptAmount = dbhelpher.getPrepayAmount(refNumb, cmpnyNo);
			String receiptNumber = dbhelpher.getPrepayReceiptNo(refNumb,
					cmpnyNo);
			dbhelpher.closeDatabase();
			edtTxtPrePayRecptAmt.setText(receiptAmount + "");
			edtTxtPrePayChkNo.setText(receiptNumber);

			dueAmt = total - receiptAmount;
			strDueAmt = supporter.getCurrencyFormat(dueAmt);

			txtPrePayAmtDue.setText(strDueAmt);
		} else {
			strDueAmt = supporter.getCurrencyFormat(total);
			txtPrePayAmtDue.setText(strDueAmt);
		}
		strTotal = supporter.getCurrencyFormat(total);
		txtPrePayOrdTotal.setText(strTotal);

		dbhelpher.openReadableDatabase();
		payCodeList = dbhelpher.getReceiptType(cmpnyNo);
		dbhelpher.closeDatabase();

		adptPayCodeSpn = new ArrayAdapter<String>(Prepayment.this,
				android.R.layout.simple_dropdown_item_1line, payCodeList);
		// adptPayCodeSpn.setDropDownViewResource(R.layout.spinner_item_layout);

		spnPrePaymentCode.setAdapter(adptPayCodeSpn);
		spnPrePaymentCode2.setAdapter(adptPayCodeSpn);


		pCode = (String) spnPrePaymentCode.getItemAtPosition(spnPrePaymentCode
				.getSelectedItemPosition());
		dbhelpher.openReadableDatabase();
		txtPrePayCodeDesc.setText(dbhelpher.getReciptName(pCode, cmpnyNo));
		dbhelpher.closeDatabase();

		edtTxtPrePayRecptDate.setText(shipDate);

		// second, we create the TextWatcher
		TextWatcher recptAmtWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {

				String stxt = charSequence.toString();

				if (!stxt.equals("")) {

					String strReceipAmount = edtTxtPrePayRecptAmt.getText()
							.toString();
					int indexOFdec = strReceipAmount.indexOf(".");

					if (strReceipAmount.equals(".")) {
						edtTxtPrePayRecptAmt.setText("");
						edtTxtPrePayRecptAmt.append("0.");
					}

					if (strReceipAmount
							.matches("^(-?[0-9]+[\\.\\,][0-9]{1,5})?[0-9]*$")) {
						double recAmt = Double.parseDouble(strReceipAmount);

						double amtdue = total - recAmt;
						strDueAmt = supporter.getCurrencyFormat(amtdue);

						/*
						 * if (amtdue == 0) { strDueAmt =
						 * strDueAmt.split("-")[1]; }
						 */

						txtPrePayAmtDue.setText(strDueAmt);
					}
					if (indexOFdec >= 0) {

						if (strReceipAmount.substring(indexOFdec).length() > deciValue) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									edtTxtPrePayRecptAmt.getWindowToken(), 0);
						}
					}

				} else {
					strDueAmt = supporter.getCurrencyFormat(total);
					txtPrePayAmtDue.setText(strDueAmt);
					// edtTxtPrePayRecptAmt.setFocusableInTouchMode(true);
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		};

		edtTxtPrePayRecptAmt.addTextChangedListener(recptAmtWatcher);

		// to handle editor click funtion
		edtTxtPrePayRecptDate.setOnTouchListener(new OnTouchListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				showDialog(DATE_DIALOG_ID_SHIP);
				return true;
			}
		});

		spnPrePaymentCode
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {

					}

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						pCode = (String) spnPrePaymentCode
								.getItemAtPosition(spnPrePaymentCode
										.getSelectedItemPosition());
						dbhelpher.openReadableDatabase();
						txtPrePayCodeDesc.setText(dbhelpher.getReciptName(
								pCode, cmpnyNo));
						dbhelpher.closeDatabase();
					}

				});

		btnPrePayOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					btnPrePayOk.setEnabled(false);
					String strReciptAmt = edtTxtPrePayRecptAmt.getText()
							.toString();

					String strReciptAmt1 = edtTxtPrePayRecptAmt1.getText()
							.toString();

					String chkReceiptNo = edtTxtPrePayChkNo.getText()
							.toString();

					String Receipttype1 = spnPrePaymentCode.getSelectedItem().toString();
					String Receipttype2 = spnPrePaymentCode2.getSelectedItem().toString();

					if (strReciptAmt.equals("")) {
						toastMsg.showToast(Prepayment.this,
								"Please enter your receipt amount.");
						btnPrePayOk.setEnabled(true);
					} else if (!supporter.isAmountValid(strReciptAmt)) {
						toastMsg.showToast(Prepayment.this,
								"Please enter valid amount.");
						btnPrePayOk.setEnabled(true);
					} else if (chkReceiptNo.equals("")) {
						toastMsg.showToast(Prepayment.this,
								"Enter check or receipt number.");
						btnPrePayOk.setEnabled(true);
					} else if (validateChkReceiptNo(chkReceiptNo)
							&& !(mode.equals("orderEdit") || mode
									.equals("invoiceEdit"))) {
						toastMsg.showToast(Prepayment.this,
								"Check or Receipt number already exist, please change it.");
						btnPrePayOk.setEnabled(true);
					} else if (validateChkReceiptNoForEdit(chkReceiptNo,
							entryDetails.getString("referencenumber", ""))
							&& (mode.equals("orderEdit") || mode
									.equals("invoiceEdit"))) {
						toastMsg.showToast(Prepayment.this,
								"Check or Receipt number already exist, please change it.");
						btnPrePayOk.setEnabled(true);
					} else {
						double amoutDue = Double.parseDouble(txtPrePayAmtDue
								.getText().toString());
						double reciptAmt = Double.parseDouble(strReciptAmt);

						double reciptAmt1 = Double.parseDouble(strReciptAmt1);



						if(Receipttype1.contains("CASH")){
							String rptAmt1 = PriceFormatter(reciptAmt);
							reciptAmt = Double.parseDouble(rptAmt1);

						}
						if(Receipttype2.contains("CASH")){
							String rptAmt2 = PriceFormatter(reciptAmt1);
							reciptAmt1 = Double.parseDouble(rptAmt2);

						}


						HhPrepayment01 prePayDetail = new HhPrepayment01();

						if (mode.equals("oe")) {
							prePayDetail.setHhPrePayment_transType("S");
						} else if (mode.equals("ie")) {
							prePayDetail.setHhPrePayment_transType("I");
						}
						prePayDetail.setHhPrePayment_referenceNumber(refNo);
						prePayDetail.setHhPrePayment_invoiceNumber("0");
						prePayDetail.setHhPrePayment_customerNumber(custNo);
						prePayDetail.setHhPrePayment_currency(txtPrePayCurrency
								.getText().toString());
						prePayDetail.setHhPrePayment_orderTotal(total);
						prePayDetail.setHhPrePayment_amtDue(amoutDue);
						prePayDetail.setHhPrePayment_receiptType(pCode);
						prePayDetail.setHhPrePayment_receiptType2(Receipttype2);
						prePayDetail.setHhPrePayment_receiptAmount1(reciptAmt1);

						// if (pCode.equals("CHECK")) {
						prePayDetail
								.setHhPrePayment_checkReceiptNo(chkReceiptNo);
						// }

						String receiptDate = edtTxtPrePayRecptDate.getText()
								.toString();
						dbhelpher.openReadableDatabase();
						Calendar cal = supporter
								.stringDateToCalender(receiptDate);
						dbhelpher.closeDatabase();

						prePayDetail.setHhPrePayment_receiptDay(cal
								.get(Calendar.DAY_OF_MONTH));
						prePayDetail.setHhPrePayment_receiptMonth(cal
								.get(Calendar.MONTH) + 1);
						prePayDetail.setHhPrePayment_receiptYear(cal
								.get(Calendar.YEAR));
						prePayDetail.setHhPrePayment_receiptAmount(reciptAmt);
						prePayDetail.setHhPrePayment_accpacOrdNumber(""); // need
																			// to
																			// understand
						prePayDetail.setHhPrePayment_flag("0"); // need to
																// understand
						prePayDetail.setHhPrePayment_refNo(""); // need to
																// understand
						prePayDetail.setHhPrePayment_companycode(cmpnyNo);

						String message = "Prepayment for "+ refNo;

						if (mode.equals("orderEdit")
								|| mode.equals("invoiceEdit")) {

							if (mode.equals("orderEdit")) {
								prePayDetail.setHhPrePayment_transType("S");
							} else if (mode.equals("invoiceEdit")) {
								prePayDetail.setHhPrePayment_transType("I");
							}

							dbhelpher.openWritableDatabase();
							String receipNum = dbhelpher.getPrepayReceiptNo(
									refNo, cmpnyNo);
							if (receipNum.equals("")) {
								dbhelpher.addPrepayment(prePayDetail);
							} else {
								dbhelpher.updatePrepayment(prePayDetail,
										cmpnyNo);
							}
							dbhelpher.closeDatabase();
							message = "Prepayment updated "+ refNo;
						} else {
							dbhelpher.openWritableDatabase();
							dbhelpher.addPrepayment(prePayDetail);
							dbhelpher.closeDatabase();
						}

						// display alert
						final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								Prepayment.this);
						alertDialog.setTitle("Information");
						alertDialog.setIcon(R.drawable.tick);
						alertDialog.setCancelable(false);
						alertDialog.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										supporter.clearPreference(
												shippingDetails, entryDetails);
										if (autoReportSetting == 1) {
											callPrinter(refNo);
										} else {
											supporter
													.simpleNavigateTo(MainMenu.class);
										}
										dialog.dismiss();
									}
								});

						alertDialog.setMessage(message);
						////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
					}
				} catch (Exception e) {
					Toast.makeText(Prepayment.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					LogfileCreator.appendLog(e.getMessage());
					btnPrePayOk.setEnabled(true);
				}
			}

		});


		edtTxtPrePayRecptAmt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(edtTxtPrePayRecptAmt.getText().toString().length() > 0 ) {

					Double prepayamtdue1 = Double.parseDouble(edtTxtPrePayRecptAmt.getText().toString());
					String a = String.valueOf(prepayamtdue1);

					if(a.length() > 0 ) {
						Double prepayamtdue = Double.parseDouble(txtPrePayOrdTotal.getText().toString());
						Double prepayamt = Double.parseDouble(edtTxtPrePayRecptAmt.getText().toString());

						DecimalFormat formater = new DecimalFormat("#.##");
						String balAmount = String.valueOf(prepayamtdue - prepayamt);
						Double bAmt = Double.parseDouble(balAmount);
						String Bamt = formater.format(bAmt);
						if(Bamt.contains("0.0")){
							edtTxtPrePayRecptAmt1.setText("0");
						}else{
							edtTxtPrePayRecptAmt1.setText(Bamt);
						}

					}else{

						edtTxtPrePayRecptAmt1.setText("0");
					}
				}else{
					edtTxtPrePayRecptAmt1.setText("0");
				}

			}

			@Override
			public void afterTextChanged(Editable s) {



			}
		});
	}

	private String PriceFormatter(double receiptAmt){

		String s = String.valueOf(receiptAmt);
		String spa[] = s.split("\\.");
		String a = spa[1];

		if(a.length() == 1){
			return s;
		}else{
			int indexOfDecimal = Integer.parseInt(a);
			int value = indexOfDecimal % 10;
			System.out.println(value);
			char[] digits = String.valueOf(indexOfDecimal).toCharArray();

			if(value<=2){
				Array.setChar(digits,digits.length-1,'0');
				indexOfDecimal=Integer.parseInt(String.valueOf(digits));
				spa[1]= String.valueOf(indexOfDecimal);
				System.out.println(Integer.parseInt(String.valueOf(digits)));

			}else if(value<=7){
				Array.setChar(digits,digits.length-1,'5');
				indexOfDecimal=Integer.parseInt(String.valueOf(digits));
				spa[1]= String.valueOf(indexOfDecimal);
				System.out.println(Integer.parseInt(String.valueOf(digits)));

			}else{
				if(digits[digits.length-1]=='8'){
					indexOfDecimal=indexOfDecimal+2;
					spa[1]= String.valueOf(indexOfDecimal);
					System.out.println(indexOfDecimal);
				}else if(digits[digits.length-1]=='9'){
					indexOfDecimal=indexOfDecimal+1;
					spa[1]= String.valueOf(indexOfDecimal);
					System.out.println(indexOfDecimal);
				}
			}

			String val = (spa[0]+"."+spa[1]);

			return val;
		}
	}

	private void callPrinter(final String refNo) {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Prepayment.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						//</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
							Intent intent = new Intent(Prepayment.this, TransPDFCreate.class);
                        intent.putExtra("refno", refNo);
                        startActivity(intent);
						}
						else{
							// navigationProcess(refNo, TransactionDetail.class);
						List<Devices> deviceList = supporter.getPairedDevices();

						if (deviceList.size() != 0) {
							final TransDetailDialog dialog1 = new TransDetailDialog(
									Prepayment.this, refNo, dbhelpher,
									supporter, deviceList);
							dialog1.setContentView(R.layout.print_list_layout);
							dialog1.show();
						} else {
							toastMsg.showToast(Prepayment.this,
									"Paired printer not available for printing");
							supporter.simpleNavigateTo(MainMenu.class);
						}
						}




					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						supporter.simpleNavigateTo(MainMenu.class);
					}
				});
		alertDialog.setMessage("Do you want to Print ?");
		////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	protected void navigationProcess(String refNo, Class<?> cls) {
		Intent intent = new Intent(Prepayment.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("referencenumber", refNo);
		startActivity(intent);
	}

	private boolean validateChkReceiptNo(String chkReceiptNo) {

		dbhelpher.openReadableDatabase();

		boolean isExist = dbhelpher.isCheckReceiptExistForPrepayment(
				chkReceiptNo, cmpnyNo);

		dbhelpher.closeDatabase();

		return isExist;
	}

	private boolean validateChkReceiptNoForEdit(String chkReceiptNo,
			String refNum) {

		dbhelpher.openReadableDatabase();

		boolean isExist = dbhelpher.isCheckReceiptExistForPrepaymentForEdit(
				chkReceiptNo, refNum, cmpnyNo);

		dbhelpher.closeDatabase();

		return isExist;
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
			/*TextView title = new TextView(this);
			title.setText("Confirmation");
			title.setTextSize(25);
			alertUser.setCustomTitle(title);*/
			alertUser.setTitle("Confirmation");
			alertUser.setIcon(R.drawable.warning);
			alertUser.setCancelable(false);
			alertUser.setMessage("Do you want to Cancel ?");
			alertUser.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							supporter.clearPreference(shippingDetails,
									entryDetails);
							if (autoReportSetting == 1) {
								callPrinter(refNo);
							} else {
								supporter.simpleNavigateTo(MainMenu.class);
							}
						}
					});

			alertUser.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			//	//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

		}
		return super.onKeyDown(keyCode, event);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
