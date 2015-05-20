package com.ams.utils;

import java.util.HashMap;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util {
	public static final String USERNAME = "UserName";
	public static final String PASSWORD = "PassWord";
	public static Boolean isLogin = false;
	public boolean writeAccountInfo(Context mContent,String account,String password){
		Editor editor = mContent.getSharedPreferences("CheckList",
				Context.MODE_PRIVATE).edit();
		editor.putString(account, password);
		if (editor.commit()) {
			return true;
		}
		return false;
	}
	
	public HashMap<String, String> getLoginInfo(Context context) {
		String username = getSetup(context, "username");
		String passwd = getSetup(context, "password");
		String isfb = getSetup(context, "isFb");
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("username", username);
		hm.put("password", passwd);
		hm.put("isFb", isfb);
		return hm;
	}
	
	public String getSetup(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("CheckList",
				Context.MODE_PRIVATE);
		String defaultValue = "";
		String value = sp.getString(key, defaultValue);
		return value;
	}
	
	public boolean saveSetup(Context context, String key, String value) {
		Editor editor = context.getSharedPreferences("LoginInfo",
				Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		if (editor.commit()) {
			return true;
		}
		return false;
	}
	
	public static boolean saveAcountMsg(Context context, String key, String value) {
		Editor editor = context.getSharedPreferences("AcountInfo",
				Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		if (editor.commit()) {
			return true;
		}
		return false;
	}
	
	public static String getAcountMsg(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("AcountInfo",
				Context.MODE_PRIVATE);
		String defaultValue = "";
		String value = sp.getString(key, defaultValue);
		return value;
	}
	
	public static boolean saveSendFlag(Context context, String key, String value) {
		Editor editor = context.getSharedPreferences("SendFlag",
				Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		if (editor.commit()) {
			return true;
		}
		return false;
	}
	
	public static String getSendFlag(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("SendFlag",
				Context.MODE_PRIVATE);
		String defaultValue = "";
		String value = sp.getString(key, defaultValue);
		return value;
	}
	
	public static boolean saveLoginUser(Context context, String value) {
		Editor editor = context.getSharedPreferences("LoginAcount",
				Context.MODE_PRIVATE).edit();
		editor.putString("userName", value);
		if (editor.commit()) {
			return true;
		}
		return false;
	}
	public static String getLoginUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences("LoginAcount",
				Context.MODE_PRIVATE);
		String defaultValue = "";
		String value = sp.getString("userName", defaultValue);
		return value;
	}
}
