package com.mobilesalesperson.controller;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.Supporter;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;
import com.zebra.android.printer.ZebraPrinterLanguageUnknownException;

public class ReceiptDetailDialog extends Dialog {

	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private ListView blListView;
	private Activity context = null;
	private List<Devices> deviceList;
	private ArrayAdapter<Devices> devAdapter;
	private List<HhReceipt01> receiptAllList;
	private String reciptNum;
	private String devName;
	private String devAddress;
	private ToastMessage toastMsg;

	private int day;
	private int month;
	private int year;
	private String date;

	// printing format variables
	private ZebraPrinterConnection zebraPrinterConnection;
	private ZebraPrinter printer;
	private int y;

	private String prntCompName;
	private String prntCompAddrs;
	private String prntCompState;
	private String prntCompCountry;
	private String prntCompPhoneNo;
	private String prntTitle;
	private String prntDate;
	private String prntCusNo;
	private String prntCusName;
	private String prntReciptNo;
	private int sNo;
	private String prntInvNo;
	private String prntApplyAmt;
	private String prntPayMode;
	private String prntUnapplied;
	private double applyAmt;
	private double reciptTotAmt;
	private DecimalFormat formats;
	private int noOfPrint;
	private String printerModel;
	private int xSno;
	private int xApply;
	private int xInvNo;
	private int xAmt;
	private HhSetting setting;
	private PrinterLanguage printerLanguage;

