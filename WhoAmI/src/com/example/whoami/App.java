package com.example.whoami;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * 앱 정보 
 */

public class App {
	
	private static final String TAG ="whoami.app";
	private String mStartDate;
	private String mUsingDay;
	private String mUsingTime;
	private String mUsingHour; //시간별 위치정보를 구하기 위해서 추가
	private String mTitle;
	private AppPosition mAppPos;
	private String mAddr;
	private UUID mUId;
	
	
	// JSON 식별자
	private static final String JSON_START_DATE = "startdate";
	private static final String JSON_USING_DAY = "usingday";
	private static final String JSON_USING_TIME = "usingtime";
	private static final String JSON_USING_HOUR = "usinghour";
	private static final String JSON_TITLE = "title";
	private static final String JSON_LATITUDE = "latitude";
	private static final String JSON_LONGITUDE = "longitude";
	private static final String JSON_ADDRESS = "address";
	private static final String JSON_UID = "uid";
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_START_DATE, mStartDate);
		json.put(JSON_USING_DAY, mUsingDay);
		json.put(JSON_USING_TIME, mUsingTime);
		json.put(JSON_USING_HOUR, mUsingHour);
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_LATITUDE, mAppPos.getLatitude());
		json.put(JSON_LONGITUDE, mAppPos.getLongitude());
		json.put(JSON_ADDRESS, mAddr);
		json.put(JSON_UID, mUId.toString());
		return json;
	}
	
	public App(JSONObject json)throws JSONException{
		try{
			mAppPos = new AppPosition();
			mUId = UUID.fromString(json.getString(JSON_UID));
			if(json.has(JSON_TITLE)){
				mTitle = json.getString(JSON_TITLE);
			}
			if(json.has(JSON_START_DATE)){
				mStartDate = json.getString(JSON_START_DATE);
			}
			if(json.has(JSON_USING_DAY)){
				mUsingDay = json.getString(JSON_USING_DAY);
			}
			if(json.has(JSON_USING_TIME)){
				mUsingTime = json.getString(JSON_USING_TIME);
			}
			if(json.has(JSON_USING_HOUR)){
				mUsingHour = json.getString(JSON_USING_HOUR);
			}
			if(json.has(JSON_LATITUDE)){
				mAppPos.setLatitude(json.getDouble(JSON_LATITUDE));
			}
			if(json.has(JSON_LONGITUDE)){
				mAppPos.setLongitude(json.getDouble(JSON_LONGITUDE));
			}
			if(json.has(JSON_LATITUDE) && json.has(JSON_LONGITUDE)){
				mAppPos.setLatLng(json.getDouble(JSON_LATITUDE), json.getDouble(JSON_LONGITUDE));
			}
			if(json.has(JSON_ADDRESS)){
				mAddr = json.getString(JSON_ADDRESS);
			}
		}catch(Exception e){}
	}
	
	public App(App a){
		this.mAppPos = a.mAppPos;
		this.mStartDate = a.mStartDate;
		this.mUsingDay = a.mUsingDay;
		this.mUsingTime = a.mUsingTime;
		this.mUsingHour = a.mUsingHour;
		this.mAddr = a.mAddr;
		this.mTitle = a.mTitle;
		this.mUId = a.mUId;
	}
	
	public App(){
		getNow();
		mUId = UUID.randomUUID();
	}

	public UUID getUId() {
		return mUId;
	}

	public String getStartDate() {
		return mStartDate;
	}

	public String getUsingDay() {
		return mUsingDay;
	}
	
	public String getUsingTime(){
		return mUsingTime;
	}
	
	public String getUsingHour() {
		return mUsingHour;
	}

	public void setUsingHour(String usingHour) {
		mUsingHour = usingHour;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public AppPosition getAppPos() {
		return mAppPos;
	}

	public void setAppPos(AppPosition appPos) {
		mAppPos = appPos;
	}
	
	public String getAddr() {
		return mAddr;
	}

	public void setAddr(String addr) {
		mAddr = addr;
	}

	private void getNow(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		try{
			mStartDate = new SimpleDateFormat("yy년MM월dd일HH시mm분ss초", Locale.KOREA).format(date);
			mUsingDay = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(date);
			mUsingTime = new SimpleDateFormat("HH시mm분ss초", Locale.KOREA).format(date);
			mUsingHour = new SimpleDateFormat("HH", Locale.KOREA).format(date);
		}catch(NullPointerException e){
			Log.e(TAG, "App getNow() NullPointerException" + e.toString());
		}catch(IllegalArgumentException e){
			Log.e(TAG, "App getNow() IllegalArgumentException" + e.toString());
		}
	}
	
	public Drawable getAppIcon(Context c){
		try{
			return c.getPackageManager().getApplicationIcon(mTitle);
		}catch(NameNotFoundException e){
			Log.e(TAG, "App getAppIcon() NameNotFoundException" + e.toString());
			return null;
		}
	}
	
	public String getAppName(Context c){
		try{
			if(null != mTitle)
				return c.getPackageManager().getApplicationLabel(c.getPackageManager().getApplicationInfo(mTitle, PackageManager.GET_UNINSTALLED_PACKAGES)).toString();
		}catch(NameNotFoundException e){
			Log.e(TAG, "App getAppName() NameNotFoundException" + e.toString());
			return mTitle;
		}
		return mTitle;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
}
