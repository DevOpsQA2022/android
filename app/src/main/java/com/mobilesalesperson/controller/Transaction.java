package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhContractPrice01;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhHistory01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhPrice01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhSignature;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.model.Manf_Number01;
import com.mobilesalesperson.model.Mspdb;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.GPSTracker;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;

/**
 * @author TIVM class to manage transaction for order or invoice For your
 *         understanding, here temporary table is maintained for transaction
 *         called temptable In that, transaction type for order and quote is oe,
 *         and for invoice(tr-to sale,ts-to return). Same is maintained in
 *         transaction table(ie,hhTran01)as S-Order,Q-Quote,I-To sale,CN-Credit
 *         Note.
 */
public class Transaction extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private TextView txtTransCusName;
	private TextView txtTransTotal;
	private TextView txtTransTotalQty;
	private TextView txtTransDiscount;
	private TextView txtSalesCommission;
	private TextView txtTransTax;
	private TextView txtTransPreTax;
	private TextView txtTransNetAmt;
	private TextView txtModeTypeTotal;
	private TextView txtTransEntryLbl;
	private EditText edtTranScan;

	private Spinner spnEntryType;
	private ListView lstTransItems;
	private RadioGroup radioGroup;
	private RadioButton radioTransNum;
	private RadioButton radioTransUpc;
	private Button btnCusNameEdit;
	private  GPSTracker gps;
	public TempItem tempitem_e;
	public TempItem ordernum_e;
	private Button btnAddToSale;
	private Button btnAddToReturn;
	private Button btnTransFinish;
	private int prepayStatusSetting;
	private int autoReportSetting;
	private int customerNameEditable;
	private HhSetting setting;

	private String strTransTotal;
	private String strTransDiscount;
	private String strTransTax;
	private String strTransPreTax;
	private String strTransNetAmt;
	private String totype;
	private String mode;
	private SharedPreferences shippingDetails;
	private SharedPreferences entryDetails;
	private SharedPreferences signPreference;
	private SharedPreferences orderDetails;
	private List<String> entryList;
	private  List<HhHistory01> his1List;
	private ArrayAdapter<String> adptEntrySpn;
	private List<TempItem> transItemList;
	private ArrayAdapter<TempItem> adptTransItemList;
	private ArrayAdapter<HhHistory01> adptHisItemList;
	private List<TempItem> tempDeletedItems;
	private HhTran01 tran;

	private double netTotal;
	private ToastMessage toastMsg;
	final Context context = this;


	private String itemNo;
	private String ordNo;
	private String genOrdNo;
	private String strEditedCusName;
	private String cmpnyNo; // added for multicompany

	private double latitude=0.00;
	private double longitude=0.00;
	//</History> Suresh 05-Oct-2017 Added for Print model Select
private String printerModel;
	/**
	 * method to initialize the class activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.transaction_layout);

		registerBaseActivityReceiver();


		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		toastMsg = new ToastMessage();

		txtTransCusName = (TextView) findViewById(R.id.txt_TransCusName);
		txtTransTotal = (TextView) findViewById(R.id.txt_TransTotal);
		txtTransTotalQty = (TextView) findViewById(R.id.txt_TransTotalQty);
		txtTransDiscount = (TextView) findViewById(R.id.txt_TransDiscount);
		txtSalesCommission = (TextView) findViewById(R.id.txt_Sales_Commission);
		txtTransTax = (TextView) findViewById(R.id.txt_TransTax);
		// txtTransPreTax = (TextView) findViewById(R.id.txt_TransPreTax);
		txtTransNetAmt = (TextView) findViewById(R.id.txt_TransNetAmt);
		txtModeTypeTotal = (TextView) findViewById(R.id.txt_ModeTypeTotal);
		txtTransEntryLbl = (TextView) findViewById(R.id.txt_TransEntryLbl);
		edtTranScan = (EditText) findViewById(R.id.edt_TranScan);
		spnEntryType = (Spinner) findViewById(R.id.spn_EntryType);
		lstTransItems = (ListView) findViewById(R.id.lst_TransItems);
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioTransNum = (RadioButton) findViewById(R.id.radio_TransNum);
		radioTransUpc = (RadioButton) findViewById(R.id.radio_TransUpc);

		btnCusNameEdit = (Button) findViewById(R.id.btn_CusNameEdit);
		btnAddToSale = (Button) findViewById(R.id.btn_AddToSale);
		btnAddToReturn = (Button) findViewById(R.id.btn_AddToReturn);
		btnTransFinish = (Button) findViewById(R.id.btn_TransFinish);
		btnTransFinish.setEnabled(true);
		btnCusNameEdit.setVisibility(View.GONE);


		cmpnyNo = supporter.getCompanyNo(); // added jul 12/2013
		//For Richard Qoh coming wrong
		totype = supporter.getSaleType();
		final String orderNo = getIntent().getStringExtra("orderNum");
		ordNo = getIntent().getStringExtra("orderNum");
		itemNo = getIntent().getStringExtra("itemNumber");

		/*final String orderNo=orderDetails.getString(genOrdNo, "");
        tran=new HhTran01();
        tran=dbHelper.getDate(orderNo);*/

		mode = supporter.getMode();
		dbHelper.openReadableDatabase();
		setting = dbHelper.getSettingData();
		dbHelper.closeDatabase();
		prepayStatusSetting = setting.getHhSetting_showprepay();
		autoReportSetting = setting.getHhSetting_autoreportgen();
		customerNameEditable = setting.getHhSetting_cusnameeditable();

		if (customerNameEditable == 1) {
			btnCusNameEdit.setVisibility(View.VISIBLE);
		} else {
			btnCusNameEdit.setVisibility(View.GONE);
		}

		tempDeletedItems = new ArrayList<TempItem>(); // used only for edit
		// total transaction to
		// maintain deleted temp
		// items.

		shippingDetails = getSharedPreferences("shippingDetails",
				Context.MODE_PRIVATE);
		entryDetails = getSharedPreferences("entryDetails",
				Context.MODE_PRIVATE);
		signPreference = this.getSharedPreferences("signPref",
				Context.MODE_PRIVATE);
		orderDetails = getSharedPreferences("orderData", Context.MODE_PRIVATE);
		String orderNum = orderDetails.getString("orderNo", "");


