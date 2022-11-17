package com.mobilesalesperson.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhEmailSetting;
import com.mobilesalesperson.model.HhHistory01;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhPrepayment01;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.mobilesalesperson.xml.FileCustomer;
import com.mobilesalesperson.xml.FilePrepayment;
import com.mobilesalesperson.xml.FileReceipt;
import com.mobilesalesperson.xml.FileTran;

/**
 * @author T.SARAVANAN class used for partial exporting order/invoices and
 *         receipts..
 */
public class Export extends AppBaseActivity {

	private Supporter supporter;
	private MspDBHelper dbHelper;
	private TextView txtCompanyName;
	private CheckBox selectAll;
	private CheckBox posted;
	private Spinner spnSelectTypes;
	private ListView exportItems;
	private List<String> listTypes;
	private ArrayAdapter<String> selectTypeAdapter;
	private String strTypes;
	private String strCheckPosted;

	private List<HhTran01> ordersList;
	private List<HhTran01> invoiceList;
	private List<HhReceipt01> receiptList;
	private ArrayAdapter<HhTran01> orderAdapter;
	private ArrayAdapter<HhTran01> invoiceAdapter;
	private ArrayAdapter<HhReceipt01> receiptAdapter;
	private Map<String, ?> myOrderPro;
	private Map<String, ?> myInvoicePro;
	private Map<String, ?> myReciptPro;
	private SharedPreferences mOrderPrefs;
	private SharedPreferences mInvoicePrefs;
	private SharedPreferences mReciptPrefs;
	private String ordRefNo, invRefNo, docNo;

	private LinearLayout listViewLayout;
	private CheckBox cb;
	private String strTheme;
	private ToastMessage toastMsg;
	private String cmpnyNo; // added for multicompany

	private List<HhTran01> tempTransOrdList;
	private List<HhTran01> tempTransInvoiceList;
	private List<HhReceipt01> tempreceiptList;
	private List<HhPrepayment01> tempPrepayList;
	private List<HhCustomer01> custList;

	private FileCustomer fCust;
	private FileTran fTran;
	private FileReceipt fReceipt;
	private FilePrepayment fPrepayment;

	private String custXMLMsg;
	private String transXMLMsg;
	private String returnXMLMsg;
	private String prepaymentXMLMsg;
	private String receiptXMLMsg;

	private EditText fromDate;
	private EditText toDate;
	private Button loadHistory;
	private TextView txtFromDate;
	private TextView txtToDate;
	private LinearLayout lay_fromdate,lay_todate,lay_btn;

