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
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;
import com.zebra.android.printer.ZebraPrinterLanguageUnknownException;

public class TransDetailDialog extends Dialog {

	private MspDBHelper dbhelpher;
	private Supporter supporter;
	private HhSetting setting;
	private List<Devices> deviceList;
	private ListView blListView;
	private Activity context = null;
	private ArrayAdapter<Devices> devAdapter;
	private List<HhTran01> transAllList;
	private TempItem temp;
	private String referNum;
	private String devAddress;
	private String devName;
	private ToastMessage toastMsg;

	// currency format variables
	private String tranTot;
	private String taxValue;
	private String tranDiscount;
	private String preTaxValue;
	private String netTotValue;
	private String prePayValue;
	private double netTotal;
	private double prepayAmt;
	private String strTranQty;

	// printing format variables
	private ZebraPrinterConnection zebraPrinterConnection;
	private ZebraPrinter printer;
	private String transType;
	private int day;
	private int month;
	private int year;
	private String strDate;

	private int y;
	private String returnType;
	private int noOfPrint;
	private String printerModel;
	private String prntCompName;
	private String prntCompAddrs;
	private String prntCompState;
	private String prntCompCountry;
	private String prntCompPhoneNo;
	private String prntTitle;
	private String prntDate;
	private String prntCusNo;
	private String prntCusName;
	private String prntCusAddrs;
	private String prntShipLoc;
	private String prntOrdNumTitle;
	private String prntSalesPerson;
	private String prntQty;
	private String prntItmNo;
	private String prntDesc;
	private String prntUnitPrz;
	private String prntUOM;
	private String prntExtPrz;
	private String prntSubTot;
	private String prntVAT;
	private String prntTotAmt;
	private String prntLessPrepay;
	private String prntDisc;
	private String prntAmtDue;
	private int tempSize;
	private int tempPixSpace;
	private DecimalFormat formats;

	private int xQtyno;
	private int xItemNo;
	private int xDescNo;
	private int xUomNo;
	private PrinterLanguage printerLanguage;

