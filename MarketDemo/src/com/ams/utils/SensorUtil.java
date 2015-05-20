package com.ams.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorUtil implements SensorEventListener {
	private String TAG = "SensorUtil";
	Context mContext;
	private SensorManager mSensorManager;
	private Sensor gSensor;
	private Sensor mSensor;
	private static final int UPTATE_INTERVAL_TIME = 1000; // 两次检测的时间间隔
	private long lastUpdateTime; // 上一次检测的时间
	private float[] mGData = new float[3];
	private float[] mMData = new float[3];
	private float[] mR = new float[16];
	private float[] mI = new float[16];
	private float[] mOrientation = new float[3];
	public static float azimuth;
	public static float lastAzimuth;
	public static boolean isChangeDirect;
	private static final int WALK_INTERVAL = 6000;
	private long curWalkTime;
	private long lastWalkTime;
	private float pitch;
	private float roll;
	public static String mDirection;
	// 记录上一次检测X、Y、Z轴的加速度值
	private float lastAX = 0;
	private float lastAY = 0;
	private float lastAZ = 0;
	// 记录是否移动
	private float[] recodeMove = new float[10];
	private int recodeNum = 0;
	public static boolean isMoving = false;
	public static boolean lastMoving = false;
	public static float maxValue;

	// 记录方向的变化

	public void sensorInit(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		// 现在的时间变成last时间
		lastUpdateTime = currentUpdateTime;

		int type = event.sensor.getType();
		float data[];
		if (type == Sensor.TYPE_ACCELEROMETER) {
			data = mGData;
		} else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
			data = mMData;
		} else {
			return;
		}

		for (int i = 0; i < 3; i++) {
			data[i] = event.values[i];
		}

//		float ax = mGData[0];
//		float ay = mGData[1];
//		float az = mGData[2];
//		if (lastAX == 0 && lastAY == 0 && lastAZ == 0) {
//			lastAX = ax;
//			lastAY = ay;
//			lastAZ = az;
//		} else {
//			float px = Math.abs(ax - lastAX);
//			float py = Math.abs(ay - lastAY);
//			float pz = Math.abs(az - lastAZ);
//
////			getMaxValue(px, py, pz);
////
////			Message msg = Message.obtain(AdspApplication.g_Handler, 106);
////			msg.obj = "X=" + px + " Y=" + py;
////			msg.sendToTarget();
//
//			float value = (float) Math.sqrt(px*px + py*py);
//			//Log.d(TAG, "============ value = " + value);
//			
//			recodeMove[recodeNum] = value;
//			recodeNum++;
//			float sum = 0;
//			if (recodeNum == 10) {
//				for (int k = 0; k < recodeNum; k++) {
//					sum += recodeMove[k];
//				}
//				//Log.d(TAG, "=========== sum = " + sum);
//				if (sum < 13.0f) {
//					lastMoving = false;
//				} else {
//					isMoving = true;
//				}
//
//				if (lastMoving != isMoving) {
//					isMoving = !lastMoving;
//				}
//
//				recodeNum = 0;
//			}
//			
////			Log.d(TAG, "===== px = "+ px + " py = "+ py + " pz = "+ pz);
////			if(px > 0 && py > 0){
////				lastMoving = true;
////			}else {
////				lastMoving = false;
////			}
////			
////			if(lastMoving != isMoving){
////				isMoving = !lastMoving;
////			}
//
//			lastAX = ax;
//			lastAY = ay;
//			lastAZ = az;
//		}

		// 根据设备传输过来的向量数据计算倾斜矩阵mR以及旋转矩阵mI
		SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
		// 根据旋转矩阵mR计算出设备的方向
		SensorManager.getOrientation(mR, mOrientation);

		/**
		 * values[0] ：azimuth 方向角，但用（磁场+加速度）得到的数据范围是（-180～180）,
		 * 也就是说，0表示正北，90表示正东，180/-180表示正南，-90表示正西。
		 * 而直接通过方向感应器数据范围是（0～359）360/0表示正北，90表示正东，180表示正南，270表示正西。 values[1]
		 * pitch 倾斜角 即由静止状态开始，前后翻转 values[2] roll 旋转角 即由静止状态开始，左右翻转
		 */
		// 手机绕z轴旋转的度数
		azimuth = (float) Math.toDegrees(mOrientation[0]);
		Log.d(TAG, "=========== azimuth = " + azimuth);
		lastAzimuth = azimuth;
		if(Math.sqrt(lastAzimuth - azimuth) > 60 && Math.sqrt(lastAzimuth - azimuth) < 120){
			isChangeDirect = true;
			lastWalkTime = System.currentTimeMillis();
		}
		curWalkTime = System.currentTimeMillis();
		if((curWalkTime - lastWalkTime) > WALK_INTERVAL){
			isChangeDirect = false;
		}
		Log.d(TAG, "=========== lastAzimuth = " + lastAzimuth);
		// 手机绕x轴旋转的度数
		pitch = (float) Math.toDegrees(mOrientation[1]);
		// 手机绕y轴旋转的度数
		roll = (float) Math.toDegrees(mOrientation[2]);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public void registerLister() {
		mSensorManager.registerListener(this, gSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void unRegisterLister() {
		mSensorManager.unregisterListener(this);
	}

	public float getMaxValue(float px, float py, float pz) {
		float max = 0;	
		max = (float) Math.sqrt(px*px + py*py + pz*pz);
		//Log.d(TAG, "============ max = " + max);

		return max;
	}

}
