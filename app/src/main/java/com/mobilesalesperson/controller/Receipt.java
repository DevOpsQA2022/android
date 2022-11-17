package com.mobilesalesperson.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhPayment01;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.Mspdb;

import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.mobilesalesperson.xml.FileMspDb;

/**
 * @author T.SARAVANAN class to control Receipt page
 */
public class Receipt extends AppBaseActivity {

	/** variable declarations */
	private MspDBHelper dbhelpher;
	private Mspdb mMspdb;
	private Supporter supporter;
	private HhSetting setting;
	private int deciValue;
	private List<HhPayment01> openInvoices;
	private List<Mspdb> mspDbList;
	private TextView txtReciptCusNo;
	private TextView txtReciptCusName;
	private Spinner spnReciptPaymentCode,spnReciptPaymentCode1;
	private int receiptNumber;
	public int receiptNum;
	private int prepaymentNumber;
	private int maxOfNumbers;
	private int maxOfNums;
	public int prepaymentNum;
	public int mapNum;
	private String maxPrepaymentNo;
	private String maxReceiptNo;
	private String spCode;
	private TextView txtReciptPaymentDate;
	private EditText edtTxtChkReciptNo;
	private EditText edtTxtReciptAmt;
	private EditText edtTxtReciptAmt1;
	private TextView txtReciptCusAmt;
	private TextView txtReciptUSD;
	private TextView txtReciptUnApplied;
	private TextView txtReciptTotal;
	private ListView lstReciptItems;
	
	private List<String> paymentMode;
	private String mspDbXMLMsg;
	
	
	
	private ArrayAdapter<String> adptPaymentModeSpn;
	private ArrayAdapter<HhPayment01> adptOpenInvoices;
	private View mViewLine;
	private Dialog dialog;
	static final int MENU_SORTBY_DIALOG_ID = 0;
	private ListView dialog_ListView;
	private String[] listContent;
	private boolean isDialog;
	private FileMspDb mFileMspDb;
	private String sortby = "asc";
	private String stxt;
	private File dataFile;
	private String strPaymentMode;
	private Spinner spnPaymentMode;
	private String defaultBank;
	private HhManager mManager;
	private TextView mReciptTitle;
	private boolean res;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String custNo;
	private String currency;
	private boolean isDevice = false;
	private int rept;
	private ToastMessage toastMsg;
	private String cmpnyNo; // added for multicompany
//</History> Suresh 05-Oct-2017 Added for Print model Select
private String printerModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.receipt_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		txtReciptCusNo = (TextView) findViewById(R.id.txt_reciptCusNo);
		txtReciptCusName = (TextView) findViewById(R.id.txt_reciptCusName);
		spnReciptPaymentCode = (Spinner) findViewById(R.id.spn_reciptPaymentCode);
		spnReciptPaymentCode1 = (Spinner) findViewById(R.id.spn_reciptPaymentCode1);
		txtReciptPaymentDate = (TextView) findViewById(R.id.txt_reciptPaymentDate);
		edtTxtChkReciptNo = (EditText) findViewById(R.id.edtTxt_chkReciptNo);
		edtTxtReciptAmt = (EditText) findViewById(R.id.edtTxt_reciptAmt);
		edtTxtReciptAmt1 = (EditText) findViewById(R.id.edtTxt_reciptAmt1);
		txtReciptCusAmt = (TextView) findViewById(R.id.txt_reciptCustAmt);
		txtReciptUSD = (TextView) findViewById(R.id.txt_reciptUSD);
		txtReciptTotal = (TextView) findViewById(R.id.txt_reciptTotal);
		mReciptTitle = (TextView) findViewById(R.id.txt_ReceiptTitle);
		txtReciptUnApplied = (TextView) findViewById(R.id.txt_reciptUnApplied);
		lstReciptItems = (ListView) findViewById(R.id.lst_reciptItems);
		mViewLine = (View) findViewById(R.id.recipt_view_line1);
	    spCode = supporter.getSalesPerson();
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013
		custNo = getIntent().getStringExtra("customerNumber");
		currency = supporter.getCompanyCurrency();
		
		/*isDevice = isTablet(this);
		if (isDevice == false) {
			mReciptTitle.setVisibility(View.GONE);
			mViewLine.setVisibility(View.GONE);
			setTitle("Receipt Entry");
		}

		*/
		
		listContent = new String[2];
		listContent = loadInfoDialogStaticData(listContent);

	  
	   
