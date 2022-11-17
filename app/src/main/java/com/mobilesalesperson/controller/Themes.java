package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

public class Themes extends AppBaseActivity {

	private GridView themeGrid;
	private static int pos = 0;
	private static com.mobilesalesperson.controller.Themes.ThemeAdapter.ViewHolder vHoldMenu1;
	private Supporter supporter;
	private MspDBHelper dbhelpher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.theme_layout);

		registerBaseActivityReceiver();

		dbhelpher = new MspDBHelper(this);
		supporter = new Supporter(this, dbhelpher);

		themeGrid = (GridView) findViewById(R.id.grd_Theme);

		themeGrid.setAdapter(new ThemeAdapter(this));

		themeGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				pos = position;
				eventPage(pos, v);
			}
		});

	}

	protected void eventPage(final int pos2, View v) {

		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				Themes.this);
		alertDialog.setTitle("Confirmation");
		alertDialog.setIcon(R.drawable.warning);
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (pos2 == 0) {
							ThemeUtil.changeToTheme(Themes.this,
									ThemeUtil.THEME_BLACK_STONE);
							supporter.setTheme("BS");
						} else if (pos2 == 1) {
							ThemeUtil.changeToTheme(Themes.this,
									ThemeUtil.THEME_BLUE_SWIRL);
							supporter.setTheme("BSL");
						} else if (pos2 == 2) {
							ThemeUtil.changeToTheme(Themes.this,
									ThemeUtil.THEME_PLAIN);
							supporter.setTheme("PLAIN");
						}
					}
				});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setMessage("You want Apply this theme?");
		//alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

	}

	public class ThemeAdapter extends BaseAdapter {

		private LayoutInflater mLayoutInflater;

		public Integer[] mThumbIds = { R.drawable.bg, R.drawable.blueswirl,
				R.drawable.plain };

		public ThemeAdapter(Context c) {
			mLayoutInflater = LayoutInflater.from(c);
		}

		public int getCount() {
			return Menus.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {

				convertView = mLayoutInflater.inflate(R.layout.imageview_grid,
						parent, false);
				vHoldMenu1 = new ViewHolder();

				vHoldMenu1.mImageView = (ImageView) convertView
						.findViewById(R.id.grdImgview);
				vHoldMenu1.mTextView = (TextView) convertView
						.findViewById(R.id.grdText);
				vHoldMenu1.mImageView
						.setScaleType(ImageView.ScaleType.CENTER_CROP);

				vHoldMenu1.mImageView.setPadding(6, 6, 6, 6);

				convertView.setTag(vHoldMenu1);
			} else {
				vHoldMenu1 = (ViewHolder) convertView.getTag();
			}
			vHoldMenu1.mTextView.setText(Menus[position]);
			vHoldMenu1.mImageView.setImageResource(mThumbIds[position]);
			return convertView;
		}

		class ViewHolder {
			ImageView mImageView;
			TextView mTextView;
		}

		final String[] Menus = new String[] { "Black Stone", "Blue Swirl",
				"Plain" };

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent refresh = new Intent(this, MainMenu.class);
			startActivity(refresh);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
