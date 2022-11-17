package com.mobilesalesperson.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.mobilesalesperson.controller.R;

public class ThemeUtil {
	private static int sTheme;

	public final static int THEME_BLACK_STONE = 0;
	public final static int THEME_BLUE_SWIRL = 1;
	public final static int THEME_PLAIN = 2;

	/**
	 * Set the theme of the Activity, and restart it by creating a new Activity
	 * of the same type.
	 */
	public static void changeToTheme(Activity activity, int theme) {
		sTheme = theme;
		activity.finish();

		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	/** Set the theme of the activity, according to the configuration. */
	public static void onActivityCreateSetTheme(Activity activity) {
		switch (THEME_PLAIN) {
		default:
		case THEME_PLAIN:
			activity.setTheme(R.style.Theme_Plain);
			//activity.setTheme(R.style.Theme_Black_Stone);
			break;
		case THEME_BLUE_SWIRL:
			activity.setTheme(R.style.Theme_Blue_Swirl);
			break;
		case THEME_BLACK_STONE:

            activity.setTheme(R.style.Theme_Black_Stone);
			ActionBar bar = activity.getActionBar();
		   // bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#008080")));
			break;
		}
	}
}