		dbhelpher.openReadableDatabase();
		paymentMode = dbhelpher.getReceiptType(cmpnyNo);
		dbhelpher.closeDatabase();
		
		dbhelpher.openReadableDatabase();
		openInvoices = dbhelpher.getPendingPayments(custNo, cmpnyNo);
		dbhelpher.closeDatabase();
		
		dbhelpher.getReadableDatabase();
		openInvoices = dbhelpher.getPendingPayments(custNo, cmpnyNo);
		setting = dbhelpher.getSettingData();
		mMspdb=dbhelpher.getMspDbData(cmpnyNo, spCode);
		res=dbhelpher.isReceiptEmpty();
		if(res==false)
		{
		maxReceiptNo=dbhelpher.getMaxReceiptNo();
		}
		else{
			maxReceiptNo="0";
			}
		mManager=new HhManager();
		mManager = dbhelpher.getManagerData(spCode, cmpnyNo);
		mspDbList=new ArrayList<Mspdb>();
		dbhelpher.closeDatabase();
		
		dbhelpher.openWritableDatabase();
		setting = dbhelpher.getSettingData();
		dbhelpher.closeDatabase();
		deciValue = Integer.parseInt(setting.getHhSetting_decimalformat());
		rept = setting.getHhSetting_autoreportgen();
		//load bank list 
		
		
		
		adptPaymentModeSpn = new ArrayAdapter<String>(Receipt.this,
				android.R.layout.simple_dropdown_item_1line, paymentMode);
		// adptPaymentModeSpn.setDropDownViewResource(R.layout.spinner_item_layout);

		spnReciptPaymentCode.setAdapter(adptPaymentModeSpn);
		spnReciptPaymentCode1.setAdapter(adptPaymentModeSpn);

		currentDate();
		txtReciptPaymentDate.setText(supporter.getStringDate(mYear, mMonth + 1,
				mDay));

		txtReciptCusAmt.setText(supporter.getCurrencyFormat(0.00));
		txtReciptUnApplied.setText(supporter.getCurrencyFormat(0.00));
		edtTxtReciptAmt1.setEnabled(false);

