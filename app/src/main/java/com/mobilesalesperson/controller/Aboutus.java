package com.mobilesalesperson.controller;

import android.os.Bundle;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author T.SARAVANAN class to show application about page
 */
//Source modified by dilip on 19.2.2014
public class Aboutus extends AppBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.aboutus_layout);
		registerBaseActivityReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
