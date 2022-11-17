package com.mobilesalesperson.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import java.util.List;

public class ReceiptDetail extends AppBaseActivity {

	/* variable declarations */
	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private ToastMessage toastMsg;
	private List<HhReceipt01> receiptAllList;

	private TextView txtReciptDetailCusNo;
	private TextView txtReciptDetailCusName;
	private TextView txtReciptDetailPaymentMode;
	private TextView txtReciptDetailPaymentDate;
	private TextView txtReciptDetailChkNo;
	private TextView txtReciptDetailAmount;
	private TextView txtReciptDetailCustAmt;
	private TextView txtReciptDetailCustUnApplyAmt;
	private ListView lstReciptDetailItems;

	private int day;
	private int month;
	private int year;
	private String date;
	private String reciptNumbr;
	private ArrayAdapter<HhReceipt01> adptReciptReport;
	private String cmpnyNo; // added for multicompany
//</History> Suresh 05-Oct-2017 Added for Print model Select
private String printerModel;
	private HhSetting setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.receipt_detail_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		txtReciptDetailCusNo = (TextView) findViewById(R.id.txt_reciptDetailCusNo);
		txtReciptDetailCusName = (TextView) findViewById(R.id.txt_reciptDetailCusName);
		txtReciptDetailPaymentMode = (TextView) findViewById(R.id.txt_reciptDetailPaymentCode);
		txtReciptDetailPaymentDate = (TextView) findViewById(R.id.txt_reciptDetailPaymentDate);
		txtReciptDetailChkNo = (TextView) findViewById(R.id.txt_reciptDetailchkReciptNo);
		txtReciptDetailAmount = (TextView) findViewById(R.id.txt_reciptDetailAmt);
		txtReciptDetailCustAmt = (TextView) findViewById(R.id.txt_reciptDetailCustAmt);
		txtReciptDetailCustUnApplyAmt = (TextView) findViewById(R.id.txt_reciptDetailUnApplied);
		lstReciptDetailItems = (ListView) findViewById(R.id.lst_reciptDetailItems);

		reciptNumbr = getIntent().getStringExtra("receiptnumber");
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013

		dbhelpher.openReadableDatabase();
		receiptAllList = dbhelpher.getReceiptListofData(reciptNumbr, cmpnyNo);
		dbhelpher.closeDatabase();
//</History> Suresh 05-Oct-2017 Added for Print model Select
		dbhelpher.openReadableDatabase();
		setting = dbhelpher.getSettingData();
		dbhelpher.closeDatabase();

		HhReceipt01 recipt = receiptAllList.get(0);
		txtReciptDetailCusNo.setText(recipt.getHhReceipt_customernumber());
		txtReciptDetailCusName.setText(recipt.getHhReceipt_customername());
		txtReciptDetailPaymentMode.setText(recipt.getHhReceipt_receipttype());
		txtReciptDetailChkNo.setText(recipt.getHhReceipt_receiptnumber());
		txtReciptDetailAmount.setText("" + recipt.getHhReceipt_amount());
		txtReciptDetailCustUnApplyAmt.setText(""
				+ recipt.getHhReceipt_receiptunapplied());

		// getting date from receipt table
		day = recipt.getHhReceipt_receiptday();
		month = recipt.getHhReceipt_receiptmonth();
		year = recipt.getHhReceipt_receiptyear();

		date = supporter.getStringDate(year, month, day);
		txtReciptDetailPaymentDate.setText(date);

		adptReciptReport = new ReceiptDetailReportAdapter(ReceiptDetail.this,
				receiptAllList);
		lstReciptDetailItems.setAdapter(adptReciptReport);

	}// end of onCreate

	/** method for menu control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.receipt_summary_menu, menu);
		return true;
	}

	/** method for menu control */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.recipt_summary_print:
//</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
						Intent intent = new Intent(ReceiptDetail.this, ReceiptPDFCreate.class);
                                            intent.putExtra("reciptNo", reciptNumbr);
                                            startActivity(intent);
						}
						else {
							List<Devices> deviceList = supporter.getPairedDevices();

							if (deviceList.size() != 0) {
								final ReceiptDetailDialog dialog1 = new ReceiptDetailDialog(
										ReceiptDetail.this, reciptNumbr, dbhelpher, supporter,
										deviceList);
								dialog1.setContentView(R.layout.print_list_layout);
								dialog1.show();
							} else {
								toastMsg.showToast(ReceiptDetail.this,
										"Paired printer not available for printing");
							}
						}
			break;
		}
		return true;
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			String mode = supporter.getMode();
			if (mode.equals("recipt")) {

				supporter.simpleNavigateTo(MainMenu.class);
			} else {
				supporter.simpleNavigateTo(ReceiptSelection.class);
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
