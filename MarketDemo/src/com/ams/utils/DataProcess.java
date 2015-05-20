package com.ams.utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ams.ui.AdspApplication;
import com.ams.ui.AdspMapView;

public class DataProcess extends Thread {
	private String TAG = "=== DataProcess ===";
	public static Handler dataProHandler;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final int NORTH = 4;
	public static final int EWEST = 5;
	public static final int SNORTH = 6;
	public static final int ONUP = 7;
	public static final int DOWN = 8;

	private ArrayList<String> pointListStrings = null;
	private String curPointString;
	private String lastPointString;

	private int direction = 0;
	private int lastDirection = 0;

	private static float lastShowX = 0;
	private static float lastShowY = 0;
	private String lastShowString = null;
	private int mDirection = 0;
	private float mSlope; // 方向斜率
	private double mAngle; // 方向角度，由斜率计算凄1�7
	private Timer mTimer = null;
	private boolean isMoving;

	private int east_west = 0;
	private int south_north = 0;

	private final double MAXPOINTDISTANCE = 2.0;
	private final double MINPOINTDISTANCE = 0.5;
	private final float HORITAL = (float) 9.04;
	private final float VERTICAL = (float) 6.54;
	public static final int ARRAY_SIZE = 6;
	private float distance = 0;
	private final float perior = 0;

	private ArrayList<String> pointStrings = null;
	private String curPointStr = null;
	private String lastPointStr = null;
	private float[] pointXArray = null;
	private float[] pointYArray = null;
	private static int k = 0;
	private float lastPointX = 0;
	private float lastPointY = 0;
	private float dragPointx = 0;
	private float dragPointy = 0;
	private float lastDragPointx = 0;
	private float lastDragPointy = 0;

	private Handler mHandler;
	private CacheBuffer pCacheBuffer;

	public static int array_size = 3;
	float[] arrayx = new float[array_size];
	float[] arrayy = new float[array_size];

	public DataProcess(CacheBuffer cacheBuffer, Handler handler) {

		pointStrings = new ArrayList<String>(ARRAY_SIZE);
		pointXArray = new float[ARRAY_SIZE];
		pointYArray = new float[ARRAY_SIZE];

		mHandler = handler;
		pCacheBuffer = cacheBuffer;
		mTimer = new Timer();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		mTimer.schedule(mTask, 0, 1000);

		// Looper.prepare();
		// dataProHandler = new ThreadHander(Looper.myLooper());
		// Looper.loop();
	}

	class ThreadHander extends Handler {

		public ThreadHander(Looper looper) {
			super(looper);
		}

		// (200 - 299)
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200: {

				Bundle bundle = msg.getData();
				pointListStrings = bundle.getStringArrayList("pointArrayList");
				Log.d(TAG, "========== data process 200 ");
				if (parsePointStrings(pointListStrings, pointXArray,
						pointYArray) != 0) {
					Log.d(TAG, "parse point strings failed");
				}

				direction = getDirection(pointXArray, pointYArray);
				Log.d(TAG, "================= direction = " + direction);
				pointStrings.clear();

			}
				break;

