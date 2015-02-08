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
 * �޸� ���� ��Ƽ��Ƽ(����ð�, ������ġ, �±�, �޸�)
 * ���̾�α� �׸� ��Ƽ��Ƽ
 * TODO: �����߰�, �±ױ�� �߰�, MomentFragment.java ����(���̾�α� �׸� ������ ����� �����׸�Ʈ)
 */
/*public class MomentActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new MomentFragment();
	}

}*/

// ���̾�α� ����� ��Ƽ��Ƽ �׽�Ʈ
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
		
		// ��� �����ϰ� �ϱ�
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
		
		// ��ġ ���ϱ�
		Location loc = GPSManager.get(this).getLocation();
		// ��ġ���ϱ� FusedLocatino ���� ����
		//Location loc = FusedLocation.get(this.getApplicationContext(), null).getLocation();
		
		String addr = null;
		if(null != loc){
			addr = GPSManager.get(this).getAddressFromLocation(loc.getLatitude(), loc.getLongitude());
			mPos.setLatitude(loc.getLatitude());
			mPos.setLongitude(loc.getLongitude());
		}
		// �� ��
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
		
		// �� �ٹ̱�
		mWhenTV.setText(mMoment.getStartDate());
		mWhereTV.setText(addr);
		
		// Moment ��ü ä���
		mMoment.setAddr(addr);
		mMoment.setPos(mPos);
		
		
		// ���� ��ư Ŭ����
		mSaveBtn.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					// ��ư Ŭ���� �� ����
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
						Toast.makeText(getApplicationContext(), "�±� �� ������ �Է��ϼ���.", Toast.LENGTH_SHORT).show();
						return;
					}
					
					// Moment ��ü ä���
					mMoment.setMurmur(mMurmurET.getText().toString());
					mMoment.setTag(mTagET.getText().toString());
					
					// ����Ʈ �� ���Ͽ� �߰�
					MomentList.get(getApplicationContext()).addMoment(mMoment);
					MomentList.get(getApplicationContext()).saveMomentList();
					Toast.makeText(getApplicationContext(), "����Ϸ�.", Toast.LENGTH_SHORT).show();
					
					// ��Ƽ��Ƽ ����
					finish();
				}catch(NullPointerException e){
					Log.e(TAG, "MomentActivity mSaveBtn.setOnClickListener Error : " + e.toString());
				}
			}
		});
	}
}