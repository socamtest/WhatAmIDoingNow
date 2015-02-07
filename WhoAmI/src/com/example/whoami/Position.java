package com.example.whoami;

import org.json.JSONException;
import org.json.JSONObject;

public class Position {
	
	private static final String JSON_LATITUDE = "latitude";
	private static final String JSON_LONGITUDE = "longitude";
	
	private double mLatitude;
	private double mLongitude;
	
	public Position(JSONObject json) throws JSONException{
		mLatitude = json.getDouble(JSON_LATITUDE);
		mLongitude = json.getDouble(JSON_LONGITUDE);
	}
	
	public JSONObject toJSON() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_LATITUDE, mLatitude);
		json.put(JSON_LONGITUDE, mLongitude);
		
		return json;
	}
	
	public Position(){
		mLatitude = 0;
		mLongitude = 0;
	}
	
	public Position(double lat, double lon){
		this.mLatitude = lat;
		this.mLongitude = lon;
	}
	
	public Position(Position pos){
		this.mLatitude = pos.getLatitude();
		this.mLongitude = pos.getLongitude();
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
