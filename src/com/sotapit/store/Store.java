package com.sotapit.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

public class Store {
	public static final String ID_LIST="ID_LIST";
	public static final String VIDEO_LIST="VIDEO_LIST";
	private static final String PACK_NAME = Store.class.getPackage().getName();

	
	private Store() {
	};
	


	private static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				PACK_NAME, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	public static void puts(Context context, String key, String value) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putString(key, value).commit();
	}
	
	public static void puts(Context context, String key, Integer value) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putInt(key, value).commit();
	}

	public static String gets(Context context, String key, String defVal) {
		return getSharedPreferences(context).getString(key, defVal);
	}
	
	public static Integer gets(Context context, String key, Integer defVal) {
		return getSharedPreferences(context).getInt(key, defVal);
	}
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		return getSharedPreferences(context).getBoolean(key, defValue);
	}
	public static boolean getBoolean(Activity context, String key, boolean defValue) {
		return getSharedPreferences(context).getBoolean(key, defValue);
	}
	public static void putLong(Context context, String key, long value) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putLong(key, value).commit();
	}
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putInt(key, value).commit();
	}
	public static long getLong(Context context, String key, long defVal) {
		return getSharedPreferences(context).getLong(key, defVal);
	}
	public static int getInt(Context context, String key, int defVal) {
		return getSharedPreferences(context).getInt(key, defVal);
	}

	public static synchronized boolean saveObject(Context context, String key, Object obj){
		if (obj == null)
			return false;
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			String str64 = new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
			editor.putString(key, str64);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return editor.commit();
	}

	public static Object getObject(Context context, String key){
		SharedPreferences preferences = getSharedPreferences(context);
		try {
		String str64 = preferences.getString(key, "");
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		byte[] base64Bytes =Base64.decode(str64, Base64.DEFAULT);
			bais = new ByteArrayInputStream(base64Bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	public static void remove(Context context, String key) {
		SharedPreferences preferences = getSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}

	
}