	public ReceiptDetailDialog(Activity context, String receiptNo,
			MspDBHelper dbhelpher, Supporter supporter, List<Devices> deviceList) {
		super(context);
		this.context = context;
		this.reciptNum = receiptNo;
		this.dbhelpher = dbhelpher;
		this.supporter = supporter;
		this.deviceList = deviceList;

		setTitle("Select Printer");
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.print_list_layout);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tick);
		setCancelable(false);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		toastMsg = new ToastMessage();

		blListView = (ListView) findViewById(R.id.lst_Device);

		dbhelpher.openReadableDatabase();
		receiptAllList = dbhelpher.getReceiptListofData(reciptNum,
				supporter.getCompanyNo());
		dbhelpher.closeDatabase();

		formats = new DecimalFormat("0.00");

		devAdapter = new DeviceArrayAdapter(context, deviceList);
		blListView.setAdapter(devAdapter);

		blListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (deviceList.size() != 0) {
					final Devices devce = deviceList.get(pos);
					devName = devce.getName();
					devAddress = devce.getDeviceIP();

					new PrinterConnectOperation().execute();
				}
				ReceiptDetailDialog.this.dismiss();
			}
		});

		String cmpNo = supporter.getCompanyNo();
		dbhelpher.openReadableDatabase();
		HhCompany company = dbhelpher.getCompanyData(cmpNo);
		setting = dbhelpher.getSettingData();
		dbhelpher.closeDatabase();

		noOfPrint = setting.getHhSetting_numcopiesprint();
		printerModel = setting.getHhSetting_printerModel();
		prntCompName = company.getCompany_name();
		prntCompAddrs = company.getCompany_address() + ","
				+ company.getCompany_city();
		prntCompState = company.getCompany_state() + " - "
				+ company.getCompany_zip();
		prntCompCountry = company.getCompany_country();
		prntCompPhoneNo = company.getCompany_phone();
		prntTitle = "RECEIPT";

		HhReceipt01 recipt = receiptAllList.get(0);

		// getting date from receipt table
		day = recipt.getHhReceipt_receiptday();
		month = recipt.getHhReceipt_receiptmonth();
		year = recipt.getHhReceipt_receiptyear();

		date = supporter.getStringDate(year, month, day);
		// getting date for printing ...
		prntDate = date;

		prntCusNo = recipt.getHhReceipt_customernumber();
		prntCusName = recipt.getHhReceipt_customername();
		prntPayMode = recipt.getHhReceipt_receipttype();
		prntReciptNo = recipt.getHhReceipt_receiptnumber();
		prntUnapplied = "" + recipt.getHhReceipt_receiptunapplied();

	}// end of oncreate method...

	private class PrinterConnectOperation extends
			AsyncTask<Void, String, String> {

		private ProgressDialog dialog;

		public PrinterConnectOperation() {
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(Void... params) {

			String result = "";
			try {
				printer = connect();
				if (printer != null) {
					result = "success";
				} else {
					disconnect();
					result = "error";
				}

				return result;
			} catch (Exception e) {
				disconnect();
				Log.e("tag", "error", e);
				result = "error";
			}

			return result;

		}

		@Override
		protected void onPostExecute(final String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (result.equals("success")) {
				boolean isDataSentSuccess = sendDataToPrinter();
				if (isDataSentSuccess) {
					if (context.getClass().equals(Receipt.class)) {
						supporter.simpleNavigateTo(MainMenu.class);
					}
				} else {
					doPrintErrorAlert();
				}

			} else {
				doPrintErrorAlert();
			}
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Connecting...");
			this.dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			this.dialog.setMessage(values[0]);
		}
	}

	private void doPrintErrorAlert() {
		AlertDialog.Builder alertMemory = new AlertDialog.Builder(context);
		alertMemory.setTitle("Warning");
		alertMemory.setIcon(R.drawable.warning);
		alertMemory.setCancelable(false);
		alertMemory.setMessage("Printer connection problem");
		alertMemory.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (context.getClass().equals(Receipt.class)) {
							supporter.simpleNavigateTo(MainMenu.class);
						}
						dialog.cancel();

					}
				});
		alertMemory.show();
	}

	// printer connection method....
	public ZebraPrinter connect() {
		zebraPrinterConnection = null;
		zebraPrinterConnection = new BluetoothPrinterConnection(devAddress);
		try {
			zebraPrinterConnection.open();
		} catch (ZebraPrinterConnectionException e) {
			Log.e("Connection Error", "Comm Error! Disconnecting");
		}

		ZebraPrinter printer = null;

		if (zebraPrinterConnection.isConnected()) {
			try {
				printer = ZebraPrinterFactory
						.getInstance(zebraPrinterConnection);
				printerLanguage = printer.getPrinterControlLanguage().CPCL;
				Log.i("Printer Language is", printerLanguage.toString());
			} catch (ZebraPrinterConnectionException e) {
				toastMsg.showToast(context,
						"Connection : Unknown Printer Language.");
				printer = null;
				DemoSleeper.sleep(1000);
				disconnect();
			} catch (ZebraPrinterLanguageUnknownException e) {
				toastMsg.showToast(context,
						"Language : Unknown Printer Language.");
				printer = null;
				DemoSleeper.sleep(1000);
				disconnect();
			}
		}

		return printer;
	}

	public boolean sendDataToPrinter() {
		boolean resultSent = false;
		try {
			for (int i = 0; i < noOfPrint; i++) {
				writeHeaderSpace();
				writeHeader();
				writeDetail();
				writeTableTitleDetail();
				writeLine();
				writeItemDetail();
			}
			if (zebraPrinterConnection instanceof BluetoothPrinterConnection) {
				String friendlyName = ((BluetoothPrinterConnection) zebraPrinterConnection)
						.getFriendlyName();
			}
			resultSent = true;
			return resultSent;
		} catch (Exception e) {
			Log.e("ZebraPrinterConnectionException", e.toString());
			resultSent = false;
			return resultSent;
		} finally {
			disconnect();
		}
	}

	private void writeHeaderSpace() {
		y = 0;
		y += 25;
		writeText("", 12, y);
	}

	private void writeHeader() {
		try {
			byte[] compName = getCompAddrs(prntCompName);
			byte[] title = getConfigTitle();

			zebraPrinterConnection.write(compName);

			if (!prntCompAddrs.equals("")) {
				byte[] compAddr = getCompAddrs(prntCompAddrs);
				zebraPrinterConnection.write(compAddr);
			}

			if (!prntCompState.equals("")) {
				byte[] compState = getCompAddrs(prntCompState);
				zebraPrinterConnection.write(compState);
			}

			// if u need print country name add below code...
			/*
			 * if (!prntCompCountry.equals("")) { byte[] compCountry =
			 * getCompAddrs(prntCompCountry);
			 * zebraPrinterConnection.write(compCountry); }
			 */

			if (!prntCompPhoneNo.equals("")) {
				byte[] compPhoneNo = getCompAddrs("Phone No. "
						+ prntCompPhoneNo);
				zebraPrinterConnection.write(compPhoneNo);
			}

			zebraPrinterConnection.write(title);
		} catch (ZebraPrinterConnectionException e) {
			Log.e("ZebraPrinterConnectionException", e.toString());
		}

	}

	private void writeDetail() {
		if (printerModel.equals("3-inch")) {
			y += 25;
			writeText("Date : " + prntDate, 12, y);

			y += 25;
			writeText("", 12, y);

			y += 25;
			writeText("Customer Number : " + prntCusNo, 12, y);

			y += 25;
			writeText("Customer Name : " + prntCusName, 12, y);

			y += 55;
			writeText("Receipt Number : " + prntReciptNo, 12, y);

			y += 25;
			writeText("", 12, y);
		} else if (printerModel.equals("4-inch")) {
			y += 32;
			writeText("Date : " + prntDate, 40, y);

			y += 32;
			writeText("", 40, y);

			y += 32;
			writeText("Customer Number : " + prntCusNo, 40, y);

			y += 32;
			writeText("Customer Name : " + prntCusName, 40, y);

			y += 32;
			writeText("Receipt Number : " + prntReciptNo, 40, y);

			y += 32;
			writeText("", 40, y);
		}

	}

	private void writeTableTitleDetail() {
		if (printerModel.equals("3-inch")) {
			y += 25;
			writeText("SNo.", 12, y);
			writeText("Apply", 64, y);
			writeText("Invoice No.", 176, y);
			writeText("Applied Amount", 402, y);
		} else if (printerModel.equals("4-inch")) {
			y += 32;
			writeText("SNo.", 40, y);
			writeText("Apply", 132, y);
			writeText("Invoice No.", 228, y);
			writeText("Applied Amount", 512, y);
		}

	}

	private void writeLine() {
		if (printerModel.equals("3-inch")) {
			y += 15;
			writeText("----", 12, y);
			writeText("--------", 64, y);
			writeText("----------------------", 144, y);
			writeText("-----------------", 402, y);
		} else if (printerModel.equals("4-inch")) {
			y += 24;
			writeLines("--------", 40, y);
			writeLines("---------", 132, y);
			writeLines("---------------------------", 228, y);
			writeLines("-----------------------------", 512, y);
		}

	}

	private void writeItemDetail() {

		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		StringBuffer strBuff = new StringBuffer();

		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {

			if (printerModel.equals("3-inch")) {
				xSno = 14;
				xApply = 72;
				xInvNo = 144;
				xAmt = 530;
			} else if (printerModel.equals("4-inch")) {
				xSno = 48;
				xApply = 140;
				xInvNo = 228;
				xAmt = 512;
			}

			int serialPosition = 0;
			int exp = 0;
			int finaly = y;
			sNo = 1;
			reciptTotAmt = 0.00;

			for (int i = 0; i < receiptAllList.size(); i++) {
				y = finaly + exp;
				y += 25;
				serialPosition += y;

				HhReceipt01 recipt = receiptAllList.get(i);

				// for serial no printing...
				appendLineToPrint(strBuff, "" + sNo, xSno, y);

				// for apply yes to printing...
				appendLineToPrint(strBuff, "Yes", xApply, y);

				prntInvNo = recipt.getHhReceipt_docnumber();
				prntApplyAmt = supporter.getCurrencyFormat(recipt
						.getHhReceipt_appliedamount());

				applyAmt = recipt.getHhReceipt_appliedamount();

				// for itemNo printing...
				if (prntInvNo.length() <= 32) {
					appendLineToPrint(strBuff, prntInvNo, xInvNo, y);
					finaly = y;
				} else {
					int len1 = 0;
					String tempstr = "";
					int tempy = 0;
					Boolean chk = false;

					tempy = y;
					tempstr = prntInvNo;
					while (chk == false) {
						if (tempstr.length() > 32) {
							appendLineToPrint(strBuff,
									tempstr.substring(0, 31), xInvNo, y);
							len1 = tempstr.length();
							if (len1 > 64) {
								len1 = 31;
							} else {
								len1 = tempstr.length() - 32;
							}

							tempstr = tempstr.substring(32, 32 + len1);
							y += 25;
						} else {
							appendLineToPrint(strBuff, tempstr, xInvNo, y);
							chk = true;
						}
					}
					finaly = y;
					y = tempy;
				}

				if (printerModel.equals("3-inch")) {
					// for Ext. price printing...
					int tempSize2 = prntApplyAmt.length();
					int tempPixSpace2 = tempSize2 * 8;
					appendLineToPrint(strBuff, prntApplyAmt,
							416 + (80 - tempPixSpace2), y);

				} else if (printerModel.equals("4-inch")) {
					// for Ext. price printing...
					int tempSize2 = prntApplyAmt.length();
					int tempPixSpace2 = tempSize2 * 8;
					appendLineToPrint(strBuff, prntApplyAmt,
							526 + (80 - tempPixSpace2), y);
				}

				sNo++;
				reciptTotAmt = reciptTotAmt + applyAmt;
			}
			if (printerModel.equals("3-inch")) {
				y += 25;
				appendLineToPrint(strBuff, "", 12, y);

				y += 25;
				appendLineToPrint(strBuff, "Payment Mode : " + prntPayMode, 12,
						y);

				y += 25;
				appendLineToPrint(
						strBuff,
						"Receipt Unapplied : "
								+ supporter.getCurrencyFormat(Double
										.parseDouble(prntUnapplied)), 12, y);

				y += 25;
				appendLineToPrint(
						strBuff,
						"Receipt Total : "
								+ supporter.getCurrencyFormat(reciptTotAmt),
						12, y);

				y += 25;
				appendLineToPrint(strBuff, "", 12, y);

				y += 25;
				appendLineToPrint(strBuff, "", 12, y);

				y += 25;
				appendLineToPrint(strBuff, "Received by/Signature", 340, y);

				y += 25;
				appendLineToPrint(strBuff, "", 12, y);
				y += 25;
				appendLineToPrint(strBuff, "", 12, y);
				y += 25;
				appendLineToPrint(strBuff, "", 12, y);

				y += 25;
				appendLineToPrint(
						strBuff,
						"---------------------------------------------------------------------",
						12, y);
				y += 25;
				appendLineToPrint(strBuff, "", 12, y);
				y += 25;
				appendLineToPrint(strBuff, "", 12, y);
			} else if (printerModel.equals("4-inch")) {
				y += 32;
				appendLineToPrint(strBuff, "", 24, y);

				y += 32;
				appendLineToPrint(strBuff, "Payment Mode : " + prntPayMode, 40,
						y);

				y += 32;
				appendLineToPrint(
						strBuff,
						"Receipt Unapplied : "
								+ supporter.getCurrencyFormat(Double
										.parseDouble(prntUnapplied)), 40, y);

				y += 32;
				appendLineToPrint(
						strBuff,
						"Receipt Total : "
								+ supporter.getCurrencyFormat(reciptTotAmt),
						40, y);

				y += 32;
				appendLineToPrint(strBuff, "", 24, y);

				y += 32;
				appendLineToPrint(strBuff, "", 24, y);

				y += 32;
				appendLineToPrint(strBuff, "Received by/Signature", 432, y);

				y += 32;
				appendLineToPrint(strBuff, "", 24, y);
				y += 32;
				appendLineToPrint(strBuff, "", 24, y);

				y += 32;
				appendLineToPrint(strBuff,
						"--------------------------------------------------",
						12, y);
				y += 32;
				appendLineToPrint(strBuff, "", 24, y);
				y += 32;
				appendLineToPrint(strBuff, "", 24, y);
			}

			try {
				zebraPrinterConnection.write(strBuff.toString().getBytes());
			} catch (ZebraPrinterConnectionException e) {
				e.printStackTrace();
			}

		}

	}

	private StringBuffer appendLineToPrint(StringBuffer strBuff,
			String strValue, int x, int y) {
		if (printerModel.equals("3-inch")) {
			String prntFormat = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		} else if (printerModel.equals("4-inch")) {

			String prntFormat = "! U1 SETLP 5 0 24 " + "! U1 SETBOLD 0 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		}

		return strBuff;
	}

	// common method for printing data...
	private void writeText(String str, int x, int y) {
		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {
			if (printerModel.equals("3-inch")) {
				String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
						+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
						+ str;

				configLabel1 = data.getBytes();
			} else if (printerModel.equals("4-inch")) {
				String data = "! U1 SETLP 5 0 24 " + "! U1 SETBOLD 0 "
						+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
						+ str;

				configLabel1 = data.getBytes();
			}

		}
		try {
			zebraPrinterConnection.write(configLabel1);
		} catch (ZebraPrinterConnectionException e) {
			e.printStackTrace();
		}

	}

	// common method for printing data...
	private void writeLines(String str, int x, int y) {
		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {
			if (printerModel.equals("3-inch")) {
				String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
						+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
						+ str;

				configLabel1 = data.getBytes();
			} else if (printerModel.equals("4-inch")) {
				String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
						+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
						+ str;

				configLabel1 = data.getBytes();
			}

		}
		try {
			zebraPrinterConnection.write(configLabel1);
		} catch (ZebraPrinterConnectionException e) {
			e.printStackTrace();
		}

	}

	// printing Title for transaction...
	private byte[] getConfigTitle() {
		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {
			if (printerModel.equals("3-inch")) {
				String cpclConfigLabel1 = "! 0 200 200 45 1"
						+ "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 0 10 15"
						+ " " + prntTitle + "\r\n" + "PRINT\r\n";
				configLabel1 = cpclConfigLabel1.getBytes();
			} else if (printerModel.equals("4-inch")) {
				String cpclConfigLabel1 = "! 0 200 200 60 1"
						+ "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 2 10 15"
						+ " " + prntTitle + "\r\n" + "PRINT\r\n";
				configLabel1 = cpclConfigLabel1.getBytes();
			}

		}
		return configLabel1;
	}

	// printing company address in center...
	private byte[] getCompAddrs(String cmpAddrs) {
		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {
			String cpclConfigLabel1 = "! 0 200 200 40 1" + "ON-FEED IGNORE\r\n"
					+ "CENTER\r\n" + "T 5 0 10 15" + " " + cmpAddrs + "\r\n"
					+ "PRINT\r\n";
			configLabel1 = cpclConfigLabel1.getBytes();
		}
		return configLabel1;
	}

	public void disconnect() {
		try {
			if (zebraPrinterConnection != null) {
				zebraPrinterConnection.close();
			}
		} catch (ZebraPrinterConnectionException e) {
			toastMsg.showToast(context, "COMM Error! Disconnected.");
		}
	}
}
