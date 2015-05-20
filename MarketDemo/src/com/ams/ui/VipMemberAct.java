package com.ams.ui;

import android.app.Activity;
import android.os.Bundle;

public class VipMemberAct extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vip_layout);
		MyApplication.getInstance().addActivity(this);
	}
}
