package com.ams.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ams.utils.DataClient;
import com.ams.utils.Util;
import com.ams.widget.FlowIndicator;
import com.ams.widget.GalleryAdapter;

public class HomeActivity extends Activity implements OnClickListener {
	static final int SCROLL_ACTION = 0;
	// ExpandableListView mExpandableListView;
	int[] tags = new int[] { 0, 0, 0, 0, 0 };
	Gallery mGallery;
	GalleryAdapter mGalleryAdapter;
	FlowIndicator mMyView;
	Timer mTimer;
	LinearLayout headerLiner = null;
	ImageView mVipView, mProductView, mCinemaView, mSingerView, mWifiView,
			mStoretView, mCarView, mGpsView;
	public static String className = null;
	private long exitTime = 0;
	private String name = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		MyApplication.getInstance().addActivity(this);
		name = Util.getLoginUser(HomeActivity.this);
		prepareView();
			
	}

	private void prepareView() {
		// mExpandableListView = (ExpandableListView)
		// findViewById(R.id.expandableListView1);
		View header = LayoutInflater.from(this).inflate(R.layout.header_view,
				null);
		headerLiner = (LinearLayout) findViewById(R.id.headLiner);
		mGallery = (Gallery) header.findViewById(R.id.home_gallery);
		mMyView = (FlowIndicator) header.findViewById(R.id.myView);
		mVipView = (ImageView) findViewById(R.id.vip_car);
		mVipView.setOnClickListener(this);
		mProductView = (ImageView) findViewById(R.id.product_car);
		mProductView.setOnClickListener(this);
		mCinemaView = (ImageView) findViewById(R.id.cinema_car);
		mCinemaView.setOnClickListener(this);
		mSingerView = (ImageView) findViewById(R.id.singer_car);
		mSingerView.setOnClickListener(this);
		mWifiView = (ImageView) findViewById(R.id.wifi_car);
		mWifiView.setOnClickListener(this);
		mStoretView = (ImageView) findViewById(R.id.store_car);
		mStoretView.setOnClickListener(this);
		mCarView = (ImageView) findViewById(R.id.augmented_car);
		mCarView.setOnClickListener(this);
		mGpsView = (ImageView) findViewById(R.id.parking_car);
		mGpsView.setOnClickListener(this);
		mGalleryAdapter = new GalleryAdapter(this);
		mMyView.setCount(mGalleryAdapter.getCount());
		mGallery.setAdapter(mGalleryAdapter);
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mMyView.setSeletion(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		headerLiner.addView(header);
		// mExpandableListView.addHeaderView(header);
		// mExpandableListView.setAdapter(adapter);
		// mExpandableListView
		// .setOnGroupExpandListener(new OnGroupExpandListener() {
		//
		// @Override
		// public void onGroupExpand(int arg0) {
		// // TODO Auto-generated method stub
		// tags[arg0] = 1;
		// }
		// });
		// mExpandableListView
		// .setOnGroupCollapseListener(new OnGroupCollapseListener() {
		//
		// @Override
		// public void onGroupCollapse(int arg0) {
		// // TODO Auto-generated method stub
		// tags[arg0] = 0;
		// }
		// });
	}

	private class MyTask extends TimerTask {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(SCROLL_ACTION);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SCROLL_ACTION:
				// int curPos = mGallery.getSelectedItemPosition();
				// if (curPos < mGalleryAdapter.getCount() - 1) {
				// curPos++;
				// } else {
				// curPos = 0;
				// }
				// // mGallery.setLayoutAnimation(new LayoutAnimationController(
				// // AnimationUtils.loadAnimation(HomeActivity.this,
				// // R.anim.gallery_in)));
				// mGallery.setSelection(curPos, true);
				MotionEvent e1 = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
						89.333336f, 265.33334f, 0);
				MotionEvent e2 = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
						300.0f, 238.00003f, 0);

				mGallery.onFling(e1, e2, -1300, 0);
				break;

			default:
				break;
			}
		}
	};

	class GroupHolder {
		ImageView img;
		TextView title;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vip_car:
			if (name != null && !name.equals("")) {
				Intent intent = new Intent(HomeActivity.this,
						VipMemberAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				className = VipMemberAct.class.getName();
			} else {
				Intent intent = new Intent(HomeActivity.this, LoginAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				className = LoginAct.class.getName();
			}
			break;
		case R.id.product_car:
			Intent productIntent = new Intent(HomeActivity.this,
					ProductActivity.class);
			startActivity(productIntent);
			className = ProductActivity.class.getName();
			break;
		case R.id.cinema_car:
			Intent cinemaIntent = new Intent(HomeActivity.this,
					CinemaActivity.class);
			startActivity(cinemaIntent);
			className = CinemaActivity.class.getName();
			break;
		case R.id.singer_car:
			Intent singerIntent = new Intent(HomeActivity.this,
					SingerActivity.class);
			startActivity(singerIntent);
			className = SingerActivity.class.getName();
			break;
		case R.id.wifi_car:
			if (name != null && !name.equals("")) {
				Intent intent = new Intent(HomeActivity.this,
						WifiSettingAct.class);
				startActivity(intent);
				className = WifiSettingAct.class.getName();
			} else {
				Intent wifiIntent = new Intent(HomeActivity.this,
						LoginAct.class);
				startActivity(wifiIntent);
				className = LoginAct.class.getName();
			}
			break;
		case R.id.store_car:
			Intent storeIntent = new Intent(HomeActivity.this, StoreAct.class);
			startActivity(storeIntent);
			className = StoreAct.class.getName();
			break;
		case R.id.augmented_car:   // 调用室内定位apk
			String userName = Util.getLoginUser(HomeActivity.this);
			if(userName != null && !userName.equals("")){
				Intent augmentedIntent = new Intent(HomeActivity.this, AdspApplication.class);
				startActivity(augmentedIntent);	
			}else{
				Toast.makeText(HomeActivity.this, getString(R.string.no_login_prompt), Toast.LENGTH_SHORT).show();
				Intent augmentedIntent = new Intent(HomeActivity.this, LoginAct.class);
				startActivity(augmentedIntent);	
			}
					
			
//			ComponentName componetName = new ComponentName("com.cn.adsp",
//					"com.cn.application.AdspApplication");
//			Intent intent = new Intent();
//			intent.setComponent(componetName);
//			startActivity(intent);
			break;
		case R.id.parking_car:
			Intent parkingIntent = new Intent(HomeActivity.this,
					FindMyCarAct.class);
			startActivity(parkingIntent);
			className = FindMyCarAct.class.getName();
			break;
		default:
			break;
		}

	}

	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	// super.onBackPressed();
	//
	// }
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==
	// KeyEvent.ACTION_DOWN){
	// System.out.println("=====================KeyEvent.KEYCODE_BACK");
	// if((System.currentTimeMillis()-exitTime) > 2000){
	// Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
	// Toast.LENGTH_SHORT).show();
	// exitTime = System.currentTimeMillis();
	// } else {
	// finish();
	// System.exit(0);
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	//

}
