package com.ams.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ams.ui.AdspApplication;
import com.ams.ui.RegisterAct;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DataClient extends Thread {
	private static String TAG = "===== DataClient =====";

	// 服务器信�??
	private final String ServerIp = "192.168.1.141";
	private final int ServerPort = 8882;

	private Socket mSocket = null;
	private BufferedReader mBufferedReader = null;
	private static PrintWriter mPrintWriter = null;
	private int number;
	private char[] buffer;
	private boolean isFistShow = true;
	private String msg = null;
	private String lastMsg = null;
	private String bufMsg = null;
	private CacheBuffer cacheBuffer;
	private Handler mHandler;
	// 连接成功与否标识
	private boolean flag;
	public static boolean isDisconnect;

	// 判断两点距离，检测是否发送点数据
	private static final float DISTANCEPOINT = (float) 2.8;

	private Date date;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private String time;
	private String str;
	String filePath = Environment.getExternalStorageDirectory() + "/point.txt";
	OutputStream outputStream;
	OutputStreamWriter outputStreamWriter;
	private Context mContext;

	private ArrayList<String> pointArrayList = new ArrayList<String>(
			DataProcess.ARRAY_SIZE);

	public DataClient(CacheBuffer cacheBuffer, Handler handler, Context mContext) {
		Log.d(TAG, "init socket before !!!");
		this.cacheBuffer = cacheBuffer;
		this.mContext = mContext;
		mHandler = handler;
		buffer = new char[32];

		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			outputStream = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(outputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			Log.d(TAG, "connect to adsp server...");
			mSocket = new Socket(ServerIp, ServerPort);
			mBufferedReader = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
			mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
			flag = true;
			Log.d(TAG, "connect adsp server success...");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			flag = false;
			isDisconnect = true;
		}

		// String isSend = Util.getSendFlag(mContext, RegisterAct.g_Acount);
		//
		// if (isSend.equalsIgnoreCase("New")) {
		// String acountMsg = Util.getAcountMsg(mContext, "Acount");
		// sendMessage(acountMsg + "\n");
		//
		// Util.saveSendFlag(mContext, RegisterAct.g_Acount, "Old");
		// }

		// hsl update
		// String account = Util.getLoginUser(mContext);
		// if (!account.equals("")) {
		// sendMessage("user:" + account + "\n");
		// }

		//sendMessage(AdspApplication.macAddr + "\n");

		while (flag) {
			try {
				mSocket.sendUrgentData(0xFF);
				isDisconnect = false;
				if ((msg = mBufferedReader.readLine()) != null && msg != "") {
					Log.d(TAG, "data client   msg = " + msg);
					cacheBuffer.putCacheItem(msg);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isDisconnect = true;
				break;
			}
		}
	}

	public static void sendMessage(String msg) {
		if (!isDisconnect) {
			mPrintWriter.println(msg);
		}
	}

	public void disConnect() {
		try {
			flag = false;
			outputStream.close();
			outputStreamWriter.close();
			if (mBufferedReader != null) {
				mBufferedReader.close();
			}
			if (mPrintWriter != null) {
				mPrintWriter.close();
			}
			if (mSocket != null) {
				mSocket.close();
			}
			isDisconnect = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 判断两点距离大于2，则不放入缓存buffer�??
	 */
	public String checkErrorPoint(String curPoint, String lastPoint) {

		float curPointx, curPointy;
		float lastPointx, lastPointy;
		float disx, disy;
		float distance;
		String tempStr = null;
		String yStr = null;
		String xStr = null;

		tempStr = curPoint.substring(curPoint.indexOf("y"), curPoint.length());
		yStr = tempStr.substring(tempStr.indexOf("=") + 1, tempStr.length());
		xStr = curPoint.substring(curPoint.indexOf("=") + 1,
				curPoint.indexOf(" "));
		curPointx = Float.valueOf(xStr);
		curPointy = Float.valueOf(yStr);

		tempStr = lastPoint.substring(lastPoint.indexOf("y"),
				lastPoint.length());
		yStr = tempStr.substring(tempStr.indexOf("=") + 1, tempStr.length());
		xStr = lastPoint.substring(lastPoint.indexOf("=") + 1,
				lastPoint.indexOf(" "));
		lastPointx = Float.valueOf(xStr);
		lastPointy = Float.valueOf(yStr);

		disx = Math.abs(curPointx - lastPointx);
		disy = Math.abs(curPointy - lastPointy);
		distance = (float) Math.sqrt(disx * disx + disy * disy);

		if (distance > DISTANCEPOINT) {
			return null;
		}

		return curPoint;
	}
}
