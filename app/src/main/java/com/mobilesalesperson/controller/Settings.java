package com.mobilesalesperson.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author T.SARAVANAN class used to set the common properties for this
 *         application...
 */

public class Settings extends AppBaseActivity {

	/** variable declarations */
	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private EditText edtsetNoOfPrint;
	private EditText edtsetNoOfItmSave;
	private EditText edtsetTotPieceDiscount;
	private EditText edtsetDiscountPrice;
	private EditText edtsetServerPath;
	private Spinner spnSyncAccnt;
	private Spinner spnDateFormat;
	private Spinner spnDecimalFormat;
	private ImageView btnsetTC;
	private Spinner spnPrinterName;
	private Spinner spnPrinterModel;
	private CheckBox chkShipment;
	private CheckBox chkPrepayment;
	private CheckBox chkAutoReport;
	private CheckBox chkCusNameEdit;
	private CheckBox chkNonStockItem;
	private List<String> listContent;
	private List<String> listDecicontent;
	private List<String> listDataTransferContent;
	private List<String> listPrinterName;
	private List<String> listPrinterModel;
	private ArrayAdapter<String> dateFormatAdapter;
	private ArrayAdapter<String> decimalFormatAdapter;
	private ArrayAdapter<String> dataTransferAdapter;
	private ArrayAdapter<String> printerNameAdapter;
	private ArrayAdapter<String> printerModelAdapter;
	private int showShip, showPrepay, showAutoGen, showCusNameEdit,
			showNonStockItem;
	HhSetting setting;
	int ship = 0;
	int prepay = 0;
	int autogen = 0;
	int cusNameEdit = 0;
	int nonStockItem = 0;

	private ToastMessage toastMsg;
	private String syncAcc;
	private String dformat;
	private String deciformat;
	private String prntrName;
	private String prntrModel;

