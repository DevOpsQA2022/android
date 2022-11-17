package com.mobilesalesperson.controller;

import android.os.Bundle;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author TIVM class to show customer information
 */
public class CustomerInfo extends AppBaseActivity {

	/** variable declarations */
	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private TextView txtCustomerNumber;
	private TextView txtCustomerName;
	private TextView txtCustomerAddress;
	private TextView txtCustomerCity;
	private TextView txtCustomerState;
	private TextView txtCustomerZip;
	private TextView txtCustomerCountry;
	private TextView txtCustomerPhone;
	private TextView txtCustomerEmail;
	private TextView txtCustomerWebsite;

	/**
	 * method to initialize the class activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.customer_info_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		txtCustomerNumber = (TextView) findViewById(R.id.txt_CustomerNumber);
		txtCustomerName = (TextView) findViewById(R.id.txt_CustomerName);
		txtCustomerAddress = (TextView) findViewById(R.id.txt_CustomerAddress);
		txtCustomerCity = (TextView) findViewById(R.id.txt_CustomerCity);
		txtCustomerState = (TextView) findViewById(R.id.txt_CustomerState);
		txtCustomerZip = (TextView) findViewById(R.id.txt_CustomerZip);
		txtCustomerCountry = (TextView) findViewById(R.id.txt_CustomerCountry);
		txtCustomerPhone = (TextView) findViewById(R.id.txt_CustomerPhone);
		txtCustomerEmail = (TextView) findViewById(R.id.txt_CustomerEmail);
		txtCustomerWebsite = (TextView) findViewById(R.id.txt_CustomerWebsite);

		String custNo = getIntent().getStringExtra("customerNumber");

		dbhelpher.openReadableDatabase();
		HhCustomer01 customer = dbhelpher.getCustomerData(custNo,
				supporter.getCompanyNo());
		dbhelpher.closeDatabase();

		txtCustomerNumber.setText(customer.getHhCustomer_number());
		txtCustomerName.setText(customer.getHhCustomer_name());
		txtCustomerAddress.setText(customer.getHhCustomer_address());
		txtCustomerCity.setText(customer.getHhCustomer_city());
		txtCustomerState.setText(customer.getHhCustomer_state());
		txtCustomerZip.setText(customer.getHhCustomer_zip());
		txtCustomerCountry.setText(customer.getHhCustomer_country());
		txtCustomerPhone.setText(customer.getHhCustomer_phone1());
		txtCustomerEmail.setText(customer.getHhCustomer_email2());
		txtCustomerWebsite.setText(customer.getHhCustomer_website());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
