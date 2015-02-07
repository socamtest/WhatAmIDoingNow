package com.example.whoami;

import java.io.File;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public final class Util {
	
	/**
	 * WHOAMI 에서 사용하기 위한 유틸 클래스
	 */
	
	private static final String TAG = "whoami.util";
	
	public static final class Const {
		
		// 상수는 1000 부터 시작
		public static final int INIT_TO_DAY = 1000; // 날짜로 초기화
	}
	
	public static final boolean FileExists(Context context, String path) {
		File file = null;
		file = context.getFileStreamPath(path);
		Log.i(TAG, "Util FileExists() file path is : "+ file );
		if(null != file && true == file.exists())
			return true;
		
		return false;
	}
	
	public static boolean isScreenOn(Context context) {
		try{
			if(null == context){
				Log.i(TAG, "Util context is null :  return false" );
				return false;
			}
			Log.i(TAG, "Util isScreenOn is Success" );
			boolean result = ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
			return result;
		}catch(Exception e){
			Log.i(TAG, "Util isScreenOn Exception : return false" );
			return false;
		}
	}
}