package com.example.whoami;

import com.google.android.gms.maps.model.LatLng;

/**
 * 위치 관리
 */

public class AppPosition {
	private double mLatitude;   //위도
	private double mLongitude; //경도
	private LatLng mLatLng;
	
	public AppPosition(){
		mLatitude = 0.0;
		mLongitude = 0.0;
		//mLatLng = new LatLng(mLatitude, mLongitude);
	}
	
	public AppPosition(double lat, double lon){
		mLatitude = lat;
		mLongitude = lon;
		mLatLng = new LatLng(lat, lon);
	}
	
	public AppPosition(AppPosition a){
		this.mLatitude = a.mLatitude;
		this.mLongitude = a.mLongitude;
		this.mLatLng = a.mLatLng;
	}
	
	public double getLatitude() {
		return mLatitude;
	}
	
	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}
	
	public double getLongitude() {
		return mLongitude;
	}
	
	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}

	public LatLng getLatLng() {
		return mLatLng;
	}

	public void setLatLng(double lat, double lon) {
		mLatLng = new LatLng(lat, lon);
	}
}
