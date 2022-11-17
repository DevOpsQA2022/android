package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.Mspdb;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author T.SARAVANAN class used for updating sequence number...
 */

public class AdminLogin extends AppBaseActivity {

	/** variable declarations */
	private MspDBHelper dbHelper;
	private Supporter supporter;
	private ArrayAdapter<String> cmpnySpnAdapter;
	private Spinner spnCompany;
	private List<HhCompany> lstCompany;
	private Button btnLogin;
	private EditText edtPwd;
	private String adminPass;
	private LinearLayout adminLoginLay;
	private RelativeLayout sequenceLay;

	private EditText edtTranNo;
	private EditText edtCustNo;
	private EditText edtQuoteNo;
	private Button btnAdminUpdate;
	private int transNo;
	private int custNo;
	private int quoteNo;
	private Mspdb mspdb;
	private int tempTransNo;
	private int tempCustNo;
	private int tempQuoteNo;
	private ToastMessage toastMsg;
	private String cmpnyNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.admin_login_layout);
		registerBaseActivityReceiver();

		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		toastMsg = new ToastMessage();

		adminLoginLay = (LinearLayout) findViewById(R.id.lay_seqLogin);
		sequenceLay = (RelativeLayout) findViewById(R.id.lay_sequence);

		edtPwd = (EditText) findViewById(R.id.edtTxt_admin_Pswd);
		spnCompany = (Spinner) findViewById(R.id.spn_admin_Company);
		btnLogin = (Button) findViewById(R.id.btn_admin_Login);
		edtTranNo = (EditText) findViewById(R.id.edtTxt_NxtTranNo);
		edtCustNo = (EditText) findViewById(R.id.edtTxt_NxtCustNo);
		edtQuoteNo = (EditText) findViewById(R.id.edtTxt_NxtQuoteNo);
		btnAdminUpdate = (Button) findViewById(R.id.btn_sequUpdate);

		btnLogin.setEnabled(true);
		btnAdminUpdate.setEnabled(true);

		disableSequencelay();

		// loading company's in spinner
		List<String> mlist = loadListOfCmpny();
		cmpnySpnAdapter = new ArrayAdapter<String>(AdminLogin.this,
				android.R.layout.simple_dropdown_item_1line, mlist);
		// cmpnySpnAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnCompany.setAdapter(cmpnySpnAdapter);

		// admin button login...
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					btnLogin.setEnabled(false);
					sequenceLogin();
				} catch (Exception e) {
					Toast.makeText(AdminLogin.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					LogfileCreator.appendLog(e.getMessage());
					btnLogin.setEnabled(true);
				}
			}
		});
	}

	protected void sequenceLogin() {

		String pswd = edtPwd.getText().toString();

		if (pswd.equals("")) {
			toastMsg.showToast(AdminLogin.this, "Enter admin password");
			btnLogin.setEnabled(true);
		} else {
			cmpnyNo = lstCompany.get(spnCompany.getSelectedItemPosition())
					.getCompany_number();
			dbHelper.openReadableDatabase();
			HhManager manager = dbHelper.getAdminData(pswd, cmpnyNo);
			dbHelper.closeDatabase();
			if (manager != null) {
				adminPass = manager.getHhManager_adminpass();
				if (adminPass.equals(pswd)) {
					disableAdminLay();
					enableSequencelay();
				}
			} else {
				toastMsg.showToast(AdminLogin.this,
						"Enter valid admin password");
				btnLogin.setEnabled(true);
			}
		}
	}

	private void enableSequencelay() {
		for (int i = 0; i < sequenceLay.getChildCount(); i++) {
			View child = sequenceLay.getChildAt(i);
			child.setEnabled(true);
		}

		dbHelper.openReadableDatabase();
		mspdb = dbHelper.getAdminMspDbData(cmpnyNo);
		dbHelper.closeDatabase();
		transNo = mspdb.getMspdb_orderNumber();
		custNo = mspdb.getMspdb_customerNumber();
		quoteNo = mspdb.getMspdb_quoteNumber();

		edtTranNo.setText("" + transNo);
		edtCustNo.setText("" + custNo);
		edtQuoteNo.setText("" + quoteNo);

		btnAdminUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnAdminUpdate.setEnabled(false);
				checkValidation();

			}
		});

	}

	private List<String> loadListOfCmpny() {
		dbHelper.openReadableDatabase();
		lstCompany = dbHelper.getCompanyDatas();
		dbHelper.closeDatabase();
		List<String> lstCompany1 = new ArrayList<String>();

		for (int i = 0; i < lstCompany.size(); i++) {
			lstCompany1.add(lstCompany.get(i).getCompany_name());
		}

		return lstCompany1;
	}

	private void disableAdminLay() {
		for (int i = 0; i < adminLoginLay.getChildCount(); i++) {
			View child = adminLoginLay.getChildAt(i);
			child.setEnabled(false);
		}
	}

	private void disableSequencelay() {
		for (int i = 0; i < sequenceLay.getChildCount(); i++) {
			View child = sequenceLay.getChildAt(i);
			child.setEnabled(false);
		}
	}

	protected void checkValidation() {
		String strTranNo = edtTranNo.getText().toString();
		String strCusNo = edtCustNo.getText().toString();
		String strQuoteNo = edtQuoteNo.getText().toString();

		if (strTranNo.equals("") && strCusNo.equals("")
				&& strQuoteNo.equals("")) {
			toastMsg.showToast(AdminLogin.this, "Enter value in all fields");
			btnAdminUpdate.setEnabled(true);
		} else if (strTranNo.equals("")) {
			toastMsg.showToast(AdminLogin.this, "Enter Transaction number");
			btnAdminUpdate.setEnabled(true);
		} else if (strCusNo.equals("")) {
			toastMsg.showToast(AdminLogin.this, "Enter Customer number");
			btnAdminUpdate.setEnabled(true);
		} else if (strQuoteNo.equals("")) {
			toastMsg.showToast(AdminLogin.this, "Enter Quotation number");
			btnAdminUpdate.setEnabled(true);
		} else {
			tempTransNo = Integer.parseInt(strTranNo);
			tempCustNo = Integer.parseInt(strCusNo);
			tempQuoteNo = Integer.parseInt(strQuoteNo);

			if (tempTransNo < transNo) {
				toastMsg.showToast(AdminLogin.this,
						"Transaction Number should be greater than " + transNo);
				btnAdminUpdate.setEnabled(true);
			} else if (tempCustNo < custNo) {
				toastMsg.showToast(AdminLogin.this,
						"Customer Number should be greater than " + custNo);
				btnAdminUpdate.setEnabled(true);
			} else if (tempQuoteNo < quoteNo) {
				toastMsg.showToast(AdminLogin.this,
						"Quotation Number should be greater than " + quoteNo);
				btnAdminUpdate.setEnabled(true);
			} else if (tempTransNo == transNo && tempCustNo == custNo
					&& tempQuoteNo == quoteNo) {
				showAlert();
			} else if (tempTransNo >= transNo && tempCustNo >= custNo
					&& tempQuoteNo >= quoteNo) {
				updateSequenceNos(tempTransNo, tempCustNo, tempQuoteNo);
			}
		}

	}

	private void showAlert() {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				AdminLogin.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						supporter.simpleNavigateTo(Login.class);
					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						btnAdminUpdate.setEnabled(true);
					}
				});
		alertDialog
				.setMessage("Do you want to proceed without updating sequence number ?");
		////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

	}

	private void updateSequenceNos(int transNo2, int custNo2, int quoteNo2) {
		System.out.println("Company Num: " + cmpnyNo);
		mspdb.setMspdb_companyNumber(cmpnyNo);
		mspdb.setMspdb_orderNumber(transNo2);
		mspdb.setMspdb_customerNumber(custNo2);
		mspdb.setMspdb_quoteNumber(quoteNo2);
		dbHelper.openWritableDatabase();
		dbHelper.updateMspDb(mspdb, cmpnyNo);
		dbHelper.closeDatabase();
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Information");
		alertUser.setIcon(R.drawable.tick);
		alertUser.setCancelable(false);
		alertUser.setMessage("Sequence Number updated successfully");
		alertUser.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						supporter.simpleNavigateTo(Login.class);
					}
				});
			//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Confirmation");
		alertUser.setCancelable(false);
		alertUser.setIcon(R.drawable.warning);
		alertUser
				.setMessage("Are you sure want to cancel the sequence number updation?");
		alertUser.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						supporter.simpleNavigateTo(Login.class);
					}
				});

		alertUser.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

			//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