	private static final String SOAP_POST_ACTION = "http://tempuri.org/PostXMLStr";
	private static final String METHOD_POST_NAME = "PostXMLStr";
	private static final String NAMESPACE_POST = "http://tempuri.org/";
	private static final String URL_POST_A = "http://";
	private static final String URL_POST_B = "/amspws/AMSPWS.asmx";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.export_layout);
		registerBaseActivityReceiver();

		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		toastMsg = new ToastMessage();

		txtCompanyName = (TextView) findViewById(R.id.txt_cmpnyName);
		selectAll = (CheckBox) findViewById(R.id.chk_selectAll);
		posted = (CheckBox) findViewById(R.id.chk_posted);
		spnSelectTypes = (Spinner) findViewById(R.id.export_spn_types);
		exportItems = (ListView) findViewById(R.id.lst_ExportItems);

		fromDate = (EditText) findViewById(R.id.edt_ExportFromDate);
		toDate = (EditText) findViewById(R.id.edt_ExportToDate);
		loadHistory = findViewById(R.id.btn_loadTransummary);

		txtFromDate = (TextView) findViewById(R.id.txt_frmdate);
		txtToDate = (TextView) findViewById(R.id.txt_Todate);
		lay_fromdate = findViewById(R.id.EXPFromDate);
		lay_todate = findViewById(R.id.EXPTODate);
		lay_btn = findViewById(R.id.EXPButton);

		txtFromDate.setVisibility(View.GONE);
		txtToDate.setVisibility(View.GONE);
		fromDate.setVisibility(View.GONE);
		toDate.setVisibility(View.GONE);
		loadHistory.setVisibility(View.GONE);
		lay_fromdate.setVisibility(View.GONE);
		lay_todate.setVisibility(View.GONE);
		lay_btn.setVisibility(View.GONE);



		tempTransOrdList = new ArrayList<HhTran01>();
		tempTransInvoiceList = new ArrayList<HhTran01>();
		tempreceiptList = new ArrayList<HhReceipt01>();
		tempPrepayList = new ArrayList<HhPrepayment01>();

		receiptList = new ArrayList<HhReceipt01>();
		invoiceList = new ArrayList<HhTran01>();
		ordersList = new ArrayList<HhTran01>();

		cmpnyNo = supporter.getCompanyNo(); // its used for multicompany

		mOrderPrefs = getSharedPreferences("OrderFile", Context.MODE_PRIVATE);
		mInvoicePrefs = getSharedPreferences("CreditNoteFile",
				Context.MODE_PRIVATE);
		mReciptPrefs = getSharedPreferences("ReceiptFile", Context.MODE_PRIVATE);

		strTheme = supporter.getTheme();
		if (strTheme.equals("PLAIN")) {
			selectAll.setTextColor(Color.BLACK);
			posted.setTextColor(Color.BLACK);
		} else {
			selectAll.setTextColor(Color.BLACK);
			posted.setTextColor(Color.BLACK);
		}
		dbHelper.openReadableDatabase();
		HhCompany company = dbHelper.getCompanyData(cmpnyNo);
		dbHelper.closeDatabase();

		txtCompanyName.setText(company.getCompany_name());

		// default set to new for viewing order list...
		strCheckPosted = "New";

		invoiceAdapter = new ExportCreditNoteAdapter(this,
				new ArrayList<HhTran01>(), mInvoicePrefs, exportItems,
				selectAll);
		receiptAdapter = new ExportReceiptAdapter(this,
				new ArrayList<HhReceipt01>(), mReciptPrefs, exportItems,
				selectAll);

		// Load static selection type formats...
		listTypes = supporter.loadInSelectionTypes();

		// to set the selection types in spinner...
		selectTypeAdapter = new ArrayAdapter<String>(Export.this,
				android.R.layout.simple_dropdown_item_1line, listTypes);
		// selectTypeAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnSelectTypes.setAdapter(selectTypeAdapter);

		// selection Types spinner selected items stored in variables..
		strTypes = (String) spnSelectTypes.getItemAtPosition(spnSelectTypes
				.getSelectedItemPosition());

		// default viewing orderlist....
		viewOrderDatas();

		selectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isChecked = selectAll.isChecked();

				if (isChecked) {

					if (strTypes.equals("Orders")) {
						supporter.setSelectAllCheckedForOrder();
						viewOrderDatas();
					} else if (strTypes.equals("Invoices")) {

						supporter.setSelectAllCheckedForCN();
						viewCreditNoteDatas();

					} else {
						supporter.setSelectAllCheckedForReceipt();
						viewReciptDatas();
					}

				} else {

					unCheckAll();
				}

			}
		});

		// spinner select types on item selected...
		spnSelectTypes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				strTypes = (String) spnSelectTypes
						.getItemAtPosition(spnSelectTypes
								.getSelectedItemPosition());

				if (strTypes.equals("Orders")) {
					int isSelectAllChecked = supporter
							.getSelectAllCheckedForOrder();

					if (isSelectAllChecked == 1) {
						selectAll.setChecked(true);
					} else {
						selectAll.setChecked(false);
					}

					viewOrderDatas();
				} else if (strTypes.equals("Invoices")) {

					int isSelectAllChecked = supporter
							.getSelectAllCheckedForCN();

					if (isSelectAllChecked == 1) {
						selectAll.setChecked(true);
					} else {
						selectAll.setChecked(false);
					}

					viewCreditNoteDatas();
				} else if (strTypes.equals("Receipts")) {

					int isSelectAllChecked = supporter
							.getSelectAllCheckedForReceipt();

					if (isSelectAllChecked == 1) {
						selectAll.setChecked(true);
					} else {
						selectAll.setChecked(false);
					}

					viewReciptDatas();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// checkbox posted method...
		posted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean isChecked) {

				fromDate.setText("");
				toDate.setText("");
				selectAll.setChecked(false);
				unCheckAll();
				supporter.clearPreference(mReciptPrefs);
				supporter.clearPreference(mOrderPrefs);
				supporter.clearPreference(mInvoicePrefs);
				if (isChecked) {
					txtFromDate.setVisibility(View.VISIBLE);
					txtToDate.setVisibility(View.VISIBLE);
					fromDate.setVisibility(View.VISIBLE);
					toDate.setVisibility(View.VISIBLE);
					loadHistory.setVisibility(View.VISIBLE);
					lay_fromdate.setVisibility(View.VISIBLE);
					lay_todate.setVisibility(View.VISIBLE);
					lay_btn.setVisibility(View.VISIBLE);

					strCheckPosted = "Exported";
					if (strTypes.equals("Orders")) {
						viewOrderDatas();
					} else if (strTypes.equals("Invoices")) {
						viewCreditNoteDatas();
					} else if (strTypes.equals("Receipts")) {
						viewReciptDatas();
					}

				} else {
					txtFromDate.setVisibility(View.GONE);
					txtToDate.setVisibility(View.GONE);
					fromDate.setVisibility(View.GONE);
					toDate.setVisibility(View.GONE);
					loadHistory.setVisibility(View.GONE);
					lay_fromdate.setVisibility(View.GONE);
					lay_todate.setVisibility(View.GONE);
					lay_btn.setVisibility(View.GONE);

					strCheckPosted = "New";
					if (strTypes.equals("Orders")) {
						viewOrderDatas();
					} else if (strTypes.equals("Invoices")) {
						viewCreditNoteDatas();
					} else if (strTypes.equals("Receipts")) {
						viewReciptDatas();
					}
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
				DatePickerDialog datePickerDialog  = new DatePickerDialog(Export.this,
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
				DatePickerDialog datePickerDialog  = new DatePickerDialog(Export.this,
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

				if(fromdate.equals("") || Todate.equals("")) {
					toastMsg.showToast(Export.this, "Please select from and to date");
				}
				else{
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
						ordersList.clear();
						orderAdapter.clear();
						orderAdapter.notifyDataSetChanged();
						toastMsg.showToast(Export.this, "Enter valid from and to date");
					} else if (fryear > Tyear) {
						ordersList.clear();
						orderAdapter.clear();
						orderAdapter.notifyDataSetChanged();
						toastMsg.showToast(Export.this, "Enter valid from and to date");
					} else if ((fryear == Tyear) && (frmonth > Tmonth)) {
						ordersList.clear();
						orderAdapter.clear();
						orderAdapter.notifyDataSetChanged();
						toastMsg.showToast(Export.this, "Enter valid from and to date");

					} else if ((fryear == Tyear) && (frday > Tday) && (frmonth > Tmonth)) {
						ordersList.clear();
						orderAdapter.clear();
						orderAdapter.notifyDataSetChanged();

						toastMsg.showToast(Export.this, "Enter valid from and to date");
					} else if (((frmonth > currentMONTH) || (Tmonth > currentMONTH))
							&& (fryear == Tyear)) {
						ordersList.clear();
						orderAdapter.clear();
						orderAdapter.notifyDataSetChanged();
						toastMsg.showToast(Export.this, "Enter valid from and to date");
					} else {
						listLoaded(frday,
								frmonth, fryear, Tday, Tmonth, Tyear);
					}
				}
			}

		});

	}

	private void listLoaded(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear) {

		dbHelper.openWritableDatabase();

		if (strTypes.equals("Orders")) {
			ordersList= dbHelper.getHistoryDateTimeData(frday,
					frmonth, fryear, tday, tmonth, tyear,strTypes);
		}else if(strTypes.equals("Invoices")){
			invoiceList= dbHelper.getHistoryDateTimeData(frday,
					frmonth, fryear, tday, tmonth, tyear,strTypes);
		}else  if(strTypes.equals("Receipts")){
			receiptList= dbHelper.getHistoryDateTimeDataReceipts(frday,
					frmonth, fryear, tday, tmonth, tyear,strTypes);
		}

		dbHelper.closeDatabase();
		if (strTypes.equals("Orders")) {
			if (ordersList != null) {
				orderAdapter = new ExportOrderAdapter(Export.this, ordersList, mOrderPrefs, exportItems, selectAll);
				exportItems.setAdapter(orderAdapter);
			} else {
				exportItems.setAdapter(null);
				orderAdapter.notifyDataSetChanged();
				toastMsg.showToast(Export.this, "No History Data Available");
			}
		} else if (strTypes.equals("Invoices")) {
			if (invoiceList != null) {
				invoiceAdapter = new ExportCreditNoteAdapter(Export.this, invoiceList, mInvoicePrefs, exportItems, selectAll);
				exportItems.setAdapter(invoiceAdapter);
			} else {
				exportItems.setAdapter(null);
				invoiceAdapter.notifyDataSetChanged();
				toastMsg.showToast(Export.this, "No History Data Available");
			}
		} else if (strTypes.equals("Receipts")) {
			if (receiptList != null) {
				receiptAdapter = new ExportReceiptAdapter(Export.this, receiptList, mReciptPrefs, exportItems, selectAll);
				exportItems.setAdapter(receiptAdapter);
			} else {
				exportItems.setAdapter(null);
				receiptAdapter.notifyDataSetChanged();
				toastMsg.showToast(Export.this, "No History Data Available");
			}
		} else {

			toastMsg.showToast(Export.this, "No History Data Available");

		}

	}

	private void unCheckAll() {
		if (strTypes.equals("Orders")) {
			supporter.setSelectAllNotCheckedForOrder();

			supporter.clearPreference(mOrderPrefs);

			orderAdapter = new ExportOrderAdapter(this, ordersList,
					mOrderPrefs, exportItems, selectAll);
			exportItems.setAdapter(orderAdapter);
			exportItems.setSelectionAfterHeaderView();

		} else if (strTypes.equals("Invoices")) {
			supporter.setSelectAllNotCheckedForCN();

			supporter.clearPreference(mInvoicePrefs);

			invoiceAdapter = new ExportCreditNoteAdapter(this, invoiceList,
					mInvoicePrefs, exportItems, selectAll);
			exportItems.setAdapter(invoiceAdapter);
			exportItems.setSelectionAfterHeaderView();

		} else {
			supporter.setSelectAllNotCheckedForReceipt();
			supporter.clearPreference(mReciptPrefs);

			receiptAdapter = new ExportReceiptAdapter(this, receiptList,
					mReciptPrefs, exportItems, selectAll);
			exportItems.setAdapter(receiptAdapter);
			exportItems.setSelectionAfterHeaderView();
		}

	}

	// to set order or invoices list in listview...
	private void viewOrderDatas() {
		// to set distinct order list...

		if (strCheckPosted.equals("New")) {
			selectAll.setEnabled(true);
			dbHelper.openReadableDatabase();
			ordersList = dbHelper.getExportOrderList(strCheckPosted, cmpnyNo);
			dbHelper.closeDatabase();

			if (ordersList.isEmpty()) {
				selectAll.setEnabled(false);
				toastMsg.showToast(Export.this,
						"New Orders data not available...");
			} else {
				int isSelectAllChecked = supporter
						.getSelectAllCheckedForOrder();

				if (isSelectAllChecked == 1) {
					supporter.clearPreference(mOrderPrefs);

					for (int i = 0; i < ordersList.size(); i++) {
						ordRefNo = ordersList.get(i)
								.getHhTran_referenceNumber();
						supporter.saveOrderInPreference(ordRefNo, 1);
					}
					supporter.setSelectAllCheckedForOrder(); // to reset After
																// clear
																// preference
																// above
				}
			}

			orderAdapter = new ExportOrderAdapter(this, ordersList,
					mOrderPrefs, exportItems, selectAll);
			exportItems.setAdapter(orderAdapter);
			exportItems.setSelectionAfterHeaderView();

		} else if (strCheckPosted.equals("Exported")) {
			if (ordersList != null) {
				ordersList.clear();
			}
			orderAdapter.clear();
			selectAll.setEnabled(true);
			dbHelper.openReadableDatabase();
			ordersList = dbHelper.getExportOrderList(strCheckPosted, cmpnyNo);
			dbHelper.closeDatabase();

			if (ordersList.isEmpty()) {
				selectAll.setEnabled(false);
				toastMsg.showToast(Export.this,
						"Exported data not available...");
			} else {
				int isSelectAllChecked = supporter
						.getSelectAllCheckedForOrder();

				if (isSelectAllChecked == 1) {
					supporter.clearPreference(mOrderPrefs);

					for (int i = 0; i < ordersList.size(); i++) {
						ordRefNo = ordersList.get(i)
								.getHhTran_referenceNumber();
						supporter.saveOrderInPreference(ordRefNo, 1);
					}
					supporter.setSelectAllCheckedForOrder(); // to reset After
																// clear
																// preference
																// above
				}
			}

			orderAdapter = new ExportOrderAdapter(this, ordersList,
					mOrderPrefs, exportItems, selectAll);
			exportItems.setAdapter(orderAdapter);
			exportItems.setSelectionAfterHeaderView();

		}

	}

	private void viewCreditNoteDatas() {
		if (strCheckPosted.equals("New")) {
			selectAll.setEnabled(true);
			dbHelper.openReadableDatabase();
			invoiceList = dbHelper.getExportCNList(strCheckPosted, cmpnyNo);
			dbHelper.closeDatabase();

			if (invoiceList.isEmpty()) {
				selectAll.setEnabled(false);
				toastMsg.showToast(Export.this,
						"New CreditNote data not available...");
			} else {

				int isSelectAllChecked = supporter.getSelectAllCheckedForCN();

				if (isSelectAllChecked == 1) {
					supporter.clearPreference(mInvoicePrefs);
					for (int i = 0; i < invoiceList.size(); i++) {
						invRefNo = invoiceList.get(i)
								.getHhTran_referenceNumber();
						supporter.saveCreditNoteInPreference(invRefNo, 1);
					}
					supporter.setSelectAllCheckedForCN(); // to reset After
															// clear preference
															// above
				}

			}

			invoiceAdapter = new ExportCreditNoteAdapter(this, invoiceList,
					mInvoicePrefs, exportItems, selectAll);
			exportItems.setAdapter(invoiceAdapter);
			exportItems.setSelectionAfterHeaderView();

		} else if (strCheckPosted.equals("Exported")) {
			selectAll.setEnabled(true);
			if (invoiceList != null) {
				invoiceList.clear();
			}
			invoiceAdapter.clear();
			dbHelper.openReadableDatabase();
			invoiceList = dbHelper.getExportCNList(strCheckPosted, cmpnyNo);
			dbHelper.closeDatabase();

			if (invoiceList.isEmpty()) {
				selectAll.setEnabled(false);
				toastMsg.showToast(Export.this,
						"Exported data not available...");
			} else {

				int isSelectAllChecked = supporter.getSelectAllCheckedForCN();

				if (isSelectAllChecked == 1) {
					supporter.clearPreference(mInvoicePrefs);
					for (int i = 0; i < invoiceList.size(); i++) {
						invRefNo = invoiceList.get(i)
								.getHhTran_referenceNumber();
						supporter.saveCreditNoteInPreference(invRefNo, 1);
					}
					supporter.setSelectAllCheckedForCN(); // to reset After
															// clear preference
															// above

				}

			}

			invoiceAdapter = new ExportCreditNoteAdapter(this, invoiceList,
					mInvoicePrefs, exportItems, selectAll);
			exportItems.setAdapter(invoiceAdapter);
			exportItems.setSelectionAfterHeaderView();

		}

	}

	private void viewReciptDatas() {

		if (strCheckPosted.equals("New")) {
			selectAll.setEnabled(true);
			dbHelper.openReadableDatabase();
			receiptList = dbHelper.getReceiptsList(strCheckPosted, cmpnyNo);
			dbHelper.closeDatabase();

			if (receiptList.isEmpty()) {
				selectAll.setEnabled(false);
				toastMsg.showToast(Export.this,
						"New Receipt data not available...");
			} else {
				int isSelectAllChecked = supporter
						.getSelectAllCheckedForReceipt();

				if (isSelectAllChecked == 1) {
					supporter.clearPreference(mReciptPrefs);

					for (int i = 0; i < receiptList.size(); i++) {
						docNo = receiptList.get(i).getHhReceipt_receiptnumber();
						supporter.saveReceiptInPreference(docNo, 1);
					}
					supporter.setSelectAllCheckedForReceipt(); // to reset After
																// clear
																// preference
																// above

				}
			}

			receiptAdapter = new ExportReceiptAdapter(this, receiptList,
					mReciptPrefs, exportItems, selectAll);
			exportItems.setAdapter(receiptAdapter);
			exportItems.setSelectionAfterHeaderView();

		} else if (strCheckPosted.equals("Exported")) {
			selectAll.setEnabled(true);
			if(receiptList != null) {
				receiptList.clear();
			}
			receiptAdapter.clear();
			dbHelper.openReadableDatabase();
			receiptList = dbHelper.getReceiptsList(strCheckPosted, cmpnyNo);
			dbHelper.closeDatabase();

			if (receiptList.isEmpty()) {
				selectAll.setEnabled(false);
				toastMsg.showToast(Export.this,
						"Exported data not available...");
			} else {
				int isSelectAllChecked = supporter
						.getSelectAllCheckedForReceipt();

				if (isSelectAllChecked == 1) {
					supporter.clearPreference(mReciptPrefs);

					for (int i = 0; i < receiptList.size(); i++) {
						docNo = receiptList.get(i).getHhReceipt_receiptnumber();
						supporter.saveReceiptInPreference(docNo, 1);
					}
					supporter.setSelectAllCheckedForReceipt(); // to reset After
																// clear
																// preference
																// above

				}
			}

			receiptAdapter = new ExportReceiptAdapter(this, receiptList,
					mReciptPrefs, exportItems, selectAll);
			exportItems.setAdapter(receiptAdapter);
			exportItems.setSelectionAfterHeaderView();
		}

	}

	// method to control menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.export_menu, menu);
		return true;
	}

	// method to control menu options
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnu_export:

			if (strCheckPosted.equals("New")) {
				checkExportMethod();
			} else {
				checkExportMethodForPostedData();
			}

			int sizeTransOrdList = tempTransOrdList.size();
			int sizeTransCNList = tempTransInvoiceList.size();
			int sizereceiptList = tempreceiptList.size();

			if (sizeTransOrdList > 0 || sizeTransCNList > 0
					|| sizereceiptList > 0) {
				new UploadDataCreator().execute();
			} else {
				toastMsg.showToast(Export.this,
						"Export cannot be done without selecting any transaction.");
			}

			break;

		
		 case R.id.mnu_delete:
			 myOrderPro = mOrderPrefs.getAll();
			 myInvoicePro = mInvoicePrefs.getAll();
			 myReciptPro = mReciptPrefs.getAll();
			 final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					 Export.this);
			 alertDialog.setTitle("Confirm Delete");
			 alertDialog.setIcon(R.drawable.dlg_delete);
			 alertDialog.setCancelable(false);
			 alertDialog.setPositiveButton("Yes",
					 new DialogInterface.OnClickListener() {

						 @Override
						 public void onClick(DialogInterface dialog, int which) {
							 dbHelper.openWritableDatabase();
							 Log.i("Writable DB Open", "Writable Database Opened.");
							 dbHelper.mBeginTransaction();
							 Log.i("Transaction started",
									 "Transaction successfully started for Transaction detail page.");

							 // getting data from order preferences...
							 if ((myOrderPro.size() != 0) || (myInvoicePro.size() != 0)) {

								 TransactionDetail tranDetail = new TransactionDetail();
								 for (String key : myOrderPro.keySet()) {
									 // tranDetail.updateItemsBeforeDelete(key);
									 dbHelper.deleteTransaction(key, cmpnyNo);
									 dbHelper.deletePrepayment(key, cmpnyNo);
									 dbHelper.deleteSignature(key, cmpnyNo);
								 }

								 for (String key : myInvoicePro.keySet()) {

									 dbHelper.deleteTransaction(key, cmpnyNo);
									 dbHelper.deletePrepayment(key, cmpnyNo);
									 dbHelper.deleteSignature(key, cmpnyNo);
								 }


							 } else {
								 toastMsg.showToast(Export.this,
										 "No Transactions are selected");
							 }

							 dbHelper.mSetTransactionSuccess(); // setting the
							 // transaction
							 // successfull.
							 Log.i("Transaction success", "Transaction success.");
							 dbHelper.mEndTransaction();
							 Log.i("Transaction success", "Transaction end.");
							 dbHelper.closeDatabase();
							 Log.i("DB closed", "Database closed successfully.");
							 orderAdapter.notifyDataSetChanged();
							 supporter.simpleNavigateTo(MainMenu.class);
						 }
					 });

			 alertDialog.setNegativeButton("No",
					 new DialogInterface.OnClickListener() {

						 @Override
						 public void onClick(DialogInterface dialog, int which) {
							 dialog.dismiss();
						 }
					 });
			 alertDialog
					 .setMessage("Do you want to Delete ?");
			 ////alertDialog.show();
			 AlertDialog alert = alertDialog.create();
			 alert.show();
			 alert.getWindow().getAttributes();
			 TextView textView = (TextView) alert.findViewById(android.R.id.message);
			 textView.setTextSize(29);
			 //deleteListItems();
			 break;
		 

		}
		return true;
	}

	private void deleteListItems() {

	}

	private void checkExportMethod() {
		myOrderPro = mOrderPrefs.getAll();
		myInvoicePro = mInvoicePrefs.getAll();
		myReciptPro = mReciptPrefs.getAll();

		tempTransOrdList.clear();
		tempTransInvoiceList.clear();
		tempreceiptList.clear();
		tempPrepayList.clear(); 

		// getting data from order preferences...
		if (myOrderPro.size() != 0) {
			dbHelper.openReadableDatabase();
			for (String key : myOrderPro.keySet()) {
				tempTransOrdList = dbHelper.getTransactionExportOrderList(key,
						tempTransOrdList, cmpnyNo, false);
				tempPrepayList = dbHelper
						.getPrepaymentList(key, tempPrepayList);
			}
			dbHelper.closeDatabase();
		}

		// getting data from creditnote preferences...
		if (myInvoicePro.size() != 0) {
			dbHelper.openReadableDatabase();
			for (String key : myInvoicePro.keySet()) {
				tempTransInvoiceList = dbHelper
						.getTransactionExportCreditNoteList(key,
								tempTransInvoiceList, cmpnyNo, false);
				tempPrepayList = dbHelper
						.getPrepaymentList(key, tempPrepayList);
			}
			dbHelper.closeDatabase();
		}

		// getting data from receipt preferences...
		if (myReciptPro.size() != 0) {
			dbHelper.openReadableDatabase();
			for (String key : myReciptPro.keySet()) {
				tempreceiptList = dbHelper.getExportReceiptsList(key,
						tempreceiptList, cmpnyNo, false);
			}
			dbHelper.closeDatabase();
		}

	}

	private void checkExportMethodForPostedData() {
		myOrderPro = mOrderPrefs.getAll();
		myInvoicePro = mInvoicePrefs.getAll();
		myReciptPro = mReciptPrefs.getAll();

		tempTransOrdList.clear();
		tempTransInvoiceList.clear();
		tempreceiptList.clear();
		tempPrepayList.clear();

		// getting data from order preferences...
		if (myOrderPro.size() != 0) {
			dbHelper.openReadableDatabase();
			for (String key : myOrderPro.keySet()) {
				tempTransOrdList = dbHelper.getTransactionExportOrderList(key,
						tempTransOrdList, cmpnyNo, true);
				tempPrepayList = dbHelper
						.getPrepaymentList(key, tempPrepayList);
			}
			dbHelper.closeDatabase();
		}

		// getting data from creditnote preferences...
		if (myInvoicePro.size() != 0) {
			dbHelper.openReadableDatabase();
			for (String key : myInvoicePro.keySet()) {
				tempTransInvoiceList = dbHelper
						.getTransactionExportCreditNoteList(key,
								tempTransInvoiceList, cmpnyNo, true);
				tempPrepayList = dbHelper
						.getPrepaymentList(key, tempPrepayList);
			}
			dbHelper.closeDatabase();
		}

		// getting data from receipt preferences...
		if (myReciptPro.size() != 0) {
			dbHelper.openReadableDatabase();
			for (String key : myReciptPro.keySet()) {
				tempreceiptList = dbHelper.getExportReceiptsList(key,
						tempreceiptList, cmpnyNo, true);
			}
			dbHelper.closeDatabase();
		}

	}

	private class UploadDataCreator extends AsyncTask<String, String, String> {

		private ProgressDialog dialog;

		public UploadDataCreator() {
			dialog = new ProgressDialog(Export.this);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {

			String result = "";
			custXMLMsg = "";
			transXMLMsg = "";
			returnXMLMsg = "";
			prepaymentXMLMsg = "";
			receiptXMLMsg = "";

			try {

				dbHelper.openReadableDatabase();
				custList = dbHelper.getNewCustomerDatasObj(cmpnyNo);
				// tranList = dbHelper.getTransactionExportList();
				// tranReturnList = dbHelper.getTransactionExportReturnList();
				// receiptList = dbHelper.getReceiptsList();
				// prepayList = dbHelper.getPrepaymentList();
				dbHelper.closeDatabase();

				fCust = new FileCustomer();
				fTran = new FileTran();
				fReceipt = new FileReceipt();
				fPrepayment = new FilePrepayment();

				// get the list of new customers xml...
				StringBuilder custXMLBuilder = new StringBuilder();
				custXMLBuilder.append("<NewDataSet>");
				for (int i = 0; i < custList.size(); i++) {
					custXMLBuilder = fCust.writeXml(custList.get(i),
							custXMLBuilder);
				}
				custXMLBuilder.append("</NewDataSet>");

				custXMLMsg = custXMLBuilder.toString();

				// get the list of order or quote transactions xml...
				StringBuilder transXMLBuilder = new StringBuilder();
				boolean isReturn = false;
				transXMLBuilder.append("<NewDataSet>");
				for (int i = 0; i < tempTransOrdList.size(); i++) {

					transXMLBuilder.append("<TRAN>");
					transXMLBuilder = fTran.writeXml(tempTransOrdList.get(i),
							transXMLBuilder, isReturn);
					transXMLBuilder.append("</TRAN>");
				}
				// to get sale from invoice
				for (int i = 0; i < tempTransInvoiceList.size(); i++) {

					HhTran01 saleTran = tempTransInvoiceList.get(i);

					String type = saleTran.getHhTran_transType();

					HhHistory01 history01 = new HhHistory01();


					if (type.equals("I")) {
						transXMLBuilder.append("<TRAN>");
						transXMLBuilder = fTran.writeXml(saleTran,
								transXMLBuilder, isReturn);
						transXMLBuilder.append("</TRAN>");
					}

				}
				transXMLBuilder.append("</NewDataSet>");

				transXMLMsg = transXMLBuilder.toString();

				// get the list of return returns xml...
				StringBuilder returnXMLBuilder = new StringBuilder();
				isReturn = true;
				returnXMLBuilder.append("<NewDataSet>");
				for (int i = 0; i < tempTransInvoiceList.size(); i++) {
					HhTran01 cnTran = tempTransInvoiceList.get(i);

					String type = cnTran.getHhTran_transType();

					if (type.equals("CN")) {
						returnXMLBuilder.append("<RETURN>");
						returnXMLBuilder = fTran.writeXml(cnTran,
								returnXMLBuilder, isReturn);
						returnXMLBuilder.append("</RETURN>");
					}

				}
				returnXMLBuilder.append("</NewDataSet>");

				returnXMLMsg = returnXMLBuilder.toString();

				// get the list of prepayment xml...
				StringBuilder prepaymentXMLBuilder = new StringBuilder();
				prepaymentXMLBuilder.append("<NewDataSet>");
				for (int i = 0; i < tempPrepayList.size(); i++) {
					prepaymentXMLBuilder = fPrepayment.writeXml(
							tempPrepayList.get(i), prepaymentXMLBuilder);
				}
				prepaymentXMLBuilder.append("</NewDataSet>");

				prepaymentXMLMsg = prepaymentXMLBuilder.toString();

				// get the list of receipt xml...
				StringBuilder receiptXMLBuilder = new StringBuilder();
				receiptXMLBuilder.append("<NewDataSet>");
				for (int i = 0; i < tempreceiptList.size(); i++) {
					receiptXMLBuilder = fReceipt.writeXml(
							tempreceiptList.get(i), receiptXMLBuilder);
				}
				receiptXMLBuilder.append("</NewDataSet>");

				receiptXMLMsg = receiptXMLBuilder.toString();

				dbHelper.openReadableDatabase();
				HhSetting setting = dbHelper.getSettingData();
				dbHelper.closeDatabase();
				String service = setting.getHhSetting_datasyncservice();

				if (service.equals("Wireless Sync")
						|| service.equals("Wired Sync")) {
					// to write xml string in file
					String CUST_FILE_NAME = "CustXmlToPost.xml";
					FileOutputStream fos = openFileOutput(CUST_FILE_NAME,
							Context.MODE_PRIVATE);
					fos.write(custXMLMsg.getBytes());
					fos.close();

					String TRANS_FILE_NAME = "TransXmlToPost.xml";
					FileOutputStream fos1 = openFileOutput(TRANS_FILE_NAME,
							Context.MODE_PRIVATE);
					fos1.write(transXMLMsg.getBytes());
					fos1.close();

					String RETURN_FILE_NAME = "ReturnXmlToPost.xml";
					FileOutputStream fos2 = openFileOutput(RETURN_FILE_NAME,
							Context.MODE_PRIVATE);
					fos2.write(returnXMLMsg.getBytes());
					fos2.close();

					String PREPAYMENT_FILE_NAME = "PrepaymentXmlToPost.xml";
					FileOutputStream fos3 = openFileOutput(PREPAYMENT_FILE_NAME,
							Context.MODE_PRIVATE);
					fos3.write(prepaymentXMLMsg.getBytes());
					fos3.close();

					String RECEIPT_FILE_NAME = "ReceiptXmlToPost.xml";
					FileOutputStream fos4 = openFileOutput(RECEIPT_FILE_NAME,
							Context.MODE_PRIVATE);
					fos4.write(receiptXMLMsg.getBytes());
					fos4.close();
				}

//				HhTran01 saleTran = tempTransInvoiceList.get(0);
//				String type = saleTran.getHhTran_transType();







					result = "success";
				return result;
			} catch (Exception e) {

				e.printStackTrace();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream stream = new PrintStream(baos);
				e.printStackTrace(stream);
				stream.flush();

				LogfileCreator.appendLog("Error in Upload Data Creation: "
						+ new String(baos.toByteArray()));

				result = "error";
				return result;
			}
		}

		@Override
		protected void onPostExecute(final String result) {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}

//			if(mode.equals("oe")){
//
//			}
			if (result.equals("success")) {
				new DataUploadOperation().execute();
			} else if (result.equals("error")) {
				toastMsg.showToast(Export.this,
						"Error in preparing data to post.");
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Preparing data to post..");// Data uploading
								// started..
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}
	}

	private class DataUploadOperation extends AsyncTask<String, String, String> {

		private ProgressDialog dialog;

		public DataUploadOperation() {
			dialog = new ProgressDialog(Export.this);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {

			String result = "";

			try {
				String salPerson = supporter.getSalesPerson();
				dbHelper.openReadableDatabase();
				HhManager manager = dbHelper.getManagerData(salPerson, cmpnyNo);
				HhSetting setting = dbHelper.getSettingData();
				dbHelper.closeDatabase();
				String cmpName = manager.getHhManager_companyid();
				String service = setting.getHhSetting_datasyncservice();



//				dbHelper.openWritableDatabase();
//				HhTran01 transDetail= new HhTran01();
//
//
//				HhHistory01 history01 = new HhHistory01();
//
//				history01.setHhTran_transType_new(transDetail.getHhTran_transType());
//
//				history01.setHhTran_referenceNumber_new(transDetail.getHhTran_referenceNumber());
//				history01.setHhTran_invoiceNumber_new(transDetail.getHhTran_invoiceNumber());
//				history01.setHhTran_orderNumber_new(transDetail.getHhTran_orderNumber());
//				history01.setHhTran_transDay_new(transDetail.getHhTran_transDay());
//				history01.setHhTran_transMonth_new(transDetail.getHhTran_transMonth());
//				history01.setHhTran_transYear_new(transDetail.getHhTran_transYear());
//				history01.setHhTran_expShipDay_new(transDetail.getHhTran_expShipDay());
//				history01.setHhTran_expShipMonth_new(transDetail.getHhTran_expShipMonth());
//				history01.setHhTran_expShipYear_new(transDetail.getHhTran_expShipYear());
//				history01.setHhTran_customerNumber_new(transDetail.getHhTran_customerNumber());
//				history01.setHhTran_salesPerson_new(transDetail.getHhTran_salesPerson());
//				history01.setHhTran_itemNumber_new(transDetail.getHhTran_itemNumber());
//				history01.setHhTran_locId_new(transDetail.getHhTran_locId());
//				history01.setHhTran_terms_new(transDetail.getHhTran_terms());
//				history01.setHhTran_currency_new(transDetail.getHhTran_currency());
//				history01.setHhTran_priceListCode_new(transDetail.getHhTran_priceListCode());
//				history01.setHhTran_uom_new(transDetail.getHhTran_uom());
//				history01.setHhTran_qty_new(transDetail.getHhTran_qty());
//				history01.setHhTran_price_new(transDetail.getHhTran_price());
//				history01.setHhTran_discPrice_new(transDetail.getHhTran_discPrice());
//				history01.setHhTran_netPrice_new(transDetail.getHhTran_netPrice());
//				history01.setHhTran_extPrice_new(transDetail.getHhTran_extPrice());
//				history01.setHhTran_tax_new(transDetail.getHhTran_tax());
//				history01.setHhTran_shipToCode_new(transDetail.getHhTran_shipToCode());
//				history01.setHhTran_shipViaCode_new(transDetail.getHhTran_shipViaCode());
//				history01.setHhTran_status_new(transDetail.getHhTran_status());
//				history01.setHhTran_lineItem_new(transDetail.getHhTran_lineItem());
//				history01.setHhTran_discValue_new(transDetail.getHhTran_discValue());
//				history01.setHhTran_discType_new(transDetail.getHhTran_discType());
//				history01.setHhTran_ordShipDay_new(transDetail.getHhTran_ordShipDay());
//				history01.setHhTran_ordShipMonth_new(transDetail.getHhTran_ordShipMonth());
//				history01.setHhTran_ordShipYear_new(transDetail.getHhTran_ordShipYear());
//				history01.setHhTran_editable_new(transDetail.getHhTran_editable());
//				history01.setHhTran_manItemNo_new(transDetail.getHhTran_manItemNo());
//				history01.setHhTran_preTax_new(transDetail.getHhTran_preTax());
//				history01.setHhTran_refNo_new(transDetail.getHhTran_refNo());
//				history01.setHhTran_orderdiscount_new(transDetail.getHhTran_orderdiscount());
//				history01.setHhTran_taxamount_new(transDetail.getHhTran_taxamount());
//				history01.setHhTran_totalcarton_new(transDetail.getHhTran_totalcarton());
//				history01.setHhTran_editedcustomername_new(transDetail.getHhTran_editedcustomername());
//				history01.setHhTran_itemunitweight_new(transDetail.getHhTran_itemunitweight());
//				history01.setHhTran_companycode_new(transDetail.getHhTran_companycode());
//				history01.setHhTran_lat_new(transDetail.getHhTran_lat());
//				history01.setHhTran_lon_new(transDetail.getHhTran_lon());
//				dbHelper.addHistory(history01);
//
//				dbHelper.closeDatabase();

				if (service.equals("Wireless Sync")
						|| service.equals("Wired Sync")) { // Wireless Sync
					try {

						SoapObject request = new SoapObject(NAMESPACE_POST,
								METHOD_POST_NAME);
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);

						request.addProperty("tran", transXMLMsg); // trans xml
																	// string
						request.addProperty("cust", custXMLMsg); // customer xml
																	// string
						request.addProperty("recp", receiptXMLMsg); // receipt xml
																	// string
						request.addProperty("ppay", prepaymentXMLMsg); // prepayment
																		// xml
																		// string
						request.addProperty("ret", returnXMLMsg); // return xml
																	// string
						request.addProperty("sCode", salPerson); // salesperson
																	// code
						request.addProperty("companyShortName", cmpName); // company
																			// name
						request.addProperty("companyCode", cmpnyNo); // company code
						// request.addProperty("Sign", ""); // need to clarify
						// request.addProperty("SignAffVal", ""); // need to clarify

						envelope.dotNet = true;// to handle .net services asmx/aspx
						envelope.setOutputSoapObject(request);

						HttpTransportSE ht = new HttpTransportSE(URL_POST_A
								+ setting.getHhSetting_mspserverpath() + URL_POST_B);
						ht.debug = true;
						ht.call(SOAP_POST_ACTION, envelope);

						final SoapPrimitive response = (SoapPrimitive) envelope
								.getResponse();

						if (response != null) {
							String resp = response.toString();
							Log.i("Posting result", resp);
							if (resp.contains("Successfully posted")) {
								result = "success";

							} else {
								LogfileCreator.appendLog("Posting Error result: "
										+ resp);
								result = "failed";
							}
						} else {
							result = "nodata";
						}

						
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
						result = "connection timeout";
					} catch (IOException e) {
						result = "input error";
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						result = "error";
						e.printStackTrace();
					} catch (Exception e) {
						Log.e("tag", "error", e);
						result = "error";
					}

					return result;
				}
				 else if (service.equals("Email")) {


						List<String> lst = new ArrayList<String>();
						String arr[] = new String[5];
						FileOutputStream fos;

						File root_path = Environment.getExternalStorageDirectory();

						String filePath1 = root_path.getAbsoluteFile() + "/"
								+ "Android/MSPFILES";

						arr[0] = "HHNEWCUSTOMER" + cmpnyNo + ".xml";
						arr[1] = "HHTran" + cmpnyNo + ".xml";
						arr[2] = "HHReturn" + cmpnyNo + ".xml";
						arr[3] = "HHPREPAYMENT" + cmpnyNo + ".xml";
						arr[4] = "HHReceipt" + cmpnyNo + ".xml";

						lst.add(filePath1 + "/" + arr[0]);
						lst.add(filePath1 + "/" + arr[1]);
						lst.add(filePath1 + "/" + arr[2]);
						lst.add(filePath1 + "/" + arr[3]);
						lst.add(filePath1 + "/" + arr[4]);

						try {
							for (int i = 0; i < lst.size(); i++) {

								File newFile = new File(filePath1);
								newFile.mkdirs();

								String testFile = arr[i];
								File file1 = new File(filePath1, testFile);
								if (!file1.exists()) {
									try {
										file1.createNewFile();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								if (testFile.equals("HHNEWCUSTOMER" + cmpnyNo
										+ ".xml")) {
									fos = new FileOutputStream(file1);
									custXMLMsg = checkXmlForSpecialCharacter(custXMLMsg);
									fos.write(custXMLMsg.getBytes());
									Log.i("My", "FILE Content--------" + custXMLMsg);
									Log.i("File",
											" CustXmlToPost created Successfully");
									fos.flush();
									fos.close();

								} else if (testFile.equals("HHTran" + cmpnyNo
										+ ".xml")) {
									fos = new FileOutputStream(file1);
									transXMLMsg = checkXmlForSpecialCharacter(transXMLMsg);
									fos.write(transXMLMsg.getBytes());
									Log.i("My", "FILE Content--------"
											+ transXMLMsg);
									Log.i("File",
											" TransXmlToPost created Successfully");
									fos.flush();
									fos.close();
								} else if (testFile.equals("HHReturn" + cmpnyNo
										+ ".xml")) {
									fos = new FileOutputStream(file1);
									returnXMLMsg = checkXmlForSpecialCharacter(returnXMLMsg);
									fos.write(returnXMLMsg.getBytes());
									Log.i("My", "FILE Content--------"
											+ returnXMLMsg);
									Log.i("File",
											" ReturnXmlToPost created Successfully");
									fos.flush();
									fos.close();
								} else if (testFile.equals("HHPREPAYMENT" + cmpnyNo
										+ ".xml")) {
									fos = new FileOutputStream(file1);
									prepaymentXMLMsg = checkXmlForSpecialCharacter(prepaymentXMLMsg);
									fos.write(prepaymentXMLMsg.getBytes());
									Log.i("My", "FILE Content--------"
											+ prepaymentXMLMsg);
									Log.i("File",
											" PrePaymentXmlToPost created Successfully");
									fos.flush();
									fos.close();
								} else if (testFile.equals("HHReceipt" + cmpnyNo
										+ ".xml")) {
									fos = new FileOutputStream(file1);
									receiptXMLMsg = checkXmlForSpecialCharacter(receiptXMLMsg);
									fos.write(receiptXMLMsg.getBytes());
									Log.i("My", "FILE Content--------"
											+ receiptXMLMsg);
									Log.i("File",
											" ReceiptXmlToPost created Successfully");
									fos.flush();
									fos.close();
								}

							}
							result = SendMail(lst, arr);
							Log.i("Return type", "Mail " + result);

						}

						catch (Exception e) {
							result = "error";
							e.printStackTrace();
						}

					 return result;
				 }

			} catch (Exception e) {

				e.printStackTrace();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream stream = new PrintStream(baos);
				e.printStackTrace(stream);
				stream.flush();

				LogfileCreator.appendLog("Error in Data Upload Operation: "
						+ new String(baos.toByteArray()));

				result = "error";
				return result;
			}

			return result;
		}

		private String checkXmlForSpecialCharacter(String xmlSpecChar) {
			// TODO Auto-generated method stub
			String xmlWithoutSpecChar = xmlSpecChar;

			if (xmlWithoutSpecChar.contains("&")) {
				xmlWithoutSpecChar = xmlWithoutSpecChar
						.replaceAll("&", "&amp;");
			}
			if (xmlWithoutSpecChar.contains("'")) {
				xmlWithoutSpecChar = xmlWithoutSpecChar.replaceAll("'",
						"&quot;");
			}

			if (xmlWithoutSpecChar.contains("$")) {
				xmlWithoutSpecChar = xmlWithoutSpecChar.replaceAll("$", "\\$");
			}

			if (xmlWithoutSpecChar.contains(")")) {
				xmlWithoutSpecChar = xmlWithoutSpecChar.replaceAll(")", "\\)");
			}
			if (xmlWithoutSpecChar.contains("(")) {
				xmlWithoutSpecChar = xmlWithoutSpecChar.replaceAll("(", "\\(");
			}

			return xmlWithoutSpecChar;
		}

		private String SendMail(List<String> list, String arr[]) {
			// TODO Auto-generated method stub
			String result = "";
			dbHelper.openReadableDatabase();
			HhEmailSetting email = dbHelper.getEmail();
			dbHelper.closeDatabase();

			final String cmpy_mail = email.getHhEmailSetting_companyemail();
			String cmpy_port = email.getHhEmailSetting_companyportno();
			final String sp_mail = email.getHhEmailSetting_salespersonemail();
			final String sp_pwd = email.getHhEmailSetting_salespersonpwd();
			String sp_host = email.getHhEmailSetting_salespersonhostname();

			// SSL
			/*
			 * Properties props = new Properties();
			 * 
			 * props.put("mail.smtp.host", sp_host);
			 * 
			 * props.put("mail.smtp.socketFactory.port", cmpy_port);
			 * 
			 * props.put("mail.smtp.socketFactory.class",
			 * "javax.net.ssl.SSLSocketFactory");
			 * 
			 * props.put("mail.smtp.auth", "true");
			 */

			// TLS

			Properties props = new Properties();
			props.put("mail.smtp.host", sp_host);
			props.put("mail.smtp.port", cmpy_port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			// If you need to authenticate
			// Use the following if you need SSL
			/*
			 * props.put("mail.smtp.socketFactory.port", cmpy_port);
			 * props.put("mail.smtp.socketFactory.class",
			 * "javax.net.ssl.SSLSocketFactory");
			 * props.put("mail.smtp.socketFactory.fallback", "false");
			 */
			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {

						protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
							return new javax.mail.PasswordAuthentication(
									sp_mail, sp_pwd);
						}

					});

			try {

				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(sp_mail));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(cmpy_mail));
				// Setting Subject
				message.setSubject("FROM MSP");

				Multipart multipart = new MimeMultipart();
				String spCode = supporter.getTempSalesPersonCode();
				BodyPart messageBodyPart = new MimeBodyPart();

				// Invoking Company Name by Company No
				/* String cmpNo = supporter.getCompanyNo().toString(); */

				dbHelper.openReadableDatabase();
				/* HhCompany cmpy=dbHelper.getCompanyData(cmpNo); */
				HhManager manager = dbHelper.getManagerData(spCode,cmpnyNo);
				String cmpName = manager.getHhManager_companyid();
				dbHelper.closeDatabase();

				// Setting the Body COntent
				String body = spCode + "-" + cmpName;
				messageBodyPart.setText(body.toUpperCase());
				multipart.addBodyPart(messageBodyPart);
				// Attaching all the files for sending mail
				for (int i = 0; i < list.size(); i++) {

					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource((String) list.get(i));
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName((String) arr[i]);
					multipart.addBodyPart(messageBodyPart);

					message.setContent(multipart);

				}

				// Attach all things to message and send it
				message.setContent(multipart);
				// Sending Mail with Attachment

				Transport.send(message);
				System.out.println("Mail Sent Successfully?.");
				result = "success";

			}

			catch (MessagingException e) {

				result = "failed";
			}
			return result;

		}

		@Override
		protected void onPostExecute(final String result) {

//			dbHelper.openWritableDatabase();
//			dbHelper.getTransData(result);
//			dbHelper.closeDatabase();
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}



			if (result.equals("success")) {



//				    HhHistory01 history01 = new HhHistory01();
//					Cursor data = dbHelper.getData();
//				ArrayList<String > list = new ArrayList<>();
//				while(data.moveToNext()) {
//					list.add(data.getString(1));
//				}
//				history01.setHhTran_transType_new();
//
//				history01.setHhTran_referenceNumber_new(data.getString(1));
//				history01.setHhTran_invoiceNumber_new(data.getString(2));
//				history01.setHhTran_orderNumber_new(data.getString(3));
//				history01.setHhTran_transDay_new(data.getInt(4));
//				history01.setHhTran_transMonth_new(data.getInt(5));
//				history01.setHhTran_transYear_new(data.getInt(6));
//				history01.setHhTran_expShipDay_new(data.getInt(7));
//				history01.setHhTran_expShipMonth_new(data.getInt(8));
//				history01.setHhTran_expShipYear_new(data.getInt(9));
//				history01.setHhTran_customerNumber_new(data.getString(10));
//				history01.setHhTran_salesPerson_new(data.getString(11));
//				history01.setHhTran_itemNumber_new(data.getString(12));
//				history01.setHhTran_locId_new(data.getString(13));
//				history01.setHhTran_terms_new(data.getString(14));
//				history01.setHhTran_currency_new(data.getString(15));
//				history01.setHhTran_priceListCode_new(data.getString(16));
//				history01.setHhTran_uom_new(data.getString(17));
//				history01.setHhTran_qty_new(data.getInt(18));
//				history01.setHhTran_price_new(data.getInt(19));
//				history01.setHhTran_discPrice_new(data.getInt(20));
//				history01.setHhTran_netPrice_new(data.getInt(21));
//				history01.setHhTran_extPrice_new(data.getInt(22));
//				history01.setHhTran_tax_new(data.getInt(23));
//				history01.setHhTran_shipToCode_new(data.getString(24));
//				history01.setHhTran_shipViaCode_new(data.getString(25));
//				history01.setHhTran_status_new(data.getInt(26));
//				history01.setHhTran_lineItem_new(data.getInt(27));
//				history01.setHhTran_discValue_new(data.getInt(28));
//				history01.setHhTran_discType_new(data.getString(29));
//				history01.setHhTran_ordShipDay_new(data.getInt(30));
//				history01.setHhTran_ordShipMonth_new(data.getInt(31));
//				history01.setHhTran_ordShipYear_new(data.getInt(32));
//				history01.setHhTran_editable_new(data.getInt(33));
//				history01.setHhTran_manItemNo_new(data.getString(34));
//				history01.setHhTran_preTax_new(data.getInt(35));
//				history01.setHhTran_refNo_new(data.getString(36));
//				history01.setHhTran_orderdiscount_new(data.getInt(37));
//				history01.setHhTran_taxamount_new(data.getInt(38));
//				history01.setHhTran_totalcarton_new(data.getInt(39));
//				history01.setHhTran_editedcustomername_new(data.getString(40));
//				history01.setHhTran_itemunitweight_new(data.getInt(41));
//				history01.setHhTran_companycode_new(data.getString(42));
//				history01.setHhTran_lat_new(data.getInt(43));
//				history01.setHhTran_lon_new(data.getInt(44));
//				dbHelper.addHistory(history01);

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						Export.this);
				alertDialog.setTitle("Information");
				alertDialog.setIcon(R.drawable.tick);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

								// change status flag to 1 in trans, customer,
								// prepayment and
								// receipt table.
								dbHelper.openWritableDatabase();
								Log.i("Writable DB Open",
										"Writable Database Opened.");

								dbHelper.mBeginTransaction();
								Log.i("Transaction started",
										"Transaction successfully started for status flag update.");

								// getting data from order preferences...
								if (myOrderPro.size() != 0) {

									for (String key : myOrderPro.keySet()) {
										dbHelper.statusUpdateTran(key, cmpnyNo);
										dbHelper.statusUpdatePrepayment(key,
												cmpnyNo);


									}
								}

								// getting data from creditnote preferences...
								if (myInvoicePro.size() != 0) {

									for (String key : myInvoicePro.keySet()) {
										dbHelper.statusUpdateInvoice(key,
												cmpnyNo);
										dbHelper.statusUpdatePrepayment(key,
												cmpnyNo);
									}

								}

								// getting data from receipt preferences...
								if (myReciptPro.size() != 0) {

									for (String key : myReciptPro.keySet()) {
										dbHelper.statusUpdateReceipt(key,
												cmpnyNo);
									}

								}

								dbHelper.statusUpdateCustomer(cmpnyNo);


								dbHelper.getTransData("success");




								dbHelper.mSetTransactionSuccess(); // setting
																	// the
																	// transaction
																	// successfull.

								Log.i("Transaction success",
										"Transaction success.");
								dbHelper.mEndTransaction();
								Log.i("Transaction success", "Transaction end.");
								dbHelper.closeDatabase();
								Log.i("DB closed",
										"Database closed successfully.");

								supporter.clearPreference(mReciptPrefs);
								supporter.clearPreference(mOrderPrefs);
								supporter.clearPreference(mInvoicePrefs);

								supporter.simpleNavigateTo(MainMenu.class);
							}

						});

				alertDialog.setMessage("Data Exported Successfully.");
				//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

			} else if (result.equals("failed")) { // should change
				toastMsg.showToast(Export.this, "Failure in posting data.");
			} else if (result.equals("nodata")) {
				toastMsg.showToast(Export.this, "No response from server.");
			} else if (result.equals("connection timeout")) {
				toastMsg.showToast(Export.this,
						"Data not posted!. connection timeout.");
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Data uploading process started..");
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			exportCancelMethod();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	/*private void updateItem(String key)
	{
		List<HhTran01> tranListToUpdateItem =dbHelper 
				.getTranDetForItemUpdate(key, cmpnyNo);

		double qanty = 0.0;
		String uom = "";
		double conFact = 0.0;
		double rQty = 0.0;

		for (int i = 0; i < tranListToUpdateItem.size(); i++) {

			HhTran01 tranItem = tranListToUpdateItem.get(i);
			String tranType = tranItem.getHhTran_transType();
			qanty = (double) tranItem.getHhTran_qty();
			uom = tranItem.getHhTran_uom();

			conFact = dbHelper.getUOMConvFactor(
					tranItem.getHhTran_itemNumber(), uom, cmpnyNo);

			rQty = qanty * conFact;
			if (tranType.equals("I")) {

				dbHelper.updateItemOnHand(tranItem.getHhTran_itemNumber(),
						"add", rQty, tranItem.getHhTran_locId() + "",
						cmpnyNo);

			} else {

				dbHelper.updateItemOnHand(tranItem.getHhTran_itemNumber(),
						"remove", rQty, tranItem.getHhTran_locId() + "",
						cmpnyNo);

			}
		}
	}*/
	
	private void exportCancelMethod() {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Export.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						supporter.clearPreference(mReciptPrefs);
						supporter.clearPreference(mOrderPrefs);
						supporter.clearPreference(mInvoicePrefs);
						supporter.simpleNavigateTo(MainMenu.class);
					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setMessage("Do you want to Cancel ?");
		//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
