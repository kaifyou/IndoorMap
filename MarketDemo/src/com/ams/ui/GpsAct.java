package com.ams.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class GpsAct extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ComponentName componetName = new ComponentName(
               "com.cn.ml",
               "com.cn.ml.IntoApp"); 
//       Intent intent= new Intent("chroya.foo");
       Intent intent= new Intent();
       Bundle bundle = new Bundle();
       intent.putExtras(bundle);
       intent.setComponent(componetName);
       startActivity(intent);
       MyApplication.getInstance().addActivity(this);
	}
}
