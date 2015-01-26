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
 * �� ��뿡 ���� ��ġ������ ������ ǥ���ϱ� ���� �� �����׸�Ʈ
 * TODO: ��¥������ ���� �ش� ��¥�� �ش��ϴ� �� UI�� ����
 * (�̵� ��κ� ��� ��, �ۿ� ����� ���� ������ ���� UI ��)
 */

public class MapFragment extends SupportMapFragment {
	
	private static final String TAG ="WhoAmI.MapFragment";
	public static final String ARG_APP_ID = "APP_ID";
	private GoogleMap mGoogleMap;
	private Context mContext;
	private App mApp;
	private ArrayList<App> mApps;
	 
	// MapFragment ������ ���õ� App UUID�� �ޱ����� �Լ�
	public static MapFragment newInstance(UUID id){
		Bundle args = new Bundle();
		args.putSerializable(ARG_APP_ID, id);
		
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
	
	// ���۸� ����(��Ŀ ��)
	public void mapInit(){
		
		try{
			// ���� ���� �÷��� ��� ���� ���� Ȯ��
			int enableGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			if(ConnectionResult.SUCCESS == enableGooglePlay){
				mGoogleMap = getMap();
				// ���� ��ġ ǥ��
				mGoogleMap.setMyLocationEnabled(true);
				
				// TODO: �����Ͱ� ���� ��ġ ǥ��
				LatLng pos = new LatLng(mApp.getAppPos().getLatitude(), mApp.getAppPos().getLongitude());

				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
				
				MarkerOptions marker = new MarkerOptions();
				
				//�ּұ��ϱ�
				String addr = GPSManager.get(mContext).getAddressFromLocation(mApp.getAppPos().getLatitude(), mApp.getAppPos().getLongitude());
				
				// ��Ŀ����
				// TODO: �����Ÿ��ȿ� �ִ� �����ʹ� �ϳ��� ��Ŀ�� �����ϰ� 
				marker.position(pos);
				marker.title("�ּ�");
				marker.snippet(addr);
				
				mGoogleMap.addMarker(marker).showInfoWindow();
				
				// ��ĿŬ��������
				// TODO: ���� ������ �����ִ� �κ�?
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
	// ���õ� ��¥�� ���� ��ġ�� �����ͼ� ǥ����
	public void mapInitForDay(String day){
		try{
			// ���� ���� �÷��� ��� ���� ���� Ȯ��
			int enableGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			if(ConnectionResult.SUCCESS == enableGooglePlay){
				mGoogleMap = getMap();
				// ���� ��ġ ǥ��
				mGoogleMap.setMyLocationEnabled(true);
				
				// �ش� ��¥�� ���� ����
				if(null != mApp.getUsingDay() && mApp.getUsingDay().equals(day)){
					try{
				    	AppJSONSerializer serializer = new AppJSONSerializer(getActivity(), AppList.FILENAME);
				    	mApps = serializer.loadAppsForDay(day);
				    	
				    	LatLng pos = new LatLng(0,0);
				    	for(App a : mApps){
				    		pos = new LatLng(a.getAppPos().getLatitude(), a.getAppPos().getLongitude());
				    		
				    		MarkerOptions marker = new MarkerOptions();
							
							//�ּұ��ϱ�
							String addr = GPSManager.get(mContext).getAddressFromLocation(a.getAppPos().getLatitude(), a.getAppPos().getLongitude());
							
							// ��Ŀ����
							// TODO: �����Ÿ��ȿ� �ִ� �����ʹ� �ϳ��� ��Ŀ�� �����ϰ�
							marker.position(pos);
							marker.title(addr);
							marker.snippet(a.getStartDate());
							
							mGoogleMap.addMarker(marker).showInfoWindow();
				    	}
				    	//������ ��ġ�� ī�޶� �̵�
				    	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
				    	
				    	// ��ĿŬ��������
						// TODO: ���� ������ �����ִ� �κ�?
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
