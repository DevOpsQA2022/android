package com.mobilesalesperson.controller;

import android.os.Bundle;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author T.SARAVANAN class to Showing salesperson information...
 */

public class SalesPersonInfo extends AppBaseActivity {

	private Supporter supporter;
	private MspDBHelper dbhelpher;
	private TextView txtSalesManName;
	private TextView txtSalesManLocation;
	private TextView txtSalesManEmail;
	private TextView txtSalesManCurrency;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.sales_person_info_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		txtSalesManName = (TextView) findViewById(R.id.txt_SalesManName);
		txtSalesManLocation = (TextView) findViewById(R.id.txt_SalesManLocation);
		txtSalesManEmail = (TextView) findViewById(R.id.txt_SalesManEmail);
		txtSalesManCurrency = (TextView) findViewById(R.id.txt_SalesManCurrency);

		String spName = supporter.getSalesPerson();
		dbhelpher.openReadableDatabase();
		HhManager manager = dbhelpher.getManagerData(spName,
				supporter.getCompanyNo());
		dbhelpher.closeDatabase();

		txtSalesManName.setText(manager.getHhManager_username());
		txtSalesManLocation.setText(manager.getHhManager_locid());
		txtSalesManEmail.setText(manager.getHhManager_email());
		txtSalesManCurrency.setText(manager.getHhManager_currency());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}