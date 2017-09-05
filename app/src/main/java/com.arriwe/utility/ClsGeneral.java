package com.arriwe.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ClsGeneral {
	@SuppressLint("StaticFieldLeak")
	private static Context mContext;



	public static void setPreferences(Context context, String key, String value) {
		mContext = context;
		SharedPreferences.Editor editor = mContext.getSharedPreferences(
				"WED_APP", Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getPreferences(Context context, String key) {
		mContext = context;
		SharedPreferences prefs = mContext.getSharedPreferences("WED_APP",
				Context.MODE_PRIVATE);
		return  prefs.getString(key, "");
	}

	public static void clearPreference(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences("WED_APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}



	public static final String DEVICE_TOKEN = "device_token";
	

}
