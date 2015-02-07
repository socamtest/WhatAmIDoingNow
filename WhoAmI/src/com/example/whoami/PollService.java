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
 * �� ������ ������ ���� ��׶��� ����
 * (�۸�, �� ���� �ð�, �� ��� ��ġ��)
 * TODO: ���׶��� ���񽺷� ����
 *  whoami ���� ������ ������ ����Ʈ�� �����ϹǷ� ���� ���� ��� ����Ʈ ����� �� ����
 */

public class PollService extends IntentService {
	private static final String TAG="PollService";
	private static final int POLL_INTERVAL= 1000 * 5; // poll ����
	private static final String PREFS_FILE = "lastActivity"; // ������ ����� ��Ƽ��Ƽ ������ ���� SHAREDPREFERENCES
	private static final String PREFS_CURRENT_RUN_NAME = "lastRunActivity"; // ������ ����� ��Ƽ��Ƽ��
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
		mPrefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE); //onCreate���� ȣ������ ���� ����
		
		// 2015.01.30 FusedLocation start
		// TODO: FusedLocation ���� ���� �� onConnected ���������� ȣ�⹮��
/*		if(null != mContext){
			mFusedLocation = new FusedLocation(mContext, new FusedLocationReceiver(){
				public void onLocationChanged(){
					updateLocation();
				}
			});
		}*/
		
		// 2015.02.07
		// ȭ�� ����������쿡�� FusedLocation ����
		// TODO: �̷� ��� ��ġ���� ����� ���� �� ������ �׽�Ʈ �ʿ�(ª���ð�)
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

	// ���� ȣ��� ����
	@Override
	protected void onHandleIntent(Intent intent) {
		String currRunActivity = getActivityProc();
		
		// ȭ���� �������� ��� ����
		if(null != mContext 
			&& false == ((PowerManager)mContext.getSystemService(Context.POWER_SERVICE)).isScreenOn()){
			Log.i(TAG, "PollService onHandleIntent()  finish because [isScreenOn is Off] ");
			return;
		}
		Log.i(TAG, "PollService onHandleIntent()  proceeding because [mContext != null && isScreenOn is On] ");
		
		// ��ó, ������ ����� �۰� ������ ��� ����ó��
		// TODO: ��ó�� ��� mPrefs �ʱ�ȭ
		String prevRunActivity = mPrefs.getString(PREFS_CURRENT_RUN_NAME,"");
		if(currRunActivity.contains(LAUNCHER)
				|| currRunActivity.equals(WHOAMI)
				|| currRunActivity.equals(prevRunActivity)){
			return;
		}
		
		// 2015.01.30 FusedLocation start
		// FusedLocation API ���
		updateLocation();
		App app = new App();
		app.setTitle(currRunActivity);
		AppPosition pos = new AppPosition();
		if(null != mLocation){
			pos.setLatitude(mLocation.getLatitude());
			pos.setLongitude(mLocation.getLongitude());
			pos.setLatLng(mLocation.getLatitude(), mLocation.getLongitude());
			
		}else{
			// FusedLocation API�� �̿��Ͽ� ��ġ ���� ���н� LocationManager �̿��Ͽ� ��ġ ����
			// �Ʒ� ���� �߰��� GPS ��� ����
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
		// 2015.01.30 FusedLocation �׽�Ʈ ���ؼ� �ּ�ó�� start
		
		// ������ ����
		/*App app = new App();
		app.setTitle(currRunActivity);
		Location loc = GPSManager.get(mContext).getLocation();
		AppPosition pos = new AppPosition();
		if(null != loc){	
			pos.setLatitude(loc.getLatitude());
			pos.setLongitude(loc.getLongitude());
		}
		app.setAppPos(pos);
		
		// �ּ� ���ϴ� �κ� �߰�
		// TODO: ���� �̽� �ذ�
		app.setAddr(GPSManager.get(mContext).getAddressFromLocation(app.getAppPos().getLatitude(), app.getAppPos().getLongitude()));*/
		
		// 2015.01.30 FusedLocation �׽�Ʈ ���ؼ� �ּ�ó�� end
		////////////////////////////////////////////////////////////
		
		// �ش縮��Ʈ �ٷ� ����ϸ� ���� �߻�
		// UI ���� ������ MAIN UI ���� ó���ؾ���
		// �켱 MAIN UI���� ���Ͽ��� �ε��Ͽ� ó��(������ �ѹ� �ʰ� ��)
		AppList.get(mContext).addApp(app);
		
		// �������� �� SHAREDPREFERENCES�� ����
		mPrefs.edit().putString(PREFS_CURRENT_RUN_NAME, currRunActivity).commit();
		
	}
	
	// ���׶���� �̸�
	public String getActivityProc(){
		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appList = am.getRunningAppProcesses();
		for(int i=0; i<appList.size(); i++){
			if(appList.get(i).importance ==  RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
				return appList.get(i).processName;
		}
		return null;
	}
	
	// ���� �˶� ����(On/Off)
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
	
	// �˶� ���� Ȯ��
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
		
		// ȭ�� ����������� GoogleApiClient disconnect
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
