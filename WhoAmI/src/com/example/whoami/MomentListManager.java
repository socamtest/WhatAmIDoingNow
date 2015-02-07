package com.example.whoami;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;


/**
 * �޸� ������
 * (��¥, �ð� �±׵����� ����)
 * ����ϱ� ���� �ݵ�� �ʱ�ȭ �ʿ�(InitForDay()��)
 * TODO: �ð�, �±׵� �پ��� ������� �ʱ�ȭ �߰�
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
	
	// ��¥�� �ʱ�ȭ
	public void initForDay(String day){
		try{
			mList = MomentList.get(mContext).loadOndayMoments(day);
		}catch(Exception e){
			mList = new ArrayList<Moment>();
		}
	}
	
	// �ʱ�ȭ ���� ����Ʈ
	public ArrayList<Moment> getList(){
		return mList;
	}
	
	// �ʱ�ȭ ���� ��ġ ����Ʈ
	public ArrayList<Position> getPos(){
		mPosList = new ArrayList<Position>();
		for(Moment m : mList){
			mPosList.add(new Position(m.getPos()));
		}
		return mPosList;
	}
	
	// �ʱ�ȭ ���� �ּ� ����Ʈ
	public ArrayList<String> getAddress(){
		mAddrList = new ArrayList<String>();
		for(Moment m : mList){
			mAddrList.add(m.getAddr());
		}
		return mAddrList;
	}
	
	// �ʱ�ȭ ���� �±� ����Ʈ
	public ArrayList<String> getTag(){
		mTagList = new ArrayList<String>();
		for(Moment m : mList){
			mTagList.add(m.getTag());
		}
		return mTagList;
	}
	
	// �ʱ�ȭ ���� �޸� ����Ʈ
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
