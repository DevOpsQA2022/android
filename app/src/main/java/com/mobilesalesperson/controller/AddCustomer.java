package com.mobilesalesperson.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhTaxGroup01;
import com.mobilesalesperson.model.Mspdb;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

public class AddCustomer extends AppBaseActivity {

	private Supporter supporter;
	private MspDBHelper dbhelpher;
	private TextView txtAddCusTitle;
	private EditText edtTxtCustNumber;
	private EditText edtTxtCustName;
	private EditText edtTxtCustAddress;
	private EditText edtTxtCustCity;
	private EditText edtTxtCustPhone;
	private EditText edtTxtCustTermsCode;
	private EditText edtTxtCustComment;
	private EditText edtTextCustAltNo;
	private Spinner spnCustGroupCode;
	private ArrayAdapter<String> adtpCustGroupCodeSpinner;
	private Spinner spnCustType;
	private ArrayAdapter<String> adptCustTypeSpinnerAdapter;
	private Spinner spnCustPriceList;
	private ArrayAdapter<String> adptCustPriceListSpinner;
	private Button btnSaveCustomer;
	private TextView txtSetTax;
	private List<String> custGroupCodeList;
	private List<String> custTypeList;
	private List<String> custPriceList;
	private String currentGroupCode;
	private String currentTerms;
	private String currentCustType;
	private String currentPrcList;
	private Mspdb mspdb;
	private String taxGroup;
	private String strTaxClassValue1;
	private String strTaxClassValue2;
	private String strTaxClassValue3;
	private String strTaxClassValue4;
	private String strTaxClassValue5;
	private String mode;
	private HhCustomer01 customer;

	private ToastMessage toastMsg;
	private List<String> taxClassList1;
	private List<String> taxClassList2;
	private List<String> taxClassList3;
	private List<String> taxClassList4;
	private List<String> taxClassList5;

	private String taxauthority1;
	private String taxauthority2;
	private String taxauthority3;
	private String taxauthority4;
	private String taxauthority5;
	private String cmpnyNo; // added for multicompany

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.add_customer_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		txtAddCusTitle = (TextView) findViewById(R.id.txt_AddCusTitle);
		edtTxtCustNumber = (EditText) findViewById(R.id.edtTxt_CustNo);
		edtTxtCustName = (EditText) findViewById(R.id.edtTxt_CustName);
		edtTxtCustAddress = (EditText) findViewById(R.id.edtTxt_CustAdd);
		edtTxtCustCity = (EditText) findViewById(R.id.edtTxt_CustCity);
		edtTxtCustPhone = (EditText) findViewById(R.id.edtTxt_CustPhone);
		edtTextCustAltNo = (EditText) findViewById(R.id.Customer_Alt_no);
		spnCustGroupCode = (Spinner) findViewById(R.id.spn_CustGroupCode);
		edtTxtCustTermsCode = (EditText) findViewById(R.id.edtTxt_CustTermsCode);
		spnCustType = (Spinner) findViewById(R.id.spn_CustType);
		spnCustPriceList = (Spinner) findViewById(R.id.spn_AddCustPriceList);
		edtTxtCustComment = (EditText) findViewById(R.id.edtTxt_CustComment);

		btnSaveCustomer = (Button) findViewById(R.id.btn_AddCustOk);
		txtSetTax = (TextView) findViewById(R.id.txt_AddCustSetTax);
		underlineText(txtSetTax);

		cmpnyNo = supporter.getCompanyNo(); // added jul 12/2013

		taxGroup = "";
		strTaxClassValue1 = "";
		strTaxClassValue2 = "";
		strTaxClassValue3 = "";
		strTaxClassValue4 = "";
		strTaxClassValue5 = "";

		// to load customer group code
		dbhelpher.openReadableDatabase();
		custGroupCodeList = dbhelpher.getCustomerGroupCode(cmpnyNo);
		dbhelpher.closeDatabase();

		adtpCustGroupCodeSpinner = new ArrayAdapter<String>(AddCustomer.this,
				android.R.layout.simple_dropdown_item_1line, custGroupCodeList);
		// adtpCustGroupCodeSpinner.setDropDownViewResource(R.layout.spinner_item_layout);
		spnCustGroupCode.setAdapter(adtpCustGroupCodeSpinner);
		currentGroupCode = "";
		currentGroupCode = (String) spnCustGroupCode
				.getItemAtPosition(spnCustGroupCode.getSelectedItemPosition());

