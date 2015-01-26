package com.example.whoami;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * ��ġ���� �Ŵ���
 */

public class GPSManager implements LocationListener{
	
	private static final String TAG = "whoami.gpsmanager";
	private static GPSManager sGPSManager;
	private static Context mContext;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //���ŰŸ� 10����
	private static final long MIN_TIME_GPS_UPDATES = 1000 * 60 * 1;      //���Žð� 1��

	private GPSManager(Context c){
		mContext = c;
	}
	
	public static GPSManager get(Context c){
		if(null == sGPSManager){
			sGPSManager = new GPSManager(c);
		}
		return sGPSManager;
	}
	
	// ��ġ����
	// TODO: NETWORK_PROVIDER �� �ȵ���?
	public Location getLocation(){
		Log.i("whoami", "getNetworkLocation start");
		LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		Location loc;
		if(null == (loc = getLocationToProvider(lm, LocationManager.GPS_PROVIDER))
				&& null == (loc = getLocationToProvider(lm, LocationManager.NETWORK_PROVIDER))){
			return null;
		}
		return loc;
	}
	
	// GPS �Ǵ� NETWORK �̿�
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
	
	// �ּ�
	public String getAddressFromLocation(double loc, double lon) {
		Locale.setDefault(Locale.KOREA); 
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addressList = null ;

        // �����ڴ��� �̿��Ͽ� �ּ� ����Ʈ�� ���մϴ�.
        try {
            addressList = geocoder.getFromLocation(loc, lon, 1);
        } catch (IOException e) {
            Toast. makeText( mContext, "getAddressFromLocation() fail, Check network status!", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "�ּ� �ν� �Ұ�" ;
        }

        // �ּ� ����Ʈ�� ����ִ��� Ȯ���մϴ�. ��� ������, �ּ� ��� �װ��� ������ �˸��� ���ڿ��� �����մϴ�.
        if (1 > addressList.size()) {
            return "�ش� ��ġ�� �ּ� ����" ;
        }
        
        // �ּҸ� ��� ���ڿ��� �����ϰ� �����մϴ�.
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
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
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
