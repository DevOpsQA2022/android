package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

public class CurrencyCounterSettings extends AppBaseActivity {
	
	private EditText mEditTxt_CurSetCurrencyName;
	private EditText mEditTxt_CurSetSymbol;
	private EditText mEditTxt_CurSetCoinName;
	private EditText mEditTxt_CurSetConversion;
	private EditText mEditTxt_CurSetAddCur;
	private EditText mEditTxt_CurSetAddCoin;
	private ListView mListView_CurSetCurrency;
	private ListView mListView_CurSetCoin;
	private Button mBtn_CurSetAddCurrency;
	private Button mBtn_CurSetClearCurrency;
	private Button mBtn_CurSetAddCoin;
	private Button mBtn_CurSetClearCoin;
	private Button mBtn_CurSetSave;
	private Button mBtn_CurSetClearAll;
	
	private List<String> mList_Currency;
	private List<String> mList_Coin;
	
	private ArrayAdapter<String> mAdapter_Currency;
	private ArrayAdapter<String> mAdapter_Coin;
	
	public String PREFERENCES_TODO = "Currency_List_Shared_Preferences";
	public SharedPreferences mPref_CurSetPref;
	private MspDBHelper dbHelper;
	private Supporter supporter;
	private ToastMessage toastMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.currency_setting_layout);
		registerBaseActivityReceiver();
		
		findViewByID();
		
		mList_Currency = new ArrayList<String>();
		mList_Coin = new ArrayList<String>();
		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		toastMessage = new ToastMessage();
		
		mAdapter_Currency = new ArrayAdapter<String>(CurrencyCounterSettings.this, R.layout.currency_row, mList_Currency);
		mAdapter_Coin = new ArrayAdapter<String>(CurrencyCounterSettings.this, R.layout.currency_row, mList_Coin);
				
		mPref_CurSetPref = getSharedPreferences(PREFERENCES_TODO, MODE_PRIVATE);
		
		mEditTxt_CurSetCurrencyName.setText(mPref_CurSetPref.getString("CurrencyName", ""));
		mEditTxt_CurSetSymbol.setText(mPref_CurSetPref.getString("CurrencySymbol", ""));
		mEditTxt_CurSetCoinName.setText(mPref_CurSetPref.getString("CoinName", ""));
		mEditTxt_CurSetConversion.setText(mPref_CurSetPref.getString("CoinConversion", ""));
		int currencyListSize = Integer.parseInt(mPref_CurSetPref.getString("CurrencyListSize", "0"));
		int coinListSize = Integer.parseInt(mPref_CurSetPref.getString("CoinListSize", "0"));
		
		if(currencyListSize != 0) {
			if(mList_Currency.size() != 0) {
				mList_Currency.clear();
			}
			
			for (int i = 0; i < currencyListSize; i++) {
				mList_Currency.add(mPref_CurSetPref.getString("Currency"+i, ""));
			}
			mListView_CurSetCurrency.setAdapter(mAdapter_Currency);
		}
		
		if(coinListSize != 0) {
			if(mList_Coin.size() != 0) {
				mList_Coin.clear();
			}
			
			for (int i = 0; i < coinListSize; i++) {
				mList_Coin.add(mPref_CurSetPref.getString("Coin"+i, ""));
			}
			mListView_CurSetCoin.setAdapter(mAdapter_Coin);
		}
		
		mBtn_CurSetAddCurrency.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!(mEditTxt_CurSetAddCur.getText().length() == 0 || mEditTxt_CurSetAddCur.getText().toString().equals("0"))) {
					boolean val = validateFields();
					if(val == true) {
						String currency = mEditTxt_CurSetAddCur.getText().toString();
						if(!mList_Currency.contains(currency)) {
							mList_Currency.add(currency);
							mListView_CurSetCurrency.setAdapter(mAdapter_Currency);
						} else {
							toastMessage.showToast(CurrencyCounterSettings.this, "You have already added "+currency);
						}
						mEditTxt_CurSetAddCur.setText("");
					} else {
						toastMessage.showToast(CurrencyCounterSettings.this, "Please fill all the fields");
					}
				} else {
					toastMessage.showToast(CurrencyCounterSettings.this, "Please enter a valid currency value");
				}
			}
		});
		
		mBtn_CurSetAddCoin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!(mEditTxt_CurSetAddCoin.getText().length() == 0 || mEditTxt_CurSetAddCoin.getText().toString().equals("0"))) {
					boolean val = validateFields();
					if(val == true) {
						String coin = mEditTxt_CurSetAddCoin.getText().toString();
						String coinName = coin+" "+mEditTxt_CurSetCoinName.getText().toString();
						if(!mList_Coin.contains(coinName)) {
							mList_Coin.add(coinName);
							mListView_CurSetCoin.setAdapter(mAdapter_Coin);
						} else {
							Toast.makeText(CurrencyCounterSettings.this, "You have already added "+coin, Toast.LENGTH_SHORT).show();
						}
						mEditTxt_CurSetAddCoin.setText("");
					} else {
						toastMessage.showToast(CurrencyCounterSettings.this, "Please fill all the fields");
					}
				} else {
					toastMessage.showToast(CurrencyCounterSettings.this, "Please enter a valid coin value");
				}
			}
		});
		
		mBtn_CurSetClearCurrency.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mList_Currency.size() != 0) {
					mList_Currency.remove(mList_Currency.size() - 1);
					mListView_CurSetCurrency.setAdapter(mAdapter_Currency);
				} else {
					toastMessage.showToast(CurrencyCounterSettings.this, "No items to remove");
				}
			}
		});
		
		mBtn_CurSetClearCoin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mList_Coin.size() != 0) {
				mList_Coin.remove(mList_Coin.size() - 1);
				mListView_CurSetCoin.setAdapter(mAdapter_Coin);
				} else {
					toastMessage.showToast(CurrencyCounterSettings.this, "No items to remove");
				}
			}
		});
		
		mBtn_CurSetSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean val = validateFields();
				if(val == true) {
					mPref_CurSetPref = getSharedPreferences(PREFERENCES_TODO, MODE_PRIVATE);
					Editor mEditor = mPref_CurSetPref.edit();
					mEditor.putString("CurrencyName", mEditTxt_CurSetCurrencyName.getText().toString());
					mEditor.putString("CurrencySymbol", mEditTxt_CurSetSymbol.getText().toString());
					mEditor.putString("CoinName", mEditTxt_CurSetCoinName.getText().toString());
					mEditor.putString("CoinConversion", mEditTxt_CurSetConversion.getText().toString());
					
					mEditor.putString("CurrencyListSize", String.valueOf(mList_Currency.size()));
					for(int i = 0; i < mList_Currency.size(); i++) {
						mEditor.putString("Currency"+i, mList_Currency.get(i));
					}
					
					mEditor.putString("CoinListSize", String.valueOf(mList_Coin.size()));
					for (int j = 0; j < mList_Coin.size(); j++) {
						mEditor.putString("Coin"+j, mList_Coin.get(j));
					}
					mEditor.commit();
					
					AlertDialog.Builder alertUser = new AlertDialog.Builder(CurrencyCounterSettings.this);
					alertUser.setTitle("Information");
					alertUser.setIcon(R.drawable.tick);
					alertUser.setCancelable(false);
					alertUser.setMessage("Currency Counter Settings updated successfully");
					alertUser.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									supporter.simpleNavigateTo(CurrencyCounter.class);
								}
							});
						//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
				} else {
					toastMessage.showToast(CurrencyCounterSettings.this, "Please enter details in all fields");
				}
				
			}
		});
		
		mBtn_CurSetClearAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearAllFields();
			}
		});
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Confirmation");
		alertUser.setIcon(R.drawable.warning);
		alertUser.setCancelable(false);
		alertUser
				.setMessage("Are you sure want to cancel the currency counter settings updation?");
		alertUser.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						supporter.simpleNavigateTo(MainMenu.class);
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



	public void findViewByID() {
		mEditTxt_CurSetCurrencyName = (EditText)findViewById(R.id.edtTxt_Currency);
		mEditTxt_CurSetSymbol = (EditText)findViewById(R.id.edtTxt_CurrSymbol);
		mEditTxt_CurSetCoinName = (EditText)findViewById(R.id.edtTxt_CoinName);
		mEditTxt_CurSetConversion = (EditText)findViewById(R.id.edtTxt_CurrConv);
		mEditTxt_CurSetAddCur = (EditText)findViewById(R.id.edtTxt_AddCurrency);
		mEditTxt_CurSetAddCoin = (EditText)findViewById(R.id.edtTxt_AddCoin);
		mListView_CurSetCurrency = (ListView)findViewById(R.id.listView_Currency);
		mListView_CurSetCoin = (ListView)findViewById(R.id.listView_Coin);
		mBtn_CurSetAddCurrency = (Button)findViewById(R.id.btn_AddCurrency);
		mBtn_CurSetClearCurrency = (Button)findViewById(R.id.btn_ClearCurrency);
		mBtn_CurSetAddCoin = (Button)findViewById(R.id.btn_AddCoin);
		mBtn_CurSetClearCoin = (Button)findViewById(R.id.btn_ClearCoin);
		mBtn_CurSetSave = (Button)findViewById(R.id.btn_SaveCurrencySetting);
		mBtn_CurSetClearAll = (Button)findViewById(R.id.btn_CurrSettingClearAll);
	}
	
	public boolean validateFields() {
		boolean result = false;
		if (mEditTxt_CurSetCurrencyName.getText().length() == 0) {
			mEditTxt_CurSetCurrencyName.requestFocus();
			mEditTxt_CurSetCurrencyName.setError("Enter currency name");
			result = false;
		} else {
			if (mEditTxt_CurSetSymbol.getText().length() == 0) {
				mEditTxt_CurSetSymbol.requestFocus();
				mEditTxt_CurSetSymbol.setError("Enter currency symbol");
				result = false;
			} else {
				if (mEditTxt_CurSetCoinName.getText().length() == 0) {
					mEditTxt_CurSetCoinName.requestFocus();
					mEditTxt_CurSetCoinName.setError("Enter coin name");
					result = false;
				} else {
					if (mEditTxt_CurSetConversion.getText().length() == 0) {
						mEditTxt_CurSetConversion.requestFocus();
						mEditTxt_CurSetConversion.setError("Enter coin conversion");
						result = false;
					} else {
						result = true;
					}
				} 
			} 
		} 
		return result;
	}
	
	public void clearAllFields() {
		mEditTxt_CurSetCurrencyName.setText("");
		mEditTxt_CurSetSymbol.setText("");
		mEditTxt_CurSetCoinName.setText("");
		mEditTxt_CurSetConversion.setText("");
		mList_Currency.clear();
		mList_Coin.clear();
		
		mListView_CurSetCurrency.setAdapter(mAdapter_Currency);
		mListView_CurSetCoin.setAdapter(mAdapter_Coin);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