		dbhelpher.openReadableDatabase();
		currentTerms = dbhelpher.getCustomerTerms(currentGroupCode, cmpnyNo);
		dbhelpher.closeDatabase();

		edtTxtCustTermsCode.setText(currentTerms);

		// to load customer type
		custTypeList = loadCustomerType();
		adptCustTypeSpinnerAdapter = new ArrayAdapter<String>(AddCustomer.this,
				android.R.layout.simple_dropdown_item_1line, custTypeList);
		// adptCustTypeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_layout);

		spnCustType.setAdapter(adptCustTypeSpinnerAdapter);

		// to load customer price list
		dbhelpher.openReadableDatabase();
		custPriceList = dbhelpher.getPriceList(cmpnyNo);
		dbhelpher.closeDatabase();
		adptCustPriceListSpinner = new ArrayAdapter<String>(AddCustomer.this,
				android.R.layout.simple_dropdown_item_1line, custPriceList);
		// adptCustPriceListSpinner.setDropDownViewResource(R.layout.spinner_item_layout);

		spnCustPriceList.setAdapter(adptCustPriceListSpinner);

		edtTxtCustNumber.setFocusable(false); // user not allowed to change
		// number since mspdb is
		// changed
		// using this generated
		// number

		mode = supporter.getMode();
		if (mode.equals("addcust")) {
			txtAddCusTitle.setText("Add Customer");

			dbhelpher.openReadableDatabase();
			mspdb = dbhelpher.getMspDbData(cmpnyNo, supporter.getSalesPerson());
			dbhelpher.closeDatabase();

			int number = mspdb.getMspdb_customerNumber();
			String newCustNo = "MSP-" + mspdb.getMspdb_mapNo() + "-"
					+ String.format("%04d", number);
			edtTxtCustNumber.setText(newCustNo);

		} else {
			txtAddCusTitle.setText("Edit Customer");
			String custNo = getIntent().getStringExtra("customerNumber");

			dbhelpher.openReadableDatabase();
			customer = dbhelpher.getCustomerData(custNo, cmpnyNo);
			dbhelpher.closeDatabase();

			edtTxtCustNumber.setText(custNo);
			edtTxtCustName.setText(customer.getHhCustomer_name());
			edtTxtCustAddress.setText(customer.getHhCustomer_address());
			edtTxtCustCity.setText(customer.getHhCustomer_city());
			edtTxtCustPhone.setText(customer.getHhCustomer_phone1());
			edtTextCustAltNo.setText(customer.getHhCustomer_customer_alt_no());

			String grpCode = customer.getHhCustomer_groupcode();
			int index1 = custGroupCodeList.indexOf(grpCode);
			spnCustGroupCode.setSelection(index1);

			String cusType = customer.getHhCustomer_type();
			int index2 = custTypeList.indexOf(cusType);
			spnCustType.setSelection(index2);

			String przLst = customer.getHhCustomer_pricelistcode();
			int index3 = custPriceList.indexOf(przLst);
			spnCustPriceList.setSelection(index3);

			edtTxtCustComment.setText(customer.getHhCustomer_comment());

			taxGroup = customer.getHhCustomer_taxgroup();

		}

		spnCustGroupCode
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						currentGroupCode = (String) spnCustGroupCode
								.getItemAtPosition(spnCustGroupCode
										.getSelectedItemPosition());

						dbhelpher.openReadableDatabase();
						currentTerms = dbhelpher.getCustomerTerms(
								currentGroupCode, cmpnyNo);
						dbhelpher.closeDatabase();

						edtTxtCustTermsCode.setText(currentTerms);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		currentPrcList = (String) spnCustPriceList
				.getItemAtPosition(spnCustPriceList.getSelectedItemPosition());

		btnSaveCustomer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String cNum = edtTxtCustNumber.getText().toString();
					String cName = edtTxtCustName.getText().toString();
					String add = edtTxtCustAddress.getText().toString();
					String city = edtTxtCustCity.getText().toString();
					String phone = edtTxtCustPhone.getText().toString();
					//String cus_alt_no  =edtTextCustAltNo.getText().toString();
					String cusType = "0";

					currentCustType = (String) spnCustType
							.getItemAtPosition(spnCustType
									.getSelectedItemPosition());

