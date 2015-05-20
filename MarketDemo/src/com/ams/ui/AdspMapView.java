package com.ams.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ams.utils.CacheBuffer;
import com.ams.utils.DataClient;
import com.ams.utils.DataProcess;

import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class AdspMapView extends View {
	private String TAG = this.getClass().getName();

	private static final float MINDISTANCE = 1.0f;
	private static final float MaxDIS = 2.0f;
	private Bitmap sourceBitmap;
	private Bitmap alterBitmap;
	private Bitmap localBitmap;
	private Bitmap tBitmap;
	private Matrix matrix;
	private Paint mPaint;
	private Canvas canvasPaint;
	private float localBitMapWidth;
	private float localBitMapHeight;

	String pathString = Environment.getExternalStorageDirectory()
			+ "/drawPoint.txt";
	FileOutputStream drawOutputStream = null;

	// private Handler mHandler;
	public static final int STATUS_INIT = 1;
	public static final int STATUS_ZOOM_OUT = 2;
	public static final int STATUS_ZOOM_IN = 3;
	public static final int STATUS_MOVE = 4;
	public static final int STATUS_PAINT = 5;
	public static final int STATUS_PAINT_ARRAY = 6;
	private int currentStatus;

	private float currentBitmapWidth;
	private float currentBitmapHeight;
	private int bitmapWidth;
	private int bitmapHeight;

	// 记录画图点坐栄1�7
	private float pointDrawX = 0;
	private float pointDrawY = 0;
	// 记录下一次画图点

	// 记录后两次的画图炄1�7
	private float pointX3 = 0;
	private float pointY3 = 0;

	private int drawPointX = 0;
	private int drawPointY = 0;
	private float tdrawPointX = 0;
	private float tdrawPointY = 0;
	// 点与矩形朄1�7短距离，距离朄1�7小的点应拖至矩形冄1�7
	private int minDisPointX = 0;
	private int minDisPointY = 0;
	private int count = 0; // 标记点离哪个矩形朄1�7迄1�7

	private double lastFingerDis;
	private float lastXMove = -1;
	private float lastYMove = -1;
	private float movedDistanceX;
	private float movedDistanceY;

	// 记录两指同时放在屏幕上时，中心点的横坐标倄1�7
	private float centerPointX;
	// 记录两指同时放在屏幕上时，中心点的纵坐标倄1�7
	private float centerPointY;
	// 记录手指移动的距离所造成的缩放比侄1�7
	private float scaledRatio;

	private float totalTranslateX;
	private float totalTranslateY;

	// 按图像比例缩放图僄1�7
	private float initRatio;
	private float totalRatio;
	// 按屏幕缩放图僄1�7
	private float ratioX;
	private float ratioY;

	private ImageView mImageView;

	public static Region horizontalRegion;
	public static Region verticalRegion;

	public static Rect rect1;
	public static Rect rect2;
	public static Rect rect3;
	private Rect rect4;
	private Rect trainRect;
	private Rect roadRect;
	private Rect roadRect_1;
	private Rect wetRect;
	private Rect workRect1;
	private Rect workRect2;
	public static Rect storgRect;
	private List<Rect> rectList;

	private float lastPointX = 0;
	private float lastPointY = 0;

	private List<String> pointArray;
	int k = 0;
	private int flag = 0; // (0/1/2/3)

	private Context mContext;
	private int srceenW, srceenH;
	public static float srceenRatioX;
	public static float srceenRatioY;
	private static final float BITMAPWIDTH = (float) 8.0;
	private static final float BITMAPHEIGHT = (float) 5.0;

	private DataProcess mDataProcess;
	private DataClient mDataClient = null;
	private static final int CACHE_SIZE = 1000;
	private CacheBuffer mCacheBuffer = null;

	private float[] drawArrayX;
	private float[] drawArrayY;
	private int num = 0;
	private TextView textView_1;
	private TextView textView_2;

	/*
	 * 测试点假数据
	 */
	private Timer mTimer = new Timer();
	// 测试Y轴
	// private float tempx = (float) 0.0;
	// private float tempy = (float) 8.177;
	// 测试X轴
	// private float tempx = (float) 8.055;
	// private float tempy = (float) 0.0;

	private float tempx = (float) 18.0;
	private float tempy = (float) 14.0;
	private float kis = (float) 0.5;

	public class pointTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message msgStr = Message.obtain(mHandler, 100);
			Bundle bundle = new Bundle();
			bundle.putFloat("dragPointX", (tempx));
			bundle.putFloat("dragPointY", (tempy));
			msgStr.setData(bundle);
			msgStr.sendToTarget();

			tempx = tempx - kis;
			tempy = tempy - kis;
		}
	}

	public AdspMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Log.d(TAG, "init AdspMapView context");
		mContext = context;
	}

	public void initialize(int w, int h) {
		localBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.locate);
		localBitMapHeight = localBitmap.getHeight();
		localBitMapWidth = localBitmap.getWidth();
		srceenW = w;
		srceenH = h;
		Log.d(TAG, "======= srceenw = " + srceenW + " srceenh = " + srceenH);
		srceenRatioX = srceenW / BITMAPWIDTH;
		srceenRatioY = srceenH / BITMAPHEIGHT;
		matrix = new Matrix();
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		currentStatus = STATUS_INIT;

		// ��ʼ�������߳�
		initUtil();
	}

	public void setTextView(TextView textView1, TextView textView2) {
		textView_1 = textView1;
		textView_2 = textView2;
	}

	public void setHandler(Handler handler) {
		// mHandler = handler;
	}

	public void setPaintPoint(float x, float y) {
		pointDrawX = x * srceenRatioX;
		pointDrawY = srceenH - y * srceenRatioY;
		// pointDrawX = x;
		// pointDrawY = y;
		Log.d(TAG, "pointDrawX: " + pointDrawX + " pointDrawY: " + pointDrawY);
		if (pointDrawX > 1920) {
			pointDrawX = 1910;
		} else if (pointDrawX < 0) {
			pointDrawX = 10;
		}

		if (pointDrawY > 1080) {
			pointDrawY = 1070;
		} else if (pointDrawY < 0) {
			pointDrawY = 10;
		}

		currentStatus = STATUS_PAINT;
		invalidate();
	}

	public void setPaintPointArray(float x, float y) {
		pointDrawX = x * srceenRatioX;
		pointDrawY = srceenH - y * srceenRatioY;

		// Message msg = Message.obtain(AdspApplication.g_Handler, 10);
		// msg.obj = "x=" + x + " y=" + y + "\n";
		// msg.sendToTarget();

		currentStatus = STATUS_PAINT_ARRAY;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		switch (currentStatus) {
		case STATUS_INIT:
			initBitmap(canvas);
			break;
		case STATUS_PAINT:
			showBitmap(canvas, pointDrawX, pointDrawY);
			break;
		case STATUS_PAINT_ARRAY:
			showBitmapArray(canvas, pointDrawX, pointDrawY);
			break;
		default:
			break;
		}
	}

	// (100 - 199)
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100: {
				Bundle bundle = msg.getData();
				float drawx = bundle.getFloat("dragPointX");
				float drawy = bundle.getFloat("dragPointY");
				textView_1.setText("drawX = " + drawx + " drawY = " + drawy);
				setPaintPoint(drawx, drawy);
			}
				break;
			case 101: {

				Bundle bundle = msg.getData();
				drawArrayX = bundle.getFloatArray("arrayx");
				drawArrayY = bundle.getFloatArray("arrayy");

				setPaintPointArray(drawArrayX[0], drawArrayY[0]);
				num = 1;

				textView_2.setText("num=1, drawx[0]=" + drawArrayX[0]
						+ " drawy[0]=" + drawArrayY[0]);
			}
				break;

			case 102: {
				if (num < DataProcess.array_size) {
					setPaintPointArray(drawArrayX[num], drawArrayY[num]);
					textView_2.setText("num=" + num + " drawx[" + num + "]="
							+ drawArrayX[num] + " drawy[" + num + "]="
							+ drawArrayY[num]);
					num++;
				}
			}
				break;
			case 103: { // ��ӡԭʼ���
				if (msg != null) {
					String str = msg.obj.toString();
					textView_1.setText(str);
				}
			}
				break;
			default:
				break;
			}
		};
	};

	// 更新刷新bitmap图像
	public void showBitmap(Canvas canvas, float x, float y) {
		// canvas.drawCircle(x, y, 10, mPaint);
		canvas.drawBitmap(localBitmap, (x - localBitMapWidth / 2),
				(y - localBitMapHeight), null);
	}

	public void showBitmapArray(Canvas canvas, float x, float y) {

		canvas.drawBitmap(localBitmap, (x - localBitMapWidth / 2),
				(y - localBitMapHeight), null);
		// canvas.drawCircle(x, y, 10, mPaint);
		mHandler.sendEmptyMessageDelayed(102, 300);
	}

	public void initBitmap(Canvas canvas) {
		initRegion();
	}

	public void initUtil() {
		Log.d(TAG, "AdspMapView initUtil");

		mCacheBuffer = new CacheBuffer(CACHE_SIZE);

		mDataProcess = new DataProcess(mCacheBuffer, mHandler);
		mDataProcess.start();

		// DataReadTask dataReadTask = new DataReadTask();
		// dataReadTask.execute();

		mDataClient = new DataClient(mCacheBuffer, mHandler, mContext);
		mDataClient.start();

		// mTimer.schedule(new pointTask(), 1000, 2000);
	}

	public void initRegion() {
		int left1 = (int) (7.092 * srceenRatioX);
		int top1 = (int) (srceenH - (9.440 * srceenRatioY));
		int right1 = (int) (16.946 * srceenRatioX);
		int bottom1 = (int) (srceenH - 8.54 * srceenRatioY);
		rect1 = new Rect(left1, top1, right1, bottom1);
		horizontalRegion = new Region(rect1);

		int left2 = (int) (6.192 * srceenRatioX);
		int top2 = (int) (srceenH - 9.44 * srceenRatioY);
		int right2 = (int) (7.092 * srceenRatioX);
		int bottom2 = (int) (srceenH - 8.54 * srceenRatioY);
		rect2 = new Rect(left2, top2, right2, bottom2);
		// horizontalRegion.op(rect2, Region.Op.UNION);

		int left3 = (int) (6.192 * srceenRatioX);
		int top3 = (int) (srceenH - 8.54 * srceenRatioY);
		int right3 = (int) (9.196 * srceenRatioX);
		int bottom3 = (int) (srceenH - 2.89 * srceenRatioY);
		rect3 = new Rect(left3, top3, right3, bottom3);
		verticalRegion = new Region(rect3);

		// int left4 = (int) (8.646 * srceenRatioX);
		// int top4 = (int) (srceenH - 4.89 * srceenRatioY);
		// int right4 = (int) (10.125 * srceenRatioX);
		// int bottom4 = (int) (srceenH - 0.89 * srceenRatioY);
		// rect4 = new Rect(left4, top4, right4, bottom4);
		// verticalRegion = new Region(rect4);
		//

		int roadLeft = (int) (9.196 * srceenRatioX);
		int roadTop = (int) (srceenH - 8.54 * srceenRatioY);
		int roadRight = (int) (14.305 * srceenRatioX);
		int roadBottom = (int) (srceenH - 0.89 * srceenRatioY);
		roadRect = new Rect(roadLeft, roadTop, roadRight, roadBottom);
		horizontalRegion.op(roadRect, Region.Op.UNION);

		//
		int wetLeft = (int) (6.192 * srceenRatioX);
		int wetTop = (int) (srceenH - 11.8 * srceenRatioY);
		int wetRight = (int) (16.468 * srceenRatioX);
		int wetBottom = (int) (srceenH - 9.44 * srceenRatioY);
		wetRect = new Rect(wetLeft, wetTop, wetRight, wetBottom);
		horizontalRegion.op(wetRect, Region.Op.UNION);
		//
		int workLeft1 = (int) (3.58 * srceenRatioX);
		int workTop1 = (int) (srceenH - 11.8 * srceenRatioY);
		int workRight1 = (int) (6.192 * srceenRatioX);
		int workBottom1 = (int) (srceenH - 2.89 * srceenRatioY);
		workRect1 = new Rect(workLeft1, workTop1, workRight1, workBottom1);
		verticalRegion.op(workRect1, Region.Op.UNION);
		//
		// int workLeft2 = (int) (7.092 * srceenRatioX);
		// int workTop2 = (int) (srceenH - 8.54 * srceenRatioY);
		// int workRight2 = (int) (8.646 * srceenRatioX);
		// int workBottom2 = (int) (srceenH - 2.89 * srceenRatioY);
		// workRect2 = new Rect(workLeft2, workTop2, workRight2, workBottom2);
		// verticalRegion.op(workRect2, Region.Op.UNION);
		//
		int stroyLeft = (int) (3.58 * srceenRatioX);
		int storyTop = (int) (srceenH - 2.89 * srceenRatioY);
		int storyRight = (int) (9.196 * srceenRatioX);
		int storyBottom = (int) (srceenH - 0.89 * srceenRatioY);
		storgRect = new Rect(stroyLeft, storyTop, storyRight, storyBottom);
	}

	/**
	 * 计算两个手指之间的距离�1�7�1�7
	 * 
	 * @param event
	 * @return 两个手指之间的距禄1�7
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 计算两个手指之间中心点的坐标〄1�7
	 * 
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
	}

	// private String curPointStr = null;
	//
	// public class DataReadTask extends AsyncTask {
	//
	// @Override
	// protected Object doInBackground(Object... params) {
	// // TODO Auto-generated method stub
	// while (mCacheBuffer != null) {
	//
	// Log.d(TAG, "enter dataprocess task, mCacheBuffer is not null");
	// Log.d(TAG, "==== curPonitSTR = " + curPointStr);
	// if ((curPointStr = mCacheBuffer.getCacheItem()) != null
	// && curPointStr != "") {
	//
	// Log.d(TAG, "curPointStr is not null");
	// // parseOnePoint(curPointStr);
	// }
	//
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	//
	// return null;
	// }
	//
	// }

	public void onStop() {
		mDataClient.disConnect();
		mDataProcess.clearData();
	}
}
