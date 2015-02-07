package com.example.whoami;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

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
	
	// 
	SparseArray<AppPosition> mPosToTime;
	
	// AppList ������ JSON ���� �ε�
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
	
	// TODO: �����ڿ��� �ε��� �����͸� ������ ó���ؾ� �ϳ�?
	public ArrayList<App> loadOndayApps(String day){
		try{
			return mSerializer.loadOnedayApps(day);
		}catch(Exception e){
			Log.e(TAG, "AppList loadOndayApps fail");
		}
		return null;
		
		/*// TODO ���� ����
		ArrayList<App> ondayApps = new ArrayList<App>();
		for(App a : mApps){
			if(null != a.getUsingDay() && true == a.getUsingDay().equals(day)){
				ondayApps.add(a);
			
				// 2015.02.01 �ð���(1��,2��...) ��ġ ����(�ִ� 24��)
				if(a.getAppPos().getLatitude() != 0){
					int key = Integer.valueOf(a.getUsingHour());
					mOnedayPos.put(key, a.getAppPos());
				}
			}
		}*/
	}
	
	/**
	 * 2015.02.01 �ð���(1��,2��...) ��ġ ����(�ִ� 24��) start
	 * loadOnedayApps �ȿ��� ���� �����ϹǷ� ����ϱ� ����
	 * loadOndayApps�� ȣ���Ͽ��� �������� ���� ����
	 */
	public SparseArray<Path> getDayPos(boolean beforeUsingLoadedOnedayApps){
		//
		if(true == beforeUsingLoadedOnedayApps)
			return mSerializer.getOnedayPos();
		return null;
	}
	/**
	 * 2015.02.01 Ư�� ��¥�� ���ؼ� �ð��뺰 ��ġ���� ���� �߰� end
	 */
	
	// Ư�� ��¥�� ���� �����Ͱ� �����ϴ��� Ȯ��
	public boolean haveOnedayApp(String day){
		for(App a : mApps){
			if(true == a.getUsingDay().equals(day))
				return true;
		}
		return false;
	}

	// AppList�� App �߰�
	public boolean addApp(App app){
		if(true == mApps.add(app))
			return true;
		return false;
	}
	
	// AppList�� App ����
	public boolean deleteApp(App app){

		/**
		 * �� ����Ʈ ������ �ϳ����� ���� �ǹǷ� iterator ������� �����غ� 
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
					// saveApps(); // ����Ʈ ���� �� ����?
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