//        if(mode.equals("he")){
//			btnAddToSale.setVisibility(View.GONE);
//			btnAddToReturn.setVisibility(View.GONE);
//			txtModeTypeTotal.setText("Order Total");
//
//
//			dbHelper.openReadableDatabase();
//			his1List = dbHelper.getHistoryData(cmpnyNo);
//			dbHelper.closeDatabase();
//
//
//			adptHisItemList =new HistoryItemListAdapter(this,his1List);
//
//
//
//			lstTransItems.setAdapter(adptHisItemList);
//			adptHisItemList.notifyDataSetChanged();
//			registerForContextMenu(lstTransItems);
//
//
//
//
//		}

		if (mode.equals("oe") || mode.equals("orderEdit")) {
			btnAddToSale.setVisibility(View.GONE);
			btnAddToReturn.setVisibility(View.GONE);
			txtModeTypeTotal.setText("Order Total");

			if (mode.equals("orderEdit")) {
				spnEntryType.setClickable(false);
			}

		} else if (mode.equals("ie") || mode.equals("invoiceEdit")) {
			txtTransEntryLbl.setVisibility(View.GONE);
			spnEntryType.setVisibility(View.GONE);

			String invEntType = entryDetails.getString("invEntTyp", "sale");

			if (invEntType.equals("sale")) {
				//btnAddToReturn.setTextColor(0Xffc9c9c9);
			} else {
				//btnAddToSale.setTextColor(0Xffc9c9c9);
			}
			//For Richard Qoh coming wrong
			if (!totype.equals("")) {
				if (totype.equals("tosale")) {
					btnAddToReturn.setVisibility(View.GONE);
				} else {
					btnAddToSale.setVisibility(View.GONE);
				}
			}
		}
		if (mode.equals("oe") || mode.equals("orderEdit") || mode.equals("ie") || mode.equals("invoiceEdit")) {
			strEditedCusName = shippingDetails.getString("customerName", "");
		}
		// to load entry types in list
		entryList = loadEntryList();
		adptEntrySpn = new ArrayAdapter<String>(Transaction.this,
				android.R.layout.simple_dropdown_item_1line, entryList);
		adptEntrySpn.setDropDownViewResource(R.layout.spinner_item_layout);

		spnEntryType.setAdapter(adptEntrySpn);

		final String entryName = entryDetails.getString("entryName", "");
		txtTransCusName.setText(strEditedCusName);
		// to maintain entry name
		if (entryName.equals("")) {
			Editor ed = entryDetails.edit();
			ed.putString("entryName", "Order");
			ed.commit();
		} else {
			if (entryName.equals("Quote")) {
				spnEntryType.setSelection(1);
			}
		}
		if (mode.equals("he")) {
			String compname = getIntent().getStringExtra("value1");
			String price = getIntent().getStringExtra("value2");
			String discprice = getIntent().getStringExtra("value4");
			txtTransCusName.setText(compname);
			txtTransTotal.setText(price);
			txtTransDiscount.setText(discprice);
			txtTransTax.setText("1");
			txtModeTypeTotal.setText("Price");
			txtTransNetAmt.setText(price + discprice);
			btnAddToSale.setVisibility(View.GONE);
			btnAddToReturn.setVisibility(View.GONE);
			/*btnTransFinish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Transaction.this,EmailPage.class);
					startActivity(intent);
				}
			});*/


		}


		dbHelper.openReadableDatabase();
		transItemList = dbHelper.getTempItems();
		dbHelper.closeDatabase();



		if(mode.equals("he")){
			String refNo = getIntent().getStringExtra("refno");
			dbHelper.openReadableDatabase();
			his1List = dbHelper.getHistorySingleData(refNo);
			dbHelper.closeDatabase();

			adptHisItemList = new HistoryItemListAdapter(Transaction.this,his1List);

			lstTransItems.setAdapter(adptHisItemList);
//				adptHisItemList.notifyDataSetChanged();
				registerForContextMenu(lstTransItems);
			}
		else if (mode.equals("oe") || mode.equals("orderEdit") || mode.equals("ie") || mode.equals("invoiceEdit")) {
			adptTransItemList = new TransItemListAdapter(Transaction.this,
					transItemList);
			lstTransItems.setAdapter(adptTransItemList);


//		dbHelper.openReadableDatabase();
//			his1List = dbHelper.getHistoryData(cmpnyNo);
//			dbHelper.closeDatabase();
//
//
//			adptHisItemList =new HistoryItemListAdapter(this,his1List);
//
//
//
//			lstTransItems.setAdapter(adptHisItemList);
//			adptHisItemList.notifyDataSetChanged();
//			registerForContextMenu(lstTransItems);


			// for long press list item
			registerForContextMenu(lstTransItems);

			calculateTransDetails(transItemList);

			String custNumber = shippingDetails.getString("customerNumber", "");

			boolean crdLimExcd = calculateCL(custNumber);
			dbHelper.openReadableDatabase();
			boolean isNewCustomer = dbHelper.isNewCustomer(custNumber, cmpnyNo);
			dbHelper.closeDatabase();
			String allowCreditLimit = "";
			allowCreditLimit = supporter.getCreditAllowConformation();
			if (crdLimExcd && allowCreditLimit.equals("") && !isNewCustomer) {
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						Transaction.this);
				alertDialog.setTitle("Warning");
				alertDialog.setIcon(R.drawable.warning);
				alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("Proceed",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								supporter.setCreditAllowConformation("yes");
								dialog.dismiss();
							}
						});

				alertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								alertUserToCancelTransaction();
							}
						});

				alertDialog.setMessage("Credit limit exceeded");
				//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
			}

			spnEntryType.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {

				}

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
										   int arg2, long arg3) {
					String entryTyp = (String) spnEntryType
							.getItemAtPosition(spnEntryType
									.getSelectedItemPosition());
					Editor ed = entryDetails.edit();
					ed.putString("entryName", entryTyp);
					ed.commit();
				}

			});

			// return btn click function
			btnAddToSale.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//btnAddToReturn.setTextColor(0Xffc9c9c9);
					//btnAddToSale.setTextColor(0Xffffffff);

					Editor ed = entryDetails.edit();
					ed.putString("invEntTyp", "sale");
					ed.commit();
					Intent intent = new Intent(Transaction.this, ItemSelection.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("isEdit", false);
					intent.putExtra("orderNum", ordNo);
					startActivity(intent);
				}
			});

			btnAddToReturn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//btnAddToSale.setTextColor(0Xffc9c9c9);
					//btnAddToReturn.setTextColor(0Xffffffff);

					Editor ed = entryDetails.edit();
					ed.putString("invEntTyp", "return");
					ed.commit();
					Intent intent = new Intent(Transaction.this, ItemSelection.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("isEdit", false);
					intent.putExtra("orderNum", ordNo);
					startActivity(intent);
				}
			});


			// item edit click function
			lstTransItems.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
										long arg3) {

					final TempItem obj = transItemList.get(pos);
					Intent intent = new Intent(Transaction.this, ItemEntry.class);
					intent.putExtra("itemNumber", obj.getTemp_itemNum());
					intent.putExtra("locId", obj.getTemp_location());
					intent.putExtra("isEdit", true);
					intent.putExtra("eUom", obj.getTemp_uom());
					intent.putExtra("eQty", obj.getTemp_qty());
					intent.putExtra("ePrc", obj.getTemp_extPrice());
					intent.putExtra("eDate", obj.getTemp_date());
					intent.putExtra("eType", obj.getTemp_entryType());
					intent.putExtra("eDiscount", obj.getTemp_discount());
					intent.putExtra("eDiscType", obj.getTemp_discType());
					intent.putExtra("orderNumber", orderNo);
					startActivity(intent);
				}

			});

			edtTranScan.addTextChangedListener(scanTextChange);

			// april 26 barcode integration
			edtTranScan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int radioButtonID = radioGroup.getCheckedRadioButtonId();
					View radioButton = radioGroup.findViewById(radioButtonID);
					int idx = radioGroup.indexOfChild(radioButton);
					edtTranScan.setText(""); // to set empty edit box
					// jun 28 barcode integration based on Manf_Number
					if (idx == 0) {
						try {

							Intent intent = new Intent(
									"com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE",
									"QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 0);

						} catch (Exception e) {
							e.printStackTrace();
							LogfileCreator.appendLog(e.getMessage());
						}
					} else if (idx == 1) { // apr 26 barcode integration based on
						// UPC_Number
						try {

							Intent intent = new Intent(
									"com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE",
									"QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 1);

						} catch (Exception e) {
							e.printStackTrace();
							LogfileCreator.appendLog(e.getMessage());
						}
					}

				}

			});


			btnTransFinish.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					try {
						//For Richard Qoh coming wrong
						supporter.setSaleType("");
						btnTransFinish.setEnabled(false);

						int listCount = transItemList.size();

						if (listCount == 0) {
							final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
									Transaction.this);
							alertDialog.setTitle("Warning");
							alertDialog.setIcon(R.drawable.warning);
							alertDialog.setCancelable(false);
							alertDialog.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
															int which) {
											dialog.dismiss();
										}
									});

							alertDialog
									.setMessage("Please add some items to process this transaction");
							//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
						} else if (mode.equals("orderEdit")
								|| mode.equals("invoiceEdit")) {

							final String refNo = entryDetails.getString(
									"referencenumber", "");
							final String docNo = entryDetails
									.getString("docno", "");

							dbHelper.openReadableDatabase();
							final double receiptAmt = dbHelper.getPrepayAmount(
									refNo, cmpnyNo);
							dbHelper.closeDatabase();

							if ((receiptAmt <= 0.0) || netTotal > 0) {
								doEditAction(refNo, docNo, receiptAmt);
							} else {
								final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										Transaction.this);
								alertDialog.setTitle("Warning");
								alertDialog.setIcon(R.drawable.warning);
								alertDialog.setCancelable(false);
								alertDialog.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												doEditAction(refNo, docNo,
														receiptAmt);
											}
										});
								alertDialog.setNegativeButton("No",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												dbHelper.openWritableDatabase();
												dbHelper.deleteTempTableRecords();
												dbHelper.closeDatabase();
												supporter
														.clearSignPreference(signPreference);
												supporter.clearPreference(
														shippingDetails,
														entryDetails);
												supporter
														.simpleNavigateTo(MainMenu.class);
												dialog.dismiss();
											}
										});

								alertDialog
										.setMessage("Existing prepayment will be deleted. \nDo you want to continue?"
												+ "\nNote: Selecting No will cancel this transaction.");
								//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
							}

						} else {
							String entryTyp = (String) spnEntryType
									.getItemAtPosition(spnEntryType
											.getSelectedItemPosition());
							final String entryTyp_ = (String) spnEntryType
									.getItemAtPosition(spnEntryType
											.getSelectedItemPosition());
							if (mode.equals("ie")) {
								entryTyp = "Invoice";
							}


							// transaction is started here..
							dbHelper.openWritableDatabase();
							Log.i("Writable DB Open", "Writable Database Opened.");
							dbHelper.mBeginTransaction();
							Log.i("Transaction started",
									"Transaction successfully started for transaction page.");

							final Mspdb mspdb = dbHelper.getMspDbData(cmpnyNo,
									supporter.getSalesPerson());

							if (entryTyp.equals("Order")
									|| entryTyp.equals("Invoice")) {

								// change invoice no generation jun 28
								int number = 0;
								genOrdNo = "";

								number = mspdb.getMspdb_orderNumber();
								genOrdNo = mspdb.getMspdb_salesPerson() + "-"
										+ mspdb.getMspdb_mapNo() + "-"
										+ String.format("%09d", number);
						/*	boolean flag=saveLocation(genOrdNo, entryTyp);
							//If Location not available
							if(flag==true){
								
								
								//display continue or not alert
								 AlertDialog.Builder alertDialog = new AlertDialog.Builder(Transaction.this);
							      
								 	alertDialog.setTitle("Information");
									alertDialog.setIcon(R.drawable.tick);
							        alertDialog.setCancelable(false);
								    alertDialog.setMessage("Continue without accessing transaction location?");
							  
							        // On pressing Settings button
							        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog,int which) {
							            	saveTransaction(entryTyp_, mspdb, genOrdNo);
											saveSignature(genOrdNo, entryTyp_);
											
											dbHelper.mSetTransactionSuccess(); // setting the
																				// transaction
																				// successfull.
											Log.i("Transaction success", "Transaction success.");
											dbHelper.mEndTransaction();
											Log.i("Transaction success", "Transaction end.");
											dbHelper.closeDatabase();
											Log.i("DB closed", "Database closed successfully.");

											// display alert
											final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
													Transaction.this);
											alertDialog.setTitle("Information");
											alertDialog.setIcon(R.drawable.tick);
											alertDialog.setCancelable(false);
											alertDialog.setPositiveButton("OK",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {

															if ((netTotal > 0)
																	&& (prepayStatusSetting == 1)) {
																openPrepaymentWindow(genOrdNo);
															} else {
																// supporter.simpleNavigateTo(MainMenu.class);
																dialog.dismiss();
																supporter.clearPreference(
																		shippingDetails,
																		entryDetails);
																if (autoReportSetting == 1) {
																	callPrinter(genOrdNo);
																} else {
																	supporter
																			.simpleNavigateTo(MainMenu.class);
																}
															}

														}
													});

											alertDialog.setMessage(entryTyp_
													+ " For. \nRef. No. " + genOrdNo);
											//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
							            }
							        });
							  
							        // on pressing cancel button
							        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog, int which) {
							            	dbHelper.mSetTransactionSuccess(); 
											Log.i("Transaction failed",
													"Transaction failed.");
											dbHelper.mEndTransaction();
											Log.i("Transaction failed", "Transaction end.");
											dbHelper.closeDatabase();
											Log.i("DB closed",
													"Database closed successfully.");
											supporter
											.simpleNavigateTo(MainMenu.class);
							            }
							        });
							  
							        // Showing Alert Message
							        //alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

							}
							else
							{*/
								saveTransaction(entryTyp, mspdb, genOrdNo);
								saveSignature(genOrdNo, entryTyp);

								dbHelper.mSetTransactionSuccess(); // setting the
								// transaction
								// successfull.
								Log.i("Transaction success", "Transaction success.");
								dbHelper.mEndTransaction();
								Log.i("Transaction success", "Transaction end.");
								dbHelper.closeDatabase();
								Log.i("DB closed", "Database closed successfully.");

								// display alert
								final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										Transaction.this);
								alertDialog.setTitle("Information");
								alertDialog.setIcon(R.drawable.tick);
								alertDialog.setCancelable(false);
								alertDialog.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												if ((netTotal > 0)
														&& (prepayStatusSetting == 1)) {
													openPrepaymentWindow(genOrdNo);
												} else {
													// supporter.simpleNavigateTo(MainMenu.class);
													dialog.dismiss();
													supporter.clearPreference(
															shippingDetails,
															entryDetails);
													if (autoReportSetting == 1) {
														callPrinter(genOrdNo);
													} else {
														supporter
																.simpleNavigateTo(MainMenu.class);
													}
												}

											}
										});

								alertDialog.setMessage(entryTyp
										+ " for Ref. No. " + genOrdNo);
								//alertDialog.getWindow().getAttributes();
								//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

								/*Button btn1 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
								btn1.setTextSize(16);*/
								//}//ELSE CONTION BRACE FOR IF(FLAG=TRUE)


							} else if (entryTyp.equals("Quote")) {
								int number = mspdb.getMspdb_quoteNumber();

								final String ordNo = "QT-"
										+ mspdb.getMspdb_salesPerson() + "-"
										+ mspdb.getMspdb_mapNo() + "-"
										+ String.format("%09d", number);

								saveTransaction(entryTyp, mspdb, ordNo);
								saveSignature(ordNo, entryTyp);

								dbHelper.mSetTransactionSuccess(); // setting the
								// transaction
								// successfull.
								Log.i("Transaction success", "Transaction success.");
								dbHelper.mEndTransaction();
								Log.i("Transaction success", "Transaction end.");
								dbHelper.closeDatabase();
								Log.i("DB closed", "Database closed successfully.");

								// display alert
								final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										Transaction.this);
								alertDialog.setTitle("Information");
								alertDialog.setIcon(R.drawable.tick);
								alertDialog.setCancelable(false);
								alertDialog.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// supporter.simpleNavigateTo(MainMenu.class);
												supporter.clearPreference(
														shippingDetails,
														entryDetails);
												dialog.dismiss();
												if (autoReportSetting == 1) {
													callPrinter(ordNo);
												} else {
													supporter
															.simpleNavigateTo(MainMenu.class);
												}
											}
										});

								alertDialog.setMessage(entryTyp
										+ " for Ref. No. " + ordNo);
								//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

							}
						}

					} catch (Exception e) {
						LogfileCreator.appendLog(e.getMessage());
						btnTransFinish.setEnabled(true);
					}
				}

			});

			// Customer Name Editing option for printing purpose only...
			btnCusNameEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					final Dialog dialog = new Dialog(Transaction.this);
					dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
					dialog.setContentView(R.layout.customer_name_edit_dialog_layout);
					// dialog.setCancelable(false);
					dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
							R.drawable.cus_edit_dlg);
					dialog.setTitle("Edit Customer Name");
					dialog.show();

					final EditText edtDlgCusName = (EditText) dialog
							.findViewById(R.id.edtTxt_CusName);
					Button btnOk = (Button) dialog
							.findViewById(R.id.btn_cusNameDlgOk);

					edtDlgCusName.setText(shippingDetails.getString("customerName",
							""));
					btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							strEditedCusName = edtDlgCusName.getText().toString();
							if (strEditedCusName.equals("")) {
								strEditedCusName = shippingDetails.getString(
										"customerName", "");
							}

							txtTransCusName.setText(strEditedCusName);
							dialog.dismiss();
						}
					});

				}
			});
		}

	} // end of onCreate...

	private void callPrinter(final String refNo) {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Transaction.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {



						//</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
							Intent intent = new Intent(Transaction.this, TransPDFCreate.class);
                        intent.putExtra("refno", refNo);
                        startActivity(intent);
						}
						else{
						// dbHelper.openReadableDatabase();
						List<Devices> deviceList = supporter.getPairedDevices();
						// dbHelper.closeDatabase();

						if (deviceList.size() != 0) {
							final TransDetailDialog dialog1 = new TransDetailDialog(
									Transaction.this, refNo, dbHelper,
									supporter, deviceList);
							dialog1.setContentView(R.layout.print_list_layout);
							dialog1.show();
						} else {
							toastMsg.showToast(Transaction.this,
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
		//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	protected void navigationProcess(String refNo, Class<?> cls) {
		Intent intent = new Intent(Transaction.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("referencenumber", refNo);
		startActivity(intent);
	}

	private void doEditAction(final String refNo, String docNo,
			final double receiptAmt) {

		try {
			tempDeletedItems = getDeletedItemList();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// transaction is started here..
		dbHelper.openWritableDatabase();
		Log.i("Writable DB Open", "Writable Database Opened.");
		dbHelper.mBeginTransaction();
		Log.i("Transaction started",
				"Transaction successfully started for transaction page.");

		// items deleted from temp items table when order edit
		// and
		// invoice edit needs to be deleted from tran table.
		//For Invoice Edit
		for (int i = 0; i < tempDeletedItems.size(); i++) {
			String uom = "";
			double conFact = 0.0;
			double rQty = 0.0;

			uom = tempDeletedItems.get(i).getTemp_uom();
			String itemNum = tempDeletedItems.get(i).getTemp_itemNum();
			String locId = tempDeletedItems.get(i).getTemp_location();

			String tranType = tempDeletedItems.get(i).getTemp_entryType();
			conFact = dbHelper.getUOMConvFactor(itemNum, uom, cmpnyNo);

			rQty = Double.parseDouble(tempDeletedItems.get(i).getTemp_qty())
					* conFact;

			if (tranType.equals("ts") || tranType.equals("tr")) {
				if (tranType.equals("ts")) {
					dbHelper.updateItemOnHand(itemNum, "add", rQty, locId + "",
							cmpnyNo);
				} else {
					dbHelper.updateItemOnHand(itemNum, "remove", rQty, locId
							+ "", cmpnyNo);
				}
			}

			dbHelper.deleteTranItem(refNo, tempDeletedItems.get(i));
		}

		String entryTyp = (String) spnEntryType.getItemAtPosition(spnEntryType
				.getSelectedItemPosition());
		final String tempEntryTyp = entryTyp;

		final Mspdb mspdb = dbHelper
				.getMspDbData(cmpnyNo, supporter.getSalesPerson());

		if (mode.equals("invoiceEdit")) {
			entryTyp = "Invoice";
			mspdb.setMspdb_orderNumber(Integer.parseInt(docNo));
		} else {
			if (entryTyp.equals("Quote")) {
				mspdb.setMspdb_quoteNumber(Integer.parseInt(docNo));
			} else if (entryTyp.equals("Order")) {
				mspdb.setMspdb_orderNumber(Integer.parseInt(docNo));
			}
		}
	/*	boolean flag=saveLocation(genOrdNo, entryTyp);
		//If Location not available
		if(flag==true){
			dbHelper.mSetTransactionSuccess(); 
			Log.i("Transaction failed",
					"Transaction failed.");
			dbHelper.mEndTransaction();
			Log.i("Transaction failed", "Transaction end.");
			dbHelper.closeDatabase();
			Log.i("DB closed",
					"Database closed successfully.");
			supporter
			.simpleNavigateTo(MainMenu.class);

			
			
			//display continue or not alert
			 AlertDialog.Builder alertDialog = new AlertDialog.Builder(Transaction.this);
		      
		     
		        alertDialog.setCancelable(false);
		        alertDialog.setMessage("Continue without accessing transaction location?");
		        alertDialog.setTitle("Information");
				alertDialog.setIcon(R.drawable.tick);
				alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	saveTransaction(tempEntryTyp, mspdb, genOrdNo);
						saveSignature(genOrdNo, tempEntryTyp);
						
						dbHelper.mSetTransactionSuccess(); // setting the
															// transaction
															// successfull.
						Log.i("Transaction success", "Transaction success.");
						dbHelper.mEndTransaction();
						Log.i("Transaction success", "Transaction end.");
						dbHelper.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");

						// display alert
						final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								Transaction.this);
						alertDialog.setTitle("Information");
						alertDialog.setIcon(R.drawable.tick);
						alertDialog.setCancelable(false);
						alertDialog.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {

										if ((netTotal > 0)
												&& (prepayStatusSetting == 1)) {
											openPrepaymentWindow(genOrdNo);
										} else {
											// supporter.simpleNavigateTo(MainMenu.class);
											dialog.dismiss();
											supporter.clearPreference(
													shippingDetails,
													entryDetails);
											if (autoReportSetting == 1) {
												callPrinter(genOrdNo);
											} else {
												supporter
														.simpleNavigateTo(MainMenu.class);
											}
										}

									}
								});

						alertDialog.setMessage(tempEntryTyp
								+ " For. \nRef. No. " + genOrdNo);
						//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
		            }
		        });
		  
		        // on pressing cancel button
		        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dbHelper.mSetTransactionSuccess(); 
						Log.i("Transaction failed",
								"Transaction failed.");
						dbHelper.mEndTransaction();
						Log.i("Transaction failed", "Transaction end.");
						dbHelper.closeDatabase();
						Log.i("DB closed",
								"Database closed successfully.");
						supporter
						.simpleNavigateTo(MainMenu.class);
		            }
		        });
		  
		        // Showing Alert Message
		        //alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
			
		
		}
		else
		{*/
			saveTransaction(entryTyp, mspdb, refNo);
			saveSignature(refNo, entryTyp);

			dbHelper.mSetTransactionSuccess(); // setting the
												// transaction
												// successfull.
			Log.i("Transaction success", "Transaction success.");
			dbHelper.mEndTransaction();
			Log.i("Transaction success", "Transaction end.");
			dbHelper.closeDatabase();
			Log.i("DB closed", "Database closed successfully.");

			// display alert
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					Transaction.this);
			alertDialog.setTitle("Information");
			alertDialog.setIcon(R.drawable.tick);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// to update prepayment if exists

							if ((receiptAmt > 0.0) && netTotal > 0) {
								dbHelper.openWritableDatabase();
								dbHelper.updatePrepaymentThrTran(refNo, netTotal,
										receiptAmt, cmpnyNo);
								dbHelper.closeDatabase();
							} else if ((receiptAmt > 0.0) && netTotal <= 0) {
								// to delete prepayment
								dbHelper.openWritableDatabase();
								dbHelper.deletePrepayment(refNo, cmpnyNo);
								dbHelper.closeDatabase();
							}

							if ((prepayStatusSetting == 1)
									&& !tempEntryTyp.equals("Quote")) {
								if (netTotal > 0) {
									openPrepaymentWindow(refNo);
								} else {
									supporter.clearPreference(shippingDetails,
											entryDetails);
									supporter.clearPreference(signPreference);
									if (autoReportSetting == 1) {
										callPrinter(refNo);
									} else {
										supporter.simpleNavigateTo(MainMenu.class);
									}
								}
							} else {

								// supporter.simpleNavigateTo(MainMenu.class);

								supporter.clearPreference(shippingDetails,
										entryDetails);
								supporter.clearPreference(signPreference);
								if (autoReportSetting == 1) {
									callPrinter(refNo);
								} else {
									supporter.simpleNavigateTo(MainMenu.class);
								}

							}
							dialog.dismiss();
						}

					});

			alertDialog.setMessage(entryTyp + " Edited. \nRef. No. " + refNo);
			//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
		//}//ELSE CONTION BRACE FOR IF(FLAG=TRUE)


	}

	TextWatcher scanTextChange = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {

			 /*String itmNum = edtTranScan.toString().trim();
			 scanStarter(itmNum);*/
			 int radioButtonID = radioGroup.getCheckedRadioButtonId();
				View radioButton = radioGroup.findViewById(radioButtonID);
				int idx = radioGroup.indexOfChild(radioButton);
				// jun 28 barcode integration based on Manf_Number
				if (idx == 0) {
					try {

						String manfCode = edtTranScan.getText().toString().trim();
						manfCode = manfCode.toUpperCase();

						dbHelper.openReadableDatabase();
						Manf_Number01 manf = dbHelper.getItemFromManfNum(manfCode,
								cmpnyNo);
						dbHelper.closeDatabase();

						if (manf != null) {
							scanManfStarter(manf.getManuf_itemno(),
									manf.getManuf_uom());
						} else {
							// toastMsg.showToast(Transaction.this,
							// "Item not available");
						}
					} catch (Exception e) {
						e.printStackTrace();
						LogfileCreator.appendLog(e.getMessage());
					}
				} else if (idx == 1) { // apr 26 barcode integration based on
										// UPC_Number
					try {
						String upcCode = edtTranScan.getText().toString().trim();
						upcCode = upcCode.toUpperCase();

						dbHelper.openReadableDatabase();
						String itemNumber = dbHelper.getItemFromUpc(upcCode,
								cmpnyNo);
						dbHelper.closeDatabase();

						if (!itemNumber.equals("")) {
							scanStarter(itemNumber);
						} else {
							// toastMsg.showToast(Transaction.this,
							// "Item not available");
						}
					} catch (Exception e) {
						e.printStackTrace();
						LogfileCreator.appendLog(e.getMessage());
					}
				}

		}

	};

	// In the same activity you?ll need the following to retrieve the results:
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			// Scan Result for ManfNumber
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String manfCode = intent.getStringExtra("SCAN_RESULT");

				dbHelper.openReadableDatabase();
				Manf_Number01 manf = dbHelper.getItemFromManfNum(manfCode,
						cmpnyNo);
				dbHelper.closeDatabase();

				if (manf != null) {
					scanManfStarter(manf.getManuf_itemno(), manf.getManuf_uom());
				} else {
					toastMsg.showToast(Transaction.this, "Item not available");
				}

			} else if (resultCode == RESULT_CANCELED) {
				toastMsg.showToast(Transaction.this, "Scanning cancelled");
				Log.i("Scanning cancelled ", "Scanning cancelled");
			}
		} else if (requestCode == 1) {
			// Scan Result for UPC
			if (resultCode == RESULT_OK) {
				Log.i("Scan resul format: ",
						intent.getStringExtra("SCAN_RESULT_FORMAT"));

				String upcCode = intent.getStringExtra("SCAN_RESULT");

				dbHelper.openReadableDatabase();
				String itemNumber = dbHelper.getItemFromUpc(upcCode, cmpnyNo);
				dbHelper.closeDatabase();

				if (!itemNumber.equals("")) {
					scanStarter(itemNumber);
				} else {
					toastMsg.showToast(Transaction.this, "Item not available");
				}

			} else if (resultCode == RESULT_CANCELED) {
				toastMsg.showToast(Transaction.this, "Scanning cancelled");
				Log.i("Scanning cancelled ", "Scanning cancelled");
			}

		}
	}


	// Scan UPC/Item Number
	private void scanStarter(String itmNum) {

		itmNum = itmNum.toUpperCase();

		HhItem01 item = null;
		dbHelper.openReadableDatabase();
		/*tempitem_e=dbHelper.getTempData();
		String location=tempitem_e.getTemp_location();*/
		item = dbHelper.getItemDataByNum(itmNum, cmpnyNo);
		dbHelper.closeDatabase();
		if (item != null) {

			scanItemProcess(itmNum,item);
		}
	}

	// Scan ManfNumber/Item Number
	private void scanManfStarter(String itmNum, String uom) {

		itmNum = itmNum.toUpperCase();
		HhItem01 item = null;
		dbHelper.openReadableDatabase();
		/*tempitem_e=dbHelper.getTempData();
		String location=tempitem_e.getTemp_location();*/
		item = dbHelper.getItemDataByNum(itmNum, uom, cmpnyNo);
		dbHelper.closeDatabase();
		if (item != null)  {
			scanItemProcess(itmNum,item);
		}
	}

	// method to control menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transaction_menu, menu);
		return true;
	}

	// method to control menu options
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.trans_mnu_add:

			Intent intent = new Intent(Transaction.this, ItemSelection.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isEdit", false);
			intent.putExtra("orderNum", ordNo);
			startActivity(intent);
			break;

		case R.id.trans_mnu_sign:
			supporter.simpleNavigateTo(Sign.class);
			break;

		}
		return true;
	}

	// method to help prepayment process
	private void openPrepaymentWindow(String ordNo) {
		Intent intent = new Intent(Transaction.this, Prepayment.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("refNo", ordNo);
		intent.putExtra("netTotal", netTotal);
		startActivity(intent);
	}

	private void saveTransaction(String entryTyp, Mspdb mspdb, String ordNo) {
		HhTran01 transDetail = null;

		Calendar todayCal = Calendar.getInstance();

		todayCal.set(Calendar.HOUR_OF_DAY, 0);
		todayCal.set(Calendar.MINUTE, 0);
		todayCal.set(Calendar.SECOND, 0);
		todayCal.set(Calendar.MILLISECOND, 0);

		String custNum = shippingDetails.getString("customerNumber", "");
		String strOrdShipDate = shippingDetails.getString("shipDate", "");

		Calendar shipDateCal = supporter.stringDateToCalender(strOrdShipDate);

		HhCustomer01 customer = dbHelper.getCustomerData(custNum, cmpnyNo);

		String custPriceList = supporter.getPriceList();

		for (int i = 0; i < transItemList.size(); i++) {

			String itemNum = transItemList.get(i).getTemp_itemNum();
			// HhItem01 item = dbHelper.getItemData(itemNum);

			double exPrc = Double.parseDouble(transItemList.get(i)
					.getTemp_extPrice());

			String strExpShipDate = transItemList.get(i).getTemp_date();
			Calendar expShipDate = supporter
					.stringDateToCalender(strExpShipDate);

			// adding transaction detail to object
			transDetail = new HhTran01();

			transDetail.setHhTran_referenceNumber(ordNo);

			double qanty = Double.parseDouble(transItemList.get(i)
					.getTemp_qty());

			// For Invoice Edit
			String item = transItemList.get(i).getTemp_itemNum();
			String itemUom = transItemList.get(i).getTemp_uom();

			// By Pranesh
			// For Invoice Edit
			if (mode.equals("invoiceEdit")) {
				final String refNo = entryDetails.getString("referencenumber",
						"");
				String locId = transItemList.get(i).getTemp_location();
				String prvEntType = transItemList.get(i).getTemp_entryType();

				HhTran01 transactionData = dbHelper.getTransactionList_(refNo,
						item, locId, itemUom, prvEntType);

				double prevqty = 0;
				if (transactionData != null)
					prevqty = Double.parseDouble(transactionData
							.getHhTran_qty() + "");

				qanty = (qanty - prevqty);

			}

			String uom = transItemList.get(i).getTemp_uom();

			double conFact = dbHelper.getUOMConvFactor(itemNum, uom, cmpnyNo);

			double rQty = qanty * conFact; // to get actual quantity using uom

			int rNumber = mspdb.getMspdb_orderNumber();

			double discount = Double.parseDouble(transItemList.get(i)
					.getTemp_discount());
			double netPrice = exPrc - discount;

			if (entryTyp.equals("Invoice")) {

				String eTyp = transItemList.get(i).getTemp_entryType();

				if (eTyp.equals("ts")) {
					transDetail.setHhTran_transType("I"); // for tosale

					if (mode.equals("ie") || mode.equals("oe")) { // for edit
																	// transactions
																	// the below
																	// update is
																	// done in
																	// dbhelper
																	// class.

						dbHelper.updateItemOnHand(itemNum, "remove", rQty,
								transItemList.get(i).getTemp_location(),
								cmpnyNo);

					}
				} else if (eTyp.equals("tr")) {
					transDetail.setHhTran_transType("CN"); // for toreturn
					if (mode.equals("ie") || mode.equals("oe")) { // for edit
																	// transactions
																	// the below
																	// update is
																	// done in
																	// dbhelper
																	// class.

						dbHelper.updateItemOnHand(itemNum, "add", rQty,
								transItemList.get(i).getTemp_location(),
								cmpnyNo);

					}
				}
				transDetail.setHhTran_invoiceNumber(rNumber + "");
				transDetail.setHhTran_orderNumber("0"); // zero for invoice
														// number
			} else if (entryTyp.equals("Order")) {
				transDetail.setHhTran_transType("S"); // for order
				transDetail.setHhTran_orderNumber(rNumber + "");
				transDetail.setHhTran_invoiceNumber("0");
			} else if (entryTyp.equals("Quote")) {
				rNumber = mspdb.getMspdb_quoteNumber();
				transDetail.setHhTran_transType("Q"); // for quote
				transDetail.setHhTran_orderNumber(rNumber + "");
				transDetail.setHhTran_invoiceNumber("0");
			}

			transDetail.setHhTran_transDay(todayCal.get(Calendar.DAY_OF_MONTH)); // transaction
																					// day
			transDetail.setHhTran_transMonth(todayCal.get(Calendar.MONTH) + 1); // transaction
																				// month
			transDetail.setHhTran_transYear(todayCal.get(Calendar.YEAR)); // transaction
																			// year

			transDetail.setHhTran_expShipDay(expShipDate
					.get(Calendar.DAY_OF_MONTH)); // item ship day
			transDetail
					.setHhTran_expShipMonth(expShipDate.get(Calendar.MONTH) + 1); // item
																					// ship
																					// month
			transDetail.setHhTran_expShipYear(expShipDate.get(Calendar.YEAR)); // item
																				// ship
																				// year

			transDetail.setHhTran_customerNumber(custNum);
			transDetail.setHhTran_salesPerson(mspdb.getMspdb_salesPerson());
			transDetail.setHhTran_itemNumber(itemNum);
			transDetail
					.setHhTran_locId(transItemList.get(i).getTemp_location());
			transDetail.setHhTran_terms(customer.getHhCustomer_terms()); // customer
																			// terms
																			// code
			transDetail.setHhTran_currency(supporter.getCompanyCurrency());
			transDetail.setHhTran_priceListCode(custPriceList);
			transDetail.setHhTran_uom(transItemList.get(i).getTemp_uom());

			int qQty = Integer.parseInt(transItemList.get(i).getTemp_qty());

			transDetail.setHhTran_qty(qQty);
			transDetail.setHhTran_price(exPrc / (double) qQty); // price without
																// discount
			transDetail.setHhTran_discPrice(0.00);
			transDetail.setHhTran_netPrice(netPrice);
			transDetail
					.setHhTran_extPrice(netPrice + getTax(netPrice, itemNum));

			transDetail.setHhTran_tax(getTax(netPrice, itemNum));
			transDetail.setHhTran_shipToCode(shippingDetails.getString(
					"shipLoc", ""));
			transDetail.setHhTran_shipViaCode(shippingDetails.getString(
					"shipViaCode", ""));
			transDetail.setHhTran_status(0); // change status to 1 if export
												// completed
			transDetail.setHhTran_lineItem(i + 1); // item position in the list
			transDetail.setHhTran_discValue(discount);
			transDetail.setHhTran_discType(transItemList.get(i)
					.getTemp_discType()); // discount type (percent - p,
			// amount - a)
			transDetail.setHhTran_ordShipDay(shipDateCal
					.get(Calendar.DAY_OF_MONTH));
			transDetail
					.setHhTran_ordShipMonth(shipDateCal.get(Calendar.MONTH) + 1);
			transDetail.setHhTran_ordShipYear(shipDateCal.get(Calendar.YEAR));
			transDetail.setHhTran_editable(0); // change editable to 1 if export
												// completed

			Manf_Number01 manufDet = dbHelper.getManufItemDetail(itemNum,
					cmpnyNo);

			if (manufDet != null) {
				transDetail.setHhTran_manItemNo(manufDet.getManuf_manitemno()
						+ ""); // manufature item number
			} else {
				transDetail.setHhTran_manItemNo(""); // manufature item number
			}

			transDetail.setHhTran_preTax(0.00); // need to understand
			transDetail.setHhTran_refNo(rNumber + "");

			transDetail.setHhTran_orderdiscount(0.00); // need to understand
			transDetail.setHhTran_taxamount(0.00); // need to understand
			transDetail.setHhTran_totalcarton(0.00); // need to understand
			transDetail.setHhTran_editedcustomername(strEditedCusName);
			transDetail.setHhTran_itemunitweight(0.00); // need to understand
			transDetail.setHhTran_companycode(cmpnyNo);
			String st = getIntent().getStringExtra("Value");
			//transDetail.setHhSalescommission(st);


			//Location details
			transDetail.setHhTran_lat(latitude);
			transDetail.setHhTran_lon(longitude);




			if (mode.equals("orderEdit") || mode.equals("invoiceEdit")) {

				dbHelper.updateOrAddTranTable(ordNo, transDetail,qanty , cmpnyNo);

			} else {

				dbHelper.addTransactionDetail(transDetail);

//				dbHelper.addHistory(history01);
			}

		}

		dbHelper.deleteTempTableRecords();

		transItemList.clear();
		adptTransItemList.clear();
		adptTransItemList.notifyDataSetChanged();

		if (mode.equals("ie") || mode.equals("oe")) { // update only for new
														// transactions
			if (entryTyp.equals("Invoice")) {
				mspdb.setMspdb_orderNumber(mspdb.getMspdb_orderNumber() + 1);
			} else if (entryTyp.equals("Order")) {
				mspdb.setMspdb_orderNumber(mspdb.getMspdb_orderNumber() + 1);
			} else {
				mspdb.setMspdb_quoteNumber(mspdb.getMspdb_quoteNumber() + 1);
			}

			dbHelper.updateMspDb(mspdb, cmpnyNo);

		}

	}
/*	private boolean saveLocation(String refNo, String entryTyp) {
		// save signature

		gps = new GPSTracker(Transaction.this);
		if(gps.canGetLocation()){
			latitude = gps.getLatitude();
            longitude = gps.getLongitude();
			//toastMsg.showToast(Transaction.this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
			return false;
        }else{

        	//toastMsg.showToast(Transaction.this, "Please on the GPS to perform the Transaction");
        	//gps.showSettingsAlert();
        	return true;
        }

	}*/
	private void saveSignature(String refNo, String entryTyp) {
		// save signature

		String strSign = "";
		strSign = signPreference.getString("signature", "");

		if (!strSign.equals("")) {
			Calendar c = Calendar.getInstance();
			String today = supporter.getDefaultStringDateFormat(
					c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
					c.get(Calendar.DAY_OF_MONTH));

			/*
			 * String type = "S"; // ref no update if (entryTyp.equals("Quote"))
			 * { type = "Q"; }
			 * 
			 * if (type.equals("Q") && !refNo.startsWith("QT")) { refNo = "QT-"
			 * + refNo; } else if (!type.equals("Q") && refNo.startsWith("QT"))
			 * { String[] splits = refNo.split("QT-"); refNo = splits[1]; }
			 */

			Bitmap mBitmap = supporter.decodeStringToImage(strSign);
			HhCompany company = dbHelper.getCompanyData(cmpnyNo);
			String cmpName = company.getCompany_name();

			HhSignature signature = new HhSignature();
			signature.setHhSignature_comapanyname(cmpName);
			signature.setHhSignature_referencenumber(refNo);
			signature.setHhSignature_signature(mBitmap);
			signature.setHhSignature_status(0);
			signature.setHhSignature_date(today);
			signature.setHhSignature_companycode(cmpnyNo);

			if (mode.equals("orderEdit") || mode.equals("invoiceEdit")) {

				byte[] signByte = dbHelper.getSignature(refNo, cmpnyNo);
				if (signByte != null) {
					dbHelper.updateSignature(signature, cmpnyNo);
				} else {
					dbHelper.addSignature(signature);
				}

			} else {

				dbHelper.addSignature(signature);

			}

		}
	}

	// method to delete list item if it long pressed
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view,
			final ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.lst_TransItems) {

			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			final TempItem obj = transItemList.get(info.position);

			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					Transaction.this);
			alertDialog.setTitle("Confirm Delete");
			alertDialog.setIcon(R.drawable.dlg_delete);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dbHelper.openWritableDatabase();
							dbHelper.deleteTempItem(obj);
							dbHelper.closeDatabase();

							if (mode.equals("orderEdit")
									|| mode.equals("invoiceEdit")) {

								try {
									addDeletedItem(obj);
								} catch (IOException e) {
									e.printStackTrace();
								}

							}
							//For Richard Qoh coming wrong

							if(transItemList.size()==1)
							{
								supporter.setSaleType("");
							}
							adptTransItemList.remove(obj);
							transItemList.remove(obj);
							adptTransItemList.notifyDataSetChanged();
							calculateTransDetails(transItemList);
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
			//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

		}
	}

	private void addDeletedItem(TempItem obj) throws IOException {

		if (null == tempDeletedItems) {
			tempDeletedItems = new ArrayList<TempItem>();
		}
		tempDeletedItems.add(obj);

		// save the deleted item list to preference
		SharedPreferences prefs = getSharedPreferences("entryDetails",
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("deletedItems",
				ObjectSerializer.serialize((Serializable) tempDeletedItems));
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	private List<TempItem> getDeletedItemList() throws ClassNotFoundException,
			IOException {

		if (null == tempDeletedItems) {
			tempDeletedItems = new ArrayList<TempItem>();
		}

		// load list from preference
		SharedPreferences prefs = getSharedPreferences("entryDetails",
				Context.MODE_PRIVATE);

		tempDeletedItems = (ArrayList<TempItem>) ObjectSerializer
				.deserialize(prefs.getString("deletedItems",
						ObjectSerializer.serialize(new ArrayList<TempItem>())));

		return tempDeletedItems;
	}

	// to calculate resultant transaction values
	private void calculateTransDetails(List<TempItem> transItemList2) {

		double transTotal = 0.00;
		int transQty = 0;
		double tax = 0.00;
		double preTax = 0.00;
		double netTot = 0.00;
		double discount = 0.00;

		for (int i = 0; i < transItemList2.size(); i++) {
			TempItem item = transItemList2.get(i);

			String type = item.getTemp_entryType();
			String strTransTot = item.getTemp_extPrice();
			String strQty = item.getTemp_qty();
			String itemNumb = item.getTemp_itemNum();
			String strDiscount = item.getTemp_discount();

			if (type.equals("tr")) {

				// to calculate total
				double cTot = Double.parseDouble(strTransTot);
				transTotal = transTotal - cTot;

				// to calculate qty
				int cQty = Integer.parseInt(strQty);
				transQty = transQty - cQty;

				// to calculate discount
				double cDiscount = Double.parseDouble(strDiscount);
				discount = discount - cDiscount;

				// to calculate tax
				dbHelper.openReadableDatabase();
				double cTax = getTax((cTot - cDiscount), itemNumb);
				dbHelper.closeDatabase();

				tax = tax - cTax;

			} else {
				// to calculate total
				double cTot = Double.parseDouble(strTransTot);
				transTotal = transTotal + cTot;

				// to calculate qty
				int cQty = Integer.parseInt(strQty);
				transQty = transQty + cQty;

				// to calculate
				double cDiscount = Double.parseDouble(strDiscount);
				discount = discount + cDiscount;

				// to calculate tax
				dbHelper.openReadableDatabase();
				double cTax = getTax((cTot - cDiscount), itemNumb);
				dbHelper.closeDatabase();

				tax = tax + cTax;

			}

		}

		netTot = transTotal + tax + preTax - discount;
		netTotal = netTot;
        String st = getIntent().getStringExtra("Value");
		strTransTotal = supporter.getCurrencyFormat(transTotal);
		strTransDiscount = supporter.getCurrencyFormat(discount);
		strTransTax = supporter.getCurrencyFormat(tax);
		strTransPreTax = supporter.getCurrencyFormat(preTax);
		strTransNetAmt = supporter.getCurrencyFormat(netTot);
		txtTransTotal.setText(strTransTotal);
		txtTransTotalQty.setText(transQty + "");



//		txtTransTotalQty.setText((CharSequence) ed);
		txtTransDiscount.setText(strTransDiscount);
//		txtSalesCommission.setText(st);
		txtTransTax.setText(strTransTax);
		// txtTransPreTax.setText(strTransPreTax);
		txtTransNetAmt.setText(strTransNetAmt);
	}

	public boolean calculateCL(String cusNum) {
		boolean isCreditLimitExceeds = false;

		double result = 0.0;

		dbHelper.openReadableDatabase();
		double creditLimit = dbHelper.getCustomerCreditLimit(cusNum, cmpnyNo);
		double pendingBalance = dbHelper.getPendingBalance(cusNum, cmpnyNo);
		dbHelper.closeDatabase();
		double curTot = Double.parseDouble(txtTransTotal.getText().toString());
		result = (creditLimit - pendingBalance) - curTot;

		if (result <= 0) {
			isCreditLimitExceeds = true;
		}

		return isCreditLimitExceeds;
	}

	// to load entry types
	private List<String> loadEntryList() {

		List<String> list = new ArrayList<String>();
		list.add("Order");
		list.add("Quote");

		return list;
	}

	// to calculate tax
	private double getTax(double price, String itemNumber) { // use itemnumber
																// to calculate
																// tax from
																// itemtaxrate
		double sTax = 0.0;
		double percent = 0.0;
		String customerNumber = shippingDetails.getString("customerNumber", "");

		HhCustomer01 customer = dbHelper.getCustomerData(customerNumber,
				cmpnyNo);

		List<List<String>> taxables = getTaxables(customer, itemNumber);

		int size = taxables.size();

		if (size != 0) {

			List<String> authItm = taxables.get(0);
			List<String> cusTaxstts = taxables.get(1);
			List<String> itmTaxstts = taxables.get(2); // Issue fixed from
														// get(1) to get(2)

			for (int i = 0; i < authItm.size(); i++) {
				percent = percent
						+ dbHelper.getTaxRate(authItm.get(i),
								cusTaxstts.get(i), itmTaxstts.get(i), cmpnyNo);
			}
		}
		if (percent != 0.0) {
			sTax = (price * percent) / 100; // to calcu
		}
		sTax = Double.parseDouble(supporter.getCurrencyFormatWithDbOpen(sTax));
		return sTax;
	}

	private List<List<String>> getTaxables(HhCustomer01 customer, String itemNum) {

		List<List<String>> taxables = new ArrayList<List<String>>();

		List<String> auth = new ArrayList<String>();
		List<String> txble = new ArrayList<String>();
		List<String> cusTxstts = new ArrayList<String>();
		List<String> itemTxstts = new ArrayList<String>();

		String taxauthority1 = "";
		String taxauthority2 = "";
		String taxauthority3 = "";
		String taxauthority4 = "";
		String taxauthority5 = "";

		taxauthority1 = customer.getHhCustomer_taxauthority1();
		taxauthority2 = customer.getHhCustomer_taxauthority2();
		taxauthority3 = customer.getHhCustomer_taxauthority3();
		taxauthority4 = customer.getHhCustomer_taxauthority4();
		taxauthority5 = customer.getHhCustomer_taxauthority5();

		if ((taxauthority1 != null) && (!taxauthority1.isEmpty())) {
			auth.add(taxauthority1);
			txble.add(customer.getHhCustomer_taxable1() + "");
			cusTxstts.add(customer.getHhCustomer_taxstts1());
		}

		if ((taxauthority2 != null) && (!taxauthority2.isEmpty())) {
			auth.add(taxauthority2);
			txble.add(customer.getHhCustomer_taxable2() + "");
			cusTxstts.add(customer.getHhCustomer_taxstts2());
		}

		if ((taxauthority3 != null) && (!taxauthority3.isEmpty())) {
			auth.add(taxauthority3);
			txble.add(customer.getHhCustomer_taxable3() + "");
			cusTxstts.add(customer.getHhCustomer_taxstts3());
		}

		if ((taxauthority4 != null) && (!taxauthority4.isEmpty())) {
			auth.add(taxauthority4);
			txble.add(customer.getHhCustomer_taxable4() + "");
			cusTxstts.add(customer.getHhCustomer_taxstts4());
		}

		if ((taxauthority5 != null) && (!taxauthority5.isEmpty())) {
			auth.add(taxauthority5);
			txble.add(customer.getHhCustomer_taxable5() + "");
			cusTxstts.add(customer.getHhCustomer_taxstts5());
		}

		boolean isTaxable = isTaxable(txble);

		if (isTaxable && (auth.size() != 0)) {
			taxables.add(auth);
			taxables.add(cusTxstts);

			List<List<String>> itemTaxables = dbHelper.getItemTaxstts(itemNum,
					auth, cmpnyNo);

			if (itemTaxables.size() != 0) {
				List<String> authItm = itemTaxables.get(0);
				List<String> itemTaxstts = itemTaxables.get(1);

				for (int i = 0; i < auth.size(); i++) {
					int index = authItm.indexOf(auth.get(i));

					itemTxstts.add(itemTaxstts.get(index));

				}

			} else {
				/*
				 * if data not available in Item Tax then by default five
				 * taxstts(sales tax class)value for five authority Modified
				 * Date: 7 Oct 2013 by TIVM
				 */
				itemTxstts.add("1");
				itemTxstts.add("1");
				itemTxstts.add("1");
				itemTxstts.add("1");
				itemTxstts.add("1");
			}

			taxables.add(itemTxstts);
		}

		return taxables;
	}

	// to check taxability
	public boolean isTaxable(List<String> txble) {

		boolean isTaxable = false;

		for (int i = 0; i < txble.size(); i++) {

			String table = txble.get(i);

			if (table.equals("0")) {
				isTaxable = true;
			}

		}
		return isTaxable;
	}

	private void alertUserToCancelTransaction() {
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Confirmation");
		alertUser.setIcon(R.drawable.warning);
		alertUser.setCancelable(false);
		alertUser.setMessage("Do you want to Cancel ?");
		alertUser.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						 //For Richard Qoh coming wrong
						supporter.setSaleType("");
						dbHelper.openWritableDatabase();
						dbHelper.deleteTempTableRecords();
						dbHelper.closeDatabase();
						supporter.clearSignPreference(signPreference);
						supporter
								.clearPreference(shippingDetails, entryDetails);
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

	private void scanItemProcess(String itemNum, HhItem01 item) {


		ItemEntry itemEntry = new ItemEntry();
				String etyp = "oe";

				if (mode.equals("ie") || mode.equals("invoiceEdit")) {

					String invEntType = entryDetails.getString("invEntTyp", "sale");

					if (invEntType.equals("sale")) {
						etyp = "ts";

					} else {
						etyp = "tr";
					}
				}

				double quantity = itemEntry.getActualHandOnQty(itemNum,
						item.getHhItem_qty_on_hand(), item.getHhItem_loc_id(),
						dbHelper, supporter);

				String tuom = item.getHhItem_loc_uom();
				dbHelper.openReadableDatabase();
				double uomConvFactor = dbHelper
						.getUOMConvFactor(itemNum, tuom, cmpnyNo);
				dbHelper.closeDatabase();

				int dQty = 1; // default qty is one for scanning
				double rQty = uomConvFactor * dQty;


				dbHelper.openReadableDatabase();
				boolean flag = dbHelper.checkItemExist(itemNum);
				dbHelper.closeDatabase();
				int existQty = 1;
				if (flag) {
					dbHelper.openReadableDatabase();
					List<TempItem> tempData = dbHelper.getTempItems(itemNum, item.getHhItem_loc_id());
					dbHelper.closeDatabase();
					existQty = Integer.parseInt(tempData.get(0).getTemp_qty());
				}


				double eQty = uomConvFactor * existQty;
				if (etyp.equals("tr") || (quantity > eQty)/* || (quantity < rQty)*/) {

					dbHelper.openReadableDatabase();
					String custNum = shippingDetails.getString("customerNumber", "");

					String custPriceList = supporter.getPriceList();

					HhPrice01 priceDetail = dbHelper.getPrcData(custPriceList, itemNum,
							cmpnyNo);

					double basePrc = 0.0;

					if (priceDetail != null) {

						basePrc = priceDetail.getHhPrice_price1(); // default price
					}

					// contract price calculation
					HhContractPrice01 contPrc = dbHelper.getContractPrc(custNum,
							itemNum, custPriceList, supporter.getCompanyCurrency(),
							cmpnyNo);
					dbHelper.closeDatabase();

					if ((contPrc != null) && (basePrc > 0.0)) {

						int prcType = contPrc.getHhContPrc_pricetype();
						double discValueByContPrc = 0.0;
						if (prcType == 1) { // for price type 1--get amount value
							discValueByContPrc = contPrc.getHhContPrc_discamount();

							basePrc = basePrc - discValueByContPrc;
						} else if (prcType == 2) { // for price type 2--get percent
							// value

							double percent = contPrc.getHhContPrc_discpercent();
							if (percent > 0.0) {
								basePrc = basePrc - ((basePrc * percent) / 100);
							}
						}
					} else {
						dbHelper.openReadableDatabase();
						String category = dbHelper.getItemCategory(itemNum, item.getHhItem_loc_id());
						HhContractPrice01 contPrice = dbHelper.getContractPrc(custNum, category);
						dbHelper.closeDatabase();

						if ((contPrice != null) && (basePrc > 0.0)) {

							int prcType = contPrice.getHhContPrc_pricetype();
							double discValueByContPrc = 0.0;
							if (prcType == 1) { // for price type 1--get amount value
								discValueByContPrc = contPrice.getHhContPrc_discamount();

								basePrc = basePrc - discValueByContPrc;
							} else if (prcType == 2) { // for price type 2--get percent value

								double percent = contPrice.getHhContPrc_discpercent();
								if (percent > 0.0) {
									basePrc = basePrc - ((basePrc * percent) / 100);
								}
							}


						}
					}
			/*String pricingUOM=priceDetail.getHhPrice_pricing_uom();
			dbHelper.openReadableDatabase();
			double existingUOMConFact=dbHelper.getUOMConvFactor(itemNum, pricingUOM, cmpnyNo);
			dbHelper.openReadableDatabase();*/
					dbHelper.openReadableDatabase();
					HhCompany company = new HhCompany();
					company = dbHelper.getCompanyData(cmpnyNo);
					int mIsStockingUOM = company.getCompany_isstockinguom();
					String mDefaultUOM = dbHelper.getItemUom(itemNum, cmpnyNo);
					double uomConFact = dbHelper.getUOMConvFactor(itemNum, mDefaultUOM, cmpnyNo);
					dbHelper.closeDatabase();

					double unitPrice = basePrc;

					if (mIsStockingUOM != 1) {
						unitPrice = (uomConFact * basePrc) / uomConvFactor;
					}

					if (unitPrice <= 0.0) {
						toastMsg.showToast(Transaction.this,
								"Price is not available for this item.");
					} else {


						dbHelper.openWritableDatabase();
						SimpleDateFormat df = supporter.getDateFormatter();
						String shipDate = shippingDetails.getString("shipDate",
								df.format(new Date()));
						TempItem tempItem = new TempItem();
						tempItem.setTemp_itemNum(itemNum);
						tempItem.setTemp_extPrice((unitPrice * dQty) + "");
						tempItem.setTemp_location(item.getHhItem_loc_id());
						tempItem.setTemp_uom(tuom);
						tempItem.setTemp_qty(dQty+ "");
						tempItem.setTemp_date(shipDate);
						tempItem.setTemp_entryType(etyp);
						tempItem.setTemp_discount("0.00");
						tempItem.setTemp_discType("A");

						boolean isItemExist = dbHelper.checkItemExist(itemNum);
						boolean isEdit = false;

						if (!isItemExist) { // if item not exist/ perform (
							// addTempItem
							// )

							dbHelper.addTempItem(tempItem);
							toastMsg.showToast(Transaction.this, itemNum
									+ " item addedd successfully");
						} else if (isItemExist && (!isEdit)) { // item exist, no
							// itm
							// avail with
							// current
							// uom) --
							// perform
							// (update to
							// add)

							dbHelper.updateTempItem(itemNum, tuom, tempItem, isEdit);
							toastMsg.showToast(Transaction.this, itemNum
									+ " item updated successfully");
						}

						transItemList.clear();
						adptTransItemList.clear();
						transItemList = dbHelper.getTempItems();
						dbHelper.closeDatabase();

						// to refresh list items in listview
						adptTransItemList = new TransItemListAdapter(Transaction.this,
								transItemList);
						lstTransItems.setAdapter(adptTransItemList);
						lstTransItems.setSelectionAfterHeaderView();


						calculateTransDetails(transItemList);
						edtTranScan.setText("");
						edtTranScan.requestFocus();

					}
				} else if ((quantity <= eQty) && (!etyp.equals("tr"))) {
					edtTranScan.setText("");
					toastMsg.showToast(Transaction.this,
							"Quantity on hand not available.");
				}
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			alertUserToCancelTransaction();

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
