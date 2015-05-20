package com.ams.ui;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application{
	private List<Activity> activityList = new LinkedList<Activity>();

	private static MyApplication instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}
	public static MyApplication getInstance() {
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void exit() {
		// É¾³ý±¾µØ»º´æ
		SharedPreferences sp = getSharedPreferences("playsong",
				Context.MODE_PRIVATE);
		for (Activity activity : activityList) {
			if (activity != null)
				activity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
