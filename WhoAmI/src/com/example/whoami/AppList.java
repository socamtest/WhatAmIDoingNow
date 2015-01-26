package com.example.whoami;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

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
	
	// AppList 생성시 JSON 파일 로드
	private AppList(Context context){
		mContext = context;
		mSerializer = new AppJSONSerializer(mContext, FILENAME);
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

	// AppList에 App 추가
	// TODO: 파일도 바로 갱신해줘야 하나?
	public boolean addApp(App app){
		if(true == mApps.add(app))
			return true;
		return false;
	}
	
	// AppList에 App 삭제
	// TODO: 파일도 바로 갱신해줘야 하나?
	public boolean deleteApp(App app){
		try{
			// 받아온 app 직접 remove에 주면 제거 안되 왜??
			for(App a : mApps){
				if(a.getUId().equals(app.getUId()))
					mApps.remove(a);
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
}
