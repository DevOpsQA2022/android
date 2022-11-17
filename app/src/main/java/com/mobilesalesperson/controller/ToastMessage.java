package com.mobilesalesperson.controller;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ToastMessage {
	public void showToast(Activity a, String msg) {
		LayoutInflater inflater = a.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_custom_layout,
				(ViewGroup) a.findViewById(R.id.toast_layout_root));
		TextView text = (TextView) layout.findViewById(R.id.txt_toastMsg);
		text.setText(msg);

		Toast toast = new Toast(a.getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 90);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
}
