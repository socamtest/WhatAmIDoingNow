package com.example.whoami;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

/**
 * �� ���� ����Ʈ
 */

public class AppList {
	
	private static final String TAG = "whoami.applist";
	private static AppList sAppList;
	private  ArrayList<App> mApps;
	public static final String FILENAME = "apps.json";
	private AppJSONSerializer mSerializer;
	private Context mContext;
	
	// AppList ������ JSON ���� �ε�
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
	
	// AppList JSON ���Ͽ� ����
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

	// AppList�� App �߰�
	// TODO: ���ϵ� �ٷ� ��������� �ϳ�?
	public boolean addApp(App app){
		if(true == mApps.add(app))
			return true;
		return false;
	}
	
	// AppList�� App ����
	// TODO: ���ϵ� �ٷ� ��������� �ϳ�?
	public boolean deleteApp(App app){
		try{
			// �޾ƿ� app ���� remove�� �ָ� ���� �ȵ� ��??
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
