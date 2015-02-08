package com.example.whoami;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 메모 저장 액티비티(현재시간, 현재위치, 태그, 메모)
 * 다이얼로그 테마 액티비티
 * TODO: 사진추가, 태그기능 추가, MomentFragment.java 삭제(다이얼로그 테마 이전에 사용한 프레그먼트)
 */
/*public class MomentActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new MomentFragment();
	}

}*/

// 다이얼로그 모양의 액티비티 테스트
// TODO:

public class MomentActivity extends Activity{
	
	private static final String TAG = "whoami.MomentActivity";
	ImageView mNowPictureIV;
	TextView mStaticWhenTV, mWhenTV, mStaticWhereTV, mWhereTV, mStaticTagTV;
	EditText mMurmurET, mTagET;
	Button mSaveBtn;
	Moment mMoment;
	Position mPos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_moment);
		
		// 배경 투명하게 하기
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		/*WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = 0.5f;
		getWindow().setAttributes(layoutParams);*/

		mMoment = new Moment();
		mPos = new Position();
		
		mNowPictureIV = (ImageView)findViewById(R.id.momentPic_imageView);
		
		mStaticWhenTV = (TextView)findViewById(R.id.staticMomentWhen_textView);
		mWhenTV = (TextView)findViewById(R.id.momentWhen_textView);
		
		mStaticWhereTV = (TextView)findViewById(R.id.staticMomentWhere_textView);
		mWhereTV = (TextView)findViewById(R.id.momentWhere_textView);
		
		mStaticTagTV = (TextView)findViewById(R.id.staticMomentTag_textView);
		mTagET = (EditText)findViewById(R.id.momentTag_textView);
		
		mMurmurET = (EditText)findViewById(R.id.momentContent_textView);
		mSaveBtn = (Button)findViewById(R.id.momentSave_button);
		
		// 위치 구하기
		Location loc = GPSManager.get(this).getLocation();
		// 위치구하기 FusedLocatino 으로 변경
		//Location loc = FusedLocation.get(this.getApplicationContext(), null).getLocation();
		
		String addr = null;
		if(null != loc){
			addr = GPSManager.get(this).getAddressFromLocation(loc.getLatitude(), loc.getLongitude());
			mPos.setLatitude(loc.getLatitude());
			mPos.setLongitude(loc.getLongitude());
		}
		// 뷰 색
		mStaticWhenTV.setTextColor(Color.rgb(108, 239, 76));
		mStaticWhenTV.setBackgroundColor(Color.TRANSPARENT);
		mWhenTV.setTextColor(Color.rgb(108, 198, 76));
		mWhenTV.setBackgroundColor(Color.TRANSPARENT);
		mStaticWhereTV.setTextColor(Color.rgb(108, 239, 76));
		mStaticWhereTV.setBackgroundColor(Color.TRANSPARENT);
		mWhereTV.setTextColor(Color.rgb(108, 198, 76));
		mWhereTV.setBackgroundColor(Color.TRANSPARENT);
		mStaticTagTV.setTextColor(Color.rgb(108, 239, 76));
		mStaticTagTV.setBackgroundColor(Color.TRANSPARENT);
		mTagET.setTextColor(Color.rgb(108, 198, 76));
		mTagET.setBackgroundColor(Color.TRANSPARENT);
		mMurmurET.setTextColor(Color.rgb(108, 198, 76));
		mSaveBtn.setBackgroundColor(Color.argb(122, 108, 198, 76));
		
		// 뷰 꾸미기
		mWhenTV.setText(mMoment.getStartDate());
		mWhereTV.setText(addr);
		
		// Moment 객체 채우기
		mMoment.setAddr(addr);
		mMoment.setPos(mPos);
		
		
		// 저장 버튼 클릭시
		mSaveBtn.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					// 버튼 클릭시 색 변경
					if(MotionEvent.ACTION_DOWN == event.getAction()){
						mSaveBtn.setBackgroundColor(Color.argb(50, 108, 198, 76));
					}
					if(MotionEvent.ACTION_UP == event.getAction()){
						mSaveBtn.setBackgroundColor(Color.argb(122, 108, 198, 76));
					}
					
					return false;
				}
			});
		mSaveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					
					if(0 == mTagET.getText().length()
						|| 0 == mMurmurET.getText().length()){
						Toast.makeText(getApplicationContext(), "태그 및 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
						return;
					}
					
					// Moment 객체 채우기
					mMoment.setMurmur(mMurmurET.getText().toString());
					mMoment.setTag(mTagET.getText().toString());
					
					// 리스트 및 파일에 추가
					MomentList.get(getApplicationContext()).addMoment(mMoment);
					MomentList.get(getApplicationContext()).saveMomentList();
					Toast.makeText(getApplicationContext(), "저장완료.", Toast.LENGTH_SHORT).show();
					
					// 액티비티 종료
					finish();
				}catch(NullPointerException e){
					Log.e(TAG, "MomentActivity mSaveBtn.setOnClickListener Error : " + e.toString());
				}
			}
		});
	}
}