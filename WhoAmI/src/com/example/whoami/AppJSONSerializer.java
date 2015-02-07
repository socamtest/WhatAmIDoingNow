package com.example.whoami;

import java.io.BufferedReader;
import java.io.File;
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
import android.util.SparseArray;
import android.widget.Toast;

/**
 * JSON Serializer
 * (저장, 로드 관리)
 */

public class AppJSONSerializer {
	
	private static final String TAG ="whoami.appjsonserializer";
	private Context mContext;
	private String mFileName;
	
	private SparseArray<Path> mOnedayPos;
	
	public AppJSONSerializer(Context c, String f){
		mContext = c;
		mFileName = f;
		
		mOnedayPos = new SparseArray<Path>();
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
		
		// 파일 유무 검사
		if(true == Util.FileExists(mContext, mFileName)){
			Log.i(TAG, "AppJSONSerializer loadApps FileExists success");
		}else{
			Log.i(TAG, "AppJSONSerializer loadApps FileExists fail");
			return apps;
		}
		
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
	public ArrayList<App> loadOnedayApps(String day)throws IOException, JSONException{
		// 2015.0203 mOnedayPos 초기화 안되므로 초기화
		mOnedayPos.clear();
		
		ArrayList<App> apps = new ArrayList<App>();
		BufferedReader reader = null;
		
		// 파일 유무 검사
		if(true == Util.FileExists(mContext, mFileName)){
			Log.i(TAG, "AppJSONSerializer loadApps(String day) FileExists success");
		}else{
			Log.i(TAG, "AppJSONSerializer loadApps(String day) FileExists fail");
			return apps;
		}
					
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
			
			// 이전 위치 저장
			String nullPos = "해당 위치에 주소 없음"; // 해당 위치에 주소 없음
			String prevPos = null;
			for(int i=0; i<len; i++){
				App a = new App(array.getJSONObject(i));
				if(null != a.getUsingDay() && true == a.getUsingDay().equals(day)){
					apps.add(new App(array.getJSONObject(i)));
					
					// 시간별로 저장하면 짧은시간에 이동한 거리가 사라지므로 이전과 동일한 위치만 제외하고 모두 저장
					// AppPosition, address 체크(이후 로직에서 mOnedayPos 사용시 검사 안함
					if(null == a.getAddr() || a.getAddr().equals(nullPos) || a.getAddr().equals(prevPos))
						continue;
					Path p = new Path();
					p.setTime(a.getUsingTime());
					p.setAddr(a.getAddr());
					p.setPos(new AppPosition(a.getAppPos()));
					mOnedayPos.put(i, p);
					prevPos = a.getAddr();
					
					/*// 2015.02.01 시간별(1시,2시...) 위치 저장(최대 24개)
					if(a.getAppPos().getLatitude() != 0){
						// 시간별로 저장하면 짧은시간에 이동한 거리가 사라지므로 이전과 동일한 위치만 제외하고 모두 저장
						int key = Integer.valueOf(a.getUsingHour());
						mOnedayPos.put(key, a.getAppPos());
					}*/
				}
			}
		}catch(FileNotFoundException e){
			Log.e(TAG, "AppJSONSerializer loadAppsForDay() fail");
		}finally{
			if(reader != null)
				reader.close();
		}
		return apps;
	}
	
	public SparseArray<Path> getOnedayPos() {
		return mOnedayPos;
	}
	
}
