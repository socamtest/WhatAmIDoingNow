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
 * FusedLocation API�� ����� ��ġ ������ ���ϱ� ���� Ŭ����
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
	
	// ������
	public FusedLocation(Context context, FusedLocationReceiver receiver){
		this.mContext = context;
		this.mReceiver = receiver;
		
		/**
		 *  �ش� �������� NullPointerException �߻��ϸ鼭 �� ����
		 *  ���� �������� �𸣰���
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
		
		
		// TODO: ���⼭ ��ġ�� ���� �ƴҰ�� �׳� �Ѱ��ֹǷ� ������Ʈ�� ���Ͼ��???
		// REFRESH_TIME�� �༭ ������Ʈ ���� �ʿ䰡 ���� ��???
		//test
		Location currLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if(null != currLoc){
			this.mLocation = currLoc;
			// requestLocationUpdates ȣ��� onLocationChanged �߻�?
			// TODO: ������ requestLocationUpdates ȣ��� �����߻� Ȯ��(��Ʈ��ũ ��뷮, �嵥��)
			Log.i(TAG, "FusedLocation onConnected() requestLocationUpdates Call");
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
		
		/*if(null != currLoc && REFRESH_TIME < currLoc.getTime()){
			this.mLocation = currLoc;
		}else{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			
			// requestLocationUpdates ���� �ٷ� removeLocationUpdates �ص� �ǳ�?
			//LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusedLocation.this);
			*//**
			 * TODO: �ش� ��ġ���� OutOfMemory �߻�(ġ���� ����)
			 * �ϴ� finalize(), disconnectGoogleApiClient() �Լ����� removeLocationUpdates �ϵ��� ����
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
	 * �ǳ������� ȣ����� �ʴ´�?
	 */
	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "FusedLocation onLocationChanged() start");
		// OnConnected���� ��ġ ����
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, FusedLocation.this);
		
		// getAccuracy() ������ġ�������� ������ �Ÿ�  
		if(null == mLocation || mLocation.getAccuracy() > location.getAccuracy()){
			Log.i(TAG, "FusedLocation onLocationChanged() mLocation(location) accurancy : " + mLocation.getAccuracy() + "(" + location.getAccuracy() + ")");
			mLocation = location;
			
			// TODO: FusedLocationReceiver
			mReceiver.onLocationChanged();
		}
	}

	/*// finalize ��Ȯ�� �뵵�� �𸣰ڴ� �ϴ� �ּ�
	// �Ҹ���?
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
	
	// GoogleApiClient ����
	public void disconnectGoogleApiClient(){
		try{
			if(null != mGoogleApiClient)
				mGoogleApiClient.disconnect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation disconnectGoogleApiClient() NullPointerException : " + e.toString());
		}
	}
	
}
