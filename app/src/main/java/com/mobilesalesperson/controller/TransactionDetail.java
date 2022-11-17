package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import java.util.List;

/**
 * @author T.SARAVANAN class to showing transaction detail for particular order
 *         or invoice...
 */

public class TransactionDetail extends AppBaseActivity {

	/* variable declarations */
	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private List<HhTran01> transAllList;
	private TextView txttransDetailTitle;
	private TextView txtRefNoTitle;
	private TextView txtTransDetailRefNo;
	private TextView txtTransDetailNo;
	private TextView txttransDetailDate;
	private TextView txtTransDetailTerms;
	private TextView txttransDetailCusNo;
	private TextView txtShipInfo;
	private TextView txttransDetailSB;
	private TextView txtTransDetailTotal;
	private TextView txttransDetailTotalQty;
	private TextView txtTransDetailDiscAmt;
	private TextView txttransDetailPreTax;
	private TextView txttransDetailTax;
	private TextView txttransDetailNetAmt;
	private TextView txttransDetailPrepayment;
	private TextView txttransDetailStatus;
	private Spinner spntransDetailMailTo;
	private ListView lstTransDetailItems;
	private TextView lblPreTax;
	private ToastMessage toastMsg;

	private String refno;
	private String[] mail;
	private int status;
	private String transType;
	private int day;
	private int month;
	private int year;
	private ArrayAdapter<String> mailSpnAdapter;
	private ArrayAdapter<HhTran01> adptTransReport;
	private double netTotal;
	private double prepayAmt;
	private String mode;

	// currency format variables
	private String tranTot;
	private String taxValue;
	private String tranDiscount;
	private String preTaxValue;
	private String netTotValue;
	private String prePayValue;

	// hiding fields
	private TextView transDetailMailTo;
	private HhCustomer01 cust;
	private HhTran01 tran;
	private String strMsg;

	private String cmpnyNo; // added for multicompany
//</History> Suresh 05-Oct-2017 Added for Print model Select
private String printerModel;
	private HhSetting setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.transaction_detail_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		txttransDetailTitle = (TextView) findViewById(R.id.txt_transDetailTitle);
		txtRefNoTitle = (TextView) findViewById(R.id.txt_RefNoTitle);
		txtTransDetailRefNo = (TextView) findViewById(R.id.txt_TransDetailRefNo);
		txtTransDetailNo = (TextView) findViewById(R.id.txt_TransDetailNo);
		txttransDetailDate = (TextView) findViewById(R.id.txt_transDetailDate);
		txtTransDetailTerms = (TextView) findViewById(R.id.txt_TransDetailTerms);
		txttransDetailCusNo = (TextView) findViewById(R.id.txt_transDetailCusNo);
		txtShipInfo = (TextView) findViewById(R.id.txt_ShipInfo);
		txttransDetailSB = (TextView) findViewById(R.id.txt_transDetailSB);
		txtTransDetailTotal = (TextView) findViewById(R.id.txt_TransDetailTotal);
		txttransDetailTotalQty = (TextView) findViewById(R.id.txt_transDetailTotalQty);
		txtTransDetailDiscAmt = (TextView) findViewById(R.id.txt_TransDetailDiscAmt);
		txttransDetailPreTax = (TextView) findViewById(R.id.txt_transDetailPreTax);
		txttransDetailTax = (TextView) findViewById(R.id.txt_transDetailTax);
		txttransDetailNetAmt = (TextView) findViewById(R.id.txt_transDetailNetAmt);
		txttransDetailPrepayment = (TextView) findViewById(R.id.txt_transDetailPrepayment);
		txttransDetailStatus = (TextView) findViewById(R.id.txt_transDetailStatus);
		transDetailMailTo = (TextView) findViewById(R.id.txt_transDetailMailTo);
		spntransDetailMailTo = (Spinner) findViewById(R.id.spn_transDetailMailTo);
		lstTransDetailItems = (ListView) findViewById(R.id.lst_TransDetailItems);
		lblPreTax = (TextView) findViewById(R.id.lbl_transDetPreTax);
		lblPreTax.setVisibility(View.GONE);
		txttransDetailPreTax.setVisibility(View.GONE);
		transDetailMailTo.setVisibility(View.GONE);
		spntransDetailMailTo.setVisibility(View.GONE);

