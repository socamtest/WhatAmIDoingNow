package com.example.whoami;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;


/**
 * 메모 관리자
 * (날짜, 시간 태그등으로 관리)
 * 사용하기 전에 반드시 초기화 필요(InitForDay()등)
 * TODO: 시간, 태그등 다양한 방식으로 초기화 추가
 */

public class MomentListManager {
	
	private Context mContext;
	private ArrayList<Moment> mList;
	private ArrayList<Position> mPosList;
	private ArrayList<String> mAddrList;
	private ArrayList<String> mTagList;
	private ArrayList<String> mMurmurList;
	
	public MomentListManager(Context context){
		this.mContext = context;
	}
	
	// 날짜로 초기화
	public void initForDay(String day){
		try{
			mList = MomentList.get(mContext).loadOndayMoments(day);
		}catch(Exception e){
			mList = new ArrayList<Moment>();
		}
	}
	
	// 초기화 버전 리스트
	public ArrayList<Moment> getList(){
		return mList;
	}
	
	// 초기화 버전 위치 리스트
	public ArrayList<Position> getPos(){
		mPosList = new ArrayList<Position>();
		for(Moment m : mList){
			mPosList.add(new Position(m.getPos()));
		}
		return mPosList;
	}
	
	// 초기화 버전 주소 리스트
	public ArrayList<String> getAddress(){
		mAddrList = new ArrayList<String>();
		for(Moment m : mList){
			mAddrList.add(m.getAddr());
		}
		return mAddrList;
	}
	
	// 초기화 버전 태그 리스트
	public ArrayList<String> getTag(){
		mTagList = new ArrayList<String>();
		for(Moment m : mList){
			mTagList.add(m.getTag());
		}
		return mTagList;
	}
	
	// 초기화 버전 메모 리스트
	public ArrayList<String> getMurmur(){
		mMurmurList = new ArrayList<String>();
		try{
		for(Moment m : mList){
			mMurmurList.add(m.getMurmur());
		}
		}catch(Exception e){
			
		}
		return mMurmurList;
	}
	
}
