package com.ams.ui;

import android.app.Activity;
import android.os.Bundle;

public class CinemaActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cinema_activity);
		MyApplication.getInstance().addActivity(this);
	}
   
}
