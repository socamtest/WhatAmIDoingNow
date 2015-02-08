package com.example.whoami;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * ����ȭ��
 */

public class SplashActivity extends Activity {
	
	private static final String TAG = "whoami.splashactivity";
	private static final int DELAY_TIME = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				/*
				 * 2015.02.07
				 * FATAL Exception �߻�
				 * java.lang.RuntimeException: Unable to start activity ComponentInfo
				 * try-catch �߰�
				 */
				try{
					startActivity(new Intent(SplashActivity.this, CalendarActivity.class));
					
					// �� ���� ����Ʈ ���� ���� �߰�
					AppList.get(SplashActivity.this).saveApps();
					
					finish();
				}catch(ActivityNotFoundException e){
					Log.i(TAG, "SplashActiivty onCreate ActivityNotFoundException");
				}catch(NullPointerException e){
					Log.i(TAG, "SplashActiivty onCreate NullPointerException");
				}
			}
		}, 1000);
	}
}
