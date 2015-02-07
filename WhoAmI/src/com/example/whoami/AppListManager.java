package com.example.whoami;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * �� ������
 * (��¥, �ð�, �̸������� ����)
 * ����ϱ� ���� �ݵ�� �ʱ�ȭ �ʿ�(InitForDay()��)
 * TODO: �ð�, �̸��� �پ��� ������� �ʱ�ȭ �߰�
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
	
	// ��¥�� �ʱ�ȭ
	public void initForDay(String day){
		try{
			mList = AppList.get(mContext).loadOndayApps(day);
		}catch(Exception e){
			mList = new ArrayList<App>();
		}
	}
	
	// �ʱ�ȭ ���� ����Ʈ
	public ArrayList<App> getList(){
		return mList;
	}
	
	// �ʱ�ȭ ���� Ÿ��Ʋ ����Ʈ
	public ArrayList<String> getTitle(){
		mTitle = new ArrayList<String>();
		for(App a : mList){
			mTitle.add(a.getTitle());
		}
		return mTitle;
	}
	
	// �ʱ�ȭ ���� ��ġ ����Ʈ
	public ArrayList<AppPosition> getPosition(){
		mPos = new ArrayList<AppPosition>();
		for(App a: mList){
			mPos.add(new AppPosition(a.getAppPos()));
		}
		return mPos;
	}
	
	// �ʱ�ȭ ���� �ּ� ����Ʈ
	public ArrayList<String> getAddress(){
		mAddress = new ArrayList<String>();
		for(App a : mList){
			mAddress.add(a.getAddr());
		}
		return mAddress;
	}
	
	// �ʱ�ȭ ���� �̸� ����Ʈ
	public ArrayList<String> getName(){
		mName = new ArrayList<String>();
		for(App a : mList){
			mName.add(a.getAppName(mContext));
		}
		return mName;
	}
	
	// �ʱ�ȭ ���� ������ ����Ʈ
	public ArrayList<Drawable> getIcon(){
		mIcon = new ArrayList<Drawable>();
		for(App a : mList){
			mIcon.add(a.getAppIcon(mContext));
		}
		return mIcon;
	}
}
