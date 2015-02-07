package com.example.whoami;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * 앱 사용에 대한 위치정보를 지도에 표시하기 위한 맵 프레그먼트
 * TODO: 날짜정보를 얻어와 해당 날짜에 해당하는 맵 UI를 구현
 * (이동 경로별 사용 앱, 앱에 저장된 실제 데이터 관련 UI 등)
 */

public class MapFragment extends SupportMapFragment {
	
	private static final String TAG ="WhoAmI.MapFragment";
	public static final String ARG_APP_ID = "APP_ID";
	public static final String ARG_APP_USING_DAY = "APP_USING_DAY";
	private GoogleMap mGoogleMap;
	private Context mContext;
	//private ArrayList<App> mTodayApps;
	
	// 2015.01.31 커스텀맵뷰 추가 추가
	private View mView;
	private MapManager mMapManager;
	
	private String mSelectedDay;
	
	// MapFragment 생성시 선택된 App UUID를 받기위한 함수
	public static MapFragment newInstance(String usingDay){
		Bundle args = new Bundle();
		args.putSerializable(ARG_APP_USING_DAY, usingDay);
		
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
		
		mSelectedDay = (String)getArguments().getSerializable(ARG_APP_USING_DAY);
		//mTodayApps = AppList.get(mContext).loadOndayApps(mSelectedDay);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mView = super.onCreateView(inflater, container, savedInstanceState);
		
		/**
		 * 2015.01.31 커스텀맵뷰 추가 start
		 */
		if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
		try {
            // Inflate the layout for this fragment.
			mView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
            // Map is already there, just return view as it is.
        }
		FragmentManager fmanager = getActivity().getSupportFragmentManager();
		mGoogleMap = ( (SupportMapFragment) fmanager.findFragmentById(R.id.map)).getMap();
		/**
		 * 2015.01.31 커스텀맵뷰 추가 end
		 * mGoogleMap = getMap(); // 커스텀 맵뷰 사용안할 경우에 이용
		 */
		
		// 맵 설정
		mMapManager = new MapManager(mContext, mGoogleMap, mSelectedDay);
		if(true == mMapManager.enableGooglePlay()){
			mMapManager.init();
			mMapManager.draw();
		}
		
		return mView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mGoogleMap.setMyLocationEnabled(false);
	}
	
}
