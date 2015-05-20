package com.ams.utils;

import java.util.ArrayList;

public class CacheBuffer {
	private String TAG = "====== CacheBuffer ======";
	private ArrayList<String> pointArray;
	private int size;
	private int wp;
	private int rp;

	public CacheBuffer(int large) {
		size = large;
		pointArray = new ArrayList<String>(size);
		wp = rp = 0;
	}

	// writeCheck = true 可写 ， 否则， 可读
	public int checkSpace(boolean writeCheck) {
		int s;
		if (writeCheck) {
			if (wp > rp) {
				s = rp - wp + size - 1;
			} else if (wp < rp) {
				s = rp - wp - 1;
			} else
				s = size - 1;
		} else {
			if (wp > rp) {
				s = wp - rp;
			} else if (wp < rp) {
				s = wp - rp + size;
			} else {
				s = 0;
			}
		}
		return s;
	}

	public String getCacheItem() {
		String pointString = null;
		int remain;
		if ((remain = checkSpace(false)) == 0) {
			return null;
		}

		pointString = pointArray.get(rp);
		// Log.d(TAG, "======== get str = " + pointString);
		if (pointString != null) {
			rp++;
		}
		
		if (rp == size) {
			rp = 0;
		}

		return pointString;
	}

	public int putCacheItem(String str) {
		int remain;
		if ((remain = checkSpace(true)) == 0) {
			return -1;
		}
//		Log.d(TAG, "======= add str = " + str);
		if (str != null && str != "") {
			pointArray.add(str);
			wp++;
			if (wp == size) {
				wp = 0;
			}
		}
		return 0;
	}

	public void closeCache() {
		pointArray = null;
		rp = wp = 0;
	}
}