		mail = loadMailToData(mail);
		refno = getIntent().getStringExtra("referencenumber");
		mode = supporter.getMode();
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013

		dbhelpher.openReadableDatabase();
		transAllList = dbhelpher.getTransactionList(refno, cmpnyNo);
		dbhelpher.closeDatabase();
		//</History> Suresh 05-Oct-2017 Added for Print model Select
		dbhelpher.openReadableDatabase();
		setting = dbhelpher.getSettingData();
		dbhelpher.closeDatabase();
		//<History/> Suresh 05-Oct-2017 Added for Print model Select
		tran = transAllList.get(0);

		transType = tran.getHhTran_transType();
		if (transType.equals("S")) {
			txttransDetailTitle.setText("Order Detail");
			txtRefNoTitle.setText("Ord.Ref.");
			txtTransDetailNo.setText(tran.getHhTran_orderNumber());
		} else if (transType.equals("I") || transType.equals("CN")) {
			txttransDetailTitle.setText("Invoice Detail");
			txtRefNoTitle.setText("Inv.Ref.");
			txtTransDetailNo.setText(tran.getHhTran_invoiceNumber());
		} else {
			txttransDetailTitle.setText("Quote Detail");
			txtRefNoTitle.setText("Quote Ref.");
			txtTransDetailNo.setText(tran.getHhTran_orderNumber());
		}

		if (mode.equals("orderDelete")) {
			txttransDetailTitle.setText("Order Delete");
		} else if (mode.equals("invoiceDelete")) {
			txttransDetailTitle.setText("Invoice Delete");
		}

		// getting date from trans table
		day = tran.getHhTran_transDay();
		month = tran.getHhTran_transMonth();
		year = tran.getHhTran_transYear();

		txtTransDetailRefNo.setText(tran.getHhTran_referenceNumber());
		txttransDetailDate.setText(supporter.getStringDate(year, month, day));
		txtTransDetailTerms.setText(tran.getHhTran_terms());
		txttransDetailCusNo.setText(tran.getHhTran_customerNumber());
		txttransDetailSB.setText(tran.getHhTran_salesPerson());
		status = tran.getHhTran_status();
		if (status == 0) {
			txttransDetailStatus.setText("Not Sent");
			txttransDetailStatus.setTextColor(Color.RED);
		} else {
			txttransDetailStatus.setText("Sent");
			txttransDetailStatus.setTextColor(Color.GREEN);
		}

		mailSpnAdapter = new ArrayAdapter<String>(TransactionDetail.this,
				android.R.layout.simple_dropdown_item_1line, mail);
		// mailSpnAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spntransDetailMailTo.setAdapter(mailSpnAdapter);

		adptTransReport = new TransDetailReportAdapter(TransactionDetail.this,
				transAllList);
		lstTransDetailItems.setAdapter(adptTransReport);

		// calculation of listview items price & tax details...
		calculateTransDetails(transAllList);

		dbhelpher.openReadableDatabase();
		cust = dbhelpher.getCustomerData(txttransDetailCusNo.getText()
				.toString(), cmpnyNo);
		dbhelpher.closeDatabase();

