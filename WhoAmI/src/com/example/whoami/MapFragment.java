package com.example.whoami;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 앱 사용에 대한 위치정보를 지도에 표시하기 위한 맵 프레그먼트
 * TODO: 날짜정보를 얻어와 해당 날짜에 해당하는 맵 UI를 구현
 * (이동 경로별 사용 앱, 앱에 저장된 실제 데이터 관련 UI 등)
 */

public class MapFragment extends SupportMapFragment {
	
	private static final String TAG ="WhoAmI.MapFragment";
	public static final String ARG_APP_ID = "APP_ID";
	private GoogleMap mGoogleMap;
	private Context mContext;
	private App mApp;
	private ArrayList<App> mApps;
	 
	// MapFragment 생성시 선택된 App UUID를 받기위한 함수
	public static MapFragment newInstance(UUID id){
		Bundle args = new Bundle();
		args.putSerializable(ARG_APP_ID, id);
		
		MapFragment fragment = new MapFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	// Context정보를 저장하기 위한 시점(다른 시점 이용시 null)
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ArrayList<App> mApps;
		mApps = AppList.get(mContext).getApps();
		UUID id = (UUID)getArguments().getSerializable(ARG_APP_ID);
		for(App a : mApps){
			if(a.getUId().equals(id) == true){
				mApp = a;
				break;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		//mapInit();
		mapInitForDay(mApp.getUsingDay());
		
		return v;
	}
	
	// 구글맵 설정(마커 등)
	public void mapInit(){
		
		try{
			// 구글 서비스 플레이 사용 가능 여부 확인
			int enableGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			if(ConnectionResult.SUCCESS == enableGooglePlay){
				mGoogleMap = getMap();
				// 현재 위치 표시
				mGoogleMap.setMyLocationEnabled(true);
				
				// TODO: 데이터가 가진 위치 표시
				LatLng pos = new LatLng(mApp.getAppPos().getLatitude(), mApp.getAppPos().getLongitude());

				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
				
				MarkerOptions marker = new MarkerOptions();
				
				//주소구하기
				String addr = GPSManager.get(mContext).getAddressFromLocation(mApp.getAppPos().getLatitude(), mApp.getAppPos().getLongitude());
				
				// 마커설정
				// TODO: 일정거리안에 있는 데이터는 하나의 마커로 설정하고 
				marker.position(pos);
				marker.title("주소");
				marker.snippet(addr);
				
				mGoogleMap.addMarker(marker).showInfoWindow();
				
				// 마커클릭리스너
				// TODO: 실제 데이터 보여주는 부분?
				mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
					
					@Override
					public boolean onMarkerClick(Marker arg0) {
						
						return false;
					}
				});
			}
		}catch(NullPointerException e){
			Log.e(TAG, "MapFragment mapInit() fail");
		}
	}
	
	// test
	// 선택된 날짜에 대한 위치를 가져와서 표시함
	public void mapInitForDay(String day){
		try{
			// 구글 서비스 플레이 사용 가능 여부 확인
			int enableGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			if(ConnectionResult.SUCCESS == enableGooglePlay){
				mGoogleMap = getMap();
				// 현재 위치 표시
				mGoogleMap.setMyLocationEnabled(true);
				
				// 해당 날짜에 대한 정보
				if(null != mApp.getUsingDay() && mApp.getUsingDay().equals(day)){
					try{
				    	AppJSONSerializer serializer = new AppJSONSerializer(getActivity(), AppList.FILENAME);
				    	mApps = serializer.loadAppsForDay(day);
				    	
				    	LatLng pos = new LatLng(0,0);
				    	for(App a : mApps){
				    		pos = new LatLng(a.getAppPos().getLatitude(), a.getAppPos().getLongitude());
				    		
				    		MarkerOptions marker = new MarkerOptions();
							
							//주소구하기
							String addr = GPSManager.get(mContext).getAddressFromLocation(a.getAppPos().getLatitude(), a.getAppPos().getLongitude());
							
							// 마커설정
							// TODO: 일정거리안에 있는 데이터는 하나의 마커로 설정하고
							marker.position(pos);
							marker.title(addr);
							marker.snippet(a.getStartDate());
							
							mGoogleMap.addMarker(marker).showInfoWindow();
				    	}
				    	//마지막 위치로 카메라 이동
				    	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
				    	
				    	// 마커클릭리스너
						// TODO: 실제 데이터 보여주는 부분?
						mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
							
							@Override
							public boolean onMarkerClick(Marker arg0) {
								
								return false;
							}
						});
				    	
			    	}catch(Exception e){
			    		
			    	}
				}
			}
		}catch(NullPointerException e){
			Log.e(TAG, "MapFragment mapInit() fail");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mGoogleMap.setMyLocationEnabled(false);
	}
	
}
