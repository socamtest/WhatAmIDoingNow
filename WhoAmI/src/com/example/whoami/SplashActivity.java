package com.example.whoami;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 시작화면
 */

public class SplashActivity extends Activity {
	
	private static final int DELAY_TIME = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, CalendarActivity.class));
				
				// 앱 저장 리스트 저장 로직 추가
				AppList.get(SplashActivity.this).saveApps();
				
				finish();
			}
		}, 1000);
	}
}
