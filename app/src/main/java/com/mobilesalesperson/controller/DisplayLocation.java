
package com.mobilesalesperson.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.Supporter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.Supporter;

*/
public class DisplayLocation extends FragmentActivity {
/*
	private Button loadSalesSummary;
	private EditText fromDate;
	private MspDBHelper dbHelper;
	private Supporter supporter;
	GoogleMap googleMap;
	private int fromYear;
	private int fromMonth;
	private int fromDay;
	int locationCount = 0;
	String cmpnyNo;
	private Dialog dialog;
	static final int FROM_DATE_DIALOG_ID = 0;
	List<HhTran01> locDetails = new ArrayList<HhTran01>();
	private ToastMessage toastMsg;
	private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
			fromYear = year;
			fromMonth = monthOfYear;
			fromDay = dayOfMonth;
			fromDate.setText(supporter.getStringDate(fromYear, fromMonth + 1,
					fromDay));
			
			Calendar c = Calendar.getInstance();
			int currentYear = c.get(Calendar.YEAR);
			int currentMonth = c.get(Calendar.MONTH);
			int currentDay = c.get(Calendar.DAY_OF_MONTH);

			if ((fromYear > currentYear)) {					
				toastMsg.showToast(DisplayLocation.this,
						"Enter valid date");
			} else if (((fromMonth > currentMonth))
					&& (fromYear == currentYear)) {
				
				toastMsg.showToast(DisplayLocation.this,
						"Enter valid date");
			} else if (((fromDay > currentDay))
					&& (fromYear == currentYear)) {
				toastMsg.showToast(DisplayLocation.this,
						"Enter valid date");
			} else {
				googleMap.clear();
				
				dbHelper.openReadableDatabase();
				locDetails = dbHelper.getTranLocationDatas(fromDay,  fromMonth + 1,
						fromYear, cmpnyNo);
				dbHelper.closeDatabase();
				
				loadMapLocation();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_map);

		loadSalesSummary = (Button) findViewById(R.id.btn_loadSalessummary);
		fromDate = (EditText) findViewById(R.id.edt_SalesSummaryFromDate);
		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);

		cmpnyNo = supporter.getCompanyNo();
		toastMsg = new ToastMessage();
		
		currentDate();
		fromDate.setText(supporter.getStringDate(fromYear, fromMonth + 1,
				fromDay));

		dbHelper.openReadableDatabase();
		locDetails = dbHelper.getTranLocationDatas(fromDay,  fromMonth + 1,
				fromYear, cmpnyNo);
		dbHelper.closeDatabase();

		loadMapLocation();
		loadSalesSummary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar c = Calendar.getInstance();
				int currentYear = c.get(Calendar.YEAR);
				int currentMonth = c.get(Calendar.MONTH);
				int currentDay = c.get(Calendar.DAY_OF_MONTH);

				if ((fromYear > currentYear)) {					
					toastMsg.showToast(DisplayLocation.this,
							"Enter valid date");
				} else if (((fromMonth > currentMonth))
						&& (fromYear == currentYear)) {
					
					toastMsg.showToast(DisplayLocation.this,
							"Enter valid date");
				} else if (((fromDay > currentDay))
						&& (fromYear == currentYear)) {
					toastMsg.showToast(DisplayLocation.this,
							"Enter valid date");
				} else {
					googleMap.clear();
					
					dbHelper.openReadableDatabase();
					locDetails = dbHelper.getTranLocationDatas(fromDay,  fromMonth + 1,
							fromYear, cmpnyNo);
					dbHelper.closeDatabase();
					
					loadMapLocation();
				}
			}
		});
		// to handle fromDate click funtion
		fromDate.setOnTouchListener(new OnTouchListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				showDialog(FROM_DATE_DIALOG_ID);
				return true;
			}
		});
	
	}

	private void loadMapLocation() {
		// TODO Auto-generated method stub
		// Getting Google Play availability status
				int status = GooglePlayServicesUtil
						.isGooglePlayServicesAvailable(getBaseContext());

				// Showing status
				if (status != ConnectionResult.SUCCESS) { // Google Play Services are
															// not available

					int requestCode = 10;
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
							requestCode);
					dialog.show();

				} else { // Google Play Services are available

					// Getting reference to the SupportMapFragment of activity_main.xml
					SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
							.findFragmentById(R.id.map);

					// Getting GoogleMap object from the fragment
					googleMap = fm.getMap();

					// Enabling MyLocation Layer of Google Map
					googleMap.setMyLocationEnabled(true);

					// Getting number of locations already stored
					locationCount = locDetails.size();

					// Getting stored zoom level if exists else return 0
					// String zoom = sharedPreferences.getString("zoom", "0");

					// If locations are already saved
					if (locationCount != 0) {

						String lat = "";
						String lng = "";
						String ref="";
						// Iterating through all the locations stored
						for (int i = 0; i < locationCount; i++) {

							lat = locDetails.get(i).getHhTran_lat() + "";

							lng = locDetails.get(i).getHhTran_lon() + "";

							ref=locDetails.get(i).getHhTran_referenceNumber();
							// Drawing marker on the map
							drawMarker(new LatLng(Double.parseDouble(lat),
									Double.parseDouble(lng)),ref);
						}

						// Moving CameraPosition to last clicked position
						googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
								Double.parseDouble(lat), Double.parseDouble(lng))));

						// Setting the zoom level in the map on last position is clicked
						googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float
								.parseFloat(Float.toString(googleMap
										.getCameraPosition().zoom))));
					}
				}
	}

	private void drawMarker(LatLng point,String ref) {
		
		Marker marker = googleMap.addMarker(new MarkerOptions().position(point).title(ref));
		marker.showInfoWindow();
		
		*/
/*RelativeLayout tv = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.custom_marker_layout, null, false);
		tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
		                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		tv.setDrawingCacheEnabled(true);
		tv.buildDrawingCache();
		TextView txt=(TextView)tv.findViewById(R.id.num_txt);
		txt.setText(ref);
		Bitmap bm = tv.getDrawingCache();

		BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);

		BitmapDescriptorFactory.fromResource(R.drawable.map);
		googleMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).icon(icon));*//*

	
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog myDialog = null;
		dialog = null;
		switch (id) {
		case FROM_DATE_DIALOG_ID:

			myDialog = new DatePickerDialog(this, fromDateListener, fromYear,
					fromMonth, fromDay);
			break;
		}
		return myDialog;
	}

	// to set current date
	private void currentDate() {
		Calendar c = Calendar.getInstance();
		fromYear = c.get(Calendar.YEAR);
		fromMonth = c.get(Calendar.MONTH);
		fromDay = c.get(Calendar.DAY_OF_MONTH);
	}
*/

}