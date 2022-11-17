package com.mobilesalesperson.controller;

import android.os.Bundle;
import android.webkit.WebView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.ThemeUtil;

public class WebViewActivity extends AppBaseActivity {

	private WebView webview;
	private MspDBHelper dbHelper;
	private HhSetting setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.test_connection_layout);

		registerBaseActivityReceiver();

		dbHelper = new MspDBHelper(this);
		dbHelper.openReadableDatabase();
		setting = dbHelper.getSettingData();
		dbHelper.closeDatabase();
		String serverIP = getIntent().getStringExtra("IP Address");
		webview = (WebView) findViewById(R.id.wView_TC);
		webview.getSettings().setJavaScriptEnabled(true);
		String url = "http://" + serverIP + Login.URL_IMPORT_B;
		webview.loadUrl(url);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