					if (currentCustType.equals("Base")) {
						cusType = "0";
					} else if (currentCustType.equals("A")) {
						cusType = "1";

					} else if (currentCustType.equals("B")) {
						cusType = "2";
					} else if (currentCustType.equals("C")) {
						cusType = "3";
					} else if (currentCustType.equals("D")) {
						cusType = "4";
					} else if (currentCustType.equals("E")) {
						cusType = "5";
					}

					if (!cNum.equals("") && !cName.equals("")
							&& !add.equals("") && !city.equals("")
							&& !phone.equals("")) {

						if (taxGroup.equals("")) {
							toastMsg.showToast(AddCustomer.this,
									"Tax need to be set");
						} else {
							String[] arrState = new String[2];
							String[] arrCounty = new String[2];
							String[] arrTaxClass3 = new String[2];
							String[] arrTaxClass4 = new String[2];
							String[] arrTaxClass5 = new String[2];

							arrState[0] = "0";
							arrCounty[0] = "0";
							arrTaxClass3[0] = "0";
							arrTaxClass4[0] = "0";
							arrTaxClass5[0] = "0";

							if (!strTaxClassValue1.equals("")) {
								arrState = strTaxClassValue1.split(" ");
							}

							if (!strTaxClassValue2.equals("")) {
								arrCounty = strTaxClassValue2.split(" ");
							}

							if (!strTaxClassValue3.equals("")) {
								arrTaxClass3 = strTaxClassValue3.split(" ");
							}

							if (!strTaxClassValue4.equals("")) {
								arrTaxClass4 = strTaxClassValue4.split(" ");
							}

							if (!strTaxClassValue5.equals("")) {
								arrTaxClass5 = strTaxClassValue5.split(" ");
							}

							if (mode.equals("editdeletcust")
									&& strTaxClassValue1.equals("")) {
								arrState[0] = customer.getHhCustomer_taxstts1();
								arrCounty[0] = customer
										.getHhCustomer_taxstts2();
								arrTaxClass3[0] = customer
										.getHhCustomer_taxstts3();
								arrTaxClass4[0] = customer
										.getHhCustomer_taxstts4();
								arrTaxClass5[0] = customer
										.getHhCustomer_taxstts5();
										
								if(taxauthority1==null){
									taxauthority1 = customer.getHhCustomer_taxauthority1();
									taxauthority2 = customer.getHhCustomer_taxauthority2();
									taxauthority3 = customer.getHhCustomer_taxauthority3();
									taxauthority4 = customer.getHhCustomer_taxauthority4();
									taxauthority5 = customer.getHhCustomer_taxauthority5();
								}		
							}

							if (currentGroupCode == null) {
								currentGroupCode = "";
							}

							// to add new customer
							HhCustomer01 customer = new HhCustomer01();

							customer.setHhCustomer_number(cNum);
							customer.setHhCustomer_name(cName);
							customer.setHhCustomer_address(add);
							customer.setHhCustomer_city(city);
							customer.setHhCustomer_phone1(phone);
							customer.setHhCustomer_groupcode(currentGroupCode);
							customer.setHhCustomer_terms(currentTerms);
							customer.setHhCustomer_type(cusType);
							customer.setHhCustomer_pricelistcode(currentPrcList);
							customer.setHhCustomer_comment(edtTxtCustComment
									.getText().toString() + "");
							customer.setHhCustomer_status(0);
							customer.setHhCustomer_taxgroup(taxGroup);
							customer.setHhCustomer_taxauthority1(taxauthority1);
							customer.setHhCustomer_taxstts1(arrState[0]);
							customer.setHhCustomer_taxauthority2(taxauthority2);
							customer.setHhCustomer_taxstts2(arrCounty[0]);
							customer.setHhCustomer_taxauthority3(taxauthority3);
							customer.setHhCustomer_taxstts3(arrTaxClass3[0]);
							customer.setHhCustomer_taxauthority4(taxauthority4);
							customer.setHhCustomer_taxstts4(arrTaxClass4[0]);
							customer.setHhCustomer_taxauthority5(taxauthority5);
							customer.setHhCustomer_taxstts5(arrTaxClass5[0]);
							customer.setHhCustomer_companycode(cmpnyNo);
							//customer.setHhCustomer_customer_alt_no(cus_alt_no);
							customer.setHhCustomer_shipvia("");

							if (mode.equals("addcust")) {

								dbhelpher.openWritableDatabase();
								Log.i("Writable DB Open",
										"Writable Database Opened.");
								dbhelpher.mBeginTransaction();
								Log.i("Transaction started",
										"Transaction successfully started for customer adding.");
								dbhelpher.addCustomer(customer);
								mspdb.setMspdb_companyNumber(cmpnyNo);
								mspdb.setMspdb_customerNumber(mspdb
										.getMspdb_customerNumber() + 1);
								dbhelpher.updateMspDb(mspdb, cmpnyNo);
								dbhelpher.mSetTransactionSuccess(); // setting
																	// the
																	// transaction
																	// successfull.
								Log.i("Transaction success",
										"Transaction success.");
								dbhelpher.mEndTransaction();
								Log.i("Transaction success", "Transaction end.");
								dbhelpher.closeDatabase();
								Log.i("DB closed",
										"Database closed successfully.");

								String msg = "added.";
								alertUser(cNum, msg);
							} else {
								dbhelpher.openWritableDatabase();
								dbhelpher.updateCustomer(customer, cmpnyNo);
								dbhelpher.closeDatabase();

								String msg = cName
										+ " data updated successfully";
								alertUser(cNum, msg);
							}

						}

					} else {
						toastMsg.showToast(AddCustomer.this,
								"Enter value in all fields");
					}
				} catch (Exception e) {
					Toast.makeText(AddCustomer.this, "Exception Occured",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					// writing complete exception in string
					StringWriter stringWritter = new StringWriter();
					PrintWriter printWritter = new PrintWriter(stringWritter,
							true);
					e.printStackTrace(printWritter);
					printWritter.flush();
					stringWritter.flush();

					LogfileCreator.appendLog(stringWritter.toString());
				}
			}

		});

		// set Tax for customer method
		txtSetTax.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// /supporter.simpleNavigateTo(SetTax.class);
					final Dialog dialog = new Dialog(AddCustomer.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.customer_tax_layout);

					final Spinner spn_TaxGroup = (Spinner) dialog
							.findViewById(R.id.spn_TaxGroup);

					final TableRow taxRow1 = (TableRow) dialog
							.findViewById(R.id.taxRow1);
					final TableRow taxRow2 = (TableRow) dialog
							.findViewById(R.id.taxRow2);
					final TableRow taxRow3 = (TableRow) dialog
							.findViewById(R.id.taxRow3);
					final TableRow taxRow4 = (TableRow) dialog
							.findViewById(R.id.taxRow4);
					final TableRow taxRow5 = (TableRow) dialog
							.findViewById(R.id.taxRow5);

					final TextView txtAuthrty1 = (TextView) dialog
							.findViewById(R.id.txtAuthority1);
					final TextView txtAuthrty2 = (TextView) dialog
							.findViewById(R.id.txtAuthority2);
					final TextView txtAuthrty3 = (TextView) dialog
							.findViewById(R.id.txtAuthority3);
					final TextView txtAuthrty4 = (TextView) dialog
							.findViewById(R.id.txtAuthority4);
					final TextView txtAuthrty5 = (TextView) dialog
							.findViewById(R.id.txtAuthority5);

					final Spinner spn_TaxClass1 = (Spinner) dialog
							.findViewById(R.id.spn_State);
					final Spinner spn_TaxClass2 = (Spinner) dialog
							.findViewById(R.id.spn_County);
					final Spinner spn_TaxClass3 = (Spinner) dialog
							.findViewById(R.id.spn_TaxClass3);
					final Spinner spn_TaxClass4 = (Spinner) dialog
							.findViewById(R.id.spn_TaxClass4);
					final Spinner spn_TaxClass5 = (Spinner) dialog
							.findViewById(R.id.spn_TaxClass5);

					Button btnCusTaxOk = (Button) dialog
							.findViewById(R.id.btn_CusTaxOk);

					taxauthority1 = "";
					taxauthority2 = "";
					taxauthority3 = "";
					taxauthority4 = "";
					taxauthority5 = "";

					taxClassList1 = new ArrayList<String>();
					taxClassList2 = new ArrayList<String>();
					taxClassList3 = new ArrayList<String>();
					taxClassList4 = new ArrayList<String>();
					taxClassList5 = new ArrayList<String>();

					dbhelpher.openReadableDatabase();
					List<String> taxGroupLst = dbhelpher.getTaxGroup(cmpnyNo);
					dbhelpher.closeDatabase();
					ArrayAdapter<String> adptTaxGroupSpn = new ArrayAdapter<String>(
							AddCustomer.this,
							android.R.layout.simple_dropdown_item_1line,
							taxGroupLst);
					// adptTaxGroupSpn.setDropDownViewResource(R.layout.spinner_item_layout);
					spn_TaxGroup.setAdapter(adptTaxGroupSpn);

					String strTaxGrp = (String) spn_TaxGroup
							.getItemAtPosition(spn_TaxGroup
									.getSelectedItemPosition());
					dbhelpher.openReadableDatabase();
					HhTaxGroup01 taxGroupObj = dbhelpher.getTaxGroupAuthority(
							strTaxGrp, cmpnyNo);
					dbhelpher.closeDatabase();

					taxauthority1 = taxGroupObj.getHhTaxGroup_taxauthority1();
					taxauthority2 = taxGroupObj.getHhTaxGroup_taxauthority2();
					taxauthority3 = taxGroupObj.getHhTaxGroup_taxauthority3();
					taxauthority4 = taxGroupObj.getHhTaxGroup_taxauthority4();
					taxauthority5 = taxGroupObj.getHhTaxGroup_taxauthority5();

					if ((taxauthority1 != null) && (!taxauthority1.isEmpty())) {
						txtAuthrty1.setText(taxauthority1);
						dbhelpher.openReadableDatabase();
						taxClassList1 = dbhelpher.getTaxAuthority(
								taxauthority1, cmpnyNo);
						dbhelpher.closeDatabase();
						ArrayAdapter<String> adptTaxStateSpn = new ArrayAdapter<String>(
								AddCustomer.this,
								android.R.layout.simple_dropdown_item_1line,
								taxClassList1);
						// adptTaxStateSpn.setDropDownViewResource(R.layout.spinner_item_layout);

						spn_TaxClass1.setAdapter(adptTaxStateSpn);
					} else {
						taxRow1.setVisibility(View.GONE);
					}
					if ((taxauthority2 != null) && (!taxauthority2.isEmpty())) {
						txtAuthrty2.setText(taxauthority2);
						dbhelpher.openReadableDatabase();
						taxClassList2 = dbhelpher.getTaxAuthority(
								taxauthority2, cmpnyNo);
						dbhelpher.closeDatabase();
						ArrayAdapter<String> adptTaxCountySpn = new ArrayAdapter<String>(
								AddCustomer.this,
								android.R.layout.simple_dropdown_item_1line,
								taxClassList2);
						// adptTaxCountySpn.setDropDownViewResource(R.layout.spinner_item_layout);
						spn_TaxClass2.setAdapter(adptTaxCountySpn);
					} else {
						taxRow2.setVisibility(View.GONE);
					}
					if ((taxauthority3 != null) && (!taxauthority3.isEmpty())) {
						txtAuthrty3.setText(taxauthority3);
						dbhelpher.openReadableDatabase();
						taxClassList3 = dbhelpher.getTaxAuthority(
								taxauthority3, cmpnyNo);
						dbhelpher.closeDatabase();
						ArrayAdapter<String> adptTaxCountySpn = new ArrayAdapter<String>(
								AddCustomer.this,
								android.R.layout.simple_dropdown_item_1line,
								taxClassList3);
						// adptTaxCountySpn.setDropDownViewResource(R.layout.spinner_item_layout);
						spn_TaxClass3.setAdapter(adptTaxCountySpn);
					} else {
						taxRow3.setVisibility(View.GONE);
					}
					if ((taxauthority4 != null) && (!taxauthority4.isEmpty())) {
						txtAuthrty4.setText(taxauthority4);

						dbhelpher.openReadableDatabase();
						taxClassList4 = dbhelpher.getTaxAuthority(
								taxauthority4, cmpnyNo);
						dbhelpher.closeDatabase();
						ArrayAdapter<String> adptTaxCountySpn = new ArrayAdapter<String>(
								AddCustomer.this,
								android.R.layout.simple_dropdown_item_1line,
								taxClassList4);
						// adptTaxCountySpn.setDropDownViewResource(R.layout.spinner_item_layout);
						spn_TaxClass4.setAdapter(adptTaxCountySpn);
					} else {
						taxRow4.setVisibility(View.GONE);
					}
					if ((taxauthority5 != null) && (!taxauthority5.isEmpty())) {
						txtAuthrty5.setText(taxauthority5);
						dbhelpher.openReadableDatabase();
						taxClassList5 = dbhelpher.getTaxAuthority(
								taxauthority5, cmpnyNo);
						dbhelpher.closeDatabase();
						ArrayAdapter<String> adptTaxCountySpn = new ArrayAdapter<String>(
								AddCustomer.this,
								android.R.layout.simple_dropdown_item_1line,
								taxClassList5);
						// adptTaxCountySpn.setDropDownViewResource(R.layout.spinner_item_layout);
						spn_TaxClass5.setAdapter(adptTaxCountySpn);
					} else {
						taxRow5.setVisibility(View.GONE);
					}

					if (!taxGroup.equals("")) {

						int indexTaxGroup = taxGroupLst.indexOf(taxGroup);
						spn_TaxGroup.setSelection(indexTaxGroup);

						if (mode.equals("editdeletcust")
								&& strTaxClassValue1.equals("")) {
							String strTaxStts1 = customer
									.getHhCustomer_taxstts1();
							String strTaxStts2 = customer
									.getHhCustomer_taxstts2();
							String strTaxStts3 = customer
									.getHhCustomer_taxstts3();
							String strTaxStts4 = customer
									.getHhCustomer_taxstts4();
							String strTaxStts5 = customer
									.getHhCustomer_taxstts5();

							if (taxClassList1.size() != 0) {
								for (int i = 0; i <= taxClassList1.size(); i++) {
									String row = taxClassList1.get(i);
									String[] tempArr = row.split(" ");
									if (strTaxStts1.equals(tempArr[0])) {
										strTaxClassValue1 = taxClassList1
												.get(i);
										spn_TaxClass1.setSelection(i);
										break;
									}
								}
							}

							if (taxClassList2.size() != 0) {
								for (int i = 0; i <= taxClassList2.size(); i++) {
									String row1 = taxClassList2.get(i);
									String[] tempArr1 = row1.split(" ");
									if (strTaxStts2.equals(tempArr1[0])) {
										strTaxClassValue2 = taxClassList2
												.get(i);
										spn_TaxClass2.setSelection(i);
										break;
									}
								}
							}

							if (taxClassList3.size() != 0) {
								for (int i = 0; i <= taxClassList3.size(); i++) {
									String row1 = taxClassList3.get(i);
									String[] tempArr1 = row1.split(" ");
									if (strTaxStts3.equals(tempArr1[0])) {
										strTaxClassValue3 = taxClassList3
												.get(i);
										spn_TaxClass3.setSelection(i);
										break;
									}
								}
							}

							if (taxClassList4.size() != 0) {
								for (int i = 0; i <= taxClassList4.size(); i++) {
									String row1 = taxClassList4.get(i);
									String[] tempArr1 = row1.split(" ");
									if (strTaxStts4.equals(tempArr1[0])) {
										strTaxClassValue4 = taxClassList4
												.get(i);
										spn_TaxClass4.setSelection(i);
										break;
									}
								}
							}

							if (taxClassList5.size() != 0) {
								for (int i = 0; i <= taxClassList5.size(); i++) {
									String row1 = taxClassList5.get(i);
									String[] tempArr1 = row1.split(" ");
									if (strTaxStts5.equals(tempArr1[0])) {
										strTaxClassValue5 = taxClassList5
												.get(i);
										spn_TaxClass5.setSelection(i);
										break;
									}
								}
							}
						} else {
							if (taxClassList1.size() != 0) {
								int indexTaxVal1 = taxClassList1
										.indexOf(strTaxClassValue1);
								spn_TaxClass1.setSelection(indexTaxVal1);
							}
							if (taxClassList2.size() != 0) {
								int indexTaxVal2 = taxClassList2
										.indexOf(strTaxClassValue2);
								spn_TaxClass2.setSelection(indexTaxVal2);
							}

							if (taxClassList3.size() != 0) {
								int indexTaxVal3 = taxClassList3
										.indexOf(strTaxClassValue3);
								spn_TaxClass3.setSelection(indexTaxVal3);
							}

							if (taxClassList4.size() != 0) {
								int indexTaxVal4 = taxClassList4
										.indexOf(strTaxClassValue4);
								spn_TaxClass4.setSelection(indexTaxVal4);
							}

							if (taxClassList5.size() != 0) {
								int indexTaxVal5 = taxClassList5
										.indexOf(strTaxClassValue5);
								spn_TaxClass5.setSelection(indexTaxVal5);
							}

						}

					} else {
						taxGroup = (String) spn_TaxGroup
								.getItemAtPosition(spn_TaxGroup
										.getSelectedItemPosition());

						if (taxClassList1.size() != 0) {
							strTaxClassValue1 = (String) spn_TaxClass1
									.getItemAtPosition(spn_TaxClass1
											.getSelectedItemPosition());
						}
						if (taxClassList2.size() != 0) {
							strTaxClassValue2 = (String) spn_TaxClass2
									.getItemAtPosition(spn_TaxClass2
											.getSelectedItemPosition());
						}

						if (taxClassList3.size() != 0) {
							strTaxClassValue3 = (String) spn_TaxClass3
									.getItemAtPosition(spn_TaxClass3
											.getSelectedItemPosition());
						}

						if (taxClassList4.size() != 0) {
							strTaxClassValue4 = (String) spn_TaxClass4
									.getItemAtPosition(spn_TaxClass4
											.getSelectedItemPosition());
						}

						if (taxClassList5.size() != 0) {

							strTaxClassValue5 = (String) spn_TaxClass5
									.getItemAtPosition(spn_TaxClass5
											.getSelectedItemPosition());
						}

					}

					spn_TaxGroup
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									taxGroup = (String) spn_TaxGroup
											.getItemAtPosition(spn_TaxGroup
													.getSelectedItemPosition());

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});

					spn_TaxClass1
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									strTaxClassValue1 = (String) spn_TaxClass1
											.getItemAtPosition(spn_TaxClass1
													.getSelectedItemPosition());

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});

					spn_TaxClass2
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									strTaxClassValue2 = (String) spn_TaxClass2
											.getItemAtPosition(spn_TaxClass2
													.getSelectedItemPosition());

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});

					spn_TaxClass3
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									strTaxClassValue3 = (String) spn_TaxClass3
											.getItemAtPosition(spn_TaxClass3
													.getSelectedItemPosition());

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});

					spn_TaxClass4
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									strTaxClassValue4 = (String) spn_TaxClass4
											.getItemAtPosition(spn_TaxClass4
													.getSelectedItemPosition());

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});

					spn_TaxClass5
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									strTaxClassValue5 = (String) spn_TaxClass5
											.getItemAtPosition(spn_TaxClass5
													.getSelectedItemPosition());

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});

					btnCusTaxOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.cancel();
						}
					});

					dialog.show();
				} catch (Exception e) {
					Toast.makeText(AddCustomer.this, "Exception Occured",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					// writing complete exception in string
					StringWriter stringWritter = new StringWriter();
					PrintWriter printWritter = new PrintWriter(stringWritter,
							true);
					e.printStackTrace(printWritter);
					printWritter.flush();
					stringWritter.flush();

					LogfileCreator.appendLog(stringWritter.toString());
				}
			}

		});

		dbhelpher.INSERT_DATA_NEW("Mathes","098765432");
	}


	public List<String> loadCustomerType() {
		List<String> custTypeList = new ArrayList<String>();
		custTypeList.add("Base");
		custTypeList.add("A");
		custTypeList.add("B");
		custTypeList.add("C");
		custTypeList.add("D");
		custTypeList.add("E");

		return custTypeList;
	}

	// To create underlink under text
	public void underlineText(TextView textView) {
		SpannableString content = new SpannableString(textView.getText());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		textView.setText(content);
	}

	public void alertUser(String custNo, String msg) {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				AddCustomer.this);
		alertDialog.setTitle("Information");
		alertDialog.setIcon(R.drawable.tick);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						supporter.simpleNavigateTo(MainMenu.class);

						dialog.cancel();

					}
				});

		alertDialog.setMessage(custNo + " " + msg);
		////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		String message = "Do you want to Cancel?";
		if (mode.equals("editdeletcust")) {
			message = "Do you want to Cancel ?";
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
			alertUser.setTitle("Confirmation");
			alertUser.setIcon(R.drawable.warning);
			alertUser.setCancelable(false);
			alertUser.setMessage(message);
			alertUser.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							supporter.simpleNavigateTo(MainMenu.class);

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
