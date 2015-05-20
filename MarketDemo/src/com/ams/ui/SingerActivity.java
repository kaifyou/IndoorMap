package com.ams.ui;

import android.app.Activity;
import android.os.Bundle;

public class SingerActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singer_layout);
		MyApplication.getInstance().addActivity(this);
	}

}
