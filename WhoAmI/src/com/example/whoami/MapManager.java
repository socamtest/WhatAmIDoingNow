package com.example.whoami;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapManager implements OnMarkerClickListener{
	
	private Context mContext;
	private GoogleMap mGoogleMap;
	private String mSelectedDay;
	
	private ArrayList<Moment> mSelectedMoments;
	
	private LatLng mPos = new LatLng(0,0); // 카메라 이동을 위한 의미있는 위치 지정
	
	// 생성자
	public MapManager(Context context, GoogleMap googleMap){
		this.mContext = context;
		this.mGoogleMap = googleMap;
	}
	
	public MapManager(Context context, GoogleMap googleMap, String selectedDay){
		this.mContext = context;
		this.mGoogleMap = googleMap;
		this.mSelectedDay = selectedDay;
	}
	
	// 맵뷰 초기화
	public void init(){
		// 현재 위치 표시
		mGoogleMap.setMyLocationEnabled(true);
		mSelectedMoments = MomentList.get(mContext).loadOndayMoments(mSelectedDay);
	}
	
	// 카메라
	public void moveCamera(LatLng pos, int zoom){
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
	}
	
	// 시간별 위치에 대한 마커 및 이미지
	public void draw(){
		SparseArray<Path> posArray = AppList.get(mContext).getDayPos(true); // 시간별 위치
		ArrayList<LatLng> lines = new ArrayList<LatLng>(); //polyline에 추가할 라인

		Path p = new Path();
		for(int i=0; i<posArray.size(); i++){
			int key = posArray.keyAt(i); // 키값은 위치가 저장된 시간
			// p = new Path(posArray.get(key));
			p = posArray.get(key);
			mPos = new LatLng(p.getPos().getLatitude(), p.getPos().getLongitude());
			lines.add(mPos); // 시간대별 위치 저장
			
			String addr = p.getAddr();
			
			// 마커 추가
			markerDraw(mPos, p.getTime() + "에 머뭄", addr);
		}
		
		// 메모 마커
		momentMarkerDraw();
		
		// 폴리라운 추가
		polylineDraw(lines, Color.rgb(0,176,240), 7); // 파란색
		
		// 카메라 설정
		moveCamera(mPos,15);
		
	}
	
	public void markerDraw(LatLng position, String title, String snippet){
		MarkerOptions options = new MarkerOptions();
		
		options.position(position);
		options.title(title);
		options.snippet(snippet);
		options.icon(BitmapDescriptorFactory.fromResource(R.drawable.path));
		
		mGoogleMap.addMarker(options);
	}
	public void polylineDraw(ArrayList<LatLng> lines, int lineColor, float lineWidth){
		PolylineOptions options = new PolylineOptions();
		
		options.addAll(lines);
		options.color(lineColor);
		options.width(lineWidth);
		
		mGoogleMap.addPolyline(options);
	}
	
	public void momentMarkerDraw(){
		MarkerOptions options = new MarkerOptions();
		for(Moment m : mSelectedMoments){
			if(0.0 != m.getPos().getLatitude() && 0.0 != m.getPos().getLongitude()){
				mPos = new LatLng(m.getPos().getLatitude(), m.getPos().getLongitude());
				options.position(mPos);
				options.title(m.getTag());
				options.snippet(m.getMurmur());
				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark));
				
				mGoogleMap.addMarker(options);
			}
		}
	}
	
	// 해제
	public void release(){
		
	}
	
	public boolean enableGooglePlay(){
		int enableGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if(ConnectionResult.SUCCESS == enableGooglePlay)
			return true;
		return false;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
