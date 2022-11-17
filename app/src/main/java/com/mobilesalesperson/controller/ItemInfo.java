package com.mobilesalesperson.controller;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author TIVM class to show item information
 */
public class ItemInfo extends AppBaseActivity {

	/** variable declarations */
	private MspDBHelper dbHelper;
	private Supporter supporter;
	private TextView txtItemNumber;
	private TextView txtItemDesc;
	private Spinner spnItemPriceListSpinner;
	private TextView txtItemPrc;
	private TextView txtItemCurrency;
	private TextView txtItemLocUOM;
	private TextView txtItemQtyNoHand;
	private TextView txtItemCostingMethod;
	private TextView txtItemAltItem;
	private TextView txtItemAltItemDesc;
	private TextView txtItemLocId;
	private List<String> prcListCode;
	private ArrayAdapter<String> itmPrcLstSpinnerAdapter;
	private String itemNo;
	private String locId;
	private String cmpnyNo; // added for multicompany

	/**
	 * method to initialize the class activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.item_info_layout);

		registerBaseActivityReceiver();

		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);

		txtItemNumber = (TextView) findViewById(R.id.txt_ItemNumber);
		txtItemDesc = (TextView) findViewById(R.id.txt_ItemDesc);
		spnItemPriceListSpinner = (Spinner) findViewById(R.id.txt_ItemPriceListSpinner);
		txtItemPrc = (TextView) findViewById(R.id.txt_ItemPrice);
		txtItemCurrency = (TextView) findViewById(R.id.txt_ItemCurrency);
		txtItemLocUOM = (TextView) findViewById(R.id.txt_ItemUOM);
		txtItemQtyNoHand = (TextView) findViewById(R.id.txt_ItemOnHand);
		txtItemCostingMethod = (TextView) findViewById(R.id.txt_ItemCostMethod);
		txtItemAltItem = (TextView) findViewById(R.id.txt_ItemAltId);
		txtItemAltItemDesc = (TextView) findViewById(R.id.txt_ItemAltDesc);
		txtItemLocId = (TextView) findViewById(R.id.txt_ItemLoc);

		itemNo = getIntent().getStringExtra("itemNumber");

		locId = getIntent().getStringExtra("locId");
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013

		// to load item price list code spinner
		dbHelper.openReadableDatabase();
		prcListCode = dbHelper.getPriceListFromPriceTable(itemNo, cmpnyNo);
		dbHelper.closeDatabase();
		itmPrcLstSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, prcListCode);
		// itmPrcLstSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnItemPriceListSpinner.setAdapter(itmPrcLstSpinnerAdapter);

		// to get current price list code
		String currentPriceListCode = (String) spnItemPriceListSpinner
				.getItemAtPosition(spnItemPriceListSpinner
						.getSelectedItemPosition());

		// to set price for current price list code
		dbHelper.openReadableDatabase();
		double currentPrice = dbHelper.getCurrentPrice(locId,
				currentPriceListCode, itemNo, cmpnyNo);
		dbHelper.closeDatabase();
		String itmPrz = supporter.getCurrencyFormat(currentPrice);

		// to load item details
		dbHelper.openReadableDatabase();
		HhItem01 item = dbHelper.getItemData(itemNo, locId, cmpnyNo);
		dbHelper.closeDatabase();

		txtItemNumber.setText(item.getHhItem_number());
		txtItemDesc.setText(item.getHhItem_description());
		txtItemPrc.setText(itmPrz);
		txtItemCurrency.setText(item.getHhItem_currency());
		txtItemLocUOM.setText(item.getHhItem_loc_uom());
		txtItemQtyNoHand.setText(item.getHhItem_qty_on_hand() + "");
		txtItemCostingMethod.setText(item.getHhItem_costing_method());
		txtItemAltItem.setText(item.getHhItem_alt_itm());
		txtItemAltItemDesc.setText(item.getHhItem_alt_itm_desc());
		txtItemLocId.setText(item.getHhItem_loc_id());

		// Item spinner item click function
		spnItemPriceListSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						// to get current price list code
						String currentPriceListCode = (String) spnItemPriceListSpinner
								.getItemAtPosition(spnItemPriceListSpinner
										.getSelectedItemPosition());

						// to set price for current price list code
						dbHelper.openReadableDatabase();
						double currentPrice = dbHelper.getCurrentPrice(locId,
								currentPriceListCode, itemNo, cmpnyNo);
						dbHelper.closeDatabase();
						String itmPrz = supporter
								.getCurrencyFormat(currentPrice);
						txtItemPrc.setText(itmPrz);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});


	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
