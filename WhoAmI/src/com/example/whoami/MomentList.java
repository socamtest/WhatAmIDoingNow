package com.example.whoami;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

/**
 * 메모 관리 리스트
 */

public class MomentList {
	
	private static final String TAG = "whoami.momentlist";
	private Context mContext;
	private static MomentList sMomentList;
	private ArrayList<Moment> mMomentList;
	private MomentJSONSerializer mSerializer;
	private static final String FILENAME = "moments.json";
	
	private MomentList(Context c){
		mContext = c;
		mSerializer = new MomentJSONSerializer(mContext, FILENAME);
		try{
			mMomentList = mSerializer.loadMoments();
		}catch(Exception e){
			Log.e(TAG, "MomentList consructor Error : " + e.toString());
			mMomentList = new ArrayList<Moment>();
		}
	}
	
	public static MomentList get(Context c){
		if(null == sMomentList){
			sMomentList = new MomentList(c);
		}
		return sMomentList;
	}
	
	public boolean saveMomentList(){
		try{
			mSerializer.saveMoment(mMomentList);
			return true;
		}catch(Exception e){
			Log.e(TAG, "MomentList saveMomentList Error : " + e.toString());
			return false;
		}
	}
	
	public ArrayList<Moment> loadMomentList(){
		try{
			return mSerializer.loadMoments();
		}catch(Exception e){
		}
		return null;
	}
	
	public ArrayList<Moment> loadOndayMoments(String day){
		try{
			return mSerializer.loadOnedayMoments(day);
		}catch(Exception e){
			Log.e(TAG, "MomentList loadOndayMoments fail");
		}
		return null;
	}
	
	public ArrayList<Moment> getMomentList() {
		return mMomentList;
	}

	public void setMomentList(ArrayList<Moment> momentList) {
		mMomentList = momentList;
	}

	public Moment getApp(UUID id){
		for(Moment m : mMomentList){
			if(id == m.getUId())
				return m;
		}
		return null;
	}
	
	public boolean addMoment(Moment m){
		if(true == mMomentList.add(m))
			return true;
		return false;
	}
	
	public void deleteMoment(Moment moment){
		
		// ConcurrentModificationException 발생
		/*try{
			for(Moment m : mMomentList){
				if(m.getUId().equals(moment.getUId())){
					mMomentList.remove(m);
				}
			}
		}catch(Exception e){
			Log.e(TAG,e.toString());
		}*/
		
		// 삭제 후 파일에 저장 해야함
		try{
			for(Iterator<Moment> iterator = mMomentList.iterator(); iterator.hasNext(); ){
				Moment m = iterator.next();
				if(m.getUId().equals(moment.getUId())){
					iterator.remove();
					
					// 파일에 저장
					saveMomentList();
					break;
				}
		}
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
	}
	
	public int getSize(){
		return mMomentList.size();
	}
}
