package com.mobilesalesperson.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.EditText;

import com.mobilesalesperson.controller.ObjectSerializer;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Supporter {

	private Activity curActivity;
	private SharedPreferences publicData;
	private SharedPreferences transdata;
	private SharedPreferences refnum;
	private SharedPreferences commonData;
	private SharedPreferences shippingDetails;
	private SharedPreferences receiptData;
	private SharedPreferences orderData;
	private SharedPreferences creditNoteData;
	private SharedPreferences receiptListPref;
	private MspDBHelper dbHelper;
	private Cursor cursor;
	private SQLiteDatabase db;

	public Supporter(Activity activity, MspDBHelper dbHelper) {
		curActivity = activity;
		publicData = curActivity.getSharedPreferences("publicData",
				Context.MODE_PRIVATE);
		transdata = curActivity.getSharedPreferences("transdata",
				Context.MODE_PRIVATE);
		refnum = curActivity.getSharedPreferences("refno",
				Context.MODE_PRIVATE);
		commonData = curActivity.getSharedPreferences("commonData",
				Context.MODE_PRIVATE);
		shippingDetails = curActivity.getSharedPreferences("shippingDetails",
				Context.MODE_PRIVATE);
		orderData = curActivity.getSharedPreferences("OrderFile",
				Context.MODE_PRIVATE);
		creditNoteData = curActivity.getSharedPreferences("CreditNoteFile",
				Context.MODE_PRIVATE);
		receiptData = curActivity.getSharedPreferences("ReceiptFile",
				Context.MODE_PRIVATE);
		receiptListPref = curActivity.getSharedPreferences("prefReceiptList",
				Context.MODE_PRIVATE);
		this.dbHelper = dbHelper;
	}

	// to set device id
	public void setDeviceId(String devId) {
		Editor ed = publicData.edit();
		ed.putString("devId", devId);
		ed.commit();
	}
	public void setcommentsdata(String comments){
		Editor ed = transdata.edit();
		ed.putString("comments",comments);
		ed.commit();
	}

	public String getcommentsdata() {
		String comments = "";
		comments = transdata.getString("comments","");
		return comments;
	}
	public void setrefno(String refno){
		Editor ed = refnum.edit();
		ed.putString("refno",refno);
		ed.commit();
	}

	public String getrefno() {
		String refno = "";
		refno = refnum.getString("refno","");
		return refno;
	}

	// to get device id
	public String getDeviceId() {
		String devId = "0000";
		devId = publicData.getString("devId", "0000");
		return devId;
	}

	// to set device id
	public void setTheme(String theme) {
		Editor ed = publicData.edit();
		ed.putString("theme", theme);
		ed.commit();
	}

	// to get device id
	public String getTheme() {
		String theme = "0000";
		theme = publicData.getString("theme", "BS");
		return theme;
	}

	public void setCompanyNo(String cmpnyNo) {
		Editor ed = commonData.edit();
		ed.putString("company", cmpnyNo);
		ed.commit();
	}

	public String getCompanyNo() {
		String cmpny = "";
		cmpny = commonData.getString("company", "");
		return cmpny;
	}

	public void setTempCompanyNo(String cmpnyNo) {
		Editor ed = publicData.edit();
		ed.putString("company", cmpnyNo);
		ed.commit();
	}

	public String getTempCompanyNo() {
		String cmpny = "";
		cmpny = publicData.getString("company", "");
		return cmpny;
	}

	public void setTempSalesPersonCode(String spCode) {
		Editor ed = publicData.edit();
		ed.putString("spCode", spCode);
		ed.commit();
	}

	public String getTempSalesPersonCode() {
		String cmpny = "";
		cmpny = publicData.getString("spCode", "");
		return cmpny;
	}

	public void setSalesPerson(String spName) {
		Editor ed = commonData.edit();
		ed.putString("salespersoname", spName);
		ed.commit();
	}

	public String getSalesPerson() {
		String spName = "";
		spName = commonData.getString("salespersoname", "");
		return spName;
	}

	public void setCompanyCurrency(String cmpCurrency) {
		Editor ed = commonData.edit();
		ed.putString("companycurrency", cmpCurrency);
		ed.commit();
		;
	}

	public String getCompanyCurrency() {
		String cmpCurrency = "";
		cmpCurrency = commonData.getString("companycurrency", "");
		return cmpCurrency;
	}

	public void setMainPageViewType(String view) {
		Editor ed = publicData.edit();
		ed.putString("mainpageviewtype", view);
		ed.commit();
	}

	public String getMainPageViewType() {
		String view = "";
		view = publicData.getString("mainpageviewtype", "grid");
		return view;
	}

	public void setOverwriteDB(String isOverwrite) {
		Editor ed = publicData.edit();
		ed.putString("isOverwrite", isOverwrite);
		ed.commit();
	}

	public String getOverwriteDB() {
		String overWrite = "";
		overWrite = publicData.getString("isOverwrite", "");
		return overWrite;
	}

	// to set mode
	public void setMode(String mode) {
		Editor ed = commonData.edit();
		ed.putString("mode", mode);
		ed.commit();
	}

	// to get mode
	public String getMode() {
		String mode = "";
		mode = commonData.getString("mode", "");
		return mode;
	}

	// to get totype
	public String getSaleType() {
		String type = "";
		type = commonData.getString("totype", "");
		return type;
	}

	// to set totype
	public void setSaleType(String totype) {
		Editor ed = commonData.edit();
		ed.putString("totype", totype);
		ed.commit();
	}

	// to set LogedIn permission
	public void setLogedIn(boolean isLogedIn) {
		Editor ed = commonData.edit();
		ed.putBoolean("isLogedIn", isLogedIn);
		ed.commit();
	}

	// to get LogedIn permission
	public boolean isLogedIn() {
		boolean isLogedIn = false;
		isLogedIn = commonData.getBoolean("isLogedIn", false);
		return isLogedIn;
	}

	// simple navigation
	public void simpleNavigateTo(Class<?> cls) {
		Intent intent = new Intent(curActivity, cls);
		curActivity.startActivity(intent);
		// curActivity.finish();
	}

	// to get date format
	public String getDateFormat() {
		HhSetting dateformat = dbHelper.getSettingData();
		String df = dateformat.getHhSetting_dateformat();
		return df;
	}

	// to get date formatter
	public SimpleDateFormat getDateFormatter() {

		SimpleDateFormat sFormat = new SimpleDateFormat(getDateFormat(),
				Locale.ENGLISH);

		return sFormat;
	}
	
	// to check sdcard availability
		public static boolean isSdPresent() {

			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);

		}

	// to get application common working folder
		public static File getAppCommonPath() {

			File file = null;

			if (isSdPresent()) {
				File root_path = Environment.getExternalStorageDirectory();

				File miscs_common_path = new File(root_path.getAbsoluteFile() + "/"
						+ "Android/AMSP");

				if (!miscs_common_path.exists()) {
					miscs_common_path.mkdirs();
				}

				file = miscs_common_path;
			}

			return file;

		}
	// to convert string date to calendar
	public Calendar stringDateToCalender(String strOrdShipDate) {
		Date sDate = null;
		try {
			sDate = getDateFormatter().parse(strOrdShipDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar calender = new GregorianCalendar();
		calender.setTime(sDate);

		return calender;
	}

	// to update editor with current selected date
	public String getStringDate(int mYear, int mMonth, int mDay) {
		String date = "";


		dbHelper.openReadableDatabase();
		HhSetting dateformat = dbHelper.getSettingData();
		dbHelper.closeDatabase();
		String df = dateformat.getHhSetting_dateformat();
		if (df.equals("dd/MM/yyyy")) {
			StringBuilder date1 = new StringBuilder().append(mDay).append("/")
					.append(mMonth).append("/").append(mYear).append(" ");
			date = date1.toString();

		} else if (df.equals("MM/dd/yyyy")) {
			StringBuilder date2 = new StringBuilder().append(mMonth)
					.append("/").append(mDay).append("/").append(mYear)
					.append(" ");
			date = date2.toString();
		} else if (df.equals("yyyy/MM/dd")) {
			StringBuilder date2 = new StringBuilder().append(mYear).append("/")
					.append(mMonth).append("/").append(mDay).append(" ");
			date = date2.toString();
		} else if (df.equals("yyyy/dd/MM")) {
			StringBuilder date2 = new StringBuilder().append(mYear).append("/")
					.append(mDay).append("/").append(mMonth).append(" ");
			date = date2.toString();
		}

		return date;
	}

	// default data format
	public String getDefaultStringDateFormat(int mYear, int mMonth, int mDay) {
		String date = "";
		StringBuilder date1 = new StringBuilder().append(mDay).append("/")
				.append(mMonth).append("/").append(mYear).append(" ");
		date = date1.toString();

		return date;
	}

	// to set all prize values to this decimal format...
	public String getCurrencyFormat(double value) {
		String deci = "";

		dbHelper.openReadableDatabase();
		HhSetting deciformat = dbHelper.getSettingData();
		dbHelper.closeDatabase();

		String df = deciformat.getHhSetting_decimalformat();
		if (df.equals("6")) {
			double temp = (double) (value * 1e6) / 1e6;
			deci = String.format("%.6f", temp);
		} else if (df.equals("5")) {
			double temp = (double) (value * 1e5) / 1e5;
			deci = String.format("%.5f", temp);
		} else if (df.equals("4")) {
			double temp = (double) (value * 1e4) / 1e4;
			deci = String.format("%.4f", temp);
		} else if (df.equals("3")) {
			double temp = (double) (value * 1e3) / 1e3;
			deci = String.format("%.3f", temp);
		} else if (df.equals("2")) {
			double temp = (double) (value * 1e2) / 1e2;
			deci = String.format("%.2f", temp);
		}

		return deci;
	}

	// to set all prize values to this decimal format is DB Open...
	public String getCurrencyFormatWithDbOpen(double value) {
		String deci = "";

		HhSetting deciformat = dbHelper.getSettingData();

		String df = deciformat.getHhSetting_decimalformat();
		if (df.equals("6")) {
			double temp = (long) (value * 1e6) / 1e6;
			deci = String.format("%.6f", temp);
		} else if (df.equals("5")) {
			double temp = (long) (value * 1e5) / 1e5;
			deci = String.format("%.5f", temp);
		} else if (df.equals("4")) {
			double temp = (long) (value * 1e4) / 1e4;
			deci = String.format("%.4f", temp);
		} else if (df.equals("3")) {
			double temp = (long) (value * 1e3) / 1e3;
			deci = String.format("%.3f", temp);
		} else if (df.equals("2")) {
			double temp = (long) (value * 1e2) / 1e2;
			deci = String.format("%.2f", temp);
		}

		return deci;
	}

	// to set all quantity values to this decimal format...
	public String getQtyFormat(double value) {
		DecimalFormat formatter = new DecimalFormat("0");
		String prz = formatter.format(value);
		return prz;
	}

	public void addShippingDetails(String custNo, String custName,
			String strLocDet, String strViaCode, String strShipDate) {
		Editor ed = shippingDetails.edit();
		ed.putString("customerNumber", custNo);
		ed.putString("customerName", custName);
		ed.putString("shipLoc", strLocDet);
		ed.putString("shipViaCode", strViaCode);
		ed.putString("shipDate", strShipDate);
		ed.commit();

	}

	public void setCreditAllowConformation(String conf) {
		Editor ed = shippingDetails.edit();
		ed.putString("isProceed", conf);
		ed.commit();
	}

	public String getCreditAllowConformation() {
		String isProceed = "";
		isProceed = shippingDetails.getString("isProceed", "");
		return isProceed;
	}

	public void setPriceList(String priceList) {
		Editor ed = shippingDetails.edit();
		ed.putString("priceList", priceList);
		ed.commit();
	}

	public String getPriceList() {
		String priceList = "";
		priceList = shippingDetails.getString("priceList", "");
		return priceList;
	}

	public List<String> loadInDateFormatStaticData() {
		List<String> listContent2 = new ArrayList<String>();

		listContent2.add("dd/MM/yyyy");
		listContent2.add("MM/dd/yyyy");
		listContent2.add("yyyy/MM/dd");
		listContent2.add("yyyy/dd/MM");

		return listContent2;
	}

	public List<String> loadInSelectionTypes() {
		List<String> listContent2 = new ArrayList<String>();

		listContent2.add("Orders");
		listContent2.add("Invoices");
		listContent2.add("Receipts");

		return listContent2;
	}

	public List<String> loadPrinterName() {
		List<String> listPrinterName = new ArrayList<String>();
		listPrinterName.add("Zebra");
		return listPrinterName;
	}

	public List<String> loadPrinterModel() {
		List<String> listPrinterModel = new ArrayList<String>();
		listPrinterModel.add("3-inch");
		listPrinterModel.add("4-inch");
		listPrinterModel.add("PDF");
		return listPrinterModel;
	}

	public List<String> loadInSelectionSettings() {
		List<String> listContent2 = new ArrayList<String>();

		listContent2.add("Settings");
		listContent2.add("Sequence Number");
		listContent2.add("Device Info");

		return listContent2;
	}

	public List<String> loadInTransferSyncStaticData() {
		List<String> listContent2 = new ArrayList<String>();

		listContent2.add("Wireless Sync");
		listContent2.add("Email");
		listContent2.add("Wired Sync");

		return listContent2;
	}

	public List<String> loadInDiscountStaticData() {
		List<String> listContent2 = new ArrayList<String>();

		listContent2.add("Amount");
		listContent2.add("Percentage");

		return listContent2;
	}

	public List<String> loadDecimalFormatData() {
		List<String> listContent2 = new ArrayList<String>();

		listContent2.add("6");
		listContent2.add("5");
		listContent2.add("4");
		listContent2.add("3");
		listContent2.add("2");

		return listContent2;
	}

	public boolean calCreditLimit(String cusNum, String cmpnyNo) {

		boolean isCreditLimitExceeds = false;

		double result = 0.0;

		dbHelper.openReadableDatabase();
		double creditLimit = dbHelper.getCustomerCreditLimit(cusNum, cmpnyNo);
		double pendingBalance = dbHelper.getPendingBalance(cusNum, cmpnyNo);
		dbHelper.closeDatabase();

		result = creditLimit - pendingBalance;

		if (result <= 0) {
			isCreditLimitExceeds = true;
		}

		return isCreditLimitExceeds;
	}

	public boolean isAmountValid(String value) {

		boolean isValid = false;

		if (value.matches("^(-?[0-9]+[\\.\\,][0-9]{1,5})?[0-9]*$")) {
			double numValue = Double.parseDouble(value);

			if (numValue > 0) {
				isValid = true;
			}
		}

		return isValid;
	}

	public boolean isAmountValidWithZero(String value) {

		boolean isValid = false;

		if (value.matches("^(-?[0-9]+[\\.\\,][0-9]{1,5})?[0-9]*$")) {
			double numValue = Double.parseDouble(value);

			if (numValue >= 0) {
				isValid = true;
			}
		}

		return isValid;
	}

	// to convert image into string
	public String encodeImageToString(Bitmap bitmap) {
		String strImg = "";
		byte[] bMapArray = dbHelper.convertBitmapToByte(bitmap);
		strImg = Base64.encodeToString(bMapArray, Base64.DEFAULT);
		return strImg;
	}

	// to convert string format image into bitmap image
	public Bitmap decodeStringToImage(String imageString) {
		byte[] bMapArray = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bMapArray, 0,
				bMapArray.length);
		return bitmap;
	}

	// to clear shared Preference
	public void clearPreference(SharedPreferences shippingDetails,
			SharedPreferences entryDetails) {

		Editor ed1 = shippingDetails.edit();
		ed1.clear();
		ed1.commit();

		Editor ed2 = entryDetails.edit();
		ed2.clear();
		ed2.commit();
	}

	// to clear sign shared Preference
	public void clearSignPreference(SharedPreferences signPreference) {

		Editor ed = signPreference.edit();
		ed.clear();
		ed.commit();
	}

	// /// Export Saving Receipts Preference /////

	// to save partial export receipt data..
	public void saveReceiptInPreference(String docNum, int chkd) {
		Editor ed = receiptData.edit();
		ed.putInt(docNum, chkd);
		ed.commit();
	}

	// to remove partial export receipt data..
	public void removeReceiptFromPreference(String docNum) {
		Editor ed = receiptData.edit();
		ed.remove(docNum);
		ed.commit();
	}

	// to save checked select all check box for receipt
	public void setSelectAllCheckedForReceipt() {
		Editor ed = receiptData.edit();
		ed.putInt("selectAllForReceipt", 1);
		ed.commit();
	}

	public void setSelectAllNotCheckedForReceipt() {
		Editor ed = receiptData.edit();
		ed.putInt("selectAllForReceipt", -1);
		ed.commit();
	}

	public int getSelectAllCheckedForReceipt() {
		int chked = -1;
		chked = receiptData.getInt("selectAllForReceipt", -1);
		return chked;
	}

	// /// End of Export Saving Receipts Preference /////

	// /// Export Saving CreditNote Preference /////

	// to save checked select all check box for credit note
	public void setSelectAllCheckedForCN() {
		Editor ed = creditNoteData.edit(); // changed
		ed.putInt("selectAllForCN", 1);
		ed.commit();
	}

	public void setSelectAllNotCheckedForCN() {
		Editor ed = creditNoteData.edit(); // changed
		ed.putInt("selectAllForCN", -1);
		ed.commit();
	}

	public int getSelectAllCheckedForCN() {
		int chked = -1;
		// chked = receiptData.getInt("selectAllForCN", -1); // changed on
		// 27/9/2013 before
		chked = creditNoteData.getInt("selectAllForCN", -1); // changed on
																// 27/9/2013
		return chked;
	}

	public void saveCreditNoteInPreference(String refNumbr, int chkd) {
		Editor ed = creditNoteData.edit();
		ed.putInt(refNumbr, chkd);
		ed.commit();
	}

	public void removeCreditNoteFromPreference(String refNumbr) {
		Editor ed = creditNoteData.edit();
		ed.remove(refNumbr);
		ed.commit();
	}

	// /// End of Export Saving CreditNote Preference /////

	// /// End of Export Saving Orders Preference /////

	// to save checked select all check box for order
	public void setSelectAllCheckedForOrder() {
		Editor ed = orderData.edit(); // changed
		ed.putInt("selectAllForOrder", 1);
		ed.commit();
	}

	public void setSelectAllNotCheckedForOrder() {
		Editor ed = orderData.edit(); // changed
		ed.putInt("selectAllForOrder", -1);
		ed.commit();
	}

	public int getSelectAllCheckedForOrder() {
		int chked = -1; // changed
		chked = orderData.getInt("selectAllForOrder", -1);
		return chked;
	}

	// to save partial export orders data..
	public void saveOrderInPreference(String ordNum, int chkd) {
		Editor ed = orderData.edit();
		ed.putInt(ordNum, chkd);
		ed.commit();
	}

	// to remove partial export orders data..
	public void removeOrderFromPreference(String ordNum) {
		Editor ed = orderData.edit();
		ed.remove(ordNum);
		ed.commit();
	}

	// /// End of Export Saving Orders Preference /////

	// to clear order and receipt partial export preference
	public void clearPreference(SharedPreferences myPrefs) {
		Editor edit = myPrefs.edit();
		edit.clear();
		edit.commit();
	}

	// to clear shared Preference
	public void clearCommonPreference() {

		Editor edtComDate = commonData.edit();
		edtComDate.clear();
		edtComDate.commit();

	}

	// to get receipt list from preference
	public Map<Integer, HhReceipt01> getPrefReceiptList() {
		Map<Integer, HhReceipt01> receiptList = (HashMap<Integer, HhReceipt01>) ObjectSerializer
				.deserialize(receiptListPref.getString("receiptsList",
						ObjectSerializer
								.serialize(new HashMap<Integer, HhReceipt01>())));
		return receiptList;
	}

	// to refresh preference in receipt page after changes
	public void refreshPrefReceiptList(Map<Integer, HhReceipt01> receiptList) {
		Editor editor = receiptListPref.edit();
		editor.putString("receiptsList",
				ObjectSerializer.serialize((Serializable) receiptList));
		editor.commit();
	}

	public List<Devices> getPairedDevices() {
		// paired bluetooth device added in devlist...
		List<Devices> devList = new ArrayList<Devices>();

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();

			if (pairedDevices.size() > 0) {

				Devices devPojo = null;
				for (BluetoothDevice device : pairedDevices) {
					devPojo = new Devices();
					devPojo.setName(device.getName());
					devPojo.setDeviceIP(device.getAddress());
					devList.add(devPojo);
				}
			}
		}

		return devList;

	}
	
	public String getShipDate(String orderNo, String itemNo)
	{
		String shipDate = null;
		System.out.println("Order Number is "+ orderNo);
		HhTran01 tran=new HhTran01();
		String query="select expshipday, expshipmonth, expshipyear from hhTran01 where referencenumber='" + orderNo + "' and itemnumber='" + itemNo +"'";
		cursor=db.rawQuery(query, null);
		if(!itemNo.equals(""))
		{
		while(cursor.moveToNext())
		{
			tran.setHhTran_expShipDay(cursor.getInt(0));
			tran.setHhTran_expShipMonth(cursor.getInt(1));
			tran.setHhTran_expShipYear(cursor.getInt(2));
			
		}
		int day=cursor.getInt(0);
		int month=cursor.getInt(1);
		int year=cursor.getInt(2);
		shipDate=getStringDate(year, month, day);
		}
		return shipDate;
	}

}
