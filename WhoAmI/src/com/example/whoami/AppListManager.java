package com.example.whoami;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 앱 관리자
 * (날짜, 시간, 이름등으로 관리)
 * 사용하기 전에 반드시 초기화 필요(InitForDay()등)
 * TODO: 시간, 이름등 다양한 방식으로 초기화 추가
 */

public class AppListManager {

	private Context mContext;
	private ArrayList<App> mList;
	private ArrayList<String> mTitle;
	private ArrayList<AppPosition> mPos;
	private ArrayList<String> mAddress;
	private ArrayList<String> mName;
	private ArrayList<Drawable> mIcon;
	
	public AppListManager(Context context){
		this.mContext = context;
	}
	
	// 날짜로 초기화
	public void initForDay(String day){
		try{
			mList = AppList.get(mContext).loadOndayApps(day);
		}catch(Exception e){
			mList = new ArrayList<App>();
		}
	}
	
	// 초기화 버전 리스트
	public ArrayList<App> getList(){
		return mList;
	}
	
	// 초기화 버전 타이틀 리스트
	public ArrayList<String> getTitle(){
		mTitle = new ArrayList<String>();
		for(App a : mList){
			mTitle.add(a.getTitle());
		}
		return mTitle;
	}
	
	// 초기화 버전 위치 리스트
	public ArrayList<AppPosition> getPosition(){
		mPos = new ArrayList<AppPosition>();
		for(App a: mList){
			mPos.add(new AppPosition(a.getAppPos()));
		}
		return mPos;
	}
	
	// 초기화 버전 주소 리스트
	public ArrayList<String> getAddress(){
		mAddress = new ArrayList<String>();
		for(App a : mList){
			mAddress.add(a.getAddr());
		}
		return mAddress;
	}
	
	// 초기화 버전 이름 리스트
	public ArrayList<String> getName(){
		mName = new ArrayList<String>();
		for(App a : mList){
			mName.add(a.getAppName(mContext));
		}
		return mName;
	}
	
	// 초기화 버전 아이콘 리스트
	public ArrayList<Drawable> getIcon(){
		mIcon = new ArrayList<Drawable>();
		for(App a : mList){
			mIcon.add(a.getAppIcon(mContext));
		}
		return mIcon;
	}
}
