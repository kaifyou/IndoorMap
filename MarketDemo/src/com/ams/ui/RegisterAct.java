package com.ams.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ams.utils.Util;
import com.ams.view.PickerView;
import com.ams.view.PickerView.onSelectListener;

public class RegisterAct extends Activity {
	private String TAG = "RegisterAct";
	private TextView registBtn = null;
	private EditText accountEdit, passwordEdit, mailEdit, qqEdit, ageEdit,
			weixinEdit;
	private TextView loginBtn,selectAge;
	private ImageView showPsw;
	private ImageView backBtn;
	private String account = null, password = null, mail = null, age = null,
			weixin = null, sex = null;
	public static String g_Acount = null;
	private Util mUtil;
	private RadioButton mRadioMale, mRadioFemale;
	private PickerView mSpinner1 = null;
	private Spinner mSpinner2 = null;
	private Spinner mSpinner3 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist_layout);
		MyApplication.getInstance().addActivity(this);
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
		// detectDiskReads().detectDiskWrites()
		// .detectNetwork().penaltyLog().build());
		mUtil = new Util();
		initUi();
	}

	private void initUi() {
		registBtn = (TextView) findViewById(R.id.registBtn);
		accountEdit = (EditText) findViewById(R.id.reg_account);
		passwordEdit = (EditText) findViewById(R.id.reg_password);
		mailEdit = (EditText) findViewById(R.id.reg_mail);
		weixinEdit = (EditText) findViewById(R.id.weixin);
		mRadioMale = (RadioButton) findViewById(R.id.male);
		mRadioFemale = (RadioButton) findViewById(R.id.female);
		
//		mSpinner2 = (Spinner) findViewById(R.id.age_spinner2);
//		mSpinner3 = (Spinner) findViewById(R.id.age_spinner3);
		loginBtn = (TextView) findViewById(R.id.reg_login);
		backBtn = (ImageView) findViewById(R.id.back);
		showPsw = (ImageView) findViewById(R.id.pswVisible);
		selectAge = (TextView) findViewById(R.id.ageEdit);
		mRadioMale.setChecked(true);
		selectAge.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShowSelectDialog();
			}
		});
//		List<String> data1 = new ArrayList<String>();
//        List<String> data2 = new ArrayList<String>();
//        List<String> data3 = new ArrayList<String>();
//        for (int i = 0; i < 10; i++)
//        {
//            data2.add("" + i);
//        }
//        for (int i = 0; i < 10; i++)
//        {
//            data3.add("" + i);
//        }
//        mSpinner1.setData(data1);
//        mSpinner1.setOnSelectListener(new onSelectListener()
//        {
// 
//            @Override
//            public void onSelect(String text)
//            {
//            	age1 = text;
//            }
//        });
//        mSpinner2.setData(data2);
//        mSpinner2.setOnSelectListener(new onSelectListener()
//        {
// 
//            @Override
//            public void onSelect(String text)
//            {
//            	age2 = text;
//            }
//        });
//        
//        mSpinner3.setData(data3);
//        mSpinner3.setOnSelectListener(new onSelectListener()
//        {
// 
//            @Override
//            public void onSelect(String text)
//            {
//            	age3 = text;
//            }
//        });
//		String[] mItems = getResources().getStringArray(R.array.age1);
//		ArrayAdapter<String> mAdapter1 = new ArrayAdapter<String>(
//				RegisterAct.this, android.R.layout.simple_spinner_item, mItems);
//		mSpinner1.setAdapter(mAdapter1);
//		// Spinner��ѡ���¼��󶨡�
//		mSpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				age1 = parent.getItemAtPosition(position).toString();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
//		
//		String[] mItems2 = getResources().getStringArray(R.array.age2);
//		ArrayAdapter<String> mAdapter2 = new ArrayAdapter<String>(
//				RegisterAct.this, android.R.layout.simple_spinner_item, mItems2);
//		mSpinner2.setAdapter(mAdapter2);
//		// Spinner��ѡ���¼��󶨡�
//		mSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				age2 = parent.getItemAtPosition(position).toString();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
//		
//		String[] mItems3 = getResources().getStringArray(R.array.age3);
//		ArrayAdapter<String> mAdapter3 = new ArrayAdapter<String>(
//				RegisterAct.this, android.R.layout.simple_spinner_item, mItems3);
//		mSpinner3.setAdapter(mAdapter3);
//		// Spinner��ѡ���¼��󶨡�
//		mSpinner3.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				age3 = parent.getItemAtPosition(position).toString();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
		registBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				account = accountEdit.getText().toString();
				password = passwordEdit.getText().toString();
				weixin = weixinEdit.getText().toString();
				mail = mailEdit.getText().toString();
