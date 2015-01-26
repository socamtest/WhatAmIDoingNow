package com.example.whoami;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * JSON Serializer
 * (저장, 로드 관리)
 */

public class AppJSONSerializer {
	
	private static final String TAG ="whoami.appjsonserializer";
	private Context mContext;
	private String mFileName;
	
	public AppJSONSerializer(Context c, String f){
		mContext = c;
		mFileName = f;
	}
	
	// 파일에 저장
	public void saveApps(ArrayList<App> apps)throws JSONException, IOException{
		JSONArray array = new JSONArray();
		for(App a : apps){
			array.put(a.toJson());
		}
		Writer writer = null;
		try{
			OutputStream out = mContext.openFileOutput(mFileName,  Context.MODE_PRIVATE);
			writer= new OutputStreamWriter(out);
			writer.write(array.toString());
		}finally{
			if(writer != null)
				writer.close();
		}
	}
	
	// 파일에서 로드
	public ArrayList<App> loadApps()throws IOException, JSONException{
		ArrayList<App> apps = new ArrayList<App>();
		BufferedReader reader = null;
		try{
			InputStream in = mContext.openFileInput(mFileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			int len = array.length();
			for(int i=0; i<len; i++){
				apps.add(new App(array.getJSONObject(i)));
			}
		}catch(FileNotFoundException e){
			Log.e(TAG, "AppJSONSerializer loadApps() fail");
		}finally{
			if(reader != null)
				reader.close();
		}
		return apps;
	}
	
	//파일에서 특정 날짜 데이터 로드
	public ArrayList<App> loadAppsForDay(String day)throws IOException, JSONException{
		ArrayList<App> apps = new ArrayList<App>();
		BufferedReader reader = null;
		try{
			InputStream in = mContext.openFileInput(mFileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			int len = array.length();
			for(int i=0; i<len; i++){
				App a = new App(array.getJSONObject(i));
				if(null != a.getUsingDay() && true == a.getUsingDay().equals(day))
					apps.add(new App(array.getJSONObject(i)));
			}
		}catch(FileNotFoundException e){
			Log.e(TAG, "AppJSONSerializer loadAppsForDay() fail");
		}finally{
			if(reader != null)
				reader.close();
		}
		return apps;
	}
}
