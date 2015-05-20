package com.ams.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ams.utils.CacheBuffer;
import com.ams.utils.DataClient;
import com.ams.utils.DataProcess;
import com.ams.utils.SensorUtil;

public class AdspApplication extends Activity {
	public static final String TAG = "AdspApplication";

	private Thread mThread;
	private DataClient dataClient;
	public static Handler g_Handler;
	public static final int CONNECT_EXCEPTION = 100;
	public static final int SERVER_SIZE_EXCEPTION = 101;

	private Bitmap mBitmap;
	private Bitmap locatBitmap;
	AdspMapView mapView;
	private GetImageTask mImageTask;
	TextView textView;
	TextView gTextView;
	private ProgressDialog mProgressDialog = null;

	private final String imagePath = "http://192.168.1.131:8088/resource/background.jpg";
	private String filePath = Environment.getExternalStorageDirectory()
			+ "/receServer.txt";
	private String caclePath = Environment.getExternalStorageDirectory()
			+ "/caclePoint.txt";
	FileOutputStream outputStream;
	FileOutputStream outputStream2;

	Date date;
	Date date2;
	String tempTime;
	String tempTime2;
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private int srceenW;
	private int srceenH;

	boolean isArrayRead = false;
	boolean isFirstShow = false;
	int number1 = 0;
	int number2 = 0;
	String str;
	private String data;
	Timer timer;

	private int DIRFLAG = 0;
	private int k = 0;
	private float[] tempArray;
	private ArrayList<String> tempArrayList = new ArrayList<String>(4);

	SensorUtil mSensorUtil;

	private float[] tempArrayX;
	private float[] tempArrayY;
	private float tempFirstX;
	private float tempFirstY;

	public static String macAddr;
	public static int srceenWidth, srceenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.map_activity);
		mapView = (AdspMapView) findViewById(R.id.mapView);

		textView = (TextView) findViewById(R.id.textView1);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		gTextView = (TextView) findViewById(R.id.textView2);

		g_Handler = mHandler;

		Display display = getWindowManager().getDefaultDisplay();
		srceenWidth = display.getWidth();
		srceenHeight = display.getHeight();

		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);

		//设置mapView 显示大小
		// FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams)
		// mapView
		// .getLayoutParams();
		// linearParams.height = 1080;
		// linearParams.width = 1920;
		// mapView.setLayoutParams(linearParams);

		macAddr = getLocalMacAddress();
		Log.d(TAG, "============== mac addr = " + macAddr);

		//下载地图提示对话框
		// mProgressDialog = new ProgressDialog(AdspApplication.this);
		// mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// mProgressDialog.setMessage("初始化地图...");
		// mProgressDialog.setIndeterminate(false);
		// mProgressDialog.setCancelable(false);
		// mProgressDialog.show();

		mapView.setHandler(g_Handler);
		mapView.setTextView(textView, gTextView);
		mapView.initialize(1920, 1080);
		
		//mImageTask = new GetImageTask();
		//mImageTask.execute(imagePath);

	}
	
	/*
	 * 获取本机Mac地址
	 */
	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	// (10 - 99)
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 10: {
				String str = msg.obj.toString();
				textView.setText(str);
			}
				break;
			case 11: {
				String str = msg.obj.toString();
				gTextView.setText(str);
			}
				break;
			case 12: {
				if(mProgressDialog != null){
					mProgressDialog.dismiss();
				}
			}
				break;
			case 13: {
				Toast.makeText(getApplicationContext(), "north",
						Toast.LENGTH_SHORT).show();
			}
				break;
			case 14: {
				Toast.makeText(getApplicationContext(), "no direct",
						Toast.LENGTH_SHORT).show();
			}
				break;
			case 15: {
				String mString = msg.obj.toString();
				gTextView.setText(mString);
			}
				break;
			default:
				break;
			}

		};
	};



	public class GetImageTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			URL fileUrl = null;
			InputStream is = null;
			Bitmap imageBitmap = null;
			HttpURLConnection httpConn = null;
			Log.d(TAG, "download image task...");

			try {
				fileUrl = new URL(params[0]);

				httpConn = (HttpURLConnection) fileUrl.openConnection();
				httpConn.setDoInput(true);
				httpConn.connect();
				is = httpConn.getInputStream();
				imageBitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (httpConn != null) {
						httpConn.disconnect();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return imageBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			Log.d(TAG, "ready show image...");
			// Message disMsg = Message.obtain(mHandler, 12);
			// disMsg.sendToTarget();

			mapView.setBackground(BitmapConvertToDrawale(result));
			super.onPostExecute(result);
		}

		// Bitmap--> Drawable
		public Drawable BitmapConvertToDrawale(Bitmap bitmap) {
			// Bitmap bitmap = new Bitmap();
			Drawable drawable = new BitmapDrawable(bitmap);
			return drawable;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// mProgressDialog = new ProgressDialog(AdspApplication.this);
			// mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// mProgressDialog.setMessage("初始化地图...");
			// mProgressDialog.setIndeterminate(false);
			// mProgressDialog.setCancelable(false);
			// mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub

			super.onProgressUpdate(values);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// mapView.onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// mapView.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// mapView.initialize(srceenWidth, srceenHeight);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//mProgressDialog.dismiss();
		super.onDestroy();
	}
}
