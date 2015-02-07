package com.example.whoami;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

/**
 * 앱 관리 리스트
 */

public class AppList {
	
	private static final String TAG = "whoami.applist";
	private static AppList sAppList;
	private  ArrayList<App> mApps;
	public static final String FILENAME = "apps.json";
	private AppJSONSerializer mSerializer;
	private Context mContext;
	
	// 
	SparseArray<AppPosition> mPosToTime;
	
	// AppList 생성시 JSON 파일 로드
	private AppList(Context context){
		mContext = context;
		mSerializer = new AppJSONSerializer(mContext, FILENAME);
		mPosToTime = new SparseArray<AppPosition>();
		try{
			mApps = mSerializer.loadApps();
		}catch(Exception e){
			Log.i(TAG, "AppList AppList fail");
			mApps = new ArrayList<App>();
		}
	}
	
	public static AppList get(Context c){
		if(null == sAppList){
			sAppList = new AppList(c);
		}
		return sAppList;
	}
	
	// AppList JSON 파일에 저장
	public boolean saveApps(){
		try{
			mSerializer.saveApps(mApps);
			Log.i(TAG, "AppList saveApps Success");
			return true;
		}catch(Exception e){
			Log.e(TAG, "AppList saveApps fail");
			return false;
		}
	}
	
	// TODO: 생성자에서 로드한 데이터를 가지고 처리해야 하나?
	public ArrayList<App> loadOndayApps(String day){
		try{
			return mSerializer.loadOnedayApps(day);
		}catch(Exception e){
			Log.e(TAG, "AppList loadOndayApps fail");
		}
		return null;
		
		/*// TODO 수정 로직
		ArrayList<App> ondayApps = new ArrayList<App>();
		for(App a : mApps){
			if(null != a.getUsingDay() && true == a.getUsingDay().equals(day)){
				ondayApps.add(a);
			
				// 2015.02.01 시간별(1시,2시...) 위치 저장(최대 24개)
				if(a.getAppPos().getLatitude() != 0){
					int key = Integer.valueOf(a.getUsingHour());
					mOnedayPos.put(key, a.getAppPos());
				}
			}
		}*/
	}
	
	/**
	 * 2015.02.01 시간별(1시,2시...) 위치 저장(최대 24개) start
	 * loadOnedayApps 안에서 값을 저장하므로 사용하기 전에
	 * loadOndayApps을 호출하여야 정상적인 값이 나옴
	 */
	public SparseArray<Path> getDayPos(boolean beforeUsingLoadedOnedayApps){
		//
		if(true == beforeUsingLoadedOnedayApps)
			return mSerializer.getOnedayPos();
		return null;
	}
	/**
	 * 2015.02.01 특정 날짜에 대해서 시간대별 위치저장 로직 추가 end
	 */
	
	// 특정 날짜에 대한 데이터가 존재하는지 확인
	public boolean haveOnedayApp(String day){
		for(App a : mApps){
			if(true == a.getUsingDay().equals(day))
				return true;
		}
		return false;
	}

	// AppList에 App 추가
	public boolean addApp(App app){
		if(true == mApps.add(app))
			return true;
		return false;
	}
	
	// AppList에 App 삭제
	public boolean deleteApp(App app){

		/**
		 * 앱 리스트 삭제시 하나씩만 변경 되므로 iterator 방식으로 변경해봄 
		 */
		/*try{
			for(App a : mApps){
				if(a.getUId().equals(app.getUId()))
					mApps.remove(a);
				break;
			}
		}catch(Exception e){
			return false;
		}
		return true;*/
		try{
			for( Iterator<App> iterator = mApps.iterator(); iterator.hasNext(); ){
				App a = iterator.next();
				if(a.getUId().equals(app.getUId())){
					iterator.remove();
					// saveApps(); // 리스트 삭제 후 저장?
					break;
				}
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public ArrayList<App> getApps() {
		return mApps;
	}

	public void setApps(ArrayList<App> apps) {
		mApps = apps;
	}
	
	public App getApp(UUID id){
		for(App a : mApps){
			if(a.getUId() == id)
				return a;
		}
		return null;
	}
	
	public int getSize(){
		return mApps.size();
	}
}
