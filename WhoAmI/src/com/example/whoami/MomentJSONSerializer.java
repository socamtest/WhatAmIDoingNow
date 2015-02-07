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
import android.widget.Toast;

/**
 * JSON Serializer
 * (저장, 로드 관리)
 * TODO: 파일처리 템플릿 클래스 필요
 */

public class MomentJSONSerializer {
	
	private static final String TAG = "whoami.momentjsonserializer";
	private Context mContext;
	private String mFilename;
	
	public MomentJSONSerializer(Context c, String f){
		this.mContext = c;
		this.mFilename = f;
	}
	
	// 파일에 저장
	public void saveMoment(ArrayList<Moment> moments) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for(Moment m : moments){
			array.put(m.toJSON());
		}
		Writer writer = null;
		try{
			OutputStream out = mContext.openFileOutput(mFilename,  Context.MODE_PRIVATE);
			writer= new OutputStreamWriter(out);
			writer.write(array.toString());
		}catch(Exception e){
			Log.e(TAG, "MomentJSONSerializer saveMoment() fail Error : " + e.toString());
		}
		finally{
			if(writer != null)
				writer.close();
		}
	}
	
	// 파일에서 로드
	public ArrayList<Moment> loadMoments()throws IOException, JSONException{
		ArrayList<Moment> MomentList = new ArrayList<Moment>();
		BufferedReader reader = null;
		
		// 파일 유무 검사
		if(true == Util.FileExists(mContext, mFilename)){
			Log.i(TAG, "MomentJSONSerializer loadMoments FileExists success");
		}else{
			Log.i(TAG, "MomentJSONSerializer loadMoments FileExists fail");
			return MomentList;
		}
				
		try{
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			int len = array.length();
			for(int i=0; i<len; i++){
				MomentList.add(new Moment(array.getJSONObject(i)));
			}
		}catch(FileNotFoundException e){
			Log.e(TAG, "MomentJSONSerializer loadMoments() fail Error : " + e.toString());
		}finally{
			if(reader != null)
				reader.close();
		}
		return MomentList;
	}
	
	// 파일에서 특정 날짜 데이터 로드
	public ArrayList<Moment> loadOnedayMoments(String day)throws IOException, JSONException{
		ArrayList<Moment> MomentList = new ArrayList<Moment>();
		BufferedReader reader = null;

		// 파일 유무 검사
		if(true == Util.FileExists(mContext, mFilename)){
			Log.i(TAG, "MomentJSONSerializer loadMoments(String day) FileExists success");
		}else{
			Log.i(TAG, "MomentJSONSerializer loadMoments(String day) FileExists fail");
			return MomentList;
		}
		
		try{
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			int len = array.length();
			for(int i=0; i<len; i++){
				Moment moment = new Moment(array.getJSONObject(i));
				if(null != moment.getUsingDay() && true == moment.getUsingDay().equals(day)){
					MomentList.add(new Moment(array.getJSONObject(i)));
				}
			}
		}catch(FileNotFoundException e){
			Log.e(TAG, "MomentJSONSerializer loadOnedayMoments(day) fail Error : " + e.toString());
		}finally{
			if(reader != null)
				reader.close();
		}
		return MomentList;
	}
}