		// TextWatcher for Receipt Amount
		TextWatcher reciptPrcWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {
				String stxt = charSequence.toString();

				if (!stxt.equals("")) {

					String strDiscPrice = edtTxtReciptAmt.getText().toString();
					int indexOFdec = strDiscPrice.indexOf(".");

					if (strDiscPrice.equals(".")) {
						edtTxtReciptAmt.setText("");
						edtTxtReciptAmt.append("0.");
						edtTxtReciptAmt.setSelection(edtTxtReciptAmt.getText()
								.length());
					} else if (indexOFdec >= 0) {

						if (strDiscPrice.substring(indexOFdec).length() > deciValue) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									edtTxtReciptAmt.getWindowToken(), 0);
						}
					}

				}

			}

			@Override
			public void afterTextChanged(Editable s) {
				String strNo = s.toString().trim();
				if(!strNo.equals("")) {
					double D_strNo = Double.valueOf(strNo);
					String  Str_No = String.valueOf(D_strNo);
					edtTxtReciptAmt1.setEnabled(true);

					edtTxtReciptAmt1.setText("");
					txtReciptCusAmt.setText(Str_No);
					txtReciptUnApplied.setText(Str_No);
				}

				if (strNo.equals("")) {
					edtTxtReciptAmt1.setText("");
					txtReciptCusAmt.setText(supporter.getCurrencyFormat(0.00));
					txtReciptUnApplied.setText(supporter
							.getCurrencyFormat(0.00));
				}

			}

		};

		edtTxtReciptAmt1.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				String stxt = s.toString();

				if (!stxt.equals("")) {

					String strDiscPrice = edtTxtReciptAmt1.getText().toString();
					int indexOFdec = strDiscPrice.indexOf(".");

					if (strDiscPrice.equals(".")) {
						edtTxtReciptAmt1.setText("");
						edtTxtReciptAmt1.append("0.");
						edtTxtReciptAmt1.setSelection(edtTxtReciptAmt1.getText()
								.length());
					} else if (indexOFdec >= 0) {

						if (strDiscPrice.substring(indexOFdec).length() > deciValue) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									edtTxtReciptAmt1.getWindowToken(), 0);
						}
					}

				}

			}

			@Override
			public void afterTextChanged(Editable s) {

				String stxt = s.toString();

				String recptFirstAmt = edtTxtReciptAmt.getText().toString();

				if (!recptFirstAmt.equals("")) {

					double recptAmt1 = Double.parseDouble(edtTxtReciptAmt.getText().toString());

					if (stxt.equals("")) {

						int ReceiptAmt2 = 0;
						double strDiscPrice1 = recptAmt1 + ReceiptAmt2;

						String strDiscPrice = String.valueOf(strDiscPrice1);

						txtReciptCusAmt.setText(strDiscPrice);
						txtReciptUnApplied.setText(strDiscPrice);

						if (strDiscPrice.equals("")) {
							txtReciptCusAmt.setText(supporter.getCurrencyFormat(0.00));
							txtReciptUnApplied.setText(supporter
									.getCurrencyFormat(0.00));
						}

					} else if ((!stxt.equals(""))) {

						double receipt2 = Double.parseDouble(edtTxtReciptAmt1.getText().toString());

						double strDiscPrice1 = recptAmt1 + receipt2;

						DecimalFormat format = new DecimalFormat("#.##");
						String strdisprice = format.format(strDiscPrice1);
						double strdisPrice = Double.parseDouble(strdisprice);

						String strDiscPrice = String.valueOf(strdisPrice);

						txtReciptCusAmt.setText(strDiscPrice);
						txtReciptUnApplied.setText(strDiscPrice);

						if (strDiscPrice.equals("")) {
							txtReciptCusAmt.setText(supporter.getCurrencyFormat(0.00));
							txtReciptUnApplied.setText(supporter
									.getCurrencyFormat(0.00));
						}

					}

				}else if(recptFirstAmt.equals("")){

					txtReciptCusAmt.setText(supporter.getCurrencyFormat(0.00));
					txtReciptUnApplied.setText(supporter
							.getCurrencyFormat(0.00));
				}
			}
		});

		edtTxtReciptAmt.addTextChangedListener(reciptPrcWatcher);

		String currency = supporter.getCompanyCurrency();
		txtReciptUSD.setText(currency);
		


		if (openInvoices.isEmpty()) {

			AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
			alertUser.setTitle("Information");
			alertUser.setIcon(R.drawable.tick);
			alertUser.setCancelable(false);
			alertUser.setMessage("This Customer don't have open Invoices");
			alertUser.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							supporter.simpleNavigateTo(CustomerSelection.class);
						}
					});
				//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
		} else {
		
		edtTxtChkReciptNo.setFocusable(false);
						// set Customer Number
			txtReciptCusNo.setText(custNo);
			// set CustomerName
			txtReciptCusName
					.setText(openInvoices.get(0).getHhPayment_cusname());
			adptOpenInvoices = new ReceiptInvoiceListAdapter(Receipt.this,
					openInvoices, edtTxtReciptAmt, txtReciptUnApplied,
					lstReciptItems);
			lstReciptItems.setAdapter(adptOpenInvoices);
			
			//set total pending amount

			float total=0;
			for(int i=0; i<openInvoices.size();i++)
			{
			float pending=openInvoices.get(i).getHhPayment_pendingbalance();
			total+=pending;
			String mTotal=String.valueOf(supporter.getCurrencyFormat(total));
			txtReciptTotal.setText(mTotal);
			}
			
			// generate receiptnumber automatically
			
						String FILE_NAME = "Mspdb.xml";
						File file=new File(Supporter.getAppCommonPath(),FILE_NAME);
						FileInputStream stream;
						try {
							stream = new FileInputStream(file);
							String[] result=parseDocument(stream);
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						
						receiptNumber = mMspdb.getMspdb_receiptnumber();
					/*	prepaymentNumber=mMspdb.getMspdb_orderNumber();*/
						/*maxOfNumbers=Math.max(prepaymentNumber, Math.max(receiptNum, Integer.parseInt(maxReceiptNo)));*/
						maxOfNums=Math.max(receiptNumber, Math.max(receiptNum, Integer.parseInt(maxReceiptNo)));
						/*if(mReceiptNumber>=receiptNum)
						{*/
						String newReceiptNo = spCode + "-" + mMspdb.getMspdb_mapNo() + "-"
								+ String.format("%07d", maxOfNums);
						edtTxtChkReciptNo.setText(newReceiptNo);
					
		}

	} // end of oncreate

	public String[] loadInfoDialogStaticData(String[] lstStr) {
		lstStr[0] = "Document Number";
		lstStr[1] = "Pending Amount";
		return lstStr;
	}
	
	public String[] parseDocument(InputStream stream)
	{
		String[] resultArray=new String[8];
		String result="";
		SAXParserFactory parserFactory=SAXParserFactory.newInstance();
		try {
			SAXParser parser=parserFactory.newSAXParser();
			XMLReader reader=parser.getXMLReader();
			Xmlhandler handler=new Xmlhandler();
			reader.setContentHandler(handler);
			InputSource inputSource = new InputSource();
			inputSource.setEncoding("UTF-8");
			inputSource.setByteStream(stream);
			reader.parse(inputSource);
			receiptNum=Integer.parseInt(handler.receiptNo);
			/*prepaymentNum=Integer.parseInt(handler.prepaymentNo);*/
			mapNum=Integer.parseInt(handler.mapNo);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result=resultArray[7];
		return resultArray;
	}
	
	
		/** method for menu control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.login, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.receipt_menu, menu);
		return true;
	}

	/** method for menu control */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.recipt_mnu_sortBy:
			int childcount1 = lstReciptItems.getChildCount();
			for (int i = 0; i < childcount1; i++) {
				View v = lstReciptItems.getChildAt(i);
				TextView appliedAmt = (TextView) v
						.findViewById(R.id.txt_ReceiptApplyAmt);
				String applyAmt = appliedAmt.getText().toString();

				double iApplyAmt = Double.parseDouble(applyAmt);

				if (iApplyAmt == 0) {
					isDialog = true;
				} else {
					isDialog = false;
				}
			}
			showDialog(MENU_SORTBY_DIALOG_ID);
			break;

		case R.id.recipt_mnu_ok:

			String paymentCode = (String) spnReciptPaymentCode
					.getItemAtPosition(spnReciptPaymentCode
							.getSelectedItemPosition());

			String paymentCode1 = (String) spnReciptPaymentCode1
					.getItemAtPosition(spnReciptPaymentCode1
							.getSelectedItemPosition());
		
			String edtRefNo = edtTxtChkReciptNo.getText().toString();
			String edtReptAmt = edtTxtReciptAmt.getText().toString();
			String edtReptAmt1 = edtTxtReciptAmt1.getText().toString();

			if (edtRefNo.equals("")) {
				toastMsg.showToast(Receipt.this, "Enter Receipt No.");
			} else if (edtReptAmt.equals("")) {
				toastMsg.showToast(Receipt.this, "Enter Receipt Amount.");
			} else if (checkReceiptNumber(edtRefNo)) {
				toastMsg.showToast(Receipt.this,
						"Receipt number already exist, please change the receipt number.");
			} else {

				/**
				 * here get the receipts from receiptlist which is stored in
				 * preference and add additional details before storing into
				 * database. docNo,applied amount, pending amount and net amount
				 * are already added.
				 */
				Map<Integer, HhReceipt01> receiptList = supporter
						.getPrefReceiptList();
				int receiptsListSize = receiptList.size();

				if (receiptsListSize != 0) {

					HhReceipt01 receipt = null;

					dbhelpher.openWritableDatabase();
					Log.i("Writable DB Open", "Writable Database Opened.");
					dbhelpher.mBeginTransaction();
					Log.i("Transaction started",
							"Transaction successfully started for recipt entry.");

					for (int key : receiptList.keySet()) { // a way to get all
															// key

						receipt = receiptList.get(key);
						String apply = "Yes";
						String documentNo = receipt.getHhReceipt_docnumber();
						String appliedAmount1 = receipt
								.getHhReceipt_appliedamount() + "";
						String pedintBalance = receipt
								.getHhReceipt_pendingbal();
						double netAmount = Double.parseDouble(pedintBalance)
								- receipt.getHhReceipt_appliedamount();
						String netAmount1 = netAmount + "";

						// add receipt details to receipt table...
						receipt.setHhReceipt_customernumber(txtReciptCusNo
								.getText().toString());
						receipt.setHhReceipt_customername(txtReciptCusName
								.getText().toString());
						receipt.setHhReceipt_currency(txtReciptUSD.getText()
								.toString());
						receipt.setHhReceipt_apply1("Y");
						receipt.setHhReceipt_receipttype(paymentCode);
						receipt.setHhReceipt_amount(Double
								.parseDouble(edtTxtReciptAmt.getText()
										.toString()));
						receipt.setHhReceipt_receiptnumber(edtRefNo);
						receipt.setHhReceipt_receiptday(mDay);
						receipt.setHhReceipt_receiptmonth(mMonth + 1);
						receipt.setHhReceipt_receiptyear(mYear);
						receipt.setHhReceipt_status(0);
						receipt.setHhReceipt_receiptunapplied(Double
								.parseDouble(txtReciptUnApplied.getText()
										.toString()));
						receipt.setHhReceipt_customeramount(Double
								.parseDouble(txtReciptCusAmt.getText()
										.toString()));
						receipt.setHhReceipt_refno("" + maxOfNums);
						receipt.setHhReceipt_companycode(cmpnyNo);
						receipt.setHhReceipt_receipttype2(paymentCode1);
						if(edtTxtReciptAmt1.getText()
								.toString().equals("")){
							receipt.setHhReceipt_amount2(Double
									.parseDouble("0"));

						}else {
							receipt.setHhReceipt_amount2(Double
									.parseDouble(edtTxtReciptAmt1.getText()
											.toString()));
						}
						dbhelpher.addReceipt(receipt);

						// update method for payment table...
						dbhelpher.updatePayment(documentNo, apply,
								appliedAmount1, netAmount1, custNo, cmpnyNo);

					}

					dbhelpher.mSetTransactionSuccess(); // setting the
														// transaction
														// successfull.
					Log.i("Transaction success", "Transaction success.");
					dbhelpher.mEndTransaction();
					Log.i("Transaction success", "Transaction end.");
					dbhelpher.closeDatabase();
					Log.i("DB closed", "Database closed successfully.");
					
					/*
					 * Update ReceiptNumber in MSPDB table...
					 */
					dbhelpher.getWritableDatabase();
					mMspdb.setMspdb_receiptnumber(maxOfNums +1);
				/*	mMspdb.setMspdb_orderNumber(prepaymentNumber);*/
					dbhelpher.updateMspDb(mMspdb, cmpnyNo);
					dbhelpher.closeDatabase();

					mFileMspDb = new FileMspDb();
					mspDbList.add(mMspdb);
					
					StringBuilder mspdbXMLBuilder = new StringBuilder();
					
					for (int i = 0; i < mspDbList.size(); i++) {
						mspdbXMLBuilder = mFileMspDb.writeXml(
								mspDbList.get(i), mspdbXMLBuilder);
					}
					
					mspDbXMLMsg = mspdbXMLBuilder.toString();
					
					String MSPDB_FILE_NAME = "Mspdb.xml";
					dataFile=new File(Supporter.getAppCommonPath(),MSPDB_FILE_NAME);
					FileOutputStream fos4;
					
					try {
						
						fos4 = new FileOutputStream(dataFile);
						fos4.write(mspDbXMLMsg.getBytes());
			            fos4.close();
			            
					} catch (FileNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
                    catch (IOException e1) {
	               // TODO Auto-generated catch block
	                  e1.printStackTrace();
                    }
					
					

					if (rept == 1) {
						AlertDialog.Builder alertUser = new AlertDialog.Builder(
								this);
						alertUser.setTitle("Information");
						alertUser.setIcon(R.drawable.tick);
						alertUser.setCancelable(false);
						alertUser
								.setMessage("Receipt Saved.Do you want Print ?");
						alertUser.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										String reciptNo = edtTxtChkReciptNo
												.getText().toString();
//</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
					Intent intent = new Intent(Receipt.this, ReceiptPDFCreate.class);
                                            intent.putExtra("reciptNo", reciptNo);
                                            startActivity(intent);
						}
						else {
							List<Devices> deviceList = supporter
									.getPairedDevices();

							if (deviceList.size() != 0) {
								final ReceiptDetailDialog dialog1 = new ReceiptDetailDialog(
										Receipt.this, reciptNo,
										dbhelpher, supporter,
										deviceList);
								dialog1.setContentView(R.layout.print_list_layout);
								dialog1.show();
								dialog.dismiss();
							} else {
								dialog.dismiss();
								toastMsg.showToast(Receipt.this,
										"Paired printer not available for printing");
								supporter
										.simpleNavigateTo(MainMenu.class);
							}
						}
									}
								});
						alertUser.setNegativeButton("No",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										supporter
												.simpleNavigateTo(MainMenu.class);
									}
								});
							//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
					} else {

						AlertDialog.Builder alertUser = new AlertDialog.Builder(
								this);
						alertUser.setTitle("Information");
						alertUser.setIcon(R.drawable.tick);
						alertUser.setCancelable(false);
						alertUser.setMessage("Receipt saved successfully");
						alertUser.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										supporter
												.simpleNavigateTo(MainMenu.class);
									}
								});
							//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
					}
				} else {
					final AlertDialog alertDialog = new AlertDialog.Builder(
							Receipt.this).create();
					alertDialog.setTitle("Warning");
					alertDialog.setIcon(R.drawable.warning);
					alertDialog.setCancelable(false);
					alertDialog.setMessage("Need to Apply Receipt Amount");
					// Setting OK Button
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									alertDialog.dismiss();
								}
							});

					alertDialog.show();
				}
			}

			break;

		}
		return true;
	}

	protected void navigationProcess(String reciptNo, Class<?> cls) {

		Intent intent = new Intent(Receipt.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("receiptnumber", reciptNo);
		startActivity(intent);
	}

	private boolean checkReceiptNumber(String receiptNo) {

		dbhelpher.openReadableDatabase();

		boolean isExist = dbhelpher.isReceiptExist(receiptNo, cmpnyNo);

		dbhelpher.closeDatabase();

		return isExist;
	}

	/* Info Dialog creation */
	@Override
	protected Dialog onCreateDialog(int id) {

		dialog = null;

		switch (id) {
		case MENU_SORTBY_DIALOG_ID:
			dialog = new Dialog(Receipt.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.sort_layout);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});

			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
				}
			});

			// Prepare ListView in dialog
			dialog_ListView = (ListView) dialog
					.findViewById(R.id.lst_sortDialog);
			ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
					Receipt.this, R.drawable.dialog_list_item,
					R.id.txt_Dlg_ListDetails, listContent);

			dialog_ListView.setAdapter(adapter1);
			dialog_ListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					if (position == 0) {
						if (sortby.equals("asc")) {
							((ReceiptInvoiceListAdapter) adptOpenInvoices)
									.sortByDocumentNumberAsc();
							sortby = "desc";
						} else {
							((ReceiptInvoiceListAdapter) adptOpenInvoices)
									.sortByDocumentNumberDesc();
							sortby = "asc";
						}

					} else if (position == 1) {

						if (isDialog) {
							if (sortby.equals("asc")) {
								((ReceiptInvoiceListAdapter) adptOpenInvoices)
										.sortByPendingAscAmount();
								sortby = "desc";
							} else {
								((ReceiptInvoiceListAdapter) adptOpenInvoices)
										.sortByPendingDescAmount();
								sortby = "asc";
							}
						} else if (!isDialog) {
							toastMsg.showToast(Receipt.this,
									"Cannot be sorted when amount is applied to the line items..");
						}

					}

					dialog.dismiss();
				}
			});

			break;
		}

		return dialog;
	}

	// to set current date
	private void currentDate() {
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			String valRecAmt = edtTxtReciptAmt.getText().toString();

			if (!valRecAmt.equals("")) {

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						Receipt.this);
				alertDialog.setTitle("Confirmation");
				alertDialog.setIcon(R.drawable.warning);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								supporter.setMode("recipt");
								supporter
										.simpleNavigateTo(CustomerSelection.class);
							}
						});

				alertDialog.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alertDialog
						.setMessage("Do you want to proceed without applying receipt amount?");
				//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

			} else {
				supporter.setMode("recipt");
				supporter.simpleNavigateTo(CustomerSelection.class);
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	public boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
	class Xmlhandler extends DefaultHandler
	{
        public String tempVal;
		public String receiptNo;
		public String prepaymentNo;
		public String mapNo;
		public String value;
		
		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			value="";
			tempVal="";
			
			
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			
			System.out.println("Value:" + tempVal);
			System.out.println("End:" + localName);
			
			if(localName.equalsIgnoreCase("MAPNO"))
			{
				mapNo=tempVal;
			}
			if(localName.equalsIgnoreCase("RECEIPTNO"))
			{
				receiptNo=tempVal;
			}
			if(localName.equalsIgnoreCase("ORDERNO"))
			{
				prepaymentNo=tempVal;
			}
			
			
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			value=new String(ch, start, length);
			tempVal=value+tempVal;
		}
		
		
		
	}
}