		strMsg = cust.getHhCustomer_name() + ", "
				+ cust.getHhCustomer_address() + ",\n"
				+ cust.getHhCustomer_city() + ", " + cust.getHhCustomer_zip()
				+ ",\n" + cust.getHhCustomer_country();
		txttransDetailCusNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showInfoDlg("Customer Info", strMsg);
			}

		});

		txtShipInfo.setOnClickListener(new OnClickListener() {

			String msg = strMsg + "\n\n" + "Ship Via : "
					+ tran.getHhTran_shipViaCode();

			@Override
			public void onClick(View v) {
				showInfoDlg("Shipping Info", msg);
			}
		});

	}

	protected void showInfoDlg(String strTitle, String strMsg) {

		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				TransactionDetail.this);
		alertDialog.setTitle(strTitle);
		alertDialog.setIcon(R.drawable.tick);
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		alertDialog.setMessage(strMsg);
		////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	private String[] loadMailToData(String[] mail) {
		String[] mail1 = new String[2];
		mail1[0] = "Company";
		mail1[1] = "Sales Person";
		return mail1;
	}

	/** method for menu control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.trans_detail_menu, menu);

		if (mode.equals("orderDelete")) {
			menu.getItem(1).setVisible(true);
		} else if (mode.equals("invoiceDelete")) {
			menu.getItem(1).setVisible(true);
		} else if (mode.equals("report") || mode.equals("ie")
				|| mode.equals("oe") || mode.equals("orderEdit")
				|| mode.equals("invoiceEdit")) {
			menu.getItem(1).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
	}

	/** method for menu control */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.trans_detail_mnu_print:
			//</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
							Intent intent = new Intent(TransactionDetail.this, TransPDFCreate.class);
                        intent.putExtra("refno", refno);
                        startActivity(intent);
						}
						else{

			// dbhelpher.openReadableDatabase();
			List<Devices> deviceList = supporter.getPairedDevices();
			// dbhelpher.closeDatabase();

			if (deviceList.size() != 0) {
				final TransDetailDialog dialog1 = new TransDetailDialog(
						TransactionDetail.this, refno, dbhelpher, supporter,
						deviceList);
				dialog1.setContentView(R.layout.print_list_layout);
				dialog1.show();
			} else {
				toastMsg.showToast(TransactionDetail.this,
						"Paired printer not available for printing");
			}
			break;

						}

		case R.id.trans_detail_delete:

			transactionDelete();

			break;

		case R.id.trans_detail_mnu_sign:
			showSignature();
			break;

		}
		return true;
	}

	private void showSignature() {
		dbhelpher.openReadableDatabase();
		byte[] signByte = dbhelpher.getSignature(txtTransDetailRefNo.getText()
				.toString(), cmpnyNo);
		System.out.println(txtTransDetailRefNo.getText().toString());
		dbhelpher.closeDatabase();

		if (signByte != null) {
			Bitmap mBitmap = BitmapFactory.decodeByteArray(signByte, 0,
					signByte.length);
			// custom dialog
			final Dialog dialog = new Dialog(TransactionDetail.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.sign_view_layout);

			// set the custom dialog components - text, image and button
			ImageView image = (ImageView) dialog.findViewById(R.id.signView);
			image.setImageBitmap(mBitmap);

			dialog.show();

		} else {
			toastMsg.showToast(TransactionDetail.this,
					"Signature not available.");
		}
	}

	private void transactionDelete() {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				TransactionDetail.this);
		alertDialog.setTitle("Confirm Delete");
		alertDialog.setIcon(R.drawable.dlg_delete);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbhelpher.openWritableDatabase();
						Log.i("Writable DB Open", "Writable Database Opened.");
						dbhelpher.mBeginTransaction();
						Log.i("Transaction started",
								"Transaction successfully started for Transaction detail page.");

						updateItemsBeforeDelete(refno); // item will be
														// updated only if
														// invoice deleted.

						dbhelpher.deleteTransaction(refno, cmpnyNo);
						dbhelpher.deletePrepayment(refno, cmpnyNo);
						dbhelpher.deleteSignature(refno, cmpnyNo);

						dbhelpher.mSetTransactionSuccess(); // setting the
															// transaction
															// successfull.
						Log.i("Transaction success", "Transaction success.");
						dbhelpher.mEndTransaction();
						Log.i("Transaction success", "Transaction end.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");

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
	}

	protected void updateItemsBeforeDelete(String refNum) {
		if (mode.equals("invoiceDelete")) {

			List<HhTran01> tranListToUpdateItem = dbhelpher
					.getTranDetForItemUpdate(refNum, cmpnyNo);

			double qanty = 0.0;
			String uom = "";
			double conFact = 0.0;
			double rQty = 0.0;

			for (int i = 0; i < tranListToUpdateItem.size(); i++) {

				HhTran01 tranItem = tranListToUpdateItem.get(i);
				String tranType = tranItem.getHhTran_transType();
				qanty = (double) tranItem.getHhTran_qty();
				uom = tranItem.getHhTran_uom();

				conFact = dbhelpher.getUOMConvFactor(
						tranItem.getHhTran_itemNumber(), uom, cmpnyNo);

				rQty = qanty * conFact;
				if (tranType.equals("I")) {

					dbhelpher.updateItemOnHand(tranItem.getHhTran_itemNumber(),
							"add", rQty, tranItem.getHhTran_locId() + "",
							cmpnyNo);

				} else {

					dbhelpher.updateItemOnHand(tranItem.getHhTran_itemNumber(),
							"remove", rQty, tranItem.getHhTran_locId() + "",
							cmpnyNo);

				}
			}
		}

	}

	// to calculate resultant transaction values
	private void calculateTransDetails(List<HhTran01> transItemList2) {

		double transTotal = 0.00;
		int transQty = 0;
		double tax = 0.00;
		double preTax = 0.00;
		double netTot = 0.00;
		double discount = 0.00;

		for (int i = 0; i < transItemList2.size(); i++) {
			HhTran01 item = transItemList2.get(i);

			String type = item.getHhTran_transType();
			double cDiscount = item.getHhTran_discValue();
			if (type.equals("CN")) {

				int cQty = item.getHhTran_qty();
				transQty = transQty - cQty;

				double cTot = item.getHhTran_price() * cQty;
				transTotal = transTotal - cTot;

				discount = discount - cDiscount;

				double cTax = item.getHhTran_tax();
				tax = tax - cTax;

			} else {
				int cQty = item.getHhTran_qty();
				transQty = transQty + cQty;

				double cTot = item.getHhTran_price() * cQty;
				transTotal = transTotal + cTot;

				discount = discount + cDiscount;

				double cTax = item.getHhTran_tax();
				tax = tax + cTax;
			}

		}

		tranTot = supporter.getCurrencyFormat(transTotal);
		tranDiscount = supporter.getCurrencyFormat(discount);
		taxValue = supporter.getCurrencyFormat(tax);
		preTaxValue = supporter.getCurrencyFormat(preTax);

		netTot = transTotal + tax + preTax - discount;
		netTotal = netTot;
		netTotValue = supporter.getCurrencyFormat(netTotal);

		txtTransDetailTotal.setText(tranTot);
		txttransDetailTotalQty.setText(transQty + "");
		txtTransDetailDiscAmt.setText(tranDiscount);
		txttransDetailTax.setText(taxValue);
		txttransDetailPreTax.setText(preTaxValue);
		txttransDetailNetAmt.setText(netTotValue);

		dbhelpher.openReadableDatabase();
		prepayAmt = dbhelpher.getPrepayAmount(refno, cmpnyNo);
		dbhelpher.closeDatabase();
		prePayValue = supporter.getCurrencyFormat(prepayAmt);
		txttransDetailPrepayment.setText(prePayValue);
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			String mode = supporter.getMode();
			if (mode.equals("ie") || mode.equals("oe")
					|| mode.equals("orderEdit") || mode.equals("invoiceEdit")) {
				supporter.simpleNavigateTo(MainMenu.class);
			} else {
				supporter.simpleNavigateTo(OrderSelection.class);
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
