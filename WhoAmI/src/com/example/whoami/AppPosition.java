package com.example.whoami;

/**
 * 위치 관리
 */

public class AppPosition {
	private double mLatitude;   //위도
	private double mLongitude; //경도
	
	public AppPosition(){
		mLatitude = 0.0;
		mLongitude = 0.0;
	}
	
	public AppPosition(double lat, double lon){
		mLatitude = lat;
		mLongitude = lon;
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
}