//				age = age1 + age2 + age3;
				if (mRadioMale.isChecked()) {
					sex = "man";
				} else if (mRadioFemale.isChecked()) {
					sex = "woman";
				}
				if(account != null && account.length() != 11){
					Toast.makeText(RegisterAct.this, getString(R.string.phone_error),
							Toast.LENGTH_LONG).show();
					return;
				}
				if(password != null && password.length() < 6){
					Toast.makeText(RegisterAct.this, getString(R.string.pasword_error),
							Toast.LENGTH_LONG).show();
					return;
				}
				if(!mail.equals("") && !mail.contains("@")){
					Toast.makeText(RegisterAct.this, getString(R.string.email_error),
							Toast.LENGTH_LONG).show();
				}
				if (!account.equals("") && account != null
						&& !password.equals("") && password != null
						&& sex != null && !sex.equals("")) {
					mUtil.saveSetup(RegisterAct.this, mUtil.USERNAME, account);
					mUtil.saveSetup(RegisterAct.this, mUtil.PASSWORD, password);
					mUtil.writeAccountInfo(RegisterAct.this, account, password);
					mUtil.isLogin = true;
					// new connectSever().start();

					g_Acount = account;
					String msg = "account=" + account + " password=" + password
							+ " weixin=" + weixin + " mail=" + mail + " sex="
							+ sex + " age=" + age;
					Log.d(TAG, "register msg = " + msg);
					mUtil.saveSendFlag(RegisterAct.this, g_Acount, "New");
					mUtil.saveAcountMsg(RegisterAct.this, "Acount", msg);
					mUtil.saveLoginUser(RegisterAct.this, account);
					Toast.makeText(RegisterAct.this, getString(R.string.regist_success),
							Toast.LENGTH_LONG).show();
					startActivity(new Intent(RegisterAct.this,
							HomeActivity.class));
					finish();
				} else {
					myHandler.sendEmptyMessage(0);
				}
			}
		});
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(RegisterAct.this, LoginAct.class));
				finish();
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(RegisterAct.this, HomeActivity.class));
				finish();
			}
		});
		showPsw.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passwordEdit
						.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());
			}
		});
	}

	Handler myHandler = new Handler() {
		// 03-30 17:05:46.070: E/AndroidRuntime(29110):
		// java.lang.RuntimeException: This Toast was not created with
		// Toast.makeText()

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(RegisterAct.this,getString(R.string.regist_tost),
						Toast.LENGTH_LONG).show();
				// Toast toast = new Toast(RegisterAct.this);
				// toast.setText("�뽫ע������Ϣ��д����");
				// toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				// toast.show();
				break;

			default:
				break;
			}

		}

	};

	public void connecttoserver(String socketData) throws UnknownHostException,
			IOException {
		System.out.println("===========connect to server");
		Socket socket = RequestSocket("192.168.1.115", 8813);
		System.out.println("==============SendMsg");
		SendMsg(socket, socketData);
		String txt = ReceiveMsg(socket);
	}

	private Socket RequestSocket(String host, int port)
			throws UnknownHostException, IOException {
		System.out.println("===========RequestSocket");
		Socket socket = new Socket(host, port);
		System.out.println("===========return socket;");
		return socket;
	}

	private void SendMsg(Socket socket, String msg) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		writer.write(msg.replace("\n", " ") + "\n");
		writer.flush();
		System.out.println("======msg:" + msg);
	}

	private String ReceiveMsg(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String txt = reader.readLine();
		return txt;
	}

	public class connectSever extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("=======connecttoserver");
			try {
				connecttoserver(account + password);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.run();
		}

	}

	private void ShowSelectDialog() {
		// TODO Auto-generated method stub
	final Dialog builder = new Dialog(this,R.style.share_dialog_white);
	View view = LayoutInflater.from(this).inflate(R.layout.select_age,
			null);
	mSpinner1 = (PickerView) view.findViewById(R.id.age_spinner1);
	List<String> data1 = new ArrayList<String>();
  for (int i = 1950; i < 2001; i++)
  {
      data1.add(i+"");
  }
	 mSpinner1.setData(data1);
     mSpinner1.setOnSelectListener(new onSelectListener()
     {

         @Override
         public void onSelect(String text)
         {
        	
         	age = text;
         }
     });
	builder.show();
	builder.setCancelable(false);
	builder.setCanceledOnTouchOutside(false);
	builder.setContentView(view);
	Button btnOK = (Button) view.findViewById(R.id.confirm);
	btnOK.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			selectAge.setText(age);
			builder.dismiss();
		}
	});

	}
}
