package com.ams.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ams.utils.Util;

public class LoginAct extends Activity{ 
	private ImageView registBtn = null;
	private EditText accountEdit, passwordEdit; 
	private ImageView loginBtn,backBtn;
	private String account = null,password = null;
	private Util mUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		MyApplication.getInstance().addActivity(this);
		mUtil = new Util();
		initUi();
		
	}
	
	private void initUi(){
		registBtn = (ImageView)findViewById(R.id.registText);
		accountEdit = (EditText)findViewById(R.id.accountEdit);
		passwordEdit = (EditText)findViewById(R.id.passwordEdit);
		loginBtn = (ImageView)findViewById(R.id.loginBtn);
		backBtn = (ImageView)findViewById(R.id.back);
		registBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginAct.this,RegisterAct.class));
				finish();
			}
		});
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				account = accountEdit.getText().toString().trim();
				password = passwordEdit.getText().toString().trim();
				if(!account.equals("") && !account.equals("")){
					String confirmPd = mUtil.getSetup(LoginAct.this, account);
					if(confirmPd.equals("")){
						Message msg = new Message();
						msg.what = 0;
						msg.obj = "∏√’À∫≈≤ª¥Ê‘⁄£°";
						myHandler.sendMessage(msg);
						return;
					}else if(!confirmPd.equals(password)){
							Message msg = new Message();
							msg.what = 0;
							msg.obj = "√‹¬Î¥ÌŒÛ£¨«Î÷ÿ–¬ ‰»Î£°";
							myHandler.sendMessage(msg);
							return;
					}else{
						Util.saveLoginUser(LoginAct.this,account);
						Toast.makeText(LoginAct.this, getString(R.string.login_success),
								Toast.LENGTH_LONG).show();
					}
						
					mUtil.isLogin = true;
					startActivity(new Intent(LoginAct.this,HomeActivity.class));
					finish();					
				}else{
					Message msg = new Message();
					msg.what = 0;
					msg.obj = "«ÎÃÓ–¥’À∫≈∫Õ√‹¬Î£°";
					myHandler.sendMessage(msg);
				}
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginAct.this,HomeActivity.class));
				finish();
			}
		});
	}
	
	Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				String str = (String)msg.obj;
				passwordEdit.setText("");
				Toast.makeText(LoginAct.this, str, Toast.LENGTH_LONG).show();
//				Toast toast = new Toast(LoginAct.this);
//				toast.setText(str);
//				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//				toast.show();
				break;

			default:
				break;
			}
			
		}
		
	};
}
