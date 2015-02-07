package com.example.whoami;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MomentFragment extends Fragment {
	
	Context mContext;
	ImageView mNowPictureIV;
	TextView mWhenTV, mWhereTV;
	EditText mMurmurET, mTag;
	Button mSaveBtn;
	GPSManager mGPSManager;
	Moment mMoment;
	Position mPos;

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mGPSManager = GPSManager.get(mContext);
		
		mMoment = new Moment();
		mPos = new Position();
	}
	
	

	@Override
	public void onPause() {
		super.onPause();
		
		// TODO: LocationManager 해제 되는지 확인
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_moment, parent, false);
		
		mNowPictureIV = (ImageView)v.findViewById(R.id.momentPic_imageView);
		mWhenTV = (TextView)v.findViewById(R.id.momentWhen_textView);
		mWhereTV = (TextView)v.findViewById(R.id.momentWhere_textView);
		mSaveBtn = (Button)v.findViewById(R.id.momentSave_button);
		mMurmurET = (EditText)v.findViewById(R.id.momentContent_textView);
		mTag = (EditText)v.findViewById(R.id.momentTag_textView);
		
		// 위치 구하기
		Location loc = mGPSManager.getLocation();
		if(null != loc){
			String addr = mGPSManager.getAddressFromLocation(loc.getLatitude(), loc.getLongitude());
			mPos.setLatitude(loc.getLatitude());
			mPos.setLongitude(loc.getLongitude());
			mMoment.setAddr(addr);
			mWhereTV.setText(addr);
			mMoment.setPos(mPos);
			mMoment.setPhoto(null);
		}
		mWhenTV.setText(mMoment.getStartDate());
		mTag.setText(mMoment.getTag());
		
		
		mSaveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					
					mMoment.setMurmur(mMurmurET.getText().toString());
					// 리스트에 추가
					MomentList.get(mContext).addMoment(mMoment);
					
					// 내용이 없을 경우 저장 안함
					if(0 == mMurmurET.getText().length()){
						Toast.makeText(getActivity(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
						return;
					}
					
					// 파일에 저장
					MomentList.get(mContext).saveMomentList();
				}catch(NullPointerException e){
					Log.e("test", e.toString());
				}
				
			}
		});
		
		return v;
	}

}
