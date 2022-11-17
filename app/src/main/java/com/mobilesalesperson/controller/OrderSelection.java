package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

public class OrderSelection extends AppBaseActivity {

	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private EditText edtOrdTxtSearch;
	private ListView orderList;
	private List<HhTran01> orderSelectionList;
	private ArrayAdapter<HhTran01> adptOrderSelection;
	private String mode;
	private String cmpnyNo; // added for multicompany
    private SharedPreferences orderDetails;
	private ToastMessage toastMsg;
	private EditText fromDate;
	private EditText toDate;
	private Button loadHistory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.order_selection_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		orderDetails=getSharedPreferences("orderData", Context.MODE_PRIVATE);
		
		edtOrdTxtSearch = (EditText) findViewById(R.id.edtTxt_OrdSearch);
		orderList = (ListView) findViewById(R.id.lst_ordersList);

		fromDate = (EditText) findViewById(R.id.edt_OrdInvFromDate);
		toDate = (EditText) findViewById(R.id.edt_OrdInvToDate);
		loadHistory = findViewById(R.id.btn_OrdInvReport);

		mode = supporter.getMode();
		cmpnyNo = supporter.getCompanyNo();// added jul 15/2013
		toastMsg = new ToastMessage();

		orderSelectionList = new ArrayList<HhTran01>();
		dbhelpher.openReadableDatabase();
		if (mode.equals("report")) {
			orderSelectionList = dbhelpher.getTransDocNoList(0, 0, 0, "",
					cmpnyNo); // to
			// get
			// all
			// records
		} else if (mode.equals("orderEdit") || mode.equals("invoiceEdit")) {
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int month = c.get(Calendar.MONTH);
			int year = c.get(Calendar.YEAR);

			orderSelectionList = dbhelpher.getTransDocNoList(day, month + 1,
					year, mode, cmpnyNo); // to get current date records
		} else if (mode.equals("orderDelete")) {
			orderSelectionList = dbhelpher.getTransDocNoList(0, 0, 0, mode,
					cmpnyNo); // to
			// get
			// all
			// records
		} else if (mode.equals("invoiceDelete")) {
			orderSelectionList = dbhelpher.getTransDocNoList(0, 0, 0, mode,
					cmpnyNo); // to
			// get
			// all
			// records
		}
		dbhelpher.closeDatabase();

		if (orderSelectionList.size() == 0) {
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
			adptOrderSelection = new OrderSelectionAdapter(OrderSelection.this,
					orderSelectionList, dbhelpher);
			orderList.setAdapter(adptOrderSelection);
			adptOrderSelection.notifyDataSetChanged();

		}

		edtOrdTxtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				OrderSelection.this.adptOrderSelection.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		orderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				HhTran01 tranData = (HhTran01) adptOrderSelection
						.getItem(position);

				String refNo = tranData.getHhTran_referenceNumber();
				String docNo = tranData.getHhTran_refNo();

				String custNumber = tranData.getHhTran_customerNumber();

