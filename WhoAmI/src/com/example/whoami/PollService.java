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
 * �� ������ ������ ���� ��׶��� ����
 * (�۸�, �� ���� �ð�, �� ��� ��ġ��)
 * TODO: ���׶��� ���񽺷� ����
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPrefs.edit().remove(PREFS_FILE).commit();
	}

	// ���� ȣ��� ����
	@Override
	protected void onHandleIntent(Intent intent) {
		String currRunActivity = getActivityProc();
		
		// ��ó, ������ ����� �۰� ������ ��� ����ó��
		// TODO: ��ó�� ��� mPrefs �ʱ�ȭ
		String prevRunActivity = mPrefs.getString(PREFS_CURRENT_RUN_NAME,"");
		if(currRunActivity.contains(LAUNCHER)
				|| currRunActivity.equals(WHOAMI)
				|| currRunActivity.equals(prevRunActivity)){
			return;
		}
		
		// ������ ����
		App app = new App();
		app.setTitle(currRunActivity);
		Location loc = GPSManager.get(mContext).getLocation();
		AppPosition pos = new AppPosition();
		if(null != loc){	
			pos.setLatitude(loc.getLatitude());
			pos.setLongitude(loc.getLongitude());
		}
		app.setAppPos(pos);
		
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
}

/*@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Notification notification = new Notification(R.drawable.ic_launcher, "���� �����", System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), "Screen Service", "Foreground�� �����", null);

		startForeground(1, notification);
		return super.onStartCommand(intent, flags, startId);
	}*/