	public TransDetailDialog(Activity context, String refNo,
			MspDBHelper dbhelpher, Supporter supporter, List<Devices> deviceList) {
		super(context);
		this.context = context;
		this.referNum = refNo;
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
		transAllList = dbhelpher.getTransactionList(referNum,
				supporter.getCompanyNo());
		setting = dbhelpher.getSettingData();
		dbhelpher.closeDatabase();

		// calculation of listview items price & tax details...
		calculateTransDetails(transAllList);

		formats = new DecimalFormat("0.00");

		devAdapter = new DeviceArrayAdapter(context, deviceList);
		blListView.setAdapter(devAdapter);

		blListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				if (deviceList.size() != 0) {
					final Devices devce = deviceList.get(pos);
					devAddress = devce.getDeviceIP();
					devName = devce.getName();

					new PrinterConnectOperation().execute();

				}
				TransDetailDialog.this.dismiss();
			}
		});

		// printing section...
		HhTran01 tran = transAllList.get(0);
		transType = tran.getHhTran_transType();
		if (transType.equals("S")) {
			prntTitle = "SALES ORDER";
			prntOrdNumTitle = "Sales Order Number : ";
		} else if (transType.equals("I") || transType.equals("CN")) {
			prntTitle = "VAT INVOICE";
			prntOrdNumTitle = "VAT Invoice Number : ";
		} else {
			prntTitle = "QUOTATION";
			prntOrdNumTitle = "Quotation Number : ";
		}

		// getting date from trans table
		day = tran.getHhTran_transDay();
		month = tran.getHhTran_transMonth();
		year = tran.getHhTran_transYear();
		strDate = supporter.getStringDate(year, month, day);

		String cmpNo = supporter.getCompanyNo();
		dbhelpher.openReadableDatabase();
		HhCompany company = dbhelpher.getCompanyData(cmpNo);
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

		// getting date for printing ...
		prntDate = "Date : " + strDate;

		// getting customer number...
		prntCusNo = tran.getHhTran_customerNumber();

		// getting customer name...
		prntCusName = tran.getHhTran_editedcustomername();

		// getting customer address...
		dbhelpher.openReadableDatabase();
		HhCustomer01 customer = dbhelpher.getCustomerData(prntCusNo,
				supporter.getCompanyNo());
		dbhelpher.closeDatabase();
		prntCusAddrs = customer.getHhCustomer_address();

		// getting ship to location name...
		String tempShipto = tran.getHhTran_shipToCode();
		if (tempShipto.equals("")) {
			prntShipLoc = "DEFAULT";
		} else {
			prntShipLoc = tempShipto;
		}

		// getting salesperson name...
		prntSalesPerson = tran.getHhTran_salesPerson();

		// getting subtotal value...
		prntSubTot = formats.format(Double.parseDouble(tranTot));
		System.out.println("Sub Total : " + prntSubTot);

		// getting tax value...
		prntVAT = formats.format(Double.parseDouble(taxValue));
		System.out.println("VAT : " + prntVAT);

		// setting total amount value...
		double tempSubTot = Double.parseDouble(prntSubTot);
		double tempVat = Double.parseDouble(prntVAT);
		double totAmt = tempSubTot + tempVat;
		prntTotAmt = formats.format(totAmt);
		System.out.println("Total Amt : " + prntTotAmt);

		// getting less prepayment value...
		prntLessPrepay = formats.format(Double.parseDouble(prePayValue));
		System.out.println("Prepayment : " + prntLessPrepay);

		// getting discount value...
		prntDisc = formats.format(Double.parseDouble(tranDiscount));
		System.out.println("Discount : " + prntDisc);

		// setting amount due value...
		double tempLessPrepay = Double.parseDouble(prntLessPrepay);
		double tempDisc = Double.parseDouble(prntDisc);
		double amtDue = totAmt - (tempLessPrepay + tempDisc);
		prntAmtDue = formats.format(amtDue);

		System.out.println("AmtDue : " + prntAmtDue);

	}// end of oncreate method...

	// to calculate resultant transaction values
	private void calculateTransDetails(List<HhTran01> transItemList2) {

		double transTotal = 0.00;
		int transQty = 0;
		double tax = 0.00;
		double preTax = 0.00;
		double netTot = 0.00;
		double discount = 0.00;

		for (int i = 0; i < transItemList2.size(); i++) {
			HhTran01 item = transItemList2.get(i);

			String type = item.getHhTran_transType();
			double cDiscount = item.getHhTran_discValue();
			if (type.equals("CN")) {

				int cQty = item.getHhTran_qty();
				transQty = transQty - cQty;

				double cTot = item.getHhTran_price() * cQty;
				transTotal = transTotal - cTot;

				discount = discount - cDiscount;

				double cTax = item.getHhTran_tax();
				tax = tax - cTax;

			} else {
				int cQty = item.getHhTran_qty();
				transQty = transQty + cQty;

				double cTot = item.getHhTran_price() * cQty;
				transTotal = transTotal + cTot;

				discount = discount + cDiscount;

				double cTax = item.getHhTran_tax();
				tax = tax + cTax;
			}

		}

		tranTot = supporter.getCurrencyFormat(transTotal);
		tranDiscount = supporter.getCurrencyFormat(discount);
		taxValue = supporter.getCurrencyFormat(tax);
		preTaxValue = supporter.getCurrencyFormat(preTax);

		netTot = transTotal + tax + preTax - discount;
		netTotal = netTot;
		netTotValue = supporter.getCurrencyFormat(netTotal);
		strTranQty = transQty + "";

		dbhelpher.openReadableDatabase();
		prepayAmt = dbhelpher.getPrepayAmount(referNum,
				supporter.getCompanyNo());
		dbhelpher.closeDatabase();
		prePayValue = supporter.getCurrencyFormat(prepayAmt);
	}

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
				LogfileCreator.appendLog("In doInBackground method: "
						+ e.getMessage());
				result = "error";
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
				boolean isDataSentSuccess = sendDataToPrinter();
				if (isDataSentSuccess) {
					if (context.getClass().equals(Transaction.class)
							|| context.getClass().equals(Prepayment.class)) {
						supporter.simpleNavigateTo(MainMenu.class);
					}
				} else {
					doPrintErrorAlert();
					LogfileCreator.appendLog("Printing Not Successful");
				}

			} else {
				doPrintErrorAlert();
			}

		}// end of PostExecute method...

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
						if (context.getClass().equals(Transaction.class)
								|| context.getClass().equals(Prepayment.class)) {
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

	public void disconnect() {
		try {
			if (zebraPrinterConnection != null) {
				zebraPrinterConnection.close();
			}
		} catch (ZebraPrinterConnectionException e) {
			toastMsg.showToast(context, "COMM Error! Disconnected.");
		}
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
			LogfileCreator.appendLog("In sendDataToPrinter method: "
					+ e.getMessage());
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
			byte[] compName = getCompName();
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
			writeText(prntDate, 12, y);
			// writeText(prntTime, 435, y);

			y += 25;
			writeText(prntOrdNumTitle + referNum, 12, y);

			y += 25;
			writeText("Customer Number : " + prntCusNo, 12, y);

			y += 25;
			writeText("Customer Name : " + prntCusName, 12, y);

			y += 25;
			writeText("Customer Address : " + prntCusAddrs, 12, y);

			y += 25;
			writeText("Ship To Location : " + prntShipLoc, 12, y);

			y += 25;
			writeText("Reference Number : " + referNum, 12, y);

			y += 25;
			writeText("Salesperson : " + prntSalesPerson, 12, y);

			y += 25;
			writeText("", 12, y);
		} else if (printerModel.equals("4-inch")) {
			y += 25;
			writeText(prntDate, 24, y);
			// writeText(prntTime, 435, y);

			y += 32;
			writeText(prntOrdNumTitle + referNum, 24, y);

			y += 32;
			writeText("Customer Number : " + prntCusNo, 24, y);

			y += 32;
			writeText("Customer Name : " + prntCusName, 24, y);

			y += 32;
			writeText("Customer Address : " + prntCusAddrs, 24, y);

			y += 32;
			writeText("Ship To Location : " + prntShipLoc, 24, y);

			y += 32;
			writeText("Reference Number : " + referNum, 24, y);

			y += 32;
			writeText("Salesperson : " + prntSalesPerson, 24, y);

			y += 32;
			writeText("", 24, y);
		}

	}

	private void writeTableTitleDetail() {
		// List view ... (dont change it..)
		if (printerModel.equals("3-inch")) {
			y += 25;
			writeText("Qty.", 12, y);
			writeText("Item No.", 64, y);
			writeText("Description", 165, y);
			writeText("Unit Price", 306, y);
			writeText("UOM", 435, y);
			writeText("Ext Price", 490, y);
		} else if (printerModel.equals("4-inch")) {
			y += 32;
			writeText("Qty.", 24, y);
			writeText("Item No.", 92, y);
			writeText("Description", 240, y);
			writeText("Unit Price", 432, y);
			writeText("UOM", 560, y);
			writeText("Ext Price", 664, y);
		}

	}

	private void writeLine() {
		if (printerModel.equals("3-inch")) {
			y += 15;
			writeText("----", 12, y);
			writeText("----------", 64, y);
			writeText("-------------", 165, y);
			writeText("-----------", 306, y);
			writeText("----", 435, y);
			writeText("----------", 490, y);
		} else if (printerModel.equals("4-inch")) {
			y += 24;
			writeLines("------", 24, y);
			writeLines("-------------", 92, y);
			writeLines("-----------------", 240, y);
			writeLines("---------------", 432, y);
			writeLines("--------", 560, y);
			writeLines("--------------", 664, y);
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
				xQtyno = 12;
				xItemNo = 64;
				xDescNo = 165;
				xUomNo = 435;
			} else if (printerModel.equals("4-inch")) {
				xQtyno = 24;
				xItemNo = 92;
				xDescNo = 240;
				xUomNo = 560;
			}

			int serialPosition = 0;
			int exp = 0;
			int finaly = 0;

			for (int i = 0; i < transAllList.size(); i++) {
				y = finaly + exp;
				if(i!=0)
				y += 32;
				serialPosition += y;

				HhTran01 tran = transAllList.get(i);

				returnType = tran.getHhTran_transType();
				int tempQty = tran.getHhTran_qty();
				if (returnType.equals("CN")) {
					prntQty = "-" + tempQty;
				} else {
					prntQty = "" + tempQty;
				}

				prntItmNo = tran.getHhTran_itemNumber();

				// getting item description
				dbhelpher.openReadableDatabase();
				/*temp=dbhelpher.getTempData();
				String location=temp.getTemp_location();*/
				HhItem01 item = dbhelpher.getItemDataByNum(prntItmNo,
						supporter.getCompanyNo());
				dbhelpher.closeDatabase();
				prntDesc = item.getHhItem_description();

				// getting item unitprize
				double tempUnitPrz = tran.getHhTran_price();
				prntUnitPrz = formats.format(tempUnitPrz);

				// getting item UOM
				prntUOM = tran.getHhTran_uom();

				// getting item Ext. Price...
				double tempExtPrz = tempUnitPrz * (double) tempQty;
				// double tempExtPrz = tran.getHhTran_extPrice();
				if (returnType.equals("CN")) {
					prntExtPrz = "-" + formats.format(tempExtPrz);
				} else {
					prntExtPrz = formats.format(tempExtPrz);
				}

				// for itemQty printing...
				appendLineToPrint(strBuff, prntQty, xQtyno, y);

				// for itemNo printing...
				if (prntItmNo.length() <= 8) {
					appendLineToPrint(strBuff, prntItmNo, xItemNo, y);
					finaly = y;
				} else {
					int len1 = 0;
					String tempstr = "";
					int tempy = 0;
					Boolean chk = false;

					tempy = y;
					tempstr = prntItmNo;
					while (chk == false) {
						if (tempstr.length() > 8) {
							appendLineToPrint(strBuff, tempstr.substring(0, 7),
									xItemNo, y);
							len1 = tempstr.length();
							if (len1 > 16) {
								len1 = 7;
							} else {
								len1 = tempstr.length() - 8;
							}

							tempstr = tempstr.substring(8, 8 + len1);
							y += 25;
						} else {
							appendLineToPrint(strBuff, tempstr, xItemNo, y);
							chk = true;
						}
					}
					finaly = y;
					y = tempy;
				}

				// for description printing
				int strLength = prntDesc.length();
				int index = 0;
				int strPrint = 15;
				String strTemp = "";
				int tempy = 0;
				tempy = y;
				while (index != strLength) {
					strTemp = prntDesc;
					if (strPrint < (strLength - index)) {
						appendLineToPrint(strBuff,
								strTemp.substring(index, strPrint + index),
								xDescNo, y);
						index = index + 15;
						y += 25;
					} else {
						appendLineToPrint(strBuff,
								strTemp.substring(index, strLength), xDescNo, y);
						index = index + (strLength - index);
						// y += 25;
					}
				}

				if (finaly < y) {
					finaly = y;
				}
				serialPosition = y;
				y = tempy;

				if (printerModel.equals("3-inch")) {
					// for unit price printing...
					int tempSize1 = prntUnitPrz.length();
					int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8
														// dots.
					appendLineToPrint(strBuff, prntUnitPrz,
							306 + (80 - tempPixSpace1), y);

					// for UOM printing...
					appendLineToPrint(strBuff, prntUOM, xUomNo, y);

					// for Ext. price printing...
					int tempSize2 = prntExtPrz.length();
					int tempPixSpace2 = tempSize2 * 8; // Each pixel have 8
														// dots.
					appendLineToPrint(strBuff, prntExtPrz,
							488 + (80 - tempPixSpace2), y);
				} else if (printerModel.equals("4-inch")) {
					// for unit price printing...
					int tempSize1 = prntUnitPrz.length();
					int tempPixSpace1 = tempSize1 * 8;
					appendLineToPrint(strBuff, prntUnitPrz,
							432 + (80 - tempPixSpace1), y);// 512

					// for UOM printing...
					appendLineToPrint(strBuff, prntUOM, xUomNo, y);

					// for Ext. price printing...
					int tempSize2 = prntExtPrz.length();
					int tempPixSpace2 = tempSize2 * 8;
					appendLineToPrint(strBuff, prntExtPrz,
							664 + (80 - tempPixSpace2), y);
				}

			}

			if (printerModel.equals("3-inch")) {
				y += 25;
				appendLineToPrint(strBuff, "", 12, y);

				y += 25;
				appendLineToPrint(strBuff,
						"----------------------------------", 290, y);

				y += 25;
				appendLineToPrint(strBuff, "Sub Total", 290, y);
				// for subtotal printing...
				tempSize = prntSubTot.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntSubTot,
						488 + (80 - tempPixSpace), y);

				y += 25;
				appendLineToPrint(strBuff, "VAT", 290, y);
				// for tax printing...
				tempSize = prntVAT.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntVAT, 488 + (80 - tempPixSpace),
						y);

				y += 25;
				appendLineToPrint(strBuff, "Total Amount", 290, y);
				// for total amount printing...
				tempSize = prntTotAmt.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntTotAmt,
						488 + (80 - tempPixSpace), y);

				y += 25;
				appendLineToPrint(strBuff, "Prepayment", 290, y);

				// for less prepayment printing...
				tempSize = prntLessPrepay.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntLessPrepay,
						488 + (80 - tempPixSpace), y);

				y += 25;
				appendLineToPrint(strBuff, "Discount", 290, y);
				// for discount printing...
				tempSize = prntDisc.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntDisc, 488 + (80 - tempPixSpace),
						y);

				y += 25;
				appendLineToPrint(strBuff, "Amount Due", 290, y);
				// for tax printing...
				tempSize = prntAmtDue.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntAmtDue,
						488 + (80 - tempPixSpace), y);

				y += 25;
				appendLineToPrint(strBuff, "", 12, y);

				y += 25;
				appendLineToPrint(strBuff, "Delivered by/Signature", 12, y);
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
				String str="! 0 200 200 " + y + " 1\r\n" +"SETBOLD 1\r\n";
				strBuff.insert(0, str );
				strBuff.append("PRINT\r\n\n");


			} else if (printerModel.equals("4-inch")) {
				y += 32;
				appendLineToPrint(strBuff, "", 12, y);

				y += 32;
				appendLineToPrint(strBuff, "------------------------", 432, y);

				y += 32;
				appendLineToPrint(strBuff, "Sub Total", 432, y);
				// for subtotal printing...
				tempSize = prntSubTot.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntSubTot,
						664 + (80 - tempPixSpace), y);

				y += 32;
				appendLineToPrint(strBuff, "VAT", 432, y);
				// for tax printing...
				tempSize = prntVAT.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntVAT, 664 + (80 - tempPixSpace),
						y);

				y += 32;
				appendLineToPrint(strBuff, "Total Amount", 432, y);
				// for total amount printing...
				tempSize = prntTotAmt.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntTotAmt,
						664 + (80 - tempPixSpace), y);

				y += 32;
				appendLineToPrint(strBuff, "Prepayment", 432, y);

				// for less prepayment printing...
				tempSize = prntLessPrepay.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntLessPrepay,
						664 + (80 - tempPixSpace), y);

				y += 32;
				appendLineToPrint(strBuff, "Discount", 432, y);
				// for discount printing...
				tempSize = prntDisc.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntDisc, 664 + (80 - tempPixSpace),
						y);

				y += 25;
				appendLineToPrint(strBuff, "Amount Due", 432, y);
				// for tax printing...
				tempSize = prntAmtDue.length();
				tempPixSpace = tempSize * 8; // Each pixel have 8 dots.
				appendLineToPrint(strBuff, prntAmtDue,
						664 + (80 - tempPixSpace), y);

				y += 32;
				appendLineToPrint(strBuff, "", 12, y);

				y += 32;
				appendLineToPrint(strBuff, "Delivered by/Signature", 24, y);
				appendLineToPrint(strBuff, "Received by/Signature", 440, y);

				y += 32;
				appendLineToPrint(strBuff, "", 12, y);
				y += 32;
				appendLineToPrint(strBuff, "", 12, y);
				y += 32;
				appendLineToPrint(strBuff, "", 12, y);

				y += 32;
				appendLineToPrint(strBuff,
						"--------------------------------------------------",
						12, y);
				y += 32;
				appendLineToPrint(strBuff, "", 12, y);
				y += 32;
				appendLineToPrint(strBuff, "", 12, y);
				String str="! 0 200 200 " + y + " 1\r\n";// +"SETBOLD 1\r\n";
				strBuff.insert(0, str );
				strBuff.append("PRINT\r\n\n");
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
		if (printerModel.equals("3-inch")) {/*
			String prntFormat = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		*/
			String prntFormat = "T 0 2 " + " " + x + " "  + y + " "
					+ strValue+"\r\n";

			strBuff = strBuff.append(prntFormat);

		} else if (printerModel.equals("4-inch")) {/*

			String prntFormat = "! U1 SETLP 5 0 24 " + "! U1 SETBOLD 0 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		*/
			
			String prntFormat = "T 5 0 " + " " + x + " "  + y + " "
					+ strValue+"\r\n";

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

	// printing company name in center...
	private byte[] getCompName() {
		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {

			if (printerModel.equals("3-inch")) {

				String cpclConfigLabel1 = "! 0 200 200 40 1"
						+ "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 0 10 15"
						+ " " + prntCompName + "\r\n" + "PRINT\r\n";
				configLabel1 = cpclConfigLabel1.getBytes();

			} else if (printerModel.equals("4-inch")) {

				String cpclConfigLabel1 = "! 0 200 200 60 1"
						+ "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 2 10 15"
						+ " " + prntCompName + "\r\n" + "PRINT\r\n";
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

	// printing Title for transaction...
	private byte[] getConfigTitle() {
		// PrinterLanguage printerLanguage =
		// printer.getPrinterControlLanguage();

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {
			String cpclConfigLabel1 = "! 0 200 200 45 1" + "ON-FEED IGNORE\r\n"
					+ "CENTER\r\n" + "T 5 0 10 15" + " " + prntTitle + "\r\n"
					+ "PRINT\r\n";
			configLabel1 = cpclConfigLabel1.getBytes();
		}
		return configLabel1;
	}

}
