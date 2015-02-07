package com.example.whoami;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 위치관리 매니저
 */

public class GPSManager implements LocationListener{
	
	private static final String TAG = "whoami.gpsmanager";
	private static GPSManager sGPSManager;
	private static Context mContext;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //갱신거리 10미터
	private static final long MIN_TIME_GPS_UPDATES = 1000 * 60 * 1;      //갱신시간 1분
	private LocationManager mLm;
	
	private GPSManager(Context c){
		mContext = c;
	}
	
	public static GPSManager get(Context c){
		if(null == sGPSManager){
			sGPSManager = new GPSManager(c);
		}
		return sGPSManager;
	}
	
	public void release(){
		if(null == mLm)
			mLm.removeUpdates(this);
	}
	
	// 위치정보
	// TODO: NETWORK_PROVIDER 왜 안되지?
	public Location getLocation(){
		Log.i("whoami", "getNetworkLocation start");
		LocationManager mLm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		Location loc;
		
		if(null != (loc = getLocationToProvider(mLm, LocationManager.GPS_PROVIDER))
				|| null != (loc = getLocationToProvider(mLm, LocationManager.NETWORK_PROVIDER))){
			// 위치만 구하고 LocationManager 바로 해제
			mLm.removeUpdates(this);
			return loc;
		}
		
		return null;
	}
	
	// GPS 또는 NETWORK 이용
	public Location getLocationToProvider(LocationManager lm, String provider){
		try{
			if(true == lm.isProviderEnabled(provider)){
				lm.requestLocationUpdates(provider, 
						MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_GPS_UPDATES, this);
				return lm.getLastKnownLocation(provider);
			}
		}catch(IllegalArgumentException e){
			Log.e("whoami", "getLocation error:" + e.toString());
		}
		return null;
	}
	
	// 주소
	public String getAddressFromLocation(double loc, double lon) {
		Locale.setDefault(Locale.KOREA);
		
		// 2015.02.07.12.58 앱 크래시 발생(NullPointerException)
		// 널 검사 추가함
		if(null == mContext)
			return null;
		
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addressList = null ;

        // 지오코더를 이용하여 주소 리스트를 구합니다.
        try {
            addressList = geocoder.getFromLocation(loc, lon, 1);
        } catch (IOException e) {
            Toast. makeText( mContext, "getAddressFromLocation() fail, Check network status!", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }

        // 주소 리스트가 비어있는지 확인합니다. 비어 있으면, 주소 대신 그것이 없음을 알리는 문자열을 리턴합니다.
        if (1 > addressList.size()) {
            return "해당 위치에 주소 없음" ;
        }
        
        // 주소를 담는 문자열을 생성하고 리턴합니다.
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }
        return addressStringBuilder.toString();
    }
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(LocationManager.GPS_PROVIDER == location.getProvider()){
			
		}else if(LocationManager.NETWORK_PROVIDER == location.getProvider()){
			
		}else if(LocationManager.PASSIVE_PROVIDER == location.getProvider()){
			
		}
		
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}