				if (mode.equals("report") || mode.equals("orderDelete")
						|| mode.equals("invoiceDelete")) {
					navigationProcess(refNo, TransactionDetail.class);
				} else if (mode.equals("orderEdit")
						|| mode.equals("invoiceEdit")) {

					dbhelpher.openReadableDatabase();
					List<HhTran01> tranList = dbhelpher.getTransactionList(
							refNo, cmpnyNo);

					// String custName =
					// dbhelpher.getCustomerName(tranData.getHhTran_customerNumber());
					// old method

					String custName = tranList.get(0)
							.getHhTran_editedcustomername();

					dbhelpher.closeDatabase();

					SharedPreferences entryDetails = getSharedPreferences(
							"entryDetails", Context.MODE_PRIVATE);

					String ordType = "";
					String strPriceList = tranList.get(0)
							.getHhTran_priceListCode();
					String strLocDet = tranList.get(0).getHhTran_shipToCode();
					String strViaCode = tranList.get(0).getHhTran_shipViaCode();
					String strShipDate = supporter.getStringDate(tranList
							.get(0).getHhTran_ordShipYear(), tranList.get(0)
							.getHhTran_ordShipMonth(), tranList.get(0)
							.getHhTran_ordShipDay());

					for (int i = 0; i < tranList.size(); i++) {

						HhTran01 tran = tranList.get(i);

						String tranType = tran.getHhTran_transType();
						String entType = "";

						if (tranType.equals("S") || tranType.equals("Q")) {
							entType = "oe";

							if (i == 0) {
								if (tranType.equals("S")) {
									ordType = "Order";
									Editor ed = entryDetails.edit();
									ed.putString("entryName", ordType);
									ed.commit();
								} else {
									ordType = "Quote";
									Editor ed = entryDetails.edit();
									ed.putString("entryName", ordType);
									ed.commit();
								}
							}

						} else if (tranType.equals("I")) {
							entType = "ts";
						} else if (tranType.equals("CN")) {
							entType = "tr";
						}

						int qntty = tran.getHhTran_qty();

						TempItem tempItem = new TempItem();

						tempItem.setTemp_entryType(entType);
						tempItem.setTemp_itemNum(tran.getHhTran_itemNumber());
						tempItem.setTemp_qty("" + qntty);
						tempItem.setTemp_extPrice(""
								+ (tran.getHhTran_price() * (double) qntty));
						tempItem.setTemp_location(tran.getHhTran_locId());
						tempItem.setTemp_uom(tran.getHhTran_uom());
						tempItem.setTemp_date(supporter.getStringDate(
								tran.getHhTran_expShipYear(),
								tran.getHhTran_expShipMonth(),
								tran.getHhTran_expShipDay()));
						tempItem.setTemp_discount(tran.getHhTran_discValue()
								+ "");
						tempItem.setTemp_discType(tran.getHhTran_discType());

						dbhelpher.openWritableDatabase();
						dbhelpher.addTempItem(tempItem);
						dbhelpher.closeDatabase();

					}

					supporter.addShippingDetails(custNumber, custName,
							strLocDet, strViaCode, strShipDate);
					supporter.setPriceList(strPriceList);

					Editor ed = entryDetails.edit();
					ed.putString("referencenumber", refNo);
					ed.putString("docno", docNo);
					ed.commit();

					// to set signature in preference to display it in signature
					// screen
					SharedPreferences signPreference = getSharedPreferences(
							"signPref", Context.MODE_PRIVATE);
					dbhelpher.openReadableDatabase();
					byte[] signByte = dbhelpher.getSignature(refNo, cmpnyNo);
					dbhelpher.closeDatabase();

					if (signByte != null) {
						Bitmap mBitmap = BitmapFactory.decodeByteArray(
								signByte, 0, signByte.length);
						String encodedSign = supporter
								.encodeImageToString(mBitmap); // encode sign
																// image into
																// string format

						// to store string image in preference
						Editor edt = signPreference.edit();
						edt.putString("signature", encodedSign);
						edt.commit();
					}

					Intent intent = new Intent(OrderSelection.this,
							Transaction.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("orderType", ordType);
					intent.putExtra("orderNum", refNo);
					startActivity(intent);

				}

			}
		});

		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);

		fromDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog  = new DatePickerDialog(OrderSelection.this,
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
				DatePickerDialog datePickerDialog  = new DatePickerDialog(OrderSelection.this,
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
					orderSelectionList.clear();
					adptOrderSelection.clear();
					adptOrderSelection.notifyDataSetChanged();
					toastMsg.showToast(OrderSelection.this, "Enter valid from and to date");
				} else if (fryear > Tyear) {
					orderSelectionList.clear();
					adptOrderSelection.clear();
					adptOrderSelection.notifyDataSetChanged();
					toastMsg.showToast(OrderSelection.this, "Enter valid from and to date");
				} else if ((fryear == Tyear) && (frmonth > Tmonth)) {
					orderSelectionList.clear();
					adptOrderSelection.clear();
					adptOrderSelection.notifyDataSetChanged();
					toastMsg.showToast(OrderSelection.this, "Enter valid from and to date");

				} else if ((fryear == Tyear) && (frday > Tday) && (frmonth > Tmonth)) {
					orderSelectionList.clear();
					adptOrderSelection.clear();
					adptOrderSelection.notifyDataSetChanged();

					toastMsg.showToast(OrderSelection.this, "Enter valid from and to date");
				} else if ((fryear == Tyear) && (frday > Tday) && (frmonth == Tmonth)) {
					orderSelectionList.clear();
					adptOrderSelection.clear();
					adptOrderSelection.notifyDataSetChanged();

					toastMsg.showToast(OrderSelection.this, "Enter valid from and to date");
				}else if (((frmonth > currentMONTH) || (Tmonth > currentMONTH))
						&& (fryear == Tyear)) {
					orderSelectionList.clear();
					adptOrderSelection.clear();
					adptOrderSelection.notifyDataSetChanged();
					toastMsg.showToast(OrderSelection.this, "Enter valid from and to date");
				} else {
					listLoaded(frday,
							frmonth, fryear, Tday, Tmonth, Tyear);
				}
			}

		});




	}

	private void listLoaded(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear) {

		dbhelpher.openWritableDatabase();
		orderSelectionList= dbhelpher.getOrdInvData(frday,
				frmonth, fryear, tday, tmonth, tyear,cmpnyNo);
		/*    list1 = db.getHistoryData();*/
		dbhelpher.closeDatabase();

		if(orderSelectionList !=  null)  {
			adptOrderSelection = new OrderSelectionAdapter(OrderSelection.this, orderSelectionList,dbhelpher);
			orderList.setAdapter(adptOrderSelection);


		}else{
			orderList.setAdapter(null);
			adptOrderSelection.notifyDataSetChanged();
			toastMsg.showToast(OrderSelection.this, "No Order/Invoice Data Available");


		}

	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			supporter.simpleNavigateTo(MainMenu.class);
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void navigationProcess(String refno, Class<?> cls) {

		Intent intent = new Intent(OrderSelection.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("referencenumber", refno);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
