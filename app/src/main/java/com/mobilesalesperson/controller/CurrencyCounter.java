package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.CurrencyCount;
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;
import com.zebra.android.printer.ZebraPrinterLanguageUnknownException;

public class CurrencyCounter extends AppBaseActivity{

	private EditText mEditTxt_CurCountTotal;
	private EditText mEditTxt_CurCountGrandTotal;
	private ListView mListView_CurrCount;
	private EditText mEditTxt_CurCountCheque;
	private EditText mEditTxt_CurCountCreditCard;
	private EditText mEditTxt_CurCountDebitCard;
	private Button mBtn_CurCountGrandTotal;
	private Button mBtn_CurCountReset;
	
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private CurrencyCounterSettings mSettings;
	private ToastMessage toastMsg;
	private Devices devPojo;
	private HhSetting setting;
	
	private SharedPreferences mPref_CurCount;
	private ZebraPrinterConnection zebraPrinterConnection;
	private ZebraPrinter printer;
	private PrinterLanguage printerLanguage;
	private Dialog dialog;
	private Map<String, String> mapPrint;
	private Map<String, String> mapCurrenyPrint;
	private Map<String, String> mapCoinPrint;
	private List<String> mList_Currency;
	private List<String> mList_Coin;
	private List<CurrencyCount> mListCurCoin;
	private ListView mListView_Printer;
	private List<Devices> devList;
	private String currencyName;
	private String currencySymbol;
	private String coinName;
	private String coinConversion;
	private String devAddress;
	private String devName;
	private String mStrKey;
	private String mStrValue;
	private String printerModel;
	static final int DIALOG_ID = 0;
	private int currencyListSize;
	private int coinListSize;
	private int y;
	private int noOfPrint;
	private double dCheque;
	private double dCredit;
	private double dDebit;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.currency_count_layout);
		registerBaseActivityReceiver();
		
		findViewByID();
		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		mSettings = new CurrencyCounterSettings();
		mList_Currency = new ArrayList<String>();
		mList_Coin = new ArrayList<String>();
		mListCurCoin = new ArrayList<CurrencyCount>();
		toastMsg = new ToastMessage();
		mapPrint = new HashMap<String, String>();
		mapCurrenyPrint = new TreeMap<String, String>();
		mapCoinPrint = new TreeMap<String, String>();
		dbHelper = new MspDBHelper(this);
		setting = new HhSetting();
		
		mPref_CurCount = getSharedPreferences(mSettings.PREFERENCES_TODO, MODE_PRIVATE);
		currencyListSize = Integer.parseInt(mPref_CurCount.getString("CurrencyListSize", "0"));
		coinListSize = Integer.parseInt(mPref_CurCount.getString("CoinListSize", "0"));
				
		dbHelper.openReadableDatabase();
		setting = dbHelper.getSettingData();
		dbHelper.closeDatabase();

		noOfPrint = setting.getHhSetting_numcopiesprint();
		printerModel = setting.getHhSetting_printerModel();

		if (currencyListSize == 0 && coinListSize == 0) {
			AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
			alertUser.setTitle("Confirmation");
			alertUser.setIcon(R.drawable.warning);
			alertUser.setCancelable(false);
			alertUser.setMessage("Currency Counter data not available. Do you want to update the settings?");
			alertUser.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							supporter.simpleNavigateTo(CurrencyCounterSettings.class);
						}
					});

			alertUser.setNegativeButton("No", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							supporter.simpleNavigateTo(MainMenu.class);
						}
					});

				//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
		} else {
			currencyName = mPref_CurCount.getString("CurrencyName", "");
			currencySymbol = mPref_CurCount.getString("CurrencySymbol", "");
			coinName = mPref_CurCount.getString("CoinName", "");
			coinConversion = mPref_CurCount.getString("CoinConversion", "");
			
			mList_Currency.clear();
			mList_Coin.clear();
			mListCurCoin.clear();
			for (int i = 0; i < currencyListSize; i++) {
				String currency = mPref_CurCount.getString("Currency"+i, "");
				CurrencyCount cc = new CurrencyCount(currency, "0", "0");
				mListCurCoin.add(cc);
			}
			
			for (int i = 0; i < coinListSize; i++) {
				String coin = mPref_CurCount.getString("Coin"+i, "");
				CurrencyCount cc = new CurrencyCount(coin, "0", "0");
				mListCurCoin.add(cc);
			}
		}
		
		mListView_CurrCount.setAdapter(new CurrencyListAdapter(CurrencyCounter.this, android.R.layout.simple_list_item_1, mListCurCoin));
		
		mBtn_CurCountGrandTotal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String cheque = mEditTxt_CurCountCheque.getText().toString();
				String credit = mEditTxt_CurCountCreditCard.getText().toString();
				String debit = mEditTxt_CurCountDebitCard.getText().toString();
				String total = mEditTxt_CurCountTotal.getText().toString();
				
				if(cheque.equals("")) {
					cheque = "0";
				}
				if(credit.equals("")) {
					credit = "0";
				}
				if(debit.equals("")) {
					debit = "0";
				}
				if(total.equals("")) {
					total = "0";
				}
				
				float grandTotal = Float.parseFloat(total) + Float.parseFloat(cheque) + Float.parseFloat(credit) + Float.parseFloat(debit);
				mEditTxt_CurCountGrandTotal.setText(String.valueOf(grandTotal));
			}
		});
		
		mBtn_CurCountReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEditTxt_CurCountTotal.setText("");
				mEditTxt_CurCountGrandTotal.setText("");
				mEditTxt_CurCountCheque.setText("");
				mEditTxt_CurCountCreditCard.setText("");
				mEditTxt_CurCountDebitCard.setText("");
				
				mListCurCoin.clear();
				for (int i = 0; i < currencyListSize; i++) {
					String currency = mPref_CurCount.getString("Currency"+i, "");
					CurrencyCount cc = new CurrencyCount(currency, "0", "0");
					mListCurCoin.add(cc);
				}
				
				for (int i = 0; i < coinListSize; i++) {
					String coin = mPref_CurCount.getString("Coin"+i, "");
					CurrencyCount cc = new CurrencyCount(coin, "0", "0");
					mListCurCoin.add(cc);
				}
				mListView_CurrCount.setAdapter(new CurrencyListAdapter(CurrencyCounter.this, android.R.layout.simple_list_item_1, mListCurCoin));
			}
		});
		
		mEditTxt_CurCountCheque.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mEditTxt_CurCountGrandTotal.setText("");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mEditTxt_CurCountCreditCard.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mEditTxt_CurCountGrandTotal.setText("");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mEditTxt_CurCountDebitCard.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mEditTxt_CurCountGrandTotal.setText("");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	public void findViewByID() {
		mEditTxt_CurCountTotal = (EditText)findViewById(R.id.edtTxt_CurrCountTotal);
		mEditTxt_CurCountGrandTotal = (EditText)findViewById(R.id.edtTxt_CurrCountGrandTotal);
		mListView_CurrCount = (ListView)findViewById(R.id.listView_CurrCount);
		mEditTxt_CurCountCheque = (EditText)findViewById(R.id.edtTxt_Cheque);
		mEditTxt_CurCountCreditCard = (EditText)findViewById(R.id.edtTxt_CreditCard);
		mEditTxt_CurCountDebitCard = (EditText)findViewById(R.id.edtTxt_DebitCard);
		mBtn_CurCountGrandTotal = (Button)findViewById(R.id.btn_CurrCountGrandTotal);
		mBtn_CurCountReset = (Button)findViewById(R.id.btn_CurrCountReset);
	}
	
	public class CurrencyListAdapter extends ArrayAdapter<CurrencyCount> {

		List<CurrencyCount> mList;
		
		public CurrencyListAdapter(Context context, int textViewResourceId,
				 List<CurrencyCount> mList) {
			super(context, textViewResourceId, mList);
			this.mList = mList;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.currency_list_item, parent, false);
			
			final ItemHolder holder = new ItemHolder();
			holder.count = mList.get(position);
			
			holder.txt_Currency = (TextView)convertView.findViewById(R.id.txt_ListItemCurCountAmount);
			holder.edit_Qty = (EditText)convertView.findViewById(R.id.edtTxt_CurCountListItem);
			holder.txt_Result = (TextView)convertView.findViewById(R.id.txt_ListItemCurCountResult);
			
			
			holder.txt_Currency.setText(holder.count.getCurrency());
			holder.edit_Qty.setText(holder.count.getQuantity());
			holder.txt_Result.setText(holder.count.getResult());
			convertView.setTag(holder);
			
			holder.edit_Qty.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					float result;
					if(s.length() == 0 || s.equals("")) {
						holder.count.setQuantity("0");
						holder.count.setResult("0");
						holder.txt_Result.setText("0");
						calculateTotal(mList);
					} else {
						holder.count.setQuantity(s.toString());
						int qty = Integer.parseInt(s.toString());
						String currency = holder.count.getCurrency().toString();
						
						if(currency.contains(coinName)) {
							String[] fCur = currency.split(" ");
							float res = qty * (Integer.parseInt(fCur[0]));
							float conv = Float.parseFloat(coinConversion);
							result = res / conv;
							
							holder.txt_Result.setText(String.valueOf(result));
							holder.count.setQuantity(s.toString());
							holder.count.setResult(String.valueOf(result));
							calculateTotal(mList);
							
							if(mapPrint.containsKey(currency)) {
								mapPrint.remove(currency);
								mapPrint.put(currency, s.toString());
							} else {
								mapPrint.put(currency, s.toString());
							}
						} else {
							result = Integer.parseInt(s.toString()) * Integer.parseInt(holder.count.getCurrency().toString());
							holder.txt_Result.setText(String.valueOf(result));
							holder.count.setResult(String.valueOf(result));
							holder.count.setQuantity(s.toString());
							calculateTotal(mList);
							
							if(mapPrint.containsKey(currency)) {
								mapPrint.remove(currency);
								mapPrint.put(currency, s.toString());
							} else {
								mapPrint.put(currency, s.toString());
							}
						}
					}
				}
			});
			
			return convertView;
		}
		
		public void calculateTotal(List<CurrencyCount> list) {			
			float a = 0;
			for (int i = 0; i < list.size(); i++) {
				CurrencyCount c = list.get(i);
				a = a + Float.parseFloat(c.getResult());
				mEditTxt_CurCountTotal.setText(String.valueOf(a));
			}
		}
		
	}
	
	public static class ItemHolder {
		public CurrencyCount count;
		public TextView txt_Currency;
		public TextView txt_Result;
		public EditText edit_Qty;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.currency_counter_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.curr_count_settings:
			supporter.simpleNavigateTo(CurrencyCounterSettings.class);
				break;
		case R.id.curr_count_print:
			if(mEditTxt_CurCountGrandTotal.getText().length() == 0 || mEditTxt_CurCountGrandTotal.equals("")) {
				toastMsg.showToast(CurrencyCounter.this, "You cannot print without grand total");
			} else {
				devList = supporter.getPairedDevices();
				if (devList.size() != 0) {
					showDialog(DIALOG_ID);
				} else {
					toastMsg.showToast(CurrencyCounter.this, "Paired printer not available for printing");
				}
			}
			break;
		}
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		dialog = null;
		ArrayAdapter<Devices> devAdapter;

		switch (id) {
		case DIALOG_ID:
			dialog = new Dialog(CurrencyCounter.this);
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
			dialog.setContentView(R.layout.print_list_layout);
			dialog.setTitle("Select Printer");
			dialog.setCancelable(false);
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
					R.drawable.tick);
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
			mListView_Printer = (ListView) dialog.findViewById(R.id.lst_Device);
			devAdapter = new DeviceArrayAdapter(CurrencyCounter.this, devList);
			mListView_Printer.setAdapter(devAdapter);

			mListView_Printer.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					if (devList.size() != 0) {
						final Devices devce = devList.get(pos);
						devName = devce.getName();
						devAddress = devce.getDeviceIP();

						new PrinterConnectOperation().execute();
					}
					dialog.dismiss();
				}
			});
			break;
		}
		return dialog;

	}
	
	// for Zebra QL 420plus 4inch printer...
	private class PrinterConnectOperation extends AsyncTask<Void, String, String> {

		private ProgressDialog dialog;

		public PrinterConnectOperation() {
			dialog = new ProgressDialog(CurrencyCounter.this);
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
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			if (result.equals("success")) {
				dialog.dismiss();
				sendDataToPrinter();
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
				toastMsg.showToast(CurrencyCounter.this, "Connection : Unknown Printer Language");				
				printer = null;
				DemoSleeper.sleep(1000);
				disconnect();
			} catch (ZebraPrinterLanguageUnknownException e) {
				toastMsg.showToast(CurrencyCounter.this, "Language : Unknown Printer Language");	
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
			toastMsg.showToast(CurrencyCounter.this, "COMM Error! Disconnected");			
		}
	}
	
	public void doPrintErrorAlert() {
		AlertDialog.Builder alertMemory = new AlertDialog.Builder(
				CurrencyCounter.this);
		alertMemory.setTitle("Warning");
		alertMemory.setIcon(R.drawable.warning);
		alertMemory.setCancelable(false);
		alertMemory.setMessage("Printer connection problem");
		alertMemory.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertMemory.show();
	}
	
	
	// printing data to Zebra printer....
	private void sendDataToPrinter() {
		try {
			for (int i = 0; i < noOfPrint; i++) {
				writeHeaderSpace();
				writeHeader();
				writeDetail();
				writeItemDetail();
			}
			if (zebraPrinterConnection instanceof BluetoothPrinterConnection) {
				String friendlyName = ((BluetoothPrinterConnection) zebraPrinterConnection).getFriendlyName();
			}
		} catch (Exception e) {
			Log.e("ZebraPrinterConnectionException", e.toString());
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
		byte[] compName = getCompName();
		try {
			zebraPrinterConnection.write(compName);
		} catch (ZebraPrinterConnectionException e) {
			e.printStackTrace();
		}
	}

	private byte[] getCompName() {

		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {
			String cpclConfigLabel1 = "! 0 200 200 40 1" + "ON-FEED IGNORE\r\n"
					+ "CENTER\r\n" + "T 5 0 10 15 Currency Counter\r\n"
					+ "PRINT\r\n";
			configLabel1 = cpclConfigLabel1.getBytes();
		}
		return configLabel1;
	}
	
	private void writeDetail() {
		y = 0;
		y += 25;

		writeText("Currency/Coins", 36, y);
		writeText("Count", 235, y);
		writeText("Amount", 448, y);
		
		y += 15;
		writeText("--------------", 36, y);
		writeText("-----", 235, y);
		writeText("-----------", 432, y);
		
	}

	private void writeItemDetail() {
		int serialPosition = 0;
		int exp = 0;
		int finaly = y;
		int conv=1;
		StringBuffer strBuff = new StringBuffer();
		
		for (Map.Entry<String, String> e:mapPrint.entrySet()) {
			String check = e.getKey().toString();
			if(check.contains(coinName)){
				mStrKey = e.getKey().toString();
				mStrValue = e.getValue().toString();
				mapCoinPrint.put(mStrKey, mStrValue);
			}else{
				mStrKey = e.getKey().toString();
				mStrValue = e.getValue().toString();
				mapCurrenyPrint.put(mStrKey, mStrValue);
			}
		}
		
		for (Map.Entry<String, String> e:mapCurrenyPrint.entrySet()) {
			String amount;
			amount = e.getKey();
			conv=1;
			y = finaly + exp;
			y += 25;
			serialPosition += y;
			appendLineToPrint(strBuff, e.getKey(), 41, y);
			appendLineToPrint(strBuff, e.getValue(), 238, y);
			float amt = Float.parseFloat(amount);
			float qty = Float.parseFloat(e.getValue());
			float result = (amt * qty) / conv;
			int tempSize1 = String.valueOf(result).length();
			int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
			appendLineToPrint(strBuff, String.valueOf(result), 432 + (80 - tempPixSpace1), y);
			finaly = y + 20;
		}
		
		for (Map.Entry<String, String> e:mapCoinPrint.entrySet()) {
			String amount;
			
			String[] s3 = e.getKey().split(" ");
			amount = s3[0];
			conv = Integer.parseInt(coinConversion);
			
			y = finaly + exp;
			y += 25;
			serialPosition += y;
			appendLineToPrint(strBuff, e.getKey(), 41, y);
			appendLineToPrint(strBuff, e.getValue(), 238, y);
			float amt = Float.parseFloat(amount);
			float qty = Float.parseFloat(e.getValue());
			float result = (amt * qty) / conv;
			int tempSize1 = String.valueOf(result).length();
			int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
			appendLineToPrint(strBuff, String.valueOf(result), 432 + (80 - tempPixSpace1), y);
			finaly = y + 20;
		}
		
			y+=25;
			String str1=mEditTxt_CurCountCheque.getText().toString();
			String str2 = mEditTxt_CurCountCreditCard.getText().toString();
			String str3=mEditTxt_CurCountDebitCard.getText().toString();
		   		   
		   if(!str1.matches(""))
		   {
			   y+=25;
			   appendLineToPrint(strBuff, "Cheque", 235, y);
			   
			   dCheque = Double.parseDouble(str1);
			   int tempSize1 = String.valueOf(dCheque).length();
			   int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
			   appendLineToPrint(strBuff, String.valueOf(dCheque), 432 + (80 - tempPixSpace1), y);
		   }
		   
		   if(!str2.matches(""))
		   {
			   y+=32;
			   appendLineToPrint(strBuff, "Credit Card", 235, y);
			   dCredit = Double.parseDouble(str2);
			   int tempSize1 = String.valueOf(dCredit).length();
			   int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
			   appendLineToPrint(strBuff, String.valueOf(dCredit),	432 + (80 - tempPixSpace1), y);
		   }
		   
		   if(!str3.matches(""))
		   {
			   y+=32;
			   appendLineToPrint(strBuff, "Debit Card", 235, y);
			   dDebit = Double.parseDouble(str3);
			   int tempSize1 = String.valueOf(dDebit).length();
			   int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
			   appendLineToPrint(strBuff, String.valueOf(dDebit), 432 + (80 - tempPixSpace1), y);
		   }
		   
		   y += 25;
		   appendLineToPrint(strBuff, "-------------------------------------", 235, y);
		   y += 32;			
		   
		   appendLineToPrint(strBuff, "Total Amount", 235, y);
		   String total = mEditTxt_CurCountGrandTotal.getText().toString();
		   int tempSize1 = total.length();
		   int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
		   appendLineToPrint(strBuff, total, 432 + (80 - tempPixSpace1), y);
		   
		   y += 25;
		   y += 25;
		   appendLineToPrint(strBuff, "---------------------------------------------------------------------", 12, y);
			try {
				zebraPrinterConnection.write(strBuff.toString().getBytes());
			} catch (ZebraPrinterConnectionException e) {
				e.printStackTrace();
			}
	}
	
	private void writeText(String string, int i, int y2) {
		
		byte[] configLabel1 = null;
		if (printerLanguage == PrinterLanguage.ZPL) {
			configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
					.getBytes();
		} else if (printerLanguage == PrinterLanguage.CPCL) {

			String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 " + "! U1 X"
					+ " " + i + " " + "! U1 Y" + " " + y2 + " " + string;

			configLabel1 = data.getBytes();
		}
		try {
			zebraPrinterConnection.write(configLabel1);
		} catch (ZebraPrinterConnectionException e) {
			e.printStackTrace();
		}
	}
	
	private StringBuffer appendLineToPrint(StringBuffer strBuff,
			String strValue, int x, int y) {
		
			String prntFormat = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		
		return strBuff;
	}
	
	@Override
	public void onBackPressed() {
		supporter.simpleNavigateTo(MainMenu.class);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
