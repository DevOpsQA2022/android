package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.Manf_Number01;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author TIVM class to display list of items
 */
public class ItemSelection extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private List<HhItem01> itemList;
	private ListView lstViewItem;
	private ArrayAdapter<HhItem01> itemAdapter;
	private EditText edtTxtItemSearch;
	private String mode;
	private String locSpnData;
	private Spinner spnLocation;
	private List<String> locList;
	private ArrayAdapter<String> adptLoctionSpn;
	private boolean spnControl;
	private List<Manf_Number01> mManfList;
	StringBuilder mBuilder;
	StringBuffer mBuff;
	private String cmpnyNo; // added for multicompany
    private String ordNo;
	private TempItem temp;
	// private int allowNonStockItem; // added for nonstock item 19July2013
	// private HhSetting setting;
	private int stockItem = 1;

	/**
	 * method to initialize the class activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.item_selection_layout);

		registerBaseActivityReceiver();

		
		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		ordNo=getIntent().getStringExtra("orderNum");

		mode = supporter.getMode();
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013

		spnLocation = (Spinner) findViewById(R.id.spn_Location);
		dbHelper.openReadableDatabase();
		locList = dbHelper.getAvailableLocation(cmpnyNo);
		dbHelper.closeDatabase();
		adptLoctionSpn = new ArrayAdapter<String>(ItemSelection.this,
				android.R.layout.simple_dropdown_item_1line, locList);
		// adptLoctionSpn.setDropDownViewResource(R.layout.spinner_item_layout);
		spnLocation.setAdapter(adptLoctionSpn);

		edtTxtItemSearch = (EditText) findViewById(R.id.edtTxt_ItemSearch);

		locSpnData = (String) spnLocation.getItemAtPosition(spnLocation
				.getSelectedItemPosition());

		dbHelper.openReadableDatabase();
		itemList = dbHelper.getItemDataList(locSpnData, cmpnyNo);
		dbHelper.closeDatabase();

		/*
		 * The below code used for Allow Non-Stock Item based on Settings.
		 * 
		 * allowNonStockItem = setting.getHhSetting_nonstockitem();
		 * 
		 * dbHelper.openReadableDatabase(); if (allowNonStockItem == 1) {
		 * itemList = dbHelper.getItemDataList(locSpnData, cmpnyNo); } else {
		 * itemList = dbHelper.getItemDataList(locSpnData, cmpnyNo, stockItem);
		 * } dbHelper.closeDatabase();
		 */

		lstViewItem = (ListView) findViewById(R.id.lst_Item);
		itemAdapter = new ItemSelectionAdapter(ItemSelection.this, itemList);
		lstViewItem.setAdapter(itemAdapter);
		
		

		spnControl = false; // to stop executing spinner on load

		spnLocation.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				if (spnControl) {
					locSpnData = (String) spnLocation
							.getItemAtPosition(spnLocation
									.getSelectedItemPosition());
					edtTxtItemSearch.setText("");
					ItemSelection.this.itemAdapter.getFilter().filter(""); // to
																			// set
																			// no
																			// filter
																			// value
																			// to
																			// load
																			// data
					itemList.clear();
					itemAdapter.clear();
					dbHelper.openReadableDatabase();
					itemList = dbHelper.getItemDataList(locSpnData, cmpnyNo);
					dbHelper.closeDatabase();

					// to refresh list items in listview
					itemAdapter = new ItemSelectionAdapter(ItemSelection.this,
							itemList);
					lstViewItem.setAdapter(itemAdapter);
					lstViewItem.setSelectionAfterHeaderView();
				}

				spnControl = true;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		/**
		 * Enabling Search Filter
		 * */
		edtTxtItemSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
			
				ItemSelection.this.itemAdapter.getFilter().filter(cs);
					
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
				
				/*ArrayList<String> sList = new ArrayList<String>();*/
				
				
				/*for (int i = 0; i < sList.size(); i++) {
					String itemNo=m.getManuf_itemno().toString();
					mBuilder=new StringBuilder();
					
					ItemSelection.this.itemAdapter.getFilter().filter(sList.get(i).toString());
					System.out.println("ITEM NO: " + m.getManuf_itemno().toString());
				}*/
				
			}
			
		});
		
		edtTxtItemSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String itemSearch=edtTxtItemSearch.getText().toString();
				dbHelper.openReadableDatabase();
				mManfList=dbHelper.showUPC(itemSearch.toUpperCase());
				Manf_Number01 m = new Manf_Number01();
				
				for (int i = 0; i < mManfList.size(); i++) {
					m = mManfList.get(i);
					ItemSelection.this.itemAdapter.getFilter().filter(m.getManuf_itemno().toString());
					System.out.println("ITEM NO: " + m.getManuf_itemno().toString());
				}
			}
		});
		

		// Item list item click function
		lstViewItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HhItem01 customer = (HhItem01) itemAdapter.getItem(position);

				String itemNumber = customer.getHhItem_number();

				if (mode.equals("ie") || mode.equals("oe")
						|| mode.equals("orderEdit")
						|| mode.equals("invoiceEdit")) {
					navigationProcess(itemNumber, locSpnData, ItemEntry.class);
				} else if (mode.equals("view")) {
					navigationProcess(itemNumber, locSpnData, ItemInfo.class);
				}

			}
		});

	}

	protected void navigationProcess(String itemNumber, String locId,
			Class<?> cls) {

		Intent intent = new Intent(ItemSelection.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("itemNumber", itemNumber);
		intent.putExtra("locId", locId);
		intent.putExtra("isEdit", false);
		intent.putExtra("orderNumber", ordNo);
		startActivity(intent);
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String mode = supporter.getMode();

			if (mode.equals("view")) {

				supporter.simpleNavigateTo(MainMenu.class);

			} else {
				dbHelper.openWritableDatabase();
				boolean isTempDataExist = dbHelper.checkTempDataExist();
				dbHelper.closeDatabase();

				if (!isTempDataExist) {
					// alert user
					final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							ItemSelection.this);
					alertDialog.setTitle("Confirmation");
					alertDialog.setIcon(R.drawable.warning);
					alertDialog.setCancelable(false);
					alertDialog.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dbHelper.openWritableDatabase();
									//dbHelper.deleteTempTableRecords();
									dbHelper.closeDatabase();
									supporter
											.clearSignPreference(getSharedPreferences(
													"signPref",
													Context.MODE_PRIVATE));
									supporter.clearPreference(
											getSharedPreferences(
													"shippingDetails",
													Context.MODE_PRIVATE),
											getSharedPreferences(
													"entryDetails",
													Context.MODE_PRIVATE));
									supporter.simpleNavigateTo(MainMenu.class);
								}
							});

					alertDialog.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					alertDialog
							.setMessage("Do you want to Cancel ?");
					//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

				} else {
					supporter.simpleNavigateTo(Transaction.class);
				}

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
