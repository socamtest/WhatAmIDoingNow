package com.example.whoami;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.PowerManager;
import android.util.Log;

/**
 * 앱 정보를 얻어오기 위한 백그라운드 서비스
 * (앱명, 앱 시작 시간, 앱 사용 위치등)
 * TODO: 포그라운드 서비스로 변경
 *  whoami 시작 전까지 정보를 리스트에 관리하므로 앱이 꺼질 경우 리스트 사라질 수 있음
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
	
	// 2015.01.30 FusedLocation
	private FusedLocation mFusedLocation;
	private static Location mLocation;
	
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
		
		// 2015.01.30 FusedLocation start
		// TODO: FusedLocation 생성 시점 및 onConnected 지속적으로 호출문제
/*		if(null != mContext){
			mFusedLocation = new FusedLocation(mContext, new FusedLocationReceiver(){
				public void onLocationChanged(){
					updateLocation();
				}
			});
		}*/
		
		// 2015.02.07
		// 화면 켜져있을경우에만 FusedLocation 생성
		// TODO: 이럴 경우 위치값을 제대로 얻어올 수 있을지 테스트 필요(짧은시간)
		if(null != mContext 
				&& true == ((PowerManager)mContext.getSystemService(Context.POWER_SERVICE)).isScreenOn()
				&& null == mFusedLocation){
			Log.i(TAG, "PollService onCreate()  Create FusedLocation because [isScreenOn is On] ");
			mFusedLocation = new FusedLocation(mContext, new FusedLocationReceiver(){
				public void onLocationChanged(){
					updateLocation();
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPrefs.edit().remove(PREFS_FILE).commit();
		if(null != mFusedLocation)
			mFusedLocation.disconnectGoogleApiClient();
	}
	
	public static boolean isScreenOn(Context context) {
		try{
			return ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
		}catch(Exception e){
			Log.i(TAG, "PollService isScreenOn : error return false" );
			return false;
		}
	}

	// 서비스 호출시 동작
	@Override
	protected void onHandleIntent(Intent intent) {
		String currRunActivity = getActivityProc();
		
		// 화면이 꺼져있을 경우 리턴
		if(null != mContext 
			&& false == ((PowerManager)mContext.getSystemService(Context.POWER_SERVICE)).isScreenOn()){
			Log.i(TAG, "PollService onHandleIntent()  finish because [isScreenOn is Off] ");
			return;
		}
		Log.i(TAG, "PollService onHandleIntent()  proceeding because [mContext != null && isScreenOn is On] ");
		
		// 런처, 직전에 사용한 앱과 동일할 경우 예외처리
		// TODO: 런처일 경우 mPrefs 초기화
		String prevRunActivity = mPrefs.getString(PREFS_CURRENT_RUN_NAME,"");
		if(currRunActivity.contains(LAUNCHER)
				|| currRunActivity.equals(WHOAMI)
				|| currRunActivity.equals(prevRunActivity)){
			return;
		}
		
		// 2015.01.30 FusedLocation start
		// FusedLocation API 사용
		updateLocation();
		App app = new App();
		app.setTitle(currRunActivity);
		AppPosition pos = new AppPosition();
		if(null != mLocation){
			pos.setLatitude(mLocation.getLatitude());
			pos.setLongitude(mLocation.getLongitude());
			pos.setLatLng(mLocation.getLatitude(), mLocation.getLongitude());
			
		}else{
			// FusedLocation API를 이용하여 위치 저장 실패시 LocationManager 이용하여 위치 저장
			// 아래 로직 추가시 GPS 계속 켜짐
			/*Location loc = GPSManager.get(mContext).getLocation();
			if(null != loc){
				pos.setLatitude(loc.getLatitude());
				pos.setLongitude(loc.getLongitude());
			}*/
		}
		app.setAddr(GPSManager.get(mContext).getAddressFromLocation(pos.getLatitude(), pos.getLongitude()));
		app.setAppPos(pos);
		
		// mFusedLocation.disconnectGoogleApiClient();
		// 2015.01.30 FusedLocation end

		
		//////////////////////////////////////////////////////////////
		// 2015.01.30 FusedLocation 테스트 위해서 주석처리 start
		
		// 앱정보 저장
		/*App app = new App();
		app.setTitle(currRunActivity);
		Location loc = GPSManager.get(mContext).getLocation();
		AppPosition pos = new AppPosition();
		if(null != loc){	
			pos.setLatitude(loc.getLatitude());
			pos.setLongitude(loc.getLongitude());
		}
		app.setAppPos(pos);
		
		// 주소 구하는 부분 추가
		// TODO: 성능 이슈 해결
		app.setAddr(GPSManager.get(mContext).getAddressFromLocation(app.getAppPos().getLatitude(), app.getAppPos().getLongitude()));*/
		
		// 2015.01.30 FusedLocation 테스트 위해서 주석처리 end
		////////////////////////////////////////////////////////////
		
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
	
	// 2015.01.30 FusedLocation start
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		// 화면 꺼져있을경우 GoogleApiClient disconnect
		if(null != mContext 
				&& false == ((PowerManager)mContext.getSystemService(Context.POWER_SERVICE)).isScreenOn()){
				if(null != mFusedLocation){
					Log.i(TAG, "PollService onStartCommand() disconnect GoogleApiClient because [isScreenOn is Off] ");
					mFusedLocation.disconnectGoogleApiClient();
				}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void updateLocation(){
		Log.i(TAG, "updateLocation()");
		if(null != mFusedLocation){
			mLocation = mFusedLocation.getLocation();
			
			if(null != mLocation){
				Log.i(TAG, "Latitude : " + mLocation.getLatitude());
				Log.i(TAG, "Longitude : " + mLocation.getLongitude());
				Log.i(TAG, "Altitude : " + mLocation.getAltitude());
				Log.i(TAG, "Accuracy : " + mLocation.getAccuracy());
				Log.i(TAG, "time : " + mLocation.getTime());
				Log.i(TAG, "provider : " + mLocation.getProvider());
			}
		}
	}
	// 2015.01.30 FusedLocation end
}
