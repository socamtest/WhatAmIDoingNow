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
 * FusedLocation API�� ����� ��ġ ������ ���ϱ� ���� Ŭ����
 */

public class FusedLocation implements
										LocationListener,
										GoogleApiClient.ConnectionCallbacks,
										GoogleApiClient.OnConnectionFailedListener{
	private static final String TAG = "whoai.fusedlocation";
	private static final long ONE_MIN = 1000 * 60;
	private static final long FIVE_MIN = ONE_MIN * 5;
	private static final long INTERVAL = 1000 * 10; //reference�� 1000 * 10 �� ����
	private static final long FAST_INTERVAL = 5000; //reference�� 1000 * 5 �� ����
	private static final long REFRESH_TIME = FIVE_MIN;
	private static final float MINIMUM_ACCURACY = 50.f;
	private static final int REFRESH_REQUEST_CNT = 15; //requestLocationUpdates ȣ�� ����
	private static final int REFRESH_REMOVE_CNT = 60; //removeLocationUpdates ȣ�� ����
	
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	
	private static String mLastUpdateTime;
	private static boolean mCalledOnLocationChanged;
	
	private Context mContext;
	private FusedLocationReceiver mReceiver;
	private Location mLocation;
	
	private static FusedLocation sFusedLocation;
	private static int sRequestCnt = 1;
	
	// ������
	public FusedLocation(Context context, FusedLocationReceiver receiver){
		this.mContext = context;
		this.mReceiver = receiver;
		this.mCalledOnLocationChanged = false;
		
		/**
		 *  �ش� �������� NullPointerException �߻��ϸ鼭 �� ����
		 *  ���� �������� �𸣰���
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
	 * PRIORITY_NO_POWER �нú�
	 * PRIORITY_BALANCED_POWER_ACCURACY ��������, ��
	 * PRIORITY_HIGH_ACCURACY GPS, ��������
	 * 
	 * requestLocationUpdates ȣ��� onLocationChanged �߻�
	 * 
	 * TODO: ������ requestLocationUpdates ȣ��� �����߻� Ȯ��(��Ʈ��ũ ��뷮, �嵥��)
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
			mCalledOnLocationChanged = false; // �ʱ�ȭ
			Log.i(TAG, "FusedLocation onConnected() Called onLocationChanged");
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "FusedLocation onConnectionSuspended() start");
	}

	/**
	 * WIFIȯ�濡���� onLocationChanged ȣ����� �ʴ´�.
	 * onLocationChange�� GPS�� ����(Ȯ�� �ʿ�)
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
		
		// Ȯ�ο� �佺Ʈ
		// ��ġ��ȭ Ȯ�ο� �޽���
		/*if(null != mLocation && null != location){
			Toast.makeText(mContext, "Previous Latitude : "+mLocation.getLatitude(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mContext,  "New Latitude : "+location.getLatitude(), Toast.LENGTH_SHORT).show();
			
			Toast.makeText(mContext, "Previous Longitude : "+mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mContext,  "New Longitude : "+location.getLongitude(), Toast.LENGTH_SHORT).show();
			
			Toast.makeText(mContext, "Previous Accuracy : "+mLocation.getAccuracy(), Toast.LENGTH_SHORT).show();
			Toast.makeText(mContext,  "New Accuracy : "+location.getAccuracy(), Toast.LENGTH_SHORT).show();
		}*/
	}

	// GoogleApiClient ����
	public void disconnectGoogleApiClient(){
		try{
			if(null != mGoogleApiClient)
				mGoogleApiClient.disconnect();
		}catch(NullPointerException e){
			Log.e(TAG, "FusedLocation disconnectGoogleApiClient() NullPointerException : " + e.toString());
		}
	}
	
	// ��ġ���� ���ſ�û �Լ�
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
