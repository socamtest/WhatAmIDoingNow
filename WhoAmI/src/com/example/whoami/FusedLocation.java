package com.example.whoami;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * FusedLocation API를 사용한 위치 정보를 구하기 위한 클래스
 */

public class FusedLocation implements
										LocationListener,
										GoogleApiClient.ConnectionCallbacks,
										GoogleApiClient.OnConnectionFailedListener{
	private static final String TAG = "whoai.fusedlocation";
	private static final long ONE_MIN = 1000 * 60;
	private static final long FIVE_MIN = ONE_MIN * 5;
	private static final long INTERVAL = 1000 * 10; //reference에 1000 * 10 이 적당
	private static final long FAST_INTERVAL = 5000; //reference에 1000 * 5 가 적당
	private static final long REFRESH_TIME = FIVE_MIN;
	private static final float MINIMUM_ACCURACY = 50.f;
	private static final int REFRESH_REQUEST_CNT = 15; //requestLocationUpdates 호출 조건
	private static final int REFRESH_REMOVE_CNT = 60; //removeLocationUpdates 호출 조건
	
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	
	private static String mLastUpdateTime;
	private static boolean mCalledOnLocationChanged;
	
	private Context mContext;
	private FusedLocationReceiver mReceiver;
	private Location mLocation;
	
	private static FusedLocation sFusedLocation;
	private static int sRequestCnt = 1;
	
	// 생성자
	public FusedLocation(Context context, FusedLocationReceiver receiver){
		this.mContext = context;
		this.mReceiver = receiver;
		this.mCalledOnLocationChanged = false;
		
		/**
		 *  해당 시점에서 NullPointerException 발생하면서 앱 종료
		 *  무슨 이유인지 모르겠음
		 */
		try{
			mGoogleApiClient = new GoogleApiClient.Builder(mContext.getApplicationContext())
												.addApi(LocationServices.API)
												.addConnectionCallbacks(this)
												.addOnConnectionFailedListener(this)
												.build();
			
			if(null != mGoogleApiClient)
				mGoogleApiClient.connect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation FusedLocation() NullPointerException : " + e.toString());
		}
	}
	
	public static FusedLocation get(Context c, FusedLocationReceiver receiver){
		if(null == sFusedLocation)
			sFusedLocation = new FusedLocation(c, receiver);
		return sFusedLocation;
	}
	
	public Location getLocation(){
		return this.mLocation;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.i(TAG, "FusedLocation onConnectionFailed() start");
		
	}

	/**
	 * PRIORITY_NO_POWER 패시브
	 * PRIORITY_BALANCED_POWER_ACCURACY 와이파이, 셀
	 * PRIORITY_HIGH_ACCURACY GPS, 와이파이
	 * 
	 * requestLocationUpdates 호출시 onLocationChanged 발생
	 * 
	 * TODO: 무조건 requestLocationUpdates 호출시 문제발생 확인(네트워크 사용량, 밧데리)
	 */
	
	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "FusedLocation onConnected() start");
		
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setFastestInterval(FAST_INTERVAL);
		
		Location currLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		
		if(null != currLoc){
			this.mLocation = currLoc;
			callRequestLocationUpdates(REFRESH_REQUEST_CNT, REFRESH_REMOVE_CNT);
		}
		
		if(true == mCalledOnLocationChanged){
			mCalledOnLocationChanged = false; // 초기화
			Log.i(TAG, "FusedLocation onConnected() Called onLocationChanged");
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "FusedLocation onConnectionSuspended() start");
	}

	/**
	 * WIFI환경에서는 onLocationChanged 호출되지 않는다.
	 * onLocationChange는 GPS와 연관(확인 필요)
	 */
	
	@Override
	public void onLocationChanged(Location location) {
		// Toast.makeText(mContext, "onLocationChanged call", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "FusedLocation onLocationChanged() start");
		mCalledOnLocationChanged = true;
		
		this.mLocation = location;
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		// TODO: FusedLocationReceiver
		mReceiver.onLocationChanged();
		
		// 확인용 토스트
		// 위치변화 확인용 메시지
		/*if(null != mLocation && null != location){
			Toast.makeText(mContext, "Previous Latitude : "+mLocation.getLatitude(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mContext,  "New Latitude : "+location.getLatitude(), Toast.LENGTH_SHORT).show();
			
			Toast.makeText(mContext, "Previous Longitude : "+mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mContext,  "New Longitude : "+location.getLongitude(), Toast.LENGTH_SHORT).show();
			
			Toast.makeText(mContext, "Previous Accuracy : "+mLocation.getAccuracy(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mContext,  "New Accuracy : "+location.getAccuracy(), Toast.LENGTH_SHORT).show();
		}*/
	}

	// GoogleApiClient 해제
	public void disconnectGoogleApiClient(){
		try{
			if(null != mGoogleApiClient)
				mGoogleApiClient.disconnect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation disconnectGoogleApiClient() NullPointerException : " + e.toString());
		}
	}
	
	// 위치정보 갱신요청 함수
	public void callRequestLocationUpdates(int refreshCnt, int removeCnt){
		Log.i(TAG, "FusedLocation callRequestLocationUpdates() sRequestCnt : " + sRequestCnt);
		
		if(0>refreshCnt || 0>removeCnt){
			refreshCnt = 15;
			removeCnt = 30;
		}
		
		if(10000 == sRequestCnt)
			sRequestCnt = 0;
		
		if(0 == sRequestCnt%refreshCnt){
			if( null != mGoogleApiClient && true == mGoogleApiClient.isConnected() && null != mLocationRequest){
				Log.i(TAG, "FusedLocation callRequestLocationUpdates [requestLocationUpdates call]");
				LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			}
		}
		
		if(0 == sRequestCnt%removeCnt){
			if( null != mGoogleApiClient && true == mGoogleApiClient.isConnected() && null != mLocationRequest){
				Log.i(TAG, "FusedLocation callRequestLocationUpdates [removeLocationUpdates call]");
				LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			}
		}
		++sRequestCnt;
	}
	
}
