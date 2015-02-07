package com.example.whoami;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * 순간을 기록(언제, 어디서, 끄적끄적)
 * TODO: 태그 추가
 */

public class Moment {
	
	private String TAG = "whoami.moment";
	private UUID mUId;
	private String mDate;// 몇년몇월몇일몇시몇분몇초
	private String mDay; // 몇년몇월몇일
	private String mTime;// 몇시몇분몇초
	private String mHour; // 시간
	private Photo mPhoto;
	private Position mPos;
	private String mAddr;
	private String mMurmur;
	private String mTag; // 태그별 기록 검색 위한 변수
	
	// JSON 식별자
	private static final String JSON_UID = "uid";
	private static final String JSON_MOMENT_DATE = "momentdate";
	private static final String JSON_MOMENT_DAY = "momentday";
	private static final String JSON_MOMENT_TIME = "momenttime";
	private static final String JSON_MOMENT_HOUR = "jsonmomenthour";
	private static final String JSON_MOMENT_PHOTO = "jsonmomentphoto";
	private static final String JSON_MOMENT_POS = "jsonmomentpos";
	private static final String JSON_MOMENT_ADDRESS = "jsonmomentaddr";
	private static final String JSON_MOMENT_MURMUR = "jsonmomentmurmur";
	private static final String JSON_MOMENT_TAG = "jsonmomenttag";
	
	private static final String JSON_LATITUDE = "latitude";
	private static final String JSON_LONGITUDE = "longitude";
	
	public Moment(){
		getNow();
		mUId = UUID.randomUUID();
	}
	
	public Moment(Moment m){
		this.mUId = m.mUId;
		this.mDate = m.mDate;
		this.mDay = m.mDay;
		this.mTime = m.mTime;
		this.mHour = m.mHour;
		this.mPhoto = m.mPhoto;
		this.mPos = m.mPos;
		this.mAddr = m.mAddr;
		this.mMurmur = m.mMurmur;
		this.mTag = m.mTag;
	}
	
	public Moment(JSONObject json) throws JSONException{
		if(json.has(JSON_UID))
			mUId = UUID.fromString(json.getString(JSON_UID));
		if(json.has(JSON_MOMENT_DATE))
			mDate = json.getString(JSON_MOMENT_DATE);
		if(json.has(JSON_MOMENT_DAY))
			mDay = json.getString(JSON_MOMENT_DAY);
		if(json.has(JSON_MOMENT_TIME))
			mTime = json.getString(JSON_MOMENT_TIME);
		if(json.has(JSON_MOMENT_HOUR))
			mHour = json.getString(JSON_MOMENT_HOUR);
		if(json.has(JSON_MOMENT_PHOTO))
			mPhoto = new Photo(JSON_MOMENT_PHOTO);
		
		/**
		 * JSON 파일안에 클래스 들어 있을 경우 JSONObject로 가져옴
		 */
		
		if(json.has(JSON_MOMENT_POS)){
			mPos = new Position((JSONObject)json.get(JSON_MOMENT_POS));
		}
		if(json.has(JSON_MOMENT_ADDRESS))
			mAddr = json.getString(JSON_MOMENT_ADDRESS);
		if(json.has(JSON_MOMENT_MURMUR))
				mMurmur = json.getString(JSON_MOMENT_MURMUR);
		if(json.has(JSON_MOMENT_TAG))
			mTag = json.getString(JSON_MOMENT_TAG);
	}

	public JSONObject toJSON() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_UID, mUId);
		json.put(JSON_MOMENT_DATE, mDate);
		json.put(JSON_MOMENT_DAY, mDay);
		json.put(JSON_MOMENT_TIME, mTime);
		json.put(JSON_MOMENT_HOUR, mHour);
		if(null != mPhoto)
			json.put(JSON_MOMENT_PHOTO, mPhoto.toJSON());
		if(null != mPos)
			json.put(JSON_MOMENT_POS, mPos.toJSON());
		json.put(JSON_MOMENT_ADDRESS, mAddr);
		json.put(JSON_MOMENT_MURMUR, mMurmur);
		json.put(JSON_MOMENT_TAG, mTag);
		
		return json;
	}
	
	private void getNow(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		try{
			mDate = new SimpleDateFormat("yy년MM월dd일HH시mm분ss초", Locale.KOREA).format(date);
			mDay = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(date);
			mTime = new SimpleDateFormat("HH시mm분ss초", Locale.KOREA).format(date);
			mHour = new SimpleDateFormat("HH", Locale.KOREA).format(date);
		}catch(NullPointerException e){
			Log.e(TAG, "Moment getNow() NullPointerException" + e.toString());
		}catch(IllegalArgumentException e){
			Log.e(TAG, "Moment getNow() IllegalArgumentException" + e.toString());
		}
	}

	public UUID getUId() {
		return mUId;
	}

	public String getStartDate() {
		return mDate;
	}

	public String getUsingDay() {
		return mDay;
	}

	public String getUsingTime() {
		return mTime;
	}

	public String getUsingHour() {
		return mHour;
	}

	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}

	public Position getPos() {
		return mPos;
	}

	public void setPos(Position pos) {
		mPos = pos;
	}

	public String getMurmur() {
		return mMurmur;
	}

	public void setMurmur(String murmur) {
		mMurmur = murmur;
	}

	public String getAddr() {
		return mAddr;
	}

	public void setAddr(String addr) {
		mAddr = addr;
	}

	public String getTag() {
		return mTag;
	}

	public void setTag(String tag) {
		mTag = tag;
	}
	
}
