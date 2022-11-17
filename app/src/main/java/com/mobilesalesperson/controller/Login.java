package com.mobilesalesperson.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Flags;
import javax.mail.Session;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;

import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.Encryptor;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhEmailSetting;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.FileLoader;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.sun.mail.pop3.POP3Folder;
import android.provider.Settings.Secure;

/**
 * @author TIVM class to control login activity
 */
public class Login extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private EditText edtTxtUserName;
	private EditText edtTxtPswd;
	private Spinner spnCompany;
	private Button btnLogin;

	private MspDBHelper dbHelper;
	private List<HhCompany> lstCompany;
	private List<String> listSettings;
	static final int MENU_SETTING_DIALOG_ID = 0;
	private Dialog dialog;
	private ListView dialog_ListView;
	private ArrayAdapter<String> cmpnySpnAdapter;

	/** websevice variable declarations */
	private static final String SOAP_ACTION = "http://MSPWirelessSync/xmlret";
	private static final String METHOD_NAME = "xmlret";
	public static final String NAMESPACE = "http://MSPWirelessSync";
	public static final String URL_PART_A = "http://";
	public static final String URL_PART_B = "/MSPWirelessSync/MSPWSService.asmx";

	// temporary change for testing
	public static final String SOAP_ACTION_GETCOUNT = "http://tempuri.org/getXMLFileSize";
	public static final String SOAP_ACTION_GETXML = "http://tempuri.org/getXMLStr";
	public static final String SOAP_ACTION_DELETETXT = "http://MSPWirelessSync/Deletetxt";
	public static final String METHOD_GET_COUNT = "getXMLFileSize";
	public static final String METHOD_GET_XML = "getXMLStr";
	public static final String NAMESPACE_TEST = "http://tempuri.org/";
	public static final String URL_IMPORT_A = "http://";
	public static final String URL_IMPORT_B = "/AMSPWS/AMSPWS.asmx";
	public static final String METHOD_DELETE_TXT = "Deletetxt";

	private String spCode = "";
	private String typeOfImport = "";
	private HhSetting setting;
	private Encryptor encryptor;
	private String existingSPUserCode = "";

	private TelephonyManager telephonyManager;
	private String devId;
	private String dbDevId;
	private ToastMessage toastMsg;

	private String cmpnyNo; // added jul 15/2013
	private File outputFile;// added jul 23/2013
	private String sFile;
	private String serviceIP;
	private List<String> mlistCompany;

	/**
	 * method to initialize the class activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.login_layout);

		registerBaseActivityReceiver();
		
		edtTxtUserName = (EditText) findViewById(R.id.edtTxt_UserName);
		edtTxtPswd = (EditText) findViewById(R.id.edtTxt_Pswd);
		spnCompany = (Spinner) findViewById(R.id.spn_Company);
		btnLogin = (Button) findViewById(R.id.btn_Login);

		btnLogin.setEnabled(true);
		/*telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		devId = telephonyManager.getDeviceId();*/
		String android_id = Secure.getString(Login.this.getContentResolver(),Secure.ANDROID_ID);
		devId = android_id;

		if (devId == null) {
			devId = Build.SERIAL;
			if (devId == null) {
				devId = "1234";
			}
		}

		toastMsg = new ToastMessage();




		encryptor = new Encryptor();
		if (Encryptor.isSdPresent()) {

			long size = checkMemorySpace();

			if (size < 50) {
				callExpiredAlert("Your Device have low memory space. Please delete unwanted files.");
			}

			encryptor.createCommonPath(); // to create application common path

			dbHelper = new MspDBHelper(this);
			supporter = new Supporter(this, dbHelper);
			supporter.setDeviceId(devId); // to set device id in preference
			supporter.setLogedIn(false);

			dbHelper.openWritableDatabase();
			setting = dbHelper.getSettingData();
			dbHelper.closeDatabase();
			if (setting == null) {
				setting = new HhSetting();
				setting.setHhSetting_numcopiesprint(1);
				setting.setHhSetting_itemautosave(1);
				setting.setHhSetting_numofpiecedic(0);
				setting.setHhSetting_discprice(0.00);
				setting.setHhSetting_dateformat("MM/dd/yyyy");
				setting.setHhSetting_decimalformat("2");
				setting.setHhSetting_datasyncservice("Wireless Sync");
				setting.setHhSetting_mspserverpath("");
				setting.setHhSetting_printerName("Zebra");
				setting.setHhSetting_printerModel("3-inch");
				// setting.setHhSetting_printerModel("MZ 320"); this is old
				setting.setHhSetting_showship(1);
				setting.setHhSetting_showprepay(1);
				setting.setHhSetting_autoreportgen(1);
				setting.setHhSetting_spCode("");
				setting.setHhSetting_cusnameeditable(0);
				setting.setHhSetting_nonstockitem(0);

				dbHelper.openWritableDatabase();
				dbHelper.addSetting(setting);
				dbHelper.closeDatabase();
			} else {

				spCode = supporter.getTempSalesPersonCode(); // to get sales
																// person code
																// from
																// publicData
																// preference
				// to set preference data from setting table/
				if (spCode.equals("")) {
					spCode = setting.getHhSetting_spCode();
					supporter.setTempSalesPersonCode(spCode);
				}

				File root_path = Environment.getExternalStorageDirectory();

				final File folder_path = new File(root_path.getAbsoluteFile()
						+ "/" + "Android/AMSP/" + spCode);
				sFile = folder_path + "/" + "MSP.xml";
				java.io.File file = new java.io.File(sFile);

				if (file.exists() && !spCode.equals("")) {

					final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							Login.this);
					alertDialog.setTitle("Information");
					alertDialog.setIcon(R.drawable.tick);
					alertDialog.setCancelable(false);
					alertDialog.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									if (checkDatabase()) {
										dialog.cancel();
										doAlertToOverWriteDB(folder_path);

									} else {
										List<String> fileName = encryptor
												.getFileNames(folder_path);

										String[] availFiles = new String[fileName
												.size()];

										for (int i = 0; i < fileName.size(); i++) {
											availFiles[i] = fileName.get(i);
										}

										new DataLoadToDBOperation()
												.execute(availFiles);
									}
								}
							});

					alertDialog.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									encryptor.deleteFileFolder(spCode);
									dialog.dismiss();
								}
							});
					alertDialog
							.setMessage("New files are detected, Do you want to load file data to database?");
					//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
				}
			}

			listSettings = supporter.loadInSelectionSettings();

			mlistCompany = loadListOfCmpny();
			// loading company's in spinner
			cmpnySpnAdapter = new ArrayAdapter<String>(Login.this,
					android.R.layout.simple_dropdown_item_1line, mlistCompany);
			spnCompany.setAdapter(cmpnySpnAdapter);

			// to change company data
			spnCompany.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					String cmpNo = lstCompany.get(position).getCompany_number();
					// supporter.setTempCompanyNo(cmpNo);
					// supporter.setCompanyNo(cmpNo);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

			// login btn click function
			btnLogin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						btnLogin.setEnabled(false);
						String licenseDetail = encryptor.checkLicenceValidity();

						if (!checkDatabase()) {

							toastMsg.showToast(Login.this,
									"Please import data to login");
							btnLogin.setEnabled(true);

						} else {

							final String userName = edtTxtUserName.getText()
									.toString();
							String pswd = edtTxtPswd.getText().toString();

							cmpnyNo = lstCompany.get(
									spnCompany.getSelectedItemPosition())
									.getCompany_number();

							System.out.println(cmpnyNo);
							if (userName.equals("") && pswd.equals("")) {
								toastMsg.showToast(Login.this,
										"Enter salesperson code and password");
								btnLogin.setEnabled(true);
							} else {
								dbHelper.openReadableDatabase();
								HhManager manager = dbHelper.getManagerData(
										userName, cmpnyNo);
								dbHelper.closeDatabase();
								if (manager != null) {
									String uName = manager
											.getHhManager_userid();
									String uPass = manager
											.getHhManager_userpass();
									final String currency = manager
											.getHhManager_currency();
									if (uName.equals(userName)
											&& uPass.equals(pswd)) {

										dbDevId = manager
												.getHhManager_activekey();

										if (dbDevId.equals(devId)
												|| devId.startsWith("00000")) {
											if (licenseDetail.equals("nodata")) {
												callExpiredAlert("License details is not available, please import fresh data");
												btnLogin.setEnabled(true);
											} else if (licenseDetail
													.equals("expired")) {
												callExpiredAlert("Your license was expired!");
												btnLogin.setEnabled(true);
											} else {

												if (!licenseDetail
														.equals("full license")
														&& !licenseDetail
																.equals("expired")
														&& !licenseDetail
																.equals("nodata")) {

													AlertDialog.Builder alertUser = new AlertDialog.Builder(
															Login.this);
													alertUser
															.setTitle("Warning");
													alertUser
															.setIcon(R.drawable.warning);
													alertUser
															.setCancelable(false);
													alertUser
															.setMessage("Your license will expire within "
																	+ licenseDetail
																	+ " days");
													alertUser
															.setPositiveButton(
																	"OK",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			supporter
																					.setCompanyNo(cmpnyNo);
																			supporter
																					.setSalesPerson(userName);
																			supporter
																					.setCompanyCurrency(currency);
																			supporter
																					.simpleNavigateTo(MainMenu.class);
																			dialog.cancel();
																		}
																	});
														AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
												} else {
													supporter
															.setCompanyNo(cmpnyNo);
													supporter
															.setSalesPerson(userName);
													supporter
															.setCompanyCurrency(currency);
													supporter
															.simpleNavigateTo(MainMenu.class);
												}

											}
										} else {
											exitAlert("Unauthorized access\nContact Administrator");
											btnLogin.setEnabled(true);
										}
									} else {
										toastMsg.showToast(Login.this,
												"Enter valid salesperson code and password");
										btnLogin.setEnabled(true);
									}
								} else {
									toastMsg.showToast(Login.this,
											"Enter valid salesperson code and password");
									btnLogin.setEnabled(true);
								}
							}

						}
					} catch (Exception e) {
						Toast.makeText(Login.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
						LogfileCreator.appendLog(e.getMessage());
						btnLogin.setEnabled(true);
					}
				}
			});

		} else {
			exitAlert("SD card is required for this application");
		}

		edtTxtPswd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					edtTxtPswd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					android.provider.Settings.System.putInt(
							Login.this.getContentResolver(),
							android.provider.Settings.System.TEXT_SHOW_PASSWORD,
							0);
				}
			}
		});

	}// End of onCreate...

	private void callExpiredAlert(String message) {
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Warning");
		alertUser.setIcon(R.drawable.warning);
		alertUser.setCancelable(false);
		alertUser.setMessage(message);
		alertUser.setPositiveButton("OK",
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

	private long checkMemorySpace() {
		final long SIZE_KB = 1024L;
		final long SIZE_MB = SIZE_KB * SIZE_KB;
		long availableSpace = -1L;
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		availableSpace = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		long size = availableSpace / SIZE_MB;
		Log.i("Available Space in sdcard:", "Size in MB: " + size);

		return size;
	}

	private void exitAlert(String msg) {
		AlertDialog.Builder alertMemory = new AlertDialog.Builder(this);
		alertMemory.setTitle("Warning");
		alertMemory.setIcon(R.drawable.warning);
		alertMemory.setCancelable(false);
		alertMemory.setMessage(msg);
		alertMemory.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						//closeAllActivities();
						finishAffinity();
					}
				});
		alertMemory.show();
	}

	protected void doAlertToOverWriteDB(final File folder_path) {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Login.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dbHelper.openWritableDatabase();

						dbHelper.deleteAllTableRecords(Login.this); // to delete
																	// all table
						supporter.setOverwriteDB("yes");

						dbHelper.closeDatabase();

						List<String> fileName = encryptor
								.getFileNames(folder_path);

						String[] availFiles = new String[fileName.size()];

						for (int i = 0; i < fileName.size(); i++) {
							availFiles[i] = fileName.get(i);
						}

						new DataLoadToDBOperation().execute(availFiles);
						dialog.cancel();
					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// encryptor.moveFile(spCode, "Old Imports");
						encryptor.deleteFileFolder(spCode);
						dialog.dismiss();
					}
				});
		alertDialog
				.setMessage("Do you want to overwrite the existing database?");
		//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
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

	/** method for menu control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.login, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	/** method for menu control */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_mnu_exit:

			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					Login.this);
			alertDialog.setTitle("Confirmation");
			alertDialog.setIcon(R.drawable.warning);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							//closeAllActivities();
							finishAffinity();
						}
					});

			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alertDialog.setMessage("Do you want to exit?");
			//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

			break;

		case R.id.login_mnu_imports:

			dbHelper.openReadableDatabase();
			boolean isPostingDataAvail = dbHelper.isDataAvailableForPost();
			dbHelper.closeDatabase();

			spCode = edtTxtUserName.getText().toString();
			typeOfImport = "IMP";

			dbHelper.openReadableDatabase();
			setting = dbHelper.getSettingData();
			dbHelper.closeDatabase();

			supporter.setOverwriteDB("no"); // to check overwrite permission

			serviceIP = setting.getHhSetting_mspserverpath();

			existingSPUserCode = supporter.getTempSalesPersonCode();
			System.out.println("existing sp code: " + existingSPUserCode);
			String service = setting.getHhSetting_datasyncservice();
			if (spCode.equals("")) {
				toastMsg.showToast(Login.this, "Enter Valid Salesperson Code");
			} else if (!existingSPUserCode.equals("")
					&& !existingSPUserCode.equals(spCode)) {
				toastMsg.showToast(Login.this,
						"Wrong or Invalid Salesperson Code");
			} else if (serviceIP.equals("") && !service.equals("Email")) {
				toastMsg.showToast(Login.this,
						"Please enter server path in setting page, and continue");
			}

			else if (checkDatabase() && isPostingDataAvail) {

				final AlertDialog.Builder alertExport = new AlertDialog.Builder(
						Login.this);
				alertExport.setTitle("Confirmation");
				alertExport.setIcon(R.drawable.warning);
				alertExport.setCancelable(false);
				alertExport.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				alertExport
						.setMessage("Unexported data is available. You need to export first");
				alertExport.show();
			} else {
				if (service.equals("Wireless Sync")) { // Wireless Sync
					// new DataLoadToFileOperation().execute();
					new LargeDataLoadToFileOperation().execute(); // added for
																	// large
																	// data
																	// wireless
																	// transfer

				} else if (service.equals("Email")) {
					new DataLoadFromMailOperation().execute();
				} else { // for Wired Sync

					if (Encryptor.isSdPresent()) { // to check sdcard
													// availability

						File sdCardRoot = Environment
								.getExternalStorageDirectory();
						File msp_root_path = new File(
								sdCardRoot.getAbsoluteFile() + "/"
										+ "Android/AMSP");
						File spDir = new File(msp_root_path, spCode);
						sFile = spDir + "/" + "MSP.xml";
						java.io.File file = new java.io.File(sFile);

						if (file.exists()) { // to check MSP.xml availability

							if (checkDatabase()) {

								doAlertToOverWriteDB(spDir);

							} else {
								supporter.setTempSalesPersonCode(spCode); // to
																			// set
																			// sales
																			// person
																			// code
																			// in
																			// publicData
																			// preference
								List<String> fileName = encryptor
										.getFileNames(spDir);

								String[] availFiles = new String[fileName
										.size()];

								for (int i = 0; i < fileName.size(); i++) {
									availFiles[i] = fileName.get(i);
								}

								new DataLoadToDBOperation().execute(availFiles);
							}

						} else {
							toastMsg.showToast(Login.this,
									"Files not available");
						}

					} else {
						toastMsg.showToast(Login.this, "Sd card required");
					}
				}

			}

			break;
		case R.id.login_mnu_import_inventory:

			spCode = edtTxtUserName.getText().toString();
			typeOfImport = "INV";
			spCode = edtTxtUserName.getText().toString();
			serviceIP = setting.getHhSetting_mspserverpath();
			existingSPUserCode = supporter.getTempSalesPersonCode();

			if (spCode.equals("")) {
				toastMsg.showToast(Login.this, "Enter Valid Salesperson Code");
			} else if (!existingSPUserCode.equals("")
					&& !existingSPUserCode.equals(spCode)) {
				toastMsg.showToast(Login.this,
						"Wrong or Invalid Salesperson Code");
			} else if (serviceIP.equals("")) {
				toastMsg.showToast(Login.this,
						"Please enter server path in setting page, and continue");
			} else if (!checkDatabase()) {
				toastMsg.showToast(Login.this, "Please import data...");
			} else {
				new LargeDataLoadToFileOperation().execute();
			}
			break;
		case R.id.login_mnu_incremental:
			spCode = edtTxtUserName.getText().toString();
			typeOfImport = "INC";
			spCode = edtTxtUserName.getText().toString();
			serviceIP = setting.getHhSetting_mspserverpath();
			existingSPUserCode = supporter.getTempSalesPersonCode();

			if (spCode.equals("")) {
				toastMsg.showToast(Login.this, "Enter Valid Salesperson Code");
			} else if (!existingSPUserCode.equals("")
					&& !existingSPUserCode.equals(spCode)) {
				toastMsg.showToast(Login.this,
						"Wrong or Invalid Salesperson Code");
			} else if (serviceIP.equals("")) {
				toastMsg.showToast(Login.this,
						"Please enter server path in setting page, and continue");
			} else if (!checkDatabase()) {
				toastMsg.showToast(Login.this, "Please import data...");
			} else {
				new LargeDataLoadToFileOperation().execute();
			}
			break;
		case R.id.login_mnu_settings:
			showDialog(MENU_SETTING_DIALOG_ID);
			break;

		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem importInventory = menu
				.findItem(R.id.login_mnu_import_inventory);
		MenuItem incrementalTranfer = menu.findItem(R.id.login_mnu_incremental);
		//SURESH HIDED THE IF CONTION WE HAVE TOO HIDE THIS  OPTION
		/*if (checkDatabase()) {
			importInventory.setVisible(true);
			incrementalTranfer.setVisible(true);
		} else {*/
			importInventory.setVisible(false);
			incrementalTranfer.setVisible(false);
		/*}*/
		return true;
	}

	
	private boolean checkDatabase() {
		dbHelper.openReadableDatabase();
		boolean isExst = dbHelper.checkDB();
		dbHelper.closeDatabase();
		return isExst;
	}

	private class DataLoadFromMailOperation extends
			AsyncTask<String, String, String> {
		private ProgressDialog dialog;

		final String keyword = "To MobileSalesperson";
		int m = 0;
		// IMAP

		Hashtable<Long, Integer> hasTab = new Hashtable<Long, Integer>();
		Long id;

		/*
		 * // POP3
		 * 
		 * Hashtable<String, Integer> hasTab = new Hashtable<String, Integer>();
		 * String id = "";
		 */

		String result;

		private String[] availFiles;

		public DataLoadFromMailOperation() {
			dialog = new ProgressDialog(Login.this);
			dialog.setCancelable(false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.setMessage("Accessing Data File..");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			Properties properties = new Properties();
			HhEmailSetting email = dbHelper.getEmail();

			String sp_downloading_host = email
					.getHhEmailSetting_salespersonhostname();
			int sp_downloading_port = email
					.getHhEmailSetting_salespersonportno();
			final String sp_mail = email.getHhEmailSetting_salespersonemail();
			final String sp_pwd = email.getHhEmailSetting_salespersonpwd();
			final String cmpy_mail = email.getHhEmailSetting_companyemail();
			dbHelper.closeDatabase();

			// IMAP

			// server setting
			properties.put("mail.imap.host", sp_downloading_host);
			properties.put("mail.imap.port", sp_downloading_port);

			// SSL setting
			properties.setProperty("mail.imap.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			properties.setProperty("mail.imap.socketFactory.fallback", "false");
			properties.setProperty("mail.imap.socketFactory.port",
					String.valueOf(sp_downloading_port));

			// POP3

			// server setting
			/*
			 * properties.put("mail.pop3.host", sp_downloading_host);
			 * properties.put("mail.pop3.port", sp_downloading_port);
			 * 
			 * // SSL setting
			 * properties.setProperty("mail.pop3.socketFactory.class",
			 * "javax.net.ssl.SSLSocketFactory");
			 * properties.setProperty("mail.pop3.socketFactory.fallback",
			 * "false"); properties.setProperty("mail.pop3.socketFactory.port",
			 * String.valueOf(sp_downloading_port));
			 */
			// Session session = Session.getDefaultInstance(properties);

			Session session = Session.getInstance(properties,
					new javax.mail.Authenticator() {

						protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
							return new javax.mail.PasswordAuthentication(
									sp_mail, sp_pwd);
						}

					});
			try {

				// IMAP

				// connects to the message store
				Store store = session.getStore("imap");

				// POP3

				/*
				 * // connects to the message store Store store =
				 * session.getStore("pop3");
				 */

				store.connect(sp_downloading_host, sp_mail, sp_pwd);

				// publishProgress("Accessing Data File..");
				// opens the inbox folder
				Folder folderInbox = store.getFolder("INBOX");

				folderInbox.open(Folder.READ_WRITE);

				// Searching Mail Based On subject,from mailid..
				/*
				 * SearchTerm searchCondition = new SearchTerm() {
				 * 
				 * @Override public boolean match(Message message) { try {
				 * 
				 * // Checking From Address Address[] fromAddress =
				 * message.getFrom(); if (fromAddress != null &&
				 * fromAddress.length > 0) {
				 * 
				 * if (fromAddress[0].toString().contains( cmpy_mail)) { //
				 * Checking Subject As MSP String sub =
				 * message.getSubject().trim(); String specific_Sub =
				 * keyword.trim(); if (sub.equalsIgnoreCase(specific_Sub)) {
				 * 
				 * return true;
				 * 
				 * } } } } catch (MessagingException ex) { ex.printStackTrace();
				 * } catch (Exception e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } return false; } };
				 */
				// Checking the Subject as "To MobileSalesPerson"
				SubjectTerm term1 = new SubjectTerm(keyword);

				// Checking the From Mail Id as "Company Mail Id"
				FromStringTerm fromTerm = new FromStringTerm(cmpy_mail);

				// Checking the Filetered Messages with And cond
				SearchTerm term2 = new AndTerm(fromTerm, term1);

				// Contains mails based on term2 search condition

				Message[] foundMessages = folderInbox.search(term2);

				int tot = foundMessages.length;

				String path = "";

				// IMAP

				for (int i = 0; i < tot; i++) {

					// Checking body content contains Sales person code
					// if condn true then put mail id n number in hashtable

					int num = foundMessages[i].getMessageNumber();
					UIDFolder uf = (UIDFolder) folderInbox;
					Long messageId = uf.getUID(foundMessages[i]);
					hasTab.put(messageId, num);

				}

				// POP3

				/*
				 * for (int i = 0; i < tot; i++) { // String // Checking body
				 * content contains Sales person code // if condn true then
				 * 
				 * int num = foundMessages[i].getMessageNumber(); POP3Folder uf
				 * = (POP3Folder) folderInbox; String messageId =
				 * uf.getUID(foundMessages[i]); hasTab.put(messageId, num); }
				 */

				if (tot >= 1) {

					// IMAP

					// Accessing the most recent mail from hash table
					id = Accessing_Recent_Message_IMAP(hasTab);

					// POP3

					/*
					 * // Accessing the most recent mail from hash table POP3 id
					 * = Accessing_Recent_Message_POP3(hasTab);
					 */

					System.out
							.println(" Check Recent ID" + id + " TOTAL" + tot);
					for (int i = 0; i < foundMessages.length; i++) {
						Integer val = foundMessages[i].getMessageNumber();

						if (val.equals(hasTab.get(id))) {

							String contentType = foundMessages[i]
									.getContentType();
							String messageContent = null;
							String attachFiles = "";

							if (contentType.contains("multipart")) {
								// content may contain attachments
								Multipart multiPart = (Multipart) foundMessages[i]
										.getContent();
								int numberOfParts = multiPart.getCount();
								for (int partCount = 0; partCount < numberOfParts; partCount++) {
									MimeBodyPart part = (MimeBodyPart) multiPart
											.getBodyPart(partCount);
									if (Part.ATTACHMENT.equalsIgnoreCase(part
											.getDisposition())) {
										// this part is attachment
										String fileName = part.getFileName();
										attachFiles += fileName + ", ";
										// part.saveFile(saveDirectory +
										// File.separator + fileName);
										File root_path = Environment
												.getExternalStorageDirectory();

										File msp_common_path = new File(
												root_path.getAbsoluteFile()
														+ "/" + "Android/AMSP");
										part.saveFile(msp_common_path
												+ File.separator + fileName);
										path = msp_common_path + File.separator
												+ fileName;

									} else {
										// this part may be the message content
										messageContent = part.getContent()
												.toString();
									}
								}

							}
							if (attachFiles.length() > 1) {
								attachFiles = attachFiles.substring(0,
										attachFiles.length() - 2);
							} else if (contentType.contains("text/plain")
									|| contentType.contains("text/html")) {
								Object content = foundMessages[i].getContent();
								if (content != null) {
									messageContent = content.toString();
								}
							}

						}
					}

					// IMAP

					// Deleting All Mail with Sub->To MobileSalesPerson
					// except the recent one mail which is recent

					for (int count = 0; count < tot; count++) {
						UIDFolder uf = (UIDFolder) folderInbox;
						Long messageId = uf.getUID(foundMessages[count]);
						if (!messageId.equals(id)) {
							foundMessages[count].setFlag(Flags.Flag.DELETED,
									true);
							folderInbox.expunge();
						}
					}

					// POP3

					// Deleting All Mail with Sub->To MobileSalesPerson
					// * except the recent one mail which is recent
					/*for (int count = 0; count < tot; count++) {
						POP3Folder uf = (POP3Folder) folderInbox;
						String messageId = uf.getUID(foundMessages[count])
								.toString();

						if (!messageId.equals(id)) {
							foundMessages[count].setFlag(Flags.Flag.DELETED,
									true);
						}
					}*/

					folderInbox.close(true);
					store.close();

					publishProgress("Moving data file");
					File root_path = Environment.getExternalStorageDirectory();

					File msp_common_path = new File(root_path.getAbsoluteFile()
							+ "/" + "Android/AMSP/Msp.xml");

					path = msp_common_path + File.separator;

					if (msp_common_path.exists()) {
						List<String[]> resultList = Encryptor.parseEmailOutput(
								path, spCode);

						String[] resultArray = resultList.get(0);
						result = resultArray[0];
						availFiles = resultList.get(1);
					} else {
						result = "nodata";
					}

				} else {
					result = "nomsg";
				}

			} catch (IOException e) {
				result = "input error";

				e.printStackTrace();
			} catch (Exception e) {
				Log.e("tag", "error", e);

				result = "error";
			}

			return result;

		}

		private String getText(Part p) throws MessagingException, IOException {
			if (p.isMimeType("text/plain")) {
				return (String) p.getContent();
			} else if (p.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) p.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					String s = getText(mp.getBodyPart(i));
					if (s != null)
						return s;
				}
			}
			return null;

		}

		@Override
		protected void onPostExecute(final String result) {

			// String spCode = "BB";

			if (dialog.isShowing()) {
				this.dialog.dismiss();
			}

			if (result.equals("success")) {

				if (checkDatabase()) {
					File root_path = Environment.getExternalStorageDirectory();
					final File folder_path = new File(
							root_path.getAbsoluteFile() + "/" + "Android/AMSP/"
									+ spCode);
					dialog.cancel();
					doAlertToOverWriteDB(folder_path);

				} else {
					supporter.setTempSalesPersonCode(spCode); // to set sales
																// person code
																// in publicData
																// preference
					new DataLoadToDBOperation().execute(availFiles); // to start
																		// db
																		// load
																		// operation
				}

			} else if (result.equals("nosd")) {
				toastMsg.showToast(Login.this, "Sd card required");
			} else if (result.equals("connection timeout")) {
				toastMsg.showToast(Login.this, "Unable to connect server.");
			} else if (result.equals("error in file creation")) {
				toastMsg.showToast(Login.this, "File creation failed.");
			} else if (result.equals("nomsg")) {
				toastMsg.showToast(Login.this, "No Mail Found For Importing");
			} else if (result.equals("nodata")) {
				toastMsg.showToast(Login.this, "No Data Available.");
			} else if (result.equals("error")) {
				toastMsg.showToast(Login.this,
						"Error in Data Transfer, Contact Administrator");
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			this.dialog.setMessage(values[0]);
			dialog.show();
		}

		// IMAP
		private Long Accessing_Recent_Message_IMAP(
				Hashtable<Long, Integer> hasTab) {
			// TODO Auto-generated method stub
			long x = 0;
			ArrayList as = new ArrayList(hasTab.entrySet());

			Collections.sort(as, new Comparator() {
				public int compare(Object o1, Object o2) {
					Map.Entry e1 = (Map.Entry) o1;
					Map.Entry e2 = (Map.Entry) o2;
					Integer first = (Integer) e1.getValue();
					Integer second = (Integer) e2.getValue();
					return first.compareTo(second);
				}
			});
			Iterator it = as.iterator();

			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				System.out.println(entry.getKey() + " : " + entry.getValue());
				x = (Long) entry.getKey();
			}
			return x;

		}

		// POP3
		private String Accessing_Recent_Message_POP3(
				Hashtable<String, Integer> hasTab) {
			// TODO Auto-generated method stub
			String x = "";
			ArrayList as = new ArrayList(hasTab.entrySet());

			Collections.sort(as, new Comparator() {
				public int compare(Object o1, Object o2) {
					Map.Entry e1 = (Map.Entry) o1;
					Map.Entry e2 = (Map.Entry) o2;
					Integer first = (Integer) e1.getValue();
					Integer second = (Integer) e2.getValue();
					return first.compareTo(second);
				}
			});
			Iterator it = as.iterator();

			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				System.out.println(entry.getKey() + " : " + entry.getValue());
				x = (String) entry.getKey();
			}
			return x;

		}

	}

	private class LargeDataLoadToFileOperation extends
			AsyncTask<String, String, String> {

		private ProgressDialog dialog;
		private List<List<String>> fResult;
		private List<String> availFiles;

		public LargeDataLoadToFileOperation() {
			dialog = new ProgressDialog(Login.this);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {

			String result = "";

			File common_path = encryptor.createCommonPath();
			File folder_path = new File(common_path, spCode);
			if (typeOfImport.equals("IMP")) {
				outputFile = new File(folder_path, "MSP.xml");
			} else if (typeOfImport.equals("INC")) {
				outputFile = new File(folder_path, "MSPIncremental.xml");
			} else {
				outputFile = new File(folder_path, "MSPTransfer.xml");
			}

			if (!folder_path.exists()) {
				folder_path.mkdirs();
			}

			if (!outputFile.exists()) {
				createFile(outputFile);
			} else {
				outputFile.delete(); // to refresh the file
				createFile(outputFile);
			}

			List<String> rsltCountCall = callServiceForTotalLineCount();

			result = rsltCountCall.get(0);

			int totalServiceCall = Integer.parseInt(rsltCountCall.get(1));

			if (totalServiceCall != 0 && result.equals("success")) {

				// while (returnResult.equals("In Progress")) {
				for (int i = 0; i < totalServiceCall; i++) {

					try {
						SoapObject request = new SoapObject(NAMESPACE_TEST,
								METHOD_GET_XML);
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						request.addProperty("spCode", spCode);
						request.addProperty("size", i);
						request.addProperty("type", typeOfImport);

						envelope.dotNet = true;// to handle .net services
												// asmx/aspx
						envelope.setOutputSoapObject(request);

						HttpTransportSE ht = new HttpTransportSE(URL_IMPORT_A
								+ setting.getHhSetting_mspserverpath()
								+ URL_IMPORT_B);
						ht.debug = true;

						ht.call(SOAP_ACTION_GETXML, envelope);
						SoapPrimitive resultString = (SoapPrimitive) envelope
								.getResponse();

						BufferedWriter buf = new BufferedWriter(new FileWriter(
								outputFile, true));
						buf.append(resultString.toString());

						if (i == (totalServiceCall - 1)) {
							result = "success";
						} else {
							buf.append("\n");
						}

						buf.close();

					} catch (SocketTimeoutException e) {
						e.printStackTrace();
						result = "connection timeout";
						break; // to break for loop
					} catch (IOException e) {
						result = "input error";
						e.printStackTrace();
						break; // to break for loop
					} catch (XmlPullParserException e) {
						result = "error";
						e.printStackTrace();
						break; // to break for loop
					} catch (Exception e) {
						Log.e("tag", "error", e);
						result = "error";
						break; // to break for loop
					}

				}
			} else {
				if (result.equals("success")) {
					result = "nodata";
				}
			}

			if (result.equals("success")) {
				// int fileLineCount = getFileLineCount(outputFile);
				// System.out.println("File line count" + fileLineCount);

				fResult = writeFile(outputFile);

				List<String> resultList = fResult.get(0);

				availFiles = fResult.get(1);

				result = resultList.get(0);
			}

			return result;

		}

		@Override
		protected void onPostExecute(final String result) {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			if (result.equals("success")) {
				if (typeOfImport.equals("IMP")) {
					if (checkDatabase()) {
						File root_path = Environment
								.getExternalStorageDirectory();
						final File folder_path = new File(
								root_path.getAbsoluteFile() + "/"
										+ "Android/AMSP/" + spCode);
						dialog.cancel();
						sFile = folder_path + "/" + "MSP.xml";
						java.io.File file = new java.io.File(sFile);
						if (file.exists()) {
							doAlertToOverWriteDB(folder_path);
						}

					} else {
						supporter.setTempSalesPersonCode(spCode); // to set
																	// sales
																	// person
																	// code
																	// in
																	// publicData
																	// preference
						String[] mlistFile = new String[availFiles.size()];

						for (int i = 0; i < availFiles.size(); i++) {
							mlistFile[i] = availFiles.get(i);
						}

						new DataLoadToDBOperation().execute(mlistFile); // to
																		// start
																		// db
																		// load
																		// operation
					}
				} else if (typeOfImport.equals("INC")) {
					String[] mlistFile = new String[availFiles.size()];

					for (int i = 0; i < availFiles.size(); i++) {
						mlistFile[i] = availFiles.get(i);
					}

					new DataLoadToIncrementalTransferOperation()
							.execute(mlistFile);
				} else {

					String[] mInventorylistFile = new String[availFiles.size()];

					for (int i = 0; i < availFiles.size(); i++) {
						mInventorylistFile[i] = availFiles.get(i);
					}

					new DataLoadToInventoryOperation()
							.execute(mInventorylistFile);
				}

			} else if (result.equals("nosd")) {
				toastMsg.showToast(Login.this, "Sd card required");
			} else if (result.equals("connection timeout")) {
				toastMsg.showToast(Login.this, "Unable to connect server");
				encryptor.deleteFileFolder(spCode);
			} else if (result.equals("error in file creation")) {
				toastMsg.showToast(Login.this, "File creation failed");
				encryptor.deleteFileFolder(spCode);
			} else if (result.equals("nodata")) {
				toastMsg.showToast(Login.this, "No Data Available");
				encryptor.deleteFileFolder(spCode);
			} else if (result.equals("Parsing error")) {
				toastMsg.showToast(Login.this, "Error during parsing the data");
				encryptor.deleteFileFolder(spCode);
			} else if (result.equals("error")) {
				toastMsg.showToast(Login.this,
						"Error in Data Transfer, Contact Administrator");
				encryptor.deleteFileFolder(spCode);
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Accessing data file");
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			this.dialog.setMessage(values[0]);
		}
	}

	private class DataLoadToDBOperation extends
			AsyncTask<String, String, String> {

		private ProgressDialog dialog;

		public DataLoadToDBOperation() {
			dialog = new ProgressDialog(Login.this);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String result = encryptor.loadData(dbHelper, spCode, params,
						Login.this);

				
				dbHelper.openReadableDatabase();
				setting = dbHelper.getSettingData();
				dbHelper.closeDatabase();
				String service = setting.getHhSetting_datasyncservice();
				if (!service.equals("Email")) {
					SoapObject soapObject = new SoapObject(NAMESPACE,
							METHOD_DELETE_TXT);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					soapObject.addProperty("spcode", spCode);

					envelope.dotNet = true;
					envelope.setOutputSoapObject(soapObject);

					HttpTransportSE httpTransport = new HttpTransportSE(URL_PART_A
							+ setting.getHhSetting_mspserverpath() + URL_PART_B);
					httpTransport.debug = true;

					httpTransport.call(SOAP_ACTION_DELETETXT, envelope);
				}
				
				

				return result;

			}

			catch (Exception e) {
				Log.e("tag", "error", e);
				String result = "error";
				return result;
			}
		}

		@Override
		protected void onPostExecute(final String result) {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			if (result.equals("success")) {

				mlistCompany = loadListOfCmpny();

				cmpnySpnAdapter = new ArrayAdapter<String>(Login.this,
						android.R.layout.simple_dropdown_item_1line,
						mlistCompany);
				spnCompany.setAdapter(cmpnySpnAdapter);

				// encryptor.moveFile(spCode, "Old Imports");
				encryptor.deleteFileFolder(spCode);

				String isOverwrite = supporter.getOverwriteDB();
				if (isOverwrite.equals("yes")) {
					supporter.setOverwriteDB("no");
				}

				// showImportSummaryAlert();

				toastMsg.showToast(Login.this, "Data loaded successfully");

				// to update setting with spcode and company when db is
				// maintained in uninstall
				dbHelper.openWritableDatabase();
				HhSetting upSetting = new HhSetting();
				upSetting.setHhSetting_spCode(supporter
						.getTempSalesPersonCode());
				dbHelper.updateSettingInImport(upSetting);
				dbHelper.closeDatabase();

			} else if (result.equals("nosd")) {
				toastMsg.showToast(Login.this, "Sd card required");
			} else if (result.equals("salesPerson not valid")) {
				toastMsg.showToast(Login.this, "Enter Valid Salesperson Code");
			} else {
				toastMsg.showToast(Login.this, "Error");
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Please wait until the data is loaded");
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}
	}

	private void createFile(File outputFile) {
		try {
			outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class DataLoadToInventoryOperation extends
			AsyncTask<String, String, String> {

		private ProgressDialog dialog;

		public DataLoadToInventoryOperation() {
			dialog = new ProgressDialog(Login.this);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				cmpnyNo = lstCompany.get(spnCompany.getSelectedItemPosition())
						.getCompany_number();
				String result = encryptor.loadInventoryData(dbHelper, spCode,
						cmpnyNo, params, Login.this);
				return result;
			} catch (Exception e) {
				Log.e("tag", "error", e);
				String result = "error";
				return result;
			}
		}

		@Override
		protected void onPostExecute(final String result) {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			if (result.equals("success")) {

				// encryptor.moveFile(spCode, "Old Imports");
				encryptor.deleteFileFolder(spCode);

				toastMsg.showToast(Login.this,
						"Inventory Data loaded successfully");

			} else if (result.equals("nosd")) {
				toastMsg.showToast(Login.this, "Sd card required");
			} else if (result.equals("salesPerson not valid")) {
				toastMsg.showToast(Login.this, "Enter Valid Salesperson Code");
			} else {
				toastMsg.showToast(Login.this, "Error");
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Please wait until the data is loaded");
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}
	}

	private class DataLoadToIncrementalTransferOperation extends
			AsyncTask<String, String, String> {

		private ProgressDialog dialog;

		public DataLoadToIncrementalTransferOperation() {
			dialog = new ProgressDialog(Login.this);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String result = encryptor.loadIncremetalData(dbHelper, spCode,
						params, Login.this);
				return result;
			} catch (Exception e) {
				Log.e("tag", "error", e);
				String result = "error";
				return result;
			}
		}

		@Override
		protected void onPostExecute(final String result) {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			if (result.equals("success")) {

				mlistCompany.clear();
				cmpnySpnAdapter.clear();

				mlistCompany = loadListOfCmpny();

				cmpnySpnAdapter = new ArrayAdapter<String>(Login.this,
						android.R.layout.simple_dropdown_item_1line,
						mlistCompany);
				cmpnySpnAdapter.notifyDataSetChanged();

				spnCompany.setAdapter(cmpnySpnAdapter);

				// encryptor.moveFile(spCode, "Old Imports");
				encryptor.deleteFileFolder(spCode);

				toastMsg.showToast(Login.this,
						"Incremental Data loaded successfully");

			} else if (result.equals("nosd")) {
				toastMsg.showToast(Login.this, "Sd card required");
			} else if (result.equals("salesPerson not valid")) {
				toastMsg.showToast(Login.this, "Enter Valid Salesperson Code");
			} else {
				toastMsg.showToast(Login.this, "Error");
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Please wait until the data is loaded");
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}
	}

	public List<String> callServiceForTotalLineCount() {

		List<String> resultList = new ArrayList<String>();

		int totalLineCount = 0;
		String result = "";

		try {
			SoapObject request = new SoapObject(NAMESPACE_TEST,
					METHOD_GET_COUNT);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("spCode", spCode);
			request.addProperty("Type", typeOfImport);
			envelope.dotNet = true;// to handle .net services asmx/aspx
			envelope.setOutputSoapObject(request);

			HttpTransportSE ht = new HttpTransportSE(URL_IMPORT_A
					+ setting.getHhSetting_mspserverpath() + URL_IMPORT_B);
			ht.debug = true;

			ht.call(SOAP_ACTION_GETCOUNT, envelope);

			SoapPrimitive resultObj = (SoapPrimitive) envelope.getResponse();
			totalLineCount = Integer.parseInt(resultObj.toString());

			result = "success";

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = "connection timeout";
		} catch (IOException e) {
			result = "input error";
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			result = "error";
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("tag", "error", e);
			result = "error";
		}

		resultList.add(result);
		resultList.add(totalLineCount + "");

		return resultList;
	}

	public List<List<String>> writeFile(File outputFile) {

		List<List<String>> fResultLst = new ArrayList<List<String>>();

		FileLoader fileLoader = new FileLoader(Login.this, dbHelper, encryptor,
				spCode);

		InputStream inputStream;
		try {
			inputStream = new FileInputStream(outputFile);
			fResultLst = fileLoader.parseDocument(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return fResultLst;
	}

	public void showImportSummaryAlert() {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Login.this);
		alertDialog.setTitle("Information");
		alertDialog.setIcon(R.drawable.tick);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						supporter.simpleNavigateTo(ImportSummary.class);
					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog
				.setMessage("Data loaded successfully.\nDo you want view import summary ?");
		//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	/* Info Dialog creation */
	@Override
	protected Dialog onCreateDialog(int id) {

		dialog = null;

		switch (id) {
		case MENU_SETTING_DIALOG_ID:
			dialog = new Dialog(Login.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.info_layout);
			dialog.setCancelable(true);

			dialog.setCanceledOnTouchOutside(true);

			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});

			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
				}
			});

			// Prepare ListView in dialog
			dialog_ListView = (ListView) dialog.findViewById(R.id.lst_Dialog);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.drawable.dialog_list_item, R.id.txt_Dlg_ListDetails,
					listSettings);

			dialog_ListView.setAdapter(adapter);
			dialog_ListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					if (position == 0) {

						supporter.simpleNavigateTo(Settings.class);

					} else if (position == 1) {
						if (checkDatabase()) {
							supporter.simpleNavigateTo(AdminLogin.class);
						} else {
							toastMsg.showToast(Login.this,
									"Please import data and proceed");
						}

					} else if (position == 2) {
						showDeviceId("Device Id is " + devId);
					}

					dialog.dismiss();
				}
			});

			break;
		}

		return dialog;
	}

	protected void showDeviceId(String msg) {
		AlertDialog.Builder alertMemory = new AlertDialog.Builder(this);
		alertMemory.setTitle("Information");
		alertMemory.setIcon(R.drawable.tick);
		alertMemory.setCancelable(false);
		alertMemory.setMessage(msg);
		alertMemory.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertMemory.show();
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					Login.this);
			alertDialog.setTitle("Confirmation");
			alertDialog.setIcon(R.drawable.warning);
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							//closeAllActivities();
							finishAffinity();
						}
					});

			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alertDialog.setMessage("Do you want to exit?");
			//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