	private TableRow autoSave;
	private TableRow totPiece;
	private TableRow discPrice;
	private TableRow allowNotStockItem;
	private boolean isLogedIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.settings_layout);
		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);
		toastMsg = new ToastMessage();

		edtsetNoOfPrint = (EditText) findViewById(R.id.edt_setNoOfPrint);
		edtsetNoOfItmSave = (EditText) findViewById(R.id.edt_setNoOfItmSave);
		edtsetTotPieceDiscount = (EditText) findViewById(R.id.edt_setTotPieceDiscount);
		edtsetDiscountPrice = (EditText) findViewById(R.id.edt_setDiscountPrice);
		spnDateFormat = (Spinner) findViewById(R.id.spn_setDateFormat);
		spnDecimalFormat = (Spinner) findViewById(R.id.spn_setDecimalFormat);
		spnSyncAccnt = (Spinner) findViewById(R.id.spn_emailAccnt);
		edtsetServerPath = (EditText) findViewById(R.id.edt_setServerPath);
		spnPrinterName = (Spinner) findViewById(R.id.spn_printerName);
		spnPrinterModel = (Spinner) findViewById(R.id.spn_printerModel);
		chkShipment = (CheckBox) findViewById(R.id.chk_Shipment);
		chkPrepayment = (CheckBox) findViewById(R.id.chk_Prepayment);
		chkAutoReport = (CheckBox) findViewById(R.id.chk_AutoReport);
		chkCusNameEdit = (CheckBox) findViewById(R.id.chk_CusNameEdit);
		chkNonStockItem = (CheckBox) findViewById(R.id.chk_NonStockItem);
		btnsetTC = (ImageView) findViewById(R.id.btn_setTC);

		autoSave = (TableRow) findViewById(R.id.row_autoSave);
		totPiece = (TableRow) findViewById(R.id.row_totPiece);
		discPrice = (TableRow) findViewById(R.id.row_discPrice);
		allowNotStockItem = (TableRow) findViewById(R.id.row_AllowNonStockItem);

		autoSave.setVisibility(View.GONE);
		totPiece.setVisibility(View.GONE);
		discPrice.setVisibility(View.GONE);
		allowNotStockItem.setVisibility(View.GONE);

		isLogedIn = supporter.isLogedIn();

		// set cursor positon in editext
		edtsetNoOfPrint.setSelection(edtsetNoOfPrint.getText().length());

		// Load static date format's
		listContent = supporter.loadInDateFormatStaticData();

		dateFormatAdapter = new ArrayAdapter<String>(Settings.this,
				android.R.layout.simple_dropdown_item_1line, listContent);
		// dateFormatAdapter.setDropDownViewResource(R.layout.spinner_item_layout);

		spnDateFormat.setAdapter(dateFormatAdapter);

		// Load static decimal format
		listDecicontent = supporter.loadDecimalFormatData();
		decimalFormatAdapter = new ArrayAdapter<String>(Settings.this,
				android.R.layout.simple_dropdown_item_1line, listDecicontent);
		// decimalFormatAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnDecimalFormat.setAdapter(decimalFormatAdapter);

		// Load static transfer data account
		listDataTransferContent = supporter.loadInTransferSyncStaticData();
		dataTransferAdapter = new ArrayAdapter<String>(Settings.this,
				android.R.layout.simple_dropdown_item_1line,
				listDataTransferContent);
		// dataTransferAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnSyncAccnt.setAdapter(dataTransferAdapter);

		// load printer name..
		listPrinterName = supporter.loadPrinterName();
		printerNameAdapter = new ArrayAdapter<String>(Settings.this,
				android.R.layout.simple_dropdown_item_1line, listPrinterName);
		// printerNameAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnPrinterName.setAdapter(printerNameAdapter);

		// load printer name..
		listPrinterModel = supporter.loadPrinterModel();
		printerModelAdapter = new ArrayAdapter<String>(Settings.this,
				android.R.layout.simple_dropdown_item_1line, listPrinterModel);
		// printerModelAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
		spnPrinterModel.setAdapter(printerModelAdapter);

		// getting setting data from db
		dbhelpher.openReadableDatabase();
		setting = dbhelpher.getSettingData();
		dbhelpher.closeDatabase();
		if (setting != null) {

			edtsetNoOfPrint.setText("" + setting.getHhSetting_numcopiesprint());
			edtsetNoOfItmSave.setText("" + setting.getHhSetting_itemautosave());
			edtsetTotPieceDiscount.setText(""
					+ setting.getHhSetting_numofpiecedic());
			edtsetDiscountPrice.setText("" + setting.getHhSetting_discprice());

			String datFormat = setting.getHhSetting_dateformat();
			int index1 = listContent.indexOf(datFormat);
			spnDateFormat.setSelection(index1);

			String deciFormat = setting.getHhSetting_decimalformat();
			int index2 = listDecicontent.indexOf(deciFormat);
			spnDecimalFormat.setSelection(index2);

			edtsetServerPath.setText(setting.getHhSetting_mspserverpath());

			String transferDataAcc = setting.getHhSetting_datasyncservice();
			int index3 = listDataTransferContent.indexOf(transferDataAcc);
			spnSyncAccnt.setSelection(index3);

			String prntName = setting.getHhSetting_printerName();
			int index4 = listPrinterName.indexOf(prntName);
			spnPrinterName.setSelection(index4);

			String prntModel = setting.getHhSetting_printerModel();
			int index5 = listPrinterModel.indexOf(prntModel);
			spnPrinterModel.setSelection(index5);

		}

		// date format spinner selected items stored in variables..
		dformat = (String) spnDateFormat.getItemAtPosition(spnDateFormat
				.getSelectedItemPosition());

		// date format spinner selected items stored in variables..
		deciformat = (String) spnDecimalFormat
				.getItemAtPosition(spnDecimalFormat.getSelectedItemPosition());

		// transfer data account spinner selected items stored in variables..
		syncAcc = (String) spnSyncAccnt.getItemAtPosition(spnSyncAccnt
				.getSelectedItemPosition());

		// printer name spinner selected items stored in variables...
		prntrName = (String) spnPrinterName.getItemAtPosition(spnPrinterName
				.getSelectedItemPosition());

		// printer model spinner selected items stored in variables...
		prntrModel = (String) spnPrinterModel.getItemAtPosition(spnPrinterModel
				.getSelectedItemPosition());

		// spinner dateformat on item selected...
		spnDateFormat.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				dformat = (String) spnDateFormat
						.getItemAtPosition(spnDateFormat
								.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// spinner dateformat on item selected...
		spnDecimalFormat
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						deciformat = (String) spnDecimalFormat
								.getItemAtPosition(spnDecimalFormat
										.getSelectedItemPosition());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		// spinner email on item selected listener..
		spnSyncAccnt.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				syncAcc = (String) spnSyncAccnt.getItemAtPosition(spnSyncAccnt
						.getSelectedItemPosition());
								
				if (syncAcc.equals("Email")) {
					edtsetServerPath.setText("");
					edtsetServerPath.setEnabled(false);
					edtsetServerPath.setClickable(false);
					btnsetTC.setEnabled(false);
					btnsetTC.setClickable(false);

				} else {
					/*edtsetServerPath.setText("");*/
					edtsetServerPath.setEnabled(true);
					edtsetServerPath.setClickable(true);
					btnsetTC.setEnabled(true);
					btnsetTC.setClickable(true);
				}
				
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// spinner printer name on item selected...
		spnPrinterName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				prntrName = (String) spnPrinterName
						.getItemAtPosition(spnPrinterName
								.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// spinner printer model on item selected...
		spnPrinterModel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				prntrModel = (String) spnPrinterModel
						.getItemAtPosition(spnPrinterModel
								.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// show shipment checkbox click event
		chkShipment.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					ship = 1;
				} else {
					ship = 0;
				}
			}
		});

		// show prepayment checkbox click event
		chkPrepayment.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					prepay = 1;
				} else {
					prepay = 0;
				}
			}
		});

		// auto report generation checkbox click event
		chkAutoReport.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					autogen = 1;
				} else {
					autogen = 0;
				}
			}
		});

		// customer name editable checkbox click event
		chkCusNameEdit
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cusNameEdit = 1;
						} else {
							cusNameEdit = 0;
						}
					}
				});

		// allow nonstock item checkbox click event
		chkNonStockItem
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							nonStockItem = 1;
						} else {
							nonStockItem = 0;
						}
					}
				});

		showShip = Integer.parseInt("" + setting.getHhSetting_showship());
		if (showShip == 1) {
			chkShipment.setChecked(true);
		} else {
			chkShipment.setChecked(false);
		}

		showPrepay = Integer.parseInt("" + setting.getHhSetting_showprepay());
		if (showPrepay == 1) {
			chkPrepayment.setChecked(true);
		} else {
			chkPrepayment.setChecked(false);
		}

		showAutoGen = Integer.parseInt(""
				+ setting.getHhSetting_autoreportgen());
		if (showAutoGen == 1) {
			chkAutoReport.setChecked(true);
		} else {
			chkAutoReport.setChecked(false);
		}

		showCusNameEdit = Integer.parseInt(""
				+ setting.getHhSetting_cusnameeditable());
		if (showCusNameEdit == 1) {
			chkCusNameEdit.setChecked(true);
		} else {
			chkCusNameEdit.setChecked(false);
		}

		showNonStockItem = Integer.parseInt(""
				+ setting.getHhSetting_nonstockitem());
		if (showNonStockItem == 1) {
			chkNonStockItem.setChecked(true);
		} else {
			chkNonStockItem.setChecked(false);
		}

		btnsetTC.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String strIp = checkIp();
				if (strIp.equals("Success")) {
					String path = edtsetServerPath.getText().toString();
					Intent intent = new Intent(Settings.this,
							WebViewActivity.class);
					intent.putExtra("IP Address", path);
					startActivity(intent);
				} else {
					toastMsg.showToast(Settings.this,
							"Enter Valid IP Address...");
				}

			}
		});

	}

	protected String checkIp() {
		String result = "";
		String path = edtsetServerPath.getText().toString();
		final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(path);
		boolean IPcheck = matcher.matches();

		final String PATTERN2 = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\:" + "([0-9]+)";

		Pattern pattern2 = Pattern.compile(PATTERN2);
		Matcher matcher2 = pattern2.matcher(path);
		boolean IPcheck2 = matcher2.matches();

		if (IPcheck) {
			result = "Success";
		} else if (IPcheck2) {
			result = "Success";
		} else {
			result = "Fail";
		}

		return result;

	}

	// method to control menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setting_menu, menu);
		return true;
	}

	// method to control menu options
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setting_mnu_save:
			if(!syncAcc.equals("Email")){
				String strIp = checkIp();
				if (strIp.equals("Success")) {
					String strPrint = edtsetNoOfPrint.getText().toString();
					if (strPrint.trim().equals("")) {
						toastMsg.showToast(Settings.this,
								"No. of copies to print field should not be empty.");
					} else {
						settingUpdation();
					}
				} else {
					toastMsg.showToast(Settings.this, "Enter Valid IP Address...");
				}
			}
			else
			{
				settingUpdation();
				
			}
			
			break;

		case R.id.setting_mnu_cancel:
			Settings.this.finish();
			break;
		}
		return true;
	}

	private void settingUpdation() {
		HhSetting setting1 = new HhSetting();
		setting1.setHhSetting_numcopiesprint(Integer.parseInt(edtsetNoOfPrint
				.getText().toString()));
		setting1.setHhSetting_itemautosave(Integer.parseInt(edtsetNoOfItmSave
				.getText().toString()));
		setting1.setHhSetting_numofpiecedic(Integer
				.parseInt(edtsetTotPieceDiscount.getText().toString()));
		setting1.setHhSetting_discprice(Double.parseDouble(edtsetDiscountPrice
				.getText().toString()));
		setting1.setHhSetting_dateformat(dformat);
		setting1.setHhSetting_decimalformat(deciformat);
		setting1.setHhSetting_datasyncservice(syncAcc);
		setting1.setHhSetting_mspserverpath(edtsetServerPath.getText()
				.toString());

		setting1.setHhSetting_showship(ship);
		setting1.setHhSetting_showprepay(prepay);
		setting1.setHhSetting_autoreportgen(autogen);
		setting1.setHhSetting_printerName(prntrName);
		setting1.setHhSetting_printerModel(prntrModel);
		setting1.setHhSetting_cusnameeditable(cusNameEdit);
		setting1.setHhSetting_nonstockitem(nonStockItem);

		dbhelpher.openWritableDatabase();
		dbhelpher.updateSetting(setting1);
		dbhelpher.closeDatabase();

		if(!syncAcc.equals("Email")){
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Information");
		alertUser.setIcon(R.drawable.tick);
		alertUser.setCancelable(false);
		alertUser.setMessage("Settings updated successfully");
		alertUser.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isLogedIn) {
							supporter.simpleNavigateTo(MainMenu.class);
						} else {
							supporter.simpleNavigateTo(Login.class);
						}
					}
				});
		//	//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

		}
		else{
			supporter.simpleNavigateTo(EmailSettings.class);	
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Confirmation");
		alertUser.setIcon(R.drawable.warning);
		alertUser.setCancelable(false);
		alertUser
				.setMessage("Do you want to Cancel ?");
		alertUser.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isLogedIn) {
							supporter.simpleNavigateTo(MainMenu.class);
						} else {
							supporter.simpleNavigateTo(Login.class);
						}
					}
				});

		alertUser.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		//	//alertUser.show();
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
