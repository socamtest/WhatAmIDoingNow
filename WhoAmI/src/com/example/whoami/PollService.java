package com.example.whoami;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

/**
 * 앱 정보를 얻어오기 위한 백그라운드 서비스
 * (앱명, 앱 시작 시간, 앱 사용 위치등)
 * TODO: 포그라운드 서비스로 변경
 */

public class PollService extends IntentService {
	private static final String TAG="PollService";
	private static final int POLL_INTERVAL= 1000 * 5; // poll 간격
	private static final String PREFS_FILE = "lastActivity"; // 마지막 실행된 액티비티 저장을 위한 SHAREDPREFERENCES
	private static final String PREFS_CURRENT_RUN_NAME = "lastRunActivity"; // 마지막 실행된 액티비티명
	private static final String LAUNCHER = "launcher";
	private static final String WHOAMI = "com.example.whoami";
	private SharedPreferences mPrefs;
	private static Context mContext;
	
	public PollService(){
		super(TAG);
	}
	public PollService(Context c){
		super(TAG);
		mContext = c;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mPrefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE); //onCreate에서 호출하지 않음 오류
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPrefs.edit().remove(PREFS_FILE).commit();
	}

	// 서비스 호출시 동작
	@Override
	protected void onHandleIntent(Intent intent) {
		String currRunActivity = getActivityProc();
		
		// 런처, 직전에 사용한 앱과 동일할 경우 예외처리
		// TODO: 런처일 경우 mPrefs 초기화
		String prevRunActivity = mPrefs.getString(PREFS_CURRENT_RUN_NAME,"");
		if(currRunActivity.contains(LAUNCHER)
				|| currRunActivity.equals(WHOAMI)
				|| currRunActivity.equals(prevRunActivity)){
			return;
		}
		
		// 앱정보 저장
		App app = new App();
		app.setTitle(currRunActivity);
		Location loc = GPSManager.get(mContext).getLocation();
		AppPosition pos = new AppPosition();
		if(null != loc){	
			pos.setLatitude(loc.getLatitude());
			pos.setLongitude(loc.getLongitude());
		}
		app.setAppPos(pos);
		
		// 해당리스트 바로 사용하면 오류 발생
		// UI 관련 로직은 MAIN UI 에서 처리해야함
		// 우선 MAIN UI에서 파일에서 로드하여 처리(갱신이 한번 늦게 됨)
		AppList.get(mContext).addApp(app);
		
		// 직전실행 앱 SHAREDPREFERENCES에 저장
		mPrefs.edit().putString(PREFS_CURRENT_RUN_NAME, currRunActivity).commit();
		
	}
	
	// 포그라운드앱 이름
	public String getActivityProc(){
		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appList = am.getRunningAppProcesses();
		for(int i=0; i<appList.size(); i++){
			if(appList.get(i).importance ==  RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
				return appList.get(i).processName;
		}
		return null;
	}
	
	// 서비스 알람 설정(On/Off)
	public void setServiceAlarm(Context context, boolean isOn){
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context,0,i,0);
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		if(isOn){
			am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
		}else{
			am.cancel(pi);
			pi.cancel();
		}
	}
	
	// 알람 상태 확인
	public boolean isServiceAlarmOn(Context context){
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}
	
	public boolean removeSharedPref(String key){
		if(true == mPrefs.edit().remove(key).commit())
			return true;
		return false;
	}
}

/*@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Notification notification = new Notification(R.drawable.ic_launcher, "서비스 실행됨", System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), "Screen Service", "Foreground로 실행됨", null);

		startForeground(1, notification);
		return super.onStartCommand(intent, flags, startId);
	}*/