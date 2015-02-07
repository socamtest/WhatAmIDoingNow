package com.example.whoami;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {
	
	private static final String TAG = "whoami.photo";
	private static final String JSON_FILENAME = "filename";
	private String mFilename;
	
	public Photo(String filename){
		mFilename = null;
		mFilename = filename;
	}

	public Photo(JSONObject json) throws JSONException {
		mFilename = json.getString(JSON_FILENAME);
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, mFilename);
		return json;
	}
	
	public String getFilename() {
		return mFilename;
	}

}