			default:
				break;
			}
		}
	}

	/*
	 * 定时器从buffer中获取点数据
	 */
	public TimerTask mTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (pCacheBuffer != null) {

				// Log.d(TAG,
				// "enter dataprocess task, mCacheBuffer is not null");
				if ((curPointStr = pCacheBuffer.getCacheItem()) != null
						&& curPointStr != "") {

					// Log.d(TAG, "curPointStr is not null");
					parseOnePoint(curPointStr);
				}
			}
		}
	};

	/*
	 * 解析获取到的点数据
	 */
	public void parseOnePoint(String pointStr) {

		String tempStr = pointStr.substring(pointStr.indexOf("y"),
				pointStr.length());
		String yStr = tempStr.substring(tempStr.indexOf("=") + 1,
				tempStr.length());
		String xStr = pointStr.substring(pointStr.indexOf("=") + 1,
				pointStr.indexOf(" "));

		float curPointX = Float.valueOf(xStr);
		float curPointY = Float.valueOf(yStr);

		// 测试原始数据
		 //dragPointx = curPointX;
		 //dragPointy = curPointY;

		// 拉点操作
		if (lastPointX != curPointX && lastPointY != curPointY) {
			if (lastPointX != 0 || lastPointY != 0) {
				distance = (float) Math.sqrt(Math.pow((curPointX - lastPointX),
						2) + Math.pow((curPointY - lastPointY), 2));
				if (distance > MINPOINTDISTANCE) {
					float k = (float) (MINPOINTDISTANCE / distance);
					if (curPointX > lastPointX) {
						dragPointx = lastPointX + k * (curPointX - lastPointX);
					} else if (curPointX < lastPointX) {
						dragPointx = (lastPointX - k * (lastPointX - curPointX));
					} else {
						dragPointx = curPointX;
					}

					if (curPointY > lastPointY) {
						dragPointy = (lastPointY + k * (curPointY - lastPointY));
					} else if (curPointY < lastPointY) {
						dragPointy = lastPointY - k * (lastPointY - curPointY);
					} else {
						dragPointy = curPointY;
					}
				} else {
					dragPointx = curPointX;
					dragPointy = curPointY;
				}

				if (lastDragPointx != 0 || lastDragPointy != 0) {
					AddPoints(lastDragPointx, lastDragPointy, dragPointx,
							dragPointy);
				}

				lastDragPointx = dragPointx;
				lastDragPointy = dragPointy;
			} else {
				dragPointx = curPointX;
				dragPointy = curPointY; 
			}

			lastPointX = curPointX;
			lastPointY = curPointY;
		}

		// 单个坐标点数据消息发送
//		 Message msgStr = Message.obtain(mHandler, 100);
//		 Bundle bundle = new Bundle();
//		 bundle.putFloat("dragPointX", dragPointx);
//		 bundle.putFloat("dragPointY", dragPointy);
//		 msgStr.setData(bundle);
//		 msgStr.sendToTarget();
	}

	// 加点显示
	public void AddPoints(float x1, float y1, float x2, float y2) {
		if (x2 > x1) {
			for (int i = 0; i < array_size; i++) {
				arrayx[i] = (x2 - x1) * (i + 1) / 3 + x1;
			}
		} else if (x1 > x2) {
			for (int i = 0; i < array_size; i++) {
				arrayx[i] = x1 - (x1 - x2) * (i + 1) / 3;
			}
		} else {
			for (int i = 0; i < array_size; i++) {
				arrayx[i] = x2;
			}
		}

		if (y2 > y1) {
			for (int i = 0; i < array_size; i++) {
				arrayy[i] = (y2 - y1) * (i + 1) / 3 + y1;
			}
		} else if (y1 > y2) {
			for (int i = 0; i < array_size; i++) {
				arrayy[i] = y1 - (y1 - y2) * (i + 1) / 3;
			}
		} else {
			for (int i = 0; i < array_size; i++) {
				arrayy[i] = y2;
			}
		}

		// 多个坐标点数据消息发送
		Message msgStr = Message.obtain(mHandler, 101);
		Bundle bundle = new Bundle();
		bundle.putFloatArray("arrayx", arrayx);
		bundle.putFloatArray("arrayy", arrayy);
		msgStr.setData(bundle);
		msgStr.sendToTarget();
	}

	public float getSlope(float x1, float y1, float x2, float y2) {
		float slope = 0;

		slope = (y2 - y1) / (x2 - x1);
		mSlope = slope;

		return slope;
	}

	public double getAngle(float x1, float y1, float x2, float y2) {
		double angle = 0;

		angle = Math.atan2((y2 - y1), (x2 - x1));
		mAngle = angle;

		return angle;
	}

	public int getLineDir(float pointx, float pointy, float slope, float newx,
			float newy) {
		float verSlope = (-1) / slope; // 两垂直直线斜率乘积等亄1�7-1

		return 0;
	}

	public int parsePointStrings(ArrayList<String> arrayList, float[] arrayX,
			float[] arrayY) {

		float tempx = 0;
		float tempy = 0;
		String pointStr = null;
		String tempStr = null;
		String yStr = null;
		String xStr = null;

		for (int i = 0; i < arrayList.size(); i++) {
			pointStr = arrayList.get(i);
			if (pointStr != null && pointStr != "") {
				tempStr = pointStr.substring(pointStr.indexOf("y"),
						pointStr.length());
				yStr = tempStr.substring(tempStr.indexOf("=") + 1,
						tempStr.length());
				xStr = pointStr.substring(pointStr.indexOf("=") + 1,
						pointStr.indexOf(" "));

				tempx = Float.valueOf(xStr);
				arrayX[i] = tempx;
				tempy = Float.valueOf(yStr);
				arrayY[i] = tempy;
			}

		}

		for (int j = 0; j < arrayX.length; j++) {
			Log.d(TAG, "arrayX[" + j + "] = " + arrayX[j]);
			Log.d(TAG, "arrayY[" + j + "] = " + arrayY[j]);
		}

		return 0;
	}

	public int getDirection(float[] arrayX, float[] arrayY) {

		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;

		float pointx = arrayX[0];
		float pointy = arrayY[0];

		int px = (int) (arrayX[1] * AdspMapView.srceenRatioX);
		int py = (int) (AdspApplication.srceenHeight - arrayY[1]
				* AdspMapView.srceenRatioY);

		if (AdspMapView.horizontalRegion.contains(px, py)) {
			direction = EWEST;
		} else if (AdspMapView.verticalRegion.contains(px, py)) {
			direction = SNORTH;
		} else {
			Log.d(TAG, "========= this point is not in region");
			direction = lastDirection;
		}
		lastDirection = direction;

		for (int i = 1; i < arrayX.length; i++) {
			if (arrayX[i] > pointx) {
				right++;
			} else {
				left++;
			}

			if (arrayY[i] > pointy) {
				top++;
			} else {
				bottom++;
			}

		}

		if (direction == SNORTH) {
			if (top > bottom) {
				return NORTH;
			} else if (top < bottom) {
				return SOUTH;
			}
		}

		if (direction == EWEST) {
			if (left > right) {
				return WEST;
			} else if (left < right) {
				return EAST;
			}
		}

		return 0;
	}

	public void sortPointArray(int direct, ArrayList<String> pArrayList,
			float[] arrayX, float[] arrayY) {

		switch (direct) {
		case EAST: {
			float[] tempArray = new float[ARRAY_SIZE * 2];
			float pointx = arrayX[0];
			float max = maxDataArray(arrayX);

			if (isMoving) {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					if (pointx > max) {
						tempArray[j] = (float) (pointx + 0.1 * j);
					} else {
						tempArray[j] = pointx + (max - pointx)
								/ (ARRAY_SIZE * 2 - 1) * j;
					}

					if (tempArray[j] >= 14.305) {
						tempArray[j] = (float) 14.305;
					}
				}
			} else {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					tempArray[j] = pointx;
				}
			}

			// for (int i = 0; i < tempArray.length; i++) {
			// Log.d(TAG, "east tempArray[" + i + "] = " + tempArray[i]);
			// }

			lastShowX = tempArray[ARRAY_SIZE * 2 - 1];
			lastShowY = (float) 9.04;

			Message msg = Message.obtain(AdspApplication.g_Handler, 100);
			Bundle bundle = new Bundle();
			bundle.putFloatArray("ewest", tempArray);
			msg.setData(bundle);
			msg.sendToTarget();

			AdspApplication.g_Handler.sendEmptyMessage(10);
		}
			break;
		case SOUTH: {

			// float[] tempArray = bubbleSortToSal(arrayY);
			float[] tempArray = new float[ARRAY_SIZE * 2];
			float pointy = arrayY[0];
			float min = minDataArray(arrayY);

			if (isMoving) {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					if (pointy < min) {
						tempArray[j] = (float) (pointy - 0.1 * j);
					} else {
						tempArray[j] = pointy - (pointy - min)
								/ (ARRAY_SIZE * 2 - 1) * j;
					}

					if (tempArray[j] < 2.99) {
						tempArray[j] = (float) 2.99;
					}
				}
			} else {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					tempArray[j] = pointy;
				}
			}

			// for (int i = 0; i < tempArray.length; i++) {
			// Log.d(TAG, "south tempArray[" + i + "] = " + tempArray[i]);
			// }

			lastShowX = (float) 6.59;
			lastShowY = tempArray[ARRAY_SIZE * 2 - 1];

			Message msg = Message.obtain(AdspApplication.g_Handler, 101);
			Bundle bundle = new Bundle();
			bundle.putFloatArray("snorth", tempArray);
			msg.setData(bundle);
			msg.sendToTarget();

			AdspApplication.g_Handler.sendEmptyMessage(11);
		}
			break;
		case WEST: {

			float[] tempArray = new float[ARRAY_SIZE * 2];
			float pointx = arrayX[0];
			float min = minDataArray(arrayX);

			if (isMoving) {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					if (pointx < min) {
						tempArray[j] = (float) (pointx - 0.1 * j);
					} else {
						tempArray[j] = pointx - (pointx - min)
								/ (ARRAY_SIZE * 2 - 1) * j;
					}

					if (tempArray[j] < 4.756) {
						tempArray[j] = (float) 4.756;
					}
				}
			} else {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					tempArray[j] = pointx;
				}
			}

			for (int i = 0; i < ARRAY_SIZE * 2; i++) {
				Log.d(TAG, "west tempArray[" + i + "] = " + tempArray[i]);
			}

			lastShowX = tempArray[ARRAY_SIZE * 2 - 1];
			lastShowY = (float) 9.04;

			Message msg = Message.obtain(AdspApplication.g_Handler, 100);
			Bundle bundle = new Bundle();
			bundle.putFloatArray("ewest", tempArray);
			msg.setData(bundle);
			msg.sendToTarget();

			AdspApplication.g_Handler.sendEmptyMessage(12);
		}
			break;
		case NORTH: {

			// float[] tempArray = bubbleSortToBig(arrayY);
			float[] tempArray = new float[ARRAY_SIZE * 2];
			float pointy = arrayY[0];
			float max = maxDataArray(arrayY);

			if (isMoving) {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					if (pointy > max) {
						tempArray[j] = (float) (pointy + 0.1 * j);
					} else {
						tempArray[j] = pointy + (max - pointy)
								/ (ARRAY_SIZE * 2 - 1) * j;
					}

					if (tempArray[j] > 9.44) {
						tempArray[j] = (float) 9.44;
					}
				}
			} else {
				for (int j = 0; j < ARRAY_SIZE * 2; j++) {
					tempArray[j] = pointy;
				}
			}

			lastShowX = (float) 6.59;
			lastShowY = tempArray[ARRAY_SIZE * 2 - 1];
			for (int i = 0; i < ARRAY_SIZE * 2; i++) {
				Log.d(TAG, "north tempArray[" + i + "] = " + tempArray[i]);
			}

			Message msg = Message.obtain(AdspApplication.g_Handler, 101);
			Bundle bundle = new Bundle();
			bundle.putFloatArray("snorth", tempArray);
			msg.setData(bundle);
			msg.sendToTarget();

			AdspApplication.g_Handler.sendEmptyMessage(13);
		}
			break;
		default:
			AdspApplication.g_Handler.sendEmptyMessage(14);
			break;
		}

		Message msg = Message.obtain(AdspApplication.g_Handler, 105);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("pointArrayList", pArrayList);
		msg.setData(bundle);
		msg.sendToTarget();

	}

	// 排序数组各1�7�数据，从小到大
	public float[] bubbleSortToBig(float[] arrayX) {

		for (int i = 1; i < arrayX.length - 1; i++) {
			for (int j = i + 1; j < arrayX.length; j++) {
				if (arrayX[i] > arrayX[j]) {
					float temp = arrayX[i];
					arrayX[i] = arrayX[j];
					arrayX[j] = temp;
				}
			}
		}

		for (int k = 0; k < arrayX.length; k++) {
			Log.d(TAG, "======bubbleSortToBig arrayX[" + k + "] " + "= "
					+ arrayX[k]);
		}
		return arrayX;
	}

	// 排序数组各1�7�数据，从大到小
	public float[] bubbleSortToSal(float[] arrayY) {

		for (int i = 1; i < arrayY.length - 1; i++) {
			for (int j = i + 1; j < arrayY.length; j++) {
				if (arrayY[i] < arrayY[j]) {
					float temp = arrayY[i];
					arrayY[i] = arrayY[j];
					arrayY[j] = temp;
				}
			}
		}

		for (int k = 0; k < arrayY.length; k++) {
			Log.d(TAG, "======bubbleSortToSal arrayY[" + k + "] " + "= "
					+ arrayY[k]);
		}
		return arrayY;
	}

	public float maxDataArray(float[] array) {
		float max = 0;

		for (int i = 1; i < array.length; i++) {
			if (max == 0) {
				max = array[i];
			}
			if (max != 0 && max < array[i]) {
				max = array[i];
			}
		}

		return max;
	}

	public float minDataArray(float[] array) {
		float min = 0;

		for (int i = 1; i < array.length; i++) {
			if (min == 0) {
				min = array[i];
			}
			if (min != 0 && min > array[i]) {
				min = array[i];
			}
		}

		return min;
	}

	public void clearData() {
		mTimer.cancel();
	}
}
