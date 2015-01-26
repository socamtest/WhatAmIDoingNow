package com.example.whoami;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 앱 정보 
 */

public class App {
	
	private static final String TAG ="whoami.app";
	private String mStartDate;
	private String mUsingDay;
	private String mTitle;
	private AppPosition mAppPos;
	private UUID mUId;
	
	// JSON 식별자
	private static final String JSON_START_DATE = "startdate";
	private static final String JSON_USING_DAY = "usingday";
	private static final String JSON_TITLE = "title";
	private static final String JSON_LATITUDE = "latitude";
	private static final String JSON_LONGITUDE = "longitude";
	private static final String JSON_UID = "uid";
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_START_DATE, mStartDate);
		json.put(JSON_USING_DAY, mUsingDay);
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_LATITUDE, mAppPos.getLatitude());
		json.put(JSON_LONGITUDE, mAppPos.getLongitude());
		json.put(JSON_UID, mUId.toString());
		return json;
	}
	
	public App(JSONObject json)throws JSONException{
		try{
			mAppPos = new AppPosition(); //초기화 안해서 3시간 삽질
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
			if(json.has(JSON_LATITUDE)){
				mAppPos.setLatitude(json.getDouble(JSON_LATITUDE));
			}
			if(json.has(JSON_LONGITUDE)){
				mAppPos.setLongitude(json.getDouble(JSON_LONGITUDE));
			}
		}catch(Exception e){}
	}
	
	public App(App a){
		this.mAppPos = a.mAppPos;
		this.mStartDate = a.mStartDate;
		this.mUsingDay = a.mUsingDay;
		this.mTitle = a.mTitle;
		this.mUId = a.mUId;
	}
	
	public App(){
		getNow();
		mUId = UUID.randomUUID();
	}

	// UUID 디버깅시 값이 변하나?
	public UUID getUId() {
		return mUId;
	}

	public String getStartDate() {
		return mStartDate;
	}

	public String getUsingDay() {
		return mUsingDay;
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
	
	private void getNow(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		try{
			mStartDate = new SimpleDateFormat("yy년MM월dd일HH시mm분ss초", Locale.KOREA).format(date);
			mUsingDay = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(date);
		}catch(NullPointerException e){
			Log.e(TAG, "App getNow() NullPointerException" + e.toString());
		}catch(IllegalArgumentException e){
			Log.e(TAG, "App getNow() IllegalArgumentException" + e.toString());
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
}
