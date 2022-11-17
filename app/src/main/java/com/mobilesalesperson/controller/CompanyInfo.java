package com.mobilesalesperson.controller;

import android.os.Bundle;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author T.SARAVANAN class to kill all activities
 */

public class CompanyInfo extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbhelpher;
	private TextView txtCompanyName;
	private TextView txtCompanyAddress;
	private TextView txtCompanyCity;
	private TextView txtCompanyState;
	private TextView txtCompanyZip;
	private TextView txtCompanyCountry;
	private TextView txtCompanyContact;
	private TextView txtCompanyPhone;

	private String cmpNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.company_info_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		txtCompanyName = (TextView) findViewById(R.id.txt_CompanyName);
		txtCompanyAddress = (TextView) findViewById(R.id.txt_CompanyAddress);
		txtCompanyCity = (TextView) findViewById(R.id.txt_CompanyCity);
		txtCompanyState = (TextView) findViewById(R.id.txt_CompanyState);
		txtCompanyZip = (TextView) findViewById(R.id.txt_CompanyZip);
		txtCompanyCountry = (TextView) findViewById(R.id.txt_CompanyCountry);
		txtCompanyContact = (TextView) findViewById(R.id.txt_CompanyContact);
		txtCompanyPhone = (TextView) findViewById(R.id.txt_CompanyPhone);

		cmpNo = supporter.getCompanyNo();
		dbhelpher.openReadableDatabase();
		HhCompany company = dbhelpher.getCompanyData(cmpNo);
		dbhelpher.closeDatabase();

		txtCompanyName.setText(company.getCompany_name());
		txtCompanyAddress.setText(company.getCompany_address());
		txtCompanyCity.setText(company.getCompany_city());
		txtCompanyState.setText(company.getCompany_state());
		txtCompanyZip.setText(company.getCompany_zip());
		txtCompanyCountry.setText(company.getCompany_country());
		txtCompanyContact.setText(company.getCompany_contact());
		txtCompanyPhone.setText(company.getCompany_phone());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
