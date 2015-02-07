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
 * �� ��뿡 ���� ��ġ������ ������ ǥ���ϱ� ���� �� �����׸�Ʈ
 * TODO: ��¥������ ���� �ش� ��¥�� �ش��ϴ� �� UI�� ����
 * (�̵� ��κ� ��� ��, �ۿ� ����� ���� ������ ���� UI ��)
 */

public class MapFragment extends SupportMapFragment {
	
	private static final String TAG ="WhoAmI.MapFragment";
	public static final String ARG_APP_ID = "APP_ID";
	public static final String ARG_APP_USING_DAY = "APP_USING_DAY";
	private GoogleMap mGoogleMap;
	private Context mContext;
	//private ArrayList<App> mTodayApps;
	
	// 2015.01.31 Ŀ���Ҹʺ� �߰� �߰�
	private View mView;
	private MapManager mMapManager;
	
	private String mSelectedDay;
	
	// MapFragment ������ ���õ� App UUID�� �ޱ����� �Լ�
	public static MapFragment newInstance(String usingDay){
		Bundle args = new Bundle();
		args.putSerializable(ARG_APP_USING_DAY, usingDay);
		
		MapFragment fragment = new MapFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	// Context������ �����ϱ� ���� ����(�ٸ� ���� �̿�� null)
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
		 * 2015.01.31 Ŀ���Ҹʺ� �߰� start
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
		 * 2015.01.31 Ŀ���Ҹʺ� �߰� end
		 * mGoogleMap = getMap(); // Ŀ���� �ʺ� ������ ��쿡 �̿�
		 */
		
		// �� ����
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
