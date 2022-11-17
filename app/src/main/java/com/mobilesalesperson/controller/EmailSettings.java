package com.mobilesalesperson.controller;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhEmailSetting;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EmailSettings extends AppBaseActivity {

	private Button btnEmailSave;
	private EditText edtSalespersonEmail;
	private EditText edtSalespersonPwd;
	private EditText edtCompanyEmail;
	private EditText edtSalespersonHostName;
	private EditText edtSalespersonPortNo;
	private EditText edtCompanyPortNo;
	private ToastMessage toastMsg;

	private MspDBHelper helper;
	private boolean isLogedIn;
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private HhEmailSetting e_Setting;

	String salesPersonEmail, salespersonPwd, companyEmail, salespersonHostName,
			companyPortNo;
	int salespersonPortNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.email_settings_layout);
		registerBaseActivityReceiver();

		helper = new MspDBHelper(this);
		supporter = new Supporter(this, helper);
		dbHelper = new MspDBHelper(this);

		btnEmailSave = (Button) findViewById(R.id.btn_emailSettingSave);
		edtSalespersonEmail = (EditText) findViewById(R.id.edt_emailSettingSalespersonEmail);
		edtSalespersonPwd = (EditText) findViewById(R.id.edt_emailSettingSalespersonPassword);
		edtCompanyEmail = (EditText) findViewById(R.id.edt_emailSettingCompanyEmail);
		edtSalespersonHostName = (EditText) findViewById(R.id.edt_emailSettingSalespersonHostName);
		edtSalespersonPortNo = (EditText) findViewById(R.id.edt_emailSettingSalespersonPortNo);
		edtCompanyPortNo = (EditText) findViewById(R.id.edt_emailSettingCompanyPortNo);

		 /* edtSalespersonEmail.setText("pranesh@ciglobalsolutions.com");
		  edtSalespersonPwd.setText("Passw0rd");
		  edtCompanyEmail.setText("vikas@ciglobalsolutions.com");
		  edtSalespersonHostName.setText("box803.bluehost.com");
		  edtSalespersonPortNo.setText("993"); edtCompanyPortNo.setText("465");*/
		 
		
		toastMsg = new ToastMessage();
		e_Setting = dbHelper.getEmail();

		if (e_Setting != null) {
			salesPersonEmail = e_Setting.getHhEmailSetting_salespersonemail();
			salespersonPwd = e_Setting.getHhEmailSetting_salespersonpwd();
			companyEmail = e_Setting.getHhEmailSetting_companyemail();
			salespersonHostName = e_Setting
					.getHhEmailSetting_salespersonhostname();
			salespersonPortNo = e_Setting.getHhEmailSetting_salespersonportno();
			companyPortNo = e_Setting.getHhEmailSetting_companyportno();

			edtSalespersonEmail.setText(salesPersonEmail);
			edtSalespersonPwd.setText(salespersonPwd);
			edtCompanyEmail.setText(companyEmail);
			edtSalespersonHostName.setText(salespersonHostName);
			edtSalespersonPortNo.setText("" + salespersonPortNo);
			edtCompanyPortNo.setText(companyPortNo);
			
	/*		  edtSalespersonEmail.setText("pranesh@ciglobalsolutions.com");
			  edtSalespersonPwd.setText("Passw0rd");
			  edtCompanyEmail.setText("vikas@ciglobalsolutions.com");
			  edtSalespersonHostName.setText("box803.bluehost.com");
			  //imap.gmail.com--->This is the salesperson's Server name
			  edtSalespersonPortNo.setText("993"); edtCompanyPortNo.setText("465");
			 
			*/

		}

		btnEmailSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {

					salesPersonEmail = edtSalespersonEmail.getText().toString();
					salespersonPwd = edtSalespersonPwd.getText().toString();
					companyEmail = edtCompanyEmail.getText().toString();
					salespersonHostName = edtSalespersonHostName.getText()
							.toString();
					salespersonPortNo = Integer.parseInt(edtSalespersonPortNo
							.getText().toString());
					companyPortNo = edtCompanyPortNo.getText().toString();

					if (!(salesPersonEmail.isEmpty()
							|| salespersonPwd.isEmpty()
							|| companyEmail.isEmpty()
							|| salespersonHostName.isEmpty()
							|| companyPortNo.isEmpty() || salespersonPortNo < 0)) {
						HhEmailSetting email = new HhEmailSetting();
						email.setHhEmailSetting_salespersonemail(edtSalespersonEmail
								.getText().toString());
						email.setHhEmailSetting_salespersonpwd(edtSalespersonPwd
								.getText().toString());
						email.setHhEmailSetting_companyemail(edtCompanyEmail
								.getText().toString());
						email.setHhEmailSetting_salespersonhostname(edtSalespersonHostName
								.getText().toString());
						email.setHhEmailSetting_salespersonportno(Integer
								.parseInt(edtSalespersonPortNo.getText()
										.toString()));
						email.setHhEmailSetting_companyportno(edtCompanyPortNo
								.getText().toString());

						if (e_Setting == null) {
							helper.openWritableDatabase();
							helper.addEmailSetting(email);
							helper.closeDatabase();
						} else {
							helper.openWritableDatabase();
							helper.updateEmailSetting(email);
							helper.closeDatabase();
						}

						isLogedIn = supporter.isLogedIn();
						if (isLogedIn) {
							supporter.simpleNavigateTo(MainMenu.class);
						} else {
							supporter.simpleNavigateTo(Login.class);
						}

					} else {

						toastMsg.showToast(EmailSettings.this,
								"Please Enter the above fields !!");

					}
				} catch (NumberFormatException e) {
					toastMsg.showToast(EmailSettings.this,
							"Please Enter the SP PortNo correctly!!");

				} catch (Exception e) {
					e.printStackTrace();

				}
			}

		});

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
