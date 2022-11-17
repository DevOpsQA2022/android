package com.mobilesalesperson.controller;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.util.ThemeUtil;

public class ImportSummary extends AppBaseActivity {

	private MspDBHelper dbHelper;
	private ImageView imgOEview;
	private ImageView imgReciptview;
	private ListView lstSumReportView;

	private SummaryResultAdapter resultAdapter;
	private ArrayList<SummaryResult> mylist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.import_summary_layout);
		registerBaseActivityReceiver();

		// Prepare ListView in dialog

		imgOEview = (ImageView) findViewById(R.id.imgSummaryOE);
		imgReciptview = (ImageView) findViewById(R.id.imgSummaryRecipt);
		lstSumReportView = (ListView) findViewById(R.id.lst_ImportSummary);

		dbHelper = new MspDBHelper(this);
		mylist = new ArrayList<SummaryResult>();

		dbHelper.openReadableDatabase();
		boolean tempOEStatus = dbHelper.isDataAvailableForOE();
		boolean tempReciptStaus = dbHelper.isDataAvailableForRecipt();
		dbHelper.closeDatabase();

		if (!tempOEStatus) {
			imgOEview.setImageResource(R.drawable.red_ball);
		} else {
			imgOEview.setImageResource(R.drawable.green_ball);
		}

		if (!tempReciptStaus) {
			imgReciptview.setImageResource(R.drawable.red_ball);
		} else {
			imgReciptview.setImageResource(R.drawable.green_ball);
		}

		// Load Order and Invoice Entry Tables Defaults...
		loadOETables();

		// We declare the adapter using the other class
		resultAdapter = new SummaryResultAdapter(this,
				R.layout.listview_item_row, mylist);
		// We set the adapter, here is the "place" where we populate the List.
		lstSumReportView.setAdapter(resultAdapter);

		imgOEview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mylist.clear();
				resultAdapter.clear();
				loadOETables();
				resultAdapter = new SummaryResultAdapter(ImportSummary.this,
						R.layout.listview_item_row, mylist);
				lstSumReportView.setAdapter(resultAdapter);
				lstSumReportView.setSelectionAfterHeaderView();
			}
		});

		imgReciptview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mylist.clear();
				resultAdapter.clear();
				loadReciptTable();
				resultAdapter = new SummaryResultAdapter(ImportSummary.this,
						R.layout.listview_item_row, mylist);
				lstSumReportView.setAdapter(resultAdapter);
				lstSumReportView.setSelectionAfterHeaderView();
			}
		});

	}

	private void loadOETables() {
		dbHelper.openReadableDatabase();
		mylist.add(new SummaryResult("Conversion Factor", dbHelper
				.isConvFactAvail()));
		mylist.add(new SummaryResult("Customer", dbHelper.isCustAvail()));
		mylist.add(new SummaryResult("Item", dbHelper.isItemDataAvail()));
		mylist.add(new SummaryResult("Price Master", dbHelper
				.isPrcMasterAvail()));
		mylist.add(new SummaryResult("Price", dbHelper.isPriceAvail()));
		mylist.add(new SummaryResult("MSP_DB", dbHelper.isMspdbAvail()));
		mylist.add(new SummaryResult("UPC", dbHelper.isUPCDataAvail()));
		dbHelper.closeDatabase();
	}

	private void loadReciptTable() {
		dbHelper.openReadableDatabase();
		mylist.add(new SummaryResult("Receipt Type", dbHelper
				.isReciptTypeAvail()));
		mylist.add(new SummaryResult("Payment", dbHelper.isPaymentAvail()));
		dbHelper.closeDatabase();
	}

	public class SummaryResult {
		public String name;
		public boolean result;

		public SummaryResult() {
			super();
		}

		public SummaryResult(String name, boolean result) {
			super();
			this.name = name;
			this.result = result;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
