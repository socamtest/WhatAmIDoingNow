package com.example.whoami;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * FusedLocation API를 사용한 위치 정보를 구하기 위한 클래스
 */

public class FusedLocation implements
										LocationListener,
										ConnectionCallbacks,
										OnConnectionFailedListener{
	private static final String TAG = "whoai.fusedlocation";
	private static final long ONE_MIN = 1000 * 60;
	private static final long FIVE_MIN = ONE_MIN * 5;
	private static final long INTERVAL = ONE_MIN;
	private static final long FAST_INTERVAL = 1000 * 30;
	private static final long REFRESH_TIME = FIVE_MIN;
	private static final float MINIMUM_ACCURACY = 50.f;
	
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	
	private Context mContext;
	private FusedLocationReceiver mReceiver;
	private Location mLocation;
	
	// 생성자
	public FusedLocation(Context context, FusedLocationReceiver receiver){
		this.mContext = context;
		this.mReceiver = receiver;
		
		/**
		 *  해당 시점에서 NullPointerException 발생하면서 앱 종료
		 *  무슨 이유인지 모르겠음
		 */
		try{
			mGoogleApiClient = new GoogleApiClient.Builder(mContext)
												.addApi(LocationServices.API)
												.addConnectionCallbacks(this)
												.addOnConnectionFailedListener(this)
												.build();
			
			mLocationRequest = new LocationRequest();
			mLocationRequest.setInterval(INTERVAL);
			//mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			mLocationRequest.setFastestInterval(FAST_INTERVAL);
			
			if(null != mGoogleApiClient)
				mGoogleApiClient.connect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation FusedLocation() NullPointerException : " + e.toString());
		}
	}
	
	public Location getLocation(){
		return this.mLocation;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "FusedLocation onConnectionFailed() start");
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "FusedLocation onConnected() start");
		
		
		// TODO: 여기서 위치가 널이 아닐경우 그냥 넘겨주므로 업데이트가 안일어나나???
		// REFRESH_TIME을 줘서 업데이트 해줄 필요가 있을 듯???
		//test
		Location currLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(null != currLoc){
			this.mLocation = currLoc;
			// requestLocationUpdates 호출시 onLocationChanged 발생?
			// TODO: 무조건 requestLocationUpdates 호출시 문제발생 확인(네트워크 사용량, 밧데리)
			Log.i(TAG, "FusedLocation onConnected() requestLocationUpdates Call");
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
		
		/*if(null != currLoc && REFRESH_TIME < currLoc.getTime()){
			this.mLocation = currLoc;
		}else{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			
			// requestLocationUpdates 이후 바로 removeLocationUpdates 해도 되나?
			//LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusedLocation.this);
			*//**
			 * TODO: 해당 위치에서 OutOfMemory 발생(치명적 오류)
			 * 일단 finalize(), disconnectGoogleApiClient() 함수에서 removeLocationUpdates 하도록 변경
			 *//*
			Executors.newScheduledThreadPool(1).schedule(new Runnable(){
				public void run(){
					LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusedLocation.this);
				}
			}, FIVE_MIN, TimeUnit.MILLISECONDS);
		}*/
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "FusedLocation onConnectionSuspended() start");
		
	}

	/**
	 * 실내에서는 호출되지 않는다?
	 */
	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "FusedLocation onLocationChanged() start");
		// OnConnected에서 위치 변경
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusedLocation.this);
		
		// getAccuracy() 현재위치기준으로 반지름 거리  
		if(null == mLocation || mLocation.getAccuracy() > location.getAccuracy()){
			Log.i(TAG, "FusedLocation onLocationChanged() mLocation(location) accurancy : " + mLocation.getAccuracy() + "(" + location.getAccuracy() + ")");
			mLocation = location;
			
			// TODO: FusedLocationReceiver
			mReceiver.onLocationChanged();
		}
	}

	/*// finalize 정확한 용도를 모르겠다 일단 주석
	// 소멸자?
	@Override
	protected void finalize() throws Throwable {
		Log.i(TAG, "FusedLocation finalize() start");
		super.finalize();
		try{
			if(null != mGoogleApiClient)
				mGoogleApiClient.disconnect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation finalize() NullPointerException : " + e.toString());
		}
	}*/
	
	// GoogleApiClient 해제
	public void disconnectGoogleApiClient(){
		try{
			if(null != mGoogleApiClient)
				mGoogleApiClient.disconnect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation disconnectGoogleApiClient() NullPointerException : " + e.toString());
		}
	}
	
}
