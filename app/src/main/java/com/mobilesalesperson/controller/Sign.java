package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author T.SARAVANAN class for signature creating page
 */

public class Sign extends AppBaseActivity {

	private LinearLayout mContent;
	private View mView;
	private signature mSignature;
	private Bitmap mBitmap;
	private boolean isChangedStat = false;
	private SharedPreferences signPreference;
	private Supporter supporter;
	private MspDBHelper dbHelper;
	//private ImageView showMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.signature_layout);

		registerBaseActivityReceiver();
		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);

		//showMenu = (ImageView) findViewById(R.id.img_signShowMenu);
		
		
		mContent = (LinearLayout) findViewById(R.id.lay_signature);
		mSignature = new signature(this, null);
		mSignature.setBackgroundColor(Color.WHITE);
		mContent.addView(mSignature, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);		
		
		signPreference = this.getSharedPreferences("signPref",
				Context.MODE_PRIVATE);

		String previouslyEncodedImage = signPreference.getString("signature",
				"");

		if (!previouslyEncodedImage.equalsIgnoreCase("")) {

			System.out.println("Image stored in preference");

			Bitmap bitmap = supporter
					.decodeStringToImage(previouslyEncodedImage);
			Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			mSignature.setBackgroundDrawable(new BitmapDrawable(getResources(),
					mutableBitmap));
		}

		mView = mContent;

		/*showMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Sign.this.openOptionsMenu();
							
			}
		});		*/
		
	}

	// method to control menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.signature_menu, menu);
		return true;
	}

	// method to control menu options
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sign_mnu_clear:
			Log.v("log_tag", "Panel Cleared");
			mSignature.clear();
			isChangedStat = false;
			break;

		case R.id.sign_mnu_save:
			Log.v("log_tag", "Panel Saved");
			mView.setDrawingCacheEnabled(true);
			mSignature.save(mView);
			Bundle b = new Bundle();
			b.putString("status", "done");
			Intent intent = new Intent();
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;

		case R.id.sign_mnu_cancel:
			Log.v("log_tag", "Panel Canceled");
			Bundle b1 = new Bundle();
			b1.putString("status", "cancel");
			Intent intent1 = new Intent();
			intent1.putExtras(b1);
			setResult(RESULT_OK, intent1);
			finish();
			break;

		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(R.id.sign_mnu_save);
		if (isChangedStat == true) {
			item.setVisible(true);
			item.setEnabled(true);
		} else {
			item.setVisible(true);
			item.setEnabled(false);
		}
		return true;
	}

	// Canvas for signature
	public class signature extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
		private Paint paint = new Paint();
		private Path path = new Path();

		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		public signature(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		public void save(View v) {
			Log.v("log_tag", "Width: " + v.getWidth());
			Log.v("log_tag", "Height: " + v.getHeight());
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(mContent.getWidth(),
						mContent.getHeight(), Bitmap.Config.RGB_565);
			}
			Canvas canvas = new Canvas(mBitmap);
			v.draw(canvas);
			// here write your code to save signature

			String encodedSign = supporter.encodeImageToString(mBitmap); // encode
																			// sign
																			// image
																			// into
																			// string
																			// format

			// to store string image in preference
			Editor ed = signPreference.edit();
			ed.putString("signature", encodedSign);
			ed.commit();

		}

		public void clear() {
			this.setBackgroundColor(Color.WHITE);
			path.reset();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(path, paint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();
			isChangedStat = true;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				return true;

			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:

				resetDirtyRect(eventX, eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(eventX, eventY);
				break;

			default:
				debug("Ignored touch event: " + event.toString());
				return false;
			}

			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
					(int) (dirtyRect.top - HALF_STROKE_WIDTH),
					(int) (dirtyRect.right + HALF_STROKE_WIDTH),
					(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;
		}

		private void debug(String string) {
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}

			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		private void resetDirtyRect(float eventX, float eventY) {
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isChangedStat) {
				AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
				alertUser.setTitle("Confirmation");
				alertUser.setIcon(R.drawable.warning);
				alertUser.setCancelable(false);
				alertUser.setMessage("Do you want to Cancel ?");
				alertUser.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Sign.this.finish();
								dialog.dismiss();
							}
						});

				alertUser.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

					//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
			} else {
				Sign.this.finish();
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
